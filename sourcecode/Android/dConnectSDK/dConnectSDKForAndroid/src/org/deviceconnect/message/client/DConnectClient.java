/*
 DConnectClient.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.message.client;

import java.io.IOException;

import org.apache.http.client.HttpClient;
import org.deviceconnect.message.DConnectMessage;

/**
 * DConnectMessageを実行するクライアントの共通機能を定義するインターフェース.
 * DConnectClientの実装クラスはリクエストの実行メソッドを提供し、レスポンスメッセージを
 * 返信しなければならない。
 * @author NTT DOCOMO, INC.
 */
public interface DConnectClient extends HttpClient {

    /**
     * リクエストを実行する.
     * @param request リクエスト
     * @return レスポンス
     * @throws IOException I/Oエラーが発生した場合
     */
    DConnectMessage execute(final DConnectMessage request) throws IOException;
}
