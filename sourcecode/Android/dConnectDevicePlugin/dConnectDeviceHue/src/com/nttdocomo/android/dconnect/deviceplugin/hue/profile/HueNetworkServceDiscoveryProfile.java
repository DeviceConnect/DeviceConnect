/*
HueNetworkServceDiscoveryProfile
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

package com.nttdocomo.android.dconnect.deviceplugin.hue.profile;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;

import com.nttdocomo.android.dconnect.deviceplugin.util.DcLoggerHue;
import com.nttdocomo.android.dconnect.message.MessageUtils;
import com.nttdocomo.android.dconnect.profile.NetworkServiceDiscoveryProfile;
import com.nttdocomo.dconnect.message.DConnectMessage;
import com.nttdocomo.dconnect.profile.NetworkServiceDiscoveryProfileConstants;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHHueSDK;

/**
 * 
 * スマートデバイス検索機能を提供するAPI.>
 * 
 */
public class HueNetworkServceDiscoveryProfile extends NetworkServiceDiscoveryProfile {

    /**
     * ロガー.
     */
    private DcLoggerHue mLogger = new DcLoggerHue();

    @Override
    protected boolean onGetGetNetworkServices(final Intent request, final Intent response) {
        try {

            mLogger.entering(this, "onGetGetNetworkServices", new Object[] {request, response});

            List<PHAccessPoint> allAccessPointList = PHHueSDK.getInstance().getAccessPointsFound();
            List<Bundle> services = new ArrayList<Bundle>();
            for (PHAccessPoint accessPoint : allAccessPointList) {
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

            mLogger.exiting(this, "onGetGetNetworkServices", true);

        } catch (Exception e) {
            mLogger.warning(this, "onGetGetNetworkServices", "", e);
            setResult(response, DConnectMessage.RESULT_ERROR);
            MessageUtils.setUnknownError(response);
        }
        return true;
    }

}
