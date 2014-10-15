/*
 IntentResponseReceiver.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.message.intent.impl.io;

import java.util.logging.Logger;

import org.deviceconnect.message.intent.event.IntentEventManager;
import org.deviceconnect.message.intent.message.IntentDConnectMessage;
import org.deviceconnect.sdk.BuildConfig;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * レスポンスメッセージレシーバー.
 *
 * <p>
 * リクエストメッセージの送信先アプリケーションからレスポンスメッセージを受信する。
 * @author NTT DOCOMO, INC.
 */
public class IntentResponseReceiver extends BroadcastReceiver {

    /**
     * ロガー.
     */
    private Logger mLogger = Logger.getLogger("org.deviceconnect.sdk.android");

    /**
     * レシーバー登録フラグ.
     */
    private boolean mRegistered;

    /**
     * レシーバー登録フラグを取得する.
     * @return レシーバー登録フラグ
     */
    public boolean isRegistered() {
        mLogger.entering(this.getClass().getName(), "isRegistered");
        mLogger.exiting(this.getClass().getName(), "isRegistered", mRegistered);
        return mRegistered;
    }

    /**
     * レシーバー登録フラグを設定する.
     * @param registered レシーバー登録フラグ
     */
    public void setRegistered(final boolean registered) {
        mLogger.entering(this.getClass().getName(),
                "setRegistered", registered);
        mRegistered = registered;
        mLogger.exiting(this.getClass().getName(), "setRegistered");
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        mLogger.entering(this.getClass().getName(), "onReceive",
                new Object[] {context, intent});

        String action = intent.getAction();
        if (action != null && action.equals(IntentDConnectMessage.ACTION_RESPONSE)) {
            onResponseRecieve(context, intent);
        } else if (action != null && action.equals(IntentDConnectMessage.ACTION_EVENT)) {
            onEventRecieve(context, intent);
        } else {
            mLogger.warning("invalid response, action is not RESPONSE.");
            mLogger.exiting(this.getClass().getName(), "onReceive");
        }

        mLogger.exiting(this.getClass().getName(), "onReceive");
    }

    /**
     * レスポンスレシーバー.
     * @param context コンテキスト
     * @param intent レスポンスインテント
     */
    protected void onResponseRecieve(final Context context, final Intent intent) {
        mLogger.entering(this.getClass().getName(), "onResponseRecieve",
                new Object[] {context, intent});

        Bundle extra = intent.getExtras();
        if (extra == null) {
            return;
        }

        mLogger.fine("response: " + intent);
        mLogger.fine("response extra: " + extra);
        if (extra != null) {
            for (String key : extra.keySet()) {
                mLogger.fine(key + ": " + extra.get(key));
            }
        }

        IntentHttpMessageParser.addResponse(intent);

        mLogger.exiting(this.getClass().getName(), "onResponseRecieve");
    }

    /**
     * イベントレシーバー.
     * @param context コンテキスト
     * @param intent イベントインテント
     */
    protected void onEventRecieve(final Context context, final Intent intent) {
        mLogger.entering(this.getClass().getName(), "onEventRecieve",
                new Object[] {context, intent});

        Bundle extra = intent.getExtras();
        if (extra == null) {
            return;
        }

        if (BuildConfig.DEBUG) {
            mLogger.fine("event: " + intent);
            mLogger.fine("event extra: " + extra);
            if (extra != null) {
                for (String key : extra.keySet()) {
                    mLogger.fine(key + ": " + extra.get(key));
                }
            }            
        }
        
        IntentEventManager.INSTANCE.sendEvent(intent);

//        int requestCode = extra.getInt(DConnectMessage.EXTRA_REQUEST_CODE, 0);
//        if (requestCode == 0) {
//            mLogger.warning("invalid response, request code is 0.");
//            mLogger.fine(intent.toString());
//            mLogger.exiting(this.getClass().getName(), "onEventRecieve");
//            return;
//        }
//
//        Map<String, HttpMessageHandler> eveHandlerMap = DefaultIntentClient.getEventHandlerMap();
//        if (eveHandlerMap.get("" + requestCode) == null) {
//            mLogger.warning("invalid event, request code is " + requestCode + ".");
//            mLogger.fine(intent.toString());
//            mLogger.exiting(this.getClass().getName(), "onEventRecieve");
//            return;
//        }
//
//        DConnectMessage dmessage = (new IntentMessageFactory()).newDConnectMessage(intent);
//        HttpMessage message = (new HttpMessageFactory()).newPackagedMessage(dmessage);
//        eveHandlerMap.get(requestCode).handleMessage((HttpResponse) message);

        mLogger.exiting(this.getClass().getName(), "onEventRecieve");
    }

}
