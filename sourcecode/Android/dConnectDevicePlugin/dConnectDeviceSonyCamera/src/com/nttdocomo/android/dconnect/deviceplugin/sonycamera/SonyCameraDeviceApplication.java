package com.nttdocomo.android.dconnect.deviceplugin.sonycamera;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import android.app.Application;

import com.nttdocomo.android.dconnect.logger.AndroidHandler;

/**
SonyCameraDeviceApplication
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/
public class SonyCameraDeviceApplication extends Application {
    /** ロガー. */
    private Logger mLogger = Logger.getLogger("dconnect.dplugin");

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            AndroidHandler handler = new AndroidHandler("dconnect.dplugin");
            handler.setFormatter(new SimpleFormatter());
            handler.setLevel(Level.ALL);
            mLogger.addHandler(handler);
            mLogger.setLevel(Level.ALL);
        } else {
            mLogger.setLevel(Level.OFF);
        }
    }

}
