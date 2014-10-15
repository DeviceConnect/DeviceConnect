/*
HueLightAttribute
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

package org.deviceconnect.android.deviceplugin.hue.profile.attribute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.deviceconnect.android.deviceplugin.hue.HueDeviceService;
import org.deviceconnect.android.deviceplugin.hue.control.HueControl;
import org.deviceconnect.android.deviceplugin.hue.control.HueControlLight;
import org.deviceconnect.android.deviceplugin.hue.param.DcParamLightHue;
import org.deviceconnect.android.deviceplugin.hue.param.DcParamLightHue.XyData;
import org.deviceconnect.android.deviceplugin.hue.util.DConnectMessageHandler;
import org.deviceconnect.android.deviceplugin.param.DcParam;
import org.deviceconnect.android.deviceplugin.param.DcParam.DcParamException;
import org.deviceconnect.android.deviceplugin.param.DcParamLight.RgbData;
import org.deviceconnect.android.deviceplugin.util.DcAsync;
import org.deviceconnect.android.deviceplugin.util.DcLoggerHue;
import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.message.DConnectMessage;

import android.content.Intent;
import android.os.Bundle;

import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLight.PHLightColorMode;
import com.philips.lighting.model.PHLightState;

/**
 * HueライトAttributeクラス.
 * @author NTT DOCOMO, INC.
 */
public class HueLightAttribute extends HueCommonAttribute {
    /**
     * 数値定義.
     */
    private static final int VALUE_THREE = 3;
    /**
     * デフォルトカラーコード.
     */
    private static final int DEFAULT_COL_VALUE = 255;

    /**
     * Constructor.
     * 
     * @param context context
     */
    public HueLightAttribute(final HueDeviceService context) {
        super(context);
    }

    /**
     * ロガー.
     */
    private DcLoggerHue mLogger = new DcLoggerHue();

    /**
     * ライトステータス更新.
     * 
     * @param request request
     * @param response response
     * @return LightStatus Update
     * @throws DcParamException DcParam
     */
    public boolean updateLightState(final Intent request, final Intent response) throws DcParamException {

        PHLightState lightState = getLightStateFromRequest(request);

        mLogger.exiting(this, "onPutRequest");

        return updateLightState(request, response, lightState);

    }

    /**
     * ライトステータスアップデート.
     * 
     * @param request request
     * @param response response
     * @param lightState lightstate
     * @return result
     * @throws DcParamException DcParam
     */
    protected boolean updateLightState(final Intent request, final Intent response, final PHLightState lightState)
            throws DcParamException {

        mLogger.entering(this, "updateLightState");

        PHLight light = getLight(request);

        if (light == null) {

            mLogger.exiting(this, "updateLightState ライトが見つかりません");
            MessageUtils.setUnknownError(response, "ライトが見つかりません");

            return SYNC_RESPONSE;

        }

        mLogger.fine(this, "updateLightState", "update light on");

        updateLightState(light, lightState, response);

        mLogger.exiting(this, "updateLightState");
        return ASYNC_RESPONSE;

    }

    /**
     * パラメータ指定された内容に従い、ライトの設定を返す.
     * 
     * @param request リクエストパラメータ
     * @return ライトの設定
     * @throws DcParamException DcParam
     */
    private PHLightState getLightStateFromRequest(final Intent request) throws DcParamException {

        mLogger.fine(this, "getLightStateFromRequest", request);

        PHLightState lightState = new PHLightState();

        // パラメータ ON
        lightState.setOn(true);

        // パラメータ config
        // 将来対応
        // if (DcParamLightHue.getConfigIsEnable(request)) {
        //
        // String config = DcParamLightHue.getConfig(request);
        //
        // mLogger.fine(this, "getLightStateFromRequest config", config);
        //
        // }

        // パラメータ RGB
        if (DcParamLightHue.getColorIsEnable(request)) {
            mLogger.fine(this, "getLightStateFromRequest RGB", DcParamLightHue.getColorValue(request));

            XyData xyData = DcParamLightHue.getColorXYValue(DcParamLightHue.getColorValue(request));

            mLogger.fine(this, "getLightStateFromRequest RGB", "x:" + xyData.getX() + " y:" + xyData.getY());

            lightState.setColorMode(PHLightColorMode.COLORMODE_XY);
            lightState.setX(xyData.getX());
            lightState.setY(xyData.getY());

        } else {
            // 指定なしは白色

            mLogger.fine(this, "getLightStateFromRequest Default White", "");

            RgbData rgbdata = new RgbData();
            rgbdata.setRedParam(DEFAULT_COL_VALUE);
            rgbdata.setGreenParam(DEFAULT_COL_VALUE);
            rgbdata.setBlueParam(DEFAULT_COL_VALUE);

            XyData xyData = DcParamLightHue.convRgbToXy(rgbdata);

            mLogger.fine(this, "getLightStateFromRequest RGB", "x:" + xyData.getX() + " y:" + xyData.getY());

            lightState.setColorMode(PHLightColorMode.COLORMODE_XY);
            lightState.setX(xyData.getX());
            lightState.setY(xyData.getY());

        }

        // パラメータ Brightness
        if (DcParamLightHue.getBrightnessIsEnable(request)) {

            int bri = DcParamLightHue.getHueBrightnessValue(request);
            mLogger.fine(this, "getLightStateFromRequest Brightness", bri);

            lightState.setBrightness(bri);
        }

        mLogger.fine(this, "getLightStateFromRequest", lightState);

        return lightState;
    }

