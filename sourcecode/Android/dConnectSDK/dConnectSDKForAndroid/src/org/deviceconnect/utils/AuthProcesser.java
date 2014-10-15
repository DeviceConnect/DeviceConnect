/*
 AuthProcesser.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.utils;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.deviceconnect.android.cipher.signature.AuthSignature;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.message.DConnectMessage.ErrorCode;
import org.deviceconnect.profile.AuthorizationProfileConstants;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * LocalOAuthの手続きユーティリティクラス.
 * 
 * <h3>サンプルコード</h3>
 * <h4>非同期での認証処理</h4>
 * 
 * <pre>
 * {@code
 * AuthProcesser.asyncAuthorize("localhost", 4035, false, "com.example.client", "Auth Example App", 
 *             new String[] {"battery", "system", "network_service_discovery"},
 *             new AuthorizationHandler() 
 *      {
 *
 *          public void onAuthorized(String clientId, String clientSecret, String accessToken) {
 *              // 認証完了時の処理
 *          }
 *
 *          public void onAuthFailed(ErrorCode error) {
 *           // 認証失敗時の処理
 *          }
 *      });
 * }
 * </pre>
 * @author NTT DOCOMO, INC.
 */
public final class AuthProcesser {
    
    /**
     * ユーティリティのためprivate.
     */
    private AuthProcesser() {
    }
    
