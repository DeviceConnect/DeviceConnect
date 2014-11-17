/*
 HostSystemProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.host.profile;

import org.deviceconnect.android.deviceplugin.host.setting.HostSettingActivity;
import org.deviceconnect.android.event.EventManager;
import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.android.profile.DConnectProfileProvider;
import org.deviceconnect.android.profile.SystemProfile;
import org.deviceconnect.message.DConnectMessage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * ホストデバイスプラグイン, System プロファイル.
 * 
 * @author NTT DOCOMO, INC.
 */
public class HostSystemProfile extends SystemProfile {

    /**
     * System Profile.
     * 
     * @param provider プロバイダ
     */
    public HostSystemProfile(final DConnectProfileProvider provider) {
        super(provider);
    }

    /**
     * 設定画面を設定.
     * 
     * @param request リクエスト
     * @param bundle　バンドル
     * 
     * @return 設定アクティビティ
     */
    protected Class<? extends Activity> getSettingPageActivity(final Intent request, final Bundle bundle) {
        return HostSettingActivity.class;
    }

    @Override
    protected boolean onDeleteEvents(final Intent request, final Intent response, final String sessionKey) {

        if (sessionKey == null || sessionKey.length() == 0) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else if (EventManager.INSTANCE.removeEvents(sessionKey)) {
            setResult(response, DConnectMessage.RESULT_OK);
        } else {
            MessageUtils.setUnknownError(response);
        }

        return true;
    }
}
