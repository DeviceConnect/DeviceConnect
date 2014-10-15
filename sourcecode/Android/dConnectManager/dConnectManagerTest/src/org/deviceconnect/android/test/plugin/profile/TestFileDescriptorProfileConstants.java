/*
 TestFileDescriptorProfileConstants.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.test.plugin.profile;

import android.util.Base64;

/**
 * JUnit用テストデバイスプラグイン、FileDescriptorプロファイル.
 * @author NTT DOCOMO, INC.
 */
public interface TestFileDescriptorProfileConstants {

    /**
     * 容量.
     */
    int BYTE = 64000;

    /**
     * Base64エンコードされたファイルデータの文字列表現.
     */
    String FILE_DATA = Base64.encodeToString(new byte[] {0}, Base64.DEFAULT);

    /**
     * 書き込み先のファイルを示すパス.
     */
    String PATH = "test.txt";

    /**
     * ファイルの現在の更新時間.
     */
    String CURR = "2014-06-01T00:00:00+0900";

    /**
     * ファイルの前回の更新時間.
     */
    String PREV = "2014-06-01T00:00:00+0900";

}
