/*
 SpheroDeviceOrientationProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.sphero.profile;

import org.deviceconnect.android.deviceplugin.sphero.SpheroManager;
import org.deviceconnect.android.deviceplugin.sphero.data.DeviceInfo;

import android.content.Intent;

import org.deviceconnect.android.event.EventError;
import org.deviceconnect.android.event.EventManager;
import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.android.profile.DeviceOrientationProfile;
import org.deviceconnect.message.DConnectMessage;

/**
 * DeviceOrientation Profile.
 * @author NTT DOCOMO, INC.
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
