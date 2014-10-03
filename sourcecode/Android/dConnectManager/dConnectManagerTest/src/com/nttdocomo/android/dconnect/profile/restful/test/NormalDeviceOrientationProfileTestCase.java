/*
 NormalDeviceOrientationProfileTestCase.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.profile.restful.test;

import junit.framework.Assert;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONException;
import org.json.JSONObject;

import com.nttdocomo.dconnect.profile.AuthorizationProfileConstants;
import com.nttdocomo.dconnect.profile.DConnectProfileConstants;
import com.nttdocomo.dconnect.profile.DeviceOrientationProfileConstants;

/**
 * Device Orientationプロファイルの正常系テスト.
 * @author NTT DOCOMO, INC.
 */
public class NormalDeviceOrientationProfileTestCase extends
        RESTfulDConnectTestCase {

    /**
     * コンストラクタ.
     * @param string テストタグ
     */
    public NormalDeviceOrientationProfileTestCase(final String string) {
        super(string);
    }

    /**
     * ondeviceorientationイベントのコールバック登録テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /deviceorientation/ondeviceorientation?deviceId=xxxx&sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・コールバック登録後にイベントを受信すること。
     * </pre>
     */
    public void testPutOnDeviceOrientation() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + DeviceOrientationProfileConstants.PROFILE_NAME);
        builder.append("/" + DeviceOrientationProfileConstants.ATTRIBUTE_ON_DEVICE_ORIENTATION);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(DConnectProfileConstants.PARAM_SESSION_KEY + "=" + getClientId());
        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpUriRequest request = new HttpPut(builder.toString());
            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            assertResultOK(root);

            JSONObject event = waitForEvent();
            JSONObject orientation = event.getJSONObject(DeviceOrientationProfileConstants.PARAM_ORIENTATION);
            Assert.assertNotNull(orientation);
            JSONObject a1 = orientation.getJSONObject(DeviceOrientationProfileConstants.PARAM_ACCELERATION);
            Assert.assertNotNull(a1);
            Assert.assertEquals(0.0, a1.getDouble(DeviceOrientationProfileConstants.PARAM_X));
            Assert.assertEquals(0.0, a1.getDouble(DeviceOrientationProfileConstants.PARAM_Y));
            Assert.assertEquals(0.0, a1.getDouble(DeviceOrientationProfileConstants.PARAM_Z));
            JSONObject a2 = 
                    orientation.getJSONObject(DeviceOrientationProfileConstants.PARAM_ACCELERATION_INCLUDING_GRAVITY);
            Assert.assertNotNull(a2);
            Assert.assertEquals(0.0, a2.getDouble(DeviceOrientationProfileConstants.PARAM_X));
            Assert.assertEquals(0.0, a2.getDouble(DeviceOrientationProfileConstants.PARAM_Y));
            Assert.assertEquals(0.0, a2.getDouble(DeviceOrientationProfileConstants.PARAM_Z));
            JSONObject r = orientation.getJSONObject(DeviceOrientationProfileConstants.PARAM_ROTATION_RATE);
            Assert.assertNotNull(r);
            Assert.assertEquals(0.0, r.getDouble(DeviceOrientationProfileConstants.PARAM_ALPHA));
            Assert.assertEquals(0.0, r.getDouble(DeviceOrientationProfileConstants.PARAM_BETA));
            Assert.assertEquals(0.0, r.getDouble(DeviceOrientationProfileConstants.PARAM_GAMMA));
            Assert.assertEquals(0.0, orientation.getDouble(DeviceOrientationProfileConstants.PARAM_INTERVAL));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * ondeviceorientationイベントのコールバック登録テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /deviceorientation/ondeviceorientation?deviceId=xxxx&sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testDeleteOnDeviceOrientation() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + DeviceOrientationProfileConstants.PROFILE_NAME);
        builder.append("/" + DeviceOrientationProfileConstants.ATTRIBUTE_ON_DEVICE_ORIENTATION);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(DConnectProfileConstants.PARAM_SESSION_KEY + "=" + getClientId());
        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpUriRequest request = new HttpDelete(builder.toString());
            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            assertResultOK(root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

}
