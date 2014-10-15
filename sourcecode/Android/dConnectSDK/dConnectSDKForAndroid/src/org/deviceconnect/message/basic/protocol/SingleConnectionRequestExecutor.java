/*
 SingleConnectionRequestExecutor.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.message.basic.protocol;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Logger;

import org.apache.http.Header;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.deviceconnect.message.HttpHeaders;
import org.deviceconnect.message.basic.conn.SingleHttpConnection;
import org.deviceconnect.message.conn.HttpConnection;

/**
 * シングルコネクションリクエストエグゼキュータ.
 * @author NTT DOCOMO, INC.
 */
public class SingleConnectionRequestExecutor extends ConnectionRequestExecutor {

    /**
     * リクエストコード.
     */
    private UUID mRequestCode = UUID.randomUUID();

    /**
     * ロガー.
     */
    private Logger mLogger = Logger.getLogger("org.deviceconnect.sdk");

    /**
     * コンストラクタ.
     * @param conn エグゼキュータ
     */
    public SingleConnectionRequestExecutor(final HttpConnection conn) {
        super(conn);
        mLogger.entering(getClass().getName(), "SingleConnectionRequestExecutor");

        if (!(conn instanceof SingleHttpConnection)) {
            throw new IllegalArgumentException("conn is not SingleHttpConnection");
        }

        mLogger.exiting(getClass().getName(), "SingleConnectionRequestExecutor");
    }

    @Override
    protected HttpResponse doSendRequest(
            final HttpRequest request,
            final HttpClientConnection conn,
            final HttpContext context)
                    throws IOException, HttpException {
        mLogger.entering(getClass().getName(), "doSendRequest");

        request.addHeader(HttpHeaders.X_REQUEST_CODE, Integer.toString(mRequestCode.hashCode()));
        HttpResponse response = super.doSendRequest(request, conn, context);

        mLogger.exiting(getClass().getName(), "doSendRequest", response);
        return response;
    }

    @Override
    protected HttpResponse doReceiveResponse(
            final HttpRequest request,
            final HttpClientConnection conn,
            final HttpContext context)
                    throws IOException, HttpException {
        mLogger.entering(getClass().getName(), "doReceiveResponse");

        HttpResponse response = null;
        do {
            response = super.doReceiveResponse(request, conn, context);

            Header reqKey = request.getFirstHeader(HttpHeaders.X_REQUEST_CODE);
            Header resKey = response.getFirstHeader(HttpHeaders.X_REQUEST_CODE);

            if (reqKey != null && resKey != null
                    && reqKey.getValue().equals(resKey.getValue())) {
                mLogger.fine("last response is match");
            } else {
                SingleHttpConnection sconn = (SingleHttpConnection) conn;

                // put last response into cache
                sconn.addResponse(response);
                response = null;

                // search response from cache
                mLogger.fine("search cached response");
                for (HttpResponse res : sconn.getResponseList()) {
                    resKey = res.getFirstHeader(HttpHeaders.X_REQUEST_CODE);
                    if (reqKey != null && resKey != null
                            && reqKey.getValue().equals(resKey.getValue())) {
                        response = res;
                        break;
                    }
                }
            }
        } while (response == null);

        mLogger.exiting(getClass().getName(), "doReceiveResponse", response);
        return response;
    }

}
