/*
 AuthorizatonException.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.localoauth.exception;

import org.restlet.ext.oauth.OAuthError;
import org.restlet.ext.oauth.OAuthException;

/**
 * LocalOAuthAPIが返す例外.
 * @author NTT DOCOMO, INC.
 */
public class AuthorizatonException extends Exception {

    /** 例外コード. */
    public static final int CLIENT_NOT_FOUND = 1;
    /** 例外コード. */
    public static final int ACCESS_DENIED = 2;
    /** 例外コード. */
    public static final int INVALID_CLIENT = 3;
    /** 例外コード. */
    public static final int INVALID_GRANT = 4;
    /** 例外コード. */
    public static final int INVALID_REQUEST = 5;
    /** 例外コード. */
    public static final int INVALID_SCOPE = 6;
    /** 例外コード. */
    public static final int UNAUTHORIZED_CLIENT = 7;
    /** 例外コード. */
    public static final int UNSUPPORTED_GRANT_TYPE = 8;
    /** 例外コード. */
    public static final int UNSUPPORTED_RESPONSE_TYPE = 9;
    /** 例外コード. */
    public static final int SERVER_ERROR = 10;
    /** 例外コード. */
    public static final int INVALID_TOKEN = 11;
    /** 例外コード. */
    public static final int INSUFFICIENT_SCOPE = 12;
    /** 例外コード. */
    public static final int CLIENT_COUNTS_IS_FULL = 13;
    /** 例外コード. */
    public static final int SQLITE_ERROR = 14;
    
    /**
     * シリアル番号.<br>
     * Eclipseの警告回避用。シリアライズしないので固定値を設定。
     */
    private static final long serialVersionUID = 1L;

    
    /**
     * エラーコード.
     */
    private int mErrorCode = 0;
    
    /**
     * コンストラクタ.
     * @param errorCode エラーコード
     */
    public AuthorizatonException(final int errorCode) {
        mErrorCode = errorCode;
    }

    /**
     * コンストラクタ.
     * @param e OAuthException例外
     */
    public AuthorizatonException(final OAuthException e) {
        OAuthError error = e.getError();
        mErrorCode = convertErrorCode(error);
    }

    /**
     * OAuthErrorのエラーコードを変換する.
     * @param error OAuthErrorのエラーコード
     * @return AuthorizationExceptionのエラーコード
     */
    private int convertErrorCode(final OAuthError error) {
        if (error == OAuthError.access_denied) {
            return ACCESS_DENIED;
        } else if (error == OAuthError.invalid_client) {
            return INVALID_CLIENT;
        } else if (error == OAuthError.invalid_grant) {
            return INVALID_GRANT;
        } else if (error == OAuthError.invalid_request) {
            return INVALID_REQUEST;
        } else if (error == OAuthError.invalid_scope) {
            return INVALID_SCOPE;
        } else if (error == OAuthError.unauthorized_client) {
            return UNAUTHORIZED_CLIENT;
        } else if (error == OAuthError.unsupported_grant_type) {
            return UNSUPPORTED_GRANT_TYPE;
        } else if (error == OAuthError.unsupported_response_type) {
            return UNSUPPORTED_RESPONSE_TYPE;
        } else if (error == OAuthError.server_error) {
            return SERVER_ERROR;
        } else if (error == OAuthError.invalid_token) {
            return INVALID_TOKEN;
        } else if (error == OAuthError.insufficient_scope) {
            return INSUFFICIENT_SCOPE;
        }
        
        throw new RuntimeException("error is unknown.");
    }

    /**
     * エラーコード取得.
     * @return  エラーコード
     */
    public int getErrorCode() {
        return mErrorCode;
    }
    
}
