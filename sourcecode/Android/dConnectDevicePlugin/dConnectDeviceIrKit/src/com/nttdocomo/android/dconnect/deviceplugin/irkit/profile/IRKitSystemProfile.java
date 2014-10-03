package com.nttdocomo.android.dconnect.deviceplugin.irkit.profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.nttdocomo.android.dconnect.deviceplugin.irkit.IRKitDevice;
import com.nttdocomo.android.dconnect.deviceplugin.irkit.IRKitDeviceService;
import com.nttdocomo.android.dconnect.deviceplugin.irkit.settings.activity.IRKitSettingActivity;
import com.nttdocomo.android.dconnect.event.EventManager;
import com.nttdocomo.android.dconnect.message.MessageUtils;
import com.nttdocomo.android.dconnect.profile.DConnectProfileProvider;
import com.nttdocomo.android.dconnect.profile.SystemProfile;
import com.nttdocomo.dconnect.message.DConnectMessage;

/**
 * IrKitデバイスプラグイン System プロファイル.
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
