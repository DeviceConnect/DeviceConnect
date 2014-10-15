/*
 ConnectionRequestExecutor.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.message.basic.protocol;

import java.io.IOException;
import java.util.logging.Logger;

import org.apache.http.HttpClientConnection;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestExecutor;
import org.deviceconnect.message.conn.HttpConnection;

/**
 * リクエストエグゼキュータ.
 * @author NTT DOCOMO, INC.
 */
public class ConnectionRequestExecutor extends HttpRequestExecutor {

    /**
     * ロガー.
     */
    private Logger mLogger = Logger.getLogger("org.deviceconnect.sdk");

    /**
     * コネクション.
     */
    private HttpConnection mConnection;

    /**
     * コンストラクタ.
     * @param conn エグゼキュータ
     */
    public ConnectionRequestExecutor(final HttpConnection conn) {
        mLogger.entering(getClass().getName(), "BasicRequestExecutor");
        mConnection = conn;
        mLogger.exiting(getClass().getName(), "BasicRequestExecutor");
    }

    @Override
    public HttpResponse execute(
            final HttpRequest request,
            final HttpClientConnection conn,
            final HttpContext context)
                    throws IOException, HttpException {
        mLogger.entering(getClass().getName(), "execute");

        HttpResponse response = doSendRequest(request, mConnection, context);
        if (response == null) {
            response = doReceiveResponse(request, mConnection, context);
        }

        mLogger.exiting(getClass().getName(), "doSendRequest", response);
        return response;
    }

    @Override
    protected HttpResponse doSendRequest(
            final HttpRequest request,
            final HttpClientConnection conn,
            final HttpContext context)
                    throws IOException, HttpException {
        mLogger.entering(getClass().getName(), "doSendRequest");

        conn.sendRequestHeader(request);
        if (request instanceof HttpEntityEnclosingRequest) {
            conn.sendRequestEntity((HttpEntityEnclosingRequest) request);
        }
        conn.flush();

        mLogger.exiting(getClass().getName(), "doSendRequest", null);
        return null;
    }

    @Override
    protected HttpResponse doReceiveResponse(
            final HttpRequest request,
            final HttpClientConnection conn,
            final HttpContext context)
                    throws IOException, HttpException {
        mLogger.entering(getClass().getName(), "doReceiveResponse");

        HttpResponse response = null;
        int status = 0;

        HttpConnection hconn = (HttpConnection) conn;

        while (response == null || status < HttpStatus.SC_OK) {
            response = (HttpResponse) hconn.receiveMessageHeader();
            if (canResponseHaveBody(request, response)) {
                hconn.receiveMessageEntity(response);
            }
            status = response.getStatusLine().getStatusCode();
        }

        mLogger.exiting(getClass().getName(), "doReceiveResponse", response);
        return response;
    }

}
