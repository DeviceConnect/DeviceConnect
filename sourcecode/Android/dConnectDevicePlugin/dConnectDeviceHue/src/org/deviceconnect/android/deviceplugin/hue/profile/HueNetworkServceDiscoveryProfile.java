/*
HueNetworkServceDiscoveryProfile
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

package org.deviceconnect.android.deviceplugin.hue.profile;

import java.util.ArrayList;
import java.util.List;

import org.deviceconnect.android.profile.NetworkServiceDiscoveryProfile;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.profile.NetworkServiceDiscoveryProfileConstants;

import android.content.Intent;
import android.os.Bundle;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHHueSDK;

/**
 * スマートデバイス検索機能を提供するAPI.>
 * @author NTT DOCOMO, INC.
 */
public class HueNetworkServceDiscoveryProfile extends NetworkServiceDiscoveryProfile {

    @Override
    protected boolean onGetGetNetworkServices(final Intent request, final Intent response) {
        PHHueSDK hueSDK = PHHueSDK.getInstance();
        List<PHAccessPoint> allAccessPointList = hueSDK.getAccessPointsFound();
        List<Bundle> services = new ArrayList<Bundle>();
        for (PHAccessPoint accessPoint : allAccessPointList) {
            if (!hueSDK.isAccessPointConnected(accessPoint)) {
                continue;
            }
            Bundle service = new Bundle();
            service.putString(NetworkServiceDiscoveryProfileConstants.PARAM_ID, accessPoint.getIpAddress());
            service.putString(NetworkServiceDiscoveryProfileConstants.PARAM_NAME, "hue " + accessPoint.getMacAddress());
            service.putString(NetworkServiceDiscoveryProfileConstants.PARAM_TYPE, "wifi");
            service.putBoolean(NetworkServiceDiscoveryProfileConstants.PARAM_ONLINE, true);
            services.add(service);
        }
        // レスポンスを設定
        setServices(response, services);
        setResult(response, DConnectMessage.RESULT_OK);
        return true;
    }

}
