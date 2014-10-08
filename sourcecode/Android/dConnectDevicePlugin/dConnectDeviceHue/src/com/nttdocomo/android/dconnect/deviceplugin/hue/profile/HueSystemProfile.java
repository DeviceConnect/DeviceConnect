/*
HueSystemProfile
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

package com.nttdocomo.android.dconnect.deviceplugin.hue.profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.nttdocomo.android.dconnect.deviceplugin.hue.activity.HueMainActivity;
import com.nttdocomo.android.dconnect.deviceplugin.util.DcLoggerHue;
import com.nttdocomo.android.dconnect.profile.DConnectProfileProvider;
import com.nttdocomo.android.dconnect.profile.SystemProfile;

/**
 * Hueデバイスプラグイン, System プロファイル.
 * @author NTT DOCOMO, INC.
 */
public class HueSystemProfile extends SystemProfile {

    /**
     * ロガー.
     */
    private DcLoggerHue mLogger = new DcLoggerHue();

    /**
     * Constructor.
     * @param provider provider
     */
    public HueSystemProfile(final DConnectProfileProvider provider) {
        super(provider);
        
        mLogger.entering(this, "HueSystemProfile");
        mLogger.exiting(this, "HueSystemProfile");
    }

    @Override
    protected Class<? extends Activity> getSettingPageActivity(final Intent request, final Bundle param) {

        mLogger.entering(this, "getSettingPageActivity");
        mLogger.exiting(this, "getSettingPageActivity");

        return HueMainActivity.class;
    }

}
