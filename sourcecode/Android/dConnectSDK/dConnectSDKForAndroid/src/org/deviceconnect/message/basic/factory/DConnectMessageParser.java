/*
 DConnectMessageParser.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.message.basic.factory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.deviceconnect.message.DConnectMessage;

/**
 * デフォルトdConnectメッセージパーサ.
 * @author NTT DOCOMO, INC.
 */
public class DConnectMessageParser extends AbstractMessageParser<DConnectMessage> {

    /**
     * ロガー.
     */
    private Logger mLogger = Logger.getLogger("org.deviceconnect.sdk");

    @SuppressWarnings("unchecked")
    @Override
    protected Set<String> getKeySet(final Object message) {

        Set<String> keySet = null;

        if (message instanceof Map) {
            keySet = ((Map<String, Object>) message).keySet();
        }

        return keySet;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Object getValue(final Object message, final String key) {
        mLogger.entering(getClass().getName(), "getValue", key);

        Object value = ((Map<String, Object>) message).get(key);

        mLogger.exiting(getClass().getName(), "getValue", value);
        return value;
    }

    @Override
    protected boolean isArray(final Object value) {
        mLogger.entering(getClass().getName(), "isArray");

        boolean array = super.isArray(value);
        if (!array && value instanceof List) {
            array = true;
        }

        mLogger.exiting(getClass().getName(), "isArray", array);
        return array;
    }

    @Override
    protected boolean isMap(final Object value) {
        mLogger.entering(getClass().getName(), "isMap");

        boolean map = false;

        if (value instanceof Map) {
            map = true;
        }

        mLogger.exiting(getClass().getName(), "isMap", map);
        return map;
    }

    @Override
    protected List<?> toArray(final Object value) {
        mLogger.entering(getClass().getName(), "toArray");

        List<?> list = (List<?>) value;

        mLogger.exiting(getClass().getName(), "toArray", list);
        return list;
    }

}
