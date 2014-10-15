/**
 * Copyright 2005-2014 Restlet
 * 
 * The contents of this file are subject to the terms of one of the following
 * open source licenses: Apache 2.0 or LGPL 3.0 or LGPL 2.1 or CDDL 1.0 or EPL
 * 1.0 (the "Licenses"). You can select the license that you prefer but you may
 * not use this file except in compliance with one of these Licenses.
 * 
 * You can obtain a copy of the Apache 2.0 license at
 * http://www.opensource.org/licenses/apache-2.0
 * 
 * You can obtain a copy of the LGPL 3.0 license at
 * http://www.opensource.org/licenses/lgpl-3.0
 * 
 * You can obtain a copy of the LGPL 2.1 license at
 * http://www.opensource.org/licenses/lgpl-2.1
 * 
 * You can obtain a copy of the CDDL 1.0 license at
 * http://www.opensource.org/licenses/cddl1
 * 
 * You can obtain a copy of the EPL 1.0 license at
 * http://www.opensource.org/licenses/eclipse-1.0
 * 
 * See the Licenses for the specific language governing permissions and
 * limitations under the Licenses.
 * 
 * Alternatively, you can obtain a royalty free commercial license with less
 * limitations, transferable or non-transferable, directly at
 * http://www.restlet.com/products/restlet-framework
 * 
 * Restlet is a registered trademark of Restlet
 */

package org.deviceconnect.android.localoauth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import org.deviceconnect.android.cipher.signature.AuthSignature;
import org.deviceconnect.android.localoauth.activity.AccessTokenListActivity;
import org.deviceconnect.android.localoauth.activity.ConfirmAuthActivity;
import org.deviceconnect.android.localoauth.exception.AuthorizatonException;
import org.deviceconnect.android.localoauth.oauthserver.LoginPageServerResource;
import org.deviceconnect.android.localoauth.oauthserver.SampleUser;
import org.deviceconnect.android.localoauth.oauthserver.SampleUserManager;
import org.deviceconnect.android.localoauth.oauthserver.db.LocalOAuthOpenHelper;
import org.deviceconnect.android.localoauth.oauthserver.db.SQLiteClient;
import org.deviceconnect.android.localoauth.oauthserver.db.SQLiteClientManager;
import org.deviceconnect.android.localoauth.oauthserver.db.SQLiteToken;
import org.deviceconnect.android.localoauth.oauthserver.db.SQLiteTokenManager;
import org.deviceconnect.android.localoauth.temp.RedirectRepresentation;
import org.deviceconnect.android.localoauth.temp.ResultRepresentation;
import org.json.JSONException;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ClientInfo;
import org.restlet.data.Cookie;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.ext.oauth.AccessTokenServerResource;
import org.restlet.ext.oauth.AuthPageServerResource;
import org.restlet.ext.oauth.AuthorizationBaseServerResource;
import org.restlet.ext.oauth.AuthorizationServerResource;
import org.restlet.ext.oauth.OAuthException;
import org.restlet.ext.oauth.PackageInfoOAuth;
import org.restlet.ext.oauth.internal.Client;
import org.restlet.ext.oauth.internal.Client.ClientType;
import org.restlet.ext.oauth.internal.ClientManager;
import org.restlet.ext.oauth.internal.Scope;
import org.restlet.ext.oauth.internal.ServerToken;
import org.restlet.ext.oauth.internal.Token;
import org.restlet.ext.oauth.internal.TokenManager;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.util.Series;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Base64;

/**
 * Local OAuth API.
 */
public final class LocalOAuth2Main {

    /** LoginPageActivityでOKボタンが押された後のBroadcast通知. */
    public static final String ACTION_LOGIN = "com.gclue.localoauth.LOGIN";

    /** AuthPageActivityでOKボタンが押された後のBroadcast通知. */
    public static final String ACTION_AUTHPAGE = "com.gclue.localoauth.AUTHPAGE";

    /** Service向けACTION:ログイン時に通知される. */
    public static final String SERVICE_LOGIN = "service_login";

    /** Service向けACTION:認証画面で承認されたときに通知される. */
    public static final String SERVICE_AUTHPAGE = "service_authpage";

    /** EXTRAキー:セッションID. */
    public static final String EXTRA_SESSIONID = "sessionid";

    /** EXTRAキー:ユーザーID. */
    public static final String EXTRA_USERID = "userid";

    /** EXTRAキー:パスワード. */
    public static final String EXTRA_PASSWORD = "password";

    /** EXTRAキー:スコープ. */
    public static final String EXTRA_SCOPES = "scopes";

    /** EXTRAキー:認証コード. */
    public static final String EXTRA_AUTHCODE = "authcode";

    /** EXTRAキー:クライアントID. */
    public static final String EXTRA_CLIENTID = "clientId";

    /** EXTRAキー:グラントタイプ. */
    public static final String EXTRA_GRANTTYPE = "grantType";

    /** EXTRAキー:デバイスID. */
    public static final String EXTRA_DEVICEID = "deviceId";

    /** authorization_code. */
    public static final String AUTHORIZATION_CODE = "authorization_code";

    
    
    /** メッセージID(アクセストークン発行成功通知). */
    public static final int MESSAGE_PUBLISH_ACCESSTOKEN_SUCCESS = 1000;
    
    /** メッセージID(アクセストークン発行中の例外発生通知). */
    public static final int MESSAGE_PUBLISH_ACCESSTOKEN_EXCEPTION = 1001;
    
    /** メッセージID(threadIdがキューに残っているか確認通知). */
    public static final int MESSAGE_CHECK_THREADID_RESULT = 1002;
    
    /** UserManager. */
    private static SampleUserManager userManager;

    /** ClientManager. */
    private static ClientManager clientManager;

    /** TokenManager. */
    private static TokenManager tokenManager;

    /** DBHelper. */
    private static LocalOAuthOpenHelper mDbHelper = null;
    
    /** ロガー. */
    private static Logger sLogger = Logger.getLogger("org.deviceconnect.localoauth");

    /** 自動テストモードフラグ. */
    private static boolean mAutoTestMode = false;
    
    /** DBアクセス用Lockオブジェクト. */
    private static Object mLockForDbAccess = new Object();
    
    
    /**
     * コンストラクタ.
     */
    private LocalOAuth2Main() {
        
    }
    
    
    /**
     * UserManagerを返す.
     * 
     * @return UserManagerのポインタ
     */
    public static SampleUserManager getSampleUserManager() {
        return userManager;
    }

    /**
     * ClientManagerを返す.
     * 
     * @return ClientManagerのポインタ
     */
    public static ClientManager getClientManager() {
        return clientManager;
    }

    /**
     * TokenManagerを返す.
     * 
     * @return TokenManagerのポインタ
     */
    public static TokenManager getTokenManager() {
        return tokenManager;
    }

    /** ダミー値(RedirectURI). */
    public static final String DUMMY_REDIRECTURI = "dummyRedirectURI";

    /** ダミー値(OriginalRef). */
    private static final String DUMMY_ORIGINALREF = "dummyOriginalRef";

    /** ダミー値(Reference). */
    private static final String DUMMY_REFERENCE = "dummyReference";

    /** ダミー値(Scope). */
    private static final String SCOPE1 = "scope1";


    /**
     * (0)LocalOAuth2Serviceで使用されるBinderを返す.<br>
     * ※LocalOAuthのユーザーは利用する必要はない.<br>
     * 
     * @param intent Intent
     * @return Binder
     */
    public static IBinder onBind(final Intent intent) {
        return mMessenger.getBinder();
    }

    /**
     * 自動テストモードフラグ設定(単体テスト用).
     * @param autoTestMode 自動テストモードモードフラグ(true: 有効にする / false: 無効にする) 
     */
    public static void setUseAutoTestMode(final boolean autoTestMode) {
        mAutoTestMode = autoTestMode;
    }

    /**
     * 自動テストモードが有効か判定する.
     * @return true: 有効 / false: 無効 
     */
    public static boolean isAutoTestMode() {
        return mAutoTestMode;
    }

    /**
     * (1)Local OAuthを初期化する.<br>
     * - 変数を初期化する。<br>
     * - ユーザーを1件追加する。
     * @param context   コンテキスト
     */
    public static void initialize(final android.content.Context context) {

        /* DB初期化処理 */
        mDbHelper = new LocalOAuthOpenHelper(context);
        
        /* 初期化処理 */
        userManager = new SampleUserManager();
        clientManager = new SQLiteClientManager();
        tokenManager = new SQLiteTokenManager();

        /* ユーザー追加 */
        addUserData(SampleUser.LOCALOAUTH_USER, SampleUser.LOCALOAUTH_PASS);
    }
    
    /**
     * (1)-2.LocalOAuth終了処理.
     */
    public static void destroy() {
        
        /* DBをまとめてクローズ */
        if (mDbHelper != null) {
            mDbHelper.close();
        }
        
        userManager = null;
        clientManager = null;
        tokenManager = null;
        mDbHelper = null;
    }
    

