/*
 LocalOAuthRequest.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.manager.request;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.deviceconnect.android.localoauth.LocalOAuth2Main;
import org.deviceconnect.android.localoauth.exception.AuthorizatonException;
import org.deviceconnect.android.manager.DConnectLocalOAuth;
import org.deviceconnect.android.manager.DConnectLocalOAuth.OAuthData;
import org.deviceconnect.android.manager.DevicePlugin;
import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.message.intent.message.IntentDConnectMessage;
import org.deviceconnect.profile.AuthorizationProfileConstants;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * LocalOAuthを行うためのリクエスト.
 * @author NTT DOCOMO, INC.
 */
public class LocalOAuthRequest extends DConnectRequest {
    /** リトライ回数の最大値を定義. */
    protected static final int MAX_RETRY_COUNT = 3;

    /** ロガー. */
    private final Logger sLogger = Logger.getLogger("dconnect.manager");

    /** 送信先のデバイスプラグイン. */
    protected DevicePlugin mDevicePlugin;

    /** Local OAuthを使用するクラス. */
    protected DConnectLocalOAuth mLocalOAuth;

    /** ロックオブジェクト. */
    protected final Object mLockObj = new Object();

    /** リクエストコード. */
    protected int mRequestCode;

    /** アクセストークンの使用フラグ. */
    protected boolean mUseAccessToken;

    /** リトライ回数. */
    protected int retryCount;

    /**
     * 送信先のデバイスプラグインを設定する.
     * @param plugin デバイスプラグイン
     */
    public void setDestination(final DevicePlugin plugin) {
        mDevicePlugin = plugin;
    }

    /**
     * Local OAuth管理クラスを設定する.
     * @param auth Local OAuth管理クラス
     */
    public void setLocalOAuth(final DConnectLocalOAuth auth) {
        mLocalOAuth = auth;
    }

    /**
     * アクセストークンの使用フラグを設定する.
     * @param useAccessToken 使用する場合はtrue、それ以外はfalse
     */
    public void setUseAccessToken(final boolean useAccessToken) {
        mUseAccessToken = useAccessToken;
    }

    /**
     * アクセストークンの使用フラグを取得する.
     * @return アクセストークンを使用する場合はtrue、それ以外はfalse
     */
    public boolean isUseAccessToken() {
        return mUseAccessToken;
    }

    @Override
    public void setResponse(final Intent response) {
        super.setResponse(response);
        synchronized (mLockObj) {
            mLockObj.notifyAll();
        }
    }

    @Override
    public boolean hasRequestCode(final int requestCode) {
        return mRequestCode == requestCode;
    }

    @Override
    public void run() {
        if (mRequest == null) {
            throw new RuntimeException("mRequest is null.");
        }

        if (mDevicePlugin == null) {
            throw new RuntimeException("mDevicePlugin is null.");
        }

        // リトライ回数を定義
        retryCount = 0;

        // リクエストコードを作成する
        mRequestCode = UUID.randomUUID().hashCode();

        // 実行
        executeRequest();
    }

    /**
     * クライアントの作成をデバイスプラグインに要求する.
     * 
     * 結果が返ってくるまで、この関数は返り値を返却しない。
     * 返り値がnullの場合には、クライアントの作成に失敗している。
     * 
     * [実装要求]
     * null(エラー)を返す場合には、リクエスト元にレスポンスを返却するので注意が必要。
     * 
     * @param deviceId デバイスID
     * @return クライアントデータ
     */
    protected ClientData executeCreateClient(final String deviceId) {
        // 命令を実行する前にレスポンスを初期化しておく
        mResponse = null;

        sLogger.info("executeCreateClient: " + deviceId);

        // 各デバイスに送信するリクエストを作成
        Intent request = createRequestMessage(mRequest, mDevicePlugin);
        request.setAction(IntentDConnectMessage.ACTION_GET);
        request.setComponent(mDevicePlugin.getComponentName());
        request.putExtra(IntentDConnectMessage.EXTRA_REQUEST_CODE, mRequestCode);
        request.putExtra(DConnectMessage.EXTRA_PROFILE, AuthorizationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, AuthorizationProfileConstants.ATTRIBUTE_CREATE_CLIENT);
        request.putExtra(AuthorizationProfileConstants.PARAM_PACKAGE, getContext().getPackageName());

        // デバイスプラグインに送信
        mContext.sendBroadcast(request);

        if (mResponse == null) {
            // 各デバイスのレスポンスを待つ
            waitForResponse();
        }

        // レスポンスを返却する
        if (mResponse != null) {
            int result = getResult(mResponse);
            if (result == DConnectMessage.RESULT_OK) {
                String clientId = mResponse.getStringExtra(AuthorizationProfileConstants.PARAM_CLIENT_ID);
                String clientSecret = mResponse.getStringExtra(AuthorizationProfileConstants.PARAM_CLIENT_SECRET);
                if (clientId == null || clientSecret == null) {
                    // クライアントの作成にエラー
                    sendCannotCreateClient();
                } else {
                    // クライアントデータを
                    ClientData client = new ClientData();
                    client.clientId = clientId;
                    client.clientSecret = clientSecret;
                    return client;
                }
            } else {
                int errorCode = getErrorCode(mResponse);
                if (errorCode == DConnectMessage.ErrorCode.NOT_SUPPORT_PROFILE.getCode()) {
                    // authorizationプロファイルに対応していないのでアクセストークンはいらない。
                    sLogger.info("DevicePlugin not support Authorization Profile.");
                    executeRequest(null);
                } else {
                    sendResponse(mResponse);
                }
            }
        } else {
            sendTimeout();
        }
        return null;
    }

