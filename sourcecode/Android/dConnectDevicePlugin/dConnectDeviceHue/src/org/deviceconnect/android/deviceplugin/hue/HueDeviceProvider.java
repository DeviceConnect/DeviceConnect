/*
HueDeviceProvider
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

package org.deviceconnect.android.deviceplugin.hue;

import org.deviceconnect.android.message.DConnectMessageServiceProvider;

import android.app.Service;


/**
 * hueデバイスプラグインサービスプロバイダ.
 * @param <T> Service
 * @author NTT DOCOMO, INC.
 */
public class HueDeviceProvider<T extends Service> extends DConnectMessageServiceProvider<Service> {

    /**
     * サービスクラスを取得する.
     * 
     * @return サービスクラス
     */
    @SuppressWarnings("unchecked")
    @Override
    protected Class<Service> getServiceClass() {
        Class<? extends Service> clazz = (Class<? extends Service>) HueDeviceService.class;
        return (Class<Service>) clazz;
    }

}
