/*
 PhoneProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.profile;

import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.profile.PhoneProfileConstants;
import org.deviceconnect.profile.PhoneProfileConstants.CallState;
import org.deviceconnect.profile.PhoneProfileConstants.PhoneMode;

import android.content.Intent;
import android.os.Bundle;

/**
 * Phone プロファイル.
 * 
 * <p>
 * 通話操作機能を提供するAPI.<br/>
 * 通話操作機能を提供するデバイスプラグインは当クラスを継承し、対応APIを実装すること。 <br/>
 * </p>
 * 
 * <h1>各API提供メソッド</h1>
 * <p>
 * Phone Profile の各APIへのリクエストに対し、以下のコールバックメソッド群が自動的に呼び出される。<br/>
 * サブクラスは以下のメソッド群からデバイスプラグインが提供するAPI用のメソッドをオーバーライドし、機能を実装すること。<br/>
 * オーバーライドされていない機能は自動的に非対応APIとしてレスポンスを返す。
 * </p>
 * <ul>
 * <li>Phone Call API [POST] : {@link PhoneProfile#onPostCall(Intent, Intent, String, String)}</li>
 * <li>Phone Setting API [PUT] : {@link PhoneProfile#onPutSet(Intent, Intent, String, 
 * org.deviceconnect.profile.PhoneProfileConstants.PhoneMode)}</li>
 * <li>Phone Connect Event API [Register] :
 * {@link PhoneProfile#onPutOnConnect(Intent, Intent, String, String)}</li>
 * <li>Phone Connect Event API [Unregister] :
 * {@link PhoneProfile#onDeleteOnConnect(Intent, Intent, String, String)}</li>
 * </ul>
 * @author NTT DOCOMO, INC.
 */
public abstract class PhoneProfile extends DConnectProfile implements PhoneProfileConstants {

    @Override
    public final String getProfileName() {
        return PROFILE_NAME;
    }

    @Override
    protected boolean onPostRequest(final Intent request, final Intent response) {
        String attribute = getAttribute(request);
        boolean result = true;

        if (ATTRIBUTE_CALL.equals(attribute)) {
            result = onPostCall(request, response, getDeviceID(request), getPhoneNumber(request));
        } else {
            MessageUtils.setUnknownAttributeError(response);
        }

        return result;
    }

    @Override
    protected boolean onPutRequest(final Intent request, final Intent response) {
        String attribute = getAttribute(request);
        boolean result = true;

        if (attribute == null) {
            MessageUtils.setUnknownAttributeError(response);
        } else {
            String deviceId = getDeviceID(request);
            if (attribute.equals(ATTRIBUTE_SET)) {
                result = onPutSet(request, response, deviceId, getMode(request));
            } else if (attribute.equals(ATTRIBUTE_ON_CONNECT)) {
                result = onPutOnConnect(request, response, deviceId, getSessionKey(request));
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
            if (attribute.equals(ATTRIBUTE_ON_CONNECT)) {
                result = onDeleteOnConnect(request, response, getDeviceID(request), getSessionKey(request));
            } else {
                MessageUtils.setUnknownAttributeError(response);
            }
        }

        return result;
    }

    // ------------------------------------
    // POST
    // ------------------------------------

    /**
     * デバイスへの電話発信要求リクエストハンドラー.<br/>
     * 電話発信をし、その結果をレスポンスパラメータに格納する。 レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param phoneNumber 発信先の電話番号
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onPostCall(final Intent request, final Intent response, final String deviceId, 
            final String phoneNumber) {
        setUnsupportedError(response);
        return true;
    }

    // ------------------------------------
    // PUT
    // ------------------------------------

    /**
     * 設定項目設定リクエストハンドラー.<br/>
     * リクエストパラメータに応じてデバイスのサービスを提供し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param mode 電話のモード
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onPutSet(final Intent request, final Intent response, final String deviceId,
            final PhoneMode mode) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * onconnectコールバック登録リクエストハンドラー.<br/>
     * onconnectコールバックを登録し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param sessionKey セッションキー
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onPutOnConnect(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {
        setUnsupportedError(response);
        return true;
    }

    // ------------------------------------
    // DELETE
    // ------------------------------------

    /**
     * onconnectコールバック解除リクエストハンドラー.<br/>
     * onconnectコールバックを解除し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param sessionKey セッションキー
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onDeleteOnConnect(final Intent request, final Intent response, final String deviceId, 
            final String sessionKey) {
        setUnsupportedError(response);
        return true;
    }

    // ------------------------------------
    // リクエストゲッターメソッド群
    // ------------------------------------

    /**
     * リクエストから発信先の電話番号を取得する.
     * 
     * @param request リクエストパラメータ
     * @return 発信先の電話番号。無い場合はnullを返す。
     */
    public static String getPhoneNumber(final Intent request) {
        String value = request.getStringExtra(PARAM_PHONE_NUMBER);
        return value;
    }

    /**
     * リクエストから電話のモードを取得する.
     * 
     * @param request リクエストパラメータ
     * @return モード。無い場合は{@link PhoneMode#UNKNOWN}を返す。
     *         <ul>
     *         <li>{@link PhoneMode#SILENT}</li>
     *         <li>{@link PhoneMode#MANNER}</li>
     *         <li>{@link PhoneMode#SOUND}</li>
     *         <li>{@link PhoneMode#UNKNOWN}</li>
     *         </ul>
     */
    public static PhoneMode getMode(final Intent request) {
        Integer value = parseInteger(request, PARAM_MODE);
        if (value == null) {
            value = PhoneMode.UNKNOWN.getValue();
        }
        PhoneMode mode = PhoneMode.getInstance(value);
        return mode;
    }

    // ------------------------------------
    // レスポンスセッターメソッド群
    // ------------------------------------

    /**
     * レスポンスにイベントオブジェクトを設定する.
     * 
     * @param response レスポンスパラメータ
     * @param phoneStatus イベントオブジェクト
     */
    public static void setPhoneStatus(final Intent response, final Bundle phoneStatus) {
        response.putExtra(PARAM_PHONE_STATUS, phoneStatus);
    }

    /**
     * レスポンスのphoneStatusイベントオブジェクトに通話状態を設定する.
     * 
     * @param phoneStatus イベントオブジェクトのパラメータ
     * @param state 通話状態
     */
    public static void setState(final Bundle phoneStatus, final CallState state) {

        if (state == CallState.UNKNOWN) {
            throw new IllegalArgumentException("State should not be UNKNOWN.");
        }

        phoneStatus.putInt(PARAM_STATE, state.getValue());
    }

    /**
     * レスポンスのphoneStatusイベントオブジェクトに発信先の電話番号を設定する.
     * 
     * @param phoneStatus イベントオブジェクト
     * @param phoneNumber 発信先の電話番号
     */
    public static void setPhoneNumber(final Bundle phoneStatus, final String phoneNumber) {
        phoneStatus.putString(PARAM_PHONE_NUMBER, phoneNumber);
    }
}
