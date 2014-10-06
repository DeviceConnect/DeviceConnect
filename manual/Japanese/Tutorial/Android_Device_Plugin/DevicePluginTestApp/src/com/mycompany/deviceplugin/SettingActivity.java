package com.mycompany.deviceplugin;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.nttdocomo.android.dconnect.ui.activity.DConnectSettingPageFragmentActivity;

public class SettingActivity extends DConnectSettingPageFragmentActivity {
    /**
     * 設定画面のページ数.
     * @return ページ数
     */
    @Override
    public int getPageCount() {
        return 3;
    }

    /**
     * 設定画面のページ.
     * @param position 表示するページ
     * @return 表示するFragment
     */
    @Override
    public Fragment createPage(int position) {
        Bundle b = new Bundle();
        b.putInt("position", position);
        MyFragment f = new MyFragment();
        f.setArguments(b);
        return f;
    }
}
