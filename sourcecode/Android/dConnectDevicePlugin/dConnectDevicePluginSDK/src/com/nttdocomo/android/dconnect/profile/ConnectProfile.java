/*
 ConnectProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.profile;

import android.content.Intent;
import android.os.Bundle;

import com.nttdocomo.android.dconnect.message.MessageUtils;
import com.nttdocomo.dconnect.profile.ConnectProfileConstants;

/**
 * Connect プロファイル.
 * 
 * <p>
 * スマートデバイスとのネットワーク接続情報を提供するAPI.<br/>
 * ネットワーク接続情報を提供するデバイスプラグインは当クラスを継承し、対応APIを実装すること。 <br/>
 * AndroidManifest.xmlに追加する必要の有るパーミッション： wifi: ACCESS_WIFI_STATE,
 * CHANGE_WIFI_STATE bluetooth: BLUETOOTH, BLUETOOTH_ADMIN nfc: NFC
 * </p>
 * 
 * <h1>各API提供メソッド</h1>
 * <p>
 * Connect Profile の各APIへのリクエストに対し、以下のコールバックメソッド群が自動的に呼び出される。<br/>
 * サブクラスは以下のメソッド群からデバイスプラグインが提供するAPI用のメソッドをオーバーライドし、機能を実装すること。<br/>
 * オーバーライドされていない機能は自動的に非対応APIとしてレスポンスを返す。
 * </p>
 * <ul>
 * <li>WiFi Connect API [GET] :
 * {@link ConnectProfile#onGetWifi(Intent, Intent, String)}</li>
 * <li>WiFi Connect API [PUT] :
 * {@link ConnectProfile#onPutWifi(Intent, Intent, String)}</li>
 * <li>WiFi Connect API [DELETE] :
 * {@link ConnectProfile#onDeleteWifi(Intent, Intent, String)}</li>
 * <li>WiFi Connect Status Change Event API [Register] :
 * {@link ConnectProfile#onPutOnWifiChange(Intent, Intent, String, String)}</li>
 * <li>WiFi Connect Status Change Event API [Unregister] :
 * {@link ConnectProfile#onDeleteOnWifiChange(Intent, Intent, String, String)}</li>
 * <li>Bluetooth Connect API [GET] :
 * {@link ConnectProfile#onGetBluetooth(Intent, Intent, String)}</li>
 * <li>Bluetooth Connect API [PUT] :
 * {@link ConnectProfile#onPutBluetooth(Intent, Intent, String)}</li>
 * <li>Bluetooth Connect API [DELETE] :
 * {@link ConnectProfile#onDeleteBluetooth(Intent, Intent, String)}</li>
 * <li>Bluetooth Connect Status Change Event API [Register] :
 * {@link ConnectProfile#onPutOnBluetoothChange(Intent, Intent, String, String)
 * )}</li>
 * <li>Bluetooth Connect Status Change Event API [Unregister] :
 * {@link ConnectProfile#onDeleteOnBluetoothChange(Intent, Intent, String, String)
 * )}</li>
 * <li>Bluetooth Discoverable Status API [PUT] :
 * {@link ConnectProfile#onPutBluetoothDiscoverable(Intent, Intent, String)}</li>
 * <li>Bluetooth Discoverable Status API [DELETE] :
 * {@link ConnectProfile#onDeleteBluetoothDiscoverable(Intent, Intent, String)}</li>
 * <li>Bluetooth Discoverable Status API [DELETE] :
 * {@link ConnectProfile#onDeleteBluetoothDiscoverable(Intent, Intent, String)}</li>
 * <li>NFC Connect API [GET] :
 * {@link ConnectProfile#onGetNFC(Intent, Intent, String)}</li>
 * <li>NFC Connect API [PUT] :
 * {@link ConnectProfile#onPutNFC(Intent, Intent, String)}</li>
 * <li>NFC Connect API [DELETE] :
 * {@link ConnectProfile#onDeleteNFC(Intent, Intent, String)}</li>
 * <li>NFC Connect Status Change Event API [Register] :
 * {@link ConnectProfile#onPutOnNFCChange(Intent, Intent, String, String)}</li>
 * <li>NFC Connect Status Change Event API [Unregister] :
 * {@link ConnectProfile#onDeleteOnNFCChange(Intent, Intent, String, String)}</li>
 * <li>BLE Connect API [GET] :
 * {@link ConnectProfile#onGetBLE(Intent, Intent, String)}</li>
 * <li>BLE Connect API [PUT] :
 * {@link ConnectProfile#onPutBLE(Intent, Intent, String)}</li>
 * <li>BLE Connect API [DELETE] :
 * {@link ConnectProfile#onDeleteBLE(Intent, Intent, String)}</li>
 * <li>BLE Connect Status Change Event API [Register] :
 * {@link ConnectProfile#onPutOnBLEChange(Intent, Intent, String, String)}</li>
 * <li>BLE Connect Status Change Event API [Unregister] :
 * {@link ConnectProfile#onDeleteOnBLEChange(Intent, Intent, String, String)}</li>
 * </ul>
 * 
 * @author NTT DOCOMO, INC.
 */
