/*
 SettingsProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.profile;

import android.content.Intent;

import com.nttdocomo.android.dconnect.message.MessageUtils;
import com.nttdocomo.dconnect.profile.SettingsProfileConstants;

/**
 * Settings プロファイル.
 * 
 * <p>
 * スマートデバイスの各種設定状態の取得および設定機能を提供するAPI.<br/>
 * スマートデバイスの各種設定状態の取得および設定機能を提供するデバイスプラグインは当クラスを継承し、対応APIを実装すること。 <br/>
 * </p>
 * 
 * <h1>各API提供メソッド</h1>
 * <p>
 * Settings Profile の各APIへのリクエストに対し、以下のコールバックメソッド群が自動的に呼び出される。<br/>
 * サブクラスは以下のメソッド群からデバイスプラグインが提供するAPI用のメソッドをオーバーライドし、機能を実装すること。<br/>
 * オーバーライドされていない機能は自動的に非対応APIとしてレスポンスを返す。
 * </p>
 * <ul>
 * <li>Volume Settings API [GET] :
 * {@link SettingsProfile#onGetSoundVolume(Intent, Intent, String, 
 * com.nttdocomo.dconnect.profile.SettingsProfileConstants.VolumeKind)}</li>
 * <li>Volume Settings API [PUT] :
 * {@link SettingsProfile#onPutSoundVolume(Intent, Intent, String, 
 * com.nttdocomo.dconnect.profile.SettingsProfileConstants.VolumeKind, Double))}</li>
 * <li>Date Settings API [GET] :
 * {@link SettingsProfile#onGetDate(Intent, Intent, String)}</li>
 * <li>Date Settings API [PUT] :
 * {@link SettingsProfile#onPutDate(Intent, Intent, String, String)}</li>
 * <li>Display Light Settings API [GET] :
 * {@link SettingsProfile#onGetDisplayLight(Intent, Intent, String)}</li>
 * <li>Display Light Settings API [PUT] :
 * {@link SettingsProfile#onPutDisplayLight(Intent, Intent, String, Double)}</li>
 * <li>Display Sleep Settings API [GET] :
 * {@link SettingsProfile#onGetDisplaySleep(Intent, Intent, String)}</li>
 * <li>Display Sleep Settings API [PUT] :
 * {@link SettingsProfile#onPutDisplaySleep(Intent, Intent, String, Integer)}</li>
 * </ul>
 * @author NTT DOCOMO, INC.
 */
public abstract class SettingsProfile extends DConnectProfile implements SettingsProfileConstants {

    @Override
    public final String getProfileName() {
        return PROFILE_NAME;
    }

    @Override
    protected boolean onGetRequest(final Intent request, final Intent response) {
        String inter = getInterface(request);
        String attribute = getAttribute(request);
        boolean result = true;

        if (attribute == null) {
            MessageUtils.setUnknownAttributeError(response);
        } else {

            String deviceId = getDeviceID(request);

            if (inter == null) {
                if (attribute.equals(ATTRIBUTE_DATE)) {
                    result = onGetDate(request, response, deviceId);
                } else {
                    MessageUtils.setUnknownAttributeError(response);
                }
            } else {
                if (inter.equals(INTERFACE_SOUND) && attribute.equals(ATTRIBUTE_VOLUME)) {
                    result = onGetSoundVolume(request, response, deviceId, getVolumeKind(request));
                } else if (inter.equals(INTERFACE_DISPLAY) && attribute.equals(ATTRIBUTE_LIGHT)) {
                    result = onGetDisplayLight(request, response, deviceId);
                } else if (inter.equals(INTERFACE_DISPLAY) && attribute.equals(ATTRIBUTE_SLEEP)) {
                    result = onGetDisplaySleep(request, response, deviceId);
                } else {
                    MessageUtils.setUnknownAttributeError(response);
                }
            }
        }

        return result;
    }

