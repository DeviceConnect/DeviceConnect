/*
 MessageParser.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.message.factory;


/**
 * メッセージパーサ.
 * @param <M> メッセージクラス
 * @author NTT DOCOMO, INC.
 */
public interface MessageParser<M> {

    /**
     * メッセージをパースする.
     * @param message メッセージ
     * @param handler メッセージパースハンドラ
     */
    void parse(M message, MessageHandler handler);

}
