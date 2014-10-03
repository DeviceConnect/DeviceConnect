package com.nttdocomo.android.dconnect.deviceplugin.sw.profile;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;

import com.nttdocomo.android.dconnect.deviceplugin.util.DcLoggerSW;
import com.nttdocomo.android.dconnect.event.EventError;
import com.nttdocomo.android.dconnect.event.EventManager;
import com.nttdocomo.android.dconnect.message.MessageUtils;
import com.nttdocomo.android.dconnect.profile.DeviceOrientationProfile;
import com.nttdocomo.dconnect.message.DConnectMessage;

/**
DeviceOrientationProfile
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

public class SWDeviceOrientationProfile extends DeviceOrientationProfile {

    /** ロガー. */
    private DcLoggerSW mLogger = new DcLoggerSW();

    @Override
    protected boolean onPutOnDeviceOrientation(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {
        BluetoothDevice device = SWUtil.findSmartWatch(deviceId);
        if (device == null) {
            MessageUtils.setNotFoundDeviceError(response, "No device is found: " + deviceId);
            return true;
        }

        mLogger.info(this, "onPutOnDeviceOrientation", sessionKey);

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
