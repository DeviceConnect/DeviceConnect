/*
 IntentEventManager.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.dconnect.message.intent.event;

import java.io.IOException;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.nttdocomo.dconnect.message.HttpHeaders;
import com.nttdocomo.dconnect.message.event.AbstractEventManager;
import com.nttdocomo.dconnect.message.event.EventHandler;
import com.nttdocomo.dconnect.message.intent.impl.client.DefaultIntentClient;
import com.nttdocomo.dconnect.message.intent.params.IntentConnectionParams;
import com.nttdocomo.dconnect.message.intent.util.JSONFactory;
import com.nttdocomo.dconnect.utils.URIBuilder;

/**
 * イベント管理クラス.
 * 
 * <p>
 * Intentによるイベントの登録、解除、受信のユーティリティ機能を提供する。<br/>
 * 当クラスを利用するには AndroidManifest.xmlへの{@link com.nttdocomo.dconnect.message.intent.impl.io.IntentResponseReceiver}
 * の登録が必要となる。
 * </p>
 * 
 * 
 * @author NTT DOCOMO, INC.
 */
public final class IntentEventManager extends AbstractEventManager {

    /**
     * IntentEventManagerのシングルトンなインスタンス.
     */
    public static final IntentEventManager INSTANCE = new IntentEventManager();

    /**
     * ロガー.
     */
    private Logger mLogger = Logger.getLogger("dconnect.sdk.android");

    /**
     * コンテキストオブジェクト.
     */
    private Context mContext;

    /**
     * 送り先のコンポーネントネーム.
     */
    private ComponentName mDConnectComponentName;

    /**
     * シングルのためprivate.
     */
    private IntentEventManager() {
    }

    /**
     * 接続情報を設定する.
     * <p>
     * {@link #registerEvent(com.nttdocomo.dconnect.utils.URIBuilder, 
     * com.nttdocomo.dconnect.message.event.EventHandler)}、 
     * {@link #unregisterEvent(com.nttdocomo.dconnect.utils.URIBuilder)}
     * メソッドを呼び出した場合、 このメソッドの引数を元に通信する。
     * 引数で指定されたコンテキストオブジェクトで、引数で指定されたコンポーネントネームをもつDevice Connectに対してIntentを送信する。
     * {@link #disconnect()}メソッドを呼び出すことで接続情報をクリアする。
     * </p>
     * 
     * @param context コンテキストオブジェクト
     * @param dConnectComponent Device Connect Managerのコンポーネントネーム
     */
    public synchronized void connect(final Context context, final ComponentName dConnectComponent) {
        mContext = context;
        mDConnectComponentName = dConnectComponent;
    }

    @Override
    public synchronized void disconnect() {
        mContext = null;
        mDConnectComponentName = null;
    }

    @Override
    public HttpResponse registerEvent(final URIBuilder builder, final EventHandler handler) throws IOException {

        // 送り先はComponentNameで設定するため、以下のデータはエラーにならないようにするためのダミーデータを入れておく。
        builder.setHost("localhost");
        builder.setPort(0);
        builder.setScheme("http");

        return super.registerEvent(builder, handler);
    }

    @Override
    public HttpResponse unregisterEvent(final URIBuilder builder) throws IOException {

        // 送り先はComponentNameで設定するため、以下のデータはエラーにならないようにするためのダミーデータを入れておく。
        builder.setHost("localhost");
        builder.setPort(0);
        builder.setScheme("http");

        return super.unregisterEvent(builder);
    }

    /**
     * 受信したイベントを通知する.
     * 
     * @param event イベントデータ
     */
    public void sendEvent(final Intent event) {

        try {
            JSONObject json = JSONFactory.convertBundleToJSON(event.getExtras());
            sendEvent(json);
        } catch (JSONException e) {
            mLogger.warning("IntentEventManager#sendEvent. Exception : " + e.getMessage());
        }

    }

    @Override
    protected synchronized HttpResponse execute(final HttpUriRequest request) throws IOException {

        if (mContext == null || mDConnectComponentName == null) {
            throw new IllegalStateException("Connection is not established. "
                    + "Call connect method before call registerEvent/unregisterEvent method.");
        }

        HttpParams params = new BasicHttpParams();
        IntentConnectionParams.setContext(params, mContext);
        IntentConnectionParams.setComponent(params, mDConnectComponentName);
        DefaultIntentClient client = new DefaultIntentClient(params);
        // DefaultIntentClientの仕様として、Hostで行き先を指定しているのでコンポーネント名を設定しておく
        request.setHeader(HttpHeaders.HOST, mDConnectComponentName.flattenToShortString());
        HttpResponse response = client.execute(request);
        // shutdownをするとEntityが取れなくなるためコピーを作成する。
        HttpResponse retRes = copyResponse(response);
        client.getConnectionManager().shutdown();
        return retRes;
    }
}
