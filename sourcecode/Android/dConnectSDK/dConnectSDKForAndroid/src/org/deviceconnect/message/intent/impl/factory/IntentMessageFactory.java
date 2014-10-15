/*
 IntentMessageFactory.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.message.intent.impl.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.message.basic.message.DConnectEventMessage;
import org.deviceconnect.message.basic.message.DConnectRequestMessage;
import org.deviceconnect.message.basic.message.DConnectResponseMessage;
import org.deviceconnect.message.factory.MessageFactory;
import org.deviceconnect.message.intent.message.IntentDConnectMessage;

import android.content.Intent;
import android.os.Bundle;

/**
 * Intentメッセージファクトリー.
 * @author NTT DOCOMO, INC.
 */
public class IntentMessageFactory implements MessageFactory<Intent> {

    /**
     * ロガー.
     */
    private Logger mLogger = Logger.getLogger("org.deviceconnect.sdk.android");

    /**
     * メッセージファクトリー.
     */
    private static IntentMessageFactory sMessageFactory;

    /**
     * メッセージファクトリーを取得する.
     * @return メッセージファクトリー
     */
    public static IntentMessageFactory getMessageFactory() {
        Logger.getLogger("org.deviceconnect.sdk.android").entering(
                IntentMessageFactory.class.getName(), "getMessageFactory");

        if (sMessageFactory == null) {
            sMessageFactory = new IntentMessageFactory();
        }

        Logger.getLogger("org.deviceconnect.sdk.android").exiting(
                IntentMessageFactory.class.getName(), "getMessageFactory", sMessageFactory);
        return sMessageFactory;
    }

    @Override
    public Class<Intent> getPackagedClass() {
        return Intent.class;
    }

    @Override
    public Intent newPackagedMessage(final DConnectMessage message) {
        mLogger.entering(this.getClass().getName(), "newPackagedMessage");

        Intent intent;
        if (message instanceof DConnectRequestMessage) {
            intent = newIntentRequestMessage((DConnectRequestMessage) message);
        } else if (message instanceof DConnectResponseMessage) {
            intent = newIntentResponseMessage((DConnectResponseMessage) message);
        } else if (message instanceof DConnectEventMessage) {
            intent = newIntentEventMessage((DConnectEventMessage) message);
        } else {
            throw new IllegalArgumentException("invalid message.");
        }

        for (String key: message.keySet()) {
            Object value = message.get(key);
            if (value instanceof Integer) {
                intent.putExtra(key, (Integer) value);
            } else if (value instanceof Long) {
                intent.putExtra(key, (Long) value);
            } else if (value instanceof Float) {
                intent.putExtra(key, (Float) value);
            } else if (value instanceof Double) {
                intent.putExtra(key, (Double) value);
            } else if (value instanceof Boolean) {
                intent.putExtra(key, (Boolean) value);
            } else if (value instanceof String) {
                intent.putExtra(key, (String) value);
            }
        }

        mLogger.exiting(this.getClass().getName(), "newPackagedMessage", intent);
        return intent;
    }

    @Override
    public DConnectMessage newDConnectMessage(final Intent message) {
        mLogger.entering(this.getClass().getName(), "newDConnectMessage");

        DConnectMessage dmessage;

        String action = message.getAction();
        if (IntentDConnectMessage.ACTION_GET.equals(action)
                || IntentDConnectMessage.ACTION_POST.equals(action)
                || IntentDConnectMessage.ACTION_PUT.equals(action)
                || IntentDConnectMessage.ACTION_DELETE.equals(action)) {
            dmessage = newDConnectRequestMessage(message);
        } else if (IntentDConnectMessage.ACTION_RESPONSE.equals(action)) {
            dmessage = newDConnectResponseMessage(message);
        } else if (IntentDConnectMessage.ACTION_EVENT.equals(action)) { 
            dmessage = newDConnectEventMessage(message);
        } else {
            throw new IllegalArgumentException("message in not intent message.");
        }

        mLogger.exiting(this.getClass().getName(), "newDConnectMessage", message);
        return dmessage;
    }

