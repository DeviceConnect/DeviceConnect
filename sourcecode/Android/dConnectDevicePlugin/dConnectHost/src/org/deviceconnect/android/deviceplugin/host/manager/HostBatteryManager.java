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
import android.util.Log;

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
        
    /** TAG. */
    private static final String TAG = "HOST";
    
    /**
     * バッテリーのIntentから情報を取得.
     * @param context Context
     */
    public void getBatteryInfo(final Context context) {
           
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, ifilter);
            
            // バッテリーの変化を取得
            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            Log.i(TAG, "status:" + status);
            switch (status) {
            case BatteryManager.BATTERY_STATUS_UNKNOWN:
                statusBattery = this.BATTERY_STATUS_UNKNOWN;
                break;
            case BatteryManager.BATTERY_STATUS_CHARGING:
                statusBattery = this.BATTERY_STATUS_CHARGING;
                break;
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                statusBattery = this.BATTERY_STATUS_DISCHARGING;
                break;
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                statusBattery = this.BATTERY_STATUS_NOT_CHARGING;
                break;
            case BatteryManager.BATTERY_STATUS_FULL:
                statusBattery = this.BATTERY_STATUS_FULL;
                break;
            default:
                statusBattery = this.BATTERY_STATUS_UNKNOWN;
                break;
            }
            
            // プラグの状態を取得
            int plugged = batteryStatus.getIntExtra("plugged", 0);
            Log.i(TAG, "plugged:" + plugged);
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
            Log.i(TAG, "valueLevel :" + valueLevel);
            Log.i(TAG, "valueScale:" + valueScale);
    }

    /**
     * バッテリーのIntentを設定.
     * @param intent Batteryの変化で取得できたIntent
     */
    /**
     * バッテリーのIntentを設定.
     * @param intent Batteryの変化で取得できたIntent
     */
    public void setBatteryRequest(final Intent intent) {
           String mAction = intent.getAction();
           
           Log.i(TAG, "mAction:" + mAction);
           if (Intent.ACTION_BATTERY_CHANGED.equals(mAction)
                   || Intent.ACTION_BATTERY_LOW.equals(mAction) 
                   || Intent.ACTION_BATTERY_OKAY.equals(mAction)){
               // バッテリーの変化を取得
               int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
               Log.i(TAG, "status:" + status);
               switch (status) {
               case BatteryManager.BATTERY_STATUS_UNKNOWN:
                   statusBattery = this.BATTERY_STATUS_UNKNOWN;
                   break;
               case BatteryManager.BATTERY_STATUS_CHARGING:
                   statusBattery = this.BATTERY_STATUS_CHARGING;
                   break;
               case BatteryManager.BATTERY_STATUS_DISCHARGING:
                   statusBattery = this.BATTERY_STATUS_DISCHARGING;
                   break;
               case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                   statusBattery = this.BATTERY_STATUS_NOT_CHARGING;
                   break;
               case BatteryManager.BATTERY_STATUS_FULL:
                   statusBattery = this.BATTERY_STATUS_FULL;
                   break;
               default:
                   statusBattery = this.BATTERY_STATUS_UNKNOWN;
                   break;
               }
               
               valueLevel = intent.getIntExtra("level", 0);
               valueScale = intent.getIntExtra("scale", 0);
               Log.i(TAG, "valueLevel :" + valueLevel);
               Log.i(TAG, "valueScale:" + valueScale);
               
           } else if (Intent.ACTION_POWER_CONNECTED.equals(mAction)
                   || Intent.ACTION_POWER_DISCONNECTED.equals(mAction)) {
               
               // プラグの状態を取得
               int plugged = intent.getIntExtra("plugged", 0);
               Log.i(TAG, "plugged:" + plugged);
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
     * @return batteryStatus バッテリーの状態
     */
    public int getBatteryStatus() {
        return this.statusBattery;
    }
    
    /**
     * プラグの状態を取得.
     * @return batteryStatus バッテリーの状態
     */
    public int getStatusPlugged() {
        return this.statusBattery;
    }
    
    /**
     * バッテリーレベルの取得.
     * @return batteryStatus バッテリーの状態
     */
    public int getBatteryLevel() {
        return this.valueLevel;
    }
    
    /**
     * スケールの取得.
     * @return batteryStatus バッテリーの状態
     */
    public int getBatteryScale() {
        return this.valueScale;
    }
}
