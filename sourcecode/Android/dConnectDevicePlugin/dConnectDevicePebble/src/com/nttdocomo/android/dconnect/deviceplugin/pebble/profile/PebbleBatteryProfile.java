package com.nttdocomo.android.dconnect.deviceplugin.pebble.profile;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Intent;
import android.os.Bundle;

import com.getpebble.android.kit.util.PebbleDictionary;
import com.nttdocomo.android.dconnect.deviceplugin.pebble.PebbleDeviceService;
import com.nttdocomo.android.dconnect.deviceplugin.pebble.util.PebbleManager;
import com.nttdocomo.android.dconnect.deviceplugin.pebble.util.PebbleManager.OnReceivedEventListener;
import com.nttdocomo.android.dconnect.deviceplugin.pebble.util.PebbleManager.OnSendCommandListener;
import com.nttdocomo.android.dconnect.event.Event;
import com.nttdocomo.android.dconnect.event.EventError;
import com.nttdocomo.android.dconnect.event.EventManager;
import com.nttdocomo.android.dconnect.message.MessageUtils;
import com.nttdocomo.android.dconnect.profile.BatteryProfile;
import com.nttdocomo.dconnect.message.DConnectMessage;


/**
 * Pebble用 Batteryプロファイル.
 * 
 * @author dconnect04
 */
public class PebbleBatteryProfile extends BatteryProfile {
    /** パーセント値にする時の定数. */
    private static final double TO_PERCENT = 100.0;
    /**
     * コンストラクタ.
     * @param service Pebble デバイスサービス
     */
    public PebbleBatteryProfile(final PebbleDeviceService service) {
        service.getPebbleManager().addEventListener(PebbleManager.PROFILE_BATTERY, new OnReceivedEventListener() {
            @Override
            public void onReceivedEvent(final PebbleDictionary dic) {
                Long attribute = dic.getInteger(PebbleManager.KEY_ATTRIBUTE);
                if (attribute == null) {
                    return;
                }

                switch (attribute.intValue()) {
                case PebbleManager.BATTERY_ATTRIBUTE_ON_BATTERY_CHANGE:
                    sendOnBatteryChange(dic);
                    break;
                case PebbleManager.BATTERY_ATTRIBUTE_ON_CHARGING_CHANGE:
                    sendOnChargingChange(dic);
                    break;
                }
            }
        });
    }

