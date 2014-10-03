package com.nttdocomo.android.dconnect.deviceplugin.hue.profile;

import java.util.ArrayList;
import java.util.List;
import android.content.Intent;
import android.os.Bundle;
import com.nttdocomo.android.dconnect.deviceplugin.hue.control.HueControlBrige;
import com.nttdocomo.android.dconnect.deviceplugin.util.DcLoggerHue;
import com.nttdocomo.android.dconnect.message.MessageUtils;
import com.nttdocomo.android.dconnect.profile.NetworkServiceDiscoveryProfile;
import com.nttdocomo.dconnect.message.DConnectMessage;
import com.philips.lighting.hue.sdk.PHAccessPoint;

/**
HueNetworkServceDiscoveryProfile
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

/**
 * 
 * スマートデバイス検索機能を提供するAPI.<br/>
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

            List<Bundle> services = getSmartDeviceList();

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

    /**
     * デバイスリスト取得.
     * 
     * @return DeviceList
     */
    private List<Bundle> getSmartDeviceList() {

        mLogger.entering(this, "getSmartDeviceList");

        List<Bundle> services = new ArrayList<Bundle>();

        services = getBridgeServices();

        if (services.size() == 0) {

            // １回で取れない場合があるので、もう一度リトライ
            services = getBridgeServices();

        }

        mLogger.exiting(this, "getSmartDeviceList size", services.size());

        return services;

    }

    /**
     * ブリッジリスト取得.
     * 
     * @return Bridge List
     */
    private List<Bundle> getBridgeServices() {

        mLogger.entering(this, "getBridgeServices");

        HueControlBrige cb = new HueControlBrige();
        List<PHAccessPoint> allAccessPointList = cb.getSyncAllAccessPoint();

        mLogger.fine(this, "getBridgeServices AllBridges", allAccessPointList);

        List<Bundle> services = new ArrayList<Bundle>();

        for (PHAccessPoint accessPoint : allAccessPointList) {

            try {

                mLogger.fine(this, "getBridgeServices header", accessPoint);
                Bundle service = new Bundle();
                service.putString("id", accessPoint.getIpAddress());
                service.putString("name", "hue " + accessPoint.getMacAddress());
                service.putString("type", "wifi");
                service.putBoolean("online", true);

                mLogger.fine(this, "getBridgeServices service", service);
                services.add(service);

            } catch (Exception e) {
                // 接続出来なかったのは無視
                // CheckStyleエラー解消用
                e.equals("Exception");
            }

        }

        mLogger.exiting(this, "getBridgeServices", services.size());

        return services;

    }

}
