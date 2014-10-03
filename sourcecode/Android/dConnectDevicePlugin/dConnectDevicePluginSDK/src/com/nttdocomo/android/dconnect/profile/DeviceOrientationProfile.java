/*
 DeviceOrientationProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.profile;

import android.content.Intent;
import android.os.Bundle;

import com.nttdocomo.android.dconnect.message.MessageUtils;
import com.nttdocomo.dconnect.profile.DeviceOrientationProfileConstants;

/**
 * Device Orientation プロファイル.
 * 
 * <p>
 * スマートデバイスのセンサー操作機能を提供するAPI.<br/>
 * センサー操作機能を提供するデバイスプラグインは当クラスを継承し、対応APIを実装すること。 <br/>
 * </p>
 * 
 * <h1>各API提供メソッド</h1>
 * <p>
 * Device Orientation Profile の各APIへのリクエストに対し、以下のコールバックメソッド群が自動的に呼び出される。<br/>
 * サブクラスは以下のメソッド群からデバイスプラグインが提供するAPI用のメソッドをオーバーライドし、機能を実装すること。<br/>
 * オーバーライドされていない機能は自動的に非対応APIとしてレスポンスを返す。
 * </p>
 * <ul>
 * <li>Device Orientation Event API [Register] :
 * {@link DeviceOrientationProfile#onPutOnDeviceOrientation(Intent, Intent, String, String)}</li>
 * <li>Device Orientation Event API [Unregister] :
 * {@link DeviceOrientationProfile#onDeleteOnDeviceOrientation(Intent, Intent, String, String)}</li>
 * </ul>
 * 
 * @author NTT DOCOMO, INC.
 */
public class DeviceOrientationProfile extends DConnectProfile implements DeviceOrientationProfileConstants {

    @Override
    public final String getProfileName() {
        return PROFILE_NAME;
    }

    @Override
    protected boolean onPutRequest(final Intent request, final Intent response) {
        boolean result = true;
        String attribute = getAttribute(request);

        if (ATTRIBUTE_ON_DEVICE_ORIENTATION.equals(attribute)) {
            result = onPutOnDeviceOrientation(request, response, getDeviceID(request), getSessionKey(request));
        } else {
            MessageUtils.setUnknownAttributeError(response);
        }

        return result;
    }

    @Override
    protected boolean onDeleteRequest(final Intent request, final Intent response) {
        boolean result = true;
        String attribute = getAttribute(request);

        if (ATTRIBUTE_ON_DEVICE_ORIENTATION.equals(attribute)) {
            result = onDeleteOnDeviceOrientation(request, response, getDeviceID(request), getSessionKey(request));
        } else {
            MessageUtils.setUnknownAttributeError(response);
        }

        return result;
    }

    // ------------------------------------
    // PUT
    // ------------------------------------

    /**
     * ondeviceorientationコールバック登録リクエストハンドラー.<br/>
     * ondeviceorientationコールバックを登録し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param sessionKey セッションキー
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onPutOnDeviceOrientation(final Intent request, final Intent response, 
            final String deviceId, final String sessionKey) {
        setUnsupportedError(response);
        return true;
    }

    // ------------------------------------
    // DELETE
    // ------------------------------------

    /**
     * ondeviceorientationコールバック解除リクエストハンドラー.<br/>
     * ondeviceorientationコールバックを解除し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param sessionKey セッションキー
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onDeleteOnDeviceOrientation(final Intent request, final Intent response,
            final String deviceId, final String sessionKey) {
        setUnsupportedError(response);
        return true;
    }

    // ------------------------------------
    // メッセージセッターメソッド群
    // ------------------------------------

    /**
     * センサー情報にインターバルを設定する.
     * 
     * @param orientation センサー情報
     * @param interval インターバル(ミリ秒)
     */
    public static void setInterval(final Bundle orientation, final long interval) {
        orientation.putLong(PARAM_INTERVAL, interval);
    }

    /**
     * メッセージにセンサー情報を設定する.
     * 
     * @param message メッセージパラメータ
     * @param orientation センサー情報
     */
    public static void setOrientation(final Intent message, final Bundle orientation) {
        message.putExtra(PARAM_ORIENTATION, orientation);
    }

    /**
     * センサー情報に加速度センサー情報を設定します.
     * 
     * @param orientation センサー情報
     * @param acceleration 加速度センサー情報
     */
    public static void setAcceleration(final Bundle orientation, final Bundle acceleration) {
        orientation.putBundle(PARAM_ACCELERATION, acceleration);
    }

    /**
     * センサー情報に重力加速度センサー情報を設定します.
     * 
     * @param orientation センサー情報
     * @param accelerationIncludingGravity 重力加速度センサー情報
     */
    public static void setAccelerationIncludingGravity(final Bundle orientation,
            final Bundle accelerationIncludingGravity) {
        orientation.putBundle(PARAM_ACCELERATION_INCLUDING_GRAVITY, accelerationIncludingGravity);
    }

    /**
     * センサー情報に回転加速度センサー情報を設定します.
     * 
     * @param orientation センサー情報
     * @param rotationRate 回転加速度センサー情報
     */
    public static void setRotationRate(final Bundle orientation, final Bundle rotationRate) {
        orientation.putBundle(PARAM_ROTATION_RATE, rotationRate);
    }

    /**
     * センサー情報にX方向への加速度を設定する.
     * 
     * @param sensor センサー情報
     * @param x X方向への加速度
     */
    public static void setX(final Bundle sensor, final double x) {
        sensor.putDouble(PARAM_X, x);
    }

    /**
     * センサー情報にY方向への加速度を設定する.
     * 
     * @param sensor センサー情報
     * @param y Y方向への加速度
     */
    public static void setY(final Bundle sensor, final double y) {
        sensor.putDouble(PARAM_Y, y);
    }

    /**
     * センサー情報にZ方向への加速度を設定する.
     * 
     * @param sensor センサー情報
     * @param z Z方向への加速度
     */
    public static void setZ(final Bundle sensor, final double z) {
        sensor.putDouble(PARAM_Z, z);
    }

    /**
     * 回転加速度センサー情報にz軸回転の角度を設定する.
     * 
     * @param rotationRate 回転加速度センサー情報
     * @param alpha z軸回転の角度
     */
    public static void setAlpha(final Bundle rotationRate, final double alpha) {
        rotationRate.putDouble(PARAM_ALPHA, alpha);
    }

    /**
     * 回転加速度センサー情報にx軸回転の角度を設定する.
     * 
     * @param rotationRate 回転加速度センサー情報
     * @param beta x軸回転の角度
     */
    public static void setBeta(final Bundle rotationRate, final double beta) {
        rotationRate.putDouble(PARAM_BETA, beta);
    }

    /**
     * 回転加速度センサー情報にy軸回転の角度を設定する.
     * 
     * @param rotationRate 回転加速度センサー情報
     * @param gamma y軸回転の角度
     */
    public static void setGamma(final Bundle rotationRate, final double gamma) {
        rotationRate.putDouble(PARAM_GAMMA, gamma);
    }
}
