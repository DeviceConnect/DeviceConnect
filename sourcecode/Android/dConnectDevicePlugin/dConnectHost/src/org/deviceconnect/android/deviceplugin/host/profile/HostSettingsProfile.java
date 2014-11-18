/*
 HostSettingsProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.host.profile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.android.profile.SettingsProfile;
import org.deviceconnect.message.DConnectMessage;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings;

/**
 * Settingsプロファイル.
 * 
 * @author NTT DOCOMO, INC.
 */
public class HostSettingsProfile extends SettingsProfile {

    /** Light Levelの最大値. */
    private static final int MAX_LIGHT_LEVEL = 255;

    /** Error. */
    private static final int ERROR_VALUE_IS_NULL = 100;

    @Override
    protected boolean onGetSoundVolume(final Intent request, final Intent response, final String deviceId,
            final VolumeKind kind) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {

            this.getContext();
            AudioManager manager = (AudioManager) this.getContext().getSystemService(Context.AUDIO_SERVICE);
            double mVolume = 0;
            double maxVolume = 1;

            if (kind == VolumeKind.ALARM) {
                mVolume = manager.getStreamVolume(AudioManager.STREAM_ALARM);
                maxVolume = manager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
                setResult(response, DConnectMessage.RESULT_OK);
                setVolumeLevel(response, mVolume / maxVolume);
            } else if (kind == VolumeKind.CALL) {
                mVolume = manager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
                maxVolume = manager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
                setResult(response, DConnectMessage.RESULT_OK);
                setVolumeLevel(response, mVolume / maxVolume);
            } else if (kind == VolumeKind.RINGTONE) {
                mVolume = manager.getStreamVolume(AudioManager.STREAM_RING);
                maxVolume = manager.getStreamMaxVolume(AudioManager.STREAM_RING);
                setResult(response, DConnectMessage.RESULT_OK);
                setVolumeLevel(response, mVolume / maxVolume);
            } else if (kind == VolumeKind.MAIL) {
                setResult(response, DConnectMessage.RESULT_OK);
            } else if (kind == VolumeKind.MEDIA_PLAYER) {
                mVolume = manager.getStreamVolume(AudioManager.STREAM_MUSIC);
                maxVolume = manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                setResult(response, DConnectMessage.RESULT_OK);
                setVolumeLevel(response, mVolume / maxVolume);
            } else {
                List<Bundle> resp = new ArrayList<Bundle>();

                // Alerm
                Bundle mAlermParam = new Bundle();
                mVolume = manager.getStreamVolume(AudioManager.STREAM_ALARM);
                maxVolume = manager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
                mAlermParam.putString("alerm", "" + mVolume / maxVolume);
                resp.add(mAlermParam);

                // Call
                Bundle mCallParam = new Bundle();
                mVolume = manager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
                maxVolume = manager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
                mCallParam.putString("call", "" + mVolume / maxVolume);
                resp.add(mCallParam);

                // Ringtone
                Bundle mRingtoneParam = new Bundle();
                mVolume = manager.getStreamVolume(AudioManager.STREAM_RING);
                maxVolume = manager.getStreamMaxVolume(AudioManager.STREAM_RING);
                mRingtoneParam.putString("ringtone", "" + mVolume / maxVolume);
                resp.add(mRingtoneParam);

                // Mail
                Bundle mMailParam = new Bundle();
                mMailParam.putString("mail", "0");
                resp.add(mMailParam);

                // Media
                Bundle mMediaplayerParam = new Bundle();
                mVolume = manager.getStreamVolume(AudioManager.STREAM_MUSIC);
                maxVolume = manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                mMediaplayerParam.putString("mediaplayer", "" + mVolume / maxVolume);
                resp.add(mMediaplayerParam);

                setResult(response, DConnectMessage.RESULT_OK);
                response.putExtra("volumes", resp.toArray(new Bundle[resp.size()]));

            }
        }
        return true;
    }

    @Override
    protected boolean onGetDate(final Intent request, final Intent response, final String deviceId) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {

            // 現在の時刻を取得
            Date date = new Date();
            SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy'-'MM'-'dd' 'kk':'mm':'ss'+0900'");

            setDate(response, mDateFormat.format(date));
            setResult(response, DConnectMessage.RESULT_OK);
        }
        return true;
    }

    @Override
    protected boolean onGetDisplayLight(final Intent request, final Intent response, final String deviceId) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {

            // 自動調整ボタンが有効な場合 0が変える
            // 端末画面の明るさを取得(0～255)
            double mLighetLevel = Settings.System.getInt(this.getContext().getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS, 0);
            int maxLevel = MAX_LIGHT_LEVEL;

            setLightLevel(response, mLighetLevel / maxLevel);
            setResult(response, DConnectMessage.RESULT_OK);
        }
        return true;
    }

    @Override
    protected boolean onGetDisplaySleep(final Intent request, final Intent response, final String deviceId) {

        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
            int timeout = Settings.System.getInt(this.getContext().getContentResolver(),
                    Settings.System.SCREEN_OFF_TIMEOUT, 0);
            setTime(response, timeout);
            setResult(response, DConnectMessage.RESULT_OK);
        }
        return true;
    }

    @Override
    protected boolean onPutSoundVolume(final Intent request, final Intent response, final String deviceId,
            final VolumeKind kind, final Double level) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
            if (level == -1.0) {
                MessageUtils.setError(response, ERROR_VALUE_IS_NULL, "level must be more than 0");
                return true;
            }

            this.getContext();
            AudioManager manager = (AudioManager) this.getContext().getSystemService(Context.AUDIO_SERVICE);

            double maxVolume = 1;
            if (kind == VolumeKind.ALARM) {
                maxVolume = manager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
                manager.setStreamVolume(AudioManager.STREAM_ALARM, (int) (maxVolume * level), 1);
                setResult(response, DConnectMessage.RESULT_OK);
            } else if (kind == VolumeKind.CALL) {
                maxVolume = manager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
                manager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, (int) (maxVolume * level), 1);
                setResult(response, DConnectMessage.RESULT_OK);
            } else if (kind == VolumeKind.RINGTONE) {
                maxVolume = manager.getStreamMaxVolume(AudioManager.STREAM_RING);
                manager.setStreamVolume(AudioManager.STREAM_RING, (int) (maxVolume * level), 1);
                setResult(response, DConnectMessage.RESULT_OK);
            } else if (kind == VolumeKind.MAIL) {
                setResult(response, DConnectMessage.RESULT_OK);
            } else if (kind == VolumeKind.OTHER) {
                maxVolume = manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                manager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) (maxVolume * level), 1);
                setResult(response, DConnectMessage.RESULT_OK);
            }
        }
        return true;
    }

    @Override
    protected boolean onPutDate(final Intent request, final Intent response, final String deviceId, final String date) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
            if (date != null) {
                MessageUtils.setError(response, ERROR_VALUE_IS_NULL, "not support");
                return true;
            } else {
                MessageUtils.setError(response, ERROR_VALUE_IS_NULL, "not support");
                return true;
            }

        }
        return true;
    }

    @Override
    protected boolean onPutDisplayLight(final Intent request, final Intent response,
            final String deviceId, final Double level) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
            if (level == -1.0) {
                MessageUtils.setError(response, ERROR_VALUE_IS_NULL, "level must be more than 0");
                return true;
            }

            int maxLevel = MAX_LIGHT_LEVEL;

            Settings.System.putInt(this.getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,
                    (int) (maxLevel * level));
            setResult(response, DConnectMessage.RESULT_OK);
        }
        return true;
    }

    @Override
    protected boolean onPutDisplaySleep(final Intent request, final Intent response,
            final String deviceId, final Integer time) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
            if (time == -1.0) {
                MessageUtils.setError(response, ERROR_VALUE_IS_NULL, "time must be more than 0");
                return true;
            }
            Settings.System.putInt(this.getContext().getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, time);
            setResult(response, DConnectMessage.RESULT_OK);
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
