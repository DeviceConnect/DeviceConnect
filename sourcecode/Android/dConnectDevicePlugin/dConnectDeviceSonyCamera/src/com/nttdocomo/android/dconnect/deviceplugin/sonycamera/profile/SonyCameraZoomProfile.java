package com.nttdocomo.android.dconnect.deviceplugin.sonycamera.profile;

import java.util.logging.Logger;

import android.content.Intent;

import com.nttdocomo.android.dconnect.deviceplugin.sonycamera.SonyCameraDeviceService;

/**
SonyCameraZoomProfile
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/
public class SonyCameraZoomProfile extends CameraProfile {

    /**
     * ログ処理.
     */
    Logger mLogger = Logger.getLogger("dconnect.dplugin");

    @Override
    protected boolean onPutActZoom(final Intent request, final Intent response, final String deviceId,
            final String direction, final String movement) {
        return ((SonyCameraDeviceService) getContext())
                .onPutActZoom(request, response, deviceId, direction, movement);
    }

    @Override
    protected boolean onGetZoomDiameter(final Intent request, final Intent response, final String deviceId) {
        
        return ((SonyCameraDeviceService) getContext()).onGetZoomDiameter(request, response, deviceId);
    }
}