    /**
     * (2)クライアントを登録する。アプリやデバイスプラグインがインストールされるときに実行する.
     * 
     * @param packageInfo アプリ(Android)の場合は、パッケージ名を入れる。<br>
     *            アプリ(Web)の場合は、パッケージ名にURLを入れる。<br>
     *            デバイスプラグインの場合は、パッケージ名とデバイスIDを入れる。<br>
     * @return 登録したクライアント情報(クライアントID, クライアントシークレット)を返す。<br>
     *         nullは返らない。
     * @throws AuthorizatonException Authorization例外.
     */
    public static ClientData createClient(final PackageInfoOAuth packageInfo) throws AuthorizatonException {

        /* 引数チェック */
        if (packageInfo == null) {
            throw new IllegalArgumentException("packageInfo is null.");
        } else if (packageInfo.getPackageName() == null) {
            throw new IllegalArgumentException("packageInfo.getPackageName() is null.");
        } else if (packageInfo.getPackageName().length() <= 0) {
            throw new IllegalArgumentException("packageInfo is empty.");
        }

        /* 長時間使用されていなかったclientIdをクリーンアップする(DBアクセスしたついでに有効クライアント数も取得する) */
        int clientCount = cleanupClient();
        
        /* クライアント数が上限まで使用されていれば例外発生 */
        if (clientCount >= LocalOAuth2Settings.CLIENT_MAX) {
            throw new AuthorizatonException(AuthorizatonException.CLIENT_COUNTS_IS_FULL);
        }
        
        /* クライアント追加 */
        ClientData clientData = null;
        SQLiteDatabase db = null;
        SQLiteTokenManager sqliteTokenManager = null;
        SQLiteClientManager sqliteClientManager = null;
                
        /* DBを同時アクセスさせない */
        synchronized (mLockForDbAccess) {
            try {
                /* DBオープン */
                db = mDbHelper.getWritableDatabase();
                db.beginTransaction();
                
                /* TokenManagerにDBオブジェクトを設定 */
                sqliteTokenManager = (SQLiteTokenManager) tokenManager;
                sqliteTokenManager.setDb(db);
                
                /* ClientManagerにDBオブジェクトを設定 */
                sqliteClientManager = (SQLiteClientManager) clientManager;
                sqliteClientManager.setDb(db);
                
                /* パッケージ情報に対応するクライアントIDがすでに登録済なら破棄する */
                Client client = getClientManager().findByPackageInfo(packageInfo);
                if (client != null) {
                    String clientId = client.getClientId();
                    
                    /* クライアントデータ削除 */
                    removeClientData(clientId);
                    
                    client = null;
                }
                
                /* クライアントデータを新規生成して返す */
                client = addClientData(packageInfo);
                clientData = new ClientData(client.getClientId(), String.copyValueOf(client.getClientSecret()));
                
                /* コミット */
                db.setTransactionSuccessful();
                
            } catch (SQLiteException e) {
                throw new RuntimeException(e);
            } finally {
                if (db != null) {
                    db.endTransaction();
                    db.close();
                }
                
                /* TokenManagerのDBオブジェクトをクリア設定 */
                if (sqliteTokenManager != null) {
                    sqliteTokenManager.setDb(null);
                }
                
                /* ClientManagerのDBオブジェクトをクリア設定 */
                if (sqliteClientManager != null) {
                    sqliteClientManager.setDb(null);
                }
            }
        }
        
        return clientData;
    }

    /**
     * (3)クライアントを破棄する。アプリやデバイスプラグインがアンインストールされるときに実行する.
     * 
     * @param clientId クライアントID
     * @throws AuthorizatonException Authorization例外.
     */
    public static void destroyClient(final String clientId) throws AuthorizatonException {

        /* 引数チェック */
        if (clientId  == null) {
            throw new IllegalArgumentException("clientId is null.");
        }
        
        SQLiteDatabase db = null;
        SQLiteTokenManager sqliteTokenManager = null;
        SQLiteClientManager sqliteClientManager = null;
        
        /* DBを同時アクセスさせない */
        synchronized (mLockForDbAccess) {
            try {
                /* DBオープン */
                db = mDbHelper.getWritableDatabase();
                db.beginTransaction();
                
                /* TokenManagerにDBオブジェクトを設定 */
                sqliteTokenManager = (SQLiteTokenManager) tokenManager;
                sqliteTokenManager.setDb(db);
                
                /* ClientManagerにDBオブジェクトを設定 */
                sqliteClientManager = (SQLiteClientManager) clientManager;
                sqliteClientManager.setDb(db);
                
                /* クライアントデータ削除 */
                removeClientData(clientId);
                
                /* コミット */
                db.setTransactionSuccessful();
    
            } catch (SQLiteException e) {
                throw new RuntimeException(e);
            } finally {
                if (db != null) {
                    db.endTransaction();
                    db.close();
                }
                
                /* TokenManagerのDBオブジェクトをクリア設定 */
                if (sqliteTokenManager != null) {
                    sqliteTokenManager.setDb(null);
                }
                
                /* ClientManagerのDBオブジェクトをクリア設定 */
                if (sqliteClientManager != null) {
                    sqliteClientManager.setDb(null);
                }
            }
        }
        
        sLogger.fine("destroyClient() - clientId:" + clientId);
    }

    /**
     * (4)アプリまたはデバイスプラグインから受け取ったsignatureが、Local
     * OAuthで生成したsignatureと一致するかチェックする.
     * 
     * @param signature signature
     * @param clientId クライアントID
     * @param grantType グラントタイプ("authorization_code"が渡される)
     * @param deviceId デバイスID(デバイスプラグインの場合のみ設定する。アプリの場合はnullを入れる)
     * @param scopes 要求されたスコープの配列
     * @return true: 一致した / false: 一致しなかった
     * @throws AuthorizatonException Authorization例外.
     */
    public static boolean checkSignature(final String signature, final String clientId, final String grantType,
            final String deviceId, final String[] scopes) throws AuthorizatonException {

        /* 引数チェック */
        if (signature == null) {
            throw new IllegalArgumentException("signature is null.");
        } else if (clientId == null) {
            throw new IllegalArgumentException("clientId is null.");
        } else if (grantType == null) {
            throw new IllegalArgumentException("grantType is null.");
        } else if (scopes == null) {
            throw new IllegalArgumentException("scopes is null.");
        }
        
        boolean result = false;
        
        SQLiteDatabase db = null;
        
        /* DBを同時アクセスさせない */
        synchronized (mLockForDbAccess) {
            try {
                /* DBオープン */
                db = mDbHelper.getReadableDatabase();
                
                /* ClientManagerにDBオブジェクトを設定 */
                SQLiteClientManager sqliteClientManager = (SQLiteClientManager) clientManager;
                sqliteClientManager.setDb(db);
                
                /* LocalOAuthが保持しているクライアントシークレットを取得 */
                Client client = sqliteClientManager.findById(clientId);
                if (client != null) {
                    String clientSecret = String.copyValueOf(client.getClientSecret());
                    
                    /*
                     * LocalOAuthが保持しているclientSecretとリクエストのclient_id, grant_type,
                     * scopesを結合して暗号化しsignature作成
                     */
                    String innerSignature = AuthSignature.generateSignature(clientId, grantType,
                            deviceId, scopes, clientSecret);
                    
                    /* Signature一致判定 */
                    if (innerSignature.equals(signature)) {
                        result = true;
                    } else {
                        String strScopes = "";
                        for (int i = 0; i < scopes.length; i++) {
                            if (i > 0) {
                                strScopes += ",";
                            }
                            strScopes += scopes[i];
                        }
                        sLogger.fine("checkSignature() - signature not equal.");
                        sLogger.fine(" - signature: " + signature);
                        sLogger.fine(" - innerSignature:" + innerSignature);
                        sLogger.fine(" - clientId:" + clientId);
                        sLogger.fine(" - grantType:" + grantType);
                        sLogger.fine(" - deviceId:" + deviceId);
                        sLogger.fine(" - scopes:" + strScopes);
                        sLogger.fine(" - clientSecret:" + clientSecret);
                    }
                } else {
                    sLogger.fine("client not found.  clientId: " + clientId);
                }
                
                /* TokenManagerのDBオブジェクトをクリア設定 */
                sqliteClientManager.setDb(null);
                
            } catch (SQLiteException e) {
                throw new RuntimeException(e);
            } finally {
                if (db != null) {
                    db.close();
                }
            }
        }
        
        return result;
    }

    /**
     * (4)-2.Device Connect Managerから受け取ったアクセストークン受信用signatureが、アプリで生成したsignatureと一致するかチェックする.
     * @param signature signature
     * @param accessToken アクセストークン
     * @param clientSecret クライアントシークレット
     * @return true: 一致した / false: 一致しなかった
     */
    public static boolean checkSignature(final String signature, final String accessToken, final String clientSecret) {
        
        /* 引数チェック */
        if (signature == null) {
            throw new IllegalArgumentException("signature is null.");
        } else if (accessToken == null) {
            throw new IllegalArgumentException("accessToken is null.");
        } else if (clientSecret == null) {
            throw new IllegalArgumentException("clientSecret is null.");
        }
        
        /* Signature作成 */
        String innerSignature = AuthSignature.generateSignature(accessToken, clientSecret);
        
        /* Signatureが一致するか */
        boolean result = signature.equals(innerSignature);
        if (!result) {
            sLogger.fine("checkSignature() - signature not equal.");
            sLogger.fine(" - signature: " + signature);
            sLogger.fine(" - innerSignature:" + innerSignature);
            sLogger.fine(" - accessToken:" + accessToken);
            sLogger.fine(" - clientSecret:" + clientSecret);
        }
        
        return result;
    }

