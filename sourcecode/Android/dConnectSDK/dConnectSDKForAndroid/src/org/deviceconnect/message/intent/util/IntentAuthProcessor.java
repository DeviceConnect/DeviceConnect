/*
 IntentAuthProcessor.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.message.intent.util;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.deviceconnect.android.cipher.signature.AuthSignature;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.message.HttpHeaders;
import org.deviceconnect.message.DConnectMessage.ErrorCode;
import org.deviceconnect.message.intent.impl.client.DefaultIntentClient;
import org.deviceconnect.message.intent.params.IntentConnectionParams;
import org.deviceconnect.profile.AuthorizationProfileConstants;
import org.deviceconnect.utils.URIBuilder;
import org.deviceconnect.utils.AuthProcesser.AuthorizationHandler;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ComponentName;
import android.content.Context;

/**
 * LocalOAuthの手続きユーティリティクラス. Intent通信による手続きを行う。
 * 
 * <h3>サンプルコード</h3>
 * <h4>非同期での認証処理</h4>
 * <pre>
 * {@code
 * IntentAuthProcessor.asyncAuthorize(this, 
 *              ComponentName.unflattenFromString("org.deviceconnect.manager/.DConnectBroadcastReceiver"), 
 *              getPackageName(), 
 *              "Auth Example App", 
 *              new String[] {"battery", "system", "network_service_discovery"}, 
 *              new AuthorizationHandler() {
 *          
 *          public void onAuthorized(String clientId, String clientSecret, String accessToken) {
 *              // 認証完了時の処理
 *          }
 *          
 *          public void onAuthFailed(ErrorCode error) {
 *              // 認証失敗時の処理
 *          }
 *      });
 * }
 * </pre>
 * 
 * @author NTT DOCOMO, INC.
 */
public final class IntentAuthProcessor {

    /**
     * ユーティリティのためprivate.
     */
    private IntentAuthProcessor() {
    }

