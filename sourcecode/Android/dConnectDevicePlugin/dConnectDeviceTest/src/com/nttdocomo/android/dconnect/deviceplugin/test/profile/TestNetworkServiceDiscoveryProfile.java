/*
 TestNetworkServiceDiscoveryProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.deviceplugin.test.profile;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;

import com.nttdocomo.android.dconnect.message.MessageUtils;
import com.nttdocomo.android.dconnect.profile.NetworkServiceDiscoveryProfile;
import com.nttdocomo.dconnect.message.DConnectMessage;

/**
 * JUnit用テストデバイスプラグイン、NetworkServiceDiscoveryプロファイル.
 * @author NTT DOCOMO, INC.
 */
public class TestNetworkServiceDiscoveryProfile extends NetworkServiceDiscoveryProfile {

    /**
     * テスト用デバイスID.
     */
    public static final String DEVICE_ID = "test_device_id";

    /**
     * 特殊文字を含むテスト用デバイスID.
     */
    public static final String DEVICE_ID_SPECIAL_CHARACTERS = "!#$'()-~¥@[;+:*],._/=?&%^|`\"{}<>";

    /**
     * テスト用デバイス名: {@value}
     */
    public static final String DEVICE_NAME = "Test Success Device";

    /**
     * テスト用デバイス名: {@value}
     */
    public static final String DEVICE_NAME_SPECIAL_CHARACTERS = "Test Device ID Special Characters";

    /**
     * テスト用デバイスタイプ.
     */
    public static final String DEVICE_TYPE = "TEST";

    /**
     * テスト用オンライン状態.
     */
    public static final boolean DEVICE_ONLINE = true;

    /**
     * テスト用コンフィグ.
     */
    public static final String DEVICE_CONFIG = "test config";

    /**
     * セッションキーが空の場合のエラーを作成する.
     * @param response レスポンスを格納するIntent
     */
    private void createEmptySessionKey(final Intent response) {
        MessageUtils.setInvalidRequestParameterError(response);
    }

    @Override
    protected boolean onGetGetNetworkServices(final Intent request, final Intent response) {
        List<Bundle> services = new ArrayList<Bundle>();

        // 典型的なサービス
        Bundle service = new Bundle();
        setId(service, DEVICE_ID);
        setName(service, DEVICE_NAME);
        setType(service, DEVICE_TYPE);
        setOnline(service, DEVICE_ONLINE);
        setConfig(service, DEVICE_CONFIG);
        services.add(service);

        // デバイスIDが特殊なサービス
        service = new Bundle();
        setId(service, DEVICE_ID_SPECIAL_CHARACTERS);
        setName(service, DEVICE_NAME_SPECIAL_CHARACTERS);
        setType(service, DEVICE_TYPE);
        setOnline(service, DEVICE_ONLINE);
        setConfig(service, DEVICE_CONFIG);
        services.add(service);

        setResult(response, DConnectMessage.RESULT_OK);
        setServices(response, services);
        
        return true;
    }

    @Override
    protected boolean onPutOnServiceChange(Intent request, Intent response, String deviceId, String sessionKey) {
        
        if (sessionKey == null) {
            createEmptySessionKey(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);

            Intent message = MessageUtils.createEventIntent();
            setSessionKey(message, sessionKey);
            setDeviceID(message, deviceId);
            setProfile(message, getProfileName());
            setAttribute(message, ATTRIBUTE_ON_SERVICE_CHANGE);
            
            Bundle service = new Bundle();
            setId(service, DEVICE_ID);
            setName(service, DEVICE_NAME);
            setType(service, DEVICE_TYPE);
            setOnline(service, DEVICE_ONLINE);
            setConfig(service, DEVICE_CONFIG);
            
            setNetworkService(message, service);
            
            Util.sendBroadcast(getContext(), message);
        }
        
        return true;
    }

    @Override
    protected boolean onDeleteOnServiceChange(Intent request, Intent response, String deviceId, String sessionKey) {
        if (sessionKey == null) {
            createEmptySessionKey(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
        }
        return true;
    }
}
