/*
 SystemProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.profile;

import java.util.ArrayList;
import java.util.List;

import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.profile.SystemProfileConstants;
import org.deviceconnect.profile.SystemProfileConstants.ConnectState;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

/**
 * System プロファイル.
 * 
 * <p>
 * システム情報を提供するAPI.<br/>
 * システム情報を提供するデバイスプラグインは当クラスを継承し、対応APIを実装すること。 <br/>
 * </p>
 * 
 * <h1>各API提供メソッド</h1>
 * <p>
 * System Profile の各APIへのリクエストに対し、以下のコールバックメソッド群が自動的に呼び出される。<br/>
 * サブクラスは以下のメソッド群からデバイスプラグインが提供するAPI用のメソッドをオーバーライドし、機能を実装すること。<br/>
 * オーバーライドされていない機能は自動的に非対応APIとしてレスポンスを返す。
 * </p>
 * <ul>
 * <li>System API [GET] :
 * {@link SystemProfile#onGetSystem(Intent, Intent, String)}</li>
 * <li>Device System API[GET] :
 * {@link SystemProfile#onGetDevice(Intent, Intent, String)}</li>
 * <li>Device System Wake Up API [PUT] :
 * {@link SystemProfile#onPutWakeup(Intent, Intent, String)}</li>
 * <li>Device System Wake Up API [DELETE] :
 * {@link SystemProfile#onDeleteWakeup(Intent, Intent, String)}</li>
 * </ul>
 * 
 * @author NTT DOCOMO, INC.
 */
public abstract class SystemProfile extends DConnectProfile implements SystemProfileConstants {

    /**
     * 設定画面起動用IntentのパラメータオブジェクトのExtraキー.
     */
    public static final String SETTING_PAGE_PARAMS = "org.deviceconnect.profile.system.setting_params";

    /**
     * プロファイルプロバイダー.
     */
    private DConnectProfileProvider mProvider;

    /**
     * 指定されたプロファイルプロバイダーをもつSystemプロファイルを生成する.
     * 
     * @param provider プロファイルプロバイダー
     */
    public SystemProfile(final DConnectProfileProvider provider) {
        this.mProvider = provider;
    }

    /**
     * 遷移先の設定画面用のActivityのクラス.
     * 
     * @param request リクエストパラメータ
     * @param param 
     *            Activity起動用Intentのパラメータ。設定画面用のActivityを呼び出すときにIntentのExtra要素として付加される
     *            。Extraのキーは {@link SystemProfile#SETTING_PAGE_PARAMS} となる。
     * 
     * @return ActivityのClassクラス
     */
    protected abstract Class<? extends Activity> getSettingPageActivity(Intent request, Bundle param);

    /**
     * プロファイルプロバイダーを取得する.
     * 
     * @return プロファイルプロバイダー
     */
    protected DConnectProfileProvider getProfileProvider() {
        return mProvider;
    }

    @Override
    public final String getProfileName() {
        return PROFILE_NAME;
    }

