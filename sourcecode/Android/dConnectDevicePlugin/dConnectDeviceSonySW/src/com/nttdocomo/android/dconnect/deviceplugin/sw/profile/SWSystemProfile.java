package com.nttdocomo.android.dconnect.deviceplugin.sw.profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.nttdocomo.android.dconnect.deviceplugin.sw.setting.SWSettingMainActivity;
import com.nttdocomo.android.dconnect.event.EventManager;
import com.nttdocomo.android.dconnect.message.MessageUtils;
import com.nttdocomo.android.dconnect.profile.DConnectProfileProvider;
import com.nttdocomo.android.dconnect.profile.SystemProfile;
import com.nttdocomo.dconnect.message.DConnectMessage;

/**
SystemProfile
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

public class SWSystemProfile extends SystemProfile {
    /**
     * VERSION.
     */

    /**
     * コンストラクタ.
     * 
     * @param provider プロファイルプロバイダ
     */
    public SWSystemProfile(final DConnectProfileProvider provider) {
        super(provider);
    }

    @Override
    protected Class<? extends Activity> getSettingPageActivity(final Intent request, final Bundle param) {
        return SWSettingMainActivity.class;
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
