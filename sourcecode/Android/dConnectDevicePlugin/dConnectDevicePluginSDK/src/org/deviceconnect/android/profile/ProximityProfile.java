/*
 ProximityProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.profile;

import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.profile.ProximityProfileConstants;

import android.content.Intent;
import android.os.Bundle;

/**
 * Proximity プロファイル.
 * 
 * <p>
 * スマートデバイスの近接センサーの検知通知を提供するAPI.<br/>
 * スマートデバイスの近接センサーの検知通知を提供するデバイスプラグインは当クラスを継承し、対応APIを実装すること。 <br/>
 * </p>
 * 
 * <h1>各API提供メソッド</h1>
 * <p>
 * Proximity Profile の各APIへのリクエストに対し、以下のコールバックメソッド群が自動的に呼び出される。<br/>
 * サブクラスは以下のメソッド群からデバイスプラグインが提供するAPI用のメソッドをオーバーライドし、機能を実装すること。<br/>
 * オーバーライドされていない機能は自動的に非対応APIとしてレスポンスを返す。
 * </p>
 * <ul>
 * <li>Proximity Device Event API [Register] :
 * {@link ProximityProfile#onPutOnDeviceProximity(Intent, Intent, String, String)}</li>
 * <li>Proximity Device Event API [Unregister] :
 * {@link ProximityProfile#onDeleteOnDeviceProximity(Intent, Intent, String, String)}</li>
 * <li>Proximity User Event API [Register] :
 * {@link ProximityProfile#onPutOnUserProximity(Intent, Intent, String, String)}</li>
 * <li>Proximity User Event API [Unregister] :
 * {@link ProximityProfile#onDeleteOnUserProximity(Intent, Intent, String, String)}</li>
 * </ul>
 * @author NTT DOCOMO, INC.
 */
public class ProximityProfile extends DConnectProfile implements ProximityProfileConstants {

    @Override
    public final String getProfileName() {
        return PROFILE_NAME;
    }

    @Override
    protected boolean onPutRequest(final Intent request, final Intent response) {
        String attribute = getAttribute(request);
        boolean result = true;

        if (attribute == null) {
            MessageUtils.setUnknownAttributeError(response);
        } else {
            
            String deviceId = getDeviceID(request);
            String sessionKey = getSessionKey(request);
            
            if (attribute.equals(ATTRIBUTE_ON_DEVICE_PROXIMITY)) {
                result = onPutOnDeviceProximity(request, response, deviceId, sessionKey);
            } else if (attribute.equals(ATTRIBUTE_ON_USER_PROXIMITY)) {
                result = onPutOnUserProximity(request, response, deviceId, sessionKey);
            } else {
                MessageUtils.setUnknownAttributeError(response);
            }
        }
        return result;
    }

    @Override
    protected boolean onDeleteRequest(final Intent request, final Intent response) {
        String attribute = getAttribute(request);
        boolean result = true;

        if (attribute == null) {
            MessageUtils.setUnknownAttributeError(response);
        } else {
            
            String deviceId = getDeviceID(request);
            String sessionKey = getSessionKey(request);
            
            if (attribute.equals(ATTRIBUTE_ON_DEVICE_PROXIMITY)) {
                result = onDeleteOnDeviceProximity(request, response, deviceId, sessionKey);
            } else if (attribute.equals(ATTRIBUTE_ON_USER_PROXIMITY)) {
                result = onDeleteOnUserProximity(request, response, deviceId, sessionKey);
            } else {
                MessageUtils.setUnknownAttributeError(response);
            }
        }

        return result;
    }

    // ------------------------------------
    // PUT
    // ------------------------------------

    /**
     * ondeviceproximityコールバック登録リクエストハンドラー.<br/>
     * ondeviceproximityコールバックを登録し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param sessionKey セッションキー
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onPutOnDeviceProximity(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * onuserproximityコールバック登録リクエストハンドラー.<br/>
     * onuserproximityコールバックを登録し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param sessionKey セッションキー
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onPutOnUserProximity(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {
        setUnsupportedError(response);
        return true;
    }

    // ------------------------------------
    // DELETE
    // ------------------------------------

    /**
     * ondeviceproximityコールバック解除リクエストハンドラー.<br/>
     * ondeviceproximityコールバックを解除し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param sessionKey セッションキー
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onDeleteOnDeviceProximity(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * onuserproximityコールバック解除リクエストハンドラー.<br/>
     * onuserproximityコールバックを解除し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param sessionKey セッションキー
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onDeleteOnUserProximity(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * メッセージに近接距離を設定する.
     * 
     * @param proximity メッセージパラメータ
     * @param value 近接距離
     */
    public static void setValue(final Bundle proximity, final double value) {
        proximity.putDouble(PARAM_VALUE, value);
    }

    /**
     * メッセージに近接距離の最小値を設定する.
     * 
     * @param proximity メッセージパラメータ
     * @param min 最小値
     */
    public static void setMin(final Bundle proximity, final double min) {
        proximity.putDouble(PARAM_MIN, min);
    }

    /**
     * メッセージに近接距離の最大値を設定する.
     * 
     * @param proximity メッセージパラメータ
     * @param max 最大値
     */
    public static void setMax(final Bundle proximity, final double max) {
        proximity.putDouble(PARAM_MAX, max);
    }

    /**
     * メッセージに閾値を設定する.
     * 
     * @param proximity メッセージパラメータ
     * @param threshold 閾値
     */
    public static void setThreshold(final Bundle proximity, final double threshold) {
        proximity.putDouble(PARAM_THRESHOLD, threshold);
    }

    /**
     * メッセージに近接センサー情報を設定する.
     * 
     * @param message メッセージパラメータ
     * @param proximity 近接センサー情報
     */
    public static void setProximity(final Intent message, final Bundle proximity) {
        message.putExtra(PARAM_PROXIMITY, proximity);
    }

    /**
     * メッセージに近接の有無を設定する.
     * 
     * @param message メッセージパラメータ
     * @param near 近接の有無(true: 近接している、false: 近接していない)
     */
    public static void setNear(final Bundle message, final boolean near) {
        message.putBoolean(PARAM_NEAR, near);
    }
}
