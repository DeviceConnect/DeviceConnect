package com.nttdocomo.android.dconnect.deviceplugin.irkit.profile;

import android.content.Intent;

import com.nttdocomo.android.dconnect.deviceplugin.irkit.IRKitDeviceService;
import com.nttdocomo.android.dconnect.event.EventError;
import com.nttdocomo.android.dconnect.event.EventManager;
import com.nttdocomo.android.dconnect.message.MessageUtils;
import com.nttdocomo.android.dconnect.profile.NetworkServiceDiscoveryProfile;
import com.nttdocomo.dconnect.message.DConnectMessage;

/**
 * Network service discovery profile.
 * 
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
