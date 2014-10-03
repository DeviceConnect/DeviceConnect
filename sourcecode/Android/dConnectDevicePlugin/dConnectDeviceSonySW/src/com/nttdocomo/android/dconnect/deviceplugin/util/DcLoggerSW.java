package com.nttdocomo.android.dconnect.deviceplugin.util;

import java.util.logging.Logger;

/**
DcLoggerSW
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

/**
 * 
 * SonyWatch用ログ出力クラス.
 *
 */

public class DcLoggerSW extends DcLogger {

    /**
     * コンストラクタ.
     */
    public DcLoggerSW() {
        super();
        mLogger = Logger.getLogger("dconnect.dplugin.sw");
    }
    
}