    /**
     * アクセストークンの取得要求をデバイスプラグインに対して行う.
     * 
     * 結果が返ってくるまで、この関数は返り値を返却しない。
     * アクセストークンの取得に失敗した場合にはnullを返却する。
     * 
     * [実装要求]
     * null(エラー)を返す場合には、リクエスト元にレスポンスを返却するので注意が必要。
     * 
     * @param deviceId デバイスID
     * @param clientId クライアントID
     * @param clientSecret クライアントシークレット
     * @return アクセストークン
     */
    protected String executeAccessToken(final String deviceId, final String clientId, final String clientSecret) {
        // 命令を実行する前にレスポンスを初期化しておく
        mResponse = null;

        sLogger.info("executeAccessToken: {deviceId: " + deviceId + ", clientId: " + clientId
                + ", clientSecret: " + clientSecret + "}");

        // 各デバイスに送信するリクエストを作成
        Intent request = createRequestMessage(mRequest, mDevicePlugin);
        request.setAction(IntentDConnectMessage.ACTION_GET);
        request.setComponent(mDevicePlugin.getComponentName());
        request.putExtra(IntentDConnectMessage.EXTRA_REQUEST_CODE, mRequestCode);
        request.putExtra(DConnectMessage.EXTRA_PROFILE, AuthorizationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, AuthorizationProfileConstants.ATTRIBUTE_REQUEST_ACCESS_TOKEN);
        request.putExtra(AuthorizationProfileConstants.PARAM_CLIENT_ID, clientId);
        request.putExtra(AuthorizationProfileConstants.PARAM_GRANT_TYPE,
                AuthorizationProfileConstants.GrantType.AUTHORIZATION_CODE.getValue());
        request.putExtra(AuthorizationProfileConstants.PARAM_APPLICATION_NAME, getApplicationName());
        request.putExtra(AuthorizationProfileConstants.PARAM_SCOPE, combineStr(getScope()));

        // シグネイチャ作成
        String signature = createSignature(clientId, clientSecret,
                AuthorizationProfileConstants.GrantType.AUTHORIZATION_CODE.getValue(), 
                request.getStringExtra(DConnectMessage.EXTRA_DEVICE_ID), getScope());
        if (signature != null) {
            request.putExtra(AuthorizationProfileConstants.PARAM_SIGNATURE, signature);
        } else {
            // シグネイチャの作成に失敗
            sendCannotCreateSignature();
            return null;
        }

        // トークン取得を行う
        mContext.sendBroadcast(request);

        if (mResponse == null) {
            // 各デバイスのレスポンスを待つ
            waitForResponse();
        }

        // レスポンスを返却する
        if (mResponse != null) {
            int result = getResult(mResponse);
            if (result == DConnectMessage.RESULT_OK) {
                String accessToken = mResponse.getStringExtra(DConnectMessage.EXTRA_ACCESS_TOKEN);
                if (accessToken == null) {
                    sendCannotCreateAccessToken();
                } else {
                    return accessToken;
                }
            } else {
                // 認証エラーで、有効期限切れ・スコープ範囲外以外はClientIdを作り直す処理を入れる
                int errorCode = getErrorCode(mResponse);
                if (errorCode == DConnectMessage.ErrorCode.NOT_FOUND_CLIENT_ID.getCode() 
                        || errorCode == DConnectMessage.ErrorCode.AUTHORIZATION.getCode()) {
                    mLocalOAuth.deleteOAuthData(deviceId);
                }
                sendResponse(mResponse);
            }
        } else {
            sendTimeout();
        }
        return null;
    }

