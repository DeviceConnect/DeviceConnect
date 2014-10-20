/*
 DConnectMessageService.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.deviceconnect.android.event.Event;
import org.deviceconnect.android.event.EventManager;
import org.deviceconnect.android.event.cache.db.DBCacheController;
import org.deviceconnect.android.localoauth.CheckAccessTokenResult;
import org.deviceconnect.android.localoauth.LocalOAuth2Main;
import org.deviceconnect.android.logger.AndroidHandler;
import org.deviceconnect.android.manager.DConnectLocalOAuth.OAuthData;
import org.deviceconnect.android.manager.DevicePluginManager.DevicePluginEventListener;
import org.deviceconnect.android.manager.profile.AuthorizationProfile;
import org.deviceconnect.android.manager.profile.DConnectDeliveryProfile;
import org.deviceconnect.android.manager.profile.DConnectFilesProfile;
import org.deviceconnect.android.manager.profile.DConnectNetworkServiceDiscoveryProfile;
import org.deviceconnect.android.manager.profile.DConnectSystemProfile;
import org.deviceconnect.android.manager.request.DConnectRequest;
import org.deviceconnect.android.manager.request.DConnectRequestManager;
import org.deviceconnect.android.manager.request.DiscoveryDeviceRequest;
import org.deviceconnect.android.manager.request.RegisterNetworkServiceDiscovery;
import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.android.profile.DConnectProfile;
import org.deviceconnect.android.profile.DConnectProfileProvider;
import org.deviceconnect.android.profile.NetworkServiceDiscoveryProfile;
import org.deviceconnect.android.provider.FileManager;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.message.intent.message.IntentDConnectMessage;
import org.deviceconnect.profile.NetworkServiceDiscoveryProfileConstants;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

/**
 * DConnectMessageを受信するサービス.
 * @author NTT DOCOMO, INC.
 */
