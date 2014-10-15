/*
 DConnectMessageService.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.deviceconnect.android.BuildConfig;
import org.deviceconnect.android.localoauth.CheckAccessTokenResult;
import org.deviceconnect.android.localoauth.LocalOAuth2Main;
import org.deviceconnect.android.profile.AuthorizationProfile;
import org.deviceconnect.android.profile.DConnectProfile;
import org.deviceconnect.android.profile.DConnectProfileProvider;
import org.deviceconnect.android.profile.NetworkServiceDiscoveryProfile;
import org.deviceconnect.android.profile.SystemProfile;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.message.intent.message.IntentDConnectMessage;
import org.deviceconnect.profile.AuthorizationProfileConstants;
import org.deviceconnect.profile.NetworkServiceDiscoveryProfileConstants;
import org.deviceconnect.profile.SystemProfileConstants;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

/**
 * Device Connectメッセージサービス.
 * 
 * <p>
 * Device Connectリクエストメッセージを受信し、Device Connectレスポンスメッセージを送信するサービスである。
 * {@link DConnectMessageServiceProvider}から呼び出されるサービスとし、UIレイヤーから明示的な呼び出しは行わない。
 * @author NTT DOCOMO, INC.
 */
public abstract class DConnectMessageService extends Service implements DConnectProfileProvider {
    
    /** 
     * LocalOAuthで無視するプロファイル群.
     */
    private static final String[] IGNORE_PROFILES = {
        AuthorizationProfileConstants.PROFILE_NAME,
        SystemProfileConstants.PROFILE_NAME,
        NetworkServiceDiscoveryProfileConstants.PROFILE_NAME
    };

    /**
     * ロガー.
     */
    private Logger mLogger = Logger.getLogger("org.deviceconnect.dplugin");

    /**
     * プロファイルインスタンスマップ.
     */
    private Map<String, DConnectProfile> mProfileMap = new HashMap<String, DConnectProfile>();

    /**
     * Local OAuth使用フラグ.
     * デフォルトではtrueにしておくこと。
     */
    private boolean mUseLocalOAuth = true;

    /**
     * SystemProfileを取得する.
     * SystemProfileは必須実装となるため、本メソッドでSystemProfileのインスタンスを渡すこと。
     * このメソッドで返却したSystemProfileは自動で登録される。
     * 
     * @return SystemProfileのインスタンス
     */
    protected abstract SystemProfile getSystemProfile();

    /**
     * NetworkServiceDiscoveryProfileを取得する.
     * NetworkServiceDiscoveryProfileは必須実装となるため
     * 本メソッドでNetworkServiceDiscoveryProfileのインスタンスを渡すこと。
     * このメソッドで返却したNetworkServiceDiscoveryProfileは自動で登録される。
     * 
     * @return NetworkServiceDiscoveryProfileのインスタンス
     */
    protected abstract NetworkServiceDiscoveryProfile getNetworkServiceDiscoveryProfile();

    @Override
    public void onCreate() {
        super.onCreate();

        // LocalOAuthの初期化
        LocalOAuth2Main.initialize(this);

        // 認証プロファイルの追加
        addProfile(new AuthorizationProfile());
        // 必須プロファイルの追加
        addProfile(getSystemProfile());
        addProfile(getNetworkServiceDiscoveryProfile());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // LocalOAuthの後始末
        LocalOAuth2Main.destroy();
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        super.onStartCommand(intent, flags, startId);

        if (intent == null) {
            return START_STICKY;
        }

        String action = intent.getAction();
        mLogger.fine("request action: " + action);
        if (action == null) {
            return START_STICKY;
        }

        Bundle resExtra = new Bundle();

        resExtra.putInt(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_ERROR);
        resExtra.putInt(DConnectMessage.EXTRA_REQUEST_CODE, intent.getIntExtra(DConnectMessage.EXTRA_REQUEST_CODE, -1));
        Intent response = MessageUtils.createResponseIntent(intent.getExtras(), resExtra);
        if (BuildConfig.DEBUG) {
            response.putExtra("debug", "DevicePlugin");
        }

        if (IntentDConnectMessage.ACTION_GET.equals(action) || IntentDConnectMessage.ACTION_POST.equals(action)
                || IntentDConnectMessage.ACTION_PUT.equals(action)
                || IntentDConnectMessage.ACTION_DELETE.equals(action)) {
            onRequest(intent, response);
        }

        return START_STICKY;
    }

