/*
 DConnectService.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Intent;

import com.nttdocomo.android.dconnect.manager.util.DConnectUtil;
import com.nttdocomo.dconnect.message.DConnectMessage;
import com.nttdocomo.dconnect.server.DConnectServer;
import com.nttdocomo.dconnect.server.DConnectServerConfig;
import com.nttdocomo.dconnect.server.nanohttpd.DConnectServerNanoHttpd;

/**
 * dConnect Manager本体.
 * @author NTT DOCOMO, INC.
 */
public class DConnectService extends DConnectMessageService {
    /** 内部用タイプを定義する. */
    public static final String EXTRA_INNER_TYPE = "_type";
    /** HTTPからの通信タイプを定義する. */
    public static final String EXTRA_TYPE_HTTP = "http";

    /** Webサーバ. */
    private DConnectServer mWebServer;

    /** Webサーバからのイベントを受領するリスナー. */
    private DConnectServerEventListenerImpl mWebServerListener;

    @Override
    public void onCreate() {
        super.onCreate();

        sLogger.entering(this.getClass().getName(), "onCreate");

        // HTTPサーバ起動
        startHttpServer();

        sLogger.exiting(this.getClass().getName(), "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // HTTPサーバ停止
        stopHttpServer();
    }

    @Override
    public void sendResponse(final Intent request, final Intent response) {
        Intent intent = createResponseIntent(request, response);
        if (EXTRA_TYPE_HTTP.equals(request.getStringExtra(EXTRA_INNER_TYPE))) {
            mWebServerListener.onResponse(intent);
        } else {
            sendBroadcast(intent);
        }
    }

    @Override
    public void sendEvent(final String receiver, final Intent event) {
        if (receiver == null || receiver.length() <= 0) {
            String key = event.getStringExtra(DConnectMessage.EXTRA_SESSION_KEY);
            try {
                sLogger.fine("■ sendEvent: " + key + " extra: " + event.getExtras());
                JSONObject root = new JSONObject();
                DConnectUtil.convertBundleToJSON(root, event.getExtras());
                mWebServer.sendEvent(key, root.toString());
            } catch (Exception e) {
                sLogger.warning("Exception in sendEvent: " + e.toString());
            }
        } else {
            super.sendEvent(receiver, event);
        }
    }

    /**
     * HTTPサーバを開始する.
     */
    private void startHttpServer() {
        mWebServerListener = new DConnectServerEventListenerImpl(this);
        mWebServerListener.setFileManager(mFileMgr);

        DConnectServerConfig.Builder builder = new DConnectServerConfig.Builder();
        builder.port(mSettings.getPort()).isSsl(mSettings.isSSL())
            .documentRootPath(getFilesDir().getAbsolutePath());

        if (!mSettings.alowGlobalIP()) {
            ArrayList<String> list = new ArrayList<String>();
            list.add("127.0.0.1");
            builder.ipWhiteList(list);
        }

        sLogger.fine("Host: " + mSettings.getHost());
        sLogger.fine("Port: " + mSettings.getPort());
        sLogger.fine("SSL: " + mSettings.isSSL());
        sLogger.fine("Global IP: " + mSettings.alowGlobalIP());

        if (mWebServer == null) {
            mWebServer = new DConnectServerNanoHttpd(builder.build(), this);
            mWebServer.setServerEventListener(mWebServerListener);
            mWebServer.start();
        }
    }

    /**
     * HTTPサーバを停止する.
     */
    private void stopHttpServer() {
        if (mWebServer != null) {
            mWebServer.shutdown();
            mWebServer = null;
        }
    }
}
