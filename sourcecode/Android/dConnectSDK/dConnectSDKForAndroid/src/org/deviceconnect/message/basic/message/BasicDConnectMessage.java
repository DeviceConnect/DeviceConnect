/*
 BasicDConnectMessage.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.message.basic.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.message.basic.factory.DConnectMessageParser;
import org.deviceconnect.message.factory.MessageHandler;
import org.deviceconnect.message.factory.MessageParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * メッセージ.
 * @author NTT DOCOMO, INC.
 */
public class BasicDConnectMessage extends HashMap<String, Object> implements DConnectMessage {

    /**
     * シリアルバージョン.
     */
    private static final long serialVersionUID = 1L;

    /**
     * ロガーインスタンス.
     */
    private static Logger mLogger = Logger.getLogger("org.deviceconnect.sdk");

    /**
     * コンストラクタ.
     */
    public BasicDConnectMessage() {
        super();
        mLogger.entering(getClass().getName(), "DefaultDConnectMessage");
        mLogger.exiting(getClass().getName(), "DefaultDConnectMessage");
    }

    /**
     * メッセージをJSONから生成する.
     *
     * @param json メッセージJSON
     * @throws JSONException JSONエラー.
     */
    public BasicDConnectMessage(final String json) throws JSONException {
        this(new JSONObject(json));
        mLogger.entering(getClass().getName(), "DefaultDConnectMessage", json);
        mLogger.exiting(getClass().getName(), "DefaultDConnectMessage");
    }

    /**
     * メッセージをJSONから生成する.
     *
     * @param json メッセージJSON
     * @throws JSONException JSONエラー.
     */
    @SuppressWarnings("unchecked")
    public BasicDConnectMessage(final JSONObject json) throws JSONException {
        this((Map<String, Object>) parseJSONObject(json));
        mLogger.entering(getClass().getName(), "DefaultDConnectMessage", json);
        mLogger.exiting(getClass().getName(), "DefaultDConnectMessage");
    }

    /**
     * メッセージをMapから生成する.
     *
     * @param map メッセージMap
     */
    public BasicDConnectMessage(final Map<String, Object> map) {
        super(map);
        mLogger.entering(getClass().getName(), "DefaultDConnectMessage", map);
        mLogger.exiting(getClass().getName(), "DefaultDConnectMessage");
    }

    /**
     * Stringを取得する.
     * @param key キー
     * @return 値
     */
    public String getString(final String key) {
        mLogger.entering(getClass().getName(), "getString", key);
        if (!containsKey(key)) {
            mLogger.exiting(getClass().getName(), "getString", null);
            return null;
        }

        Object value = get(key);
        if (value != null && !(value instanceof String)) {
            mLogger.fine("value is not String: " + value.getClass().getName());
            mLogger.exiting(getClass().getName(), "getString", null);
            return null;
        }

        mLogger.exiting(getClass().getName(), "getString", value);
        return (String) value;
    }

    /**
     * intを取得する.
     * @param key キー
     * @return 値
     */
    public int getInt(final String key) {
        mLogger.entering(getClass().getName(), "getInt", key);
        if (!containsKey(key)) {
            mLogger.exiting(getClass().getName(), "getInt", 0);
            return 0;
        }

        Object value = get(key);
        if (value == null || !(value instanceof Integer)) {
            if (value == null) {
                mLogger.fine("value is not Integer: null");
            } else {
                mLogger.fine("value is not Integer: " + value.getClass().getName());
            }
            mLogger.exiting(getClass().getName(), "getInt", 0);
            return 0;
        }

        mLogger.exiting(getClass().getName(), "getInt", value);
        return (Integer) value;
    }

    /**
     * booleanを取得する.
     * @param key キー
     * @return 値
     */
    public boolean getBoolean(final String key) {
        mLogger.entering(getClass().getName(), "getBoolean", key);
        if (!containsKey(key)) {
            mLogger.exiting(getClass().getName(), "getBoolean", false);
            return false;
        }

        Object value = get(key);
        if (value == null || !(value instanceof Boolean)) {
            if (value == null) {
                mLogger.fine("value is not Boolean: null");
            } else {
                mLogger.fine("value is not Boolean: " + value.getClass().getName());
            }
            mLogger.exiting(getClass().getName(), "getBoolean", false);
            return false;
        }

        mLogger.exiting(getClass().getName(), "getBoolean", value);
        return (Boolean) value;
    }

    /**
     * doubleを取得する.
     * @param key キー
     * @return 値
     */
    public float getFloat(final String key) {
        mLogger.entering(getClass().getName(), "getFloat", key);
        if (!containsKey(key)) {
            mLogger.exiting(getClass().getName(), "getFloat", 0);
            return 0;
        }

        Object value = get(key);
        if (value == null || !(value instanceof Float)) {
            if (value == null) {
                mLogger.fine("value is not Float: null");
            } else {
                mLogger.fine("value is not Float: " + value.getClass().getName());
            }
            mLogger.exiting(getClass().getName(), "getFloat", 0f);
            return 0f;
        }

        mLogger.exiting(getClass().getName(), "getFloat", value);
        return (Float) value;
    }

