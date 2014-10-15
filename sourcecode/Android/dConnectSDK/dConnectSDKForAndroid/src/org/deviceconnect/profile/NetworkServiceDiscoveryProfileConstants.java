/*
 NetworkServiceDiscoveryProfileConstants.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.profile;

/**
 * Network Service Discovery Profile API 定数群.<br/>
 * Network Service Discovery Profile API のパラメータ名、インタフェース名、属性名、プロファイル名を定義する。
 * @author NTT DOCOMO, INC.
 */
public interface NetworkServiceDiscoveryProfileConstants extends DConnectProfileConstants {

    /**
     * プロファイル名: {@value} .
     */
    String PROFILE_NAME = "network_service_discovery";

    /**
     * 属性: {@value} .
     */
    String ATTRIBUTE_GET_NETWORK_SERVICES = "getnetworkservices";

    /**
     * 属性: {@value} .
     */
    String ATTRIBUTE_ON_SERVICE_CHANGE = "onservicechange";

    /**
     * パス: {@value}.
     */
    String PATH_PROFILE = PATH_ROOT + SEPARATOR + PROFILE_NAME;

    /**
     * パス: {@value} .
     */
    String PATH_GET_NETWORK_SERVICES = PATH_PROFILE + SEPARATOR + ATTRIBUTE_GET_NETWORK_SERVICES;

    /**
     * パス: {@value} .
     */
    String PATH_ON_SERVICE_CHANGE = PATH_PROFILE + SEPARATOR + ATTRIBUTE_ON_SERVICE_CHANGE;

    /**
     * パラメータ: {@value} .
     */
    String PARAM_NETWORK_SERVICE = "networkService";

    /**
     * パラメータ: {@value} .
     */
    String PARAM_SERVICES = "services";

    /**
     * パラメータ: {@value} .
     */
    String PARAM_STATE = "state";
    
    /**
     * パラメータ: {@value} .
     */
    String PARAM_ID = "id";

    /**
     * パラメータ: {@value} .
     */
    String PARAM_NAME = "name";

    /**
     * パラメータ: {@value} .
     */
    String PARAM_TYPE = "type";

    /**
     * パラメータ: {@value} .
     */
    String PARAM_ONLINE = "online";

    /**
     * パラメータ: {@value} .
     */
    String PARAM_CONFIG = "config";

    /**
     * ネットワークタイプ.
     */
    enum NetworkType {
        /** 未定義値. */
        UNKNOWN("Unknown"),
        /** WiFi. */
        WIFI("WiFi"),
        /** BLE. */
        BLE("BLE"),
        /** NFC. */
        NFC("NFC"),
        /** Bluetooth. */
        BLUETOOTH("Bluetooth");

        /**
         * 定義値.
         */
        String mValue;

        /**
         * 指定された文字列を定義値に持つ定数を定義する.
         * 
         * @param value 定義値
         */
        private NetworkType(final String value) {
            this.mValue = value;
        }

        /**
         * 定義値を取得する.
         * 
         * @return 定義値
         */
        public String getValue() {
            return mValue;
        }

        /**
         * 定義値からネットワークタイプを取得する.
         * 
         * @param value 定義値
         * @return ネットワークタイプ
         */
        public static NetworkType getInstance(final String value) {
            for (NetworkType type : values()) {
                if (type.getValue().equals(value)) {
                    return type;
                }
            }
            return UNKNOWN;
        }
    }

}
