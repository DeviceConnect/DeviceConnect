/*
 VibrationProfileConstants.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.profile;

/**
 * Vibration Profile API 定数群.<br/>
 * Vibration Profile API のパラメータ名、インタフェース名、属性名、プロファイル名を定義する。
 * @author NTT DOCOMO, INC.
 */
public interface VibrationProfileConstants extends DConnectProfileConstants {

    /**
     * プロファイル名: {@value} .
     */
    String PROFILE_NAME = "vibration";

    /**
     * 属性: {@value} .
     */
    String ATTRIBUTE_VIBRATE = "vibrate";

    /**
     * パス: {@value}.
     */
    String PATH_PROFILE = PATH_ROOT + SEPARATOR + PROFILE_NAME;

    /**
     * パス: {@value}.
     */
    String PATH_VIBRATE = PATH_PROFILE + SEPARATOR + ATTRIBUTE_VIBRATE;

    /**
     * パラメータ: {@value} .
     */
    String PARAM_PATTERN = "pattern";

}
