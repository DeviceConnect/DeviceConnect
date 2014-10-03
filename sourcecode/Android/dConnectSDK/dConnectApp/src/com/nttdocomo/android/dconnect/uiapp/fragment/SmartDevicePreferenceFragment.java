package com.nttdocomo.android.dconnect.uiapp.fragment;

import java.util.logging.Logger;

import org.apache.http.HttpHost;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.preference.PreferenceFragment;

import com.nttdocomo.android.dconnect.uiapp.R;
import com.nttdocomo.android.dconnect.uiapp.device.SmartDevice;
import com.nttdocomo.dconnect.message.client.DConnectClient;
import com.nttdocomo.dconnect.message.http.impl.client.HttpDConnectClient;

/**
 * スマートデバイスプロパティインターフェース.
 */
public abstract class SmartDevicePreferenceFragment extends PreferenceFragment {

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
    private Logger mLogger = Logger.getLogger("dconnect.uiapp");

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
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getActivity().getApplicationContext());
        String accessToken = prefs.getString(
                getString(R.string.key_settings_dconn_access_token), null);
        return accessToken;
    }

    /**
     * デフォルトホストを取得する.
     * @return ホスト
     */
    public HttpHost getDefaultHost() {
        return mDefaultTarget;
    }

    /**
     * ターゲットホスト設定を読み込む.
     */
    private void loadTargetHostSettings() {
        mLogger.entering(getClass().getName(), "loadTargetHostSettings");

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getActivity());

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
