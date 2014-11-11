/*
 NormalFileProfileTestCase.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.profile.restful.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.Assert;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.mime.MultipartEntity;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.profile.AuthorizationProfileConstants;
import org.deviceconnect.profile.DConnectProfileConstants;
import org.deviceconnect.profile.FileProfileConstants;
import org.deviceconnect.utils.URIBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.AssetManager;

/**
 * Fileプロファイルの正常系テスト.
 * @author NTT DOCOMO, INC.
 */
public class NormalFileProfileTestCase extends RESTfulDConnectTestCase {
    /** バッファサイズを定義. */
    private static final int BUF_SIZE = 4096;

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
     * 【HTTP通信】
     * Method: GET
     * Path: /file/list?deviceid=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testGetList001() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_LIST);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultOK(root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * ファイル一覧取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /file/list?deviceid=xxxx&mimeType=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・
     * </pre>
     */
    public void testGetList002() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_LIST);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(FileProfileConstants.PARAM_MIME_TYPE, "text/plain");
        builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultOK(root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * ファイル受信テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /file/receive?deviceid=xxxx&mediaid=xxxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testGetReceive() {
        testSend();
        final String name = "test.png";
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_RECEIVE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(FileProfileConstants.PARAM_PATH, name);
        builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultOK(root);

            String uri = root.getString(FileProfileConstants.PARAM_URI);
            Assert.assertNotNull("uri is null.", uri);
            uri += "&" + AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken();
            byte[] data = getBytesFromHttp(uri);
            byte[] orig = getBytesFromAssets(name);
            Assert.assertNotNull("data is invalid.", data);
            Assert.assertEquals("data is invalid", orig.length, data.length);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * ファイルの送信を行う.
     * <pre>
     * Method: POST
     * Path: /file/send?deviceid=xxxx&filename=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testSend() {
        final String name = "test.png";
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_SEND);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());
        builder.addParameter(FileProfileConstants.PARAM_PATH, "/test/test.png");
        builder.addParameter(FileProfileConstants.PARAM_FILE_TYPE,
                String.valueOf(FileProfileConstants.FileType.FILE.getValue()));

        AssetManager manager = getApplicationContext().getAssets();
        InputStream in = null;
        try {
            MultipartEntity entity = new MultipartEntity();
            in = manager.open(name);
            // ファイルのデータを読み込む
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int len;
            byte[] buf = new byte[BUF_SIZE];
            while ((len = in.read(buf)) > 0) {
                baos.write(buf, 0, len);
            }
            // ボディにデータを追加
            entity.addPart(FileProfileConstants.PARAM_DATA, new BinaryBody(baos.toByteArray(), name));

            HttpPost request = new HttpPost(builder.toString());
            request.setEntity(entity);

            JSONObject root = sendRequest(request);
            assertResultOK(root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        } catch (IOException e) {
            fail("IOException in JSONObject." + e.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * ファイルの削除を行う.
     * <pre>
     * Method: Delete
     * Path: /file/remove?deviceid=xxxx&filename=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testRemove() {
        final String name = "test.png";
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_REMOVE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(FileProfileConstants.PARAM_PATH, name);
        builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());

        try {
            HttpDelete request = new HttpDelete(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultOK(root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * ディレクトリの作成および削除を行う.
     * <pre>
     * Method: DELETE
     * Path: /file/rmdir?deviceid=xxxx&path=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testMkdirRmdir01() {
        final String name = "test";
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_MKDIR);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(FileProfileConstants.PARAM_PATH, name);
        builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());

        try {
            HttpPost request = new HttpPost(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultOK(root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }

        builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_RMDIR);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(FileProfileConstants.PARAM_PATH, name);
        builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());

        try {
            HttpDelete request = new HttpDelete(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultOK(root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * ディレクトリの作成および削除を行う.
     * <pre>
     * Method: DELETE
     * Path: /file/rmdir?deviceid=xxxx&path=xxxx&force=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testMkdirRmdir02() {
        final String name = "test";
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_MKDIR);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(FileProfileConstants.PARAM_PATH, name);
        builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());

        try {
            HttpPost request = new HttpPost(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultOK(root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }

        builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_RMDIR);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(FileProfileConstants.PARAM_PATH, name);
        builder.addParameter(FileProfileConstants.PARAM_FORCE, "true");
        builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());

        try {
            HttpDelete request = new HttpDelete(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultOK(root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }
}
