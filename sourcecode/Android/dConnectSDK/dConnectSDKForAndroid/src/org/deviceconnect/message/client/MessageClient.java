/*
 MessageClient.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.message.client;

import java.io.IOException;

/**
 * 任意のリクエストメッセージを実行するクライアントの共通機能を定義するインターフェース.
 * MessageClientの実装クラスはリクエストの実行メソッドを提供し、任意のレスポンスメッセージを
 * 返信しなければならない。
 * 
 * @param <Q> リクエスト
 * @param <S> レスポンス
 * @author NTT DOCOMO, INC.
 */
public interface MessageClient<Q, S> {

    /**
     * リクエストを実行する.
     * @param request リクエスト
     * @return レスポンス
     * @throws IOException I/Oエラーが発生した場合
     */
    S execute(final Q request) throws IOException;
}
