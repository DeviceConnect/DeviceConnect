/*
 WearSystemProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.wear.profile;

import org.deviceconnect.android.deviceplugin.wear.setting.WearSettingActivity;
import org.deviceconnect.android.profile.DConnectProfileProvider;
import org.deviceconnect.android.profile.SystemProfile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Systemプロファイル.
 * 
 * @author NTT DOCOMO, INC.
 */
public class WearSystemProfile extends SystemProfile {
    /**
     * コンストラクタ.
     * 
     * @param plugin プラグイン
     */
    public WearSystemProfile(final DConnectProfileProvider plugin) {
        super(plugin);
    }

    @Override
    protected Class<? extends Activity> getSettingPageActivity(final Intent request, final Bundle param) {
        return WearSettingActivity.class;
    }
}
