package com.nttdocomo.android.dconnect.deviceplugin.sphero.profile;

import android.content.Intent;

import com.nttdocomo.android.dconnect.deviceplugin.sphero.SpheroManager;
import com.nttdocomo.android.dconnect.deviceplugin.sphero.data.DeviceInfo;
import com.nttdocomo.android.dconnect.event.EventError;
import com.nttdocomo.android.dconnect.event.EventManager;
import com.nttdocomo.android.dconnect.message.MessageUtils;
import com.nttdocomo.android.dconnect.profile.DeviceOrientationProfile;
import com.nttdocomo.dconnect.message.DConnectMessage;

/**
 * DeviceOrientation Profile.
 * 
 */
public class SpheroDeviceOrientationProfile extends DeviceOrientationProfile {

    @Override
    protected boolean onPutOnDeviceOrientation(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {
        
        DeviceInfo device = SpheroManager.INSTANCE.getDevice(deviceId);
        if (device == null) {
            MessageUtils.setNotFoundDeviceError(response);
            return true;
        }

        EventError error = EventManager.INSTANCE.addEvent(request);
        switch (error) {
        case NONE:
            setResult(response, DConnectMessage.RESULT_OK);
            SpheroManager.INSTANCE.startSensor(device);
            break;
        case INVALID_PARAMETER:
            MessageUtils.setInvalidRequestParameterError(response);
            break;
        default:
            MessageUtils.setUnknownError(response);
            break;
        }

        return true;
    }

    @Override
    protected boolean onDeleteOnDeviceOrientation(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {
        
        // Deleteはデバイスが無くてもゴミを残さないように削除できるようにしておく。
        EventError error = EventManager.INSTANCE.removeEvent(request);
        switch (error) {
        case NONE:
            setResult(response, DConnectMessage.RESULT_OK);
            DeviceInfo device = SpheroManager.INSTANCE.getDevice(deviceId);
            if (device != null) {
                if (!SpheroManager.INSTANCE.hasSensorEvent(device)) {
                    SpheroManager.INSTANCE.stopSensor(device);
                }
            }
            break;
        case INVALID_PARAMETER:
            MessageUtils.setInvalidRequestParameterError(response);
            break;
        default:
            MessageUtils.setUnknownError(response);
            break;
        }
        return true;
    }

}
