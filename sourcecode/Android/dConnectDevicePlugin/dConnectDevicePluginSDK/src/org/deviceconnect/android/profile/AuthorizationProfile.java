/*
 AuthorizationProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.profile;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.deviceconnect.android.localoauth.AccessTokenData;
import org.deviceconnect.android.localoauth.AccessTokenScope;
import org.deviceconnect.android.localoauth.ClientData;
import org.deviceconnect.android.localoauth.ConfirmAuthParams;
import org.deviceconnect.android.localoauth.LocalOAuth2Main;
import org.deviceconnect.android.localoauth.PublishAccessTokenListener;
import org.deviceconnect.android.localoauth.exception.AuthorizatonException;
import org.deviceconnect.android.message.DConnectMessageService;
import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.profile.AuthorizationProfileConstants;
import org.restlet.ext.oauth.PackageInfoOAuth;

import android.content.Intent;
import android.os.Bundle;

/**
 * Authorization プロファイル.
 * 
 * <p>
 * Local OAuthの認可機能を提供するAPI.<br/>
 * Local OAuthの認可機能を提供するデバイスプラグインは当クラスを継承し、対応APIを実装すること。
 * </p>
 * @author NTT DOCOMO, INC.
 */
public class AuthorizationProfile extends DConnectProfile implements AuthorizationProfileConstants {

    /** ロックオブジェクト. */
    private final Object mLockObj = new Object();

    @Override
    public final String getProfileName() {
        return PROFILE_NAME;
    }

    @Override
    protected final boolean onGetRequest(final Intent request, final Intent response) {
        // Local OAuthを使用しない場合にはNot Supportを返却する
        DConnectMessageService service = (DConnectMessageService) getContext();
        if (!service.isUseLocalOAuth()) {
            MessageUtils.setNotSupportProfileError(response);
            return true;
        }

        boolean send = true;
        String attribute = getAttribute(request);
        if (ATTRIBUTE_CREATE_CLIENT.equals(attribute)) {
            send = onGetCreateClient(request, response);
        } else if (ATTRIBUTE_REQUEST_ACCESS_TOKEN.equals(attribute)) {
            send = onGetRequestAccessToken(request, response);
        } else {
            MessageUtils.setUnknownAttributeError(response);
            send = true;
        }
        return send;
    }

