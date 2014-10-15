/*
 SWSystemProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.sw.profile;

import org.deviceconnect.android.deviceplugin.sw.setting.SWSettingMainActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import org.deviceconnect.android.event.EventManager;
import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.android.profile.DConnectProfileProvider;
import org.deviceconnect.android.profile.SystemProfile;
import org.deviceconnect.message.DConnectMessage;

/**
 * SonySWデバイスプラグインの{@link SystemProfile}実装.
 * @author NTT DOCOMO, INC.
 */
public class SWSystemProfile extends SystemProfile {

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
