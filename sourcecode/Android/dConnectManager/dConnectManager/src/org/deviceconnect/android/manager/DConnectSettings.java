/*
 DConnectSettings.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.manager;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * DConnectの設定を保持するクラス.
 * @author NTT DOCOMO, INC.
 */
public final class DConnectSettings {
    /** デフォルトのホスト名を定義. */
    public static final String DEFAULT_HOST = "localhost";
    /** デフォルトのポート番号を定義. */
    public static final int DEFAULT_PORT = 4035;
    /** デフォルトのインターバルを定義. */
    public static final int DEFAULT_INTERVAL = 1000 * 60 * 5;
    /** ポート番号. */
    private int mPort = DEFAULT_PORT;
    /** ホスト名. */
    private String mHost = DEFAULT_HOST;
    /** SSL使用フラグ. */
    private boolean mSSL = false;

    /** 外部IPのアクセス権限. */
    private boolean mAllowExternalIP = false;

    /** LocalOAuthの使用フラグ. */
    private boolean mUseALocalOAuth = true;

    /** 監視時間を定義. */
    private int mObservationInterval;

    /** このクラスの唯一のインスタンス. */
    private static DConnectSettings mInstance;

    /**
     * コンストラクタ.
     * シングルトンにするのでprivateで定義.
     */
    private DConnectSettings() {
    }

    /**
     * DConnectSettingsのインスタンスを取得する.
     * @return {@link DConnectSettings}
     */
    public static synchronized DConnectSettings getInstance() {
        if (mInstance == null) {
            mInstance = new DConnectSettings();
        }
        return mInstance;
    }

    /**
     * SharedPreferencesのデータを読み込む.
     * @param context コンテキスト
     */
    public void load(final Context context) {
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName() + "_preferences",
                Context.MODE_MULTI_PROCESS);
        setHost(sp.getString(context.getString(R.string.key_settings_dconn_host), DConnectSettings.DEFAULT_HOST));
        setSSL(sp.getBoolean(context.getString(R.string.key_settings_dconn_ssl), false));
        setUseALocalOAuth(sp.getBoolean(context.getString(R.string.key_settings_dconn_local_oauth), true));
        setAllowExternalIP(sp.getBoolean(context.getString(R.string.key_settings_dconn_allow_external_ip), false));
        try {
            setObservationInterval(Integer.parseInt(sp.getString(
                    context.getString(R.string.key_settings_dconn_observation_interval),
                    String.valueOf(DEFAULT_INTERVAL))));
        } catch (NumberFormatException e) {
            setObservationInterval(DConnectSettings.DEFAULT_INTERVAL);
        }
        try {
            setPort(Integer.parseInt(sp.getString(context.getString(R.string.key_settings_dconn_port), "4035")));
        } catch (NumberFormatException e) {
            setPort(DConnectSettings.DEFAULT_PORT);
        }
    }

    /**
     * ポート番号を取得する.
     * @return ポート番号
     */
    public int getPort() {
        return mPort;
    }

    /**
     * ポート番号を設定する.
     * @param port ポート番号
     */
    public void setPort(final int port) {
        this.mPort = port;
    }

    /**
     * ホスト名を取得する.
     * @return ホスト名
     */
    public String getHost() {
        return mHost;
    }

    /**
     * ホスト名を設定する.
     * @param host ホスト名
     */
    public void setHost(final String host) {
        this.mHost = host;
    }

    /**
     * SSL使用フラグを取得する.
     * @return SSL使用フラグ
     */
    public boolean isSSL() {
        return mSSL;
    }

    /**
     * SSL使用フラグを設定する.
     * @param ssl SSL使用フラグ
     */
    public void setSSL(final boolean ssl) {
        this.mSSL = ssl;
    }

    /**
     * LocalOAuth使用フラグを取得する.
     * @return 使用する場合はtrue、それ以外はfalse
     */
    public boolean isUseALocalOAuth() {
        return mUseALocalOAuth;
    }

    /**
     * Local OAuth使用フラグを設定する.
     * @param useALocalOAuth 使用する場合はtrue、それ以外はfalse
     */
    public void setUseALocalOAuth(final boolean useALocalOAuth) {
        this.mUseALocalOAuth = useALocalOAuth;
    }

    /**
     * 外部IP承認フラグを取得する.
     * <p>
     * デフォルトではfalseに設定されている。
     * </p>
     * @return trueの場合は許可、falseの場合は不許可
     */
    public boolean alowExternalIP() {
        return mAllowExternalIP;
    }

    /**
     * 外部IP承認フラグを設定する.
     * <p>
     * デフォルトではfalseに設定されている。
     * </p>
     * @param allow trueの場合は許可、falseの場合は不許可
     */
    public void setAllowExternalIP(final boolean allow) {
        this.mAllowExternalIP = allow;
    }

    /**
     * 監視するインターバルを取得する.
     * @return インターバル
     */
    public int getObservationInterval() {
        return mObservationInterval;
    }

    /**
     * 監視するインターバルを設定する.
     * @param interval インターバル
     */
    public void setObservationInterval(final int interval) {
        mObservationInterval = interval;
    }
}
