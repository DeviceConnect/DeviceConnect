/*
 NormalFileDescriptorProfileTestCase.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.profile.restful.test;

import java.io.UnsupportedEncodingException;

import junit.framework.Assert;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONException;
import org.json.JSONObject;

import com.nttdocomo.android.dconnect.test.plugin.profile.TestFileDescriptorProfileConstants;
import com.nttdocomo.dconnect.message.DConnectMessage;
import com.nttdocomo.dconnect.profile.AuthorizationProfileConstants;
import com.nttdocomo.dconnect.profile.DConnectProfileConstants;
import com.nttdocomo.dconnect.profile.FileDescriptorProfileConstants;

/**
 * FileDescriptorプロファイルの正常系テスト.
 * @author NTT DOCOMO, INC.
 */
public class NormalFileDescriptorProfileTestCase extends RESTfulDConnectTestCase {

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
     * 【HTTP通信】
     * Method: GET
     * Path: /file_descriptor/open?deviceid=xxxx&mediaid=xxxx&flag=xxxx&mode=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * ・mediaidに"test.txt"が返ってくること。
     * </pre>
     */
    public void testOpen() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + FileDescriptorProfileConstants.PROFILE_NAME);
        builder.append("/" + FileDescriptorProfileConstants.ATTRIBUTE_OPEN);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(FileDescriptorProfileConstants.PARAM_PATH + "=test.txt");
        builder.append("&");
        builder.append(FileDescriptorProfileConstants.PARAM_FLAG + "=r");
        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * ファイルをクローズするテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /file_descriptor/close?deviceid=xxxx&mediaid=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testClose() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + FileDescriptorProfileConstants.PROFILE_NAME);
        builder.append("/" + FileDescriptorProfileConstants.ATTRIBUTE_CLOSE);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(FileDescriptorProfileConstants.PARAM_PATH + "=test.txt");
        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpUriRequest request = new HttpPut(builder.toString());
            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 指定したサイズ分のデータをファイルから読み込むテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /file_descriptor/read?deviceid=xxxx&mediaid=xxxx&length=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testRead001() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + FileDescriptorProfileConstants.PROFILE_NAME);
        builder.append("/" + FileDescriptorProfileConstants.ATTRIBUTE_READ);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(FileDescriptorProfileConstants.PARAM_PATH + "=test.txt");
        builder.append("&");
        builder.append(FileDescriptorProfileConstants.PARAM_LENGTH + "=256");
        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            assertResultOK(root);
            Assert.assertEquals(TestFileDescriptorProfileConstants.BYTE,
                    root.getInt(FileDescriptorProfileConstants.PARAM_SIZE));
            Assert.assertEquals(TestFileDescriptorProfileConstants.FILE_DATA, 
                    root.getString(FileDescriptorProfileConstants.PARAM_FILE_DATA));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 指定したサイズ分のデータをファイルから読み込むテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /file_descriptor/read?deviceid=xxxx&mediaid=xxxx&length=xxxx&position=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testRead002() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + FileDescriptorProfileConstants.PROFILE_NAME);
        builder.append("/" + FileDescriptorProfileConstants.ATTRIBUTE_READ);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(FileDescriptorProfileConstants.PARAM_PATH + "=test.txt");
        builder.append("&");
        builder.append(FileDescriptorProfileConstants.PARAM_LENGTH + "=256");
        builder.append("&");
        builder.append(FileDescriptorProfileConstants.PARAM_POSITION + "=0");
        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
            Assert.assertEquals(TestFileDescriptorProfileConstants.BYTE,
                    root.getInt(FileDescriptorProfileConstants.PARAM_SIZE));
            Assert.assertEquals(TestFileDescriptorProfileConstants.FILE_DATA, 
                    root.getString(FileDescriptorProfileConstants.PARAM_FILE_DATA));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * ファイルに書き込むテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /file_descriptor/write?deviceid=xxxx&mediaid=xxxx
     * Entity: 文字列"test"。
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testWrite001() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + FileDescriptorProfileConstants.PROFILE_NAME);
        builder.append("/" + FileDescriptorProfileConstants.ATTRIBUTE_WRITE);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(FileDescriptorProfileConstants.PARAM_PATH + "=test.txt");
        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            MultipartEntity entity = new MultipartEntity();
            entity.addPart("media", new StringBody("test"));
            HttpPut request = new HttpPut(builder.toString());
            request.addHeader("Content-Disposition", "form-data; name=\"media\"; filename=\"test.txt\"");
            request.setEntity(entity);
            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            assertResultOK(root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        } catch (UnsupportedEncodingException e) {
            fail("Exception in StringBody." + e.getMessage());
        }
    }

    /**
     * ファイルに書き込むテストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /file_descriptor/write?deviceid=xxxx&mediaid=xxxx&position=xxx
     * Entity: 文字列"test"。
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testWrite002() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + FileDescriptorProfileConstants.PROFILE_NAME);
        builder.append("/" + FileDescriptorProfileConstants.ATTRIBUTE_WRITE);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(FileDescriptorProfileConstants.PARAM_PATH + "=test.txt");
        builder.append("&");
        builder.append(FileDescriptorProfileConstants.PARAM_POSITION + "=0");

        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            MultipartEntity entity = new MultipartEntity();
            entity.addPart("media", new StringBody("test"));
            HttpPut request = new HttpPut(builder.toString());
            request.addHeader("Content-Disposition", "form-data; name=\"media\"; filename=\"test.txt\"");
            request.setEntity(entity);
            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            assertResultOK(root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        } catch (UnsupportedEncodingException e) {
            fail("Exception in StringBody." + e.getMessage());
        }
    }

    /**
     * ファイルの更新通知のコールバック登録テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /file_descriptor/watchfile?deviceid=xxxx&session_key=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testWatchFile01() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + FileDescriptorProfileConstants.PROFILE_NAME);
        builder.append("/" + FileDescriptorProfileConstants.ATTRIBUTE_ON_WATCH_FILE);
        builder.append("?");
        builder.append(DConnectProfileConstants.PARAM_DEVICE_ID + "=" + getDeviceId());
        builder.append("&");
        builder.append(DConnectProfileConstants.PARAM_SESSION_KEY + "=" + getClientId());
        builder.append("&");
        builder.append(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN + "=" + getAccessToken());
        try {
            HttpPut request = new HttpPut(builder.toString());
            JSONObject root = sendRequest(request);
            Assert.assertNotNull("root is null.", root);
            Assert.assertEquals(DConnectMessage.RESULT_OK, root.getInt(DConnectMessage.EXTRA_RESULT));
            JSONObject response = waitForEvent();
            JSONObject file = response.getJSONObject(FileDescriptorProfileConstants.PARAM_FILE);
            Assert.assertEquals(TestFileDescriptorProfileConstants.PATH, 
                    file.getString(FileDescriptorProfileConstants.PARAM_PATH));
            Assert.assertEquals(TestFileDescriptorProfileConstants.CURR, 
                    file.getString(FileDescriptorProfileConstants.PARAM_CURR));
            Assert.assertEquals(TestFileDescriptorProfileConstants.PREV, 
                    file.getString(FileDescriptorProfileConstants.PARAM_PREV));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * ファイルの更新通知のコールバック解除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /file_descriptor/watchfile?deviceid=xxxx&session_key=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testWatchFile02() {
        StringBuilder builder = new StringBuilder();
        builder.append(DCONNECT_MANAGER_URI);
        builder.append("/" + FileDescriptorProfileConstants.PROFILE_NAME);
        builder.append("/" + FileDescriptorProfileConstants.ATTRIBUTE_ON_WATCH_FILE);
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
