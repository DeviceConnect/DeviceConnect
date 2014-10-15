/*
 DConnectFragmentPagerAdapter.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * ViewPager用アダプタ Fragment用.
 * @author NTT DOCOMO, INC.
 */
public class DConnectFragmentPagerAdapter extends FragmentPagerAdapter {
    
    /** 
     * ページクリエイター.
     */
    private DConnectPageCreater<Fragment> mCreater;

    /**
     * 指定されたフラグメントマネージャを持つアダプタを新規生成する.
     * 
     * @param fm フラグメントマネージャ
     * @param creater ページクリエイター
     */
    public DConnectFragmentPagerAdapter(final FragmentManager fm, final DConnectPageCreater<Fragment> creater) {
        super(fm);
        this.mCreater = creater;
    }

    @Override
    public Fragment getItem(final int position) {
        Fragment f = mCreater.createPage(position);
        return f;
    }

    @Override
    public int getCount() {
        return mCreater.getPageCount();
    }

}
