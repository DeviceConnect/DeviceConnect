/*
 DConnectRequestManager.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.manager.request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Intent;
import android.util.Log;

import com.nttdocomo.android.dconnect.BuildConfig;
import com.nttdocomo.dconnect.message.intent.message.IntentDConnectMessage;

/**
 * dConnect Managerで処理されるリクエストを管理するクラス.
 * @author NTT DOCOMO, INC.
 */
public class DConnectRequestManager {
    /** エラーコードを定義する. */
    private static final int ERROR_CODE = Integer.MIN_VALUE;

    /** 最大スレッド数を定義する. */
    private static final int MAX_THREAD_SIZE = 4;

    /** リクエストを実行するためのスレッドを管理するExecutor. */
    private final ExecutorService mExecutor = Executors.newFixedThreadPool(MAX_THREAD_SIZE);

    /** シングルスレッドでリクエストを実行するためのスレッドを管理するExecutor. */
    private final ExecutorService mSingleExecutor = Executors.newSingleThreadExecutor();

    /** リクエスト一覧. */
    private final List<DConnectRequest> mRequestList = Collections.synchronizedList(new ArrayList<DConnectRequest>());

    /**
     * リクエスト管理を終了する.
     */
    public void shutdown() {
        mExecutor.shutdown();
        mSingleExecutor.shutdown();
    }

    /**
     * 実行するリクエストを追加する.
     * @param request 追加するリクエスト
     */
    public void addRequest(final DConnectRequest request) {
        request.setRequestMgr(this);
        mRequestList.add(request);
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    request.run();
                } catch (Throwable e) {
                    request.sendRuntimeException(e.getMessage());
                    if (BuildConfig.DEBUG) {
                        Log.e("dConnectManager", "runtime", e);
                    }
                } finally {
                    mRequestList.remove(request);
                }
            }
        });
    }

    /**
     * レスポンスを受け付ける.
     * @param response レスポンス
     */
    public void setResponse(final Intent response) {
        int code = response.getIntExtra(
                IntentDConnectMessage.EXTRA_REQUEST_CODE, ERROR_CODE);
        for (int i = 0; i < mRequestList.size(); i++) {
            DConnectRequest request = mRequestList.get(i);
            if (request.hasRequestCode(code)) {
                request.setResponse(response);
                return;
            }
        }
    }

    /**
     * シングルスレッドで実行するリクエストを追加する.
     * @param request 追加するリクエスト
     */
    public void addRequestOnSingleThread(final DConnectRequest request) {
        request.setRequestMgr(this);
        mRequestList.add(request);
        mSingleExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    request.run();
                } catch (Throwable e) {
                    request.sendRuntimeException(e.getMessage());
                    if (BuildConfig.DEBUG) {
                        Log.e("dConnectManager", "runtime", e);
                    }
                } finally {
                    mRequestList.remove(request);
                }
            }
        });
    }

    /**
     * レスポンスに対応するリクエストを取得する.
     * 対応するリクエストが存在しない場合にはnullを返却する。
     * @param response レスポンス
     * @return リクエストIntent
     */
    public Intent getRequestIntent(final Intent response) {
        int code = response.getIntExtra(
                IntentDConnectMessage.EXTRA_REQUEST_CODE, ERROR_CODE);
        for (int i = 0; i < mRequestList.size(); i++) {
            DConnectRequest request = mRequestList.get(i);
            if (request.hasRequestCode(code)) {
                return request.getRequest();
            }
        }
        return null;
    }
}