public abstract class ConnectProfile extends DConnectProfile implements ConnectProfileConstants {

    @Override
    public final String getProfileName() {
        return PROFILE_NAME;
    }

    @Override
    protected boolean onGetRequest(final Intent request, final Intent response) {
        String attribute = getAttribute(request);
        boolean result = true;

        if (attribute == null) {
            setUnsupportedError(response);
        } else {
            String deviceId = getDeviceID(request);
            if (attribute.equals(ATTRIBUTE_WIFI)) {
                result = onGetWifi(request, response, deviceId);
            } else if (attribute.equals(ATTRIBUTE_BLUETOOTH)) {
                result = onGetBluetooth(request, response, deviceId);
            } else if (attribute.equals(ATTRIBUTE_NFC)) {
                result = onGetNFC(request, response, deviceId);
            } else if (attribute.equals(ATTRIBUTE_BLE)) {
                result = onGetBLE(request, response, deviceId);
            } else {
                MessageUtils.setUnknownAttributeError(response);
            }
        }

        return result;
    }

    @Override
    protected boolean onPutRequest(final Intent request, final Intent response) {
        boolean result = true;

        String inter = getInterface(request);
        String attribute = getAttribute(request);

        if (inter == null) {
            if (attribute == null) {
                MessageUtils.setUnknownAttributeError(response);
            } else {
                String deviceId = getDeviceID(request);
                String sessionKey = getSessionKey(request);
                if (attribute.equals(ATTRIBUTE_WIFI)) {
                    result = onPutWifi(request, response, deviceId);
                } else if (attribute.equals(ATTRIBUTE_ON_WIFI_CHANGE)) {
                    result = onPutOnWifiChange(request, response, deviceId, sessionKey);
                } else if (attribute.equals(ATTRIBUTE_BLUETOOTH)) {
                    result = onPutBluetooth(request, response, deviceId);
                } else if (attribute.equals(ATTRIBUTE_ON_BLUETOOTH_CHANGE)) {
                    result = onPutOnBluetoothChange(request, response, deviceId, sessionKey);
                } else if (attribute.equals(ATTRIBUTE_NFC)) {
                    result = onPutNFC(request, response, deviceId);
                } else if (attribute.equals(ATTRIBUTE_ON_NFC_CHANGE)) {
                    result = onPutOnNFCChange(request, response, deviceId, sessionKey);
                } else if (attribute.equals(ATTRIBUTE_BLE)) {
                    result = onPutBLE(request, response, deviceId);
                } else if (attribute.equals(ATTRIBUTE_ON_BLE_CHANGE)) {
                    result = onPutOnBLEChange(request, response, deviceId, sessionKey);
                } else {
                    MessageUtils.setUnknownAttributeError(response);
                }
            }
        } else if (inter.equals(INTERFACE_BLUETOOTH) && attribute.equals(ATTRIBUTE_DISCOVERABLE)) {
            result = onPutBluetoothDiscoverable(request, response, getDeviceID(request));
        } else {
            MessageUtils.setUnknownAttributeError(response);
        }

        return result;
    }

