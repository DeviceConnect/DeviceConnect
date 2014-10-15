/*
 CloseHandler.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.message.http.event;

/**
 * 通信の接続状態に関する通知をハンドルするインターフェース.
 * このインターフェースを実装することでコネクションの切断を感知することができる。
 *
 * @author NTT DOCOMO, INC.
 */
public interface CloseHandler {

    /**
     * コネクションが遮断されたことを通知します.
     */
    void onClosed();
}
