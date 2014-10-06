package com.mycompany.deviceplugin;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;

import com.nttdocomo.android.dconnect.profile.NetworkServiceDiscoveryProfile;
import com.nttdocomo.dconnect.message.intent.message.IntentDConnectMessage;

/**
 * Network Service Discovery プロファイルの実装クラス.
 * 
 * @author docomo
 */
public class ExampleNetworkServiceDiscoveryProfile extends NetworkServiceDiscoveryProfile {
    /** サンプルのデバイスIDを定義する. */
    private static final String DEVICE_ID = "example_device_id";

    /** サンプルのデバイス名を定義する. */
    private static final String DEVICE_NAME = "Example Device";

    /**
     * Network Service Discoveryプロファイル.
     * [/network_service_discovery/getnetworkservices]に対応するメソッド.
     * 
     * @param request リクエスト
     * @param response レスポンス
     * @return 即座に返却する場合はtrue, 非同期に返却する場合はfalse
     */
    @Override
    public boolean onGetGetNetworkServices(final Intent request, final Intent response) {
        List<Bundle> services = new ArrayList<Bundle>();
        Bundle service = new Bundle();
        setId(service, DEVICE_ID);
        setName(service, DEVICE_NAME);
        setType(service, NetworkType.WIFI);
        setOnline(service, true);
        services.add(service);
        setServices(response, services.toArray(new Bundle[services.size()]));
        setResult(response, IntentDConnectMessage.RESULT_OK);
        return true;
    }
}
