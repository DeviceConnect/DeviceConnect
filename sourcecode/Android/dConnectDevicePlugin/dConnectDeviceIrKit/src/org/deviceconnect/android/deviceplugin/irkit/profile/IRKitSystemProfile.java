/*
 IRKitSystemProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.irkit.profile;

import org.deviceconnect.android.deviceplugin.irkit.IRKitDevice;
import org.deviceconnect.android.deviceplugin.irkit.IRKitDeviceService;
import org.deviceconnect.android.deviceplugin.irkit.settings.activity.IRKitSettingActivity;
import org.deviceconnect.android.event.EventManager;
import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.android.profile.DConnectProfileProvider;
import org.deviceconnect.android.profile.SystemProfile;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.profile.SystemProfileConstants.ConnectState;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * IrKitデバイスプラグイン System プロファイル.
 * @author NTT DOCOMO, INC.
 */
public class IRKitSystemProfile extends SystemProfile {

    /**
     * System Profile.
     * 
     * @param provider Provider
     */
    public IRKitSystemProfile(final DConnectProfileProvider provider) {
        super(provider);
    }

    @Override
    protected Class<? extends Activity> getSettingPageActivity(final Intent request, final Bundle param) {
        return IRKitSettingActivity.class;
    }

    @Override
    protected ConnectState getWifiState(final String deviceId) {
        
        IRKitDeviceService service = (IRKitDeviceService) getContext();
        IRKitDevice device = service.getDevice(deviceId);
        
        if (device != null) {
            return ConnectState.ON;
        } else {
            return ConnectState.OFF;
        }
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
