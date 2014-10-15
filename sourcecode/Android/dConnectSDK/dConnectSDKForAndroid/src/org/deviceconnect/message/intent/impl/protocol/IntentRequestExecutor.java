/*
 IntentRequestExecutor.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.message.intent.impl.protocol;

import org.deviceconnect.message.basic.protocol.SingleConnectionRequestExecutor;
import org.deviceconnect.message.conn.HttpConnection;

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
