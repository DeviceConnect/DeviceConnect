/*
 TestBatteryProfileConstants.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.test.plugin.profile;


/**
 * JUnit用テストデバイスプラグイン、Batteryプロファイル.
 * @author NTT DOCOMO, INC.
 */
public interface TestBatteryProfileConstants {

    /**
     * バッテリー充電時間を定義する.
     */
    double CHARGING_TIME = 50000;

    /**
     * バッテリー放電時間を定義する.
     */
    double DISCHARGING_TIME = 10000;

    /**
     * バッテリーレベルを定義する.
     */
    double LEVEL = 0.5;

    /**
     * バッテリー充電フラグを定義する.
     */
    boolean CHARGING = false;

}
