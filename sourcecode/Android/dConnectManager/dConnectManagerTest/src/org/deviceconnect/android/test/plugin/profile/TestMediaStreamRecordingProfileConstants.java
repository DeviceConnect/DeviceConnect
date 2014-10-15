/*
 TestMediaStreamRecordingProfileConstants.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.test.plugin.profile;

import org.deviceconnect.profile.MediaStreamRecordingProfileConstants;


/**
 * JUnit用テストデバイスプラグイン、MediaStreamRecordingプロファイル.
 * @author NTT DOCOMO, INC.
 */
public interface TestMediaStreamRecordingProfileConstants {

    /**
     * カメラID.
     */
    String ID = "test_camera_id";

    /**
     * カメラの名前.
     */
    String NAME = "test_camera_name";

    /**
     * カメラの状態.
     */
    String STATE = MediaStreamRecordingProfileConstants.RecorderState.INACTIVE.getValue();

    /**
     * レコーダの横幅.
     */
    int IMAGE_WIDTH = 1920;

    /**
     * レコーダの縦幅.
     */
    int IMAGE_HEIGHT = 1080;

    /**
     * レコーダの状態.
     */
    String STATUS = "recording";

    /**
     * レコーダのエンコードするMIMEタイプ.
     */
    String MIME_TYPE = "video/mp4";

    /**
     * カメラ設定.
     */
    String CONFIG = "test_config";

    /**
     * 撮影した写真のURI.
     */
    String URI = "content://test/test.mp4";

    /**
     * メディアID.
     */
    String PATH = "test.mp4";

    /**
     * タイムスライス.
     */
    long TIME_SLICE = 3600L;

}
