/*
HueSystemProfile
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

package org.deviceconnect.android.deviceplugin.hue.profile;

import org.deviceconnect.android.deviceplugin.hue.activity.HueMainActivity;
import org.deviceconnect.android.profile.DConnectProfileProvider;
import org.deviceconnect.android.profile.SystemProfile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


/**
 * Hueデバイスプラグイン, System プロファイル.
 * @author NTT DOCOMO, INC.
 */
public class HueSystemProfile extends SystemProfile {

    /**
     * Constructor.
     * @param provider provider
     */
    public HueSystemProfile(final DConnectProfileProvider provider) {
        super(provider);
    }

    @Override
    protected Class<? extends Activity> getSettingPageActivity(final Intent request, final Bundle param) {
        return HueMainActivity.class;
    }

}
