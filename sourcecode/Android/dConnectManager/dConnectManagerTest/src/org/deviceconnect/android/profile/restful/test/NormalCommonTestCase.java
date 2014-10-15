/*
 NormalCommonTestCase.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.profile.restful.test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.deviceconnect.profile.AuthorizationProfileConstants;
import org.deviceconnect.profile.DConnectProfileConstants;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * プロファイル共通の正常系テスト.
 * @author NTT DOCOMO, INC.
 */
public class NormalCommonTestCase extends RESTfulDConnectTestCase {

    /**
     * コンストラクタ.
     * 
     * @param tag テストタグ
     */
    public NormalCommonTestCase(final String tag) {
        super(tag);
    }

    /**
     * URLエンコード済みの予約文字がリクエストパラメータ値に含まれている場合も、正常にリクエストが処理されること。
     * <p>
     * URLの予約文字についてはRFC3986 Appendix Aを参照のこと。
     * </p>
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /battery?deviceId&accessToken=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・各リクエストパラメータに指定した文字列がそのまま返されること
     * </pre>
     * 
     * @throws UnsupportedEncodingException URLエンコーディングに失敗した場合
     */
    public void testRequestParametersWithURLEncodedReservedCharacters() throws UnsupportedEncodingException {
        final String value = ":/?#[]@!$&'()*+,;=";
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/unique/test/ping");
        builder.append("?key1=" + URLEncoder.encode(value, "UTF-8"));
        builder.append("&key2=" + URLEncoder.encode(value, "UTF-8"));
        builder.append("&");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        builder.append("&key3=" + URLEncoder.encode(value, "UTF-8"));
        builder.append("&key4=" + URLEncoder.encode(value, "UTF-8"));
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultOK(root);
            assertEquals("GET /unique/test/ping", root.getString("path"));
            assertEquals(value, root.getString("key1"));
            assertEquals(value, root.getString("key2"));
            assertEquals(value, root.getString("key3"));
            assertEquals(value, root.getString("key4"));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * POSTリクエストのボディ部とパラメータ部の両方にリクエストパラメータを指定するテスト.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /battery?deviceId&accessToken=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・キーが同じパラメータについては、ボディでの指定が優先されること。
     * </pre>
     * 
     * @throws UnsupportedEncodingException リクエストのBodyのエンコーディングに失敗した場合
     */
    public void testPostRequestParametersBothBodyAndParameterPart() throws UnsupportedEncodingException {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/unique/test/ping");
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=unknown");
        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpPost request = new HttpPost(builder.toString());
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId()));
            params.add(new BasicNameValuePair(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken()));
            request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            JSONObject root = sendRequest(request);
            assertResultOK(root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * PUTリクエストのボディ部とパラメータ部の両方にリクエストパラメータを指定するテスト.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /battery?deviceId&accessToken=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・キーが同じパラメータについては、ボディでの指定が優先されること。
     * </pre>
     * 
     * @throws UnsupportedEncodingException リクエストのBodyのエンコーディングに失敗した場合
     */
    public void testPutRequestParametersBothBodyAndParameterPart() throws UnsupportedEncodingException {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/unique/test/ping");
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=unknown");
        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpPut request = new HttpPut(builder.toString());
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId()));
            params.add(new BasicNameValuePair(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken()));
            request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            JSONObject root = sendRequest(request);
            assertResultOK(root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }
}
