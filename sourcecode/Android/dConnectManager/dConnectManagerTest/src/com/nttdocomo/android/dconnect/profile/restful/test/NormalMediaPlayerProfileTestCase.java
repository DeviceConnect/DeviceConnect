/*
 NormalMediaPlayerProfileTestCase.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.profile.restful.test;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONException;
import org.json.JSONObject;

import com.nttdocomo.android.dconnect.test.plugin.profile.TestMediaPlayerProfileConstants;
import com.nttdocomo.dconnect.message.DConnectMessage;
import com.nttdocomo.dconnect.profile.AuthorizationProfileConstants;
import com.nttdocomo.dconnect.profile.DConnectProfileConstants;
import com.nttdocomo.dconnect.profile.MediaPlayerProfileConstants;
import com.nttdocomo.dconnect.utils.URIBuilder;

/**
 * MediaPlayerプロファイルの正常系テスト.
 * @author NTT DOCOMO, INC.
 */
public class NormalMediaPlayerProfileTestCase extends RESTfulDConnectTestCase
    implements TestMediaPlayerProfileConstants {

    /**
     * コンストラクタ.
     * 
     * @param tag テストタグ
     */
    public NormalMediaPlayerProfileTestCase(final String tag) {
        super(tag);
    }

    /**
     * 再生コンテンツの変更要求を送信するテスト.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /media_player/media?deviceId=xxxx&mediaId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testPutMedia() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(MediaPlayerProfileConstants.PROFILE_NAME);
        builder.setAttribute(MediaPlayerProfileConstants.ATTRIBUTE_MEDIA);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(MediaPlayerProfileConstants.PARAM_MEDIA_ID, TEST_MEDIA_ID);
        try {
            HttpUriRequest request = new HttpPut(builder.toString());
            JSONObject response = sendRequest(request);
            assertResultOK(response);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 再生コンテンツ情報の取得要求を送信するテスト.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /media_player/media?deviceId=xxxx&mediaId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testGetMedia() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(MediaPlayerProfileConstants.PROFILE_NAME);
        builder.setAttribute(MediaPlayerProfileConstants.ATTRIBUTE_MEDIA);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(MediaPlayerProfileConstants.PARAM_MEDIA_ID, TEST_MEDIA_ID);
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject response = sendRequest(request);
            assertResultOK(response);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 再生コンテンツ一覧の取得要求を送信するテスト.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /media_player/media_list?deviceId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testGetMediaList() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(MediaPlayerProfileConstants.PROFILE_NAME);
        builder.setAttribute(MediaPlayerProfileConstants.ATTRIBUTE_MEDIA_LIST);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject response = sendRequest(request);
            assertResultOK(response);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * コンテンツ再生状態の取得要求を送信するテスト.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /media_player/play_status?deviceId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testGetPlayStatus() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(MediaPlayerProfileConstants.PROFILE_NAME);
        builder.setAttribute(MediaPlayerProfileConstants.ATTRIBUTE_PLAY_STATUS);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject response = sendRequest(request);
            assertResultOK(response);
            assertEquals(MediaPlayerProfileConstants.PlayStatus.PLAY.getValue(),
                    response.getString(MediaPlayerProfileConstants.PARAM_STATUS));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メディアプレイヤーの再生要求を送信するテスト.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /media_player/play?deviceId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testPutPlay() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(MediaPlayerProfileConstants.PROFILE_NAME);
        builder.setAttribute(MediaPlayerProfileConstants.ATTRIBUTE_PLAY);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        try {
            HttpUriRequest request = new HttpPut(builder.toString());
            JSONObject response = sendRequest(request);
            assertResultOK(response);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メディアプレイヤーの停止要求を送信するテスト.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /media_player/stop?deviceId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testPutStop() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(MediaPlayerProfileConstants.PROFILE_NAME);
        builder.setAttribute(MediaPlayerProfileConstants.ATTRIBUTE_STOP);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        try {
            HttpUriRequest request = new HttpPut(builder.toString());
            JSONObject response = sendRequest(request);
            assertResultOK(response);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メディアプレイヤーの一時停止要求を送信するテスト.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /media_player/pause?deviceId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testPutPause() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(MediaPlayerProfileConstants.PROFILE_NAME);
        builder.setAttribute(MediaPlayerProfileConstants.ATTRIBUTE_PAUSE);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        try {
            HttpUriRequest request = new HttpPut(builder.toString());
            JSONObject response = sendRequest(request);
            assertResultOK(response);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メディアプレイヤーの一時停止解除要求を送信するテスト.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /media_player/resume?deviceId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testPutResume() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(MediaPlayerProfileConstants.PROFILE_NAME);
        builder.setAttribute(MediaPlayerProfileConstants.ATTRIBUTE_RESUME);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        try {
            HttpUriRequest request = new HttpPut(builder.toString());
            JSONObject response = sendRequest(request);
            assertResultOK(response);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 再生位置の変更要求を送信するテスト.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /media_player/seek?deviceId=xxxx&pos=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testPutSeek() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(MediaPlayerProfileConstants.PROFILE_NAME);
        builder.setAttribute(MediaPlayerProfileConstants.ATTRIBUTE_SEEK);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(MediaPlayerProfileConstants.PARAM_POS, String.valueOf(0));
        try {
            HttpUriRequest request = new HttpPut(builder.toString());
            JSONObject response = sendRequest(request);
            assertResultOK(response);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 再生位置の取得要求を送信するテスト.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /media_player/seek?deviceId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testGetSeek() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(MediaPlayerProfileConstants.PROFILE_NAME);
        builder.setAttribute(MediaPlayerProfileConstants.ATTRIBUTE_SEEK);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject response = sendRequest(request);
            assertResultOK(response);
            assertEquals(0, response.getInt(MediaPlayerProfileConstants.PARAM_POS));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 再生音量の変更要求を送信するテスト.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /media_player/volume?deviceId=xxxx&volume=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testPutVolume() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(MediaPlayerProfileConstants.PROFILE_NAME);
        builder.setAttribute(MediaPlayerProfileConstants.ATTRIBUTE_VOLUME);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(MediaPlayerProfileConstants.PARAM_VOLUME, String.valueOf(TEST_VOLUME));
        try {
            HttpUriRequest request = new HttpPut(builder.toString());
            JSONObject response = sendRequest(request);
            assertResultOK(response);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 再生音量の取得要求を送信するテスト.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /media_player/volume?deviceId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testGetVolume() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(MediaPlayerProfileConstants.PROFILE_NAME);
        builder.setAttribute(MediaPlayerProfileConstants.ATTRIBUTE_VOLUME);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject response = sendRequest(request);
            assertResultOK(response);
            assertEquals(0.5, response.getDouble(MediaPlayerProfileConstants.PARAM_VOLUME));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * ミュートを有効にする要求を送信するテスト.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /media_player/mute?deviceId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testPutMute() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(MediaPlayerProfileConstants.PROFILE_NAME);
        builder.setAttribute(MediaPlayerProfileConstants.ATTRIBUTE_MUTE);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        try {
            HttpUriRequest request = new HttpPut(builder.toString());
            JSONObject response = sendRequest(request);
            assertResultOK(response);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * ミュートを無効にする要求を送信するテスト.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /media_player/mute?deviceId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testDeleteMute() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(MediaPlayerProfileConstants.PROFILE_NAME);
        builder.setAttribute(MediaPlayerProfileConstants.ATTRIBUTE_MUTE);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        try {
            HttpUriRequest request = new HttpDelete(builder.toString());
            JSONObject response = sendRequest(request);
            assertResultOK(response);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * ミュート状態の取得要求を送信するテスト.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /media_player/mute?deviceId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testGetMute() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(MediaPlayerProfileConstants.PROFILE_NAME);
        builder.setAttribute(MediaPlayerProfileConstants.ATTRIBUTE_MUTE);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject response = sendRequest(request);
            assertResultOK(response);
            assertEquals(true, response.getBoolean(MediaPlayerProfileConstants.PARAM_MUTE));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * コンテンツ再生状態変化通知のコールバック登録テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /media_player/onstatuschange?deviceid=xxxx&session_key=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testPutOnStatusChangePlay() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + MediaPlayerProfileConstants.PROFILE_NAME);
        builder.append("/" + MediaPlayerProfileConstants.ATTRIBUTE_ON_STATUS_CHANGE);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(DConnectProfileConstants.PARAM_SESSION_KEY + "=" + getClientId());
        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpUriRequest request = new HttpPut(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultOK(root);
            // イベントメッセージを受け取る
            JSONObject event = waitForEvent();
            assertNotNull(event);
            assertEquals(MediaPlayerProfileConstants.ATTRIBUTE_ON_STATUS_CHANGE,
                    event.getString(DConnectMessage.EXTRA_ATTRIBUTE));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 再生コンテンツ再生状態変化通知のコールバック解除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /media_player/onstatuschange?deviceid=xxxx&session_key=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testDeleteOnPlayStatusChange() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + MediaPlayerProfileConstants.PROFILE_NAME);
        builder.append("/" + MediaPlayerProfileConstants.ATTRIBUTE_ON_STATUS_CHANGE);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(DConnectProfileConstants.PARAM_SESSION_KEY + "=" + getClientId());
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