    /**
     * 新しいリクエストメッセージを作成する.
     * @param request Intentリクエスト
     * @return リクエストメッセージ
     */
    private DConnectRequestMessage newDConnectRequestMessage(final Intent request) {
        mLogger.entering(this.getClass().getName(), "newDConnectRequestMessage");

        DConnectRequestMessage dmessage;
        dmessage = new DConnectRequestMessage();

        // put action
        String action = request.getAction();
        if (IntentDConnectMessage.ACTION_GET.equals(action)) {
            dmessage.setMethod(DConnectRequestMessage.METHOD_GET);
        } else if (IntentDConnectMessage.ACTION_PUT.equals(action)) {
            dmessage.setMethod(DConnectRequestMessage.METHOD_PUT);
        } else if (IntentDConnectMessage.ACTION_POST.equals(action)) {
            dmessage.setMethod(DConnectRequestMessage.METHOD_POST);
        } else if (IntentDConnectMessage.ACTION_DELETE.equals(action)) {
            dmessage.setMethod(DConnectRequestMessage.METHOD_DELETE);
        } else {
            throw new IllegalArgumentException("invalid intent request mehtod: " + action);
        }

        String api = request.getStringExtra(DConnectMessage.EXTRA_API);
        String profile = request.getStringExtra(DConnectMessage.EXTRA_PROFILE);
        String inter = request.getStringExtra(DConnectMessage.EXTRA_INTERFACE);
        String attribute = request.getStringExtra(DConnectMessage.EXTRA_ATTRIBUTE);

        dmessage.setAPI(api);
        dmessage.setProfile(profile);
        dmessage.setInterface(inter);
        dmessage.setAttribute(attribute);

        Bundle extras = request.getExtras();
        if (extras != null) {
            for (String key: extras.keySet()) {
                Object value = extras.get(key);
                dmessage.put(key, value);
            }
        }

        mLogger.exiting(this.getClass().getName(), "newDConnectRequestMessage", dmessage);
        return dmessage;
    }

    /**
     * 新しいレスポンスメッセージを作成する.
     * @param response Intentレスポンス
     * @return レスポンスメッセージ
     */
    private DConnectResponseMessage newDConnectResponseMessage(final Intent response) {
        mLogger.entering(this.getClass().getName(), "newDConnectRequestMessage");

        DConnectResponseMessage dmessage;
        dmessage = new DConnectResponseMessage();

        // put action
        String action = response.getAction();
        if (!IntentDConnectMessage.ACTION_RESPONSE.equals(action)) {
            throw new IllegalArgumentException("invalid intent request mehtod: " + action);
        }

        Bundle extras = response.getExtras();
        if (extras != null) {
            for (String key: extras.keySet()) {
                Object value = getBundleValue(extras, key);
                dmessage.put(key, value);
            }
        }

        mLogger.exiting(this.getClass().getName(), "newDConnectRequestMessage", dmessage);
        return dmessage;
    }
    
    /**
     * 新しいイベントメッセージを作成する.
     * 
     * @param event Intentイベントデータ
     * @return レスポンスメッセージ
     */
    private DConnectEventMessage newDConnectEventMessage(final Intent event) {
        
        mLogger.entering(this.getClass().getName(), "newDConnectEventMessage");

        DConnectEventMessage dmessage = new DConnectEventMessage();
        Bundle extras = event.getExtras();
        if (extras != null) {
            for (String key: extras.keySet()) {
                Object value = extras.get(key);
                dmessage.put(key, value);
            }
        }

        mLogger.exiting(this.getClass().getName(), "newDConnectEventMessage", dmessage);
        return dmessage;
    }

