/*
 TestDeviceOrientationProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.deviceplugin.test.profile;

import android.content.Intent;
import android.os.Bundle;

import com.nttdocomo.android.dconnect.message.MessageUtils;
import com.nttdocomo.android.dconnect.profile.DeviceOrientationProfile;
import com.nttdocomo.dconnect.message.DConnectMessage;

/**
 * JUnit用テストデバイスプラグイン、Connectプロファイル.
 * 
 * @author NTT DOCOMO, INC.
 */
public class TestDeviceOrientationProfile extends DeviceOrientationProfile {

    /**
     * デバイスIDをチェックする.
     * @param deviceId デバイスID
     * @return <code>deviceId</code>がテスト用デバイスIDに等しい場合はtrue、そうでない場合はfalse
     */
    private boolean checkDeviceId(final String deviceId) {
        return TestNetworkServiceDiscoveryProfile.DEVICE_ID.equals(deviceId);
    }

    /**
     * デバイスIDが空の場合のエラーを作成する.
     * @param response レスポンスを格納するIntent
     */
    private void createEmptyDeviceId(final Intent response) {
        setResult(response, DConnectMessage.RESULT_ERROR);
    }

    /**
     * デバイスが発見できなかった場合のエラーを作成する.
     * @param response レスポンスを格納するIntent
     */
    private void createNotFoundDevice(final Intent response) {
        setResult(response, DConnectMessage.RESULT_ERROR);
    }

    @Override
    protected boolean onPutOnDeviceOrientation(final Intent request, final Intent response, final String deviceId, 
            final String sessionKey) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (sessionKey == null) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);

            Intent intent = MessageUtils.createEventIntent();
            setSessionKey(intent, sessionKey);
            setDeviceID(intent, deviceId);
            setProfile(intent, getProfileName());
            setAttribute(intent, ATTRIBUTE_ON_DEVICE_ORIENTATION);
            
            Bundle orientation = new Bundle();
            Bundle a1 = new Bundle();
            setX(a1, 0.0);
            setY(a1, 0.0);
            setZ(a1, 0.0);
            Bundle a2 = new Bundle();
            setX(a2, 0.0);
            setY(a2, 0.0);
            setZ(a2, 0.0);
            Bundle r = new Bundle();
            setAlpha(r, 0.0);
            setBeta(r, 0.0);
            setGamma(r, 0.0);
            
            setAcceleration(orientation, a1);
            setAccelerationIncludingGravity(orientation, a2);
            setRotationRate(orientation, r);
            setInterval(orientation, 0);
            
            setOrientation(intent, orientation);
            Util.sendBroadcast(getContext(), intent);
        }
        return true;
    }

    @Override
    protected boolean onDeleteOnDeviceOrientation(final Intent request, final Intent response, final String deviceId, 
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

}
