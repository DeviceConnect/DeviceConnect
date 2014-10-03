/*
 RegisterNetworkServiceDiscovery.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.manager.request;

import java.util.UUID;
import java.util.logging.Logger;

import android.content.Intent;

import com.nttdocomo.android.dconnect.DevicePlugin;
import com.nttdocomo.dconnect.message.DConnectMessage;
import com.nttdocomo.dconnect.message.intent.message.IntentDConnectMessage;
import com.nttdocomo.dconnect.profile.NetworkServiceDiscoveryProfileConstants;

/**
 * Network Service Discovery Status Change Event 登録用リクエスト.
 * @author NTT DOCOMO, INC.
 */
public class RegisterNetworkServiceDiscovery extends DConnectRequest {
    /** ロガー. */
    private final Logger sLogger = Logger.getLogger("dconnect.manager");

    /** 送信先のデバイスプラグイン. */
    private DevicePlugin mDevicePlugin;

    /** ロックオブジェクト. */
    private final Object mLockObj = new Object();

    /** リクエストコード. */
    private int mRequestCode;

    /** セッションキー. */
    private String mSessionKey;

    /**
     * 送信先のデバイスプラグインを設定する.
     * @param plugin デバイスプラグイン
     */
    public void setDestination(final DevicePlugin plugin) {
        mDevicePlugin = plugin;
    }

    /**
     * セッションキーを設定する.
     * @param sessionKey セッションキー
     */
    public void setSessionKey(final String sessionKey) {
        mSessionKey = sessionKey;
    }

    @Override
    public void setResponse(final Intent response) {
        super.setResponse(response);
        synchronized (mLockObj) {
            mLockObj.notifyAll();
        }
    }

    @Override
    public boolean hasRequestCode(final int requestCode) {
        return mRequestCode == requestCode;
    }

    @Override
    public void run() {
        // リクエストコードを作成する
        mRequestCode = UUID.randomUUID().hashCode();

        // リクエストを作成
        mRequest = new Intent(IntentDConnectMessage.ACTION_PUT);
        mRequest.putExtra(DConnectMessage.EXTRA_PROFILE,
                NetworkServiceDiscoveryProfileConstants.PROFILE_NAME);
        mRequest.putExtra(DConnectMessage.EXTRA_ATTRIBUTE,
                NetworkServiceDiscoveryProfileConstants.ATTRIBUTE_ON_SERVICE_CHANGE);
        mRequest.putExtra(DConnectMessage.EXTRA_SESSION_KEY, mSessionKey);

        Intent request = createRequestMessage(mRequest, mDevicePlugin);
        request.putExtra(IntentDConnectMessage.EXTRA_REQUEST_CODE, mRequestCode);
        request.setComponent(mDevicePlugin.getComponentName());

        // リクエスト送信
        mContext.sendBroadcast(request);

        if (mResponse == null) {
            // 各デバイスのレスポンスを待つ
            waitForResponse();
        }

        // レスポンスを解析して、処理を行う
        if (mResponse != null) {
            // リカバリ不可能なのでログだけ出して終了
            // ここで、登録できなかった場合には、デバイス発見イベントは使用することができない。
            // ただし、getnetworkservicesは使用できるので問題はないと考える。
            int result = getResult(mResponse);
            if (result == DConnectMessage.RESULT_ERROR) {
                int errorCode = getErrorCode(mResponse);
                String errorMsg = getErrorMessage(mResponse);
                sLogger.severe("Failed to register onservicechange event." 
                        + "errorCode=" + errorCode + " errorMessage=" + errorMsg);
            }
        } else {
            sendTimeout();
        }
    }

    /**
     * resultの値をレスポンスのIntentから取得する.
     * @param response レスポンスのIntent
     * @return resultの値
     */
    private int getResult(final Intent response) {
        int result = response.getIntExtra(DConnectMessage.EXTRA_RESULT,
                DConnectMessage.RESULT_ERROR);
        return result;
    }

    /**
     * エラーコードを取得する.
     * @param response レスポンス
     * @return エラーコード
     */
    private int getErrorCode(final Intent response) {
        int code = response.getIntExtra(DConnectMessage.EXTRA_ERROR_CODE,
                DConnectMessage.ErrorCode.UNKNOWN.getCode());
        return code;
    }

    /**
     * エラーメッセージを取得する.
     * @param response レスポンス
     * @return エラーメッセージ
     */
    private String getErrorMessage(final Intent response) {
        String msg = response.getStringExtra(DConnectMessage.EXTRA_ERROR_MESSAGE);
        return msg;
    }
    /**
     * 各デバイスからのレスポンスを待つ.
     * 
     * この関数から返答があるのは以下の条件になる。
     * <ul>
     * <li>デバイスプラグインからレスポンスがあった場合
     * <li>指定された時間無いにレスポンスが返ってこない場合
     * </ul>
     */
    private void waitForResponse() {
        synchronized (mLockObj) {
            try {
                mLockObj.wait(mTimeout);
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
