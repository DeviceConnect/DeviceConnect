/*
HueControlLight
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

package org.deviceconnect.android.deviceplugin.hue.control;

import org.deviceconnect.android.deviceplugin.hue.HueDeviceService;

import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;

/**
 * HueControlLightクラス.
 * @author NTT DOCOMO, INC.
 */
public class HueControlLight extends HueControl {

    /**
     * mContext.
     */
    protected HueDeviceService mContext;

    /**
     * コンストラクタ.
     * @param context 
     */
    public HueControlLight(final HueDeviceService context) {
        super();
        mContext = context;
    }

    /**
     * lightオブジェクトの取得.
     * @param deviceid 
     * @param lightID 
     * @return lightオブジェクト
     */
    public PHLight getLight(final String deviceid, final String lightID) {

        HueControlBridge controlBridge = new HueControlBridge();

        PHBridge phBridge = controlBridge.getBridgeSync(deviceid);

        if (phBridge == null) {
            return null;
        }

        for (PHLight light : phBridge.getResourceCache().getAllLights()) {
            if (light.getIdentifier().equals(lightID)) {

                return light;
            }
        }

        return null;
    }

    /**
     * lightオブジェクトの取得.
     * @param deviceid 
     * @return lightオブジェクト
     */
    public PHLight getLight(final String deviceid) {
        // PHHueSDK phHueSDK = HueControl.getPHHueSDK();

        HueControlBridge controlBridge = new HueControlBridge();
        PHBridge phBridge = controlBridge.getBridgeSync(deviceid);

        if (phBridge != null) {
            for (PHLight light : phBridge.getResourceCache().getAllLights()) {
                if (light.getIdentifier().equals(deviceid)) {
                    return light;
                }

            }

        }

        return null;

    }

}
