/*
 FailFileProfileTestCase.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.profile.restful.test;

import java.io.IOException;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.deviceconnect.android.test.plugin.profile.TestFileProfileConstants;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.message.DConnectMessage.ErrorCode;
import org.deviceconnect.profile.AuthorizationProfileConstants;
import org.deviceconnect.profile.DConnectProfileConstants;
import org.deviceconnect.profile.FileProfileConstants;
import org.deviceconnect.profile.FileProfileConstants.FileType;
import org.deviceconnect.utils.URIBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.AssetManager;


/**
 * FileDescriptorプロファイルの異常系テスト.
 * @author NTT DOCOMO, INC.
 */
public class FailFileProfileTestCase extends RESTfulDConnectTestCase {

    /**
     * コンストラクタ.
     * 
     * @param tag テストタグ
     */
    public FailFileProfileTestCase(final String tag) {
        super(tag);
    }

    /**
     * deviceIdが無い状態でファイル一覧取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /file/list
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetListNoDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_LIST);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.EMPTY_DEVICE_ID.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceIdが空状態でファイル一覧取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /file/list?deviceId=
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetListEmptyDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_LIST);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "");
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 存在しないdeviceIdでファイル一覧取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /file/list?deviceId=123456789
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetListInvalidDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_LIST);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 定義にない属性を指定してファイル一覧取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /file/list?deviceId=xxxxx&abc=abc
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・定義にない属性は無視されること。
     * ・resultが0で返ってくること。
     * </pre>
     */
    public void testGetListUndefinedAttribute() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_LIST);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter("abc", "abc");
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultOK(root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceIdを2重に指定してファイル一覧取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /file/list?deviceId=123456789&deviceId=xxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・先に定義された属性が優先されること。
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetListDuplicatedDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_LIST);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メソッドにPOSTを指定してファイル一覧取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /file/list?deviceId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetListInvalidMethodPost() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_LIST);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpPost(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.UNKNOWN_ATTRIBUTE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メソッドにPUTを指定してファイル一覧取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /file/list?deviceId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetListInvalidMethodPut() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_LIST);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpPut(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_SUPPORT_ACTION.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メソッドにDELETEを指定してファイル一覧取得テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /file/list?deviceId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetListInvalidMethodDelete() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_LIST);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpDelete(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.UNKNOWN_ATTRIBUTE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceIdが無い状態でファイル受信テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /file/receive?mediaId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetReceiveNoDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_RECEIVE);
        builder.addParameter(FileProfileConstants.PARAM_PATH,
                TestFileProfileConstants.PATH);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.EMPTY_DEVICE_ID.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceIdが空状態でファイル受信テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /file/receive?deviceId=&mediaId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetReceiveEmptyDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_RECEIVE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "");
        builder.addParameter(FileProfileConstants.PARAM_PATH,
                TestFileProfileConstants.PATH);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 存在しないdeviceIdでファイル受信テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /file/receive?deviceId=123456789&mediaId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetReceiveInvalidDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_RECEIVE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(FileProfileConstants.PARAM_PATH,
                TestFileProfileConstants.PATH);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 定義にない属性を指定してファイル受信テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /file/receive?deviceId=xxxxx&mediaId=xxxx&abc=abc
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・定義にない属性は無視されること。
     * ・resultが0で返ってくること。
     * </pre>
     */
    public void testGetReceiveUndefinedAttribute() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_RECEIVE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(FileProfileConstants.PARAM_PATH,
                TestFileProfileConstants.PATH);
        builder.addParameter("abc", "abc");
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultOK(root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceIdを2重に指定してファイル受信テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /file/receive?deviceId=123456789&deviceId=xxx&mediaId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・先に定義された属性が優先されること。
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetReceiveDuplicatedDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_RECEIVE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(FileProfileConstants.PARAM_PATH,
                TestFileProfileConstants.PATH);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 定義にない属性を指定してファイル受信テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /file/receive?deviceId=xxxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・定義にない属性は無視されること。
     * ・resultが0で返ってくること。
     * </pre>
     */
    public void testGetReceiveNoMediaId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_RECEIVE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メソッドにPOSTを指定してファイル受信テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /file/receive?deviceId=xxxx&mediaId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetReceiveInvalidMethodPost() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_RECEIVE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(FileProfileConstants.PARAM_PATH,
                TestFileProfileConstants.PATH);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpPost(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.UNKNOWN_ATTRIBUTE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メソッドにPUTを指定してファイル受信テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /file/receive?deviceId=xxxx&mediaId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetReceiveInvalidMethodPut() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_RECEIVE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(FileProfileConstants.PARAM_PATH,
                TestFileProfileConstants.PATH);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpPut(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_SUPPORT_ACTION.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メソッドにDELETEを指定してファイル受信テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /file/receive?deviceId=xxxx&mediaId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetReceiveInvalidMethodDelete() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_RECEIVE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(FileProfileConstants.PARAM_PATH,
                TestFileProfileConstants.PATH);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpUriRequest request = new HttpDelete(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.UNKNOWN_ATTRIBUTE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceIdが無い状態でファイル送信テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /file/send?mediaId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPostSendNoDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_SEND);
        builder.addParameter(FileProfileConstants.PARAM_PATH,
                TestFileProfileConstants.PATH);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpPost request = new HttpPost(builder.toString());
            request.setEntity(getEntity());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.EMPTY_DEVICE_ID.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceIdが空状態でファイル送信テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /file/send?deviceId=&mediaId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPostSendEmptyDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_SEND);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "");
        builder.addParameter(FileProfileConstants.PARAM_PATH,
                TestFileProfileConstants.PATH);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpPost request = new HttpPost(builder.toString());
            request.setEntity(getEntity());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 存在しないdeviceIdでファイル送信テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /file/send?deviceId=123456789&mediaId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPostSendInvalidDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_SEND);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(FileProfileConstants.PARAM_PATH,
                TestFileProfileConstants.PATH);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpPost request = new HttpPost(builder.toString());
            request.setEntity(getEntity());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 定義にない属性を指定してファイル送信テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /file/send?deviceId=xxxxx&mediaId=xxxx&abc=abc
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・定義にない属性は無視されること。
     * ・resultが0で返ってくること。
     * </pre>
     */
    public void testPostSendUndefinedAttribute() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_SEND);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(FileProfileConstants.PARAM_PATH,
                TestFileProfileConstants.PATH);
        builder.addParameter(FileProfileConstants.PARAM_FILE_TYPE, String.valueOf(FileType.FILE.getValue()));
        builder.addParameter("abc", "abc");
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpPost request = new HttpPost(builder.toString());
            request.setEntity(getEntity());
            JSONObject root = sendRequest(request);
            assertResultOK(root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceIdを2重に指定してファイル送信テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /file/send?deviceId=123456789&deviceId=xxx&mediaId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・先に定義された属性が優先されること。
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPostSendDuplicatedDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_SEND);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(FileProfileConstants.PARAM_PATH,
                TestFileProfileConstants.PATH);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpPost request = new HttpPost(builder.toString());
            request.setEntity(getEntity());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * media属性を指定せずにファイル送信テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /file/send?deviceId=xxxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・定義にない属性は無視されること。
     * ・resultが0で返ってくること。
     * </pre>
     */
    public void testPostSendNoMedia() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_SEND);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpPost request = new HttpPost(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.INVALID_REQUEST_PARAMETER.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メソッドにPUTを指定してファイル送信テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /file/send?deviceId=xxxx&mediaId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPostSendInvalidMethodPut() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_SEND);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(FileProfileConstants.PARAM_PATH,
                TestFileProfileConstants.PATH);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpPut request = new HttpPut(builder.toString());
            request.setEntity(getEntity());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_SUPPORT_ACTION.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メソッドにDELETEを指定してファイル送信テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /file/send?deviceId=xxxx&mediaId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPostSendInvalidMethodDelete() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_SEND);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(FileProfileConstants.PARAM_PATH,
                TestFileProfileConstants.PATH);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpDelete request = new HttpDelete(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.UNKNOWN_ATTRIBUTE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceIdが無い状態でファイル削除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /file/remove?mediaId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteRemoveNoDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_REMOVE);
        builder.addParameter(FileProfileConstants.PARAM_PATH,
                TestFileProfileConstants.PATH);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpDelete request = new HttpDelete(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.EMPTY_DEVICE_ID.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceIdが空状態でファイル削除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /file/remove?deviceId=&mediaId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteRemoveEmptyDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_REMOVE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "");
        builder.addParameter(FileProfileConstants.PARAM_PATH,
                TestFileProfileConstants.PATH);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpDelete request = new HttpDelete(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 存在しないdeviceIdでファイル削除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /file/remove?deviceId=123456789&mediaId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteRemoveInvalidDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_REMOVE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(FileProfileConstants.PARAM_PATH,
                TestFileProfileConstants.PATH);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpDelete request = new HttpDelete(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 定義にない属性を指定してファイル削除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /file/remove?deviceId=xxxxx&mediaId=xxxx&abc=abc
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・定義にない属性は無視されること。
     * ・resultが0で返ってくること。
     * </pre>
     */
    public void testDeleteRemoveUndefinedAttribute() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_REMOVE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(FileProfileConstants.PARAM_PATH,
                TestFileProfileConstants.PATH);
        builder.addParameter("abc", "abc");
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpDelete request = new HttpDelete(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultOK(root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceIdを2重に指定してファイル削除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /file/remove?deviceId=123456789&deviceId=xxx&mediaId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・先に定義された属性が優先されること。
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteRemoveDuplicatedDeviceId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_REMOVE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "123456789");
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(FileProfileConstants.PARAM_PATH,
                TestFileProfileConstants.PATH);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpDelete request = new HttpDelete(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * pathを指定せずにファイル削除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /file/remove?deviceId=xxxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・定義にない属性は無視されること。
     * ・resultが1で返ってくること。
     * </pre>
     */
    public void testDeleteRemoveNoPath() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_REMOVE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpDelete request = new HttpDelete(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メソッドにGETを指定してファイル削除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /file/remove?deviceId=xxxx&mediaId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteRemoveInvalidMethodGet() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_REMOVE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(FileProfileConstants.PARAM_PATH,
                TestFileProfileConstants.PATH);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpGet request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.UNKNOWN_ATTRIBUTE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メソッドにPUTを指定してファイル削除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /file/remove?deviceId=xxxx&mediaId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteRemoveInvalidMethodPut() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_REMOVE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(FileProfileConstants.PARAM_PATH,
                TestFileProfileConstants.PATH);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpPut request = new HttpPut(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.NOT_SUPPORT_ACTION.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }
 
    /**
     * メソッドにPOSTを指定してファイル削除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /file/remove?deviceId=xxxx&mediaId=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteRemoveInvalidMethodPost() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_REMOVE);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(FileProfileConstants.PARAM_PATH,
                TestFileProfileConstants.PATH);
        builder.addParameter(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN, getAccessToken());
        try {
            HttpPost request = new HttpPost(builder.toString());
            request.setEntity(getEntity());
            JSONObject root = sendRequest(request);
            assertResultError(ErrorCode.UNKNOWN_ATTRIBUTE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceIdが無い状態でディレクトリ作成テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /file/mkdir?path=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPostMkdirNoDeviceId() {
        final String name = "test";
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_MKDIR);
        builder.addParameter(FileProfileConstants.PARAM_PATH, name);
        builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());

        try {
            HttpPost request = new HttpPost(builder.toString());
            JSONObject root = sendRequest(request);            
            assertResultError(root);
            assertResultError(ErrorCode.EMPTY_DEVICE_ID.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceIdが空状態でディレクトリ作成テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /file/mkdir?deviceId=&path=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPostMkdirEmptyDeviceId() {
        final String name = "test";
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_MKDIR);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "");
        builder.addParameter(FileProfileConstants.PARAM_PATH, name);
        builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());

        try {
            HttpPost request = new HttpPost(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(root);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 存在しないdeviceIdでディレクトリ作成テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /file/mkdir?deviceId=123456789&path=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPostMkdirInvalidDeviceId() {
        final String name = "test";
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_MKDIR);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "12345678");
        builder.addParameter(FileProfileConstants.PARAM_PATH, name);
        builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());

        try {
            HttpPost request = new HttpPost(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(root);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 定義に無い属性を指定してディレクトリ作成テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /file/mkdir?deviceId=xxxx&path=xxxx&abc=abc
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testPostMkdirUndefinedAttribute() {
        final String name = "test";
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_MKDIR);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(FileProfileConstants.PARAM_PATH, name);
        builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());
        builder.addParameter("abc", "abc");

        try {
            HttpPost request = new HttpPost(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultOK(root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceIdを2重に指定してディレクトリ作成テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /file/mkdir?deviceId=12345678&deviceId=xxxx&path=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPostMkdirDuplicatedDeviceId() {
        final String name = "test";
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_MKDIR);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "12345678");
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(FileProfileConstants.PARAM_PATH, name);
        builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());

        try {
            HttpPost request = new HttpPost(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(root);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メソッドにGETを指定してディレクトリ作成テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /file/mkdir?deviceId=xxxx&path=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPostMkdirInvalidMethodGet() {
        final String name = "test";
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_MKDIR);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(FileProfileConstants.PARAM_PATH, name);
        builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());

        try {
            HttpGet request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(root);
            assertResultError(ErrorCode.UNKNOWN_ATTRIBUTE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メソッドにPUTを指定してディレクトリ作成テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /file/mkdir?deviceId=xxxx&path=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPostMkdirInvalidMethodPut() {
        final String name = "test";
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_MKDIR);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(FileProfileConstants.PARAM_PATH, name);
        builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());

        try {
            HttpPut request = new HttpPut(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(root);
            assertResultError(ErrorCode.NOT_SUPPORT_ACTION.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メソッドにDELETEを指定してディレクトリ作成テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /file/mkdir?deviceId=xxxx&path=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testPostMkdirInvalidMethodDelete() {
        final String name = "test";
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_MKDIR);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(FileProfileConstants.PARAM_PATH, name);
        builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());

        try {
            HttpDelete request = new HttpDelete(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(root);
            assertResultError(ErrorCode.UNKNOWN_ATTRIBUTE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceIdが無い状態でディレクトリ削除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /file/rmdir?path=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteRmdirNoDeviceId() {
        final String name = "test";
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_RMDIR);
        builder.addParameter(FileProfileConstants.PARAM_PATH, name);
        builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());

        try {
            HttpDelete request = new HttpDelete(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(root);
            assertResultError(ErrorCode.EMPTY_DEVICE_ID.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceIdが空状態でディレクトリ削除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /file/rmdir?deviceId=&path=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteRmdirEmptyDeviceId() {
        final String name = "test";
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_RMDIR);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "");
        builder.addParameter(FileProfileConstants.PARAM_PATH, name);
        builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());

        try {
            HttpDelete request = new HttpDelete(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(root);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 存在しないdeviceIdでディレクトリ削除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /file/rmdir?deviceId=123456789&path=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteRmdirInvalidDeviceId() {
        final String name = "test";
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_RMDIR);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "12345678");
        builder.addParameter(FileProfileConstants.PARAM_PATH, name);
        builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());

        try {
            HttpDelete request = new HttpDelete(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(root);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 定義に無い属性を指定してディレクトリ削除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /file/rmdir?deviceId=xxxx&path=xxxx&abc=abc
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに0が返ってくること。
     * </pre>
     */
    public void testDeleteRmdirUndefinedAttribute() {
        final String name = "test";
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_RMDIR);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(FileProfileConstants.PARAM_PATH, name);
        builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());
        builder.addParameter("abc", "abc");

        try {
            HttpDelete request = new HttpDelete(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultOK(root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * deviceIdを2重に指定してディレクトリ削除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /file/rmdir?deviceId=12345678&deviceId=xxxx&path=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteRmdirDuplicatedDeviceId() {
        final String name = "test";
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_RMDIR);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, "12345678");
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(FileProfileConstants.PARAM_PATH, name);
        builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());

        try {
            HttpDelete request = new HttpDelete(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(root);
            assertResultError(ErrorCode.NOT_FOUND_DEVICE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メソッドにGETを指定してディレクトリ削除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /file/rmdir?deviceId=xxxx&path=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteRmdirInvalidMethodGet() {
        final String name = "test";
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_RMDIR);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(FileProfileConstants.PARAM_PATH, name);
        builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());

        try {
            HttpGet request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(root);
            assertResultError(ErrorCode.UNKNOWN_ATTRIBUTE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メソッドにPOSTを指定してディレクトリ削除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /file/rmdir?deviceId=xxxx&path=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteRmdirInvalidMethodPost() {
        final String name = "test";
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_RMDIR);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(FileProfileConstants.PARAM_PATH, name);
        builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());

        try {
            HttpPost request = new HttpPost(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(root);
            assertResultError(ErrorCode.UNKNOWN_ATTRIBUTE.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メソッドにPUTを指定してディレクトリ削除テストを行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /file/rmdir?deviceId=xxxx&path=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testDeleteRmdirInvalidMethodPut() {
        final String name = "test";
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(FileProfileConstants.PROFILE_NAME);
        builder.setAttribute(FileProfileConstants.ATTRIBUTE_RMDIR);
        builder.addParameter(DConnectProfileConstants.PARAM_DEVICE_ID, getDeviceId());
        builder.addParameter(FileProfileConstants.PARAM_PATH, name);
        builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, getAccessToken());

        try {
            HttpPut request = new HttpPut(builder.toString());
            JSONObject root = sendRequest(request);
            assertResultError(root);
            assertResultError(ErrorCode.NOT_SUPPORT_ACTION.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * マルチパート送信テスト用ファイルデータを生成する.
     * 
     * @return ファイルデータを格納したマルチパート
     */
    private MultipartEntity getEntity() {
        AssetManager manager = getApplicationContext().getAssets();
        String name = "test.png";
        MultipartEntity entity = new MultipartEntity();
        try {
            entity.addPart(FileProfileConstants.PARAM_DATA, new InputStreamBody(manager.open(name), name));
        } catch (IOException e) {
            fail("Failed to obtain the file: " + e.getMessage());
        }
        return entity;
    }
}
