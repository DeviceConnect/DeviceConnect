/*
 * SonyCameraSystemProfile
 * Copyright (c) 2014 NTT DOCOMO,INC.
 * Released under the MIT license
 * http://opensource.org/licenses/mit-license.php
 */

package org.deviceconnect.android.deviceplugin.sonycamera.profile;

import org.deviceconnect.android.deviceplugin.sonycamera.activity.SonyCameraSettingActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import org.deviceconnect.android.event.EventManager;
import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.android.profile.DConnectProfileProvider;
import org.deviceconnect.android.profile.SystemProfile;
import org.deviceconnect.message.DConnectMessage;

/**
 * Sony Camera 用 System プロファイル.
 * @author NTT DOCOMO, INC.
 */
public class SonyCameraSystemProfile extends SystemProfile {

    /**
     * コンストラクタ.
     * 
     * @param provider プロバイダ
     */
    public SonyCameraSystemProfile(final DConnectProfileProvider provider) {
        super(provider);
    }

    @Override
    protected Class<? extends Activity> getSettingPageActivity(final Intent request, final Bundle param) {
        return SonyCameraSettingActivity.class;
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
