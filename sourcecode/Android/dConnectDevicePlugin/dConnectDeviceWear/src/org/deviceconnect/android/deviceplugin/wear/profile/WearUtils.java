/*
WearUtil.java
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.wear.profile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Wear Utils.
 * 
 * @author NTT DOCOMO, INC.
 */
public final class WearUtils {
    /**
     * コンストラクタ.
     */
    private WearUtils() {
    }
    /**
     * デバイスIDをチェックする.
     * 
     * @param deviceId デバイスID
     * @return <code>deviceId</code>がテスト用デバイスIDに等しい場合はtrue、そうでない場合はfalse
     */
    public static boolean checkDeviceId(final String deviceId) {
        String regex = WearNetworkServiceDiscoveryProfile.DEVICE_ID;
        Pattern mPattern = Pattern.compile(regex);
        Matcher match = mPattern.matcher(deviceId);
        return match.find();
    }
}
