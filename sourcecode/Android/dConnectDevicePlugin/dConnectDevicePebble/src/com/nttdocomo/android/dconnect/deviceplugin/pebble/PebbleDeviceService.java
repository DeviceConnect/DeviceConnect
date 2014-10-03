package com.nttdocomo.android.dconnect.deviceplugin.pebble;

import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import com.nttdocomo.android.dconnect.deviceplugin.pebble.profile.PebbleBatteryProfile;
import com.nttdocomo.android.dconnect.deviceplugin.pebble.profile.PebbleDeviceOrientationProfile;
import com.nttdocomo.android.dconnect.deviceplugin.pebble.profile.PebbleFileProfile;
import com.nttdocomo.android.dconnect.deviceplugin.pebble.profile.PebbleNetworkServceDiscoveryProfile;
import com.nttdocomo.android.dconnect.deviceplugin.pebble.profile.PebbleNotificationProfile;
import com.nttdocomo.android.dconnect.deviceplugin.pebble.profile.PebbleSettingProfile;
import com.nttdocomo.android.dconnect.deviceplugin.pebble.profile.PebbleSystemProfile;
import com.nttdocomo.android.dconnect.deviceplugin.pebble.profile.PebbleVibrationProfile;
import com.nttdocomo.android.dconnect.deviceplugin.pebble.util.PebbleManager;
import com.nttdocomo.android.dconnect.event.EventManager;
import com.nttdocomo.android.dconnect.event.cache.db.DBCacheController;
import com.nttdocomo.android.dconnect.message.DConnectMessageService;
import com.nttdocomo.android.dconnect.profile.NetworkServiceDiscoveryProfile;
import com.nttdocomo.android.dconnect.profile.SystemProfile;
import com.nttdocomo.android.dconnect.provider.FileManager;

/**
 * Pebbleデバイスプロバイダ.
 */
public class PebbleDeviceService extends DConnectMessageService {
    /**
     * Pebbleとのインターフェースを管理するクラス.
     */
    private PebbleManager mPebbleManager;

    @Override
    public void onCreate() {
        // super.onCreate前に初期かしておかないとダメ
        mPebbleManager = new PebbleManager(this);

        super.onCreate();

        // initialize of the EventManager
        EventManager.INSTANCE.setController(new DBCacheController(this));

        // create FileManager 
        FileManager fileMgr = new FileManager(this);

        // add supported profiles
        addProfile(new PebbleNotificationProfile());
        addProfile(new PebbleDeviceOrientationProfile(this));
        addProfile(new PebbleVibrationProfile());
        addProfile(new PebbleBatteryProfile(this));
        addProfile(new PebbleSettingProfile());
        addProfile(new PebbleFileProfile(fileMgr));
    }

    @Override
    public void onDestroy() {
        // Pebbleの後始末を行う
        mPebbleManager.destory();
        super.onDestroy();
    }

    @Override
    protected SystemProfile getSystemProfile() {
        return new PebbleSystemProfile(this);
    }

    @Override
    protected NetworkServiceDiscoveryProfile getNetworkServiceDiscoveryProfile() {
        return new PebbleNetworkServceDiscoveryProfile(this);
    }

    /**
     * Pebble管理クラスを取得する.
     * 
     * @return Pebble管理クラス
     */
    public PebbleManager getPebbleManager() {
        return mPebbleManager;
    }
    
    /**
     * 現在接続されているPebbleのデバイスIDを取得する.
     * <p>
     * 発見されない場合にはnullを返却する。
     * </p>
     * <p>
     * Pebbleが複数台接続されたときの挙動が不明。<br/>
     * PebbleKitでは、命令を識別して出す機能はない。<br/>
     * 基本は1対1で考える。<br/>
     * </p>
     * @return デバイスID
     */
    public String getDeviceId() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> bondedDevices = defaultAdapter.getBondedDevices();
        if (bondedDevices.size() > 0) {
            for (BluetoothDevice device : bondedDevices) {
                String deviceName = device.getName();
                String deviceAddress = device.getAddress();
                // URIに使えるように、Macアドレスの":"を取り除いて小文字に変換する 
                String deviceid = deviceAddress.replace(":", "").toLowerCase();
                if (deviceName.indexOf("Pebble") != -1) {
                    return PebbleNetworkServceDiscoveryProfile.DEVICE_ID + deviceid;
                }
            }
        }
        return null;
    }
}
