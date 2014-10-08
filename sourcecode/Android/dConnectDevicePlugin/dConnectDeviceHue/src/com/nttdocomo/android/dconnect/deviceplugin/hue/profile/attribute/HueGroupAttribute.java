/*
HueGroupAttribute
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

package com.nttdocomo.android.dconnect.deviceplugin.hue.profile.attribute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import com.nttdocomo.android.dconnect.deviceplugin.hue.HueDeviceService;
import com.nttdocomo.android.dconnect.deviceplugin.hue.control.HueControl;
import com.nttdocomo.android.dconnect.deviceplugin.hue.control.HueControlBridge;
import com.nttdocomo.android.dconnect.deviceplugin.hue.param.DcParamLightHue;
import com.nttdocomo.android.dconnect.deviceplugin.hue.profile.HueLightProfileConstants;
import com.nttdocomo.android.dconnect.deviceplugin.param.DcParam;
import com.nttdocomo.android.dconnect.deviceplugin.param.DcParam.DcParamException;
import com.nttdocomo.android.dconnect.deviceplugin.util.DcLoggerHue;
import com.nttdocomo.android.dconnect.message.MessageUtils;
import com.nttdocomo.dconnect.message.DConnectMessage;
import com.philips.lighting.hue.listener.PHGroupListener;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHGroup;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLightState;

/**
 * HueグループAttribute.
 *
 */
public class HueGroupAttribute extends HueLightAttribute implements HueLightProfileConstants {

    /**
     * エラーコード301.
     */
    private static final int HUE_SDK_ERROR_301 = 301;

    /**
     * Constructor.
     * @param context context
     */
    public HueGroupAttribute(final HueDeviceService context) {
        super(context);
    }

    /**
     * ロガー.
     */
    private DcLoggerHue mLogger = new DcLoggerHue();

    /**
     * グループ作成.
     * @param request request
     * @param response response
     * @return RESPONSE
     * @throws DcParamException DcParam
     */
    public boolean createGroup(final Intent request, final Intent response) throws DcParamException {

        mLogger.entering(this, "createGroup");

        String deviceid = getDeviceID(request);
        String groupName = DcParamLightHue.getGroupName(request);
        String lightIds = DcParamLightHue.getLightIds(request);

        mLogger.fine(this, "createGroup GroupName", groupName);
        mLogger.fine(this, "createGroup lightIdList", lightIds);

        // 空チェック
        if (!checkCreateGroup(groupName, lightIds)) {

            mLogger.fine(this, "createGroup", "空チェック NG" + lightIds);
            
            DcParam.throwPramException("lightIdsが指定されていません");

            return SYNC_RESPONSE;
        }

        String[] lightIdsArray = getSelectedIdList(lightIds);

        mLogger.fine(this, "createGroup selectedlightIds", lightIdsArray);

        // ID List check
        if (lightIdsArray == null) {

            mLogger.fine(this, "createGroup", "ID List check NG " + lightIds);

            DcParam.throwPramException("lightIdsが正しくありません");

            return SYNC_RESPONSE;

        } else if (lightIdsArray.length < 1) {

            mLogger.fine(this, "createGroup", "ID List check NG " + lightIds);

            DcParam.throwPramException("lightIdsが正しくありません");

            return SYNC_RESPONSE;

        }

        HueControlBridge controlBridge = new HueControlBridge();

        PHBridge bridge = controlBridge.getBridgeSync(deviceid);

        if (bridge == null) {

            mLogger.exiting(this, "createGroup ブリッジが見つかりません", true);
            MessageUtils.setUnknownError(response, "ブリッジが見つかりません");

            return SYNC_RESPONSE;

        } else {

            mLogger.fine(this, "createGroup selectedlightIds", lightIdsArray);

            boolean isSyncResponse = createGroup(bridge, groupName, Arrays.asList(lightIdsArray) , response);

            mLogger.exiting(this, "createGroup", isSyncResponse);

            return isSyncResponse;

        }

    }

