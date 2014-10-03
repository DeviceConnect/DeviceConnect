/*
 NormalFileProfileTestCase.java
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
import android.os.Parcelable;

import com.nttdocomo.android.dconnect.test.plugin.profile.TestFileProfileConstants;
import com.nttdocomo.dconnect.message.DConnectMessage;
import com.nttdocomo.dconnect.message.intent.message.IntentDConnectMessage;
import com.nttdocomo.dconnect.profile.FileProfileConstants;
import com.nttdocomo.dconnect.profile.FileProfileConstants.FileType;


/**
 * Fileプロファイルの正常系テスト.
 * @author NTT DOCOMO, INC.
 */
public class NormalFileProfileTestCase extends IntentDConnectTestCase {

    /**
     * バッファサイズ.
     */
    private static final int BUF_SIZE = 1024;

    /**
     * コンストラクタ.
     * @param string テストタグ
     */
    public NormalFileProfileTestCase(final String string) {
        super(string);
    }

    /**
     * ファイル一覧取得テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: GET
     * Extra:
     *     profile=file
     *     attribute=list
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・fileにBundle[]型の値が返ってくること。
     * ・file[0].mediaIdにString型の値が返ってくること。
     * ・file[0].mimeTypeにString型の値が返ってくること。
     * ・file[0].fileNameにString型の値が返ってくること。
     * ・file[0].fileSizeにint型の値が返ってくること。
     * </pre>
     */
    public void testGetList001() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_GET);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, FileProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, FileProfileConstants.ATTRIBUTE_LIST);
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
        Parcelable[] files = (Parcelable[]) response.getParcelableArrayExtra(FileProfileConstants.PARAM_FILES);
        Bundle file = (Bundle) files[0];
        assertEquals(TestFileProfileConstants.PATH, file.getString(FileProfileConstants.PARAM_PATH));
        assertEquals(TestFileProfileConstants.MIME_TYPE, file.getString(FileProfileConstants.PARAM_MIME_TYPE));
        assertEquals(TestFileProfileConstants.FILE_NAME, file.getString(FileProfileConstants.PARAM_FILE_NAME));
        assertEquals(TestFileProfileConstants.BYTE, file.getInt(FileProfileConstants.PARAM_FILE_SIZE));
    }

    /**
     * ファイル一覧取得テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: GET
     * Extra:
     *     profile=file
     *     attribute=list
     *     mimeType=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・fileにBundle[]型の値が返ってくること。
     * ・file[0].mediaIdにString型の値が返ってくること。
     * ・file[0].mimeTypeにString型の値が返ってくること。
     * ・file[0].fileNameにString型の値が返ってくること。
     * ・file[0].fileSizeにint型の値が返ってくること。
     * </pre>
     */
    public void testGetList002() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_GET);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, FileProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, FileProfileConstants.ATTRIBUTE_LIST);
        request.putExtra(FileProfileConstants.PARAM_MIME_TYPE, TestFileProfileConstants.MIME_TYPE);
        Intent response = sendRequest(request);

        assertTrue(response.hasExtra(IntentDConnectMessage.EXTRA_RESULT));
        assertEquals(IntentDConnectMessage.RESULT_OK, 
                response.getIntExtra(IntentDConnectMessage.EXTRA_RESULT, -1));
        Parcelable[] files = (Parcelable[]) response.getParcelableArrayExtra(FileProfileConstants.PARAM_FILES);
        Bundle file = (Bundle) files[0];
        assertEquals(TestFileProfileConstants.PATH, file.getString(FileProfileConstants.PARAM_PATH));
        assertEquals(TestFileProfileConstants.MIME_TYPE, file.getString(FileProfileConstants.PARAM_MIME_TYPE));
        assertEquals(TestFileProfileConstants.FILE_NAME, file.getString(FileProfileConstants.PARAM_FILE_NAME));
        assertEquals(TestFileProfileConstants.BYTE, file.getInt(FileProfileConstants.PARAM_FILE_SIZE));
    }

    /**
     * ファイル受信テストを行う.
     * <pre>
     * 【Intent通信】
     * Action: GET
     * Extra:
     *     profile=file
     *     attribute=list
     *     mediaId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・mimeTypeにString型の値が返ってくること。
     * ・uriにString型の値が返ってくること。
     * </pre>
     */
    public void testGetReceive() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_GET);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, FileProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, FileProfileConstants.ATTRIBUTE_RECEIVE);
        request.putExtra(FileProfileConstants.PARAM_PATH, TestFileProfileConstants.PATH);
        Intent response = sendRequest(request);
        assertResultOK(response);
        assertNotNull(TestFileProfileConstants.MIME_TYPE, 
                response.getStringExtra(FileProfileConstants.PARAM_MIME_TYPE));
        assertNotNull(TestFileProfileConstants.URI,
                response.getStringExtra(FileProfileConstants.PARAM_URI));
    }

    /**
     * ファイルの送信を行う.
     * <pre>
     * Action: POST
     * Extra:
     *     profile=file
     *     attribute=send
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testSend() {
        final String name = "test.png";
        Intent request = new Intent(IntentDConnectMessage.ACTION_POST);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, FileProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, FileProfileConstants.ATTRIBUTE_SEND);
        request.putExtra(FileProfileConstants.PARAM_PATH, TestFileProfileConstants.PATH);
        request.putExtra(FileProfileConstants.PARAM_FILE_TYPE, FileType.FILE.getValue());
        try {
            String uri = saveAssetFile(name);
            request.putExtra(FileProfileConstants.PARAM_URI, uri);
            Intent response = sendRequest(request);
            assertResultOK(response);
        } catch (IOException e) {
            fail();
        }
    }

    /**
     * ファイルの送信を行う.
     * <pre>
     * Action: DELETE
     * Extra:
     *     profile=file
     *     attribute=remove
     *     mediaId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testRemove() {
        Intent request = new Intent(IntentDConnectMessage.ACTION_DELETE);
        request.putExtra(DConnectMessage.EXTRA_DEVICE_ID, getDeviceId());
        request.putExtra(DConnectMessage.EXTRA_PROFILE, FileProfileConstants.PROFILE_NAME);
        request.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, FileProfileConstants.ATTRIBUTE_REMOVE);
        request.putExtra(FileProfileConstants.PARAM_PATH, TestFileProfileConstants.PATH);
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