    /**
     * (5)アクセストークン発行承認確認画面表示.<br>
     * 
     * - Activityはprocess指定を行わないのでDevice Connect Managerのスレッドとは別プロセスとなる。<br>
     * - Device Connect Managerのサービスととプロセス間通信が行えるように(0)onBind()でBindする。<br>
     * - Messenger, Handler はLocalOAuth内部に持つ。<br>
     * 
     * - 状況別で動作が変わる。<br>
     * 
     * - (a)有効なアクセストークンが存在しない。(失効中も含む) =>
     * 承認確認画面を表示する。承認/拒否はBindされたServiceへMessageで通知される。<br>
     * 
     * - (b)アクセストークンは存在するがスコープが不足している。 =>
     * 承認確認画面を表示する。承認/拒否はBindされたServiceへMessageで通知される。<br>
     * 
     * - (c)アクセストークンは存在しスコープも満たされている。 => 承認確認画面は表示しない。(Message通知されない)<br>
     * 
     * @param params パラメータ
     * @param listener アクセストークン発行リスナー(承認確認画面で承認／拒否ボタンが押されたら実行される)
     * @throws AuthorizatonException Authorization例外.
     */
    public static void confirmPublishAccessToken(final ConfirmAuthParams params,
            final PublishAccessTokenListener listener) throws AuthorizatonException {
        
        /* 引数チェック */
        if (params == null) {
            throw new IllegalArgumentException("confirmAuthParams is null.");
        } else if (listener == null) {
            throw new IllegalArgumentException("publishAccessTokenListener is null.");
        } else if (params.getContext() == null) {
            throw new IllegalArgumentException("Context is null.");
        } else if (params.getApplicationName() == null || params.getApplicationName().isEmpty()) {
            throw new IllegalArgumentException("ApplicationName is null.");
        } else if (params.getClientId() == null || params.getClientId().isEmpty()) {
            throw new IllegalArgumentException("ClientId is null.");
        } else if (params.getGrantType() == null || params.getGrantType().isEmpty()) {
            throw new IllegalArgumentException("GrantType is null.");
        } else if (params.getScopes() == null || params.getScopes().length <= 0) {
            throw new IllegalArgumentException("Scope is null.");
        }
        
        /* トークンの状態取得 */
        boolean isExpiredAccessToken = false; /* true: 有効期限切れ / false: 有効期限内 */
        boolean isIncludeScope = false; /*
                                         * true: 要求スコープが全て含まれている / false:
                                         * 一部または全部含まれていない
                                         */
        Client client = null;
        Token token = null; /*
                             * not null: アクセストークンあり / null: アクセストークンなし
                             */
        
        SQLiteDatabase db = null;
        SQLiteClientManager sqliteClientManager = null;
        SQLiteTokenManager sqliteTokenManager = null;
        
        /* DBを同時アクセスさせない */
        synchronized (mLockForDbAccess) {
            try {
                /* DBオープン */
                db = mDbHelper.getWritableDatabase();
                db.beginTransaction();
                
                /* ClientManagerにDBオブジェクトを設定 */
                sqliteClientManager = (SQLiteClientManager) clientManager;
                sqliteClientManager.setDb(db);
    
                /* TokenManagerにDBオブジェクトを設定 */
                sqliteTokenManager = (SQLiteTokenManager) tokenManager;
                sqliteTokenManager.setDb(db);
    
                /* クライアントをDBから読み込み */
                client = getClient(params);
    
                /* トークンをDBから読み込み */
                token = tokenManager.findToken(client, SampleUser.USERNAME);
    
                /* コミット */
                db.setTransactionSuccessful();
    
            } catch (SQLiteException e) {
                throw new RuntimeException(e);
            } finally {
                if (db != null) {
                    db.endTransaction();
                    db.close();
                }
                
                /* ClientManagerのDBオブジェクトをクリア設定 */
                if (sqliteClientManager != null) {
                    sqliteClientManager.setDb(null);
                }
                
                /* TokenManagerのDBオブジェクトをクリア設定 */
                if (sqliteClientManager != null) {
                    sqliteClientManager.setDb(null);
                }
            }
        }
        
        if (token != null) {
            /* アクセストークンが存在するか確認(全スコープの有効期限が切れていたら有効期限切れとみなす) */
            if (((ServerToken) token).isExpired()) {
                isExpiredAccessToken = true;
            }
        }

        /* (a), (b)なら承認確認画面を表示する */
        if (token == null /* (a) */
                || isExpiredAccessToken /* (a) */
                || !isIncludeScope) { /* (b) */

            /* カレントスレッドID取得 */
            Thread thread = Thread.currentThread();
            final long threadId = thread.getId();

            /* ロケール取得(アンダーバーがついている場合("ja_JP"等)は、アンダーバーから後の文字列は削除する) */
            String locale = Locale.getDefault().toString();
            String[] splitlocales = locale.split("_");
            if (splitlocales != null && splitlocales.length > 1) {
                locale = splitlocales[0];
            }
            
            /* デバイスプラグインの場合はdevicePlugin.xmlからロケールが一致する表示スコープ名を取得する */
            Map<String, DevicePluginXmlProfile> supportProfiles = null;
            if (params.isForDevicePlugin()) {
                supportProfiles =
                        DevicePluginXmlUtil.getSupportProfiles(
                                params.getContext(),
                        params.getContext().getPackageName());
            }
            String[] scopes = params.getScopes();
            String[] displayScopes = new String[scopes.length];
            int storePos = 0;
            for (String scope : scopes) {
                
                /* ローカライズされたプロファイル名取得する */
                displayScopes[storePos++] = ScopeUtil.getDisplayScope(params.getContext(), scope, locale,
                        supportProfiles);
            }
            
            /* リクエストデータを作成する */
            final Date currentTime = new Date();
            ConfirmAuthRequest request = new ConfirmAuthRequest(threadId, params, listener,
                    currentTime, displayScopes);
            
            /* キューが空なら、リクエストをキューに追加した後すぐにActivityを起動する */
            if (countRequest() <= 0) {
                enqueueRequest(request);
                startConfirmAuthActivity(request);
            /* 空で無ければ、リクエストをキューに追加して先の処理が完了した後に処理する */
            } else {
                enqueueRequest(request);
            }
        }
    }

    /**
     * (6)OAuthクライアント情報からアクセストークンを取得する.
     * 
     * @param packageInfo パッケージ情報
     * @return not null: アクセストークンデータ / null:アクセストークンがない
     */
    public static AccessTokenData findAccessToken(final PackageInfoOAuth packageInfo) {

        /* 引数チェック */
        if (packageInfo == null) {
            throw new IllegalArgumentException("packageInfo is null.");
        } else if (packageInfo.getPackageName() == null) {
            throw new IllegalArgumentException("packageInfo.getPackageName() is null.");
        }
        
        AccessTokenData acccessTokenData = null;
        SQLiteDatabase db = null;
        SQLiteClientManager sqliteClientManager = null;
        SQLiteTokenManager sqliteTokenManager = null;

        /* DBを同時アクセスさせない */
        synchronized (mLockForDbAccess) {
            try {
                /* DBオープン */
                db = mDbHelper.getReadableDatabase();
                
                /* ClientManagerにDBオブジェクトを設定 */
                sqliteClientManager = (SQLiteClientManager) clientManager;
                sqliteClientManager.setDb(db);
                
                /* TokenManagerにDBオブジェクトを設定 */
                sqliteTokenManager = (SQLiteTokenManager) tokenManager;
                sqliteTokenManager.setDb(db);
                
                /* パッケージ情報からクライアントデータを取得 */
                Client client = sqliteClientManager.findByPackageInfo(packageInfo);
                if (client != null) {
                    
                    /* クライアントからトークンを取得する */
                    Token token = sqliteTokenManager.findToken(client, SampleUser.USERNAME);
                    if (token != null) {
                        String accessToken = token.getAccessToken();
                        AccessTokenScope[] accessTokenScopes = scopesToAccessTokenScopes(token.getScope());
                        acccessTokenData = new AccessTokenData(accessToken, accessTokenScopes);
                    }
                }
                
            } catch (SQLiteException e) {
                throw new RuntimeException(e);
            } finally {
                if (db != null) {
                    db.close();
                }
                /* ClientManagerのDBオブジェクトをクリア設定 */
                if (sqliteClientManager != null) {
                    sqliteClientManager.setDb(null);
                }
                /* TokenManagerのDBオブジェクトをクリア設定 */
                if (sqliteTokenManager != null) {
                    sqliteTokenManager.setDb(null);
                }
            }
        }

        return acccessTokenData;
    }


