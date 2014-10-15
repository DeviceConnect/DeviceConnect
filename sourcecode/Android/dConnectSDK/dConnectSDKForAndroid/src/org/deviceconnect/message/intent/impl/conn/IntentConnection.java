/*
 IntentConnection.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.message.intent.impl.conn;

import java.util.logging.Logger;

import org.apache.http.HttpRequestFactory;
import org.apache.http.io.HttpMessageParser;
import org.apache.http.io.HttpMessageWriter;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.io.SessionOutputBuffer;
import org.apache.http.params.HttpParams;
import org.deviceconnect.message.basic.conn.SingleHttpConnection;
import org.deviceconnect.message.intent.impl.io.IntentHttpMessageParser;
import org.deviceconnect.message.intent.impl.io.IntentHttpMessageWriter;
import org.deviceconnect.message.intent.params.IntentConnectionParams;

import android.content.Context;

/**
 * Intentコネクション.
 * @author NTT DOCOMO, INC.
 */
public class IntentConnection extends SingleHttpConnection {

    /**
     * ロガー.
     */
    private Logger mLogger = Logger.getLogger("org.deviceconnect.sdk.android");

    @Override
    protected HttpMessageParser createRequestParser(
            final SessionInputBuffer buffer,
            final HttpRequestFactory requestFactory,
            final HttpParams params) {
        mLogger.entering(getClass().getName(), "createRequestParser");

        HttpMessageParser parser = new IntentHttpMessageParser(params);

        mLogger.exiting(getClass().getName(), "createRequestParser", parser);
        return parser;
    }

    @Override
    protected HttpMessageWriter createResponseWriter(
            final SessionOutputBuffer buffer,
            final HttpParams params) {
        mLogger.entering(getClass().getName(), "createRequestParser");

        Context context = IntentConnectionParams.getContext(params);
        HttpMessageWriter writer =  new IntentHttpMessageWriter(context, params);

        mLogger.exiting(getClass().getName(), "createRequestParser", writer);
        return writer;
    }

    @Override
    public void flush() {
        // do nothing
    }

}
