/*
 HostConnectProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */

package com.nttdocomo.android.dconnect.deviceplugin.host.profile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;

import com.nttdocomo.android.dconnect.deviceplugin.host.HostDeviceService;
import com.nttdocomo.android.dconnect.deviceplugin.host.activity.BluetoothManageActivity;
import com.nttdocomo.android.dconnect.event.EventError;
import com.nttdocomo.android.dconnect.event.EventManager;
import com.nttdocomo.android.dconnect.message.MessageUtils;
import com.nttdocomo.android.dconnect.profile.ConnectProfile;
import com.nttdocomo.dconnect.message.DConnectMessage;
import com.nttdocomo.dconnect.message.intent.message.IntentDConnectMessage;

/**
 * Connect プロファイル.
 * @author NTT DOCOMO, INC.
 */
public class HostConnectProfile extends ConnectProfile {

    /** Debug Tag. */
    private static final String TAG = "HOST";

    /**
     * Bluetooth Adapter.
     */
    private BluetoothAdapter mBluetoothAdapter;

    /** Error. */
    private static final int ERROR_VALUE_IS_NULL = 100;
    
    /**
     * コンストラクタ.
     * 
     * @param bluetoothAdapter Bluetoothアダプタ.
     */
    public HostConnectProfile(final BluetoothAdapter bluetoothAdapter) {
        mBluetoothAdapter = bluetoothAdapter;
    }

