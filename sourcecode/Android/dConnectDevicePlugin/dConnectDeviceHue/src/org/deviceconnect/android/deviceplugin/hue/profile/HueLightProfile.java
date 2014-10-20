/*
HueLightProfile
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

package org.deviceconnect.android.deviceplugin.hue.profile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.android.profile.LightProfile;
import org.deviceconnect.message.DConnectMessage;

import android.content.Intent;
import android.os.Bundle;

import com.philips.lighting.hue.listener.PHGroupListener;
import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.utilities.PHUtilities;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHBridgeResourcesCache;
import com.philips.lighting.model.PHGroup;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLight.PHLightColorMode;
import com.philips.lighting.model.PHLightState;


/**
 * 親クラスで振り分けられたメソッドに対して、Hueのlight attribute処理を呼び出す.
 * @author NTT DOCOMO, INC.
 */
public class HueLightProfile extends LightProfile {

    /**
     * エラーコード301.
     */
    private static final int HUE_SDK_ERROR_301 = 301;

    @Override
    protected boolean onGetLight(final Intent request, final Intent response) {
        String deviceId = getDeviceID(request);
        PHBridge bridge = findBridge(deviceId);
        if (bridge == null) {
            MessageUtils.setNotFoundDeviceError(response, "Not found bridge: " + deviceId);
            return true;
        }
        List<Bundle> lightsParam = new ArrayList<Bundle>();
        for (PHLight light : bridge.getResourceCache().getAllLights()) {
            Bundle lightParam = new Bundle();
            lightParam.putString(PARAM_LIGHT_ID, light.getIdentifier());
            lightParam.putString(PARAM_NAME, light.getName());
            lightParam.putString(PARAM_CONFIG, "");
            PHLightState state = light.getLastKnownLightState();
            lightParam.putBoolean(PARAM_ON, state.isOn());
            lightsParam.add(lightParam);
        }
        response.putExtra(PARAM_LIGHTS, lightsParam.toArray(new Bundle[lightsParam.size()]));
        sendResultOK(response);
        return false;
    }

    @Override
    protected boolean onPostLight(final Intent request, final Intent response) {
        String deviceId = getDeviceID(request);
        String lightId = getLightID(request);

        // 必須パラメータの存在チェック
        if (lightId == null || lightId.length() == 0) {
            MessageUtils.setInvalidRequestParameterError(response, "lightId is not specified.");
            return true;
        }

        PHBridge bridge = findBridge(deviceId);
        if (bridge == null) {
            MessageUtils.setNotFoundDeviceError(response, "Not found bridge: " + deviceId);
            return true;
        }
        PHLight light = bridge.getResourceCache().getLights().get(lightId);
        if (light == null) {
            MessageUtils.setNotFoundDeviceError(response, "Not found light: " + lightId + "@" + deviceId);
            return true;
        }

        PHLightState lightState = new PHLightState();
        lightState.setOn(true);
        lightState.setColorMode(PHLightColorMode.COLORMODE_XY);
        String colorParam = getColor(request);
        if (colorParam == null) {
            colorParam = "FFFFFF";
        }
        Color hueColor = new Color(colorParam);
        lightState.setX(hueColor.x);
        lightState.setY(hueColor.y);
        String brightnessParam = getBrightness(request);
        if (brightnessParam != null) {
            lightState.setBrightness(Integer.valueOf(new Brightness(brightnessParam).value));
        }

        bridge.updateLightState(light, lightState, new PHLightAdapter() {
            @Override
            public void onStateUpdate(final Map<String, String> successAttribute, final List<PHHueError> errorAttribute) {
                sendResultOK(response);
            }

            @Override
            public void onError(final int code, final String message) {
                MessageUtils.setUnknownError(response, code + ": " + message);
                sendResultERR(response);
            }
        });
        return false;
    }

