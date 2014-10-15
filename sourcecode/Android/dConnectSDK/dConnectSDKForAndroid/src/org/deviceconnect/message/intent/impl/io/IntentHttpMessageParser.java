/*
 IntentHttpMessageParser.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.message.intent.impl.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpException;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.io.HttpMessageParser;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.message.http.impl.factory.HttpMessageFactory;
import org.deviceconnect.message.intent.impl.factory.IntentMessageFactory;

import android.content.Intent;

/**
 * HTTPメッセージパーサー.
 * @author NTT DOCOMO, INC.
 */
public class IntentHttpMessageParser implements HttpMessageParser {

    /**
     * ウェイトタイム.
     */
    public static final int WAIT_TIME = 50;

    /**
     * タイムアウト.
     */
    private int mSoTimeout;

    /**
     * ロガー.
     */
    private Logger mLogger = Logger.getLogger("org.deviceconnect.sdk.android");

    /**
     * レスポンスバッファ.
     */
    private static List<Intent> mResponseList = new ArrayList<Intent>();

    /**
     * コンストラクタ.
     * @param params HTTPパラメータ
     */
    public IntentHttpMessageParser(final HttpParams params) {
        mLogger.entering(this.getClass().getName(), "IntentHttpMessageParser");

        mSoTimeout = HttpConnectionParams.getSoTimeout(params);

        mLogger.exiting(this.getClass().getName(), "IntentHttpMessageParser");
    }

    @Override
    public HttpMessage parse() throws IOException, HttpException {

        long parseStart = System.currentTimeMillis();

        // wait for response, or response timeout
        // if SoTimeout is 0, infinite wait
        while (mResponseList.size() == 0 && (mSoTimeout == 0
                || mSoTimeout > System.currentTimeMillis() - parseStart)) {
            try {
                Thread.sleep(WAIT_TIME);
            } catch (InterruptedException e) {
                mLogger.log(Level.FINE, e.toString(), e);
                throw new IOException(e);
            }
        }

        if (mResponseList.size() <= 0 && mSoTimeout <= System.currentTimeMillis() - parseStart) {
            throw new IOException("response timeout");
        }

        Intent intent = mResponseList.remove(0);

        DConnectMessage dmessage =
                IntentMessageFactory.getMessageFactory().newDConnectMessage(intent);
        HttpMessage message =
                HttpMessageFactory.getMessageFactory().newPackagedMessage(dmessage);
        HttpResponse response = (HttpResponse) message;

        return response;

    }

    /**
     * レスポンスを追加する.
     * @param intent レスポンスインテント
     */
    static void addResponse(final Intent intent) {
        mResponseList.add(intent);
    }

}
