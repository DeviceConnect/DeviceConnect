/*
 FailAuthorizationProfileTestCase.java
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
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.message.DConnectMessage.ErrorCode;
import org.deviceconnect.profile.AuthorizationProfileConstants;
import org.deviceconnect.utils.URIBuilder;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Authorizationプロファイルの異常系テスト.
 * @author NTT DOCOMO, INC.
 */
public class FailAuthorizationProfileTestCase extends RESTfulDConnectTestCase {

    /**
     * アプリケーション名: {@value}.
     */
    private static final String TEST_APPLICATION_NAME = "dConnectManagerTest";

    /**
     * グラントタイプ: {@value}.
     */
    private static final String GRANT_TYPE = "authorization_code";

    /**
     * コンストラクタ.
     * 
     * @param tag テストタグ
     */
    public FailAuthorizationProfileTestCase(final String tag) {
        super(tag);
    }

    @Override
    protected boolean isLocalOAuth() {
        return false;
    }

    @Override
    protected boolean isSearchDevices() {
        return false;
    }

    @Override
    protected String getClientPackageName() {
        return "abc";
    }

    /**
     * packageが無い状態でクライアント作成を行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /authorization/create_client
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetCreateClientNoPackage() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(AuthorizationProfileConstants.PROFILE_NAME);
        builder.setAttribute(AuthorizationProfileConstants.ATTRIBUTE_CREATE_CLIENT);
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request, false);
            assertResultError(ErrorCode.INVALID_REQUEST_PARAMETER.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * packageが空状態でクライアント作成を行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /authorization/create_client?package=
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetCreateClientEmptyPackage() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(AuthorizationProfileConstants.PROFILE_NAME);
        builder.setAttribute(AuthorizationProfileConstants.ATTRIBUTE_CREATE_CLIENT);
        builder.addParameter(AuthorizationProfileConstants.PARAM_PACKAGE, "");
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request, false);
            assertResultError(ErrorCode.INVALID_REQUEST_PARAMETER.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 定義にない属性を指定してクライアント作成を行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /authorization/create_client?package=xxxxx&abc=abc
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・定義にない属性は無視されること。
     * ・resultが0で返ってくること。
     * </pre>
     */
    public void testGetCreateClientUndefinedAttribute() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(AuthorizationProfileConstants.PROFILE_NAME);
        builder.setAttribute(AuthorizationProfileConstants.ATTRIBUTE_CREATE_CLIENT);
        builder.addParameter(AuthorizationProfileConstants.PARAM_PACKAGE, "abc");
        builder.addParameter("def", "def");
        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request, false);
            assertResultOK(root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メソッドにPOSTを指定してクライアント作成を行う.
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /authorization/create_client?package=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetCreateClientInvalidMethodPost() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(AuthorizationProfileConstants.PROFILE_NAME);
        builder.setAttribute(AuthorizationProfileConstants.ATTRIBUTE_CREATE_CLIENT);
        builder.addParameter(AuthorizationProfileConstants.PARAM_PACKAGE, "abc");
        try {
            HttpUriRequest request = new HttpPost(builder.toString());
            JSONObject root = sendRequest(request, false);
            assertResultError(ErrorCode.NOT_SUPPORT_ACTION.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メソッドにPUTを指定してクライアント作成を行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /authorization/create_client?package=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetCreateClientInvalidMethodPut() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(AuthorizationProfileConstants.PROFILE_NAME);
        builder.setAttribute(AuthorizationProfileConstants.ATTRIBUTE_CREATE_CLIENT);
        builder.addParameter(AuthorizationProfileConstants.PARAM_PACKAGE, "abc");
        try {
            HttpUriRequest request = new HttpPut(builder.toString());
            JSONObject root = sendRequest(request, false);
            assertResultError(ErrorCode.NOT_SUPPORT_ACTION.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メソッドにDELETEを指定してクライアント作成を行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /authorization/create_client?package=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetCreateClientInvalidMethodDelete() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(AuthorizationProfileConstants.PROFILE_NAME);
        builder.setAttribute(AuthorizationProfileConstants.ATTRIBUTE_CREATE_CLIENT);
        builder.addParameter(AuthorizationProfileConstants.PARAM_PACKAGE, "abc");
        try {
            HttpUriRequest request = new HttpDelete(builder.toString());
            JSONObject root = sendRequest(request, false);
            assertResultError(ErrorCode.NOT_SUPPORT_ACTION.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * clientIdが無い状態でアクセストークン作成を行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /authorization/request_accesstoken?grantType=xxxx&scope=xxxx&applicationName=xxxx&signature=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetRequestAccessTokenNoClientId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(AuthorizationProfileConstants.PROFILE_NAME);
        builder.setAttribute(AuthorizationProfileConstants.ATTRIBUTE_REQUEST_ACCESS_TOKEN);
        builder.addParameter(AuthorizationProfileConstants.PARAM_GRANT_TYPE, GRANT_TYPE);
        builder.addParameter(AuthorizationProfileConstants.PARAM_SCOPE, "battery");
        builder.addParameter(AuthorizationProfileConstants.PARAM_APPLICATION_NAME,
                TEST_APPLICATION_NAME);
        builder.addParameter(AuthorizationProfileConstants.PARAM_SIGNATURE, "");

        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request, false);
            assertResultError(ErrorCode.INVALID_REQUEST_PARAMETER.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * clientIdに空文字を指定した状態でアクセストークン作成を行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /authorization/request_accesstoken?clintId=&grantType=xxxx&scope=xxxx&applicationName=xxxx&signature=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetRequestAccessTokenEmptyClientId() {
        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(AuthorizationProfileConstants.PROFILE_NAME);
        builder.setAttribute(AuthorizationProfileConstants.ATTRIBUTE_REQUEST_ACCESS_TOKEN);
        builder.addParameter(AuthorizationProfileConstants.PARAM_CLIENT_ID, "");
        builder.addParameter(AuthorizationProfileConstants.PARAM_GRANT_TYPE, GRANT_TYPE);
        builder.addParameter(AuthorizationProfileConstants.PARAM_SCOPE, "battery");
        builder.addParameter(AuthorizationProfileConstants.PARAM_APPLICATION_NAME,
                TEST_APPLICATION_NAME);
        builder.addParameter(AuthorizationProfileConstants.PARAM_SIGNATURE, "");

        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request, false);
            assertResultError(ErrorCode.AUTHORIZATION.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 未登録のclientIdを指定した状態でアクセストークン作成を行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /authorization/request_accesstoken?grantType=xxxx&scope=xxxx&applicationName=xxxx&signature=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetRequestAccessTokenNotRegisteredClientId() {
        final String clientId = "not_registered_client_id";
        final String clientSecret = "dummy_client_secret";

        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(AuthorizationProfileConstants.PROFILE_NAME);
        builder.setAttribute(AuthorizationProfileConstants.ATTRIBUTE_REQUEST_ACCESS_TOKEN);
        builder.addParameter(AuthorizationProfileConstants.PARAM_CLIENT_ID, clientId);
        builder.addParameter(AuthorizationProfileConstants.PARAM_GRANT_TYPE, GRANT_TYPE);
        builder.addParameter(AuthorizationProfileConstants.PARAM_SCOPE, "battery");
        builder.addParameter(AuthorizationProfileConstants.PARAM_APPLICATION_NAME,
                TEST_APPLICATION_NAME);
        String signature = createSignature(clientId, new String[] {"battery"}, clientSecret);
        builder.addParameter(AuthorizationProfileConstants.PARAM_SIGNATURE, signature);

        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request, false);
            assertResultError(ErrorCode.AUTHORIZATION.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * grantTypeが無い状態でアクセストークン作成を行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /authorization/request_accesstoken?clientId=xxxx&scope=xxxx&applicationName=xxxx&signature=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetRequestAccessTokenNoGrantType() {
        String[] client = createClient("abc");

        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(AuthorizationProfileConstants.PROFILE_NAME);
        builder.setAttribute(AuthorizationProfileConstants.ATTRIBUTE_REQUEST_ACCESS_TOKEN);
        builder.addParameter(AuthorizationProfileConstants.PARAM_CLIENT_ID, client[0]);
        builder.addParameter(AuthorizationProfileConstants.PARAM_SCOPE, "battery");
        builder.addParameter(AuthorizationProfileConstants.PARAM_APPLICATION_NAME,
                TEST_APPLICATION_NAME);
        String signature = createSignature(client[0], new String[] {"battery"}, client[1]);
        builder.addParameter(AuthorizationProfileConstants.PARAM_SIGNATURE, signature);

        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request, false);
            assertResultError(ErrorCode.INVALID_REQUEST_PARAMETER.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * grantTypeに空文字を指定した状態でアクセストークン作成を行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /authorization/request_accesstoken?clientId=xxxx&grantType=&scope=xxxx&applicationName=xxxx&signature=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetRequestAccessTokenEmptyGrantType() {
        String[] client = createClient("abc");

        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(AuthorizationProfileConstants.PROFILE_NAME);
        builder.setAttribute(AuthorizationProfileConstants.ATTRIBUTE_REQUEST_ACCESS_TOKEN);
        builder.addParameter(AuthorizationProfileConstants.PARAM_CLIENT_ID, client[0]);
        builder.addParameter(AuthorizationProfileConstants.PARAM_GRANT_TYPE, "");
        builder.addParameter(AuthorizationProfileConstants.PARAM_SCOPE, "battery");
        builder.addParameter(AuthorizationProfileConstants.PARAM_APPLICATION_NAME,
                TEST_APPLICATION_NAME);
        String signature = createSignature(client[0], new String[] {"battery"}, client[1]);
        builder.addParameter(AuthorizationProfileConstants.PARAM_SIGNATURE, signature);

        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request, false);
            assertResultError(ErrorCode.AUTHORIZATION.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 未定義のgrantTypeを指定した状態でアクセストークン作成を行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /authorization/request_accesstoken?
     *           clientId=xxxx&grantType=undefined_grant_type&scope=xxxx&applicationName=xxxx&signature=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetRequestAccessTokenUndefinedGrantType() {
        String[] client = createClient("abc");

        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(AuthorizationProfileConstants.PROFILE_NAME);
        builder.setAttribute(AuthorizationProfileConstants.ATTRIBUTE_REQUEST_ACCESS_TOKEN);
        builder.addParameter(AuthorizationProfileConstants.PARAM_CLIENT_ID, client[0]);
        builder.addParameter(AuthorizationProfileConstants.PARAM_GRANT_TYPE, "undefined_grant_type");
        builder.addParameter(AuthorizationProfileConstants.PARAM_SCOPE, "battery");
        builder.addParameter(AuthorizationProfileConstants.PARAM_APPLICATION_NAME,
                TEST_APPLICATION_NAME);
        String signature = createSignature(client[0], new String[] {"battery"}, client[1]);
        builder.addParameter(AuthorizationProfileConstants.PARAM_SIGNATURE, signature);

        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request, false);
            assertResultError(ErrorCode.AUTHORIZATION.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * scopeが無い状態でアクセストークン作成を行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /authorization/request_accesstoken?clientId=xxxx&grantType=xxxx&applicationName=xxxx&signature=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetRequestAccessTokenNoScope() {
        String[] client = createClient("abc");

        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(AuthorizationProfileConstants.PROFILE_NAME);
        builder.setAttribute(AuthorizationProfileConstants.ATTRIBUTE_REQUEST_ACCESS_TOKEN);
        builder.addParameter(AuthorizationProfileConstants.PARAM_CLIENT_ID, client[0]);
        builder.addParameter(AuthorizationProfileConstants.PARAM_GRANT_TYPE, GRANT_TYPE);
        builder.addParameter(AuthorizationProfileConstants.PARAM_APPLICATION_NAME,
                TEST_APPLICATION_NAME);
        String signature = createSignature(client[0], new String[] {"battery"}, client[1]);
        builder.addParameter(AuthorizationProfileConstants.PARAM_SIGNATURE, signature);

        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request, false);
            assertResultError(ErrorCode.INVALID_REQUEST_PARAMETER.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * scopeに空文字を指定した状態でアクセストークン作成を行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /authorization/request_accesstoken?
     *           clientId=xxxx&grantType=authorization_code&scope=&applicationName=xxxx&signature=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetRequestAccessTokenEmptyScope() {
        String[] client = createClient("abc");

        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(AuthorizationProfileConstants.PROFILE_NAME);
        builder.setAttribute(AuthorizationProfileConstants.ATTRIBUTE_REQUEST_ACCESS_TOKEN);
        builder.addParameter(AuthorizationProfileConstants.PARAM_CLIENT_ID, client[0]);
        builder.addParameter(AuthorizationProfileConstants.PARAM_GRANT_TYPE, GRANT_TYPE);
        builder.addParameter(AuthorizationProfileConstants.PARAM_SCOPE, "");
        builder.addParameter(AuthorizationProfileConstants.PARAM_APPLICATION_NAME,
                TEST_APPLICATION_NAME);
        String signature = createSignature(client[0], new String[] {"battery"}, client[1]);
        builder.addParameter(AuthorizationProfileConstants.PARAM_SIGNATURE, signature);

        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request, false);
            assertResultError(ErrorCode.AUTHORIZATION.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * applicationNameが無い状態でアクセストークン作成を行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /authorization/request_accesstoken?clientId=xxxx&grantType=xxxx&scope=xxxx&signature=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetRequestAccessTokenNoApplicationName() {
        String[] client = createClient("abc");

        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(AuthorizationProfileConstants.PROFILE_NAME);
        builder.setAttribute(AuthorizationProfileConstants.ATTRIBUTE_REQUEST_ACCESS_TOKEN);
        builder.addParameter(AuthorizationProfileConstants.PARAM_CLIENT_ID, client[0]);
        builder.addParameter(AuthorizationProfileConstants.PARAM_GRANT_TYPE, GRANT_TYPE);
        builder.addParameter(AuthorizationProfileConstants.PARAM_SCOPE, "battery");
        String signature = createSignature(client[0], new String[] {"battery"}, client[1]);
        builder.addParameter(AuthorizationProfileConstants.PARAM_SIGNATURE, signature);

        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request, false);
            assertResultError(ErrorCode.INVALID_REQUEST_PARAMETER.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * applicationに空文字を指定した状態でアクセストークン作成を行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /authorization/request_accesstoken?
     *           clientId=xxxx&grantType=authorization_code&scope=&applicationName=&signature=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetRequestAccessTokenEmptyApplicationName() {
        String[] client = createClient("abc");

        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(AuthorizationProfileConstants.PROFILE_NAME);
        builder.setAttribute(AuthorizationProfileConstants.ATTRIBUTE_REQUEST_ACCESS_TOKEN);
        builder.addParameter(AuthorizationProfileConstants.PARAM_CLIENT_ID, client[0]);
        builder.addParameter(AuthorizationProfileConstants.PARAM_GRANT_TYPE, GRANT_TYPE);
        builder.addParameter(AuthorizationProfileConstants.PARAM_SCOPE, "battery");
        builder.addParameter(AuthorizationProfileConstants.PARAM_APPLICATION_NAME, "");
        String signature = createSignature(client[0], new String[] {"battery"}, client[1]);
        builder.addParameter(AuthorizationProfileConstants.PARAM_SIGNATURE, signature);

        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request, false);
            assertResultError(ErrorCode.INVALID_REQUEST_PARAMETER.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * signatureが無い状態でアクセストークン作成を行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /authorization/request_accesstoken?clientId=xxxx&grantType=xxxx&scope=xxxx&applicationName=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetRequestAccessTokenNoSignature() {
        String[] client = createClient("abc");

        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(AuthorizationProfileConstants.PROFILE_NAME);
        builder.setAttribute(AuthorizationProfileConstants.ATTRIBUTE_REQUEST_ACCESS_TOKEN);
        builder.addParameter(AuthorizationProfileConstants.PARAM_CLIENT_ID, client[0]);
        builder.addParameter(AuthorizationProfileConstants.PARAM_GRANT_TYPE, GRANT_TYPE);
        builder.addParameter(AuthorizationProfileConstants.PARAM_SCOPE, "battery");
        builder.addParameter(AuthorizationProfileConstants.PARAM_APPLICATION_NAME,
                TEST_APPLICATION_NAME);

        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request, false);
            assertResultError(root);
            assertEquals(ErrorCode.INVALID_REQUEST_PARAMETER.getCode(), root.getInt(DConnectMessage.EXTRA_ERROR_CODE));
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * signatureに空文字を指定した状態でアクセストークン作成を行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /authorization/request_accesstoken?
     *           clientId=xxxx&grantType=authorization_code&scope=xxxx&applicationName=xxxx&signature=
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetRequestAccessTokenEmptySignature() {
        String[] client = createClient("abc");

        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(AuthorizationProfileConstants.PROFILE_NAME);
        builder.setAttribute(AuthorizationProfileConstants.ATTRIBUTE_REQUEST_ACCESS_TOKEN);
        builder.addParameter(AuthorizationProfileConstants.PARAM_CLIENT_ID, client[0]);
        builder.addParameter(AuthorizationProfileConstants.PARAM_GRANT_TYPE, GRANT_TYPE);
        builder.addParameter(AuthorizationProfileConstants.PARAM_SCOPE, "battery");
        builder.addParameter(AuthorizationProfileConstants.PARAM_APPLICATION_NAME,
                TEST_APPLICATION_NAME);
        builder.addParameter(AuthorizationProfileConstants.PARAM_SIGNATURE, "");

        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request, false);
            assertResultError(ErrorCode.AUTHORIZATION.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 空文字でない不正なsignatureを指定した状態でアクセストークン作成を行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /authorization/request_accesstoken?
     *           clientId=xxxx&grantType=authorization_code&scope=xxxx&applicationName=xxxx&signature=
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetRequestAccessTokenWrongSignature() {
        String[] client = createClient("abc");

        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(AuthorizationProfileConstants.PROFILE_NAME);
        builder.setAttribute(AuthorizationProfileConstants.ATTRIBUTE_REQUEST_ACCESS_TOKEN);
        builder.addParameter(AuthorizationProfileConstants.PARAM_CLIENT_ID, client[0]);
        builder.addParameter(AuthorizationProfileConstants.PARAM_GRANT_TYPE, GRANT_TYPE);
        builder.addParameter(AuthorizationProfileConstants.PARAM_SCOPE, "battery");
        builder.addParameter(AuthorizationProfileConstants.PARAM_APPLICATION_NAME,
                TEST_APPLICATION_NAME);
        String wrongSignature = "wrong" + createSignature(client[0], new String[] {"battery"}, client[1]);
        builder.addParameter(AuthorizationProfileConstants.PARAM_SIGNATURE, wrongSignature);

        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request, false);
            assertResultError(ErrorCode.AUTHORIZATION.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * 定義にない属性を指定してアクセストークン作成を行う.
     * <pre>
     * 【HTTP通信】
     * Method: GET
     * Path: /authorization/request_accesstoken?
     *           clientId=xxxx&grantType=xxxx&scope=xxxx&applicationName=xxxxsignature=xxxx&abc=abc
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・定義にない属性は無視されること。
     * ・resultが0で返ってくること。
     * </pre>
     */
    public void testGetRequestAccessTokenUndefinedAttribute() {
        String[] client = createClient("abc");

        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(AuthorizationProfileConstants.PROFILE_NAME);
        builder.setAttribute(AuthorizationProfileConstants.ATTRIBUTE_REQUEST_ACCESS_TOKEN);
        builder.addParameter(AuthorizationProfileConstants.PARAM_CLIENT_ID, client[0]);
        builder.addParameter(AuthorizationProfileConstants.PARAM_GRANT_TYPE, GRANT_TYPE);
        builder.addParameter(AuthorizationProfileConstants.PARAM_SCOPE, "battery");
        builder.addParameter(AuthorizationProfileConstants.PARAM_APPLICATION_NAME,
                TEST_APPLICATION_NAME);
        String signature = createSignature(client[0], new String[] {"battery"}, client[1]);
        builder.addParameter(AuthorizationProfileConstants.PARAM_SIGNATURE, signature);
        builder.addParameter("abc", "abc");

        try {
            HttpUriRequest request = new HttpGet(builder.toString());
            JSONObject root = sendRequest(request, false);
            assertResultOK(root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メソッドにPOSTを指定してアクセストークン作成を行う.
     * <pre>
     * 【HTTP通信】
     * Method: POST
     * Path: /authorization/request_accesstoken?
     *           clientId=xxxx&grantType=xxxx&scope=xxxx&applicationName=xxxxsignature=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetRequestAccessTokenInvalidMethodPost() {
        String[] client = createClient("abc");

        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(AuthorizationProfileConstants.PROFILE_NAME);
        builder.setAttribute(AuthorizationProfileConstants.ATTRIBUTE_REQUEST_ACCESS_TOKEN);
        builder.addParameter(AuthorizationProfileConstants.PARAM_CLIENT_ID, client[0]);
        builder.addParameter(AuthorizationProfileConstants.PARAM_GRANT_TYPE, GRANT_TYPE);
        builder.addParameter(AuthorizationProfileConstants.PARAM_SCOPE, "battery");
        builder.addParameter(AuthorizationProfileConstants.PARAM_APPLICATION_NAME,
                TEST_APPLICATION_NAME);
        String signature = createSignature(client[0], new String[] {"battery"}, client[1]);
        builder.addParameter(AuthorizationProfileConstants.PARAM_SIGNATURE, signature);
        try {
            HttpUriRequest request = new HttpPost(builder.toString());
            JSONObject root = sendRequest(request, false);
            assertResultError(ErrorCode.NOT_SUPPORT_ACTION.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メソッドにPUTを指定してアクセストークン作成を行う.
     * <pre>
     * 【HTTP通信】
     * Method: PUT
     * Path: /authorization/request_accesstoken?
     *           clientId=xxxx&grantType=xxxx&scope=xxxx&applicationName=xxxxsignature=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetRequestAccessTokenInvalidMethodPut() {
        String[] client = createClient("abc");

        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(AuthorizationProfileConstants.PROFILE_NAME);
        builder.setAttribute(AuthorizationProfileConstants.ATTRIBUTE_REQUEST_ACCESS_TOKEN);
        builder.addParameter(AuthorizationProfileConstants.PARAM_CLIENT_ID, client[0]);
        builder.addParameter(AuthorizationProfileConstants.PARAM_GRANT_TYPE, GRANT_TYPE);
        builder.addParameter(AuthorizationProfileConstants.PARAM_SCOPE, "battery");
        builder.addParameter(AuthorizationProfileConstants.PARAM_APPLICATION_NAME,
                TEST_APPLICATION_NAME);
        String signature = createSignature(client[0], new String[] {"battery"}, client[1]);
        builder.addParameter(AuthorizationProfileConstants.PARAM_SIGNATURE, signature);
        try {
            HttpUriRequest request = new HttpPut(builder.toString());
            JSONObject root = sendRequest(request, false);
            assertResultError(ErrorCode.NOT_SUPPORT_ACTION.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

    /**
     * メソッドにDELETEを指定してアクセストークン作成を行う.
     * <pre>
     * 【HTTP通信】
     * Method: DELETE
     * Path: /authorization/request_accesstoken?
     *           clientId=xxxx&grantType=xxxx&scope=xxxx&applicationName=xxxxsignature=xxxx
     * </pre>
     * <pre>
     * 【期待する動作】
     * ・resultに1が返ってくること。
     * </pre>
     */
    public void testGetRequestAccessTokenInvalidMethodDelete() {
        String[] client = createClient("abc");

        URIBuilder builder = TestURIBuilder.createURIBuilder();
        builder.setProfile(AuthorizationProfileConstants.PROFILE_NAME);
        builder.setAttribute(AuthorizationProfileConstants.ATTRIBUTE_REQUEST_ACCESS_TOKEN);
        builder.addParameter(AuthorizationProfileConstants.PARAM_CLIENT_ID, client[0]);
        builder.addParameter(AuthorizationProfileConstants.PARAM_GRANT_TYPE, GRANT_TYPE);
        builder.addParameter(AuthorizationProfileConstants.PARAM_SCOPE, "battery");
        builder.addParameter(AuthorizationProfileConstants.PARAM_APPLICATION_NAME,
                TEST_APPLICATION_NAME);
        String signature = createSignature(client[0], new String[] {"battery"}, client[1]);
        builder.addParameter(AuthorizationProfileConstants.PARAM_SIGNATURE, signature);
        try {
            HttpUriRequest request = new HttpDelete(builder.toString());
            JSONObject root = sendRequest(request, false);
            assertResultError(ErrorCode.NOT_SUPPORT_ACTION.getCode(), root);
        } catch (JSONException e) {
            fail("Exception in JSONObject." + e.getMessage());
        }
    }

}
