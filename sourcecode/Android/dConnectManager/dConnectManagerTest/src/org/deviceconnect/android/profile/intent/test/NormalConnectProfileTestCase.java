/*
 NormalConnectProfileTestCase.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.profile.intent.test;

import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.message.intent.message.IntentDConnectMessage;
import org.deviceconnect.profile.ConnectProfileConstants;

import android.content.Intent;
import android.os.Bundle;

/**
 * Connectプロファイルの正常系テスト.
 * @author NTT DOCOMO, INC.
 */
public class NormalConnectProfileTestCase extends IntentDConnectTestCase {

    /**
     * コンストラクタ.
     * 
     * @param string テストタグ
     */
    public NormalConnectProfileTestCase(final String string) {
        super(string);
    }

    /**
     * WiFi機能有効状態(ON/OFF)取得テストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: GET
     * Profile: connect
     * Interface: なし
     * Attribute: wifi
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testGetWifi() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_GET);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, ConnectProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, ConnectProfileConstants.ATTRIBUTE_WIFI);
        Intent response = sendRequest(request);
        assertResultOK(response);
        assertTrue(response.hasExtra(ConnectProfileConstants.PARAM_ENABLE));
        assertEquals(true, response.getBooleanExtra(ConnectProfileConstants.PARAM_ENABLE, false));
    }

    /**
     * WiFi機能有効化テストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Profile: connect
     * Interface: なし
     * Attribute: wifi
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testPutWifi() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, ConnectProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, ConnectProfileConstants.ATTRIBUTE_WIFI);
        Intent response = sendRequest(request);
        assertResultOK(response);
    }

    /**
     * WiFi機能無効化テストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Profile: connect
     * Interface: なし
     * Attribute: wifi
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testDeleteWifi() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_DELETE);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, ConnectProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, ConnectProfileConstants.ATTRIBUTE_WIFI);
        Intent response = sendRequest(request);
        assertResultOK(response);
    }

    /**
     * WiFi機能有効状態変化イベントのコールバック登録テストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Profile: connect
     * Interface: なし
     * Attribute: onwifichange
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・コールバック登録後にイベントが通知されること。
     * </pre>
     */
    public void testPutOnWifiChange() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_SESSION_KEY, getClientId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, ConnectProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, ConnectProfileConstants.ATTRIBUTE_ON_WIFI_CHANGE);
        Intent response = sendRequest(request);
        assertResultOK(response);
        Intent event = waitForEvent();
        assertTrue(event.hasExtra(ConnectProfileConstants.PARAM_CONNECT_STATUS));
        Bundle connectStatus = event.getBundleExtra(ConnectProfileConstants.PARAM_CONNECT_STATUS);
        assertTrue(connectStatus.containsKey(ConnectProfileConstants.PARAM_ENABLE));
        assertEquals(true, connectStatus.getBoolean(ConnectProfileConstants.PARAM_ENABLE));
    }

    /**
     * WiFi機能有効状態変化イベントのコールバック解除テストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: DELETE
     * Profile: connect
     * Interface: なし
     * Attribute: onwifichange
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testDeleteOnWifiChange() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_DELETE);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_SESSION_KEY, TEST_SESSION_KEY);
        request.putExtra(DConnectMessage.EXTRA_PROFILE, ConnectProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, ConnectProfileConstants.ATTRIBUTE_ON_WIFI_CHANGE);
        Intent response = sendRequest(request);
        assertResultOK(response);
    }

    /**
     * Bluetooth機能有効状態(ON/OFF)取得テストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: GET
     * Profile: connect
     * Interface: なし
     * Attribute: bluetooth
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testGetBluetooth() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_GET);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, ConnectProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, ConnectProfileConstants.ATTRIBUTE_BLUETOOTH);
        Intent response = sendRequest(request);
        assertResultOK(response);
        assertTrue(response.hasExtra(ConnectProfileConstants.PARAM_ENABLE));
        assertEquals(true, response.getBooleanExtra(ConnectProfileConstants.PARAM_ENABLE, false));
    }

    /**
     * Bluetooth機能有効化テストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Profile: connect
     * Interface: なし
     * Attribute: bluetooth
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testPutBluetooth() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, ConnectProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, ConnectProfileConstants.ATTRIBUTE_BLUETOOTH);
        Intent response = sendRequest(request);
        assertResultOK(response);
    }

    /**
     * Bluetooth機能無効化テストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: DELETE
     * Profile: connect
     * Interface: なし
     * Attribute: bluetooth
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testDeleteBluetooth() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_DELETE);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, ConnectProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, ConnectProfileConstants.ATTRIBUTE_BLUETOOTH);
        Intent response = sendRequest(request);
        assertResultOK(response);
    }

    /**
     * Bluetooth機能有効状態変化イベントのコールバック登録テストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Profile: connect
     * Interface: なし
     * Attribute: onbluetoothchange
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testPutOnBluetoothChange() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_SESSION_KEY, getClientId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, ConnectProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, ConnectProfileConstants.ATTRIBUTE_ON_BLUETOOTH_CHANGE);
        Intent response = sendRequest(request);
        assertResultOK(response);
        Intent event = waitForEvent();
        assertTrue(event.hasExtra(ConnectProfileConstants.PARAM_CONNECT_STATUS));
        Bundle connectStatus = event.getBundleExtra(ConnectProfileConstants.PARAM_CONNECT_STATUS);
        assertTrue(connectStatus.containsKey(ConnectProfileConstants.PARAM_ENABLE));
        assertEquals(true, connectStatus.getBoolean(ConnectProfileConstants.PARAM_ENABLE));
    }

    /**
     * Bluetooth機能有効状態変化イベントのコールバック解除テストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: DELETE
     * Profile: connect
     * Interface: なし
     * Attribute: onbluetoothchange
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testDeleteOnBluetoothChange() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_DELETE);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_SESSION_KEY, getClientId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, ConnectProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, ConnectProfileConstants.ATTRIBUTE_ON_BLUETOOTH_CHANGE);
        Intent response = sendRequest(request);
        assertResultOK(response);
    }

    /**
     * Bluetooth検索可能状態を有効にするテストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Profile: connect
     * Interface: bluetooth
     * Attribute: discoverable
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testPutBluetoothDiscoverable() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, ConnectProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_INTERFACE, ConnectProfileConstants.INTERFACE_BLUETOOTH);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, ConnectProfileConstants.ATTRIBUTE_DISCOVERABLE);
        Intent response = sendRequest(request);
        assertResultOK(response);
    }

    /**
     * Bluetooth検索可能状態を無効にするテストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: DELETE
     * Profile: connect
     * Interface: bluetooth
     * Attribute: discoverable
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testDeleteBluetoothDiscoverable() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_DELETE);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, ConnectProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_INTERFACE, ConnectProfileConstants.INTERFACE_BLUETOOTH);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, ConnectProfileConstants.ATTRIBUTE_DISCOVERABLE);
        Intent response = sendRequest(request);
        assertResultOK(response);
    }

    /**
     * NFC機能有効状態(ON/OFF)取得テストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: GET
     * Profile: connect
     * Interface: なし
     * Attribute: nfc
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testGetNFC() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_GET);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, ConnectProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, ConnectProfileConstants.ATTRIBUTE_NFC);
        Intent response = sendRequest(request);
        assertResultOK(response);
        assertTrue(response.hasExtra(ConnectProfileConstants.PARAM_ENABLE));
        assertEquals(true, response.getBooleanExtra(ConnectProfileConstants.PARAM_ENABLE, false));
    }

    /**
     * NFC機能有効化テストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Profile: connect
     * Interface: なし
     * Attribute: nfc
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testPutNFC() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, ConnectProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, ConnectProfileConstants.ATTRIBUTE_NFC);
        Intent response = sendRequest(request);
        assertResultOK(response);
    }

    /**
     * NFC機能無効化テストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: DELETE
     * Profile: connect
     * Interface: なし
     * Attribute: nfc
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testDeleteNFC() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_DELETE);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, ConnectProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, ConnectProfileConstants.ATTRIBUTE_NFC);
        Intent response = sendRequest(request);
        assertResultOK(response);
    }

    /**
     * NFC機能有効状態変化イベントのコールバック登録テストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Profile: connect
     * Interface: なし
     * Attribute: onnfcchange
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testPutOnNFCChange() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_SESSION_KEY, getClientId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, ConnectProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, ConnectProfileConstants.ATTRIBUTE_ON_NFC_CHANGE);
        Intent response = sendRequest(request);
        assertResultOK(response);
        Intent event = waitForEvent();
        assertTrue(event.hasExtra(ConnectProfileConstants.PARAM_CONNECT_STATUS));
        Bundle connectStatus = event.getBundleExtra(ConnectProfileConstants.PARAM_CONNECT_STATUS);
        assertTrue(connectStatus.containsKey(ConnectProfileConstants.PARAM_ENABLE));
        assertEquals(true, connectStatus.getBoolean(ConnectProfileConstants.PARAM_ENABLE));
    }

    /**
     * NFC機能有効状態変化イベントのコールバック解除テストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: DELETE
     * Profile: connect
     * Interface: なし
     * Attribute: onnfcchange
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testDeleteOnNFCChange() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_DELETE);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_SESSION_KEY, getClientId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, ConnectProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, ConnectProfileConstants.ATTRIBUTE_ON_NFC_CHANGE);
        Intent response = sendRequest(request);
        assertResultOK(response);
    }

    /**
     * BLE機能有効状態(ON/OFF)取得テストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: GET
     * Profile: connect
     * Interface: なし
     * Attribute: ble
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testGetBLE() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_GET);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, ConnectProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, ConnectProfileConstants.ATTRIBUTE_BLE);
        Intent response = sendRequest(request);
        assertResultOK(response);
        assertTrue(response.hasExtra(ConnectProfileConstants.PARAM_ENABLE));
        assertEquals(true, response.getBooleanExtra(ConnectProfileConstants.PARAM_ENABLE, false));
    }

    /**
     * BLE機能有効化テストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Profile: connect
     * Interface: なし
     * Attribute: ble
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testPutBLE() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, ConnectProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, ConnectProfileConstants.ATTRIBUTE_BLE);
        Intent response = sendRequest(request);
        assertResultOK(response);
    }

    /**
     * BLE機能無効化テストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: DELETE
     * Profile: connect
     * Interface: なし
     * Attribute: ble
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testDeleteBLE() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_DELETE);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, ConnectProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, ConnectProfileConstants.ATTRIBUTE_BLE);
        Intent response = sendRequest(request);
        assertResultOK(response);
    }

    /**
     * BLE機能有効状態変化イベントのコールバック登録テストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Profile: connect
     * Interface: なし
     * Attribute: onblechange
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testPutOnBLEChange() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_SESSION_KEY, getClientId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, ConnectProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, ConnectProfileConstants.ATTRIBUTE_ON_BLE_CHANGE);
        Intent response = sendRequest(request);
        assertResultOK(response);
        Intent event = waitForEvent();
        assertTrue(event.hasExtra(ConnectProfileConstants.PARAM_CONNECT_STATUS));
        Bundle connectStatus = event.getBundleExtra(ConnectProfileConstants.PARAM_CONNECT_STATUS);
        assertTrue(connectStatus.containsKey(ConnectProfileConstants.PARAM_ENABLE));
        assertEquals(true, connectStatus.getBoolean(ConnectProfileConstants.PARAM_ENABLE));
    }

    /**
     * BLE機能有効状態変化イベントのコールバック解除テストを行う.
     * 
     * <pre>
     * 【Intent通信】
     * Action: DELETE
     * Profile: connect
     * Interface: なし
     * Attribute: onblechange
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testDeleteOnBLEChange() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_DELETE);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_SESSION_KEY, getClientId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, ConnectProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, ConnectProfileConstants.ATTRIBUTE_ON_BLE_CHANGE);
        Intent response = sendRequest(request);
        assertResultOK(response);
    }

}
