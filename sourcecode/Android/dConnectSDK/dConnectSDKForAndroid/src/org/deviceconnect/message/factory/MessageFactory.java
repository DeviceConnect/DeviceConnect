/*
 MessageFactory.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.message.factory;

import org.deviceconnect.message.DConnectMessage;

/**
 * メッセージファクトリー.
 *
 * @param <T> メッセージ型
 * @author NTT DOCOMO, INC.
 */
public interface MessageFactory<T> {

    /**
     * パッケージ型のクラスを取得する.
     * @return パッケージ型
     */
    Class<T> getPackagedClass();

    /**
     * 新しいパッケージメッセージを作成する.
     * @param message メッセージ
     * @return メッセージ
     */
    T newPackagedMessage(final DConnectMessage message);

    /**
     * 新しいメッセージを作成する.
     * @param source メッセージ
     * @return メッセージ
     */
    DConnectMessage newDConnectMessage(final T source);

}
