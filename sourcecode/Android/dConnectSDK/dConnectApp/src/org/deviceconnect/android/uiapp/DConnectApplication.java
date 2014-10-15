/*
 DConnectApplication.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.uiapp;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.deviceconnect.android.logger.AndroidHandler;

import android.app.Application;

/**
 * Bluetooth Device Application.
 */
public class DConnectApplication extends Application {

    /**
     * ロガー.
     */
    private Logger logger = Logger.getLogger("deviceconnect");

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            AndroidHandler handler = new AndroidHandler("deviceconnect.uiapp");
            handler.setFormatter(new SimpleFormatter());
            handler.setLevel(Level.INFO);
            logger.addHandler(handler);
            logger.setLevel(Level.INFO);
        } else {
            logger.setLevel(Level.OFF);
        }
    }
}
