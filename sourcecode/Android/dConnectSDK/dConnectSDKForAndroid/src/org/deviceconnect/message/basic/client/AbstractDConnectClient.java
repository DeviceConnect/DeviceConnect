/*
 AbstractDConnectClient.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.message.basic.client;

import java.io.IOException;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.message.client.DConnectClient;
import org.deviceconnect.message.http.impl.factory.HttpMessageFactory;

/**
 * このクラスはDConnectClientインターフェースの基本実装を提供する.
 * 
 * @author NTT DOCOMO, INC.
 */
public abstract class AbstractDConnectClient
        extends DefaultHttpClient implements DConnectClient {

    /**
     * ロガー.
     */
    private Logger mLogger = Logger.getLogger("org.deviceconnect.sdk");

    /**
     * メッセージファクトリー.
     */
    private HttpMessageFactory mHttpMessageFactory = HttpMessageFactory.getMessageFactory();

    /**
     * コンストラクタ.
     */
    public AbstractDConnectClient() {
        super();
    }

    /**
     * コンストラクタ.
     * @param params HTTPパラメータ
     */
    public AbstractDConnectClient(final HttpParams params) {
        super(params);
    }

    @Override
    public DConnectMessage execute(final DConnectMessage request) throws IOException {
        mLogger.entering(getClass().getName(), "execute", request);

        HttpUriRequest httpRequest = (HttpUriRequest)
                mHttpMessageFactory.newPackagedMessage(request);
        mLogger.fine("HTTP Request: " + httpRequest.getRequestLine());

        HttpResponse httpResponse = execute(httpRequest);
        mLogger.fine("HTTP Response: " + httpResponse.getStatusLine());

        DConnectMessage response = mHttpMessageFactory.newDConnectMessage(httpResponse);

        mLogger.exiting(getClass().getName(), "execute", response);
        return response;
    }

}
