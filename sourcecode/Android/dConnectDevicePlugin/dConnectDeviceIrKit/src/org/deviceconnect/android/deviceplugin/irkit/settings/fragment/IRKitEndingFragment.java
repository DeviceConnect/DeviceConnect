/*
 IRKitEndingFragment.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.irkit.settings.fragment;

import org.deviceconnect.android.deviceplugin.irkit.IRKitManager;
import org.deviceconnect.android.deviceplugin.irkit.IRKitManager.IRKitConnectionCallback;
import org.deviceconnect.android.deviceplugin.irkit.IRKitManager.IRKitConnectionCheckingCallback;
import org.deviceconnect.android.deviceplugin.irkit.R;
import org.deviceconnect.android.deviceplugin.irkit.settings.activity.IRKitSettingActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * 最後の画面のフラグメント.
 * @author NTT DOCOMO, INC.
 */
public class IRKitEndingFragment extends IRKitBaseFragment implements OnClickListener {

    /**
     * ステート定数.
     */
    private enum State {
        IDLING, CONNECTING_TO_IRKIT, WAITING_FOR_LAN, CONNECTED,
    }

    /**
     * ステート.
     */
    private State mState;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, 
            final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.irkit_settings_step_4, null);
        Button conBtn = (Button) rootView.findViewById(R.id.buttonWiFiSettings);
        conBtn.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(final View v) {

        synchronized (IRKitEndingFragment.this) {
            mState = State.CONNECTING_TO_IRKIT;
        }

        IRKitSettingActivity a = (IRKitSettingActivity) getActivity();
        if (a == null) {
            return;
        }

        showProgress();
        v.setEnabled(false);
        IRKitManager.INSTANCE.connectIRKitToWiFi(a.getSSID(), a.getPassword(), a.getSecType(), a.getDeviceKey(),
                new IRKitConnectionCallback() {

                    @Override
                    public void onConnectedToWiFi(final boolean isConnect) {

                        Activity a = getActivity();
                        if (a == null) {
                            return;
                        }

                        a.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                synchronized (IRKitEndingFragment.this) {
                                    closeProgress();
                                    if (isConnect) {
                                        showAlert(R.string.alert_title_connection,
                                                R.string.alert_message_connected_with_wifi, R.string.alert_btn_close,
                                                new DialogInterface.OnClickListener() {

                                                    @Override
                                                    public void onClick(final DialogInterface dialog, final int which) {
                                                        synchronized (IRKitEndingFragment.this) {
                                                            mState = State.WAITING_FOR_LAN;
                                                        }
                                                    }
                                                });
                                    } else {
                                        mState = State.IDLING;
                                        showAlert(R.string.alert_title_error, R.string.alert_message_network_error,
                                                R.string.alert_btn_close, new DialogInterface.OnClickListener() {

                                                    @Override
                                                    public void onClick(final DialogInterface dialog, final int which) {
                                                        v.setEnabled(true);
                                                    }
                                                });
                                    }
                                }

                            }
                        });
                    }
                });
    }

    @Override
    public synchronized void onEnterForeground() {
        super.onEnterForeground();

        if (mState == State.WAITING_FOR_LAN) {

            IRKitSettingActivity a = (IRKitSettingActivity) getActivity();
            if (a == null) {
                return;
            }

            showProgress();
            IRKitManager.INSTANCE.checkIfIRKitIsConnectedToInternet(a.getClientKey(), a.getDeviceId(),
                    new IRKitConnectionCheckingCallback() {

                        @Override
                        public void onConnectedToInternet(final boolean isConnect) {

                            Activity a = getActivity();
                            if (a == null) {
                                return;
                            }

                            a.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    synchronized (IRKitEndingFragment.this) {
                                        closeProgress();
                                        if (isConnect) {
                                            mState = State.CONNECTED;
                                            showAlert(R.string.alert_title_connection, 
                                                    R.string.alert_message_connected, 
                                                    R.string.alert_btn_close, 
                                                    new DialogInterface.OnClickListener() {
                                                        
                                                        @Override
                                                        public void onClick(
                                                                final DialogInterface dialog, 
                                                                final int which) {
                                                            Activity a = getActivity();
                                                            if (a != null) {
                                                                a.finish();
                                                            }
                                                        }
                                                    });
                                        } else {
                                            mState = State.IDLING;
                                            showAlert(R.string.alert_title_error, 
                                                    R.string.alert_message_network_error, 
                                                    R.string.alert_btn_close, 
                                                    new DialogInterface.OnClickListener() {
                                                        
                                                        @Override
                                                        public void onClick(
                                                                final DialogInterface dialog, 
                                                                final int which) {
                                                            
                                                            View root = getView();
                                                            if (root != null) {
                                                                Button conBtn = (Button) 
                                                                        root.findViewById(R.id.buttonWiFiSettings);
                                                                conBtn.setEnabled(true);
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                }
                            });
                        }
                    });
        }
    }
}
