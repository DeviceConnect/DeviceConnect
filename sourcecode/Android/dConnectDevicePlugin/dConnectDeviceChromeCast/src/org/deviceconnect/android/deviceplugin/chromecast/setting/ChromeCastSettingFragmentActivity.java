/*
 ChromeCastSettingFragmentActivity.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.chromecast.setting;

import java.util.ArrayList;
import android.support.v4.app.Fragment;
import org.deviceconnect.android.ui.activity.DConnectSettingPageFragmentActivity;

/**
 * チュートリアル画面（ステップ）
 * <p>
 * 画面を作成する
 * </p>
 * 
 * @author NTT DOCOMO, INC.
 */
public class ChromeCastSettingFragmentActivity extends DConnectSettingPageFragmentActivity {

    private ArrayList<Fragment> fragments;
    
    public ChromeCastSettingFragmentActivity() {
        fragments = new ArrayList<Fragment>();
        fragments.add(new ChromeCastSettingFragmentPage1());
        fragments.add(new ChromeCastSettingFragmentPage2());
        fragments.add(new ChromeCastSettingFragmentPage3());
    }

    @Override
    public int getPageCount() {
        return fragments.size();
    }

    @Override
    public Fragment createPage(final int position) {
        return fragments.get(position);
    }
}
