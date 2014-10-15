/*
DcAsync
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

package org.deviceconnect.android.deviceplugin.util;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.deviceconnect.android.deviceplugin.hue.util.DConnectMessageHandler;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.message.basic.message.DConnectResponseMessage;
import org.deviceconnect.message.client.DConnectClient;
import org.deviceconnect.message.http.impl.client.HttpDConnectClient;
import org.deviceconnect.message.http.impl.factory.HttpMessageFactory;

import android.os.AsyncTask;


/**
 * 非同期クラス.
 * @author NTT DOCOMO, INC.
 */
public class DcAsync {

    /**
     * 非同期レスポンス取得.
     * 
     * @param url url
     * @param listener listener
     */
    public static void getAsyncResponse(final String url, final DConnectMessageHandler listener) {
        AsyncTask<Void, Void, DConnectMessage> task = new AsyncTask<Void, Void, DConnectMessage>() {
            @Override
            protected DConnectMessage doInBackground(final Void... params) {
                try {

                    DcLoggerHue logger = new DcLoggerHue();
                    logger.fine("DcAsync", "getAsyncResponse", url);

                    DConnectClient client = new HttpDConnectClient();
                    HttpGet request = new HttpGet(url);
                    HttpResponse response = client.execute(request);
                    return (new HttpMessageFactory()).newDConnectMessage(response);
                } catch (IOException e) {
                    return new DConnectResponseMessage(DConnectMessage.RESULT_ERROR);
                }
            }

            @Override
            protected void onPostExecute(final DConnectMessage message) {
                if (listener != null) {
                    listener.handleMessage(message);
                }
            }
        };
        task.execute();
    }

    /**
     * 非同期タスク.
     */
    private AsyncTask<Void, Void, DConnectMessage> mTask;

    /**
     * 非同期レスポンス取得.
     * 
     * @param url url
     * @param listener listner
     */
    public synchronized void getAsyncResponse2(final String url, final DConnectMessageHandler listener) {
        mTask = new AsyncTask<Void, Void, DConnectMessage>() {
            @Override
            protected DConnectMessage doInBackground(final Void... params) {
                try {
                    DcLoggerHue logger = new DcLoggerHue();
                    logger.fine("DcAsync", "getAsyncResponse2", url);

                    DConnectClient client = new HttpDConnectClient();
                    HttpGet request = new HttpGet(url);
                    HttpResponse response = client.execute(request);

                    logger.fine("DcAsync", "getAsyncResponse2 response", response.toString());

                    return (new HttpMessageFactory()).newDConnectMessage(response);
                } catch (IOException e) {
                    return new DConnectResponseMessage(DConnectMessage.RESULT_ERROR);
                }
            }

            @Override
            protected void onPostExecute(final DConnectMessage message) {
                if (listener != null) {
                    listener.handleMessage(message);
                }
            }
        };
        mTask.execute();
    }

    /**
     * タスク停止.
     */
    public synchronized void stopTask() {
        mTask.cancel(true);
    }

}
