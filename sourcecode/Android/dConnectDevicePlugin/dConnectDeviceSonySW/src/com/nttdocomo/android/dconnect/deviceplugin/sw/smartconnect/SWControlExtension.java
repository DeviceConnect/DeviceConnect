/*
Copyright (c) 2011 Sony Ericsson Mobile Communications AB
Copyright (C) 2012 Sony Mobile Communications AB

All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

 * Neither the name of the Sony Ericsson Mobile Communications AB nor the names
  of its contributors may be used to endorse or promote products derived from
  this software without specific prior written permission.

 * Neither the name of the Sony Mobile Communications AB nor the names
  of its contributors may be used to endorse or promote products derived from
  this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.nttdocomo.android.dconnect.deviceplugin.sw.smartconnect;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.nttdocomo.android.dconnect.deviceplugin.sw.R;
import com.nttdocomo.android.dconnect.deviceplugin.sw.SWConstants;
import com.nttdocomo.android.dconnect.deviceplugin.util.DcLoggerSW;
import com.nttdocomo.android.dconnect.event.Event;
import com.nttdocomo.android.dconnect.event.EventManager;
import com.nttdocomo.android.dconnect.localoauth.CheckAccessTokenResult;
import com.nttdocomo.android.dconnect.localoauth.LocalOAuth2Main;
import com.nttdocomo.android.dconnect.profile.DeviceOrientationProfile;
import com.nttdocomo.dconnect.message.DConnectMessage;
import com.nttdocomo.dconnect.profile.AuthorizationProfileConstants;
import com.nttdocomo.dconnect.profile.NetworkServiceDiscoveryProfileConstants;
import com.nttdocomo.dconnect.profile.SystemProfileConstants;
import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.aef.registration.Registration.SensorTypeValue;
import com.sonyericsson.extras.liveware.aef.sensor.Sensor;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.control.ControlTouchEvent;
import com.sonyericsson.extras.liveware.extension.util.registration.DeviceInfoHelper;
import com.sonyericsson.extras.liveware.extension.util.sensor.AccessorySensor;
import com.sonyericsson.extras.liveware.extension.util.sensor.AccessorySensorEvent;
import com.sonyericsson.extras.liveware.extension.util.sensor.AccessorySensorEventListener;
import com.sonyericsson.extras.liveware.extension.util.sensor.AccessorySensorException;
import com.sonyericsson.extras.liveware.extension.util.sensor.AccessorySensorManager;

/**
ControlExtension
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

class SWControlExtension extends ControlExtension {
    /**
     * デバイスセンサー.
     */
    private AccessorySensor mSensor;

    /**
     * 画面サイズ(横).
     */
    private int mWidth = SWConstants.DEFAULT_DISPLAY_WIDTH;
    /**
     * 画面サイズ(縦).
     */
    private int mHeight = SWConstants.DEFAULT_DISPLAY_HEIGHT;
    /**
     * 加速度取得インターバル.
     */
    private int mInterval = SWConstants.DEFAULT_SENSOR_INTERVAL;

    /** Logger. */
    private DcLoggerSW mLogger = new DcLoggerSW();
    /**
    * 加速度センサーイベントリスナー.
    */
    private final AccessorySensorEventListener mListener = new AccessorySensorEventListener() {

        long start = 0;

        @Override
        public void onSensorEvent(final AccessorySensorEvent sensorEvent) {

            if (start == 0 || System.currentTimeMillis() - start > mInterval) {

                float[] values = sensorEvent.getSensorValues();
                Bundle orientation = new Bundle();
                Bundle acceleration = new Bundle();
                orientation.putBundle(DeviceOrientationProfile.PARAM_ACCELERATION_INCLUDING_GRAVITY, acceleration);
                orientation.putInt(DeviceOrientationProfile.PARAM_INTERVAL, mInterval);
                acceleration.putDouble(DeviceOrientationProfile.PARAM_X, values[0]);
                acceleration.putDouble(DeviceOrientationProfile.PARAM_Y, values[1]);
                acceleration.putDouble(DeviceOrientationProfile.PARAM_Z, values[2]);

                DeviceOrientationProfile orientationProf = new DeviceOrientationProfile();

                String deviceId = makeDeviceId();

                try {
                    List<Event> events = EventManager.INSTANCE.getEventList(deviceId, orientationProf.getProfileName(),
                            null, DeviceOrientationProfile.ATTRIBUTE_ON_DEVICE_ORIENTATION);

                    for (Event event : events) {
                        mLogger.fine(this, "onSensorEvent:events", events);
                        Intent message = EventManager.createEventMessage(event);
                        message.putExtra(DeviceOrientationProfile.PARAM_ORIENTATION, orientation);
                        sendEvent(message, event.getAccessToken());
                    }
                } catch (Exception e) {
                    mLogger.fine(this, "onSensorEvent:", "Event is null");
                }

                start = System.currentTimeMillis();
            }
        }
    };

    /**
     * ペアリングしているSonyWatchからdeviceIdの作成.
     * 
     * @return deviceId
     */
    public String makeDeviceId() {
        String deviceId = "";

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> bondedDevices = adapter.getBondedDevices();
        if (bondedDevices.size() > 0) {

            try {
                for (BluetoothDevice device : bondedDevices) {
                    String deviceName = device.getName();
                    if (deviceName.startsWith("SmartWatch")) {
                        String address = device.getAddress();
                        deviceId = address.replace(":", "").toLowerCase(Locale.ENGLISH);
                    }
                }
            } catch (Exception e) {
                return deviceId;
            }

            return deviceId;
        }
        return deviceId;

    }

    /**
     * イベントの送信処理.
     * 
     * @param event イベント
     * @param accessToken アクセストークン
     * @return event
     */
    public final boolean sendEvent(final Intent event, final String accessToken) {

        if (event == null) {
            throw new IllegalArgumentException("Event is null.");
        }

        CheckAccessTokenResult result = LocalOAuth2Main.checkAccessToken(accessToken,
                event.getStringExtra(DConnectMessage.EXTRA_PROFILE), IGNORE_PROFILES);
        if (!checkAccessTokenResult(result)) {
            return false;
        }

        mContext.sendBroadcast(event);
        return true;
    }

    /**
     * Local OAuth使用フラグを取得する.
     * 
     * @return 使用する場合にはtrue、それ以外はfalse
     */
    protected boolean isUseLocalOAuth() {
        return !LocalOAuth2Main.isAutoTestMode();
    }

    /**
     * アクセストークンのチェックを行う.
     * 
     * @param result アクセスのチェック結果
     * @return アクセストークンが正常の場合はtrue,それ以外の場合はfalse
     */
    private boolean checkAccessTokenResult(final CheckAccessTokenResult result) {
        if (!isUseLocalOAuth()) {
            return true;
        }
        return result.checkResult();
    }

    /**
     * LocalOAuthで無視するプロファイル群.
     */
    private static final String[] IGNORE_PROFILES = {AuthorizationProfileConstants.PROFILE_NAME,
            SystemProfileConstants.PROFILE_NAME, NetworkServiceDiscoveryProfileConstants.PROFILE_NAME };

    /**
     * Creates a control extension.
     * 
     * @param hostAppPackageName Package name of host application.
     * @param context The context.
     */
    SWControlExtension(final Context context, final String hostAppPackageName) {
        super(context, hostAppPackageName);
        AccessorySensorManager manager = new AccessorySensorManager(context, hostAppPackageName);

        // Add accelerometer, if supported by the host application.
        if (DeviceInfoHelper.isSensorSupported(context, hostAppPackageName, SensorTypeValue.ACCELEROMETER)) {
            mSensor = manager.getSensor(SensorTypeValue.ACCELEROMETER);

            showDisplay();
        }

        // Determine host application screen size.
        determineSize(context, hostAppPackageName);

    }

    /**
     * SmartWatchDisplayへの表示.
     */
    private void showDisplay() {

        // Create bitmap to draw in.
        Bitmap bitmap = Bitmap.createBitmap(mWidth, mHeight, SWConstants.DEFAULT_BITMAP_CONFIG);

        // Set default density to avoid scaling.
        bitmap.setDensity(DisplayMetrics.DENSITY_DEFAULT);

        LinearLayout root = new LinearLayout(mContext);
        root.setLayoutParams(new ViewGroup.LayoutParams(mWidth, mHeight));
        root.setGravity(Gravity.CENTER);

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout sensorLayout = (LinearLayout) inflater.inflate(R.layout.generic_sensor_values, root, true);

        root.measure(mWidth, mHeight);
        root.layout(0, 0, mWidth, mHeight);

        Canvas canvas = new Canvas(bitmap);
        sensorLayout.draw(canvas);

        showBitmap(bitmap);
    }

    @Override
    public void onResume() {
        // Note: Setting the screen to be always on will drain the accessory
        // battery. It is done here solely for demonstration purposes.
        setScreenState(Control.Intents.SCREEN_STATE_ON);

        // Start listening for sensor updates.
        register();

    }

    @Override
    public void onPause() {
        // Stop sensor.
        unregister();
    }

    @Override
    public void onDestroy() {
        // Stop sensor.
        unregisterAndDestroy();
    }

    /**
     * Checks if the control extension supports the given width.
     * 
     * @param context The context.
     * @param width The width.
     * @return True if the control extension supports the given width.
     */
    public static boolean isWidthSupported(final Context context, final int width) {
        return width == context.getResources().getDimensionPixelSize(R.dimen.smart_watch_2_control_width)
                || width == context.getResources().getDimensionPixelSize(R.dimen.smart_watch_control_width);
    }

    /**
     * Checks if the control extension supports the given height.
     * 
     * @param context The context.
     * @param height The height.
     * @return True if the control extension supports the given height.
     */
    public static boolean isHeightSupported(final Context context, final int height) {
        return height == context.getResources().getDimensionPixelSize(R.dimen.smart_watch_2_control_height)
                || height == context.getResources().getDimensionPixelSize(R.dimen.smart_watch_control_height);
    }

    @Override
    public void onTouch(final ControlTouchEvent event) {
        super.onTouch(event);
    }

    /**
     * Determines the width and height in pixels of a given host application.
     * 
     * @param context The context.
     * @param hostAppPackageName The host application.
     */
    private void determineSize(final Context context, final String hostAppPackageName) {
        boolean smartWatch2Supported = DeviceInfoHelper.isSmartWatch2ApiAndScreenDetected(context, hostAppPackageName);
        if (smartWatch2Supported) {
            mWidth = context.getResources().getDimensionPixelSize(R.dimen.smart_watch_2_control_width);
            mHeight = context.getResources().getDimensionPixelSize(R.dimen.smart_watch_2_control_height);
        } else {
            mWidth = context.getResources().getDimensionPixelSize(R.dimen.smart_watch_control_width);
            mHeight = context.getResources().getDimensionPixelSize(R.dimen.smart_watch_control_height);
        }
    }

    /**
     * Returns the sensor currently being used.
     * 
     * @return The sensor.
     */
    private AccessorySensor getCurrentSensor() {
        return mSensor;
    }

    /**
     * Checks if the sensor currently being used supports interrupt mode and
     * registers an interrupt listener if it does. If not, a fixed rate listener
     * will be registered instead.
     */
    private void register() {
        AccessorySensor sensor = getCurrentSensor();
        if (sensor != null) {
            try {
                if (sensor.isInterruptModeSupported()) {
                    sensor.registerInterruptListener(mListener);
                } else {
                    sensor.registerFixedRateListener(mListener, Sensor.SensorRates.SENSOR_DELAY_UI);
                }
            } catch (AccessorySensorException e) {
                Log.e(SWConstants.LOG_TAG, "Failed to register listener", e);
            }
        }
    }

    /**
     * Unregisters any sensor event listeners connected to the sensor currently
     * being used.
     */
    private void unregister() {
        AccessorySensor sensor = getCurrentSensor();
        if (sensor != null) {
            sensor.unregisterListener();
        }
    }

    /**
     * Unregisters any sensor event listeners and unsets the sensor currently
     * being used.
     */
    private void unregisterAndDestroy() {
        unregister();
        mSensor = null;
    }

}
