/*
 DConnectServerNanoHttpd.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.dconnect.server.nanohttpd;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

import javax.net.ssl.SSLServerSocketFactory;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.nttdocomo.dconnect.server.DConnectServer;
import com.nttdocomo.dconnect.server.DConnectServerConfig;
import com.nttdocomo.dconnect.server.DConnectServerError;
import com.nttdocomo.dconnect.server.http.HttpRequest;
import com.nttdocomo.dconnect.server.http.HttpResponse;
import com.nttdocomo.dconnect.server.nanohttpd.logger.AndroidHandler;
import com.nttdocomo.dconnect.server.nanohttpd.security.Firewall;
import com.nttdocomo.dconnect.server.nanohttpd.util.KeyStoreManager;
import com.nttdocomo.dconnect.server.websocket.DConnectWebSocket;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import fi.iki.elonen.NanoWSD;
import fi.iki.elonen.WebSocket;
import fi.iki.elonen.WebSocketFrame;
import fi.iki.elonen.WebSocketFrame.CloseCode;

/**
 * d-Connectサーバー NanoHTTPD.
 * 
 * @author NTT DOCOMO, INC.
 */
public class DConnectServerNanoHttpd extends DConnectServer {

    /** ログ用タグ. */
    private static final String TAG = "DConnectServerNanoHttpd";

    /** バージョン. */
    private static final String VERESION = "0.0.1";

    /** WebSocketのKeepAlive処理のインターバル. */
    private static final int WEBSOCKET_KEEP_ALIVE_INTERVAL = 3000;

    /** 対応するMIME_TYPE群. */
    private static final Map<String, String> MIME_TYPES;

    /** サーバーオブジェクト. */
    private NanoServer mServer;

    /** キーストア管理用. */
    private KeyStoreManager mKeyStoreManager;

    /** コンテキストオブジェクト. */
    private Context mContext;

    /**
     * Keep-Aliveの状態定数.
     * 
     * @author NTT DOCOMO, INC.
     * 
     */
    private enum KeepAliveState {
        /** クライアントの返事待ち状態. */
        WAITING_PONG,

        /** pong受信完了状態. */
        GOT_PONG,

    }

    /**
     * 設定値を元にサーバーを構築します.
     * 
     * @param config サーバー設定。
     * @param context コンテキストオブジェクト。
     * @throws IllegalArgumentException contextがnullの場合スローされる。
     */
    public DConnectServerNanoHttpd(DConnectServerConfig config, Context context) {
        super(config);

        if (context == null) {
            throw new IllegalArgumentException("Context must not be null.");
        }

        mContext = context;
        if (BuildConfig.DEBUG) {
            Handler handler = new AndroidHandler(TAG);
            handler.setFormatter(new SimpleFormatter());
            handler.setLevel(Level.ALL);
            mLogger.addHandler(handler);
            mLogger.setLevel(Level.WARNING);
        }
    }

    static {
        MIME_TYPES = new HashMap<String, String>();
        MIME_TYPES.put("css", "text/css");
        MIME_TYPES.put("htm", "text/html");
        MIME_TYPES.put("html", "text/html");
        MIME_TYPES.put("txt", "text/plain");
        MIME_TYPES.put("jpg", "image/jpeg");
        MIME_TYPES.put("jpeg", "image/jpeg");
        MIME_TYPES.put("png", "image/png");
        MIME_TYPES.put("js", "application/javascript");

        // lighttpdには指定していないが使いそうなものなので入れておく
        MIME_TYPES.put("gif", "image/gif");
        MIME_TYPES.put("zip", "application/octet-stream");
    }

