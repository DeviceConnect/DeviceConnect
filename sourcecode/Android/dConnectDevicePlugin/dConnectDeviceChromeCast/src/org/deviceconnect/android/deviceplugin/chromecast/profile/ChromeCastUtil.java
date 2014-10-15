/*
 ChromeCastUtil.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.chromecast.profile;

import android.content.Context;

public final class ChromeCastUtil {
    static Context context;
    
    private ChromeCastUtil(final Context context) {
        ChromeCastUtil.context = context;
    }
}
