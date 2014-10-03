/*
 TestCaseBroadcastReceiver.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.nttdocomo.dconnect.message.intent.message.IntentDConnectMessage;

/**
 * d-Connect Managerからのレスポンスを待つレシーバー.
 * @author NTT DOCOMO, INC.
 */
public class TestCaseBroadcastReceiver extends BroadcastReceiver {
    /**
     * テスト結果を受け取るレシーバー.
     * @param context コンテキスト
     * @param intent レスポンス
     */
    @Override
    public void onReceive(final Context context, final Intent intent) {
        String action = intent.getAction();
        String testAction = null;
        if (IntentDConnectMessage.ACTION_RESPONSE.equals(action)) {
            testAction = DConnectTestCase.TEST_ACTION_RESPONSE;
        } else if (IntentDConnectMessage.ACTION_EVENT.equals(action)) {
            testAction = DConnectTestCase.TEST_ACTION_EVENT;
        }
        if (testAction != null) {
            Intent targetIntent = new Intent(testAction);
            targetIntent.putExtras(intent.getExtras());
            context.sendBroadcast(targetIntent);
        }
    }
}
