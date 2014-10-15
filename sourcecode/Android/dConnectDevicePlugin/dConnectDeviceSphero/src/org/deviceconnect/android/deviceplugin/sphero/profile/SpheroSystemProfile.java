/*
 SpheroSystemProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.sphero.profile;

import java.util.Collection;
import java.util.List;

import org.deviceconnect.android.deviceplugin.sphero.SpheroManager;
import org.deviceconnect.android.deviceplugin.sphero.data.DeviceInfo;
import org.deviceconnect.android.deviceplugin.sphero.setting.SettingActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import org.deviceconnect.android.event.Event;
import org.deviceconnect.android.event.EventManager;
import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.android.profile.DConnectProfileProvider;
import org.deviceconnect.android.profile.SystemProfile;
import org.deviceconnect.message.DConnectMessage;

/**
 * System プロファイル.
 * @author NTT DOCOMO, INC.
 */
public class SpheroSystemProfile extends SystemProfile {

    /**
     * システムプロファイルを生成する.
     * 
     * @param provider プロバイダー
     */
    public SpheroSystemProfile(final DConnectProfileProvider provider) {
        super(provider);
    }

    @Override
    protected Class<? extends Activity> getSettingPageActivity(final Intent request, final Bundle param) {
        return SettingActivity.class;
    }

    @Override
    protected boolean onDeleteEvents(final Intent request, final Intent response, final String sessionKey) {
        
        if (sessionKey == null || sessionKey.length() == 0) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else if (EventManager.INSTANCE.removeEvents(sessionKey)) {
            setResult(response, DConnectMessage.RESULT_OK);
            
            Collection<DeviceInfo> devices = SpheroManager.INSTANCE.getConnectedDevices();
            for (DeviceInfo info : devices) {
                if (!SpheroManager.INSTANCE.hasSensorEvent(info)) {
                    SpheroManager.INSTANCE.stopSensor(info);
                }
                List<Event> events = EventManager.INSTANCE.getEventList(
                        info.getDevice().getUniqueId(), SpheroProfile.PROFILE_NAME, 
                        SpheroProfile.INTER_COLLISION, SpheroProfile.ATTR_ON_COLLISION);
                
                if (events.size() == 0) {
                    SpheroManager.INSTANCE.stopCollision(info);
                }
            }
        } else {
            MessageUtils.setUnknownError(response);
        }
        
        return true;
    }
}
