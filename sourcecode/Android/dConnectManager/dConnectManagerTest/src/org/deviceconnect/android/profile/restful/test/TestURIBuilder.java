/*
 TestURIBuilder.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.profile.restful.test;

import org.deviceconnect.utils.URIBuilder;

/**
 * テスト用URIBuilder.
 * @author NTT DOCOMO, INC.
 */
public final class TestURIBuilder extends URIBuilder {

    /**
     * ポート番号: {@value} .
     */
    private static final int PORT = 4035;

    /**
     * コンストラクタ.
     */
    private TestURIBuilder() {
        super();
        setScheme("http");
        setHost("localhost");
        setPort(PORT);
    }

    /**
     * テスト用URIBuilderを生成する.
     * 
     * @return テスト用URIBuilder
     */
    public static URIBuilder createURIBuilder() {
        return new TestURIBuilder();
    }

}
