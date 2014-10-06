package com.mycompany.deviceplugin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.nttdocomo.android.dconnect.profile.DConnectProfileProvider;
import com.nttdocomo.android.dconnect.profile.SystemProfile;

/**
 * System プロファイル実装クラス.
 * 
 * @author docomo
 */
public class ExampleSystemProfile extends SystemProfile {
    /**
     * デバイスプラグインのプロファイルを設定するコンストラクタ.
     * @param provider ProfileのProvider
     */
    public ExampleSystemProfile(DConnectProfileProvider provider) {
        super(provider);
    }

    @Override
    protected Class<? extends Activity> getSettingPageActivity(Intent request, Bundle param) {
        Class<? extends Activity> clazz = (Class<? extends Activity>) SettingActivity.class;
        return clazz;
    }
}