    /**
     * (7)アクセストークンを確認する.
     * 
     * @param accessToken 確認するアクセストークン
     * @param scope このスコープがアクセストークンに含まれるかチェックする
     * @param specialScopes 承認許可されていなくてもアクセス可能なscopes(nullなら指定無し)
     * @return チェック結果
     */
    public static CheckAccessTokenResult checkAccessToken(final String accessToken, final String scope,
            final String[] specialScopes) {

        /* 引数チェック */
        if (scope == null) {
            throw new IllegalArgumentException("scope is null.");
        }
        boolean isExistClientId = false; /*
                                          * true: アクセストークンを発行したクライアントIDあり /
                                          * false: アクセストークンを発行したクライアントIDなし.
                                          */
        boolean isExistAccessToken = false; /*
                                             * true: アクセストークンあり / false:
                                             * アクセストークンなし
                                             */
        boolean isExistScope = false; /* true: スコープあり / false: スコープなし */
        boolean isNotExpired = false; /* true: 有効期限内 / false: 有効期限切れ */

        // 無視するスコープが指定されていた場合
        if (specialScopes != null && Arrays.asList(specialScopes).contains(scope)) {
            return new CheckAccessTokenResult(true, true, true, true);
        }

        SQLiteDatabase db = null;
        SQLiteClientManager sqliteClientManager = null;
        SQLiteTokenManager sqliteTokenManager = null;
        
        /* DBを同時アクセスさせない */
        synchronized (mLockForDbAccess) {
            try {
                /* DBオープン */
                db = mDbHelper.getWritableDatabase();
                db.beginTransaction();
                
                /* ClientManagerにDBオブジェクトを設定 */
                sqliteClientManager = (SQLiteClientManager) clientManager;
                sqliteClientManager.setDb(db);
                
                /* TokenManagerにDBオブジェクトを設定 */
                sqliteTokenManager = (SQLiteTokenManager) tokenManager;
                sqliteTokenManager.setDb(db);
                
                /* アクセストークンを元にトークンを検索する */
                SQLiteToken token = (SQLiteToken) sqliteTokenManager.findTokenByAccessToken(accessToken);
                if (token != null) {
                    isExistAccessToken = true; /* アクセストークンあり */
                    Scope[] scopes = token.getScope();
                    for (Scope s : scopes) {
                        /* token.scopeに"*"が含まれていたら、どんなスコープにもアクセスできる */
                        if (s.getScope().equals("*")) {
                            isExistScope = true; /* スコープあり */
                            isNotExpired = true; /* 有効期限 */
                            break;
                        }
                        if (s.getScope().equals(scope)) {
                            isExistScope = true; /* スコープあり */
                            
                            if (s.getExpirePeriod() == 0) {
                                /* 有効期限0の場合は、トークン発行から1分以内の初回アクセスなら有効期限内とする */
                                long t = System.currentTimeMillis() - token.getRegistrationDate();
                                if (0 <= t
                                && t <= (LocalOAuth2Settings.ACCESS_TOKEN_GRACE_TIME * LocalOAuth2Settings.MSEC)
                                && token.isFirstAccess()) {
                                    isNotExpired = true;
                                }
                            } else if (s.getExpirePeriod() > 0) {
                                /* 有効期限1以上の場合は、トークン発行からの経過時間が有効期限内かを判定して返す */
                                isNotExpired = !s.isExpired();
                            } else {
                                /* 有効期限にマイナス値が設定されていたら、有効期限切れとみなす */
                                isNotExpired = false;
                            }
                            break;
                        }
                    }
    
                    /* このトークンを発行したクライアントIDが存在するかチェック */
                    if (sqliteClientManager.findById(token.getClientId()) != null) {
                        isExistClientId = true;
                    }
    
                    /* トークンのアクセス時間更新 */
                    SQLiteToken sqliteToken = (SQLiteToken) token;
                    sqliteToken.dbUpdateTokenAccessTime(db);
                }
                
                /* コミット */
                db.setTransactionSuccessful();
                
            } catch (SQLiteException e) {
                throw new RuntimeException(e);
            } finally {
                if (db != null) {
                    db.endTransaction();
                    db.close();
                }
                /* TokenManagerのDBオブジェクトをクリア設定 */
                if (sqliteTokenManager != null) {
                    sqliteTokenManager.setDb(null);
                }
                /* ClientManagerのDBオブジェクトをクリア設定 */
                if (sqliteClientManager != null) {
                    sqliteClientManager.setDb(null);
                }
            }
        }
        
        CheckAccessTokenResult result = new CheckAccessTokenResult(isExistClientId, isExistAccessToken, isExistScope,
                isNotExpired);
        if (!result.checkResult()) {
            sLogger.fine("checkAccessToken() - error.");
            sLogger.fine(" - isExistClientId: " + isExistClientId);
            sLogger.fine(" - isExistAccessToken: " + isExistAccessToken);
            sLogger.fine(" - isExistScope:" + isExistScope);
            sLogger.fine(" - isNotExpired:" + isNotExpired);
            sLogger.fine(" - accessToken:" + accessToken);
            sLogger.fine(" - scope:" + scope);
        }
        return result;
    }

    /**
     * (8)Signatureを作成する.
     * 
     * @param clientId クライアントID
     * @param grantType グラントタイプ
     * @param deviceId デバイスID
     * @param scopes スコープ
     * @param clientSecret クライアントシークレット
     * @return not null: 作成したSignature / null: nullは返さない。
     * @throws AuthorizatonException Authorization例外.
     */
    public static String createSignature(final String clientId, final String grantType, final String deviceId,
            final String[] scopes, final String clientSecret) throws AuthorizatonException {

        /* 引数チェック */
        if (clientId == null) {
            throw new IllegalArgumentException("clientId is null.");
        } else if (grantType == null) {
            throw new IllegalArgumentException("grantType is null.");
        } else if (scopes == null) {
            throw new IllegalArgumentException("scopes is null.");
        } else if (clientSecret == null) {
            throw new IllegalArgumentException("clientSecret is null.");
        }
        
        String signature = AuthSignature.generateSignature(clientId, grantType, deviceId, scopes,
                clientSecret);
        return signature;
    }

    /**
     * (8)-2.アクセストークンを返却する際に添付するSignatureを作成する.
     * 
     * @param accessToken アクセストークン
     * @param clientId クライアントId
     * @return not null: 作成したSignature / null: nullは返さない。
     * @throws AuthorizatonException Authorization例外.
     */
    public static String createSignature(final String accessToken, final String clientId) throws AuthorizatonException {

        /* 引数チェック */
        if (accessToken == null) {
            throw new IllegalArgumentException("accessToken is null.");
        } else if (clientId == null) {
            throw new IllegalArgumentException("clientId is null.");
        }
        
        /* clientIdからclientSecretを取得する */
        String clientSecret = null;
        SQLiteDatabase db = null;
        SQLiteClientManager sqliteClientManager = null;
        
        /* DBを同時アクセスさせない */
        synchronized (mLockForDbAccess) {
            try {
                /* DBオープン */
                db = mDbHelper.getReadableDatabase();
                
                /* ClientManagerにDBオブジェクトを設定 */
                sqliteClientManager = (SQLiteClientManager) clientManager;
                sqliteClientManager.setDb(db);
                
                /* クライアントIDを元にをクライアントデータを検索する */
                Client client = (Client) sqliteClientManager.findById(clientId);
                if (client != null) {
                    clientSecret = String.copyValueOf(client.getClientSecret());
                } else {
                    throw new AuthorizatonException(AuthorizatonException.CLIENT_NOT_FOUND);
                }
                
            } catch (SQLiteException e) {
                throw new RuntimeException(e);
            } finally {
                if (db != null) {
                    db.close();
                }
                /* TokenManagerのDBオブジェクトをクリア設定 */
                if (sqliteClientManager != null) {
                    sqliteClientManager.setDb(null);
                }
            }
        }
        
        /* Signature作成 */
        String signature = AuthSignature.generateSignature(accessToken, clientSecret);
        return signature;
    }

