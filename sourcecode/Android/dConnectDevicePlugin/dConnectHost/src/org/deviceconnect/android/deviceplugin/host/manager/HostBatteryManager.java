/*
 HostBatteryManager.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.host.manager;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

/**
 * バッテリー関連の値の処理と保持.
 */
public class HostBatteryManager {

    /** バッテリーの状態. */
    private static int statusBattery;

    /** プラグの状態. */
    private static int statusPlugged;

    /** バッテリーのレベル. */
    private static int valueLevel;

    /** バッテリーのスケール. */
    private static int valueScale;

    /** バッテリーの状態 不明. */
    public static final int BATTERY_STATUS_UNKNOWN = 1;

    /** バッテリーの状態 充電中. */
    public static final int BATTERY_STATUS_CHARGING = 2;

    /** バッテリーの状態 放電中. */
    public static final int BATTERY_STATUS_DISCHARGING = 3;

    /** バッテリーの状態 非充電中. */
    public static final int BATTERY_STATUS_NOT_CHARGING = 4;

    /** バッテリーの状態 満杯. */
    public static final int BATTERY_STATUS_FULL = 5;

    /** 充電中 AC. */
    public static final int BATTERY_PLUGGED_AC = 1;

    /** 充電中 USB. */
    public static final int BATTERY_PLUGGED_USB = 2;

    /**
     * バッテリーのIntentから情報を取得.
     * 
     * @param context Context
     */
    public void getBatteryInfo(final Context context) {

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);

        // バッテリーの変化を取得
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        switch (status) {
        case BatteryManager.BATTERY_STATUS_UNKNOWN:
            statusBattery = HostBatteryManager.BATTERY_STATUS_UNKNOWN;
            break;
        case BatteryManager.BATTERY_STATUS_CHARGING:
            statusBattery = HostBatteryManager.BATTERY_STATUS_CHARGING;
            break;
        case BatteryManager.BATTERY_STATUS_DISCHARGING:
            statusBattery = HostBatteryManager.BATTERY_STATUS_DISCHARGING;
            break;
        case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
            statusBattery = HostBatteryManager.BATTERY_STATUS_NOT_CHARGING;
            break;
        case BatteryManager.BATTERY_STATUS_FULL:
            statusBattery = HostBatteryManager.BATTERY_STATUS_FULL;
            break;
        default:
            statusBattery = HostBatteryManager.BATTERY_STATUS_UNKNOWN;
            break;
        }

        // プラグの状態を取得
        int plugged = batteryStatus.getIntExtra("plugged", 0);
        switch (plugged) {
        case BatteryManager.BATTERY_PLUGGED_AC:
            statusPlugged = BATTERY_PLUGGED_AC;
            break;
        case BatteryManager.BATTERY_PLUGGED_USB:
            statusPlugged = BATTERY_PLUGGED_USB;
            break;
        default:
            break;
        }

        valueLevel = batteryStatus.getIntExtra("level", 0);
        valueScale = batteryStatus.getIntExtra("scale", 0);
    }

    /**
     * バッテリーのIntentを設定.
     * 
     * @param intent Batteryの変化で取得できたIntent
     */
    /**
     * バッテリーのIntentを設定.
     * 
     * @param intent Batteryの変化で取得できたIntent
     */
    public void setBatteryRequest(final Intent intent) {
        String mAction = intent.getAction();

        if (Intent.ACTION_BATTERY_CHANGED.equals(mAction) || Intent.ACTION_BATTERY_LOW.equals(mAction)
                || Intent.ACTION_BATTERY_OKAY.equals(mAction)) {
            // バッテリーの変化を取得
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            switch (status) {
            case BatteryManager.BATTERY_STATUS_UNKNOWN:
                statusBattery = HostBatteryManager.BATTERY_STATUS_UNKNOWN;
                break;
            case BatteryManager.BATTERY_STATUS_CHARGING:
                statusBattery = HostBatteryManager.BATTERY_STATUS_CHARGING;
                break;
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                statusBattery = HostBatteryManager.BATTERY_STATUS_DISCHARGING;
                break;
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                statusBattery = HostBatteryManager.BATTERY_STATUS_NOT_CHARGING;
                break;
            case BatteryManager.BATTERY_STATUS_FULL:
                statusBattery = HostBatteryManager.BATTERY_STATUS_FULL;
                break;
            default:
                statusBattery = HostBatteryManager.BATTERY_STATUS_UNKNOWN;
                break;
            }

            valueLevel = intent.getIntExtra("level", 0);
            valueScale = intent.getIntExtra("scale", 0);

        } else if (Intent.ACTION_POWER_CONNECTED.equals(mAction) || Intent.ACTION_POWER_DISCONNECTED.equals(mAction)) {

            // プラグの状態を取得
            int plugged = intent.getIntExtra("plugged", 0);
            switch (plugged) {
            case BatteryManager.BATTERY_PLUGGED_AC:
                statusPlugged = BATTERY_PLUGGED_AC;
                break;
            case BatteryManager.BATTERY_PLUGGED_USB:
                statusPlugged = BATTERY_PLUGGED_USB;
                break;
            default:
                break;
            }
        }
    }

    /**
     * バッテリーの状態を取得.
     * 
     * @return statusBattery バッテリーの状態
     */
    public int getBatteryStatus() {
        return HostBatteryManager.statusBattery;
    }

    /**
     * プラグの状態を取得.
     * 
     * @return statusPlugged プラグの状態
     */
    public int getStatusPlugged() {
        return HostBatteryManager.statusPlugged;
    }

    /**
     * バッテリーレベルの取得.
     * 
     * @return valueLevel バッテリーレベル
     */
    public int getBatteryLevel() {
        return HostBatteryManager.valueLevel;
    }

    /**
     * スケールの取得.
     * 
     * @return batteryStatus バッテリーの状態
     */
    public int getBatteryScale() {
        return HostBatteryManager.valueScale;
    }
}
