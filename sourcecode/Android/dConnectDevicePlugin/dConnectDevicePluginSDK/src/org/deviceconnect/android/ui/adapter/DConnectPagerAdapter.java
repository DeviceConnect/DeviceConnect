/*
 DConnectPagerAdapter.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.ui.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * ViewPager用アダプタ.
 * 
 * @author NTT DOCOMO, INC.
 */
public class DConnectPagerAdapter extends PagerAdapter {

    /**
     * ページクリエイター.
     */
    private DConnectPageCreater<View> mCreater;

    /**
     * 指定されたページクリエイターを持つアダプタを新規生成します.
     * 
     * @param creater ページクリエイター
     */
    public DConnectPagerAdapter(final DConnectPageCreater<View> creater) {
        this.mCreater = creater;
    }

    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        View page = mCreater.createPage(position);
        container.addView(page);
        return page;
    }

    @Override
    public int getCount() {
        return mCreater.getPageCount();
    }

    @Override
    public boolean isViewFromObject(final View view, final Object obj) {
        return view.equals(obj);
    }
}
