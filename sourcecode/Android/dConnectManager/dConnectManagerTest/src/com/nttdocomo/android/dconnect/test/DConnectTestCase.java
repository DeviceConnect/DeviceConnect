/*
 DConnectTestCase.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.logging.Logger;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.test.InstrumentationTestCase;

import com.nttdocomo.android.dconnect.cipher.signature.AuthSignature;
import com.nttdocomo.android.dconnect.test.plugin.profile.TestNetworkServiceDiscoveryProfileConstants;
import com.nttdocomo.dconnect.profile.AuthorizationProfileConstants;
import com.nttdocomo.dconnect.profile.BatteryProfileConstants;
import com.nttdocomo.dconnect.profile.ConnectProfileConstants;
import com.nttdocomo.dconnect.profile.DeviceOrientationProfileConstants;
import com.nttdocomo.dconnect.profile.FileDescriptorProfileConstants;
import com.nttdocomo.dconnect.profile.FileProfileConstants;
import com.nttdocomo.dconnect.profile.MediaPlayerProfileConstants;
import com.nttdocomo.dconnect.profile.MediaStreamRecordingProfileConstants;
import com.nttdocomo.dconnect.profile.NetworkServiceDiscoveryProfileConstants;
import com.nttdocomo.dconnect.profile.NotificationProfileConstants;
import com.nttdocomo.dconnect.profile.PhoneProfileConstants;
import com.nttdocomo.dconnect.profile.ProximityProfileConstants;
import com.nttdocomo.dconnect.profile.SettingsProfileConstants;
import com.nttdocomo.dconnect.profile.SystemProfileConstants;
import com.nttdocomo.dconnect.profile.VibrationProfileConstants;

/**
 * dConnectのTestCaseのスーパークラス.
 * @author NTT DOCOMO, INC.
 */
public abstract class DConnectTestCase extends InstrumentationTestCase {

    /** d-ConnectManagerへのURI. */
    protected static final String DCONNECT_MANAGER_URI = "http://localhost:4035/gotapi";

    /**
     * プロファイル一覧.
     * <p>
     * OAuth処理でのスコープの指定に使用する.
     * </p>
     */
    protected static final String[] PROFILES = {
            BatteryProfileConstants.PROFILE_NAME,
            ConnectProfileConstants.PROFILE_NAME,
            DeviceOrientationProfileConstants.PROFILE_NAME,
            FileDescriptorProfileConstants.PROFILE_NAME,
            FileProfileConstants.PROFILE_NAME,
            MediaStreamRecordingProfileConstants.PROFILE_NAME,
            MediaPlayerProfileConstants.PROFILE_NAME,
            NetworkServiceDiscoveryProfileConstants.PROFILE_NAME,
            NotificationProfileConstants.PROFILE_NAME,
            PhoneProfileConstants.PROFILE_NAME,
            ProximityProfileConstants.PROFILE_NAME,
            SettingsProfileConstants.PROFILE_NAME,
            SystemProfileConstants.PROFILE_NAME,
            VibrationProfileConstants.PROFILE_NAME,
            "files",
            "unique",
            "json_test",
            "abc" // 実際には実装しないプロファイル
    };

    /** テスト用Action: RESPONSE. */
    public static final String TEST_ACTION_RESPONSE
            = "com.nttdocomo.android.dconnect.test.intent.action.RESPONSE";

    /** テスト用Action: EVENT. */
    public static final String TEST_ACTION_EVENT
            = "com.nttdocomo.android.dconnect.test.intent.action.EVENT";

    /** テスト用プラグインID. */
    public static final String TEST_PLUGIN_ID = "test_plugin_id";

    /** テスト用セッションID. */
    public static final String TEST_SESSION_KEY = "test_session_key";

    /** OAUTH認証情報を保存するファイル名. */
    private static final String FILE_NAME_OAUTH = "oauth.db";

    /** クライアントIDのキー. */
    private static final String KEY_CLIENT_ID = "clientId";

    /** クライアントシークレットのキー. */
    private static final String KEY_CLIENT_SECRET = "clientSecret";

    /** アクセストークンのキー. */
    private static final String KEY_ACCESS_TOKEN = "accessToken";

//    /** dConnectManagerの起動を待つ時間を定義. */
//    private static final int TIME_WAIT_FOR_DCONNECT = 1000;

