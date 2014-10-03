package com.nttdocomo.android.dconnect.uiapp;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import android.app.Application;

import com.nttdocomo.android.dconnect.logger.AndroidHandler;

/**
 * Bluetooth Device Application.
 */
public class DConnectApplication extends Application {

    /**
     * ロガー.
     */
    private Logger logger = Logger.getLogger("dconnect");

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            AndroidHandler handler = new AndroidHandler("dconnect.uiapp");
            handler.setFormatter(new SimpleFormatter());
            handler.setLevel(Level.INFO);
            logger.addHandler(handler);
            logger.setLevel(Level.INFO);
        } else {
            logger.setLevel(Level.OFF);
        }
    }
}
