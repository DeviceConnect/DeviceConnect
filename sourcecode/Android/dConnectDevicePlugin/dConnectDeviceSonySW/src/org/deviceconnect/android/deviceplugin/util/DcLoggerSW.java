/*
 DcLoggerSW.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.util;

import java.util.logging.Logger;

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