    /** バッファサイズ. */
    private static final int BUF_SIZE = 8192;

    /** デバイス一覧. */
    private List<DeviceInfo> mDevices;

    /** プラグイン一覧. */
    private List<PluginInfo> mPlugins;

    /** ロガー. */
    protected final Logger sLogger = Logger.getLogger("dconnect.manager");

    /** クライアントID. */
    protected String mClientId;

    /** クライアントシークレット. */
    protected String mClientSecret;

    /** アクセストークン. */
    protected String mAccessToken;

    /**
     * コンストラクタ.
     * @param string テストタグ
     */
    public DConnectTestCase(final String string) {
        setName(string);
    }

    /**
     * ApplicationContext を取得します.
     * @return コンテキスト
     */
    protected Context getApplicationContext() {
        return getInstrumentation()
                .getTargetContext().getApplicationContext();
    }

    /**
     * JUnitプロジェクトのコンテキストを取得する.
     * @return コンテキスト
     */
    protected Context getContext() {
        return getInstrumentation().getContext();
    }

    /**
     * dConnectManagerに対してクライアント作成リクエストを送信する.
     * <p>
     * レスポンスとしてクライアントIDまたはクライアントシークレットのいずれかを受信できなかった場合はnullを返すこと.
     * </p>
     * 
     * @param packageName パッケージ名
     * @return クライアントIDおよびクライアントシークレットを格納した配列
     */
    protected abstract String[] createClient(String packageName);

    /**
     * dConnectManagerに対してアクセストークン取得リクエストを送信する.
     * <p>
     * レスポンスとしてアクセストークンを受信できなかった場合はnullを返すこと.
     * </p>
     * 
     * @param clientId クライアントID
     * @param clientSecret クライアントシークレット
     * @param scopes スコープ指定
     * @return アクセストークン
     */
    protected abstract String requestAccessToken(String clientId, String clientSecret,
            String[] scopes);

    /**
     * dConnectManagerから最新のデバイス一覧を取得する.
     * @return dConnectManagerから取得した最新のデバイス一覧
     */
    protected abstract List<DeviceInfo> searchDevices();

    /**
     * dConnectManagerから最新のプラグイン一覧を取得する.
     * @return dConnectManagerから取得した最新のプラグイン一覧
     */
    protected abstract List<PluginInfo> searchPlugins();

    /**
     * クライアントIDのオンメモリ上のキャッシュを取得する.
     * 
     * @return クライアントIDのオンメモリ上のキャッシュ
     */
    protected String getClientId() {
        return mClientId;
    }

    /**
     * クライアントシークレットのオンメモリ上のキャッシュを取得する.
     * 
     * @return クライアントシークレットのオンメモリ上のキャッシュ
     */
    protected String getClientSecret() {
        return mClientSecret;
    }

    /**
     * アクセストークンのオンメモリ上のキャッシュを取得する.
     * 
     * @return アクセストークンのオンメモリ上のキャッシュ
     */
    protected String getAccessToken() {
        return mAccessToken;
    }

    /**
     * クライアントIDのキャッシュを取得する.
     * 
     * @return クライアントIDのキャッシュ
     */
    protected String getClientIdCache() {
        SharedPreferences pref = getContext().getSharedPreferences(FILE_NAME_OAUTH, Context.MODE_PRIVATE);
        return pref.getString(KEY_CLIENT_ID, null);
    }

    /**
     * クライアントシークレットのキャッシュを取得する.
     * 
     * @return クライアントシークレットのキャッシュ
     */
    protected String getClientSecretCache() {
        SharedPreferences pref = getContext().getSharedPreferences(FILE_NAME_OAUTH, Context.MODE_PRIVATE);
        return pref.getString(KEY_CLIENT_SECRET, null);
    }

    /**
     * アクセストークンのキャッシュを取得する.
     * 
     * @return アクセストークンのキャッシュ
     */
    protected String getAccessTokenCache() {
        SharedPreferences pref = getContext().getSharedPreferences(FILE_NAME_OAUTH, Context.MODE_PRIVATE);
        return pref.getString(KEY_ACCESS_TOKEN, null);
    }

