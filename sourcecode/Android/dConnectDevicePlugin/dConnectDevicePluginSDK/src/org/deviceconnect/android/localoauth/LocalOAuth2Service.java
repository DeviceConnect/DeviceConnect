/*
 LocalOAuth2Service.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.localoauth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * LocalOAuth2Service.
 * 
 * @author NTT DOCOMO, INC.
 */
public class LocalOAuth2Service extends Service {
    @Override
    public IBinder onBind(final Intent intent) {
        return LocalOAuth2Main.onBind(intent);
    }

    @Override
    public boolean onUnbind(final Intent intent) {
        LocalOAuth2Main.onUnbind();
        return super.onUnbind(intent);
    }
}
