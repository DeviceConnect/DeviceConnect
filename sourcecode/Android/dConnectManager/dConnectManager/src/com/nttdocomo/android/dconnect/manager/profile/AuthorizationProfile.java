/*
 AuthorizationProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.manager.profile;

import android.content.Intent;

import com.nttdocomo.android.dconnect.DConnectMessageService;
import com.nttdocomo.android.dconnect.DConnectService;
import com.nttdocomo.android.dconnect.DConnectSettings;
import com.nttdocomo.android.dconnect.manager.request.CreateClientRequest;
import com.nttdocomo.android.dconnect.manager.request.DConnectRequest;
import com.nttdocomo.android.dconnect.manager.request.GetAccessTokenRequest;
import com.nttdocomo.android.dconnect.message.MessageUtils;
import com.nttdocomo.android.dconnect.profile.DConnectProfile;
import com.nttdocomo.dconnect.profile.AuthorizationProfileConstants;

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

    @Override
    public String getProfileName() {
        return PROFILE_NAME;
    }

    @Override
    protected boolean onGetRequest(final Intent request, final Intent response) {
        // Local OAuthを使用しない場合にはNot Supportを返却する
        DConnectSettings settings = DConnectSettings.getInstance();
        if (!settings.isUseALocalOAuth()) {
            MessageUtils.setNotSupportProfileError(response);
            return true;
        }

        String attribute = getAttribute(request);
        if (ATTRIBUTE_CREATE_CLIENT.equals(attribute)) {
            onGetCreateClient(request, response);
        } else if (ATTRIBUTE_REQUEST_ACCESS_TOKEN.equals(attribute)) {
            onGetRequestAccessToken(request, response);
        } else {
            sendUnknownAttributeError(request, response);
        }

        // 各デバイスプラグインに送信する場合にはfalseを返却、
        // dConnectManagerで止める場合にはtrueを返却する
        // ここでは、各デバイスには渡さないのでtrueを返却する。
        return true;
    }

    @Override
    protected boolean onPostRequest(final Intent request, final Intent response) {
        MessageUtils.setNotSupportActionError(response);
        ((DConnectService) getContext()).sendResponse(request, response);
        return true;
    }

    @Override
    protected boolean onPutRequest(final Intent request, final Intent response) {
        MessageUtils.setNotSupportActionError(response);
        ((DConnectService) getContext()).sendResponse(request, response);
        return true;
    }

    @Override
    protected boolean onDeleteRequest(final Intent request, final Intent response) {
        MessageUtils.setNotSupportActionError(response);
        ((DConnectService) getContext()).sendResponse(request, response);
        return true;
    }

    /**
     * Local OAuthで使用するクライアントを作成要求を行う.
     * 
     * 各デバイスプラグインに送信する場合にはtrueを返却、
     * dConnectManagerで止める場合にはfalseを返却する
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * 
     * @return 各デバイスプラグインに送信する場合にはtrue、dConnectManagerで止める場合にはfalseを返却する
     */
    private boolean onGetCreateClient(final Intent request, final Intent response) {
        DConnectRequest req = new CreateClientRequest();
        req.setContext(getContext());
        req.setRequest(request);
        req.setResponse(response);
        ((DConnectMessageService) getContext()).addRequest(req);
        return true;
    }

    /**
     * Local OAuthで使用するクライアントを作成要求を行う.
     * 
     * 各デバイスプラグインに送信する場合にはtrueを返却、
     * dConnectManagerで止める場合にはfalseを返却する
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * 
     * @return 各デバイスプラグインに送信する場合にはtrue、dConnectManagerで止める場合にはfalseを返却する
     */
    private boolean onGetRequestAccessToken(final Intent request, final Intent response) {
        DConnectRequest req = new GetAccessTokenRequest();
        req.setContext(getContext());
        req.setRequest(request);
        req.setResponse(response);
        ((DConnectMessageService) getContext()).addRequest(req);
        return true;
    }

    /**
     * Authorizationで定義されていないattributeが指定されていたときのエラーを返却する.
     * @param request リクエスト
     * @param response レスポンス
     */
    private void sendUnknownAttributeError(final Intent request, final Intent response) {
        MessageUtils.setUnknownAttributeError(response);
        ((DConnectService) getContext()).sendResponse(request, response);
    }
}