    @Override
    protected boolean onPutRequest(final Intent request, final Intent response) {
        String inter = getInterface(request);
        String attribute = getAttribute(request);
        boolean result = true;

        if (attribute == null) {
            MessageUtils.setUnknownAttributeError(response);
        } else {

            String deviceId = getDeviceID(request);

            if (inter == null) {
                if (attribute.equals(ATTRIBUTE_DATE)) {
                    result = onPutDate(request, response, deviceId, getDate(request));
                } else {
                    MessageUtils.setUnknownAttributeError(response);
                }
            } else {
                if (inter.equals(INTERFACE_SOUND) && attribute.equals(ATTRIBUTE_VOLUME)) {
                    result = onPutSoundVolume(request, response, deviceId, getVolumeKind(request),
                                getVolumeLevel(request));
                } else if (inter.equals(INTERFACE_DISPLAY) && attribute.equals(ATTRIBUTE_LIGHT)) {
                    result = onPutDisplayLight(request, response, deviceId, getLightLevel(request));
                } else if (inter.equals(INTERFACE_DISPLAY) && attribute.equals(ATTRIBUTE_SLEEP)) {
                    result = onPutDisplaySleep(request, response, deviceId, getTime(request));
                } else {
                    MessageUtils.setUnknownAttributeError(response);
                }
            }
        }

        return result;
    }

    // ------------------------------------
    // GET
    // ------------------------------------

    /**
     * デバイスの音量取得リクエストハンドラー.<br/>
     * デバイスの音量を提供し、その結果をレスポンスパラメータに格納する。 レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param kind 種別
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onGetSoundVolume(final Intent request, final Intent response, final String deviceId,
            final VolumeKind kind) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * デバイスの日時取得リクエストハンドラー.<br/>
     * デバイスの日時を提供し、その結果をレスポンスパラメータに格納する。 レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onGetDate(final Intent request, final Intent response, final String deviceId) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * デバイスのバックライト明度取得リクエストハンドラー.<br/>
     * デバイスのバックライト明度を提供し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onGetDisplayLight(final Intent request, final Intent response, final String deviceId) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * デバイスの画面消灯設定取得リクエストハンドラー.<br/>
     * デバイスの画面消灯設定を提供し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onGetDisplaySleep(final Intent request, final Intent response, final String deviceId) {
        setUnsupportedError(response);
        return true;
    }

    // ------------------------------------
    // PUT
    // ------------------------------------

    /**
     * デバイスの音量設定リクエストハンドラー.<br/>
     * デバイスの音量を設定し、その結果をレスポンスパラメータに格納する。 レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param kind 種別
     * @param level 音量
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onPutSoundVolume(final Intent request, final Intent response, final String deviceId,
            final VolumeKind kind, final Double level) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * デバイスの日時設定リクエストハンドラー.<br/>
     * デバイスの日時を設定し、その結果をレスポンスパラメータに格納する。 レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param date 日時
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onPutDate(final Intent request, final Intent response, final String deviceId, final String date) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * デバイスのバックライト明度設定リクエストハンドラー.<br/>
     * デバイスのバックライト明度を設定し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param level 明度パーセント
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onPutDisplayLight(final Intent request, final Intent response, final String deviceId,
            final Double level) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * デバイスの画面消灯設定リクエストハンドラー.<br/>
     * デバイスの画面消灯設定を設定し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param time 消灯するまでの時間(ミリ秒)
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onPutDisplaySleep(final Intent request, final Intent response, final String deviceId,
            final Integer time) {
        setUnsupportedError(response);
        return true;
    }

    // ------------------------------------
    // リクエストゲッターメソッド群
    // ------------------------------------

    /**
     * リクエストから音量種別を取得する.
     * 
     * @param request リクエストパラメータ
     * @return 通知タイプ。無い場合は{@link VolumeKind#UNKNOWN}を返す。
     *         <ul>
     *         <li>{@link VolumeKind#ALARM}</li>
     *         <li>{@link VolumeKind#CALL}</li>
     *         <li>{@link VolumeKind#RINGTONE}</li>
     *         <li>{@link VolumeKind#MAIL}</li>
     *         <li>{@link VolumeKind#OTHER}</li>
     *         <li>{@link VolumeKind#UNKNOWN}</li>
     *         </ul>
     */
    public static VolumeKind getVolumeKind(final Intent request) {
        Integer value = parseInteger(request, PARAM_KIND);
        if (value == null) {
            value = VolumeKind.UNKNOWN.getValue();
        }
        VolumeKind kind = VolumeKind.getInstance(value);
        return kind;
    }