    /**
     * 認証手続きを行う. Device Connect Managerに対しクライアントの作成依頼、及びアクセストークンの発行依頼を実行する。
     * Device Connect ManagerとはIntentによる通信を行う。<br/>
     * 通信処理は非同期で行われる。<br/>
     * 当メソッドを利用するには AndroidManifest.xmlへの
     * {@link org.deviceconnect.message.intent.impl.io.IntentResponseReceiver}
     * の登録が必要となる。
     * 
     * @param context コンテキストオブジェクト
     * @param dConnectComponent Device Connect Managerのコンポーネントネーム
     * @param packageName Authorization Create Client APIのpackageパラメータの値
     * @param appName Authorization Create Access Token
     *            APIのapplicationNameパラメータの値
     * @param scopes Authorization Create Access Token APIのscopeパラメータの値
     * @param callback 処理結果の通知を受けるハンドラ
     */
    public static void asyncAuthorize(final Context context, final ComponentName dConnectComponent,
            final String packageName, final String appName, final String[] scopes,
            final AuthorizationHandler callback) {
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                authorize(context, dConnectComponent, packageName, appName, scopes, callback);
            }
        }).start();
        
    }
    
    /**
     * 認証手続きを行う. Device Connect Managerに対しクライアントの作成依頼、及びアクセストークンの発行依頼を実行する。
     * Device Connect ManagerとはIntentによる通信を行う。<br/>
     * 通信処理は同期で行われる。Intent通信を妨げてしまうため、UIスレッドで呼び出しては<strong>いけない</strong>。<br/>
     * 当メソッドを利用するには AndroidManifest.xmlへの
     * {@link org.deviceconnect.message.intent.impl.io.IntentResponseReceiver}
     * の登録が必要となる。
     * 
     * @param context コンテキストオブジェクト
     * @param componentName Device Connect Managerのコンポーネントネーム
     * @param packageName Authorization Create Client APIのpackageパラメータの値
     * @param appName Authorization Create Access Token
     *            APIのapplicationNameパラメータの値
     * @param scopes Authorization Create Access Token APIのscopeパラメータの値
     * @param callback 処理結果の通知を受けるハンドラ
     */
    public static void authorize(final Context context, final ComponentName componentName,
            final String packageName, final String appName, final String[] scopes,
            final AuthorizationHandler callback) {
        
        if (callback == null) {
            throw new IllegalArgumentException("Callback is null.");
        } else if (scopes == null || scopes.length == 0) {
            throw new IllegalArgumentException("No scopes.");
        } else if (componentName == null) {
            throw new IllegalArgumentException("componentName is null.");
        } else if (packageName == null) {
            throw new IllegalArgumentException("Package name is null.");
        } else if (appName == null) {
            throw new IllegalArgumentException("App name is null.");
        }

        if (Thread.currentThread().equals(context.getMainLooper().getThread())) {
            throw new IllegalStateException("DON'T CALL this method in UI thread.");
        }
        
        URIBuilder builder = new URIBuilder();
        // 送り先はComponentNameで設定するため、以下のデータはエラーにならないようにするためのダミーデータを入れておく。
        builder.setHost("localhost");
        builder.setPort(0);
        builder.setScheme("http");

        builder.setProfile(AuthorizationProfileConstants.PROFILE_NAME);
        builder.setAttribute(AuthorizationProfileConstants.ATTRIBUTE_CREATE_CLIENT);
        builder.addParameter(AuthorizationProfileConstants.PARAM_PACKAGE, packageName);

        HttpUriRequest request = null;
        try {
            request = new HttpGet(builder.build());
        } catch (URISyntaxException e1) {
            throw new IllegalArgumentException("Invalid Param. Check parameters.");
        }
        // DefaultIntentClientの仕様として、Hostで行き先を指定しているのでコンポーネント名を設定しておく
        request.setHeader(HttpHeaders.HOST, componentName.flattenToShortString());

        ErrorCode error = null;
        HttpParams params = new BasicHttpParams();
        IntentConnectionParams.setContext(params, context);
        IntentConnectionParams.setComponent(params, componentName);
        DefaultIntentClient client = new DefaultIntentClient(params);

        do {
            try {
                JSONObject json = execute(client, request);
                error = checkResponse(json);
                if (error != null) {
                    break;
                }

                String clientId = json.getString(AuthorizationProfileConstants.PARAM_CLIENT_ID);
                String clientSecret = json.getString(AuthorizationProfileConstants.PARAM_CLIENT_SECRET);
                String signature = AuthSignature.generateSignature(clientId,
                        AuthorizationProfileConstants.GrantType.AUTHORIZATION_CODE.getValue(), null, scopes,
                        clientSecret);
                // アクセストークンの取得処理
                builder.setAttribute(AuthorizationProfileConstants.ATTRIBUTE_REQUEST_ACCESS_TOKEN);
                builder.addParameter(AuthorizationProfileConstants.PARAM_CLIENT_ID, clientId);
                builder.addParameter(AuthorizationProfileConstants.PARAM_SCOPE, combineStr(scopes));
                builder.addParameter(AuthorizationProfileConstants.PARAM_GRANT_TYPE,
                        AuthorizationProfileConstants.GrantType.AUTHORIZATION_CODE.getValue());
                builder.addParameter(AuthorizationProfileConstants.PARAM_SIGNATURE, signature);
                builder.addParameter(AuthorizationProfileConstants.PARAM_APPLICATION_NAME, appName);
                try {
                    request = new HttpGet(builder.build());
                } catch (URISyntaxException e1) {
                    error = ErrorCode.UNKNOWN;
                    break;
                }

                // clientを使い回すと警告文がでるので作り直して回避。
                client.getConnectionManager().shutdown();
                client = new DefaultIntentClient(params);
                json = execute(client, request);
                error = checkResponse(json);
                if (error != null) {
                    break;
                }
                String accessToken = json.getString(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN);
                callback.onAuthorized(clientId, clientSecret, accessToken);
            } catch (Exception e) {
                error = ErrorCode.UNKNOWN;
                e.printStackTrace();
                break;
            }
        } while (false);

        client.getConnectionManager().shutdown();

        if (error != null) {
            callback.onAuthFailed(error);
        }

    }

    /**
     * リクエストを実行し、レスポンスのJSONを返す.
     * 
     * @param client リクエスト実行クライアント
     * @param request リクエスト
     * @return レスポンスデータ。エラーの場合はnullを返す。
     * @throws IOException 文字列変換に失敗した場合にスローされる
     */
    private static JSONObject execute(final HttpClient client, final HttpUriRequest request) throws IOException {

        HttpResponse response = client.execute(request);
        String entity = EntityUtils.toString(response.getEntity(), "UTF-8");
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            return null;
        }

        JSONObject json;
        try {
            json = new JSONObject(entity);
        } catch (JSONException e) {
            json = null;
        }
        return json;
    }

    /**
     * JSONをチェックし、エラーがあればエラーコードを返す.
     * 
     * @param json レスポンスデータのJSON
     * @return エラーがあればErrorCodeのインスタンスを返す。無い場合はnullを返す。
     * @throws JSONException json操作でエラーがあった場合スローされる。
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

}
