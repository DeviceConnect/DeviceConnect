/*
 HttpConnection.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.message.conn;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.HttpClientConnection;
import org.apache.http.HttpException;
import org.apache.http.HttpMessage;
import org.apache.http.HttpServerConnection;
import org.apache.http.params.HttpParams;

/**
 * コネクション.
 * @author NTT DOCOMO, INC.
 */
public interface HttpConnection
        extends HttpServerConnection, HttpClientConnection,
                HttpMessageCache {

    /**
     * HTTPパラメータでバインドする.
     * @param params HTTPパラメータ
     */
    void bind(final HttpParams params);

    /**
     * 入出力ストリームとサーバコネクションを接合する.
     * @param input 入力ストリーム
     * @param output 出力ストリーム
     * @param params HTTPパラメータ
     * @throws IOException I/Oエラーが発生した場合
     */
    void bind(final InputStream input, final OutputStream output,
            final HttpParams params) throws IOException;

    /**
     * メッセージヘッダを受信する.
     * @return メッセージ
     * @throws HttpException HTTPエラーが発生した場合
     * @throws IOException I/Oエラーが発生した場合
     */
    HttpMessage receiveMessageHeader() throws HttpException, IOException;

    /**
     * メッセージエンティティを受信する.
     * @param message メッセージ
     * @throws HttpException HTTPエラーが発生した場合
     * @throws IOException I/Oエラーが発生した場合
     */
    void receiveMessageEntity(final HttpMessage message) throws HttpException, IOException;

    /**
     * メッセージヘッダを送信する.
     * @param message メッセージ
     * @throws HttpException HTTPエラーが発生した場合
     * @throws IOException I/Oエラーが発生した場合
     */
    void sendMessageHeader(final HttpMessage message) throws HttpException, IOException;

    /**
     * メッセージエンティティを送信する.
     * @param message メッセージ
     * @throws HttpException HTTPエラーが発生した場合
     * @throws IOException I/Oエラーが発生した場合
     */
    void sendMessageEntity(final HttpMessage message) throws HttpException, IOException;

}
