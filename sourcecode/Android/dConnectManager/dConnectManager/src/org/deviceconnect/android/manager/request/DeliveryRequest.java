/*
 DeliveryRequest.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.manager.request;

import java.util.logging.Logger;

import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.message.intent.message.IntentDConnectMessage;

import android.content.Intent;

/**
 * 指定されたリクエストメッセージを各デバイスプラグインに配送するDConnectRequset実装クラス.
 * @author NTT DOCOMO, INC.
 */
public class DeliveryRequest extends LocalOAuthRequest {
    /** ロガー. */
    private final Logger sLogger = Logger.getLogger("dconnect.manager");

    /**
     * 実際の命令を行う.
     * 
     * 有効期限切れの場合には、アクセストークンを取得してから
     * 再度リクエストを実行する。
     * 
     * @param accessToken アクセストークン
     */
    protected void executeRequest(final String accessToken) {
        // 命令を実行する前にレスポンスを初期化しておく
        mResponse = null;

        sLogger.info("Delivery Request: " + mDevicePlugin.getPackageName() 
                + ", intent: " + mRequest.getExtras());

        // 命令をデバイスプラグインに送信
        Intent request = createRequestMessage(mRequest, mDevicePlugin);
        request.setComponent(mDevicePlugin.getComponentName());
        request.putExtra(IntentDConnectMessage.EXTRA_REQUEST_CODE, mRequestCode);
        if (accessToken != null) {
            request.putExtra(DConnectMessage.EXTRA_ACCESS_TOKEN, accessToken);
        }
        mContext.sendBroadcast(request);

        if (mResponse == null) {
            // 各デバイスのレスポンスを待つ
            waitForResponse();
        }

        // レスポンスを解析して、処理を行う
        if (mResponse != null) {
            int result = getResult(mResponse);
            if (result == DConnectMessage.RESULT_ERROR) {
                retryCount++;
                int errorCode = getErrorCode(mResponse);
                if (retryCount < MAX_RETRY_COUNT 
                        && errorCode == DConnectMessage.ErrorCode.NOT_FOUND_CLIENT_ID.getCode()) {
                    // クライアントIDが発見できなかった場合は、dConnectManagerとデバイスプラグインで
                    // 一致していないので、dConnectManagerのローカルに保存しているclientIdを削除
                    // してから、再度デバイスプラグインにクライアントIDの作成を要求を行う.
                    String deviceId = mRequest.getStringExtra(DConnectMessage.EXTRA_DEVICE_ID);
                    if (deviceId != null) {
                        mLocalOAuth.deleteOAuthData(deviceId);
                    }
                    executeRequest();
                } else if (retryCount < MAX_RETRY_COUNT 
                        && errorCode == DConnectMessage.ErrorCode.EXPIRED_ACCESS_TOKEN.getCode()) {
                    // アクセストークンの有効期限切れ
                    mLocalOAuth.deleteAccessToken(accessToken);
                    executeRequest();
                } else {
                    sendResponse(mResponse);
                }
            } else {
                sendResponse(mResponse);
            }
        } else {
            sendTimeout();
        }
    }
}
