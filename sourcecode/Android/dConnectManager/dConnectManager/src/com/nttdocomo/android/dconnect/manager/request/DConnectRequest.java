/*
 DConnectRequest.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.manager.request;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.nttdocomo.android.dconnect.DConnectBroadcastReceiver;
import com.nttdocomo.android.dconnect.DConnectMessageService;
import com.nttdocomo.android.dconnect.DevicePlugin;
import com.nttdocomo.android.dconnect.DevicePluginManager;
import com.nttdocomo.android.dconnect.message.MessageUtils;
import com.nttdocomo.dconnect.message.DConnectMessage;
import com.nttdocomo.dconnect.message.intent.message.IntentDConnectMessage;

/**
 * DConnectリクエスト.
 * @author NTT DOCOMO, INC.
 */
public abstract class DConnectRequest {
    /** タイムアウト時間を定義する. */
    private static final int DEFAULT_TIMEOUT = 60000;

    /** オリジナルのリクエスト. */
    protected Intent mRequest;
    /** デバイスプラグインからのレスポンス. */
    protected Intent mResponse;
    /** このクラスが属するコンテキスト. */
    protected Context mContext;

    /** プラグイン管理クラス. */
    protected DevicePluginManager mPluginMgr;

    /** タイムアウト時間. */
    protected int mTimeout = DEFAULT_TIMEOUT;

    /** リクエスト管理クラス. */
    protected DConnectRequestManager mRequestMgr;

    /**
     * コンストラクタ.
     */
    public DConnectRequest() {
        mResponse = null;
    }

    /**
     * リクエスト管理クラスを取得する.
     * @return リクエスト管理クラス
     */
    public DConnectRequestManager getRequestMgr() {
        return mRequestMgr;
    }

    /**
     * リクエスト管理クラスを設定する.
     * @param mgr リクエスト管理クラス
     */
    public void setRequestMgr(final DConnectRequestManager mgr) {
        mRequestMgr = mgr;
    }

    /**
     * コンテキストを設定する.
     * @param context このクラスが属するコンテキスト
     */
    public void setContext(final Context context) {
        mContext = context;
    }

    /**
     * コンテキストを取得する.
     * @return コンテキスト
     */
    public Context getContext() {
        return mContext;
    }
    
    /**
     * 送信先のデバイスプラグインを設定する.
     * @param mgr デバイスプラグイン
     */
    public void setDevicePluginManager(final DevicePluginManager mgr) {
        mPluginMgr = mgr;
    }

    /**
     * 設定されているタイムアウト時間を取得する.
     * @return タイムアウト時間
     */
    public int getTimeout() {
        return mTimeout;
    }

    /**
     * タイムアウト時間を設定する.
     * @param timeout タイムアウト時間(ミリ秒)
     */
    public void setTimeout(final int timeout) {
        mTimeout = timeout;
    }

    /**
     * リクエストを設定する.
     * @param request リクエスト
     */
    public void setRequest(final Intent request) {
        mRequest = request;
    }

    /**
     * リクエストを取得する.
     * @return リクエスト
     */
    public Intent getRequest() {
        return mRequest;
    }

    /**
     * レスポンスを受け取る.
     * @param response レスポンス
     */
    public void setResponse(final Intent response) {
        mResponse = response;
    }

    /**
     * 各デバイスプラグインへ配送するリクエストを作成する.
     * @param request 配送元のリクエスト用Intent
     * @param plugin 配送先のデバイスプラグイン
     * @return 配送用Intent
     */
    protected Intent createRequestMessage(final Intent request, final DevicePlugin plugin) {
        Intent targetIntent = new Intent(request);
        String deviceid = request.getStringExtra(DConnectMessage.EXTRA_DEVICE_ID);
        String sessionKey = request.getStringExtra(DConnectMessage.EXTRA_SESSION_KEY);
        if (plugin != null) {
            // deviceIdを書き換えてしまうので、deviceIdよりも先にsessionKeyを変換する
            if (sessionKey != null) {
                mPluginMgr.appendPluginIdToSessionKey(targetIntent, plugin);
            }
            if (deviceid != null) {
                mPluginMgr.splitePluginIdToDeviceId(targetIntent);
            }
        }
        targetIntent.putExtra(IntentDConnectMessage.EXTRA_RECEIVER,
                new ComponentName(mContext, DConnectBroadcastReceiver.class));
        targetIntent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        return targetIntent;
    }

    /**
     * 取得したレスポンスを返却する.
     * @param response 返却するレスポンス
     */
    public void sendResponse(final Intent response) {
        ((DConnectMessageService) mContext).sendResponse(mRequest, response);
    }

    /**
     * タイムアウトのレスポンスを返却する.
     */
    protected void sendTimeout() {
        Intent response = new Intent(IntentDConnectMessage.ACTION_RESPONSE);
        MessageUtils.setTimeoutError(response);
        sendResponse(response);
    }

    /**
     * 実行時エラーが発生したことを通知する.
     * @param message エラーメッセージ
     */
    protected void sendRuntimeException(final String message) {
        Intent response = new Intent(IntentDConnectMessage.ACTION_RESPONSE);
        MessageUtils.setUnknownError(response, message);
        sendResponse(response);
    }

    /**
     * 指定されたリクエストコードを持っているかチェックする.
     * @param requestCode リクエストコード
     * @return リクエストコードを持っている場合はtrue、それ以外はfalse
     */
    public abstract boolean hasRequestCode(final int requestCode);
    
    /**
     * 各デバイスプラグインへのリクエスト送信とレスポンスを待つ処理を行う.
     */
    public abstract void run();
}