    /**
     * 認証情報をキャッシュする.
     * 
     * @param clientId クライアントID
     * @param clientSecret クライアントシークレット
     * @param accessToken アクセストークン
     */
    protected void storeOAuthInfo(final String clientId, final String clientSecret, final String accessToken) {
        SharedPreferences pref = getContext().getSharedPreferences(FILE_NAME_OAUTH, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_CLIENT_ID, clientId);
        editor.putString(KEY_CLIENT_SECRET, clientSecret);
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        boolean edited = editor.commit();
        if (!edited) {
            fail("Failed to store oauth info: clientId, cliendSecret, accessToken");
        }
    }

    /**
     * テストの前に実行される.
     * @exception Exception 設定に失敗した場合に発生
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        checkDConnectService();
        if (isLocalOAuth()) {
            mClientId = getClientIdCache();
            mClientSecret = getClientSecretCache();
            mAccessToken = getAccessTokenCache();
            // クライアントID、クライアントシークレット取得
            if (mClientId == null || mClientSecret == null) {
                String[] client = createClient(getClientPackageName());
                assertNotNull(client);
                assertNotNull(client[0]);
                assertNotNull(client[1]);
                mClientId = client[0];
                mClientSecret = client[1];
            }
            // アクセストークン取得
            if (mAccessToken == null) {
                mAccessToken = requestAccessToken(mClientId, mClientSecret, PROFILES);
                assertNotNull(mAccessToken);
            }
            // 認証情報をキャッシュする
            storeOAuthInfo(mClientId, mClientSecret, mAccessToken);
        }
        if (isSearchDevices()) {
            // テストデバイスプラグインを探す
            setDevices(searchDevices());
            setPlugins(searchPlugins());
        }
    }

    @Override
    protected void tearDown() throws Exception {
        mClientId = null;
        mClientSecret = null;
        mAccessToken = null;
        super.tearDown();
    }

    /**
     * OAuthクライアントとしてのパッケージ名を取得する.
     * 
     * @return パッケージ名
     */
    protected String getClientPackageName() {
        return getContext().getPackageName();
    }

