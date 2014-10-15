/*
 NormalDeviceOrientationProfileTestCase.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.profile.intent.test;

import junit.framework.Assert;

import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.message.intent.message.IntentDConnectMessage;
import org.deviceconnect.profile.DeviceOrientationProfileConstants;

import android.content.Intent;
import android.os.Bundle;

/**
 * DeviceOrientationプロファイルの正常系テスト.
 * @author NTT DOCOMO, INC.
 */
public class NormalDeviceOrientationProfileTestCase extends IntentDConnectTestCase {

    /**
     * コンストラクタ.
     * @param string テストタグ
     */
    public NormalDeviceOrientationProfileTestCase(final String string) {
        super(string);
    }

    /**
     * ondeviceorientationイベントのコールバック登録テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Extra: 
     *     profile=deviceorientation
     *     attribute=ondeviceorientation
     *     sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・コールバック登録後にイベントを受信すること。
     * </pre>
     */
    public void testPutOnDeviceOrientation() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_SESSION_KEY, getClientId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, DeviceOrientationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE,
                DeviceOrientationProfileConstants.ATTRIBUTE_ON_DEVICE_ORIENTATION);
        Intent response = sendRequest(request);
        Assert.assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        Assert.assertEquals(IntentDConnectMessage.RESULT_OK,
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
        Intent event = waitForEvent();
        Bundle orientation = event.getBundleExtra(DeviceOrientationProfileConstants.PARAM_ORIENTATION);
        Bundle a1 = orientation.getBundle(DeviceOrientationProfileConstants.PARAM_ACCELERATION);
        Assert.assertEquals(0.0, a1.getDouble(DeviceOrientationProfileConstants.PARAM_X));
        Assert.assertEquals(0.0, a1.getDouble(DeviceOrientationProfileConstants.PARAM_Y));
        Assert.assertEquals(0.0, a1.getDouble(DeviceOrientationProfileConstants.PARAM_Z));
        Bundle a2 = orientation.getBundle(DeviceOrientationProfileConstants.PARAM_ACCELERATION_INCLUDING_GRAVITY);
        Assert.assertEquals(0.0, a2.getDouble(DeviceOrientationProfileConstants.PARAM_X));
        Assert.assertEquals(0.0, a2.getDouble(DeviceOrientationProfileConstants.PARAM_Y));
        Assert.assertEquals(0.0, a2.getDouble(DeviceOrientationProfileConstants.PARAM_Z));
        Bundle r = orientation.getBundle(DeviceOrientationProfileConstants.PARAM_ROTATION_RATE);
        Assert.assertEquals(0.0, r.getDouble(DeviceOrientationProfileConstants.PARAM_ALPHA));
        Assert.assertEquals(0.0, r.getDouble(DeviceOrientationProfileConstants.PARAM_BETA));
        Assert.assertEquals(0.0, r.getDouble(DeviceOrientationProfileConstants.PARAM_GAMMA));
        Assert.assertEquals(0, orientation.getLong(DeviceOrientationProfileConstants.PARAM_INTERVAL));
    }

    /**
     * ondeviceorientationイベントのコールバック解除テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: DELETE
     * Extra: 
     *     profile=deviceorientation
     *     attribute=ondeviceorientation
     *     sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testDeleteOnDeviceOrientation() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_DELETE);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_SESSION_KEY, getClientId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, DeviceOrientationProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE,
                DeviceOrientationProfileConstants.ATTRIBUTE_ON_DEVICE_ORIENTATION);
        Intent response = sendRequest(request);
        Assert.assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        Assert.assertEquals(IntentDConnectMessage.RESULT_OK,
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
    }

}
