/*
 JSONFactory.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.message.intent.util;

import java.util.List;

import org.deviceconnect.message.intent.message.IntentDConnectMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Parcelable;

/**
 * JSONファクトリークラス.
 * JSONデータを生成する機能を提供する。
 * 
 *
 * @author NTT DOCOMO, INC.
 */
public final class JSONFactory {

    /**
     * ユーティリティクラスなのでprivate.
     */
    private JSONFactory() {
    }
    
    /**
     * BundleをJSONObjectに変換する.
     * 
     * @param bundle 変換するBundle
     * @return 変換後のJSONデータ
     * @throws JSONException JSONへの変換に失敗した場合に発生
     */
    public static JSONObject convertBundleToJSON(final Bundle bundle) throws JSONException {
        JSONObject json = new JSONObject();
        convertBundleToJSON(json, bundle);
        return json;
    }

    /**
     * BundleからJSONObjectに変換する.
     * @param root JSONObjectに変換したデータを格納するオブジェクト
     * @param b 変換するBundle
     * @throws JSONException JSONへの変換に失敗した場合に発生
     */
    public static void convertBundleToJSON(
            final JSONObject root, final Bundle b) throws JSONException {
        
        if (root == null || b == null) {
            return;
        }
        
        for (String key : b.keySet()) {
            Object value = b.get(key);
            if (key.equals(IntentDConnectMessage.EXTRA_REQUEST_CODE)) {
                // request_codeはRESTfulにはいらないので削除しておく
                continue;
            } else if (value instanceof Integer[] || value instanceof Long[] || value instanceof Short[]
                    || value instanceof Byte[] || value instanceof Character[] || value instanceof Float[]
                    || value instanceof Double[] || value instanceof Boolean[] || value instanceof String[]) {
                Object[] bb = (Object[]) value;
                JSONArray array = new JSONArray();
                for (int i = 0; i < bb.length; i++) {
                    array.put(bb[i]);
                }
                root.put(key, array);
            } else if (value instanceof Integer) {
                root.put(key, ((Integer) value).intValue());
            } else if (value instanceof int[]) {
                int[] bb = (int[]) value;
                JSONArray array = new JSONArray();
                for (int i = 0; i < bb.length; i++) {
                    array.put(bb[i]);
                }
                root.put(key, array);
            } else if (value instanceof Short) {
                root.put(key, ((Short) value).shortValue());
            } else if (value instanceof short[]) {
                short[] bb = (short[]) value;
                JSONArray array = new JSONArray();
                for (int i = 0; i < bb.length; i++) {
                    array.put(bb[i]);
                }
                root.put(key, array);
            } else if (value instanceof Character) {
                root.put(key, ((Character) value).charValue());
            } else if (value instanceof char[]) {
                char[] bb = (char[]) value;
                JSONArray array = new JSONArray();
                for (int i = 0; i < bb.length; i++) {
                    array.put(bb[i]);
                }
                root.put(key, array);
            } else if (value instanceof Byte) {
                root.put(key, ((Byte) value).byteValue());
            } else if (value instanceof byte[]) {
                byte[] bb = (byte[]) value;
                JSONArray array = new JSONArray();
                for (int i = 0; i < bb.length; i++) {
                    array.put(bb[i]);
                }
                root.put(key, array);
            } else if (value instanceof Long) {
                root.put(key, ((Long) value).longValue());
            } else if (value instanceof long[]) {
                long[] bb = (long[]) value;
                JSONArray array = new JSONArray();
                for (int i = 0; i < bb.length; i++) {
                    array.put(bb[i]);
                }
                root.put(key, array);
            } else if (value instanceof Float) {
                root.put(key, ((Float) value).floatValue());
            } else if (value instanceof float[]) {
                float[] bb = (float[]) value;
                JSONArray array = new JSONArray();
                for (int i = 0; i < bb.length; i++) {
                    array.put(bb[i]);
                }
                root.put(key, array);
            } else if (value instanceof Double) {
                root.put(key, ((Double) value).doubleValue());
            } else if (value instanceof double[]) {
                double[] bb = (double[]) value;
                JSONArray array = new JSONArray();
                for (int i = 0; i < bb.length; i++) {
                    array.put(bb[i]);
                }
                root.put(key, array);
            } else if (value instanceof Boolean) {
                root.put(key, ((Boolean) value).booleanValue());
            } else if (value instanceof boolean[]) {
                boolean[] bb = (boolean[]) value;
                JSONArray array = new JSONArray();
                for (int i = 0; i < bb.length; i++) {
                    array.put(bb[i]);
                }
                root.put(key, array);
            } else if (value instanceof String) {
                root.put(key, (String) value);
            } else if (value instanceof Bundle) {
                JSONObject obj = new JSONObject();
                convertBundleToJSON(obj, (Bundle) value);
                root.put(key, obj);
            } else if (value instanceof Bundle[]) {
                Bundle[] bb = (Bundle[]) value;
                JSONArray array = new JSONArray();
                for (int i = 0; i < bb.length; i++) {
                    JSONObject obj = new JSONObject();
                    convertBundleToJSON(obj, bb[i]);
                    array.put(obj);
                }
                root.put(key, array);
            } else if (value instanceof Parcelable[]) {
                Parcelable[] bb = (Parcelable[]) value;
                JSONArray array = new JSONArray();
                for (int i = 0; i < bb.length; i++) {
                    JSONObject obj = new JSONObject();
                    if (bb[i] instanceof Bundle) {
                        convertBundleToJSON(obj, (Bundle) bb[i]);
                    }
                    array.put(obj);
                }
                root.put(key, array);
            } else if (value instanceof Object[]) {
                // プリミティブ型のラッパークラスの配列がObject[]として扱われる場合への対処
                Object[] bb = (Object[]) value;
                if (isPrimitiveWrapperArray(bb)) {
                    JSONArray array = new JSONArray();
                    for (int i = 0; i < bb.length; i++) {
                        array.put(bb[i]);
                    }
                    root.put(key, array);
                }
            } else if (value instanceof List<?>) {
                List<?> bb = (List<?>) value;
                JSONArray array = new JSONArray();
                for (int i = 0; i < bb.size(); i++) {
                    Object v = bb.get(i);
                    if (v instanceof Bundle) {
                        JSONObject obj = new JSONObject();
                        convertBundleToJSON(obj, (Bundle) bb.get(i));
                        array.put(obj);
                    } else if (v instanceof Parcelable) {
                        JSONObject obj = new JSONObject();
                        convertBundleToJSON(obj, (Bundle) bb.get(i));
                        array.put(obj);
                    } else {
                        array.put(bb.get(i));
                    }
                }
                root.put(key, array);
            }
        }
    }

