/*
 FailConnectProfileTestCase.java
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
import org.deviceconnect.message.DConnectMessage.ErrorCode;
import org.deviceconnect.profile.AuthorizationProfileConstants;
import org.deviceconnect.profile.ConnectProfileConstants;
import org.deviceconnect.profile.DConnectProfileConstants;
import org.deviceconnect.utils.URIBuilder;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Connectプロファイルの異常系テスト.
 * @author NTT DOCOMO, INC.
 */
public class FailConnectProfileTestCase extends RESTfulDConnectTestCase {

    /**
     * コンストラクタ.
     * 
     * @param tag テストタグ
     */
    public FailConnectProfileTestCase(final String tag) {
        super(tag);
    }

    /**
     * deviceIdが無い状態でWiFi機能有効状態(ON/OFF)取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /connect/wifi
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetWifi001() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_WIFI);
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
     * deviceIdが空状態でWiFi機能有効状態(ON/OFF)取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /connect/wifi?deviceId=
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetWifi002() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_WIFI);
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
     * 存在しないdeviceIdでWiFi機能有効状態(ON/OFF)取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /connect/wifi?deviceId=123456789
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetWifi003() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_WIFI);
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
     * 定義にない属性を指定してWiFi機能有効状態(ON/OFF)取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /connect/wifi?deviceId=xxxxx&abc=abc
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・定義にない属性は無視されること。
     * ・resultが0で返ってくること。
     * ・powerがfalseで返ってくること。
     * </pre>
     */
    public void testGetWifi004() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_WIFI);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter("abc", "abc");
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultOK(root);
            assertEquals(true, root.getBoolean(ConnectProfileConstants.PARAM_ENABLE));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceIdを2重に指定してWiFi機能有効状態(ON/OFF)取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /connect/wifi?deviceId=123456789&deviceId=xxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・先に定義された属性が優先されること。
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetWifi005() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_WIFI);
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
     * deviceIdが無い状態でWiFi機能有効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /connect/wifi
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutWifi001() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_WIFI);
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
     * deviceIdが空状態でWiFi機能有効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /connect/wifi?deviceId=
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutWifi002() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_WIFI);
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
     * 存在しないdeviceIdでWiFi機能有効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /connect/wifi?deviceId=123456789
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutWifi003() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_WIFI);
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
     * 定義にない属性を指定してWiFi機能有効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /connect/wifi?deviceId=xxxxx&abc=abc
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・定義にない属性は無視されること。
     * ・resultが0で返ってくること。
     * </pre>
     */
    public void testPutWifi004() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_WIFI);
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
     * deviceIdを2重に指定してWiFi機能有効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /connect/wifi?deviceId=123456789&deviceId=xxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・先に定義された属性が優先されること。
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutWifi005() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_WIFI);
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
     * deviceIdが無い状態でWiFi機能無効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /connect/wifi
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteWifi001() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_WIFI);
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
     * deviceIdが空状態でWiFi機能無効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /connect/wifi?deviceId=
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteWifi002() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_WIFI);
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
     * 存在しないdeviceIdでWiFi機能無効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /connect/wifi?deviceId=123456789
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteWifi003() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_WIFI);
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
     * 定義にない属性を指定してWiFi機能無効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /connect/wifi?deviceId=xxxxx&abc=abc
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・定義にない属性は無視されること。
     * ・resultが0で返ってくること。
     * </pre>
     */
    public void testDeleteWifi004() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_WIFI);
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
     * deviceIdを2重に指定してWiFi機能無効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /connect/wifi?deviceId=123456789&deviceId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・先に定義された属性が優先されること。
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteWifi005() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_WIFI);
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
     * メソッドをPOSTに指定してWiFi機能無効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /connect/wifi?deviceId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testConnectWifiInvalidMethodPost() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_WIFI);
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
     * deviceIdが無い状態でonwifichange属性のコールバック登録テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /connect/onwifichange
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutOnWifiChange001() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_ON_WIFI_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
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
     * deviceIdが空状態でonwifichange属性のコールバック登録テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /connect/onwifichange?deviceId=
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutOnWifiChange002() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_ON_WIFI_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "");
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
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
     * 存在しないdeviceIdでonwifichange属性のコールバック登録テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /connect/onwifichange?deviceId=123456789
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutOnWifiChange003() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_ON_WIFI_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
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
     * 定義にない属性を指定してonwifichange属性のコールバック登録テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /connect/onwifichange?deviceId=xxxxx&abc=abc
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・定義にない属性は無視されること。
     * ・resultが0で返ってくること。
     * </pre>
     */
    public void testPutOnWifiChange004() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_ON_WIFI_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter("abc", "abc");
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
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
     * deviceIdを2重に指定してonwifichange属性のコールバック登録テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /connect/onwifichange?deviceId=123456789&deviceId=xxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・先に定義された属性が優先されること。
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutOnWifiChange005() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_ON_WIFI_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
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
     * deviceIdが無い状態でonwifichange属性のコールバック解除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /connect/onwifichange
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteOnWifiChange001() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_ON_WIFI_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
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
     * deviceIdが空状態でonwifichange属性のコールバック解除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /connect/onwifichange?deviceId=
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteOnWifiChange002() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_ON_WIFI_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "");
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
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
     * 存在しないdeviceIdでonwifichange属性のコールバック解除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /connect/onwifichange?deviceId=123456789
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteOnWifiChange003() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_ON_WIFI_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
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
     * 定義にない属性を指定してonwifichange属性のコールバック解除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /connect/onwifichange?deviceId=xxxxx&abc=abc
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・定義にない属性は無視されること。
     * ・resultが0で返ってくること。
     * </pre>
     */
    public void testDeleteOnWifiChange004() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_ON_WIFI_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter("abc", "abc");
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
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
     * deviceIdを2重に指定してonwifichange属性のコールバック解除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /connect/onwifichange?deviceId=123456789&deviceId=xxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・先に定義された属性が優先されること。
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteOnWifiChange005() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_ON_WIFI_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
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
     * メソッドをGETに指定して/connect/onwifichangeにアクセスするテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /connect/onwifichange?deviceId=xxxx&sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testConnectOnWifiChangeInvalidMethodGet() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_ON_WIFI_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
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
     * メソッドをPOSTに指定して/connect/onwifichangeにアクセスするテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /connect/onwifichange?deviceId=xxxx&sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testConnectOnWifiChangeInvalidMethodPost() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_ON_WIFI_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
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
     * deviceIdが無い状態でBluetooth機能有効状態(ON/OFF)取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /connect/bluetooth
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetBluetooth001() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_BLUETOOTH);
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
     * deviceIdが空状態でBluetooth機能有効状態(ON/OFF)取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /connect/bluetooth?deviceId=
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetBluetooth002() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_BLUETOOTH);
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
     * 存在しないdeviceIdでBluetooth機能有効状態(ON/OFF)取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /connect/bluetooth?deviceId=123456789
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetBluetooth003() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_BLUETOOTH);
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
     * 定義にない属性を指定してBluetooth機能有効状態(ON/OFF)取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /connect/bluetooth?deviceId=xxxxx&abc=abc
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・定義にない属性は無視されること。
     * ・resultが0で返ってくること。
     * ・powerがfalseで返ってくること。
     * </pre>
     */
    public void testGetBluetooth004() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_BLUETOOTH);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter("abc", "abc");
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultOK(root);
            assertEquals(true, root.getBoolean(ConnectProfileConstants.PARAM_ENABLE));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceIdを2重に指定してBluetooth機能有効状態(ON/OFF)取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /connect/bluetooth?deviceId=123456789&deviceId=xxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・先に定義された属性が優先されること。
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetBluetooth005() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_BLUETOOTH);
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
     * deviceIdが無い状態でBluetooth機能有効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /connect/bluetooth
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutBluetooth001() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_BLUETOOTH);
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
     * deviceIdが空状態でBluetooth機能有効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /connect/bluetooth?deviceId=
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutBluetooth002() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_BLUETOOTH);
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
     * 存在しないdeviceIdでBluetooth機能有効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /connect/bluetooth?deviceId=123456789
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutBluetooth003() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_BLUETOOTH);
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
     * 定義にない属性を指定してBluetooth機能有効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /connect/bluetooth?deviceId=xxxxx&abc=abc
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・定義にない属性は無視されること。
     * ・resultが0で返ってくること。
     * </pre>
     */
    public void testPutBluetooth004() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_BLUETOOTH);
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
     * deviceIdを2重に指定してBluetooth機能有効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /connect/bluetooth?deviceId=123456789&deviceId=xxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・先に定義された属性が優先されること。
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutBluetooth005() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_BLUETOOTH);
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
     * deviceIdが無い状態でBluetooth機能無効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /connect/bluetooth
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteBluetooth001() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_BLUETOOTH);
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
     * deviceIdが空状態でBluetooth機能無効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /connect/bluetooth?deviceId=
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteBluetooth002() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_BLUETOOTH);
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
     * 存在しないdeviceIdでBluetooth機能無効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /connect/bluetooth?deviceId=123456789
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteBluetooth003() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_BLUETOOTH);
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
     * 定義にない属性を指定してBluetooth機能無効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /connect/bluetooth?deviceId=xxxxx&abc=abc
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・定義にない属性は無視されること。
     * ・resultが0で返ってくること。
     * </pre>
     */
    public void testDeleteBluetooth004() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_BLUETOOTH);
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
     * deviceIdを2重に指定してBluetooth機能無効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /connect/bluetooth?deviceId=123456789&deviceId=xxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・先に定義された属性が優先されること。
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteBluetooth005() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_BLUETOOTH);
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
     * メソッドをPOSTに指定してBluetooth機能無効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /connect/bluetooth?deviceId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testConnectBluetoothInvalidMethodPost() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_BLUETOOTH);
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
     * deviceIdが無い状態でBluetooth機能有効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /connect/bluetooth/discoverable
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutBluetoothAvailable001() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setInterface(ConnectProfileConstants.INTERFACE_BLUETOOTH);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_DISCOVERABLE);
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
     * deviceIdが空状態でBluetooth機能有効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /connect/bluetooth/discoverable?deviceId=
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutBluetoothAvailable002() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setInterface(ConnectProfileConstants.INTERFACE_BLUETOOTH);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_DISCOVERABLE);
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
     * 存在しないdeviceIdでBluetooth機能有効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /connect/bluetooth/discoverable?deviceId=123456789
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutBluetoothAvailable003() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setInterface(ConnectProfileConstants.INTERFACE_BLUETOOTH);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_DISCOVERABLE);
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
     * 定義にない属性を指定してBluetooth機能有効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /connect/bluetooth/discoverable?deviceId=xxxxx&abc=abc
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・定義にない属性は無視されること。
     * ・resultが0で返ってくること。
     * </pre>
     */
    public void testPutBluetoothAvailable004() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setInterface(ConnectProfileConstants.INTERFACE_BLUETOOTH);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_DISCOVERABLE);
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
     * deviceIdを2重に指定してBluetooth機能有効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /connect/bluetooth/discoverable?deviceId=123456789&deviceId=xxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・先に定義された属性が優先されること。
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutBluetoothAvailable005() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setInterface(ConnectProfileConstants.INTERFACE_BLUETOOTH);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_DISCOVERABLE);
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
     * deviceIdが無い状態でBluetooth機能無効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /connect/bluetooth/discoverable
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteBluetoothAvailable001() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setInterface(ConnectProfileConstants.INTERFACE_BLUETOOTH);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_DISCOVERABLE);
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
     * deviceIdが空状態でBluetooth機能無効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /connect/bluetooth/discoverable?deviceId=
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteBluetoothAvailable002() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setInterface(ConnectProfileConstants.INTERFACE_BLUETOOTH);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_DISCOVERABLE);
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
     * 存在しないdeviceIdでBluetooth機能無効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /connect/bluetooth/discoverable?deviceId=123456789
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteBluetoothAvailable003() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setInterface(ConnectProfileConstants.INTERFACE_BLUETOOTH);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_DISCOVERABLE);
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
     * 定義にない属性を指定してBluetooth機能無効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /connect/bluetooth/discoverable?deviceId=xxxxx&abc=abc
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・定義にない属性は無視されること。
     * ・resultが0で返ってくること。
     * </pre>
     */
    public void testDeleteBluetoothAvailable004() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setInterface(ConnectProfileConstants.INTERFACE_BLUETOOTH);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_DISCOVERABLE);
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
     * deviceIdを2重に指定してBluetooth機能無効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /connect/bluetooth/discoverable?deviceId=123456789&deviceId=xxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・先に定義された属性が優先されること。
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteBluetoothAvailable005() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setInterface(ConnectProfileConstants.INTERFACE_BLUETOOTH);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_DISCOVERABLE);
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
     * deviceIdが無い状態でonbluetoothchange属性のコールバック登録テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /connect/onbluetoothchange
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutOnBluetoothChange001() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_ON_BLUETOOTH_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
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
     * deviceIdが空状態でonbluetoothchange属性のコールバック登録テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /connect/onbluetoothchange?deviceId=
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutOnBluetoothChange002() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_ON_BLUETOOTH_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "");
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
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
     * 存在しないdeviceIdでonbluetoothchange属性のコールバック登録テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /connect/onbluetoothchange?deviceId=123456789
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutOnBluetoothChange003() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_ON_BLUETOOTH_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
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
     * 定義にない属性を指定してonbluetoothchange属性のコールバック登録テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /connect/onbluetoothchange?deviceId=xxxxx&abc=abc
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・定義にない属性は無視されること。
     * ・resultが0で返ってくること。
     * </pre>
     */
    public void testPutOnBluetoothChange004() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_ON_BLUETOOTH_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter("abc", "abc");
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
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
     * deviceIdを2重に指定してonbluetoothchange属性のコールバック登録テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /connect/onbluetoothchange?deviceId=123456789&deviceId=xxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・先に定義された属性が優先されること。
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutOnBluetoothChange005() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_ON_BLUETOOTH_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
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
     * deviceIdが無い状態でonbluetoothchange属性のコールバック解除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /connect/onbluetoothchange
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteOnBluetoothChange001() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_ON_BLUETOOTH_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
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
     * deviceIdが空状態でonbluetoothchange属性のコールバック解除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /connect/onbluetoothchange?deviceId=
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteOnBluetoothChange002() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_ON_BLUETOOTH_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "");
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
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
     * 存在しないdeviceIdでonbluetoothchange属性のコールバック解除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /connect/onbluetoothchange?deviceId=123456789
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteOnBluetoothChange003() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_ON_BLUETOOTH_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
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
     * 定義にない属性を指定してonbluetoothchange属性のコールバック解除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /connect/onbluetoothchange?deviceId=xxxxx&abc=abc
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・定義にない属性は無視されること。
     * ・resultが0で返ってくること。
     * </pre>
     */
    public void testDeleteOnBluetoothChange004() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_ON_BLUETOOTH_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter("abc", "abc");
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
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
     * deviceIdを2重に指定してonbluetoothchange属性のコールバック解除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /connect/onbluetoothchange?deviceId=123456789&deviceId=xxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・先に定義された属性が優先されること。
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteOnBluetoothChange005() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_ON_BLUETOOTH_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
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
     * メソッドをGETに指定して/connect/onbluetoothchangeにアクセスするテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /connect/onbluetoothchange?deviceId=xxxx&sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testConnectOnBluetoothChangeInvalidMethodGet() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_ON_BLUETOOTH_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
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
     * メソッドをPOSTに指定して/connect/onbluetoothchangeにアクセスするテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /connect/onbluetoothchange?deviceId=xxxx&sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testConnectOnBluetoothChangeInvalidMethodPost() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_ON_BLUETOOTH_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
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
     * deviceIdが無い状態でNFC機能有効状態(ON/OFF)取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /connect/nfc
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetNFC001() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_NFC);
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
     * deviceIdが空状態でNFC機能有効状態(ON/OFF)取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /connect/nfc?deviceId=
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetNFC002() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_NFC);
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
     * 存在しないdeviceIdでNFC機能有効状態(ON/OFF)取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /connect/nfc?deviceId=123456789
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetNFC003() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_NFC);
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
     * 定義にない属性を指定してNFC機能有効状態(ON/OFF)取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /connect/nfc?deviceId=xxxxx&abc=abc
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・定義にない属性は無視されること。
     * ・resultが0で返ってくること。
     * ・powerがfalseで返ってくること。
     * </pre>
     */
    public void testGetNFC004() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_NFC);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter("abc", "abc");
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultOK(root);
            assertEquals(true, root.getBoolean(ConnectProfileConstants.PARAM_ENABLE));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceIdを2重に指定してNFC機能有効状態(ON/OFF)取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /connect/nfc?deviceId=123456789&deviceId=xxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・先に定義された属性が優先されること。
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetNFC005() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_NFC);
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
     * deviceIdが無い状態でNFC機能有効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /connect/nfc
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutNFC001() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_NFC);
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
     * deviceIdが空状態でNFC機能有効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /connect/nfc?deviceId=
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutNFC002() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_NFC);
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
     * 存在しないdeviceIdでNFC機能有効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /connect/nfc?deviceId=123456789
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutNFC003() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_NFC);
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
     * 定義にない属性を指定してNFC機能有効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /connect/nfc?deviceId=xxxxx&abc=abc
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・定義にない属性は無視されること。
     * ・resultが0で返ってくること。
     * </pre>
     */
    public void testPutNFC004() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_NFC);
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
     * deviceIdを2重に指定してNFC機能有効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /connect/nfc?deviceId=123456789&deviceId=xxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・先に定義された属性が優先されること。
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutNFC005() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_NFC);
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
     * deviceIdが無い状態でNFC機能無効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /connect/nfc
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteNFC001() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_NFC);
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
     * deviceIdが空状態でNFC機能無効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /connect/nfc?deviceId=
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteNFC002() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_NFC);
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
     * 存在しないdeviceIdでNFC機能無効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /connect/nfc?deviceId=123456789
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteNFC003() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_NFC);
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
     * 定義にない属性を指定してNFC機能無効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /connect/nfc?deviceId=xxxxx&abc=abc
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・定義にない属性は無視されること。
     * ・resultが0で返ってくること。
     * </pre>
     */
    public void testDeleteNFC004() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_NFC);
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
     * deviceIdを2重に指定してNFC機能無効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /connect/nfc?deviceId=123456789&deviceId=xxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・先に定義された属性が優先されること。
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteNFC005() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_NFC);
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
     * メソッドをPOSTに指定してNFC機能無効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /connect/nfc?deviceId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testConnectNFCInvalidMethodPost() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_NFC);
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
     * deviceIdが無い状態でonnfcchange属性のコールバック登録テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /connect/onnfcchange
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutOnNFCChange001() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_ON_NFC_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
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
     * deviceIdが空状態でonnfcchange属性のコールバック登録テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /connect/onnfcchange?deviceId=
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutOnNFCChange002() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_ON_NFC_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "");
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
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
     * 存在しないdeviceIdでonnfcchange属性のコールバック登録テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /connect/onnfcchange?deviceId=123456789
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutOnNFCChange003() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_ON_NFC_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
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
     * 定義にない属性を指定してonnfcchange属性のコールバック登録テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /connect/onnfcchange?deviceId=xxxxx&abc=abc
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・定義にない属性は無視されること。
     * ・resultが0で返ってくること。
     * </pre>
     */
    public void testPutOnNFCChange004() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_ON_NFC_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter("abc", "abc");
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
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
     * deviceIdを2重に指定してonnfcchange属性のコールバック登録テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /connect/onnfcchange?deviceId=123456789&deviceId=xxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・先に定義された属性が優先されること。
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutOnNFCChange005() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_ON_NFC_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
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
     * deviceIdが無い状態でonnfcchange属性のコールバック解除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /connect/onnfcchange
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteOnNFCChange001() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_ON_NFC_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
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
     * deviceIdが空状態でonnfcchange属性のコールバック解除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /connect/onnfcchange?deviceId=
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteOnNFCChange002() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_ON_NFC_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "");
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
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
     * 存在しないdeviceIdでonnfcchange属性のコールバック解除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /connect/onnfcchange?deviceId=123456789
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteOnNFCChange003() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_ON_NFC_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
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
     * 定義にない属性を指定してonnfcchange属性のコールバック解除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /connect/onnfcchange?deviceId=xxxxx&abc=abc
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・定義にない属性は無視されること。
     * ・resultが0で返ってくること。
     * </pre>
     */
    public void testDeleteOnNFCChange004() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_ON_NFC_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter("abc", "abc");
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
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
     * deviceIdを2重に指定してonnfcchange属性のコールバック解除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /connect/onnfcchange?deviceId=123456789&deviceId=xxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・先に定義された属性が優先されること。
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteOnNFCChange005() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_ON_NFC_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
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
     * メソッドをGETに指定して/connect/onnfcchangeにアクセスするテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /connect/onnfcchange?deviceId=xxxx&sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testConnectOnNFCChangeInvalidMethodGet() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_ON_NFC_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
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
     * メソッドをPOSTに指定して/connect/onnfcchangeにアクセスするテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /connect/onnfcchange?deviceId=xxxx&sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testConnectOnNFCChangeInvalidMethodPost() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_ON_NFC_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
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
     * deviceIdが無い状態でBLE機能有効状態(ON/OFF)取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /connect/ble
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetBLE001() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_BLE);
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
     * deviceIdが空状態でBLE機能有効状態(ON/OFF)取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /connect/ble?deviceId=
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetBLE002() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_BLE);
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
     * 存在しないdeviceIdでBLE機能有効状態(ON/OFF)取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /connect/ble?deviceId=123456789
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetBLE003() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_BLE);
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
     * 定義にない属性を指定してBLE機能有効状態(ON/OFF)取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /connect/ble?deviceId=xxxxx&abc=abc
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・定義にない属性は無視されること。
     * ・resultが0で返ってくること。
     * ・powerがfalseで返ってくること。
     * </pre>
     */
    public void testGetBLE004() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_BLE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter("abc", "abc");
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultOK(root);
            assertEquals(true, root.getBoolean(ConnectProfileConstants.PARAM_ENABLE));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceIdを2重に指定してBLE機能有効状態(ON/OFF)取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /connect/ble?deviceId=123456789&deviceId=xxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・先に定義された属性が優先されること。
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetBLE005() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_BLE);
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
     * deviceIdが無い状態でBLE機能有効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /connect/ble
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutBLE001() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_BLE);
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
     * deviceIdが空状態でBLE機能有効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /connect/ble?deviceId=
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutBLE002() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_BLE);
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
     * 存在しないdeviceIdでBLE機能有効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /connect/ble?deviceId=123456789
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutBLE003() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_BLE);
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
     * 定義にない属性を指定してBLE機能有効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /connect/ble?deviceId=xxxxx&abc=abc
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・定義にない属性は無視されること。
     * ・resultが0で返ってくること。
     * </pre>
     */
    public void testPutBLE004() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_BLE);
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
     * deviceIdを2重に指定してBLE機能有効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /connect/ble?deviceId=123456789&deviceId=xxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・先に定義された属性が優先されること。
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutBLE005() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_BLE);
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
     * deviceIdが無い状態でBLE機能無効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /connect/ble
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteBLE001() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_BLE);
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
     * deviceIdが空状態でBLE機能無効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /connect/ble?deviceId=
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteBLE002() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_BLE);
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
     * 存在しないdeviceIdでBLE機能無効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /connect/ble?deviceId=123456789
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteBLE003() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_BLE);
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
     * 定義にない属性を指定してBLE機能無効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /connect/ble?deviceId=xxxxx&abc=abc
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・定義にない属性は無視されること。
     * ・resultが0で返ってくること。
     * </pre>
     */
    public void testDeleteBLE004() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_BLE);
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
     * deviceIdを2重に指定してBLE機能無効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /connect/ble?deviceId=123456789&deviceId=xxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・先に定義された属性が優先されること。
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteBLE005() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_BLE);
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
     * メソッドをPOSTに指定してBLE機能無効化テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /connect/ble?deviceId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testConnectBLEInvalidMethodPost() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_BLE);
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
     * deviceIdが無い状態でonblechange属性のコールバック登録テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /connect/onblechange
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutOnBLEChange001() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_ON_BLE_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
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
     * deviceIdが空状態でonblechange属性のコールバック登録テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /connect/onblechange?deviceId=
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutOnBLEChange002() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_ON_BLE_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "");
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
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
     * 存在しないdeviceIdでonblechange属性のコールバック登録テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /connect/onblechange?deviceId=123456789
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutOnBLEChange003() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_ON_BLE_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
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
     * 定義にない属性を指定してonblechange属性のコールバック登録テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /connect/onblechange?deviceId=xxxxx&abc=abc
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・定義にない属性は無視されること。
     * ・resultが0で返ってくること。
     * </pre>
     */
    public void testPutOnBLEChange004() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_ON_BLE_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
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
     * deviceIdを2重に指定してonblechange属性のコールバック登録テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /connect/onblechange?deviceId=123456789&deviceId=xxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・先に定義された属性が優先されること。
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPutOnBLEChange005() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_ON_BLE_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
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
     * deviceIdが無い状態でonblechange属性のコールバック解除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /connect/onblechange
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteOnBLEChange001() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_ON_BLE_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
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
     * deviceIdが空状態でonblechange属性のコールバック解除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /connect/onblechange?deviceId=
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteOnBLEChange002() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_ON_BLE_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "");
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
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
     * 存在しないdeviceIdでonblechange属性のコールバック解除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /connect/onblechange?deviceId=123456789
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteOnBLEChange003() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_ON_BLE_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
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
     * 定義にない属性を指定してonblechange属性のコールバック解除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /connect/onblechange?deviceId=xxxxx&abc=abc
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・定義にない属性は無視されること。
     * ・resultが0で返ってくること。
     * </pre>
     */
    public void testDeleteOnBLEChange004() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_ON_BLE_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter("abc", "abc");
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
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
     * deviceIdを2重に指定してonblechange属性のコールバック解除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /connect/onblechange?deviceId=123456789&deviceId=xxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・先に定義された属性が優先されること。
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteOnBLEChange005() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_ON_BLE_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
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
     * メソッドをGETに指定して/connect/onblechangeにアクセスするテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /connect/onblechange?deviceId=xxxx&sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testConnectOnBLEChangeInvalidMethodGet() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_ON_BLE_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
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
     * メソッドをPOSTに指定して/connect/onblechangeにアクセスするテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /connect/onblechange?deviceId=xxxx&sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testConnectOnBLEChangeInvalidMethodPost() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(ConnectProfileConstants.PROFILE_NAME);
        builder.setAttribute(ConnectProfileConstants.ATTRIBUTE_ON_BLE_CHANGE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
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
