
package com.nttdocomo.android.dconnect.deviceplugin.host;

import com.nttdocomo.android.dconnect.deviceplugin.host.IHostDeviceCallback;

interface IHostDeviceService { 
    oneway void registerCallback(IHostDeviceCallback callback); 
    oneway void unregisterCallback(IHostDeviceCallback callback);
    void searchHost();
    void invokeHost();
    int getHostStatus();
}
