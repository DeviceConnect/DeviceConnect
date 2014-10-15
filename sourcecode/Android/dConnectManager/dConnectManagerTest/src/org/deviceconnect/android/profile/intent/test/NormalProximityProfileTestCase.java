/*
 NormalProximityProfileTestCase.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.profile.intent.test;

import org.deviceconnect.android.test.plugin.profile.TestProximityProfileConstants;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.message.intent.message.IntentDConnectMessage;
import org.deviceconnect.profile.ProximityProfileConstants;

import android.content.Intent;
import android.os.Bundle;




/**
 * Proximityプロファイルの正常系テスト.
 * @author NTT DOCOMO, INC.
 */
public class NormalProximityProfileTestCase extends IntentDConnectTestCase {

    /**
     * コンストラクタ.
     * @param tag テストタグ
     */
    public NormalProximityProfileTestCase(final String tag) {
        super(tag);
    }

    /**
     * 近接センサーによる物の検知のコールバック登録テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Extra: 
     *     profile=proximity
     *     attribute=ondeviceproximity
     *     session_key=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testOnDeviceProximity01() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, ProximityProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, ProximityProfileConstants.ATTRIBUTE_ON_DEVICE_PROXIMITY);
        request.putExtra(DConnectMessage.EXTRA_SESSION_KEY, TEST_SESSION_KEY);
        Intent response = sendRequest(request);

        assertResultOK(response);

        Intent event = waitForEvent();
        Bundle obj = event.getBundleExtra(ProximityProfileConstants.PARAM_PROXIMITY);
        assertEquals(Double.valueOf(TestProximityProfileConstants.VALUE), 
                obj.getDouble(ProximityProfileConstants.PARAM_VALUE));
        assertEquals(Double.valueOf(TestProximityProfileConstants.THRESHOLD), 
                obj.getDouble(ProximityProfileConstants.PARAM_THRESHOLD));
        assertEquals(Double.valueOf(TestProximityProfileConstants.MAX), 
                obj.getDouble(ProximityProfileConstants.PARAM_MAX));
        assertEquals(Double.valueOf(TestProximityProfileConstants.MIN), 
                obj.getDouble(ProximityProfileConstants.PARAM_MIN));
    }

    /**
     * 近接センサーによる物の検知のコールバック解除テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: DELETE
     * Extra: 
     *     profile=proximity
     *     attribute=ondeviceproximity
     *     session_key=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testOnDeviceProximity02() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_DELETE);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, ProximityProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, ProximityProfileConstants.ATTRIBUTE_ON_DEVICE_PROXIMITY);
        request.putExtra(DConnectMessage.EXTRA_SESSION_KEY, TEST_SESSION_KEY);
        Intent response = sendRequest(request);

        assertResultOK(response);
    }

    /**
     * 近接センサーによる人の検知のコールバック登録テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Extra: 
     *     profile=proximity
     *     attribute=onuserproximity
     *     session_key=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testOnUserProximity01() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, ProximityProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, ProximityProfileConstants.ATTRIBUTE_ON_USER_PROXIMITY);
        request.putExtra(DConnectMessage.EXTRA_SESSION_KEY, TEST_SESSION_KEY);
        Intent response = sendRequest(request);

        assertResultOK(response);

        Intent event = waitForEvent();
        Bundle obj = event.getBundleExtra(ProximityProfileConstants.PARAM_PROXIMITY);
        assertEquals(TestProximityProfileConstants.NEAR, 
                obj.getBoolean(ProximityProfileConstants.PARAM_NEAR));
    }

    /**
     * 近接センサーによる人の検知のコールバック解除テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: DELETE
     * Extra: 
     *     profile=proximity
     *     attribute=onuserproximity
     *     session_key=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testOnUserProximity02() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_DELETE);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, ProximityProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, ProximityProfileConstants.ATTRIBUTE_ON_USER_PROXIMITY);
        request.putExtra(DConnectMessage.EXTRA_SESSION_KEY, TEST_SESSION_KEY);
        Intent response = sendRequest(request);

        assertResultOK(response);
    }

}
