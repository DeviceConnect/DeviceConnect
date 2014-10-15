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

package org.deviceconnect.android.localoauth.oauthserver.db;

import java.util.Calendar;

import org.restlet.ext.oauth.internal.Scope;
import org.restlet.ext.oauth.internal.ServerToken;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

/**
 * token.
 */
public class SQLiteToken implements ServerToken {

    /** SQLフィールド名. */
    public static final String ID_FIELD = "id";
    
    /** SQLフィールド名. */
    public static final String ACCCESS_TOKEN_FIELD = "access_token";

    /** SQLフィールド名. */
    public static final String TOKEN_TYPE_FIELD = "token_type";

    /** SQLフィールド名. */
    public static final String CLIENTID_FIELD = "client_id";

    /** SQLフィールド名. */
    public static final String USERS_USERID_FIELD = "users_userid";

    /** SQLフィールド名. */
    public static final String REGISTRATION_DATE_FIELD = "registration_date";

    /** SQLフィールド名. */
    public static final String ACCESS_DATE_FIELD = "access_date";

    /** SQLフィールド名. */
    public static final String APPLICATION_NAME_FIELD = "application_name";

    /** SQLフィールド名一覧. */
    public static final String[] TOKEN_ALL_FIELIDS = {
        ID_FIELD,
        ACCCESS_TOKEN_FIELD,
        TOKEN_TYPE_FIELD,
        CLIENTID_FIELD,
        USERS_USERID_FIELD,
        REGISTRATION_DATE_FIELD,
        ACCESS_DATE_FIELD,
        APPLICATION_NAME_FIELD
    };
    
    /** SQLフィールドのデータ型. */
    public static final String DATA_TYPE_STRING = "[STRING]";
    
    /** SQLフィールドのデータ型. */
    public static final String DATA_TYPE_LONG = "[LONG]";
    
    
    
    
    /** ID. */
    private long mId = 0;

    /** アクセストークン(未発行ならnull). */
    private String mAccessToken = null;

    /** トークンタイプ("code"を設定). */
    private String mTokenType = null;

    /** リフレッシュトークン(未使用のためnullを設定). */
    private String mRefreshToken = null;

    /** スコープ配列. */
    private Scope[] mScope = null;

    /** クライアントID. */
    private String mClientId = null;

    /** ユーザー名(1件固定なので今回はnullを設定). */
    private String mUsername = null;

    /** 登録日時(1970/1/1 00:00:00 UTC からの経過ミリ秒。System.currentTimeMillis()で取得した値). */
    private long mRegistrationDate = 0;

    /** アクセス日時(1970/1/1 00:00:00 UTC からの経過ミリ秒。System.currentTimeMillis()で取得した値). */
    private long mAccessDate = 0;

    /** アプリケーション名. */
    private String mApplicationName = null;

    
    
    /**
     * コンストラクタ.
     */
    protected SQLiteToken() {
        long currentTimeMillis = System.currentTimeMillis(); 
        mRegistrationDate = currentTimeMillis; 
        mAccessDate = currentTimeMillis;
    }

    /**
     * ID設定.
     * @param id    ID
     */
    public void setId(final long id) {
        mId = id;
    }
    
    /**
     * ID取得.
     * @return  ID
     */
    public long getId() {
        return mId;
    }

    /**
     * @return the accessToken
     */
    public String getAccessToken() {
        return mAccessToken;
    }
    
    /**
     * @param accessToken the accessToken to set
     */
    public void setAccessToken(final String accessToken) {
        this.mAccessToken = accessToken;
    }

    /**
     * @return the tokenType
     */
    public String getTokenType() {
        return mTokenType;
    }

    /**
     * トークンタイプを設定.
     * @param tokenType the tokenType to set
     */
    public void setTokenType(final String tokenType) {
        this.mTokenType = tokenType;
    }
    
    /**
     * @return the refreshToken
     */
    public String getRefreshToken() {
        return mRefreshToken;
    }

