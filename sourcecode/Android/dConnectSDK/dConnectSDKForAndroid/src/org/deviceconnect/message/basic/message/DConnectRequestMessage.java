/*
 DConnectRequestMessage.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.message.basic.message;

import java.net.URI;
import java.util.List;
import java.util.logging.Logger;

import org.apache.http.NameValuePair;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.utils.URIBuilder;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * リクエストメッセージ.
 * @author NTT DOCOMO, INC.
 */
public class DConnectRequestMessage extends BasicDConnectMessage {

    /**
     * シリアルバージョン.
     */
    private static final long serialVersionUID = 1L;

    /**
     * ロガーインスタンス.
     */
    private Logger mLogger = Logger.getLogger("org.deviceconnect.sdk");

    /**
     * コンストラクタ.
     */
    public DConnectRequestMessage() {
        super();
        mLogger.entering(DConnectMessage.class.getName(), "DConnectRequestMessage");
        setMethod(METHOD_GET);
        mLogger.exiting(DConnectMessage.class.getName(), "DConnectRequestMessage");
    }

    /**
     * コンストラクタ.
     * @param method メソッド
     */
    public DConnectRequestMessage(final String method) {
        mLogger.entering(DConnectMessage.class.getName(), "DConnectRequestMessage");
        setMethod(method);
        mLogger.exiting(DConnectMessage.class.getName(), "DConnectRequestMessage");
    }

    /**
     * メッセージをURIから生成する.
     *
     * @param json メッセージJSON
     * @throws JSONException JSONエラー.
     */
    public DConnectRequestMessage(final JSONObject json) throws JSONException {
        this(METHOD_GET, json);
    }

    /**
     * メッセージをURIから生成する.
     *
     * @param uri メッセージURI
     */
    public DConnectRequestMessage(final URI uri) {
        this(METHOD_GET, uri);
    }

    /**
     * メッセージをJSONから生成する.
     *
     * @param method メソッド
     * @param json メッセージJSON
     * @throws JSONException JSONエラー.
     */
    public DConnectRequestMessage(final String method, final String json) throws JSONException {
        super(json);
        mLogger.entering(DConnectMessage.class.getName(), "DConnectRequestMessage", json);
        setMethod(method);
        mLogger.exiting(DConnectMessage.class.getName(), "DConnectRequestMessage");
    }

    /**
     * メッセージをJSONから生成する.
     *
     * @param method メソッド
     * @param json メッセージJSON
     * @throws JSONException JSONエラー.
     */
    public DConnectRequestMessage(final String method, final JSONObject json) throws JSONException {
        super(json);
        mLogger.entering(DConnectMessage.class.getName(), "DConnectRequestMessage", json);
        setMethod(method);
        mLogger.exiting(DConnectMessage.class.getName(), "DConnectRequestMessage");
    }

    /**
     * メッセージをURIから生成する.
     *
     * @param method メソッド
     * @param uri メッセージURI
     */
    public DConnectRequestMessage(final String method, final URI uri) {
        super();
        mLogger.entering(DConnectMessage.class.getName(), "DConnectRequestMessage", uri);
        setMethod(method);

        // set scheme, host and port
        setSchemeName(uri.getScheme());
        setHost(uri.getHost());
        setPort(uri.getPort());

        // parse uri, and set profile, interface and attribute
        String path = uri.getPath();
        if (path.indexOf('?') > 0) {
            path = path.substring(0, path.indexOf('?'));
        }
        int sepIndexProfile = path.indexOf('/', 1);
        int sepIndexInterface = path.indexOf('/', sepIndexProfile + 1);

        String profile = null;
        String inter = null;
        String attribute = null;
        if (sepIndexProfile < 0) {
            // case: /system
            profile = path.substring(1);
        } else if (sepIndexInterface < 0) {
            // case: /notification/notify?body=message
            profile = path.substring(1, sepIndexProfile);
            attribute = path.substring(sepIndexProfile + 1, path.length());
        } else {
            // case: /system/device/information
            profile = path.substring(1, sepIndexProfile);
            inter = path.substring(sepIndexProfile + 1, sepIndexInterface);
            attribute = path.substring(sepIndexInterface + 1, path.length());
        }

        // set profile, interface and attirbute from path
        if (profile != null) {
            setProfile(profile);
        }
        if (inter != null) {
            setInterface(inter);
        }
        if (attribute != null) {
            setAttribute(attribute);
        }

        // set query parameters
        List<NameValuePair> params = (new URIBuilder(uri)).getQueryParams();
        if (params != null) {
            for (NameValuePair pair: params) {
                put(pair.getName(), pair.getValue());
            }
        }

        mLogger.exiting(DConnectMessage.class.getName(), "DConnectRequestMessage");
    }

    /**
     * コンストラクタ.
     * @param message メッセージ
     */
    public DConnectRequestMessage(final DConnectMessage message) {
        super(message);
    }

    /**
     * リクエストメソッドを設定する.
     * @param method リクエストメソッド
     */
    public void setMethod(final String method) {
        put(EXTRA_METHOD, method);
    }
    
    /**
     * API名を設定する.
     * 
     * @param api API名
     */
    public void setAPI(final String api) {
        put(EXTRA_API, api);
    }

    /**
     * プロファイル名を設定する.
     * @param profile プロファイル名.
     */
    public void setProfile(final String profile) {
        put(EXTRA_PROFILE, profile);
    }

    /**
     * インターフェースを設定する.
     * @param inter インターフェース
     */
    public void setInterface(final String inter) {
        put(EXTRA_INTERFACE, inter);
    }

    /**
     * 属性を設定する.
     * @param attribute 属性
     */
    public void setAttribute(final String attribute) {
        put(EXTRA_ATTRIBUTE, attribute);
    }

    /**
     * スキーマを設定する.
     * @param scheme スキーマ
     */
    public void setSchemeName(final String scheme) {
        put(EXTRA_SCHEME, scheme);
    }

    /**
     * ホストを設定する.
     * @param host ホスト
     */
    public void setHost(final String host) {
        put(EXTRA_HOST, host);
    }

    /**
     * ポートを背呈する.
     * @param port ポート
     */
    public void setPort(final int port) {
        put(EXTRA_PORT, port);
    }

    /**
     * リクエストメソッドを取得する.
     * @return リクエストメソッド
     */
    public String getMethod() {
        return getString(EXTRA_METHOD);
    }

    /**
     * デバイスIDを取得する.
     * @return デバイスID
     */
    public String getDeviceId() {
        return getString(EXTRA_DEVICE_ID);
    }

    /**
     * API名を取得する.
     * 
     * @return API名
     */
    public String getAPI() {
        return getString(EXTRA_API);
    }
    
    /**
     * プロファイル名を取得する.
     * @return プロファイル名
     */
    public String getProfile() {
        return getString(EXTRA_PROFILE);
    }

    /**
     * インターフェース名を取得する.
     * @return インターフェース名
     */
    public String getInterface() {
        return getString(EXTRA_INTERFACE);
    }

    /**
     * 属性名を取得する.
     * @return 属性名
     */
    public String getAttribute() {
        return getString(EXTRA_ATTRIBUTE);
    }

    /**
     * スキーマを取得する.
     * @return スキーマ
     */
    public String getScheme() {
        return getString(EXTRA_SCHEME);
    }

    /**
     * ホストを取得する.
     * @return ホスト
     */
    public String getHost() {
        return getString(EXTRA_HOST);
    }

    /**
     * ポートを取得する.
     * @return ポート
     */
    public int getPort() {
        try {
            return getInt(EXTRA_PORT);
        } catch (RuntimeException e) {
            return -1;
        }
    }

}
