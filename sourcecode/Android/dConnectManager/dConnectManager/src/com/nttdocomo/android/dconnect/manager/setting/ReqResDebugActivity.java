/*
 ReqResDebugActivity.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.manager.setting;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.AbstractContentBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.nttdocomo.android.dconnect.DConnectSettings;
import com.nttdocomo.android.dconnect.R;
import com.nttdocomo.android.dconnect.cipher.signature.AuthSignature;
import com.nttdocomo.android.dconnect.manager.profile.AuthorizationProfile;
import com.nttdocomo.android.dconnect.profile.NetworkServiceDiscoveryProfile;
import com.nttdocomo.dconnect.message.DConnectMessage;
import com.nttdocomo.dconnect.message.DConnectMessage.ErrorCode;
import com.nttdocomo.dconnect.profile.AuthorizationProfileConstants;
import com.nttdocomo.dconnect.profile.BatteryProfileConstants;
import com.nttdocomo.dconnect.profile.ConnectProfileConstants;
import com.nttdocomo.dconnect.profile.DeviceOrientationProfileConstants;
import com.nttdocomo.dconnect.profile.FileDescriptorProfileConstants;
import com.nttdocomo.dconnect.profile.FileProfileConstants;
import com.nttdocomo.dconnect.profile.MediaPlayerProfileConstants;
import com.nttdocomo.dconnect.profile.MediaStreamRecordingProfileConstants;
import com.nttdocomo.dconnect.profile.NetworkServiceDiscoveryProfileConstants;
import com.nttdocomo.dconnect.profile.NotificationProfileConstants;
import com.nttdocomo.dconnect.profile.PhoneProfileConstants;
import com.nttdocomo.dconnect.profile.ProximityProfileConstants;
import com.nttdocomo.dconnect.profile.SettingsProfileConstants;
import com.nttdocomo.dconnect.profile.SystemProfileConstants;
import com.nttdocomo.dconnect.profile.VibrationProfileConstants;
import com.nttdocomo.dconnect.utils.AuthProcesser;
import com.nttdocomo.dconnect.utils.AuthProcesser.AuthorizationHandler;
import com.nttdocomo.dconnect.utils.URIBuilder;

/**
 * d-Connect RESTfulAPIのリクエストとレスポンスを見るための画面を持つActivity.
 * @author NTT DOCOMO, INC.
 */
