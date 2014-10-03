/*
 NormalPhoneProfileTestCase.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.profile.restful.test;

import junit.framework.Assert;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONException;
import org.json.JSONObject;

import com.nttdocomo.android.dconnect.test.plugin.profile.TestPhoneProfileConstants;
import com.nttdocomo.dconnect.message.DConnectMessage;
import com.nttdocomo.dconnect.profile.AuthorizationProfileConstants;
import com.nttdocomo.dconnect.profile.DConnectProfileConstants;
import com.nttdocomo.dconnect.profile.PhoneProfileConstants;

/**
 * Phoneプロファイルの正常系テスト.
 * @author NTT DOCOMO, INC.
 */
public class NormalPhoneProfileTestCase extends RESTfulDConnectTestCase {

    /**
     * コンストラクタ.
     * @param tag テストタグ
     */
    public NormalPhoneProfileTestCase(final String tag) {
        super(tag);
    }

    /**
     * 電話発信要求テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /phone/call?deviceid=xxxx&mediaid=yyyy
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultが0で返ってくること。
     * </pre>
     */
    public void testPostCall() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + PhoneProfileConstants.PROFILE_NAME);
        builder.append("/" + PhoneProfileConstants.ATTRIBUTE_CALL);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(PhoneProfileConstants.PARAM_PHONE_NUMBER + "=" + TestPhoneProfileConstants.PHONE_NUMBER);
        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpUriRequest request = new HttpPost(builder.toString());
            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 電話に関する設定項目(サイレント・マナー・音あり)の設定テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /phone/set?deviceid=xxxx&mode=0
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultが0で返ってくること。
     * </pre>
     */
    public void testPutSet001() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + PhoneProfileConstants.PROFILE_NAME);
        builder.append("/" + PhoneProfileConstants.ATTRIBUTE_SET);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(PhoneProfileConstants.PARAM_MODE + "=0");
        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpUriRequest request = new HttpPut(builder.toString());
            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 電話に関する設定項目(マナー)の設定テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /phone/set?deviceid=xxxx&mode=1
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultが0で返ってくること。
     * </pre>
     */
    public void testPutSet002() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + PhoneProfileConstants.PROFILE_NAME);
        builder.append("/" + PhoneProfileConstants.ATTRIBUTE_SET);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(PhoneProfileConstants.PARAM_MODE + "=1");
        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpUriRequest request = new HttpPut(builder.toString());
            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 電話に関する設定項目(音あり)の設定テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /phone/set?deviceid=xxxx&mode=2
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultが0で返ってくること。
     * </pre>
     */
    public void testPutSet003() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + PhoneProfileConstants.PROFILE_NAME);
        builder.append("/" + PhoneProfileConstants.ATTRIBUTE_SET);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(PhoneProfileConstants.PARAM_MODE + "=2");
        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpUriRequest request = new HttpPut(builder.toString());
            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 通話関連イベントのコールバック登録テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /phone/onconnect?deviceid=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultが0で返ってくること。
     * </pre>
     */
    public void testPutOnConnect() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + PhoneProfileConstants.PROFILE_NAME);
        builder.append("/" + PhoneProfileConstants.ATTRIBUTE_ON_CONNECT);
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
            Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
            JSONObject event = waitForEvent();
            JSONObject phoneStatus = event.getJSONObject(PhoneProfileConstants.PARAM_PHONE_STATUS);
            Assert.assertEquals(TestPhoneProfileConstants.PHONE_NUMBER, phoneStatus.getString(PhoneProfileConstants.PARAM_PHONE_NUMBER));
            Assert.assertEquals(PhoneProfileConstants.CallState.FINISHED.getValue(), phoneStatus.getInt(PhoneProfileConstants.PARAM_STATE));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 通話関連イベントのコールバック解除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /phone/onconnect?deviceid=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultが0で返ってくること。
     * </pre>
     */
    public void testDeleteOnConnect() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + PhoneProfileConstants.PROFILE_NAME);
        builder.append("/" + PhoneProfileConstants.ATTRIBUTE_ON_CONNECT);
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
            Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

}
