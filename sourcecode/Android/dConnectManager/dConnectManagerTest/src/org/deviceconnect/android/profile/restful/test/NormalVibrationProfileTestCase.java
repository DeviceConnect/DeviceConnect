/*
 NormalVibrationProfileTestCase.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.profile.restful.test;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import org.deviceconnect.profile.AuthorizationProfileConstants;
import org.deviceconnect.profile.DConnectProfileConstants;
import org.deviceconnect.profile.VibrationProfileConstants;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Vibratorプロファイルの正常系テスト.
 * @author NTT DOCOMO, INC.
 */
public class NormalVibrationProfileTestCase extends RESTfulDConnectTestCase {

    /**
     * コンストラクタ.
     * @param string テストタグ
     */
    public NormalVibrationProfileTestCase(final String string) {
        super(string);
    }

    /**
     * Vibration開始要求を送信するテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /vibration/vibrate?deviceid=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testPutVibrate001() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + VibrationProfileConstants.PROFILE_NAME);
        builder.append("/" + VibrationProfileConstants.ATTRIBUTE_VIBRATE);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&" + AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpUriRequest request = new HttpPut(builder.toString());

            // パターンのデータを追加
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(VibrationProfileConstants.PARAM_PATTERN,
                    "100, 100, 100, 100"));
            ((HttpPut) request).setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

            JSONObject root = sendRequest(request);
            assertResultOK(root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        } catch (Exception e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * Vibration開始要求を送信するテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /vibration/vibrate?deviceid=xxxx&pattern=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testPutVibrate002() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + VibrationProfileConstants.PROFILE_NAME);
        builder.append("/" + VibrationProfileConstants.ATTRIBUTE_VIBRATE);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(VibrationProfileConstants.PARAM_PATTERN + "=500,500,500");
        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpUriRequest request = new HttpPut(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultOK(root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * Vibration停止要求を送信するテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /vibration/vibrate?deviceid=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testDeleteVibrate() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + VibrationProfileConstants.PROFILE_NAME);
        builder.append("/" + VibrationProfileConstants.ATTRIBUTE_VIBRATE);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpUriRequest request = new HttpDelete(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultOK(root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }
}
