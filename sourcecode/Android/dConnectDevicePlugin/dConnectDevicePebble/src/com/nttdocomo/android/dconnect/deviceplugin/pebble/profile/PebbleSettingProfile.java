package com.nttdocomo.android.dconnect.deviceplugin.pebble.profile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Intent;

import com.getpebble.android.kit.util.PebbleDictionary;
import com.nttdocomo.android.dconnect.deviceplugin.pebble.PebbleDeviceService;
import com.nttdocomo.android.dconnect.deviceplugin.pebble.util.PebbleManager;
import com.nttdocomo.android.dconnect.deviceplugin.pebble.util.PebbleManager.OnSendCommandListener;
import com.nttdocomo.android.dconnect.message.MessageUtils;
import com.nttdocomo.android.dconnect.profile.SettingsProfile;
import com.nttdocomo.dconnect.message.DConnectMessage;

/**
 * pebble端末内時間情報取得プロファイル.
 */
public class PebbleSettingProfile extends SettingsProfile {
    @Override
    protected boolean onGetDate(final Intent request, final Intent response, 
            final String deviceId) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
            return true;
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
            return true;
        } else {
            // Pebbleに送信
            PebbleManager mgr = ((PebbleDeviceService) getContext()).getPebbleManager();
            PebbleDictionary dic = new PebbleDictionary();
            dic.addInt8(PebbleManager.KEY_PROFILE, (byte) PebbleManager.PROFILE_SETTING);
            dic.addInt8(PebbleManager.KEY_ATTRIBUTE, (byte) PebbleManager.SETTING_ATTRIBUTE_DATE);
            dic.addInt8(PebbleManager.KEY_ACTION, (byte) PebbleManager.ACTION_GET);
            mgr.sendCommandToPebble(dic, new OnSendCommandListener() {
                @Override
                public void onReceivedData(final PebbleDictionary dic) {
                    if (dic == null) {
                        MessageUtils.setTimeoutError(response);
                    } else {
                        String date = dic.getString(PebbleManager.KEY_PARAM_SETTING_DATE);
                        if (date == null) {
                            MessageUtils.setTimeoutError(response);
                        } else {
                            setResult(response, DConnectMessage.RESULT_OK);
                            setDate(response, date);
                        }
                    }
                    getContext().sendBroadcast(response);
                }
            });
            return false;
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
     * デバイスが発見できなかった場合のエラーを作成する.
     * 
     * @param response レスポンスを格納するIntent
     */
    private void createNotFoundDevice(final Intent response) {
        MessageUtils.setNotFoundDeviceError(response);
    }
}
