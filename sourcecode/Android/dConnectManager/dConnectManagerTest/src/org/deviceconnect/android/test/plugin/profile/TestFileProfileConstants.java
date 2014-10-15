/*
 TestFileProfileConstants.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.test.plugin.profile;


/**
 * JUnit用テストデバイスプラグイン、Fileプロファイル.
 * @author NTT DOCOMO, INC.
 */
public interface TestFileProfileConstants {

    /**
     * 容量.
     */
    int BYTE = 0;

    /**
     * ファイル名.
     */
    String FILE_NAME = "test.png";

    /**
     * ファイルのメディアID.
     */
    String PATH = "/test.png";

    /**
     * ファイルのMIMEタイプ.
     */
    String MIME_TYPE = "image/png";

    /**
     * 書き込むデータを保存したファイルのURI.
     */
    String URI = "test_uri"; // TODO 現在、仮のURIにしているの、適切な形式のURIにする

}