    /**
     * (9)クライアントが発行したアクセストークンを破棄して利用できないようにする.<br>
     * クライアントはパッケージ名で指定する.
     * 
     * @param packageInfo パッケージ名
     */
    public static void destroyAccessToken(final PackageInfoOAuth packageInfo) {

        /* 引数チェック */
        if (packageInfo == null) {
            throw new IllegalArgumentException("packageInfo is null.");
        } else if (packageInfo.getPackageName() == null) {
            throw new IllegalArgumentException("packageInfo.getPackageName() is null.");
        }
        
        SQLiteDatabase db = null;
        SQLiteClientManager sqliteClientManager = null;
        SQLiteTokenManager sqliteTokenManager = null;
        
        /* DBを同時アクセスさせない */
        synchronized (mLockForDbAccess) {
            try {
                /* DBオープン */
                db = mDbHelper.getWritableDatabase();
                db.beginTransaction();
                
                /* ClientManagerにDBオブジェクトを設定 */
                sqliteClientManager = (SQLiteClientManager) clientManager;
                sqliteClientManager.setDb(db);
                
                /* TokenManagerにDBオブジェクトを設定 */
                sqliteTokenManager = (SQLiteTokenManager) tokenManager;
                sqliteTokenManager.setDb(db);
                
                Client client = clientManager.findByPackageInfo(packageInfo);
                tokenManager.revokeAllTokens(client);
                sLogger.fine("destroyAccessToken()");
                sLogger.fine(" - clientId:" + client.getClientId());
                sLogger.fine(" - packageName:" + packageInfo.getPackageName());
                if (packageInfo.getDeviceId() != null) {
                    sLogger.fine(" - deviceId:" + packageInfo.getDeviceId());
                }
                
                /* コミット */
                db.setTransactionSuccessful();
                
            } catch (SQLiteException e) {
                throw new RuntimeException(e);
            } finally {
                if (db != null) {
                    db.endTransaction();
                    db.close();
                }
                /* ClientManagerのDBオブジェクトをクリア設定 */
                if (sqliteClientManager != null) {
                    sqliteClientManager.setDb(null);
                }
                /* TokenManagerのDBオブジェクトをクリア設定 */
                if (sqliteTokenManager != null) {
                    sqliteTokenManager.setDb(null);
                }
            }
        }
    }
    
    /**
     * (10)アクセストークンからクライアントパッケージ情報を取得する.
     * 
     * @param accessToken アクセストークン
     * @return not null: クライアントパッケージ情報 / null:アクセストークンがないのでクライアントパッケージ情報が取得できない。
     */
    public static ClientPackageInfo findClientPackageInfoByAccessToken(final String accessToken) {

        /* 引数チェック */
        if (accessToken == null) {
            throw new IllegalArgumentException("accessToken is null.");
        }
        
        ClientPackageInfo clientPackageInfo = null;
        
        /* DBオープン */
        SQLiteDatabase db = null;
        SQLiteClientManager sqliteClientManager = null;
        SQLiteTokenManager sqliteTokenManager = null;

        /* DBを同時アクセスさせない */
        synchronized (mLockForDbAccess) {
            try {
                /* DBオープン */
                db = mDbHelper.getReadableDatabase();
                
                /* ClientManagerにDBオブジェクトを設定 */
                sqliteClientManager = (SQLiteClientManager) clientManager;
                sqliteClientManager.setDb(db);
                
                /* TokenManagerにDBオブジェクトを設定 */
                sqliteTokenManager = (SQLiteTokenManager) tokenManager;
                sqliteTokenManager.setDb(db);
                
                SQLiteToken token = (SQLiteToken) sqliteTokenManager.findTokenByAccessToken(accessToken);
                if (token != null) {
                    String clientId = token.getClientId();
                    if (clientId != null) {
                        Client client = sqliteClientManager.findById(clientId);
                        if (client != null) {
                            clientPackageInfo = new ClientPackageInfo(client.getPackageInfo(), clientId);
                        }
                    }
                }
                
            } finally {
                if (db != null) {
                    db.close();
                }
                /* ClientManagerのDBオブジェクトをクリア設定 */
                if (sqliteClientManager != null) {
                    sqliteClientManager.setDb(null);
                }
                /* TokenManagerのDBオブジェクトをクリア設定 */
                if (sqliteTokenManager != null) {
                    sqliteTokenManager.setDb(null);
                }
            }
        }
        
        return clientPackageInfo;
    }
    
    
    /**
     * (13)アクセストークン一覧Activityを表示.
     * @param context コンテキスト
     */
    public static void startAccessTokenListActivity(final android.content.Context context) {
        Intent intent = new Intent();
        intent.setClass(context, AccessTokenListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    
    /**
     * (13)-1.アクセストークン一覧を取得する(startAccessTokenListActivity()用).
     * @return not null: アクセストークンの配列 / null: アクセストークンなし
     */
    public static SQLiteToken[] getAccessTokens() {
        SQLiteToken[] tokens = null;
        
        SQLiteDatabase db = null;
        SQLiteTokenManager sqliteTokenManager = null;
        
        /* DBを同時アクセスさせない */
        synchronized (mLockForDbAccess) {
            try {
                /* DBオープン */
                db = mDbHelper.getReadableDatabase();
                
                /* ClientManagerにDBオブジェクトを設定 */
                sqliteTokenManager = (SQLiteTokenManager) tokenManager;
                sqliteTokenManager.setDb(db);
                
                /* LocalOAuthが保持しているクライアントシークレットを取得 */
                tokens = (SQLiteToken[]) sqliteTokenManager.findTokens(SampleUser.USERNAME);
                
            } catch (SQLiteException e) {
                throw new RuntimeException(e);
            } finally {
                if (db != null) {
                    db.close();
                }
                
                /* TokenManagerのDBオブジェクトをクリア設定 */
                if (sqliteTokenManager != null) {
                    sqliteTokenManager.setDb(null);
                }
                
            }
        }
        
        return tokens;
    }
    
    /**
     * (13)-2.アクセストークンを破棄して利用できないようにする(startAccessTokenListActivity()用.
     * 
     * @param tokenId トークンID
     */
    public static void destroyAccessToken(final long tokenId) {

        SQLiteDatabase db = null;
        SQLiteTokenManager sqliteTokenManager = null;
        
        /* DBを同時アクセスさせない */
        synchronized (mLockForDbAccess) {
            try {
                /* DBオープン */
                db = mDbHelper.getWritableDatabase();
                db.beginTransaction();
                
                /* TokenManagerにDBオブジェクトを設定 */
                sqliteTokenManager = (SQLiteTokenManager) tokenManager;
                sqliteTokenManager.setDb(db);
                
                sqliteTokenManager.revokeToken(tokenId);
                
                /* コミット */
                db.setTransactionSuccessful();
                
            } catch (SQLiteException e) {
                throw new RuntimeException(e);
            } finally {
                if (db != null) {
                    db.endTransaction();
                    db.close();
                }
                /* TokenManagerのDBオブジェクトをクリア設定 */
                if (sqliteTokenManager != null) {
                    sqliteTokenManager.setDb(null);
                }
            }
        }
    }

    /**
     * (13)-3.DBのトークンデータを削除(startAccessTokenListActivity()用.
     * 
     */
    public static void destroyAllAccessToken() {

        SQLiteDatabase db = null;
        SQLiteTokenManager sqliteTokenManager = null;
        
        /* DBを同時アクセスさせない */
        synchronized (mLockForDbAccess) {
            try {
                /* DBオープン */
                db = mDbHelper.getWritableDatabase();
                db.beginTransaction();
                
                /* TokenManagerにDBオブジェクトを設定 */
                sqliteTokenManager = (SQLiteTokenManager) tokenManager;
                sqliteTokenManager.setDb(db);
                
                sqliteTokenManager.revokeAllTokens(SampleUser.USERNAME);
                
                /* コミット */
                db.setTransactionSuccessful();
                
            } catch (SQLiteException e) {
                throw new RuntimeException(e);
            } finally {
                if (db != null) {
                    db.endTransaction();
                    db.close();
                }
                /* TokenManagerのDBオブジェクトをクリア設定 */
                if (sqliteTokenManager != null) {
                    sqliteTokenManager.setDb(null);
                }
            }
        }
    }

    /**
     * (13)-4.クライアントIDが一致するクライアントデータを取得する(startAccessTokenListActivity()用).
     * @param clientId クライアントID
     * @return not null: クライアント / null: クライアントなし
     */
    public static SQLiteClient findClientByClientId(final String clientId) {
        SQLiteClient client = null;
        
        SQLiteDatabase db = null;
        SQLiteClientManager sqliteClientManager = null;
        
        /* DBを同時アクセスさせない */
        synchronized (mLockForDbAccess) {
            try {
                /* DBオープン */
                db = mDbHelper.getReadableDatabase();
                
                /* ClientManagerにDBオブジェクトを設定 */
                sqliteClientManager = (SQLiteClientManager) clientManager;
                sqliteClientManager.setDb(db);
                
                /* LocalOAuthが保持しているクライアントシークレットを取得 */
                client = (SQLiteClient) sqliteClientManager.findById(clientId);
                
            } catch (SQLiteException e) {
                throw new RuntimeException(e);
            } finally {
                if (db != null) {
                    db.close();
                }
                
                /* TokenManagerのDBオブジェクトをクリア設定 */
                if (sqliteClientManager != null) {
                    sqliteClientManager.setDb(null);
                }
                
            }
        }
        
        return client;
    }
    
    /**
     * 長時間使用されていなかったclientIdをクリーンアップする(DBアクセスしたついでに有効クライアント数も取得する).
     * @return 有効クライアント数
     */
    private static int cleanupClient() {
        int clientCount = 0;
        
        SQLiteDatabase db = null;
        
        /* DBを同時アクセスさせない */
        synchronized (mLockForDbAccess) {
            try {
                /* DBオープン */
                db = mDbHelper.getWritableDatabase();
                db.beginTransaction();
                
                /* ClientManagerにDBオブジェクトを設定 */
                SQLiteClientManager sqliteClientManager = (SQLiteClientManager) clientManager;
                sqliteClientManager.setDb(db);
                
                sqliteClientManager.cleanupClient(LocalOAuth2Settings.CLIENT_CLEANUP_TIME);
                
                /* 有効クライアント数を取得する */
                clientCount = sqliteClientManager.countClients();
                
                /* ClientManagerのDBオブジェクトをクリア設定 */
                sqliteClientManager.setDb(null);
                
                /* コミット */
                db.setTransactionSuccessful();
                
            } catch (SQLiteException e) {
                throw new RuntimeException(e);
            } finally {
                if (db != null) {
                    db.endTransaction();
                    db.close();
                }
            }
        }
        
        return clientCount;
    }
    
    
    /** プロセス間通信用メッセージID : 承認確認画面で許可／拒否されたかをMessageでDevice ConnectのServiceに送る. */
    private static final int MSG_CONFIRM_APPROVAL = 1000;

    /** プロセス間通信用メッセージID : threadIdが有効かをMessageでDevice ConnectのServiceに送る. */
    private static final int MSG_CONFIRM_CHECK_THREADID = 1001;
    
    
    /** Handler of incoming messages from clients. */
    private static class ApprovalHandler extends Handler {
        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what) {
            /* 承認確認画面Activityから受け取ったメッセージを処理する */
            case MSG_CONFIRM_APPROVAL: /* arg1:スレッドID / arg2:承認(=1) 拒否(=0) */

                /* Messageからデータ取得 */
                long threadId = (long) msg.arg1;
                boolean isApproval = false; /* true: 承認 / false: 拒否 */
                if (msg.arg2 > 0) {
                    isApproval = true;
                }

                /* 承認確認画面を表示する直前に保存しておいたパラメータデータを取得する(キューからは削除される) */
                ConfirmAuthRequest request = dequeueRequest(threadId, true);
                if (request != null) {
                    PublishAccessTokenListener publishAccessTokenListener = request.getPublishAccessTokenListener();
                    ConfirmAuthParams params = request.getConfirmAuthParams();

                    /* 許可された */
                    if (isApproval) {
                        
                        AccessTokenData accessTokenData = null;
                        AuthorizatonException exception = null;
                        
                        SQLiteDatabase db = null;
                        SQLiteClientManager sqliteClientManager = null;
                        SQLiteTokenManager sqliteTokenManager = null;
                        
                        /* DBを同時アクセスさせない */
                        synchronized (mLockForDbAccess) {
                            try {
                                /* DBオープン */
                                db = mDbHelper.getWritableDatabase();
                                db.beginTransaction();
                                
                                /* ClientManagerにDBオブジェクトを設定 */
                                sqliteClientManager = (SQLiteClientManager) clientManager;
                                sqliteClientManager.setDb(db);
                                
                                /* TokenManagerにDBオブジェクトを設定 */
                                sqliteTokenManager = (SQLiteTokenManager) tokenManager;
                                sqliteTokenManager.setDb(db);
                                
                                /* アクセストークン発行する前に古い無効なトークン(クライアントIDが削除されて残っていたトークン)をクリーンアップする */
                                sqliteTokenManager.cleanup();
                                
                                /* アクセストークン発行 */
                                accessTokenData = publishAccessToken(params);
                                
                                /* コミット */
                                db.setTransactionSuccessful();
    
                            } catch (AuthorizatonException e) {
                                e.printStackTrace();
                                exception = e;
                            } catch (SQLiteException e) {
                                e.printStackTrace();
                                exception = new AuthorizatonException(AuthorizatonException.SQLITE_ERROR);
                            } finally {
                                if (db != null) {
                                    db.endTransaction();
                                    db.close();
                                }
                                
                                /* ClientManagerのDBオブジェクトをクリア設定 */
                                if (sqliteClientManager != null) {
                                    sqliteClientManager.setDb(null);
                                }
                                
                                /* TokenManagerのDBオブジェクトをクリア設定 */
                                if (sqliteTokenManager != null) {
                                    sqliteTokenManager.setDb(null);
                                }
                            }
                        }

                        if (exception == null) {
                            /* リスナーを通じてアクセストークンを返す */
                            callPublishAccessTokenListener(params, accessTokenData, publishAccessTokenListener);
                            /* Activityに発行成功を通知する(Activityを閉じる) */
                            sendMessageId(msg.replyTo, MESSAGE_PUBLISH_ACCESSTOKEN_SUCCESS, null, null);
                            
                            /* キューにリクエストが残っていれば、次のキューを取得してActivityを起動する */
                            ConfirmAuthRequest nextRequest = pickupRequest();
                            if (nextRequest != null) {
                                startConfirmAuthActivity(nextRequest);
                            } 
                        } else {
                            /* リスナーを通じて発生した例外を返す */
                            callExceptionListener(params, exception, publishAccessTokenListener);
                            /* Activityに例外発生を通知する(Activityを閉じる) */
                            sendMessageId(msg.replyTo, MESSAGE_PUBLISH_ACCESSTOKEN_EXCEPTION, null, null);
                            
                            /* キューにリクエストが残っていれば、次のキューを取得してActivityを起動する */
                            ConfirmAuthRequest nextRequest = pickupRequest();
                            if (nextRequest != null) {
                                startConfirmAuthActivity(nextRequest);
                            }
                        }

                    } else { /* 拒否された */

                        /* リスナーを通じて拒否通知を返す */
                        callPublishAccessTokenListener(params, null, publishAccessTokenListener);
                        
                        /* キューにリクエストが残っていれば、次のキューを取得してActivityを起動する */
                        ConfirmAuthRequest nextRequest = pickupRequest();
                        if (nextRequest != null) {
                            startConfirmAuthActivity(nextRequest);
                        }
                    }
                }
                break;
                
            case MSG_CONFIRM_CHECK_THREADID:
                /* Messageからデータ取得 */
                long checkThreadId = (long) msg.arg1;
                /* キューにthreadIdが存在するか判定(1:true / 0:false) */
                int result = 0;
                if (dequeueRequest(checkThreadId, false) != null) {
                    result = 1;
                }
                
                /* Activityに例外発生を通知する(Activityを閉じる) */
                sendMessageId(msg.replyTo, MESSAGE_CHECK_THREADID_RESULT, result, null);
                
                break;
                
            default:
                super.handleMessage(msg);
            }
        }
    }

