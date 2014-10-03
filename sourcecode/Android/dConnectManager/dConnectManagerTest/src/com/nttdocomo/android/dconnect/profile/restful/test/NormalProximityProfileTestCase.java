/*
 NormalProximityProfileTestCase.java
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

import com.nttdocomo.android.dconnect.test.plugin.profile.TestProximityProfileConstants;
import com.nttdocomo.dconnect.message.DConnectMessage;
import com.nttdocomo.dconnect.profile.AuthorizationProfileConstants;
import com.nttdocomo.dconnect.profile.DConnectProfileConstants;
import com.nttdocomo.dconnect.profile.ProximityProfileConstants;


/**
 * Proximityプロファイルの正常系テスト.
 * @author NTT DOCOMO, INC.
 */
public class NormalProximityProfileTestCase extends RESTfulDConnectTestCase {

    /**
     * コンストラクタ.
     * @param tag テストタグ
     */
    public NormalProximityProfileTestCase(final String tag) {
        super(tag);
    }

    /**
     * 近接センサーによる物の検知のコールバック登録テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /proximity/ondeviceproximity?deviceid=xxxx&session_key=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testOnDeviceProximity01() {
        try {
            JSONObject event = registerEventCallback(ProximityProfileConstants.ATTRIBUTE_ON_DEVICE_PROXIMITY);
            JSONObject proximity = event.getJSONObject(ProximityProfileConstants.PARAM_PROXIMITY);
            Assert.assertEquals(Double.valueOf(TestProximityProfileConstants.VALUE),
                    proximity.getDouble(ProximityProfileConstants.PARAM_VALUE));
            Assert.assertEquals(Double.valueOf(TestProximityProfileConstants.MIN), 
                    proximity.getDouble(ProximityProfileConstants.PARAM_MIN));
            Assert.assertEquals(Double.valueOf(TestProximityProfileConstants.MAX), 
                    proximity.getDouble(ProximityProfileConstants.PARAM_MAX));
            Assert.assertEquals(Double.valueOf(TestProximityProfileConstants.THRESHOLD),
                    proximity.getDouble(ProximityProfileConstants.PARAM_THRESHOLD));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 近接センサーによる物の検知のコールバック解除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /proximity/ondeviceproximity?deviceid=xxxx&session_key=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testOnDeviceProximity02() {
        unregisterEventCallback(ProximityProfileConstants.ATTRIBUTE_ON_DEVICE_PROXIMITY);
    }

    /**
     * 近接センサーによる人の検知のコールバック登録テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /proximity/onuserproximity?deviceid=xxxx&session_key=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testOnUserProximity01() {
        try {
            JSONObject event = registerEventCallback(ProximityProfileConstants.ATTRIBUTE_ON_USER_PROXIMITY);
            JSONObject proximity = event.getJSONObject(ProximityProfileConstants.PARAM_PROXIMITY);
            Assert.assertEquals(true, proximity.getBoolean(ProximityProfileConstants.PARAM_NEAR));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 近接センサーによる人の検知のコールバック解除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /proximity/onuserproximity?deviceid=xxxx&session_key=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testOnUserProximity02() {
        unregisterEventCallback(ProximityProfileConstants.ATTRIBUTE_ON_USER_PROXIMITY);
    }

    /**
     * コールバック登録リクエストを送信する.
     * @param attribute コールバックの属性名
     * @return 受信したイベント
     * @throws JSONException JSONの解析に失敗した場合
     */
    private JSONObject registerEventCallback(final String attribute) throws JSONException {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + ProximityProfileConstants.PROFILE_NAME);
        builder.append("/" + attribute);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(DConnectProfileConstants.PARAM_SESSION_KEY + "=" + getClientId());
        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        HttpUriRequest request = new HttpPut(builder.toString());
        JSONObject root = sendRequest(request);
        Assert.assertNotNull("root is null.", root);
        Assert.assertEquals(DConnectMessage.RESULT_OK,
                root.getInt(DConnectMessage.EXTRA_RESULT));
        JSONObject event = waitForEvent();
        Assert.assertNotNull("event is null.", event);
        return event;
    }

    /**
     * コールバック解除リクエストを送信する.
     * @param attribute コールバックの属性名
     */
    private void unregisterEventCallback(final String attribute) {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + ProximityProfileConstants.PROFILE_NAME);
        builder.append("/" + attribute);
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
            Assert.assertEquals(DConnectMessage.RESULT_OK,
                    root.getInt(DConnectMessage.EXTRA_RESULT));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

}
