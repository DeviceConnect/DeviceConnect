/*
 NormalUniqueProfileTestCase.java
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
import org.deviceconnect.profile.AuthorizationProfileConstants;
import org.deviceconnect.profile.DConnectProfileConstants;
import org.deviceconnect.utils.URIBuilder;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 独自プロファイルの正常系テスト.
 * <p>
 * 各メソッドについてリクエストが通知されるかどうかのみをテストする.
 * </p>
 * @author NTT DOCOMO, INC.
 */
public class NormalUniqueProfileTestCase extends RESTfulDConnectTestCase {

    /** プロファイル名: {@value} . */
    private static final String PROFILE_NAME = "unique";

    /** インターフェース名: {@value} . */
    private static final String INTERFACE_TEST = "test";

    /** 属性名: {@value} . */
    private static final String ATTIBUTE_PING = "ping";

    /** パラメータ: {@value} . */
    private static final String PARAM_PATH = "path";

    /** メソッド名: {@value} . */
    private static final String METHOD_GET = "GET";

    /** メソッド名: {@value} . */
    private static final String METHOD_POST = "POST";

    /** メソッド名: {@value} . */
    private static final String METHOD_PUT = "PUT";

    /** メソッド名: {@value} . */
    private static final String METHOD_DELETE = "DELETE";

    /**
     * コンストラクタ.
     * 
     * @param tag テストタグ
     */
    public NormalUniqueProfileTestCase(final String tag) {
        super(tag);
    }

    /**
     * デバイスのシステムプロファイルを取得する.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /unique
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testGetUnique() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(PROFILE_NAME);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultOK(root);
            assertEquals(createPath(METHOD_GET, null, null), root.getString(PARAM_PATH));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * デバイスのシステムプロファイルを取得する.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /unique/ping
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testGetUniquePing() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(PROFILE_NAME);
        builder.setAttribute(ATTIBUTE_PING);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultOK(root);
            assertEquals(createPath(METHOD_GET, null, ATTIBUTE_PING), root.getString(PARAM_PATH));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * デバイスのシステムプロファイルを取得する.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /unique/test/ping
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testGetUniqueTestPing() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(PROFILE_NAME);
        builder.setInterface(INTERFACE_TEST);
        builder.setAttribute(ATTIBUTE_PING);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultOK(root);
            assertEquals(createPath(METHOD_GET, INTERFACE_TEST, ATTIBUTE_PING), root.getString(PARAM_PATH));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * デバイスのシステムプロファイルを取得する.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /unique
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testPostUnique() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(PROFILE_NAME);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpPost(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultOK(root);
            assertEquals(createPath(METHOD_POST, null, null), root.getString(PARAM_PATH));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * デバイスのシステムプロファイルを取得する.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /unique/ping
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testPostUniquePing() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(PROFILE_NAME);
        builder.setAttribute(ATTIBUTE_PING);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpPost(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultOK(root);
            assertEquals(createPath(METHOD_POST, null, ATTIBUTE_PING), root.getString(PARAM_PATH));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * デバイスのシステムプロファイルを取得する.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /unique/test/ping
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testPostUniqueTestPing() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(PROFILE_NAME);
        builder.setInterface(INTERFACE_TEST);
        builder.setAttribute(ATTIBUTE_PING);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpPost(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultOK(root);
            assertEquals(createPath(METHOD_POST, INTERFACE_TEST, ATTIBUTE_PING), root.getString(PARAM_PATH));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * デバイスのシステムプロファイルを取得する.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /unique
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testPutUnique() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(PROFILE_NAME);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpPut(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultOK(root);
            assertEquals(createPath(METHOD_PUT, null, null), root.getString(PARAM_PATH));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * デバイスのシステムプロファイルを取得する.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /unique/ping
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testPutUniquePing() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(PROFILE_NAME);
        builder.setAttribute(ATTIBUTE_PING);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpPut(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultOK(root);
            assertEquals(createPath(METHOD_PUT, null, ATTIBUTE_PING), root.getString(PARAM_PATH));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * デバイスのシステムプロファイルを取得する.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /unique/test/ping
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testPutUniqueTestPing() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(PROFILE_NAME);
        builder.setInterface(INTERFACE_TEST);
        builder.setAttribute(ATTIBUTE_PING);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpPut(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultOK(root);
            assertEquals(createPath(METHOD_PUT, INTERFACE_TEST, ATTIBUTE_PING), root.getString(PARAM_PATH));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * デバイスのシステムプロファイルを取得する.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /unique
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testDeleteUnique() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(PROFILE_NAME);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpDelete(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultOK(root);
            assertEquals(createPath(METHOD_DELETE, null, null), root.getString(PARAM_PATH));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * デバイスのシステムプロファイルを取得する.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /unique/ping
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testDeleteUniquePing() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(PROFILE_NAME);
        builder.setAttribute(ATTIBUTE_PING);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpDelete(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultOK(root);
            assertEquals(createPath(METHOD_DELETE, null, ATTIBUTE_PING), root.getString(PARAM_PATH));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * デバイスのシステムプロファイルを取得する.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /unique/test/ping
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testDeleteUniqueTestPing() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(PROFILE_NAME);
        builder.setInterface(INTERFACE_TEST);
        builder.setAttribute(ATTIBUTE_PING);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpDelete(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultOK(root);
            assertEquals(createPath(METHOD_DELETE, INTERFACE_TEST, ATTIBUTE_PING), root.getString(PARAM_PATH));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 指定したAPIのパスを生成する.
     * 
     * @param method メソッド名
     * @param inter インターフェース名
     * @param attribute 属性名
     * @return パス
     */
    private static String createPath(final String method, final String inter, final String attribute) {
        StringBuilder builder = new StringBuilder();
        builder.append(method);
        builder.append(" /");
        builder.append(PROFILE_NAME);
        if (inter != null) {
            builder.append("/");
            builder.append(inter);
        }
        if (attribute != null) {
            builder.append("/");
            builder.append(attribute);
        }
        return builder.toString();
    }
}
