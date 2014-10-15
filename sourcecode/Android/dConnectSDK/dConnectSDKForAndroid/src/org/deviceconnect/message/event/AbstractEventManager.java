/*
 AbstractEventManager.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.message.event;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.EnglishReasonPhraseCatalog;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.util.EntityUtils;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.utils.URIBuilder;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * イベント管理の基本機能を提供するクラス.
 * このクラスのサブクラスはリクエストの実行、接続解除を実装しなければならない。
 * 
 * @author NTT DOCOMO, INC.
 */
public abstract class AbstractEventManager {

    /**
     * イベント受信ハンドラーマップ. リクエストコードをキーにし、ハンドラーを保持する。
     */
    private Map<String, EventHandler> mHandlers;
    
    /**
     * ロガー.
     */
    private Logger mLogger = Logger.getLogger("org.deviceconnect.sdk");

    /**
     * セッションキー.
     */
    private String mSessionKey;

    /**
     * EventManagerを生成する.
     */
    public AbstractEventManager() {
        mHandlers = new ConcurrentHashMap<String, EventHandler>();
    }

    /**
     * 指定されたキーでハンドラーをキャッシュする.
     * 
     * @param key キー
     * @param handler ハンドラー
     */
    private void addHandler(final String key, final EventHandler handler) {
        if (key == null) {
            throw new IllegalArgumentException("requestCode must no be null.");
        } else if (handler == null) {
            throw new IllegalArgumentException("handler must no be null.");
        }
        mHandlers.put(key, handler);
    }

    /**
     * 指定されたキーに紐づくハンドラーを削除する.
     * 
     * @param key キー
     */
    private void removeHandler(final String key) {
        if (key == null) {
            throw new IllegalArgumentException("requestCode must no be null.");
        }
        mHandlers.remove(key);
    }

    /**
     * セッションキーを設定する.
     * 
     * @param sessionKey セッションキー
     */
    protected void setSessionKey(final String sessionKey) {
        mSessionKey = sessionKey;
    }

    /**
     * セッションキーを取得する.
     * 
     * @return セッションキー
     */
    protected String getSessionKey() {
        return mSessionKey;
    }
    
    /**
     * 受信したイベントデータをハンドラに送信する.
     * 
     * @param message イベントデータ
     * @throws JSONException JSONの解析に失敗した場合スローされる
     */
    protected void sendEvent(final JSONObject message) throws JSONException {
        String deviceId = message.getString(DConnectMessage.EXTRA_DEVICE_ID);
        String profile = message.getString(DConnectMessage.EXTRA_PROFILE);
        String inter = null;
        if (message.has(DConnectMessage.EXTRA_INTERFACE)) {
            inter = message.getString(DConnectMessage.EXTRA_INTERFACE);
        }
        String attribute = message.getString(DConnectMessage.EXTRA_ATTRIBUTE);
        String key = getKey(profile, inter, attribute, deviceId);
        EventHandler handler = null;
        synchronized (this) {
            handler = mHandlers.get(key);
        }
        if (handler != null) {
            handler.onEvent(message);
        }
    }

    /**
     * 指定されたURIBuilderで生成されるイベントAPIへの登録を行う. イベントの登録が正常に受理された場合、指定されたハンドラにイベント通知が
     * 送信されるようになる。
     * 
     * @param builder 登録するイベントAPIへのURIを作成するためのパラメータ設定を行ったビルダー
     * @param handler イベントを受信するハンドラ
     * @return イベント登録依頼の結果
     * @throws IOException リクエストの作成に失敗した場合にスローされる
     * 
     */
    public HttpResponse registerEvent(final URIBuilder builder, final EventHandler handler) 
            throws IOException {
        
        if (builder == null) {
            throw new IllegalArgumentException("builder must not be null.");
        } else if (handler == null) {
            throw new IllegalArgumentException("handler must not be null.");
        }
        
        HttpPut request;
        String key;
        try {
            List<NameValuePair> params = builder.getQueryParams();
            key = getKey(builder);
            // クエリストリングを消すためにnullを設定。パラメータはbodyで送る。
            builder.setParameters(null);
            request = new HttpPut(builder.build());
            request.setEntity(new UrlEncodedFormEntity(params));
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URI parameter.");
        }
        
        HttpResponse response = execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == HttpStatus.SC_OK) {
            String entity = EntityUtils.toString(response.getEntity(), "UTF-8");
            try {
                JSONObject json = new JSONObject(entity);
                if (json != null) {
                    int result = json.getInt(DConnectMessage.EXTRA_RESULT);
                    if (result == DConnectMessage.RESULT_OK) {
                        addHandler(key, handler);
                    }
                }
            } catch (JSONException e) {
                // JSONのパースに失敗した場合はresponseをそのまま返し、処理は呼び出し元に任せる
                mLogger.warning("AbstractEventManager#registerEvent. Invalid response. : " + e.getMessage());
            }
        }
        
