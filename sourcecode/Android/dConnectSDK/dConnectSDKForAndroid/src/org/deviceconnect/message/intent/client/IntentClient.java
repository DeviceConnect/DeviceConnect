/*
 IntentClient.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.message.intent.client;

import org.deviceconnect.message.client.DConnectClient;

import android.content.ComponentName;
import android.content.Context;

/**
 * Intent クライアント.
 * @author NTT DOCOMO, INC.
 */
public interface IntentClient extends DConnectClient {

    /**
     * コンテキストを設定する.
     * @param context コンテキスト
     */
    void setContext(final Context context);

    /**
     * コンポーネントを設定する.
     * @param target レシーバーコンポーネント
     */
    void setDefaultComponent(final ComponentName target);

    /**
     * レシーバーコンポーネントを取得する.
     * @return レシーバーコンポーネント
     */
    ComponentName getDefaultComponent();

}
