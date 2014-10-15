/*
 WiFiUtil.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.irkit.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

/**
 * WiFi周りのユーティリティクラス.
 * @author NTT DOCOMO, INC.
 */
public final class WiFiUtil {

    /**
     * ユーティリティクラスのためprivate.
     */
    private WiFiUtil() {
    }

    /**
     * 現在のSSIDを取得する.
     * 
     * @param context コンテキストオブジェクト
     * @return SSID文字列。WiFiでは無い場合はnullを返す。
     */
    public static String getCurrentSSID(final Context context) {

        String ssid = null;
        
        if (isOnWiFi(context)) {
            WifiManager wifi = (WifiManager) context.getSystemService(android.content.Context.WIFI_SERVICE);
            ssid = wifi.getConnectionInfo().getSSID();

            if (ssid != null) {
                // ダブルクォーテーションを含んでいるので外す
                ssid = ssid.replaceAll("\"", "");
            }
        }

        return ssid;
    }

    /**
     * WiFiにつながっているかどうかチェックする.
     * 
     * @param context コンテキストオブジェクト
     * @return つながっている場合true、その他はfalseを返す。
     */
    public static boolean isOnWiFi(final Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();

        if (ni == null || !ni.isConnected() || (ni.getType() != ConnectivityManager.TYPE_WIFI)) {
            return false;
        }

        return true;
    }
    
    /**
     * 
     * 
     * @param context context.
     * @param oldSSID 古い ssid.
     * @return SSID が変更になったかどうか.
     */
    public static boolean isChangedSSID(final Context context, final String oldSSID) {
        String current = getCurrentSSID(context);
        if (current == null) {
            return (oldSSID != null);
        }
        return !current.equals(oldSSID);
    }

}
