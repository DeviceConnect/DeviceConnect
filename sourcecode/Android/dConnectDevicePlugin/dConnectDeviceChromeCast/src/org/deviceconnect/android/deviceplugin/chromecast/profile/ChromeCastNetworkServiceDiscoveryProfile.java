/*
 ChromeCastNetworkServiceDiscoveryProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.chromecast.profile;

import java.util.ArrayList;
import java.util.List;

import org.deviceconnect.android.deviceplugin.chromecast.ChromeCastService;
import org.deviceconnect.android.deviceplugin.chromecast.R;
import org.deviceconnect.android.deviceplugin.chromecast.core.ChromeCastDiscovery;
import org.deviceconnect.android.event.EventError;
import org.deviceconnect.android.event.EventManager;
import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.android.profile.NetworkServiceDiscoveryProfile;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.message.intent.message.IntentDConnectMessage;

import android.content.Intent;
import android.os.Bundle;

/**
 * Network Service Discovery プロファイル (Chromecast)
 * <p>
 * Chromecastの検索機能を提供する
 * </p>
 * @author NTT DOCOMO, INC.
 */
public class ChromeCastNetworkServiceDiscoveryProfile extends NetworkServiceDiscoveryProfile {

    @Override
    protected boolean onGetGetNetworkServices(final Intent request, final Intent response) {
        NetworkType DEVICE_TYPE = NetworkType.WIFI;
        String DEVICE_NAME = getContext().getResources().getString(R.string.device_name);

        ChromeCastDiscovery discovery = ((ChromeCastService) getContext()).getChromeCastDiscovery();
        List<Bundle> services = new ArrayList<Bundle>();
        for(int i=0; i<discovery.getDeviceNames().size(); i++){
            Bundle service = new Bundle();
            setId(service, discovery.getDeviceNames().get(i));
            setName(service, DEVICE_NAME + " (" + discovery.getDeviceNames().get(i) + ")");
            setType(service, DEVICE_TYPE);
            setOnline(service, true);
            services.add(service);
        }
        setServices(response, services);
        setResult(response, DConnectMessage.RESULT_OK);
        response.putExtra(IntentDConnectMessage.EXTRA_REQUEST_CODE, 
                request.getIntExtra(IntentDConnectMessage.EXTRA_REQUEST_CODE, -1));
        response.putExtra(PARAM_SERVICES, services.toArray(new Bundle[services.size()]));
        return true;
    }

    @Override
    protected boolean onPutOnServiceChange(Intent request, Intent response, String deviceId, String sessionKey) {
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
    protected boolean onDeleteOnServiceChange(Intent request, Intent response, String deviceId, String sessionKey) {
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