    @Override
    protected boolean onDeleteLight(final Intent request, final Intent response) {
        String deviceId = getDeviceID(request);
        String lightId = getLightID(request);

        // 必須パラメータの存在チェック
        if (lightId == null || lightId.length() == 0) {
            MessageUtils.setInvalidRequestParameterError(response, "lightId is not specified.");
            return true;
        }

        PHBridge bridge = findBridge(deviceId);
        if (bridge == null) {
            MessageUtils.setNotFoundDeviceError(response, "Not found bridge: " + deviceId);
            return true;
        }
        PHLight light = bridge.getResourceCache().getLights().get(lightId);
        if (light == null) {
            MessageUtils.setNotFoundDeviceError(response, "Not found light: " + lightId + "@" + deviceId);
            return true;
        }
        PHLightState lightState = new PHLightState();
        lightState.setOn(false);
        bridge.updateLightState(light, lightState, new PHLightAdapter() {
            @Override
            public void onStateUpdate(final Map<String, String> successAttribute, final List<PHHueError> errorAttribute) {
                sendResultOK(response);
            }

            @Override
            public void onError(final int code, final String message) {
                String errMsg = "ライトの状態更新に失敗しました hue:code = " 
                        + Integer.toString(code) + "  message = " + message;
                MessageUtils.setUnknownError(response, errMsg);
                sendResultERR(response);
            }
        });
        return false;
    }

    @Override
    protected boolean onPutLight(final Intent request, final Intent response) {
        String deviceId = getDeviceID(request);
        String lightId = getLightID(request);
        String name = getName(request);

        // 必須パラメータの存在チェック
        if (lightId == null || lightId.length() == 0) {
            MessageUtils.setInvalidRequestParameterError(response, "lightId is not specified.");
            return true;
        }
        if (name == null || name.length() == 0) {
            MessageUtils.setInvalidRequestParameterError(response, "name is not specified.");
            return true;
        }

        PHBridge bridge = findBridge(deviceId);
        if (bridge == null) {
            MessageUtils.setNotFoundDeviceError(response, "Not found bridge: " + deviceId);
            return true;
        }
        PHLight light = getLight(bridge, lightId);
        if (light == null) {
            MessageUtils.setNotFoundDeviceError(response, "Not found light: " + lightId + "@" + deviceId);
            return true;
        }

        PHLight newLight = new PHLight(light);
        newLight.setName(name);
        bridge.updateLight(newLight, new PHLightAdapter() {
            @Override
            public void onSuccess() {
                sendResultOK(response);
            }

            @Override
            public void onError(final int code, final String message) {
                String errMsg = "ライトの名称変更に失敗しました hue:code = " 
                        + Integer.toString(code) + "  message = " + message;
                MessageUtils.setUnknownError(response, errMsg);
                sendResultERR(response);
            }
        });
        return false;
    }

    @Override
    protected boolean onGetLightGroup(final Intent request, final Intent response) {
        String deviceId = getDeviceID(request);
        PHBridge bridge = findBridge(deviceId);
        if (bridge == null) {
            MessageUtils.setNotFoundDeviceError(response, "Not found bridge: " + deviceId);
            return true;
        }
        List<Bundle> groupsParam = new ArrayList<Bundle>();
        Map<String, PHLight> allLights = bridge.getResourceCache().getLights();
        for (PHGroup group : bridge.getResourceCache().getAllGroups()) {
            Bundle groupParam = new Bundle();
            groupParam.putString(PARAM_GROUP_ID, group.getIdentifier());
            groupParam.putString(PARAM_NAME, group.getName());
            List<Bundle> lightsParam = new ArrayList<Bundle>();
            for (String lightId : group.getLightIdentifiers()) {
                PHLight light = allLights.get(lightId);
                if (light != null) {
                    Bundle lightParam = new Bundle();
                    lightParam.putString(PARAM_LIGHT_ID, lightId);
                    lightParam.putString(PARAM_NAME, light.getName());
                    PHLightState state = light.getLastKnownLightState();
                    if (state != null) {
                        lightParam.putBoolean(PARAM_ON, state.isOn().booleanValue());
                    }
                    lightParam.putString(PARAM_CONFIG, "");
                    lightsParam.add(lightParam);
                }
            }
            groupParam.putParcelableArray(PARAM_LIGHTS, lightsParam.toArray(new Bundle[lightsParam.size()]));
            groupParam.putString(PARAM_CONFIG, "");
            groupsParam.add(groupParam);
        }
        response.putExtra(PARAM_LIGHT_GROUPS, groupsParam.toArray(new Bundle[groupsParam.size()]));
        setResult(response, DConnectMessage.RESULT_OK);
        return true;
    }

