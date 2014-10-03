/*
 FailVibrationProfileTestCase.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.profile.restful.test;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONException;
import org.json.JSONObject;

import com.nttdocomo.dconnect.message.DConnectMessage.ErrorCode;
import com.nttdocomo.dconnect.profile.AuthorizationProfileConstants;
import com.nttdocomo.dconnect.profile.DConnectProfileConstants;
import com.nttdocomo.dconnect.profile.VibrationProfileConstants;
import com.nttdocomo.dconnect.utils.URIBuilder;

/**
 * Vibrationプロファイルの異常系テスト.
 * @author NTT DOCOMO, INC.
 */
public class FailVibrationProfileTestCase extends RESTfulDConnectTestCase {

    /**
     * コンストラクタ.
     * 
     * @param tag テストタグ
     */
    public FailVibrationProfileTestCase(final String tag) {
        super(tag);
    }

    /**
     * deviceIdを指定せずにバイブレーションを開始するテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /vibration/vibrate
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutVibrateNoDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(VibrationProfileConstants.PROFILE_NAME);
        builder.setAttribute(VibrationProfileConstants.ATTRIBUTE_VIBRATE);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpPut(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.EMPTY_DEVICE_ID.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceIdが空状態でバイブレーションを開始するテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /vibration/vibrate?deviceId=
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutVibrateEmptyDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(VibrationProfileConstants.PROFILE_NAME);
        builder.setAttribute(VibrationProfileConstants.ATTRIBUTE_VIBRATE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "");
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpPut(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 存在しないdeviceIdでバイブレーションを開始するテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /vibration/vibrate?deviceId=123456789&mediId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutVibrateInvalidDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(VibrationProfileConstants.PROFILE_NAME);
        builder.setAttribute(VibrationProfileConstants.ATTRIBUTE_VIBRATE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpPut(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 定義にない属性を指定してバイブレーションを開始するテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /vibration/vibrate?deviceId=xxxxx&abc=abc
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・定義にない属性は無視されること。
     * ・resultが0で返ってくること。
     * </pre>
     */
    public void testPutVibrateUndefinedAttribute() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(VibrationProfileConstants.PROFILE_NAME);
        builder.setAttribute(VibrationProfileConstants.ATTRIBUTE_VIBRATE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter("abc", "abc");
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpPut(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultOK(root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceIdを2重に指定してバイブレーションを開始するテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /vibration/vibrate?deviceId=123456789&deviceId=xxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・先に定義された属性が優先されること。
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutVibrateDuplicatedDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(VibrationProfileConstants.PROFILE_NAME);
        builder.setAttribute(VibrationProfileConstants.ATTRIBUTE_VIBRATE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpPut(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メソッドにGETを指定してバイブレーションを開始するテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /vibration/vibrate?deviceId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutVibrateInvalidMethodGet() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(VibrationProfileConstants.PROFILE_NAME);
        builder.setAttribute(VibrationProfileConstants.ATTRIBUTE_VIBRATE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_SUPPORT_ACTION.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メソッドにPOSTを指定してバイブレーションを開始するテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /vibration/vibrate?deviceId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutVibrateInvalidMethodPost() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(VibrationProfileConstants.PROFILE_NAME);
        builder.setAttribute(VibrationProfileConstants.ATTRIBUTE_VIBRATE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpPost(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_SUPPORT_ACTION.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceIdを指定せずにバイブレーションを停止するテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /vibration/vibrate
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteVibrateNoDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(VibrationProfileConstants.PROFILE_NAME);
        builder.setAttribute(VibrationProfileConstants.ATTRIBUTE_VIBRATE);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpDelete(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.EMPTY_DEVICE_ID.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceIdが空状態でバイブレーションを停止するテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /vibration/vibrate?deviceId=
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteVibrateEmptyDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(VibrationProfileConstants.PROFILE_NAME);
        builder.setAttribute(VibrationProfileConstants.ATTRIBUTE_VIBRATE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "");
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpDelete(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 存在しないdeviceIdでバイブレーションを停止するテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /vibration/vibrate?deviceId=123456789&mediId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteVibrateInvalidDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(VibrationProfileConstants.PROFILE_NAME);
        builder.setAttribute(VibrationProfileConstants.ATTRIBUTE_VIBRATE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpDelete(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 定義にない属性を指定してバイブレーションを停止するテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /vibration/vibrate?deviceId=xxxxx&abc=abc
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・定義にない属性は無視されること。
     * ・resultが0で返ってくること。
     * </pre>
     */
    public void testDeleteVibrateUndefinedAttribute() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(VibrationProfileConstants.PROFILE_NAME);
        builder.setAttribute(VibrationProfileConstants.ATTRIBUTE_VIBRATE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter("abc", "abc");
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpDelete(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultOK(root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceIdを2重に指定してバイブレーションを停止するテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /vibration/vibrate?deviceId=123456789&deviceId=xxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・先に定義された属性が優先されること。
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteVibrateDuplicatedDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(VibrationProfileConstants.PROFILE_NAME);
        builder.setAttribute(VibrationProfileConstants.ATTRIBUTE_VIBRATE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpDelete(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メソッドにGETを指定してバイブレーションを停止するテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /vibration/vibrate?deviceId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteVibrateInvalidMethodGet() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(VibrationProfileConstants.PROFILE_NAME);
        builder.setAttribute(VibrationProfileConstants.ATTRIBUTE_VIBRATE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_SUPPORT_ACTION.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メソッドにPOSTを指定してバイブレーションを停止するテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /vibration/vibrate?deviceId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteVibrateInvalidMethodPost() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(VibrationProfileConstants.PROFILE_NAME);
        builder.setAttribute(VibrationProfileConstants.ATTRIBUTE_VIBRATE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpPost(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_SUPPORT_ACTION.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }
}