    /** メッセンジャー. */
    private static Messenger mMessenger = new Messenger(new ApprovalHandler());

    /**
     * アクセストークンをリスナー経由で返す処理.
     * 
     * @param params 承認確認画面のパラメータ
     * @return アクセストークンデータ(アクセストークン, 有効期間(アクセストークン発行時間から使用可能な時間。単位:ミリ秒) を返す。<br>
     * @throws AuthorizatonException Authorization例外.
     */
    private static AccessTokenData publishAccessToken(final ConfirmAuthParams params) throws AuthorizatonException {

        String clientId = params.getClientId();

        Client client = clientManager.findById(clientId);
        if (client != null) {

            /* AuthSessionを登録してセッションIDを取得 */
            RedirectRepresentation redirectRepresentation = callAuthorizationServerResource(client, true, null);
            if (redirectRepresentation != null) {
                String sessionId = redirectRepresentation.getOptions().get(RedirectRepresentation.SESSION_ID)
                        .toString();

                /* ログイン処理 */
                ResultRepresentation result = (ResultRepresentation) callLoginPageServerResource(
                        SampleUser.LOCALOAUTH_USER, SampleUser.LOCALOAUTH_PASS);
                if (result.getResult()) {

                    RedirectRepresentation result3 = callAuthorizationServerResource(client, false, sessionId);

                    if (result3 != null) {

                        /* デバイスプラグインならxmlファイルに有効期限が存在すれば取得して使用する */
                        Map<String, DevicePluginXmlProfile> supportProfiles = null;
                        if (params.isForDevicePlugin()) {
                            supportProfiles = DevicePluginXmlUtil.getSupportProfiles(params.getContext(),
                                    params.getContext().getPackageName());
                        }
                        
                        /* スコープを登録(Scope型に変換して有効期限を追加する) */
                        String[] scopes = params.getScopes();
                        ArrayList<Scope> settingScopes = new ArrayList<Scope>();
                        for (String scope : scopes) {
                            
                            /* デバイスプラグインならxmlファイルに有効期限が存在すれば取得して使用する(無ければデフォルト値) */
                            long expirePeriod = LocalOAuth2Settings.DEFAULT_TOKEN_EXPIRE_PERIOD;
                            if (supportProfiles != null) {
                                DevicePluginXmlProfile xmlProfile = supportProfiles.get(scope);
                                if (xmlProfile != null) {
                                    expirePeriod = xmlProfile.getExpirePeriod();
                                }
                            }
                            
                            Scope s = new Scope(scope, System.currentTimeMillis(), expirePeriod);
                            settingScopes.add(s);
                        }

                        /* 認可コード取得 */
                        String applicationName = params.getApplicationName();
                        RedirectRepresentation result4 = (RedirectRepresentation) LocalOAuth2Main
                                .callAuthPageServerResource(sessionId, settingScopes, applicationName);
                        if (result4 != null) {
                            Map<String, Object> options = result4.getOptions();
                            String authCode = (String) options.get(AuthPageServerResource.CODE);

                            if (authCode != null) {

                                /* アクセストークン取得 */
                                String accessToken = callAccessTokenServerResource(client, authCode, applicationName);
                                
                                /* トークンデータを参照 */
                                Token token = tokenManager.findTokenByAccessToken(accessToken);
                                
                                /* アクセストークンデータを返す */
                                AccessTokenScope[] accessTokenScopes = scopesToAccessTokenScopes(token.getScope());
                                AccessTokenData acccessTokenData = new AccessTokenData(accessToken, accessTokenScopes);
                                return acccessTokenData;
                            }
                        }
                    }
                }
            }
        } else {
            throw new AuthorizatonException(AuthorizatonException.CLIENT_NOT_FOUND);
        }

        return null;
    }

