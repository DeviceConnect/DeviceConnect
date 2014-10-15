/*
 HttpDConnectClient.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.message.http.impl.client;

import java.io.IOException;
import java.util.logging.Logger;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.AuthenticationHandler;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.RequestDirector;
import org.apache.http.client.UserTokenHandler;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.client.DefaultRequestDirector;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestExecutor;
import org.deviceconnect.message.basic.client.AbstractDConnectClient;

/**
 * HTTP Device Connectクライアント.
 * DConnectMessage及びHttpRequestを実行する機能を提供する。
 * @author NTT DOCOMO, INC.
 */
public class HttpDConnectClient extends AbstractDConnectClient {

    /**
     * デフォルトターゲット.
     */
    private HttpHost mTarget;

    /**
     * ロガー.
     */
    private Logger mLogger = Logger.getLogger("org.deviceconnect.sdk");

    /**
     * ターゲットを設定する.
     * @param target ターゲット
     */
    public void setDefaultTarget(final HttpHost target) {
        mTarget = target;
    }

    /**
     * ターゲットを取得する.
     * @return ターゲット
     */
    public HttpHost getDefaultTarget() {
        return mTarget;
    }

    @Override
    protected HttpRequestExecutor createRequestExecutor() {
        return super.createRequestExecutor();
    }

    @Override
    protected RequestDirector createClientRequestDirector(
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
            final UserTokenHandler stateHandler,
            final HttpParams params) {
        return new HostRewriteRequestDirector(
                requestExec, conman, reustrat, kastrat, rouplan, httpProcessor,
                retryHandler, redirectHandler, targetAuthHandler, proxyAuthHandler,
                stateHandler, params);
    }

    /**
     * ホストリライトリクエストディレクタ.
     */
    private class HostRewriteRequestDirector extends DefaultRequestDirector {

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
        public HostRewriteRequestDirector(
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
            mLogger.entering(getClass().getName(), "execute");

            HttpHost host;
            if (target == null && mTarget != null) {
                host = mTarget;
            } else {
                host = target;
            }

            HttpResponse response = super.execute(host, request, context);

            mLogger.entering(getClass().getName(), "execute");
            return response;
        }
    }

}
