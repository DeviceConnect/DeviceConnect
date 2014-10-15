/*
 DeviceInfo.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.sphero.data;

import orbotix.robot.base.CollisionDetectedAsyncData;
import orbotix.robot.base.ConfigureLocatorCommand;
import orbotix.robot.sensor.DeviceSensorsData;
import orbotix.sphero.CollisionListener;
import orbotix.sphero.SensorControl;
import orbotix.sphero.SensorFlag;
import orbotix.sphero.SensorListener;
import orbotix.sphero.Sphero;
import android.util.Log;

import org.deviceconnect.android.deviceplugin.sphero.BuildConfig;

/**
 * デバイス情報.
 * @author NTT DOCOMO, INC.
 */
public class DeviceInfo implements SensorListener, CollisionListener {
    
    /** 
     * センサーの周期. {@value} Hz
     */
    private static final int SENSOR_RATE = 2;

    /**
     * デバイス.
     */
    private Sphero mDevice;

    /**
     * バックLEDの明るさ.
     */
    private float mBackBrightness;

    /**
     * 現在の色.
     */
    private int mColor;

    /**
     * センサーリスナー.
     */
    private DeviceSensorListener mSensorListener;
    
    /** 
     * 衝突リスナー.
     */
    private DeviceCollisionListener mCollisionListener;
    
    /** 
     * センサーが稼働しているか.
     */
    private boolean mIsSensorStarted;
    
    /** 
     * 衝突検知が稼働しているか.
     */
    private boolean mIsCollisionStarted;
    
    /** 
     * 前のセンサーのタイムスタンプ.
     */
    private long mPreSensorTimestamp;

    /**
     * デバイスを取得する.
     * 
     * @return デバイス
     */
    public Sphero getDevice() {
        return mDevice;
    }

    /**
     * デバイスを設定する.
     * 
     * @param device デバイス
     */
    public void setDevice(final Sphero device) {
        this.mDevice = device;
    }

    /**
     * バックライトの明るさを取得する.
     * 
     * @return バックライトの明るさ
     */
    public float getBackBrightness() {
        return mBackBrightness;
    }

    /**
     * バックライトの明るさを設定する.
     * 
     * @param backBrightness バックライトの明るさ
     */
    public void setBackBrightness(final float backBrightness) {
        this.mDevice.setBackLEDBrightness(backBrightness);
        this.mBackBrightness = backBrightness;
    }

    /**
     * 色を設定する.
     * 
     * @param r 赤
     * @param g 緑
     * @param b 青
     */
    public void setColor(final int r, final int g, final int b) {
        mDevice.setColor(r, g, b);
        // SpheroのgetColorでは正しく現在の色がとれない(消灯しているのに0xff000000以外が返ってくる)ので
        // 自前で色を管理する。
        mColor = (0xff000000 | (r << 16) | (g << 8) | b);
    }

    /**
     * 色を取得する.
     * 
     * @return 色
     */
    public int getColor() {
        return mColor;
    }
    
    /**
     * センサーが稼働しているかチェックする.
     * 
     * @return センサーが稼働している場合true、その他はfalseを返す。
     */
    public boolean isSensorStarted() {
        return mIsSensorStarted;
    }
    
    /**
     * 衝突の監視が稼働しているかチェックする.
     * 
     * @return 稼働している場合true、その他はfalseを返す。
     */
    public boolean isCollisionStarted() {
        return mIsCollisionStarted;
    }

    /**
     * センサーを開始する.
     * 
     * @param listener リスナー
     */
    public void startSensor(final DeviceSensorListener listener) {
        
        if (mIsSensorStarted) {
            return;
        }
        
        mIsSensorStarted = true;
        mSensorListener = listener;
        SensorControl sc = mDevice.getSensorControl();
        sc.setRate(SENSOR_RATE);
        sc.enableStreaming(true);
        sc.addSensorListener(this, SensorFlag.ACCELEROMETER_NORMALIZED, 
                SensorFlag.GYRO_NORMALIZED, SensorFlag.ATTITUDE,
                SensorFlag.QUATERNION, SensorFlag.LOCATOR);
        
        ConfigureLocatorCommand.sendCommand(mDevice, 
                ConfigureLocatorCommand.ROTATE_WITH_CALIBRATE_FLAG_OFF, 0, 0, 0);
        mPreSensorTimestamp = System.currentTimeMillis();
    }

    /**
     * センサーを停止する.
     */
    public void stopSensor() {
        
        if (!mIsSensorStarted) {
            return;
        }
        mIsSensorStarted = false;
        mDevice.getSensorControl().removeSensorListener(this);
        mDevice.getSensorControl().stopStreaming();
        mSensorListener = null;
    }
    
    /**
     * 衝突の監視を開始する.
     * 
     * @param listener リスナー
     */
    public void startCollistion(final DeviceCollisionListener listener) {
        
        if (mIsCollisionStarted) {
            return;
        }
        
        if (BuildConfig.DEBUG) {
            Log.d("", "start collision");
        }
        
        mIsCollisionStarted = true;
        mCollisionListener = listener;
        mDevice.getCollisionControl().addCollisionListener(this);
        mDevice.getCollisionControl().startDetection(90, 90, 130, 130, 100);
    }
    
    /**
     * 衝突イベントの検知を終了する.
     */
    public void stopCollision() {
        if (!mIsCollisionStarted) {
            return;
        }
        
        mIsCollisionStarted = false;
        mCollisionListener = null;
        mDevice.getCollisionControl().removeCollisionListener(this);
        mDevice.getCollisionControl().stopDetection();
        
        if (BuildConfig.DEBUG) {
            Log.d("", "stop collision");
        }
    }

    @Override
    public void sensorUpdated(final DeviceSensorsData data) {
        
        long timestamp = data.getTimeStamp();
        mSensorListener.sensorUpdated(this, data, timestamp - mPreSensorTimestamp);
        mPreSensorTimestamp = timestamp;
    }
    
    @Override
    public void collisionDetected(final CollisionDetectedAsyncData data) {
        if (BuildConfig.DEBUG) {
            Log.d("", "collisionDetected");
        }
        mCollisionListener.collisionDetected(this, data);
    }

    /**
     * デバイスのセンサーのイベント通知を受けるリスナー.
     */
    public interface DeviceSensorListener {

        /**
         * センサーがアップデートされたことを通知する.
         * 
         * @param info センサーを持つデバイス
         * @param data センサーデータ
         * @param interval 前のアップデートからの間隔
         */
        void sensorUpdated(final DeviceInfo info, final DeviceSensorsData data, final long interval);
    }
    
    /**
     * デバイスの衝突イベントの通知を受けるリスナー.
     */
    public interface DeviceCollisionListener {
        
        /**
         * デバイスが衝突したことを通知する.
         * 
         * @param info デバイス
         * @param data 衝突データ
         */
        void collisionDetected(final DeviceInfo info, final CollisionDetectedAsyncData data);
    }
}