    /**
     * リスナーを通じてアクセストークンを返す.
     * 
     * @param params 承認確認画面パラメータ
     * @param accessTokenData アクセストークンデータ
     * @param publishAccessTokenListener アクセストークン発行リスナー
     */
    private static void callPublishAccessTokenListener(final ConfirmAuthParams params,
            final AccessTokenData accessTokenData, final PublishAccessTokenListener publishAccessTokenListener) {

        if (publishAccessTokenListener != null) {

            /* リスナーを実行してアクセストークンデータを返す */
            publishAccessTokenListener.onReceiveAccessToken(accessTokenData);
        } else {
            /* リスナーが登録されていないので通知できない */
            throw new RuntimeException("publishAccessTokenListener is null.");
        }
    }

    /**
     * リスナーを通じてアクセストークンを返す.
     * 
     * @param params 承認確認画面パラメータ
     * @param exception 例外
     * @param publishAccessTokenListener アクセストークン発行リスナー
     */
    private static void callExceptionListener(final ConfirmAuthParams params, final Exception exception,
            final PublishAccessTokenListener publishAccessTokenListener) {

        if (publishAccessTokenListener != null) {

            /* リスナーを実行して例外データを返す */
            publishAccessTokenListener.onReceiveException(exception);
            
        } else {
            /* リスナーが登録されていないので通知できない */
            throw new RuntimeException("publishAccessTokenListener is null.");
        }
    }
    
    /**
     * AuthorizationServerResourceを実行.
     * 
     * @param client クライアント情報
     * @param initialize 初期化フラグ(trueにするとContextを初期化する)
     * @param sessionId セッションID(引き継ぐセッションIDが存在すれば指定する、無ければnullを設定する)
     * @return not null: RedirectRepresentation型の戻り値を返す / null: エラー
     * @throws AuthorizatonException Authorization例外.
     */
    private static RedirectRepresentation callAuthorizationServerResource(final Client client,
            final boolean initialize, final String sessionId) throws AuthorizatonException {

        /* AuthorizationServerResourceを初期化する */
        if (initialize) {
            Context context = new Context(Logger.getLogger("LocalOAuth"));
            AuthorizationServerResource.init(context);
        }

        /* request, responseを初期化 */
        Request request = new Request();
        request.setOriginalRef(new Reference(DUMMY_ORIGINALREF));
        Response response = new Response(request);
        request.setResourceRef(new Reference(DUMMY_REFERENCE));

        /* セッションIDが指定されていたらRequestに設定する */
        if (sessionId != null) {
            Series<Cookie> cookies = new Series<Cookie>(Cookie.class);
            cookies.add(AuthorizationBaseServerResource.ClientCookieID, sessionId);
            request.setCookies(cookies);
        }

        AuthorizationServerResource.init(request, response, clientManager, tokenManager);
        
        /* Formに設定する */
        Form paramsA = new Form();
        paramsA.add(AuthorizationServerResource.CLIENT_ID, client.getClientId());
        paramsA.add(AuthorizationServerResource.REDIR_URI, DUMMY_REDIRECTURI);
        paramsA.add(AuthorizationServerResource.RESPONSE_TYPE, "code");
        paramsA.add(AuthorizationServerResource.SCOPE, SCOPE1);

        /* requestAuthorizationを実行する */
        Representation representationA = null;
        try {
            representationA = AuthorizationServerResource.requestAuthorization(paramsA);
        } catch (OAuthException e) {
            throw new AuthorizatonException(e);
        }

        /* 正常終了(ログイン画面リダイレクト) */
        if (representationA instanceof RedirectRepresentation) {
            return (RedirectRepresentation) representationA;
        }

        return null;
    }

    /**
     * LoginPageServerResourceを実行する.
     * 
     * @param userId ユーザーID
     * @param password パスワード
     * @return 戻り値(ResultRepresentation)
     */
    private static ResultRepresentation callLoginPageServerResource(final String userId, final String password) {

        /* 前の処理からセッションIDを引き継ぐ */
        String sessionId = AuthorizationServerResource.getSessionId();
        Series<Cookie> cookies = new Series<Cookie>(Cookie.class);
        cookies.add(AuthorizationBaseServerResource.ClientCookieID, sessionId);

        /* (B)の処理 */
        LoginPageServerResource.initResult();
        Request request = new Request();
        Reference requestReference = new Reference(DUMMY_ORIGINALREF);
        requestReference.addQueryParameter(LoginPageServerResource.USER_ID, userId);
        requestReference.addQueryParameter(LoginPageServerResource.PASSWORD, password);
        requestReference.addQueryParameter(LoginPageServerResource.CONTINUE,
                RedirectRepresentation.RedirectProc.requestAuthorization.toString());

        /* QueryParameterとは別の変数に値を入れているので、直接値を設定する */
        ArrayList<String> userIds = new ArrayList<String>();
        userIds.add(userId);
        ArrayList<String> passwords = new ArrayList<String>();
        passwords.add(password);
        ArrayList<String> continues = new ArrayList<String>();
        continues.add(RedirectRepresentation.RedirectProc.requestAuthorization.toString());

        LoginPageServerResource.getQuery().put(LoginPageServerResource.USER_ID, userIds);
        LoginPageServerResource.getQuery().put(LoginPageServerResource.PASSWORD, passwords);
        LoginPageServerResource.getQuery().put(LoginPageServerResource.CONTINUE, continues);

        request.setCookies(cookies);
        request.setOriginalRef(requestReference);
        request.setResourceRef(requestReference);
        Response response = new Response(request);
        LoginPageServerResource.init(request, response);
        ResultRepresentation resultRepresentation = null;
        try {
            resultRepresentation = (ResultRepresentation) LoginPageServerResource.getPage();
        } catch (OAuthException e) {
            // e.getMessage();
            // e.getErrorDescription();
            // e.printStackTrace();
            resultRepresentation = new ResultRepresentation();
            resultRepresentation.setResult(false);
            resultRepresentation.setError(e.getMessage(), e.getErrorDescription());
        }

        return resultRepresentation;
    }

    /**
     * AuthPageServerResourceを実行する.
     * 
     * @param sessionId セッションID
     * @param scopes スコープ
     * @param applicationName アプリケーション名
     * @return 戻り値(RedirectRepresentation)
     */
    private static RedirectRepresentation callAuthPageServerResource(final String sessionId,
            final ArrayList<Scope> scopes, final String applicationName) {

        Series<Cookie> cookies = new Series<Cookie>(Cookie.class);
        cookies.add(AuthorizationBaseServerResource.ClientCookieID, sessionId);

        /* scopesを文字列配列に変換する */
        ArrayList<String> strScopes = ScopeUtil.scopesToStrings(scopes);
        
        ArrayList<String> actions = new ArrayList<String>();
        actions.add(AuthPageServerResource.ACTION_ACCEPT);
        
        ArrayList<String> applicationNames = new ArrayList<String>();
        applicationNames.add(applicationName);
        
        AuthPageServerResource.getQuery().put(AuthPageServerResource.SCOPE, strScopes);
        AuthPageServerResource.getQuery().put(AuthPageServerResource.GRANTED_SCOPE, new ArrayList<String>());
        AuthPageServerResource.getQuery().put(AuthPageServerResource.ACTION, actions);
        AuthPageServerResource.getQuery().put(AuthPageServerResource.APPLICATION_NAME, applicationNames);

        Request request = new Request();
        request.setCookies(cookies);
        Response response = new Response(request);
        AuthPageServerResource.init(request, response);

        RedirectRepresentation redirectRepresentation = null;
        try {
            Representation representation = AuthPageServerResource.showPage();
            if (representation != null) {
                if (representation instanceof RedirectRepresentation) {
                    redirectRepresentation = (RedirectRepresentation) representation;
                }
            }
        } catch (OAuthException e) {
            // e.getMessage();
            // e.getErrorDescription();
            e.printStackTrace();
        }

        return redirectRepresentation;
    }