    /**
     * 認証手続きを非同期的に行う.
     * Device Connect Managerに対しクライアントの作成依頼、及びアクセストークンの発行依頼を実行する。
     * 処理結果はハンドラに通知される。
     * 
     * @param host Device Connect Managerのホスト名
     * @param port Device Connect Managerのポート番号
     * @param isSSL trueの場合はhttps、falseの場合はhttpで通信する
     * @param packageName Authorization Create Client APIのpackageパラメータの値
     * @param appName Authorization Create Access Token APIのapplicationNameパラメータの値
     * @param scopes Authorization Create Access Token APIのscopeパラメータの値
     * @param callback 処理結果の通知を受けるハンドラ
     */
    public static void asyncAuthorize(final String host, final int port, final boolean isSSL, final String packageName,
            final String appName,
            final String[] scopes, final AuthorizationHandler callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                authorize(host, port, isSSL, packageName, appName, scopes, callback);
            }
        }).start();
    }

    /**
     * 認証手続きを同期的に行う.
     * Device Connect Managerに対しクライアントの作成依頼、及びアクセストークンの発行依頼を実行する。
     * 処理結果はハンドラに通知される。
     * 
     * @param host Device Connect Managerのホスト名
     * @param port Device Connect Managerのポート番号
     * @param isSSL trueの場合はhttps、falseの場合はhttpで通信する
     * @param packageName Authorization Create Client APIのpackageパラメータの値
     * @param appName Authorization Create Access Token APIのapplicationNameパラメータの値
     * @param scopes Authorization Create Access Token APIのscopeパラメータの値
     * @param callback 処理結果の通知を受けるハンドラ
     */
    public static void authorize(final String host, final int port, final boolean isSSL, final String packageName,
            final String appName,
            final String[] scopes, final AuthorizationHandler callback) {

        if (callback == null) {
            throw new IllegalArgumentException("Callback is null.");
        } else if (scopes == null || scopes.length == 0) {
            throw new IllegalArgumentException("No scopes.");
        } else if (host == null) {
            throw new IllegalArgumentException("Host is null.");
        } else if (packageName == null) {
            throw new IllegalArgumentException("Package name is null.");
        } else if (appName == null) {
            throw new IllegalArgumentException("App name is null.");
        }

        String scheme = null;
        if (isSSL) {
            scheme = "https";
        } else {
            scheme = "http";
        }
        
        URIBuilder builder = new URIBuilder();
        builder.setHost(host);
        builder.setPort(port);
        builder.setScheme(scheme);
        builder.setProfile(AuthorizationProfileConstants.PROFILE_NAME);
        builder.setAttribute(AuthorizationProfileConstants.ATTRIBUTE_CREATE_CLIENT);
        builder.addParameter(AuthorizationProfileConstants.PARAM_PACKAGE, packageName);

        HttpUriRequest request = null;
        try {
            request = new HttpGet(builder.build());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URI. Check parameters.");
        }

        DefaultHttpClient client = new DefaultHttpClient();
        try {
            JSONObject json = execute(client, request);
            ErrorCode error = checkResponse(json);
            if (error == null) {
                String clientId = json.getString(AuthorizationProfileConstants.PARAM_CLIENT_ID);
                String clientSecret = json.getString(AuthorizationProfileConstants.PARAM_CLIENT_SECRET);
                refreshAccessToken(host, port, isSSL, clientId, clientSecret, appName, scopes, callback);
            } else {
                callback.onAuthFailed(error);
            }
        } catch (IllegalArgumentException e) {
            callback.onAuthFailed(ErrorCode.INVALID_REQUEST_PARAMETER);
        } catch (Exception e) {
            callback.onAuthFailed(ErrorCode.UNKNOWN);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    /**
     * アクセストークンのリフレッシュ処理を非同期的に行う.
     * <p>
     * Device Connect Managerに対しびアクセストークンの発行依頼を実行する。
     * 処理結果はハンドラに通知される。
     * </p>
     * @param host Device Connect Managerのホスト名
     * @param port Device Connect Managerのポート番号
     * @param isSSL trueのhttps、falseの場合httpで通信する
     * @param clientId クライアントID
     * @param clientSecret クライアントシークレット
     * @param appName Authorization Create Access Token APIのapplicationNameパラメータの値
     * @param scopes Authorization Create Access Token APIのscopeパラメータの値
     * @param callback 処理結果の通知を受けるハンドラ
     */
    public static void asyncRefreshToken(final String host, final int port, final boolean isSSL, 
            final String clientId, final String clientSecret, final String appName,
            final String[] scopes, final AuthorizationHandler callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                refreshAccessToken(host, port, isSSL, clientId, clientSecret, appName, scopes, callback);
            }
        }).start();
    }

    /**
     * アクセストークンのリフレッシュ処理を行う.
     * <p>
     * Device Connect Managerに対しアクセストークンの発行依頼を実行する。
     * 処理結果はハンドラに通知される。
     * </p>
     * @param host Device Connect Managerのホスト名
     * @param port Device Connect Managerのポート番号
     * @param isSSL trueの場合はhttps、falseの場合はhttpで通信する
     * @param clientId クライアントID
     * @param clientSecret クライアントシークレット
     * @param appName Authorization Create Access Token APIのapplicationNameパラメータの値
     * @param scopes Authorization Create Access Token APIのscopeパラメータの値
     * @param callback 処理結果の通知を受けるハンドラ
     */
    public static void refreshAccessToken(final String host, final int port, final boolean isSSL, 
            final String clientId, final String clientSecret, final String appName,
            final String[] scopes, final AuthorizationHandler callback) {

        // 引数チェック
        if (callback == null) {
            throw new IllegalArgumentException("Callback is null.");
        } else if (scopes == null || scopes.length == 0) {
            throw new IllegalArgumentException("No scopes.");
        } else if (host == null) {
            throw new IllegalArgumentException("Host is null.");
        } else if (appName == null) {
            throw new IllegalArgumentException("App name is null.");
        } else if (clientId == null) {
            throw new IllegalArgumentException("Client ID is null.");
        } else if (clientSecret == null) {
            throw new IllegalArgumentException("Client Secret is null.");
        }

        // シグネチャー作成
        String signature = AuthSignature.generateSignature(clientId,
                AuthorizationProfileConstants.GrantType.AUTHORIZATION_CODE.getValue(), null, scopes,
                clientSecret);

        String scheme = null;
        if (isSSL) {
            scheme = "https";
        } else {
            scheme = "http";
        }

        // アクセストークンの取得処理
        URIBuilder builder = new URIBuilder();
        builder.setHost(host);
        builder.setPort(port);
        builder.setScheme(scheme);
        builder.setProfile(AuthorizationProfileConstants.PROFILE_NAME);
        builder.setAttribute(AuthorizationProfileConstants.ATTRIBUTE_REQUEST_ACCESS_TOKEN);
        builder.addParameter(AuthorizationProfileConstants.PARAM_CLIENT_ID, clientId);
        builder.addParameter(AuthorizationProfileConstants.PARAM_SCOPE, combineStr(scopes));
        builder.addParameter(AuthorizationProfileConstants.PARAM_GRANT_TYPE,
                AuthorizationProfileConstants.GrantType.AUTHORIZATION_CODE.getValue());
        builder.addParameter(AuthorizationProfileConstants.PARAM_SIGNATURE, signature);
        builder.addParameter(AuthorizationProfileConstants.PARAM_APPLICATION_NAME, appName);

        HttpUriRequest request = null;
        try {
            request = new HttpGet(builder.build());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URI. Check parameters.");
        }
        
        DefaultHttpClient client = new DefaultHttpClient();
        try {
            JSONObject json = execute(client, request);
            ErrorCode error = checkResponse(json);
            if (error == null) {
                String accessToken = json.getString(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN);
                callback.onAuthorized(clientId, clientSecret, accessToken);
            } else {
                callback.onAuthFailed(error);
            }
        } catch (IOException e) {
            callback.onAuthFailed(ErrorCode.UNKNOWN);
        } catch (JSONException e) {
            callback.onAuthFailed(ErrorCode.UNKNOWN);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    /**
     * スコープを一つの文字列に連結する.
     * 
     * @param scopes スコープ一覧
     * @return 連結された文字列
     */
    private static String combineStr(final String[] scopes) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < scopes.length; i++) {
            if (i > 0) {
                builder.append(",");
            }
            builder.append(scopes[i].trim());
        }
        return builder.toString();
    }

    /**
     * リクエストを実行し、レスポンスのJSONを返す.
     * 
     * @param client リクエスト実行クライアント
     * @param request リクエスト
     * @return レスポンスデータ。エラーの場合はnullを返す。
     * @throws IOException 文字列変換に失敗した場合にスローされる
     * @throws JSONException JSONのパースに失敗した場合スローされる
     */
    private static JSONObject execute(final HttpClient client, final HttpUriRequest request)
            throws IOException, JSONException {

        HttpResponse response = client.execute(request);
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            return null;
        }

        String entity = EntityUtils.toString(response.getEntity(), "UTF-8");
        JSONObject json = new JSONObject(entity);
        return json;
    }

    /**
     * JSONをチェックし、エラーがあればエラーコードを返す.
     * 
     * @param json レスポンスデータのJSON
     * @return エラーがあればErrorCodeのインスタンスを返す。無い場合はnullを返す。
     * @throws JSONException JSONの操作に失敗した場合スローされる
     */
    private static ErrorCode checkResponse(final JSONObject json) throws JSONException {

        if (json == null) {
            return ErrorCode.UNKNOWN;
        }

        int result = json.getInt(DConnectMessage.EXTRA_RESULT);
        if (result != DConnectMessage.RESULT_OK) {
            int errorCode = json.getInt(DConnectMessage.EXTRA_ERROR_CODE);
            return ErrorCode.getInstance(errorCode);
        }

        return null;

    }

    /**
     * 認証処理ハンドラ.
     */
    public interface AuthorizationHandler {
        
        /**
         * 認証処理の結果を通知する.
         * 
         * @param clientId 認証処理によって生成されたLocalOAuth用のクライアントID
         * @param clientSecret 認証処理によって生成されたLocalOAuth用のクライアントシークレット
         * @param accessToken 認証処理によって生成されたLocalOAuth用のアクセストークン
         */
        void onAuthorized(String clientId, String clientSecret, String accessToken);

        /**
         * 認証処理の途中でエラーが発生したことを通知する.
         * 
         * @param error 発生したエラー
         */
        void onAuthFailed(DConnectMessage.ErrorCode error);
    }
}
