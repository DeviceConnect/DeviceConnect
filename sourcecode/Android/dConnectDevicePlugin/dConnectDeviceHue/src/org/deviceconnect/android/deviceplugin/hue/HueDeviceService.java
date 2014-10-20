/*
HueDeviceService
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

package org.deviceconnect.android.deviceplugin.hue;

import java.util.List;

import org.deviceconnect.android.deviceplugin.hue.control.HueControl;
import org.deviceconnect.android.deviceplugin.hue.profile.HueLightProfile;
import org.deviceconnect.android.deviceplugin.hue.profile.HueNetworkServceDiscoveryProfile;
import org.deviceconnect.android.deviceplugin.hue.profile.HueSystemProfile;
import org.deviceconnect.android.message.DConnectMessageService;
import org.deviceconnect.android.profile.NetworkServiceDiscoveryProfile;
import org.deviceconnect.android.profile.SystemProfile;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueParsingError;

/**
 * 本デバイスプラグインのプロファイルをDeviceConnectに登録するサービス.
 * @author NTT DOCOMO, INC.
 */
public class HueDeviceService extends DConnectMessageService {

    @Override
    public void onCreate() {
        super.onCreate();
        addProfile(new HueLightProfile());

        //hue SDKの初期化
        PHHueSDK hueSDK = PHHueSDK.getInstance();
        hueSDK.setAppName(HueControl.APNAME);
        hueSDK.getNotificationManager().registerSDKListener(mPhListener);

        //前もってキャッシュをupdateしておく
        PHBridgeSearchManager sm = (PHBridgeSearchManager) hueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
        sm.search(true, true);
    }

    @Override
    public void onDestroy() {
        //hue SDKの後始末
        PHHueSDK hueSDK = PHHueSDK.getInstance();
        hueSDK.getNotificationManager().unregisterSDKListener(mPhListener);
        hueSDK.disableAllHeartbeat();

        PHBridge bridge = hueSDK.getSelectedBridge();
        if (bridge != null) {
            hueSDK.disconnect(bridge);
        }
        super.onDestroy();
    }

    @Override
    protected SystemProfile getSystemProfile() {
        return new HueSystemProfile(this);
    }

    @Override
    protected NetworkServiceDiscoveryProfile getNetworkServiceDiscoveryProfile() {
        return new HueNetworkServceDiscoveryProfile();
    }

    private final PHSDKListener mPhListener = new PHSDKListener() {

        @Override
        public void onAccessPointsFound(final List<PHAccessPoint> accessPoint) {
            PHHueSDK hueSDK = PHHueSDK.getInstance();
            if (accessPoint != null && accessPoint.size() > 0) {
                hueSDK.getAccessPointsFound().clear();
                hueSDK.getAccessPointsFound().addAll(accessPoint);
            } else {
                PHBridgeSearchManager sm = (PHBridgeSearchManager) hueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
                sm.search(false, false, true);
            }
        }
        
        @Override
        public void onCacheUpdated(final List<Integer> arg0, final PHBridge arg1) {
        }

        @Override
        public void onBridgeConnected(final PHBridge b) {
            PHHueSDK phHueSDK = PHHueSDK.getInstance();
            phHueSDK.setSelectedBridge(b);
            phHueSDK.enableHeartbeat(b, PHHueSDK.HB_INTERVAL);
            phHueSDK.getLastHeartbeat().put(b.getResourceCache().getBridgeConfiguration() 
                    .getIpAddress(), System.currentTimeMillis());
        }

        @Override
        public void onAuthenticationRequired(final PHAccessPoint accessPoint) {
            PHHueSDK phHueSDK = PHHueSDK.getInstance();
            phHueSDK.startPushlinkAuthentication(accessPoint);
        }

        @Override
        public void onConnectionResumed(final PHBridge bridge) {
            PHHueSDK phHueSDK = PHHueSDK.getInstance();
            for (int i = 0; i < phHueSDK.getDisconnectedAccessPoint().size(); i++) {
                if (phHueSDK.getDisconnectedAccessPoint().get(i).getIpAddress()
                        .equals(bridge.getResourceCache().getBridgeConfiguration().getIpAddress())) {
                    phHueSDK.getDisconnectedAccessPoint().remove(i);
                }
            }
        }

        @Override
        public void onConnectionLost(final PHAccessPoint accessPoint) {
            PHHueSDK phHueSDK = PHHueSDK.getInstance();
            if (!phHueSDK.getDisconnectedAccessPoint().contains(accessPoint)) {
                phHueSDK.getDisconnectedAccessPoint().add(accessPoint);
            }
        }

        @Override
        public void onError(final int code, final String message) {
        }

        @Override
        public void onParsingErrors(final List<PHHueParsingError> errors) {
        }
    };
}
