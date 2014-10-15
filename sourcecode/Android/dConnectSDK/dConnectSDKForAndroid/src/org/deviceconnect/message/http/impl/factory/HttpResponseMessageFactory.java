/*
 HttpResponseMessageFactory.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.message.http.impl.factory;

import java.util.logging.Logger;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.impl.EnglishReasonPhraseCatalog;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.protocol.HTTP;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.message.basic.message.DConnectResponseMessage;

/**
 * HTTPメッセージファクトリー.
 * @author NTT DOCOMO, INC.
 */
public class HttpResponseMessageFactory extends AbstractHttpMessageFactory<HttpResponse> {

    /**
     * メッセージファクトリー.
     */
    private static HttpResponseMessageFactory mHttpMessageFactory =
            new HttpResponseMessageFactory();

    /**
     * ロガー.
     */
    private Logger mLogger = Logger.getLogger("org.deviceconnect.sdk");

    /**
     * メッセージファクトリーを取得する.
     * @return メッセージファクトリー
     */
    public static HttpResponseMessageFactory getMessageFactory() {
        return mHttpMessageFactory;
    }

    @Override
    public Class<HttpResponse> getPackagedClass() {
        return HttpResponse.class;
    }

    @Override
    public HttpResponse newPackagedMessage(final DConnectMessage message) {
        mLogger.entering(this.getClass().getName(), "newPackagedMessage", message);

        mLogger.fine("create http request from dmessage");
        HttpResponse response = new BasicHttpResponse(
                HttpVersion.HTTP_1_1, HttpStatus.SC_OK,
                EnglishReasonPhraseCatalog.INSTANCE.getReason(HttpStatus.SC_OK, null));

        mLogger.fine("put request headers");
        for (Header header: createHttpHeader(message)) {
            response.addHeader(header);
        }

        mLogger.fine("put request body");
        HttpEntity entity = createHttpEntity(message);
        response.addHeader(HTTP.CONTENT_LEN, "" + entity.getContentLength());
        response.setEntity(entity);

        mLogger.exiting(this.getClass().getName(), "newPackagedMessage", response);
        return response;
    }

    @Override
    public DConnectMessage newDConnectMessage(final HttpResponse message) {
        mLogger.entering(this.getClass().getName(), "newDConnectMessage");

        DConnectResponseMessage dmessage = null;

        mLogger.fine("create message from request");
        dmessage = (DConnectResponseMessage) parseFirstLine(message);

        mLogger.fine("parse request headers");
        parseHttpHeader(dmessage, message);

        mLogger.fine("parse request body");
        parseHttpBody(dmessage, message);

        mLogger.exiting(this.getClass().getName(), "newDConnectMessage", dmessage);
        return dmessage;
    }

}