    @Override
    protected boolean onGetAll(final Intent request, final Intent response, final String deviceId) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
            return true;
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
            return true;
        } else {
            PebbleManager mgr = ((PebbleDeviceService) getContext()).getPebbleManager();
            PebbleDictionary dic = new PebbleDictionary();
            dic.addInt8(PebbleManager.KEY_PROFILE, (byte) PebbleManager.PROFILE_BATTERY);
            dic.addInt8(PebbleManager.KEY_ATTRIBUTE, (byte) PebbleManager.BATTERY_ATTRIBUTE_ALL);
            dic.addInt8(PebbleManager.KEY_ACTION, (byte) PebbleManager.ACTION_GET);
            mgr.sendCommandToPebble(dic, new OnSendCommandListener() {
                @Override
                public void onReceivedData(final PebbleDictionary dic) {
                    if (dic == null) {
                        errorPebbleSideApplicationNotFound(response);
                    } else {
                        Long level = dic.getInteger(PebbleManager.KEY_PARAM_BATTERY_LEVEL);
                        Long charging = dic.getInteger(PebbleManager.KEY_PARAM_BATTERY_CHARGING);
                        if (charging == null || level == null) {
                            MessageUtils.setUnknownError(response);
                        } else {
                            double l = level.intValue() / TO_PERCENT;
                            boolean isCharging = (charging.intValue() == PebbleManager.BATTERY_CHARGING_ON);
                            setResult(response, DConnectMessage.RESULT_OK);
                            setCharging(response, isCharging);
                            setLevel(response, l);
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
    protected boolean onGetLevel(final Intent request, final Intent response, final String deviceId) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
            return true;
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
            return true;
        } else {
            PebbleManager mgr = ((PebbleDeviceService) getContext()).getPebbleManager();
            PebbleDictionary dic = new PebbleDictionary();
            dic.addInt8(PebbleManager.KEY_PROFILE, (byte) PebbleManager.PROFILE_BATTERY);
            dic.addInt8(PebbleManager.KEY_ATTRIBUTE, (byte) PebbleManager.BATTERY_ATTRIBUTE_LEVEL);
            dic.addInt8(PebbleManager.KEY_ACTION, (byte) PebbleManager.ACTION_GET);
            mgr.sendCommandToPebble(dic, new OnSendCommandListener() {
                @Override
                public void onReceivedData(final PebbleDictionary dic) {
                    if (dic == null) {
                        errorPebbleSideApplicationNotFound(response);
                    } else {
                        Long level = dic.getInteger(PebbleManager.KEY_PARAM_BATTERY_LEVEL);
                        if (level == null) {
                            MessageUtils.setUnknownError(response);
                        } else {
                            double l = level.intValue() / TO_PERCENT;
                            setResult(response, DConnectMessage.RESULT_OK);
                            setLevel(response, l);
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
    protected boolean onGetCharging(final Intent request, final Intent response, final String deviceId) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
            return true;
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
            return true;
        } else {
            PebbleManager mgr = ((PebbleDeviceService) getContext()).getPebbleManager();
            PebbleDictionary dic = new PebbleDictionary();
            dic.addInt8(PebbleManager.KEY_PROFILE, (byte) PebbleManager.PROFILE_BATTERY);
            dic.addInt8(PebbleManager.KEY_ATTRIBUTE, (byte) PebbleManager.BATTERY_ATTRIBUTE_CHARING);
            dic.addInt8(PebbleManager.KEY_ACTION, (byte) PebbleManager.ACTION_GET);
            mgr.sendCommandToPebble(dic, new OnSendCommandListener() {
                @Override
                public void onReceivedData(final PebbleDictionary dic) {
                    if (dic == null) {
                        errorPebbleSideApplicationNotFound(response);
                    } else {
                        Long charging = dic.getInteger(PebbleManager.KEY_PARAM_BATTERY_CHARGING);
                        if (charging == null) {
                            MessageUtils.setUnknownError(response);
                        } else {
                            boolean isCharging = (charging.intValue() == PebbleManager.BATTERY_CHARGING_ON);
                            setResult(response, DConnectMessage.RESULT_OK);
                            setCharging(response, isCharging);
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
    protected boolean onPutOnBatteryChange(final Intent request, final Intent response, 
            final String deviceId, final String sessionKey) {
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
            PebbleDictionary dic = new PebbleDictionary();
            dic.addInt8(PebbleManager.KEY_PROFILE, (byte) PebbleManager.PROFILE_BATTERY);
            dic.addInt8(PebbleManager.KEY_ATTRIBUTE, (byte) PebbleManager.BATTERY_ATTRIBUTE_ON_BATTERY_CHANGE);
            dic.addInt8(PebbleManager.KEY_ACTION, (byte) PebbleManager.ACTION_PUT);
            mgr.sendCommandToPebble(dic, new OnSendCommandListener() {
                @Override
                public void onReceivedData(final PebbleDictionary dic) {
                    if (dic == null) {
                        MessageUtils.setUnknownError(response);
                    } else {
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
    protected boolean onPutOnChargingChange(final Intent request, final Intent response, 
            final String deviceId, final String sessionKey) {
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
            PebbleDictionary dic = new PebbleDictionary();
            dic.addInt8(PebbleManager.KEY_PROFILE, (byte) PebbleManager.PROFILE_BATTERY);
            dic.addInt8(PebbleManager.KEY_ATTRIBUTE, (byte) PebbleManager.BATTERY_ATTRIBUTE_ON_CHARGING_CHANGE);
            dic.addInt8(PebbleManager.KEY_ACTION, (byte) PebbleManager.ACTION_PUT);
            mgr.sendCommandToPebble(dic, new OnSendCommandListener() {
                @Override
                public void onReceivedData(final PebbleDictionary dic) {
                    if (dic == null) {
                        MessageUtils.setUnknownError(response);
                    } else {
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
    protected boolean onDeleteOnBatteryChange(Intent request, Intent response, String deviceId, String sessionKey) {
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
            PebbleDictionary dic = new PebbleDictionary();
            dic.addInt8(PebbleManager.KEY_PROFILE, (byte) PebbleManager.PROFILE_BATTERY);
            dic.addInt8(PebbleManager.KEY_ATTRIBUTE, (byte) PebbleManager.BATTERY_ATTRIBUTE_ON_BATTERY_CHANGE);
            dic.addInt8(PebbleManager.KEY_ACTION, (byte) PebbleManager.ACTION_DELETE);
            mgr.sendCommandToPebble(dic, new OnSendCommandListener() {
                @Override
                public void onReceivedData(final PebbleDictionary dic) {
                    // do nothing.
                }
            });
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

    @Override
    protected boolean onDeleteOnChargingChange(final Intent request, final Intent response, 
            final String deviceId, final String sessionKey) {
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
            PebbleDictionary dic = new PebbleDictionary();
            dic.addInt8(PebbleManager.KEY_PROFILE, (byte) PebbleManager.PROFILE_BATTERY);
            dic.addInt8(PebbleManager.KEY_ATTRIBUTE, (byte) PebbleManager.BATTERY_ATTRIBUTE_ON_CHARGING_CHANGE);
            dic.addInt8(PebbleManager.KEY_ACTION, (byte) PebbleManager.ACTION_DELETE);
            mgr.sendCommandToPebble(dic, new OnSendCommandListener() {
                @Override
                public void onReceivedData(final PebbleDictionary dic) {
                    // do nothing.
                }
            });
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

    /**
     * バッテリーの状態変更イベントを送信する.
     * 
     * @param dic バッテリー状態変更イベント
     */
    private void sendOnBatteryChange(final PebbleDictionary dic) {
        PebbleDeviceService service = (PebbleDeviceService) getContext();

        Long level = dic.getInteger(PebbleManager.KEY_PARAM_BATTERY_LEVEL);
        if (level == null) {
            return;
        }

        Bundle battery = new Bundle();
        setLevel(battery, level.intValue() / TO_PERCENT);

        List<Event> evts = EventManager.INSTANCE.getEventList(service.getDeviceId(),
                PROFILE_NAME, null, ATTRIBUTE_ON_BATTERY_CHANGE);
        for (Event evt : evts) {
            Intent intent = EventManager.createEventMessage(evt);
            setBattery(intent, battery);
            ((PebbleDeviceService) getContext()).sendEvent(intent, evt.getAccessToken());
        }
    }

    /**
     * バッテリーチャージングイベントを返却する.
     * 
     * @param dic チャージングイベント
     */
    private void sendOnChargingChange(final PebbleDictionary dic) {
        PebbleDeviceService service = (PebbleDeviceService) getContext();

        Long charging = dic.getInteger(PebbleManager.KEY_PARAM_BATTERY_CHARGING);
        if (charging == null) {
            return;
        }

        boolean isCharging = (charging.intValue() == PebbleManager.BATTERY_CHARGING_ON);

        Bundle battery = new Bundle();
        setCharging(battery, isCharging);

        List<Event> evts = EventManager.INSTANCE.getEventList(service.getDeviceId(),
                PROFILE_NAME, null, ATTRIBUTE_ON_CHARGING_CHANGE);
        for (Event evt : evts) {
            Intent intent = EventManager.createEventMessage(evt);
            setBattery(intent, battery);
            ((PebbleDeviceService) getContext()).sendEvent(intent, evt.getAccessToken());
        }
    }
    /**
     * Pebble 側のアプリケーションが存在しない場合のエラーメッセージ.
     * @param response レスポンス.
     */
    private void errorPebbleSideApplicationNotFound( final Intent response) {
        MessageUtils.setTimeoutError(response, "Pebble side application is NOT FOUND!");
    }
}
