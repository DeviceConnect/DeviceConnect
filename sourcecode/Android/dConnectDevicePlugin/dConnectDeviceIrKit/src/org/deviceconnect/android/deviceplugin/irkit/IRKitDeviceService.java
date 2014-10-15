/*
 IRKitDeviceService.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.irkit;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.deviceconnect.android.deviceplugin.irkit.IRKitManager.DetectionListener;
import org.deviceconnect.android.deviceplugin.irkit.network.WiFiUtil;
import org.deviceconnect.android.deviceplugin.irkit.profile.IRKitNetworkServceDiscoveryProfile;
import org.deviceconnect.android.deviceplugin.irkit.profile.IRKitRmeoteControllerProfile;
import org.deviceconnect.android.deviceplugin.irkit.profile.IRKitSystemProfile;
import org.deviceconnect.android.event.Event;
import org.deviceconnect.android.event.EventManager;
import org.deviceconnect.android.event.cache.MemoryCacheController;
import org.deviceconnect.android.localoauth.LocalOAuth2Main;
import org.deviceconnect.android.message.DConnectMessageService;
import org.deviceconnect.android.profile.NetworkServiceDiscoveryProfile;
import org.deviceconnect.android.profile.SystemProfile;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.profile.NetworkServiceDiscoveryProfileConstants.NetworkType;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

/**
 * IRKitデバイスプラグインサービス.
 * @author NTT DOCOMO, INC.
 */
public class IRKitDeviceService extends DConnectMessageService implements DetectionListener {
    /**
     * 検知したデバイス群.
     */
    private ConcurrentHashMap<String, IRKitDevice> mDevices;

    /**
     * 現在のSSID.
     */
    private String mCurrentSSID;

    @Override
    public void onCreate() {
        super.onCreate();
        
        EventManager.INSTANCE.setController(new MemoryCacheController());
        mDevices = new ConcurrentHashMap<String, IRKitDevice>();

        IRKitManager.INSTANCE.init(this);
        IRKitManager.INSTANCE.setDetectionListener(this);
        if (WiFiUtil.isOnWiFi(this)) {
            startDetection();
        }

        // 追加するプロファイル
        addProfile(new IRKitRmeoteControllerProfile());

        mCurrentSSID = WiFiUtil.getCurrentSSID(this);
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        super.onStartCommand(intent, flags, startId);

        if (intent != null) {

            String action = intent.getAction();

            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                if (!WiFiUtil.isOnWiFi(this) && IRKitManager.INSTANCE.isDetecting()) {
                    stopDetection();
                } else if (WiFiUtil.isOnWiFi(this) && WiFiUtil.isChangedSSID(this, mCurrentSSID)) {
                    stopDetection();
                    startDetection();
                }
            }

        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopDetection();
        // 参照をきっておく
        IRKitManager.INSTANCE.setDetectionListener(null);
        LocalOAuth2Main.destroy();
    }

    /**
     * デバイスIDからIRKitのデバイスを取得する.
     * 
     * @param deviceId デバイスID
     * @return デバイス
     */
    public IRKitDevice getDevice(final String deviceId) {
        return mDevices.get(deviceId);
    }

    /**
     * getnetworkservicesのリクエストを用意する.
     * 
     * @param response レスポンスオブジェクト
     */
    public void prepareGetNetworkServicesResponse(final Intent response) {

        synchronized (mDevices) {

            Bundle[] services = new Bundle[mDevices.size()];
            int index = 0;
            for (IRKitDevice device : mDevices.values()) {
                Bundle service = createService(device, true);
                services[index++] = service;
                if (BuildConfig.DEBUG) {
                    Log.d("IRKit", "prepareGetNetworkServicesResponse service=" + service);
                }
            }

            NetworkServiceDiscoveryProfile.setServices(response, services);
            NetworkServiceDiscoveryProfile.setResult(response, DConnectMessage.RESULT_OK);
        }

    }

    @Override
    protected SystemProfile getSystemProfile() {
        return new IRKitSystemProfile(this);
    }

    @Override
    protected NetworkServiceDiscoveryProfile getNetworkServiceDiscoveryProfile() {
        return new IRKitNetworkServceDiscoveryProfile();
    }

    @Override
    public void onFoundDevice(final IRKitDevice device) {
        sendDeviceDetectionEvent(device, true);
    }

    @Override
    public void onLostDevice(final IRKitDevice device) {
        sendDeviceDetectionEvent(device, false);
    }

    /**
     * デバイスの検知イベントを送信する.
     * 
     * @param device デバイス
     * @param isOnline trueなら発見、falseなら消失を意味する
     */
    private void sendDeviceDetectionEvent(final IRKitDevice device, final boolean isOnline) {

        boolean hit = false;
        synchronized (mDevices) {

            IRKitDevice d = mDevices.get(device.getName());
            if (d != null) {
                hit = true;
                if (!isOnline) {
                    mDevices.remove(device.getName());
                }
            } else if (isOnline) {
                mDevices.put(device.getName(), device);
            }
        }

        if ((!hit && isOnline) || (hit && !isOnline)) {
            Bundle service = createService(device, isOnline);

            List<Event> events = EventManager.INSTANCE.getEventList(NetworkServiceDiscoveryProfile.PROFILE_NAME,
                    NetworkServiceDiscoveryProfile.ATTRIBUTE_ON_SERVICE_CHANGE);

            for (Event e : events) {
                Intent message = EventManager.createEventMessage(e);
                NetworkServiceDiscoveryProfile.setNetworkService(message, service);
                sendEvent(message, e.getAccessToken());
            }
        }

    }

    /**
     * IRKitのデバイス情報からServiceを生成する.
     * 
     * @param device デバイス情報
     * @param online オンライン状態
     * @return サービス
     */
    private Bundle createService(final IRKitDevice device, final boolean online) {
        Bundle service = new Bundle();
        NetworkServiceDiscoveryProfile.setId(service, device.getName());
        NetworkServiceDiscoveryProfile.setName(service, device.getName());
        NetworkServiceDiscoveryProfile.setType(service, NetworkType.WIFI);
        NetworkServiceDiscoveryProfile.setState(service, online);
        NetworkServiceDiscoveryProfile.setOnline(service, online);
        return service;
    }

    /**
     * 検知を開始する.
     */
    private void startDetection() {
        mCurrentSSID = WiFiUtil.getCurrentSSID(this);
        IRKitManager.INSTANCE.startDetection(this);
    }

    /**
     * 検知を終了する.
     */
    private void stopDetection() {
        mCurrentSSID = null;
        mDevices.clear();
        IRKitManager.INSTANCE.stopDetection();
    }
}
