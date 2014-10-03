package com.nttdocomo.android.dconnect.deviceplugin.util;

import java.util.logging.Logger;

/**
DcLoggerHue
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

/**
 * 
 * Hue用ログ出力クラス.
 * 
 */
public class DcLoggerHue extends DcLogger {

    /**
     * コンストラクタ.
     */
    public DcLoggerHue() {
        super();
        mLogger = Logger.getLogger("dconnect.dplugin.hue");
    }

}
