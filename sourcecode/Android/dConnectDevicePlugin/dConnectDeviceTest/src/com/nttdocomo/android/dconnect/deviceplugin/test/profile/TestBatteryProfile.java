/*
 TestBatteryProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.deviceplugin.test.profile;

import android.content.Intent;
import android.os.Bundle;

import com.nttdocomo.android.dconnect.message.MessageUtils;
import com.nttdocomo.android.dconnect.profile.BatteryProfile;
import com.nttdocomo.dconnect.message.DConnectMessage;

/**
 * JUnit用テストデバイスプラグイン、Batteryプロファイル.
 * 
 * @author NTT DOCOMO, INC.
 */
public class TestBatteryProfile extends BatteryProfile {
    /**
     * バッテリー充電時間を定義する.
     */
    public static final double CHARGING_TIME = 50000;

    /**
     * バッテリー放電時間を定義する.
     */
    public static final double DISCHARGING_TIME = 10000;

    /**
     * バッテリーレベルを定義する.
     */
    public static final double LEVEL = 0.5;

    /**
     * バッテリー充電フラグを定義する.
     */
    public static final boolean CHARGING = false;

    /**
     * デバイスIDをチェックする.
     * 
     * @param deviceId デバイスID
     * @return <code>deviceId</code>がテスト用デバイスIDに等しい場合はtrue、そうでない場合はfalse
     */
    private boolean checkDeviceId(final String deviceId) {
        return TestNetworkServiceDiscoveryProfile.DEVICE_ID.equals(deviceId);
    }

    /**
     * デバイスIDが空の場合のエラーを作成する.
     * 
     * @param response レスポンスを格納するIntent
     */
    private void createEmptyDeviceId(final Intent response) {
        MessageUtils.setEmptyDeviceIdError(response, "Device ID is empty.");
    }

    /**
     * デバイスが発見できなかった場合のエラーを作成する.
     * 
     * @param response レスポンスを格納するIntent
     */
    private void createNotFoundDevice(final Intent response) {
        MessageUtils.setNotFoundDeviceError(response, "Device is not found.");
    }

    @Override
    protected boolean onGetAll(final Intent request, final Intent response, final String deviceId) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
            setCharging(response, CHARGING);
            setChargingTime(response, CHARGING_TIME);
            setDischargingTime(response, DISCHARGING_TIME);
            setLevel(response, LEVEL);
        }
        return true;
    }

    @Override
    protected boolean onGetCharging(final Intent request, final Intent response, final String deviceId) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
            setCharging(response, CHARGING);
        }
        return true;
    }

    @Override
    protected boolean onGetDischargingTime(final Intent request, final Intent response, final String deviceId) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
            setDischargingTime(response, DISCHARGING_TIME);
        }
        return true;
    }

    @Override
    protected boolean onGetChargingTime(final Intent request, final Intent response, final String deviceId) {
        String deviceid = getDeviceID(request);
        if (deviceid == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceid)) {
            createNotFoundDevice(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
            setChargingTime(response, CHARGING_TIME);
        }
        return true;
    }

    @Override
    protected boolean onGetLevel(final Intent request, final Intent response, final String deviceId) {
        String deviceid = getDeviceID(request);
        if (deviceid == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceid)) {
            createNotFoundDevice(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
            setLevel(response, LEVEL);
        }
        return true;
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
            setResult(response, DConnectMessage.RESULT_OK);
            
            Intent message = MessageUtils.createEventIntent();
            setSessionKey(message, sessionKey);
            setDeviceID(message, deviceId);
            setProfile(message, getProfileName());
            setAttribute(message, ATTRIBUTE_ON_CHARGING_CHANGE);
            Bundle charging = new Bundle();
            setCharging(charging, false);
            setBattery(message, charging);
            Util.sendBroadcast(getContext(), message);
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
            setResult(response, DConnectMessage.RESULT_OK);
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
            setResult(response, DConnectMessage.RESULT_OK);

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
            setResult(response, DConnectMessage.RESULT_OK);
        }
        return true;
    }

}
