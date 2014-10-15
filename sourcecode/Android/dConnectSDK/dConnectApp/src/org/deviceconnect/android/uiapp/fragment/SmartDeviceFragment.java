/*
 SmartDeviceFragment.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.uiapp.fragment;

import java.util.logging.Logger;

import org.apache.http.HttpHost;
import org.deviceconnect.android.uiapp.DConnectActivity;
import org.deviceconnect.android.uiapp.R;
import org.deviceconnect.android.uiapp.device.SmartDevice;
import org.deviceconnect.message.client.DConnectClient;
import org.deviceconnect.message.http.impl.client.HttpDConnectClient;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;

/**
 * スマートデバイスプロパティインターフェース.
 */
public abstract class SmartDeviceFragment extends Fragment {

    /**
     * スマートデバイス情報.
     */
    private SmartDevice mDevice;

    /**
     * d-Connectクライアント.
     */
    private DConnectClient mDConnectClient;

    /**
     * ロガー.
     */
    private Logger mLogger = Logger.getLogger("deviceconnect.uiapp");

    /**
     * デフォルトターゲット.
     */
    private HttpHost mDefaultTarget = null;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        mLogger.entering(getClass().getName(), "onCreate", savedInstanceState);
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        mDConnectClient = new HttpDConnectClient();
        loadTargetHostSettings();

        mDevice = getArguments().getParcelable("device");
        mLogger.exiting(getClass().getName(), "onCreate");
    }

    @Override
    public void onResume() {
        mLogger.entering(getClass().getName(), "onResume");

        super.onResume();
        loadTargetHostSettings();

        mLogger.exiting(getClass().getName(), "onResume");
    }

    /**
     * スマートデバイスを取得する.
     * @return スマートデバイス
     */
    public SmartDevice getSmartDevice() {
        return mDevice;
    }

    /**
     * デフォルトホストを取得する.
     * @return ホスト
     */
    public HttpHost getDefaultHost() {
        return mDefaultTarget;
    }

    /**
     * d-Connectクライアントを取得する.
     * @return d-Connectクライアント
     */
    public DConnectClient getDConnectClient() {
        return mDConnectClient;
    }

    /**
     * アクセストークンを取得する.
     * アクセストークンがない場合にはnullを返却する。
     * @return アクセストークン
     */
    public String getAccessToken() {
        return ((DConnectActivity) getActivity()).getAccessToken();
    }

    /**
     * クライアントシークレットを取得する.
     * @return クライアントシークレット
     */
    public String getClientId() {
        return ((DConnectActivity) getActivity()).getClientId();
    }

    /**
     * ホスト名を取得する.
     * @return ホスト名
     */
    public String getHost() {
        return ((DConnectActivity) getActivity()).getHost();
    }

    /**
     * ポート番号を取得する.
     * @return ポート番号
     */
    public int getPort() {
        return ((DConnectActivity) getActivity()).getPort();
    }

    /**
     * SSLフラグ.
     * @return SSLフラグ
     */
    public boolean isSSL() {
        return ((DConnectActivity) getActivity()).isSSL();
    }

    /**
     * ターゲットホスト設定を読み込む.
     */
    private void loadTargetHostSettings() {
        mLogger.entering(getClass().getName(), "loadTargetHostSettings");

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getActivity().getApplicationContext());

        String scheme;
        if (prefs.getBoolean(getString(R.string.key_settings_dconn_ssl), false)) {
            scheme = "https";
        } else {
            scheme = "http";
        }

        String hostname = prefs.getString(
                getString(R.string.key_settings_dconn_host),
                getString(R.string.default_host));

        int port = Integer.parseInt(prefs.getString(
                getString(R.string.key_settings_dconn_port),
                getString(R.string.default_port)));

        mDefaultTarget = new HttpHost(hostname, port , scheme);

        mLogger.exiting(getClass().getName(), "loadTargetHostSettings");
    }

}
