/*
 ConnectProfileConstants.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.profile;

/**
 * Connect Profile API 定数群.<br/>
 * Connect Profile API のパラメータ名、インタフェース名、属性名、プロファイル名を定義する。
 * 
 * @author NTT DOCOMO, INC.
 */
public interface ConnectProfileConstants extends DConnectProfileConstants {

    /**
     * プロファイル名: {@value} .
     */
    String PROFILE_NAME = "connect";

    /**
     * 属性: {@value} .
     */
    String ATTRIBUTE_WIFI = "wifi";

    /**
     * 属性: {@value} .
     */
    String ATTRIBUTE_ON_WIFI_CHANGE = "onwifichange";

    /**
     * 属性: {@value} .
     */
    String ATTRIBUTE_BLUETOOTH = "bluetooth";

    /**
     * インターフェース: {@value} .
     */
    String INTERFACE_BLUETOOTH = "bluetooth";

    /**
     * 属性: {@value} .
     */
    String ATTRIBUTE_ON_BLUETOOTH_CHANGE = "onbluetoothchange";

    /**
     * 属性: {@value} .
     */
    String ATTRIBUTE_DISCOVERABLE = "discoverable";

    /**
     * 属性: {@value} .
     */
    String ATTRIBUTE_BLE = "ble";

    /**
     * 属性: {@value} .
     */
    String ATTRIBUTE_ON_BLE_CHANGE = "onblechange";

    /**
     * 属性: {@value} .
     */
    String ATTRIBUTE_NFC = "nfc";

    /**
     * 属性: {@value} .
     */
    String ATTRIBUTE_ON_NFC_CHANGE = "onnfcchange";

    /**
     * パス: {@value}.
     */
    String PATH_PROFILE = PATH_ROOT + SEPARATOR + PROFILE_NAME;

    /**
     * パス: {@value}.
     */
    String PATH_WIFI = PATH_PROFILE + SEPARATOR + ATTRIBUTE_WIFI;

    /**
     * パス: {@value}.
     */
    String PATH_ON_WIFI_CHANGE = PATH_PROFILE + SEPARATOR + ATTRIBUTE_ON_WIFI_CHANGE;
    
    /**
     * パス: {@value}.
     */
    String PATH_BLUETOOTH = PATH_PROFILE + SEPARATOR + ATTRIBUTE_BLUETOOTH;

    /**
     * パス: {@value}.
     */
    String PATH_BLUETOOTH_CHANGE = PATH_PROFILE + SEPARATOR + ATTRIBUTE_ON_BLUETOOTH_CHANGE;

    /**
     * パス: {@value}.
     */
    String PATH_DISCOVERABLE = PATH_PROFILE + SEPARATOR + INTERFACE_BLUETOOTH + SEPARATOR + ATTRIBUTE_DISCOVERABLE;

    /**
     * パス: {@value}.
     */
    String PATH_BLE = PATH_PROFILE + SEPARATOR + ATTRIBUTE_BLE;

    /**
     * パス: {@value}.
     */
    String PATH_ON_BLE_CHANGE = PATH_PROFILE + SEPARATOR + ATTRIBUTE_ON_BLE_CHANGE;

    /**
     * パス: {@value}.
     */
    String PATH_NFC = PATH_PROFILE + SEPARATOR + ATTRIBUTE_NFC;

    /**
     * パス: {@value}.
     */
    String PATH_ON_NFC_CHANGE = PATH_PROFILE + SEPARATOR + ATTRIBUTE_ON_NFC_CHANGE;

    /**
     * パラメータ: {@value} .
     */
    String PARAM_ENABLE = "enable";

    /**
     * パラメータ: {@value} .
     */
    String PARAM_CONNECT_STATUS = "connectStatus";
}
