/*
 AbstractMessageClient.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.message.basic.client;

import java.io.IOException;

import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.message.client.MessageClient;
import org.deviceconnect.message.factory.MessageFactory;

/**
 * DConnectMessage、及び任意のメッセージを実行するクライアントクラス.
 * このクラスのサブクラスはメッセージのファクトリークラスを提供しなければならない。
 * 
 * @param <Q> リクエスト
 * @param <S> レスポンス
 * @author NTT DOCOMO, INC.
 */
public abstract class AbstractMessageClient<Q, S> extends AbstractDConnectClient implements MessageClient<Q, S> {

    @Override
    public S execute(final Q request) throws IOException {

        DConnectMessage drequest = getRequestMessageFactory().newDConnectMessage(request);
        DConnectMessage dresponse = execute(drequest);

        S response = getResponseMessageFactory().newPackagedMessage(dresponse);

        return response;
    }

    /**
     * リクエストメッセージファクトリーを取得する.
     * 
     * @return リクエストメッセージファクトリー
     */
    protected abstract MessageFactory<Q> getRequestMessageFactory();

    /**
     * レスポンスメッセージファクトリーを取得する.
     * 
     * @return レスポンスメッセージファクトリー
     */
    protected abstract MessageFactory<S> getResponseMessageFactory();

}
