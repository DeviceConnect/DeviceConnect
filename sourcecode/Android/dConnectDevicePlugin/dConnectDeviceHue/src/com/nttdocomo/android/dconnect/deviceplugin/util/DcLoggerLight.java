/*
DcLoggerLight
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

package com.nttdocomo.android.dconnect.deviceplugin.util;

import java.util.logging.Logger;

/**
 * ライト関連ログクラス.
 * 
 * 
 */
public class DcLoggerLight extends DcLogger {

    /**
     * コンストラクタ.
     */
    public DcLoggerLight() {
        super();
        mLogger = Logger.getLogger("dconnect.dplugin.light");
    }
}
