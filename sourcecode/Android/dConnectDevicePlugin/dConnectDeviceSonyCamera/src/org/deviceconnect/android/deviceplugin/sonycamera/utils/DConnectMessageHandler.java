/*
DConnectMessageHandler
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

package org.deviceconnect.android.deviceplugin.sonycamera.utils;

import org.deviceconnect.message.DConnectMessage;

/**
 * SonyCameraデバイスプラグイン.
 */
public interface DConnectMessageHandler {

    /**
     * イベント受信ハンドラー.
     * @param message イベントメッセージ
     */
    void handleMessage(final DConnectMessage message);

}
