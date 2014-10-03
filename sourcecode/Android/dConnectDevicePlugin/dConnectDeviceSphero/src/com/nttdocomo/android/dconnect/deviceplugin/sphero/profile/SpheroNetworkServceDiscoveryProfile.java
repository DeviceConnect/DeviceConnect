package com.nttdocomo.android.dconnect.deviceplugin.sphero.profile;

import java.util.Collection;

import android.content.Intent;
import android.os.Bundle;

import com.nttdocomo.android.dconnect.deviceplugin.sphero.SpheroManager;
import com.nttdocomo.android.dconnect.deviceplugin.sphero.data.DeviceInfo;
import com.nttdocomo.android.dconnect.profile.NetworkServiceDiscoveryProfile;
import com.nttdocomo.dconnect.message.DConnectMessage;

/**
 * NetworkServiceDiscovery Profile.
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
