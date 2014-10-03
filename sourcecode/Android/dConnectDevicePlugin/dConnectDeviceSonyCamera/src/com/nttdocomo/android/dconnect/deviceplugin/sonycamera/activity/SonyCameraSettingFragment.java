package com.nttdocomo.android.dconnect.deviceplugin.sonycamera.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nttdocomo.android.dconnect.deviceplugin.sonycamera.R;
import com.nttdocomo.android.dconnect.deviceplugin.sonycamera.utils.DConnectMessageHandler;
import com.nttdocomo.android.dconnect.deviceplugin.sonycamera.utils.DConnectUtil;
import com.nttdocomo.android.dconnect.deviceplugin.sonycamera.utils.UserSettings;
import com.nttdocomo.android.dconnect.profile.NetworkServiceDiscoveryProfile;
import com.nttdocomo.dconnect.message.DConnectMessage;

/**
SonyCameraSettingFragment
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/
public class SonyCameraSettingFragment extends SonyCameraBaseFragment {

    /** ロガー. */
    private final Logger mLogger = Logger.getLogger("dconnect.dplugin");
    /**
     * インターバル.
     */
    private static final int INTERVAL = 1000;

    /** デバイスIDを表示するためのView. */
    private TextView mDeviceIdView;

    /** Wifi管理クラス. */
    private WifiManager wifiMgr;
    /** 設定を保持するクラス. */
    private UserSettings settings;

    /** Wifiの状態通知を受け取るReceiver. */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            // String action = intent.getAction();
            // if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
            // int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
            // WifiManager.WIFI_STATE_UNKNOWN);
            // if (state == WifiManager.WIFI_STATE_ENABLED) {
            // connectSonyCamera();
            // }
            // }
        }
    };

    @Override
    public View onCreateView(final LayoutInflater inflater, 
            final ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_wifi, container, false);

        settings = new UserSettings(getActivity());
        wifiMgr = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);

        mDeviceIdView = (TextView) view.findViewById(R.id.camera_id);

        final Button searchBtn = (Button) view.findViewById(R.id.search_and_connect_button);
        searchBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (DConnectUtil.checkSSID(wifiMgr.getConnectionInfo().getSSID())) {
                    mDeviceIdView.setText("Sonyカメラは接続済みです。");
                } else {
                    connectSonyCamera();
                }
            }
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        getActivity().registerReceiver(mReceiver, filter);
    }

    /**
     * 周りに存在するWiFiを探索する. 探索結果は、WiFiDeviceListFragmentに表示する。
     */
    private void searchSonyCamera() {
        mLogger.entering(this.getClass().getName(), "searchSonyCamera");

        if (isShowProgressDialog()) {
            mLogger.exiting(this.getClass().getName(), "searchSonyCamera", null);
            return;
        }

        showProgressDialog();

        DConnectUtil.asyncSearchDevice(new DConnectMessageHandler() {
            @Override
            public void handleMessage(final DConnectMessage message) {

                if (message == null) {
                    return;
                }

                int result = message.getInt(DConnectMessage.EXTRA_RESULT);
                if (result == DConnectMessage.RESULT_OK) {
                    List<Object> services = message.getList(NetworkServiceDiscoveryProfile.PARAM_SERVICES);
                    if (services.size() == 0) {
                        searchSonyCamera();
                    } else {
                        dismissProgressDialog();
                        for (int i = 0; i < services.size(); i++) {
                            HashMap<?, ?> service = (HashMap<?, ?>) services.get(i);
                            String name = (String) service.get(NetworkServiceDiscoveryProfile.PARAM_NAME);
                            if (name != null && name.equals("Sony Camera")) {
                                String id = (String) service.get(NetworkServiceDiscoveryProfile.PARAM_ID);
                                if (mDeviceIdView != null) {
                                    mDeviceIdView.setText("Sonyカメラに接続されました。");
                                    setDeviceId(id);
                                }
                            }
                        }
                    }
                } else {
                    mLogger.warning("error: result=" + result);
                    mDeviceIdView.setText("Sonyカメラが見つかりません。");
                }
            }
        });

        mLogger.exiting(this.getClass().getName(), "searchSonyCamera");
    }

    /**
     * SonyCameraデバイスに接続を行います.
     */
    private void connectSonyCamera() {
        if (!wifiMgr.isWifiEnabled()) {
            confirmConnectWifi();
        } else {
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
            mDeviceIdView.setText("Sonyカメラに接続中......");
            if (DConnectUtil.checkSSID(wifiInfo.getSSID())) {
                searchSonyCamera();
            } else {
                searchSonyCameraWifi();
            }
        }
    }

    /**
     * SonyCameraデバイスのWifiを探索してに接続を行います.
     */
    private void searchSonyCameraWifi() {
        List<ScanResult> scanList = new ArrayList<ScanResult>();
        wifiMgr.startScan();
        List<ScanResult> results = wifiMgr.getScanResults();
        for (ScanResult result : results) {
            if (DConnectUtil.checkSSID(result.SSID)) {
                scanList.add(result);
                mLogger.fine("Found SonyCamera Wifi. SSID=" + result.SSID);
            }
        }

        if (scanList.size() > 0) {
            confirmConnectSonyCameraWifi(scanList);
        } else {
            showErrorDialog("SonyCameraデバイスのWifiが見つかりません。\nSonyCameraデバイスの電源を入れてください。");
        }
    }

    /**
     * SonyCameraデバイスのWifiへの接続確認を行う.
     * 
     * @param configs Wifiの設定一覧
     */
    private void confirmConnectSonyCameraWifi(final List<ScanResult> configs) {
        String[] wifiList = new String[configs.size()];
        for (int i = 0; i < configs.size(); i++) {
            wifiList[i] = configs.get(i).SSID;
            wifiList[i] = wifiList[i].replace("\"", "");
        }

        final int[] pos = new int[1];
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setTitle("Wifi接続確認");
        builder.setSingleChoiceItems(wifiList, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int whichButton) {
                pos[0] = whichButton;
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int whichButton) {
                connectWifi(configs.get(pos[0]));
            }
        });
        builder.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int whichButton) {
            }
        });
        builder.setCancelable(true);
        builder.show();
    }

    /**
     * Wifi機能を入れる確認を行う.
     */
    private void confirmConnectWifi() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setTitle("Wi-Fi接続確認");
        builder.setMessage("Wi-Fiの機能をONにしても良いでしょうか？");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int whichButton) {
                turnOnWifi();
            }
        });
        builder.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int whichButton) {
            }
        });
        builder.setCancelable(true);
        builder.show();
    }

    /**
     * エラーダイアログを表示する.
     * 
     * @param message エラーメッセージ
     */
    private void showErrorDialog(final String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setTitle("Wifi接続確認");
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int whichButton) {
            }
        });
        builder.setCancelable(true);
        builder.show();
    }

    /**
     * パスワードを入力するダイアログを表示する.
     * 
     * @param password パスワード
     * @param listener リスナー
     */
    private void showPasswordDialog(final String password, final PasswordListener listener) {
        final EditText editView = new EditText(getActivity());
        if (password != null) {
            String ps = password.replace("\"", "");
            editView.setText(ps);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle("パスワード入力ダイアログ");
        builder.setView(editView).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int whichButton) {
                String password = "\"" + editView.getText() + "\"";
                if (listener != null) {
                    listener.onIntputPassword(password);
                }
            }
        });
        builder.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int whichButton) {
                if (listener != null) {
                    listener.onCancel();
                }
            }
        });
        builder.show();
    }

    /**
     * 指定されたSSIDのWiFiに接続を行う.
     * 
     * @param result 接続先のSSID
     */
    private void connectWifi(final ScanResult result) {
        final WifiConfiguration wc = new WifiConfiguration();
        String capabilities = result.capabilities;
        String ssid = "\"" + result.SSID + "\"";
        if (capabilities.contains("WPA")) {
            String password = settings.getSSIDPassword(ssid);
            showPasswordDialog(password, new PasswordListener() {
                @Override
                public void onIntputPassword(final String password) {
                    wc.SSID = "\"" + result.SSID + "\"";
                    wc.preSharedKey = password;
                    wc.hiddenSSID = true;
                    wc.status = WifiConfiguration.Status.ENABLED;
                    wc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                    wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                    wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                    wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
                    wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                    wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                    wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                    wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                    wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                    wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                    testConnectWifi(wc, password);
                }

                @Override
                public void onCancel() {
                }
            });
            mDeviceIdView.setText("Sonyカメラに接続しました。");
        } else if (capabilities.contains("WEP")) {
            String password = settings.getSSIDPassword(ssid);
            showPasswordDialog(password, new PasswordListener() {
                @Override
                public void onIntputPassword(final String password) {
                    wc.SSID = "\"" + result.SSID + "\"";
                    wc.wepKeys[0] = password;
                    wc.wepTxKeyIndex = 0;
                    wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                    wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                    testConnectWifi(wc, password);
                }

                @Override
                public void onCancel() {
                }
            });
            mDeviceIdView.setText("Sonyカメラに接続しました。");
        } else {
            wc.SSID = "\"" + result.SSID + "\"";
            wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            testConnectWifi(wc, null);
        }
        mDeviceIdView.setText("Sonyカメラに接続しました。");
    }

    /**
     * 指定されたネットワークが反映されているかをチェックして、接続を行う.
     * 
     * @param networkId ネットワークID
     * @param targetSSID 接続するSSID
     * @return 接続に成功した場合はtrue、それ以外はfalse
     */
    private boolean connectWifi(final int networkId, final String targetSSID) {
        String ssid = targetSSID.replace("\"", "");
        wifiMgr.startScan();
        for (ScanResult result : wifiMgr.getScanResults()) {
            if (result.SSID.replace("\"", "").equals(ssid)) {
                WifiInfo info = wifiMgr.getConnectionInfo();
                if (info != null) {
                    wifiMgr.disableNetwork(info.getNetworkId());
                }
                return wifiMgr.enableNetwork(networkId, true);
            }
        }
        return false;
    }

    /**
     * 指定されたWifiに接続確認を行う.
     * 
     * @param wifiConfig wifi設定
     * @param password パスワード
     */
    private void testConnectWifi(final WifiConfiguration wifiConfig, final String password) {
        final int networkId = wifiMgr.addNetwork(wifiConfig);
        if (networkId != -1) {
            wifiMgr.saveConfiguration();
            wifiMgr.updateNetwork(wifiConfig);

            // addNetworkしてから少し待たないと設定が反映されないので、
            // ここで、わざと少し待つ
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(INTERVAL);
                    } catch (InterruptedException e) {
                        return;
                    }
                    if (!connectWifi(networkId, wifiConfig.SSID)) {
                        connectSonyCamera();
                    } else {
                        if (password != null) {
                            settings.setSSIDPassword(wifiConfig.SSID, password);
                        }
                    }
                }
            }).start();
        } else {
            showErrorDialog("Wifiがつながらない");
        }
    }

    /**
     * Wifiの機能を有効にする.
     */
    private void turnOnWifi() {
        wifiMgr.setWifiEnabled(true);
    }

    /**
     * パスワードの入力完了リスナー.
     */
    private interface PasswordListener {
        /**
         * 入力されたパスワードを通知する.
         * 
         * @param password 入力されたパスワード
         */
        void onIntputPassword(String password);

        /**
         * 入力がキャンセルされたことを通知する.
         */
        void onCancel();
    }
}
