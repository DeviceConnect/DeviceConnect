/*
 SpheroNetworkServceDiscoveryProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.sphero.profile;

import java.util.Collection;

import org.deviceconnect.android.deviceplugin.sphero.SpheroManager;
import org.deviceconnect.android.deviceplugin.sphero.data.DeviceInfo;

import android.content.Intent;
import android.os.Bundle;

import org.deviceconnect.android.profile.NetworkServiceDiscoveryProfile;
import org.deviceconnect.message.DConnectMessage;

/**
 * NetworkServiceDiscovery Profile.
 * @author NTT DOCOMO, INC.
 */
public class SpheroNetworkServceDiscoveryProfile extends NetworkServiceDiscoveryProfile {

    @Override
    protected boolean onGetGetNetworkServices(final Intent request, final Intent response) {

        Collection<DeviceInfo> devices = SpheroManager.INSTANCE.getConnectedDevices();

        Bundle[] services = new Bundle[devices.size()];
        int index = 0;
        for (DeviceInfo info : devices) {

            Bundle service = new Bundle();
            NetworkServiceDiscoveryProfile.setId(service, info.getDevice().getUniqueId());
            NetworkServiceDiscoveryProfile.setName(service, info.getDevice().getName());
            NetworkServiceDiscoveryProfile.setType(service, NetworkType.BLUETOOTH);
            NetworkServiceDiscoveryProfile.setOnline(service, true);

            services[index++] = service;
        }

        setServices(response, services);
        setResult(response, DConnectMessage.RESULT_OK);

        return true;
    }
}
