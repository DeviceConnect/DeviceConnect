/*
 TestVibrationProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.test.profile;

import org.deviceconnect.android.profile.VibrationProfile;
import org.deviceconnect.message.DConnectMessage;

import android.content.Intent;

/**
 * JUnit用テストデバイスプラグイン、Vibrationプロファイル.
 * @author NTT DOCOMO, INC.
 */
public class TestVibrationProfile extends VibrationProfile {

    /**
     * デバイスをチェックする.
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
    protected boolean onPutVibrate(final Intent request, final Intent response, final String deviceId, 
            final long[] pattern) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
        }
        return true;
    }

    @Override
    protected boolean onDeleteVibrate(final Intent request, final Intent response, final String deviceId) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
        }
        return true;
    }
}