    /**
     * Hueグループ更新.
     * @param request request
     * @param response response
     * @return RESPONSE
     * @throws DcParamException DcParam
     */
    public boolean updateGroup(final Intent request, final Intent response) throws DcParamException {

        mLogger.entering(this, "updateGroup");

        boolean isSyncResponse = true;

        mLogger.entering(this, "updateGroup", new Object[] {request, response});

        String deviceid = getDeviceID(request);
        String groupName = DcParamLightHue.getName(request);
        String lightIds = DcParamLightHue.getLightIds(request);

        HueControlBridge controlBridge = new HueControlBridge();

        PHBridge bridge = controlBridge.getBridgeSync(deviceid);

        if (bridge == null) {

            mLogger.exiting(this, "updateGroup ブリッジが見つかりません", true);
            MessageUtils.setUnknownError(response, "ブリッジが見つかりません");

        } else {

            mLogger.fine(this, "updateGroup GroupName", groupName);
            mLogger.fine(this, "updateGroup LightIds", lightIds);

            PHGroup group = getGroup(deviceid, DcParamLightHue.getGroupId(request));

            if (group == null) {

                mLogger.exiting(this, "updateGroup グループが見つかりません", true);
                MessageUtils.setUnknownError(response, "グループが見つかりません");

            } else {

                isSyncResponse = updateGroup(bridge, group, groupName, lightIds, response);

            }

        }

        mLogger.exiting(this, "updateGroup", isSyncResponse);

        return isSyncResponse;

    }

    /**
     * グループ削除.
     * @param request request
     * @param response response
     * @return result of delete
     * @throws DcParamException DcParam
     */
    public boolean deleteGroup(final Intent request, final Intent response) throws DcParamException {

        mLogger.entering(this, "deleteGroup");

        String deviceid = getDeviceID(request);
        String groupId = DcParamLightHue.getGroupId(request);

        // 空チェック
        if (!checkDeleteGroup(groupId)) {

            mLogger.fine(this, "deleteGroup", "空チェック NG");

            DcParam.throwPramException("groupIdが指定されていません");

            return SYNC_RESPONSE;

        }

        HueControlBridge controlBridge = new HueControlBridge();

        PHBridge bridge = controlBridge.getBridgeSync(deviceid);

        if (bridge == null) {

            mLogger.exiting(this, "deleteGroup ブリッジが見つかりません", true);
            MessageUtils.setUnknownError(response, "ブリッジが見つかりません");

            return SYNC_RESPONSE;

        } else {

            mLogger.fine(this, "deleteGroup lightGroupId", groupId);

            boolean isSyncResponse = deleteGroup(bridge, groupId, response);

            mLogger.exiting(this, "deleteGroup", isSyncResponse);

            return isSyncResponse;

        }
    }

    /**
     * Hueグループ削除確認.
     * @param lightGroupId ID
     * @return result
     */
    private boolean checkDeleteGroup(final String lightGroupId) {
        if (lightGroupId == null) {
            return false;
        } else if (lightGroupId.length() < 1) {
            return false;
        }

        return true;
    }

    /**
     * グループ情報取得.
     * @param deviceid ID
     * @param groupID ID
     * @return ID List
     */
    private PHGroup getGroup(final String deviceid, final String groupID) {
        mLogger.entering(this, "getGroup", groupID);

        HueControlBridge controlBridge = new HueControlBridge();

        PHBridge phBridge = controlBridge.getBridgeSync(deviceid);

        mLogger.fine(this, "getGroup getBridgeConnectState", HueControl.getBridgeConnectState());
        
        if (phBridge == null) {
            return null;
        }
        // phBridge.getResourceCache().getGroups();
        List<PHGroup> phGroups = phBridge.getResourceCache().getAllGroups();

        String id;

        for (PHGroup group : phGroups) {
            id = group.getIdentifier();
            mLogger.fine(this, "getGroup group.getIdentifier()", id);

            if (id.equals(groupID)) {

                mLogger.exiting(this, "getGroup", id);
                return group;
            }
        }

        mLogger.exiting(this, "getGroup", "null");
        return null;
    }

    @Override
    protected boolean updateLightState(final Intent request, final Intent response, final PHLightState lightState)
            throws DcParamException {

        mLogger.entering(this, "updateLightState");

        String deviceid = getDeviceID(request);
        String groupId = DcParamLightHue.getGroupId(request);

        if (groupId.length() == 0) {
            DcParam.throwPramException("groupIdが指定されていません");
        }

        PHGroup group;

        if ("0".equals(groupId)) {

            updateLightStateDefaultGroup(lightState, response);

            setResultOK(response);
            mLogger.exiting(this, "updateLightState group0");

            return SYNC_RESPONSE;

        } else {
            group = getGroup(deviceid, groupId);

            if (group == null) {

                mLogger.exiting(this, "updateLightState", "グループが見つかりません");

                MessageUtils.setUnknownError(response, "グループが見つかりません");

                return SYNC_RESPONSE;

            }

        }

        updateLightState(group, lightState, response);
        mLogger.exiting(this, "updateLightState");

        return ASYNC_RESPONSE;
    }

