/*
 HoldableViewPager.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.irkit.settings.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * スクロールを制限できるViewPager.
 * @author NTT DOCOMO, INC.
 */
public class HoldableViewPager extends ViewPager {
    
    
    /** 
     * スクロールできるかのフラグ.
     */
    private boolean scrollable;

    /**
     * コンストラクタ.
     * 
     * @param context コンテキスト
     */
    public HoldableViewPager(final Context context) {
        super(context);
        scrollable = true;
    }

    /**
     * コンストラクタ.
     * 
     * @param context コンテキスト
     * @param attrs 属性値
     */
    public HoldableViewPager(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        scrollable = true;
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        
        if (!scrollable) {
            return false;
        }
        
        return super.onTouchEvent(event);
    }
 
    @Override
    public boolean onInterceptTouchEvent(final MotionEvent event) {
        if (!scrollable) {
            return false;
        }
        return super.onInterceptTouchEvent(event);
    }

    /**
     * スクロールの制限を設定する.
     * 
     * @param s 制限
     */
    public void setScrollable(final boolean s) {
        this.scrollable = s;
    }
}
