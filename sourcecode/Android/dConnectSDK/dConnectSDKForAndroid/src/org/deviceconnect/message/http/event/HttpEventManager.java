/*
 HttpEventManager.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.message.http.event;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.message.event.AbstractEventManager;
import org.deviceconnect.utils.URIBuilder;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * イベント管理クラス. 
 * イベントの登録、解除、WebSocketの開閉等のユーティリティ機能を提供する。
 * 
 * 
 * @author NTT DOCOMO, INC.
 */
public final class HttpEventManager extends AbstractEventManager {
    
    /** 
     * WebSocket通常終了コード.
     */
    private static final int NORMAL_CLOSE_CODE = 1000;
    
    /** 
     * リトライする回数.
     */
    private static final int RETRY_TIMES = 5;
    
    /** 
     * リトライ時に待つ時間.
     * 回数を重ねるたびにこの時間分だけ増えていく。
     * ex. 1回目 {@value} ミリ秒、2回目 {@value} * 2ミリ秒。
     */
    private static final long RETRY_WAIT = 30 * 1000;

    /**
     * シングルトンなEventManagerのインスタンス.
     */
    public static final HttpEventManager INSTANCE = new HttpEventManager();

    /**
     * WebSocketクライアント.
     */
    private EventWebSocketClient mWSClient;
    
    /** 
     * コネクションハンドラ.
     */
    private CloseHandler mCloseHandler;

    /**
     * ロガー.
     */
    private Logger mLogger = Logger.getLogger("org.deviceconnect.sdk");

    /**
     * ロックオブジェクト.
     */
    private Object mLock;

    /**
     * WebSocketステータス.
     * 
     * @author NTT DOCOMO, INC.
     * 
     */
    private enum Status {
        
        /** 
         * オープン待ち状態.
         */
        WAITING_OPEN,
        
        /**
         * 開いている.
         */
        OPEN,

        /**
         * 閉じている.
         */
        CLOSE,
        
        /**
         * リトライ中.
         */
        RETRYING,
    }

    /**
     * WebSocketステータス.
     */
    private Status mStatus;

    /**
     * シングルトンのためprivate.
     */
    private HttpEventManager() {
        mLock = new Object();
        mStatus = Status.CLOSE;
    }
    
    /**
     * イベントの受信用のコネクションを張る. <br/>
     * このメソッドでコネクションを張り、イベント登録をすることでイベントを受信できるようになる。<br/>
     * イベントの受信が不必要になった場合は{@link #disconnect()}を呼び出し、コネクションを破棄すること。
     * 一つのコネクションを使い回すため、コネクションを破棄すると全てのイベント受信が遮断されるので注意すること。
     * 
     * @param host ホスト名
     * @param port ポート番号
     * @param isSSL サーバーがSSLになっているかのフラグ
     * @param sessionKey セッションキー
     * @param handler クローズハンドラー。コネクションが閉じられたことを通知します。
     * @return 接続に成功した場合true、その他はfalseを返す
     */
    public synchronized boolean connect(final String host, final int port, final boolean isSSL,
            final String sessionKey, final CloseHandler handler) {

        if (host == null) {
            throw new IllegalArgumentException("host must not be null.");
        } else if (port < 0) {
            throw new IllegalArgumentException("port must be larger than 0.");
        } else if (sessionKey == null) {
            throw new IllegalArgumentException("sessionKey must not be null.");
        }

        if (mStatus == Status.OPEN || mStatus == Status.RETRYING) {

            if (!sessionKey.equals(getSessionKey())) {
                setSessionKey(sessionKey);
                if (mStatus == Status.OPEN) {
                    mWSClient.sendSessionKey(sessionKey);
                }
            }

            return true;
        }

        URIBuilder builder = new URIBuilder();
        if (isSSL) {
            builder.setScheme("wss");
        } else {
            builder.setScheme("ws");
        }

        builder.setHost(host).setPort(port).setPath("/" + DConnectMessage.EXTRA_WEBSOCKET);

        URI webSocketUri;
        try {
            webSocketUri = builder.build();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Can not create uri. Do check the parameters.");
        }

        setSessionKey(sessionKey);
        mStatus = Status.WAITING_OPEN;
        mWSClient = new EventWebSocketClient(webSocketUri);
        mWSClient.connect();

        waitConnect();

        if (mWSClient.isOpen()) {
            mCloseHandler = handler;
            return true;
        }

        setSessionKey(null);
        return false;
    }
    
