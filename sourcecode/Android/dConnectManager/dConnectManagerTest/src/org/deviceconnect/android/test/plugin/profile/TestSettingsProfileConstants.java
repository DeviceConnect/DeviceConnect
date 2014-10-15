/*
 TestSettingsProfileConstants.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.test.plugin.profile;

import org.deviceconnect.profile.SettingsProfileConstants;


/**
 * JUnit用テストデバイスプラグイン、Settingsプロファイル.
 * @author NTT DOCOMO, INC.
 */
public interface TestSettingsProfileConstants {

    /**
     * ボリュームを調整する項目.
     */
    int VOLUME_KIND = SettingsProfileConstants.VolumeKind.CALL.getValue();

    /**
     * レベル.
     */
    double LEVEL = 0.5;

    /**
     * 日時.
     */
    String DATE = "2014-01-01T01:01:01+09:00";

    /**
     * 消灯するまでの時間(ミリ秒).
     */
    int TIME = 1;

}