    @Override
    protected boolean onPostLightGroup(final Intent request, final Intent response) {
        String deviceId = getDeviceID(request);
        String groupId = getGroupId(request);

        // 必須パラメータの存在チェック
        if (groupId == null || groupId.length() == 0) {
            MessageUtils.setInvalidRequestParameterError(response, "groupId is not specified.");
            return true;
        }

        PHBridge bridge = findBridge(deviceId);
        if (bridge == null) {
            MessageUtils.setNotFoundDeviceError(response, "Not found bridge: " + deviceId);
            return true;
        }

        PHLightState lightState = new PHLightState();
        lightState.setOn(true);
        lightState.setColorMode(PHLightColorMode.COLORMODE_XY);
        String colorParam = getColor(request);
        if (colorParam == null) {
            colorParam = "FFFFFF";
        }
        Color hueColor = new Color(colorParam);
        lightState.setX(hueColor.x);
        lightState.setY(hueColor.y);
        String brightnessParam = getBrightness(request);
        if (brightnessParam != null) {
            lightState.setBrightness(Integer.valueOf(new Brightness(brightnessParam).value));
        }

        if ("0".equals(groupId)) {
            bridge.setLightStateForDefaultGroup(lightState);
            setResult(response, DConnectMessage.RESULT_OK);
            return true;
        }

        PHGroup group = getGroup(bridge, groupId);
        if (group == null) {
            MessageUtils.setUnknownError(response, "Not found group: " + groupId);
            return true;
        }
        bridge.setLightStateForGroup(group.getIdentifier(), lightState, new PHGroupAdapter() {
            @Override
            public void onError(final int code, final String message) {
                String msg = "ライトの状態更新に失敗しました hue:code = " 
                        + Integer.toString(code) + "  message = " + message;
                MessageUtils.setUnknownError(response, msg);
                sendResultERR(response);
            }

            @Override
            public void onStateUpdate(final Map<String, String> successAttributes, final List<PHHueError> errorAttributes) {
                sendResultOK(response);
            }
        });
        return false;
    }

    @Override
    protected boolean onDeleteLightGroup(final Intent request, final Intent response) {
        String deviceId = getDeviceID(request);
        String groupId = getGroupId(request);

        // 必須パラメータの存在チェック
        if (groupId == null || groupId.length() == 0) {
            MessageUtils.setInvalidRequestParameterError(response, "groupId is not specified.");
            return true;
        }

        PHBridge bridge = findBridge(deviceId);
        if (bridge == null) {
            MessageUtils.setNotFoundDeviceError(response, "Not found bridge: " + deviceId);
            return true;
        }

        PHLightState lightState = new PHLightState();
        lightState.setOn(false);

        if ("0".equals(groupId)) {
            bridge.setLightStateForDefaultGroup(lightState);
            setResult(response, DConnectMessage.RESULT_OK);
            return true;
        }

        PHGroup group = getGroup(bridge, groupId);
        if (group == null) {
            MessageUtils.setUnknownError(response, "Not found group: " + groupId);
            return true;
        }
        bridge.setLightStateForGroup(group.getIdentifier(), lightState, new PHGroupAdapter() {
            @Override
            public void onError(final int code, final String message) {
                String msg = "ライトの状態更新に失敗しました hue:code = " 
                        + Integer.toString(code) + "  message = " + message;
                MessageUtils.setUnknownError(response, msg);
                sendResultERR(response);
            }

            @Override
            public void onStateUpdate(final Map<String, String> successAttributes, final List<PHHueError> errorAttributes) {
                sendResultOK(response);
            }
        });
        return false;
    }

    @Override
    protected boolean onPutLightGroup(final Intent request, final Intent response) {
        String deviceId = getDeviceID(request);
        String groupId = getGroupId(request);
        
        // 必須パラメータの存在チェック
        if (groupId == null || groupId.length() == 0) {
            MessageUtils.setInvalidRequestParameterError(response, "groupId is not specified.");
            return true;
        }
        
        PHBridge bridge = findBridge(deviceId);
        if (bridge == null) {
            MessageUtils.setNotFoundDeviceError(response, "Not found bridge: " + deviceId);
            return true;
        }
        String name = getName(request);
        if (name.length() == 0) {
            MessageUtils.setInvalidRequestParameterError(response, "name is not specified.");
            return true;
        }
        PHGroup group = getGroup(bridge, groupId);
        if (group == null) {
            MessageUtils.setUnknownError(response, "Not found group: " + groupId);
            return true;
        }

        PHGroup newGroup = new PHGroup(name, group.getIdentifier());
        bridge.updateGroup(newGroup, new PHGroupAdapter() {
            @Override
            public void onSuccess() {
                sendResultOK(response);
            }

            @Override
            public void onError(final int code, final String message) {
                String errMsg = "グループの名称変更に失敗しました hue:code = " 
                        + Integer.toString(code) + "  message = " + message;
                MessageUtils.setUnknownError(response, errMsg);
                sendResultERR(response);
            }
        });
        return false;
    }