    /**
     * 指定したObject[]がプリミティブ型のラッパークラスの配列であるかどうかをチェックする.
     * <p>
     * なお、配列のすべての要素の型が同一でない場合、falseを返す.
     * 例えば、以下のような場合.
     * </p>
     * <pre>
     * {new Integer(0), new Double(0.0d)} // falseを返す
     * </pre>
     * @param array チェックするオブジェクト配列
     * @return プリミティブ型のラッパークラスの配列である場合はtrue、そうでない場合はfalse
     */
    private static boolean isPrimitiveWrapperArray(final Object[] array) {
        String classNameCache = null;
        for (int i = 0; i < array.length; i++) {
            Object obj = array[i];
            if (obj != null) {
                if (isPrimitiveWrapper(obj)) {
                    String className = obj.getClass().getName();
                    if (classNameCache != null) {
                        if (!classNameCache.equals(className)) {
                            return false;
                        }
                    } else {
                        classNameCache = className;
                    }
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 指定したObjectがプリミティブ型のラッパークラスであるかどうかをチェックする.
     * 
     * @param obj チェックするオブジェクト
     * @return プリミティブ型のラッパークラスである場合はtrue、そうでない場合はfalse
     */
    private static boolean isPrimitiveWrapper(final Object obj) {
        return obj instanceof Byte || obj instanceof Short || obj instanceof Integer
                || obj instanceof Long || obj instanceof Float || obj instanceof Double
                || obj instanceof Character || obj instanceof Boolean;
    }
}