    /**
     * AndroidManifest.xmlのversionNameを取得する.
     * 
     * @return バージョン名
     */
    private String getCurrentVersionName() {
        PackageManager packageManager = getContext().getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getContext().getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            return packageInfo.versionName;
        } catch (NameNotFoundException e) {
            return "Unknown";
        }
    }

    /**
     * WiFiの接続状態を取得する.
     * 
     * @param deviceId デバイスID
     * @return WiFiの接続状態
     * @see ConnectState
     */
    protected ConnectState getWifiState(final String deviceId) {
        return ConnectState.NONE;
    }

    /**
     * Bluetoothの接続状態を取得する.
     * 
     * @param deviceId デバイスID
     * @return Bluetoothの接続状態
     * @see ConnectState
     */
    protected ConnectState getBluetoothState(final String deviceId) {
        return ConnectState.NONE;
    }

    /**
     * NFCの接続状態を取得する.
     * 
     * @param deviceId デバイスID
     * @return NFCの接続状態
     * @see ConnectState
     */
    protected ConnectState getNFCState(final String deviceId) {
        return ConnectState.NONE;
    }

    /**
     * BLEの接続状態を取得する.
     * 
     * @param deviceId デバイスID
     * @return BLEの接続状態
     * @see ConnectState
     */
    protected ConnectState getBLEState(final String deviceId) {
        return ConnectState.NONE;
    }

    @Override
    protected boolean onGetRequest(final Intent request, final Intent response) {
        String attribute = getAttribute(request);
        boolean result = true;
        String deviceId = getDeviceID(request);

        if (attribute == null) {
            result = onGetSystem(request, response, deviceId);
        } else if (attribute.equals(ATTRIBUTE_DEVICE)) {
            result = onGetDevice(request, response, deviceId);
        } else {
            MessageUtils.setUnknownAttributeError(response);
        }

        return result;
    }

    @Override
    protected boolean onPutRequest(final Intent request, final Intent response) {
        String inter = getInterface(request);
        String attribute = getAttribute(request);
        boolean result = true;
        if (INTERFACE_DEVICE.equals(inter) && ATTRIBUTE_WAKEUP.equals(attribute)) {
            result = onPutWakeup(request, response, getPluginID(request));
        } else if (inter == null && ATTRIBUTE_KEYWORD.equals(attribute)) {
            result = onPutKeyword(request, response);
        } else {
            MessageUtils.setUnknownAttributeError(response);
        }

        return result;
    }

    @Override
    protected boolean onDeleteRequest(final Intent request, final Intent response) {
        String inter = getInterface(request);
        String attribute = getAttribute(request);
        boolean result = true;

        if (INTERFACE_DEVICE.equals(inter) && ATTRIBUTE_WAKEUP.equals(attribute)) {
            result = onDeleteWakeup(request, response, getPluginID(request));
        } else if (inter == null && ATTRIBUTE_EVENTS.equals(attribute)) {
            result = onDeleteEvents(request, response, getSessionKey(request));
        } else {
            MessageUtils.setUnknownAttributeError(response);
        }

        return result;
    }

    // ------------------------------------
    // GET
    // ------------------------------------

    /**
     * システム情報取得リクエストハンドラー.<br/>
     * デバイスのシステム情報を提供し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * <strong>基本的にDevice Connect Managerのみの実装となるので、他のデバイスプラグインが実装しても動作はしない。</strong>
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onGetSystem(final Intent request, final Intent response, final String deviceId) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * 周辺機器のシステム情報取得リクエストハンドラー.<br/>
     * 周辺機器のシステム情報取得を提供し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * このメソッドでは自動的にシステム情報を返信する。返信処理に変更を加えたい場合はオーバーライドし、処理を上書きすること。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onGetDevice(final Intent request, final Intent response, final String deviceId) {

        // connect
        Bundle connect = new Bundle();
        setWifiState(connect, getWifiState(deviceId));
        setBluetoothState(connect, getBluetoothState(deviceId));
        setNFCState(connect, getNFCState(deviceId));
        setBLEState(connect, getBLEState(deviceId));
        setConnect(response, connect);

        // version
        setVersion(response, getCurrentVersionName());

        // supports
        ArrayList<String> profiles = new ArrayList<String>();
        for (DConnectProfile profile : mProvider.getProfileList()) {
            profiles.add(profile.getProfileName());
        }
        setSupports(response, profiles.toArray(new String[0]));
        setResult(response, DConnectMessage.RESULT_OK);

        return true;
    }
    
    // ------------------------------------
    // PUT
    // ------------------------------------

    /**
     * デバイスプラグイン有効化リクエストハンドラー.<br/>
     * デバイスプラグインを有効にし、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * pluginIdにはnullや空文字が送られることがあるので注意すること。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param pluginId デバイスプラグインID
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onPutWakeup(final Intent request, final Intent response, final String pluginId) {
        Bundle param = new Bundle();
        Class<? extends Activity> clazz = getSettingPageActivity(request, param);
        if (clazz == null) {
            setUnsupportedError(response);
        } else {
            Intent i = new Intent(getContext(), clazz);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra(SETTING_PAGE_PARAMS, param);
            getContext().startActivity(i);
            setResult(response, DConnectMessage.RESULT_OK);
        }
        return true;
    }
    
    /**
     * Device Connect Manager設定キーワード表示リクエストハンドラー.<br/>
     * Device Connect Managerに設定されているキーワードを表示し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 
     * <strong>基本的にDevice Connect Managerのみの実装となるので、他のデバイスプラグインが実装しても動作はしない。</strong>
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onPutKeyword(final Intent request, final Intent response) {
        setUnsupportedError(response);
        return true;
    }

    // ------------------------------------
    // DELETE
    // ------------------------------------

    /**
     * デバイスプラグイン無効化リクエストハンドラー.<br/>
     * デバイスプラグインを無効にし、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * pluginIdにはnullや空文字が送られることがあるので注意すること。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param pluginId デバイスID
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onDeleteWakeup(final Intent request, final Intent response, final String pluginId) {
        setUnsupportedError(response);
        return true;
    }
    
    /**
     * イベント解除リクエストハンドラー.<br/>
     * 指定されたセッションキーに紐づくイベントを全て解除し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param sessionKey セッションキー
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onDeleteEvents(final Intent request, final Intent response, final String sessionKey) {
        // TODO ここでイベントの解除をする
        setUnsupportedError(response);
        return true;
    }

    // ------------------------------------
    // レスポンスセッターメソッド群
    // ------------------------------------

    /**
     * レスポンスにバージョンを格納する.
     * 
     * @param response レスポンスパラメータ
     * @param version バージョン
     */
    public static void setVersion(final Intent response, final String version) {
        response.putExtra(PARAM_VERSION, version);
    }

    /**
     * レスポンスにサポートしているI/Fの一覧を格納する.
     * 
     * @param response レスポンスパラメータ
     * @param supports サポートしているI/F一覧
     */
    public static void setSupports(final Intent response, final String[] supports) {
        response.putExtra(PARAM_SUPPORTS, supports);
    }

    /**
     * レスポンスにサポートしているI/Fの一覧を格納する.
     * 
     * @param response レスポンスパラメータ
     * @param supports サポートしているI/F一覧
     */
    public static void setSupports(final Intent response, final List<String> supports) {
        setSupports(response, supports.toArray(new String[supports.size()]));
    }

    /**
     * レスポンスにデバイスの接続状態を設定する.
     * 
     * @param response レスポンスパラメータ
     * @param connect デバイスの接続状態
     */
    public static void setConnect(final Intent response, final Bundle connect) {
        response.putExtra(PARAM_CONNECT, connect);
    }

    /**
     * 指定されたパラメータに接続状態を設定する.
     * 
     * @param b バンドルオブジェクト
     * @param key パラメータキー
     * @param state 状態
     */
    private static void setConnectionState(final Bundle b, final String key, final ConnectState state) {
        // 非対応のものは省略
        switch (state) {
        case ON:
            b.putBoolean(key, true);
            break;
        case OFF:
            b.putBoolean(key, false);
            break;
        default:
            break;
        }
    }

    /**
     * デバイスプラグインの接続状態にWiFiの接続状態を設定する.
     * 
     * @param connect デバイスプラグインの接続状態パラメータ
     * @param connecting true:ON、false:OFF
     */
    public static void setWifiState(final Bundle connect, final boolean connecting) {
        connect.putBoolean(PARAM_WIFI, connecting);
    }

    /**
     * デバイスプラグインの接続状態にWiFiの接続状態を設定する.
     * 
     * @param connect デバイスプラグインの接続状態パラメータ
     * @param state 接続状態
     */
    public static void setWifiState(final Bundle connect, final ConnectState state) {
        setConnectionState(connect, PARAM_WIFI, state);
    }

    /**
     * デバイスプラグインの接続状態にBluetoothの接続状態を設定する.
     * 
     * @param connect デバイスプラグインの接続状態パラメータ
     * @param connecting true:ON、false:OFF
     */
    public static void setBluetoothState(final Bundle connect, final boolean connecting) {
        connect.putBoolean(PARAM_BLUETOOTH, connecting);
    }

    /**
     * デバイスプラグインの接続状態にBluetoothの接続状態を設定する.
     * 
     * @param connect デバイスプラグインの接続状態パラメータ
     * @param state 接続状態
     */
    public static void setBluetoothState(final Bundle connect, final ConnectState state) {
        setConnectionState(connect, PARAM_BLUETOOTH, state);
    }

    /**
     * デバイスプラグインの接続状態にNFCの接続状態を設定する.
     * 
     * @param connect デバイスプラグインの接続状態パラメータ
     * @param connecting true:ON、false:OFF
     */
    public static void setNFCState(final Bundle connect, final boolean connecting) {
        connect.putBoolean(PARAM_NFC, connecting);
    }

    /**
     * デバイスプラグインの接続状態にNFCの接続状態を設定する.
     * 
     * @param connect デバイスプラグインの接続状態パラメータ
     * @param state 接続状態
     */
    public static void setNFCState(final Bundle connect, final ConnectState state) {
        setConnectionState(connect, PARAM_NFC, state);
    }

    /**
     * デバイスプラグインの接続状態にBLEの接続状態を設定する.
     * 
     * @param connect デバイスプラグインの接続状態パラメータ
     * @param connecting true:ON、false:OFF
     */
    public static void setBLEState(final Bundle connect, final boolean connecting) {
        connect.putBoolean(PARAM_BLE, connecting);
    }

    /**
     * デバイスプラグインの接続状態にBLEの接続状態を設定する.
     * 
     * @param connect デバイスプラグインの接続状態パラメータ
     * @param state 接続状態
     */
    public static void setBLEState(final Bundle connect, final ConnectState state) {
        setConnectionState(connect, PARAM_BLE, state);
    }

    /**
     * リクエストからpluginIdを取得する.
     * 
     * @param request リクエストパラメータ
     * @return プラグインのID
     */
    public static String getPluginID(final Intent request) {
        String pluginId = request.getStringExtra(PARAM_PLUGIN_ID);
        return pluginId;
    }
}
