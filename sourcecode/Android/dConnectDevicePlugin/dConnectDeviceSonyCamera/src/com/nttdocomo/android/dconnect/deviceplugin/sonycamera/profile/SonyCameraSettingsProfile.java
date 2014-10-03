package com.nttdocomo.android.dconnect.deviceplugin.sonycamera.profile;

import android.content.Intent;

import com.nttdocomo.android.dconnect.deviceplugin.sonycamera.SonyCameraDeviceService;
import com.nttdocomo.android.dconnect.profile.SettingsProfile;

/**
SonyCameraSettingsProfile
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/
public class SonyCameraSettingsProfile extends SettingsProfile {

    @Override
    protected boolean onPutDate(final Intent request, final Intent response, 
            final String deviceId, final String date) {
        return ((SonyCameraDeviceService) getContext()).onPutDate(request, response, deviceId, date);
    }

}
