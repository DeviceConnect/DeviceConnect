/*
 NormalVibrationProfileTestCase.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.profile.intent.test;

import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.message.intent.message.IntentDConnectMessage;
import org.deviceconnect.profile.VibrationProfileConstants;

import android.content.Intent;


/**
 * Vibratorプロファイルの正常系テスト.
 * @author NTT DOCOMO, INC.
 */
public class NormalVibrationProfileTestCase extends IntentDConnectTestCase {
    /**
     * コンストラクタ.
     * @param string テストタグ
     */
    public NormalVibrationProfileTestCase(final String string) {
        super(string);
    }

    /**
     * Vibration開始テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Extra: 
     *     profile=vibration
     *     attribute=vibrate
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testPutVibrate001() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, VibrationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, VibrationProfileConstants.ATTRIBUTE_VIBRATE);
        Intent response = sendRequest(request);
        assertResultOK(response);
    }

    /**
     * Vibration開始テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Extra: 
     *     profile=vibration
     *     attribute=vibrate
     *     pattern=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testPutVibrate002() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, VibrationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, VibrationProfileConstants.ATTRIBUTE_VIBRATE);
        request.putExtra(VibrationProfileConstants.PARAM_PATTERN, "500,500,500");
        Intent response = sendRequest(request);
        assertResultOK(response);
    }

    /**
     * Vibration開始テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: DELETE
     * Extra: 
     *     profile=vibration
     *     attribute=vibrate
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testDeleteVibrate() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_DELETE);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, VibrationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, VibrationProfileConstants.ATTRIBUTE_VIBRATE);
        Intent response = sendRequest(request);
        assertResultOK(response);
    }
}