        return response;
    }

    /**
     * 指定されたURIBuilderで生成されるイベントAPIへの解除を行う. イベントの解除が正常に受理された場合
     * {@link #registerEvent(URIBuilder, EventHandler)}で登録したハンドラが解除される。
     * 
     * @param builder 解除するイベントAPIへのURIを作成するためのパラメータ設定を行ったビルダー
     * @return イベント解除依頼の結果
     * @throws IOException リクエストの作成に失敗した場合にスローされる
     */
    public HttpResponse unregisterEvent(final URIBuilder builder) throws IOException {

        if (builder == null) {
            throw new IllegalArgumentException("builder must not be null.");
        }
        
        HttpDelete request;
        String key;
        try {
            List<NameValuePair> params = builder.getQueryParams();
            key = getKey(builder);
            builder.setParameters(params);
            request = new HttpDelete(builder.build());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URI parameter.");
        }
        
        HttpResponse response = execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == HttpStatus.SC_OK) {
            String entity = EntityUtils.toString(response.getEntity(), "UTF-8");
            try {
                JSONObject json = new JSONObject(entity);
                if (json != null) {
                    int result = json.getInt(DConnectMessage.EXTRA_RESULT);
                    if (result == DConnectMessage.RESULT_OK) {
                        removeHandler(key);
                    }
                }
            } catch (JSONException e) {
                // JSONのパースに失敗した場合はresponseをそのまま返し、処理は呼び出し元に任せる
                mLogger.warning("AbstractEventManager#unregisterEvent. Invalid response. : " + e.getMessage());
            }
        }
        
        return response;
    }
    
    /**
     * パラメータからdeviceIdを取り出す.
     * 
     * @param params パラメータ
     * @return デバイスID
     */
    private String getDeviceId(final List<NameValuePair> params) {
        
        String deviceId = null;
        if (params == null || params.size() == 0) {
            throw new IllegalArgumentException("No parameters. You need to set some parameters to register event.");
        } else {
            for (NameValuePair pair : params) {
                if (pair.getName().equals(DConnectMessage.EXTRA_DEVICE_ID)) {
                    deviceId = pair.getValue();
                    break;
                }
            }
            if (deviceId == null) {
                throw new IllegalArgumentException("No deviceId. Set it.");
            }
        }
        return deviceId;
    }
    
    /**
     * キーを取得する.
     * 
     * @param builder URIビルダー
     * @return キー
     */
    private String getKey(final URIBuilder builder) {
        String deviceId = getDeviceId(builder.getQueryParams());
        return getKey(builder.getProfile(), builder.getInterface(), builder.getAttribute(), deviceId);
    }
    
    /**
     * キーを取得する.
     * 
     * @param profile プロファイル名
     * @param inter インターフェース名
     * @param attribute アトリビュート名
     * @param deviceId デバイスID
     * @return キー
     */
    private String getKey(final String profile, final String inter, final String attribute, 
            final String deviceId) {
        String tmpInter = inter != null ? inter : "";
        return deviceId + "/" + profile + "/" + tmpInter + "/" + attribute;
    }

    /**
     * イベント受信用のセッションを切断する.
     */
    public abstract void disconnect();

    /**
     * 指定されたリクエストを実行し、レスポンスを返す.
     * 
     * @param request 実行するリクエスト
     * @return レスポンス
     * @throws IOException リクエスト実行時にエラーがあった場合スローされる
     */
    protected abstract HttpResponse execute(HttpUriRequest request) throws IOException;
    
    /**
     * 指定されたレスポンスのコピーを返す.
     * 
     * @param response レスポンスデータ
     * @return コピーしたレスポンス
     * @throws IOException コピーに失敗した場合スローされる
     */
    protected HttpResponse copyResponse(final HttpResponse response) throws IOException {
        int code = response.getStatusLine().getStatusCode();
        HttpResponse retRes = new BasicHttpResponse(
                response.getProtocolVersion(), code,
                EnglishReasonPhraseCatalog.INSTANCE.getReason(code, null));
        retRes.setHeaders(response.getAllHeaders());
        retRes.setEntity(new StringEntity(EntityUtils.toString(response.getEntity(), "UTF-8")));
        return retRes;
    }

}
