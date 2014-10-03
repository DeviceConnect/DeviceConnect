package com.nttdocomo.android.dconnect.deviceplugin.sphero;

import java.util.ArrayList;
import java.util.Collection;

import orbotix.sphero.Sphero;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.nttdocomo.android.dconnect.deviceplugin.sphero.SpheroManager.DeviceDiscoveryListener;
import com.nttdocomo.android.dconnect.deviceplugin.sphero.data.DeviceInfo;
import com.nttdocomo.android.dconnect.deviceplugin.sphero.profile.SpheroDeviceOrientationProfile;
import com.nttdocomo.android.dconnect.deviceplugin.sphero.profile.SpheroDriveControllerProfile;
import com.nttdocomo.android.dconnect.deviceplugin.sphero.profile.SpheroLightProfile;
import com.nttdocomo.android.dconnect.deviceplugin.sphero.profile.SpheroNetworkServceDiscoveryProfile;
import com.nttdocomo.android.dconnect.deviceplugin.sphero.profile.SpheroProfile;
import com.nttdocomo.android.dconnect.deviceplugin.sphero.profile.SpheroSystemProfile;
import com.nttdocomo.android.dconnect.deviceplugin.sphero.setting.SettingActivity;
import com.nttdocomo.android.dconnect.event.EventManager;
import com.nttdocomo.android.dconnect.event.cache.db.DBCacheController;
import com.nttdocomo.android.dconnect.message.DConnectMessageService;
import com.nttdocomo.android.dconnect.profile.NetworkServiceDiscoveryProfile;
import com.nttdocomo.android.dconnect.profile.SystemProfile;

/**
 * Spheroデバイスプラグイン.
 */
public class SpheroDeviceService extends DConnectMessageService implements DeviceDiscoveryListener {

    /** TAG. */
    private static final String TAG = "PluginSphero";

    /**
     * 検知開始アクション.
     */
    public static final String ACTION_START_DISCOVERY 
    = "com.nttdocomo.android.dconnect.deviceplugin.sphero.START_DISCOVERY";

    /**
     * 検知終了アクション.
     */
    public static final String ACTION_STOP_DISCOVERY 
    = "com.nttdocomo.android.dconnect.deviceplugin.sphero.STOP_DISCOVERY";

    /**
     * 接続アクション.
     */
    public static final String ACTION_CONNECT = "com.nttdocomo.android.dconnect.deviceplugin.sphero.CONNECT";

    /**
     * 接続解除アクション.
     */
    public static final String ACTION_DISCONNECT = "com.nttdocomo.android.dconnect.deviceplugin.sphero.DISCONNECT";

    /**
     * 接続済みデバイス取得アクション.
     */
    public static final String ACTION_GET_CONNECTED 
    = "com.nttdocomo.android.dconnect.deviceplugin.sphero.GET_CONNECTED";
    
    /**
     * Extraキー : {@value} .
     */
    public static final String EXTRA_ID = "id";

    /** 
     * レシーバー.
     */
    private BroadcastReceiver mReceiver;

    @Override
    public void onCreate() {
        super.onCreate();

        EventManager.INSTANCE.setController(new DBCacheController(this));
        SpheroManager.INSTANCE.setService(this);
        
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_CONNECT);
        filter.addAction(ACTION_DISCONNECT);
        filter.addAction(ACTION_GET_CONNECTED);
        filter.addAction(ACTION_START_DISCOVERY);
        filter.addAction(ACTION_STOP_DISCOVERY);
        
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                onStartCommand(intent, 0, 0);
            }
        };
        lbm.registerReceiver(mReceiver, filter);

        // 追加するプロファイル
        addProfile(new SpheroLightProfile());
        addProfile(new SpheroDriveControllerProfile());
        addProfile(new SpheroDeviceOrientationProfile());
        addProfile(new SpheroProfile());
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {

        if (intent != null) {
            String action = intent.getAction();
            if (action.equals(ACTION_START_DISCOVERY)) {
                SpheroManager.INSTANCE.setDiscoveryListener(this);
                SpheroManager.INSTANCE.startDiscovery(this);
            } else if (action.equals(ACTION_STOP_DISCOVERY)) {
                SpheroManager.INSTANCE.setDiscoveryListener(null);
                SpheroManager.INSTANCE.stopDiscovery();
            } else if (action.equals(ACTION_CONNECT)) {
                final String id = intent.getStringExtra(EXTRA_ID);
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        if (SpheroManager.INSTANCE.connect(id)) {
                            if (BuildConfig.DEBUG) {
                                Log.d(TAG, "************ connected **********");
                            }
                            DeviceInfo info = SpheroManager.INSTANCE.getDevice(id);
                            Sphero device;
                            if (info == null) {
                                device = null;
                            } else {
                                device = info.getDevice();
                            }
                            sendDevice(SettingActivity.ACTION_CONNECTED, device);
                        } else {
                            if (BuildConfig.DEBUG) {
                                Log.d(TAG, "************ fialed to connect **********");
                            }
                            sendDevice(SettingActivity.ACTION_CONNECTED, null);
                        }
                    }
                }).start();
            } else if (action.equals(ACTION_DISCONNECT)) {
                String id = intent.getStringExtra(EXTRA_ID);
                SpheroManager.INSTANCE.disconnect(id);
            } else if (action.equals(ACTION_GET_CONNECTED)) {
                Collection<DeviceInfo> devices = SpheroManager.INSTANCE.getConnectedDevices();
                Intent res = new Intent();
                res.setAction(SettingActivity.ACTION_ADD_CONNECTED_DEVICE);
                ArrayList<Sphero> devs = new ArrayList<Sphero>();
                for (DeviceInfo info : devices) {
                    devs.add(info.getDevice());
                }
                res.putParcelableArrayListExtra(SettingActivity.EXTRA_DEVICES, devs);
                LocalBroadcastManager.getInstance(this).sendBroadcast(res);
            } else {
                return super.onStartCommand(intent, flags, startId);
            }
        }

        return START_STICKY;
    }

    @Override
    protected SystemProfile getSystemProfile() {
        return new SpheroSystemProfile(this);
    }

    @Override
    protected NetworkServiceDiscoveryProfile getNetworkServiceDiscoveryProfile() {
        return new SpheroNetworkServceDiscoveryProfile();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
        lbm.unregisterReceiver(mReceiver);
        SpheroManager.INSTANCE.shutdown();
    }

    @Override
    public void onDeviceFound(final Sphero sphero) {
        sendDevice(SettingActivity.ACTION_ADD_DEVICE, sphero);
    }

    @Override
    public void onDeviceLost(final Sphero sphero) {
        sendDevice(SettingActivity.ACTION_REMOVE_DEVICE, sphero);
    }

    /**
     * デバイスの情報を送る.
     * 
     * @param action アクション
     * @param sphero デバイス情報
     */
    private void sendDevice(final String action, final Sphero sphero) {
        Intent res = new Intent();
        res.setAction(action);
        res.putExtra(SettingActivity.EXTRA_DEVICE, sphero);
        LocalBroadcastManager.getInstance(this).sendBroadcast(res);
    }
}
