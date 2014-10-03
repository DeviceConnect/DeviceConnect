/*
 FailPhoneProfileTestCase.java
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

import com.nttdocomo.android.dconnect.test.plugin.profile.TestPhoneProfileConstants;
import com.nttdocomo.dconnect.message.DConnectMessage;
import com.nttdocomo.dconnect.message.DConnectMessage.ErrorCode;
import com.nttdocomo.dconnect.profile.AuthorizationProfileConstants;
import com.nttdocomo.dconnect.profile.DConnectProfileConstants;
import com.nttdocomo.dconnect.profile.PhoneProfileConstants;
import com.nttdocomo.dconnect.utils.URIBuilder;


/**
 * Phoneプロファイルの異常系テスト.
 * @author NTT DOCOMO, INC.
 */
public class FailPhoneProfileTestCase extends RESTfulDConnectTestCase
    implements TestPhoneProfileConstants {

    /**
     * コンストラクタ.
     * 
     * @param tag テストタグ
     */
    public FailPhoneProfileTestCase(final String tag) {
        super(tag);
    }


    /**
     * deviceIdを指定せずに通話発信要求を送信するテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /phone/call?phoneNumber=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPostCallNoDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(PhoneProfileConstants.PROFILE_NAME);
        builder.setAttribute(PhoneProfileConstants.ATTRIBUTE_CALL);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        builder.addParameter(PhoneProfileConstants.PARAM_PHONE_NUMBER, PHONE_NUMBER);
        try {
            HttpUriRequest request = new HttpPost(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.EMPTY_DEVICE_ID.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceIdが空状態で通話発信要求を送信するテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /phone/call?deviceId=&phoneNumber=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPostCallEmptyDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(PhoneProfileConstants.PROFILE_NAME);
        builder.setAttribute(PhoneProfileConstants.ATTRIBUTE_CALL);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "");
        builder.addParameter(PhoneProfileConstants.PARAM_PHONE_NUMBER, PHONE_NUMBER);
        try {
            HttpUriRequest request = new HttpPost(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 存在しないdeviceIdで通話発信要求を送信するテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /phone/call?deviceId=123456789&phoneNumber=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPostCallInvalidDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(PhoneProfileConstants.PROFILE_NAME);
        builder.setAttribute(PhoneProfileConstants.ATTRIBUTE_CALL);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(PhoneProfileConstants.PARAM_PHONE_NUMBER, PHONE_NUMBER);
        try {
            HttpUriRequest request = new HttpPost(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceIdを2重に指定して通話発信要求を送信するテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /phone/call?deviceId=123456789&deviceId=xxx&phoneNumber=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・先に定義された属性が優先されること。
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPostCallDuplicatedDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(PhoneProfileConstants.PROFILE_NAME);
        builder.setAttribute(PhoneProfileConstants.ATTRIBUTE_CALL);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(PhoneProfileConstants.PARAM_PHONE_NUMBER, PHONE_NUMBER);
        try {
            HttpUriRequest request = new HttpPost(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * phoneNumberを指定せずに通話発信要求を送信するテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /phone/call?deviceId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPostCallNoPhoneNumber() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(PhoneProfileConstants.PROFILE_NAME);
        builder.setAttribute(PhoneProfileConstants.ATTRIBUTE_CALL);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        try {
            HttpUriRequest request = new HttpPost(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.INVALID_REQUEST_PARAMETER.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メソッドにGETを指定して通話発信要求を送信するテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /phone/call?deviceId=xxxx&phoneNumber=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPostCallInvalidMethodGet() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(PhoneProfileConstants.PROFILE_NAME);
        builder.setAttribute(PhoneProfileConstants.ATTRIBUTE_CALL);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(PhoneProfileConstants.PARAM_PHONE_NUMBER, PHONE_NUMBER);
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_SUPPORT_ACTION.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メソッドにPUTを指定して通話発信要求を送信するテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /phone/call?deviceId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPostCallInvalidMethodPut() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(PhoneProfileConstants.PROFILE_NAME);
        builder.setAttribute(PhoneProfileConstants.ATTRIBUTE_CALL);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(PhoneProfileConstants.PARAM_PHONE_NUMBER, PHONE_NUMBER);
        try {
            HttpUriRequest request = new HttpPut(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.UNKNOWN_ATTRIBUTE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メソッドにDELETEを指定して通話発信要求を送信するテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /phone/call?deviceId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPostCallInvalidMethodDelete() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(PhoneProfileConstants.PROFILE_NAME);
        builder.setAttribute(PhoneProfileConstants.ATTRIBUTE_CALL);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(PhoneProfileConstants.PARAM_PHONE_NUMBER, PHONE_NUMBER);
        try {
            HttpUriRequest request = new HttpDelete(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.UNKNOWN_ATTRIBUTE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }
    
    /**
     * deviceIdを指定せずに電話設定要求を送信するテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /phone/call?phoneNumber=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutSetNoDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(PhoneProfileConstants.PROFILE_NAME);
        builder.setAttribute(PhoneProfileConstants.ATTRIBUTE_CALL);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        builder.addParameter(PhoneProfileConstants.PARAM_MODE, String.valueOf(MODE));
        try {
            HttpUriRequest request = new HttpPost(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.EMPTY_DEVICE_ID.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceIdが空状態で電話設定要求を送信するテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /phone/call?deviceId=&phoneNumber=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutSetEmptyDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(PhoneProfileConstants.PROFILE_NAME);
        builder.setAttribute(PhoneProfileConstants.ATTRIBUTE_CALL);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "");
        builder.addParameter(PhoneProfileConstants.PARAM_MODE, String.valueOf(MODE));
        try {
            HttpUriRequest request = new HttpPost(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 存在しないdeviceIdで電話設定要求を送信するテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /phone/call?deviceId=123456789&phoneNumber=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutSetInvalidDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(PhoneProfileConstants.PROFILE_NAME);
        builder.setAttribute(PhoneProfileConstants.ATTRIBUTE_CALL);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(PhoneProfileConstants.PARAM_MODE, String.valueOf(MODE));
        try {
            HttpUriRequest request = new HttpPost(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceIdを2重に指定して電話設定要求を送信するテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /phone/call?deviceId=123456789&deviceId=xxx&phoneNumber=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・先に定義された属性が優先されること。
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutSetDuplicatedDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(PhoneProfileConstants.PROFILE_NAME);
        builder.setAttribute(PhoneProfileConstants.ATTRIBUTE_CALL);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(PhoneProfileConstants.PARAM_MODE, String.valueOf(MODE));
        try {
            HttpUriRequest request = new HttpPost(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * modeを指定せずに電話設定要求を送信するテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /phone/set?deviceId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutSetNoMode() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(PhoneProfileConstants.PROFILE_NAME);
        builder.setAttribute(PhoneProfileConstants.ATTRIBUTE_SET);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        try {
            HttpUriRequest request = new HttpPut(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.INVALID_REQUEST_PARAMETER.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メソッドにGETを指定して電話設定要求を送信するテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /phone/call?deviceId=xxxx&phoneNumber=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutSetInvalidMethodGet() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(PhoneProfileConstants.PROFILE_NAME);
        builder.setAttribute(PhoneProfileConstants.ATTRIBUTE_CALL);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(PhoneProfileConstants.PARAM_MODE, String.valueOf(MODE));
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_SUPPORT_ACTION.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メソッドにPOSTを指定して電話設定要求を送信するテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /phone/call?deviceId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutSetInvalidMethodPost() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(PhoneProfileConstants.PROFILE_NAME);
        builder.setAttribute(PhoneProfileConstants.ATTRIBUTE_SET);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(PhoneProfileConstants.PARAM_MODE, String.valueOf(MODE));
        try {
            HttpUriRequest request = new HttpPost(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.UNKNOWN_ATTRIBUTE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メソッドにDELETEを指定して電話設定要求を送信するテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /phone/call?deviceId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutSetInvalidMethodDelete() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(PhoneProfileConstants.PROFILE_NAME);
        builder.setAttribute(PhoneProfileConstants.ATTRIBUTE_CALL);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(PhoneProfileConstants.PARAM_MODE, String.valueOf(MODE));
        try {
            HttpUriRequest request = new HttpDelete(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.UNKNOWN_ATTRIBUTE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }
    
    /**
     * deviceIdが無い状態でonconnect属性のコールバック解除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /phone/onconnect?sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutOnConnectNoDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(PhoneProfileConstants.PROFILE_NAME);
        builder.setAttribute(PhoneProfileConstants.ATTRIBUTE_ON_CONNECT);
        builder.addParameter(DConnectMessage.EXTRA_SESSION_KEY, TEST_SESSION_KEY);
        builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpPut(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.EMPTY_DEVICE_ID.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceIdが空状態でonconnect属性のコールバック解除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /phone/onconnect?deviceId=&sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutOnConnectEmptyDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(PhoneProfileConstants.PROFILE_NAME);
        builder.setAttribute(PhoneProfileConstants.ATTRIBUTE_ON_CONNECT);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "");
        builder.addParameter(DConnectMessage.EXTRA_SESSION_KEY, TEST_SESSION_KEY);
        builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpPut(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 存在しないdeviceIdでonconnect属性のコールバック解除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /phone/onconnect?deviceId=123456789&sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutOnConnectInvalidDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(PhoneProfileConstants.PROFILE_NAME);
        builder.setAttribute(PhoneProfileConstants.ATTRIBUTE_ON_CONNECT);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(DConnectMessage.EXTRA_SESSION_KEY, TEST_SESSION_KEY);
        builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpPut(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceIdを2重に指定してonconnect属性のコールバック解除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /phone/onconnect?deviceId=123456789&deviceId=xxx&sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・先に定義された属性が優先されること。
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutOnConnectDuplicatedDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(PhoneProfileConstants.PROFILE_NAME);
        builder.setAttribute(PhoneProfileConstants.ATTRIBUTE_ON_CONNECT);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(DConnectMessage.EXTRA_SESSION_KEY, TEST_SESSION_KEY);
        builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpPut(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceIdが無い状態でonconnect属性のコールバック解除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /phone/onconnect?sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteOnConnectNoDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(PhoneProfileConstants.PROFILE_NAME);
        builder.setAttribute(PhoneProfileConstants.ATTRIBUTE_ON_CONNECT);
        builder.addParameter(DConnectMessage.EXTRA_SESSION_KEY, TEST_SESSION_KEY);
        builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpDelete(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.EMPTY_DEVICE_ID.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceIdが空状態でonconnect属性のコールバック解除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /phone/onconnect?deviceId=&sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteOnConnectEmptyDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(PhoneProfileConstants.PROFILE_NAME);
        builder.setAttribute(PhoneProfileConstants.ATTRIBUTE_ON_CONNECT);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "");
        builder.addParameter(DConnectMessage.EXTRA_SESSION_KEY, TEST_SESSION_KEY);
        builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpDelete(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 存在しないdeviceIdでonconnect属性のコールバック解除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /phone/onconnect?deviceId=123456789&sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteOnConnectInvalidDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(PhoneProfileConstants.PROFILE_NAME);
        builder.setAttribute(PhoneProfileConstants.ATTRIBUTE_ON_CONNECT);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(DConnectMessage.EXTRA_SESSION_KEY, TEST_SESSION_KEY);
        builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpDelete(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceIdを2重に指定してonconnect属性のコールバック解除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /phone/onconnect?deviceId=123456789&deviceId=xxx&sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・先に定義された属性が優先されること。
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteOnConnectDuplicatedDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(PhoneProfileConstants.PROFILE_NAME);
        builder.setAttribute(PhoneProfileConstants.ATTRIBUTE_ON_CONNECT);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(DConnectMessage.EXTRA_SESSION_KEY, TEST_SESSION_KEY);
        builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpDelete(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メソッドにGETを指定してonconnect属性のリクエストテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /phone/onconnect?deviceId=xxxx&sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteOnConnectInvalidMethodGet() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(PhoneProfileConstants.PROFILE_NAME);
        builder.setAttribute(PhoneProfileConstants.ATTRIBUTE_ON_CONNECT);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, TEST_SESSION_KEY);
        builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_SUPPORT_ACTION.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メソッドにPOSTを指定してonconnect属性のリクエストテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /phone/onconnect?deviceId=xxxx&sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteOnConnectInvalidMethodPost() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(PhoneProfileConstants.PROFILE_NAME);
        builder.setAttribute(PhoneProfileConstants.ATTRIBUTE_ON_CONNECT);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, TEST_SESSION_KEY);
        builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpPost(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.UNKNOWN_ATTRIBUTE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }
}
