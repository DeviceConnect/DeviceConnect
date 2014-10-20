/*
UserSettings
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

package org.deviceconnect.android.deviceplugin.sonycamera.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * UserSettings.
 * @author NTT DOCOMO, INC.
 */
public class UserSettings {
    /** プリファレンス名. */
    private static final String PREF_NAME = "qx10_shared_pref";
    /** SSIDのキー名. */
    private static final String KEY_SSID = "ssid";

    /** SharedPreferencesのインスタンス. */
    private SharedPreferences pref;
    /** SharedPreferences.Editorのインスタンス. */
    private SharedPreferences.Editor editor;

    /**
     * コンストラクタ.
     * @param context このクラスが属するコンテキスト
     */
    public UserSettings(final Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
    }

    /**
     * 登録されているSSIDを取得する.
     * SSIDが登録されていない場合にはnullを返す。
     * @return SSID
     */
    public String getSSID() {
        return pref.getString(KEY_SSID, null);
    }

    /**
     * SSIDを登録する.
     * 古いSSIDは上書きする。
     * @param ssid 登録するSSID
     */
    public void setSSID(final String ssid) {
        editor = pref.edit();
        editor.putString(KEY_SSID, ssid);
        editor.commit();
    }
    /**
     * SSIDのパスワードを設定する.
     * @param ssid SSID
     * @param password パスワード
     */
    public void setSSIDPassword(final String ssid, final String password) {
        editor = pref.edit();
        editor.putString(ssid, password);
        editor.commit();
    }
    /**
     * SSIDのパスワードを取得する.
     * @param ssid SSID
     * @return パスワード
     */
    public String getSSIDPassword(final String ssid) {
        return pref.getString(ssid, null);
    }
}