    /**
     * ライト情報更新.
     * @param phGroup グループ
     * @param lightState ライト情報
     * @param response response
     */
    private void updateLightState(final PHGroup phGroup, final PHLightState lightState, final Intent response) {

        PHHueSDK phHueSDK = HueControl.getPHHueSDK();

        PHBridge phBridge = phHueSDK.getSelectedBridge();

        phBridge.setLightStateForGroup(phGroup.getIdentifier(), lightState, new DcPHGroupListener(response));
    }

    /**
     * ライトステータスをデフォルトグループとして更新.
     * @param lightState light status
     * @param response response
     */
    private void updateLightStateDefaultGroup(final PHLightState lightState, final Intent response) {

        PHHueSDK phHueSDK = HueControl.getPHHueSDK();

        PHBridge phBridge = phHueSDK.getSelectedBridge();

        phBridge.setLightStateForDefaultGroup(lightState);

    }

    /**
     * グループ情報取得クラス.
     *
     */
    class DcPHGroupListener implements PHGroupListener {

        /**
         * response.
         */
        private Intent mResponse;

        /**
         * Constructor.
         * @param response response
         */
        public DcPHGroupListener(final Intent response) {
            super();

            mLogger.entering(this, "DcPHGroupListener");

            mResponse = response;

            mLogger.exiting(this, "DcPHGroupListener");
        }

        @Override
        public void onError(final int arg0, final String arg1) {
            String msg = "ライトの状態更新に失敗しました hue:code = " 
                    + Integer.toString(arg0) + "  message = " + arg1;
            mLogger.fine(this, "onError", msg);

            MessageUtils.setUnknownError(mResponse, msg);

            sendResultERR(mResponse);

        }

        @Override
        public void onStateUpdate(final Map<String, String> arg0, final List<PHHueError> arg1) {

            mLogger.fine(this, "updateLightState( onStateUpdate )", arg0);
            sendResultOK(mResponse);
            
        }

        @Override
        public void onSuccess() {
            mLogger.entering(this, "DcPHGroupListener( onSuccess )");
            mLogger.exiting(this, "DcPHGroupListener( onSuccess )");

        }

        @Override
        public void onCreated(final PHGroup arg0) {
            
        }

        @Override
        public void onReceivingAllGroups(final List<PHBridgeResource> arg0) {
            
        }

        @Override
        public void onReceivingGroupDetails(final PHGroup arg0) {
            
        }

    }

    /**
     * グループ作成結果の確認.
     * @param groupName Name
     * @param lightIdList List
     * @return check result
     */
    private boolean checkCreateGroup(final String groupName, final String lightIdList) {
        // REST API仕様上は空でもOK
        if (lightIdList.trim().length() == 0) {
            return false;
        }
        return true;
    }

    /**
     * 選択したIDリストを取得.
     * @param lightIdList ID List
     * @return List
     */
    private String[] getSelectedIdList(final String lightIdList) {

        mLogger.entering(this, "getSelectedIdList", lightIdList);

        String[] strAry = lightIdList.split(",");

        mLogger.fine(this, "getSelectedIdList strAry = ", strAry);
        int i = 0;
        for (String string : strAry) {
            strAry[i] = string.trim();
            i++;
        }

        mLogger.entering(this, "getSelectedIdList", strAry);

        return strAry;
    }

    /**
     * グループ作成.
     * @param bridge Bridge
     * @param groupName Name
     * @param selectedlightIds Array of ID
     * @param response response
     * @return result
     * @throws DcParamException DcParam
     */
    private boolean createGroup(final PHBridge bridge, final String groupName, final List<String> selectedlightIds,
            final Intent response) throws DcParamException {

        bridge.createGroup(groupName, selectedlightIds, new PHGroupListener() {

            @Override
            public void onError(final int code, final String msg) {

                String errMsg = "グループ作成に失敗しました hue:code = " 
                + Integer.toString(code) + "  message = " + msg;

                mLogger.fine(this, "createGroup onError", errMsg);

                if (code == HUE_SDK_ERROR_301) {
                    MessageUtils.setUnknownError(response, "グループが作成できる上限に達しています");
               } else {

                   MessageUtils.setUnknownError(response, errMsg);
                }
                
                sendResultERR(response);
            }

            @Override
            public void onStateUpdate(final Map<String, String> arg0, final List<PHHueError> arg1) {

                mLogger.fine(this, "createGroup onStateUpdate", "");
                
            }

            @Override
            public void onSuccess() {

                mLogger.fine(this, "createGroup onSuccess", "");

            }

            @Override
            public void onCreated(final PHGroup group) {

                mLogger.fine(this, "createGroup onCreated group", group);

                response.putExtra(PARAM_GROUP_ID, group.getIdentifier());
                sendResultOK(response);

            }

            @Override
            public void onReceivingAllGroups(final List<PHBridgeResource> arg0) {
                
            }

            @Override
            public void onReceivingGroupDetails(final PHGroup arg0) {
                
            }

        });

        mLogger.exiting(this, "createGroup", "false");
        return ASYNC_RESPONSE;
    }