    /**
     * @param refreshToken the refreshToken to set
     */
    public void setRefreshToken(final String refreshToken) {
        this.mRefreshToken = refreshToken;
    }

    /**
     * @return the scope
     */
    public Scope[] getScope() {
        return mScope;
    }

    /**
     * @param scope the scope to set
     */
    public void setScope(final Scope[] scope) {
        this.mScope = scope;
    }

    /**
     * @return the clientId
     */
    public String getClientId() {
        return mClientId;
    }

    /**
     * @param clientId the clientId to set
     */
    public void setClientId(final String clientId) {
        this.mClientId = clientId;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return mUsername;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(final String username) {
        this.mUsername = username;
    }
    
    /**
     * 登録日時取得.
     * @return  登録日時
     */
    public long getRegistrationDate() {
        return mRegistrationDate;
    }
    
    /**
     * 登録日時設定.
     * @param registrationDate      登録日時
     */
    public void setRegistrationDate(final long registrationDate) {
        mRegistrationDate = registrationDate;
    }
    
    /**
     * アクセス日時設定.
     * @param accessDate      アクセス日時
     */
    public void setAccessDate(final long accessDate) {
        mAccessDate = accessDate;
    }
    
    /**
     * アプリケーション名を返す.
     * @return アプリケーション名
     */
    public String getApplicationName() {
        return mApplicationName;
    }
    
    /**
     * アプリケーション名を設定する.
     * @param applicationName アプリケーション名
     */
    public void setApplicationName(final String applicationName) {
        this.mApplicationName = applicationName;
    }
    
    /**
     * tokensテーブルの新規レコードで本データを追加しIDを取得する.
     * 
     * @param db データベース
     */
    public void dbInsert(final SQLiteDatabase db) {

        ContentValues values = new ContentValues();
        values.put(ACCCESS_TOKEN_FIELD, mAccessToken);
        values.put(TOKEN_TYPE_FIELD, mTokenType);
        values.put(CLIENTID_FIELD, mClientId);
        values.put(USERS_USERID_FIELD, LocalOAuthOpenHelper.USERS_USER_ID);
        values.put(REGISTRATION_DATE_FIELD, mRegistrationDate);
        values.put(ACCESS_DATE_FIELD, mAccessDate);
        values.put(APPLICATION_NAME_FIELD, mApplicationName);
        
        long tokenId = -1;
        try {
            tokenId = db.insert(LocalOAuthOpenHelper.TOKENS_TABLE, null, values);
        } catch (SQLiteException e) {
            throw e;
        }
        if (tokenId < 0) {
            throw new SQLiteException("SQLiteException - insert error.");
        }
        this.mId = tokenId;
    }

    /**
     * tokensテーブルの既存レコードを更新する.
     * 
     * @param db データベース
     */
    public void dbUpdate(final SQLiteDatabase db) {
        /* アクセス時間を更新 */
        mAccessDate = System.currentTimeMillis();
        mRegistrationDate  = System.currentTimeMillis();

        /* tokensレコード更新 */
        ContentValues values = new ContentValues();
        values.put(ACCCESS_TOKEN_FIELD, mAccessToken);
        values.put(TOKEN_TYPE_FIELD, mTokenType);
        values.put(CLIENTID_FIELD, mClientId);
        values.put(USERS_USERID_FIELD, LocalOAuthOpenHelper.USERS_USER_ID);
        values.put(ACCESS_DATE_FIELD, mAccessDate);
        values.put(REGISTRATION_DATE_FIELD, mRegistrationDate);
        values.put(APPLICATION_NAME_FIELD, mApplicationName);
        dbUpdate(db, values, mId);
        
    }

    /**
     * トークンのアクセス時間を更新する.
     * 
     * @param db データベース
     */
    public void dbUpdateTokenAccessTime(final SQLiteDatabase db) {
        /* アクセス時間を更新 */
        mAccessDate = System.currentTimeMillis();

        /* tokensレコード更新 */
        ContentValues values = new ContentValues();
        values.put(ACCESS_DATE_FIELD, mAccessDate);
        dbUpdate(db, values, mId);
    }

    
    /**
     * 有効期限表示文字列を返す.
     * @return 有効期限表示文字列
     */
    public String getExpirePeriod() {
        Long expirePeriodDateMin = null;    /* スコープ毎の有効期限(最小値) */
        Long expirePeriodDateMax = null;    /* スコープ毎の有効期限(最大値) */
        Scope[] scopes = getScope();
        if (scopes != null) {
            for (int i = 0; i < scopes.length; i++) {
                long e = scopes[0].getTimestamp() + scopes[0].getExpirePeriod();
                
                if (expirePeriodDateMin == null) {
                    expirePeriodDateMin = e;
                    expirePeriodDateMax = e;
                } else {
                    if (e < expirePeriodDateMin) {
                        expirePeriodDateMin = e;
                    }
                    if (expirePeriodDateMax < e) {
                        expirePeriodDateMax = e;
                    }
                }
            }
        }
        
        String displayExpirePeriod = "";
        if (expirePeriodDateMin != null) {
            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTimeInMillis(expirePeriodDateMin);
            cal2.setTimeInMillis(expirePeriodDateMax);
            
            displayExpirePeriod = String.format("有効期限 %1$d年%2$d月%3$d日"
                    , cal1.get(Calendar.YEAR), cal1.get(Calendar.MONTH) + 1, cal1.get(Calendar.DAY_OF_MONTH));
            
            boolean isEqualYear = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
            boolean isEqualMonth = cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
            boolean isEqualDay = cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
            if (!isEqualYear) {
                isEqualMonth = false;
                isEqualDay = false;
            } else if (!isEqualMonth) {
                isEqualDay = false;
            }
            
            if (!isEqualYear || !isEqualMonth || !isEqualDay) {
                displayExpirePeriod += "〜";
            }
            if (!isEqualYear) {
                displayExpirePeriod += cal2.get(Calendar.YEAR);
                displayExpirePeriod += "年";
            }
            if (!isEqualMonth) {
                displayExpirePeriod += cal2.get(Calendar.MONTH) + 1;
                displayExpirePeriod += "月";
            }
            if (!isEqualDay) {
                displayExpirePeriod += cal2.get(Calendar.DAY_OF_MONTH);
                displayExpirePeriod += "日";
            }
            displayExpirePeriod += "まで";
        }
        
        return displayExpirePeriod;
    }
    
    
    /**
     * tokensテーブルの既存レコードを更新する.
     * 
     * @param db データベース
     * @param values 設定値
     * @param id tokensのid(更新したいレコードを識別する)
     */
    private void dbUpdate(final SQLiteDatabase db, final ContentValues values, final long id)  {
        
        String where = ID_FIELD + " = " + mId;
        
        try {
            /*int updateCount = */db.update(LocalOAuthOpenHelper.TOKENS_TABLE, values, where, null);
        } catch (SQLiteException e) {
            throw e;
        }
        
    }

    /**
     * アクセストークンが存在するか確認(全スコープの有効期限が切れていたら有効期限切れとみなす).
     * @return true: 有効期限切れ / false: 有効期限内
     */
    @Override
    public boolean isExpired() {
        boolean result = true;
        for (Scope scope : getScope()) {
            if (!scope.isExpired()) {
                result = false;
                break;
            }
        }
        return result;
    }

    /**
     * 初回アクセスか判定(登録日時とアクセス日時が一致すればまだアクセスされていないトークンである).
     * @return true: 初回アクセスである / false: 初回アクセスではない 
     */
    public boolean isFirstAccess() {
        if (mRegistrationDate > 0 && mAccessDate > 0 && mRegistrationDate == mAccessDate) {
            return true;
        }
        return false;
    }
}
