/*
 HostDeviceService.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.host;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.conn.util.InetAddressUtils;
import org.deviceconnect.android.deviceplugin.host.manager.HostBatteryManager;
import org.deviceconnect.android.deviceplugin.host.profile.HostBatteryProfile;
import org.deviceconnect.android.deviceplugin.host.profile.HostConnectProfile;
import org.deviceconnect.android.deviceplugin.host.profile.HostDeviceOrientationProfile;
import org.deviceconnect.android.deviceplugin.host.profile.HostFileDescriptorProfile;
import org.deviceconnect.android.deviceplugin.host.profile.HostFileProfile;
import org.deviceconnect.android.deviceplugin.host.profile.HostMediaPlayerProfile;
import org.deviceconnect.android.deviceplugin.host.profile.HostMediaStreamingRecordingProfile;
import org.deviceconnect.android.deviceplugin.host.profile.HostNetworkServiceDiscoveryProfile;
import org.deviceconnect.android.deviceplugin.host.profile.HostNotificationProfile;
import org.deviceconnect.android.deviceplugin.host.profile.HostPhoneProfile;
import org.deviceconnect.android.deviceplugin.host.profile.HostProximityProfile;
import org.deviceconnect.android.deviceplugin.host.profile.HostSettingsProfile;
import org.deviceconnect.android.deviceplugin.host.profile.HostSystemProfile;
import org.deviceconnect.android.deviceplugin.host.profile.HostVibrationProfile;
import org.deviceconnect.android.deviceplugin.host.video.VideoConst;
import org.deviceconnect.android.deviceplugin.host.video.VideoPlayer;
import org.deviceconnect.android.event.Event;
import org.deviceconnect.android.event.EventManager;
import org.deviceconnect.android.event.cache.MemoryCacheController;
import org.deviceconnect.android.localoauth.LocalOAuth2Main;
import org.deviceconnect.android.message.DConnectMessageService;
import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.android.profile.DeviceOrientationProfile;
import org.deviceconnect.android.profile.FileDescriptorProfile;
import org.deviceconnect.android.profile.MediaPlayerProfile;
import org.deviceconnect.android.profile.MediaStreamRecordingProfile;
import org.deviceconnect.android.profile.NetworkServiceDiscoveryProfile;
import org.deviceconnect.android.profile.SystemProfile;
import org.deviceconnect.android.provider.FileManager;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.message.intent.message.IntentDConnectMessage;
import org.deviceconnect.profile.FileDescriptorProfileConstants.Flag;
import org.deviceconnect.profile.MediaStreamRecordingProfileConstants;
import org.deviceconnect.profile.PhoneProfileConstants.CallState;

import android.app.ActivityManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;

/**
 * Host Device Service.
 * @author NTT DOCOMO, INC.
 */
public class HostDeviceService extends DConnectMessageService implements SensorEventListener {

    /** Debug Tag. */
    private static final String TAG = "HOST";

    /** onphotoイベント用マップ. */
    private Map<String, Intent> mOnPhotoCallback = new HashMap<String, Intent>();

    /** デバイスID. */
    private static final String DEVICE_ID = "android_camera";

    /** ondataavailable イベント用マップ. */
    private Map<String, Intent> mOnDataAvailableCallback = new HashMap<String, Intent>();

    /** プレビューフラグ. */
    private boolean mWhileFetching = false;

    /** ファイル管理クラス. */
    private FileManager mFileMgr;

    /** ファイル名に付けるプレフィックス. */
    private static final String FILENAME_PREFIX = "android_camera_";

    /** ファイルの拡張子. */
    private static final String FILE_EXTENSION = ".png";

    /** 日付のフォーマット. */
    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyyMMdd_kkmmss", Locale.JAPAN);

    /** SensorManager. */
    private SensorManager mSensorManager;
    
    /** SensorManager. */
    private SensorManager mSensorManagerProximity;

    /** onClick Event. */
    private boolean onClickEventFlag = false;

    /** onConnect Event. */
    private static boolean onConnectEventFlag = false;

    /** DeviceID. */
    private String mDeviceId;

    /** Callback. */
    private IHostDeviceCallback mCallback;

    /** IrKitのIPアドレス. */
    private HashMap<String, String> ips;

    /** ServiceのList. */
    List<Bundle> services;

    /** 圧縮比率. */
    private static final int COMPRESS_PERCENTAGE = 50;
    
    /** バッテリー関連の処理と値処理. */
    private HostBatteryManager mHostBatteryManager;
   
    @Override
    public void onCreate() {
        
        super.onCreate();

        // EventManagerの初期化
        EventManager.INSTANCE.setController(new MemoryCacheController());

        // LocalOAuthの処理
        LocalOAuth2Main.initialize(getApplicationContext());

        // add supported profiles
        addProfile(new HostConnectProfile(BluetoothAdapter.getDefaultAdapter()));
        addProfile(new HostNotificationProfile());
        addProfile(new HostDeviceOrientationProfile());
        addProfile(new HostBatteryProfile());
        addProfile(new HostMediaStreamingRecordingProfile());
        addProfile(new HostPhoneProfile());
        addProfile(new HostSettingsProfile());
        addProfile(new HostMediaPlayerProfile());
        // ファイル管理クラスの作成
        mFileMgr = new FileManager(this);
        addProfile(new HostFileProfile(mFileMgr));
        addProfile(new HostFileDescriptorProfile());
        addProfile(new HostVibrationProfile());
        addProfile(new HostProximityProfile());
        
        // バッテリー関連の処理と値の保持
        mHostBatteryManager = new HostBatteryManager();
        mHostBatteryManager.getBatteryInfo(this.getContext());
        
    }
    
    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        
        if (intent == null) {
            return START_STICKY;
        }
        
        String action = intent.getAction();

