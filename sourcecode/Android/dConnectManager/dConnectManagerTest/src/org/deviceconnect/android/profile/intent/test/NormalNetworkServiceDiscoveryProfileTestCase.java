/*
 NormalNetworkServiceDiscoveryProfileTestCase.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.profile.intent.test;

import org.deviceconnect.android.test.plugin.profile.TestNetworkServiceDiscoveryProfileConstants;
import org.deviceconnect.message.intent.message.IntentDConnectMessage;
import org.deviceconnect.profile.NetworkServiceDiscoveryProfileConstants;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;


/**
 * Network Service Discoveryプロファイルの正常系テスト.
 * 
 * @author NTT DOCOMO, INC.
 */
public class NormalNetworkServiceDiscoveryProfileTestCase extends IntentDConnectTestCase {
    /**
     * コンストラクタ.
     * 
     * @param string テストタグ
     */
    public NormalNetworkServiceDiscoveryProfileTestCase(final String string) {
        super(string);
    }

    /**
     * getnetworkservicesでサービスをの探索を行う.
     * 
     * <pre>
     * 【Intent通信】
     * Method: GET
     * Extra:
     *     profile=network_service_discovery
     *     attribute=getnetworkservices
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・servicesに少なくとも1つ以上のサービスが発見されること。
     * ・servicesの中に「Test Success Device」のnameを持ったサービスが存在すること。
     * </pre>
     */
    public void testGetNetworkServices() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_GET);
        request.putExtra(IntentDConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(IntentDConnectMessage.EXTRA_PROFILE, NetworkServiceDiscoveryProfileConstants.PROFILE_NAME);
        request.putExtra(IntentDConnectMessage.EXTRA_ATTRIBUTE,
                NetworkServiceDiscoveryProfileConstants.ATTRIBUTE_GET_NETWORK_SERVICES);
        Intent response = sendRequest(request);

        assertResultOK(response);
        Parcelable[] services =
                (Parcelable[]) response.getParcelableArrayExtra(NetworkServiceDiscoveryProfileConstants.PARAM_SERVICES);

        assertTrue("services not found.", services.length > 0);
        boolean isFoundName = false;
        for (int i = 0; i < services.length; i++) {
            Bundle service = (Bundle) services[i];
            String name = service.getString(NetworkServiceDiscoveryProfileConstants.PARAM_NAME);
            String id = service.getString(NetworkServiceDiscoveryProfileConstants.PARAM_ID);
            String type = service.getString(NetworkServiceDiscoveryProfileConstants.PARAM_TYPE);
            assertNotNull("service.name is null", name);
            assertNotNull("service.id is null", id);
            assertNotNull("service.type is null", type);
            if (name.equals(TestNetworkServiceDiscoveryProfileConstants.DEVICE_NAME)) {
                isFoundName = true;
                break;
            }
        }
        if (!isFoundName) {
            fail("Not found Test DevicePlugin.");
        }
    }
}
