/*
 NormalNotificationProfileTestCase.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.profile.restful.test;

import java.io.IOException;

import junit.framework.Assert;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.AssetManager;

import com.nttdocomo.android.dconnect.test.plugin.profile.TestNotificationProfileConstants;
import com.nttdocomo.dconnect.message.DConnectMessage;
import com.nttdocomo.dconnect.profile.AuthorizationProfileConstants;
import com.nttdocomo.dconnect.profile.DConnectProfileConstants;
import com.nttdocomo.dconnect.profile.NotificationProfileConstants;

/**
 * Notificationプロファイルの正常系テスト.
 * 
 * @author NTT DOCOMO, INC.
 */
public class NormalNotificationProfileTestCase extends RESTfulDConnectTestCase {
    /**
     * コンストラクタ.
     * 
     * @param string テストタグ
     */
    public NormalNotificationProfileTestCase(final String string) {
        super(string);
    }

    /**
     * typeを0(音声通話着信)を指定して通知を送信するテストを行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /notification/notify?deviceid=xxxx&type=0&body=xxxx
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに1が返ってくること。
     * </pre>
     */
    public void testPostNotifyType001() {
        final int type = 0;
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + NotificationProfileConstants.PROFILE_NAME);
        builder.append("/" + NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TYPE + "=" + type);
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_BODY + "=test-message");
        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpUriRequest request = new HttpPost(builder.toString());
            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
            Assert.assertEquals("notificationid is not equals.", TestNotificationProfileConstants.NOTIFICATION_ID[type],
                    root.getString(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * typeを1(メール着信)を指定して通知を送信するテストを行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /notification/notify?deviceid=xxxx&type=1&body=xxxx
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに2が返ってくること。
     * </pre>
     */
    public void testPostNotifyType002() {
        final int type = 1;
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + NotificationProfileConstants.PROFILE_NAME);
        builder.append("/" + NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TYPE + "=" + type);
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_BODY + "=test-message");
        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpUriRequest request = new HttpPost(builder.toString());
            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
            Assert.assertEquals("notificationid is not equals.", TestNotificationProfileConstants.NOTIFICATION_ID[type],
                    root.getString(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * typeを2(SMS着信)を指定して通知を送信するテストを行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /notification/notify?deviceid=xxxx&type=2&body=xxxx
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに3が返ってくること。
     * </pre>
     */
    public void testPostNotifyType003() {
        final int type = 2;
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + NotificationProfileConstants.PROFILE_NAME);
        builder.append("/" + NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TYPE + "=" + type);
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_BODY + "=test-message");
        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpUriRequest request = new HttpPost(builder.toString());
            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
            Assert.assertEquals("notificationid is not equals.", TestNotificationProfileConstants.NOTIFICATION_ID[type],
                    root.getString(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * typeを3(イベント)を指定して通知を送信するテストを行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /notification/notify?deviceid=xxxx&type=3&body=xxxx
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに4が返ってくること。
     * </pre>
     */
    public void testPostNotifyType004() {
        final int type = 3;
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + NotificationProfileConstants.PROFILE_NAME);
        builder.append("/" + NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TYPE + "=" + type);
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_BODY + "=test-message");
        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpUriRequest request = new HttpPost(builder.toString());
            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
            Assert.assertEquals("notificationid is not equals.", TestNotificationProfileConstants.NOTIFICATION_ID[type],
                    root.getString(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * dirにautoを指定して通知を送信するテストを行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /notification/notify?deviceid=xxxx&type=0&dir=auto&body=xxxx
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに1が返ってくること。
     * </pre>
     */
    public void testPostNotifyDir001() {
        final int type = 0;
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + NotificationProfileConstants.PROFILE_NAME);
        builder.append("/" + NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TYPE + "=" + type);
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_DIR + "=auto");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_BODY + "=test-message");
        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpUriRequest request = new HttpPost(builder.toString());
            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
            Assert.assertEquals("notificationid is not equals.", TestNotificationProfileConstants.NOTIFICATION_ID[type],
                    root.getString(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * dirにrtlを指定して通知を送信するテストを行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /notification/notify?deviceid=xxxx&type=0&dir=rtl&body=xxxx
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに1が返ってくること。
     * </pre>
     */
    public void testPostNotifyDir002() {
        final int type = 0;
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + NotificationProfileConstants.PROFILE_NAME);
        builder.append("/" + NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TYPE + "=" + type);
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_DIR + "=rtl");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_BODY + "=test-message");
        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpUriRequest request = new HttpPost(builder.toString());
            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
            Assert.assertEquals("notificationid is not equals.", TestNotificationProfileConstants.NOTIFICATION_ID[type],
                    root.getString(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * dirにltrを指定して通知を送信するテストを行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /notification/notify?deviceid=xxxx&type=0&dir=ltr&body=xxxx
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに1が返ってくること。
     * </pre>
     */
    public void testPostNotifyDir003() {
        final int type = 0;
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + NotificationProfileConstants.PROFILE_NAME);
        builder.append("/" + NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TYPE + "=" + type);
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_DIR + "=ltr");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_BODY + "=test-message");
        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpUriRequest request = new HttpPost(builder.toString());
            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
            Assert.assertEquals("notificationid is not equals.", TestNotificationProfileConstants.NOTIFICATION_ID[type],
                    root.getString(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: notification/notify?deviceId=xxxx&type=0&dir=auto
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに1が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional001() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + NotificationProfileConstants.PROFILE_NAME);
        builder.append("/" + NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TYPE + "=0");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_DIR + "=auto");

        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpPost request = new HttpPost(builder.toString());

            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
            Assert.assertEquals("notificationid is not equals.", TestNotificationProfileConstants.NOTIFICATION_ID[0],
                    root.getString(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: notification/notify?deviceId=xxxx&type=0&lang=jp-JP
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに1が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional002() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + NotificationProfileConstants.PROFILE_NAME);
        builder.append("/" + NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TYPE + "=0");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_LANG + "=jp-JP");

        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpPost request = new HttpPost(builder.toString());

            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
            Assert.assertEquals("notificationid is not equals.", TestNotificationProfileConstants.NOTIFICATION_ID[0],
                    root.getString(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: notification/notify?deviceId=xxxx&type=0&dir=auto&lang=jp-JP
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに1が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional003() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + NotificationProfileConstants.PROFILE_NAME);
        builder.append("/" + NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TYPE + "=0");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_DIR + "=auto");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_LANG + "=jp-JP");

        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpPost request = new HttpPost(builder.toString());

            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
            Assert.assertEquals("notificationid is not equals.", TestNotificationProfileConstants.NOTIFICATION_ID[0],
                    root.getString(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: notification/notify?deviceId=xxxx&type=0&body=test_body
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに1が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional004() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + NotificationProfileConstants.PROFILE_NAME);
        builder.append("/" + NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TYPE + "=0");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_BODY + "=test_body");

        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpPost request = new HttpPost(builder.toString());

            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
            Assert.assertEquals("notificationid is not equals.", TestNotificationProfileConstants.NOTIFICATION_ID[0],
                    root.getString(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: notification/notify?deviceId=xxxx&type=0&dir=auto&body=test_body
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに1が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional005() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + NotificationProfileConstants.PROFILE_NAME);
        builder.append("/" + NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TYPE + "=0");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_DIR + "=auto");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_BODY + "=test_body");

        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpPost request = new HttpPost(builder.toString());

            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
            Assert.assertEquals("notificationid is not equals.", TestNotificationProfileConstants.NOTIFICATION_ID[0],
                    root.getString(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: notification/notify?deviceId=xxxx&type=0&lang=jp-JP&body=test_body
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに1が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional006() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + NotificationProfileConstants.PROFILE_NAME);
        builder.append("/" + NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TYPE + "=0");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_LANG + "=jp-JP");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_BODY + "=test_body");

        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpPost request = new HttpPost(builder.toString());

            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
            Assert.assertEquals("notificationid is not equals.", TestNotificationProfileConstants.NOTIFICATION_ID[0],
                    root.getString(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: notification/notify?deviceId=xxxx&type=0&dir=auto&lang=jp-JP&body=test_body
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに1が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional007() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + NotificationProfileConstants.PROFILE_NAME);
        builder.append("/" + NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TYPE + "=0");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_DIR + "=auto");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_LANG + "=jp-JP");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_BODY + "=test_body");

        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpPost request = new HttpPost(builder.toString());

            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
            Assert.assertEquals("notificationid is not equals.", TestNotificationProfileConstants.NOTIFICATION_ID[0],
                    root.getString(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: notification/notify?deviceId=xxxx&type=0&tag=tag1,tag2,tag3
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに1が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional008() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + NotificationProfileConstants.PROFILE_NAME);
        builder.append("/" + NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TYPE + "=0");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TAG + "=tag1,tag2,tag3");

        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpPost request = new HttpPost(builder.toString());

            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
            Assert.assertEquals("notificationid is not equals.", TestNotificationProfileConstants.NOTIFICATION_ID[0],
                    root.getString(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: notification/notify?deviceId=xxxx&type=0&dir=auto&tag=tag1,tag2,tag3
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに1が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional009() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + NotificationProfileConstants.PROFILE_NAME);
        builder.append("/" + NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TYPE + "=0");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_DIR + "=auto");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TAG + "=tag1,tag2,tag3");

        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpPost request = new HttpPost(builder.toString());

            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
            Assert.assertEquals("notificationid is not equals.", TestNotificationProfileConstants.NOTIFICATION_ID[0],
                    root.getString(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: notification/notify?deviceId=xxxx&type=0&lang=jp-JP&tag=tag1,tag2,tag3
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに1が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional010() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + NotificationProfileConstants.PROFILE_NAME);
        builder.append("/" + NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TYPE + "=0");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_LANG + "=jp-JP");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TAG + "=tag1,tag2,tag3");

        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpPost request = new HttpPost(builder.toString());

            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
            Assert.assertEquals("notificationid is not equals.", TestNotificationProfileConstants.NOTIFICATION_ID[0],
                    root.getString(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: notification/notify?deviceId=xxxx&type=0&dir=auto&lang=jp-JP&tag=tag1,tag2,tag3
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに1が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional011() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + NotificationProfileConstants.PROFILE_NAME);
        builder.append("/" + NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TYPE + "=0");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_DIR + "=auto");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_LANG + "=jp-JP");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TAG + "=tag1,tag2,tag3");

        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpPost request = new HttpPost(builder.toString());

            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
            Assert.assertEquals("notificationid is not equals.", TestNotificationProfileConstants.NOTIFICATION_ID[0],
                    root.getString(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: notification/notify?deviceId=xxxx&type=0&body=test_body&tag=tag1,tag2,tag3
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに1が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional012() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + NotificationProfileConstants.PROFILE_NAME);
        builder.append("/" + NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TYPE + "=0");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_BODY + "=test_body");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TAG + "=tag1,tag2,tag3");

        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpPost request = new HttpPost(builder.toString());

            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
            Assert.assertEquals("notificationid is not equals.", TestNotificationProfileConstants.NOTIFICATION_ID[0],
                    root.getString(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: notification/notify?deviceId=xxxx&type=0&dir=auto&body=test_body&tag=tag1,tag2,tag3
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに1が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional013() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + NotificationProfileConstants.PROFILE_NAME);
        builder.append("/" + NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TYPE + "=0");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_DIR + "=auto");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_BODY + "=test_body");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TAG + "=tag1,tag2,tag3");

        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpPost request = new HttpPost(builder.toString());

            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
            Assert.assertEquals("notificationid is not equals.", TestNotificationProfileConstants.NOTIFICATION_ID[0],
                    root.getString(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: notification/notify?deviceId=xxxx&type=0&lang=jp-JP&body=test_body&tag=tag1,tag2,tag3
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに1が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional014() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + NotificationProfileConstants.PROFILE_NAME);
        builder.append("/" + NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TYPE + "=0");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_LANG + "=jp-JP");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_BODY + "=test_body");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TAG + "=tag1,tag2,tag3");

        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpPost request = new HttpPost(builder.toString());

            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
            Assert.assertEquals("notificationid is not equals.", TestNotificationProfileConstants.NOTIFICATION_ID[0],
                    root.getString(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: notification/notify?deviceId=xxxx&type=0&dir=auto&lang=jp-JP&body=test_body&tag=tag1,tag2,tag3
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに1が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional015() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + NotificationProfileConstants.PROFILE_NAME);
        builder.append("/" + NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TYPE + "=0");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_DIR + "=auto");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_LANG + "=jp-JP");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_BODY + "=test_body");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TAG + "=tag1,tag2,tag3");

        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpPost request = new HttpPost(builder.toString());

            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
            Assert.assertEquals("notificationid is not equals.", TestNotificationProfileConstants.NOTIFICATION_ID[0],
                    root.getString(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: notification/notify?deviceId=xxxx&type=0
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに1が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional016() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + NotificationProfileConstants.PROFILE_NAME);
        builder.append("/" + NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TYPE + "=0");

        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpPost request = new HttpPost(builder.toString());
            String name = "test.png";
            AssetManager manager = getApplicationContext().getAssets();
            MultipartEntity entity = new MultipartEntity();
            entity.addPart(NotificationProfileConstants.PARAM_ICON, new InputStreamBody(manager.open(name), name));
            request.setEntity(entity);

            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
            Assert.assertEquals("notificationid is not equals.", TestNotificationProfileConstants.NOTIFICATION_ID[0],
                    root.getString(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        } catch (IOException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: notification/notify?deviceId=xxxx&type=0&dir=auto
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに1が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional017() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + NotificationProfileConstants.PROFILE_NAME);
        builder.append("/" + NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TYPE + "=0");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_DIR + "=auto");

        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpPost request = new HttpPost(builder.toString());
            String name = "test.png";
            AssetManager manager = getApplicationContext().getAssets();
            MultipartEntity entity = new MultipartEntity();
            entity.addPart(NotificationProfileConstants.PARAM_ICON, new InputStreamBody(manager.open(name), name));
            request.setEntity(entity);

            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
            Assert.assertEquals("notificationid is not equals.", TestNotificationProfileConstants.NOTIFICATION_ID[0],
                    root.getString(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        } catch (IOException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: notification/notify?deviceId=xxxx&type=0&lang=jp-JP
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに1が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional018() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + NotificationProfileConstants.PROFILE_NAME);
        builder.append("/" + NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TYPE + "=0");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_LANG + "=jp-JP");

        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpPost request = new HttpPost(builder.toString());
            String name = "test.png";
            AssetManager manager = getApplicationContext().getAssets();
            MultipartEntity entity = new MultipartEntity();
            entity.addPart(NotificationProfileConstants.PARAM_ICON, new InputStreamBody(manager.open(name), name));
            request.setEntity(entity);

            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
            Assert.assertEquals("notificationid is not equals.", TestNotificationProfileConstants.NOTIFICATION_ID[0],
                    root.getString(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        } catch (IOException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: notification/notify?deviceId=xxxx&type=0&dir=auto&lang=jp-JP
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに1が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional019() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + NotificationProfileConstants.PROFILE_NAME);
        builder.append("/" + NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TYPE + "=0");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_DIR + "=auto");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_LANG + "=jp-JP");

        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpPost request = new HttpPost(builder.toString());
            String name = "test.png";
            AssetManager manager = getApplicationContext().getAssets();
            MultipartEntity entity = new MultipartEntity();
            entity.addPart(NotificationProfileConstants.PARAM_ICON, new InputStreamBody(manager.open(name), name));
            request.setEntity(entity);

            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
            Assert.assertEquals("notificationid is not equals.", TestNotificationProfileConstants.NOTIFICATION_ID[0],
                    root.getString(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        } catch (IOException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: notification/notify?deviceId=xxxx&type=0&body=test_body
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに1が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional020() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + NotificationProfileConstants.PROFILE_NAME);
        builder.append("/" + NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TYPE + "=0");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_BODY + "=test_body");

        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpPost request = new HttpPost(builder.toString());
            String name = "test.png";
            AssetManager manager = getApplicationContext().getAssets();
            MultipartEntity entity = new MultipartEntity();
            entity.addPart(NotificationProfileConstants.PARAM_ICON, new InputStreamBody(manager.open(name), name));
            request.setEntity(entity);

            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
            Assert.assertEquals("notificationid is not equals.", TestNotificationProfileConstants.NOTIFICATION_ID[0],
                    root.getString(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        } catch (IOException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: notification/notify?deviceId=xxxx&type=0&dir=auto&body=test_body
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに1が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional021() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + NotificationProfileConstants.PROFILE_NAME);
        builder.append("/" + NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TYPE + "=0");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_DIR + "=auto");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_BODY + "=test_body");

        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpPost request = new HttpPost(builder.toString());
            String name = "test.png";
            AssetManager manager = getApplicationContext().getAssets();
            MultipartEntity entity = new MultipartEntity();
            entity.addPart(NotificationProfileConstants.PARAM_ICON, new InputStreamBody(manager.open(name), name));
            request.setEntity(entity);

            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
            Assert.assertEquals("notificationid is not equals.", TestNotificationProfileConstants.NOTIFICATION_ID[0],
                    root.getString(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        } catch (IOException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: notification/notify?deviceId=xxxx&type=0&lang=jp-JP&body=test_body
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに1が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional022() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + NotificationProfileConstants.PROFILE_NAME);
        builder.append("/" + NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TYPE + "=0");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_LANG + "=jp-JP");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_BODY + "=test_body");

        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpPost request = new HttpPost(builder.toString());
            String name = "test.png";
            AssetManager manager = getApplicationContext().getAssets();
            MultipartEntity entity = new MultipartEntity();
            entity.addPart(NotificationProfileConstants.PARAM_ICON, new InputStreamBody(manager.open(name), name));
            request.setEntity(entity);

            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
            Assert.assertEquals("notificationid is not equals.", TestNotificationProfileConstants.NOTIFICATION_ID[0],
                    root.getString(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        } catch (IOException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: notification/notify?deviceId=xxxx&type=0&dir=auto&lang=jp-JP&body=test_body
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに1が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional023() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + NotificationProfileConstants.PROFILE_NAME);
        builder.append("/" + NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TYPE + "=0");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_DIR + "=auto");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_LANG + "=jp-JP");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_BODY + "=test_body");

        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpPost request = new HttpPost(builder.toString());
            String name = "test.png";
            AssetManager manager = getApplicationContext().getAssets();
            MultipartEntity entity = new MultipartEntity();
            entity.addPart(NotificationProfileConstants.PARAM_ICON, new InputStreamBody(manager.open(name), name));
            request.setEntity(entity);

            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
            Assert.assertEquals("notificationid is not equals.", TestNotificationProfileConstants.NOTIFICATION_ID[0],
                    root.getString(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        } catch (IOException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: notification/notify?deviceId=xxxx&type=0&tag=tag1,tag2,tag3
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに1が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional024() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + NotificationProfileConstants.PROFILE_NAME);
        builder.append("/" + NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TYPE + "=0");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TAG + "=tag1,tag2,tag3");

        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpPost request = new HttpPost(builder.toString());
            String name = "test.png";
            AssetManager manager = getApplicationContext().getAssets();
            MultipartEntity entity = new MultipartEntity();
            entity.addPart(NotificationProfileConstants.PARAM_ICON, new InputStreamBody(manager.open(name), name));
            request.setEntity(entity);

            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
            Assert.assertEquals("notificationid is not equals.", TestNotificationProfileConstants.NOTIFICATION_ID[0],
                    root.getString(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        } catch (IOException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: notification/notify?deviceId=xxxx&type=0&dir=auto&tag=tag1,tag2,tag3
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに1が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional025() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + NotificationProfileConstants.PROFILE_NAME);
        builder.append("/" + NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TYPE + "=0");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_DIR + "=auto");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TAG + "=tag1,tag2,tag3");

        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpPost request = new HttpPost(builder.toString());
            String name = "test.png";
            AssetManager manager = getApplicationContext().getAssets();
            MultipartEntity entity = new MultipartEntity();
            entity.addPart(NotificationProfileConstants.PARAM_ICON, new InputStreamBody(manager.open(name), name));
            request.setEntity(entity);

            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
            Assert.assertEquals("notificationid is not equals.", TestNotificationProfileConstants.NOTIFICATION_ID[0],
                    root.getString(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        } catch (IOException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: notification/notify?deviceId=xxxx&type=0&lang=jp-JP&tag=tag1,tag2,tag3
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに1が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional026() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + NotificationProfileConstants.PROFILE_NAME);
        builder.append("/" + NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TYPE + "=0");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_LANG + "=jp-JP");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TAG + "=tag1,tag2,tag3");

        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpPost request = new HttpPost(builder.toString());
            String name = "test.png";
            AssetManager manager = getApplicationContext().getAssets();
            MultipartEntity entity = new MultipartEntity();
            entity.addPart(NotificationProfileConstants.PARAM_ICON, new InputStreamBody(manager.open(name), name));
            request.setEntity(entity);

            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
            Assert.assertEquals("notificationid is not equals.", TestNotificationProfileConstants.NOTIFICATION_ID[0],
                    root.getString(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        } catch (IOException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: notification/notify?deviceId=xxxx&type=0&dir=auto&lang=jp-JP&tag=tag1,tag2,tag3
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに1が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional027() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + NotificationProfileConstants.PROFILE_NAME);
        builder.append("/" + NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TYPE + "=0");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_DIR + "=auto");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_LANG + "=jp-JP");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TAG + "=tag1,tag2,tag3");

        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpPost request = new HttpPost(builder.toString());
            String name = "test.png";
            AssetManager manager = getApplicationContext().getAssets();
            MultipartEntity entity = new MultipartEntity();
            entity.addPart(NotificationProfileConstants.PARAM_ICON, new InputStreamBody(manager.open(name), name));
            request.setEntity(entity);

            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
            Assert.assertEquals("notificationid is not equals.", TestNotificationProfileConstants.NOTIFICATION_ID[0],
                    root.getString(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        } catch (IOException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: notification/notify?deviceId=xxxx&type=0&body=test_body&tag=tag1,tag2,tag3
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに1が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional028() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + NotificationProfileConstants.PROFILE_NAME);
        builder.append("/" + NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TYPE + "=0");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_BODY + "=test_body");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TAG + "=tag1,tag2,tag3");

        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpPost request = new HttpPost(builder.toString());
            String name = "test.png";
            AssetManager manager = getApplicationContext().getAssets();
            MultipartEntity entity = new MultipartEntity();
            entity.addPart(NotificationProfileConstants.PARAM_ICON, new InputStreamBody(manager.open(name), name));
            request.setEntity(entity);

            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
            Assert.assertEquals("notificationid is not equals.", TestNotificationProfileConstants.NOTIFICATION_ID[0],
                    root.getString(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        } catch (IOException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: notification/notify?deviceId=xxxx&type=0&dir=auto&body=test_body&tag=tag1,tag2,tag3
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに1が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional029() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + NotificationProfileConstants.PROFILE_NAME);
        builder.append("/" + NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TYPE + "=0");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_DIR + "=auto");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_BODY + "=test_body");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TAG + "=tag1,tag2,tag3");

        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpPost request = new HttpPost(builder.toString());
            String name = "test.png";
            AssetManager manager = getApplicationContext().getAssets();
            MultipartEntity entity = new MultipartEntity();
            entity.addPart(NotificationProfileConstants.PARAM_ICON, new InputStreamBody(manager.open(name), name));
            request.setEntity(entity);

            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
            Assert.assertEquals("notificationid is not equals.", TestNotificationProfileConstants.NOTIFICATION_ID[0],
                    root.getString(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        } catch (IOException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: notification/notify?deviceId=xxxx&type=0&lang=jp-JP&body=test_body&tag=tag1,tag2,tag3
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに1が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional030() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + NotificationProfileConstants.PROFILE_NAME);
        builder.append("/" + NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TYPE + "=0");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_LANG + "=jp-JP");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_BODY + "=test_body");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TAG + "=tag1,tag2,tag3");

        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpPost request = new HttpPost(builder.toString());
            String name = "test.png";
            AssetManager manager = getApplicationContext().getAssets();
            MultipartEntity entity = new MultipartEntity();
            entity.addPart(NotificationProfileConstants.PARAM_ICON, new InputStreamBody(manager.open(name), name));
            request.setEntity(entity);

            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
            Assert.assertEquals("notificationid is not equals.", TestNotificationProfileConstants.NOTIFICATION_ID[0],
                    root.getString(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        } catch (IOException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: notification/notify?deviceId=xxxx&type=0&dir=auto&lang=jp-JP&body=test_body&tag=tag1,tag2,tag3
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに1が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional031() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + NotificationProfileConstants.PROFILE_NAME);
        builder.append("/" + NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TYPE + "=0");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_DIR + "=auto");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_LANG + "=jp-JP");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_BODY + "=test_body");
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_TAG + "=tag1,tag2,tag3");

        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpPost request = new HttpPost(builder.toString());
            String name = "test.png";
            AssetManager manager = getApplicationContext().getAssets();
            MultipartEntity entity = new MultipartEntity();
            entity.addPart(NotificationProfileConstants.PARAM_ICON, new InputStreamBody(manager.open(name), name));
            request.setEntity(entity);

            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
            Assert.assertEquals("notificationid is not equals.", TestNotificationProfileConstants.NOTIFICATION_ID[0],
                    root.getString(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        } catch (IOException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 通知の消去要求を送信するテストを行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /notification/notify?deviceid=xxxx&notificationId=xxxx
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testDeleteNotify() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + NotificationProfileConstants.PROFILE_NAME);
        builder.append("/" + NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(NotificationProfileConstants.PARAM_NOTIFICATION_ID + "="
                + TestNotificationProfileConstants.NOTIFICATION_ID[0]);
        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpUriRequest request = new HttpDelete(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultOK(root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 通知クリックイベントのコールバック登録テストを行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /notification/onclick?deviceId=xxxx&sessionKey=xxxx
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・コールバック登録後にイベントを受信できること。
     * </pre>
     */
    public void testPutOnClick() {
        try {
            JSONObject event = registerEventCallback(NotificationProfileConstants.ATTRIBUTE_ON_CLICK);
            Assert.assertEquals(TestNotificationProfileConstants.NOTIFICATION_ID[0],
                    event.getString(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 通知クリックイベントのコールバック解除テストを行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /notification/onshow?deviceId=xxxx&sessionKey=xxxx
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testDeleteOnClick() {
        unregisterEventCallback(NotificationProfileConstants.ATTRIBUTE_ON_CLICK);
    }

    /**
     * 通知表示イベントのコールバック登録テストを行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /notification/onshow?deviceId=xxxx&sessionKey=xxxx
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・コールバック登録後にイベントを受信できること。
     * </pre>
     */
    public void testPutOnShow() {
        try {
            JSONObject event = registerEventCallback(NotificationProfileConstants.ATTRIBUTE_ON_SHOW);
            Assert.assertEquals(TestNotificationProfileConstants.NOTIFICATION_ID[0],
                    event.getString(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 通知表示イベントのコールバック解除テストを行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /notification/onshow?deviceId=xxxx&sessionKey=xxxx
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testDeleteOnShow() {
        unregisterEventCallback(NotificationProfileConstants.ATTRIBUTE_ON_SHOW);
    }

    /**
     * 通知消去イベントのコールバック登録テストを行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /notification/onclose?deviceId=xxxx&sessionKey=xxxx
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・コールバック登録後にイベントを受信できること。
     * </pre>
     */
    public void testPutOnClose() {
        try {
            JSONObject event = registerEventCallback(NotificationProfileConstants.ATTRIBUTE_ON_CLOSE);
            Assert.assertEquals(TestNotificationProfileConstants.NOTIFICATION_ID[0],
                    event.getString(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 通知消去イベントのコールバック解除テストを行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /notification/onclose?deviceId=xxxx&sessionKey=xxxx
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testDeleteOnClose() {
        unregisterEventCallback(NotificationProfileConstants.ATTRIBUTE_ON_CLOSE);
    }

    /**
     * 通知操作エラー発生イベントのコールバック登録テストを行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /notification/onerror?deviceId=xxxx&sessionKey=xxxx
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・コールバック登録後にイベントを受信できること。
     * </pre>
     */
    public void testPutOnError() {
        try {
            JSONObject event = registerEventCallback(NotificationProfileConstants.ATTRIBUTE_ON_ERROR);
            Assert.assertEquals(TestNotificationProfileConstants.NOTIFICATION_ID[0],
                    event.getString(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 通知操作エラー発生イベントのコールバック解除テストを行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /notification/onerror?deviceId=xxxx&sessionKey=xxxx
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testDeleteOnError() {
        unregisterEventCallback(NotificationProfileConstants.ATTRIBUTE_ON_ERROR);
    }

    /**
     * コールバック登録リクエストを送信する.
     * 
     * @param attribute コールバックの属性名
     * @return 受信したイベント
     * @throws JSONException JSONの解析に失敗した場合
     */
    private JSONObject registerEventCallback(final String attribute) throws JSONException {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + NotificationProfileConstants.PROFILE_NAME);
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
        Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
        JSONObject event = waitForEvent();
        Assert.assertNotNull("event is null.", event);
        return event;
    }

    /**
     * コールバック解除リクエストを送信する.
     * 
     * @param attribute コールバックの属性名
     */
    private void unregisterEventCallback(final String attribute) {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + NotificationProfileConstants.PROFILE_NAME);
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
            Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

}
