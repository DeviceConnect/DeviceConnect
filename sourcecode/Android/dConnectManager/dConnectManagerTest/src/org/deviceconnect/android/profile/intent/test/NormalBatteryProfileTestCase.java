/*
 NormalBatteryProfileTestCase.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.profile.intent.test;

import org.deviceconnect.android.test.plugin.profile.TestBatteryProfileConstants;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.message.intent.message.IntentDConnectMessage;
import org.deviceconnect.profile.BatteryProfileConstants;

import android.content.Intent;
import android.os.Bundle;


/**
 * Batteryプロファイルの正常系テスト.
 * @author NTT DOCOMO, INC.
 */
public class NormalBatteryProfileTestCase extends IntentDConnectTestCase {
    /**
     * コンストラクタ.
     * @param string テストタグ
     */
    public NormalBatteryProfileTestCase(final String string) {
        super(string);
    }

    /**
     * バッテリー全属性取得テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: GET
     * Profile: battery
     * Interface: なし
     * Attribute: なし
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・chargingがfalseで返ってくること。
     * ・chargingtimeが50000.0で返ってくること。
     * ・dischargingtimeが10000.0で返ってくること。
     * ・levelが0.5で返ってくること。
     * </pre>
     */
    public void testBattery() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_GET);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_SESSION_KEY, TEST_SESSION_KEY);
        request.putExtra(DConnectMessage.EXTRA_PROFILE, BatteryProfileConstants.PROFILE_NAME);
        Intent response = sendRequest(request);
        assertResultOK(response);
        assertTrue(response.hasExtra(BatteryProfileConstants.PARAM_CHARGING));
        assertEquals(TestBatteryProfileConstants.CHARGING, 
                response.getBooleanExtra(BatteryProfileConstants.PARAM_CHARGING, false));
        assertTrue(response.hasExtra(BatteryProfileConstants.PARAM_CHARGING_TIME));
        assertEquals(TestBatteryProfileConstants.CHARGING_TIME, 
                response.getDoubleExtra(BatteryProfileConstants.PARAM_CHARGING_TIME, 0));
        assertTrue(response.hasExtra(BatteryProfileConstants.PARAM_DISCHARGING_TIME));
        assertEquals(TestBatteryProfileConstants.DISCHARGING_TIME, 
                response.getDoubleExtra(BatteryProfileConstants.PARAM_DISCHARGING_TIME, 0));
        assertTrue(response.hasExtra(BatteryProfileConstants.PARAM_LEVEL));
        assertEquals(TestBatteryProfileConstants.LEVEL, 
                response.getDoubleExtra(BatteryProfileConstants.PARAM_LEVEL, 0.0d));
    }

    /**
     * charging属性取得テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: GET
     * Profile: battery
     * Interface: なし
     * Attribute: charging
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・chargingがfalseで返ってくること。
     * </pre>
     */
    public void testBatteryCharging() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_GET);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_SESSION_KEY, TEST_SESSION_KEY);
        request.putExtra(DConnectMessage.EXTRA_PROFILE, BatteryProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, BatteryProfileConstants.ATTRIBUTE_CHARGING);
        Intent response = sendRequest(request);
        assertResultOK(response);
        assertTrue(response.hasExtra(BatteryProfileConstants.PARAM_CHARGING));
        assertEquals(TestBatteryProfileConstants.CHARGING, 
                response.getBooleanExtra(BatteryProfileConstants.PARAM_CHARGING, false));
    }

    /**
     * chargingTime属性取得テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: GET
     * Profile: battery
     * Interface: なし
     * Attribute: なし
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・chargingTimeが50000.0で返ってくること。
     * </pre>
     */
    public void testBatteryChargingTime() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_GET);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_SESSION_KEY, TEST_SESSION_KEY);
        request.putExtra(DConnectMessage.EXTRA_PROFILE, BatteryProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, BatteryProfileConstants.ATTRIBUTE_CHARGING_TIME);
        Intent response = sendRequest(request);
        assertResultOK(response);
        assertTrue(response.hasExtra(BatteryProfileConstants.PARAM_CHARGING_TIME));
        assertEquals(TestBatteryProfileConstants.CHARGING_TIME, 
                response.getDoubleExtra(BatteryProfileConstants.PARAM_CHARGING_TIME, 0));
    }

    /**
     * dischargingTime属性取得テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: GET
     * Profile: battery
     * Interface: なし
     * Attribute: dischargingTime
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・dischargingTimeが10000.0で返ってくること。
     * </pre>
     */
    public void testBatteryDischargingTime() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_GET);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_SESSION_KEY, TEST_SESSION_KEY);
        request.putExtra(DConnectMessage.EXTRA_PROFILE, BatteryProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, BatteryProfileConstants.ATTRIBUTE_DISCHARGING_TIME);
        Intent response = sendRequest(request);
        assertResultOK(response);
        assertTrue(response.hasExtra(BatteryProfileConstants.PARAM_DISCHARGING_TIME));
        assertEquals(TestBatteryProfileConstants.DISCHARGING_TIME, 
                response.getDoubleExtra(BatteryProfileConstants.PARAM_DISCHARGING_TIME, 0));
    }

    /**
     * level属性取得テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: GET
     * Profile: battery
     * Interface: なし
     * Attribute: なし
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・levelが0.5で返ってくること。
     * </pre>
     */
    public void testBatteryLevel() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_GET);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_SESSION_KEY, TEST_SESSION_KEY);
        request.putExtra(DConnectMessage.EXTRA_PROFILE, BatteryProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, BatteryProfileConstants.ATTRIBUTE_LEVEL);
        Intent response = sendRequest(request);
        assertResultOK(response);
        assertTrue(response.hasExtra(BatteryProfileConstants.PARAM_LEVEL));
        assertEquals(TestBatteryProfileConstants.LEVEL, 
                response.getDoubleExtra(BatteryProfileConstants.PARAM_LEVEL, 0.0d));
    }

    /**
     * onchargingchange属性のコールバック登録テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Profile: battery
     * Interface: なし
     * Attribute: onchargingchange
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・コールバック登録後にイベントを受信すること。
     * </pre>
     */
    public void testBatteryOnChargingChange01() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_SESSION_KEY, TEST_SESSION_KEY);
        request.putExtra(DConnectMessage.EXTRA_PROFILE, BatteryProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, BatteryProfileConstants.ATTRIBUTE_ON_CHARGING_CHANGE);
        Intent response = sendRequest(request);
        assertResultOK(response);
        Intent event = waitForEvent();
        assertNotNull(event);
        assertEquals(BatteryProfileConstants.PROFILE_NAME,
                event.getStringExtra(DConnectMessage.EXTRA_PROFILE));
        assertEquals(BatteryProfileConstants.ATTRIBUTE_ON_CHARGING_CHANGE,
                event.getStringExtra(DConnectMessage.EXTRA_ATTRIBUTE));
        Bundle battery = event.getBundleExtra(BatteryProfileConstants.PROFILE_NAME);
        assertEquals(false, battery.getBoolean(BatteryProfileConstants.PARAM_CHARGING));
    }

    /**
     * onchargingchange属性のコールバック解除テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: DELETE
     * Profile: battery
     * Interface: なし
     * Attribute: onchargingchange
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testBatteryOnChargingChange02() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_DELETE);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_SESSION_KEY, TEST_SESSION_KEY);
        request.putExtra(DConnectMessage.EXTRA_PROFILE, BatteryProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, BatteryProfileConstants.ATTRIBUTE_ON_CHARGING_CHANGE);
        Intent response = sendRequest(request);
        assertResultOK(response);
    }

    /**
     * onbatterychange属性のコールバック登録テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Profile: battery
     * Interface: なし
     * Attribute: onbatterychange
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・コールバック登録後にイベントを受信すること。
     * </pre>
     */
    public void testBatteryOnBatteryChange01() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_SESSION_KEY, TEST_SESSION_KEY);
        request.putExtra(DConnectMessage.EXTRA_PROFILE, BatteryProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, BatteryProfileConstants.ATTRIBUTE_ON_BATTERY_CHANGE);
        Intent response = sendRequest(request);
        assertResultOK(response);
        Intent event = waitForEvent();
        assertNotNull(event);
        assertEquals(BatteryProfileConstants.PROFILE_NAME,
                event.getStringExtra(DConnectMessage.EXTRA_PROFILE));
        assertEquals(BatteryProfileConstants.ATTRIBUTE_ON_BATTERY_CHANGE,
                event.getStringExtra(DConnectMessage.EXTRA_ATTRIBUTE));
        Bundle battery = event.getBundleExtra(BatteryProfileConstants.PARAM_BATTERY);
        assertEquals(TestBatteryProfileConstants.CHARGING_TIME,
                battery.getDouble(BatteryProfileConstants.PARAM_CHARGING_TIME));
        assertEquals(TestBatteryProfileConstants.DISCHARGING_TIME,
                battery.getDouble(BatteryProfileConstants.PARAM_DISCHARGING_TIME));
        assertEquals(TestBatteryProfileConstants.LEVEL,
                battery.getDouble(BatteryProfileConstants.PARAM_LEVEL));
    }

    /**
     * onbatterychange属性のコールバック解除テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: DELETE
     * Profile: battery
     * Interface: なし
     * Attribute: onbatterychange
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testBatteryOnBatteryChange02() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_DELETE);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_SESSION_KEY, TEST_SESSION_KEY);
        request.putExtra(DConnectMessage.EXTRA_PROFILE, BatteryProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, BatteryProfileConstants.ATTRIBUTE_ON_BATTERY_CHANGE);
        Intent response = sendRequest(request);
        assertResultOK(response);
    }

}
