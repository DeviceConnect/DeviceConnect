/*
 DConnectEventMessage.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.message.basic.message;

import java.util.logging.Logger;

import org.deviceconnect.message.DConnectMessage;
import org.json.JSONException;

/**
 * レスポンスメッセージ.
 * @author NTT DOCOMO, INC.
 */
public class DConnectEventMessage extends BasicDConnectMessage {

    /**
     * シリアルバージョン.
     */
    private static final long serialVersionUID = 1L;

    /**
     * ロガーインスタンス.
     */
    private Logger mLog = Logger.getLogger("org.deviceconnect.sdk");

    /**
     * コンストラクタ.
     */
    public DConnectEventMessage() {
        super();
        mLog.entering(DConnectMessage.class.getName(), "DConnectEventMessage");
        mLog.exiting(DConnectMessage.class.getName(), "DConnectEventMessage");
    }

    /**
     * メッセージをJSONから生成する.
     * 
     * @param json メッセージJSON
     * @throws JSONException JSONエラー.
     */
    public DConnectEventMessage(final String json) throws JSONException {
        super(json);
        mLog.entering(DConnectMessage.class.getName(), "DConnectEventMessage", json);
        mLog.exiting(DConnectMessage.class.getName(), "DConnectEventMessage");
    }

    /**
     * コンストラクタ.
     * 
     * @param message メッセージ
     */
    public DConnectEventMessage(final DConnectMessage message) {
        super(message);
    }

    /**
     * プロファイル名を取得する.
     * 
     * @return プロファイル名
     */
    public String getProfile() {
        return getString(DConnectMessage.EXTRA_PROFILE);
    }

    /**
     * プロファイル名を設定する.
     * 
     * @param profile プロファイル名
     */
    public void setProfile(final String profile) {
        put(DConnectMessage.EXTRA_PROFILE, profile);
    }

    /**
     * インターフェース名を取得する.
     * 
     * @return インターフェース名
     */
    public String getInterface() {
        return getString(DConnectMessage.EXTRA_INTERFACE);
    }

    /**
     * インターフェース名を設定する.
     * 
     * @param inter インターフェース名
     */
    public void setInterface(final String inter) {
        put(DConnectMessage.EXTRA_INTERFACE, inter);
    }
    
    /**
     * 属性名を取得する.
     * 
     * @return 属性名
     */
    public String getAttribute() {
        return getString(DConnectMessage.EXTRA_ATTRIBUTE);
    }
    
    /**
     * アトリビュート名を設定する.
     * 
     * @param attribute アトリビュート名
     */
    public void setAttribute(final String attribute) {
        put(DConnectMessage.EXTRA_ATTRIBUTE, attribute);
    }

    /**
     * セッションキーを取得する.
     * 
     * @return セッションキー
     */
    public String getSessionKey() {
        return getString(DConnectMessage.EXTRA_SESSION_KEY);
    }
    
    /**
     * セッションキーを設定する.
     * 
     * @param sessionKey セッションキー
     */
    public void setSessionKey(final String sessionKey) {
        put(DConnectMessage.EXTRA_SESSION_KEY, sessionKey);
    }
    
    /**
     * デバイスIDを取得する.
     * 
     * @return デバイスID
     */
    public String getDeviceId() {
        return getString(DConnectMessage.EXTRA_DEVICE_ID);
    }
    
    /**
     * デバイスIDを設定する.
     * 
     * @param deviceId デバイスID
     */
    public void setDeviceId(final String deviceId) {
        put(DConnectMessage.EXTRA_DEVICE_ID, deviceId);
    }
}