    @Override
    public List<Object> getList(final String key) {
        mLogger.entering(getClass().getName(), "getList", key);
        if (!containsKey(key)) {
            return null;
        }

        Object value = get(key);
        if (value == null ||  !(value instanceof List<?>)) {
            if (value == null) {
                mLogger.fine("value is not List<?>: null");
            } else {
                mLogger.fine("value is not List<?>: " + value.getClass().getName());
            }
            mLogger.exiting(getClass().getName(), "getList", null);
            return null;
        }

        mLogger.exiting(getClass().getName(), "getList", get(key));
        return castListObject((List<?>) value);
    }

    @Override
    public String toString() {
        return toString(0);
    }

    @Override
    public String toString(final int indent) {
        final StringBuilder builder = new StringBuilder();

        MessageParser<DConnectMessage> parser = new DConnectMessageParser();
        parser.parse(this, new MessageHandler() {
            private boolean mFirstKey;
            private int mParseInArray;
            private int mFirstArrayValue;
            private int mIndent;
            @Override
            public void startParse() {
            }
            @Override
            public void onKey(final String key) {
                if (!mFirstKey) {
                    builder.append(",");
                }
                appendIndentSpace();

                mFirstKey = false;
                builder.append("\"");
                builder.append(key);
                builder.append("\"");
                builder.append(":");
            }
            @Override
            public void onValue(final Object value) {
                if ((mParseInArray & (1 << mIndent)) != 0) {
                    if ((mFirstArrayValue & (1 << mIndent)) != 0) {
                        builder.append(",");
                    }
                    appendIndentSpace();
                }
                mFirstArrayValue |= (1 << mIndent);
                if (Integer.class.isInstance(value)
                        || Float.class.isInstance(value)
                        || Double.class.isInstance(value)
                        || Long.class.isInstance(value)
                        || Byte.class.isInstance(value)
                        || Short.class.isInstance(value)
                        || Boolean.class.isInstance(value)) {
                    builder.append(value);
                } else if (value == null) {
                    builder.append("null");
                } else {
                    builder.append("\"");
                    builder.append(value);
                    builder.append("\"");
                }
            }
            @Override
            public void startMap() {
                if ((mFirstArrayValue & (1 << mIndent)) != 0) {
                    builder.append(",");
                }
                appendIndentSpace();
                builder.append("{");

                mFirstKey = true;
                mIndent++;
            }
            @Override
            public void endMap() {
                mFirstArrayValue &= ~(1 << mIndent);
                mIndent--;
                mFirstArrayValue |= (1 << mIndent);

                appendIndentSpace();
                builder.append("}");
            }
            @Override
            public void startArray() {
                mIndent++;
                if ((mFirstArrayValue & (1 << mIndent)) != 0) {
                    builder.append(",");
                }
                mParseInArray |= (1 << mIndent);
                builder.append("[");
            }
            @Override
            public void endArray() {
                mParseInArray &= ~(1 << mIndent);
                mFirstArrayValue &= ~(1 << mIndent);
                mIndent--;
                mFirstArrayValue |= (1 << mIndent);

                appendIndentSpace();
                builder.append("]");
            }
            @Override
            public void endParse() {
            }

            private void appendIndentSpace() {
                if (indent <= 0) {
                    return;
                }
                if (builder.length() > 0) {
                    appendBreakLine();
                }
                for (int i = 0; i < mIndent * indent; i++) {
                    builder.append(" ");
                }
            }
            private void appendBreakLine() {
                if (indent <= 0) {
                    return;
                }
                builder.append("\n");
            }
        });

        return builder.toString();
    }

    /**
     * JSONObjectをパースしてオブジェクトとして返却する.
     * @param json パース対象JSONObject
     * @return オブジェクト
     * @throws JSONException JSONエラーが発生した場合
     */
    private static Object parseJSONObject(final Object json) throws JSONException {
        mLogger.entering(BasicDConnectMessage.class.getName(), "parseJSONObject", json);

        Object object = null;

        if (json == JSONObject.NULL) {
            return null;
        } else if (json instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) json;

            Map<String, Object> map = new HashMap<String, Object>();
            JSONArray names = jsonObject.names();
            if (names != null) {
                int length = names.length();
                for (int i = 0; i < length; i++) {
                    String name = names.getString(i);
                    map.put(name, parseJSONObject(jsonObject.get(name)));
                }
            }

            object = map;
        } else if (json instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) json;

            int length = jsonArray.length();
            List<Object> array = new ArrayList<Object>(length);
            for (int i = 0; i < length; i++) {
                array.add(parseJSONObject(jsonArray.get(i)));
            }

            object = array;
        } else {
            object = json;
        }

        mLogger.exiting(BasicDConnectMessage.class.getName(), "parseJSONObject", object);
        return object;
    }

    /**
     * List<Object>へキャストする.
     * @param list リスト
     * @return キャストされたリスト
     */
    @SuppressWarnings("unchecked")
    private List<Object> castListObject(final List<?> list) {
        return (List<Object>) list;
    }

}
