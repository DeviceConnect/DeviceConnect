/*
 NormalSystemProfileTestCase.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.profile.restful.test;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.deviceconnect.android.test.plugin.profile.TestSystemProfileConstants;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.profile.AuthorizationProfileConstants;
import org.deviceconnect.profile.DConnectProfileConstants;
import org.deviceconnect.profile.SystemProfileConstants;
import org.deviceconnect.utils.URIBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Network Service Discoveryプロファイルの正常系テスト.
 * @author NTT DOCOMO, INC.
 */
public class NormalSystemProfileTestCase extends RESTfulDConnectTestCase
    implements TestSystemProfileConstants {

    /** テスト用デバイスプラグインID. */
    private String testPluginID;

    /**
     * コンストラクタ.
     * @param string テストタグ
     */
    public NormalSystemProfileTestCase(final String string) {
        super(string);
    }

    @Override
    protected void tearDown() throws Exception {
        testPluginID = null;
        super.tearDown();
    }

    /**
     * デバイスのシステムプロファイルを取得する.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /system
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・versionにString型の値が返ってくること。
     * ・supportsにJSONArray型の値が返ってくること。
     * ・pluginsにテスト用デバイスプラグインの情報が含まれていること。
     * </pre>
     */
    public void testGetSystem() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + SystemProfileConstants.PROFILE_NAME);
        builder.append("?");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject resp = sendRequest(request);
            assertResultOK(resp);
            assertEquals(VERSION, 
                    resp.getString(SystemProfileConstants.PARAM_VERSION));
            JSONArray supports = resp.getJSONArray(SystemProfileConstants.PARAM_SUPPORTS);
            assertNotNull(supports);
            JSONArray plugins = resp.getJSONArray(SystemProfileConstants.PARAM_PLUGINS);
            assertNotNull(plugins);
            JSONObject testPlugin = null;
            for (int i = 0; i < plugins.length(); i++) {
                JSONObject plugin = plugins.getJSONObject(i);
                if ("Device Connect Device Plugin for Test".equals(plugin.getString(SystemProfileConstants.PARAM_NAME))) {
                    testPlugin = plugin;
                    break;
                }
            }
            assertNotNull(testPlugin);
            String id = testPlugin.getString(SystemProfileConstants.PARAM_ID);
            assertNotNull(id);
            testPluginID = id;
            assertNotNull(testPlugin.getString(SystemProfileConstants.PARAM_NAME));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * デバイスのシステムプロファイルを取得する.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /system/device?deviceid=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・versionにStringが返ってくること。
     * </pre>
     */
    public void testGetSystemDevice() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + SystemProfileConstants.PROFILE_NAME);
        builder.append("/" + SystemProfileConstants.ATTRIBUTE_DEVICE);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject resp = sendRequest(request);
            assertNotNull("root is null.", resp);
            assertEquals(DConnectMessage.RESULT_OK,
                    resp.getInt(DConnectMessage.EXTRA_RESULT));
            assertEquals("1.0", 
                    resp.getString(SystemProfileConstants.PARAM_VERSION));
            JSONObject connect = resp.getJSONObject(SystemProfileConstants.PARAM_CONNECT);
            assertNotNull(connect);
            assertEquals(false, connect.getBoolean(SystemProfileConstants.PARAM_WIFI));
            assertEquals(false, connect.getBoolean(SystemProfileConstants.PARAM_BLUETOOTH));
            assertEquals(false, connect.getBoolean(SystemProfileConstants.PARAM_NFC));
            assertEquals(false, connect.getBoolean(SystemProfileConstants.PARAM_BLE));
            JSONArray supports = resp.getJSONArray(SystemProfileConstants.PARAM_SUPPORTS);
            assertNotNull(supports);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * デバイスプラグインの機能を有効にする.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /system/device/wakeup?deviceid=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・versionにStringが返ってくること。
     * </pre>
     */
    public void testPutSystemWakeup() {
        testGetSystem();
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + SystemProfileConstants.PROFILE_NAME);
        builder.append("/" + SystemProfileConstants.INTERFACE_DEVICE);
        builder.append("/" + SystemProfileConstants.ATTRIBUTE_WAKEUP);
        builder.append("?");
        builder.append(SystemProfileConstants.PARAM_PLUGIN_ID + "=" + testPluginID);
        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpUriRequest request = new HttpPut(builder.toString());
            JSONObject resp = sendRequest(request);
            assertResultOK(resp);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 指定したセッションキーに対応するイベントを全て解除する.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /system/events?sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     * @throws JSONException JSON解析に失敗した場合
     */
    public void testDeleteSystemEvents() throws JSONException {
        // イベント登録
        {
            URIBuilder builder = TestURIBuilder.createURIBuilder();
            builder.setProfile("unique");
            builder.setAttribute("event");
            builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
            builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
            builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
            try {
                HttpUriRequest request = new HttpPut(builder.toString());
                JSONObject root = sendRequest(request);
                assertResultOK(root);
                JSONObject resp = waitForEvent();
                assertNotNull("response is null.", resp);
                assertEquals("unique", resp.getString(DConnectMessage.EXTRA_PROFILE));
                assertEquals("event", resp.getString(DConnectMessage.EXTRA_ATTRIBUTE));
            } catch (JSONException e) {
                fail("Exception in JSONObject." + e.getMessage());
            }
        }

        // イベント全削除
        {
            long removedTime = System.currentTimeMillis();
            
            URIBuilder builder = TestURIBuilder.createURIBuilder();
            builder.setProfile(SystemProfileConstants.PROFILE_NAME);
            builder.setAttribute(SystemProfileConstants.ATTRIBUTE_EVENTS);
            builder.addParameter(DConnectProfileConstants.PARAM_SESSION_KEY, getClientId());
            builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
            try {
                HttpUriRequest request = new HttpDelete(builder.toString());
                JSONObject resp = sendRequest(request);
                assertResultOK(resp);
            } catch (JSONException e) {
                fail("Exception in JSONObject." + e.getMessage());
            }
            
            // イベント解除確認
            JSONObject event = waitForEvent(750);
            if (event != null) {
                long publishedTime = event.getLong("time");
                assertTrue(removedTime >= publishedTime);
            }
        }
    }
}
