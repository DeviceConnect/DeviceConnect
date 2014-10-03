/*
 NetworkServiceDiscoveryProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.profile;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;

import com.nttdocomo.android.dconnect.message.MessageUtils;
import com.nttdocomo.dconnect.profile.NetworkServiceDiscoveryProfileConstants;

/**
 * Network Service Discovery プロファイル.
 * 
 * <p>
 * スマートデバイス検索機能を提供するAPI.<br/>
 * スマートデバイス検索機能を提供するデバイスプラグインは当クラスを継承し、対応APIを実装すること。 <br/>
 * 本クラスでは Found Event と Lost Event は処理しない。デバイスプラグインの任意のタイミングでデバイスの検出、消失の
 * イベントメッセージをDevice Connectに送信する必要がある。
 * </p>
 * 
 * <h1>各API提供メソッド</h1>
 * <p>
 * Network Service Discovery Profile の各APIへのリクエストに対し、以下のコールバックメソッド群が自動的に呼び出される。<br/>
 * サブクラスは以下のメソッド群からデバイスプラグインが提供するAPI用のメソッドをオーバーライドし、機能を実装すること。<br/>
 * オーバーライドされていない機能は自動的に非対応APIとしてレスポンスを返す。
 * </p>
 * <ul>
 * <li>Network Service Discovery API [GET] :
 * {@link NetworkServiceDiscoveryProfile#onGetGetNetworkServices(Intent, Intent)}
 * </li>
 * </ul>
 * 
 * @see com.nttdocomo.android.dconnect.message.DConnectMessageService#notifyNetworkServiceFound(String,
 *      String, String, boolean, String)
 * @see com.nttdocomo.android.dconnect.message.DConnectMessageService#notifyNetworkServiceLost(String,
 *      String)
 * @author NTT DOCOMO, INC.
 */
public abstract class NetworkServiceDiscoveryProfile extends DConnectProfile implements
        NetworkServiceDiscoveryProfileConstants {

    @Override
    public final String getProfileName() {
        return PROFILE_NAME;
    }

    @Override
    protected boolean onGetRequest(final Intent request, final Intent response) {
        String attribute = getAttribute(request);
        boolean result = true;

        if (ATTRIBUTE_GET_NETWORK_SERVICES.equals(attribute)) {
            result = onGetGetNetworkServices(request, response);
        } else {
            MessageUtils.setUnknownAttributeError(response);
        }

        return result;

    }
    
    @Override
    protected boolean onPutRequest(final Intent request, final Intent response) {
        String attribute = getAttribute(request);
        boolean result = true;

        if (ATTRIBUTE_ON_SERVICE_CHANGE.equals(attribute)) {
            String deviceId = getDeviceID(request);
            String sessionKey = getSessionKey(request);
            result = onPutOnServiceChange(request, response, deviceId, sessionKey);
        } else {
            MessageUtils.setUnknownAttributeError(response);
        }

        return result;
        
    }

    @Override
    protected boolean onDeleteRequest(final Intent request, final Intent response) {
        String attribute = getAttribute(request);
        boolean result = true;

        if (ATTRIBUTE_ON_SERVICE_CHANGE.equals(attribute)) {
            String deviceId = getDeviceID(request);
            String sessionKey = getSessionKey(request);
            result = onDeleteOnServiceChange(request, response, deviceId, sessionKey);
        } else {
            MessageUtils.setUnknownAttributeError(response);
        }

        return result;
    }

    // ------------------------------------
    // GET
    // ------------------------------------

    /**
     * スマートデバイス一覧取得リクエストハンドラー.<br/>
     * スマートデバイス一覧を提供し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onGetGetNetworkServices(final Intent request, final Intent response) {
        setUnsupportedError(response);
        return true;
    }

    // ------------------------------------
    // PUT
    // ------------------------------------
    
    /**
     * onservicechangeイベント登録リクエストハンドラー.<br/>
     * onservicechangeイベントを登録し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param sessionKey セッションキー
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onPutOnServiceChange(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {
        setUnsupportedError(response);
        return true;
    }
    
    // ------------------------------------
    // DELETE
    // ------------------------------------
    
    /**
     * onservicechangeイベント解除リクエストハンドラー.<br/>
     * onservicechangeイベントを解除し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param sessionKey セッションキー
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onDeleteOnServiceChange(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {
        setUnsupportedError(response);
        return true;
    }
    
    // ------------------------------------
    // レスポンスセッターメソッド群
    // ------------------------------------

    /**
     * レスポンスにデバイス一覧を設定する.
     * 
     * @param response レスポンスパラメータ
     * @param services デバイス一覧
     */
    public static void setServices(final Intent response, final Bundle[] services) {
        response.putExtra(PARAM_SERVICES, services);
    }

    /**
     * レスポンスにデバイス一覧を設定する.
     * 
     * @param response レスポンスパラメータ
     * @param services デバイス一覧
     */
    public static void setServices(final Intent response, final List<Bundle> services) {
        setServices(response, services.toArray(new Bundle[services.size()]));
    }

    /**
     * メッセージにデバイス情報を設定する.
     * 
     * @param message メッセージパラメータ
     * @param networkService デバイス情報
     */
    public static void setNetworkService(final Intent message, final Bundle networkService) {
        message.putExtra(PARAM_NETWORK_SERVICE, networkService);
    }

    /**
     * デバイスIDを設定する.
     * 
     * @param service デバイスパラメータ
     * @param id デバイスID
     */
    public static void setId(final Bundle service, final String id) {
        service.putString(PARAM_ID, id);
    }

    /**
     * デバイス名を設定する.
     * 
     * @param service デバイスパラメータ
     * @param name デバイス名
     */
    public static void setName(final Bundle service, final String name) {
        service.putString(PARAM_NAME, name);
    }

    /**
     * デバイスのネットワークタイプを設定する.
     * 
     * @param service デバイスパラメータ
     * @param type デバイスのネットワークタイプ
     *            <ul>
     *            <li>{@link NetworkType#WIFI}</li>
     *            <li>{@link NetworkType#BLE}</li>
     *            <li>{@link NetworkType#NFC}</li>
     *            <li>{@link NetworkType#BLUETOOTH}</li>
     *            </ul>
     */
    public static void setType(final Bundle service, final NetworkType type) {
        setType(service, type.getValue());
    }
    
    /**
     * デバイスのネットワークタイプを設定する.
     * 
     * @param service デバイスパラメータ
     * @param type デバイスのネットワークタイプ
     */
    public static void setType(final Bundle service, final String type) {
        service.putString(PARAM_TYPE, type);
    }

    /**
     * デバイスのオンライン状態を設定する.
     * 
     * @param service デバイスパラメータ
     * @param online オンライン: true、 オフライン: false
     */
    public static void setOnline(final Bundle service, final boolean online) {
        service.putBoolean(PARAM_ONLINE, online);
    }

    /**
     * デバイスの設定情報を設定する.
     * 
     * @param service デバイスパラメータ
     * @param config 設定情報文字列
     */
    public static void setConfig(final Bundle service, final String config) {
        service.putString(PARAM_CONFIG, config);
    }

    /**
     * デバイスの接続状態を設定する.
     * 
     * @param service デバイスパラメータ
     * @param state 接続 : true、未接続 : false
     */
    public static void setState(final Bundle service, final boolean state) {
        service.putBoolean(PARAM_STATE, state);
    }
}