public class ReqResDebugActivity extends Activity implements
        View.OnClickListener, OnItemSelectedListener {
    /**
     * リクエストとレスポンスの内容を表示するためのListView.
     */
    private ArrayAdapter<String> mListAdapter;
    /**
     * Http Methodを持つSpinner.
     */
    private Spinner mHM;
    /**
     * Profileを持つSpinner.
     */
    private Spinner mProf;
    /**
     * Interfacesを持つSpinner.
     */
    private Spinner mInter;
    /**
     * Attributeを持つSpinner.
     */
    private Spinner mAttrib;
    /**
     * deviceIdを持つSpineer.
     */
    private Spinner mDI;

    /** WebsocketのOn/Offを行うボタン. */
    private ToggleButton mWebsocketBtn;

    /** パラメータを書き込むテキスト. */
    private EditText mTextPath;

    /**
     * Spinnerを選択するか、URLを入力するかを選択するCheckBox.
     */
    private CheckBox mSelectUrl;
    /**
     * WebSocketのクライアント.
     */
    private WebSocketClient mWebsocketClient;

    /** プログレスバー. */
    private ProgressDialog mProgressDialog;

    /** dConnectの設定. */
    private DConnectSettings mSettings;

    /** SharedPrefernceの保存する名前. */
    private static final String PREF_KEY = "debug_localoauth.txt";
    /** クライアントIDのキー. */
    private static final String KEY_CLIENT_ID = "client_id";
    /** クライアントシークレットのキー. */
    private static final String KEY_CLIENT_SECRET = "client_secret";
    /** アクセストークンのキー. */
    private static final String KEY_ACCESS_TOKEN = "access_token";

    /** SharedPreferencesのインスタンス. */
    private SharedPreferences mPref;
    /** SharedPreferencesのインスタンス. */
    private SharedPreferences.Editor mEditor;

    /**
     * 許可を取得するスコープ一覧.
     */
    private String[] mScopes = {
        AuthorizationProfileConstants.PROFILE_NAME,
        BatteryProfileConstants.PROFILE_NAME,
        ConnectProfileConstants.PROFILE_NAME,
        DeviceOrientationProfileConstants.PROFILE_NAME,
        FileDescriptorProfileConstants.PROFILE_NAME,
        FileProfileConstants.PROFILE_NAME,
        MediaPlayerProfileConstants.PROFILE_NAME,
        MediaStreamRecordingProfileConstants.PROFILE_NAME,
        NetworkServiceDiscoveryProfileConstants.PROFILE_NAME,
        NotificationProfileConstants.PROFILE_NAME,
        PhoneProfileConstants.PROFILE_NAME,
        ProximityProfileConstants.PROFILE_NAME,
        SettingsProfileConstants.PROFILE_NAME,
        SystemProfileConstants.PROFILE_NAME,
        VibrationProfileConstants.PROFILE_NAME,

        // 独自プロファイル
        "light",
        "camera",
        "temperature",
        "dice",
        "sphero",
        "drive_controller",
        "remote_controller",
        "mhealth",

        // テスト用
        "*"
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPref = getSharedPreferences(PREF_KEY, Activity.MODE_PRIVATE);
        mEditor = mPref.edit();
        getActionBar().setTitle(R.string.app_name);
        setContentView(R.layout.activity_debug_main);
        findViewById(R.id.reqSend).setOnClickListener(this);
        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // websocketの再開などの処理が必要になる

        // DConnect設定
        mSettings = DConnectSettings.getInstance();
        mSettings.load(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // websocketの停止などの処理が必要になる
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.activity_debug_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_search) {
            mListAdapter.clear();
            executeNetworkServiceDiscovery();
        } else if (id == R.id.action_access_token) {
            String clientId = mPref.getString(KEY_CLIENT_ID, null);
            String clientSecret = mPref.getString(KEY_CLIENT_SECRET, null);
            if (clientId == null || clientSecret == null) {
                checkAuthorization(null);
            } else {
                requestAccessToken(clientId, clientSecret, null);
            }
        } else if (id == R.id.action_session_key) {
            inputSessionKey();
        }
        return true;
    }

    @Override
    public void onClick(final View v) {
        mListAdapter.clear();

        int id = v.getId();
        if (id == R.id.reqSend) {
            executeHttpRequest();
        } else if (id == R.id.reqWesocket) {
            if (mWebsocketBtn.isChecked()) {
                execWebsocket();
            } else {
                closeWebsocket();
            }
        }
    }

    /**
     * UIの初期化.
     */
    private void initUI() {
        // リスト用のアダプタをインスタンス化
        mListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        // リスト設定
        ListView listView = (ListView) findViewById(R.id.reqResList);
        listView.setAdapter(mListAdapter);
        initSpinner();

        // パラメータを入力するテキスト
        mTextPath = (EditText) findViewById(R.id.reqPath);

        // URL選択
        mSelectUrl = (CheckBox) findViewById(R.id.isUrl);
        mSelectUrl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, 
                    final boolean isChecked) {
                enableSetting(buttonView.isChecked());
            }
        });

        mWebsocketBtn = (ToggleButton) findViewById(R.id.reqWesocket);
        mWebsocketBtn.setOnClickListener(this);
    }

    /**
     * Spinnerの初期化.
     */
    private void initSpinner() {
        ArrayAdapter<CharSequence> adapterHttpMethod = ArrayAdapter.createFromResource(
                        this, R.array.http_methods, android.R.layout.simple_spinner_item);
        adapterHttpMethod.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> adapterProfile = ArrayAdapter.createFromResource(
                this, R.array.dconnect_profile_list, android.R.layout.simple_spinner_item);
        adapterProfile.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.string_empty, android.R.layout.simple_spinner_item);
        mHM = (Spinner) findViewById(R.id.reqMethod);
        mHM.setAdapter(adapterHttpMethod);
        mHM.setOnItemSelectedListener(this);
        mProf = (Spinner) findViewById(R.id.reqProfile);
        mProf.setAdapter(adapterProfile);
        mProf.setOnItemSelectedListener(this);
        mInter = (Spinner) findViewById(R.id.reqInterfaces);
        mInter.setOnItemSelectedListener(this);
        mInter.setAdapter(adapter);
        mAttrib = (Spinner) findViewById(R.id.reqAttribute);
        mAttrib.setOnItemSelectedListener(this);
        mAttrib.setAdapter(adapter);
        mDI = (Spinner) findViewById(R.id.reqDeviceid);
        mDI.setOnItemSelectedListener(this);
        mDI.setAdapter(adapter);
    }

    /**
     * profile,interfaces,attribute設定UIを有効にする.
     * @param enabled trueの場合は有効にする、それ以外は無効にする
     */
    private void enableSetting(final boolean enabled) {
        mProf.setEnabled(enabled);
        mInter.setEnabled(enabled);
        mAttrib.setEnabled(enabled);
        if (enabled) {
            mTextPath.setHint(R.string.url_hint1);
        } else {
            mTextPath.setHint(R.string.url_hint2);
        }
    }

    /**
     * エラーダイアログを表示する.
     */
    private void openErrorDialog() { 
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("認証エラー");
        builder.setMessage("認証がされていません。");
        builder.create().show();
    }

    /**
     * セッションキーをテキストボックスに追加.
     */
    private void inputSessionKey() {
        String clientId = mPref.getString(KEY_CLIENT_ID, null);
        if (clientId != null) {
            mTextPath.setText("sessionKey=" + clientId);
        }
    }

    /**
     * WebSocketクライアントの実行.
     */
    private void execWebsocket() {
        // クライアントIDが作成されていない場合はエラー
        final String clientId = mPref.getString(KEY_CLIENT_ID, null);
        if (clientId == null) {
            openErrorDialog();
            return;
        }

        if (mWebsocketClient != null && mWebsocketClient.isConnecting()) {
            mWebsocketClient.close();
            mWebsocketClient = null;
        }

        StringBuilder sb = new StringBuilder();
        if (mSettings.isSSL()) {
            sb.append("wss://");
        } else {
            sb.append("ws://");
        }
        sb.append(mSettings.getHost());
        sb.append(":");
        sb.append(mSettings.getPort());
        sb.append("/websocket");

        URI uri = URI.create(sb.toString());
        mWebsocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(final ServerHandshake handshake) {
                try {
                    JSONObject root = new JSONObject();
                    root.put(DConnectMessage.EXTRA_SESSION_KEY, clientId);
                    mWebsocketClient.send(root.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mListAdapter.add("Websocket open:\n");
                            mListAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (JSONException e) {
                    return; // do nothing.
                }
            }
            @Override
            public void onMessage(final String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mListAdapter.add("Event:\n" + message);
                        mListAdapter.notifyDataSetChanged();
                    }
                });
            }
            @Override
            public void onError(final Exception ex) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mListAdapter.add("Websocket ERROR:\n" + ex.getMessage());
                        mListAdapter.notifyDataSetChanged();
                        mWebsocketBtn.setChecked(false);
                    }
                });
            }
            @Override
            public void onClose(final int code, final String reason, final boolean remote) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mListAdapter.add("Websocket close:\n");
                        mListAdapter.notifyDataSetChanged();
                        mWebsocketBtn.setChecked(false);
                    }
                });
            }
        };
        mWebsocketClient.connect();
    }

    /**
     * WebSocketを閉じる.
     */
    private void closeWebsocket() {
        if (mWebsocketClient != null) {
            mWebsocketClient.close();
            mWebsocketClient = null;
        }
    }

    /**
     * テキストがnull、もしくは空文字かをチェックする.
     * @param text チェックする文字列
     * @return nullもしくは空文字の場合はtrue、それ以外はfalse
     */
    private boolean isEmpty(final String text) {
        return (text == null || text.trim().isEmpty() || text.trim().equals(""));
    }

    /**
     * 指定されたqueryを作成する.
     * @return query一覧
     */
    private List<NameValuePair> createQuery() {
        String deviceId = mDI.getSelectedItem().toString();
        String path = mTextPath.getText().toString();

        List<NameValuePair> params = new ArrayList<NameValuePair>();

        // deviceidが指定されている場合は追加
        if (!isEmpty(deviceId)) {
            params.add(new BasicNameValuePair(DConnectMessage.EXTRA_DEVICE_ID, deviceId));
        }

        // アクセストークンを設定
        if (mSettings.isUseALocalOAuth()) {
            String accessToken = mPref.getString(KEY_ACCESS_TOKEN, null);
            if (accessToken != null) {
                params.add(new BasicNameValuePair(DConnectMessage.EXTRA_ACCESS_TOKEN, accessToken));
            }
        }

        if (!isEmpty(path)) {
            String[] keyvalues = path.split("&");
            for (int i = 0; i < keyvalues.length; i++) {
                String[] kv = keyvalues[i].split("=");
                if (kv.length == 1) {
                    params.add(new BasicNameValuePair(kv[0], ""));
                } else if (kv.length == 2) {
                    params.add(new BasicNameValuePair(kv[0], kv[1]));
                }
            }
        }

        return params;
    }

    /**
     * プログレスバーを表示する.
     * 既に表示されている場合には無視する。
     * @return 表示された場合にはtrue、それ以外の場合はfalse
     */
    private synchronized boolean showProgressDialog() {
        if (mProgressDialog != null) {
            return false;
        }
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("dConnectと通信中");
        mProgressDialog.setMessage("少しお待ちください.");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        return true;
    }

    /**
     * プログレスバーを非表示にする.
     */
    private synchronized void hideProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    /**
     * LocalOAuthの認可を行う.
     * @param listener 結果を通知するリスナー
     */
    private void authorizateLocalOAuth(final AuthorizationHandler listener) {
        AuthProcesser.asyncAuthorize(mSettings.getHost(), mSettings.getPort(), 
                mSettings.isSSL(), getPackageName(), this.getClass().getSimpleName(),
                mScopes, listener);
    }

    /**
     * Local OAuthの認可チェックを行う.
     * クライアントが作成されていない場合には、作成を行う.
     * @param run 作成後に実行されるメソッド
     * @return 認可チェックが行われた場合はtrue、それ以外はfalse
     */
    private boolean checkAuthorization(final Runnable run) {
        String clientId = mPref.getString(KEY_CLIENT_ID, null);
        String clientSecret = mPref.getString(KEY_CLIENT_SECRET, null);
        if (clientId == null || clientSecret == null) {
            authorizateLocalOAuth(new AuthorizationHandler() {
                @Override
                public void onAuthorized(final String clientId, 
                        final String clientSecret, final String accessToken) {
                    mEditor.putString(KEY_CLIENT_ID, clientId);
                    mEditor.putString(KEY_CLIENT_SECRET, clientSecret);
                    mEditor.putString(KEY_ACCESS_TOKEN, accessToken);
                    mEditor.commit();
                    if (run != null) {
                        run.run();
                    }
                }
                @Override
                public void onAuthFailed(final ErrorCode error) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ReqResDebugActivity.this, 
                                    "Failed to get a accessToken.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
            return true;
        }
        return false;
    }

    /**
     * アクセストークンを取得する.
     * @param clientId クライアントID
     * @param clientSecret クライアントシークレット
     * @param listener リスナー
     */
    private void requestAccessToken(final String clientId, 
            final String clientSecret, final AccessTokenListener listener) {
        String signature = createSignature(clientId, mScopes, clientSecret);

        URIBuilder builder = createURIBuilder();
        builder.setProfile(AuthorizationProfileConstants.PROFILE_NAME);
        builder.setAttribute(AuthorizationProfileConstants.ATTRIBUTE_REQUEST_ACCESS_TOKEN);
        builder.addParameter(AuthorizationProfileConstants.PARAM_CLIENT_ID, clientId);
        builder.addParameter(AuthorizationProfileConstants.PARAM_SCOPE, combineStr(mScopes));
        builder.addParameter(AuthorizationProfileConstants.PARAM_APPLICATION_NAME, getPackageName());
        builder.addParameter(AuthorizationProfileConstants.PARAM_GRANT_TYPE,
                AuthorizationProfileConstants.GrantType.AUTHORIZATION_CODE.getValue());
        builder.addParameter(AuthorizationProfile.PARAM_SIGNATURE, signature);

        executeHttpRequest(HttpGet.METHOD_NAME, builder, new HttpListener() {
            @Override
            public void onReceivedResponse(final String response) {
                try {
                    parseRequestAccessToken(new JSONObject(response), listener);
                } catch (JSONException e) {
                    if (listener != null) {
                        listener.onReceivedError();
                    }
                }
            }
        });
    }

    /**
     * アクセストークンのレスポンスを解析する.
     * @param root レスポンスのJSON
     * @param listener リスナー
     * @throws JSONException JSONの解析に失敗した場合に発生
     */
    private void parseRequestAccessToken(final JSONObject root, final AccessTokenListener listener)
            throws JSONException {
        int result = root.getInt(DConnectMessage.EXTRA_RESULT);
        if (result != DConnectMessage.RESULT_OK) {
            if (listener != null) {
                listener.onReceivedError();
            }
            return;
        }
        String accessToken = root.getString(AuthorizationProfileConstants.PARAM_ACCESS_TOKEN);
        String signature = root.getString(AuthorizationProfileConstants.PARAM_SIGNATURE);
        if (checkSignature(signature, accessToken, mPref.getString(KEY_CLIENT_SECRET, null))) {
            if (listener != null) {
                listener.onReceivedAccessToken(accessToken);
            }
            mEditor.putString(KEY_ACCESS_TOKEN, accessToken);
            mEditor.commit();
        } else {
            if (listener != null) {
                listener.onReceivedError();
            }
        }
    }

    /**
     * 送られてきたシグネイチャをチェックする.
     * @param signature シグネイチャ
     * @param accessToken アクセストークン
     * @param clinetSecret クライアントシークレット
     * @return シグネイチャが有効の場合はtrue、それ以外はfalse
     */
    private boolean checkSignature(final String signature, final String accessToken, final String clinetSecret) {
        if (signature == null || accessToken == null) {
            return false;
        }
        try {
            String sig = AuthSignature.generateSignature(accessToken, clinetSecret);
            return signature.equals(sig);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * accessTokenをリクエストするためのシグネイチャを作成する.
     * @param clientId クライアントID
     * @param scopes スコープ
     * @param clientSecret クライアントシークレット
     * @return シグネイチャ
     */
    private String createSignature(final String clientId, final String[] scopes, final String clientSecret) {
        String signature = null;
        try {
            signature = AuthSignature.generateSignature(clientId,
                    AuthorizationProfileConstants.GrantType.AUTHORIZATION_CODE.getValue(), 
                    null, scopes, clientSecret);
            signature = URLEncoder.encode(signature, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
        return signature;
    }

    /**
     * スコープを一つの文字列に連結する.
     * @param scopes スコープ一覧
     * @return 連結された文字列
     */
    private String combineStr(final String[] scopes) {
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
     * Network Service Discoveryを実行する.
     * 実行した結果は、解析してデバイスID一覧をリストに追加する。
     */
    private void executeNetworkServiceDiscovery() {
        if (mSettings.isUseALocalOAuth()) {
            boolean result = checkAuthorization(new Runnable() {
                @Override
                public void run() {
                    executeNetworkServiceDiscovery();
                }
            });
            if (result) {
                return;
            }
        }

        URIBuilder builder = createURIBuilder();
        builder.setProfile(NetworkServiceDiscoveryProfile.PROFILE_NAME);
        builder.setAttribute(NetworkServiceDiscoveryProfile.ATTRIBUTE_GET_NETWORK_SERVICES);
        if (mSettings.isUseALocalOAuth()) {
            String accessToken = mPref.getString(KEY_ACCESS_TOKEN, null);
            builder.addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, accessToken);
        }

        URI uri = null;
        try {
            uri = builder.build();
        } catch (URISyntaxException e) {
            // URIの作成に失敗
            return;
        }

        executeHttpRequest(HttpGet.METHOD_NAME, uri, new HttpListener() {
            @Override
            public void onReceivedResponse(final String response) {
                parseNetworkServieDiscovery(response);
            }
        });
    }

    /**
     * UIに設定してある項目からHTTPリクエストを作成して実行する.
     */
    private void executeHttpRequest() {
        if (mSettings.isUseALocalOAuth()) {
            boolean result = checkAuthorization(new Runnable() {
                @Override
                public void run() {
                    executeHttpRequest();
                }
            });
            if (result) {
                return;
            }
        }

        URIBuilder builder = createURIBuilder();
        if (!mSelectUrl.isChecked()) {
            builder.setPath(mTextPath.getText().toString());
        } else {
            String profile = mProf.getSelectedItem().toString();
            String inter = mInter.getSelectedItem().toString();
            String attr = mAttrib.getSelectedItem().toString();
            if (!isEmpty(profile)) {
                builder.setProfile(profile);
            }
            if (!isEmpty(inter)) {
                builder.setInterface(inter);
            }
            if (!isEmpty(attr)) {
                builder.setAttribute(attr);
            }
            builder.setParameters(createQuery());
        }

        String method = mHM.getSelectedItem().toString();
        URI uri = null;
        try {
            uri = builder.build();
        } catch (URISyntaxException e) {
            // URIの作成に失敗
            return;
        }
        executeHttpRequest(method, uri, null);
    }

    /**
     * 共通で使用するURIBuilderを作成する.
     * @return URIBuilderのインスタンス
     */
    private URIBuilder createURIBuilder() {
        URIBuilder builder = new URIBuilder();
        if (mSettings.isSSL()) {
            builder.setScheme("https");
        } else {
            builder.setScheme("http");
        }
        builder.setHost(mSettings.getHost());
        builder.setPort(mSettings.getPort());
        return builder;
    }

    /**
     * Httpリクエストを実行する.
     * @param method HTTPメソッド
     * @param builder 実行するURI
     * @param listener Httpからのレスポンスを通知するリスナー
     */
    private void executeHttpRequest(final String method, final URIBuilder builder, final HttpListener listener) {
        URI uri = null;
        try {
            uri = builder.build();
        } catch (URISyntaxException e) {
            // URIの作成に失敗
            return;
        }
        executeHttpRequest(method, uri, listener);
    }

    /**
     * Httpリクエストを実行する.
     * @param method HTTPメソッド
     * @param uri 実行するURI
     * @param listener Httpからのレスポンスを通知するリスナー
     */
    private void executeHttpRequest(final String method, final URI uri, final HttpListener listener) {
        HttpUriRequest req = null;
        if (HttpGet.METHOD_NAME.equals(method)) {
            req = new HttpGet(uri.toString());
        } else if (HttpPost.METHOD_NAME.equals(method)) {
            req = new HttpPost(uri.toString());
        } else if (HttpPut.METHOD_NAME.equals(method)) {
            req = new HttpPut(uri.toString());
        } else if (HttpDelete.METHOD_NAME.equals(method)) {
            req = new HttpDelete(uri.toString());
        } else if ("マルチパート".equals(method)) {
            AssetManager manager = getAssets();
            try {
                // TODO ファイルは固定で問題ないか？
                String name = "test.png";

                MultipartEntity entity = new MultipartEntity();
                InputStream in = manager.open(name);
                // ファイルのデータを読み込む
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int len;
                byte[] buf = new byte[4096];
                while ((len = in.read(buf)) > 0) {
                    baos.write(buf, 0, len);
                }
                // ボディにデータを追加
                entity.addPart(FileProfileConstants.PARAM_DATA, new BinaryBody(baos.toByteArray(), name));

                req = new HttpPost(uri.toString());
                ((HttpPost) req).setEntity(entity);
            } catch (UnsupportedEncodingException e) {
                return;
            } catch (IOException e) {
                return;
            }
        } else {
            return;
        }

        if (req != null) {
            executeHttpRequest(req, listener);
        }
    }
    /**
     * Httpリクエストを実行する.
     * @param request リクエスト
     * @param listener Httpからのレスポンスを通知するリスナー
     */
    private void executeHttpRequest(final HttpUriRequest request, final HttpListener listener) {
        if (!showProgressDialog()) {
            return;
        }
        AsyncTask<HttpUriRequest, HttpUriRequest, String> task = 
                new AsyncTask<HttpUriRequest, HttpUriRequest, String>() {
            @Override
            protected String doInBackground(final HttpUriRequest... params) {
                if (params == null || params.length < 1) {
                    return "Illegal Parameter.";
                }

                HttpUriRequest request = params[0];
                DefaultHttpClient client = new DefaultHttpClient();
                try {
                    HttpResponse response = client.execute(request);
                    switch (response.getStatusLine().getStatusCode()) {
                    case HttpStatus.SC_OK:
                        try {
                            return EntityUtils.toString(response.getEntity(), "UTF-8");
                        } catch (ParseException e) {
                            return e.getMessage();
                        } catch (IOException e) {
                            return e.getMessage();
                        }
                    case HttpStatus.SC_NOT_FOUND:
                        return "Not found. 404";
                    default:
                        return "Http connect error.";
                    }
                } catch (ClientProtocolException e) {
                    return e.getMessage();
                } catch (IOException e) {
                    return e.getMessage();
                } finally {
                    client.getConnectionManager().shutdown();
                }
            }
            @Override
            protected void onPostExecute(final String response) {
                super.onPostExecute(response);

                hideProgressDialog();

                if (response == null) {
                    return;
                }

                StringBuilder sb = new StringBuilder();
                sb.append("Request:\n");
                sb.append(request.getMethod() + " " + request.getURI() + "\n");
                mListAdapter.add(sb.toString());
                mListAdapter.add("Response:\n" + response);
                mListAdapter.notifyDataSetChanged();
                if (listener != null) {
                    listener.onReceivedResponse(response);
                }
            }
        };
        task.execute(request);
    }

    /**
     * Network Service Discoveryの返り値を解析する.
     * @param res 返り値のJSON
     */
    private void parseNetworkServieDiscovery(final String res) {
        try {
            JSONObject root = new JSONObject(res);
            JSONArray services = root.getJSONArray(
                    NetworkServiceDiscoveryProfile.PARAM_SERVICES);
            if (services == null) {
                return;
            }
            String[] data = new String[services.length() + 1];
            data[0] = " "; // 最初は、空文字にしておく
            for (int i = 1; i < services.length() + 1; i++) {
                data[i] = services.getJSONObject(i - 1)
                        .getString(NetworkServiceDiscoveryProfile.PARAM_ID);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_dropdown_item, data);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mDI.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemSelected(final AdapterView<?> parent, final View v,
            final int position, final long pos) {
        int id = parent.getId();
        Spinner spinner = (Spinner) parent;
        if (id == R.id.reqProfile) {
            String profileName = spinner.getSelectedItem().toString();
            if (!profileName.equals("")) {
                int resInterfacesId = getResources()
                        .getIdentifier(profileName + "_interface_list", "array", getPackageName());
                ArrayAdapter<CharSequence> adapterInterfaces = ArrayAdapter.createFromResource(
                        this, resInterfacesId, android.R.layout.simple_spinner_item);
                adapterInterfaces.setDropDownViewResource(
                        android.R.layout.simple_spinner_dropdown_item);
                mInter.setAdapter(adapterInterfaces);
                int resAttributeId = getResources()
                        .getIdentifier(profileName + "_attrib_list", "array", getPackageName());
                ArrayAdapter<CharSequence> adapterAttribute = ArrayAdapter.createFromResource(
                        this, resAttributeId, android.R.layout.simple_spinner_item);
                adapterInterfaces.setDropDownViewResource(
                         android.R.layout.simple_spinner_dropdown_item);
                mAttrib.setAdapter(adapterAttribute);
            }
        }
    }
    @Override
    public void onNothingSelected(final AdapterView<?> parent) {
    }

    /**
     * HTTPレスポンスを通知するためのリスナ.
     * @author NTT DOCOMO, INC.
     */
    private interface HttpListener {
        /**
         * HTTPレスポンスを通知する.
         * @param response HTTPレスポンス
         */
        void onReceivedResponse(final String response);
    }
    /**
     * アクセストークン取得を通知するリスナー.
     * @author NTT DOCOMO, INC.
     */
    private interface AccessTokenListener {
        /**
         * アクセストークンを取得したときに通知される.
         * @param accessToken アクセストークン
         */
        void onReceivedAccessToken(final String accessToken);
        /**
         * アクセストークンの取得中にエラーが発生した場合に通知される.
         */
        void onReceivedError();
    }
    /**
     * バイナリを送信するためのボディクラス.
     * @author NTT DOCOMO, INC.
     */
    protected class BinaryBody extends AbstractContentBody {
        /** ファイル名. */
        private String mFileName;
        /** 送信するバイナリデータ. */
        private byte[] mBuffer;

        /**
         * コンストラクタ.
         * @param buf バッファ
         * @param fileName ファイル名
         */
        public BinaryBody(final byte[] buf, final String fileName) {
            super(fileName);
            mBuffer = buf;
            mFileName = fileName;
        }

        @Override
        public String getFilename() {
            return mFileName;
        }

        @Override
        public String getCharset() {
            return "UTF-8";
        }

        @Override
        public long getContentLength() {
            return mBuffer.length;
        }

        @Override
        public String getTransferEncoding() {
            return "UTF-8";
        }

        @Override
        public void writeTo(final OutputStream out) throws IOException {
            out.write(mBuffer);
        }
    }
}