    /**
     * Local OAuthで使用するクライアントを作成要求を行う.
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * 
     * @return レスポンスパラメータを送信するか否か
     */
    private boolean onGetCreateClient(final Intent request, final Intent response) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    createClient(request, response);
                } catch (Exception e) {
                    MessageUtils.setAuthorizationError(response, e.getMessage());
                }
                getContext().sendBroadcast(response);
            }
        }).start();
        return false;
    }

    /**
     * Local OAuthで使用するクライアントを作成要求を行う.
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * 
     * @return レスポンスパラメータを送信するか否か
     */
    private boolean onGetRequestAccessToken(final Intent request, final Intent response) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    getAccessToken(request, response);
                } catch (AuthorizatonException e) {
                    MessageUtils.setNotSupportProfileError(response, e.getMessage());
                } catch (UnsupportedEncodingException e) {
                    MessageUtils.setInvalidRequestParameterError(response, e.getMessage());
                } catch (IllegalArgumentException e) {
                    MessageUtils.setInvalidRequestParameterError(response, e.getMessage());
                } catch (IllegalStateException e) {
                    MessageUtils.setInvalidRequestParameterError(response, e.getMessage());
                } catch (Exception e) {
                    MessageUtils.setUnknownError(response, e.getMessage());
                }
                getContext().sendBroadcast(response);
            }
        }).start();
        return false;
    }

    /**
     * Clientデータを作成する.
     * 
     * @param request リクエスト
     * @param response レスポンス
     */
    private void createClient(final Intent request, final Intent response) {
        String packageName = request.getStringExtra(AuthorizationProfile.PARAM_PACKAGE);
        String deviceId = request.getStringExtra(DConnectProfile.PARAM_DEVICE_ID);
        if (packageName == null) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else {
            // Local OAuthでクライアント作成
            PackageInfoOAuth packageInfo = new PackageInfoOAuth(packageName, deviceId);
            try {
                ClientData client = LocalOAuth2Main.createClient(packageInfo);
                if (client != null) {
                    response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_OK);
                    response.putExtra(AuthorizationProfile.PARAM_CLIENT_ID, client.getClientId());
                    response.putExtra(AuthorizationProfile.PARAM_CLIENT_SECRET, client.getClientSecret());
                } else {
                    MessageUtils.setAuthorizationError(response, "Cannot create a client.");
                }
            } catch (AuthorizatonException e) {
                MessageUtils.setAuthorizationError(response, e.getMessage());
            } catch (IllegalArgumentException e) {
                MessageUtils.setInvalidRequestParameterError(response, e.getMessage());
            }
        }
    }

    /**
     * アクセストークンの取得の処理を行う.
     * 
     * @param request リクエスト
     * @param response レスポンス
     * 
     * @throws AuthorizatonException 認証に失敗した場合に発生
     * @throws UnsupportedEncodingException 文字のエンコードに失敗した場合に発生
     */
    private void getAccessToken(final Intent request, final Intent response) 
            throws AuthorizatonException, UnsupportedEncodingException {
        String deviceId = request.getStringExtra(DConnectMessage.EXTRA_DEVICE_ID);
        String clientId = request.getStringExtra(AuthorizationProfile.PARAM_CLIENT_ID);
        String grantType = request.getStringExtra(AuthorizationProfile.PARAM_GRANT_TYPE);
        String[] scopes = parseScopes(request.getStringExtra(AuthorizationProfile.PARAM_SCOPE));
        String applicationName = request.getStringExtra(AuthorizationProfile.PARAM_APPLICATION_NAME);
        String signature = request.getStringExtra(AuthorizationProfile.PARAM_SIGNATURE);
        if (signature != null) {
            signature = URLDecoder.decode(signature, "UTF-8");
        }

        // シグネイチャの確認
        if (LocalOAuth2Main.checkSignature(signature, clientId, grantType, deviceId, scopes)) {
            // TODO _typeからアプリorデバイスプラグインかを判別できる？
            ConfirmAuthParams params = new ConfirmAuthParams.Builder().context(getContext()).deviceId(deviceId)
                    .clientId(clientId).grantType(grantType).scopes(scopes).applicationName(applicationName)
                    .isForDevicePlugin(true) 
                    .build();

            // Local OAuthでAccessTokenを作成する。
            final AccessTokenData[] token = new AccessTokenData[1];
            LocalOAuth2Main.confirmPublishAccessToken(params, new PublishAccessTokenListener() {
                @Override
                public void onReceiveAccessToken(final AccessTokenData accessTokenData) {
                    token[0] = accessTokenData;
                    synchronized (mLockObj) {
                        mLockObj.notifyAll();
                    }
                }
                @Override
                public void onReceiveException(final Exception exception) {
                    token[0] = null;
                    synchronized (mLockObj) {
                        mLockObj.notifyAll();
                    }
                }
            });

            // ユーザからのレスポンスを待つ
            if (token[0] == null) {
                waitForResponse();
            }

            // アクセストークンの確認
            if (token[0] != null && token[0].getAccessToken() != null) {
                String chkSignature = createSignature(token[0].getAccessToken(), clientId);
                if (chkSignature != null) {
                    response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_OK);
                    response.putExtra(AuthorizationProfile.PARAM_ACCESS_TOKEN, token[0].getAccessToken());
                    response.putExtra(AuthorizationProfile.PARAM_SIGNATURE, chkSignature);
                    AccessTokenScope[] atScopes = token[0].getScopes();
                    if (atScopes != null) {
                        List<Bundle> s = new ArrayList<Bundle>();
                        for (int i = 0; i < atScopes.length; i++) {
                            Bundle b = new Bundle();
                            b.putString(PARAM_SCOPE, atScopes[i].getScope());
                            b.putLong(PARAM_EXPIRE_PERIOD, atScopes[i].getExpirePeriod());
                            s.add(b);
                        }
                        response.putExtra(PARAM_SCOPES, s.toArray(new Bundle[s.size()]));
                    }
                } else {
                    MessageUtils.setAuthorizationError(response, "Cannot create a signature.");
                }
            } else {
                MessageUtils.setAuthorizationError(response, "Cannot create a access token.");
            }
        } else {
            MessageUtils.setAuthorizationError(response, "signature does not match.");
        }
    }

    /**
     * レスポンスが返ってくるまでの間スレッドを停止する.
     * タイムアウトは設定していない。
     */
    private void waitForResponse() {
        synchronized (mLockObj) {
            try {
                mLockObj.wait();
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    /**
     * スコープを分割して、配列に変換します.
     * @param scope スコープ
     * @return 分割されたスコープ
     */
    private String[] parseScopes(final String scope) {
        if (scope == null) {
            return null;
        }
        String[] scopes = scope.split(",");
        for (int i = 0; i < scopes.length; i++) {
            scopes[i] = scopes[i].trim();
        }
        return scopes;
    }

    /**
     * レスポンス用のシグネイチャを作成する.
     * 
     * 作成に失敗した場合にはnullを返却する.
     * 
     * @param accessToken アクセストークン
     * @param clientId クライアントID
     * @return シグネイチャ
     */
    private String createSignature(final String accessToken, final String clientId) {
        try {
            return LocalOAuth2Main.createSignature(accessToken, clientId);
        } catch (Exception e) {
            return null;
        }
    }
}
