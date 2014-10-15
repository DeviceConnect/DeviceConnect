/*
 DConnectResponseMessage.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.message.basic.message;

import java.util.logging.Logger;

import org.deviceconnect.message.DConnectMessage;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * レスポンスメッセージ.
 * @author NTT DOCOMO, INC.
 */
public class DConnectResponseMessage extends BasicDConnectMessage {

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
    public DConnectResponseMessage() {
        super();
        mLog.entering(DConnectMessage.class.getName(), "DConnectResonseMessage");
        mLog.exiting(DConnectMessage.class.getName(), "DConnectResonseMessage");
    }

    /**
     * 結果コードを指定してメッセージを生成する.
     * @param result 結果コード
     */
    public DConnectResponseMessage(final int result) {
        super();
        setResult(result);
        mLog.entering(DConnectMessage.class.getName(), "DConnectResonseMessage");
        mLog.exiting(DConnectMessage.class.getName(), "DConnectResonseMessage");
    }

    /**
     * メッセージをJSONから生成する.
     *
     * @param json メッセージJSON
     * @throws JSONException JSONエラー.
     */
    public DConnectResponseMessage(final String json) throws JSONException {
        super(json);
        mLog.entering(DConnectMessage.class.getName(), "DConnectResonseMessage", json);
        mLog.exiting(DConnectMessage.class.getName(), "DConnectResonseMessage");
    }

    /**
     * メッセージをJSONから生成する.
     *
     * @param json メッセージJSON
     * @throws JSONException JSONエラー.
     */
    public DConnectResponseMessage(final JSONObject json) throws JSONException {
        super(json);
        mLog.entering(DConnectMessage.class.getName(), "DConnectResonseMessage", json);
        mLog.exiting(DConnectMessage.class.getName(), "DConnectResonseMessage");
    }

    /**
     * コンストラクタ.
     * @param message メッセージ
     */
    public DConnectResponseMessage(final DConnectMessage message) {
        super(message);
    }

    /**
     * デバイスIDを取得する.
     * @return デバイスID
     */
    public String getDeviceId() {
        return getString(EXTRA_DEVICE_ID);
    }

    /**
     * デバイスIDを設定する.
     * @param id デバイスID
     */
    public void setDeviceId(final String id) {
        put(EXTRA_DEVICE_ID, id);
    }

    /**
     * 結果コードを取得する.
     * @return 結果コード
     */
    public int getResult() {
        return getInt(EXTRA_RESULT);
    }

    /**
     * 結果コードを設定する.
     * @param result 結果コード
     */
    public void setResult(final int result) {
        put(EXTRA_RESULT, result);
    }

    /**
     * エラーコードを取得する.
     * @return エラーコード
     */
    public int getErrorCode() {
        return getInt(EXTRA_ERROR_CODE);
    }

    /**
     * エラーコードを設定する.
     * @param error エラーコード
     */
    public void setErrorCode(final int error) {
        put(EXTRA_RESULT, error);
    }

    /**
     * エラーメッセージを取得する.
     * @return エラーメッセージ
     */
    public String getErrorMessage() {
        return getString(EXTRA_ERROR_MESSAGE);
    }

    /**
     * エラーメッセージを設定する.
     * @param message エラーメッセージ
     */
    public void setErrorMessage(final String message) {
        put(EXTRA_RESULT, message);
    }

}
