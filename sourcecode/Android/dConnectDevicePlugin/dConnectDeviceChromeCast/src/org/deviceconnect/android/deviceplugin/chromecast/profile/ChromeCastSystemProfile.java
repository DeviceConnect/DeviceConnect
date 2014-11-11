/*
 ChromeCastSystemProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.chromecast.profile;

import java.util.ArrayList;

import org.deviceconnect.android.deviceplugin.chromecast.ChromeCastService;
import org.deviceconnect.android.deviceplugin.chromecast.core.ChromeCastApplication;
import org.deviceconnect.android.deviceplugin.chromecast.core.ChromeCastDiscovery;
import org.deviceconnect.android.deviceplugin.chromecast.setting.ChromeCastSettingFragmentActivity;
import org.deviceconnect.android.event.EventManager;
import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.android.profile.DConnectProfile;
import org.deviceconnect.android.profile.DConnectProfileProvider;
import org.deviceconnect.android.profile.SystemProfile;
import org.deviceconnect.message.DConnectMessage;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

/**
 * System プロファイル (Chromecast)
 * <p>
 * Chromecastのシステム情報を提供する
 * </p>
 * @author NTT DOCOMO, INC.
 */
public class ChromeCastSystemProfile extends SystemProfile {

    /**
     * コンストラクタ
     * 
     * @param   provider    プロバイダ
     * @return  なし
     */
    public ChromeCastSystemProfile(final DConnectProfileProvider provider) {
        super(provider);
    }

    @Override
    protected Class<? extends Activity> getSettingPageActivity(final Intent request, final Bundle param) {
        return ChromeCastSettingFragmentActivity.class;
    }

    /**
     * バージョンを取得する
     * 
     * @param   なし
     * @return  version バージョン
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
                application.connect();
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
