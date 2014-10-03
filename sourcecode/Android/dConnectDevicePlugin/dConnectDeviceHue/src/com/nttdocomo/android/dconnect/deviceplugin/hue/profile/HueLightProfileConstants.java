package com.nttdocomo.android.dconnect.deviceplugin.hue.profile;

import com.nttdocomo.android.dconnect.profile.LightProfileConstants;

/**
HueLightProfileConstants
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

/**
 * 
 *HueLightProfile定数を定義するインターフェース.
 */
public interface HueLightProfileConstants extends LightProfileConstants {
    /**
     * デバイスプラグイン設定画面数.
     */
    int SETTING_PAGE_NNMBER = 3;
    
    /**
     * link button not pressed.
     */
    int HUE_SDK_ERROR_101 = 101;
    
    /**
     * 認証エラー.
     */
    int HUE_SDK_ERROR_1158 = 1158;

    /**
     * sleepで処理を止める秒数(msec).
     */
    int HUE_SLEEP_TIME_500 = 500;
    
    /**
     * ProfileName getter.
     * @return ProfileName
     */
    String getProfileName();
}
