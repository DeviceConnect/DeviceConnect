/*
 HostDeviceOrientationProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.deviceplugin.host.profile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Intent;

import com.nttdocomo.android.dconnect.deviceplugin.host.HostDeviceService;
import com.nttdocomo.android.dconnect.event.EventError;
import com.nttdocomo.android.dconnect.event.EventManager;
import com.nttdocomo.android.dconnect.message.MessageUtils;
import com.nttdocomo.android.dconnect.profile.DeviceOrientationProfile;

/**
 * DeviceOrientation Profile.
 * 
 * @author NTT DOCOMO, INC.
 */
public class HostDeviceOrientationProfile extends DeviceOrientationProfile {

    /** TAG. */
    private static final String TAG = "HOST";

    /** Error. */
    private static final int ERROR_VALUE_IS_NULL = 100;
    
  
    @Override
    protected boolean onPutOnDeviceOrientation(final Intent request, final Intent response, final String deviceId, 
            final String sessionKey) {
       
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (sessionKey == null) {
            createEmptySessionKey(response);
        } else {
           
            // イベントの登録
            EventError error = EventManager.INSTANCE.addEvent(request);
           
            if (error == EventError.NONE) {
               ((HostDeviceService) getContext()).registerDeviceOrientationEvent(response, deviceId, sessionKey);
               return false;
            } else {
                MessageUtils.setError(response, ERROR_VALUE_IS_NULL, "Can not register event.");
                return true;
            }
            
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
            createEmptySessionKey(response);
        } else {

            // イベントの解除
            EventError error = EventManager.INSTANCE.removeEvent(request);
            if (error == EventError.NONE) {
                
                ((HostDeviceService) getContext()).unregisterDeviceOrientationEvent(response);
                return false;
                
            } else {
                MessageUtils.setError(response, ERROR_VALUE_IS_NULL, "Can not unregister event.");
                return true;

            } 
        }
        return true;
    }
    
    
    /**
     * デバイスIDをチェックする.
     * 
     * @param deviceId デバイスID
     * @return <code>deviceId</code>がテスト用デバイスIDに等しい場合はtrue、そうでない場合はfalse
     */
    private boolean checkDeviceId(final String deviceId) {
        String regex = HostNetworkServiceDiscoveryProfile.DEVICE_ID;
        Pattern mPattern = Pattern.compile(regex);
        Matcher match = mPattern.matcher(deviceId);

        return match.find();
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
     * セッションキーが空の場合のエラーを作成する.
     * 
     * @param response レスポンスを格納するIntent
     */
    private void createEmptySessionKey(final Intent response) {

        MessageUtils.setError(response, ERROR_VALUE_IS_NULL, "SessionKey not found");
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