    @Override
    protected boolean onGetWifi(final Intent request, final Intent response, final String deviceId) {

        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
            getEnabledOfWiFi(request, response);
            setResult(response, IntentDConnectMessage.RESULT_OK);
        }
        return true;
    }

    @Override
    protected boolean onGetBluetooth(final Intent request, final Intent response, final String deviceId) {

        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
            getEnabledBluetooth(request, response);
            setResult(response, IntentDConnectMessage.RESULT_OK);
        }
        return true;
    }

    @Override
    protected boolean onGetBLE(final Intent request, final Intent response, final String deviceId) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
            getEnabledOfBluetoothLowEnery(request, response);
            setResult(response, IntentDConnectMessage.RESULT_OK);
        }
        return true;
    }
    
    @Override
    protected boolean onGetNFC(final Intent request, final Intent response, final String deviceId) {
    
       if (deviceId == null) {
           createEmptyDeviceId(response);
       } else if (!checkDeviceId(deviceId)) {
           createNotFoundDevice(response);
       } else {
           NfcAdapter adapter = NfcAdapter.getDefaultAdapter(this.getContext());
           if (adapter != null) {
               if (adapter.isEnabled()) {
                   response.putExtra("enabled", "true");
               } else {
                   response.putExtra("enabled", "false");
               }
           } else {
               response.putExtra("enabled", "false");
           }
           setResult(response, IntentDConnectMessage.RESULT_OK);
       }
       
       return true;
       
    }
    
    
    @Override
    protected boolean onPutWifi(final Intent request, final Intent response, final String deviceId) {


        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
            setEnabledOfWiFi(request, response, true);
            setResult(response, IntentDConnectMessage.RESULT_OK);
        }
        return true;
    }

    @Override
    protected boolean onPutBluetooth(final Intent request, final Intent response, final String deviceId) {

        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
           setEnabledBluetooth(request, response, true);
           setResult(response, IntentDConnectMessage.RESULT_OK);
        }
        return true;
    }
    
    @Override
    protected boolean onPutBLE(final Intent request, final Intent response, final String deviceId) {
        
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
           
           setEnabledBluetooth(request, response, true);
           setResult(response, IntentDConnectMessage.RESULT_OK);
        }
        return true;
    }

    
    @Override
    protected boolean onDeleteWifi(final Intent request, final Intent response, final String deviceId) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
            setEnabledOfWiFi(request, response, false);
        }
        return true;
    }
    
    @Override
    protected boolean onDeleteBluetooth(final Intent request, final Intent response, final String deviceId) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
            setEnabledBluetooth(request, response, false);
        }
        return true;
    }
    
    @Override
    protected boolean onDeleteBLE(final Intent request, final Intent response, final String deviceId) {
        
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
            setEnabledBluetooth(request, response, false);
        }
        return true;
    }
    
    @Override
    protected boolean onDeleteNFC(final Intent request, final Intent response, final String deviceId) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
            setEnabledNfc(request, response, false);
        }
        return true;
    }
    
    @Override
    protected boolean onPutOnWifiChange(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {
        
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (sessionKey == null) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
            // イベントの登録
            EventError error = EventManager.INSTANCE.addEvent(request);

            if (error == EventError.NONE) {
                ((HostDeviceService) getContext()).setDeviceId(deviceId);
                setResult(response, DConnectMessage.RESULT_OK);
                return true;
            } else {
                setResult(response, DConnectMessage.RESULT_ERROR);
                return true;
            }
            
        }
        
        return true;
    }
    
    @Override
    protected boolean onPutOnBluetoothChange(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {
        
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (sessionKey == null) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else {
            
         
            // イベントの登録
            EventError error = EventManager.INSTANCE.addEvent(request);

            if (error == EventError.NONE) {
                ((HostDeviceService) getContext()).setDeviceId(deviceId);
                setResult(response, DConnectMessage.RESULT_OK);
            } else {
                setResult(response, DConnectMessage.RESULT_ERROR);
            }
        }

        return true;
    }
    @Override
    protected boolean onDeleteOnWifiChange(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {
        
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (sessionKey == null) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else {
            // イベントの解除
            EventError error = EventManager.INSTANCE.removeEvent(request);
            if (error == EventError.NONE) {
                setResult(response, DConnectMessage.RESULT_OK);
                return true;
            } else {
                MessageUtils.setError(response, 100, "Can not unregister event.");
                return true;
            }
        }
        return true;
    }
    
    @Override
    protected boolean onDeleteOnBluetoothChange(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (sessionKey == null) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else {
            // イベントの解除
            EventError error = EventManager.INSTANCE.removeEvent(request);
            if (error == EventError.NONE) {
                setResult(response, DConnectMessage.RESULT_OK);
                return true;
            } else {
                MessageUtils.setError(response, 100, "Can not unregister event.");
                return true;
            }
        }
        return true;
    }
    
    /**
     * Bluetooth接続の状態を取得する.
     * 
     * @param request リクエスト
     * @param response レスポンス
     */
    protected void getEnabledBluetooth(final Intent request, final Intent response) {
        setResult(response, IntentDConnectMessage.RESULT_OK);
        response.putExtra("enabled", mBluetoothAdapter.isEnabled());
    }

    /**
     * Bluetooth接続の状態を設定する.
     * 
     * @param request リクエスト
     * @param response レスポンス
     * @param enabled Bluetooth接続状態
     */
    protected void setEnabledBluetooth(final Intent request, final Intent response, final boolean enabled) {
        if (enabled) {
            // enable bluetooth
            if (!mBluetoothAdapter.isEnabled()) {
                
                Intent intent = new Intent(request);
                intent.setClass(getContext(), BluetoothManageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
            } else {
                // bluetooth has already enabled
                setResult(response, IntentDConnectMessage.RESULT_OK);
            }

        } else {
            // disable bluetooth
            boolean result = mBluetoothAdapter.disable();

            // create response
            if (result) {
                setResult(response, IntentDConnectMessage.RESULT_OK);
            } else {
                setResult(response, IntentDConnectMessage.RESULT_ERROR);
            }

        }
    }
    
    

    /**
     * WiFi接続の状態を取得する.
     * 
     * @param request リクエスト
     * @param response レスポンス
     */
    protected void getEnabledOfWiFi(final Intent request, final Intent response) {
        setResult(response, IntentDConnectMessage.RESULT_OK);

        WifiManager wifiMgr = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        response.putExtra("enabled", wifiMgr.isWifiEnabled());
    }

    /**
     * Bluetooth Low Enery接続の状態を取得する.
     * 
     * @param request リクエスト
     * @param response レスポンス
     */
    protected void getEnabledOfBluetoothLowEnery(final Intent request, final Intent response) {
        
        if (this.getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            response.putExtra("enabled", "true");
        } else {
            response.putExtra("enabled", "false");
        }
    }

    /**
     * WiFi接続の状態を設定する.
     * 
     * @param request リクエスト
     * @param response レスポンス
     * @param enabled WiFi接続状態
     */
    protected void setEnabledOfWiFi(final Intent request, final Intent response, final boolean enabled) {
        setResult(response, IntentDConnectMessage.RESULT_OK);

        WifiManager wifiMgr = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        wifiMgr.setWifiEnabled(enabled);
    }
    
    /**
     * NFCの状態を変更する.
     * 
     * @param request リクエスト
     * @param response レスポンス
     * @param enabled WiFi接続状態
     */
    protected void setEnabledNfc(final Intent request, final Intent response, final boolean enabled) {
        MessageUtils.setError(response, ERROR_VALUE_IS_NULL, "not support change status of nfc.");
    }

    /**
     * デバイスIDをチェックする.
     * 
     * @param deviceId デバイスID
     * @return <code>deviceId</code>がテスト用デバイスIDに等しい場合はtrue、そうでない場合はfalse
     */
    private boolean checkDeviceId(final String deviceId) {
        String regex = HostNetworkServiceDiscoveryProfile.DEVICE_ID;
        Pattern mPattern = Pattern.compile(regex);
        Matcher match = mPattern.matcher(deviceId);

        return match.find();
    }

    /**
     * デバイスIDが空の場合のエラーを作成する.
     * 
     * @param response レスポンスを格納するIntent
     */
    private void createEmptyDeviceId(final Intent response) {
        MessageUtils.setEmptyDeviceIdError(response);
    }

    /**
     * デバイスが発見できなかった場合のエラーを作成する.
     * 
     * @param response レスポンスを格納するIntent
     */
    private void createNotFoundDevice(final Intent response) {
        MessageUtils.setNotFoundDeviceError(response);
    }

}
