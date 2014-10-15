/*
 AbstractHttpConnection.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.message.basic.conn;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpMessage;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestFactory;
import org.apache.http.HttpResponse;
import org.apache.http.impl.AbstractHttpServerConnection;
import org.apache.http.impl.io.HttpRequestParser;
import org.apache.http.impl.io.HttpResponseWriter;
import org.apache.http.io.HttpMessageParser;
import org.apache.http.io.HttpMessageWriter;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.io.SessionOutputBuffer;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.deviceconnect.message.conn.HttpConnection;

/**
 * このクラスはHttpConnectionのインターフェーススケルトン実装を提供し、このインタフェースを実装するのに必要な作業量を最小限に抑えます.
 * 
 * @author NTT DOCOMO, INC.
 */
public abstract class AbstractHttpConnection
        extends AbstractHttpServerConnection implements HttpConnection {

    /**
     * メッセージパーサー.
     */
    private HttpMessageParser mMessageParser = null;

    /**
     * メッセージライター.
     */
    private HttpMessageWriter mMessageWriter = null;

    /**
     * HTTPパラメータ.
     */
    private HttpParams mParams;

    /**
     * ロガー.
     */
    private Logger mLogger = Logger.getLogger("org.deviceconnect.sdk");

    @Override
    public int getSocketTimeout() {
        mLogger.entering(getClass().getName(), "getSocketTimeout");

        int msoTimeout = HttpConnectionParams.getSoTimeout(getParams());

        mLogger.exiting(getClass().getName(), "getSocketTimeout", msoTimeout);
        return msoTimeout;
    }

    @Override
    public void setSocketTimeout(final int timeout) {
        mLogger.entering(getClass().getName(), "setSocketTimeout", timeout);

        HttpConnectionParams.setSoTimeout(getParams(), timeout);

        mLogger.exiting(getClass().getName(), "setSocketTimeout");
    }

    @Override
    public HttpRequest receiveRequestHeader() throws HttpException, IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public HttpResponse receiveResponseHeader() throws HttpException, IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void receiveRequestEntity(final HttpEntityEnclosingRequest request)
            throws HttpException, IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void receiveResponseEntity(final HttpResponse response)
            throws HttpException, IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendRequestHeader(final HttpRequest request) throws HttpException, IOException {
        sendMessageHeader((HttpMessage) request);
    }

    @Override
    public void sendResponseHeader(final HttpResponse response) throws HttpException, IOException {
        sendMessageHeader((HttpMessage) response);
    }

    @Override
    public void sendRequestEntity(final HttpEntityEnclosingRequest request)
            throws HttpException, IOException {
        mLogger.entering(getClass().getName(), "sendRequestEntity");

        sendMessageEntity(request);

        mLogger.exiting(getClass().getName(), "sendRequestEntity");
    }

    @Override
    public void sendResponseEntity(final HttpResponse response) throws HttpException, IOException {
        mLogger.entering(getClass().getName(), "sendResponseEntity");

        sendMessageEntity(response);

        mLogger.exiting(getClass().getName(), "sendResponseEntity");
    }

    @Override
    public void sendMessageHeader(final HttpMessage message) throws HttpException, IOException {
        mLogger.entering(getClass().getName(), "sendMessageHeader");

        if (message == null) {
            throw new IllegalArgumentException("HTTP message may not be null");
        }
        assertOpen();

        mMessageWriter.write(message);

        mLogger.exiting(getClass().getName(), "sendMessageHeader");
    }

    @Override
    public HttpMessage receiveMessageHeader() throws HttpException, IOException {
        mLogger.entering(getClass().getName(), "receiveMessageHeader");

        assertOpen();
        HttpMessage message = mMessageParser.parse();

        mLogger.exiting(getClass().getName(), "receiveMessageHeader", message);
        return message;
    }

    @Override
    public HttpMessage getMessageHeader(final HttpMessage request)
            throws HttpException, IOException {
        return null;
    }

    @Override
    public void getMessageEntity(final HttpMessage request, final HttpMessage message)
            throws HttpException, IOException {
    }

    @Override
    public void bind(final InputStream input,
            final OutputStream output, final HttpParams params) throws IOException {
        throw new UnsupportedOperationException("cannot bind input, output streams");
    }

    @Override
    public void sendMessageEntity(final HttpMessage message) throws HttpException, IOException {
    }

    @Override
    public void receiveMessageEntity(final HttpMessage message) throws HttpException, IOException {
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public void shutdown() throws IOException {
    }

    @Override
    public boolean isResponseAvailable(final int arg0) throws IOException {
        return false;
    }

    /**
     * HTTPパラメータでバインドする.
     * @param params HTTPパラメータ
     */
    public void bind(final HttpParams params) {
        init(null, null, params);
    }

    /**
     * パラメータを取得する.
     * @return パラメータ
     */
    public HttpParams getParams() {
        return mParams;
    }

    @Override
    protected void init(
            final SessionInputBuffer inbuffer,
            final SessionOutputBuffer outbuffer,
            final HttpParams params) {

        mParams = params;
        mMessageParser = createRequestParser(inbuffer, createHttpRequestFactory(), params);
        mMessageWriter = createResponseWriter(outbuffer, params);

    }

    @Override
    protected void assertOpen() throws IllegalStateException {
        if (!isOpen()) {
            throw new IllegalStateException("connection is closed");
        }
    }

    @Override
    protected HttpMessageParser createRequestParser(
            final SessionInputBuffer buffer,
            final HttpRequestFactory requestFactory,
            final HttpParams params) {
        return new HttpRequestParser(buffer, null, requestFactory, params);
    }

    @Override
    protected HttpMessageWriter createResponseWriter(
            final SessionOutputBuffer buffer,
            final HttpParams params) {
        return new HttpResponseWriter(buffer, null, params);
    }

}
