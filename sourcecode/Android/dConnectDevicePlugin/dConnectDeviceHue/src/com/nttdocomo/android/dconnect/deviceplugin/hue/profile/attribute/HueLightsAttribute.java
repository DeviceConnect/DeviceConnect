/*
HueLightsAttribute
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

package com.nttdocomo.android.dconnect.deviceplugin.hue.profile.attribute;

import java.util.List;
import java.util.Map;

import android.content.Intent;

import com.nttdocomo.android.dconnect.deviceplugin.hue.HueDeviceService;
import com.nttdocomo.android.dconnect.deviceplugin.hue.control.HueControlBridge;
import com.nttdocomo.android.dconnect.deviceplugin.util.DcLoggerHue;
import com.nttdocomo.android.dconnect.message.MessageUtils;
import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;

/**
 * 複数HueライトAttribute.
 * @author NTT DOCOMO, INC.
 */
public class HueLightsAttribute extends HueCommonAttribute {

    /**
     * ロガー.
     */
    private DcLoggerHue mLogger = new DcLoggerHue();

    /**
     * Constructor.
     * 
     * @param context context
     */
    public HueLightsAttribute(final HueDeviceService context) {
        super(context);
    }

    /**
     * ライト検索.
     * 
     * @param request request
     * @param response response
     * @return 検索結果(true or false)
     */
    public boolean searchLights(final Intent request, final Intent response) {

        boolean isSyncResponse = true;

        mLogger.entering(this, "searchLights", new Object[] {request, response});

        String deviceid = getDeviceID(request);

        HueControlBridge controlBridge = new HueControlBridge();

        PHBridge bridge = controlBridge.getBridgeSync(deviceid);

        if (bridge == null) {

            mLogger.exiting(this, "searchLights ブリッジが見つかりません");
            MessageUtils.setUnknownError(response, "ブリッジが見つかりません");

            return isSyncResponse;
        } else {
            bridge.findNewLights(new PHLightListenerNewLights(response));
            
            isSyncResponse = false;
        }

        mLogger.exiting(this, "searchLights", isSyncResponse);

        return isSyncResponse;
    }

    /**
     * The light listener object.
     * 
     * 
     */
    class PHLightListenerNewLights implements PHLightListener {

        /**
         * レスポンスインスタンス.
         */
        private Intent mResponse;

        /**
         * 新規ライト取得用.
         * 
         * @param response response
         */
        public PHLightListenerNewLights(final Intent response) {
            super();

            mLogger.entering(this, "SampleLightListener");

            mResponse = response;

            mLogger.exiting(this, "SampleLightListener");
        }

        /**
         * The callback method for error.
         * 
         * @param code the error code
         * @param message the error message
         */
        @Override
        public void onError(final int code, final String message) {

            String errMsg = "新しいライトの検索に失敗しました hue:code = " 
                    + Integer.toString(code) + "  message = " + message;

            mLogger.fine(this, "PHLightListenerNewLights onError", errMsg);

            MessageUtils.setUnknownError(mResponse, errMsg);
            sendResultERR(mResponse);

        }

        /**
         * Called to convey success without any data from bridge.
         */
        @Override
        public void onSuccess() {

            mLogger.fine(this, "onSuccess", "");

        }

        /**
         * The light headers received callback.
         * 
         * @param lightHeaders the array list of {@link PHBridgeResource}
         * 
         */
        @Override
        public void onReceivingLights(final List<PHBridgeResource> lightHeaders) {

            mLogger.fine(this, "onReceivingLights", lightHeaders);

            // 検索開始をOKとする
            sendResultOK(mResponse);

        }

        /**
         * Indicates search is complete.
         */
        @Override
        public void onSearchComplete() {

            mLogger.fine(this, "onSearchComplete", "");

        }

        @Override
        public void onStateUpdate(final Map<String, String> arg0, final List<PHHueError> arg1) {

            mLogger.fine(this, "onStateUpdate", arg1);
            // 検索開始でOKを返しているのでここでは返さない
            
        }

        @Override
        public void onReceivingLightDetails(final PHLight arg0) {
            
        }
    }
}
