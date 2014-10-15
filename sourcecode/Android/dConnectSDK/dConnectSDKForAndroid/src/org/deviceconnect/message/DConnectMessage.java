/*
 DConnectMessage.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.message;

import java.util.List;
import java.util.Map;

/**
 * メッセージ.
 * @author NTT DOCOMO, INC.
 */
public interface DConnectMessage extends Map<String, Object> {

    /**
     * GETメソッド.
     */
    String METHOD_GET = "GET";

    /**
     * POSTメソッド.
     */
    String METHOD_POST = "POST";

    /**
     * PUTメソッド.
     */
    String METHOD_PUT = "PUT";

    /**
     * DELETEメソッド.
     */
    String METHOD_DELETE = "DELETE";

    /**
     * WebSocketメソッド.
     */
    String METHOD_WS = "WS";

    /**
     * エクストラ:method.
     */
    String EXTRA_METHOD = "method";

    /**
     * エクストラ:requestCode.
     */
    String EXTRA_REQUEST_CODE = "requestCode";

    /**
     * エクストラ:deviceId.
     */
    String EXTRA_DEVICE_ID = "deviceId";

    /**
     * エクストラ:receiver.
     */
    String EXTRA_RECEIVER = "receiver";

    /**
     * エクストラ:{@value}.
     */
    String EXTRA_API = "api";

    /**
     * エクストラ:profile.
     */
    String EXTRA_PROFILE = "profile";

    /**
     * エクストラ:interface.
     */
    String EXTRA_INTERFACE = "interface";

    /**
     * エクストラ:attribute.
     */
    String EXTRA_ATTRIBUTE = "attribute";

    /**
     * エクストラ:value.
     */
    String EXTRA_VALUE = "value";

    /**
     * エクストラ:result.
     */
    String EXTRA_RESULT = "result";

    /**
     * エクストラ:errorCode.
     */
    String EXTRA_ERROR_CODE = "errorCode";

    /**
     * エクストラ:errorMessage.
     */
    String EXTRA_ERROR_MESSAGE = "errorMessage";

    /**
     * エクストラ:sessionKey.
     */
    String EXTRA_SESSION_KEY = "sessionKey";

    /**
     * エクストラ:accessToken.
     */
    String EXTRA_ACCESS_TOKEN = "accessToken";

    /**
     * エクストラ:websocket.
     */
    String EXTRA_WEBSOCKET = "websocket";

    /**
     * エクストラ:scheme.
     */
    String EXTRA_SCHEME = "_scheme";

    /**
     * エクストラ:host.
     */
    String EXTRA_HOST = "_host";

    /**
     * エクストラ:port.
     */
    String EXTRA_PORT = "_port";

    /**
     * デフォルトAPI.
     */
    String DEFAULT_API = "gotapi";

    /**
     * 結果:OK.
     */
    int RESULT_OK = 0;

    /**
     * 結果:エラー.
     */
    int RESULT_ERROR = 1;

    /**
     * Stringを取得する.
     * 
     * @param key キー
     * @return 値
     */
    String getString(String key);

    /**
     * intを取得する.
     * 
     * @param key キー
     * @return 値
     */
    int getInt(String key);

    /**
     * booleanを取得する.
     * 
     * @param key キー
     * @return 値
     */
    boolean getBoolean(String key);

    /**
     * doubleを取得する.
     * 
     * @param key キー
     * @return 値
     */
    float getFloat(String key);

    /**
     * List<Object>を取得する.
     * 
     * @param key キー
     * @return 値
     */
    List<Object> getList(String key);

    /**
     * メッセージを文字列にして返却する.
     * 
     * @param indent インデント
     * @return メッセージを表す文字列
     */
    String toString(int indent);

    /**
     * Device Connectに定義されているエラーコード、メッセージ定数群.
     */
    enum ErrorCode {
        /**
         * 原因不明のエラー.
         */
        UNKNOWN(1, "Unknown error was encountered."),
        /**
         * サポートされていないプロファイルにアクセスされた.
         */
        NOT_SUPPORT_PROFILE(2, "Non-supported Profile was accessed."),
        /**
         * サポートされていないアクションが指定された.
         */
        NOT_SUPPORT_ACTION(3, "Non-supported HTTP method was used."),
        /**
         * サポートされていない属性・インターフェースが指定された.
         */
        NOT_SUPPORT_ATTRIBUTE(4, "Non-supported attribute was used."),
        /**
         * deviceidが設定されていない.
         */
        EMPTY_DEVICE_ID(5, "Device ID is required."),
        /**
         * デバイスが発見できなかった.
         */
        NOT_FOUND_DEVICE(6, "Device was not found."),
        /**
         * タイムアウトが発生した.
         */
        TIMEOUT(7, "Response timeout."),
        /**
         * 未知のインターフェース・属性にアクセスされた.
         */
        UNKNOWN_ATTRIBUTE(8, "Illegal or nonexistent attribute or interface was accessed."),
        /**
         * バッテリー低下で操作不能.
         */
        LOW_BATTERY(9, "No enough battery to control the device."),
        /**
         * 不正なパラメータを受信した.
         */
        INVALID_REQUEST_PARAMETER(10, "Request parameters are invalid."),

        /**
         * 認証エラー.
         */
        AUTHORIZATION(11, "Authorization error."),
        /**
         * アクセストークンの有効期限切れ.
         */
        EXPIRED_ACCESS_TOKEN(12, "Access token expired."),
        /**
         * アクセストークンが設定されていない.
         */
        EMPTY_ACCESS_TOKEN(13, "Access token was required."),
        /**
         * スコープ外にアクセス要求がなされた.
         */
        SCOPE(14, "Request is out of scope."),
        
        /**
         * 認証時にclientIdが発見できなかった.
         */
        NOT_FOUND_CLIENT_ID(15, "clientId was not found."),
        
        /**
         * デバイスの状態異常エラー.
         */
        ILLEGAL_DEVICE_STATE(16, "State of device is illegality."),
        /**
         * デバイスの状態異常エラー.
         */
        ILLEGAL_SERVER_STATE(17, "State of server is illegality.");

        /**
         * エラーコード.
         */
        int mCode;

        /**
         * デフォルトエラーメッセージ.
         */
        String mMessage;

        /**
         * エラーコードとデフォルトエラーメッセージを設定する.
         * 
         * @param code エラーコード
         * @param message エラーメッセージ
         */
        private ErrorCode(final int code, final String message) {
            this.mCode = code;
            this.mMessage = message;
        }

        @Override
        public String toString() {
            return mMessage;
        }

        /**
         * エラーコードを取得する.
         * 
         * @return エラーコード
         */
        public int getCode() {
            return mCode;
        }
        
        /**
         * 指定されたエラーコードからErrorCodeオブジェクトを取得する.
         * 
         * @param code エラーコード
         * @return ErrorCodeのインスタンス
         */
        public static ErrorCode getInstance(final int code) {
            
            for (ErrorCode eCode : values()) {
                if (eCode.mCode == code) {
                    return eCode;
                }
            }
            
            return UNKNOWN;
        }
    }

}
