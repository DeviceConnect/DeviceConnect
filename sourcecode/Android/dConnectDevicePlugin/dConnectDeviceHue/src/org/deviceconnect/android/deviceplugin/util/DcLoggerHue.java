/*
DcLoggerHue
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

package org.deviceconnect.android.deviceplugin.util;

import java.util.logging.Logger;

/**
 * Hue用ログ出力クラス.
 * @author NTT DOCOMO, INC.
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
