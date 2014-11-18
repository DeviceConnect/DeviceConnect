/*
 HostSettingFragment.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */

package org.deviceconnect.android.deviceplugin.host.setting;

import static android.content.Context.WIFI_SERVICE;

import org.deviceconnect.android.deviceplugin.host.BuildConfig;
import org.deviceconnect.android.deviceplugin.host.HostDeviceService;
import org.deviceconnect.android.deviceplugin.host.IHostDeviceCallback;
import org.deviceconnect.android.deviceplugin.host.IHostDeviceService;
import org.deviceconnect.android.deviceplugin.host.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author NTT DOCOMO, INC.
 */
public class HostSettingFragment extends Fragment {

    /** Debug Tag. */
    private static final String TAG = "PluginHost";

    /** HostのIPを表示するためのTextView. */
    private TextView mDeviceHostIpTextView;

    /** context. */
    Activity mActivity;

    /** PluginID. */
    String mPluginId;

    /** 検索中のダイアログ. */
    private static ProgressDialog mDialog;

    /** Handler Action. */
    private static final int HANDLER_ACTION_DISMISS = 1;

    /** プロセス間通信でつなぐService. */
    private static IHostDeviceService mService;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {

        // Activityを取得
        mActivity = this.getActivity();
        mActivity.bindService(new Intent(this.getActivity(), HostDeviceService.class), mServiceConnection,
                Context.BIND_AUTO_CREATE);

        // Positionを取得
        Bundle mBundle = getArguments();
        int mPagePosition = mBundle.getInt("position", 0);

        int mPageLayoutId = this.getResources().getIdentifier("host_setting_" + mPagePosition, "layout",
                getActivity().getPackageName());

        View mView = inflater.inflate(mPageLayoutId, container, false);

        if (mPagePosition == 0) {
            WifiManager wifiManager = (WifiManager) this.getActivity().getSystemService(WIFI_SERVICE);
            int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
            final String formatedIpAddress = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                    (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));

            // Host IP表示用
            mDeviceHostIpTextView = (TextView) mView.findViewById(R.id.host_ipaddress);
            mDeviceHostIpTextView.setText("Your IP:" + formatedIpAddress);
        }

        return mView;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    /**
     * Host PluginをSearchします.
     */
    public void searchHost() {

        showProgressDialog();
        try {
            mService.searchHost();
        } catch (RemoteException e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }
        // Handlerに通知する
        MyHandler handler = new MyHandler();
        Message mMsg = new Message();
        mMsg.what = HANDLER_ACTION_DISMISS;
        handler.sendMessageDelayed(mMsg, 8000);
    }

    /**
     * Host PluginをInvokeします.
     */
    public void invokeHost() {

        showProgressDialog();
        try {
            mService.invokeHost();
        } catch (RemoteException e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }
        dismissProgressDialog();
    }

    /**
     * コンテキストの取得する.
     * 
     * @return コンテキスト
     */
    public Context getContext() {
        return mActivity;
    }

    /**
     * プログレスバーが表示されているか.
     * 
     * @return 表示されている場合はtrue,それ以外はfalse
     */
    public boolean isShowProgressDialog() {
        return mDialog != null;
    }

    /**
     * プログレスバーを表示する.
     */
    public void showProgressDialog() {
        if (mDialog != null) {
            return;
        }
        mDialog = new ProgressDialog(getActivity());
        mDialog.setTitle("処理中");
        mDialog.setMessage("Now Loading...");
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setCancelable(false);
        mDialog.show();
    }

    /**
     * プログレスバーを非表示にする.
     */
    public void dismissProgressDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    /**
     * Handlerクラスを継承して拡張.
     */
    class MyHandler extends Handler {

        @Override
        public void handleMessage(final Message msg) {

            if (msg.what == HANDLER_ACTION_DISMISS) {
                dismissProgressDialog();

                Toast.makeText(mActivity, "Hostが発見できません。(エラー:タイムアウト, 原因:同じネットワーク内にHostsが存在しません。)", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    /**
     * プロセス間通信用のサービス.
     */
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(final ComponentName name, final IBinder service) {
            Log.i(TAG, "onServiceConnected");
            mService = IHostDeviceService.Stub.asInterface(service);
            try {
                mService.registerCallback(mCallback);
            } catch (RemoteException e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(final ComponentName name) {
            mService = null;
            try {
                mService.unregisterCallback(mCallback);
            } catch (RemoteException e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            }
        }
    };

    /**
     * プロセス間通信用のCallback.
     */
    private IHostDeviceCallback mCallback = new IHostDeviceCallback.Stub() {

        @Override
        public void changeHostStatus(final int status) throws RemoteException {
            if (status == 1) {

                dismissProgressDialog();

                Looper.prepare();
                Toast.makeText(mActivity, "Hostを発見しました。", Toast.LENGTH_SHORT).show();
                Looper.loop();

            } else if (status == -1) {

                dismissProgressDialog();

                Looper.prepare();
                Toast.makeText(mActivity, "Hostが発見できません。(エラー:タイムアウト, 原因:同じネットワーク内にIrKitが存在しません。)", Toast.LENGTH_SHORT)
                        .show();
                Looper.loop();
            }
        }

        @Override
        public void invokeHost(final String ipaddress) throws RemoteException {

            // Handlerに通知する
            mActivity.runOnUiThread(new Runnable() {
                public void run() {
                }
            });
        }

        @Override
        public void findHost(final String ipaddress) throws RemoteException {
            // Handlerに通知する
            mActivity.runOnUiThread(new Runnable() {
                public void run() {
                    // 変化させたいUIの処理
                    mDeviceHostIpTextView.setText("Find:" + ipaddress);
                }
            });
        }
    };
}