    /**
     * リクエストから音量を取得する.
     * 
     * @param request リクエストパラメータ
     * @return 音量。無い場合はnullを返す。
     */
    public static Double getVolumeLevel(final Intent request) {
        return getLevel(request);
    }

    /**
     * リクエストからバックライト明度を取得する.
     * 
     * @param request リクエストパラメータ
     * @return バックライト明度。無い場合はnullを返す。
     */
    public static Double getLightLevel(final Intent request) {
        return getLevel(request);
    }

    /**
     * リクエストから音量を取得する.
     * 
     * @param request リクエストパラメータ
     * @return 音量。無い場合はnullを返す。
     */
    private static Double getLevel(final Intent request) {
        return parseDouble(request, PARAM_LEVEL);
    }

    /**
     * リクエストから日時を取得する.
     * 
     * @param request リクエストパラメータ
     * @return 日時文字列。 無い場合はnullを返す。
     */
    public static String getDate(final Intent request) {
        String date = request.getStringExtra(PARAM_DATE);
        return date;
    }

    /**
     * リクエストから消灯するまでの時間(ミリ秒)を取得する.
     * 
     * @param request リクエストパラメータ
     * @return 消灯するまでの時間(ミリ秒)。無い場合はnullを返す。
     */
    public static Integer getTime(final Intent request) {
        return parseInteger(request, PARAM_TIME);
    }

    // ------------------------------------
    // レスポンスセッターメソッド群
    // ------------------------------------

    /**
     * レスポンスにlevelを設定する.
     * 
     * @param response レスポンスパラメータ
     * @param level パーセント
     */
    private static void setLevel(final Intent response, final double level) {

        if (level < MIN_LEVEL || MAX_LEVEL < level) {
            throw new IllegalArgumentException("Level must be between " + MIN_LEVEL + " and " + MAX_LEVEL + ".");
        }

        response.putExtra(PARAM_LEVEL, level);
    }

    /**
     * レスポンスに音量を設定する.
     * 
     * @param response レスポンスパラメータ
     * @param level 音量
     */
    public static void setVolumeLevel(final Intent response, final double level) {
        setLevel(response, level);
    }

    /**
     * レスポンスにバックライト明度を設定する.
     * 
     * @param response レスポンスパラメータ
     * @param level 音量
     */
    public static void setLightLevel(final Intent response, final double level) {
        setLevel(response, level);
    }

    /**
     * レスポンスに日時を設定する.
     * 
     * @param response レスポンスパラメータ
     * @param date 日時。フォーマットはYYYY-MM-DDThh:mm:ss+0900(RFC3339)。
     */
    public static void setDate(final Intent response, final String date) {
        // TODO フォーマットチェックをすべきか？
        response.putExtra(PARAM_DATE, date);
    }

    /**
     * レスポンスに消灯するまでの時間(ミリ秒)を設定する.
     * 
     * @param response レスポンスパラメータ
     * @param time 消灯するまでの時間(ミリ秒)
     */
    public static void setTime(final Intent response, final int time) {

        if (time < 0) {
            throw new IllegalArgumentException("Time must be more than 0.");
        }

        response.putExtra(PARAM_TIME, time);
    }
}
