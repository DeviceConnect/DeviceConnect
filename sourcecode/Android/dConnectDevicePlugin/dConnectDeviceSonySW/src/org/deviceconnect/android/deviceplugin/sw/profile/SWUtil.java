/*
 SWUtil.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.sw.profile;

import java.util.Locale;
import java.util.Set;

import org.deviceconnect.android.deviceplugin.sw.SWConstants;
import org.deviceconnect.android.profile.NetworkServiceDiscoveryProfile;
import org.deviceconnect.profile.NetworkServiceDiscoveryProfileConstants.NetworkType;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;

/**
 * ユーティリティクラス.
 * @author NTT DOCOMO, INC.
 */
public final class SWUtil {

    /**
     * プライベートコンストラクタ.
     * ユーティリティクラスのため、インスタンスを生成させない.
     */
    private SWUtil() {
    }

    /**
     * ペアリング済みのBluetoothデバイス一覧上で指定されたデバイスを検索する.
     * 
     * @param deviceId デバイスID
     * @return BluetoothDevice 指定されたデバイスがペアリング中であれば対応する{@link BluetoothDevice}、そうでない場合はnull
     */
    public static BluetoothDevice findSmartWatch(final String deviceId) {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            return null;
        }
        Set<BluetoothDevice> bondedDevices = adapter.getBondedDevices();
        if (bondedDevices.size() > 0) {
            for (BluetoothDevice device : bondedDevices) {
                String deviceName = device.getName();
                if (deviceName.startsWith(SWConstants.DEVICE_NAME_PREFIX)) {
                    String otherDeviceId = device.getAddress().replace(":", "").toLowerCase(Locale.ENGLISH);
                    if (otherDeviceId.equals(deviceId)) {
                        return device;
                    }
                }
            }
        }
        return null;
    }

    /**
     * ペアリング済みのSonyWatchのステータスを{@link Bundle}に格納して返却する.
     * 
     * @param boundedDevice ペアリング済みデバイス一覧
     * @return {@link Bundle}インスタンス
     */
    public static Bundle toBundle(final BluetoothDevice boundedDevice) {

        String address = boundedDevice.getAddress();
        String deviceId = address.replace(":", "").toLowerCase(Locale.ENGLISH);
        Bundle result = new Bundle();
        result.putString(NetworkServiceDiscoveryProfile.PARAM_ID, deviceId);
        result.putString(NetworkServiceDiscoveryProfile.PARAM_NAME, boundedDevice.getName());
        result.putString(NetworkServiceDiscoveryProfile.PARAM_TYPE, NetworkType.BLUETOOTH.getValue());
        result.putBoolean(NetworkServiceDiscoveryProfile.PARAM_ONLINE, true);

        return result;
    }

    /**
     * ホストアプリケーションネームの返却.
     * 
     * @param deviceName デバイスネーム
     * @return ホストアプリケーションネーム
     */
    public static String toHostAppPackageName(final String deviceName) {
        if (SWConstants.DEVICE_NAME_SMART_WATCH.equals(deviceName)) {
            return SWConstants.PACKAGE_SMART_WATCH;
        }
        if (SWConstants.DEVICE_NAME_SMART_WATCH_2.equals(deviceName)) {
            return SWConstants.PACKAGE_SMART_WATCH_2;
        }
        return null;
    }
}
