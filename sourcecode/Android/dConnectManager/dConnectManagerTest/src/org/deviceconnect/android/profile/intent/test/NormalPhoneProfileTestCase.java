/*
 NormalPhoneProfileTestCase.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.profile.intent.test;

import org.deviceconnect.android.test.plugin.profile.TestPhoneProfileConstants;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.message.intent.message.IntentDConnectMessage;
import org.deviceconnect.profile.PhoneProfileConstants;

import android.content.Intent;
import android.os.Bundle;



/**
 * Phoneプロファイルの正常系テスト.
 * @author NTT DOCOMO, INC.
 */
public class NormalPhoneProfileTestCase extends IntentDConnectTestCase {

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
     * 【Intent通信】
     * Action: POST
     * Extra: 
     *     profile=phone
     *     attribute=call
     *     mediaid=yyyy
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultが0で返ってくること。
     * </pre>
     */
    public void testPostCall() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_POST);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, PhoneProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, PhoneProfileConstants.ATTRIBUTE_CALL);
        request.putExtra(PhoneProfileConstants.PARAM_PHONE_NUMBER, TestPhoneProfileConstants.PHONE_NUMBER);
        Intent response = sendRequest(request);

        assertResultOK(response);
    }

    /**
     * 電話に関する設定項目(サイレント・マナー・音あり)の設定テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Extra: 
     *     profile=phone
     *     attribute=set
     *     mode=0
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultが0で返ってくること。
     * </pre>
     */
    public void testPutSet001() {
        final int mode = 0;
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, PhoneProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, PhoneProfileConstants.ATTRIBUTE_SET);
        request.putExtra(PhoneProfileConstants.PARAM_MODE, mode);
        Intent response = sendRequest(request);

        assertResultOK(response);
    }

    /**
     * 電話に関する設定項目(マナー)の設定テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Extra: 
     *     profile=phone
     *     attribute=set
     *     mode=1
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultが0で返ってくること。
     * </pre>
     */
    public void testPutSet002() {
        final int mode = 1;
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, PhoneProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, PhoneProfileConstants.ATTRIBUTE_SET);
        request.putExtra(PhoneProfileConstants.PARAM_MODE, mode);
        Intent response = sendRequest(request);

        assertResultOK(response);
    }

    /**
     * 電話に関する設定項目(音あり)の設定テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Extra: 
     *     profile=phone
     *     attribute=set
     *     mode=2
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultが0で返ってくること。
     * </pre>
     */
    public void testPutSet003() {
        final int mode = 2;
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, PhoneProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, PhoneProfileConstants.ATTRIBUTE_SET);
        request.putExtra(PhoneProfileConstants.PARAM_MODE, mode);
        Intent response = sendRequest(request);

        assertResultOK(response);
    }

    /**
     * 通話関連イベントのコールバック登録テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Extra: 
     *     profile=phone
     *     attribute=onconnect
     *     sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultが0で返ってくること。
     * </pre>
     */
    public void testPutOnConnect() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, PhoneProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, PhoneProfileConstants.ATTRIBUTE_ON_CONNECT);
        request.putExtra(DConnectMessage.EXTRA_SESSION_KEY, getClientId());
        Intent response = sendRequest(request);

        assertResultOK(response);
        Intent event = waitForEvent();
        Bundle phoneStatus = event.getBundleExtra(PhoneProfileConstants.PARAM_PHONE_STATUS);
        assertTrue(phoneStatus.containsKey(PhoneProfileConstants.PARAM_PHONE_NUMBER));
        assertEquals(TestPhoneProfileConstants.PHONE_NUMBER,
                phoneStatus.getString(PhoneProfileConstants.PARAM_PHONE_NUMBER));
        assertTrue(phoneStatus.containsKey(PhoneProfileConstants.PARAM_STATE));
        assertEquals(PhoneProfileConstants.CallState.FINISHED.getValue(),
                phoneStatus.getInt(PhoneProfileConstants.PARAM_STATE));
    }

    /**
     * 通話関連イベントのコールバック解除テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: DELETE
     * Extra: 
     *     profile=phone
     *     attribute=onconnect
     *     sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultが0で返ってくること。
     * </pre>
     */
    public void testDeleteOnConnect() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_DELETE);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, PhoneProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, PhoneProfileConstants.ATTRIBUTE_ON_CONNECT);
        request.putExtra(DConnectMessage.EXTRA_SESSION_KEY, TEST_SESSION_KEY);
        Intent response = sendRequest(request);

        assertResultOK(response);
    }

}
