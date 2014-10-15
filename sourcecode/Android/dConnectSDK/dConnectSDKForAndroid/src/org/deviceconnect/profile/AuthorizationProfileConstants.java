/*
 AuthorizationProfileConstants.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.profile;


/**
 * Authorization Profile API 定数群.<br/>
 * Authorization Profile API のパラメータ名、インタフェース名、属性名、プロファイル名を定義する。
 *
 * @author NTT DOCOMO, INC.
 */
public interface AuthorizationProfileConstants extends DConnectProfileConstants {

    /**
     * プロファイル名: {@value}.
     */
    String PROFILE_NAME = "authorization";

    /** 
     * 属性: {@value}.
     */
    String ATTRIBUTE_CREATE_CLIENT = "create_client";

    /** 
     * 属性: {@value}.
     */
    String ATTRIBUTE_REQUEST_ACCESS_TOKEN = "request_accesstoken";

    /**
     * パス: {@value}.
     */
    String PATH_PROFILE = PATH_ROOT + SEPARATOR + PROFILE_NAME;

    /**
     * パス: {@value}.
     */
    String PATH_REQUEST_CREATE_CLIENT = PATH_PROFILE + SEPARATOR + ATTRIBUTE_CREATE_CLIENT;

    /**
     * パス: {@value}.
     */
    String PATH_REQUEST_ACCESS_TOKEN = PATH_PROFILE + SEPARATOR + ATTRIBUTE_REQUEST_ACCESS_TOKEN;

    /**
     * パラメータ: {@value}.
     */
    String PARAM_PACKAGE = "package";

    /**
     * パラメータ: {@value}.
     */
    String PARAM_CLIENT_ID = "clientId";

    /**
     * パラメータ: {@value}.
     */
    String PARAM_CLIENT_SECRET = "clientSecret";

    /**
     * パラメータ: {@value}.
     */
    String PARAM_GRANT_TYPE = "grantType";

    /**
     * パラメータ: {@value}.
     */
    String PARAM_SCOPE = "scope";

    /**
     * パラメータ: {@value}.
     */
    String PARAM_SCOPES = "scopes";

    /**
     * パラメータ: {@value}.
     */
    String PARAM_APPLICATION_NAME = "applicationName";

    /**
     * パラメータ: {@value}.
     */
    String PARAM_SIGNATURE = "signature";

    /**
     * パラメータ: {@value}.
     */
    String PARAM_ACCESS_TOKEN = "accessToken";

    /**
     * パラメータ: {@value}.
     */
    String PARAM_EXPIRE_PERIOD = "expirePeriod";

    /**
     * grantType.
     */
    enum GrantType {
        /**
         * Defined in 4.1 Authorization Code Grant.
         */
        AUTHORIZATION_CODE("authorization_code"),

        /**
         * Defined in 4.3 Resource Owner Password Credentials Grant.
         */
        PASSWORD("password"),

        /**
         * Defined in 4.4 Client Credentials Grant.
         */
        CLIENT_CREDENTIALS("client_credentials"),

        /**
         * Defined in 6 Refreshing an Access Token.
         */
        REFRESH_TOKE("refresh_token");

        /**
         * 定義値.
         */
        private String mValue;

        /**
         * 指定された定義値をもつ定数を宣言します.
         * 
         * @param value 定義値
         */
        private GrantType(final String value) {
            this.mValue = value;
        }

        /**
         * 定義値を取得する.
         * 
         * @return 定義値
         */
        public String getValue() {
            return mValue;
        }
    };
}
