/*
DcLoggerLight
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

package org.deviceconnect.android.deviceplugin.util;

import java.util.logging.Logger;

/**
 * ライト関連ログクラス.
 * @author NTT DOCOMO, INC.
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
