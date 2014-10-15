/*
 DeviceTestApplication.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.test;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.deviceconnect.android.logger.AndroidHandler;

import org.deviceconnect.android.deviceplugin.test.BuildConfig;

import android.app.Application;

/**
 * テスト用デバイスプラグインアプリケーション.
 * @author NTT DOCOMO, INC.
 */
public class DeviceTestApplication extends Application {

    /** ロガー. */
    private Logger mLogger = Logger.getLogger("dconnect.dplugin.test");

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            AndroidHandler handler = new AndroidHandler("dconnect.dplugin.test");
            handler.setFormatter(new SimpleFormatter());
            handler.setLevel(Level.ALL);
            mLogger.addHandler(handler);
            mLogger.setLevel(Level.ALL);
        } else {
            mLogger.setLevel(Level.OFF);
        }
    }

}
