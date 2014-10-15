/*
 FragmentViewPager.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.client.fragment;

import java.util.ArrayList;
import java.util.List;

import org.deviceconnect.android.uiapp.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * フラグメントビューページャー.
 */
public class FragmentViewPager extends ViewPager {

    /**
     * フラグメントリスト.
     */
    private List<Fragment> mFragmentList = new ArrayList<Fragment>();

    /**
     * スライドページャアダプタ.
     */
    private ScreenSlidePagerAdapter mPagerAdapter;

    /**
     * コンストラクタ.
     * @param context コンテキスト
     */
    public FragmentViewPager(final Context context) {
        this(context, null);
    }

    /**
     * コンストラクタ.
     * @param context コンテキスト
     * @param attrs 属性
     */
    public FragmentViewPager(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        setPageTransformer(true, new DepthPageTransformer());
        mPagerAdapter = new ScreenSlidePagerAdapter(
                ((FragmentActivity) context).getSupportFragmentManager());
        setAdapter(mPagerAdapter);
        setOnPageChangeListener((ViewPager.OnPageChangeListener) mPagerAdapter);
    }

    /**
     * 現在表示中のフラグメントを取得する.
     * @return 表示中のフラグメント
     */
    public Fragment getCurrentFragment() {
        if (mFragmentList.size() == 0) {
            return null;
        }
        return mFragmentList.get(getCurrentItem());
    }

    /**
     * フラグメントリストを設定する.
     * @param fragments フラグメントリスト
     */
    public void setFragmentList(final List<Fragment> fragments) {
        mFragmentList.clear();

        if (fragments != null && fragments.size() > 0) {
            mFragmentList.addAll(fragments);
            mPagerAdapter.notifyDataSetChanged();

            setCurrentItem(0, false);
            mPagerAdapter.onPageSelected(0);
        }
    }

    /**
     * スクリーンスライドページャアダプタ.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter
            implements ViewPager.OnPageChangeListener {

        /**
         * コンストラクタ.
         * @param fm フラグメントマネージャ.
         */
        public ScreenSlidePagerAdapter(final FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(final int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public int getItemPosition(final Object object) {
            return POSITION_NONE;
        }

        @Override
        public void onPageScrollStateChanged(final int state) {
        }

        @Override
        public void onPageScrolled(final int position,
                final float positionOffset, final int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(final int position) {
            String title;

            Bundle args = mFragmentList.get(position).getArguments();
            if (args != null) {
                title = args.getString(Intent.EXTRA_TITLE,
                        getContext().getString(R.string.app_name));
            } else {
                 title = getContext().getString(R.string.app_name);
            }

            ((FragmentActivity) getContext()).getActionBar().setTitle(title);
        }
    }

    /**
     * ページャトランスフォーマ.
     */
    public class DepthPageTransformer implements ViewPager.PageTransformer {

        /**
         * 縮小スケール.
         */
        private static final float MIN_SCALE = 0.75f;

        @Override
        public void transformPage(final View view, final float position) {
            int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);

            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);

                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

}