    @Override
    protected boolean onPostLightGroupCreate(final Intent request, final Intent response) {
        String deviceId = getDeviceID(request);
        String groupName = getGroupName(request);
        String lightIds = getLightIds(request);
        
        // 必須パラメータの存在チェック
        if (groupName == null || groupName.length() == 0) {
            MessageUtils.setInvalidRequestParameterError(response, "groupName is not specified.");
            return true;
        }
        if (lightIds == null || lightIds.length() == 0) {
            MessageUtils.setInvalidRequestParameterError(response, "lightIds is not specified.");
            return true;
        }
        String[] lightIdsArray = getSelectedIdList(lightIds);
        if (lightIdsArray == null) {
            MessageUtils.setInvalidRequestParameterError(response, "lightIds is not specified.");
            return true;
        } else if (lightIdsArray.length < 1) {
            MessageUtils.setInvalidRequestParameterError(response, "lightIds is not specified.");
            return true;
        }

        PHBridge bridge = findBridge(deviceId);
        if (bridge == null) {
            MessageUtils.setNotFoundDeviceError(response, "Not found bridge: " + deviceId);
            return true;
        }

        bridge.createGroup(groupName, Arrays.asList(lightIdsArray), new PHGroupAdapter() {
            @Override
            public void onCreated(final PHGroup group) {
                response.putExtra(PARAM_GROUP_ID, group.getIdentifier());
                sendResultOK(response);
            }

            @Override
            public void onError(final int code, final String msg) {
                String errMsg = "グループ作成に失敗しました hue:code = " 
                + Integer.toString(code) + "  message = " + msg;
                if (code == HUE_SDK_ERROR_301) {
                    MessageUtils.setUnknownError(response, "グループが作成できる上限に達しています");
               } else {
                   MessageUtils.setUnknownError(response, errMsg);
                }
                sendResultERR(response);
            }
        });
        return false;
    }

    @Override
    protected boolean onDeleteLightGroupClear(final Intent request, final Intent response) {
        String deviceId = getDeviceID(request);
        String groupId = getGroupId(request);

        // 必須パラメータの存在チェック
        if (groupId == null || groupId.length() == 0) {
            MessageUtils.setInvalidRequestParameterError(response, "groupId is not specified.");
            return true;
        }

        PHBridge bridge = findBridge(deviceId);
        if (bridge == null) {
            MessageUtils.setNotFoundDeviceError(response, "Not found bridge: " + deviceId);
            return true;
        }

        bridge.deleteGroup(groupId, new PHGroupAdapter() {
            @Override
            public void onSuccess() {
                sendResultOK(response);
            }

            @Override
            public void onError(final int code, final String msg) {
                String errMsg = "グループ削除に失敗しました hue:code = " 
                        + Integer.toString(code) + "  message = " + msg;
                MessageUtils.setUnknownError(response, errMsg);
                sendResultERR(response);
            }
        });
        return false;
    }

    private PHBridge findBridge(String deviceId) {
        PHBridge bridge = PHHueSDK.getInstance().getSelectedBridge();
        if (bridge != null) {
            PHBridgeResourcesCache cache = bridge.getResourceCache();
            String ipAddress = cache.getBridgeConfiguration().getIpAddress();
            if (deviceId.equals(ipAddress)) {
                return bridge;
            }
        }
        return null;
    }

    private PHLight getLight(final PHBridge bridge, final String lightId) {
        for (PHLight light : bridge.getResourceCache().getAllLights()) {
            if (light.getIdentifier().equals(lightId)) {
                return light;
            }
        }
        return null;
    }

    private PHGroup getGroup(final PHBridge bridge, final String groupID) {
        for (PHGroup group : bridge.getResourceCache().getAllGroups()) {
            if (groupID.equals(group.getIdentifier())) {
                return group;
            }
        }
        return null;
    }

    private String[] getSelectedIdList(final String lightIdList) {
        String[] strAry = lightIdList.split(",");
        int i = 0;
        for (String string : strAry) {
            strAry[i] = string.trim();
            i++;
        }
        return strAry;
    }

    /**
     * Error レスポンス設定.
     * @param response response
     */
    private void setResultERR(final Intent response) {
        setResult(response, DConnectMessage.RESULT_ERROR); 
    }