    /**
     * Bundleからkeyの値をプリミティブ型/Map型にして取得する.
     * @param bundle バンドル
     * @param key キー
     * @return 値
     */
    private Object getBundleValue(final Bundle bundle, final String key) {
        mLogger.entering(getClass().getName(), "getBundleValue",
                new Object[] {bundle, key});
        Object value = bundle.get(key);

        Object object = null;
        if (value == null) {
            object = null;
        } else if (value instanceof Integer
                || value instanceof Long
                || value instanceof Float
                || value instanceof Double
                || value instanceof Boolean
                || value instanceof String
                || value instanceof Bundle) {
            object = getPrimitiveValue(value);
        } else if (value.getClass().isArray()) {
            Object[] objArray = (Object[]) value;
            List<Object> array = new ArrayList<Object>(objArray.length);
            for (Object obj: objArray) {
                array.add(getPrimitiveValue(obj));
            }
            object = array;
        }

        mLogger.exiting(getClass().getName(), "getBundleValue", object);
        return object;
    }

    /**
     * オブジェクト(プリミティブ型/Bundle型)をオブジェクトに変換する.
     * @param value オブジェクト(プリミティブ型/Bundle型)
     * @return オブジェクト
     */
    private Object getPrimitiveValue(final Object value) {
        mLogger.entering(getClass().getName(), "getValue", value);

        Object object = null;
        if (value instanceof Integer
                || value instanceof Long
                || value instanceof Float
                || value instanceof Double
                || value instanceof Boolean
                || value instanceof String) {
            object = value;
        } else if (value instanceof Bundle) {
            Bundle b = (Bundle) value;
            Map<String, Object> map = new HashMap<String, Object>();
            for (String k: b.keySet()) {
                Object v = getBundleValue(b, k);
                map.put(k, v);
            }
            object = map;
        }

        mLogger.exiting(getClass().getName(), "getBundleValue", object);
        return object;
    }

    /**
     * 新しいIntentリクエストメッセージを作成する.
     * @param request リクエスト
     * @return Intentリクエストメッセージ
     */
    private Intent newIntentRequestMessage(final DConnectRequestMessage request) {
        mLogger.entering(this.getClass().getName(), "newIntentRequestMessage");

        Intent intent = new Intent();

        String method = request.getMethod();
        if (DConnectMessage.METHOD_GET.equals(method)) {
            intent.setAction(IntentDConnectMessage.ACTION_GET);
        } else if (DConnectMessage.METHOD_PUT.equals(method)) {
            intent.setAction(IntentDConnectMessage.ACTION_PUT);
        } else if (DConnectMessage.METHOD_POST.equals(method)) {
            intent.setAction(IntentDConnectMessage.ACTION_POST);
        } else if (DConnectMessage.METHOD_DELETE.equals(method)) {
            intent.setAction(IntentDConnectMessage.ACTION_DELETE);
        } else {
            throw new IllegalArgumentException("invalid  request method: " + method);
        }

        String api = request.getAPI();
        String profile = request.getProfile();
        String inter = request.getInterface();
        String attribute = request.getAttribute();

        intent.putExtra(DConnectMessage.EXTRA_API, api);
        intent.putExtra(DConnectMessage.EXTRA_PROFILE, profile);
        intent.putExtra(DConnectMessage.EXTRA_INTERFACE, inter);
        intent.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, attribute);

        mLogger.exiting(this.getClass().getName(), "newIntentRequestMessage", intent);
        return intent;
    }

    /**
     * 新しいIntentレスポンスメッセージを作成する.
     * @param response レスポンス
     * @return Intentレスポンスメッセージ
     */
    private Intent newIntentResponseMessage(final DConnectResponseMessage response) {
        mLogger.entering(this.getClass().getName(), "newIntentResponseMessage");

        Intent intent = new Intent();
        intent.setAction(IntentDConnectMessage.ACTION_RESPONSE);

        mLogger.exiting(this.getClass().getName(), "newIntentResponseMessage", intent);
        return intent;
    }
    
    /**
     * 新しいIntentイベントメッセージを作成する.
     * 
     * @param event イベントメッセージ
     * @return Intentイベントメッセージ
     */
    private Intent newIntentEventMessage(final DConnectEventMessage event) {
        mLogger.entering(this.getClass().getName(), "newIntentEventMessage");

        Intent intent = new Intent();
        intent.setAction(IntentDConnectMessage.ACTION_EVENT);

        mLogger.exiting(this.getClass().getName(), "newIntentEventMessage", intent);
        return intent;
        
    }

}
