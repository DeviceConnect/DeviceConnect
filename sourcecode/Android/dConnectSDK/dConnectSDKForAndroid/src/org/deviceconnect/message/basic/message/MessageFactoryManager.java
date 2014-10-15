/*
 MessageFactoryManager.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.message.basic.message;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.message.factory.MessageFactory;

/**
 * メッセージファクタリーマネージャー.
 * @author NTT DOCOMO, INC.
 */
public class MessageFactoryManager {

    /**
     * メッセージファクトリーリスト.
     */
    private List<MessageFactory<?>> mFactoryList = new ArrayList<MessageFactory<?>>();

    /**
     * ロガー.
     */
    private Logger mLogger = Logger.getLogger("org.deviceconnect.sdk");

    /**
     * 新しいパッケージメッセージを生成する.
     * @param <M> メッセージクラス
     * @param message パッケージメッセージ
     * @param destClass パッケージクラス
     * @return パッケージメッセージ
     */
    public <M> M newPackagedMessage(final Object message, final Class<M> destClass) {
        mLogger.entering(getClass().getName(), "newPackagedMessage",
                new Object[]{message, destClass});

        MessageFactory<M> messageFactory = getMessageFactory(destClass);
        if (messageFactory == null) {
            throw new IllegalArgumentException("unknown dest type: " + destClass.getClass());
        }

        M pmessage = messageFactory.newPackagedMessage(newDConnectMessage(message));

        mLogger.exiting(getClass().getName(), "newPackagedMessage", pmessage);
        return pmessage;
    }

    /**
     * 新しいメッセージを生成する.
     * @param source パッケージメッセージ
     * @return メッセージ
     */
    public DConnectMessage newDConnectMessage(final Object source) {
        mLogger.entering(getClass().getName(), "newDConnectMessage", source);

        if (source == null) {
            throw new NullPointerException("source is null");
        }

        if (source instanceof DConnectMessage) {
            mLogger.exiting(getClass().getName(), "newDConnectMessage", source);
            return (DConnectMessage) source;
        }

        MessageFactory<?> factory = getMessageFactory(source.getClass());
        if (factory == null) {
            throw new IllegalArgumentException("unkown source type: " + source.getClass());
        }

        DConnectMessage message = null;
        try {
            Method[] methods = factory.getClass().getMethods();
            for (Method method: methods) {
                String name = method.getName();
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (name.equals("newDConnectMessage")
                        && parameterTypes.length == 1
                        && parameterTypes[0].isInstance(source)) {
                    message = (DConnectMessage) method.invoke(factory, source);
                    break;
                }
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }

        mLogger.exiting(getClass().getName(), "newDConnectMessage", message);
        return message;
    }

    /**
     * メッセージファクトリーを追加する.
     * @param factory メッセージファクトリー.
     */
    public void addMessageFactory(final MessageFactory<?> factory) {
        mLogger.entering(getClass().getName(), "addMessageFactory", factory);

        if (factory == null) {
            throw new NullPointerException("factory is null.");
        }

        mFactoryList.add(factory);
        mLogger.exiting(getClass().getName(), "addMessageFactory");
    }

    /**
     * メッセージファクトリーを取得する.
     * @param <M> メッセージクラス
     * @param clazz メッセージクラス
     * @return メッセージファクトリー
     */
    @SuppressWarnings("unchecked")
    public <M> MessageFactory<M> getMessageFactory(final Class<M> clazz) {
        mLogger.entering(getClass().getName(), "getMessageFactory", clazz);

        if (clazz == null) {
            throw new NullPointerException("clazz is null.");
        }

        MessageFactory<?> target = null;
        for (MessageFactory<?> factory: mFactoryList) {
            if (factory.getPackagedClass().isAssignableFrom(clazz)) {
                target = factory;
                break;
            }
        }

        mLogger.exiting(getClass().getName(), "getMessageFactory", target);
        return (MessageFactory<M>) target;
    }

    /**
     * メッセージファクトリーを削除する.
     * @param factory メッセージファクトリー.
     */
    public void removeMessageFactory(final MessageFactory<?> factory) {
        mLogger.entering(getClass().getName(), "removeMessageFactory", factory);

        if (factory == null) {
            throw new NullPointerException("factory is null.");
        }

        mFactoryList.remove(factory);
        mLogger.exiting(getClass().getName(), "removeMessageFactory");
    }

    /**
     * メッセージファクトリーを削除する.
     * @param clazz メッセージクラス.
     */
    public void removeMessageFactory(final Class<?> clazz) {
        mLogger.entering(getClass().getName(), "removeMessageFactory", clazz);

        if (clazz == null) {
            throw new NullPointerException("clazz is null.");
        }

        MessageFactory<?> target = getMessageFactory(clazz);
        if (target != null) {
            removeMessageFactory(target);
        }

        mLogger.exiting(getClass().getName(), "removeMessageFactory");
    }

}
