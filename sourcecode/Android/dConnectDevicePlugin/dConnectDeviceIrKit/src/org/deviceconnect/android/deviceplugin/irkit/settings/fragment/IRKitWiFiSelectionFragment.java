/*
 IRKitWiFiSelectionFragment.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.irkit.settings.fragment;

import org.deviceconnect.android.deviceplugin.irkit.IRKitManager;
import org.deviceconnect.android.deviceplugin.irkit.IRKitManager.CheckingIRKitCallback;
import org.deviceconnect.android.deviceplugin.irkit.IRKitManager.GetClientKeyCallback;
import org.deviceconnect.android.deviceplugin.irkit.IRKitManager.GetNewDeviceCallback;
import org.deviceconnect.android.deviceplugin.irkit.R;
import org.deviceconnect.android.deviceplugin.irkit.settings.activity.IRKitSettingActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * WiFi選択画面用フラグメント.
 * @author NTT DOCOMO, INC.
 */
public class IRKitWiFiSelectionFragment extends IRKitBaseFragment {

    /**
     * ステート定数.
     */
    private enum State {
        IDLING, GOT_DEVICE, WAITING_IRKIT_SSID, CHECKING_IRKIT,
    }

    /**
     * キー ステート.
     */
    private static final String STATE = "state";

    /**
     * ステート.
     */
    private State mState;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, 
            final Bundle savedInstanceState) {
        
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.irkit_settings_step_3, null);

        Button wifiBtn = (Button) root.findViewById(R.id.buttonWiFiOpen);
        wifiBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {
                startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
            }
        });

        synchronized (this) {
            if (savedInstanceState != null) {
                String stateName = savedInstanceState.getString(STATE);
                if (stateName == null) {
                    mState = State.IDLING;
                } else {
                    mState = State.valueOf(stateName);
                }
            } else {
                mState = State.IDLING;
            }
        }
        
        return root;
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE, mState.name());
    }

    /**
     * インターネット接続がない旨を表示する.
     */
    private void showNoNetworkError() {
        showAlert(R.string.alert_title_error, R.string.alert_message_no_network, R.string.alert_btn_close,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        getActivity().finish();
                    }
                });
    }

    /**
     * デバイス情報を新たに生成する.
     * 
     * @param clientKey クライアントキー
     */
    private void createNewDevice(final String clientKey) {
        IRKitManager.INSTANCE.createNewDevice(clientKey, new GetNewDeviceCallback() {

            @Override
            public void onGetDevice(final String deviceId, final String deviceKey) {
                
                Activity a = getActivity();
                if (a == null || !(a instanceof IRKitSettingActivity)) {
                    return;
                }
                
                final boolean success = (deviceId != null && deviceKey != null); 
                
                if (success) {
                    IRKitSettingActivity sa = (IRKitSettingActivity) a;
                    sa.setDeviceId(deviceId);
                    sa.setDeviceKey(deviceKey);
                    synchronized (IRKitWiFiSelectionFragment.this) {
                        mState = State.GOT_DEVICE;
                    }
                    
                } 
                
                a.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        closeProgress();
                        
                        if (success) {
                            showAlert(R.string.alert_title_prepared, R.string.alert_message_prepared,
                                    R.string.alert_btn_close, new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(final DialogInterface dialog, final int which) {
                                            switchViewEnable(false);
                                            synchronized (IRKitWiFiSelectionFragment.this) {
                                                mState = State.WAITING_IRKIT_SSID;
                                            }
                                            
                                        }
                                    });
                        } else {
                            showNoNetworkError();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onAppear() {
        super.onAppear();

        Activity a = getActivity();
        if (a == null || !(a instanceof IRKitSettingActivity)) {
            return;
        }

        String clientKey = ((IRKitSettingActivity) a).getClientKey();
        if (clientKey == null) {
            ConnectivityManager cm = (ConnectivityManager) a.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm.getActiveNetworkInfo();
            if (ni == null || !ni.isConnected()) {
                showNoNetworkError();
            } else {
                showProgress();
                IRKitManager.INSTANCE.fetchClientKey(new GetClientKeyCallback() {

                    @Override
                    public void onGetClientKey(final String clientKey) {
                        Activity a = getActivity();

                        if (a == null || !(a instanceof IRKitSettingActivity)) {
                            return;
                        }

                        if (clientKey != null) {
                            ((IRKitSettingActivity) a).saveClientKey(clientKey);
                            createNewDevice(clientKey);
                        } else {
                            a.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    closeProgress();
                                    showNoNetworkError();
                                }
                            });
                        }
                    }
                });
            }
        } else {
            IRKitSettingActivity sa = (IRKitSettingActivity) a;
            if (sa.getDeviceKey() == null) {
                showProgress();
                createNewDevice(clientKey);
            }
        }
    }

    @Override
    public synchronized void onEnterForeground() {
        super.onEnterForeground();
        
        if (mState == State.WAITING_IRKIT_SSID) {
            mState = State.CHECKING_IRKIT;
            showProgress();
            IRKitManager.INSTANCE.checkIfTargetIsIRKit(IRKitManager.DEVICE_HOST, 
                    new CheckingIRKitCallback() {
                
                @Override
                public void onChecked(final boolean isIRKit) {
                    
                    Activity a = getActivity();
                    if (a == null || !(a instanceof IRKitSettingActivity)) {
                        return;
                    }
                    
                    DialogInterface.OnClickListener listener = null;
                    int title, message;
                    
                    if (isIRKit) {
                        synchronized (IRKitWiFiSelectionFragment.this) {
                            mState = State.IDLING;
                        }
                        title = R.string.alert_title_prepared;
                        message = R.string.alert_message_is_irkit;
                        listener = new DialogInterface.OnClickListener() {
                            
                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {
                                switchViewEnable(true);
                            }
                        };
                    } else {
                        title = R.string.alert_title_error;
                        message = R.string.alert_message_is_not_irkit;
                        listener = new DialogInterface.OnClickListener() {
                            
                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {
                                synchronized (IRKitWiFiSelectionFragment.this) {
                                    mState = State.WAITING_IRKIT_SSID;
                                    switchViewEnable(false);
                                }
                            }
                        };
                    }
                    
                    final int ft = title;
                    final int fm = message;
                    final DialogInterface.OnClickListener fl = listener;
                    
                    a.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgress();
                            showAlert(ft, fm, R.string.alert_btn_close, fl);
                        }
                    });
                }
            });
        }
    }
}
