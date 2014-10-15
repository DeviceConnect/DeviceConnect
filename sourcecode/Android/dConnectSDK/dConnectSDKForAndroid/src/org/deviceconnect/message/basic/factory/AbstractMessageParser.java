/*
 AbstractMessageParser.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.message.basic.factory;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.deviceconnect.message.factory.MessageHandler;
import org.deviceconnect.message.factory.MessageParser;

/**
 * メッセージパーサ.
 * @param <M> メッセージクラス
 * @author NTT DOCOMO, INC.
 */
public abstract class AbstractMessageParser<M> implements MessageParser<M> {

    /**
     * ロガー.
     */
    private Logger mLogger = Logger.getLogger("org.deviceconnect.sdk");

    /**
     * メッセージハンドラ.
     */
    private MessageHandler mHandler;

    @Override
    public void parse(final M message, final MessageHandler handler) {
        mLogger.entering(getClass().getName(), "parse");

        if (message == null) {
            throw new NullPointerException("message is null");
        }

        if (handler == null) {
            throw new NullPointerException("message handler is null");
        }

        mHandler = handler;

        mHandler.startParse();

        parseObject(message, handler);

        mHandler.endParse();

        mLogger.exiting(getClass().getName(), "parse");
    }

    /**
     * キーセットを取得する.
     * @param message メッセージ
     * @return キーセット
     */
    protected abstract Set<String> getKeySet(final Object message);

    /**
     * 値を取得する.
     * @param message メッセージ
     * @param key キー
     * @return 値
     */
    protected abstract Object getValue(final Object message, final String key);

    /**
     * 値がマップかどうかを判定する.
     * @param value 値
     * @return マップかどうか
     */
    protected abstract boolean isMap(final Object value);

    /**
     * 値が配列かどうかを判定する.
     * @param value 値
     * @return 配列かどうか
     */
    protected boolean isArray(final Object value) {
        mLogger.entering(getClass().getName(), "isArray", value);

        boolean array = false;
        if (value != null) {
            array = value.getClass().isArray();
        }

        mLogger.exiting(getClass().getName(), "isArray", array);
        return array;
    }

    /**
     * 配列として値を取得する.
     * @param value 値
     * @return 配列
     */
    protected List<?> toArray(final Object value) {
        mLogger.entering(getClass().getName(), "toArray", value);

        List<?> list = Arrays.asList(value);

        mLogger.exiting(getClass().getName(), "toArray", list);
        return list;
    }

    /**
     * オブジェクトをパースする.
     * @param object オブジェクト
     * @param handler メッセージパースハンドラ
     */
    private void parseObject(final Object object, final MessageHandler handler) {
        // DON'T PRINT, IT THROWS STACK OVER FLOW EXCEPTION
        // mLogger.entering(getClass().getName(), "parseObject",
        //        new Object[] { object, handler });
        mLogger.entering(getClass().getName(), "parseObject");

        if (isMap(object)) {
            handler.startMap();
            for (String key : getKeySet(object)) {
                handler.onKey(key);
                parseObject(getValue(object, key), handler);
            }
            handler.endMap();
        } else if (isArray(object)) {
            handler.startArray();
            for (Object obj : toArray(object)) {
                parseObject(obj, handler);
            }
            handler.endArray();
        } else {
            handler.onValue(object);
        }

        mLogger.exiting(getClass().getName(), "parseObject");
    }

}