    /**
     * 受信したリクエストをプロファイルに振り分ける.
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     */
    protected void onRequest(final Intent request, final Intent response) {

        mLogger.fine("request: " + request);
        mLogger.fine("request extras: " + request.getExtras());

        // プロファイル名の取得
        String profileName = request.getStringExtra(DConnectMessage.EXTRA_PROFILE);
        if (profileName == null) {
            MessageUtils.setNotSupportProfileError(response);
            sendResponse(response);
            return;
        }

        // プロファイルを取得する
        DConnectProfile profile = getProfile(profileName);
        if (profile == null) {
            MessageUtils.setNotSupportProfileError(response);
            sendResponse(response);
            return;
        }

        // 各プロファイルでリクエストを処理する
        boolean send = true;
        if (isUseLocalOAuth()) {
            // アクセストークン
            String accessToken = request.getStringExtra(AuthorizationProfile.PARAM_ACCESS_TOKEN);
            // LocalOAuth処理
            CheckAccessTokenResult result = LocalOAuth2Main.checkAccessToken(accessToken, profileName,
                    IGNORE_PROFILES);
            if (result.checkResult()) {
                send = profile.onRequest(request, response);
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
            }
        } else {
            send = profile.onRequest(request, response);
        }

        if (send) {
            // send broad cast
            mLogger.fine("send broadcast: " + response);
            mLogger.fine("send broadcast extra: " + response.getExtras());
            sendResponse(response);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DConnectProfile> getProfileList() {
        List<DConnectProfile> profileList = new ArrayList<DConnectProfile>(mProfileMap.values());
        return profileList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DConnectProfile getProfile(final String name) {
        DConnectProfile profile = mProfileMap.get(name);
        return profile;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addProfile(final DConnectProfile profile) {
        profile.setContext(this);
        mProfileMap.put(profile.getProfileName(), profile);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeProfile(final DConnectProfile profile) {
        mProfileMap.remove(profile.getProfileName());
    }

    /**
     * コンテキストの取得する.
     * 
     * @return コンテキスト
     */
    public final Context getContext() {
        return this;
    }

    /**
     * Device Connect Managerにレスポンスを返却するためのメソッド.
     * @param response レスポンス
     * @return 送信成功の場合true、それ以外はfalse
     */
    public final boolean sendResponse(final Intent response) {
        // TODO チェックが必要な追加すること。
        if (response == null) {
            throw new IllegalArgumentException("response is null.");
        }
        getContext().sendBroadcast(response);
        return true;
    }

    /**
     * Device Connectにイベントを送信する.
     * 
     * @param event イベントパラメータ
     * @param accessToken 送り先のアクセストークン
     * @return 送信成功の場合true、アクセストークンエラーの場合はfalseを返す。
     */
    public final boolean sendEvent(final Intent event, final String accessToken) {

        // TODO 返り値をもっと詳細なものにするか要検討
        if (event == null) {
            throw new IllegalArgumentException("Event is null.");
        }

        if (isUseLocalOAuth()) {
            CheckAccessTokenResult result = LocalOAuth2Main.checkAccessToken(accessToken,
                    event.getStringExtra(DConnectMessage.EXTRA_PROFILE), IGNORE_PROFILES);
            if (!result.checkResult()) {
                return false;
            }
        }

        getContext().sendBroadcast(event);
        return true;
    }

    /**
     * Local OAuth使用フラグを設定する.
     * 
     * このフラグをfalseに設定することで、LocalOAuthの機能をOFFにすることができる。
     * デフォルトでは、trueになっているので、LocalOAuthが有効になっている。
     * 
     * @param use フラグ
     */
    protected void setUseLocalOAuth(final boolean use) {
        mUseLocalOAuth = use;
    }

    /**
     * Local OAuth使用フラグを取得する.
     * 
     * @return 使用する場合にはtrue、それ以外はfalse
     */
    public boolean isUseLocalOAuth() {
        return mUseLocalOAuth;
    }
}
