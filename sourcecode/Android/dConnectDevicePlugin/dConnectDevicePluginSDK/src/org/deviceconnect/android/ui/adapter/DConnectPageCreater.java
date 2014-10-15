/*
 DConnectPageCreater.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.ui.adapter;


/**
 * ViewPagerの画面作成インターフェース.
 * 
 * @param <T> ページの型
 * @author NTT DOCOMO, INC.
 */
public interface DConnectPageCreater<T> {

    /**
     * ページ数を返す.
     * 
     * @return ページ数
     */
    int getPageCount();

    /**
     * 一画面用のビューを作成する.
     * 
     * @param position 画面番号
     * @return 画面のオブジェクト
     */
    T createPage(int position);
}
