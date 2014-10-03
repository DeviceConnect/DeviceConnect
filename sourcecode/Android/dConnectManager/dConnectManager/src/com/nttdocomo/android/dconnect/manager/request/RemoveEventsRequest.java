/*
 RemoveEventsRequest.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.manager.request;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import android.content.Intent;
import android.util.SparseArray;

import com.nttdocomo.android.dconnect.DevicePlugin;
import com.nttdocomo.dconnect.message.intent.message.IntentDConnectMessage;

/**
 * 各デバイスプラグインにイベント解除要求を行う.
 * @author NTT DOCOMO, INC.
 */
public class RemoveEventsRequest extends DConnectRequest {
    /** レスポンスが返ってきた個数. */
    private int mResponseCount;

    /** リクエストコードを格納する配列. */
    private SparseArray<DevicePlugin> mRequestCodeArray = new SparseArray<DevicePlugin>();

    /** ロガー. */
    private final Logger sLogger = Logger.getLogger("dconnect.manager");

    /** ロックオブジェクト. */
    private final Object mLockObj = new Object();

    @Override
    public void setResponse(final Intent response) {
        // リクエストコードを取得
        int requestCode = response.getIntExtra(
                IntentDConnectMessage.EXTRA_REQUEST_CODE, -1);
        if (requestCode == -1) {
            sLogger.warning("Illegal requestCode. requestCode=" + requestCode);
            return;
        }

        // レスポンス個数を追加
        mResponseCount++;
        synchronized (mLockObj) {
            mLockObj.notifyAll();
        }
    }

    @Override
    public boolean hasRequestCode(final int requestCode) {
        return mRequestCodeArray.get(requestCode) != null;
    }

    @Override
    public void run() {
        if (mRequest == null) {
            throw new RuntimeException("mRequest is null.");
        }

        if (mPluginMgr == null) {
            throw new RuntimeException("mDevicePluginManager is null.");
        }

        List<DevicePlugin> plugins = mPluginMgr.getDevicePlugins();
        for (int i = 0; i < plugins.size(); i++) {
            DevicePlugin plugin = plugins.get(i);

            // 送信用のIntentを作成
            Intent request = createRequestMessage(mRequest, plugin);

            // リクエストコード作成
            int requestCode = UUID.randomUUID().hashCode();
            mRequestCodeArray.put(requestCode, plugin);

            request.setComponent(plugin.getComponentName());
            request.putExtra(IntentDConnectMessage.EXTRA_REQUEST_CODE, requestCode);
            mContext.sendBroadcast(request);
        }

        // 各デバイスのレスポンスを待つ
        long start = System.currentTimeMillis();
        while (plugins.size() > 0 && mResponseCount < plugins.size()) {
            synchronized (mLockObj) {
                try {
                    mLockObj.wait(mTimeout);
                } catch (InterruptedException e) {
                    // do nothing.
                    sLogger.warning("Exception ouccered in wait.");
                }
            }
            // タイムアウトチェック
            if (System.currentTimeMillis() - start > mTimeout) {
                break;
            }
        }

        // パラメータを設定する
        mResponse = new Intent(IntentDConnectMessage.ACTION_RESPONSE);
        mResponse.putExtra(IntentDConnectMessage.EXTRA_RESULT,
                IntentDConnectMessage.RESULT_OK);

        // レスポンスを返却する
        sendResponse(mResponse);
    }
}
