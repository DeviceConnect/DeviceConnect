/*
HueDeviceService
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

package com.nttdocomo.android.dconnect.deviceplugin.hue;

import com.nttdocomo.android.dconnect.deviceplugin.hue.control.HueControl;
import com.nttdocomo.android.dconnect.deviceplugin.hue.profile.HueLightProfile;
import com.nttdocomo.android.dconnect.deviceplugin.hue.profile.HueNetworkServceDiscoveryProfile;
import com.nttdocomo.android.dconnect.deviceplugin.hue.profile.HueSystemProfile;
import com.nttdocomo.android.dconnect.deviceplugin.util.DcLoggerHue;
import com.nttdocomo.android.dconnect.message.DConnectMessageService;
import com.nttdocomo.android.dconnect.profile.NetworkServiceDiscoveryProfile;
import com.nttdocomo.android.dconnect.profile.SystemProfile;


/**
 * 本デバイスプラグインのプロファイルをdConnectに登録するサービス.
 */
public class HueDeviceService extends DConnectMessageService {

    /**
     * ロガー.
     */
    private DcLoggerHue mLogger = new DcLoggerHue();

    @Override
    public void onCreate() {
        super.onCreate();
        
        mLogger.entering(this, "onCreate");
        
        addProfile(new HueLightProfile());   

        //前もってキャッシュをupdateしておく
        HueControl.updateAccessPointCache();
        
        mLogger.exiting(this, "onCreate");

    }

	@Override
	protected SystemProfile getSystemProfile() {
		return new HueSystemProfile(this);
	}

	@Override
	protected NetworkServiceDiscoveryProfile getNetworkServiceDiscoveryProfile() {
		return new HueNetworkServceDiscoveryProfile();
	}

    @Override
    public void onDestroy() {

        mLogger.entering("HueDeviceService", "onDestroy");

        //リスナーの紐付けなどを一度切る。
        HueControl.destroyPHHueSDK();
        
        mLogger.exiting("HueDeviceService", "onDestroy");
        
        super.onDestroy();
    }

}
