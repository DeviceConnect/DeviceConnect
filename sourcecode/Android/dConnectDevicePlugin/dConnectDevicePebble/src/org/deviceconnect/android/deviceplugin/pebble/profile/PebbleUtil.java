package org.deviceconnect.android.deviceplugin.pebble.profile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * プロファイルで使用するユーティリティクラス.
 * @author NTT DOCOMO, INC.
 */
final class PebbleUtil {
    /**
     * コンストラクタ.
     */
    private PebbleUtil() {
    }
    /**
     * デバイスIDをチェックする.
     * 
     * @param deviceId デバイスID
     * @return <code>deviceId</code>がテスト用デバイスIDに等しい場合はtrue、そうでない場合はfalse
     */
    public static boolean checkDeviceId(final String deviceId) {
        String regex = PebbleNetworkServceDiscoveryProfile.DEVICE_ID;
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(deviceId);
        return m.find();
    }
}
