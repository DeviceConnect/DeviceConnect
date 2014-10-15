/*
 NormalSettingsProfileTestCase.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.profile.intent.test;

import org.deviceconnect.android.test.plugin.profile.TestSettingsProfileConstants;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.message.intent.message.IntentDConnectMessage;
import org.deviceconnect.profile.SettingsProfileConstants;

import android.content.Intent;


/**
 * Settingsプロファイルの正常系テスト.
 * @author NTT DOCOMO, INC.
 */
public class NormalSettingsProfileTestCase extends IntentDConnectTestCase
    implements TestSettingsProfileConstants {

    /**
     * コンストラクタ.
     * @param tag テストタグ
     */
    public NormalSettingsProfileTestCase(final String tag) {
        super(tag);
    }

    /**
     * スマートデバイスの音量取得テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: GET
     * Extra:
     *     profile=settings
     *     attribute=volume
     *     kind=1
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・levelが0.5で返ってくること。
     * </pre>
     */
    public void testGetVolume001() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_GET);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, SettingsProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_INTERFACE, SettingsProfileConstants.INTERFACE_SOUND);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, SettingsProfileConstants.ATTRIBUTE_VOLUME);
        request.putExtra(SettingsProfileConstants.PARAM_KIND, SettingsProfileConstants.VolumeKind.ALARM.getValue());
        Intent response = sendRequest(request);

        assertResultOK(response);
        assertEquals(TestSettingsProfileConstants.LEVEL,
                response.getDoubleExtra(SettingsProfileConstants.PARAM_LEVEL, 0));
    }

    /**
     * スマートデバイスの音量取得テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: GET
     * Extra:
     *     profile=settings
     *     attribute=volume
     *     kind=2
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・levelが0.5で返ってくること。
     * </pre>
     */
    public void testGetVolume002() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_GET);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, SettingsProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_INTERFACE, SettingsProfileConstants.INTERFACE_SOUND);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, SettingsProfileConstants.ATTRIBUTE_VOLUME);
        request.putExtra(SettingsProfileConstants.PARAM_KIND, SettingsProfileConstants.VolumeKind.CALL.getValue());
        Intent response = sendRequest(request);

        assertResultOK(response);
        assertEquals(TestSettingsProfileConstants.LEVEL,
                response.getDoubleExtra(SettingsProfileConstants.PARAM_LEVEL, 0));
    }

    /**
     * スマートデバイスの音量取得テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: GET
     * Extra:
     *     profile=settings
     *     attribute=volume
     *     kind=3
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・levelが0.5で返ってくること。
     * </pre>
     */
    public void testGetVolume003() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_GET);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, SettingsProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_INTERFACE, SettingsProfileConstants.INTERFACE_SOUND);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, SettingsProfileConstants.ATTRIBUTE_VOLUME);
        request.putExtra(SettingsProfileConstants.PARAM_KIND, SettingsProfileConstants.VolumeKind.RINGTONE.getValue());
        Intent response = sendRequest(request);

        assertResultOK(response);
        assertEquals(TestSettingsProfileConstants.LEVEL,
                response.getDoubleExtra(SettingsProfileConstants.PARAM_LEVEL, 0));
    }

    /**
     * スマートデバイスの音量取得テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: GET
     * Extra:
     *     profile=settings
     *     attribute=volume
     *     kind=4
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・levelが0.5で返ってくること。
     * </pre>
     */
    public void testGetVolume004() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_GET);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, SettingsProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_INTERFACE, SettingsProfileConstants.INTERFACE_SOUND);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, SettingsProfileConstants.ATTRIBUTE_VOLUME);
        request.putExtra(SettingsProfileConstants.PARAM_KIND, SettingsProfileConstants.VolumeKind.MAIL.getValue());
        Intent response = sendRequest(request);

        assertResultOK(response);
        assertEquals(TestSettingsProfileConstants.LEVEL,
                response.getDoubleExtra(SettingsProfileConstants.PARAM_LEVEL, 0));
    }

    /**
     * スマートデバイスの音量取得テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: GET
     * Extra:
     *     profile=settings
     *     attribute=volume
     *     kind=5
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・levelが0.5で返ってくること。
     * </pre>
     */
    public void testGetVolume005() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_GET);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, SettingsProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_INTERFACE, SettingsProfileConstants.INTERFACE_SOUND);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, SettingsProfileConstants.ATTRIBUTE_VOLUME);
        request.putExtra(SettingsProfileConstants.PARAM_KIND, SettingsProfileConstants.VolumeKind.OTHER.getValue());
        Intent response = sendRequest(request);

        assertResultOK(response);
        assertEquals(TestSettingsProfileConstants.LEVEL,
                response.getDoubleExtra(SettingsProfileConstants.PARAM_LEVEL, 0));
    }

    /**
     * スマートデバイスの音量設定テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Extra:
     *     profile=settings
     *     attribute=volume
     *     kind=1
     *     level=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testPutVolume001() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, SettingsProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_INTERFACE, SettingsProfileConstants.INTERFACE_SOUND);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, SettingsProfileConstants.ATTRIBUTE_VOLUME);
        request.putExtra(SettingsProfileConstants.PARAM_KIND, SettingsProfileConstants.VolumeKind.ALARM.getValue());
        request.putExtra(SettingsProfileConstants.PARAM_LEVEL, TestSettingsProfileConstants.LEVEL);
        Intent response = sendRequest(request);

        assertResultOK(response);
    }

    /**
     * スマートデバイスの音量設定テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Extra:
     *     profile=settings
     *     attribute=volume
     *     kind=2
     *     level=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testPutVolume002() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, SettingsProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_INTERFACE, SettingsProfileConstants.INTERFACE_SOUND);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, SettingsProfileConstants.ATTRIBUTE_VOLUME);
        request.putExtra(SettingsProfileConstants.PARAM_KIND, SettingsProfileConstants.VolumeKind.CALL.getValue());
        request.putExtra(SettingsProfileConstants.PARAM_LEVEL, TestSettingsProfileConstants.LEVEL);
        Intent response = sendRequest(request);

        assertResultOK(response);
    }

    /**
     * スマートデバイスの音量設定テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Extra:
     *     profile=settings
     *     attribute=volume
     *     kind=3
     *     level=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testPutVolume003() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, SettingsProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_INTERFACE, SettingsProfileConstants.INTERFACE_SOUND);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, SettingsProfileConstants.ATTRIBUTE_VOLUME);
        request.putExtra(SettingsProfileConstants.PARAM_KIND, SettingsProfileConstants.VolumeKind.RINGTONE.getValue());
        request.putExtra(SettingsProfileConstants.PARAM_LEVEL, TestSettingsProfileConstants.LEVEL);
        Intent response = sendRequest(request);

        assertResultOK(response);
    }

    /**
     * スマートデバイスの音量設定テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Extra:
     *     profile=settings
     *     attribute=volume
     *     kind=4
     *     level=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testPutVolume004() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, SettingsProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_INTERFACE, SettingsProfileConstants.INTERFACE_SOUND);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, SettingsProfileConstants.ATTRIBUTE_VOLUME);
        request.putExtra(SettingsProfileConstants.PARAM_KIND, SettingsProfileConstants.VolumeKind.MAIL.getValue());
        request.putExtra(SettingsProfileConstants.PARAM_LEVEL, TestSettingsProfileConstants.LEVEL);
        Intent response = sendRequest(request);

        assertResultOK(response);
    }

    /**
     * スマートデバイスの音量設定テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Extra:
     *     profile=settings
     *     attribute=volume
     *     kind=5
     *     level=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testPutVolume005() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, SettingsProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_INTERFACE, SettingsProfileConstants.INTERFACE_SOUND);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, SettingsProfileConstants.ATTRIBUTE_VOLUME);
        request.putExtra(SettingsProfileConstants.PARAM_KIND, SettingsProfileConstants.VolumeKind.OTHER.getValue());
        request.putExtra(SettingsProfileConstants.PARAM_LEVEL, TestSettingsProfileConstants.LEVEL);
        Intent response = sendRequest(request);

        assertResultOK(response);
    }

    /**
     * スマートデバイスの日時取得テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: GET
     * Extra:
     *     profile=settings
     *     attribute=date
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・dateが"2014-01-01T01:01:01+09:00"で返ってくること。
     * </pre>
     */
    public void testGetDate() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_GET);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, SettingsProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, SettingsProfileConstants.ATTRIBUTE_DATE);
        Intent response = sendRequest(request);

        assertResultOK(response);
        assertEquals(TestSettingsProfileConstants.DATE,
                response.getStringExtra(SettingsProfileConstants.PARAM_DATE));
    }

    /**
     * スマートデバイスの日時設定テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Extra:
     *     profile=settings
     *     attribute=date
     *     date=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testPutDate() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_GET);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, SettingsProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, SettingsProfileConstants.ATTRIBUTE_DATE);
        request.putExtra(SettingsProfileConstants.PARAM_DATE, TestSettingsProfileConstants.DATE);
        Intent response = sendRequest(request);

        assertResultOK(response);
    }

    /**
     * スマートデバイスのライト明度取得テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: GET
     * Extra:
     *     profile=settings
     *     attribute=light
     *     kind=1
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・levelが50で返ってくること。
     * </pre>
     */
    public void testGetLight001() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_GET);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, SettingsProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_INTERFACE, SettingsProfileConstants.INTERFACE_DISPLAY);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, SettingsProfileConstants.ATTRIBUTE_LIGHT);
        request.putExtra(SettingsProfileConstants.PARAM_KIND, 1);
        Intent response = sendRequest(request);

        assertResultOK(response);
        assertEquals(TestSettingsProfileConstants.LEVEL,
                response.getDoubleExtra(SettingsProfileConstants.PARAM_LEVEL, 0));
    }

    /**
     * スマートデバイスのライト明度取得テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: GET
     * Extra:
     *     profile=settings
     *     attribute=light
     *     kind=2
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・levelが50で返ってくること。
     * </pre>
     */
    public void testGetLight002() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_GET);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, SettingsProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_INTERFACE, SettingsProfileConstants.INTERFACE_DISPLAY);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, SettingsProfileConstants.ATTRIBUTE_LIGHT);
        request.putExtra(SettingsProfileConstants.PARAM_KIND, 2);
        Intent response = sendRequest(request);

        assertResultOK(response);
        assertEquals(TestSettingsProfileConstants.LEVEL,
                response.getDoubleExtra(SettingsProfileConstants.PARAM_LEVEL, 0));
    }

    /**
     * スマートデバイスのライト明度取得テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: GET
     * Extra:
     *     profile=settings
     *     attribute=light
     *     kind=3
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・levelが50で返ってくること。
     * </pre>
     */
    public void testGetLight003() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_GET);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, SettingsProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_INTERFACE, SettingsProfileConstants.INTERFACE_DISPLAY);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, SettingsProfileConstants.ATTRIBUTE_LIGHT);
        request.putExtra(SettingsProfileConstants.PARAM_KIND, 3);
        Intent response = sendRequest(request);

        assertResultOK(response);
        assertEquals(TestSettingsProfileConstants.LEVEL,
                response.getDoubleExtra(SettingsProfileConstants.PARAM_LEVEL, 0));
    }

    /**
     * スマートデバイスのライト明度設定テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Extra:
     *     profile=settings
     *     attribute=light
     *     kind=1
     *     level=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testPutLight001() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, SettingsProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_INTERFACE, SettingsProfileConstants.INTERFACE_DISPLAY);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, SettingsProfileConstants.ATTRIBUTE_LIGHT);
        request.putExtra(SettingsProfileConstants.PARAM_KIND, 1);
        request.putExtra(SettingsProfileConstants.PARAM_LEVEL, TestSettingsProfileConstants.LEVEL);
        Intent response = sendRequest(request);
        assertResultOK(response);
    }

    /**
     * スマートデバイスのライト明度設定テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Extra:
     *     profile=settings
     *     attribute=light
     *     kind=1
     *     level=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testPutLight002() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, SettingsProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_INTERFACE, SettingsProfileConstants.INTERFACE_DISPLAY);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, SettingsProfileConstants.ATTRIBUTE_LIGHT);
        request.putExtra(SettingsProfileConstants.PARAM_KIND, 2);
        request.putExtra(SettingsProfileConstants.PARAM_LEVEL, TestSettingsProfileConstants.LEVEL);
        Intent response = sendRequest(request);
        assertResultOK(response);
    }

    /**
     * スマートデバイスのライト明度設定テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Extra:
     *     profile=settings
     *     attribute=light
     *     kind=1
     *     level=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testPutLight003() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, SettingsProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_INTERFACE, SettingsProfileConstants.INTERFACE_DISPLAY);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, SettingsProfileConstants.ATTRIBUTE_LIGHT);
        request.putExtra(SettingsProfileConstants.PARAM_KIND, 3);
        request.putExtra(SettingsProfileConstants.PARAM_LEVEL, TestSettingsProfileConstants.LEVEL);
        Intent response = sendRequest(request);
        assertResultOK(response);
    }

    /**
     * 画面消灯設定の取得テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: GET
     * Extra:
     *     profile=settings
     *     interface=display
     *     attribute=sleep
     *     kind=3
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・timeが0で返ってくること。
     * </pre>
     */
    public void testGetSleep() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_GET);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, SettingsProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_INTERFACE, SettingsProfileConstants.INTERFACE_DISPLAY);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, SettingsProfileConstants.ATTRIBUTE_SLEEP);
        Intent response = sendRequest(request);

        assertResultOK(response);
        assertEquals(1, response.getIntExtra(SettingsProfileConstants.PARAM_TIME, -1));
    }

    /**
     * 画面消灯設定の設定テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Extra:
     *     profile=settings
     *     interface=display
     *     attribute=sleep
     *     kind=1
     *     level=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testPutSleep() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, SettingsProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_INTERFACE, SettingsProfileConstants.INTERFACE_DISPLAY);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, SettingsProfileConstants.ATTRIBUTE_SLEEP);
        request.putExtra(SettingsProfileConstants.PARAM_TIME, 1);
        Intent response = sendRequest(request);
        assertResultOK(response);
    }
}
