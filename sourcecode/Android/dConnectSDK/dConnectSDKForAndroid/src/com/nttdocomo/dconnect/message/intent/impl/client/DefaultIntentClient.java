/*
 DefaultIntentClient.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.dconnect.message.intent.impl.client;

import java.util.logging.Logger;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.client.AuthenticationHandler;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.RequestDirector;
import org.apache.http.client.UserTokenHandler;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestExecutor;

import android.content.ComponentName;
import android.content.Context;

import com.nttdocomo.dconnect.message.basic.client.AbstractDConnectClient;
import com.nttdocomo.dconnect.message.basic.message.MessageFactoryManager;
import com.nttdocomo.dconnect.message.conn.HttpConnection;
import com.nttdocomo.dconnect.message.http.impl.factory.HttpMessageFactory;
import com.nttdocomo.dconnect.message.intent.client.IntentClient;
import com.nttdocomo.dconnect.message.intent.impl.conn.IntentConnection;
import com.nttdocomo.dconnect.message.intent.impl.factory.IntentMessageFactory;
import com.nttdocomo.dconnect.message.intent.impl.protocol.IntentRequestExecutor;

/**
 * Android Intentクライアント.
 * @author NTT DOCOMO, INC.
 */
public class DefaultIntentClient
        extends AbstractDConnectClient implements IntentClient {

    /**
     * デフォルトメッセージレシーバー.
     */
    public static final ComponentName DEFAULT_MESSAGE_RECEIVER = ComponentName.unflattenFromString(
                    "com.nttdocomo.android.dconnect/.DConnectBroadcastReceiver");

    /**
     * ロガー.
     */
    private Logger mLogger = Logger.getLogger("dconnect.sdk.android");

    /**
     * メッセージレシーバー.
     */
    private ComponentName mMessageTarget = DEFAULT_MESSAGE_RECEIVER;

    /**
     * メッセージファクタリーマネージャー.
     */
    private MessageFactoryManager mMfManager = new MessageFactoryManager();

    /**
     * コンストラクタ.
     */
    public DefaultIntentClient() {
        mMfManager.addMessageFactory(new IntentMessageFactory());
        mMfManager.addMessageFactory(new HttpMessageFactory());
    }

    /**
     * コンストラクタ.
     * @param context コンテキスト
     */
    public DefaultIntentClient(final Context context) {
        mMfManager.addMessageFactory(new IntentMessageFactory());
        setContext(context);
        mMfManager.addMessageFactory(new HttpMessageFactory());
    }

    /**
     * コンストラクタ.
     * @param context コンテキスト
     * @param component レシーバーコンポーネント
     */
    public DefaultIntentClient(final Context context, final ComponentName component) {
        mMfManager.addMessageFactory(new IntentMessageFactory());
        setContext(context);
        setDefaultComponent(component);
        mMfManager.addMessageFactory(new HttpMessageFactory());
    }

    /**
     * コンストラクタ.
     * @param context コンテキスト
     * @param component レシーバーコンポーネント
     * @param params HTTPパラメータ
     */
    public DefaultIntentClient(final Context context, final ComponentName component,
            final HttpParams params) {
        super(params);
        mMfManager.addMessageFactory(new IntentMessageFactory());
        setContext(context);
        setDefaultComponent(component);
        mMfManager.addMessageFactory(new HttpMessageFactory());
    }

    /**
     * コンストラクタ.
     * @param params HTTPパラメータ
     */
    public DefaultIntentClient(final HttpParams params) {
        super(params);
        mMfManager.addMessageFactory(new IntentMessageFactory());
        mMfManager.addMessageFactory(new HttpMessageFactory());
    }

    @Override
    public void setContext(final Context context) {
        mLogger.entering(this.getClass().getName(), "setContext", context);
        // do  nothing
        mLogger.exiting(this.getClass().getName(), "setContext");
    }

    @Override
    public void setDefaultComponent(final ComponentName messageReceiver) {
        mLogger.entering(this.getClass().getName(), "setMessageReceiver", messageReceiver);
        if (messageReceiver == null) {
            mMessageTarget = DEFAULT_MESSAGE_RECEIVER;
        } else {
            mMessageTarget = messageReceiver;
        }
        mLogger.exiting(this.getClass().getName(), "setMessageReceiver");
    }

    @Override
    public ComponentName getDefaultComponent() {
        return mMessageTarget;
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
        return new IntentRequestDirector(
                requestExec, conman, reustrat, kastrat, rouplan,
                httpProcessor, retryHandler, redirectHandler,
                targetAuthHandler, proxyAuthHandler, stateHandler, params);
    }

    @Override
    protected HttpRequestExecutor createRequestExecutor() {
        mLogger.entering(getClass().getName(), "createRequestExecutor");

        HttpConnection conn = new IntentConnection();
        conn.bind(getParams());

        HttpRequestExecutor executor = new IntentRequestExecutor(conn);

        mLogger.exiting(getClass().getName(), "createRequestExecutor", executor);
        return executor;
    }
}
