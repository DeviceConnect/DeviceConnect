/*
WearConst.java
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.wear;

/**
 * Wear Const.
 *
 * @author NTT DOCOMO, INC.
 */
public final class WearConst {

    /** Vibrationを起動. */
    public static final String DEVICE_TO_WEAR_VIBRATION_RUN = "org.deviceconnect.wear.vibration.run";

    /** Vibrationを停止. */
    public static final String DEVICE_TO_WEAR_VIBRATION_DEL = "org.deviceconnect.wear.vibration.del";

    /** Actionが開かれたら. */
    public static final String DEVICE_TO_WEAR_NOTIFICATION_OPEN = "org.deviceconnect.wear.notification.open";

    /** DeviceOrientationが開かれたら. */
    public static final String DEVICE_TO_WEAR_DEIVCEORIENTATION_REGISTER = "org.deviceconnect.wear.deivceorienatation.regist";

    /** DeviceOrientationが開かれたら. */
    public static final String DEVICE_TO_WEAR_DEIVCEORIENTATION_UNREGISTER = "org.deviceconnect.wear.deivceorienatation.unregist";

    /** WearからAndroid. */
    public static final String WERA_TO_DEVICE_DEIVCEORIENTATION_DATA = "org.deviceconnect.wear.deivceorienatation.data";

    /** DeviceId. */
    public static final String PARAM_DEVICEID = "deviceId";

    /** NotificationId. */
    public static final String PARAM_NOTIFICATIONID = "norificationId";

    /**
     * コンストラクタ.
     */
    private WearConst() {
    }
}
