/*
 NormalMediaStreamRecordingProfileTestCase.java
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
import org.deviceconnect.android.test.plugin.profile.TestMediaStreamRecordingProfileConstants;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.profile.AuthorizationProfileConstants;
import org.deviceconnect.profile.DConnectProfileConstants;
import org.deviceconnect.profile.MediaStreamRecordingProfileConstants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * MediaStreamRecordingプロファイルの正常系テスト.
 * @author NTT DOCOMO, INC.
 */
public class NormalMediaStreamRecordingProfileTestCase extends RESTfulDConnectTestCase {

    /**
     * コンストラクタ.
     * @param tag テストタグ
     */
    public NormalMediaStreamRecordingProfileTestCase(final String tag) {
        super(tag);
    }

    /**
     * 指定したスマートデバイス上で使用可能なカメラ情報を取得するテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /mediastream_recording/mediarecorder?deviceid=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・recorderに長さ1のBundle配列が格納されていること。
     * ・recorder[0].idが"test_camera_0"であること。
     * ・recorder[0].stateが"inactive"であること。
     * ・recorder[0].imageWidthが1920であること。
     * ・recorder[0].imageHeightが1080であること。
     * ・recorder[0].mimeTypeが"video/mp4"であること。
     * ・recorder[0].configが"test_config"であること。
     * </pre>
     */
    public void testGetMediaRecorder() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + MediaStreamRecordingProfileConstants.PROFILE_NAME);
        builder.append("/" + MediaStreamRecordingProfileConstants.ATTRIBUTE_MEDIARECORDER);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertNotNull("root is null.", root);
            assertEquals(DConnectMessage.RESULT_OK,
                    root.getInt(DConnectMessage.EXTRA_RESULT));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 指定したスマートデバイスに対して写真撮影依頼を送信するテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /mediastream_recording/takephoto?deviceid=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・uriが"test.mp4"であること。
     * </pre>
     */
    public void testTakePhoto001() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + MediaStreamRecordingProfileConstants.PROFILE_NAME);
        builder.append("/" + MediaStreamRecordingProfileConstants.ATTRIBUTE_TAKE_PHOTO);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpUriRequest request = new HttpPost(builder.toString());
            JSONObject root = sendRequest(request);
            assertNotNull("root is null.", root);
            assertEquals(DConnectMessage.RESULT_OK,
                    root.getInt(DConnectMessage.EXTRA_RESULT));
            assertEquals(getFileURI(TestMediaStreamRecordingProfileConstants.URI),
                    root.getString(MediaStreamRecordingProfileConstants.PARAM_URI));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 指定したスマートデバイスに対して写真撮影依頼を送信するテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /mediastream_recording/takephoto?deviceid=xxxx&target=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・uriが"test.mp4"であること。
     * </pre>
     */
    public void testTakePhoto002() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + MediaStreamRecordingProfileConstants.PROFILE_NAME);
        builder.append("/" + MediaStreamRecordingProfileConstants.ATTRIBUTE_TAKE_PHOTO);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(MediaStreamRecordingProfileConstants.PARAM_TARGET + "="
                + TestMediaStreamRecordingProfileConstants.ID);
        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpUriRequest request = new HttpPost(builder.toString());
            JSONObject root = sendRequest(request);
            assertNotNull("root is null.", root);
            assertEquals(DConnectMessage.RESULT_OK,
                    root.getInt(DConnectMessage.EXTRA_RESULT));
            assertEquals(getFileURI(TestMediaStreamRecordingProfileConstants.URI),
                    root.getString(MediaStreamRecordingProfileConstants.PARAM_URI));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 指定したスマートデバイスに対して動画撮影または音声録音の開始依頼を送信するテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /mediastream_recording/record?deviceid=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・uriが"test.mp4"であること。
     * </pre>
     */
    public void testRecord001() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + MediaStreamRecordingProfileConstants.PROFILE_NAME);
        builder.append("/" + MediaStreamRecordingProfileConstants.ATTRIBUTE_RECORD);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpUriRequest request = new HttpPost(builder.toString());
            JSONObject root = sendRequest(request);
            assertNotNull("root is null.", root);
            assertEquals(DConnectMessage.RESULT_OK,
                    root.getInt(DConnectMessage.EXTRA_RESULT));
            assertEquals(getFileURI(TestMediaStreamRecordingProfileConstants.URI),
                    root.getString(MediaStreamRecordingProfileConstants.PARAM_URI));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 指定したスマートデバイスに対して動画撮影または音声録音の開始依頼を送信するテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /mediastream_recording/record?deviceid=xxxx&target=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・uriが"test.mp4"であること。
     * </pre>
     */
    public void testRecord002() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + MediaStreamRecordingProfileConstants.PROFILE_NAME);
        builder.append("/" + MediaStreamRecordingProfileConstants.ATTRIBUTE_RECORD);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(MediaStreamRecordingProfileConstants.PARAM_TARGET + "="
                + TestMediaStreamRecordingProfileConstants.ID);
        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpUriRequest request = new HttpPost(builder.toString());
            JSONObject root = sendRequest(request);
            assertNotNull("root is null.", root);
            assertEquals(DConnectMessage.RESULT_OK,
                    root.getInt(DConnectMessage.EXTRA_RESULT));
            assertEquals(getFileURI(TestMediaStreamRecordingProfileConstants.URI),
                    root.getString(MediaStreamRecordingProfileConstants.PARAM_URI));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 指定したスマートデバイスに対して動画撮影または音声録音の開始依頼を送信するテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /mediastream_recording/record?deviceid=xxxx&timeslice=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・uriが"test.mp4"であること。
     * </pre>
     */
    public void testRecord003() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + MediaStreamRecordingProfileConstants.PROFILE_NAME);
        builder.append("/" + MediaStreamRecordingProfileConstants.ATTRIBUTE_RECORD);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(MediaStreamRecordingProfileConstants.PARAM_TIME_SLICE + "=3600");
        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpUriRequest request = new HttpPost(builder.toString());
            JSONObject root = sendRequest(request);
            assertNotNull("root is null.", root);
            assertEquals(DConnectMessage.RESULT_OK,
                    root.getInt(DConnectMessage.EXTRA_RESULT));
            assertEquals(getFileURI(TestMediaStreamRecordingProfileConstants.URI),
                    root.getString(MediaStreamRecordingProfileConstants.PARAM_URI));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 指定したスマートデバイスに対して動画撮影または音声録音の開始依頼を送信するテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /mediastream_recording/record?deviceid=xxxx&target=xxxx&timeslice=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・uriが"test.mp4"であること。
     * </pre>
     */
    public void testRecord004() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + MediaStreamRecordingProfileConstants.PROFILE_NAME);
        builder.append("/" + MediaStreamRecordingProfileConstants.ATTRIBUTE_RECORD);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(MediaStreamRecordingProfileConstants.PARAM_TARGET + "="
                + TestMediaStreamRecordingProfileConstants.ID);
        builder.append("&");
        builder.append(MediaStreamRecordingProfileConstants.PARAM_TIME_SLICE + "=3600");
        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpUriRequest request = new HttpPost(builder.toString());
            JSONObject root = sendRequest(request);
            assertNotNull("root is null.", root);
            assertEquals(DConnectMessage.RESULT_OK,
                    root.getInt(DConnectMessage.EXTRA_RESULT));
            assertEquals(getFileURI(TestMediaStreamRecordingProfileConstants.URI),
                    root.getString(MediaStreamRecordingProfileConstants.PARAM_URI));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 指定したスマートデバイスに対して動画撮影または音声録音の一時停止依頼を送信するテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /mediastream_recording/pause?deviceid=xxxx&mediaid=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testPause() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + MediaStreamRecordingProfileConstants.PROFILE_NAME);
        builder.append("/" + MediaStreamRecordingProfileConstants.ATTRIBUTE_PAUSE);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpUriRequest request = new HttpPut(builder.toString());
            JSONObject root = sendRequest(request);
            assertNotNull("root is null.", root);
            assertEquals(DConnectMessage.RESULT_OK,
                    root.getInt(DConnectMessage.EXTRA_RESULT));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 指定したスマートデバイスに対して動画撮影または音声録音の再開依頼を送信するテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /mediastream_recording/resume?deviceid=xxxx&mediaid=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testResume() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + MediaStreamRecordingProfileConstants.PROFILE_NAME);
        builder.append("/" + MediaStreamRecordingProfileConstants.ATTRIBUTE_RESUME);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpUriRequest request = new HttpPut(builder.toString());
            JSONObject root = sendRequest(request);
            assertNotNull("root is null.", root);
            assertEquals(DConnectMessage.RESULT_OK,
                    root.getInt(DConnectMessage.EXTRA_RESULT));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 指定したスマートデバイスに対して動画撮影または音声録音の停止依頼を送信するテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /mediastream_recording/stop?deviceid=xxxx&mediaid=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testStop() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + MediaStreamRecordingProfileConstants.PROFILE_NAME);
        builder.append("/" + MediaStreamRecordingProfileConstants.ATTRIBUTE_STOP);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpUriRequest request = new HttpPut(builder.toString());
            JSONObject root = sendRequest(request);
            assertNotNull("root is null.", root);
            assertEquals(DConnectMessage.RESULT_OK,
                    root.getInt(DConnectMessage.EXTRA_RESULT));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 指定したスマートデバイスに対して動画撮影または音声録音のミュート依頼を送信するテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /mediastream_recording/mutetrack?deviceid=xxxx&mediaid=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testMuteTrack() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + MediaStreamRecordingProfileConstants.PROFILE_NAME);
        builder.append("/" + MediaStreamRecordingProfileConstants.ATTRIBUTE_MUTETRACK);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
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
     * 指定したスマートデバイスに対して動画撮影または音声録音のミュート依頼を送信するテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /mediastream_recording/unmutetrack?deviceid=xxxx&mediaid=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testUnmuteTrack() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + MediaStreamRecordingProfileConstants.PROFILE_NAME);
        builder.append("/" + MediaStreamRecordingProfileConstants.ATTRIBUTE_UNMUTETRACK);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
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
     * 指定したスマートデバイスのカメラがサポートするオプションの一覧を取得するテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /mediastream_recording/options?deviceid=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・settingsに長さ1のBundle配列が格納されていること。
     * ・settings[0].imageWidthが1920であること。
     * ・settings[0].imageHeightが1080であること。
     * ・settings[0].mimeTypeが"video/mp4"であること。
     * </pre>
     */
    public void testGetOptions001() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + MediaStreamRecordingProfileConstants.PROFILE_NAME);
        builder.append("/" + MediaStreamRecordingProfileConstants.ATTRIBUTE_OPTIONS);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertNotNull("root is null.", root);
            assertEquals(DConnectMessage.RESULT_OK,
                    root.getInt(DConnectMessage.EXTRA_RESULT));
            
            JSONObject imageWidth = root.getJSONObject(MediaStreamRecordingProfileConstants.PARAM_IMAGE_WIDTH);
            assertEquals(0, 
                    imageWidth.getInt(MediaStreamRecordingProfileConstants.PARAM_MAX));
            assertEquals(0, 
                    imageWidth.getInt(MediaStreamRecordingProfileConstants.PARAM_MIN));
            JSONObject imageHeight = root.getJSONObject(MediaStreamRecordingProfileConstants.PARAM_IMAGE_WIDTH);
            assertEquals(0, 
                    imageHeight.getInt(MediaStreamRecordingProfileConstants.PARAM_MAX));
            assertEquals(0, 
                    imageHeight.getInt(MediaStreamRecordingProfileConstants.PARAM_MIN));
            JSONArray mimeTypes = root.getJSONArray(MediaStreamRecordingProfileConstants.PARAM_MIME_TYPE);
            assertEquals("video/mp4", mimeTypes.get(0));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 指定したスマートデバイスのカメラがサポートするオプションの一覧を取得するテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /mediastream_recording/options?deviceid=xxxx&target=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・settingsに長さ1のBundle配列が格納されていること。
     * ・settings[0].imageWidthが1920であること。
     * ・settings[0].imageHeightが1080であること。
     * ・settings[0].mimeTypeが"video/mp4"であること。
     * </pre>
     */
    public void testGetOptions002() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + MediaStreamRecordingProfileConstants.PROFILE_NAME);
        builder.append("/" + MediaStreamRecordingProfileConstants.ATTRIBUTE_OPTIONS);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(MediaStreamRecordingProfileConstants.PARAM_TARGET + "="
                + TestMediaStreamRecordingProfileConstants.ID);
        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertNotNull("root is null.", root);
            assertEquals(DConnectMessage.RESULT_OK,
                    root.getInt(DConnectMessage.EXTRA_RESULT));
            
            JSONObject imageWidth = root.getJSONObject(MediaStreamRecordingProfileConstants.PARAM_IMAGE_WIDTH);
            assertEquals(0, 
                    imageWidth.getInt(MediaStreamRecordingProfileConstants.PARAM_MAX));
            assertEquals(0, 
                    imageWidth.getInt(MediaStreamRecordingProfileConstants.PARAM_MIN));
            JSONObject imageHeight = root.getJSONObject(MediaStreamRecordingProfileConstants.PARAM_IMAGE_WIDTH);
            assertEquals(0, 
                    imageHeight.getInt(MediaStreamRecordingProfileConstants.PARAM_MAX));
            assertEquals(0, 
                    imageHeight.getInt(MediaStreamRecordingProfileConstants.PARAM_MIN));
            JSONArray mimeTypes = root.getJSONArray(MediaStreamRecordingProfileConstants.PARAM_MIME_TYPE);
            assertEquals("video/mp4", mimeTypes.get(0));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 指定したスマートデバイスのカメラにオプションを設定するテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /mediastream_recording/options?deviceid=xxxx&target=xxxx&imageWidth=xxxx&imageHeight=xxxx&mimeType=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testPutOptions() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + MediaStreamRecordingProfileConstants.PROFILE_NAME);
        builder.append("/" + MediaStreamRecordingProfileConstants.ATTRIBUTE_OPTIONS);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(MediaStreamRecordingProfileConstants.PARAM_TARGET + "="
                + TestMediaStreamRecordingProfileConstants.ID);
        builder.append("&");
        builder.append(MediaStreamRecordingProfileConstants.PARAM_IMAGE_WIDTH);
        builder.append("=");
        builder.append(TestMediaStreamRecordingProfileConstants.IMAGE_WIDTH);
        builder.append("&");
        builder.append(MediaStreamRecordingProfileConstants.PARAM_IMAGE_HEIGHT);
        builder.append("=");
        builder.append(TestMediaStreamRecordingProfileConstants.IMAGE_HEIGHT);
        builder.append("&");
        builder.append(MediaStreamRecordingProfileConstants.PARAM_MIME_TYPE + "="
                + TestMediaStreamRecordingProfileConstants.MIME_TYPE);
        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpUriRequest request = new HttpPut(builder.toString());
            JSONObject root = sendRequest(request);
            assertNotNull("root is null.", root);
            assertEquals(DConnectMessage.RESULT_OK,
                    root.getInt(DConnectMessage.EXTRA_RESULT));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 写真撮影イベントのコールバック登録テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /mediastream_recording/onphoto?deviceid=xxxx&session_key=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・コールバック登録後にイベントを受信すること。
     * </pre>
     */
    public void testOnPhoto01() {
        try {
            JSONObject event = registerEventCallback(MediaStreamRecordingProfileConstants.ATTRIBUTE_ON_PHOTO);
            JSONObject photo = event.getJSONObject(MediaStreamRecordingProfileConstants.PARAM_PHOTO);
            assertEquals(TestMediaStreamRecordingProfileConstants.PATH, 
                    photo.getString(MediaStreamRecordingProfileConstants.PARAM_PATH));
            assertEquals(TestMediaStreamRecordingProfileConstants.MIME_TYPE, 
                    photo.getString(MediaStreamRecordingProfileConstants.PARAM_MIME_TYPE));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 写真撮影イベントのコールバック解除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /mediastream_recording/onphoto?deviceid=xxxx&session_key=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testOnPhoto02() {
        unregisterEventCallback(MediaStreamRecordingProfileConstants.ATTRIBUTE_ON_PHOTO);
    }

    /**
     * 動画撮影または音声録音開始イベントのコールバック登録テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /mediastream_recording/onrecordingchange?deviceid=xxxx&session_key=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・コールバック登録後にイベントを受信すること。
     * </pre>
     */
    public void testOnRecording01() {
        try {
            JSONObject event =
                    registerEventCallback(MediaStreamRecordingProfileConstants.ATTRIBUTE_ON_RECORDING_CHANGE);
            JSONObject media = event.getJSONObject(MediaStreamRecordingProfileConstants.PARAM_MEDIA);
            assertEquals(TestMediaStreamRecordingProfileConstants.STATUS, 
                    media.getString(MediaStreamRecordingProfileConstants.PARAM_STATUS));
            assertEquals(TestMediaStreamRecordingProfileConstants.PATH, 
                    media.getString(MediaStreamRecordingProfileConstants.PARAM_PATH));
            assertEquals(TestMediaStreamRecordingProfileConstants.MIME_TYPE, 
                    media.getString(MediaStreamRecordingProfileConstants.PARAM_MIME_TYPE));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 動画撮影または音声録音開始イベントのコールバック解除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /mediastream_recording/onrecordingchange?deviceid=xxxx&session_key=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testOnRecording02() {
        unregisterEventCallback(MediaStreamRecordingProfileConstants.ATTRIBUTE_ON_RECORDING_CHANGE);
    }

    /**
     * 動画撮影または音声録音の一定時間経過イベントのコールバック登録テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /mediastream_recording/ondeviceavailable?deviceid=xxxx&session_key=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・コールバック登録後にイベントを受信すること。
     * </pre>
     */
    public void testOnDataAvailable01() {
        try {
            JSONObject event = registerEventCallback(MediaStreamRecordingProfileConstants.ATTRIBUTE_ON_DATA_AVAILABLE);
            JSONObject media = event.getJSONObject(MediaStreamRecordingProfileConstants.PARAM_MEDIA);
            assertEquals(getFileURI(TestMediaStreamRecordingProfileConstants.URI), 
                    media.getString(MediaStreamRecordingProfileConstants.PARAM_URI));
            assertEquals(TestMediaStreamRecordingProfileConstants.MIME_TYPE, 
                    media.getString(MediaStreamRecordingProfileConstants.PARAM_MIME_TYPE));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 動画撮影または音声録音の一定時間経過イベントのコールバック解除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /mediastream_recording/ondeviceavailable?deviceid=xxxx&session_key=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testOnDataAvailable02() {
        unregisterEventCallback(MediaStreamRecordingProfileConstants.ATTRIBUTE_ON_DATA_AVAILABLE);
    }

    /**
     * コールバック登録リクエストを送信する.
     * @param attribute コールバックの属性名
     * @return 受信したイベント
     * @throws JSONException JSONの解析に失敗した場合
     */
    private JSONObject registerEventCallback(final String attribute) throws JSONException {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + MediaStreamRecordingProfileConstants.PROFILE_NAME);
        builder.append("/" + attribute);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(DConnectProfileConstants.PARAM_SESSION_KEY + "=" + getClientId());
        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        HttpUriRequest request = new HttpPut(builder.toString());
        JSONObject root = sendRequest(request);
        assertResultOK(root);
        JSONObject event = waitForEvent();
        assertNotNull("event is null.", event);
        return event;
    }

    /**
     * コールバック解除リクエストを送信する.
     * @param attribute コールバックの属性名
     */
    private void unregisterEventCallback(final String attribute) {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + MediaStreamRecordingProfileConstants.PROFILE_NAME);
        builder.append("/" + attribute);
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

    /**
     * メディアIDで指定したファイルのURIを取得する.
     * @param mediaId メディアID
     * @return ファイルのURI
     */
    private String getFileURI(final String mediaId) {
        return DCONNECT_MANAGER_URI + "/files?uri=" + mediaId;
    }
}
