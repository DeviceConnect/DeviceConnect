/*
 SWDeviceOrientationProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.sw.profile;

import org.deviceconnect.android.event.EventError;
import org.deviceconnect.android.event.EventManager;
import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.android.profile.DeviceOrientationProfile;
import org.deviceconnect.message.DConnectMessage;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;

/**
 * SonySWデバイスプラグインの{@link DeviceOrientationProfile}実装.
 * @author NTT DOCOMO, INC.
 */
public class SWDeviceOrientationProfile extends DeviceOrientationProfile {

    @Override
    protected boolean onPutOnDeviceOrientation(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {
        BluetoothDevice device = SWUtil.findSmartWatch(deviceId);
        if (device == null) {
            MessageUtils.setNotFoundDeviceError(response, "No device is found: " + deviceId);
            return true;
        }
        EventError error = EventManager.INSTANCE.addEvent(request);
        if (error == EventError.NONE) {
            setResult(response, DConnectMessage.RESULT_OK);
        } else if (error == EventError.INVALID_PARAMETER) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else {
            MessageUtils.setUnknownError(response);
        }
        return true;
    }

    @Override
    protected boolean onDeleteOnDeviceOrientation(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {
        BluetoothDevice device = SWUtil.findSmartWatch(deviceId);
        if (device == null) {
            MessageUtils.setNotFoundDeviceError(response, "No device is found: " + deviceId);
            return true;
        }
        EventError error = EventManager.INSTANCE.removeEvent(request);
        if (error == EventError.NONE) {
            setResult(response, DConnectMessage.RESULT_OK);
        } else if (error == EventError.INVALID_PARAMETER) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else {
            MessageUtils.setUnknownError(response);
        }
        return true;
    }

}
