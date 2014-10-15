/*
 FailBatteryProfileTestCase.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.profile.restful.test;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.deviceconnect.android.test.plugin.profile.TestBatteryProfileConstants;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.message.DConnectMessage.ErrorCode;
import org.deviceconnect.profile.AuthorizationProfileConstants;
import org.deviceconnect.profile.BatteryProfileConstants;
import org.deviceconnect.profile.DConnectProfileConstants;
import org.deviceconnect.utils.URIBuilder;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Batteryプロファイルの異常系テスト.
 * @author NTT DOCOMO, INC.
 */
public class FailBatteryProfileTestCase extends RESTfulDConnectTestCase {
    /**
     * コンストラクタ.
     * @param string テストタグ
     */
    public FailBatteryProfileTestCase(final String string) {
        super(string);
    }

    /**
     * deviceidが無い状態でバッテリー全属性取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /battery
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetBatteryNoDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.EMPTY_DEVICE_ID.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceidが空状態でバッテリー全属性取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /battery?devicdid=
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetBatteryEmptyDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "");
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 存在しないdeviceidでバッテリー全属性取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /battery?devicdid=123456789
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetBatteryInvalidDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 定義にない属性を指定してバッテリー全属性取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /battery?devicdid=xxxxx&abc=abc
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・定義にない属性は無視されること。
     * ・resultが0で返ってくること。
     * ・chargingがfalseで返ってくること。
     * ・chargingtimeが50000で返ってくること。
     * ・dischargingtimeが10000で返ってくること。
     * ・levelが0.5で返ってくること。
     * </pre>
     */
    public void testGetBatteryUndefinedAttribute() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter("abc", "abc");
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultOK(root);
            assertEquals("charging is not equals.",
                    TestBatteryProfileConstants.CHARGING,
                    root.getBoolean(BatteryProfileConstants.ATTRIBUTE_CHARGING));
            assertEquals("chargingtime is not equals.",
                    TestBatteryProfileConstants.CHARGING_TIME,
                    root.getDouble(BatteryProfileConstants.ATTRIBUTE_CHARGING_TIME));
            assertEquals("dischargingtime is not equals.",
                    TestBatteryProfileConstants.DISCHARGING_TIME,
                    root.getDouble(BatteryProfileConstants.ATTRIBUTE_DISCHARGING_TIME));
            assertEquals("level is not equals.", 
                    TestBatteryProfileConstants.LEVEL,
                    root.getDouble(BatteryProfileConstants.ATTRIBUTE_LEVEL));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceidを2重に指定してバッテリー全属性取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /battery?devicdid=123456789&deviceid=xxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・先に定義された属性が優先されること。
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetBatteryDuplicatedDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メソッドにPOSTを指定してバッテリー全属性取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /battery?deviceid=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetBatteryInvalidMethodPost() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
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
     * メソッドにPUTを指定してバッテリー全属性取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /battery?deviceid=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetBatteryInvalidMethodPut() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpPut(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.UNKNOWN_ATTRIBUTE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メソッドにDELETEを指定してバッテリー全属性取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /battery?deviceid=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetBatteryInvalidMethodDelete() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpDelete(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.UNKNOWN_ATTRIBUTE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceidが無い状態でcharging属性取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /battery/charging
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetBatteryChargingNoDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_CHARGING);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.EMPTY_DEVICE_ID.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceidが空状態でcharging属性取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /battery/charging?devicdid=
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetBatteryChargingEmptyDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_CHARGING);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "");
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 存在しないdeviceidでcharging属性取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /battery/charging?devicdid=123456789
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetBatteryChargingInvalidDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_CHARGING);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 定義にない属性を指定してcharging属性取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /battery/charging?devicdid=xxxxx&abc=abc
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・定義にない属性は無視されること。
     * ・resultが0で返ってくること。
     * ・chargingがfalseで返ってくること。
     * </pre>
     */
    public void testGetBatteryChargingUndefinedAttribute() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_CHARGING);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter("abc", "abc");
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertNotNull("root is null.", root);
            assertEquals(DConnectMessage.RESULT_OK,
                    root.getInt(DConnectMessage.EXTRA_RESULT));
            assertEquals("level is not equals.", 
                    TestBatteryProfileConstants.CHARGING,
                    root.getBoolean(BatteryProfileConstants.ATTRIBUTE_CHARGING));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceidを2重に指定してcharging属性取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /battery/charging?devicdid=123456789&deviceid=xxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・先に定義された属性が優先されること。
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetBatteryChargingDuplicatedDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_CHARGING);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メソッドにPOSTを指定してcharging属性取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /battery/charging?deviceid=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetBatteryChargingInvalidMethodPost() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_CHARGING);
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
     * メソッドにPUTを指定してcharging属性取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /battery/charging?deviceid=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetBatteryChargingInvalidMethodPut() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_CHARGING);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpPut(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.UNKNOWN_ATTRIBUTE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メソッドにDELETEを指定してcharging属性取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /battery/charging?deviceid=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetBatteryChargingInvalidMethodDelete() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_CHARGING);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpDelete(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.UNKNOWN_ATTRIBUTE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceidが無い状態でchargingTime属性取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /battery/chargingTime
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetBatteryChargingTimeNoDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_CHARGING_TIME);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.EMPTY_DEVICE_ID.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceidが空状態でchargingTime属性取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /battery/chargingTime?devicdid=
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetBatteryChargingTimeEmptyDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_CHARGING_TIME);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "");
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 存在しないdeviceidでchargingTime属性取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /battery/chargingTime?devicdid=123456789
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetBatteryChargingTimeInvalidDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_CHARGING_TIME);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 定義にない属性を指定してchargingTime属性取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /battery/chargingTime?devicdid=xxxxx&abc=abc
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・定義にない属性は無視されること。
     * ・resultが0で返ってくること。
     * ・chargingTimeが50000で返ってくること。
     * </pre>
     */
    public void testGetBatteryChargingTimeUndefinedAttribute() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_CHARGING_TIME);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter("abc", "abc");
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertNotNull("root is null.", root);
            assertEquals(DConnectMessage.RESULT_OK,
                    root.getInt(DConnectMessage.EXTRA_RESULT));
            assertEquals("level is not equals.", 
                    TestBatteryProfileConstants.CHARGING_TIME,
                    root.getDouble(BatteryProfileConstants.ATTRIBUTE_CHARGING_TIME));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceidを2重に指定してchargingTime属性取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /battery/chargingTime?devicdid=123456789&deviceid=xxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・先に定義された属性が優先されること。
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetBatteryChargingTimeDuplicatedDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_CHARGING_TIME);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メソッドにPOSTを指定してchargingTime属性取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /battery/chargingTime?deviceid=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetBatteryChargingTimeInvalidMethodPost() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_CHARGING_TIME);
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
     * メソッドにPUTを指定してchargingTime属性取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /battery/chargingTime?deviceid=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetBatteryChargingTimeInvalidMethodPut() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_CHARGING_TIME);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpPut(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.UNKNOWN_ATTRIBUTE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メソッドにDELETEを指定してchargingTime属性取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /battery/chargingTime?deviceid=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetBatteryChargingTimeInvalidMethodDelete() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_CHARGING_TIME);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpDelete(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.UNKNOWN_ATTRIBUTE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceidが無い状態でdischargingTime属性取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /battery/dischargingTime
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetBatteryDischargingTimeNoDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_DISCHARGING_TIME);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.EMPTY_DEVICE_ID.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceidが空状態でdischargingTime属性取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /battery/dischargingTime?devicdid=
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetBatteryDischargingTimeEmptyDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_DISCHARGING_TIME);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "");
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 存在しないdeviceidでdischargingTime属性取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /battery/dischargingTime?devicdid=123456789
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetBatteryDischargingTimeInvalidDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_DISCHARGING_TIME);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 定義にない属性を指定してdischargingTime属性取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /battery/dischargingTime?devicdid=xxxxx&abc=abc
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・定義にない属性は無視されること。
     * ・resultが0で返ってくること。
     * ・chargingTimeが50000で返ってくること。
     * </pre>
     */
    public void testGetBatteryDischargingTimeUndefinedAttribute() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_DISCHARGING_TIME);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter("abc", "abc");
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertNotNull("root is null.", root);
            assertEquals(DConnectMessage.RESULT_OK,
                    root.getInt(DConnectMessage.EXTRA_RESULT));
            assertEquals("level is not equals.", 
                    TestBatteryProfileConstants.DISCHARGING_TIME,
                    root.getDouble(BatteryProfileConstants.ATTRIBUTE_DISCHARGING_TIME));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceidを2重に指定してdischargingTime属性取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /battery/dischargingTime?devicdid=123456789&deviceid=xxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・先に定義された属性が優先されること。
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetBatteryDischargingTimeDuplicatedDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_DISCHARGING_TIME);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メソッドにPOSTを指定してdischargingTime属性取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /battery/dischargingTime?deviceid=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetBatteryDischargingTimeInvalidMethodPost() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_DISCHARGING_TIME);
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
     * メソッドにPUTを指定してdischargingTime属性取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /battery/dischargingTime?deviceid=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetBatteryDischargingTimeInvalidMethodPut() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_DISCHARGING_TIME);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpPut(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.UNKNOWN_ATTRIBUTE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メソッドにDELETEを指定してdischargingTime属性取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /battery/dischargingTime?deviceid=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetBatteryDischargingTimeInvalidMethodDelete() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_DISCHARGING_TIME);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpDelete(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.UNKNOWN_ATTRIBUTE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceidが無い状態でlevel属性取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /battery/level
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetBatteryLevelNoDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_LEVEL);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.EMPTY_DEVICE_ID.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceidが空状態でlevel属性取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /battery/level?devicdid=
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetBatteryLevelEmptyDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_LEVEL);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "");
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 存在しないdeviceidでlevel属性取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /battery/level?devicdid=123456789
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetBatteryLevelInvalidDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_LEVEL);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 定義にない属性を指定してlevel属性取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /battery/level?devicdid=xxxxx&abc=abc
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・定義にない属性は無視されること。
     * ・resultが0で返ってくること。
     * ・chargingがfalseで返ってくること。
     * ・chargingtimeが50000で返ってくること。
     * ・dischargingtimeが10000で返ってくること。
     * ・levelが0.5で返ってくること。
     * </pre>
     */
    public void testGetBatteryLevelUndefinedAttribute() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_LEVEL);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter("abc", "abc");
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertNotNull("root is null.", root);
            assertEquals(DConnectMessage.RESULT_OK,
                    root.getInt(DConnectMessage.EXTRA_RESULT));
            assertEquals("level is not equals.", 
                    TestBatteryProfileConstants.LEVEL,
                    root.getDouble(BatteryProfileConstants.ATTRIBUTE_LEVEL));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceidを2重に指定してlevel属性取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /battery/level?devicdid=123456789&deviceid=xxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・先に定義された属性が優先されること。
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetBatteryLevelDuplicatedDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_LEVEL);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メソッドにPOSTを指定してlevel属性取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /battery/level?deviceid=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetBatteryLevelInvalidMethodPost() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_LEVEL);
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
     * メソッドにPUTを指定してlevel属性取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /battery/level?deviceid=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetBatteryLevelInvalidMethodPut() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_LEVEL);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpPut(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.UNKNOWN_ATTRIBUTE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メソッドにDELETEを指定してlevel属性取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /battery/level?deviceid=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetBatteryLevelInvalidMethodDelete() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_LEVEL);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpDelete(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.UNKNOWN_ATTRIBUTE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceidが無い状態でonchargingchange属性のコールバック登録テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /battery/onchargingchange?sessionKey=xxxx&sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutBatteryOnChargingChangeNoDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_ON_CHARGING_CHANGE);
        builder.addParameter(DConnectMessage.EXTRA_SESSION_KEY, TEST_SESSION_KEY);
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
     * deviceidが空状態でonchargingchange属性のコールバック登録テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /battery/onchargingchange?devicdid=&sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutBatteryOnChargingChangeEmptyDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_ON_CHARGING_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "");
        builder.addParameter(DConnectMessage.EXTRA_SESSION_KEY, TEST_SESSION_KEY);
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
     * 存在しないdeviceidでonchargingchange属性のコールバック登録テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /battery/onchargingchange?devicdid=123456789&sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutBatteryOnChargingChangeInvalidDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_ON_CHARGING_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(DConnectMessage.EXTRA_SESSION_KEY, TEST_SESSION_KEY);
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
     * 定義にない属性を指定してonchargingchange属性取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /battery/onchargingchange?devicdid=xxxxx&sessionKey=xxxx&abc=abc
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・定義にない属性は無視されること。
     * ・resultが0で返ってくること。
     * ・chargingがfalseで返ってくること。
     * </pre>
     */
    public void testPutBatteryOnChargingChangeUndefinedAttribute() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_ON_CHARGING_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(DConnectMessage.EXTRA_SESSION_KEY, TEST_SESSION_KEY);
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
     * deviceidを2重に指定してonchargingchange属性取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /battery/onchargingchange?devicdid=123456789&deviceid=xxx&sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・先に定義された属性が優先されること。
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutBatteryOnChargingChangeDuplicatedDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_ON_CHARGING_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(DConnectMessage.EXTRA_SESSION_KEY, TEST_SESSION_KEY);
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
     * deviceidが無い状態でonchargingchange属性のコールバック解除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /battery/onchargingchange?sessionKey=xxxx&sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteBatteryOnChargingChangeNoDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_ON_CHARGING_CHANGE);
        builder.addParameter(DConnectMessage.EXTRA_SESSION_KEY, TEST_SESSION_KEY);
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
     * deviceidが空状態でonchargingchange属性のコールバック解除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /battery/onchargingchange?devicdid=&sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteBatteryOnChargingChangeEmptyDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_ON_CHARGING_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "");
        builder.addParameter(DConnectMessage.EXTRA_SESSION_KEY, TEST_SESSION_KEY);
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
     * 存在しないdeviceidでonchargingchange属性のコールバック解除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /battery/onchargingchange?devicdid=123456789&sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteBatteryOnChargingChangeInvalidDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_ON_CHARGING_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(DConnectMessage.EXTRA_SESSION_KEY, TEST_SESSION_KEY);
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
     * 定義にない属性を指定してonchargingchange属性のコールバック解除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /battery/onchargingchange?devicdId=xxxxx&sessionKey=xxxx&abc=abc
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・定義にない属性は無視されること。
     * ・resultが0で返ってくること。
     * ・chargingがfalseで返ってくること。
     * </pre>
     */
    public void testDeleteBatteryOnChargingChangeUndefinedAttribute() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_ON_CHARGING_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(DConnectMessage.EXTRA_SESSION_KEY, TEST_SESSION_KEY);
        builder.addParameter("abc", "abc");
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpDelete(builder.toString());
            JSONObject root = sendRequest(request);
            assertNotNull("root is null.", root);
            assertEquals(DConnectMessage.RESULT_OK,
                    root.getInt(DConnectMessage.EXTRA_RESULT));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceidを2重に指定してonchargingchange属性のコールバック解除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /battery/onchargingchange?devicdid=123456789&deviceid=xxx&sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・先に定義された属性が優先されること。
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteBatteryOnChargingChangeDuplicatedDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_ON_CHARGING_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(DConnectMessage.EXTRA_SESSION_KEY, TEST_SESSION_KEY);
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
     * メソッドにGETを指定してonchargingchange属性のリクエストテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /battery/onchargingchange?deviceid=xxxx&sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteBatteryOnChargingChangeInvalidMethodGet() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_ON_CHARGING_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, TEST_SESSION_KEY);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.UNKNOWN_ATTRIBUTE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メソッドにPOSTを指定してonchargingchange属性のリクエストテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /battery/onchargingchange?deviceId=xxxx&sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteBatteryOnChargingChangeInvalidMethodPost() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_ON_CHARGING_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, TEST_SESSION_KEY);
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
     * deviceidが無い状態でonchargingtimechange属性のコールバック登録テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /battery/onchargingtimechange?sessionKey=xxxx&sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutBatteryOnBatteryChangeNoDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_ON_BATTERY_CHANGE);
        builder.addParameter(DConnectMessage.EXTRA_SESSION_KEY, TEST_SESSION_KEY);
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
     * deviceidが空状態でonchargingtimechange属性のコールバック登録テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /battery/onbatterychage?devicdid=&sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutBatteryOnBatteryChangeEmptyDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_ON_BATTERY_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "");
        builder.addParameter(DConnectMessage.EXTRA_SESSION_KEY, TEST_SESSION_KEY);
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
     * 存在しないdeviceidでonbatterychage属性のコールバック登録テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /battery/onbatterychage?devicdid=123456789&sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutBatteryOnBatteryChangeInvalidDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_ON_BATTERY_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(DConnectMessage.EXTRA_SESSION_KEY, TEST_SESSION_KEY);
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
     * 定義にない属性を指定してonbatterychage属性取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /battery/onbatterychage?devicdid=xxxxx&sessionKey=xxxx&abc=abc
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・定義にない属性は無視されること。
     * ・resultが0で返ってくること。
     * ・chargingがfalseで返ってくること。
     * </pre>
     */
    public void testPutBatteryOnBatteryChangeUndefinedAttribute() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_ON_BATTERY_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(DConnectMessage.EXTRA_SESSION_KEY, TEST_SESSION_KEY);
        builder.addParameter("abc", "abc");
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpPut(builder.toString());
            JSONObject root = sendRequest(request);
            assertNotNull("root is null.", root);
            assertEquals(DConnectMessage.RESULT_OK,
                    root.getInt(DConnectMessage.EXTRA_RESULT));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceidを2重に指定してonbatterychage属性取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /battery/onbatterychage?devicdid=123456789&deviceid=xxx&sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・先に定義された属性が優先されること。
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutBatteryOnBatteryChangeDuplicatedDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_ON_BATTERY_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(DConnectMessage.EXTRA_SESSION_KEY, TEST_SESSION_KEY);
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
     * deviceidが無い状態でonchargingtimechange属性のコールバック解除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /battery/onchargingtimechange?sessionKey=xxxx&sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteBatteryOnBatteryChangeNoDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_ON_BATTERY_CHANGE);
        builder.addParameter(DConnectMessage.EXTRA_SESSION_KEY, TEST_SESSION_KEY);
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
     * deviceidが空状態でonbatterychage属性のコールバック解除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /battery/onbatterychage?devicdid=&sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteBatteryOnBatteryChangeEmptyDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_ON_BATTERY_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "");
        builder.addParameter(DConnectMessage.EXTRA_SESSION_KEY, TEST_SESSION_KEY);
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
     * 存在しないdeviceidでonbatterychage属性のコールバック解除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /battery/onbatterychage?devicdid=123456789&sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteBatteryOnBatteryChangeInvalidDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_ON_BATTERY_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(DConnectMessage.EXTRA_SESSION_KEY, TEST_SESSION_KEY);
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
     * 定義にない属性を指定してonbatterychage属性のコールバック解除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /battery/onbatterychage?devicdId=xxxxx&sessionKey=xxxx&abc=abc
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・定義にない属性は無視されること。
     * ・resultが0で返ってくること。
     * ・chargingがfalseで返ってくること。
     * </pre>
     */
    public void testDeleteBatteryOnBatteryChangeUndefinedAttribute() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_ON_BATTERY_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(DConnectMessage.EXTRA_SESSION_KEY, getClientId());
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
     * deviceidを2重に指定してonbatterychage属性のコールバック解除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /battery/onbatterychage?devicdid=123456789&deviceid=xxx&sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・先に定義された属性が優先されること。
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteBatteryOnBatteryChangeDuplicatedDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_ON_BATTERY_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(DConnectMessage.EXTRA_SESSION_KEY, getClientId());
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpDelete(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(root);
            assertEquals(ErrorCode.NOT_FOUND_DEVICE.getCode(), root.getInt(DConnectMessage.EXTRA_ERROR_CODE));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メソッドにGETを指定してonchargingtimechange属性のリクエストテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /battery/onchargingtimechange?deviceid=xxxx&sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteBatteryOnBatteryChangeInvalidMethodGet() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_ON_BATTERY_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, TEST_SESSION_KEY);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.UNKNOWN_ATTRIBUTE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メソッドにPOSTを指定してonchargingtimechange属性のリクエストテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /battery/onchargingtimechange?deviceId=xxxx&sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteBatteryOnBatteryChangeInvalidMethodPost() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(BatteryProfileConstants.PROFILE_NAME);
        builder.setAttribute(BatteryProfileConstants.ATTRIBUTE_ON_BATTERY_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, TEST_SESSION_KEY);
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
