/*
 Utils.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.event.cache;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Locale;

/**
 * Cacheユーティリティクラス.
 * 
 *
 * @author NTT DOCOMO, INC.
 */
public final class Utils {

    /**
     * ユーティリティなのでprivate.
     */
    private Utils() {
    }

    /**
     * 現在時刻を返す.
     * 
     * @return 現在時刻のTimestamp
     */
    public static Timestamp getCurreTimestamp() {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(System.currentTimeMillis());
        Timestamp ts = new Timestamp(calendar.getTimeInMillis());
        return ts;
    }
}
