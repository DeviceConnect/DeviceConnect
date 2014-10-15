/*
 FailNetworkServiceDiscoveryProfileTestCase.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.profile.restful.test;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.deviceconnect.android.test.plugin.profile.TestNetworkServiceDiscoveryProfileConstants;
import org.deviceconnect.message.DConnectMessage.ErrorCode;
import org.deviceconnect.profile.DConnectProfileConstants;
import org.deviceconnect.profile.NetworkServiceDiscoveryProfileConstants;
import org.deviceconnect.utils.URIBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Network Service Discovery プロファイルの異常系テスト.
 * @author NTT DOCOMO, INC.
 */
public class FailNetworkServiceDiscoveryProfileTestCase extends RESTfulDConnectTestCase {
    /**
     * コンストラクタ.
     * 
     * @param string テストタグ
     */
    public FailNetworkServiceDiscoveryProfileTestCase(final String string) {
        super(string);
    }

    /**
     * POSTメソッドでgetnetworkservicesでデバイスの探索を行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /network_service_discovery/getnetworkservices
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetNetworkServices001() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(NetworkServiceDiscoveryProfileConstants.PROFILE_NAME);
        builder.setAttribute(NetworkServiceDiscoveryProfileConstants.ATTRIBUTE_GET_NETWORK_SERVICES);
        try {
            HttpUriRequest request = new HttpPost(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_SUPPORT_ACTION.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * PUTメソッドでgetnetworkservicesでデバイスの探索を行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /network_service_discovery/getnetworkservices
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetNetworkServices002() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(NetworkServiceDiscoveryProfileConstants.PROFILE_NAME);
        builder.setAttribute(NetworkServiceDiscoveryProfileConstants.ATTRIBUTE_GET_NETWORK_SERVICES);
        try {
            HttpUriRequest request = new HttpPut(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_SUPPORT_ATTRIBUTE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * DELETEメソッドでgetnetworkservicesでデバイスの探索を行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /network_service_discovery/getnetworkservices
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetNetworkServices003() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(NetworkServiceDiscoveryProfileConstants.PROFILE_NAME);
        builder.setAttribute(NetworkServiceDiscoveryProfileConstants.ATTRIBUTE_GET_NETWORK_SERVICES);
        try {
            HttpUriRequest request = new HttpDelete(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_SUPPORT_ATTRIBUTE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceidを指定してgetnetworkservicesでデバイスの探索を行う.
     * 
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /network_service_discovery/getnetworkservices?deviceid=xxxx
     * </pre>
     * 
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・servicesに少なくとも1つ以上のサービスが発見されること。
     * ・servicesの中に「Test Success Device」のnameを持ったサービスが存在すること。
     * </pre>
     */
    public void testGetNetworkServices004() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(NetworkServiceDiscoveryProfileConstants.PROFILE_NAME);
        builder.setAttribute(NetworkServiceDiscoveryProfileConstants.ATTRIBUTE_GET_NETWORK_SERVICES);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultOK(root);
            JSONArray services = root.getJSONArray(NetworkServiceDiscoveryProfileConstants.PARAM_SERVICES);
            assertNotNull("services is null.", root);
            assertTrue("services not found.", services.length() > 0);
            boolean isFoundName = false;
            for (int i = 0; i < services.length(); i++) {
                JSONObject service = services.getJSONObject(i);
                String name = service.getString(NetworkServiceDiscoveryProfileConstants.PARAM_NAME);
                String id = service.getString(NetworkServiceDiscoveryProfileConstants.PARAM_ID);
                String type = service.getString(NetworkServiceDiscoveryProfileConstants.PARAM_TYPE);
                assertNotNull("service.name is null", name);
                assertNotNull("service.id is null", id);
                assertNotNull("service.type is null", type);
                if (name.equals(TestNetworkServiceDiscoveryProfileConstants.DEVICE_NAME)) {
                    isFoundName = true;
                }
            }
            if (!isFoundName) {
                fail("Not found Test DevicePlugin.");
            }
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }
}