    /**
     * グループ更新確認.
     * @param groupName Name
     * @return result
     */
    private boolean checkUpdateGroupGroupName(final String groupName) {

        if (groupName.trim().length() == 0) {
            return false;
        }

        return true;
    }

    /**
     * グループ更新確認.
     * @param lightIdList List
     * @return result
     */
    private boolean checkUpdateGroupLightIdList(final String lightIdList) {

        if (lightIdList.trim().length() == 0) {
            return false;
        }

        return true;
    }

    /**
     * グループ更新.
     * @param bridge Bridge
     * @param group Group
     * @param groupName Name
     * @param lightIdList ID List
     * @param response response
     * @return result
     * @throws DcParamException DcParam
     */
    private boolean updateGroup(final PHBridge bridge, final PHGroup group, final String groupName,
            final String lightIdList, final Intent response) throws DcParamException {

        // 空チェック
        if (!checkUpdateGroupGroupName(groupName)) {

            DcParam.throwPramException("name指定されていません");

            return SYNC_RESPONSE;
        }

        if (!checkUpdateGroupLightIdList(lightIdList)) {

            DcParam.throwPramException("lightIdsが指定されていません");

            return SYNC_RESPONSE;
        }

        String[] selectedlightIds = getSelectedIdList(lightIdList);

        // ID List check
        if (selectedlightIds == null || selectedlightIds.length <= 1) {

            DcParam.throwPramException("lightIdsが正しくありません");

            return SYNC_RESPONSE;
        }

        group.setName(groupName);
        group.setLightIdentifiers(Arrays.asList(selectedlightIds));

        bridge.updateGroup(group, new PHGroupListener() {

            @Override
            public void onError(final int code, final String msg) {
                
                String errMsg = "グループ更新に失敗しました hue:code = " 
                        + Integer.toString(code) + "  message = " + msg;
                
                mLogger.fine(this, "updateGroup onError", errMsg);

                MessageUtils.setUnknownError(response, errMsg);
                sendResultERR(response);
            }

            @Override
            public void onSuccess() {

            }

            @Override
            public void onCreated(final PHGroup group) {

                sendResultOK(response);
            }

            @Override
            public void onStateUpdate(final Map<String, String> arg0, final List<PHHueError> arg1) {
                
            }

            @Override
            public void onReceivingAllGroups(final List<PHBridgeResource> arg0) {
                
            }

            @Override
            public void onReceivingGroupDetails(final PHGroup arg0) {
                
            }

        });

        return ASYNC_RESPONSE;
    }

    /**
     * グループ削除.
     * @param bridge bridge
     * @param lightGroupId GroupID
     * @param response response
     * @return result
     * @throws DcParamException DcParam
     */
    private boolean deleteGroup(final PHBridge bridge, final String lightGroupId, final Intent response)
            throws DcParamException {

        bridge.deleteGroup(lightGroupId, new PHGroupListener() {

            @Override
            public void onError(final int code, final String msg) {

                String errMsg = "グループ削除に失敗しました hue:code = " 
                        + Integer.toString(code) + "  message = " + msg;

                mLogger.fine(this, "deleteGroup onError", errMsg);

                MessageUtils.setUnknownError(response, errMsg);
                sendResultERR(response);
            }

            @Override
            public void onSuccess() {
                mLogger.fine(this, "deleteGroup onSuccess", "");

                sendResultOK(response);

            }

            @Override
            public void onStateUpdate(final Map<String, String> arg0, final List<PHHueError> arg1) {
                
            }

            @Override
            public void onCreated(final PHGroup arg0) {
                
            }

            @Override
            public void onReceivingAllGroups(final List<PHBridgeResource> arg0) {
                
            }

            @Override
            public void onReceivingGroupDetails(final PHGroup arg0) {
                
            }

        });

        return ASYNC_RESPONSE;
    }

