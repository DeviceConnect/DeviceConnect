package com.nttdocomo.android.dconnect.deviceplugin.sw;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import android.app.Application;

import com.nttdocomo.android.dconnect.logger.AndroidHandler;

/**
SWApplication
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

/**
 * SonyWatchDevicePlugin_LoggerLevelSetting.
 */
public class SWApplication extends Application {

    /** ロガー. */
    private Logger mLogger = Logger.getLogger(SWConstants.LOGGER_NAME);

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            AndroidHandler handler = new AndroidHandler(SWConstants.LOGGER_NAME);
            handler.setFormatter(new SimpleFormatter());
            handler.setLevel(Level.ALL);
            mLogger.addHandler(handler);
            mLogger.setLevel(Level.ALL);
        } else {
            mLogger.setLevel(Level.OFF);
        }
    }

}
