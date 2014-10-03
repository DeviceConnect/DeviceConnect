package com.nttdocomo.android.dconnect.deviceplugin.pebble.profile;

import java.util.logging.Logger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.getpebble.android.kit.util.PebbleDictionary;
import com.nttdocomo.android.dconnect.deviceplugin.pebble.PebbleDeviceService;
import com.nttdocomo.android.dconnect.deviceplugin.pebble.setting.PebbleSettingActivity;
import com.nttdocomo.android.dconnect.deviceplugin.pebble.util.PebbleManager;
import com.nttdocomo.android.dconnect.event.EventError;
import com.nttdocomo.android.dconnect.event.EventManager;
import com.nttdocomo.android.dconnect.message.MessageUtils;
import com.nttdocomo.android.dconnect.profile.DConnectProfileProvider;
import com.nttdocomo.android.dconnect.profile.SystemProfile;
import com.nttdocomo.dconnect.message.DConnectMessage;

/**
 * Pebbleデバイスプラグイン, System プロファイル.
 */
public class PebbleSystemProfile extends SystemProfile {
    /** debug log. */
    private Logger mLogger = Logger.getLogger("Pebble");
    /**
     * コンストラクタ.
     * 
     * @param provider プロファイルリスト.
     */
    public PebbleSystemProfile(final DConnectProfileProvider provider) {
        super(provider);
    }

    @Override
    protected Class<? extends Activity> getSettingPageActivity(final Intent request, final Bundle param) {
        //設定画面を作る
        return PebbleSettingActivity.class;
    }
    
    @Override
    protected boolean onDeleteEvents(final Intent request, final Intent response, final String sessionKey) {
        mLogger.fine("onDeleteEvents delete /system/events");
        if (sessionKey == null) {
            createEmptySessionKey(response);
            return true;
        } 
        PebbleManager mgr = ((PebbleDeviceService) getContext()).getPebbleManager();
        mLogger.fine("onDeleteEvents delete system");
        // PebbleにEVENT解除依頼を送る
        sendDeleteEvent(PebbleManager.PROFILE_SYSTEM, PebbleManager.SYSTEM_ATTRIBUTE_EVENTS, mgr); 
        // ここでイベントの解除をする
        //boolean error = EventManager.INSTANCE.removeEvents(sessionKey); SpheroSystemProfileはこっちを利用
        EventError error = EventManager.INSTANCE.removeEvent(request);
        if (error == EventError.NONE) {
            mLogger.fine("onDeleteEvents delete system OK!!!");
            setResult(response, DConnectMessage.RESULT_OK);
        } else if (error == EventError.INVALID_PARAMETER) {
            mLogger.fine("onDeleteEvents delete system invalid request error!!!!!!!!!!!!!");
            MessageUtils.setInvalidRequestParameterError(response);
        } else {
            mLogger.fine("onDeleteEvents delete system unknown error!!!!!!!!!!!!!");
            MessageUtils.setUnknownError(response);
        }
        return true;
    }
    protected boolean TestTest_onDeleteEvents(final Intent request, final Intent response, final String sessionKey) {
        mLogger.fine("onDeleteEvents delete /system/events");
        if (sessionKey == null) {
            createEmptySessionKey(response);
            return true;
        } 
        PebbleManager mgr = ((PebbleDeviceService) getContext()).getPebbleManager();
        mLogger.fine("onDeleteEvents delete system");
        // PebbleにEVENT解除依頼を送る
        sendDeleteEvent(PebbleManager.PROFILE_SYSTEM, PebbleManager.SYSTEM_ATTRIBUTE_EVENTS, mgr); 
        // ここでイベントの解除をする この方法でも時間がかかる
        if (EventManager.INSTANCE.removeEvents(sessionKey)) {
            setResult(response, DConnectMessage.RESULT_OK);
        }
        else {
            MessageUtils.setUnknownError(response);
        }
        return true;
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
     * delete event を送る.
     * @param profile profile.
     * @param attribute attribute.
     * @param mgr PebbleManager
     */
    private void sendDeleteEvent(final int profile, final int attribute, final PebbleManager mgr) {
        PebbleDictionary dic = new PebbleDictionary(); 
        dic.addInt8(PebbleManager.KEY_PROFILE, (byte) profile);
        dic.addInt8(PebbleManager.KEY_ATTRIBUTE, (byte) attribute);
        dic.addInt8(PebbleManager.KEY_ACTION, (byte) PebbleManager.ACTION_DELETE);
        mgr.sendCommandToPebble(dic, null);
    }
}
