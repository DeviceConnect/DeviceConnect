/*
 TestMediaPlayerProfileConstants.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.test.plugin.profile;

import org.deviceconnect.profile.MediaPlayerProfileConstants;

/**
 * JUnit用テストデバイスプラグイン、MediaStreamsPlayプロファイル.
 * @author NTT DOCOMO, INC.
 */
public interface TestMediaPlayerProfileConstants {

    /** テスト用メディアID. */
    String TEST_MEDIA_ID = "test.mp3"; // TODO テスト用メディアIDを設定する.

    /** テスト用再生状態. */
    String TEST_STATUS = MediaPlayerProfileConstants.Status.PLAY.getValue();

    /** テスト用再生位置. */
    int TEST_POS = 1;

    /** テスト用トラック番号. */
    String TEST_TRACK_NO = "1,2,3";

    /** テスト用再生音量. */
    double TEST_VOLUME = 0.5;

}
