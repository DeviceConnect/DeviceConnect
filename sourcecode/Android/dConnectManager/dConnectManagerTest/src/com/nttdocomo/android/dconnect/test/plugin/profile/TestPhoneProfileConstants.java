/*
 TestPhoneProfileConstants.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.test.plugin.profile;

import com.nttdocomo.dconnect.profile.PhoneProfileConstants;


/**
 * JUnit用テストデバイスプラグイン、Phoneプロファイル.
 * @author NTT DOCOMO, INC.
 */
public interface TestPhoneProfileConstants {

    /**
     * 相手先の電話番号.
     */
    String PHONE_NUMBER = "090xxxxxxxx";

    /**
     * モード.
     */
    int MODE = PhoneProfileConstants.PhoneMode.MANNER.getValue(); // マナーモード

    /**
     * スマートフォンの通話状態.
     */
    int STATE = PhoneProfileConstants.CallState.FINISHED.getValue(); // 通話終了

}