    /**
     * accessTokenをリクエストするためのシグネイチャを作成する.
     * @param clientId クライアントID
     * @param scopes スコープ
     * @param clientSecret クライアントシークレット
     * @return シグネイチャ
     */
    protected String createSignature(final String clientId, final String[] scopes, final String clientSecret) {
        String signature = null;
        try {
            signature = AuthSignature.generateSignature(clientId,
                    AuthorizationProfileConstants.GrantType.AUTHORIZATION_CODE.getValue(), 
                    null, scopes, clientSecret);
            signature = URLEncoder.encode(signature, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
        return signature;
    }

    /**
     * 各テストメソッド実行前に、LocalOAuth認証を行うかどうかの設定を取得する.
     * @return テスト実行前にLocalOAuth認証を行う場合はtrue、そうでない場合はfalse
     */
    protected boolean isLocalOAuth() {
        return true;
    }

    /**
     * 各テストメソッド実行前に、デバイス一覧取得を行うかどうかの設定を取得する.
     * @return テスト実行前にデバイス一覧取得を行う場合はtrue、そうでない場合はfalse
     */
    protected boolean isSearchDevices() {
        return true;
    }

    /**
     * dConnect Managerが起動しているかどうかのチェックを行う.
     * もしもdConnect Managerが動作していない場合には起動を行い、しばらく待つ。
     */
    protected void checkDConnectService() {
        // DConnectServiceが起動していない場合
//        if (!isRunningDConnectService()) {
//            Intent intent = new Intent();
//            intent.setClass(getApplicationContext(), DConnectService.class);
//            getApplicationContext().startService(intent);
//            // DConnectServiceの起動を少し待つ
//            try {
//                Thread.sleep(TIME_WAIT_FOR_DCONNECT);
//            } catch (InterruptedException e) {
//                return;
//            }
//        }
    }

//    /**
//     * dConnectManagerが動作しているか確認を行う.
//     * @return 動作している場合はtrue、それ以外はfalse
//     */
//    private boolean isRunningDConnectService() {
//        return isRunningService(
//                "com.nttdocomo.android.dconnect.DConnectService");
//    }

//    /**
//     * 指定された名前のサービスが動作しているか確認を行う.
//     * @param serviceName サービス名
//     * @return 動作している場合はtrue、それ以外はfalse
//     */
//    private boolean isRunningService(final String serviceName) {
//        ActivityManager mgr = (ActivityManager) getApplicationContext()
//                .getSystemService(Context.ACTIVITY_SERVICE);
//        List<RunningServiceInfo> services = mgr
//                .getRunningServices(Integer.MAX_VALUE);
//        for (RunningServiceInfo info : services) {
//            if (serviceName.equals(info.service.getClassName())) {
//                return true;
//            }
//        }
//        return false;
//    }

    /**
     * デバイスIDを取得する.
     * @return デバイスID
     */
    protected String getDeviceId() {
        return getDeviceIdByName(TestNetworkServiceDiscoveryProfileConstants.DEVICE_NAME);
    }

    /**
     * 指定したデバイス名をもつデバイスのIDを取得する.
     * @param deviceName デバイス名
     * @return デバイスID
     */
    protected String getDeviceIdByName(final String deviceName) {
        for (int i = 0; i < mDevices.size(); i++) {
            DeviceInfo obj = mDevices.get(i);
            if (deviceName.equals(obj.getDeviceName())) {
                return obj.getDeviceId();
            }
        }
        return null;
    }

    /**
     * 指定したプラグイン名をもつプラグインIDを取得する.
     * @param pluginName プラグイン名
     * @return プラグインID
     */
    protected String getPluginIdByName(final String pluginName) {
        for (int i = 0; i < mPlugins.size(); i++) {
            PluginInfo obj = mPlugins.get(i);
            if (pluginName.equals(obj.getName())) {
                return obj.getId();
            }
        }
        return null;
    }

    /**
     * デバイス一覧をキャッシュする.
     * @param services デバイス一覧
     */
    protected void setDevices(final List<DeviceInfo> services) {
        this.mDevices = services;
    }

    /**
     * プラグイン一覧をキャッシュする.
     * @param plugins プラグイン一覧
     */
    protected void setPlugins(final List<PluginInfo> plugins) {
        this.mPlugins = plugins;
    }

    /**
     * assetsにあるファイルデータを取得する.
     * @param name ファイル名
     * @return ファイルデータ
     */
    protected byte[] getBytesFromAssets(final String name) {
        AssetManager mgr = getApplicationContext().getResources().getAssets();
        InputStream in = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[BUF_SIZE];
        int len;
        try {
            in = mgr.open(name);
            while ((len = in.read(buf)) > 0) {
                baos.write(buf, 0, len);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    sLogger.warning("Exception occured in close method.");
                }
            }
        }
    }

    /**
     * デバイス情報.
     */
    protected static class DeviceInfo {

        /**
         * デバイスID.
         */
        private final String mDeviceId;

        /**
         * デバイス名.
         */
        private final String mDeviceName;

        /**
         * コンストラクタ.
         * @param deviceId デバイスID
         * @param deviceName デバイス名
         */
        public DeviceInfo(final String deviceId, final String deviceName) {
            this.mDeviceId = deviceId;
            this.mDeviceName = deviceName;
        }

        /**
         * デバイスIDを取得する.
         * 
         * @return デバイスID
         */
        public String getDeviceId() {
            return mDeviceId;
        }

        /**
         * デバイス名を取得する.
         * 
         * @return デバイス名
         */
        public String getDeviceName() {
            return mDeviceName;
        }
    }

    /**
     * プラグイン情報.
     */
    protected static class PluginInfo {

        /**
         * プラグインID.
         */
        private final String mId;

        /**
         * プラグイン名.
         */
        private final String mName;

        /**
         * コンストラクタ.
         * @param id プラグインID
         * @param name プラグイン名
         */
        public PluginInfo(final String id, final String name) {
            this.mId = id;
            this.mName = name;
        }

        /**
         * プラグインIDを取得する.
         * 
         * @return プラグインID
         */
        public String getId() {
            return mId;
        }

        /**
         * プラグイン名を取得する.
         * 
         * @return プラグイン名
         */
        public String getName() {
            return mName;
        }
    }
}
