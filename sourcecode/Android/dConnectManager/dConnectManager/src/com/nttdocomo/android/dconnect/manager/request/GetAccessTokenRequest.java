/*
 GetAccessTokenRequest.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.manager.request;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;

import com.nttdocomo.android.dconnect.localoauth.AccessTokenData;
import com.nttdocomo.android.dconnect.localoauth.AccessTokenScope;
import com.nttdocomo.android.dconnect.localoauth.ConfirmAuthParams;
import com.nttdocomo.android.dconnect.localoauth.LocalOAuth2Main;
import com.nttdocomo.android.dconnect.localoauth.PublishAccessTokenListener;
import com.nttdocomo.android.dconnect.localoauth.exception.AuthorizatonException;
import com.nttdocomo.android.dconnect.manager.profile.AuthorizationProfile;
import com.nttdocomo.android.dconnect.message.MessageUtils;
import com.nttdocomo.dconnect.message.DConnectMessage;
import com.nttdocomo.dconnect.profile.AuthorizationProfileConstants;

/**
 * LocalOAuthにアクセストークンを要求するリクエスト.
 * @author NTT DOCOMO, INC.
 */
public class GetAccessTokenRequest extends DConnectRequest {

    /** ロックオブジェクト. */
    private final Object mLockObj = new Object();

    @Override
    public boolean hasRequestCode(final int requestCode) {
        return false;
    }

    @Override
    public void run() {
        try {
            getAccessToken();
        } catch (AuthorizatonException e) {
            MessageUtils.setNotSupportProfileError(mResponse, e.getMessage());
        } catch (UnsupportedEncodingException e) {
            MessageUtils.setInvalidRequestParameterError(mResponse, e.getMessage());
        } catch (IllegalArgumentException e) {
            MessageUtils.setInvalidRequestParameterError(mResponse, e.getMessage());
        } catch (IllegalStateException e) {
            MessageUtils.setInvalidRequestParameterError(mResponse, e.getMessage());
        }
        sendResponse(mResponse);
    }

    /**
     * アクセストークンの取得の処理を行う.
     * @throws AuthorizatonException 認証に失敗した場合に発生
     * @throws UnsupportedEncodingException 文字のエンコードに失敗した場合に発生
     */
    private void getAccessToken() throws AuthorizatonException, UnsupportedEncodingException {
        String deviceId = mRequest.getStringExtra(DConnectMessage.EXTRA_DEVICE_ID);
        String clientId = mRequest.getStringExtra(AuthorizationProfile.PARAM_CLIENT_ID);
        String grantType = mRequest.getStringExtra(AuthorizationProfile.PARAM_GRANT_TYPE);
        String[] scopes = parseScopes(mRequest.getStringExtra(AuthorizationProfile.PARAM_SCOPE));
        String applicationName = mRequest.getStringExtra(AuthorizationProfile.PARAM_APPLICATION_NAME);
        String signature = mRequest.getStringExtra(AuthorizationProfile.PARAM_SIGNATURE);
        if (signature != null) {
            signature = URLDecoder.decode(signature, "UTF-8");
        }

        // シグネイチャの確認
        if (LocalOAuth2Main.checkSignature(signature, clientId, grantType, deviceId, scopes)) {
            // TODO _typeからアプリorデバイスプラグインかを判別できる？
            ConfirmAuthParams params = new ConfirmAuthParams.Builder().context(mContext).deviceId(deviceId)
                    .clientId(clientId).grantType(grantType).scopes(scopes).applicationName(applicationName)
                    .isForDevicePlugin(false) 
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

            if (token[0] != null && token[0].getAccessToken() != null) {
                String chkSignature = createSignature(token[0].getAccessToken(), clientId);
                if (chkSignature != null) {
                    mResponse.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_OK);
                    mResponse.putExtra(AuthorizationProfile.PARAM_ACCESS_TOKEN, token[0].getAccessToken());
                    mResponse.putExtra(AuthorizationProfile.PARAM_SIGNATURE, chkSignature);
                    AccessTokenScope[] atScopes = token[0].getScopes();
                    if (atScopes != null) {
                        List<Bundle> s = new ArrayList<Bundle>();
                        for (int i = 0; i < atScopes.length; i++) {
                            Bundle b = new Bundle();
                            b.putString(AuthorizationProfileConstants.PARAM_SCOPE,
                                    atScopes[i].getScope());
                            b.putLong(AuthorizationProfileConstants.PARAM_EXPIRE_PERIOD,
                                    atScopes[i].getExpirePeriod());
                            s.add(b);
                        }
                        mResponse.putExtra(AuthorizationProfileConstants.PARAM_SCOPES,
                                s.toArray(new Bundle[s.size()]));
                    }
                    
                } else {
                    MessageUtils.setAuthorizationError(mResponse, "Cannot create a signature.");
                }
            } else {
                MessageUtils.setAuthorizationError(mResponse, "Cannot create a access token.");
            }
        } else {
            MessageUtils.setAuthorizationError(mResponse, "signature does not match.");
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
        } catch (AuthorizatonException e) {
            return null;
        }
    }
}
