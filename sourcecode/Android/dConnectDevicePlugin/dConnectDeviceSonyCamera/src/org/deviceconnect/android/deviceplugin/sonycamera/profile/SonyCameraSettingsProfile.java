/*
SonyCameraSettingsProfile
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

package org.deviceconnect.android.deviceplugin.sonycamera.profile;

import org.deviceconnect.android.deviceplugin.sonycamera.SonyCameraDeviceService;

import android.content.Intent;

import org.deviceconnect.android.profile.SettingsProfile;

/**
 * Sony Camera 用 Settings プロファイル.
 * @author NTT DOCOMO, INC.
 */
public class SonyCameraSettingsProfile extends SettingsProfile {

    @Override
    protected boolean onPutDate(final Intent request, final Intent response, 
            final String deviceId, final String date) {
        return ((SonyCameraDeviceService) getContext()).onPutDate(request, response, deviceId, date);
    }

}