    /**
     * 認可コードを渡してアクセストークンを取得する.
     * 
     * @param client クライアント
     * @param authCode 認可コード(TokenManager.sessionsに存在するキー値を設定する)
     * @param applicationName アプリケーション名
     * @return not null: アクセストークン / null: アクセストークン取得失敗
     */
    private static String callAccessTokenServerResource(final Client client, final String authCode,
            final String applicationName) {

        /* Request / Response */
        Request request = new Request();
        ClientInfo clientInfo = new ClientInfo();
        org.restlet.security.User user = new org.restlet.security.User(client.getClientId());
        clientInfo.setUser(user);
        request.setClientInfo(clientInfo);
        Response response = new Response(request);
        AccessTokenServerResource.init(request, response);

        /* 入力値(アプリケーション名はbase64エンコードする) */
        String base64ApplicationName = Base64.encodeToString(applicationName.getBytes(), Base64.DEFAULT);
        
        StringRepresentation input = new StringRepresentation("grant_type=authorization_code&code=" + authCode + "&"
                + AccessTokenServerResource.REDIR_URI + "=" + DUMMY_REDIRECTURI + "&"
                + AccessTokenServerResource.APPLICATION_NAME + "=" + base64ApplicationName);

        /* 処理実行 */
        try {
            ResultRepresentation resultRepresentation = (ResultRepresentation) AccessTokenServerResource
                    .requestToken(input);
            if (resultRepresentation.getResult()) {
                String accessToken = resultRepresentation.getText();
                return accessToken;
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (OAuthException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    /**
     * ユーザーデータ追加.
     * 
     * @param user ユーザーID
     * @param pass パスワード
     */
    private static void addUserData(final String user, final String pass) {
        userManager.addUser(user).setPassword(pass.toCharArray());
    }

    /**
     * クライアントデータ追加.
     * 
     * @param packageInfo パッケージ名
     * @return クライアントデータ
     */
    private static Client addClientData(final PackageInfoOAuth packageInfo) {
        
        String[] redirectURIs = {DUMMY_REDIRECTURI};
        Map<String, Object> params = new HashMap<String, Object>();

        Client client = clientManager.createClient(packageInfo, ClientType.CONFIDENTIAL, redirectURIs, params);
        return client;
    }

    /**
     * クライアントデータ削除.
     * 
     * @param clientId クライアントID
     */
    private static void removeClientData(final String clientId) {

        Client client = clientManager.findById(clientId);
        if (client != null) {
            
            /* クライアントデータ削除 */
            clientManager.deleteClient(clientId);
        }
    }

    /** 承認確認画面リクエストキュー(アクセスする際はsynchronizedが必要). */
    private static List<ConfirmAuthRequest> mRequestQueue = new ArrayList<ConfirmAuthRequest>();

    /** 承認確認画面リクエストキュー用Lockオブジェクト. */
    private static Object mLockForRequstQueue = new Object();

    /**
     * 承認確認画面リクエスト数を取得.
     * 
     * @return リクエスト数
     */
    private static int countRequest() {
        int count = 0;
        synchronized (mLockForRequstQueue) {
            if (mRequestQueue != null) {
                count = mRequestQueue.size();
            }
        }
        
        return count;
    }
    
    /**
     * 承認確認画面リクエストをキューに追加.
     * 
     * @param request リクエスト
     */
    private static void enqueueRequest(final ConfirmAuthRequest request) {
        synchronized (mLockForRequstQueue) {
            mRequestQueue.add(request);
        }
    }
    
    /**
     * キュー先頭の承認確認画面リクエストをキューから取得する.
     * 
     * @return not null: 取得したリクエスト / null: キューにデータなし
     */
    private static ConfirmAuthRequest pickupRequest() {
        
        ConfirmAuthRequest request = null;
        
        synchronized (mLockForRequstQueue) {

            int requestCount = mRequestQueue.size();
            
            /* 先頭キューを返す */
            if (requestCount > 0) {
                request = mRequestQueue.get(0);
            }
        }
        
        return request;
    }
    
    /**
     * threadIdが一致する承認確認画面リクエストをキューから取得する。(キューから削除することも可能).
     * 
     * @param threadId キーとなるスレッドID
     * @param isDeleteRequest true: スレッドIDが一致したリクエストを返すと同時にキューから削除する。 / false: 削除しない。
     * @return not null: 取り出されたリクエスト / null: 該当するデータなし(存在しないthreadIdが渡された、またはキューにデータ無し)
     */
    private static ConfirmAuthRequest dequeueRequest(final long threadId, final boolean isDeleteRequest) {
        
        ConfirmAuthRequest request = null;
        
        synchronized (mLockForRequstQueue) {
            
            /* スレッドIDが一致するリクエストデータを検索する */
            int dequeueIndex = -1;
            int requestCount = mRequestQueue.size();
            for (int i = 0; i < requestCount; i++) {
                ConfirmAuthRequest req = mRequestQueue.get(i);
                if (req.getThreadId() == threadId) {
                    dequeueIndex = i;
                    break;
                }
            }
            
            if (dequeueIndex >= 0) {
                if (isDeleteRequest) {
                    /* スレッドIDに対応するリクエストデータを取得し、キューから削除する */
                    request = mRequestQueue.remove(dequeueIndex);
                } else {
                    /* スレッドIDに対応するリクエストデータを取得 */
                    request = mRequestQueue.get(dequeueIndex);
                }
            }
        }

        return request;
    }

    /**
     * クライアントデータ取得(なければAuthorizatonExceptionをスロー).
     * 
     * @param confirmAuthParams パラメータ
     * @return クライアントデータ
     * @throws AuthorizatonException Authorization例外.
     */
    private static Client getClient(final ConfirmAuthParams confirmAuthParams) throws AuthorizatonException {
        Client client = clientManager.findById(confirmAuthParams.getClientId());
        if (client == null) {
            throw new AuthorizatonException(AuthorizatonException.CLIENT_NOT_FOUND);
        }
        return client;
    }


    /**
     * Scope[]からAccessTokenScope[]に変換して返す.
     * @param scopes Scope[]の値
     * @return AccessTokenScope[]の値
     */
    private static AccessTokenScope[] scopesToAccessTokenScopes(final Scope[] scopes) {
        if (scopes != null && scopes.length > 0) {
            AccessTokenScope[] accessTokenScopes = new AccessTokenScope[scopes.length];
            for (int i = 0; i < scopes.length; i++) {
                Scope scope = scopes[i];
                accessTokenScopes[i] = new AccessTokenScope(scope.getScope(), scope.getExpirePeriod()); 
            }
            return accessTokenScopes;
        }
        return null;
    }
    
    /**
     * メッセージIDを通知する.
     * @param replyTo 通知するMessenger
     * @param messageId 通知するメッセージID
     * @param arg1 arg1(nullなら指定なし)
     * @param arg2 arg2(nullなら指定なし)
     */
    private static void sendMessageId(final Messenger replyTo, final int messageId, final Integer arg1, final Integer arg2) {
        if (replyTo != null) {
            int iArg1 = 0;
            int iArg2 = 0;
            if (arg1 != null) {
                iArg1 = arg1;
            }
            if (arg2 != null) {
                iArg2 = arg2;
            }
            Message sendMsg = Message.obtain(null, messageId, iArg1, iArg2);
            sendMsg.replyTo = mMessenger;
            
            try {
                replyTo.send(sendMsg); // send response.
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }        
    }

    /**
     * リクエストデータを使ってアクセストークン発行承認確認画面を起動する.
     * @param request リクエストデータ
     */
    private static void startConfirmAuthActivity(final ConfirmAuthRequest request) {
        
        android.content.Context context = request.getConfirmAuthParams().getContext();
        long threadId = request.getThreadId();
        ConfirmAuthParams params = request.getConfirmAuthParams();
        String[] displayScopes = request.getDisplayScopes();
        
        /* Activity起動(許可・拒否の結果は、ApprovalHandlerへ送られる) */
        final Intent intent = new Intent();
        intent.setClass(params.getContext(), ConfirmAuthActivity.class);
        intent.putExtra(ConfirmAuthActivity.EXTRA_THREADID, threadId);
        if (params.getDeviceId() != null) {
            intent.putExtra(ConfirmAuthActivity.EXTRA_DEVICEID, params.getDeviceId());
        }
        intent.putExtra(ConfirmAuthActivity.EXTRA_APPLICATIONNAME, params.getApplicationName());
        intent.putExtra(ConfirmAuthActivity.EXTRA_SCOPES, params.getScopes());
        intent.putExtra(ConfirmAuthActivity.EXTRA_DISPLAY_SCOPES, displayScopes);
        intent.putExtra(ConfirmAuthActivity.EXTRA_APPROVAL_MESSAGEID, MSG_CONFIRM_APPROVAL);
        intent.putExtra(ConfirmAuthActivity.EXTRA_CHECK_THREADID_MESSAGEID, MSG_CONFIRM_CHECK_THREADID);
        intent.putExtra(ConfirmAuthActivity.EXTRA_IS_FOR_DEVICEPLUGIN, params.isForDevicePlugin());
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
