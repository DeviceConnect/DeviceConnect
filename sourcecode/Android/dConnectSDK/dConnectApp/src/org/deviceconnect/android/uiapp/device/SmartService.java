/*
 SmartService.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.uiapp.device;

/**
 * スマートサービス(プロファイル).
 */
public class SmartService {

    /**
     * プロファイル名.
     */
    private String mName;

    /**
     * コンストラクタ.
     * @param name プロファイル名
     */
    public SmartService(final String name) {
        mName = name;
    }

    @Override
    public String toString() {
        return mName;
    }

    /**
     * プロファイル名を設定する.
     * @param name プロファイル名
     */
    public void setName(final String name) {
        mName = name;
    }

    /**
     * プロファイル名を取得する.
     * @return プロファイル名
     */
    public String getName() {
        return mName;
    }

    /**
     * アイコンを取得する.
     * @return アイコンID
     */
    public int getIconId() {
        return android.R.drawable.ic_menu_info_details;
    }

}