    /**
     * ライトステータス更新.
     * 
     * @param light light
     * @param lightState lightstate
     * @param response response
     */
    protected void updateLightState(final PHLight light, final PHLightState lightState, final Intent response) {

        PHHueSDK phHueSDK = HueControl.getPHHueSDK();

        PHBridge phBridge = phHueSDK.getSelectedBridge();

        phBridge.updateLightState(light, lightState, new PHLightListener() {
            @Override
            public void onSuccess() {
                mLogger.entering(this, "updateLightState( onSuccess )");
                mLogger.exiting(this, "updateLightState( onSuccess )");
            }

            @Override
            public void onStateUpdate(final Map<String, String> arg0, final List<PHHueError> arg1) {

                mLogger.entering(this, "updateLightState( onStateUpdate )");

                sendResultOK(response);

                mLogger.exiting(this, "updateLightState( onStateUpdate )");
                
            }

            @Override
            public void onError(final int paramInt, final String paramString) {

                String errMsg = "ライトの状態更新に失敗しました hue:code = " 
                        + Integer.toString(paramInt) + "  message = " + paramString;

                mLogger.fine(this, "updateLightState onError", errMsg);

                MessageUtils.setUnknownError(response, errMsg);
                sendResultERR(response);

            }

            @Override
            public void onReceivingLightDetails(final PHLight arg0) {
                
            }

            @Override
            public void onReceivingLights(final List<PHBridgeResource> arg0) {
                
            }

            @Override
            public void onSearchComplete() {
                
            }

        });

    }

    /**
     * 指定したライトを消灯.
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @return レスポンスパラメータを送信するか否か
     * @throws DcParamException DcParam
     */
    public boolean deleteLight(final Intent request, final Intent response) throws DcParamException {

        PHLightState lightState = new PHLightState();
        lightState.setOn(false);

        return updateLightState(request, response, lightState);

    }

    /**
     * ライト取得.
     * 
     * @param request request
     * @return light
     * @throws DcParamException DcParam
     */
    private PHLight getLight(final Intent request) throws DcParamException {

        String deviceid = getDeviceID(request);
        String lightId = DcParamLightHue.getLightID(request);

        if (lightId.length() == 0) {
            DcParam.throwPramException("lightIdが指定されていません");
        }

        HueControlLight cl = new HueControlLight(mContext);
        PHLight light = cl.getLight(deviceid, lightId);

        return light;
    }

    /**
     * 名前変更.
     * 
     * @param request erquest
     * @param response response
     * @return RESPONSE
     * @throws DcParamException DcParam
     */
    public boolean changeName(final Intent request, final Intent response) throws DcParamException {

        mLogger.entering(this, "changeName");

        String name = DcParamLightHue.getName(request);

        if (name.length() == 0) {
            DcParamLightHue.throwPramException("変更後のnameが指定されていません");
        }

        PHLight light = getLight(request);

        if (light == null) {

            mLogger.exiting(this, "updateLightState ライトが見つかりません");
            MessageUtils.setUnknownError(response, "ライトが見つかりません");

            return SYNC_RESPONSE;

        }

        boolean isSyncResponse = changeName(light, name, response);

        mLogger.exiting(this, "changeName");
        return isSyncResponse;

    }

    /**
     * 名前変更.
     * 
     * @param light light
     * @param name name
     * @param response response
     * @return RESPONSE
     * @throws DcParamException DcParam
     */
    private boolean changeName(final PHLight light, final String name,
            final Intent response) throws DcParamException {

        mLogger.entering(this, "changeName");

        PHLight newLight = new PHLight(name, light.getIdentifier(), light.getVersionNumber(), light.getModelNumber());

        PHHueSDK phHueSDK = HueControl.getPHHueSDK();
        PHBridge phBridge = phHueSDK.getSelectedBridge();

        phBridge.updateLight(newLight, new PHLightListener() {

            @Override
            public void onSuccess() {

                sendResultOK(response);

            }

            @Override
            public void onStateUpdate(final Map<String, String> arg0, final List<PHHueError> arg1) {
                
            }

            @Override
            public void onError(final int code, final String message) {

                String errMsg = "ライトの名称変更に失敗しました hue:code = " 
                        + Integer.toString(code) + "  message = " + message;

                mLogger.fine(this, "changeName onError", errMsg);

                MessageUtils.setUnknownError(response, errMsg);
                sendResultERR(response);
            }


            @Override
            public void onReceivingLightDetails(final PHLight arg0) {
                
            }

            @Override
            public void onReceivingLights(final List<PHBridgeResource> arg0) {
                
            }

            @Override
            public void onSearchComplete() {
                
            }
        });

        mLogger.exiting(this, "changeName");
        return ASYNC_RESPONSE;

    }

