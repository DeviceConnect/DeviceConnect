package com.nttdocomo.android.dconnect.deviceplugin.sonycamera.profile;

import android.content.Intent;

import com.nttdocomo.android.dconnect.deviceplugin.sonycamera.SonyCameraDeviceService;
import com.nttdocomo.android.dconnect.message.MessageUtils;
import com.nttdocomo.android.dconnect.profile.NetworkServiceDiscoveryProfile;
import com.nttdocomo.dconnect.message.DConnectMessage;
import com.nttdocomo.android.dconnect.event.EventError;
import com.nttdocomo.android.dconnect.event.EventManager;
import com.nttdocomo.dconnect.message.intent.message.IntentDConnectMessage;

/**
 * SonyCameraNetworkServiceDiscoveryProfile Copyright (c) 2014 NTT DOCOMO,INC.
 * Released under the MIT license http://opensource.org/licenses/mit-license.php
 */
public class SonyCameraNetworkServiceDiscoveryProfile extends NetworkServiceDiscoveryProfile {

    @Override
    protected boolean onGetGetNetworkServices(final Intent request, final Intent response) {
        return ((SonyCameraDeviceService) getContext()).searchSonyCameraDevice(request, response);
    }

    @Override
    protected boolean onPutOnServiceChange(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {
        EventError error = EventManager.INSTANCE.addEvent(request);
        switch (error) {
        case NONE:
            response.putExtra(IntentDConnectMessage.EXTRA_PROFILE, NetworkServiceDiscoveryProfile.PROFILE_NAME);
            response.putExtra(IntentDConnectMessage.EXTRA_ATTRIBUTE,
                    NetworkServiceDiscoveryProfile.ATTRIBUTE_ON_SERVICE_CHANGE);

            setResult(response, DConnectMessage.RESULT_OK);

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
    protected boolean onDeleteOnServiceChange(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {
        EventError error = EventManager.INSTANCE.removeEvent(request);
        switch (error) {
        case NONE:
            response.putExtra(IntentDConnectMessage.EXTRA_PROFILE, NetworkServiceDiscoveryProfile.PROFILE_NAME);
            response.putExtra(IntentDConnectMessage.EXTRA_ATTRIBUTE,
                    NetworkServiceDiscoveryProfile.ATTRIBUTE_ON_SERVICE_CHANGE);
            setResult(response, DConnectMessage.RESULT_OK);
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
