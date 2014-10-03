package com.nttdocomo.android.dconnect.deviceplugin.sonycamera.utils;

import com.nttdocomo.dconnect.message.DConnectMessage;

/**
DConnectMessageHandler
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/
public interface DConnectMessageHandler {

    /**
     * イベント受信ハンドラー.
     * @param message イベントメッセージ
     */
    void handleMessage(final DConnectMessage message);

}
