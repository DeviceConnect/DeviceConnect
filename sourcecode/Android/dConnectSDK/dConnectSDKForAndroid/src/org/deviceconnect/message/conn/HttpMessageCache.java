/*
 HttpMessageCache.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.message.conn;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpMessage;

/**
 * HttpMessageのキャッシュ機能を定義するインターフェース.
 * このインターフェースを実装するクラスはHTTPヘッダ、HTTPボディのキャッシュデータを提供しなければならない。
 * 
 * @author NTT DOCOMO, INC.
 */
public interface HttpMessageCache {

    /**
     * キャッシュからメッセージヘッダを受信する.
     * @param request リクエスト
     * @return メッセージ
     * @throws HttpException HTTPエラーが発生した場合
     * @throws IOException I/Oエラーが発生した場合
     */
    HttpMessage getMessageHeader(final HttpMessage request)
            throws HttpException, IOException;

    /**
     * キャッシュからメッセージエンティティを受信する.
     * @param request リクエスト
     * @param message メッセージ
     * @throws HttpException HTTPエラーが発生した場合
     * @throws IOException I/Oエラーが発生した場合
     */
    void getMessageEntity(final HttpMessage request, final HttpMessage message)
            throws HttpException, IOException;

}