        // バッテリーが変化した時
        if (Intent.ACTION_BATTERY_CHANGED.equals(action) 
            || Intent.ACTION_BATTERY_LOW.equals(action)
            || Intent.ACTION_BATTERY_OKAY.equals(action)) {
            mHostBatteryManager.setBatteryRequest(intent);
            
            List<Event> events = EventManager.INSTANCE.getEventList(mDeviceId, 
                    HostBatteryProfile.PROFILE_NAME,
                    null, 
                    HostBatteryProfile.ATTRIBUTE_ON_BATTERY_CHANGE);
            for (int i = 0; i < events.size(); i++) {
                Event event = events.get(i);
                Intent mIntent = EventManager.createEventMessage(event);
                
                HostBatteryProfile.setAttribute(mIntent, HostBatteryProfile.ATTRIBUTE_ON_BATTERY_CHANGE);
                Bundle charging = new Bundle();
                Bundle battery = new Bundle();
                //HostBatteryProfile.setChargingTime(battery, 0);
                //HostBatteryProfile.setDischargingTime(battery, 0);
                HostBatteryProfile.setLevel(battery, mHostBatteryManager.getBatteryLevel());
                getContext().sendBroadcast(mIntent);
            }
            
            return START_STICKY;
        } 
        // バッテリーが充電された時
        else if (Intent.ACTION_POWER_CONNECTED.equals(action)
            || Intent.ACTION_POWER_DISCONNECTED.equals(action)) {
            mHostBatteryManager.setBatteryRequest(intent);
            
            List<Event> events = EventManager.INSTANCE.getEventList(mDeviceId, 
                    HostBatteryProfile.PROFILE_NAME,
                    null, 
                    HostBatteryProfile.ATTRIBUTE_ON_CHARGING_CHANGE);
           
            for (int i = 0; i < events.size(); i++) {
                Event event = events.get(i);
                Intent mIntent = EventManager.createEventMessage(event);
                
                HostBatteryProfile.setAttribute(mIntent, HostBatteryProfile.ATTRIBUTE_ON_CHARGING_CHANGE);
                Bundle charging = new Bundle();
                
                if (Intent.ACTION_POWER_CONNECTED.equals(action)) {
                    HostBatteryProfile.setCharging(charging, true);
                } else {
                    HostBatteryProfile.setCharging(charging, false);   
                }
                
                HostBatteryProfile.setBattery(mIntent, charging);
                getContext().sendBroadcast(mIntent);
            }
            
            return START_STICKY;    
        } 
        // Phone
        else if (action.equals("android.intent.action.NEW_OUTGOING_CALL")) {
            
            List<Event> events = EventManager.INSTANCE.getEventList(mDeviceId, 
                    HostPhoneProfile.PROFILE_NAME,
                    null, 
                    HostPhoneProfile.ATTRIBUTE_ON_CONNECT);

            for (int i = 0; i < events.size(); i++) {
                Event event = events.get(i);
                Intent mIntent = EventManager.createEventMessage(event);
                
                HostPhoneProfile.setAttribute(mIntent, HostPhoneProfile.ATTRIBUTE_ON_CONNECT);
                Bundle phoneStatus = new Bundle();
                HostPhoneProfile.setPhoneNumber(phoneStatus, intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER));
                HostPhoneProfile.setState(phoneStatus, CallState.START);
                HostPhoneProfile.setPhoneStatus(mIntent, phoneStatus);
                getContext().sendBroadcast(mIntent);
            }
            
