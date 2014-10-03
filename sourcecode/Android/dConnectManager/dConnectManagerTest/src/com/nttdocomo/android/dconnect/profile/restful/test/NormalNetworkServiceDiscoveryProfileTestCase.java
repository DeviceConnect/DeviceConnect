/*
 NormalNetworkServiceDiscoveryProfileTestCase.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.profile.restful.test;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nttdocomo.android.dconnect.test.plugin.profile.TestNetworkServiceDiscoveryProfileConstants;
import com.nttdocomo.dconnect.profile.NetworkServiceDiscoveryProfileConstants;
import com.nttdocomo.dconnect.utils.URIBuilder;

/**
 * Network Service Discoveryプロファイルの正常系テスト.
 * @author NTT DOCOMO, INC.
 */
public class NormalNetworkServiceDiscoveryProfileTestCase extends
        RESTfulDConnectTestCase {

    /**
     * コンストラクタ.
     * @param string テストタグ
     */
    public NormalNetworkServiceDiscoveryProfileTestCase(final String string) {
        super(string);
    }

    @Override
    protected boolean isSearchDevices() {
        return false;
    }

    /**
     * デバイス一覧取得リクエストを送信するテスト.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /network_service_discovery/getnetworkservices
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・servicesに少なくとも1つ以上のサービスが発見されること。
     * ・servicesの中に「Test Success Device」のnameを持ったサービスが存在すること。
     * </pre>
     */
    public void testGetNetworkServices() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(NetworkServiceDiscoveryProfileConstants.PROFILE_NAME);
        builder.setAttribute(NetworkServiceDiscoveryProfileConstants.ATTRIBUTE_GET_NETWORK_SERVICES);
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject response = sendRequest(request);
            assertResultOK(response);
            JSONArray services = response.getJSONArray(
                    NetworkServiceDiscoveryProfileConstants.PARAM_SERVICES);
            assertTrue(services.length() > 0);
            JSONObject service = getServiceByName(services,
                    TestNetworkServiceDiscoveryProfileConstants.DEVICE_NAME);
            assertNotNull(service);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 指定した名前をもつサービスをJSON配列から検索する.
     * 
     * @param services サービス一覧
     * @param name サービス名
     * @return 見つかったサービス. 見つからなかった場合は<code>null</code>
     * @throws JSONException JSONオブジェクトの解析に失敗した場合
     */
    private JSONObject getServiceByName(final JSONArray services, final String name) throws JSONException {
        for (int i = 0; i < services.length(); i++) {
            JSONObject service = services.getJSONObject(i);
            if (name.equals(service.getString(NetworkServiceDiscoveryProfileConstants.PARAM_NAME))) {
                return service;
            }
        }
        return null;
    }
}
