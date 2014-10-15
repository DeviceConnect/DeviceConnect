/*
 IntentConnectionParams.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.message.intent.params;

import org.apache.http.params.HttpParams;

import android.content.ComponentName;
import android.content.Context;

/**
 * Intentクライアントパラメータ管理.
 * @author NTT DOCOMO, INC.
 */
public class IntentConnectionParams {

    /**
     * コンストラクタ.
     */
    protected IntentConnectionParams() {
    }

    /**
     * コンテキストを取得する.
     * @param params HTTPパラメータ
     * @return コンテキスト
     */
    public static Context getContext(final HttpParams params) {
        return (Context) params.getParameter(IntentPNames.CONNECTION_CONTEXT);
    }

    /**
     * コンテキストを設定する.
     * @param params HTTPパラメータ
     * @param context コンテキスト
     */
    public static void setContext(
            final HttpParams params, final Context context) {
        params.setParameter(IntentPNames.CONNECTION_CONTEXT, context);
    }

    /**
     * コンテキストを取得する.
     * @param params HTTPパラメータ
     * @return コンテキスト
     */
    public static ComponentName getComponent(final HttpParams params) {
        return (ComponentName) params.getParameter(IntentPNames.CONNECTION_COMPONENT_NAME);
    }

    /**
     * コンポーネント名を設定する.
     * @param params HTTPパラメータ
     * @param componentName コンポーネント名
     */
    public static void setComponent(
            final HttpParams params, final ComponentName componentName) {
        params.setParameter(IntentPNames.CONNECTION_COMPONENT_NAME, componentName);
    }

    /**
     * コンポーネント名を設定する.
     * @param params HTTPパラメータ
     * @param componentName コンポーネント名
     */
    public static void setComponent(
            final HttpParams params, final String componentName) {
        setComponent(params, ComponentName.unflattenFromString(componentName));
    }

    /**
     * コンポーネント名を設定する.
     * @param params HTTPパラメータ
     * @param pkg パッケージコンテキスト
     * @param cls レシーバークラス
     */
    public static void setComponent(
            final HttpParams params, final Context pkg, final Class<?> cls) {
        setComponent(params, new ComponentName(pkg, cls));
    }

}