            return START_STICKY;
        } 
        // Wifi
        else if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)
                || WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
            List<Event> events = EventManager.INSTANCE.getEventList(mDeviceId, 
                    HostConnectProfile.PROFILE_NAME,
                    null, 
                    HostConnectProfile.ATTRIBUTE_ON_WIFI_CHANGE);
           
            for (int i = 0; i < events.size(); i++) {
                Event event = events.get(i);
                Intent mIntent = EventManager.createEventMessage(event);
                
                HostConnectProfile.setAttribute(mIntent, HostConnectProfile.ATTRIBUTE_ON_WIFI_CHANGE);
                Bundle wifiConnecting = new Bundle();
                WifiManager wifiMgr = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
                HostConnectProfile.setEnable(wifiConnecting, wifiMgr.isWifiEnabled());
                HostConnectProfile.setConnectStatus(mIntent, wifiConnecting);
                getContext().sendBroadcast(mIntent);
            } 
            
            return START_STICKY;
            
        } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
            
            List<Event> events = EventManager.INSTANCE.getEventList(mDeviceId, 
                    HostConnectProfile.PROFILE_NAME,
                    null, 
                    HostConnectProfile.ATTRIBUTE_ON_BLUETOOTH_CHANGE);
            
            for (int i = 0; i < events.size(); i++) {
                Event event = events.get(i);
                Intent mIntent = EventManager.createEventMessage(event);
                
                HostConnectProfile.setAttribute(mIntent, HostConnectProfile.ATTRIBUTE_ON_BLUETOOTH_CHANGE);
                Bundle bluetoothConnecting = new Bundle();
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                HostConnectProfile.setEnable(bluetoothConnecting, mBluetoothAdapter.isEnabled());
                HostConnectProfile.setConnectStatus(mIntent, bluetoothConnecting);
                getContext().sendBroadcast(mIntent);
            } 
            
            return START_STICKY;
        }    
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected SystemProfile getSystemProfile() {
        return new HostSystemProfile(this);
    }

    @Override
    protected NetworkServiceDiscoveryProfile getNetworkServiceDiscoveryProfile() {
        return new HostNetworkServiceDiscoveryProfile();
    }
    
    /**
     * DeviceIDを設定.
     * 
     * @param deviceId デバイスID
     */
    public void setDeviceId(final String deviceId) {
        this.mDeviceId = deviceId;
    }
    
    /**
     * Battery Profile<br>
     *  バッテリーレベルを取得.
     *  
     *  @return バッテリーレベル
     */
    public int getBatteryLevel() {
        mHostBatteryManager.getBatteryInfo(this.getContext());
        return mHostBatteryManager.getBatteryLevel();
    }
    
    /**
     * Battery Profile<br>
     *  バッテリーステータスを取得.
     *  
     *  @return バッテリーレベル
     */
    public int getBatteryStatus() {
        mHostBatteryManager.getBatteryInfo(this.getContext());
        return mHostBatteryManager.getBatteryStatus();
    }
    
    /**
     * Battery Profile<br>
     *  バッテリーレベルを取得.
     *  
     *  @return バッテリーレベル
     */
    public int getBatteryScale() {
        mHostBatteryManager.getBatteryInfo(this.getContext());
        return mHostBatteryManager.getBatteryScale();
    }
    //
    // Device Orientation Profice
    //
    /** 加速度 x. */
    private static float mAccellX;

    /** 加速度 y. */
    private static float mAccellY;

    /** 加速度 z. */
    private static float mAccellZ;

    /** Gyro x. */
    private static float mGyroX;

    /** Gyro y. */
    private static float mGyroY;

    /** Gyro z. */
    private static float mGyroZ;
    
    /** 加速度計測のインターバル. */
    private static final int INTERVAL_TIME = 200;

    /**
     * Device Orientation Profile<br>
     * イベントの登録.
     * 
     * @param response レスポンス
     * @param deviceId デバイスID
     * @param sessionKey セッションキー
     */
    public void registerDeviceOrientationEvent(final Intent response, final String deviceId, final String sessionKey) {

        mDeviceId = deviceId;
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);

        if (sensors.size() > 0) {
            Sensor sensor = sensors.get(0);
            mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        sensors = mSensorManager.getSensorList(Sensor.TYPE_GYROSCOPE);

        if (sensors.size() > 0) {
            Sensor sensor = sensors.get(0);
            mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_OK);
        response.putExtra(DConnectMessage.EXTRA_VALUE, "Register OnDeviceOrientation event");
        sendBroadcast(response);
    }
    
    /**
     * Promity Profile<br>
     * イベントの登録.
     * 
     * @param response レスポンス
     * @param deviceId デバイスID
     * @param sessionKey セッションキー
     */
    public void registerPromityEvent(final Intent response, final String deviceId, final String sessionKey) {
        
        mDeviceId = deviceId;
        mSensorManagerProximity = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> sensors = mSensorManagerProximity.getSensorList(Sensor.TYPE_PROXIMITY);

        if (sensors.size() > 0) {
            Sensor sensor = sensors.get(0);
            mSensorManagerProximity.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_OK);
        response.putExtra(DConnectMessage.EXTRA_VALUE, "Register onuserproximity event");
        sendBroadcast(response);
    }

    /**
     * Promity Profile<br>
     * イベントの解除.
     * 
     * @param response レスポンス
     */
    public void unregisterPromityEvent(final Intent response) {
        mSensorManagerProximity.unregisterListener(this);
        response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_OK);
        response.putExtra(DConnectMessage.EXTRA_VALUE, "Unregister onuserproximity event");
        sendBroadcast(response);
    }
    
    /**
     * Device Orientation Profile<br>
     * イベントの解除.
     * 
     * @param response レスポンス
     */
    public void unregisterDeviceOrientationEvent(final Intent response) {
        mSensorManager.unregisterListener(this);
        response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_OK);
        response.putExtra(DConnectMessage.EXTRA_VALUE, "Unregister OnDeviceOrientation event");
        sendBroadcast(response);
    }
    
    /**
     * センサーの値がかわった時に呼ばれる.
     * 
     * @param sensorEvent センサーイベント.
     */
    public void onSensorChanged(final SensorEvent sensorEvent) {
        // TODO Auto-generated method stub

        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            mAccellX = sensorEvent.values[0];
            mAccellY = sensorEvent.values[1];
            mAccellZ = sensorEvent.values[2];

            Bundle orientation = new Bundle();
            Bundle a1 = new Bundle();
            a1.putDouble(DeviceOrientationProfile.PARAM_X, 0.0);
            a1.putDouble(DeviceOrientationProfile.PARAM_Y, 0.0);
            a1.putDouble(DeviceOrientationProfile.PARAM_Z, 0.0);
            Bundle a2 = new Bundle();
            a2.putDouble(DeviceOrientationProfile.PARAM_X, mAccellX);
            a2.putDouble(DeviceOrientationProfile.PARAM_Y, mAccellY);
            a2.putDouble(DeviceOrientationProfile.PARAM_Z, mAccellZ);
            Bundle r = new Bundle();
            r.putDouble(DeviceOrientationProfile.PARAM_ALPHA, mGyroX);
            r.putDouble(DeviceOrientationProfile.PARAM_BETA, mGyroY);
            r.putDouble(DeviceOrientationProfile.PARAM_GAMMA, mGyroZ);
            orientation.putBundle(DeviceOrientationProfile.PARAM_ACCELERATION, a1);
            orientation.putBundle(DeviceOrientationProfile.PARAM_ACCELERATION_INCLUDING_GRAVITY, a2);
            orientation.putBundle(DeviceOrientationProfile.PARAM_ROTATION_RATE, r);
            orientation.putLong(DeviceOrientationProfile.PARAM_INTERVAL, 0);
            DeviceOrientationProfile.setInterval(orientation, INTERVAL_TIME);
            
            List<Event> events = EventManager.INSTANCE.getEventList(mDeviceId, DeviceOrientationProfile.PROFILE_NAME,
                    null, DeviceOrientationProfile.ATTRIBUTE_ON_DEVICE_ORIENTATION);

            for (int i = 0; i < events.size(); i++) {
                Event event = events.get(i);
                Intent intent = EventManager.createEventMessage(event);

                intent.putExtra(DeviceOrientationProfile.PARAM_ORIENTATION, orientation);
                getContext().sendBroadcast(intent);
            }

        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            mGyroX = sensorEvent.values[0];
            mGyroY = sensorEvent.values[1];
            mGyroZ = sensorEvent.values[2];
        } 
    }
    
    //
    // File Descriptor Profile
    //

    /** File Output Stream. */
    private static FileOutputStream mFos = null;
    
    /** File Input Stream. */
    private static FileInputStream mFis = null;
    
    /** 
     * FileがオープンかどうかのFlag.<br>
     * 開いている:true, 開いていない: false
     */
    private Boolean mFileOpenFlag = false;
    
    /** Fileネームを保持する変数. */
    private String mFileName = "";
    
    /** EventのFlag. */
    private boolean onWatchfileEventFlag = false;
    
    /** 
     * 現在の更新時間.<br>
     * 更新時間の定義は、open, write, readが実施されたタイミング
     */
    private long mFileDescriptorCurrentSystemTime;
    
    /**  現在の更新時間. */
    private String mFileDescriptorCurrentTime = "";
    
    /** 前回の更新時間. */
    private String mFileDescriptorPreviusTime = "";
    
    /** FileDescriptorの開いているファイルのPath. */
    private String mFileDescriptorPath = "";
    
    /** FileDescripor管理用DeviceId. */
    private String mFileDescriptorDeviceId = "";
    
    /** File mode. */
    private Flag mFlag = null;
    
    /** Current File Status. */
    private static int mCurrentFileStatus;
   
    /** 更新可能間隔(1分). */
   private static final int AVAILABLE_REWRITE_TIME = 60000;
    
    /**
     * 更新可能かどうかの判定.
     * @return 更新可能な場合はtrue
     */
    private boolean checkUpdate() {
        return System.currentTimeMillis() - mFileDescriptorCurrentSystemTime > AVAILABLE_REWRITE_TIME;
    }
    /**
     * ファイルを開く.
     * 
     * @param response レスポンス
     * @param deviceId デバイスID
     * @param path パス
     * @param flag ファイルが開かれているかどうかのフラグ 
     */
    public void openFile(final Intent response, final String deviceId, final String path, final Flag flag) {
        
        if (!mFileOpenFlag || checkUpdate()) {
            try {
                mFileDescriptorCurrentSystemTime = System.currentTimeMillis();
                mFileOpenFlag = true;
                File mBaseDir = mFileMgr.getBasePath();
                mFileDescriptorPath = path;
                mFos = new FileOutputStream(new File(mBaseDir + "/" + path), true);
                mFis = new FileInputStream(new File(mBaseDir + "/" + path));
                
                mFlag = flag;
                
                response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_OK);
                response.putExtra(DConnectMessage.EXTRA_VALUE, "Open file:" + Environment.getExternalStorageDirectory()
                        + path);
                sendBroadcast(response);
    
                mFileName = path;
            } catch (FileNotFoundException e) {
                mFileOpenFlag = false;
                // TODO Auto-generated catch block
                e.printStackTrace();
    
                response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_ERROR);
                response.putExtra(DConnectMessage.EXTRA_VALUE, "Can not open file:" + path + ":" + e);
                sendBroadcast(response);
    
                mFileName = "";
            }
        } else {
            response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_ERROR);
            MessageUtils.setError(response, 101, "Opening another file");
            sendBroadcast(response);
        }
    }

    /**
     * ファイルに書き込みする.
     * 
     * @param response レスポンス
     * @param deviceId デバイスID
     * @param path パス
     * @param data データ
     * @param position 書き込みポイント
     */
    public void writeDataToFile(final Intent response, final String deviceId, final String path, final byte[] data,
            final Long position) {

        if (mFileOpenFlag && mFileName.equals(path)) {
            try {
                if (mFlag.equals(Flag.RW)) {
                    // 現在の時刻を取得
                    mFileDescriptorCurrentSystemTime = System.currentTimeMillis();
                    Date date = new Date();
                    SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy'-'MM'-'dd' 'kk':'mm':'ss'+0900'");
                    mFileDescriptorCurrentTime = mDateFormat.format(date);
                    // mFos.write(data, (int)position, data.length);
                    mFos.write(data);
                    response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_OK);
                    response.putExtra(DConnectMessage.EXTRA_VALUE, "Write data:" + path);
                    sendBroadcast(response);
                    sendFileDescriptorOnWatchfileEvent();
                } else {
                    response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_ERROR);
                    response.putExtra(DConnectMessage.EXTRA_VALUE, "Read mode only");
                    sendBroadcast(response);
                }
                
            } catch (Exception e) {
                response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_ERROR);
                response.putExtra(DConnectMessage.EXTRA_VALUE, "Can not write data:" + path + e);
                sendBroadcast(response);
            }
        } else {
            response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_ERROR);
            response.putExtra(DConnectMessage.EXTRA_VALUE, "Can not write data:" + path);
            sendBroadcast(response);
        }
    }

    /**
     * File Descriptor Profile<br>
     * ファイルを読む.
     * 
     * @param response レスポンス
     * @param deviceId デバイスID
     * @param path パス
     * @param position 書き込みポジション
     * @param length 長さ
     */
    public void readFile(final Intent response, final String deviceId, final String path, final long position,
            final long length) {
       
        File mBaseDir = mFileMgr.getBasePath();
        if (mFileOpenFlag && mFileName.equals(path)) {
            try {
                mFileDescriptorCurrentSystemTime = System.currentTimeMillis();
                StringBuffer fileContent = new StringBuffer("");
                byte[] buffer = new byte[1024];
                int count = 0;
                mFis = new FileInputStream(mBaseDir + path);
                while (((mFis.read(buffer, 0, 1)) != -1) && count < position + length) {
                    if (count >= position) {
                        fileContent.append(new String(buffer, 0, 1));
                    }
                    count++;
                }

                response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_OK);
                response.putExtra(FileDescriptorProfile.PARAM_SIZE, length);
                response.putExtra(FileDescriptorProfile.PARAM_FILE_DATA, fileContent.toString()); // TODO
                                                                                                  // setFileData()が無い？
                sendBroadcast(response);

            } catch (Exception e) {

            }
        } else {
            response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_ERROR);
            response.putExtra(DConnectMessage.EXTRA_VALUE, "Can not read data:" + path);
            sendBroadcast(response);
        }
    }

    /**
     * Fileを閉じる.
     * 
     * @param response レスポンス
     * @param deviceId デバイスID
     * @param path パス
     */
    public void closeFile(final Intent response, final String deviceId, final String path) {

        // fileNameが一致した場合のみ閉じる
        if (mFileOpenFlag && mFileName.equals(path)) {
            try {
                mFileDescriptorCurrentSystemTime = 0;
                mFos.close();
                mFileOpenFlag = false;

                response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_OK);
                response.putExtra(DConnectMessage.EXTRA_VALUE, "Close file:" + path);
                sendBroadcast(response);
                mFileName = "";

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                mFileOpenFlag = false;
                response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_ERROR);
                response.putExtra(DConnectMessage.EXTRA_VALUE, "Can not close file:" + path + e);
                sendBroadcast(response);
            }
        } else {
            mFileOpenFlag = false;
            response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_ERROR);
            response.putExtra(DConnectMessage.EXTRA_VALUE, "Can not close file:" + path);
            sendBroadcast(response);
        }
    }
    
    /**
     * OnWatchFileEventの登録.
     * 
     * @param deviceId デバイスID
     */
    public void registerFileDescriptorOnWatchfileEvent(final String deviceId) {
        onWatchfileEventFlag = true;
        mFileDescriptorDeviceId = deviceId;
    }
    
    /**
     * OnWatchFileEventの削除.
     */
    public void unregisterFileDescriptorOnWatchfileEvent() {
        onWatchfileEventFlag = false; 
    }
    
 
    /**
     * 状態変化のイベントを通知.
     */
    public void sendFileDescriptorOnWatchfileEvent() {
        if (onWatchfileEventFlag) {
            List<Event> events = EventManager.INSTANCE.getEventList(
                    mFileDescriptorDeviceId, 
                    HostFileDescriptorProfile.PROFILE_NAME, 
                    null, 
                    HostFileDescriptorProfile.ATTRIBUTE_ON_WATCH_FILE);
          
            for (int i = 0; i < events.size(); i++) {
                Event event = events.get(i);
                Intent intent = EventManager.createEventMessage(event);
                
                HostFileDescriptorProfile.setAttribute(intent, FileDescriptorProfile.ATTRIBUTE_ON_WATCH_FILE);
                Bundle fileDescriptor = new Bundle();
                FileDescriptorProfile.setPath(fileDescriptor, mFileDescriptorPath);
                FileDescriptorProfile.setCurr(fileDescriptor, mFileDescriptorCurrentTime);
                FileDescriptorProfile.setPrev(fileDescriptor, "");
                intent.putExtra(FileDescriptorProfile.PARAM_FILE_DATA, fileDescriptor);
                intent.putExtra(FileDescriptorProfile.PARAM_PROFILE, FileDescriptorProfile.PROFILE_NAME);
                getContext().sendBroadcast(intent);       
            }
        }
    }
    
 
    /**
     * ondataavailableイベントを登録する.
     * 
     * @param request リクエスト
     * @param response レスポンス
     * @param deviceId デバイスID
     * @param sessionKey セッションキー
     * @return 即座に返答する場合はtrue、それ以外はfalse
     */
    public boolean onPutOnDataAvailable(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {

        response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_OK);
        if (deviceId == null || !deviceId.equals(DEVICE_ID)) {
            MessageUtils.setEmptyDeviceIdError(response);
        } else if (sessionKey == null) {
            MessageUtils.setInvalidRequestParameterError(response, "There is no sessionKey.");
        } else {
            if (mOnDataAvailableCallback.containsKey(sessionKey)) {
                MessageUtils.setInvalidRequestParameterError(response, "There is no sessionKey.");
            } else {

                mOnDataAvailableCallback.put(sessionKey, request);
                // 設定が追加されたのでプレビュー用のスレッドを開始する
                if (!mWhileFetching && mOnDataAvailableCallback.size() > 0) {

                }
              
            }
        }

        return true;
    }

    /**
     * ondataavailableイベントを解除する.
     * 
     * @param request リクエスト
     * @param response レスポンス
     * @param deviceId デバイスID
     * @param sessionKey セッションキー
     * @return 即座に返答する場合はtrue、それ以外はfalse
     */
    public boolean onDeleteOnDataAvailable(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {
        response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_OK);

        if (deviceId == null || !deviceId.equals(DEVICE_ID)) {
            MessageUtils.setEmptyDeviceIdError(response);
        } else if (sessionKey == null) {
            MessageUtils.setInvalidRequestParameterError(response, "There is no sessionKey.");
        } else {
            if (mOnDataAvailableCallback.containsKey(sessionKey)) {
                mOnDataAvailableCallback.remove(sessionKey);
                // 設定がなくなったので、プレビュー用のスレッドを止めておく
                if (mOnDataAvailableCallback.size() <= 0) {
                    mWhileFetching = false;
                }
            } else {
                MessageUtils.setInvalidRequestParameterError(response, "There is no sessionKey.");
            }
        }

        return true;
    }

    /**
     * 写真のデータを保存する.
     * 
     * @param data 写真データが格納されているbyte.
     * @return 保存先のURI
     * @throws IOException 写真データの読み込み失敗、もしくは書き込みの失敗時に発生
     */
    public String savePhoto(final byte[] data) throws IOException {
        
        String filename = FILENAME_PREFIX + mSimpleDateFormat.format(new Date()) + FILE_EXTENSION;
        mFileMgr.saveFile(filename, data);

        return filename;
    }

    /**
     * 写真撮影を通知する.
     * 
     * @param mediaid メディアID
     */
    public void notifyTakePhoto(final String mediaid) {

        for (String key : mOnPhotoCallback.keySet()) {
            Intent response = mOnPhotoCallback.get(key);
            ComponentName receiver = (ComponentName) response.getParcelableExtra("receiver");

            Bundle photo = new Bundle();
            photo.putString("mediaid", mediaid);
            photo.putString("mimetype", "image/png");

            Intent intent = new Intent();
            intent.setAction(IntentDConnectMessage.ACTION_EVENT);
            intent.setComponent(receiver);
            intent.putExtra("deviceid", "me");
            intent.putExtra("profile", "mediastream_recording");
            intent.putExtra("callback", "onphoto");
            intent.putExtra("session_key", key);
            intent.putExtra("photo", photo);
            
            
            sendBroadcast(intent);
        }
    }
    
    /**
     * onClickEventの登録.
     * 
     * @param response レスポンス
     * @param deviceId デバイスID
     * @param sessionKey セッションキー
     */
    public void registerOnClick(final Intent response, final String deviceId, final String sessionKey) {
        onClickEventFlag = true;
        response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_OK);
        response.putExtra(DConnectMessage.EXTRA_VALUE, "Register onClick event");
        sendBroadcast(response);
    }

    /**
     * onClickEventの解除.
     * 
     * @param response レスポンス
     * @param deviceId デバイスID
     * @param sessionKey セッションキー
     */
    public void unregisterOnClick(final Intent response, final String deviceId, final String sessionKey) {
        onClickEventFlag = false;
        response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_OK);
        response.putExtra(DConnectMessage.EXTRA_VALUE, "Unregister onClick event");
        sendBroadcast(response);
    }

    /**
     * OnClickEventのフラグを取得.
     * 
     * @return フラグ
     */
    public boolean getOnClickEventFlag() {
        return onClickEventFlag;
    }

    // ----------------------------------------------
    // MediaPlayer Profile
    // ----------------------------------------------
    /** MediaPlayerのインスタンス. */
    private static MediaPlayer mMediaPlayer;
    /** Mediaのステータス. */
    private static int mMediaStatus = 0;
    /** Mediaが未設定. */
    private static final int MEDIA_PLAYER_NODATA = 0;
    /** Mediaがセット. */
    private static final int MEDIA_PLAYER_SET = 1;
    /** Mediaが再生中. */
    private static final int MEDIA_PLAYER_PLAY = 2;
    /** Mediaが一時停止中. */
    private static final int MEDIA_PLAYER_PAUSE = 3;
    /** Mediaが停止. */
    private static final int MEDIA_PLAYER_STOP = 4;
    /** MEDIAタイプ(動画). */
    private static final int MEDIA_TYPE_VIDEO = 1;
    /** MEDIAタイプ(音楽). */
    private static final int MEDIA_TYPE_MUSIC = 2;
    /** MEDIAタイプ(音声). */
    private static final int MEDIA_TYPE_AUDIO = 3;
    /** Media Status. */
    private static int mSetMediaType = 0;
    /** onStatusChange Eventの状態. */
    private static boolean onStatusChangeEventFlag = false;
    /** 現在再生中のファイルパス. */
    private static String myCurrentFilePath = "";
    /** 現在再生中のファイルパス. */
    private static String myCurrentFileMIMEType = "";
    /** 現在再生中のPosition. */
    private int myCurrentMediaPosition = 0;
    /** Current MediaID. */
    private String mCurrentMediaId;

    /**
     * 再生するメディアをセットする(Idから).
     * 
     * @param response レスポンス
     * @param mediaId MediaID
     */
    public void putMediaId(final Intent response, final String mediaId) {
        mCurrentMediaId = mediaId;
 
        // Videoとしてパスを取得
        Uri mUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, Long.valueOf(mediaId));
  
        String filePath = getPathFromUri(mUri);
       
        // nullなら、Audioとしてパスを取得
        if (filePath == null) {
            mUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Long.valueOf(mediaId));
            filePath = getPathFromUri(mUri);
        }

        String mMineType = getMIMEType(filePath);

        // パス指定の場合
        if ("audio/mpeg".equals(mMineType) 
                || "audio/x-wav".equals(mMineType)
                || "application/ogg".equals(mMineType)
                || "audio/x-ms-wma".equals(mMineType)
                || "audio/mp3".equals(mMineType)
                || "audio/ogg".equals(mMineType)
                || "audio/mp4".equals(mMineType)
                        ) {
            mMediaPlayer = new MediaPlayer();

            try {
                mSetMediaType = MEDIA_TYPE_MUSIC;
                myCurrentFilePath = filePath;
                myCurrentFileMIMEType = mMineType;
                mMediaStatus = MEDIA_PLAYER_SET;
                mMediaPlayer.setDataSource(filePath);
                response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_OK);
                response.putExtra(DConnectMessage.EXTRA_VALUE, "regist:" + filePath);
                sendOnStatusChangeEvent("media");
                sendBroadcast(response);
            } catch (IOException e) {
                response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.EXTRA_ERROR_CODE);
                response.putExtra(DConnectMessage.EXTRA_VALUE, "can't not regist:" + filePath);
                sendBroadcast(response);
            }
        } else if ("video/3gpp".equals(mMineType) 
                || "video/mp4".equals(mMineType)
                || "video/m4v".equals(mMineType)
                || "video/3gpp2".equals(mMineType)
                || "video/mpeg".equals(mMineType)) {

            try {

                mSetMediaType = MEDIA_TYPE_VIDEO;
                myCurrentFilePath = filePath;
                myCurrentFileMIMEType = mMineType;

                response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_OK);
                response.putExtra(DConnectMessage.EXTRA_VALUE, "regist:" + filePath);
                sendOnStatusChangeEvent("media");
                sendBroadcast(response);
            } catch (Exception e) {
                response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.EXTRA_ERROR_CODE);
                response.putExtra(DConnectMessage.EXTRA_VALUE, "can't not mount:" + filePath);
                sendBroadcast(response);
            }
          } else {
              response.putExtra(DConnectMessage.EXTRA_RESULT,
              DConnectMessage.EXTRA_ERROR_CODE);
              response.putExtra(DConnectMessage.EXTRA_VALUE, "can't not open:" + filePath);
              sendBroadcast(response);
          }
    }

    /**
     * onStatusChange Eventの登録.
     * 
     * @param response レスポンス
     * @param deviceId デバイスID
     */
    public void registerOnStatusChange(final Intent response, final String deviceId) {
        mDeviceId = deviceId;
        onStatusChangeEventFlag = true;
        response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_OK);
        response.putExtra(DConnectMessage.EXTRA_VALUE, "Register OnStatusChange event");
        sendBroadcast(response);
    }

    /**
     * onStatusChange Eventの解除.
     * 
     * @param response レスポンス
     */
    public void unregisterOnStatusChange(final Intent response) {
        onStatusChangeEventFlag = false;
        response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_OK);
        response.putExtra(DConnectMessage.EXTRA_VALUE, "Unregister OnStatusChange event");
        sendBroadcast(response);
    }

    /**
     * 状態変化のイベントを通知.
     * 
     * @param status ステータス
     */
    public void sendOnStatusChangeEvent(final String status) {

        if (onStatusChangeEventFlag) {
            List<Event> events = EventManager.INSTANCE.getEventList(mDeviceId, 
                    MediaPlayerProfile.PROFILE_NAME, 
                    null,
                    MediaPlayerProfile.ATTRIBUTE_ON_STATUS_CHANGE);
            
            AudioManager manager = (AudioManager) this.getContext().getSystemService(Context.AUDIO_SERVICE);

            double maxVolume = 1;
            double mVolume = 0;

            mVolume = manager.getStreamVolume(AudioManager.STREAM_MUSIC);
            maxVolume = manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

            double mVolumeValue = mVolume / maxVolume;

            for (int i = 0; i < events.size(); i++) {

                Event event = events.get(i);
                Intent intent = EventManager.createEventMessage(event);

                MediaPlayerProfile.setAttribute(intent, MediaPlayerProfile.ATTRIBUTE_ON_STATUS_CHANGE);
                Bundle mediaPlayer = new Bundle();
                MediaPlayerProfile.setStatus(mediaPlayer, status);
                MediaPlayerProfile.setMediaId(mediaPlayer, myCurrentFilePath);
                MediaPlayerProfile.setMIMEType(mediaPlayer, myCurrentFileMIMEType);
                MediaPlayerProfile.setPos(mediaPlayer, myCurrentMediaPosition);
                MediaPlayerProfile.setVolume(mediaPlayer, mVolumeValue);
                MediaPlayerProfile.setMediaPlayer(intent, mediaPlayer);
                getContext().sendBroadcast(intent);
            }
        }
    }

    /**
     * URIからパスを取得.
     * 
     * @param mUri URI
     * @return パス
     */
    private String getPathFromUri(final Uri mUri) {
        try {
            Cursor c = getContentResolver().query(mUri, null, null, null, null);
            c.moveToFirst();
            String filename = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));


            return filename;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Mediaの再再生.
     * 
     * @return SessionID
     */
    public int resumeMedia() {
        if (mSetMediaType == MEDIA_TYPE_MUSIC) {
            try {
                mMediaStatus = MEDIA_PLAYER_PLAY;
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            } catch (Exception e) {
               
            }
            sendOnStatusChangeEvent("play");
            return mMediaPlayer.getAudioSessionId();
        } else if (mSetMediaType == MEDIA_TYPE_VIDEO) {

            mMediaStatus = MEDIA_PLAYER_PLAY;
            Intent mIntent = new Intent(VideoConst.SEND_HOSTDP_TO_VIDEOPLAYER);
            mIntent.putExtra(VideoConst.EXTRA_NAME, VideoConst.EXTRA_VALUE_VIDEO_PLAYER_RESUME);
            this.getContext().sendBroadcast(mIntent);
            sendOnStatusChangeEvent("play");

            return 0;
        }
        return 0;
    }

    /**
     * メディアの再生.
     * 
     * @return セッションID
     */
    public int playMedia() {
        if (mSetMediaType == MEDIA_TYPE_MUSIC) {
            try {
                mMediaStatus = MEDIA_PLAYER_PLAY;
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            } catch (Exception e) {
                
            }
            sendOnStatusChangeEvent("play");
            return mMediaPlayer.getAudioSessionId();
        } else if (mSetMediaType == MEDIA_TYPE_VIDEO) {
            String className = getClassnameOfTopActivity();
         
            if (VideoPlayer.class.getName().equals(className)) {
                mMediaStatus = MEDIA_PLAYER_PLAY;
                Intent mIntent = new Intent(VideoConst.SEND_HOSTDP_TO_VIDEOPLAYER);
                mIntent.putExtra(VideoConst.EXTRA_NAME, VideoConst.EXTRA_VALUE_VIDEO_PLAYER_PLAY);
                this.getContext().sendBroadcast(mIntent);
                sendOnStatusChangeEvent("play");

            } else {
                mMediaStatus = MEDIA_PLAYER_PLAY;
                Intent mIntent = new Intent(VideoConst.SEND_HOSTDP_TO_VIDEOPLAYER);
                mIntent.setClass(getContext(), VideoPlayer.class);
                Uri data = Uri.parse(myCurrentFilePath);
                mIntent.setDataAndType(data, myCurrentFileMIMEType);
                mIntent.putExtra(VideoConst.EXTRA_NAME, VideoConst.EXTRA_VALUE_VIDEO_PLAYER_PLAY);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mIntent);
                sendOnStatusChangeEvent("play");

            }

           
            return 0;
        } else {
            return 0;
        }
    }

    /**
     * メディアの停止.
     * 
     * @return セッションID
     */
    public int pauseMedia() {
        if (mSetMediaType == MEDIA_TYPE_MUSIC) {
            try {
                mMediaStatus = MEDIA_PLAYER_PAUSE;
                mMediaPlayer.stop();

            } catch (Exception e) {
            }
            sendOnStatusChangeEvent("pause");
            return mMediaPlayer.getAudioSessionId();

        } else if (mSetMediaType == MEDIA_TYPE_VIDEO) {
            mMediaStatus = MEDIA_PLAYER_PAUSE;
            Intent mIntent = new Intent(VideoConst.SEND_HOSTDP_TO_VIDEOPLAYER);
            mIntent.putExtra(VideoConst.EXTRA_NAME, VideoConst.EXTRA_VALUE_VIDEO_PLAYER_PAUSE);
            this.getContext().sendBroadcast(mIntent);
            sendOnStatusChangeEvent("pause");
            return 0;
        } else {
            return 0;
        }
    }

    /**
     * ポジションを返す.
     * 
     * @return 現在のポジション
     */
    public int getMediaPos() {
        // ToDo 動画
        return mMediaPlayer.getCurrentPosition();
    }

    /**
     * ポジションを変える.
     * 
     * @param response レスポンス
     * @param pos ポジション　
     */
    public void setMediaPos(final Intent response, final int pos) {
        // ToDo 動画対応
        if (mSetMediaType == MEDIA_TYPE_MUSIC) {
            mMediaPlayer.seekTo(pos);
            myCurrentMediaPosition = pos;
        } else {
            mMediaStatus = MEDIA_PLAYER_PAUSE;
            Intent mIntent = new Intent(VideoConst.SEND_HOSTDP_TO_VIDEOPLAYER);
            mIntent.putExtra(VideoConst.EXTRA_NAME, VideoConst.EXTRA_VALUE_VIDEO_PLAYER_SEEK);
            mIntent.putExtra("pos", pos);
            this.getContext().sendBroadcast(mIntent);
            myCurrentMediaPosition = pos;
        }
        response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_OK);
        sendBroadcast(response);
        sendOnStatusChangeEvent("seek");
    }

    /**
     * メディアの停止.
     */
    public void stopMedia() {
        if (mSetMediaType == MEDIA_TYPE_MUSIC) {
            try {
                mMediaPlayer.stop();
                mMediaStatus = MEDIA_PLAYER_STOP;
                sendOnStatusChangeEvent("stop");
            } catch (Exception e) {
            }
        } else if (mSetMediaType == MEDIA_TYPE_VIDEO) {
            mMediaStatus = MEDIA_PLAYER_PAUSE;
            Intent mIntent = new Intent(VideoConst.SEND_HOSTDP_TO_VIDEOPLAYER);
            mIntent.putExtra(VideoConst.EXTRA_NAME, VideoConst.EXTRA_VALUE_VIDEO_PLAYER_STOP);
            this.getContext().sendBroadcast(mIntent);
            sendOnStatusChangeEvent("stop");
        }
    }

    /**
     * Play Status.
     * 
     * @param response レスポンス
     */
    public void getPlayStatus(final Intent response) {
        String mClassName = getClassnameOfTopActivity();

        // VideoRecorderの場合は、画面から消えている場合m
        if (mSetMediaType == MEDIA_TYPE_VIDEO) {
            response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_OK);

            if (!VideoPlayer.class.getName().equals(mClassName)) {
                mMediaStatus = MEDIA_PLAYER_STOP;
                response.putExtra(MediaPlayerProfile.PARAM_STATUS, "stop");
            } else {
                if (mMediaStatus == MEDIA_PLAYER_STOP) {
                    response.putExtra(MediaPlayerProfile.PARAM_STATUS, "stop");
                } else if (mMediaStatus == MEDIA_PLAYER_PLAY) {
                    response.putExtra(MediaPlayerProfile.PARAM_STATUS, "play");
                } else if (mMediaStatus == MEDIA_PLAYER_PAUSE) {
                    response.putExtra(MediaPlayerProfile.PARAM_STATUS, "pause");
                } else if (mMediaStatus == MEDIA_PLAYER_NODATA) {
                    response.putExtra(MediaPlayerProfile.PARAM_STATUS, "no data");
                } else {
                    response.putExtra(MediaPlayerProfile.PARAM_STATUS, "stop");
                }
            }

            sendBroadcast(response);

        } else {
            response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_OK);
            if (mMediaStatus == MEDIA_PLAYER_STOP) {
                response.putExtra(MediaPlayerProfile.PARAM_STATUS, "stop");
            } else if (mMediaStatus == MEDIA_PLAYER_PLAY) {
                response.putExtra(MediaPlayerProfile.PARAM_STATUS, "play");
            } else if (mMediaStatus == MEDIA_PLAYER_PAUSE) {
                response.putExtra(MediaPlayerProfile.PARAM_STATUS, "pause");
            } else if (mMediaStatus == MEDIA_PLAYER_NODATA) {
                response.putExtra(MediaPlayerProfile.PARAM_STATUS, "no data");
            } else {
                response.putExtra(MediaPlayerProfile.PARAM_STATUS, "stop");
            }

            sendBroadcast(response);
        }
    }

    /**
     * Mediaのプロパティを取得.
     * 
     * @param response レスポンス
     */
    public void getMedia(final Intent response) {
        int sessionId = mMediaPlayer.getAudioSessionId();
        int pos = mMediaPlayer.getCurrentPosition();
        int duration = mMediaPlayer.getDuration();

        response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_OK);
        response.putExtra(MediaPlayerProfile.PARAM_MEDIA_ID, mCurrentMediaId);
        response.putExtra(MediaPlayerProfile.PARAM_DURATION, duration);
        response.putExtra(MediaPlayerProfile.PARAM_DURATION, pos);
        response.putExtra(MediaPlayerProfile.PARAM_STATUS, mMediaStatus);

        sendBroadcast(response);
    }

    // ================================
    // MediaStream_Recording
    // ================================

    /** カウント. */
    private int count = 0;

    /** プレビューの最大値を定義. */
    private static final int MAX_PREVIEW = 10;

    /**
     * DeviceIDを設定.
     * 
     * @param deviceId デバイスID
     */
    public void registerDeviceId(final String deviceId) {
        this.mDeviceId = deviceId;
    }

    /**
     * Cameraからのデータ受信用.
     */
    private IHostMediaStreamRecordingService.Stub mCameraService = new IHostMediaStreamRecordingService.Stub() {

        @Override
        public void sendPreviewData(final byte[] data, final int format, final int width, final int height) {
            
            List<Event> evts = EventManager.INSTANCE.getEventList(mDeviceId,
                    MediaStreamRecordingProfileConstants.PROFILE_NAME, 
                    null,
                    MediaStreamRecordingProfileConstants.ATTRIBUTE_ON_DATA_AVAILABLE);
            
            if (evts.size() > 0) {

                YuvImage yuvimage = new YuvImage(data, format, width, height, null);

                Rect rect = new Rect(0, 0, width, height);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                yuvimage.compressToJpeg(rect, COMPRESS_PERCENTAGE, baos);
                byte[] jdata = baos.toByteArray();

                String mediaId = "preview" + count + ".jpg";
                String uri;
                try {
                    uri = mFileMgr.saveFile(mediaId, jdata);
                    notifyDataAvailable(mediaId, uri);
                    
                    count++;
                    count %= MAX_PREVIEW;
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    };

    /**
     * mDNSで端末検索.
     * 
     */
    public void searchDeviceByBonjour() {

        // cacheがfalseの場合は、検索開始
        // 初回検索,すでにデバイスがある場合, Wifi接続のBroadcastがある場合は入る
        final String type = "_host._tcp.local.";
        new Thread(new Runnable() {
            public void run() {

                services = new ArrayList<Bundle>();

                android.net.wifi.WifiManager wifi = 
                        (android.net.wifi.WifiManager) getSystemService(android.content.Context.WIFI_SERVICE);
                WifiManager.MulticastLock lock = wifi.createMulticastLock("deviceplugin.host");
                lock.setReferenceCounted(true);
                lock.acquire();

                ips = new HashMap<String, String>();
                /*
                try {
                   
                    final JmDNS jmdns = JmDNS.create();
                    jmdns.addServiceListener(type, new ServiceListener() {
                        @Override
                        public void serviceResolved(final ServiceEvent ev) {
                

                            int portNum = ev.getInfo().getPort();
                            String deviceName = ev.getInfo().getName();

                            deviceName = deviceName.replaceAll("_", ".");
                            // String deviceAddress = "" +
                            // ev.getInfo().getInet4Addresses()[0];
                            String deviceId = HostNetworkServiceDiscoveryProfile.DEVICE_ID + ips.size();

                            try {
                                mCallback.findHost(deviceName);
                            } catch (RemoteException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void serviceRemoved(final ServiceEvent ev) {

                        }

                        @Override
                        public void serviceAdded(final ServiceEvent event) {

                            jmdns.requestServiceInfo(event.getType(), event.getName(), 1);

                        }
                    });
                    

                } catch (IOException e) {
                    e.printStackTrace();
                }
                */
            }
        }).start();

    }

    /**
     * mDNSで引っかかるように端末を起動.
     * 
     */
    public void invokeDeviceByBonjour() {

        // cacheがfalseの場合は、検索開始
        // 初回検索,すでにデバイスがある場合, Wifi接続のBroadcastがある場合は入る
        final String type = "_host._tcp.local.";

        new Thread(new Runnable() {
            public void run() {

                services = new ArrayList<Bundle>();

                android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) 
                            getSystemService(android.content.Context.WIFI_SERVICE);
                WifiManager.MulticastLock lock = wifi.createMulticastLock("deviceplugin.host");
                lock.setReferenceCounted(true);
                lock.acquire();
                
                /*
                try {
                    
                    final JmDNS jmdns = JmDNS.create();
                    jmdns.addServiceListener(type, new ServiceListener() {
                        @Override
                        public void serviceResolved(final ServiceEvent ev) {
                        }

                        @Override
                        public void serviceRemoved(final ServiceEvent ev) {

                        }

                        @Override
                        public void serviceAdded(final ServiceEvent event) {

                        }

                    });
                    String ipAddress = getLocalIpAddress();
                    try {
                        mCallback.invokeHost(ipAddress);
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    String ipAddressTmp = ipAddress.replaceAll("\\.", "_");
                    ServiceInfo serviceInfo = ServiceInfo.create(type, ipAddressTmp, 0, "Android Host Plugin");
                    jmdns.registerService(serviceInfo);
                    
                } catch (IOException e) {
                    e.printStackTrace();
                }
                */
            }
        }).start();

    }

    /**
     * 端末のIPを取得.
     * 
     * @return 端末のIPアドレス
     */
    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) {

                        String ipAddr = inetAddress.getHostAddress();
                        return ipAddr;
                    }
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
        // TODO Auto-generated method stub

    }

    @Override
    public IBinder onBind(final Intent intent) {

        if ("camera".equals(intent.getAction())) {

            return mCameraService;
        } else {

            return mStub;
        }
    }

    /**
     * Host Device Pluginのサービス.
     */
    private IHostDeviceService.Stub mStub = new IHostDeviceService.Stub() {

        @Override
        public void registerCallback(final IHostDeviceCallback callback) throws RemoteException {
            // TODO Auto-generated method stub
            mCallback = callback;
        }

        @Override
        public void unregisterCallback(final IHostDeviceCallback callback) throws RemoteException {
            // TODO Auto-generated method stub
            mCallback = callback;
        }

        @Override
        public void searchHost() throws RemoteException {
            // TODO Auto-generated method stub
            searchDeviceByBonjour();
        }

        @Override
        public int getHostStatus() throws RemoteException {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public void invokeHost() throws RemoteException {
            // TODO Auto-generated method stub
            invokeDeviceByBonjour();
        }

    };

    /**
     * onClickの登録.
     * 
     * @param response レスポンス
     * @param deviceId デバイスID
     * @param sessionKey セッションキー
     */
    public void registerOnConnect(final Intent response, final String deviceId, final String sessionKey) {
        onConnectEventFlag = true;
        response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_OK);
        response.putExtra(DConnectMessage.EXTRA_VALUE, "Register onClick event");
        sendBroadcast(response);
    }

    /**
     * onClickの削除.
     * 
     * @param response レスポンス
     * @param deviceId デバイスID
     * @param sessionKey セッションキー
     */
    public void unregisterOnConnect(final Intent response, final String deviceId, final String sessionKey) {
        onConnectEventFlag = false;
        response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_OK);
        response.putExtra(DConnectMessage.EXTRA_VALUE, "Unregister onClick event");
        sendBroadcast(response);
    }


    
    /**
     * 3GPのファイルがVideoかAudioかの判定.
     * 
     * @param mFile 判定したいURI
     * @return Videoならtrue, audioならfalse
     */
    public static boolean checkVideo(final File mFile) {
        int height = 0;
        try {
            mMediaPlayer = new MediaPlayer();
            FileInputStream mFis = null;
            FileDescriptor mFd = null;

            mFis = new FileInputStream(mFile);
            mFd = mFis.getFD();

            mMediaPlayer.setDataSource(mFd);
            mMediaPlayer.prepare();
            height = mMediaPlayer.getVideoHeight();
            mMediaPlayer.release();
        } catch (Exception e) {
        }

        return height > 0;
    }

    /**
     * ファイルからMIME Typeを取得.
     * 
     * @param path パス
     * @return MineType
     */
    public String getMIMEType(final String path) {
        
        // 空文字, 日本語対策, ファイル形式のStringを取得
        String mFileName = new File(path).getName();
        int dotPos = mFileName.lastIndexOf(".");
        String mFormat = mFileName.substring(dotPos, mFileName.length());
        
        // 拡張子を取得
        String mExt = MimeTypeMap.getFileExtensionFromUrl(mFormat);
        // 小文字に変換
        mExt = mExt.toLowerCase();
        // MIME Typeを返す
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(mExt);
    }

    /**
     * 通知.
     * 
     * @param mediaId メディアID
     * @param uri プレビューへのURI
     */
    public void notifyDataAvailable(final String mediaId, final String uri) {
        List<Event> evts = EventManager.INSTANCE.getEventList(mDeviceId,
                MediaStreamRecordingProfileConstants.PROFILE_NAME, null,
                MediaStreamRecordingProfileConstants.ATTRIBUTE_ON_DATA_AVAILABLE);
        for (Event evt : evts) {
            Bundle media = new Bundle();
            media.putString(MediaStreamRecordingProfile.PARAM_PATH, mediaId);
            media.putString(MediaStreamRecordingProfile.PARAM_URI, uri);
            media.putString(MediaStreamRecordingProfile.PARAM_MIME_TYPE, "image/jpg");

            Intent intent = new Intent(IntentDConnectMessage.ACTION_EVENT);
            intent.setComponent(ComponentName.unflattenFromString(evt.getReceiverName()));
            intent.putExtra(DConnectMessage.EXTRA_DEVICE_ID, DEVICE_ID);
            intent.putExtra(DConnectMessage.EXTRA_PROFILE, MediaStreamRecordingProfile.PROFILE_NAME);
            intent.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, MediaStreamRecordingProfile.ATTRIBUTE_ON_DATA_AVAILABLE);
            intent.putExtra(DConnectMessage.EXTRA_SESSION_KEY, evt.getSessionKey());
            intent.putExtra(MediaStreamRecordingProfile.PARAM_MEDIA, media);

            sendEvent(intent, evt.getAccessToken());
        }
    }

    /**
     * 画面の一番上にでているActivityのクラス名を取得.
     * 
     * @return クラス名
     */
    private String getClassnameOfTopActivity() {

        ActivityManager mActivityManager = (ActivityManager) getContext().getSystemService(Service.ACTIVITY_SERVICE);
        String mClassName = mActivityManager.getRunningTasks(1).get(0).topActivity.getClassName();

        return mClassName;
    }
}
