/*
 IRKitAbstractSettingActivity.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.irkit.settings.activity;

import java.util.concurrent.atomic.AtomicInteger;

import org.deviceconnect.android.deviceplugin.irkit.settings.widget.HoldableViewPager;
import org.deviceconnect.android.ui.adapter.DConnectFragmentPagerAdapter;
import org.deviceconnect.android.ui.adapter.DConnectPageCreater;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

/**
 * ViewPagerを独自拡張するためのActivity.
 * @author NTT DOCOMO, INC.
 */
public abstract class IRKitAbstractSettingActivity extends FragmentActivity implements 
DConnectPageCreater<Fragment> {

    /**
     * ID値のMAX.
     */
    private static final int MAX_VALUE = 0x00FFFFFF;

    /**
     * ページ用のビューページャー.
     */
    private ViewPager mViewPager;

    /**
     * デフォルトのタイトル文字列.
     */
    public static final String DEFAULT_TITLE = "CLOSE";

    /**
     * ViewPagerを持つレイアウトを自動的に設定する. サブクラスでオーバーライドする場合は setContentView
     * を<strong>実行しないこと</strong>。
     * 
     * @param savedInstanceState パラメータ
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewPager = new HoldableViewPager(this);
        mViewPager.setId(generateViewId());
        DConnectFragmentPagerAdapter adapter = new DConnectFragmentPagerAdapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(adapter);
        setContentView(mViewPager);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayOptions(0, ActionBar.DISPLAY_SHOW_HOME);
        getActionBar().setTitle(DEFAULT_TITLE);
    }

    /**
     * ViewPagerのIDを生成する. IDが無いとリソースIDが無いといわれエラーになってしまうため対処。
     * View.generateViewId()はAPI17からなので同等の機能を実装。
     * 
     * @return リソースID
     */
    private int generateViewId() {
        AtomicInteger sNextGeneratedId = new AtomicInteger(1);

        for (;;) {
            final int result = sNextGeneratedId.get();
            int newValue = result + 1;
            if (newValue > MAX_VALUE) {
                newValue = 1;
            }
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    
    /**
     * ViewPagerを取得する.
     * 
     * @return ViewPagerのインスタンス
     */
    protected ViewPager getViewPager() {
        return mViewPager;
    }

}
