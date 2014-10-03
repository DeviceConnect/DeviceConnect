/*
 LocalOAuth2Service.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.localoauth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * LocalOAuth2Service.
 * 
 * @author NTT DOCOMO, INC.
 */
public class LocalOAuth2Service extends Service {

    /**
     * ActivityからstartBind()が実行されたときに、この処理が実行されてBindされる.
     * 
     * @param intent Intent
     * @return Binder
     */
    @Override
    public IBinder onBind(final Intent intent) {
        return LocalOAuth2Main.onBind(intent);
    }
}
