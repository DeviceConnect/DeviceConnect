/*
 SWNetworkServiceDiscoveryProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.sw.profile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.deviceconnect.android.deviceplugin.util.DcLoggerSW;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;

import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.android.profile.NetworkServiceDiscoveryProfile;
import org.deviceconnect.message.DConnectMessage;
import com.sonyericsson.extras.liveware.extension.util.registration.RegistrationAdapter;

/**
 * SonySWデバイスプラグインの{@link NetworkServiceDiscoveryProfile}実装.
 * @author NTT DOCOMO, INC.
 */
public class SWNetworkServiceDiscoveryProfile extends NetworkServiceDiscoveryProfile {

    /** ロガー. */
    private DcLoggerSW mLogger = new DcLoggerSW();

    @Override
    protected boolean onGetGetNetworkServices(final Intent request, final Intent response) {

        mLogger.entering(this, "onGetGetNetworkServices");

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            return true;
        }
        List<Bundle> services = new ArrayList<Bundle>();
        Set<BluetoothDevice> bondedDevices = adapter.getBondedDevices();
        if (bondedDevices.size() > 0) {

            try {
                bondedDevicesList(bondedDevices, services);
                
                setResult(response, DConnectMessage.RESULT_OK);
                setServices(response, services);
                
            } catch (Exception e) {
                MessageUtils.setNotFoundDeviceError(response, "No device is found: " + e);
                return true;
            }

        } else {
            MessageUtils.setNotFoundDeviceError(response, "No device is found");
        }

        mLogger.exiting(this, "onGetGetNetworkServices");
        return true;
    }

    /**
     * 
     * @param bondedDevices ペアリング中デバイス一覧
     * @param services デバイス一覧送信用バンドル
     */
    private void bondedDevicesList(final Set<BluetoothDevice> bondedDevices, final List<Bundle> services) {

        mLogger.entering(this, "bondedDevicesList");

        for (BluetoothDevice device : bondedDevices) {
            String deviceName = device.getName();
            mLogger.info(this, "bondedDevicesList deviceName", deviceName);
            if (deviceName.startsWith("SmartWatch")) {
                Bundle bundle = SWUtil.toBundle(device);
                mLogger.fine(this, "bondedDevicesList", bundle);
                long hostAppId = RegistrationAdapter.
                        getHostApplication(getContext(), SWUtil.toHostAppPackageName(deviceName)).getId(); 
                boolean connected = SWUtil.checkDeviceConnecting(getContext(), hostAppId);
                if (connected) {
                    mLogger.fine(this, "○ bondedDevicesList deviceConnected:", connected);
                    services.add(bundle);
                } else {
                    mLogger.fine(this, "× bondedDevicesList deviceDisConnected:", connected);
                }
            }
        }

        mLogger.exiting(this, "bondedDevicesList");
    }

}
