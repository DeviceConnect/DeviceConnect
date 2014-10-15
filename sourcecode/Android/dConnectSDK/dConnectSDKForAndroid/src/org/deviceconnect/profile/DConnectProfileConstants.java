/*
 DConnectProfileConstants.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.profile;

import org.deviceconnect.message.DConnectMessage;

/**
 * Profile 共通の定数を定義する.
 * @author NTT DOCOMO, INC.
 */
public interface DConnectProfileConstants {

    /**
     * パラメータ: {@value} .
     */
    String PARAM_DEVICE_ID = "deviceId";

    /**
     * パラメータ: {@value} .
     */
    String PARAM_PROFILE = "profile";

    /**
     * パラメータ: {@value} .
     */
    String PARAM_SESSION_KEY = "sessionKey";
    
    /**
     * パラメータ: {@value} .
     */
    String PARAM_URI = "uri";

    /**
     * セパレータ:{@value}.
     */
    String SEPARATOR = "/";
    
    /**
     * パス: {@value}.
     */
    String PATH_ROOT = SEPARATOR + DConnectMessage.DEFAULT_API;
}
