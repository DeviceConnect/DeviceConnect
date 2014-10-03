/*
 NormalNotificationProfileTestCase.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.profile.intent.test;

import android.content.Intent;

import com.nttdocomo.android.dconnect.test.plugin.profile.TestNotificationProfileConstants;
import com.nttdocomo.dconnect.message.DConnectMessage;
import com.nttdocomo.dconnect.message.intent.message.IntentDConnectMessage;
import com.nttdocomo.dconnect.profile.NotificationProfileConstants;

/**
 * Notificationプロファイルの正常系テスト.
 * 
 * @author NTT DOCOMO, INC.
 */
public class NormalNotificationProfileTestCase extends IntentDConnectTestCase {
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
     * 【Intent通信】
     * Action: POST
     * Extra: 
     *     profile=notification
     *     attribute=notify
     *     type=0
     *     body=xxxx
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
        Intent request = new Intent(IntentDConnectMessage.ACTION_POST);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, NotificationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        request.putExtra(NotificationProfileConstants.PARAM_TYPE, type);
        request.putExtra(NotificationProfileConstants.PARAM_BODY, "test_body");
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
        assertEquals(TestNotificationProfileConstants.NOTIFICATION_ID[type],
                response.getStringExtra(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
    }

    /**
     * typeを1(メール着信)を指定して通知を送信するテストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: POST
     * Extra: 
     *     profile=notification
     *     attribute=notify
     *     type=1
     *     body=xxxx
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
        Intent request = new Intent(IntentDConnectMessage.ACTION_POST);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, NotificationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        request.putExtra(NotificationProfileConstants.PARAM_TYPE, type);
        request.putExtra(NotificationProfileConstants.PARAM_BODY, "test_body");
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
        assertEquals(TestNotificationProfileConstants.NOTIFICATION_ID[type],
                response.getStringExtra(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
    }

    /**
     * typeを2(SMS着信)を指定して通知を送信するテストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: POST
     * Extra: 
     *     profile=notification
     *     attribute=notify
     *     type=2
     *     body=xxxx
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
        Intent request = new Intent(IntentDConnectMessage.ACTION_POST);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, NotificationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        request.putExtra(NotificationProfileConstants.PARAM_TYPE, type);
        request.putExtra(NotificationProfileConstants.PARAM_BODY, "test_body");
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
        assertEquals(TestNotificationProfileConstants.NOTIFICATION_ID[type],
                response.getStringExtra(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
    }

    /**
     * typeを3(イベント)を指定して通知を送信するテストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: POST
     * Extra: 
     *     profile=notification
     *     attribute=notify
     *     type=3
     *     body=xxxx
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
        Intent request = new Intent(IntentDConnectMessage.ACTION_POST);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, NotificationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        request.putExtra(NotificationProfileConstants.PARAM_TYPE, type);
        request.putExtra(NotificationProfileConstants.PARAM_BODY, "test_body");
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
        assertEquals(TestNotificationProfileConstants.NOTIFICATION_ID[type],
                response.getStringExtra(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
    }

    /**
     * dirにautoを指定して通知を送信するテストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: POST
     * Extra: 
     *     profile=notification
     *     attribute=notify
     *     type=0
     *     dir=auto
     *     body=xxxx
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
        Intent request = new Intent(IntentDConnectMessage.ACTION_POST);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, NotificationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        request.putExtra(NotificationProfileConstants.PARAM_TYPE, type);
        request.putExtra(NotificationProfileConstants.PARAM_DIR, "auto");
        request.putExtra(NotificationProfileConstants.PARAM_BODY, "test_body");
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
        assertEquals(TestNotificationProfileConstants.NOTIFICATION_ID[type],
                response.getStringExtra(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
    }

    /**
     * dirにrtlを指定して通知を送信するテストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: POST
     * Extra: 
     *     profile=notification
     *     attribute=notify
     *     type=0
     *     dir=rtl
     *     body=xxxx
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
        Intent request = new Intent(IntentDConnectMessage.ACTION_POST);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, NotificationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        request.putExtra(NotificationProfileConstants.PARAM_TYPE, type);
        request.putExtra(NotificationProfileConstants.PARAM_DIR, "rtl");
        request.putExtra(NotificationProfileConstants.PARAM_BODY, "test_body");
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
        assertEquals(TestNotificationProfileConstants.NOTIFICATION_ID[type],
                response.getStringExtra(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
    }

    /**
     * dirにltrを指定して通知を送信するテストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: POST
     * Extra:
     *     profile=notification
     *     attribute=notify
     *     type=0
     *     dir=ltr
     *     body=xxxx
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
        Intent request = new Intent(IntentDConnectMessage.ACTION_POST);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, NotificationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        request.putExtra(NotificationProfileConstants.PARAM_TYPE, type);
        request.putExtra(NotificationProfileConstants.PARAM_DIR, "ltr");
        request.putExtra(NotificationProfileConstants.PARAM_BODY, "test_body");
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
        assertEquals(TestNotificationProfileConstants.NOTIFICATION_ID[type],
                response.getStringExtra(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: POST
     * Extra: 
     *     profile=notification
     *     attribute=notify
     *     type=0
     *     dir=auto
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに"1"が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional001() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_POST);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, NotificationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        request.putExtra(NotificationProfileConstants.PARAM_TYPE, 0);
        request.putExtra(NotificationProfileConstants.PARAM_DIR, "auto");
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
        assertEquals(TestNotificationProfileConstants.NOTIFICATION_ID[0],
                response.getStringExtra(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: POST
     * Extra: 
     *     profile=notification
     *     attribute=notify
     *     type=0
     *     lang=jp-JP
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに"1"が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional002() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_POST);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, NotificationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        request.putExtra(NotificationProfileConstants.PARAM_TYPE, 0);
        request.putExtra(NotificationProfileConstants.PARAM_LANG, "jp-JP");
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
        assertEquals(TestNotificationProfileConstants.NOTIFICATION_ID[0],
                response.getStringExtra(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: POST
     * Extra: 
     *     profile=notification
     *     attribute=notify
     *     type=0
     *     dir=auto
     *     lang=jp-JP
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに"1"が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional003() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_POST);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, NotificationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        request.putExtra(NotificationProfileConstants.PARAM_TYPE, 0);
        request.putExtra(NotificationProfileConstants.PARAM_DIR, "auto");
        request.putExtra(NotificationProfileConstants.PARAM_LANG, "jp-JP");
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
        assertEquals(TestNotificationProfileConstants.NOTIFICATION_ID[0],
                response.getStringExtra(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: POST
     * Extra: 
     *     profile=notification
     *     attribute=notify
     *     type=0
     *     body=test_body
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに"1"が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional004() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_POST);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, NotificationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        request.putExtra(NotificationProfileConstants.PARAM_TYPE, 0);
        request.putExtra(NotificationProfileConstants.PARAM_BODY, "test_body");
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
        assertEquals(TestNotificationProfileConstants.NOTIFICATION_ID[0],
                response.getStringExtra(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: POST
     * Extra: 
     *     profile=notification
     *     attribute=notify
     *     type=0
     *     dir=auto
     *     body=test_body
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに"1"が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional005() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_POST);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, NotificationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        request.putExtra(NotificationProfileConstants.PARAM_TYPE, 0);
        request.putExtra(NotificationProfileConstants.PARAM_DIR, "auto");
        request.putExtra(NotificationProfileConstants.PARAM_BODY, "test_body");
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
        assertEquals(TestNotificationProfileConstants.NOTIFICATION_ID[0],
                response.getStringExtra(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: POST
     * Extra: 
     *     profile=notification
     *     attribute=notify
     *     type=0
     *     lang=jp-JP
     *     body=test_body
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに"1"が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional006() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_POST);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, NotificationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        request.putExtra(NotificationProfileConstants.PARAM_TYPE, 0);
        request.putExtra(NotificationProfileConstants.PARAM_LANG, "jp-JP");
        request.putExtra(NotificationProfileConstants.PARAM_BODY, "test_body");
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
        assertEquals(TestNotificationProfileConstants.NOTIFICATION_ID[0],
                response.getStringExtra(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: POST
     * Extra: 
     *     profile=notification
     *     attribute=notify
     *     type=0
     *     dir=auto
     *     lang=jp-JP
     *     body=test_body
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに"1"が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional007() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_POST);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, NotificationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        request.putExtra(NotificationProfileConstants.PARAM_TYPE, 0);
        request.putExtra(NotificationProfileConstants.PARAM_DIR, "auto");
        request.putExtra(NotificationProfileConstants.PARAM_LANG, "jp-JP");
        request.putExtra(NotificationProfileConstants.PARAM_BODY, "test_body");
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
        assertEquals(TestNotificationProfileConstants.NOTIFICATION_ID[0],
                response.getStringExtra(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: POST
     * Extra: 
     *     profile=notification
     *     attribute=notify
     *     type=0
     *     tag=tag1,tag2,tag3
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに"1"が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional008() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_POST);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, NotificationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        request.putExtra(NotificationProfileConstants.PARAM_TYPE, 0);
        request.putExtra(NotificationProfileConstants.PARAM_TAG, "tag1,tag2,tag3");
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
        assertEquals(TestNotificationProfileConstants.NOTIFICATION_ID[0],
                response.getStringExtra(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: POST
     * Extra: 
     *     profile=notification
     *     attribute=notify
     *     type=0
     *     dir=auto
     *     tag=tag1,tag2,tag3
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに"1"が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional009() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_POST);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, NotificationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        request.putExtra(NotificationProfileConstants.PARAM_TYPE, 0);
        request.putExtra(NotificationProfileConstants.PARAM_DIR, "auto");
        request.putExtra(NotificationProfileConstants.PARAM_TAG, "tag1,tag2,tag3");
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
        assertEquals(TestNotificationProfileConstants.NOTIFICATION_ID[0],
                response.getStringExtra(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: POST
     * Extra: 
     *     profile=notification
     *     attribute=notify
     *     type=0
     *     lang=jp-JP
     *     tag=tag1,tag2,tag3
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに"1"が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional010() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_POST);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, NotificationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        request.putExtra(NotificationProfileConstants.PARAM_TYPE, 0);
        request.putExtra(NotificationProfileConstants.PARAM_LANG, "jp-JP");
        request.putExtra(NotificationProfileConstants.PARAM_TAG, "tag1,tag2,tag3");
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
        assertEquals(TestNotificationProfileConstants.NOTIFICATION_ID[0],
                response.getStringExtra(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: POST
     * Extra: 
     *     profile=notification
     *     attribute=notify
     *     type=0
     *     dir=auto
     *     lang=jp-JP
     *     tag=tag1,tag2,tag3
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに"1"が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional011() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_POST);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, NotificationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        request.putExtra(NotificationProfileConstants.PARAM_TYPE, 0);
        request.putExtra(NotificationProfileConstants.PARAM_DIR, "auto");
        request.putExtra(NotificationProfileConstants.PARAM_LANG, "jp-JP");
        request.putExtra(NotificationProfileConstants.PARAM_TAG, "tag1,tag2,tag3");
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
        assertEquals(TestNotificationProfileConstants.NOTIFICATION_ID[0],
                response.getStringExtra(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: POST
     * Extra: 
     *     profile=notification
     *     attribute=notify
     *     type=0
     *     body=test_body
     *     tag=tag1,tag2,tag3
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに"1"が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional012() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_POST);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, NotificationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        request.putExtra(NotificationProfileConstants.PARAM_TYPE, 0);
        request.putExtra(NotificationProfileConstants.PARAM_BODY, "test_body");
        request.putExtra(NotificationProfileConstants.PARAM_TAG, "tag1,tag2,tag3");
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
        assertEquals(TestNotificationProfileConstants.NOTIFICATION_ID[0],
                response.getStringExtra(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: POST
     * Extra: 
     *     profile=notification
     *     attribute=notify
     *     type=0
     *     dir=auto
     *     body=test_body
     *     tag=tag1,tag2,tag3
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに"1"が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional013() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_POST);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, NotificationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        request.putExtra(NotificationProfileConstants.PARAM_TYPE, 0);
        request.putExtra(NotificationProfileConstants.PARAM_DIR, "auto");
        request.putExtra(NotificationProfileConstants.PARAM_BODY, "test_body");
        request.putExtra(NotificationProfileConstants.PARAM_TAG, "tag1,tag2,tag3");
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
        assertEquals(TestNotificationProfileConstants.NOTIFICATION_ID[0],
                response.getStringExtra(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: POST
     * Extra: 
     *     profile=notification
     *     attribute=notify
     *     type=0
     *     lang=jp-JP
     *     body=test_body
     *     tag=tag1,tag2,tag3
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに"1"が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional014() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_POST);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, NotificationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        request.putExtra(NotificationProfileConstants.PARAM_TYPE, 0);
        request.putExtra(NotificationProfileConstants.PARAM_LANG, "jp-JP");
        request.putExtra(NotificationProfileConstants.PARAM_BODY, "test_body");
        request.putExtra(NotificationProfileConstants.PARAM_TAG, "tag1,tag2,tag3");
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
        assertEquals(TestNotificationProfileConstants.NOTIFICATION_ID[0],
                response.getStringExtra(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: POST
     * Extra: 
     *     profile=notification
     *     attribute=notify
     *     type=0
     *     dir=auto
     *     lang=jp-JP
     *     body=test_body
     *     tag=tag1,tag2,tag3
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに"1"が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional015() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_POST);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, NotificationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        request.putExtra(NotificationProfileConstants.PARAM_TYPE, 0);
        request.putExtra(NotificationProfileConstants.PARAM_DIR, "auto");
        request.putExtra(NotificationProfileConstants.PARAM_LANG, "jp-JP");
        request.putExtra(NotificationProfileConstants.PARAM_BODY, "test_body");
        request.putExtra(NotificationProfileConstants.PARAM_TAG, "tag1,tag2,tag3");
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
        assertEquals(TestNotificationProfileConstants.NOTIFICATION_ID[0],
                response.getStringExtra(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: POST
     * Extra: 
     *     profile=notification
     *     attribute=notify
     *     type=0
     *     icon=test.png
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに"1"が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional016() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_POST);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, NotificationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        request.putExtra(NotificationProfileConstants.PARAM_TYPE, 0);
        request.putExtra(NotificationProfileConstants.PARAM_ICON, "test.png");
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
        assertEquals(TestNotificationProfileConstants.NOTIFICATION_ID[0],
                response.getStringExtra(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: POST
     * Extra: 
     *     profile=notification
     *     attribute=notify
     *     type=0
     *     dir=auto
     *     icon=test.png
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに"1"が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional017() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_POST);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, NotificationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        request.putExtra(NotificationProfileConstants.PARAM_TYPE, 0);
        request.putExtra(NotificationProfileConstants.PARAM_DIR, "auto");
        request.putExtra(NotificationProfileConstants.PARAM_ICON, "test.png");
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
        assertEquals(TestNotificationProfileConstants.NOTIFICATION_ID[0],
                response.getStringExtra(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: POST
     * Extra: 
     *     profile=notification
     *     attribute=notify
     *     type=0
     *     lang=jp-JP
     *     icon=test.png
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに"1"が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional018() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_POST);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, NotificationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        request.putExtra(NotificationProfileConstants.PARAM_TYPE, 0);
        request.putExtra(NotificationProfileConstants.PARAM_LANG, "jp-JP");
        request.putExtra(NotificationProfileConstants.PARAM_ICON, "test.png");
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
        assertEquals(TestNotificationProfileConstants.NOTIFICATION_ID[0],
                response.getStringExtra(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: POST
     * Extra: 
     *     profile=notification
     *     attribute=notify
     *     type=0
     *     dir=auto
     *     lang=jp-JP
     *     icon=test.png
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに"1"が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional019() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_POST);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, NotificationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        request.putExtra(NotificationProfileConstants.PARAM_TYPE, 0);
        request.putExtra(NotificationProfileConstants.PARAM_DIR, "auto");
        request.putExtra(NotificationProfileConstants.PARAM_LANG, "jp-JP");
        request.putExtra(NotificationProfileConstants.PARAM_ICON, "test.png");
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
        assertEquals(TestNotificationProfileConstants.NOTIFICATION_ID[0],
                response.getStringExtra(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: POST
     * Extra: 
     *     profile=notification
     *     attribute=notify
     *     type=0
     *     body=test_body
     *     icon=test.png
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに"1"が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional020() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_POST);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, NotificationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        request.putExtra(NotificationProfileConstants.PARAM_TYPE, 0);
        request.putExtra(NotificationProfileConstants.PARAM_BODY, "test_body");
        request.putExtra(NotificationProfileConstants.PARAM_ICON, "test.png");
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
        assertEquals(TestNotificationProfileConstants.NOTIFICATION_ID[0],
                response.getStringExtra(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: POST
     * Extra: 
     *     profile=notification
     *     attribute=notify
     *     type=0
     *     dir=auto
     *     body=test_body
     *     icon=test.png
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに"1"が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional021() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_POST);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, NotificationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        request.putExtra(NotificationProfileConstants.PARAM_TYPE, 0);
        request.putExtra(NotificationProfileConstants.PARAM_DIR, "auto");
        request.putExtra(NotificationProfileConstants.PARAM_BODY, "test_body");
        request.putExtra(NotificationProfileConstants.PARAM_ICON, "test.png");
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
        assertEquals(TestNotificationProfileConstants.NOTIFICATION_ID[0],
                response.getStringExtra(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: POST
     * Extra: 
     *     profile=notification
     *     attribute=notify
     *     type=0
     *     lang=jp-JP
     *     body=test_body
     *     icon=test.png
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに"1"が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional022() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_POST);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, NotificationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        request.putExtra(NotificationProfileConstants.PARAM_TYPE, 0);
        request.putExtra(NotificationProfileConstants.PARAM_LANG, "jp-JP");
        request.putExtra(NotificationProfileConstants.PARAM_BODY, "test_body");
        request.putExtra(NotificationProfileConstants.PARAM_ICON, "test.png");
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
        assertEquals(TestNotificationProfileConstants.NOTIFICATION_ID[0],
                response.getStringExtra(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: POST
     * Extra: 
     *     profile=notification
     *     attribute=notify
     *     type=0
     *     dir=auto
     *     lang=jp-JP
     *     body=test_body
     *     icon=test.png
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに"1"が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional023() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_POST);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, NotificationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        request.putExtra(NotificationProfileConstants.PARAM_TYPE, 0);
        request.putExtra(NotificationProfileConstants.PARAM_DIR, "auto");
        request.putExtra(NotificationProfileConstants.PARAM_LANG, "jp-JP");
        request.putExtra(NotificationProfileConstants.PARAM_BODY, "test_body");
        request.putExtra(NotificationProfileConstants.PARAM_ICON, "test.png");
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
        assertEquals(TestNotificationProfileConstants.NOTIFICATION_ID[0],
                response.getStringExtra(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: POST
     * Extra: 
     *     profile=notification
     *     attribute=notify
     *     type=0
     *     tag=tag1,tag2,tag3
     *     icon=test.png
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに"1"が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional024() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_POST);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, NotificationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        request.putExtra(NotificationProfileConstants.PARAM_TYPE, 0);
        request.putExtra(NotificationProfileConstants.PARAM_TAG, "tag1,tag2,tag3");
        request.putExtra(NotificationProfileConstants.PARAM_ICON, "test.png");
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
        assertEquals(TestNotificationProfileConstants.NOTIFICATION_ID[0],
                response.getStringExtra(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: POST
     * Extra: 
     *     profile=notification
     *     attribute=notify
     *     type=0
     *     dir=auto
     *     tag=tag1,tag2,tag3
     *     icon=test.png
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに"1"が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional025() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_POST);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, NotificationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        request.putExtra(NotificationProfileConstants.PARAM_TYPE, 0);
        request.putExtra(NotificationProfileConstants.PARAM_DIR, "auto");
        request.putExtra(NotificationProfileConstants.PARAM_TAG, "tag1,tag2,tag3");
        request.putExtra(NotificationProfileConstants.PARAM_ICON, "test.png");
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
        assertEquals(TestNotificationProfileConstants.NOTIFICATION_ID[0],
                response.getStringExtra(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: POST
     * Extra: 
     *     profile=notification
     *     attribute=notify
     *     type=0
     *     lang=jp-JP
     *     tag=tag1,tag2,tag3
     *     icon=test.png
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに"1"が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional026() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_POST);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, NotificationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        request.putExtra(NotificationProfileConstants.PARAM_TYPE, 0);
        request.putExtra(NotificationProfileConstants.PARAM_LANG, "jp-JP");
        request.putExtra(NotificationProfileConstants.PARAM_TAG, "tag1,tag2,tag3");
        request.putExtra(NotificationProfileConstants.PARAM_ICON, "test.png");
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
        assertEquals(TestNotificationProfileConstants.NOTIFICATION_ID[0],
                response.getStringExtra(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: POST
     * Extra: 
     *     profile=notification
     *     attribute=notify
     *     type=0
     *     dir=auto
     *     lang=jp-JP
     *     tag=tag1,tag2,tag3
     *     icon=test.png
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに"1"が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional027() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_POST);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, NotificationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        request.putExtra(NotificationProfileConstants.PARAM_TYPE, 0);
        request.putExtra(NotificationProfileConstants.PARAM_DIR, "auto");
        request.putExtra(NotificationProfileConstants.PARAM_LANG, "jp-JP");
        request.putExtra(NotificationProfileConstants.PARAM_TAG, "tag1,tag2,tag3");
        request.putExtra(NotificationProfileConstants.PARAM_ICON, "test.png");
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
        assertEquals(TestNotificationProfileConstants.NOTIFICATION_ID[0],
                response.getStringExtra(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: POST
     * Extra: 
     *     profile=notification
     *     attribute=notify
     *     type=0
     *     body=test_body
     *     tag=tag1,tag2,tag3
     *     icon=test.png
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに"1"が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional028() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_POST);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, NotificationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        request.putExtra(NotificationProfileConstants.PARAM_TYPE, 0);
        request.putExtra(NotificationProfileConstants.PARAM_BODY, "test_body");
        request.putExtra(NotificationProfileConstants.PARAM_TAG, "tag1,tag2,tag3");
        request.putExtra(NotificationProfileConstants.PARAM_ICON, "test.png");
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
        assertEquals(TestNotificationProfileConstants.NOTIFICATION_ID[0],
                response.getStringExtra(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: POST
     * Extra: 
     *     profile=notification
     *     attribute=notify
     *     type=0
     *     dir=auto
     *     body=test_body
     *     tag=tag1,tag2,tag3
     *     icon=test.png
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに"1"が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional029() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_POST);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, NotificationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        request.putExtra(NotificationProfileConstants.PARAM_TYPE, 0);
        request.putExtra(NotificationProfileConstants.PARAM_DIR, "auto");
        request.putExtra(NotificationProfileConstants.PARAM_BODY, "test_body");
        request.putExtra(NotificationProfileConstants.PARAM_TAG, "tag1,tag2,tag3");
        request.putExtra(NotificationProfileConstants.PARAM_ICON, "test.png");
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
        assertEquals(TestNotificationProfileConstants.NOTIFICATION_ID[0],
                response.getStringExtra(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: POST
     * Extra: 
     *     profile=notification
     *     attribute=notify
     *     type=0
     *     lang=jp-JP
     *     body=test_body
     *     tag=tag1,tag2,tag3
     *     icon=test.png
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに"1"が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional030() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_POST);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, NotificationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        request.putExtra(NotificationProfileConstants.PARAM_TYPE, 0);
        request.putExtra(NotificationProfileConstants.PARAM_LANG, "jp-JP");
        request.putExtra(NotificationProfileConstants.PARAM_BODY, "test_body");
        request.putExtra(NotificationProfileConstants.PARAM_TAG, "tag1,tag2,tag3");
        request.putExtra(NotificationProfileConstants.PARAM_ICON, "test.png");
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
        assertEquals(TestNotificationProfileConstants.NOTIFICATION_ID[0],
                response.getStringExtra(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
    }

    /**
     * オプショナルなパラメータが省略可能であることのテストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: POST
     * Extra: 
     *     profile=notification
     *     attribute=notify
     *     type=0
     *     dir=auto
     *     lang=jp-JP
     *     body=test_body
     *     tag=tag1,tag2,tag3
     *     icon=test.png
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・notificationidに"1"が返ってくること。
     * </pre>
     */
    public void testPostNotifyOptional031() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_POST);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, NotificationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        request.putExtra(NotificationProfileConstants.PARAM_TYPE, 0);
        request.putExtra(NotificationProfileConstants.PARAM_DIR, "auto");
        request.putExtra(NotificationProfileConstants.PARAM_LANG, "jp-JP");
        request.putExtra(NotificationProfileConstants.PARAM_BODY, "test_body");
        request.putExtra(NotificationProfileConstants.PARAM_TAG, "tag1,tag2,tag3");
        request.putExtra(NotificationProfileConstants.PARAM_ICON, "test.png");
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
        assertEquals(TestNotificationProfileConstants.NOTIFICATION_ID[0],
                response.getStringExtra(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
    }

    /**
     * 通知の消去要求を送信するテストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: DELETE
     * Extra: 
     *     profile=notification
     *     attribute=notify
     *     notificationId=xxxx
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testDeleteNotify() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_DELETE);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, NotificationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, NotificationProfileConstants.ATTRIBUTE_NOTIFY);
        request.putExtra(NotificationProfileConstants.PARAM_NOTIFICATION_ID,
                TestNotificationProfileConstants.NOTIFICATION_ID[0]);
        Intent response = sendRequest(request);
        assertResultOK(response);
    }

    /**
     * 通知クリックイベントのコールバック登録テストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Extra: 
     *     profile=notification
     *     attribute=onclick
     *     sessionKey=xxxx
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・コールバック登録後にイベントを受信できること。
     * </pre>
     */
    public void testPutOnClick() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, NotificationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, NotificationProfileConstants.ATTRIBUTE_ON_CLICK);
        request.putExtra(DConnectMessage.EXTRA_SESSION_KEY, getClientId());
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));

        Intent event = waitForEvent();
        assertEquals(TestNotificationProfileConstants.NOTIFICATION_ID[0], 
                event.getStringExtra(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
    }

    /**
     * 通知クリックイベントのコールバック解除テストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: DELETE
     * Extra: 
     *     profile=notification
     *     attribute=onclick
     *     sessionKey=xxxx
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testDeleteOnClick() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_DELETE);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, NotificationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, NotificationProfileConstants.ATTRIBUTE_ON_CLICK);
        request.putExtra(DConnectMessage.EXTRA_SESSION_KEY, getClientId());
        Intent response = sendRequest(request);
        assertResultOK(response);
    }

    /**
     * 通知表示イベントのコールバック登録テストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Extra: 
     *     profile=notification
     *     attribute=onshow
     *     sessionKey=xxxx
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・コールバック登録後にイベントを受信できること。
     * </pre>
     */
    public void testPutOnShow() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, NotificationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, NotificationProfileConstants.ATTRIBUTE_ON_SHOW);
        request.putExtra(DConnectMessage.EXTRA_SESSION_KEY, getClientId());
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));

        Intent event = waitForEvent();
        assertEquals(TestNotificationProfileConstants.NOTIFICATION_ID[0], 
                event.getStringExtra(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
    }

    /**
     * 通知表示イベントのコールバック解除テストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: DELETE
     * Extra: 
     *     profile=notification
     *     attribute=onshow
     *     sessionKey=xxxx
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testDeleteOnShow() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_DELETE);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, NotificationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, NotificationProfileConstants.ATTRIBUTE_ON_SHOW);
        request.putExtra(DConnectMessage.EXTRA_SESSION_KEY, getClientId());
        Intent response = sendRequest(request);
        assertResultOK(response);
    }

    /**
     * 通知消去イベントのコールバック登録テストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Extra: 
     *     profile=notification
     *     attribute=onclose
     *     sessionKey=xxxx
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・コールバック登録後にイベントを受信できること。
     * </pre>
     */
    public void testPutOnClose() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, NotificationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, NotificationProfileConstants.ATTRIBUTE_ON_CLOSE);
        request.putExtra(DConnectMessage.EXTRA_SESSION_KEY, getClientId());
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));

        Intent event = waitForEvent();
        assertEquals(TestNotificationProfileConstants.NOTIFICATION_ID[0], 
                event.getStringExtra(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
    }

    /**
     * 通知消去イベントのコールバック解除テストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: DELETE
     * Extra: 
     *     profile=notification
     *     attribute=onclose
     *     sessionKey=xxxx
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testDeleteOnClose() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_DELETE);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, NotificationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, NotificationProfileConstants.ATTRIBUTE_ON_CLOSE);
        request.putExtra(DConnectMessage.EXTRA_SESSION_KEY, getClientId());
        Intent response = sendRequest(request);
        assertResultOK(response);
    }

    /**
     * 通知操作エラー発生イベントのコールバック登録テストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Extra: 
     *     profile=notification
     *     attribute=onerror
     *     sessionKey=xxxx
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・コールバック登録後にイベントを受信できること。
     * </pre>
     */
    public void testPutOnError() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, NotificationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, NotificationProfileConstants.ATTRIBUTE_ON_ERROR);
        request.putExtra(DConnectMessage.EXTRA_SESSION_KEY, getClientId());
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));

        Intent event = waitForEvent();
        assertEquals(TestNotificationProfileConstants.NOTIFICATION_ID[0], 
                event.getStringExtra(NotificationProfileConstants.PARAM_NOTIFICATION_ID));
    }

    /**
     * 通知操作エラー発生イベントのコールバック解除テストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: DELETE
     * Extra: 
     *     profile=notification
     *     attribute=onerror
     *     sessionKey=xxxx
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testDeleteOnError() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_DELETE);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, NotificationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, NotificationProfileConstants.ATTRIBUTE_ON_ERROR);
        request.putExtra(DConnectMessage.EXTRA_SESSION_KEY, getClientId());
        Intent response = sendRequest(request);
        assertResultOK(response);
    }

}
