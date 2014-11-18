/*
 SWConstants.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.sw;

import android.graphics.Bitmap;

/**
 * 定数を定義するインターフェース.
 */
public interface SWConstants {
    /**
     * ログタグ.
     */
    String LOG_TAG = "SWDevicePlugin";
    /**
     * ロガーネーム.
     */
    String LOGGER_NAME = "dconnect.dplugin.sw";
    /**
     * スマートコネクトパッケージネーム.
     */
    String PACKAGE_SMART_CONNECT = "com.sonyericsson.extras.liveware";
    /**
     * スマートウォッチパッケージネーム.
     */
    String PACKAGE_SMART_WATCH = "com.sonyericsson.extras.smartwatch";
    /**
     * スマートウォッチ２パッケージネーム.
     */
    String PACKAGE_SMART_WATCH_2 = "com.sonymobile.smartconnect.smartwatch2";
    /**
     * スマートウォッチ必須アプリネーム.
     */
    String APP_NAME_SMART_WATCH = "SmartWatch アプリ";
    /**
     * スマートウォッチ2必須アプリネーム.
     */
    String APP_NAME_SMART_WATCH_2 = "SmartWatch 2 アプリ";
    /**
     * SWのデバイス名のプレフィックス.
     */
    String DEVICE_NAME_PREFIX = "SmartWatch";
    /**
     * SW1を示すBluetoothデバイス名.
     */
    String DEVICE_NAME_SMART_WATCH = "SmartWatch";
    /**
     * SW2を示すBluetoothデバイス名.
     */
    String DEVICE_NAME_SMART_WATCH_2 = "SmartWatch 2";
    /**
     * エクステンションキー.
     */
    String EXTENSION_KEY_PREF = "EXTENSION_KEY_PREF";
    /**
     * エクステンションID.
     */
    String EXTENSION_SPECIFIC_ID = "EXTENSION_SPECIFIC_ID_SW";
    /**
     * ターゲットモデル.
     */
    String EXTRA_SW_MODEL = "sw.model";
    /**
     * ターゲットモデル(不明).
     */
    int SW_MODEL_UNKNOWN = 0;
    /**
     * ターゲットモデル(SW1).
     */
    int SW_MODEL_SW1 = 1;
    /**
     * ターゲットモデル(SW2).
     */
    int SW_MODEL_SW2 = 2;
    /**
     * 設定画面のページ数.
     */
    int TUTORIAL_PAGE_NNMBER = 4;
    /**
     * デフォルトバイブレーション鳴動時間. 単位はミリ秒.
     */
    int DEFAULT_VIBRATION_TIME = 6000;
    /**
     * 最大バイブレーション鳴動時間. 単位はミリ秒.
     */
    int MAX_VIBRATION_TIME = 60000;
    /**
     * バッファサイズ.
     */
    int OUTPUTSTREAM_SIZE = 256;
    /**
     * 生成するBitmapのクオリティ.
     */
    int BITMAP_DECODE_QUALITY = 100;
    /**
     * 読み込む画像の階調の指定.
     */
    Bitmap.Config DEFAULT_BITMAP_CONFIG = Bitmap.Config.RGB_565;
    /**
     * 加速度取得用デフォルトインターバル. 単位はミリ秒.
     */
    long DEFAULT_SENSOR_INTERVAL = 1000;
}