    @Override
    protected boolean onDeleteRequest(final Intent request, final Intent response) {
        boolean result = true;

        String inter = getInterface(request);
        String attribute = getAttribute(request);

        if (inter == null) {
            if (attribute == null) {
                MessageUtils.setUnknownAttributeError(response);
            } else {

                String deviceId = getDeviceID(request);
                String sessionKey = getSessionKey(request);

                if (attribute.equals(ATTRIBUTE_WIFI)) {
                    result = onDeleteWifi(request, response, deviceId);
                } else if (attribute.equals(ATTRIBUTE_ON_WIFI_CHANGE)) {
                    result = onDeleteOnWifiChange(request, response, deviceId, sessionKey);
                } else if (attribute.equals(ATTRIBUTE_BLUETOOTH)) {
                    result = onDeleteBluetooth(request, response, deviceId);
                } else if (attribute.equals(ATTRIBUTE_ON_BLUETOOTH_CHANGE)) {
                    result = onDeleteOnBluetoothChange(request, response, deviceId, sessionKey);
                } else if (attribute.equals(ATTRIBUTE_NFC)) {
                    result = onDeleteNFC(request, response, deviceId);
                } else if (attribute.equals(ATTRIBUTE_ON_NFC_CHANGE)) {
                    result = onDeleteOnNFCChange(request, response, deviceId, sessionKey);
                } else if (attribute.equals(ATTRIBUTE_BLE)) {
                    result = onDeleteBLE(request, response, deviceId);
                } else if (attribute.equals(ATTRIBUTE_ON_BLE_CHANGE)) {
                    result = onDeleteOnBLEChange(request, response, deviceId, sessionKey);
                } else {
                    MessageUtils.setUnknownAttributeError(response);
                }
            }
        } else if (inter.equals(INTERFACE_BLUETOOTH) && attribute.equals(ATTRIBUTE_DISCOVERABLE)) {
            result = onDeleteBluetoothDiscoverable(request, response, getDeviceID(request));
        } else {
            MessageUtils.setUnknownAttributeError(response);
        }

        return result;
    }

    // ------------------------------------
    // GET
    // ------------------------------------

