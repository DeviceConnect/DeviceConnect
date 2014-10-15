/*
DConnectMessageHandler
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

package org.deviceconnect.android.deviceplugin.hue.util;

import org.deviceconnect.message.DConnectMessage;


/**
 * d-Connect メッセージハンドラー.
 * @author NTT DOCOMO, INC.
 */
public interface DConnectMessageHandler {

    /**
     * イベント受信ハンドラー.
     * 
     * @param message イベントメッセージ
     */
    void handleMessage(final DConnectMessage message);

}
