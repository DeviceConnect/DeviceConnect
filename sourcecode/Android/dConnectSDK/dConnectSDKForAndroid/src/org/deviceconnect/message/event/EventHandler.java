/*
 EventHandler.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.message.event;

import org.json.JSONObject;

/**
 * イベント受信ハンドラインターフェース.
 * このインターフェースを実装することでイベントの受信ができるようになる。
 * 
 * @author NTT DOCOMO, INC.
 */
public interface EventHandler {
    
    /**
     * イベントメッセージの受信を通知します.
     * 
     * @param event イベントメッセージのJSONデータ
     */
    void onEvent(JSONObject event);
}
