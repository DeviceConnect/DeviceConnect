/*
 NormalFileDescriptorProfileTestCase.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.profile.intent.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;

import com.nttdocomo.android.dconnect.test.plugin.profile.TestFileDescriptorProfileConstants;
import com.nttdocomo.dconnect.message.DConnectMessage;
import com.nttdocomo.dconnect.message.intent.message.IntentDConnectMessage;
import com.nttdocomo.dconnect.profile.FileDescriptorProfileConstants;


/**
 * FileDescriptorプロファイルの正常系テスト.
 * @author NTT DOCOMO, INC.
 */
public class NormalFileDescriptorProfileTestCase extends IntentDConnectTestCase {

	/**
     * バッファサイズ.
     */
    private static final int BUF_SIZE = 1024;

    /**
     * ファイルの長さ.
     */
    private static final long FILE_LENGTH = 256;

    /**
     * コンストラクタ.
     * 
     * @param tag テストタグ
     */
    public NormalFileDescriptorProfileTestCase(final String tag) {
        super(tag);
    }

    /**
     * ファイルをオープンするテストを行う.
     * <pre>
     * 【Intent通信】
     * Action: GET
     * Extra:
     *     requestCode=xxxx
     *     deviceId=xxxx
     *     profile=file_descriptor
     *     attribute=open
     *     mediaId=xxxx
     *     flag=xxxx
     *     mode=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・requestCodeにリクエストコードが返ってくること。
     * </pre>
     */
    public void testOpen() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_GET);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, FileDescriptorProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, FileDescriptorProfileConstants.ATTRIBUTE_OPEN);
        request.putExtra(FileDescriptorProfileConstants.PARAM_PATH, TestFileDescriptorProfileConstants.PATH);
        request.putExtra(FileDescriptorProfileConstants.PARAM_FLAG, "r");
        Intent response = sendRequest(request);
        assertResultOK(response);
    }

    /**
     * ファイルをクローズするテストを行う.
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Extra:
     *     requestCode=xxxx
     *     deviceId=xxxx
     *     profile=file_descriptor
     *     attribute=close
     *     mediaId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・requestCodeにリクエストコードが返ってくること。
     * </pre>
     */
    public void testClose() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, FileDescriptorProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, FileDescriptorProfileConstants.ATTRIBUTE_CLOSE);
        request.putExtra(FileDescriptorProfileConstants.PARAM_PATH, TestFileDescriptorProfileConstants.PATH);
        Intent response = sendRequest(request);
        assertResultOK(response);
    }

    /**
     * 指定したサイズ分のデータをファイルから読み込むテストを行う.
     * <pre>
     * 【Intent通信】
     * Action: GET
     * Extra:
     *     requestCode=xxxx
     *     deviceId=xxxx
     *     profile=file_descriptor
     *     attribute=read
     *     mediaId=xxxx
     *     length=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・requestCodeにリクエストコードが返ってくること。
     * ・sizeにint型の値が返ってくること。
     * ・fileDataにString型のデータが返ってくること。
     * </pre>
     */
    public void testRead001() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_GET);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, FileDescriptorProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, FileDescriptorProfileConstants.ATTRIBUTE_READ);
        request.putExtra(FileDescriptorProfileConstants.PARAM_PATH, TestFileDescriptorProfileConstants.PATH);
        request.putExtra(FileDescriptorProfileConstants.PARAM_LENGTH, FILE_LENGTH);
        Intent response = sendRequest(request);
        assertResultOK(response);
        assertTrue(response.hasExtra(FileDescriptorProfileConstants.PARAM_SIZE));
        assertEquals(TestFileDescriptorProfileConstants.BYTE, 
                response.getIntExtra(FileDescriptorProfileConstants.PARAM_SIZE, -1));
        assertTrue(response.hasExtra(FileDescriptorProfileConstants.PARAM_FILE_DATA));
    }

    /**
     * 指定した位置から、指定したサイズ分のデータをファイルから読み込むテストを行う.
     * <pre>
     * 【Intent通信】
     * Action: GET
     * Extra:
     *     requestCode=xxxx
     *     deviceId=xxxx
     *     profile=file_descriptor
     *     attribute=read
     *     mediaId=xxxx
     *     length=xxxx
     *     position=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・requestCodeにリクエストコードが返ってくること。
     * ・sizeにint型のデータが返ってくること。
     * ・fileDataにString型のデータが返ってくること。
     * </pre>
     */
    public void testRead002() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_GET);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, FileDescriptorProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, FileDescriptorProfileConstants.ATTRIBUTE_READ);
        request.putExtra(FileDescriptorProfileConstants.PARAM_PATH, TestFileDescriptorProfileConstants.PATH);
        request.putExtra(FileDescriptorProfileConstants.PARAM_LENGTH, FILE_LENGTH);
        request.putExtra(FileDescriptorProfileConstants.PARAM_POSITION, 0L);
        Intent response = sendRequest(request);

        assertResultOK(response);
        assertTrue(response.hasExtra(FileDescriptorProfileConstants.PARAM_SIZE));
        assertEquals(TestFileDescriptorProfileConstants.BYTE, 
                response.getIntExtra(FileDescriptorProfileConstants.PARAM_SIZE, -1));
        assertTrue(response.hasExtra(FileDescriptorProfileConstants.PARAM_FILE_DATA));
    }

    /**
     * ファイルにデータを書き込むテストを行う.
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Extra:
     *     requestCode=xxxx
     *     deviceId=xxxx
     *     profile=file_descriptor
     *     attribute=write
     *     mediaId=xxxx
     *     uri=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・requestCodeにリクエストコードが返ってくること。
     * </pre>
     * @throws IOException テストファイルの保存に失敗した場合
     */
    public void testWrite001() throws IOException {
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, FileDescriptorProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, FileDescriptorProfileConstants.ATTRIBUTE_WRITE);
        request.putExtra(FileDescriptorProfileConstants.PARAM_PATH, TestFileDescriptorProfileConstants.PATH);
        String uri = saveAssetFile("test.png");
        request.putExtra(FileDescriptorProfileConstants.PARAM_URI, uri);
        Intent response = sendRequest(request);
        assertResultOK(response);
    }

    /**
     * ファイル内の指定した位置にデータを書き込むテストを行う.
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Extra:
     *     requestCode=xxxx
     *     deviceId=xxxx
     *     profile=file_descriptor
     *     attribute=write
     *     mediaId=xxxx
     *     uri=xxxx
     *     position=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・requestCodeにリクエストコードが返ってくること。
     * </pre>
     * @throws IOException テストファイルの保存に失敗した場合
     */
    public void testWrite002() throws IOException {
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, FileDescriptorProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, FileDescriptorProfileConstants.ATTRIBUTE_WRITE);
        request.putExtra(FileDescriptorProfileConstants.PARAM_PATH, TestFileDescriptorProfileConstants.PATH);
        String uri = saveAssetFile("test.png");
        request.putExtra(FileDescriptorProfileConstants.PARAM_URI, uri);
        request.putExtra(FileDescriptorProfileConstants.PARAM_POSITION, 0);
        Intent response = sendRequest(request);
        assertResultOK(response);
    }

    /**
     * ファイルの更新通知のコールバック登録テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: PUT
     * Extra:
     *     requestCode=xxxx
     *     deviceId=xxxx
     *     profile=file_descriptor
     *     callback=onwatchfile
     *     sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・requestCodeにリクエストコードが返ってくること。
     * ・コールバック登録後にイベントを受信すること。
     * </pre>
     */
    public void testWatchFile01() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, FileDescriptorProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, FileDescriptorProfileConstants.ATTRIBUTE_ON_WATCH_FILE);
        request.putExtra(DConnectMessage.EXTRA_SESSION_KEY, TEST_SESSION_KEY);
        Intent response = sendRequest(request);
        assertResultOK(response);
        Intent event = waitForEvent();
        assertTrue(event.hasExtra(FileDescriptorProfileConstants.PARAM_FILE));
        Bundle file = event.getBundleExtra(FileDescriptorProfileConstants.PARAM_FILE);
        assertEquals(TestFileDescriptorProfileConstants.CURR, 
                file.getString(FileDescriptorProfileConstants.PARAM_CURR));
        assertEquals(TestFileDescriptorProfileConstants.PREV, 
                file.getString(FileDescriptorProfileConstants.PARAM_PREV));
    }

    /**
     * ファイルの更新通知のコールバック解除テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: DELETE
     * Extra:
     *     requestCode=xxxx
     *     deviceId=xxxx
     *     profile=file_descriptor
     *     callback=onwatchfile
     *     sessionKey=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・requestCodeにリクエストコードが返ってくること。
     * </pre>
     */
    public void testWatchFile02() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_DELETE);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, FileDescriptorProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, FileDescriptorProfileConstants.ATTRIBUTE_ON_WATCH_FILE);
        request.putExtra(DConnectMessage.EXTRA_SESSION_KEY, TEST_SESSION_KEY);
        Intent response = sendRequest(request);
        assertResultOK(response);
    }

    /**
     * assetsフォルダ内のファイルをアプリ領域に保存する.
     * 
     * @param name assetファイル名
     * @throws IOException ファイルの保存に失敗した場合
     * @return URIを示す文字列
     */
    private String saveAssetFile(final String name) throws IOException {
        AssetManager manager = getApplicationContext().getAssets();
        InputStream is = manager.open(name);
        File file = new File(getContext().getFilesDir(), name);
        if (!file.exists()) {
            if (!file.createNewFile()) {
                fail("Failed to create file: " + name);
            }
        }
        OutputStream os = new FileOutputStream(file);
        int len;
        byte[] buf = new byte[BUF_SIZE];
        while ((len = is.read(buf)) > 0) {
            os.write(buf, 0, len);
            os.flush();
        }
        os.close();
        is.close();
        return "content://com.nttdocomo.android.dconnect.test.file/" + name;
    }
}
