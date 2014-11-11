/*
 SWVibrationProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.sw.profile;

import org.deviceconnect.android.deviceplugin.sw.SWConstants;
import org.deviceconnect.android.deviceplugin.util.DcLoggerSW;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;

import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.android.profile.VibrationProfile;
import org.deviceconnect.message.DConnectMessage;
import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.aef.registration.Registration;
import com.sonyericsson.extras.liveware.extension.util.registration.DeviceInfoHelper;

/**
 * SonySWデバイスプラグインの{@link VibrationProfile}実装.
 * @author NTT DOCOMO, INC.
 */
public class SWVibrationProfile extends VibrationProfile {

    /** ロガー. */
    private DcLoggerSW mLogger = new DcLoggerSW();

    /**
     * リクエストパラメータ省略時のバイブレーション鳴動時間.
     * 
     * @return デフォルト鳴動時間
     */
    @Override
    protected long getMaxVibrationTime() {
        return SWConstants.MAX_VIBRATION_TIME;
    }

    // Vibration開始命令
    @Override
    protected boolean onPutVibrate(final Intent request, final Intent response, final String deviceId,
            final long[] pattern) {

        mLogger.entering(this, "onPutVibrate");

        if (!checkPutVibrate(response, deviceId, pattern)) {
            return true;
        }

        runThread(response, deviceId, pattern);

        setResult(response, DConnectMessage.RESULT_OK);

        mLogger.exiting(this, "onPutVibrate");

        return true;
    }

    /**
     * エラー判定.
     * 
     * @param response レスポンス
     * @param deviceId デバイスID
     * @param pattern パターン
     * @return エラー判定結果
     */
    private boolean checkPutVibrate(final Intent response, final String deviceId, final long[] pattern) {

        mLogger.entering(this, "checkPutVibrate");

        mLogger.fine(this, "checkPutVibrate deviceId", deviceId);

        BluetoothDevice device = SWUtil.findSmartWatch(deviceId);

        mLogger.fine(this, "checkPutVibrate device", device);

        if (device == null) {
            MessageUtils.setNotFoundDeviceError(response, "No device is found: " + deviceId);

            mLogger.exiting(this, "checkPutVibrate device == null)");
            return false;
        }

        if (pattern == null) {
            MessageUtils.setInvalidRequestParameterError(response);
            mLogger.exiting(this, "checkPutVibrate pattern == null)");
            return false;
        }

        mLogger.exiting(this, "checkPutVibrate");

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
    private void runThread(final Intent response, final String deviceId, final long[] pattern) {

        Thread thread;
        thread = null;
        thread = new Thread() {
            public void run() {
                try {
                    patternSet(pattern, deviceId, response);
                } catch (Exception e1) {
                    mLogger.warning(this, "onPutVibrate thread", "", e1);
                }
            }
        };

        thread.start();

    }

    /**
     * パターン変換：DeviceConnectからSmartExtensionAPIへ振動パターンの変換.
     * 
     * @param pattern 入力された振動パターン
     * @param deviceId デバイスId
     * @param response レスポンス
     */
    private void patternSet(final long[] pattern, final String deviceId, final Intent response) {

        int setOnDuration = 0;
        int setOffDuration = 0;
        int setPatternLength = 0;
        int setDuration = 0;

        // 入力されたpatternの数だけintentを送る
        setPatternLength = pattern.length;

        for (int cnt = 1; cnt <= setPatternLength; cnt++) {
            if (cnt % 2 != 0) {
                mLogger.info(this, "onPutVibrate_pattern", cnt);
                // 奇数回の処理
                // 振動する、振動しないmsecの設定

                setOnDuration = (int) pattern[cnt - 1];
                setOffDuration = 0;
                setDuration = setOnDuration;

                // intent送信
                intentPut(setOnDuration, setOffDuration, 1, deviceId, response);
            } else {
                setOffDuration = (int) pattern[cnt - 1];
                setDuration = setOffDuration;
            }

            // Thread.sleepで処理を止める
            mySleep(setDuration);
        }
    }

    /**
     * durationメソッド.
     * 
     * @param duration 処理を止める時間.
     */
    private void mySleep(final int duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            mLogger.warning(this, "mySleep", "", e);
        }
    }

    // Vibration停止命令
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
            mLogger.fine(this, "onDeleteVibrate", intent);
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
        mLogger.entering(this, "intentPut");

        mLogger.info(this, "intentPut onDuration", onDuration);

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

        mLogger.exiting(this, "intentPut");

    }

    // ホストアプリケーションに命令送信
    /**
     * 
     * @param intent インテント
     * @param deviceId デバイスID
     */
    protected void sendToHostApp(final Intent intent, final String deviceId) {
        mLogger.entering(this, "sendToHostApp");

        BluetoothDevice device = SWUtil.findSmartWatch(deviceId);
        String deviceName = device.getName();
        intent.putExtra(Control.Intents.EXTRA_AEA_PACKAGE_NAME, getContext().getPackageName());
        intent.setPackage(SWUtil.toHostAppPackageName(deviceName));
        getContext().sendBroadcast(intent, Registration.HOSTAPP_PERMISSION);

        mLogger.exiting(this, "sendToHostApp");
    }

}
