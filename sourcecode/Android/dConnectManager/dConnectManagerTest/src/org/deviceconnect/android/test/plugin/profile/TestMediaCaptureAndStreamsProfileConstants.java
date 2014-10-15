/*
 TestMediaCaptureAndStreamsProfileConstants.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.test.plugin.profile;


/**
 * JUnit用テストデバイスプラグイン、MediaCaptureAndStreamsProfileプロファイル.
 * @author NTT DOCOMO, INC.
 */
public interface TestMediaCaptureAndStreamsProfileConstants {

    /**
     * メディアID.
     */
    String MEDIA_ID = "test.mp4";

    /**
     * MIME Type.
     */
    String MIME_TYPE = "video/mp4";

    /**
     * トラック番号.
     */
    String TRACK_NO = "1";

    /**
     * 再生状態.
     */
    String STATUS = "play";

    /**
     * トラック内の再生位置（秒単位）.
     */
    String POS = "0";

    /**
     * 再生音量.
     */
    String VOLUME = "0";

}