    /**
     * 実際の命令を行う.
     * @param accessToken アクセストークン
     */
    protected void executeRequest(final String accessToken) {
    }

    /**
     * resultの値をレスポンスのIntentから取得する.
     * @param response レスポンスのIntent
     * @return resultの値
     */
    protected int getResult(final Intent response) {
        int result = mResponse.getIntExtra(DConnectMessage.EXTRA_RESULT,
                DConnectMessage.RESULT_ERROR);
        return result;
    }

    /**
     * errorCodeの値をレスポンスのIntentから取得する.
     * @param response レスポンスのIntent
     * @return errorCodeの値
     */
    protected int getErrorCode(final Intent response) {
        int errorCode = mResponse.getIntExtra(DConnectMessage.EXTRA_ERROR_CODE,
                DConnectMessage.ErrorCode.UNKNOWN.getCode());
        return errorCode;
    }

    /**
     * 各デバイスからのレスポンスを待つ.
     * 
     * この関数から返答があるのは以下の条件になる。
     * <ul>
     * <li>デバイスプラグインからレスポンスがあった場合
     * <li>指定された時間無いにレスポンスが返ってこない場合
     * </ul>
     */
    protected void waitForResponse() {
        synchronized (mLockObj) {
            try {
                mLockObj.wait(mTimeout);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    /**
     * Local OAuthの有効期限切れの場合にリトライを行う.
     */
    protected void executeRequest() {
        String deviceId = mRequest.getStringExtra(DConnectMessage.EXTRA_DEVICE_ID);

        if (mUseAccessToken) {
            String accessToken = getAccessToken(deviceId);
            if (accessToken != null) {
                executeRequest(accessToken);
            } else {
                // 認証を行うリクエスト
                final OAuthRequest request = new OAuthRequest() {
                    @Override
                    public void onFinishAuth(final String accessToken) {
                        synchronized (this) {
                            this.notifyAll();
                        }
                    }
                };
                request.setContext(getContext());
                request.setDeviceId(deviceId);
                // OAuthの認証だけは、シングルスレッドで動作させないとおかしな挙動が発生
                mRequestMgr.addRequestOnSingleThread(request);

                synchronized (request) {
                    try {
                        request.wait(mTimeout);
                    } catch (InterruptedException e) {
                        sLogger.warning("timeout.");
                    }
                }

                accessToken = getAccessToken(deviceId);
                if (accessToken != null) {
                    executeRequest(accessToken);
                }
            }
        } else {
            executeRequest(null);
        }
    }

    /**
     * 指定されたデバイスIDに対応するアクセストークンを取得する.
     * アクセストークンが存在しない場合にはnullを返却する。
     * @param deviceId デバイスID
     * @return アクセストークン
     */
    private String getAccessToken(final String deviceId) {
        OAuthData oauth = mLocalOAuth.getOAuthData(deviceId);
        if (oauth != null) {
            return mLocalOAuth.getAccessToken(oauth.getId());
        }
        return null;
    }
    /**
     * AndroidManifest.xmlのアプリを取得する.
     * 
     * @return アプリ名
     */
    private String getApplicationName() {
        PackageManager pkgMgr = getContext().getPackageManager();
        try {
            PackageInfo packageInfo = pkgMgr.getPackageInfo(getContext().getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            return packageInfo.applicationInfo.loadLabel(pkgMgr).toString();
        } catch (NameNotFoundException e) {
            return "Unknown";
        }
    }

    /**
     * アクセストークン取得用のシグネイチャを作成する.
     * 
     * 作成に失敗した場合にはnullを返却する.
     * 
     * @param clientId クライアントID
     * @param clientSecret クライアントシークレット
     * @param grantType グラントタイプ
     * @param deviceId デバイスID
     * @param scopes スコープ
     * @return 作成されたシグネイチャ
     */
    private String createSignature(final String clientId, final String clientSecret, final String grantType,
            final String deviceId, final String[] scopes) {
        try {
            return LocalOAuth2Main.createSignature(clientId, grantType, deviceId,
                    scopes, clientSecret);
        } catch (AuthorizatonException e) {
            return null;
        }
    }

    /**
     * スコープを一つの文字列に連結する.
     * @param scopes スコープ一覧
     * @return 連結された文字列
     */
    private String combineStr(final String[] scopes) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < scopes.length; i++) {
            if (i > 0) {
                builder.append(",");
            }
            builder.append(scopes[i].trim());
        }
        return builder.toString();
    }

    /**
     * デバイスプラグインでサポートするプロファイルの一覧を取得する.
     * @return プロファイルの一覧
     */
    private String[] getScope() {
        List<String> list = mDevicePlugin.getSupportProfiles();
        return list.toArray(new String[list.size()]);
    }

    /**
     * シグネイチャの作成に失敗した場合のレスポンスを返却する.
     */
    private void sendCannotCreateSignature() {
        Intent response = new Intent(IntentDConnectMessage.ACTION_RESPONSE);
        MessageUtils.setAuthorizationError(response, "Cannot create signature.");
        sendResponse(response);
    }
    /**
     * クライアントの作成に失敗した場合のレスポンスを返却する.
     */
    private void sendCannotCreateClient() {
        Intent response = new Intent(IntentDConnectMessage.ACTION_RESPONSE);
        MessageUtils.setAuthorizationError(response, "Cannot create client data.");
        sendResponse(response);
    }
    /**
     * アクセストークンの作成に失敗した場合のレスポンスを返却する.
     */
    private void sendCannotCreateAccessToken() {
        Intent response = new Intent(IntentDConnectMessage.ACTION_RESPONSE);
        MessageUtils.setAuthorizationError(response, "Cannot create access token.");
        sendResponse(response);
    }

    /**
     * クライアントデータ.
     */
    protected class ClientData {
        /** クライアントID. */
        String clientId;
        /** クライアントシークレット. */
        String clientSecret;
    }

    /**
     * Local OAuthの処理を行うリクエスト.
     */
    private abstract class OAuthRequest extends DConnectRequest {
        /** ロックオブジェクト. */
        protected final Object mLockObj = new Object();
        /** 送信先のデバイスID. */
        protected String mDeviceId;
        /**
         * デバイスIDを設定する.
         * @param id デバイスID
         */
        public void setDeviceId(final String id) {
            mDeviceId = id;
        }
        @Override
        public void setResponse(final Intent response) {
            super.setResponse(response);
            synchronized (mLockObj) {
                mLockObj.notifyAll();
            }
        }

        @Override
        public boolean hasRequestCode(final int requestCode) {
            return false;
        }

        @Override
        public void run() {
            String clientId = null;
            String clientSecret = null;

            OAuthData oauth = mLocalOAuth.getOAuthData(mDeviceId);
            if (oauth == null) {
                ClientData client = executeCreateClient(mDeviceId);
                if (client == null) {
                    // MEMO executeCreateClientの中でレスポンスは返しているので
                    // ここでは何も処理を行わない。
                    onFinishAuth(null);
                    return;
                } else {
                    clientId = client.clientId;
                    clientSecret = client.clientSecret;
                    // クライアントデータを保存
                    mLocalOAuth.setOAuthData(mDeviceId, clientId, clientSecret);
                    oauth = mLocalOAuth.getOAuthData(mDeviceId);
                }
            } else {
                clientId = oauth.getClientId();
                clientSecret = oauth.getClientSecret();
            }

            String accessToken = mLocalOAuth.getAccessToken(oauth.getId());
            if (accessToken == null) {
                // 再度アクセストークンを取得してから再度実行
                accessToken = executeAccessToken(mDeviceId, clientId, clientSecret);
                if (accessToken == null) {
                    // MEMO executeAccessTokenの中でレスポンスは返しているので
                    // ここでは何も処理を行わない。
                    onFinishAuth(null);
                    return;
                } else {
                    // アクセストークンを保存
                    mLocalOAuth.setAccessToken(oauth.getId(), accessToken);
                }
            }

            onFinishAuth(accessToken);
        }
        /**
         * 認証完了通知用メソッド.
         * <p>
         * 認証が完了した場合に、このメソッドが呼び出される。
         * 認証に成功した場合には、アクセストークンが渡される。
         * 認証に失敗した場合にはnullが渡される。
         * </p>
         * @param accessToken アクセストークン
         */
        public abstract void onFinishAuth(String accessToken);
    };
}