    /**
     * 名前変更.
     * @param request request
     * @param response response
     * @return result
     * @throws DcParamException DcParam
     */
    public boolean changeName(final Intent request, final Intent response) throws DcParamException {

        mLogger.entering(this, "changeName");

        String name = DcParamLightHue.getName(request);

        if (name.length() == 0) {
            DcParamLightHue.throwPramException("変更後のnameが指定されていません");
        }

        String deviceid = getDeviceID(request);

        PHGroup group = getGroup(deviceid, DcParamLightHue.getGroupId(request));

        if (group == null) {

            mLogger.exiting(this, "changeName グループが見つかりません", true);
            MessageUtils.setUnknownError(response, "グループが見つかりません");

            return SYNC_RESPONSE;
        }

        boolean isSyncResponse = changeName(group, name, response);

        mLogger.exiting(this, "changeName");
        return isSyncResponse;

    }

    /**
     * 名前変更.
     * @param group Group
     * @param name Name
     * @param response response
     * @return result
     */
    private boolean changeName(final PHGroup group, final String name, final Intent response) {
     
        mLogger.entering(this, "changeName");

        PHHueSDK phHueSDK = HueControl.getPHHueSDK();
        PHBridge phBridge = phHueSDK.getSelectedBridge();

        PHGroup newGroup = new PHGroup(name, group.getIdentifier());
        phBridge.updateGroup(newGroup, new PHGroupListener() {

            @Override
            public void onSuccess() {

                sendResultOK(response);
            }

            @Override
            public void onError(final int code, final String message) {
                mLogger.fine(this, "changeName( onError )", "グループの名称変更に失敗しました");
 
                String errMsg = "グループの名称変更に失敗しました hue:code = " 
                        + Integer.toString(code) + "  message = " + message;

                mLogger.fine(this, "deleteGroup onError", errMsg);

                MessageUtils.setUnknownError(response, errMsg);
                sendResultERR(response);

            }

            @Override
            public void onStateUpdate(final Map<String, String> arg0, final List<PHHueError> arg1) {
                
            }

            @Override
            public void onCreated(final PHGroup arg0) {
                
            }

            @Override
            public void onReceivingAllGroups(final List<PHBridgeResource> arg0) {
                
            }

            @Override
            public void onReceivingGroupDetails(final PHGroup arg0) {
                
            }
        });

        mLogger.exiting(this, "changeName");
        return ASYNC_RESPONSE;
    }

    @Override
    protected List<Bundle> makeRespose(final DConnectMessage message) {
        List<Bundle> resLights = new ArrayList<Bundle>();

        //groups　HashMapを取得
        HashMap<?, ?> groupsMap = (HashMap<?, ?>) message.get("groups");

        List<String> sortedGroupIdList = getSortedId(groupsMap);
        
        for (String groupId : sortedGroupIdList) {
            Bundle bundGroup = new Bundle();
            //Group ID
            bundGroup.putString(PARAM_GROUP_ID, groupId);

            //groups 内のHashMapを取得
            HashMap<?, ?> groupData = (HashMap<?, ?>) groupsMap.get(groupId);

            mLogger.fine(this, "makeRespose groupData", groupData);

            //groups/name取得
            String name = (String) groupData.get("name");
            bundGroup.putString(PARAM_NAME, name);

            //将来対応のためのConfigを空でセットしておく
            bundGroup.putString(PARAM_CONFIG, "");
            
            //groups/lights/state のHashMapを取得
            @SuppressWarnings("unchecked")
            List<String> lightNoLish = getSortedId((List<String>) groupData.get("lights"));
            mLogger.fine(this, "makeRespose groupData.lights", lightNoLish + " " + lightNoLish.size());

            List<Bundle> lightBundleList = new ArrayList<Bundle>();
            for (String lightId : lightNoLish) {
                                
                lightBundleList.add(getLightBundle(message, lightId));

            }
            bundGroup.putParcelableArrayList("lights", (ArrayList<? extends Parcelable>) lightBundleList);

            resLights.add(bundGroup);
        }

        return resLights;
    }

    @Override
    protected String getToptag() {
        return PARAM_LIGHT_GROUPS;
    }
    
}
