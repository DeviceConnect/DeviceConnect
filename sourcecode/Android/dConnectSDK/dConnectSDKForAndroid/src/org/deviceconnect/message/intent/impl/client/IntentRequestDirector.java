/*
 IntentRequestDirector.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.message.intent.impl.client;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.AuthenticationHandler;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.UserTokenHandler;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.client.DefaultRequestDirector;
import org.apache.http.impl.client.RequestWrapper;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestExecutor;
import org.deviceconnect.message.HttpHeaders;

/**
 * Intent リクエストディレクター.
 * @author NTT DOCOMO, INC.
 */
public class IntentRequestDirector extends DefaultRequestDirector {

    /**
     * コンストラクタ.
     * @param requestExec リクエストエグゼキュータ
     * @param conman コネクションマネージャ
     * @param reustrat リクエストストラテジ
     * @param kastrat KeepAliveストラテジ
     * @param rouplan ルートプランナ
     * @param httpProcessor プロセッサ
     * @param retryHandler リトライハンドラ
     * @param redirectHandler リダイレクトハンドラ
     * @param targetAuthHandler 認証ハンドラ
     * @param proxyAuthHandler Proxy認証ハンドラ
     * @param userTokenHandler ユーザトークンハンドラ
     * @param params HTTPパラメータ
     */
    public IntentRequestDirector(
            final HttpRequestExecutor requestExec,
            final ClientConnectionManager conman,
            final ConnectionReuseStrategy reustrat,
            final ConnectionKeepAliveStrategy kastrat,
            final HttpRoutePlanner rouplan,
            final HttpProcessor httpProcessor,
            final HttpRequestRetryHandler retryHandler,
            final RedirectHandler redirectHandler,
            final AuthenticationHandler targetAuthHandler,
            final AuthenticationHandler proxyAuthHandler,
            final UserTokenHandler userTokenHandler,
            final HttpParams params) {
        super(requestExec, conman, reustrat, kastrat, rouplan, httpProcessor,
                retryHandler, redirectHandler, targetAuthHandler, proxyAuthHandler,
                userTokenHandler, params);
    }

    @Override
    public HttpResponse execute(
            final HttpHost target, final HttpRequest request, final HttpContext context)
                    throws HttpException, IOException {

        RequestWrapper origWrapper = new RequestWrapper(request);
        HttpRoute origRoute = determineRoute(target, origWrapper, context);
        Object userToken = context.getAttribute(ClientContext.USER_TOKEN);
        long timeout = HttpConnectionParams.getConnectionTimeout(params);

        ClientConnectionRequest connRequest = connManager.requestConnection(origRoute, userToken);
        ManagedClientConnection managedConn;
        try {
            managedConn = connRequest.getConnection(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new IOException(e);
        }

        if (request.getFirstHeader(HttpHeaders.HOST) == null) {
            request.setHeader(HttpHeaders.HOST,
                    "org.deviceconnect.manager/.DConnectBroadcastReceiver");
        }

        // requestExec.preProcess(request, httpProcessor, context);
        HttpResponse response = requestExec.execute(request, managedConn, context);
        // requestExec.postProcess(response, httpProcessor, context);

        return response;
    }

}
