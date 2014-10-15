/*
 DConnectAndroidClientTask.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.message.intent;

import java.util.UUID;
import java.util.logging.Logger;

import org.deviceconnect.message.DConnectMessage;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.SparseArray;

/**
 * メッセージ実行タスク.
 *
 * <p>
 * リクエストメッセージを {@link AsyncTask#execute(Intent...)} の引数に受け、
 * レスポンスメッセージを {@link AsyncTask#onPostExecute(Intent)} の引数で受ける。
 * リクエストメッセージを受けてからレスポンスメッセージを受けるまでに {@link #DEFAULT_MESSAGE_TIMEOUT} の時間を越えた場合
 * レスポンスとしてエラーレスポンスメッセージを返却する。
 * @author NTT DOCOMO, INC.
 */
public class DConnectAndroidClientTask extends AsyncTask<Intent, Integer, Intent> {

    /**
     * イベントレシーバータイムアウト(ms).
     */
    public static final long DEFAULT_MESSAGE_TIMEOUT = 30000;

    /**
     * イベントレシーバー待機時間.
     *
     * イベントレシーバーの応答をポーリングで待っている時のウェイト時間。
     * この時間 {@link Thread#sleep(long)} して、イベントを受け取っているかどうかを待つ。
     */
    private static final int POLLING_WAIT_TIME = 50;

    /**
     * ロガー.
     */
    private Logger mLogger = Logger.getLogger("org.deviceconnect.sdk.android");

    /**
     * イベントマップ.
     */
    private static SparseArray<Intent> sEventMap = new SparseArray<Intent>();

    /**
     * コンテキスト.
     */
    private Context mContext;

    /**
     * タイムアウト.
     */
    private long mTimeout = DEFAULT_MESSAGE_TIMEOUT;

    /**
     * コンストラクタ.
     * @param context コンテキスト
     */
    public DConnectAndroidClientTask(final Context context) {
        mLogger.entering(this.getClass().getName(), "AsyncReceiverTask", context);
        mContext = context;

        mLogger.exiting(this.getClass().getName(), "AsyncReceiverTask");
    }

    /**
     * タイムアウト時間を取得する.
     * @return タイムアウト時間(ms)
     */
    public long getEventTimeout() {
        mLogger.entering(this.getClass().getName(), "getEventTimeout");
        mLogger.exiting(this.getClass().getName(), "getEventTimeout", mTimeout);
        return mTimeout;
    }

    /**
     * タイムアウト時間を設定する.
     * @param timeout タイムアウト時間(ms)
     */
    public void setEventTimeout(final long timeout) {
        mLogger.entering(this.getClass().getName(), "setEventTimeout", timeout);
        mTimeout = timeout;
        mLogger.exiting(this.getClass().getName(), "setEventTimeout");
    }

    @Override
    protected void onPreExecute() {
        mLogger.entering(this.getClass().getName(), "onPreExecute");
        mLogger.exiting(this.getClass().getName(), "onPreExecute");
    }

    @Override
    protected Intent doInBackground(final Intent... args) {
        mLogger.entering(this.getClass().getName(), "doInBackground");

        // check argument
        if (args == null || args.length == 0 || args[0] == null) {
            mLogger.warning("MessageTask receive no parameter intent.");
            mLogger.exiting(this.getClass().getName(), "doInBackground", null);
            return null;
        }

        // get request intent
        Intent request = args[0];

        // put intent optional extras
        int requestCode = UUID.randomUUID().hashCode();
        request.putExtra(DConnectMessage.EXTRA_REQUEST_CODE, requestCode);
        request.putExtra(DConnectMessage.EXTRA_RECEIVER,
                new ComponentName(mContext, DConnectAndroidResponseReceiver.class));

        // send broadcast
        mLogger.fine("send request broadcast: " + request);
        mLogger.fine("send request extra: " + request.getExtras());
        mContext.sendBroadcast(request);

        // store request into event map
        sEventMap.put(requestCode, null);

        // wait response
        long now = System.currentTimeMillis();
        try {
            do {
                Thread.sleep(POLLING_WAIT_TIME);
            } while (sEventMap.get(requestCode) == null
                    && System.currentTimeMillis() - now < mTimeout);
        } catch (InterruptedException e) {
            mLogger.fine(e.toString());
        }

        // get result intent
        Intent response = sEventMap.get(requestCode);
        sEventMap.remove(requestCode);

        // response is timeout
        if (response == null) {
            Bundle bundle = new Bundle();
            bundle.putInt(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_ERROR);
            bundle.putInt(DConnectMessage.EXTRA_ERROR_CODE, 
                    DConnectMessage.ErrorCode.TIMEOUT.getCode());
            bundle.putString(DConnectMessage.EXTRA_ERROR_MESSAGE, 
                    DConnectMessage.ErrorCode.TIMEOUT.toString());
            response = new Intent();
            response.putExtras(bundle);
        }

        mLogger.exiting(this.getClass().getName(), "doInBackground", response);
        return response;
    }

    /**
     * イベントマップを取得する.
     * @return イベントマップ
     */
    static SparseArray<Intent> getEventMap() {
        return sEventMap;
    }

}
