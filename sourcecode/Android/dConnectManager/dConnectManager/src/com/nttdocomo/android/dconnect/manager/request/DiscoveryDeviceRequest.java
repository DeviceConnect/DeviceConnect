/*
 DiscoveryDeviceRequest.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.manager.request;

import java.util.List;

import android.content.Intent;

import com.nttdocomo.android.dconnect.DConnectService;
import com.nttdocomo.android.dconnect.event.Event;
import com.nttdocomo.android.dconnect.event.EventManager;
import com.nttdocomo.dconnect.message.DConnectMessage;
import com.nttdocomo.dconnect.profile.NetworkServiceDiscoveryProfileConstants;

/**
 * デバイスが発見されてdConnectManagerに通知があった場合にClientやAccessTokenを作成するためのリクエスト.
 * @author NTT DOCOMO, INC.
 */
public class DiscoveryDeviceRequest extends LocalOAuthRequest {
    /** Local OAuth認可後に送信するイベントデータ. */
    private Intent mEvent;

    /**
     * Local OAuth認可後に送信するイベントを設定する.
     * @param event イベント
     */
    public void setEvent(final Intent event) {
        mEvent = event;
    }

    @Override
    protected void executeRequest(final String accessToken) {
        List<Event> evts = EventManager.INSTANCE.getEventList(
                NetworkServiceDiscoveryProfileConstants.PROFILE_NAME,
                NetworkServiceDiscoveryProfileConstants.ATTRIBUTE_ON_SERVICE_CHANGE);
        for (int i = 0; i < evts.size(); i++) {
            Event evt = evts.get(i);
            mEvent.putExtra(DConnectMessage.EXTRA_SESSION_KEY, evt.getSessionKey());
            ((DConnectService) getContext()).sendEvent(evt.getReceiverName(), mEvent);
        }
    }
}
