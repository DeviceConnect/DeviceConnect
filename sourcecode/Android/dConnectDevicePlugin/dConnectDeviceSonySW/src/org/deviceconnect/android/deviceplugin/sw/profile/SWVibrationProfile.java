/*
 SWVibrationProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.sw.profile;

import org.deviceconnect.android.deviceplugin.sw.SWConstants;
import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.android.profile.VibrationProfile;
import org.deviceconnect.message.DConnectMessage;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;

import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.aef.registration.Registration;
import com.sonyericsson.extras.liveware.extension.util.registration.DeviceInfoHelper;

/**
 * SonySWデバイスプラグインの{@link VibrationProfile}実装.
 * @author NTT DOCOMO, INC.
 */
public class SWVibrationProfile extends VibrationProfile {

    /**
     * リクエストパラメータ省略時のバイブレーション鳴動時間.
     * 
     * @return デフォルト鳴動時間
     */
    @Override
    protected long getMaxVibrationTime() {
        return SWConstants.MAX_VIBRATION_TIME;
    }

    @Override
    protected boolean onPutVibrate(final Intent request, final Intent response, final String deviceId,
            final long[] pattern) {
        BluetoothDevice device = SWUtil.findSmartWatch(deviceId);
        if (device == null) {
            MessageUtils.setNotFoundDeviceError(response, "No device is found: " + deviceId);
            return true;
        }
        if (pattern == null) {
            MessageUtils.setInvalidRequestParameterError(response);
            return true;
        }
        runThread(response, deviceId, pattern);
        setResult(response, DConnectMessage.RESULT_OK);
        return true;
    }

    /**
     * SmartExtensionAPIの仕様とDeviceConnectのpatternの仕様が違うため.
     * 非同期で処理をDeviceConnect側の仕様に合わせる 例) pattern=100,10,50 で
     * 100msec振動、10msec止まる、50msec振動
     * 
     * @param response レスポンス
     * @param deviceId デバイスID
     * @param pattern 振動パターン
     */
    private void runThread(final Intent response, final String deviceId, final long[] origPattern) {
        Thread thread = new Thread() {
            public void run() {
                long[] pattern;
                if (origPattern.length %2 != 0) {
                    pattern = new long[origPattern.length + 1];
                    for (int i = 0; i < origPattern.length; i++) {
                        pattern[i] = origPattern[i];
                    }
                    pattern[pattern.length -1] = 0;
                } else {
                    pattern = origPattern;
                }

                long prevInterval = 0;
                for (int cnt = 0; cnt < pattern.length; cnt+=2) {
                    try {
                        if (prevInterval > 0) {
                            Thread.sleep(prevInterval);
                        }
                    } catch (InterruptedException e) {
                        // スレッドに対して割り込まれたら終了
                        return;
                    }

                    long on = (pattern[cnt] > 0) ? pattern[cnt] : 0;
                    long off = (pattern[cnt + 1] > 0) ? pattern[cnt + 1] : 0;
                    prevInterval = on + off;
                    intentPut((int) on, (int) off, 1, deviceId, response);
                }
            }
        };
        thread.start();
    }

    @Override
    protected boolean onDeleteVibrate(final Intent request, final Intent response, final String deviceId) {
        BluetoothDevice device = SWUtil.findSmartWatch(deviceId);
        if (device == null) {
            MessageUtils.setNotFoundDeviceError(response, "No device is found: " + deviceId);
            return true;
        }
        boolean smartWatch2Supported = DeviceInfoHelper.isSmartWatch2ApiAndScreenDetected(getContext(),
                SWUtil.toHostAppPackageName(device.getName()));
        if (smartWatch2Supported) {
            MessageUtils.setNotSupportActionError(response);
        } else {
            Intent intent = new Intent(Control.Intents.CONTROL_STOP_VIBRATE_INTENT);
            sendToHostApp(intent, deviceId);
            setResult(response, DConnectMessage.RESULT_OK);
        }
        return true;
    }

    /**
     * intent送信.
     * 
     * @param onDuration 振動時間
     * @param offDuration 振動しない時間
     * @param repeats 繰り返し回数
     * @param deviceId デバイスID
     * @param response レスポンス
     */
    protected void intentPut(final int onDuration, final int offDuration, final int repeats, final String deviceId,
            final Intent response) {
        if (onDuration <= 0) {
            return;
        }
        int onDURATION;
        if (onDuration > SWConstants.MAX_VIBRATION_TIME) {
            onDURATION = SWConstants.MAX_VIBRATION_TIME;
        } else {
            onDURATION = onDuration;
        }

        Intent intent = new Intent(Control.Intents.CONTROL_VIBRATE_INTENT);
        intent.putExtra(Control.Intents.EXTRA_ON_DURATION, onDURATION);
        intent.putExtra(Control.Intents.EXTRA_OFF_DURATION, offDuration);
        intent.putExtra(Control.Intents.EXTRA_REPEATS, repeats);
        sendToHostApp(intent, deviceId);
    }

    /**
     * 
     * @param intent インテント
     * @param deviceId デバイスID
     */
    protected void sendToHostApp(final Intent intent, final String deviceId) {
        BluetoothDevice device = SWUtil.findSmartWatch(deviceId);
        String deviceName = device.getName();
        intent.putExtra(Control.Intents.EXTRA_AEA_PACKAGE_NAME, getContext().getPackageName());
        intent.setPackage(SWUtil.toHostAppPackageName(deviceName));
        getContext().sendBroadcast(intent, Registration.HOSTAPP_PERMISSION);
    }

}