    /**
     * 状態取得.
     * 
     * @param request request
     * @param response response
     * @return RESPONSE
     * @throws DcParamException DcParam
     */
    public boolean getState(final Intent request, final Intent response) throws DcParamException {
        mLogger.entering("HueLightAttribute", "getState");

        DcAsync.getAsyncResponse(getUrl(request), new DConnectMessageHandler() {
            @Override
            public void handleMessage(final DConnectMessage message) {
                
                if (message == null) {
//                    mLogger.fine("HueLightAttribute", "getState", "message = null");
                    
                    MessageUtils.setUnknownError(response, "hueからのレスポンスありませんでした");
                    sendResultERR(response);
                    return;
                }

//                mLogger.fine("HueLightAttribute", "getState", "message = " + message.toString());

                List<Bundle> res = makeRespose(message);
                response.putExtra(getToptag(), res.toArray(new Bundle[res.size()]));
                sendResultOK(response);
            }
        });

        mLogger.exiting("HueLightAttribute", "getState");

        return ASYNC_RESPONSE;
    }

    /**
     * タグgetter.
     * 
     * @return PARAM_LIGHT
     */
    protected String getToptag() {
        return PARAM_LIGHTS;
    }

    /**
     * Url getter.
     * 
     * @param request request
     * @return URL
     * @throws DcParamException DcParam
     */
    protected String getUrl(final Intent request) throws DcParamException {
        String deviceId = getDeviceID(request);

        return "http://" + deviceId + "/api/" + HueControl.APNAME + "/";

    }

    /**
     * レスポンス作成.
     * 
     * @param message message
     * @return reslights
     */
    protected List<Bundle> makeRespose(final DConnectMessage message) {

        List<Bundle> resLights = new ArrayList<Bundle>();

        // lights　HashMapを取得
        HashMap<?, ?> lightsMap = (HashMap<?, ?>) message.get("lights");

        List<String> sortedLightIdList = getSortedId(lightsMap);
        for (String string : sortedLightIdList) {

            resLights.add(getLightBundle(message, string));
        }

        return resLights;

    }

    /**
     * 文字列操作メソッド.
     * 
     * @param string string
     * @param len len
     * @return string
     */
    protected String right(final String string, final int len) {
        int length = string.length();

        return string.substring(length - len, length);
    }

    /**
     * ライトBundle取得.
     * 
     * @param message message
     * @param lightId lightId
     * @return bundleLight
     */
    protected Bundle getLightBundle(final DConnectMessage message, final String lightId) {
        HashMap<?, ?> lightsMap = (HashMap<?, ?>) message.get("lights");

        Bundle bundleLight = new Bundle();
        // Light ID
        bundleLight.putString(PARAM_LIGHT_ID, lightId);

        // lights 内のHashMapを取得
        HashMap<?, ?> lightData = (HashMap<?, ?>) lightsMap.get(lightId);

        // lights/name取得
        String name = (String) lightData.get("name");
        bundleLight.putString(PARAM_NAME, name);

        // lights/state のHashMapを取得
        HashMap<?, ?> lightState = (HashMap<?, ?>) lightData.get("state");

        // lights/state/on 取得
        boolean on = (Boolean) lightState.get("on");
        bundleLight.putBoolean(PARAM_ON, on);

        // 将来対応のためのConfigを空でセットしておく
        bundleLight.putString(PARAM_CONFIG, "");

        return bundleLight;
    }

    /**
     * ソート済みID取得.
     * 
     * @param baseHashMap baseHashMap
     * @return SortedID
     */
    protected List<String> getSortedId(final HashMap<?, ?> baseHashMap) {

        List<String> temp = new ArrayList<String>();

        if (baseHashMap != null) {
            for (Entry<?, ?> keyValue : baseHashMap.entrySet()) {
                temp.add(keyValue.getKey().toString());
            }
        }

        return getSortedId(temp);
    }

    /**
     * ソート済みIDリスト取得.
     * 
     * @param baseList baseList
     * @return sortedList
     */
    protected List<String> getSortedId(final List<String> baseList) {

        List<String> unSortedList = new ArrayList<String>();

        for (String string : baseList) {

            unSortedList.add(right("000" + string.trim(), VALUE_THREE));

        }

        Collections.sort(unSortedList);

        List<String> sortedList = new ArrayList<String>();

        for (String string : unSortedList) {

            sortedList.add(String.valueOf(Integer.parseInt(string)));

        }

        return sortedList;
    }

}