    /**
     * 成功レスポンス送信.
     * @param response response
     */
    private void sendResultOK(final Intent response) {
        setResult(response, DConnectMessage.RESULT_OK);
        getContext().sendBroadcast(response);
    }

    /**
     * エラーレスポンスを送信する.
     * @param response エラーレスポンス
     */
    private void sendResultERR(final Intent response) {
        setResultERR(response);
        getContext().sendBroadcast(response);
    }

    /**
     * ライトID取得.
     * 
     * @param request request
     * @return lightid
     */
    private static final String getLightID(final Intent request) {
        return request.getStringExtra(PARAM_LIGHT_ID);
    }

    /**
     * 名前取得.
     * 
     * @param request request
     * @return myName
     */
    private static final String getName(final Intent request) {
        return request.getStringExtra(PARAM_NAME);
    }

    /**
     * グループ名取得.
     * 
     * @param request request
     * @return myName
     */
    private static final String getGroupName(final Intent request) {
        return request.getStringExtra(PARAM_GROUP_NAME);
    }

    /**
     * ライトID取得.
     * 
     * @param request request
     * @return myName
     */
    private static final String getLightIds(final Intent request) {
        return request.getStringExtra(PARAM_LIGHT_IDS);
    }

    /**
     * グループID取得.
     * 
     * @param request request
     * @return myName
     */
    private static final String getGroupId(final Intent request) {
        return request.getStringExtra(PARAM_GROUP_ID);
    }

    /**
     * 輝度取得.
     * 
     * @param request request
     * @return PARAM_BRIGHTNESS
     */
    private static final String getBrightness(final Intent request) {
        return request.getStringExtra(PARAM_BRIGHTNESS);
    }

    /**
     * リクエストからcolorパラメータを取得する.
     * 
     * @param request リクエスト
     * @return colorパラメータ
     */
    private static final String getColor(final Intent request) {
        return request.getStringExtra(PARAM_COLOR);
    }

    /**
     * Hueの色指定.
     */
    private static class Color {
        static final String MODEL = "LST001";
        
        final int r;
        final int g;
        final int b;
        
        final float x;
        final float y;
        
        Color(String rgb) {
            if (rgb == null) {
                throw new IllegalArgumentException();
            }
            if (rgb.length() != 6) {
                throw new IllegalArgumentException();
            }
            String r = rgb.substring(0, 2);
            String g = rgb.substring(2, 4);
            String b = rgb.substring(4, 6);
            if (r == null) {
                throw new IllegalArgumentException();
            }
            if (g == null) {
                throw new IllegalArgumentException();
            }
            if (b == null) {
                throw new IllegalArgumentException();
            }
            this.r = Integer.parseInt(r, 16);
            this.g = Integer.parseInt(g, 16);
            this.b = Integer.parseInt(b, 16);
            float[] xy = PHUtilities.calculateXYFromRGB(this.r, this.g, this.b, MODEL);
            x = xy[0];
            y = xy[1];
        }
    }

    private static class Brightness {
        static final int MAX_VALUE = 255;
        static final int TUNED_MAX_VALUE = 254;
        final int value;
        
        Brightness(String param) {
            int temp = (int) (MAX_VALUE * Float.parseFloat(param));
            if (temp >= MAX_VALUE) {
                temp = TUNED_MAX_VALUE; // 255を指定するとHue上のエラーとなるため254に丸める.
            }
            value = temp;
        }
    }

    private static class PHLightAdapter implements PHLightListener {

        @Override
        public void onError(int code, String message) {
        }

        @Override
        public void onStateUpdate(Map<String, String> successAttribute, List<PHHueError> errorAttribute) {
        }

        @Override
        public void onSuccess() {
        }

        @Override
        public void onReceivingLightDetails(PHLight light) {
        }

        @Override
        public void onReceivingLights(List<PHBridgeResource> lights) {
        }

        @Override
        public void onSearchComplete() {
        }
    }
    
    private static class PHGroupAdapter implements PHGroupListener {
        @Override
        public void onError(final int code, final String msg) {
        }

        @Override
        public void onStateUpdate(final Map<String, String> arg0, final List<PHHueError> arg1) {
        }

        @Override
        public void onSuccess() {
        }

        @Override
        public void onCreated(final PHGroup group) {
        }

        @Override
        public void onReceivingAllGroups(final List<PHBridgeResource> arg0) {
        }

        @Override
        public void onReceivingGroupDetails(final PHGroup arg0) {
        }
    }
}
