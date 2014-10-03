/*
 NormalMediaStreamRecordingProfileTestCase.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.profile.intent.test;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import com.nttdocomo.android.dconnect.test.plugin.profile.TestMediaStreamRecordingProfileConstants;
import com.nttdocomo.dconnect.message.DConnectMessage;
import com.nttdocomo.dconnect.message.intent.message.IntentDConnectMessage;
import com.nttdocomo.dconnect.profile.MediaStreamRecordingProfileConstants;


/**
 * MediaStreamRecordingプロファイルの正常系テスト.
 * @author NTT DOCOMO, INC.
 */
public class NormalMediaStreamRecordingProfileTestCase extends IntentDConnectTestCase
    implements TestMediaStreamRecordingProfileConstants {

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
     * 【Intent通信】
     * Action: GET
     * Extra:
     *     profile=mediastream_recording
     *     attribute=mediarecorder
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
        Intent request = new Intent(IntentDConnectMessage.ACTION_GET);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, MediaStreamRecordingProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, MediaStreamRecordingProfileConstants.ATTRIBUTE_MEDIARECORDER);
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
        Parcelable[] recorders =
                (Parcelable[]) response.getParcelableArrayExtra(MediaStreamRecordingProfileConstants.PARAM_RECORDERS);
        Bundle recorder = (Bundle) recorders[0];
        assertEquals(TestMediaStreamRecordingProfileConstants.ID,
                recorder.getString(MediaStreamRecordingProfileConstants.PARAM_ID));
        assertEquals(TestMediaStreamRecordingProfileConstants.NAME,
                recorder.getString(MediaStreamRecordingProfileConstants.PARAM_NAME));
        assertEquals(TestMediaStreamRecordingProfileConstants.STATE,
                recorder.getString(MediaStreamRecordingProfileConstants.PARAM_STATE));
        assertEquals(TestMediaStreamRecordingProfileConstants.IMAGE_WIDTH,
                recorder.getInt(MediaStreamRecordingProfileConstants.PARAM_IMAGE_WIDTH));
        assertEquals(TestMediaStreamRecordingProfileConstants.IMAGE_HEIGHT,
                recorder.getInt(MediaStreamRecordingProfileConstants.PARAM_IMAGE_HEIGHT));
        assertEquals(TestMediaStreamRecordingProfileConstants.CONFIG,
                recorder.getString(MediaStreamRecordingProfileConstants.PARAM_CONFIG));
    }

    /**
     * 指定したスマートデバイスに対して写真撮影依頼を送信するテストを行う.
     * <pre>
     * 【Intent通信】
     * Action: POST
     * Extra:
     *     profile=mediastream_recording
     *     attribute=takephoto
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・uriが"test.mp4"であること。
     * </pre>
     */
    public void testTakePhoto001() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_POST);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, MediaStreamRecordingProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, MediaStreamRecordingProfileConstants.ATTRIBUTE_TAKE_PHOTO);
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
        assertEquals(URI,
                response.getStringExtra(MediaStreamRecordingProfileConstants.PARAM_URI));
    }

    /**
     * 指定したスマートデバイスに対して写真撮影依頼を送信するテストを行う.
     * <pre>
     * 【Intent通信】
     * Action: POST
     * Extra:
     *     profile=mediastream_recording
     *     attribute=mediarecorder
     *     target=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・uriが"test.mp4"であること。
     * </pre>
     */
    public void testTakePhoto002() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_POST);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, MediaStreamRecordingProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, MediaStreamRecordingProfileConstants.ATTRIBUTE_TAKE_PHOTO);
        request.putExtra(MediaStreamRecordingProfileConstants.PARAM_TARGET,
                TestMediaStreamRecordingProfileConstants.ID);
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
        assertEquals(URI,
                response.getStringExtra(MediaStreamRecordingProfileConstants.PARAM_URI));
    }

    /**
     * 指定したスマートデバイスに対して動画撮影または音声録音の開始依頼を送信するテストを行う.
     * <pre>
     * 【Intent通信】
     * Action: POST
     * Extra:
     *     profile=mediastream_recording
     *     attribute=record
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・uriが"test.mp4"であること。
     * </pre>
     */
    public void testRecord001() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_POST);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, MediaStreamRecordingProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, MediaStreamRecordingProfileConstants.ATTRIBUTE_RECORD);
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
        assertEquals(URI,
                response.getStringExtra(MediaStreamRecordingProfileConstants.PARAM_URI));
    }

    /**
     * 指定したスマートデバイスに対して動画撮影または音声録音の開始依頼を送信するテストを行う.
     * <pre>
     * 【Intent通信】
     * Action: POST
     * Extra:
     *     profile=mediastream_recording
     *     attribute=record
     *     target=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・uriが"test.mp4"であること。
     * </pre>
     */
    public void testRecord002() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_POST);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, MediaStreamRecordingProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, MediaStreamRecordingProfileConstants.ATTRIBUTE_RECORD);
        request.putExtra(MediaStreamRecordingProfileConstants.PARAM_TARGET,
                TestMediaStreamRecordingProfileConstants.ID);
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
        assertEquals(URI,
                response.getStringExtra(MediaStreamRecordingProfileConstants.PARAM_URI));
    }

    /**
     * 指定したスマートデバイスに対して動画撮影または音声録音の開始依頼を送信するテストを行う.
     * <pre>
     * 【Intent通信】
     * Action: POST
     * Extra:
     *     profile=mediastream_recording
     *     attribute=record
     *     timeslice=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・uriが"test.mp4"であること。
     * </pre>
     */
    public void testRecord003() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_POST);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, MediaStreamRecordingProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, MediaStreamRecordingProfileConstants.ATTRIBUTE_RECORD);
        request.putExtra(MediaStreamRecordingProfileConstants.PARAM_TIME_SLICE, TIME_SLICE);
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
        assertEquals(URI,
                response.getStringExtra(MediaStreamRecordingProfileConstants.PARAM_URI));
    }

    /**
     * 指定したスマートデバイスに対して動画撮影または音声録音の開始依頼を送信するテストを行う.
     * <pre>
     * 【Intent通信】
     * Action: POST
     * Extra:
     *     profile=mediastream_recording
     *     attribute=record
     *     target=xxxx
     *     timeslice=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・uriが"test.mp4"であること。
     * </pre>
     */
    public void testRecord004() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_POST);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, MediaStreamRecordingProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, MediaStreamRecordingProfileConstants.ATTRIBUTE_RECORD);
        request.putExtra(MediaStreamRecordingProfileConstants.PARAM_TARGET,
                TestMediaStreamRecordingProfileConstants.ID);
        request.putExtra(MediaStreamRecordingProfileConstants.PARAM_TIME_SLICE, TIME_SLICE);
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
        assertEquals(URI,
                response.getStringExtra(MediaStreamRecordingProfileConstants.PARAM_URI));
    }

    /**
     * 指定したスマートデバイスに対して動画撮影または音声録音の一時停止依頼を送信するテストを行う.
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Extra:
     *     profile=mediastream_recording
     *     attribute=pause
     *     mediaId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testPause001() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, MediaStreamRecordingProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, MediaStreamRecordingProfileConstants.ATTRIBUTE_PAUSE);
        request.putExtra(MediaStreamRecordingProfileConstants.PARAM_PATH,
                TestMediaStreamRecordingProfileConstants.PATH);
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
    }

    /**
     * 指定したスマートデバイスに対して動画撮影または音声録音の一時停止依頼を送信するテストを行う.
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Extra:
     *     profile=mediastream_recording
     *     attribute=pause
     *     mediaId=xxxx
     *     target=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testPause002() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, MediaStreamRecordingProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, MediaStreamRecordingProfileConstants.ATTRIBUTE_PAUSE);
        request.putExtra(MediaStreamRecordingProfileConstants.PARAM_PATH,
                TestMediaStreamRecordingProfileConstants.PATH);
        request.putExtra(MediaStreamRecordingProfileConstants.PARAM_TARGET,
                TestMediaStreamRecordingProfileConstants.ID);
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
    }

    /**
     * 指定したスマートデバイスに対して動画撮影または音声録音の再開依頼を送信するテストを行う.
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Extra:
     *     profile=mediastream_recording
     *     attribute=resume
     *     mediaId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testResume001() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, MediaStreamRecordingProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, MediaStreamRecordingProfileConstants.ATTRIBUTE_RESUME);
        request.putExtra(MediaStreamRecordingProfileConstants.PARAM_PATH,
                TestMediaStreamRecordingProfileConstants.PATH);
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
    }

    /**
     * 指定したスマートデバイスに対して動画撮影または音声録音の再開依頼を送信するテストを行う.
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Extra:
     *     profile=mediastream_recording
     *     attribute=resume
     *     mediaId=xxxx
     *     target=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testResume002() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, MediaStreamRecordingProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, MediaStreamRecordingProfileConstants.ATTRIBUTE_RESUME);
        request.putExtra(MediaStreamRecordingProfileConstants.PARAM_PATH,
                TestMediaStreamRecordingProfileConstants.PATH);
        request.putExtra(MediaStreamRecordingProfileConstants.PARAM_TARGET,
                TestMediaStreamRecordingProfileConstants.ID);
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
    }

    /**
     * 指定したスマートデバイスに対して動画撮影または音声録音の停止依頼を送信するテストを行う.
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Extra:
     *     profile=mediastream_recording
     *     attribute=stop
     *     mediaId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testStop001() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, MediaStreamRecordingProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, MediaStreamRecordingProfileConstants.ATTRIBUTE_STOP);
        request.putExtra(MediaStreamRecordingProfileConstants.PARAM_PATH,
                TestMediaStreamRecordingProfileConstants.PATH);
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
    }

    /**
     * 指定したスマートデバイスに対して動画撮影または音声録音の停止依頼を送信するテストを行う.
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Extra:
     *     profile=mediastream_recording
     *     attribute=stop
     *     mediaId=xxxx
     *     target=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testStop002() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, MediaStreamRecordingProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, MediaStreamRecordingProfileConstants.ATTRIBUTE_STOP);
        request.putExtra(MediaStreamRecordingProfileConstants.PARAM_PATH,
                TestMediaStreamRecordingProfileConstants.PATH);
        request.putExtra(MediaStreamRecordingProfileConstants.PARAM_TARGET,
                TestMediaStreamRecordingProfileConstants.ID);
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
    }

    /**
     * 指定したスマートデバイスに対して動画撮影または音声録音のミュート依頼を送信するテストを行う.
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Extra:
     *     profile=mediastream_recording
     *     attribute=mutetrack
     *     mediaId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testMuteTrack001() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, MediaStreamRecordingProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, MediaStreamRecordingProfileConstants.ATTRIBUTE_MUTETRACK);
        request.putExtra(MediaStreamRecordingProfileConstants.PARAM_PATH,
                TestMediaStreamRecordingProfileConstants.PATH);
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
    }

    /**
     * 指定したスマートデバイスに対して動画撮影または音声録音のミュート依頼を送信するテストを行う.
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Extra:
     *     profile=mediastream_recording
     *     attribute=mutetrack
     *     mediaId=xxxx
     *     target=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testMuteTrack002() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, MediaStreamRecordingProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, MediaStreamRecordingProfileConstants.ATTRIBUTE_MUTETRACK);
        request.putExtra(MediaStreamRecordingProfileConstants.PARAM_PATH,
                TestMediaStreamRecordingProfileConstants.PATH);
        request.putExtra(MediaStreamRecordingProfileConstants.PARAM_TARGET,
                TestMediaStreamRecordingProfileConstants.ID);
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
    }

    /**
     * 指定したスマートデバイスに対して動画撮影または音声録音のミュート解除依頼を送信するテストを行う.
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Extra:
     *     profile=mediastream_recording
     *     attribute=unmutetrack
     *     mediaId=xxxx
     *     target=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testUnmuteTrack001() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, MediaStreamRecordingProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, MediaStreamRecordingProfileConstants.ATTRIBUTE_UNMUTETRACK);
        request.putExtra(MediaStreamRecordingProfileConstants.PARAM_PATH,
                TestMediaStreamRecordingProfileConstants.PATH);
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
    }

    /**
     * 指定したスマートデバイスに対して動画撮影または音声録音のミュート解除依頼を送信するテストを行う.
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Extra:
     *     profile=mediastream_recording
     *     attribute=unmutetrack
     *     mediaId=xxxx
     *     target=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testUnmuteTrack002() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, MediaStreamRecordingProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, MediaStreamRecordingProfileConstants.ATTRIBUTE_UNMUTETRACK);
        request.putExtra(MediaStreamRecordingProfileConstants.PARAM_PATH,
                TestMediaStreamRecordingProfileConstants.PATH);
        request.putExtra(MediaStreamRecordingProfileConstants.PARAM_TARGET,
                TestMediaStreamRecordingProfileConstants.ID);
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
    }

    /**
     * 指定したスマートデバイスのカメラのサポートするオプションの一覧を取得するテストを行う.
     * <pre>
     * 【Intent通信】
     * Action: GET
     * Extra:
     *     profile=mediastream_recording
     *     attribute=options
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
        Intent request = new Intent(IntentDConnectMessage.ACTION_GET);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, MediaStreamRecordingProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, MediaStreamRecordingProfileConstants.ATTRIBUTE_OPTIONS);
        Intent response = sendRequest(request);

        assertResultOK(response);
        Bundle imageWidth = response.getBundleExtra(MediaStreamRecordingProfileConstants.PARAM_IMAGE_WIDTH);
        assertEquals(0, imageWidth.getInt(MediaStreamRecordingProfileConstants.PARAM_MAX));
        assertEquals(0, imageWidth.getInt(MediaStreamRecordingProfileConstants.PARAM_MIN));
        Bundle imageHeight = response.getBundleExtra(MediaStreamRecordingProfileConstants.PARAM_IMAGE_HEIGHT);
        assertEquals(0, imageHeight.getInt(MediaStreamRecordingProfileConstants.PARAM_MAX));
        assertEquals(0, imageHeight.getInt(MediaStreamRecordingProfileConstants.PARAM_MIN));
        assertEquals(TestMediaStreamRecordingProfileConstants.MIME_TYPE,
                response.getStringArrayExtra(MediaStreamRecordingProfileConstants.PARAM_MIME_TYPE)[0]);
    }

    /**
     * 指定したスマートデバイスのカメラのサポートするオプションの一覧を取得するテストを行う.
     * <pre>
     * 【Intent通信】
     * Action: GET
     * Extra:
     *     profile=mediastream_recording
     *     attribute=options
     *     target=xxxx
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
        Intent request = new Intent(IntentDConnectMessage.ACTION_GET);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, MediaStreamRecordingProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, MediaStreamRecordingProfileConstants.ATTRIBUTE_OPTIONS);
        request.putExtra(MediaStreamRecordingProfileConstants.PARAM_TARGET,
                TestMediaStreamRecordingProfileConstants.ID);
        Intent response = sendRequest(request);

        assertResultOK(response);
        Bundle imageWidth = response.getBundleExtra(MediaStreamRecordingProfileConstants.PARAM_IMAGE_WIDTH);
        assertEquals(0, imageWidth.getInt(MediaStreamRecordingProfileConstants.PARAM_MAX));
        assertEquals(0, imageWidth.getInt(MediaStreamRecordingProfileConstants.PARAM_MIN));
        Bundle imageHeight = response.getBundleExtra(MediaStreamRecordingProfileConstants.PARAM_IMAGE_HEIGHT);
        assertEquals(0, imageHeight.getInt(MediaStreamRecordingProfileConstants.PARAM_MAX));
        assertEquals(0, imageHeight.getInt(MediaStreamRecordingProfileConstants.PARAM_MIN));
        assertEquals(TestMediaStreamRecordingProfileConstants.MIME_TYPE,
                response.getStringArrayExtra(MediaStreamRecordingProfileConstants.PARAM_MIME_TYPE)[0]);
    }

    /**
     * 指定したスマートデバイスのカメラのオプションを設定するテストを行う.
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Extra:
     *     profile=mediastream_recording
     *     attribute=options
     *     imageWidth=xxxx
     *     imageHeight=xxxx
     *     mimeType=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testPutOptions001() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, MediaStreamRecordingProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, MediaStreamRecordingProfileConstants.ATTRIBUTE_OPTIONS);
        request.putExtra(MediaStreamRecordingProfileConstants.PARAM_TARGET,
                TestMediaStreamRecordingProfileConstants.ID);
        request.putExtra(MediaStreamRecordingProfileConstants.PARAM_IMAGE_WIDTH,
                TestMediaStreamRecordingProfileConstants.IMAGE_WIDTH);
        request.putExtra(MediaStreamRecordingProfileConstants.PARAM_IMAGE_HEIGHT,
                TestMediaStreamRecordingProfileConstants.IMAGE_HEIGHT);
        request.putExtra(MediaStreamRecordingProfileConstants.PARAM_MIME_TYPE,
                TestMediaStreamRecordingProfileConstants.MIME_TYPE);
        Intent response = sendRequest(request);

        assertResultOK(response);
    }

    /**
     * 指定したスマートデバイスのカメラのオプションを設定するテストを行う.
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Extra:
     *     profile=mediastream_recording
     *     attribute=options
     *     target=xxxx
     *     imageWidth=xxxx
     *     imageHeight=xxxx
     *     mimeType=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testPutOptions002() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, MediaStreamRecordingProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, MediaStreamRecordingProfileConstants.ATTRIBUTE_OPTIONS);
        request.putExtra(MediaStreamRecordingProfileConstants.PARAM_TARGET,
                TestMediaStreamRecordingProfileConstants.ID);
        request.putExtra(MediaStreamRecordingProfileConstants.PARAM_IMAGE_WIDTH,
                TestMediaStreamRecordingProfileConstants.IMAGE_WIDTH);
        request.putExtra(MediaStreamRecordingProfileConstants.PARAM_IMAGE_HEIGHT,
                TestMediaStreamRecordingProfileConstants.IMAGE_HEIGHT);
        request.putExtra(MediaStreamRecordingProfileConstants.PARAM_MIME_TYPE,
                TestMediaStreamRecordingProfileConstants.MIME_TYPE);
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
    }

    /**
     * 写真撮影イベントのコールバック登録テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Extra:
     *     profile=mediastream_recording
     *     attribute=onphoto
     *     sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・コールバック登録後にイベントを受信すること。
     * </pre>
     */
    public void testOnPhoto01() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, MediaStreamRecordingProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, MediaStreamRecordingProfileConstants.ATTRIBUTE_ON_PHOTO);
        request.putExtra(DConnectMessage.EXTRA_SESSION_KEY, TEST_SESSION_KEY);
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));

        Intent event = waitForEvent();
        assertNotNull(event);
        Bundle obj = event.getBundleExtra(MediaStreamRecordingProfileConstants.PARAM_PHOTO);
        assertEquals(PATH, 
                obj.getString(MediaStreamRecordingProfileConstants.PARAM_PATH));
        assertEquals(TestMediaStreamRecordingProfileConstants.MIME_TYPE, 
                obj.getString(MediaStreamRecordingProfileConstants.PARAM_MIME_TYPE));
    }

    /**
     * 写真撮影イベントのコールバック解除テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: DELETE
     * Extra:
     *     profile=mediastream_recording
     *     attribute=onphoto
     *     sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testOnPhoto02() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_DELETE);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, MediaStreamRecordingProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, MediaStreamRecordingProfileConstants.ATTRIBUTE_ON_PHOTO);
        request.putExtra(DConnectMessage.EXTRA_SESSION_KEY, TEST_SESSION_KEY);
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
    }

    /**
     * 動画撮影または音声録音開始イベントのコールバック登録テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Extra:
     *     profile=mediastream_recording
     *     attribute=onrecordingchange
     *     sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・コールバック登録後にイベントを受信すること。
     * </pre>
     */
    public void testOnRecordingChange01() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, MediaStreamRecordingProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE,
                MediaStreamRecordingProfileConstants.ATTRIBUTE_ON_RECORDING_CHANGE);
        request.putExtra(DConnectMessage.EXTRA_SESSION_KEY, TEST_SESSION_KEY);
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));

        Intent event = waitForEvent();
        assertNotNull(event);
        Bundle obj = event.getBundleExtra(MediaStreamRecordingProfileConstants.PARAM_MEDIA);
        assertEquals(PATH, 
                obj.getString(MediaStreamRecordingProfileConstants.PARAM_PATH));
        assertEquals(TestMediaStreamRecordingProfileConstants.MIME_TYPE, 
                obj.getString(MediaStreamRecordingProfileConstants.PARAM_MIME_TYPE));
    }

    /**
     * 動画撮影または音声録音開始イベントのコールバック解除テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: DELETE
     * Extra:
     *     profile=mediastream_recording
     *     attribute=onrecording
     *     sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testOnRecordingChange02() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_DELETE);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, MediaStreamRecordingProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE,
                MediaStreamRecordingProfileConstants.ATTRIBUTE_ON_RECORDING_CHANGE);
        request.putExtra(DConnectMessage.EXTRA_SESSION_KEY, TEST_SESSION_KEY);
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
    }

    /**
     * 動画撮影または音声録音の一定時間経過イベントのコールバック登録テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Extra:
     *     profile=mediastream_recording
     *     attribute=ondeviceavailable
     *     sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・コールバック登録後にイベントを受信すること。
     * </pre>
     */
    public void testOnDataAvailable01() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, MediaStreamRecordingProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE,
                MediaStreamRecordingProfileConstants.ATTRIBUTE_ON_DATA_AVAILABLE);
        request.putExtra(DConnectMessage.EXTRA_SESSION_KEY, TEST_SESSION_KEY);
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));

        Intent event = waitForEvent();
        assertNotNull(event);
        Bundle obj = event.getBundleExtra(MediaStreamRecordingProfileConstants.PARAM_MEDIA);
        assertEquals(URI, 
                obj.getString(MediaStreamRecordingProfileConstants.PARAM_URI));
        assertEquals(TestMediaStreamRecordingProfileConstants.MIME_TYPE, 
                obj.getString(MediaStreamRecordingProfileConstants.PARAM_MIME_TYPE));
    }

    /**
     * 動画撮影または音声録音の一定時間経過イベントのコールバック解除テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: DELETE
     * Extra:
     *     profile=mediastream_recording
     *     attribute=ondeviceavailable
     *     sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testOnDataAvailable02() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_DELETE);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, MediaStreamRecordingProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE,
                MediaStreamRecordingProfileConstants.ATTRIBUTE_ON_DATA_AVAILABLE);
        request.putExtra(DConnectMessage.EXTRA_SESSION_KEY, TEST_SESSION_KEY);
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
    }
}
