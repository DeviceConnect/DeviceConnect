/*
 IRKitNetworkServceDiscoveryProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.irkit.profile;

import org.deviceconnect.android.deviceplugin.irkit.IRKitDeviceService;
import org.deviceconnect.android.event.EventError;
import org.deviceconnect.android.event.EventManager;
import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.android.profile.NetworkServiceDiscoveryProfile;
import org.deviceconnect.message.DConnectMessage;

import android.content.Intent;

/**
 * Network service discovery profile.
 * @author NTT DOCOMO, INC.
 */
public class IRKitNetworkServceDiscoveryProfile extends NetworkServiceDiscoveryProfile {
    
    @Override
    public boolean onGetGetNetworkServices(final Intent request, final Intent response) {
        IRKitDeviceService service = (IRKitDeviceService) getContext();
        service.prepareGetNetworkServicesResponse(response);
        return true;
    }

    @Override
    protected boolean onPutOnServiceChange(final Intent request, final Intent response, 
            final String deviceId, final String sessionKey) {
        EventError error = EventManager.INSTANCE.addEvent(request);
        switch (error) {
        case NONE:
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
    protected boolean onDeleteOnServiceChange(final Intent request, final Intent response, 
            final String deviceId, final String sessionKey) {
        
        EventError error = EventManager.INSTANCE.removeEvent(request);
        switch (error) {
        case NONE:
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
