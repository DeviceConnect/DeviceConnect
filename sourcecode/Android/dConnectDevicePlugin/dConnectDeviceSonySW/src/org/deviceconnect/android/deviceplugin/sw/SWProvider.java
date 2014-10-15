/*
 SWProvider.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.sw;

import android.app.Service;
import org.deviceconnect.android.message.DConnectMessageServiceProvider;

/**
 * Device Connect Manager本体からのインテントを受信するクラス.
 * @author NTT DOCOMO, INC.
 * @param <T> SonySWデバイスプラグインのプロファイルを公開するサービスのクラス
 */
public class SWProvider<T extends Service> extends DConnectMessageServiceProvider<Service> {

    @SuppressWarnings("unchecked")
    @Override
    protected Class<Service> getServiceClass() {
        Class<? extends Service> clazz = (Class<? extends Service>) SWService.class;
        return (Class<Service>) clazz;
    }

}
