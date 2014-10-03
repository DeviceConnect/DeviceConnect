package com.nttdocomo.android.dconnect.deviceplugin.chromecast.profile;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import com.nttdocomo.android.dconnect.deviceplugin.chromecast.ChromeCastService;
import com.nttdocomo.android.dconnect.deviceplugin.chromecast.setting.ChromeCastSettingStepsActivity;
import com.nttdocomo.android.dconnect.deviceplugin.util.ChromeCastApplication;
import com.nttdocomo.android.dconnect.deviceplugin.util.ChromeCastDiscovery;
import com.nttdocomo.android.dconnect.event.EventManager;
import com.nttdocomo.android.dconnect.message.MessageUtils;
import com.nttdocomo.android.dconnect.profile.DConnectProfile;
import com.nttdocomo.android.dconnect.profile.DConnectProfileProvider;
import com.nttdocomo.android.dconnect.profile.SystemProfile;
import com.nttdocomo.dconnect.message.DConnectMessage;

/**
 * System プロファイル (Chromecast)
 * <p>
 * Chromecastのシステム情報を提供する
 * </p>
 * 
 */
public class ChromeCastSystemProfile extends SystemProfile {
	
	/**
     * コンストラクタ
     * 
     * @param	provider	プロバイダ
     * @return	なし
     */
    public ChromeCastSystemProfile(final DConnectProfileProvider provider) {
        super(provider);
    }

    @Override
    protected Class<? extends Activity> getSettingPageActivity(final Intent request, final Bundle param) {
        return ChromeCastSettingStepsActivity.class;
    }
    
    /**
     * バージョンを取得する
     * 
     * @param	なし
     * @return	version	バージョン
     */
    private String getCurrentVersionName() {
        PackageManager packageManager = getContext().getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getContext().getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            return packageInfo.versionName;
        } catch (NameNotFoundException e) {
            return "Unknown";
        }
    }
    
    @Override
    protected boolean onGetDevice(final Intent request, final Intent response, final String deviceId) {
    	// Select Route, launch
        ChromeCastDiscovery discovery = ((ChromeCastService) getContext()).getChromeCastDiscovery();
        ChromeCastApplication application = ((ChromeCastService) getContext()).getChromeCastApplication();
        if(discovery.getSelectedDevice() != null){
        	if(discovery.getSelectedDevice().getFriendlyName().equals(deviceId)){
        		application.launchReceiver();
        	}else{
        		discovery.setRouteName(deviceId);
        	}
        }else{
        	discovery.setRouteName(deviceId);
        }
        
    	// connect
        Bundle connect = new Bundle();
        setWifiState(connect, getWifiState(deviceId));
        setBluetoothState(connect, getBluetoothState(deviceId));
        setNFCState(connect, getNFCState(deviceId));
        setBLEState(connect, getBLEState(deviceId));
        setConnect(response, connect);

        // version
        setVersion(response, getCurrentVersionName());

        // supports
        ArrayList<String> profiles = new ArrayList<String>();
        for (DConnectProfile profile : getProfileProvider().getProfileList()) {
            profiles.add(profile.getProfileName());
        }
        setSupports(response, profiles.toArray(new String[0]));
        setResult(response, DConnectMessage.RESULT_OK);
        
        return true;
    }
    
    @Override
    protected boolean onDeleteEvents(final Intent request, final Intent response, final String sessionKey) {
        
        if (sessionKey == null || sessionKey.length() == 0) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else if (EventManager.INSTANCE.removeEvents(sessionKey)) {
            setResult(response, DConnectMessage.RESULT_OK);
        } else {
            MessageUtils.setUnknownError(response);
        }
        
        return true;
    }

}