public abstract class DConnectMessageService extends Service 
        implements DConnectProfileProvider, DevicePluginEventListener {
    /** ドメイン名. */
    private static final String DCONNECT_DOMAIN = ".deviceconnect.org";
    /** ローカルのドメイン名. */
    private static final String LOCALHOST_DCONNECT = "localhost" + DCONNECT_DOMAIN;

    /** デバイスIDやセッションキーを分割するセパレータ. */
    public static final String SEPARATOR = ".";

    /** セッションキーとreceiverを分けるセパレータ. */
    public static final String SEPARATOR_SESSION = "@";

    /** リクエストコードのエラー値を定義. */
    private static final int ERROR_CODE = Integer.MIN_VALUE;

    /** ロガー. */
    protected final Logger sLogger = Logger.getLogger("dconnect.manager");

    /** dConnect Managerのドメイン名. */
    private String mDConnectDomain = LOCALHOST_DCONNECT;

    /** プロファイルインスタンスマップ. */
    private Map<String, DConnectProfile> mProfileMap = new HashMap<String, DConnectProfile>();

    /** 最後に処理されるプロファイル. */
    private DConnectProfile mDeliveryProfile;

    /** リクエスト管理クラス. */
    protected DConnectRequestManager mRequestManager;

    /** デバイスプラグイン管理. */
    protected DevicePluginManager mPluginMgr;

    /** DeviceConnectの設定. */
    protected DConnectSettings mSettings;
    
    /** ファイル管理用プロバイダ. */
    protected FileManager mFileMgr;

    /** Local OAuthのデータを管理するクラス. */
    private DConnectLocalOAuth mLocalOAuth;

    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            AndroidHandler handler = new AndroidHandler("dconnect.manager");
            handler.setFormatter(new SimpleFormatter());
            handler.setLevel(Level.ALL);
            sLogger.addHandler(handler);
            sLogger.setLevel(Level.ALL);
        } else {
            sLogger.setLevel(Level.OFF);
        }
        sLogger.entering(this.getClass().getName(), "onCreate");

        // イベント管理クラスの初期化
        EventManager.INSTANCE.setController(new DBCacheController(this));

        // DConnect設定
        mSettings = DConnectSettings.getInstance();
        mSettings.load(this);

        sLogger.info("Settings");
        sLogger.info("    SSL: " + mSettings.isSSL());
        sLogger.info("    Host: " + mSettings.getHost());
        sLogger.info("    Port: " + mSettings.getPort());
        sLogger.info("    LocalOAuth: " + mSettings.isUseALocalOAuth());

        // ファイル管理クラス
        mFileMgr = new FileManager(this);

        // Local OAuthの初期化
        LocalOAuth2Main.initialize(this);

        // デバイスプラグインとのLocal OAuth情報
        mLocalOAuth = new DConnectLocalOAuth(this);

        // リクエスト管理クラスの作成
        mRequestManager = new DConnectRequestManager();

        // デバイスプラグイン管理クラスの作成
        mPluginMgr = new DevicePluginManager(this, mDConnectDomain);
        mPluginMgr.setEventListener(this);
        mPluginMgr.createDevicePluginList();

        // プロファイルの追加
        addProfile(new AuthorizationProfile());
        addProfile(new DConnectNetworkServiceDiscoveryProfile(mPluginMgr));
        addProfile(new DConnectFilesProfile(this));
        addProfile(new DConnectSystemProfile(this, mPluginMgr));

        // dConnect Managerで処理せず、登録されたデバイスプラグインに処理させるプロファイル
        setDeliveryProfile(new DConnectDeliveryProfile(mPluginMgr, mLocalOAuth));

        sLogger.exiting(this.getClass().getName(), "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sLogger.entering(this.getClass().getName(), "onDestroy");
        // リクエストを削除
        mRequestManager.shutdown();
        // Local OAuthの後始末
        LocalOAuth2Main.destroy();
        sLogger.exiting(this.getClass().getName(), "onDestroy");
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        super.onStartCommand(intent, flags, startId);
        if (intent == null) {
            sLogger.warning("intent is null.");
            sLogger.exiting(this.getClass().getName(), "onStartCommand");
            return START_STICKY;
        }

        String action = intent.getAction();
        if (action == null) {
            sLogger.warning("action is null.");
            sLogger.exiting(this.getClass().getName(), "onStartCommand");
            return START_STICKY;
        }

        if (checkAction(action)) {
            onRequestReceive(intent);
        } else if (IntentDConnectMessage.ACTION_RESPONSE.equals(action)) {
            onResponseReceive(intent);
        } else if (IntentDConnectMessage.ACTION_EVENT.equals(action)) {
            onEventReceive(intent);
        } else if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
            mPluginMgr.checkAndAddDevicePlugin(intent);
        } else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
            mPluginMgr.checkAndRemoveDevicePlugin(intent);
        }

        return START_STICKY;
    }

    /**
     * リクエスト用Intentを受領したときの処理を行う.
     * @param request リクエスト用Intent
     */
    public void onRequestReceive(final Intent request) {
        // リクエストコードが定義されていない場合にはエラー
        int requestCode = request.getIntExtra(
                IntentDConnectMessage.EXTRA_REQUEST_CODE, ERROR_CODE);
        if (requestCode == ERROR_CODE) {
            sLogger.warning("Illegal requestCode in onRequestReceive. requestCode=" + requestCode);
            return;
        }

        // 不要になったキャッシュファイルの削除を行う
        if (mFileMgr != null) {
            mFileMgr.checkAndRemove();
        }

        // レスポンス用のIntentの用意
        Intent response = new Intent(IntentDConnectMessage.ACTION_RESPONSE);
        response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_ERROR);
        response.putExtra(DConnectMessage.EXTRA_REQUEST_CODE, requestCode);

        // プロファイル名の取得
        String profileName = request.getStringExtra(DConnectMessage.EXTRA_PROFILE);
        if (profileName == null) {
            MessageUtils.setNotSupportProfileError(response);
            sendResponse(request, response);
            return;
        }

        if (mSettings.isUseALocalOAuth()) {
            // アクセストークンの取得
            String accessToken = request.getStringExtra(AuthorizationProfile.PARAM_ACCESS_TOKEN);
            CheckAccessTokenResult result = LocalOAuth2Main.checkAccessToken(accessToken, profileName,
                    DConnectLocalOAuth.IGNORE_PROFILE);
            if (result.checkResult()) {
                executeRequest(request, response);
            } else {
                if (accessToken == null) {
                    MessageUtils.setEmptyAccessTokenError(response);
                } else if (!result.isExistAccessToken()) {
                    MessageUtils.setNotFoundClientId(response);
                } else if (!result.isExistClientId()) {
                    MessageUtils.setNotFoundClientId(response);
                } else if (!result.isExistScope()) {
                    MessageUtils.setScopeError(response);
                } else if (!result.isNotExpired()) {
                    MessageUtils.setExpiredAccessTokenError(response);
                } else {
                    MessageUtils.setAuthorizationError(response);
                }
                sendResponse(request, response);
            }
        } else {
            executeRequest(request, response);
        }
    }

    /**
     * レスポンス受信ハンドラー.
     * @param response レスポンス用Intent
     */
    public void onResponseReceive(final Intent response) {
        // リクエストコードが定義されていない場合にはエラー
        int requestCode = response.getIntExtra(
                IntentDConnectMessage.EXTRA_REQUEST_CODE, ERROR_CODE);
        if (requestCode == ERROR_CODE) {
            sLogger.warning("Illegal requestCode in onResponseReceive. requestCode=" + requestCode);
            return;
        }

        // レスポンスをリクエスト管理クラスに渡す
        mRequestManager.setResponse(response);
    }

    /**
     * イベントメッセージ受信ハンドラー.
     * @param event イベント用Intent
     */
    public void onEventReceive(final Intent event) {
        String sessionKey = event.getStringExtra(DConnectMessage.EXTRA_SESSION_KEY);
        String deviceId = event.getStringExtra(DConnectMessage.EXTRA_DEVICE_ID);
        String profile = event.getStringExtra(DConnectMessage.EXTRA_PROFILE);
        String inter = event.getStringExtra(DConnectMessage.EXTRA_INTERFACE);
        String attribute = event.getStringExtra(DConnectMessage.EXTRA_ATTRIBUTE);

        sLogger.fine("onEventReceive: [sessionKey: " + sessionKey + " deviceId: " + deviceId
                + " profile: " + profile + " inter: " + inter + " attribute: " + attribute + "]");

        if (sessionKey != null) {
            // セッションキーからreceiverを取得する
            String receiver = null;
            int index = sessionKey.indexOf(SEPARATOR_SESSION);
            if (index > 0) {
                receiver = sessionKey.substring(index + 1);
                sessionKey = sessionKey.substring(0, index);
            }
            // ここでセッションキーをデバイスプラグインIDを取得
            String pluginId = convertSessionKey2PluginId(sessionKey);
            String key = convertSessionKey2Key(sessionKey);
            DevicePlugin plugin = mPluginMgr.getDevicePlugin(pluginId);
            if (plugin == null) {
                sLogger.warning("plugin is null.");
                return;
            }
            String did = mPluginMgr.appendDeviceId(plugin, deviceId);
            event.putExtra(DConnectMessage.EXTRA_SESSION_KEY, key);

            // Local OAuthの仕様で、デバイスを発見するごとにclientIdを作成して、
            // アクセストークンを取得する作業を行う。
            if (NetworkServiceDiscoveryProfileConstants.PROFILE_NAME.equals(profile) 
                || NetworkServiceDiscoveryProfileConstants.ATTRIBUTE_ON_SERVICE_CHANGE.equals(attribute)) {

                // network service discoveryの場合には、networkServiceのオブジェクトの中にデータが含まれる
                Bundle service = (Bundle) event.getParcelableExtra(
                        NetworkServiceDiscoveryProfile.PARAM_NETWORK_SERVICE);
                String id = service.getString(NetworkServiceDiscoveryProfile.PARAM_ID);
                did = mPluginMgr.appendDeviceId(plugin, id);

                // デバイスIDを変更
                replaceDeviceId(event, plugin);

                OAuthData oauth = mLocalOAuth.getOAuthData(did);
                if (oauth == null && plugin != null) {
                    createClientOfDevicePlugin(plugin, did, event);
                } else {
                    // 送信先のセッションを取得
                    List<Event> evts = EventManager.INSTANCE.getEventList(profile, attribute);
                    for (int i = 0; i < evts.size(); i++) {
                        Event evt = evts.get(i);
                        event.putExtra(DConnectMessage.EXTRA_SESSION_KEY, evt.getSessionKey());
                        sendEvent(evt.getReceiverName(), event);
                    }
                }
            } else {
                replaceDeviceId(event, plugin);
                sendEvent(receiver, event);
            }
        } else {
            sLogger.warning("onEventReceive: sessionKey is null.");
        }
    }

    /**
     * リクエストを実行する.
     * @param request リクエスト
     * @param response レスポンス
     */
    private void executeRequest(final Intent request, final Intent response) {
        boolean send = false;
        String profileName = request.getStringExtra(DConnectMessage.EXTRA_PROFILE);
        DConnectProfile profile = getProfile(profileName);
        if (profile != null) {
            send = profile.onRequest(request, response);
        }
        if (!send) {
            sendDeliveryProfile(request, response);
        }
    }

    /**
     * セッションキーからプラグインIDに変換する.
     * 
     * 
     * @param sessionkey セッションキー
     * @return プラグインID
     */
    private String convertSessionKey2PluginId(final String sessionkey) {
        int index = sessionkey.lastIndexOf(SEPARATOR);
        if (index > 0) {
            return sessionkey.substring(index + 1);
        }
        return sessionkey;
    }

    /**
     * デバイスプラグインからのセッションキーから前半分のクライアントのセッションキーに変換する.
     * @param sessionkey セッションキー
     * @return クライアント用のセッションキー
     */
    private String convertSessionKey2Key(final String sessionkey) {
        int index = sessionkey.lastIndexOf(SEPARATOR);
        if (index > 0) {
            return sessionkey.substring(0, index);
        }
        return sessionkey;
    }

    /**
     * リクエストを追加する.
     * @param request 追加するリクエスト
     */
    public void addRequest(final DConnectRequest request) {
        mRequestManager.addRequest(request);
    }

    /**
     * DConnectLocalOAuthのインスタンスを取得する.
     * @return DConnectLocalOAuthのインスタンス
     */
    public DConnectLocalOAuth getLocalOAuth() {
        return mLocalOAuth;
    }

    @Override
    public List<DConnectProfile> getProfileList() {
        List<DConnectProfile> profileList = new ArrayList<DConnectProfile>(mProfileMap.values());
        return profileList;
    }

    @Override
    public void addProfile(final DConnectProfile profile) {
        if (profile != null) {
            profile.setContext(this);
            mProfileMap.put(profile.getProfileName(), profile);
        }
    }

    @Override
    public void removeProfile(final DConnectProfile profile) {
        if (profile != null) {
            mProfileMap.remove(profile.getProfileName());
        }
    }

    /**
     * プロファイル処理が行われなかったときに呼び出されるプロファイルを設定する.
     * @param profile プロファイル
     */
    public void setDeliveryProfile(final DConnectProfile profile) {
        if (profile != null) {
            profile.setContext(this);
            mDeliveryProfile = profile;
        }
    }

    /**
     * 指定したプロファイル名のDConnectProfileを取得する.
     * 指定したプロファイル名のDConnectProfileが存在しない場合にはnullを返却する。
     * @param name プロファイル名
     * @return DConnectProfileのインスタンス
     */
    public DConnectProfile getProfile(final String name) {
        return mProfileMap.get(name);
    }

    @Override
    public void onDeviceFound(final DevicePlugin plugin) {
        RegisterNetworkServiceDiscovery req = new RegisterNetworkServiceDiscovery();
        req.setContext(this);
        req.setSessionKey(plugin.getDeviceId());
        req.setDestination(plugin);
        req.setDevicePluginManager(mPluginMgr);
        addRequest(req);
    }

    @Override
    public void onDeviceLost(final DevicePlugin plugin) {
        mLocalOAuth.deleteOAuthDatas(plugin.getDeviceId());
    }

    /**
     * 指定されたアクションがdConnectのアクションをチェックする.
     * @param action アクション
     * @return dConnectのアクションの場合はtrue, それ以外はfalse
     */
    private boolean checkAction(final String action) {
        return (action.equals(IntentDConnectMessage.ACTION_GET) 
             || action.equals(IntentDConnectMessage.ACTION_PUT)
             || action.equals(IntentDConnectMessage.ACTION_POST) 
             || action.equals(IntentDConnectMessage.ACTION_DELETE));
    }

    /**
     * 各デバイスプラグインにリクエストを受け渡す.
     * 
     * ここで、アクセストークンをリクエストに付加する。
     * また、アクセストークンが存在しない場合には、デバイスプラグインにアクセストークンの取得要求を行う。
     * 
     * @param request リクエスト
     * @param response レスポンス
     */
    private void sendDeliveryProfile(final Intent request, final Intent response) {
        mDeliveryProfile.onRequest(request, response);
    }

    /**
     * イベント用メッセージのデバイスIDを置換する.
     * <br>
     * 
     * デバイスプラグインから送られてくるデバイスIDは、デバイスプラグインの中でIDになっている。
     * dConnect ManagerでデバイスプラグインのIDをデバイスIDに付加することでDNSっぽい動きを実現する。
     * 
     * @param event イベントメッセージ用Intent
     * @param plugin 送信元のデバイスプラグイン
     */
    private void replaceDeviceId(final Intent event, final DevicePlugin plugin) {
        String deviceId = event
                .getStringExtra(IntentDConnectMessage.EXTRA_DEVICE_ID);
        event.putExtra(IntentDConnectMessage.EXTRA_DEVICE_ID,
                mPluginMgr.appendDeviceId(plugin, deviceId));
    }

    /**
     * デバイスプラグインのクライアントを作成する.
     * @param plugin クライアントを作成するデバイスプラグイン
     * @param deviceId デバイスID
     * @param event 送信するイベント
     */
    private void createClientOfDevicePlugin(final DevicePlugin plugin, final String deviceId, final Intent event) {
        Intent intent = new Intent(IntentDConnectMessage.ACTION_GET);
        intent.setComponent(plugin.getComponentName());
        intent.putExtra(DConnectMessage.EXTRA_PROFILE,
                NetworkServiceDiscoveryProfileConstants.PROFILE_NAME);
        intent.putExtra(DConnectMessage.EXTRA_ATTRIBUTE,
                NetworkServiceDiscoveryProfileConstants.ATTRIBUTE_GET_NETWORK_SERVICES);
        intent.putExtra(DConnectMessage.EXTRA_DEVICE_ID, deviceId);

        DiscoveryDeviceRequest request = new DiscoveryDeviceRequest();
        request.setContext(this);
        request.setLocalOAuth(mLocalOAuth);
        request.setUseAccessToken(true);
        request.setDestination(plugin);
        request.setRequest(intent);
        request.setEvent(event);
        request.setDevicePluginManager(mPluginMgr);
        addRequest(request);
    }

    /**
     * レスポンス用のIntentを作成する.
     * @param request リクエスト
     * @param response リクエストに対応するレスポンス
     * @return 送信するレスポンス用Intent
     */
    protected Intent createResponseIntent(final Intent request, final Intent response) {
        int requestCode = request.getIntExtra(
                IntentDConnectMessage.EXTRA_REQUEST_CODE, ERROR_CODE);
        ComponentName cn = request
                .getParcelableExtra(IntentDConnectMessage.EXTRA_RECEIVER);

        Intent intent = new Intent(response);
        intent.putExtra(IntentDConnectMessage.EXTRA_REQUEST_CODE, requestCode);
        intent.setComponent(cn);
        return intent;
    }

    /**
     * リクエストの送信元にレスポンスを返却する.
     * @param request 送信元のリクエスト
     * @param response 返却するレスポンス
     */
    public void sendResponse(final Intent request, final Intent response) {
        sendBroadcast(createResponseIntent(request, response));
    }

    /**
     * イベントメッセージを送信する.
     * @param receiver 送信先のBroadcastReceiver
     * @param event 送信するイベントメッセージ
     */
    public void sendEvent(final String receiver, final Intent event) {
        sLogger.fine("★ sendEvent: " + receiver + " intent: " + event.getExtras());
        Intent targetIntent = new Intent(event);
        targetIntent.setComponent(ComponentName.unflattenFromString(receiver));
        sendBroadcast(targetIntent);
    }
}
