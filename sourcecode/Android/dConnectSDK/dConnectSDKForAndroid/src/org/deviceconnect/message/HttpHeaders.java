/*
 HttpHeaders.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.message;

/**
 * ヘッダーパラメータ.
 * @author NTT DOCOMO, INC.
 */
public class HttpHeaders {

    /**
     * ホスト.
     */
    public static final String HOST = "Host";

    /**
     * リクエストコードヘッダ.
     */
    public static final String X_REQUEST_CODE = "X-DConn-Request-Code";

    /**
     * コンストラクタ.
     */
    protected HttpHeaders() {
    }

}
