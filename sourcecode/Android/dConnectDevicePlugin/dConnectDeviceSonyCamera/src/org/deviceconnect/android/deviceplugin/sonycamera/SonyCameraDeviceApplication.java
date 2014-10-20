/*
SonyCameraDeviceApplication
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.sonycamera;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.deviceconnect.android.logger.AndroidHandler;

import android.app.Application;

/**
 * SonyCameraデバイスプラグイン.
 * @author NTT DOCOMO, INC.
 */
public class SonyCameraDeviceApplication extends Application {
    /** ロガー. */
    private Logger mLogger = Logger.getLogger("sonycamera.dplugin");

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            AndroidHandler handler = new AndroidHandler("sonycamera.dplugin");
            handler.setFormatter(new SimpleFormatter());
            handler.setLevel(Level.ALL);
            mLogger.addHandler(handler);
            mLogger.setLevel(Level.ALL);
        } else {
            mLogger.setLevel(Level.OFF);
        }
    }

}
