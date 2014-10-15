/*
 DeviceOrientationProfileConstants.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.profile;

/**
 * Device Orientation Profile API 定数群.<br/>
 * Device Orientation Profile API のパラメータ名、インタフェース名、属性名、プロファイル名を定義する。
 * 
 * @author NTT DOCOMO, INC.
 */
public interface DeviceOrientationProfileConstants extends DConnectProfileConstants {

    /**
     * プロファイル名: {@value} .
     */
    String PROFILE_NAME = "deviceorientation";

    /**
     * 属性: {@value} .
     */
    String ATTRIBUTE_ON_DEVICE_ORIENTATION = "ondeviceorientation";

    /**
     * パス: {@value}.
     */
    String PATH_PROFILE = PATH_ROOT + SEPARATOR + PROFILE_NAME;

    /**
     * パス: {@value}.
     */
    String PATH_ON_DEVICE_ORIENTATION = PATH_PROFILE + SEPARATOR + ATTRIBUTE_ON_DEVICE_ORIENTATION;

    /**
     * パラメータ: {@value} .
     */
    String PARAM_ORIENTATION = "orientation";

    /**
     * パラメータ: {@value} .
     */
    String PARAM_ACCELERATION = "acceleration";

    /**
     * パラメータ: {@value} .
     */
    String PARAM_X = "x";

    /**
     * パラメータ: {@value} .
     */
    String PARAM_Y = "y";

    /**
     * パラメータ: {@value} .
     */
    String PARAM_Z = "z";

    /**
     * パラメータ: {@value} .
     */
    String PARAM_ROTATION_RATE = "rotationRate";

    /**
     * パラメータ: {@value} .
     */
    String PARAM_ALPHA = "alpha";

    /**
     * パラメータ: {@value} .
     */
    String PARAM_BETA = "beta";

    /**
     * パラメータ: {@value} .
     */
    String PARAM_GAMMA = "gamma";

    /**
     * パラメータ: {@value} .
     */
    String PARAM_INTERVAL = "interval";

    /**
     * パラメータ: {@value} .
     */
    String PARAM_ACCELERATION_INCLUDING_GRAVITY = "accelerationIncludingGravity";
    
}
