/*
 FailHTTPServerTest.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.manager.test;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpUriRequest;
import org.deviceconnect.android.profile.restful.test.RESTfulDConnectTestCase;
import org.deviceconnect.android.profile.restful.test.TestURIBuilder;
import org.deviceconnect.profile.AuthorizationProfileConstants;
import org.deviceconnect.profile.DConnectProfileConstants;
import org.deviceconnect.utils.URIBuilder;


/**
 * HTTPサーバの異常系テスト.
 * @author NTT DOCOMO, INC.
 */
public class FailHTTPServerTest extends RESTfulDConnectTestCase {

    /**
     * {@link #testHTTPHeaderOver8KB()}のデバイスID.
     */
    private static final int VERY_LONG_DEVICE_ID_LENGTH = 10000;

    /**
     * コンストラクタ.
     * 
     * @param tag テストタグ
     */
    public FailHTTPServerTest(final String tag) {
        super(tag);
    }

    /**
     * HEADメソッドでHTTPサーバにアクセスする異常系テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: HEAD
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・HTTP 501 Not Implementedが返ること。
     * </pre>
     */
    public void testHttpMethodHead() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        HttpUriRequest request = new HttpHead(builder.toString());
        HttpResponse response = requestHttpResponse(request);
        assertEquals(HttpStatus.SC_NOT_IMPLEMENTED, response.getStatusLine().getStatusCode());
    }

    /**
     * HTTPヘッダのサイズが8KBを超えるHTTPリクエストを送信する異常系テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /battery?deviceId=xxxx&accessToken=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・HTTP 413 Request Entity Too Largeが返ること。
     * </pre>
     */
    public void testHTTPHeaderOver8KB() {
        // HTTPヘッダのサイズを8KBにするために、10000文字のデバイスIDを設定する
        StringBuilder deviceId = new StringBuilder();
        for (int i = 0; i < VERY_LONG_DEVICE_ID_LENGTH; i++) {
            deviceId.append("0");
        }
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/battery");
        builder.append("?");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        builder.append("&");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + deviceId.toString());
        HttpUriRequest request = new HttpGet(builder.toString());
        HttpResponse response = requestHttpResponse(request);
        assertEquals(HttpStatus.SC_REQUEST_TOO_LONG, response.getStatusLine().getStatusCode());
    }

}
