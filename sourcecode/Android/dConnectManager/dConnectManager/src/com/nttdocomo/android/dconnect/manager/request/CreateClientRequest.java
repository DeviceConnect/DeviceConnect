/*
 CreateClientRequest.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.manager.request;

import org.restlet.ext.oauth.PackageInfoOAuth;

import com.nttdocomo.android.dconnect.localoauth.ClientData;
import com.nttdocomo.android.dconnect.localoauth.LocalOAuth2Main;
import com.nttdocomo.android.dconnect.localoauth.exception.AuthorizatonException;
import com.nttdocomo.android.dconnect.manager.profile.AuthorizationProfile;
import com.nttdocomo.android.dconnect.message.MessageUtils;
import com.nttdocomo.dconnect.message.DConnectMessage;

/**
 * LocalOAuth2にClinetを作成するためのリクエスト.
 * @author NTT DOCOMO, INC.
 */
public class CreateClientRequest extends DConnectRequest {

    @Override
    public boolean hasRequestCode(final int requestCode) {
        return false;
    }

    @Override
    public void run() {
        String packageName = mRequest.getStringExtra(AuthorizationProfile.PARAM_PACKAGE);
        if (packageName == null) {
            MessageUtils.setInvalidRequestParameterError(mResponse);
        } else {
            // Local OAuthでクライアント作成
            PackageInfoOAuth packageInfo = new PackageInfoOAuth(packageName);
            try {
                ClientData client = LocalOAuth2Main.createClient(packageInfo);
                if (client != null) {
                    mResponse.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_OK);
                    mResponse.putExtra(AuthorizationProfile.PARAM_CLIENT_ID, client.getClientId());
                    mResponse.putExtra(AuthorizationProfile.PARAM_CLIENT_SECRET, client.getClientSecret());
                } else {
                    MessageUtils.setAuthorizationError(mResponse);
                }
            } catch (AuthorizatonException e) {
                MessageUtils.setAuthorizationError(mResponse, e.getMessage());
            } catch (IllegalArgumentException e) {
                MessageUtils.setInvalidRequestParameterError(mResponse, e.getMessage());
            }
        }
        sendResponse(mResponse);
    }
}
