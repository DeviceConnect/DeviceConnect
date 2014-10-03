/*
 HostSystemProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.deviceplugin.host.profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.nttdocomo.android.dconnect.deviceplugin.host.setting.HostSettingActivity;
import com.nttdocomo.android.dconnect.event.EventManager;
import com.nttdocomo.android.dconnect.message.MessageUtils;
import com.nttdocomo.android.dconnect.profile.DConnectProfileProvider;
import com.nttdocomo.android.dconnect.profile.SystemProfile;
import com.nttdocomo.dconnect.message.DConnectMessage;

/**
 * ホストデバイスプラグイン, System プロファイル.
 * @author NTT DOCOMO, INC.
 */
public class HostSystemProfile extends SystemProfile {

    /**
     * System Profile.
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

