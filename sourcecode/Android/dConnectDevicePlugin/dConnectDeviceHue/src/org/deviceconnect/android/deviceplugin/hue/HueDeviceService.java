/*
HueDeviceService
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

package org.deviceconnect.android.deviceplugin.hue;

import org.deviceconnect.android.deviceplugin.hue.control.HueControl;
import org.deviceconnect.android.deviceplugin.hue.profile.HueLightProfile;
import org.deviceconnect.android.deviceplugin.hue.profile.HueNetworkServceDiscoveryProfile;
import org.deviceconnect.android.deviceplugin.hue.profile.HueSystemProfile;
import org.deviceconnect.android.deviceplugin.util.DcLoggerHue;
import org.deviceconnect.android.message.DConnectMessageService;
import org.deviceconnect.android.profile.NetworkServiceDiscoveryProfile;
import org.deviceconnect.android.profile.SystemProfile;



/**
 * 本デバイスプラグインのプロファイルをdConnectに登録するサービス.
 * @author NTT DOCOMO, INC.
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
