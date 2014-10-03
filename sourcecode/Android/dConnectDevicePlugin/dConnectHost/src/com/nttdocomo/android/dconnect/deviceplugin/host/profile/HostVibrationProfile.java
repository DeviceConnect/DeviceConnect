/*
 HostVibrationProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.deviceplugin.host.profile;

import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;

import com.nttdocomo.android.dconnect.message.MessageUtils;
import com.nttdocomo.android.dconnect.profile.VibrationProfile;
import com.nttdocomo.dconnect.message.intent.message.IntentDConnectMessage;

/** 
 * Vibration Profile.
 * @author NTT DOCOMO, INC.
 */
public class HostVibrationProfile extends VibrationProfile {
    /** Debug Tag. */
    private static final String TAG = "HOST";

    /**
     * 振動をキャンセルする事を示すフラグ.
     */
    private boolean isCancelled = false;

    @Override
    protected boolean onPutVibrate(final Intent request, final Intent response, final String deviceId,
            final long[] pattern) {

        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (pattern == null) {
            setResult(response, IntentDConnectMessage.RESULT_ERROR);
            return true;
        } else {
                final Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);

                // Nexus7はVibratorなし
                if (vibrator == null || !vibrator.hasVibrator()) {
                    setResult(response, IntentDConnectMessage.RESULT_ERROR);
                }

                // 振動パターンを開始させたら、すぐに処理を続けたいので、
                // 振動パターン再生部分は別スレッドで実行。
                Executors.newSingleThreadExecutor().execute(new Thread() {
                    public void run() {
                        boolean vibrateMode = true;
                        for (Long dur : pattern) {
                            if (isCancelled) {
                                break;
                            }

                            if (vibrateMode) {
                                vibrator.vibrate(dur);
                            }

                            // 振動モード: vibrate()は直にリターンされるので、振動時間分だけ待ち時間を入れる。
                            // 無振動モード: 無振動時間分だけ待ち時間を入れる。
                            try {
                                Thread.sleep(dur);
                            } catch (InterruptedException e) { }

                            vibrateMode = !vibrateMode;
                        }
                    };
                });

                // 振動パターン再生セッションを終えたので、キャンセルフラグを初期化。
                isCancelled = false;

                setResult(response, IntentDConnectMessage.RESULT_OK);
            
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

            
            // Vibration Stop API
            if (ATTRIBUTE_VIBRATE.equals(getAttribute(request))) {
                Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
    
                if (vibrator == null || !vibrator.hasVibrator()) {
                    setResult(response, IntentDConnectMessage.RESULT_ERROR);
                }
    
                vibrator.cancel();
                // cancel()は現在されているの振調パターンの1節しかキャンセルしないので、
                // それ以降の振動パターンの節の再生を防ぐ為に、キャンセルされたことを示す
                // フラグをたてる。
                isCancelled = true;
    
                setResult(response, IntentDConnectMessage.RESULT_OK);
            } else {
                setResult(response, IntentDConnectMessage.RESULT_ERROR);
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
     * デバイスが発見できなかった場合のエラーを作成する.
     * 
     * @param response レスポンスを格納するIntent
     */
    private void createNotFoundDevice(final Intent response) {
        MessageUtils.setNotFoundDeviceError(response);
    }
}
