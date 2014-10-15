/*
 SingleHttpConnection.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.message.basic.conn;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;

/**
 * シングルHTTPコネクション.
 * @author NTT DOCOMO, INC.
 */
public abstract class SingleHttpConnection extends AbstractHttpConnection {

    /**
     * レスポンスリスト.
     */
    private List<HttpResponse> mResponseList = new ArrayList<HttpResponse>();

    /**
     * レスポンスリストを取得する.
     * @return レスポンスリスト
     */
    public List<HttpResponse> getResponseList() {
        return mResponseList;
    }

    /**
     * レスポンスを追加する.
     * @param response HTTPレスポンス
     */
    public void addResponse(final HttpResponse response) {
        mResponseList.add(response);
    }

    /**
     * レスポンスを削除する.
     * @param response HTTPレスポンス
     */
    public void removeResponse(final HttpResponse response) {
        mResponseList.remove(response);
    }

}
