/*
 TestPhoneProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.test.profile;

import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.android.profile.PhoneProfile;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.profile.PhoneProfileConstants.CallState;
import org.deviceconnect.profile.PhoneProfileConstants.PhoneMode;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

/**
 * JUnit用テストデバイスプラグイン、Phoneプロファイル.
 * 
 * @author NTT DOCOMO, INC.
 */
public class TestPhoneProfile extends PhoneProfile {

    /**
     * スマートフォンの通話状態.
     */
    public static final CallState STATE = CallState.FINISHED; // 通話終了

    /**
     * デバイスIDをチェックする.
     * 
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
     * セッションキーが空の場合のエラーを作成する.
     * 
     * @param response レスポンスを格納するIntent
     */
    private void createEmptySessionKey(final Intent response) {
        MessageUtils.setInvalidRequestParameterError(response);
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
    protected boolean onPostCall(final Intent request, final Intent response, final String deviceId,
            final String phoneNumber) {

        if (deviceId == null) {
            createNotFoundDevice(response);
        } else if (!checkdeviceId(deviceId)) {
            createEmptydeviceId(response);
        } else if (TextUtils.isEmpty(phoneNumber)) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
        }

        return true;
    }

    @Override
    protected boolean onPutSet(final Intent request, final Intent response, final String deviceId, final PhoneMode mode) {

        if (deviceId == null) {
            createNotFoundDevice(response);
        } else if (!checkdeviceId(deviceId)) {
            createEmptydeviceId(response);
        } else if (mode == null || mode == PhoneMode.UNKNOWN) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
        }

        return true;
    }

    @Override
    protected boolean onPutOnConnect(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {

        if (deviceId == null) {
            createEmptydeviceId(response);
        } else if (!checkdeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (sessionKey == null) {
            createEmptySessionKey(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);

            Intent message = MessageUtils.createEventIntent();
            setSessionKey(message, sessionKey);
            setDeviceID(message, deviceId);
            setProfile(message, getProfileName());
            setAttribute(message, ATTRIBUTE_ON_CONNECT);
            Bundle phoneStatus = new Bundle();
            setPhoneNumber(phoneStatus, "090xxxxxxxx");
            setState(phoneStatus, STATE);
            setPhoneStatus(message, phoneStatus);
            Util.sendBroadcast(getContext(), message);
        }

        return true;
    }

    @Override
    protected boolean onDeleteOnConnect(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {
        if (deviceId == null) {
            createEmptydeviceId(response);
        } else if (!checkdeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (sessionKey == null) {
            createEmptySessionKey(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
        }

        return true;
    }

}
