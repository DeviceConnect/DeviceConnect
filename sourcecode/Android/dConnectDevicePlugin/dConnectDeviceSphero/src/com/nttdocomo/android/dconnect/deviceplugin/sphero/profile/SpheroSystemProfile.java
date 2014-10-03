package com.nttdocomo.android.dconnect.deviceplugin.sphero.profile;

import java.util.Collection;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.nttdocomo.android.dconnect.deviceplugin.sphero.SpheroManager;
import com.nttdocomo.android.dconnect.deviceplugin.sphero.data.DeviceInfo;
import com.nttdocomo.android.dconnect.deviceplugin.sphero.setting.SettingActivity;
import com.nttdocomo.android.dconnect.event.Event;
import com.nttdocomo.android.dconnect.event.EventManager;
import com.nttdocomo.android.dconnect.message.MessageUtils;
import com.nttdocomo.android.dconnect.profile.DConnectProfileProvider;
import com.nttdocomo.android.dconnect.profile.SystemProfile;
import com.nttdocomo.dconnect.message.DConnectMessage;

/**
 * System プロファイル.
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
