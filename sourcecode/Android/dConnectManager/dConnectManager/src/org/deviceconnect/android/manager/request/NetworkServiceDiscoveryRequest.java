/*
 NetworkServiceDiscoveryRequest.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.manager.request;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.deviceconnect.android.manager.DevicePlugin;
import org.deviceconnect.android.profile.NetworkServiceDiscoveryProfile;
import org.deviceconnect.message.intent.message.IntentDConnectMessage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.SparseArray;

/**
 * Network Service Discovery用のリクエストクラス.
 * <p>
 * 他のリクエストと異なる点として、複数のレスポンスを受け取る事が挙げられる.
 * 結果として、レスポンスタイムアウトの判断基準が普通のリクエストではレスポンスを1つ受け取ったかどうか
 * になり、他方Network Service Discovery用リクエストでは登録されているデバイスプラグイン
 * の数だけレスポンスを受け取ったかどうかになっている.
 * </p>
 * @author NTT DOCOMO, INC.
 */
public class NetworkServiceDiscoveryRequest extends DConnectRequest {
    /** レスポンスが返ってきた個数. */
    private int mResponseCount;

    /** リクエストコードを格納する配列. */
    private SparseArray<DevicePlugin> mRequestCodeArray = new SparseArray<DevicePlugin>();

    /** 発見したサービスを一時的に格納しておくリスト. */
    private final List<Bundle> mServices = new ArrayList<Bundle>();

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

        // エラーが返ってきた場合には、サービスには登録しない。
        int result = response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1);
        if (result == IntentDConnectMessage.RESULT_OK) {
            // 送られてきたデバイスIDにデバイスプラグインのIDを付加して保存
            Parcelable[] services = response.getParcelableArrayExtra(
                    NetworkServiceDiscoveryProfile.PARAM_SERVICES);
            if (services != null) {
                DevicePlugin plugin = mRequestCodeArray.get(requestCode);
                for (Parcelable p : services) {
                    Bundle b = (Bundle) p;
                    String id = b.getString(NetworkServiceDiscoveryProfile.PARAM_ID);
                    b.putString(NetworkServiceDiscoveryProfile.PARAM_ID, 
                            mPluginMgr.appendDeviceId(plugin, id));
                    mServices.add(b);
                }
            }
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

        // 送信用のIntentを作成
        Intent request = createRequestMessage(mRequest, null);
        for (int i = 0; i < plugins.size(); i++) {
            DevicePlugin plugin = plugins.get(i);

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
        mResponse.putExtra(NetworkServiceDiscoveryProfile.PARAM_SERVICES,
                mServices.toArray(new Bundle[mServices.size()]));

        // レスポンスを返却する
        sendResponse(mResponse);
    }
}