    @Override
    public synchronized void disconnect() {
        if (mWSClient != null && mWSClient.isOpen()) {
            mWSClient.close();
            mWSClient = null;
            mStatus = Status.CLOSE;
        }
    }
    
    /**
     * 再接続を試みる.
     */
    private synchronized void retry() {
        mStatus = Status.RETRYING;
        new Thread(new RetryProcess()).start();
    }
    
    /**
     * WebSocketの接続を待つ.
     */
    private void waitConnect() {
        synchronized (mLock) {
            try {
                mLock.wait();
            } catch (InterruptedException e) {
                mWSClient.close();
            }
        }
    }
    
    @Override
    protected HttpResponse execute(final HttpUriRequest request) throws IOException {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpResponse response = client.execute(request);
        HttpResponse retRes = copyResponse(response);
        client.getConnectionManager().shutdown();
        return retRes;
    }

    /**
     * Event受信用のWebSocketのクライアントクラス.
     * 
     * @author NTT DOCOMO, INC.
     * 
     */
    private class EventWebSocketClient extends WebSocketClient {

        /**
         * WebSocketクライアントを生成する.
         * 
         * @param serverURI サーバーのURI
         */
        public EventWebSocketClient(final URI serverURI) {
            super(serverURI);
        }

        @Override
        public void onOpen(final ServerHandshake handshakedata) {
            mStatus = Status.OPEN;
            sendSessionKey(getSessionKey());
            synchronized (mLock) {
                mLock.notifyAll();
            }
        }

        @Override
        public void onMessage(final String message) {

            try {
                sendEvent(new JSONObject(message));
            } catch (JSONException e) {
                mLogger.warning("EventWebSocketClient#onMessage Invalid message. : " + e.getMessage());
            }

        }

        @Override
        public void onClose(final int code, final String reason, final boolean remote) {
            
            synchronized (HttpEventManager.this) {
                if (mStatus == Status.WAITING_OPEN 
                        || mStatus == Status.RETRYING) {
                    synchronized (mLock) {
                        mLock.notifyAll();
                    }
                } else if (mStatus == Status.OPEN) {
                    if (code != NORMAL_CLOSE_CODE) {
                        // 異常終了の場合はリトライし、自動的に回復を図る
                        retry();
                        return;
                    } else if (mCloseHandler != null) {
                        mCloseHandler.onClosed();
                    }
                }
                
                if (mStatus != Status.RETRYING) {
                    mStatus = Status.CLOSE;
                }
            }
        }

        @Override
        public synchronized void onError(final Exception ex) {
            mLogger.warning("EventWebSocketClient#onError : " + ex);
        }

        /**
         * セッションキーを送信する.
         * 
         * @param sessionKey セッションキー
         */
        public void sendSessionKey(final String sessionKey) {
            send("{\"" + DConnectMessage.EXTRA_SESSION_KEY + "\":\"" + sessionKey + "\"}");                
        }
    }
    
    /**
     * リトライ処理をするランナブルクラス.
     * 
     * @author NTT DOCOMO, INC.
     *
     */
    private class RetryProcess implements Runnable {
        
        @Override
        public void run() {
            mLogger.fine("RetryProcess#run. Retrying...");
            for (int i = 0; i < RETRY_TIMES; i++) {
                long wait = RETRY_WAIT * (i + 1);
                if (retry(wait)) {
                    mLogger.fine("RetryProcess#run. Successed to retry.");
                    return;
                }
            }
            
            disconnect();
            if (mCloseHandler != null) {
                mLogger.fine("RetryProcess#run. Failed to retry.");
                mCloseHandler.onClosed();
            }
        }
        
        /**
         * リトライ処理を実行する.
         * 
         * @param wait 接続前の待ち時間。すぐに再接続を試みてもサーバーが落ちている可能性もあるので少し待つ。
         * @return 接続できたらtrue、失敗したらfalseを返す
         */
        private boolean retry(final long wait) {
            
            try {
                Thread.sleep(wait);
            } catch (InterruptedException e) {
                mLogger.warning("RetryProcess#retry Interrupted. : " + e.getMessage());
            }
            
            mWSClient = new EventWebSocketClient(mWSClient.getURI());
            mWSClient.connect();
            waitConnect();
            if (mWSClient.isOpen()) {
                return true;
            }
            
            return false;
        }
    }
}
