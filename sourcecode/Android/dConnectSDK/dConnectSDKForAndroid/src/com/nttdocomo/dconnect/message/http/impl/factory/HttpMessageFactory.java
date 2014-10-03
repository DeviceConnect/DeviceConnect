/*
 HttpMessageFactory.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.dconnect.message.http.impl.factory;

import java.util.logging.Logger;

import org.apache.http.HttpMessage;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.RequestLine;
import org.apache.http.StatusLine;
import org.apache.http.impl.DefaultHttpRequestFactory;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.protocol.BasicHttpContext;

import com.nttdocomo.dconnect.message.DConnectMessage;
import com.nttdocomo.dconnect.message.basic.message.DConnectRequestMessage;
import com.nttdocomo.dconnect.message.basic.message.DConnectResponseMessage;
import com.nttdocomo.dconnect.message.factory.MessageFactory;

/**
 * HTTPメッセージファクトリー.
 * @author NTT DOCOMO, INC.
 */
public class HttpMessageFactory implements MessageFactory<HttpMessage> {

    /**
     * メッセージファクトリー.
     */
    private static HttpMessageFactory mHttpMessageFactory = new HttpMessageFactory();

    /**
     * ロガー.
     */
    private Logger mLogger = Logger.getLogger("dconnect.sdk.java");

    /**
     * レスポンスメッセージファクトリー.
     */
    private HttpResponseMessageFactory mResponseMessageFactory =
            HttpResponseMessageFactory.getMessageFactory();

    /**
     * レスポンスメッセージファクトリー.
     */
    private HttpRequestMessageFactory mRequestMessageFactory =
            HttpRequestMessageFactory.getMessageFactory();

    /**
     * メッセージファクトリーを取得する.
     * @return メッセージファクトリー
     */
    public static HttpMessageFactory getMessageFactory() {
        return mHttpMessageFactory;
    }

    @Override
    public Class<HttpMessage> getPackagedClass() {
        return HttpMessage.class;
    }

    @Override
    public HttpMessage newPackagedMessage(final DConnectMessage message) {
        mLogger.entering(this.getClass().getName(), "newPackagedMessage", message);

        HttpMessage pmessage;

        if (message instanceof DConnectRequestMessage) {
            pmessage = mRequestMessageFactory.newPackagedMessage(message);
        } else if (message instanceof DConnectResponseMessage) {
            pmessage = mResponseMessageFactory.newPackagedMessage(message);
        } else {
            throw new IllegalArgumentException("invalid message.");
        }

        mLogger.exiting(this.getClass().getName(), "newPackagedMessage", pmessage);
        return pmessage;
    }

    @Override
    public DConnectMessage newDConnectMessage(final HttpMessage message) {
        mLogger.entering(this.getClass().getName(), "newDConnectMessage");

        DConnectMessage dmessage;

        if (message instanceof HttpRequest) {
            dmessage = mRequestMessageFactory.newDConnectMessage((HttpRequest) message);
        } else if (message instanceof HttpResponse) {
            dmessage = mResponseMessageFactory.newDConnectMessage((HttpResponse) message);
        } else {
            throw new IllegalArgumentException("message in not http message.");
        }

        mLogger.exiting(this.getClass().getName(), "newDConnectMessage", message);
        return dmessage;
    }

    /**
     * 新しいHTTPメッセージを作成する.
     * @param requestline リクエストライン
     * @return HTTPメッセージ
     * @throws MethodNotSupportedException 非サポートメソッドが指定された場合
     */
    public HttpMessage newHttpMessage(final RequestLine requestline)
            throws MethodNotSupportedException {
        mLogger.entering(this.getClass().getName(), "newHttpMessage", requestline);
        mLogger.exiting(this.getClass().getName(), "newHttpMessage");
        return (new DefaultHttpRequestFactory()).newHttpRequest(requestline);
    }

    /**
     * 新しいHTTPメッセージを作成する.
     * @param statusline ステータスライン
     * @return HTTPメッセージ
     */
    public HttpMessage newHttpMessage(final StatusLine statusline) {
        mLogger.entering(this.getClass().getName(), "newHttpMessage");
        mLogger.exiting(this.getClass().getName(), "newHttpMessage");
        return (new DefaultHttpResponseFactory()).newHttpResponse(
                statusline, new BasicHttpContext());
    }

}
