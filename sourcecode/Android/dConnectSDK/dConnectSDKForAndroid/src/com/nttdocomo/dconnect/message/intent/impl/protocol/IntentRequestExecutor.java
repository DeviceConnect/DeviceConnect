/*
 IntentRequestExecutor.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.dconnect.message.intent.impl.protocol;

import com.nttdocomo.dconnect.message.basic.protocol.SingleConnectionRequestExecutor;
import com.nttdocomo.dconnect.message.conn.HttpConnection;

/**
 * Intentリクエストエグゼキュータ.
 * @author NTT DOCOMO, INC.
 */
public class IntentRequestExecutor extends SingleConnectionRequestExecutor {

    /**
     * コンストラクタ.
     * @param conn コネクション
     */
    public IntentRequestExecutor(final HttpConnection conn) {
        super(conn);
    }

}
