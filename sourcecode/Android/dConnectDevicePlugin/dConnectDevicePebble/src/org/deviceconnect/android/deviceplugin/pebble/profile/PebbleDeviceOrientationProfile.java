/*
 PebbleDeviceOrientationProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.pebble.profile;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.deviceconnect.android.deviceplugin.pebble.PebbleDeviceService;
import org.deviceconnect.android.deviceplugin.pebble.util.PebbleManager;
import org.deviceconnect.android.deviceplugin.pebble.util.PebbleManager.OnReceivedEventListener;
import org.deviceconnect.android.deviceplugin.pebble.util.PebbleManager.OnSendCommandListener;

import android.content.Intent;
import android.os.Bundle;

import com.getpebble.android.kit.util.PebbleDictionary;
import org.deviceconnect.android.event.Event;
import org.deviceconnect.android.event.EventError;
import org.deviceconnect.android.event.EventManager;
import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.android.profile.DeviceOrientationProfile;
import org.deviceconnect.message.DConnectMessage;

/**
 * Pebble用 Device Orientationプロファイル.
 * @author NTT DOCOMO, INC.
 */
public class PebbleDeviceOrientationProfile extends DeviceOrientationProfile {
    /** milli G を m/s^2 の値にする係数. */
    static final double G_TO_MS2_COEFFICIENT =  9.81 / 1000.0 ;
    /**
     * コンストラクタ.
     * @param service Pebble デバイスサービス
     */
    public PebbleDeviceOrientationProfile(final PebbleDeviceService service) {
        service.getPebbleManager().addEventListener(PebbleManager.PROFILE_DEVICE_ORIENTATION
                , new OnReceivedEventListener() {
            @Override
            public void onReceivedEvent(final PebbleDictionary dic) {
                // Pebbleから加速度が送られてきたら、登録されたイベントに対して通知を送る
                Long x = dic.getInteger(PebbleManager.KEY_PARAM_DEVICE_ORIENTATION_X);
                Long y = dic.getInteger(PebbleManager.KEY_PARAM_DEVICE_ORIENTATION_Y);
                Long z = dic.getInteger(PebbleManager.KEY_PARAM_DEVICE_ORIENTATION_Z);
                Long interval = dic.getInteger(PebbleManager.KEY_PARAM_DEVICE_ORIENTATION_INTERVAL);

                // Pebbleからの加速度をdConnectの単位に正規化してdConnect用のデータを作成
                Bundle orientation = new Bundle();
                Bundle accelerationIncludingGravity = new Bundle();
                setX(accelerationIncludingGravity, x.intValue() * G_TO_MS2_COEFFICIENT);
                setY(accelerationIncludingGravity, y.intValue() * G_TO_MS2_COEFFICIENT);
                setZ(accelerationIncludingGravity, z.intValue() * G_TO_MS2_COEFFICIENT);
                setAccelerationIncludingGravity(orientation, accelerationIncludingGravity);
                setInterval(orientation, interval.longValue());

                // 登録されたイベントリスナー一覧を取得
                List<Event> evts = EventManager.INSTANCE.getEventList(service.getDeviceId(),
                        PROFILE_NAME, null, ATTRIBUTE_ON_DEVICE_ORIENTATION);
                for (Event evt : evts) {
                    // 各イベントリスナーに通知
                    Intent intent = EventManager.createEventMessage(evt);
                    setOrientation(intent, orientation);
                    ((PebbleDeviceService) getContext()).sendEvent(intent, evt.getAccessToken());
                }
            }
        });
    }

    @Override
    protected boolean onPutOnDeviceOrientation(final Intent request, final Intent response
            , final String deviceId, final String sessionKey) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
            return true;
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
            return true;
        } else if (sessionKey == null) {
            createEmptySessionKey(response);
            return true;
        } else {
            PebbleManager mgr = ((PebbleDeviceService) getContext()).getPebbleManager();
            // Pebbleで加速度センサーの登録依頼を送る
            PebbleDictionary dic = new PebbleDictionary();
            dic.addInt8(PebbleManager.KEY_PROFILE, (byte) PebbleManager.PROFILE_DEVICE_ORIENTATION);
            dic.addInt8(PebbleManager.KEY_ATTRIBUTE
                    , (byte) PebbleManager.DEVICE_ORIENTATION_ATTRIBUTE_ON_DEVICE_ORIENTATION);
            dic.addInt8(PebbleManager.KEY_ACTION, (byte) PebbleManager.ACTION_PUT);
            mgr.sendCommandToPebble(dic, new OnSendCommandListener() {
                @Override
                public void onReceivedData(final PebbleDictionary dic) {
                    if (dic == null) {
                        MessageUtils.setUnknownError(response);
                    } else {
                        // イベントリスナーを登録
                        EventError error = EventManager.INSTANCE.addEvent(request);
                        if (error == EventError.NONE) {
                            setResult(response, DConnectMessage.RESULT_OK);
                        } else if (error == EventError.INVALID_PARAMETER) {
                            MessageUtils.setInvalidRequestParameterError(response);
                        } else {
                            MessageUtils.setUnknownError(response);
                        }
                    }
                    getContext().sendBroadcast(response);
                }
            });
            // レスポンスを非同期で返却するので、falseを返す
            return false;
        }
    }

    @Override
    protected boolean onDeleteOnDeviceOrientation(final Intent request, final Intent response
            , final String deviceId, final String sessionKey) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
            return true;
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
            return true;
        } else if (sessionKey == null) {
            createEmptySessionKey(response);
            return true;
        } else {
            PebbleManager mgr = ((PebbleDeviceService) getContext()).getPebbleManager();
            
            // Pebbleに加速度センサーの解除依頼を送る
            PebbleDictionary dic = new PebbleDictionary();
            dic.addInt8(PebbleManager.KEY_PROFILE, (byte) PebbleManager.PROFILE_DEVICE_ORIENTATION);
            dic.addInt8(PebbleManager.KEY_ATTRIBUTE
                    , (byte) PebbleManager.DEVICE_ORIENTATION_ATTRIBUTE_ON_DEVICE_ORIENTATION);
            dic.addInt8(PebbleManager.KEY_ACTION, (byte) PebbleManager.ACTION_DELETE);
            mgr.sendCommandToPebble(dic, new OnSendCommandListener() {
                @Override
                public void onReceivedData(final PebbleDictionary dic) {
                }
            });
            // イベントリスナーを解除
            EventError error = EventManager.INSTANCE.removeEvent(request);
            if (error == EventError.NONE) {
                setResult(response, DConnectMessage.RESULT_OK);
            } else if (error == EventError.INVALID_PARAMETER) {
                MessageUtils.setInvalidRequestParameterError(response);
            } else {
                MessageUtils.setUnknownError(response);
            }
            return true;
        }
    }

    /**
     * デバイスIDをチェックする.
     * 
     * @param deviceId デバイスID
     * @return <code>deviceId</code>がテスト用デバイスIDに等しい場合はtrue、そうでない場合はfalse
     */
    private boolean checkDeviceId(final String deviceId) {
        String regex = PebbleNetworkServceDiscoveryProfile.DEVICE_ID;
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
     * セッションキーが空の場合のエラーを作成する.
     * 
     * @param response レスポンスを格納するIntent
     */
    private void createEmptySessionKey(final Intent response) {
        final int errorCode = 10;
        MessageUtils.setError(response, errorCode, "sessionKey must be specified.");
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
