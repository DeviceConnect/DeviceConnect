/*
 HostBatteryProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */

package org.deviceconnect.android.deviceplugin.host.profile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.deviceconnect.android.deviceplugin.host.HostDeviceService;
import org.deviceconnect.android.deviceplugin.host.manager.HostBatteryManager;
import org.deviceconnect.android.event.EventError;
import org.deviceconnect.android.event.EventManager;
import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.android.profile.BatteryProfile;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.message.intent.message.IntentDConnectMessage;

import android.content.Intent;

/**
 * Battery Profile.
 * @author NTT DOCOMO, INC.
 */
public class HostBatteryProfile extends BatteryProfile {
    
    
    /** Debug Tag. */
    private static final String TAG = "HOST";
    
    /**
     * Timeout時間の設定.
     */
    public static final int PROCESS_TIMEOUT = 30000;
    
    /**
     * バッテリーの最大値.
     */
    public static final int BATTERY_MAX_LEVEL = 100;

    /**
     * バッテリー充電フラグを定義する.
     */
    public static final boolean CHARGING = false;
   
   
    @Override
    protected boolean onGetLevel(final Intent request, final Intent response, final String deviceId) {
     
        if (deviceId == null) {
            createEmptyDeviceId(response);
            return true;
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
            return true;
        } else {
            
            int mLevel = ((HostDeviceService) getContext()).getBatteryLevel();
            int mScale = ((HostDeviceService) getContext()).getBatteryScale();
            
            setResult(response, IntentDConnectMessage.RESULT_OK);
            setLevel(response, mLevel / (float) mScale);
            getContext().sendBroadcast(response);
            return true;
        }
    }
    
    @Override
    protected boolean onGetCharging(final Intent request, final Intent response, final String deviceId) {
     
        if (deviceId == null) {
            createEmptyDeviceId(response);
            return true;
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
            return true;
        } else {
            int mStatus = ((HostDeviceService) getContext()).getBatteryStatus();
            
            setResult(response, IntentDConnectMessage.RESULT_OK);
            setCharging(response, getBatteryChargingStatus(mStatus));
            getContext().sendBroadcast(response);
            
            return true;
        }
    }
   
    @Override
    protected boolean onGetAll(final Intent request, final Intent response, final String deviceId) {
        
        if (deviceId == null) {
            createEmptyDeviceId(response);
            return true;
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
            return true;
        } else {
            int mLevel = ((HostDeviceService) getContext()).getBatteryLevel();
            int mScale = ((HostDeviceService) getContext()).getBatteryScale();
            if (mScale <= 0) {
                MessageUtils.setUnknownError(response, "Scale of battery level is unknown.");
                return true;
            }
            if (mLevel < 0) {
                MessageUtils.setUnknownError(response, "Battery level is unknown.");
                return true;
            }
            
            setLevel(response, mLevel / (float) mScale);
            
            int mStatus = ((HostDeviceService) getContext()).getBatteryStatus();
            setCharging(response, getBatteryChargingStatus(mStatus));
           
            setResult(response, IntentDConnectMessage.RESULT_OK);
            getContext().sendBroadcast(response);
        }
        return false;
    }
    
    @Override
    protected boolean onPutOnChargingChange(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (sessionKey == null) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else {
            
            // イベントの登録
            EventError error = EventManager.INSTANCE.addEvent(request);

            if (error == EventError.NONE) {
                ((HostDeviceService) getContext()).setDeviceId(deviceId);
                setResult(response, DConnectMessage.RESULT_OK);
                return true;
            } else {
                setResult(response, DConnectMessage.RESULT_ERROR);
                return true;
            }
            
        }
        return true;
    }

    @Override
    protected boolean onDeleteOnChargingChange(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (sessionKey == null) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else {
            // イベントの解除
            EventError error = EventManager.INSTANCE.removeEvent(request);
            if (error == EventError.NONE) {
                setResult(response, DConnectMessage.RESULT_OK);
                return true;
            } else {
                MessageUtils.setError(response, 100, "Can not unregister event.");
                return true;
            }
        }
        return true;
    }

    @Override
    protected boolean onPutOnBatteryChange(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (sessionKey == null) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else {
            // イベントの登録
            EventError error = EventManager.INSTANCE.addEvent(request);

            if (error == EventError.NONE) {
                ((HostDeviceService) getContext()).setDeviceId(deviceId);
                setResult(response, DConnectMessage.RESULT_OK);
                return true;
            } else {
                setResult(response, DConnectMessage.RESULT_ERROR);
                return true;
            }
            
            /*
            Intent message = MessageUtils.createEventIntent();
            setSessionKey(message, sessionKey);
            setDeviceID(message, deviceId);
            setProfile(message, getProfileName());
            setAttribute(message, ATTRIBUTE_ON_BATTERY_CHANGE);
            Bundle battery = new Bundle();
            setChargingTime(battery, CHARGING_TIME);
            setDischargingTime(battery, DISCHARGING_TIME);
            setLevel(battery, LEVEL);
            setBattery(message, battery);
            Util.sendBroadcast(getContext(), message);
            */
            
        }
        return true;
    }

    @Override
    protected boolean onDeleteOnBatteryChange(final Intent request, final Intent response,
            final String deviceId, final String sessionKey) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (sessionKey == null) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else {
            // イベントの解除
            EventError error = EventManager.INSTANCE.removeEvent(request);
            if (error == EventError.NONE) {

                //((HostDeviceService) getContext()).unregisterOnStatusChange(response);
                return true;

            } else {
                MessageUtils.setError(response, 100, "Can not unregister event.");
                return true;

            }
        }
        return true;
    }
   
    
    /**
     * 充電状態を取得.
     * 
     * @return true:充電中 false:充電中ではない
     */
    private boolean getBatteryChargingStatus(int mStatus) {
        if (mStatus == HostBatteryManager.BATTERY_STATUS_CHARGING) {
            return true;
        } else if (mStatus == HostBatteryManager.BATTERY_STATUS_DISCHARGING) {
            return false;
        } else if (mStatus == HostBatteryManager.BATTERY_STATUS_FULL) {
            return true;
        } else if (mStatus == HostBatteryManager.BATTERY_STATUS_NOT_CHARGING) {
            return false;
        } else if (mStatus == HostBatteryManager.BATTERY_STATUS_UNKNOWN) {
            return false;
        } else {
            return false;
        }
    }

    /**
     * デバイスIDをチェックする.
     * 
     * @param deviceId デバイスID
     * @return <code>deviceId</code>がテスト用デバイスIDに等しい場合はtrue、そうでない場合はfalse
     */
    private boolean checkDeviceId(final String deviceId) {
        String regex = HostNetworkServiceDiscoveryProfile.DEVICE_ID;
        Pattern p = Pattern.compile(regex);
   
        Matcher m = p.matcher(deviceId);

        return m.find();
    }

    /**
     * デバイスIDが空の場合のエラーを作成する.
     * 
     * @param response レスポンスを格納するIntent
     */
    private void createEmptyDeviceId(final Intent response) {
        MessageUtils.setEmptyDeviceIdError(response);
    }

    /**
     * デバイスが発見できなかった場合のエラーを作成する.
     * 
     * @param response レスポンスを格納するIntent
     */
    private void createNotFoundDevice(final Intent response) {
        MessageUtils.setNotFoundDeviceError(response);
    }
}
