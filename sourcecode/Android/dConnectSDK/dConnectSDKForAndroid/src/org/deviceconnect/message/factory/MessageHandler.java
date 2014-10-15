/*
 MessageHandler.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.message.factory;

/**
 * メッセージパーサハンドラ.
 * @author NTT DOCOMO, INC.
 */
public interface MessageHandler {

    /**
     * パース開始.
     */
    void startParse();

    /**
     * キー.
     * @param key キー
     */
    void onKey(String key);

    /**
     * 値.
     * @param value 値
     */
    void onValue(Object value);

    /**
     * 配列開始.
     */
    void startArray();

    /**
     * 配列終了.
     */
    void endArray();

    /**
     * マップ開始.
     */
    void startMap();

    /**
     * マップ終了.
     */
    void endMap();

    /**
     * パース終了.
     */
    void endParse();

}