    @Override
    public synchronized void start() {

        mLogger.entering(getClass().getName(), "start");

        if (mServer != null) {
            throw new IllegalStateException("Server is already running.");
        }

        if (!checkDocumentRoot()) {
            if (mListener != null) {
                mListener.onError(DConnectServerError.LAUNCH_FAILED);
            }
            mLogger.exiting(getClass().getName(), "start");
            return;
        }

        mServer = new NanoServer(mConfig.getHost(), mConfig.getPort());

        if (mConfig.isSsl()) {
            SSLServerSocketFactory factory = createServerSocketFactory();
            if (factory == null) {
                if (mListener != null) {
                    mListener.onError(DConnectServerError.LAUNCH_FAILED);
                }
                mLogger.exiting(getClass().getName(), "start");
                return;
            }

            mServer.makeSecure(factory);
        }

        // Androidで利用する場合にMainThreadで利用できない処理がNanoServer#start()にあるため
        // 別スレッドで処理を実行する
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mServer.start();
                    if (mListener != null) {
                        mListener.onServerLaunched();
                    }
                } catch (IOException e) {
                    if (mListener != null) {
                        mListener.onError(DConnectServerError.LAUNCH_FAILED);
                    }
                    mLogger.warning("Exception in the DConnectServerNanoHttpd#start() method. " + e.toString());
                }
            }
        }).start();

        mLogger.exiting(getClass().getName(), "start");
    }

    @Override
    public synchronized void shutdown() {
        mLogger.entering(getClass().getName(), "shutdown");

        if (!isRunning()) {
            if (mListener != null) {
                mListener.onError(DConnectServerError.SHUTDOWN_FAILED);
            }
            return;
        }

        if (mSockets != null) {

            for (Entry<String, DConnectWebSocket> data : mSockets.entrySet()) {
                if (data.getValue() instanceof WebSocket) {
                    try {
                        ((WebSocket) data.getValue()).close(CloseCode.NormalClosure, "Server was shutdown.");
                    } catch (IOException e) {
                        mLogger.warning("Exception in the DConnectServerNanoHttpd#shutdown() method. " + e.toString());
                    }
                }
            }
            mSockets.clear();
        }

        mServer.stop();
        mServer = null;
        mLogger.exiting(getClass().getName(), "shutdown");
    }

    @Override
    public synchronized boolean isRunning() {
        return (mServer == null) ? false : mServer.isAlive();
    }

    /**
     * 証明書を読み込みFactoryクラスを生成する.
     * 
     * @return 読み込み成功時はSSLServerSocketFactoryを、その他はnullを返す。
     */
    private SSLServerSocketFactory createServerSocketFactory() {

        mLogger.entering(getClass().getName(), "createServerSocketFactory");
        SSLServerSocketFactory retval = null;

        do {
            mKeyStoreManager = new KeyStoreManager();
            try {
                mKeyStoreManager.initialize(mContext, false);
            } catch (GeneralSecurityException e) {
                mLogger.warning("Exception in the DConnectServerNanoHttpd#createServerSocketFactory() method. "
                        + e.toString());
                break;
            }

            retval = mKeyStoreManager.getServerSocketFactory();
        } while (false);

        mLogger.exiting(getClass().getName(), "createServerSocketFactory", retval);

        return retval;
    }

    /**
     * 設定されたドキュメントルートが正しいかチェックする.
     * 
     * @return 正しい場合true、不正な場合falseを返す。
     */
    private boolean checkDocumentRoot() {

        mLogger.entering(getClass().getName(), "checkDocumentRoot");
        boolean retval = true;
        File documentRoot = new File(mConfig.getDocumentRootPath());

        if (!documentRoot.exists() || !documentRoot.isDirectory()) {
            mLogger.warning("Invalid document root path : " + documentRoot.getPath());
            retval = false;
        }

        mLogger.exiting(getClass().getName(), "checkDocumentRoot", retval);
        return retval;
    }

    /**
     * NanoHttpサーバーの実継承クラス.
     * 
     * @author NTT DOCOMO, INC.
     * 
     */
    private class NanoServer extends NanoWSD {

        /** WebSocketのコネクションカウンター */
        private int mWebSocketCount;

        public NanoServer(String hostname, int port) {
            super(hostname, port);
            mLogger.entering(getClass().getName(), "NanoServer", new Object[] { hostname, port });

            Firewall firewall = new Firewall(mConfig.getIPWhiteList());
            setFirewall(firewall);

            mLogger.exiting(getClass().getName(), "NanoServer");
            mWebSocketCount = 0;
        }

        @Override
        public Response serve(IHTTPSession session) {
            mLogger.entering(getClass().getName(), "serve", session);
            NanoHTTPD.Response nanoRes = null;

            do {

                if (isWebsocketRequested(session)) {

                    if (!countupWebSocket()) {
                        nanoRes = new Response(Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT,
                                "Server can't create more connections.");
                        break;
                    }

                    // WebSocketを開く処理&レスポンスはNanoWSDに任せ、セッションキーが送られてから
                    // 独自のセッション管理を行う。
                    nanoRes = parseWebSocketRequest(session);
                    if (nanoRes.getStatus() != Status.SWITCH_PROTOCOL) {
                        // 不正なWebSocketのリクエストの場合はカウントを取り消す
                        countdownWebSocket();
                    }

                    break;
                }

                if (session.getMethod() == Method.GET) {
                    nanoRes = checkStaticFile(session);
                    if (nanoRes != null) {
                        break;
                    }
                }

                nanoRes = new NanoHTTPD.Response("");
                HttpRequest req = createRequest(session, nanoRes);
                if (req == null) {
                    // d-Connect用のリクエストが生成できない場合は何かしらのエラー、または別対応が入るので
                    // dConnectManagerへの通知はしない。
                    break;
                }

                HttpResponse res = new HttpResponse();
                if (mListener != null && mListener.onReceivedHttpRequest(req, res)) {

                    ByteArrayInputStream stream;

                    if (res.getBody() != null) {
                        stream = new ByteArrayInputStream(res.getBody());
                    } else {
                        stream = new ByteArrayInputStream("".getBytes());
                    }

                    nanoRes.setStatus(getStatus(res.getCode()));
                    nanoRes.setMimeType(res.getContentType());
                    nanoRes.setData(stream);

                    Map<String, String> headers = res.getHeaders();
                    for (Entry<String, String> head : headers.entrySet()) {
                        nanoRes.addHeader(head.getKey(), head.getValue());
                    }

                } else {
                    nanoRes = super.serve(session);
                }

            } while (false);

            nanoRes.addHeader("Access-Control-Allow-Origin", "*");

            // クロスドメインでアクセスする際にプリフライトリクエストで使用するHTTPヘッダを送信される。
            // このヘッダを受けた場合、Access-Control-Allow-Headersでそれらを許可する必要がある。
            // また、X-Requested-WithというヘッダーでXMLHttpRequestが使えるかの問い合わせがくる場合が
            // あるため、XMLHttpRequestを許可しておく。
            String requestHeaders = session.getHeaders().get("access-control-request-headers");
            if (requestHeaders != null) {
                requestHeaders = "XMLHttpRequest, " + requestHeaders;
            } else {
                requestHeaders = "XMLHttpRequest";
            }

            nanoRes.addHeader("Access-Control-Allow-Headers", requestHeaders);

            mLogger.exiting(getClass().getName(), "serve", nanoRes);
            return nanoRes;
        }

        /**
         * リクエストがWebSocket用かどうか判断する.
         * NanoWSDの当メソッドはFireFoxのリクエストに対応していないため、オーバーライドして修正する。
         * 
         * @param session リクエスト情報。
         * @see fi.iki.elonen.NanoWSD#isWebsocketRequested(fi.iki.elonen.NanoHTTPD.IHTTPSession)
         */
        @Override
        protected boolean isWebsocketRequested(IHTTPSession session) {
            // FireFox では Connetion : keep-alive, Upgrade とリクエストがくるので
            // Upgradeを含んでいればOKと見なす。
            Map<String, String> headers = session.getHeaders();
            String conValue = HEADER_CONNECTION_VALUE.toLowerCase(Locale.ENGLISH);
            String headValue = headers.get(HEADER_CONNECTION);
            if (headValue == null) {
                return false;
            }
            headValue = headValue.toLowerCase(Locale.ENGLISH);

            return ((HEADER_UPGRADE_VALUE.equalsIgnoreCase(headers.get(HEADER_UPGRADE))) && headValue.indexOf(conValue) != -1);
        }

        /**
         * WebSocket用のリクエストを解析する. NanoWSDのserveメソッドではハンドシェイク処理に不具合があり、FireFoxからの
         * リクエストを正しく処理できないため、オーバーライドして修正する。
         * 
         * @param session リクエスト情報。
         * @return レスポンス。
         */
        private Response parseWebSocketRequest(IHTTPSession session) {
            Map<String, String> headers = session.getHeaders();

            if (!HEADER_WEBSOCKET_VERSION_VALUE.equalsIgnoreCase(headers.get(HEADER_WEBSOCKET_VERSION))) {
                return new Response(Response.Status.BAD_REQUEST, NanoHTTPD.MIME_PLAINTEXT, "Invalid Websocket-Version "
                        + headers.get(HEADER_WEBSOCKET_VERSION));
            }
            if (!headers.containsKey(HEADER_WEBSOCKET_KEY)) {
                return new Response(Response.Status.BAD_REQUEST, NanoHTTPD.MIME_PLAINTEXT, "Missing Websocket-Key");
            }

            WebSocket webSocket = openWebSocket(session);

            try {
                webSocket.getHandshakeResponse().addHeader(HEADER_WEBSOCKET_ACCEPT,
                        makeAcceptKey(headers.get(HEADER_WEBSOCKET_KEY)));
            } catch (NoSuchAlgorithmException e) {
                return new Response(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT,
                        "The SHA-1 Algorithm required for websockets is not available on the server.");
            }
            if (headers.containsKey(HEADER_WEBSOCKET_PROTOCOL)) {
                webSocket.getHandshakeResponse().addHeader(HEADER_WEBSOCKET_PROTOCOL,
                        headers.get(HEADER_WEBSOCKET_PROTOCOL).split(",")[0]);
            }
            return webSocket.getHandshakeResponse();
        }

        /**
         * HttpRequest.StatusCodeをNanoHTTPD.Statusに変換する.
         * 
         * @param code ステータスコード
         * @return ステータスコード
         */
        private Status getStatus(HttpResponse.StatusCode code) {

            int codeNum = code.getCode();

            for (Status status : Status.values()) {
                if (status.getRequestStatus() == codeNum) {
                    return status;
                }
            }
            // NanoHTTPDで対応していない物は全てエラーとして扱う
            return Status.INTERNAL_ERROR;
        }

        @Override
        protected WebSocket openWebSocket(IHTTPSession handshake) {
            // ここでコネクション数制限をかけてnullを返しても、呼び出しもとで
            // nullチェックをしていないため、更に上位の場所で制限をかける。
            return new NanoWebSocket(handshake);
        }

        /**
         * 静的コンテンツへのリクエストかどうかをチェックする.
         * 
         * @param session HTTPリクエストデータ
         * @return d-Connectへのリクエストの場合はnullを返す。
         */
        private Response checkStaticFile(IHTTPSession session) {

            mLogger.entering(getClass().getName(), "checkStaticFile", session);
            Response retval = null;

            do {
                String mime = session.getHeaders().get("content-type");
                // httpの仕様より、content-typeでMIME Typeが特定できない場合はURIから
                // MIME Typeを推測する。
                if (mime == null || !MIME_TYPES.containsValue(mime)) {
                    mime = getMimeTypeFromURI(session.getUri());
                }

                // MIMEタイプがファイルで無い場合はdConnectへのリクエストかどうかの
                // チェックに回す。
                if (mime == null) {
                    break;
                }

                // 静的コンテンツへのアクセスの場合はdocument rootからファイルを検索する。
                File file = new File(mConfig.getDocumentRootPath(), session.getUri());

                if (!file.exists()) {
                    retval = new Response(Status.NOT_FOUND, MIME_PLAINTEXT, Status.NOT_FOUND.getDescription());
                    break;
                } else if (file.isDirectory()) {
                    break;
                } else if (!isReadableFile(file)) {
                    retval = new Response(Status.FORBIDDEN, MIME_PLAINTEXT, Status.FORBIDDEN.getDescription());
                }

                // If-None-Match対応
                String etag = Integer.toHexString((file.getAbsolutePath() + file.lastModified() + "" + file.length())
                        .hashCode());
                if (etag.equals(session.getHeaders().get("if-none-match"))) {
                    retval = new Response(Status.NOT_MODIFIED, mime, "");
                } else {
                    try {
                        retval = new Response(Status.OK, mime, new FileInputStream(file));
                        retval.addHeader("Content-Length", "" + file.length());
                        retval.addHeader("ETag", etag);
                    } catch (FileNotFoundException e) {
                        retval = new Response(Status.NOT_FOUND, MIME_PLAINTEXT, Status.NOT_FOUND.getDescription());
                        break;
                    }
                }

                // ByteRangeへの対応は必須ではないため、noneを指定して対応しないことを伝える。
                // 対応が必要な場合はbyteを設定して実装すること。
                retval.addHeader("Accept-Ranges", "none");

            } while (false);

            mLogger.exiting(getClass().getName(), "checkStaticFile", retval);
            return retval;
        }

        /**
         * URIからMIMEタイプを推測する.
         * 
         * @param uri リクエストURI
         * @return MIMEタイプが推測できた場合MIMEタイプ文字列を、その他はnullを返す
         */
        private String getMimeTypeFromURI(String uri) {

            int dot = uri.lastIndexOf('.');
            String mime = null;
            if (dot >= 0) {
                mime = MIME_TYPES.get(uri.substring(dot + 1).toLowerCase(Locale.ENGLISH));
            }

            return mime;
        }

        /**
         * WebSocketのコネクションカウンタを1増やす.
         * 
         * @return コネクション数が上限に達していない場合true、上限に達した場合はfalseを返す
         */
        private synchronized boolean countupWebSocket() {

            mLogger.entering(getClass().getName(), "countupWebSocket");
            if (mConfig.getMaxWebSocketConnectionSize() <= mWebSocketCount) {
                mLogger.exiting(getClass().getName(), "countupWebSocket", false);
                return false;
            }

            mWebSocketCount++;
            mLogger.exiting(getClass().getName(), "countupWebSocket", true);
            return true;
        }

        /**
         * WebSocketのコネクションカウンタを1減らす.
         */
        private synchronized void countdownWebSocket() {
            mLogger.entering(getClass().getName(), "countdownWebSocket");
            if (mWebSocketCount > 0) {
                mWebSocketCount--;
            }
            mLogger.exiting(getClass().getName(), "countdownWebSocket");
        }

        /**
         * IHTTPSessionからHttpRequestを生成する.
         * 
         * @param session リクエストデータ
         * @param res 
         *            NanoHTTPD用のレスポンスデータ。d-Connectへのリクエストが生成できない場合は当メソッドで適切なレスポンス値を設定する
         *            。
         * @return d-Connect用リクエストデータ。d-Connectへリクエストを渡さない場合はnullを返す。
         */
        private HttpRequest createRequest(IHTTPSession session, NanoHTTPD.Response res) {
            String method = null;
            switch (session.getMethod()) {
            case GET:
                method = HttpRequest.HTTP_METHOD_GET;
                break;
            case POST:
                method = HttpRequest.HTTP_METHOD_POST;
                break;
            case DELETE:
                method = HttpRequest.HTTP_METHOD_DELETE;
                break;
            case PUT:
                method = HttpRequest.HTTP_METHOD_PUT;
                break;
            case OPTIONS:
                // クロスドメイン対応としてOPTIONSがきたらd-Connectで対応しているメソッドを返す
                res.setStatus(Status.OK);
                res.setMimeType(MIME_PLAINTEXT);
                res.addHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE");
                // d-Connect対応外のメソッドだがエラーにはしないのでここで処理を終了。
                return null;
            default:
                mLogger.warning("This http method is not treated by d-Connect : " + session.getMethod());
                break;
            }

            if (method == null) {
                res.setStatus(Status.NOT_IMPLEMENTED);
                res.setMimeType(MIME_PLAINTEXT);
                res.setData(new ByteArrayInputStream("Not allowed HTTP method.".getBytes()));
                return null;
            }

            if (!session.getHeaders().containsKey("host")) {
                res.setStatus(Status.BAD_REQUEST);
                res.setMimeType(MIME_PLAINTEXT);
                res.setData(new ByteArrayInputStream("Bad Request.".getBytes()));
                return null;
            }

            String http = mConfig.isSsl() ? "https://" : "http://";
            String uri = http + session.getHeaders().get("host") + session.getUri();
            if (session.getQueryParameterString() != null && session.getQueryParameterString().length() != 0) {
                uri += "?" + session.getQueryParameterString();
            }

            HttpRequest req = new HttpRequest();
            req.setBody(parseBody(session));
            req.setMethod(method);
            req.setUri(uri);
            req.setHeaders(session.getHeaders());

            return req;
        }

        /**
         * リクエストからBodyを抜き出す.
         * 
         * @param session リクエストデータ
         * @return HTTPリクエストのBodyデータ
         */
        private byte[] parseBody(IHTTPSession session) {
            // NanoHTTPDのparseBodyではマルチパートの場合に自動的に一時ファイルに
            // データを格納するようになっているため、独自にBodyを抜き出す。
            mLogger.entering(getClass().getName(), "parseBody", session);

            if (!(session instanceof HTTPSession)) {
                mLogger.exiting(getClass().getName(), "parseBody", null);
                return null;
            }
            
            Map<String, String> headers = session.getHeaders();
            if (!session.getMethod().equals(Method.PUT) 
                    && !session.getMethod().equals(Method.POST)
                    && !headers.containsKey("content-length")) 
            {
                return null;
            }

            long size;
            int splitbyte = ((HTTPSession) session).getSplitbyte();
            int rlen = ((HTTPSession) session).getRlen();
            byte[] retval = null;

            if (headers.containsKey("content-length")) {
                size = Integer.parseInt(headers.get("content-length"));
            } else if (splitbyte < rlen) {
                size = rlen - splitbyte;
            } else {
                size = 0;
            }

            try {
                byte[] buf = new byte[512];
                InputStream is = session.getInputStream();
                ByteArrayOutputStream bout = new ByteArrayOutputStream();

                while (rlen >= 0 && size > 0) {
                    rlen = is.read(buf, 0, (int) Math.min(size, 512));
                    size -= rlen;
                    if (rlen > 0) {
                        bout.write(buf, 0, rlen);
                    }
                }

                if (size != 0 && bout.size() != size) {
                    throw new RuntimeException();
                }

                retval = bout.toByteArray();
            } catch (Exception e) {
                mLogger.warning("Exception in the NanoServer#parseBody() method. " + e.toString());
            }

            mLogger.exiting(getClass().getName(), "parseBody", retval);
            return retval;
        }

        /**
         * ファイルが読み込み可能なファイルかチェックする.
         * 
         * @param file チェック対象のファイル。
         * @return 読み込めるファイルの場合trueを、その他はfalseを返す。
         */
        private boolean isReadableFile(File file) {

            mLogger.entering(getClass().getName(), "isDeployedInDocumentRoot", file);
            boolean retval = false;
            try {
                // ../ などのDocument Rootより上の階層にいくファイルパスをチェックし
                // 不正なリクエストを拒否する。
                File root = new File(mConfig.getDocumentRootPath());
                String rootAbPath = root.getCanonicalPath() + "/";
                String fileAbPath = file.getCanonicalPath();
                retval = fileAbPath.contains(rootAbPath) && file.canRead();
            } catch (IOException e) {
                mLogger.warning("Exception in the NanoServer#isDeployedInDocumentRoot() method. " + e.toString());
                retval = false;
            }

            mLogger.exiting(getClass().getName(), "isDeployedInDocumentRoot", retval);
            return retval;
        }
    }

    /**
     * WebSocket.
     * 
     * @author NTT DOCOMO, INC.
     * 
     */
    private class NanoWebSocket extends WebSocket implements DConnectWebSocket {

        /** KeepAlive実行用のタイマー. */
        private Timer mKeepAliveTimer;

        /** Keep-Aliveのタスク. */
        private KeepAliveTask mKeepAliveTask;

        /** セッションキー. */
        private String mSessionKey;

        public NanoWebSocket(IHTTPSession handshakeRequest) {
            super(handshakeRequest);
            mKeepAliveTask = new KeepAliveTask();
            mKeepAliveTimer = new Timer();
            mKeepAliveTimer.scheduleAtFixedRate(mKeepAliveTask, WEBSOCKET_KEEP_ALIVE_INTERVAL,
                    WEBSOCKET_KEEP_ALIVE_INTERVAL);
        }

        @Override
        public void sendEvent(String event) {
            try {
                send(event);
            } catch (IOException e) {
                mLogger.warning("Exception in the NanoWebSocket#sendEvent() method. " + e.toString());
                if (mListener != null) {
                    mListener.onError(DConnectServerError.SEND_EVENT_FAILED);
                }
            }
        }

        @Override
        protected void onPong(WebSocketFrame pongFrame) {
            mLogger.entering(getClass().getName(), "onPong", pongFrame);
            mLogger.fine(pongFrame.toString());
            synchronized (mKeepAliveTask) {
                if (mKeepAliveTask.getState() == KeepAliveState.WAITING_PONG) {
                    mKeepAliveTask.setState(KeepAliveState.GOT_PONG);
                }
            }

            mLogger.exiting(getClass().getName(), "onPong");
        }

        @Override
        protected void onMessage(WebSocketFrame messageFrame) {
            mLogger.entering(getClass().getName(), "onMessage", messageFrame);
            String jsonText = messageFrame.getTextPayload();
            if (jsonText == null || jsonText.length() == 0) {
                mLogger.exiting(getClass().getName(), "onMessage");
                return;
            }

            try {
                JSONObject json = new JSONObject(jsonText);
                String sessionKey = json.getString(WEBSOCKET_PARAM_KEY_SESSION_KEY);

                if (sessionKey != null) {
                    // 同じクライアントからは１つのセッションしか張らせない。
                    if (mSessionKey != null) {
                        mSockets.remove(mSessionKey);
                    }

                    mSessionKey = sessionKey;
                    mSockets.put(sessionKey, this);
                    mLogger.fine("websocket session key: " + sessionKey);
                }
            } catch (JSONException e) {
                mLogger.warning("Exception in the NanoWebSocket#onMessage() method." + e.toString());
            }

            mLogger.exiting(getClass().getName(), "onMessage");
        }

        @Override
        protected void onClose(CloseCode code, String reason, boolean initiatedByRemote) {

            mLogger.entering(getClass().getName(), "onClose", new Object[] { code, reason, initiatedByRemote });

            if (mSessionKey != null) {
                mSockets.remove(mSessionKey);
                mLogger.fine("WebSocket closed. Session Key : " + mSessionKey);
                mSessionKey = null;
            }
            if (mServer != null) {
                mServer.countdownWebSocket();
            }

            mKeepAliveTimer.cancel();
            mLogger.exiting(getClass().getName(), "onClose");
        }

        @Override
        protected void onException(IOException e) {
            mLogger.warning("Exception in the NanoWebSocket#onException() method. " + e.toString());
        }

        /**
         * Keep-Alive用タイマータスク
         * 
         * @author NTT DOCOMO, INC.
         * 
         */
        private class KeepAliveTask extends TimerTask {

            /** 処理状態. */
            private KeepAliveState mState;

            public KeepAliveTask() {
                setState(KeepAliveState.GOT_PONG);
            }

            /**
             * 状態を変更する.
             * 
             * @param state
             */
            public void setState(KeepAliveState state) {
                mState = state;
            }

            /**
             * 状態を取得する.
             * 
             * @return 状態
             */
            public KeepAliveState getState() {
                return mState;
            }

            @Override
            public void run() {
                try {

                    synchronized (this) {
                        if (mState == KeepAliveState.GOT_PONG) {
                            setState(KeepAliveState.WAITING_PONG);
                            ping("".getBytes());
                        } else {
                            close(CloseCode.GoingAway, "Client is dead.");
                        }
                    }

                } catch (IOException e) {
                    // 例外が発生したらタスクを終了し、タイムアウトに任せる
                    cancel();
                }
            }
        }
    }

    @Override
    public String getVersion() {
        return VERESION;
    }
}
