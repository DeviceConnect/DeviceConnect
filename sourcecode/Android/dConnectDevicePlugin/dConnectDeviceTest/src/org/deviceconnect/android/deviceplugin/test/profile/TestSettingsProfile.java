/*
 TestSettingsProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.test.profile;

import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.android.profile.SettingsProfile;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.profile.SettingsProfileConstants.VolumeKind;

import android.content.Intent;

/**
 * JUnit用テストデバイスプラグイン、Settingsプロファイル.
 * @author NTT DOCOMO, INC.
 */
public class TestSettingsProfile extends SettingsProfile {
    /**
     * レベル.
     */
    public static final double LEVEL = 0.5;

    /**
     * 日時.
     */
    public static final String DATE = "2014-01-01T01:01:01+09:00";
    
    /**
     * デバイスIDをチェックする.
     * @param deviceId デバイスID
     * @return <code>deviceId</code>がテスト用デバイスIDに等しい場合はtrue、そうでない場合はfalse
     */
    private boolean checkdeviceId(final String deviceId) {
        return TestNetworkServiceDiscoveryProfile.DEVICE_ID.equals(deviceId);
    }

    /**
     * デバイスIDが空の場合のエラーを作成する.
     * 
     * @param response レスポンスを格納するIntent
     */
    private void createEmptydeviceId(final Intent response) {
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

    @Override
    protected boolean onGetSoundVolume(final Intent request, final Intent response, final String deviceId,
            final VolumeKind kind) {
        
        if (deviceId == null) {
            createEmptydeviceId(response);
        } else if (!checkdeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (kind == null || kind == VolumeKind.UNKNOWN) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
            setVolumeLevel(response, LEVEL);
        }
        
        return true;
    }

    @Override
    protected boolean onGetDate(final Intent request, final Intent response, final String deviceId) {
        
        if (deviceId == null) {
            createEmptydeviceId(response);
        } else if (!checkdeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
            setDate(response, DATE);
        }
        
        return true;
    }

    @Override
    protected boolean onGetDisplayLight(final Intent request, final Intent response, final String deviceId) {
        if (deviceId == null) {
            createEmptydeviceId(response);
        } else if (!checkdeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
            setLightLevel(response, LEVEL);
        }
        
        return true;
    }

    @Override
    protected boolean onGetDisplaySleep(final Intent request, final Intent response, final String deviceId) {
        if (deviceId == null) {
            createEmptydeviceId(response);
        } else if (!checkdeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
            setTime(response, 1);
        }
        return true;
    }

    @Override
    protected boolean onPutSoundVolume(final Intent request, final Intent response, final String deviceId,
            final VolumeKind kind, final Double level) {
        
        if (deviceId == null) {
            createEmptydeviceId(response);
        } else if (!checkdeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (kind == null || kind == VolumeKind.UNKNOWN
                || level == null || level < MIN_LEVEL || level > MAX_LEVEL) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
        }
        return true;
    }

    @Override
    protected boolean onPutDate(final Intent request, final Intent response, final String deviceId, final String date) {
        if (deviceId == null) {
            createEmptydeviceId(response);
        } else if (!checkdeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (date == null) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
        }
        
        return true;
    }

    @Override
    protected boolean onPutDisplayLight(final Intent request, final Intent response, final String deviceId, final Double level) {
        
        if (deviceId == null) {
            createEmptydeviceId(response);
        } else if (!checkdeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (level == null || level < MIN_LEVEL || level > MAX_LEVEL) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
        }
        return true;
    }

    @Override
    protected boolean onPutDisplaySleep(final Intent request, final Intent response, final String deviceId, final Integer time) {
        
        if (deviceId == null) {
            createEmptydeviceId(response);
        } else if (!checkdeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (time == null) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
        }
        
        return true;
    }
}
