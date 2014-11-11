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

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import org.deviceconnect.android.profile.NetworkServiceDiscoveryProfile;
import org.deviceconnect.profile.NetworkServiceDiscoveryProfileConstants.NetworkType;
import com.sonyericsson.extras.liveware.aef.registration.Registration.Device;
import com.sonyericsson.extras.liveware.aef.registration.Registration.DeviceColumns;

/**
 * ユーティリティクラス.
 * @author NTT DOCOMO, INC.
 */
public final class SWUtil {

    /**
     * プライベートコンストラクタ.
     */
    private SWUtil() {
    }

    /**
     * Bluetoothデバイスがペアリング中かどうか返却する.
     * 
     * @param deviceId デバイスID
     * @return BluetoothDevice
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
                if (deviceName.startsWith("SmartWatch")) {
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
     * ペアリング中SonyWatchのステータスをBundleに格納して返却する.
     * 
     * @param boundedDevice ペアリング中デバイス一覧
     * @return Bundle
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
     * SonyWatchがオンラインかどうかの判定をする.
     * 
     * @param context コンテキスト
     * @param hostAppId ホストアプリケーションネーム
     * @return boolean
     */
    public static boolean checkDeviceConnecting(final Context context, final long hostAppId) {
        Cursor cursor = null;
        try {
            String selection = DeviceColumns.HOST_APPLICATION_ID + " = " + hostAppId + " AND "
                    + DeviceColumns.ACCESSORY_CONNECTED + " = 1";
            cursor = context.getContentResolver().query(Device.URI, null, selection, null, null);
            if (cursor != null) {
                return (cursor.getCount() > 0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return false;
    }

    /**
     * ホストアプリケーションネームの返却.
     * 
     * @param deviceName デバイスネーム
     * @return ホストアプリケーションネーム
     */
    public static String toHostAppPackageName(final String deviceName) {
        if ("SmartWatch".equals(deviceName)) {
            return SWConstants.PACKAGE_SMART_WATCH;
        }
        if ("SmartWatch 2".equals(deviceName)) {
            return SWConstants.PACKAGE_SMART_WATCH_2;
        }
        return null;
    }
}
