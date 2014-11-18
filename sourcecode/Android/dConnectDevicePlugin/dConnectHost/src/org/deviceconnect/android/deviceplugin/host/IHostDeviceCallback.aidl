
package org.deviceconnect.android.deviceplugin.host;

oneway interface IHostDeviceCallback { 
    void changeHostStatus(int status);
    void invokeHost(String ipaddress);
    void findHost(String ipaddress);
}
