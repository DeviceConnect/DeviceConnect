
package org.deviceconnect.android.deviceplugin.host;

import org.deviceconnect.android.deviceplugin.host.IHostDeviceCallback;

interface IHostDeviceService { 
    oneway void registerCallback(IHostDeviceCallback callback); 
    oneway void unregisterCallback(IHostDeviceCallback callback);
    void searchHost();
    void invokeHost();
    int getHostStatus();
}