    /**
     * WIFI機能有効状態取得リクエストハンドラー.<br/>
     * デバイスのWIFI機能有効状態を提供し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onGetWifi(final Intent request, final Intent response, final String deviceId) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * Bluetooth機能有効状態取得リクエストハンドラー.<br/>
     * デバイスのBluetooth機能有効状態を提供し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onGetBluetooth(final Intent request, final Intent response, final String deviceId) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * NFC機能有効状態取得リクエストハンドラー.<br/>
     * デバイスのNFC機能有効状態を提供し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onGetNFC(final Intent request, final Intent response, final String deviceId) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * BLE機能有効状態取得リクエストハンドラー.<br/>
     * デバイスのBLE機能有効状態を提供し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onGetBLE(final Intent request, final Intent response, final String deviceId) {
        setUnsupportedError(response);
        return true;
    }

    // ------------------------------------
    // PUT
    // ------------------------------------

    /**
     * WIFI機能有効設定リクエストハンドラー.<br/>
     * デバイスのWIFI機能を有効にし、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onPutWifi(final Intent request, final Intent response, final String deviceId) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * onwifichangeコールバック登録リクエストハンドラー.<br/>
     * onwifichangeコールバックを登録し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param sessionKey セッションキー
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onPutOnWifiChange(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * Bluetooth機能有効設定リクエストハンドラー.<br/>
     * デバイスのBluetooth機能を有効にし、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onPutBluetooth(final Intent request, final Intent response, final String deviceId) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * onbluetoothchangeコールバック登録リクエストハンドラー.<br/>
     * onbluetoothchangeコールバックを登録し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param sessionKey セッションキー
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onPutOnBluetoothChange(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * Bluetooth検索可能状態有効設定リクエストハンドラー.<br/>
     * デバイスのBluetooth検索可能状態を有効にし、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onPutBluetoothDiscoverable(final Intent request, final Intent response, final String deviceId) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * NFC機能有効設定リクエストハンドラー.<br/>
     * デバイスのNFC機能を有効にし、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onPutNFC(final Intent request, final Intent response, final String deviceId) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * onnfcchangeコールバック登録リクエストハンドラー.<br/>
     * onnfcchangeコールバックを登録し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param sessionKey セッションキー
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onPutOnNFCChange(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * BLE機能有効設定リクエストハンドラー.<br/>
     * デバイスのBLE機能を有効にし、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onPutBLE(final Intent request, final Intent response, final String deviceId) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * onblechangeコールバック登録リクエストハンドラー.<br/>
     * onblechangeコールバックを登録し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param sessionKey セッションキー
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onPutOnBLEChange(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {
        setUnsupportedError(response);
        return true;
    }

    // ------------------------------------
    // DELETE
    // ------------------------------------

    /**
     * WIFI機能無効設定リクエストハンドラー.<br/>
     * デバイスのWIFI機能を無効にし、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onDeleteWifi(final Intent request, final Intent response, final String deviceId) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * onwifichangeコールバック解除リクエストハンドラー.<br/>
     * onwifichangeコールバックを解除し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param sessionKey セッションキー
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onDeleteOnWifiChange(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * Bluetooth機能無効設定リクエストハンドラー.<br/>
     * デバイスのBluetooth機能を無効にし、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onDeleteBluetooth(final Intent request, final Intent response, final String deviceId) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * onbluetoothchangeコールバック解除リクエストハンドラー.<br/>
     * onbluetoothchangeコールバックを解除し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param sessionKey セッションキー
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onDeleteOnBluetoothChange(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * Bluetooth検索可能状態無効設定リクエストハンドラー.<br/>
     * デバイスのBluetooth検索可能状態を無効にし、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onDeleteBluetoothDiscoverable(final Intent request, final Intent response, 
            final String deviceId) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * NFC機能無効設定リクエストハンドラー.<br/>
     * デバイスのNFC機能を無効にし、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onDeleteNFC(final Intent request, final Intent response, final String deviceId) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * onnfcchangeコールバック解除リクエストハンドラー.<br/>
     * onnfcchangeコールバックを解除し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param sessionKey セッションキー
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onDeleteOnNFCChange(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * BLE機能無効設定リクエストハンドラー.<br/>
     * デバイスのBLE機能を無効にし、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onDeleteBLE(final Intent request, final Intent response, final String deviceId) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * onblechangeコールバック解除リクエストハンドラー.<br/>
     * onblechangeコールバックを解除し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param sessionKey セッションキー
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onDeleteOnBLEChange(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {
        setUnsupportedError(response);
        return true;
    }

    // ------------------------------------
    // セッターメソッド群
    // ------------------------------------

    /**
     * レスポンスに接続状態フラグを設定する.
     * 
     * @param response レスポンスパラメータ
     * @param enable ON:true、OFF:false
     */
    public static void setEnable(final Intent response, final boolean enable) {
        response.putExtra(PARAM_ENABLE, enable);
    }

    /**
     * メッセージに接続状態を設定する.
     * 
     * @param message メッセージパラメータ
     * @param connectStatus 接続状態パラメータ
     */
    public static void setConnectStatus(final Intent message, final Bundle connectStatus) {
        message.putExtra(PARAM_CONNECT_STATUS, connectStatus);
    }

    /**
     * 接続状態パラメータに接続状態フラグを設定する.
     * 
     * @param connectStatus 接続状態パラメータ
     * @param enable ON: true、OFF: false
     */
    public static void setEnable(final Bundle connectStatus, final boolean enable) {
        connectStatus.putBoolean(PARAM_ENABLE, enable);
    }
}
