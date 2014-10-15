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

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

/**
 * scope.
 */
public class SQLiteScopeDb {

    /** SQLフィールド名. */
    public static final String TOKENS_TOKENID_FIELD = "tokens_tokenid";

    /** SQLフィールド名. */
    public static final String PROFILES_PROFILEID_FIELD = "profiles_profileid";

    /** SQLフィールド名. */
    public static final String TIMESTAMP_FIELD = "timestamp";

    /** SQLフィールド名. */
    public static final String EXPIRE_PERIOD_FIELD = "expire_period";

    /** SQLフィールド名一覧. */
    public static final String[] CLIENT_ALL_FIELIDS = {
        TOKENS_TOKENID_FIELD,
        PROFILES_PROFILEID_FIELD,
        TIMESTAMP_FIELD,
        EXPIRE_PERIOD_FIELD,
    };
    
    /** SQLフィールドのデータ型. */
    public static final String DATA_TYPE_STRING = "[STRING]";
    
    /** SQLフィールドのデータ型. */
    public static final String DATA_TYPE_LONG = "[LONG]";
    
    
    /** tokensテーブルのtokenID. */
    private long mTokensTokenId;
    
    /** profilesテーブルのプロファイルID. */
    private long mProfilesProfileId;

    /**
     * トークン更新日時.
     * - 新規作成時およびアクセストークン再承認されたときに更新する。
     * - 1970/1/1 00:00:00 UTC からの経過ミリ秒。System.currentTimeMillis()で取得した値。
     */
    private long mTimestamp;

    /** トークン有効期間(sec単位). */
    private long mExpirePeriod = 0;
    
    
    /**
     * コンストラクタ.
     */
    public SQLiteScopeDb() {
        mTokensTokenId = 0;
        mProfilesProfileId = 0;
        mTimestamp = System.currentTimeMillis();
        mExpirePeriod = 0;
    }
    
    /**
     * コンストラクタ.
     * @param tokensTokenId トークンID
     * @param profilesProfileId プロファイルID
     * @param timestamp タイムスタンプ
     * @param expirePeriod 有効期限[sec]
     */
    protected SQLiteScopeDb(final long tokensTokenId, final long profilesProfileId, final long timestamp,
            final long expirePeriod) {
        mTokensTokenId = tokensTokenId;
        mProfilesProfileId = profilesProfileId;
        mTimestamp = timestamp;
        mExpirePeriod = expirePeriod;
    }
    
    /**
     * トークンID取得.
     * @return トークンID
     */
    public long getTokensTokenId() {
        return mTokensTokenId;
    }
    
    /**
     * トークンID設定.
     * @param tokensTokenId    トークンID
     */
    public void setTokensTokenId(final long tokensTokenId) {
        mTokensTokenId = tokensTokenId;
    }
    
    /**
     * プロファイルID設定.
     * @return プロファイルID
     */
    public long getProfilesProfileId() {
        return mProfilesProfileId;
    }
    
    /**
     * プロファイルID設定.
     * @param profilesProfileId    プロファイルID
     */
    public void setProfilesProfileId(final long profilesProfileId) {
        mProfilesProfileId = profilesProfileId;
    }
    
    /**
     * タイムスタンプ取得.
     * @return  タイムスタンプ取得
     */
    public long getTimestamp() {
        return mTimestamp;
    }
    
    /**
     * タイムスタンプ設定.
     * @param timestamp  タイムスタンプ
     */
    public void setTimestamp(final long timestamp) {
        mTimestamp = timestamp;
    }

    /**
     * 有効期限を取得.
     * @return 有効期限[sec]
     */
    public long getExpirePeriod() {
        return mExpirePeriod;
    }

    /**
     * 有効期限を設定.
     * @param expirePeriod      有効期限[sec]
     */
    public void setExpirePeriod(final int expirePeriod) {
        this.mExpirePeriod = expirePeriod;
    }

    /**
     * 有効期限内か判定.
     * 
     * @return true: 有効期限内 / false: 有効期限切れ
     */
    public boolean isExpired() {
        long elapsedTime = System.currentTimeMillis() - mTimestamp;
        long timeout = mExpirePeriod;
        if (elapsedTime > timeout) {
            return true;
        }
        return false;
    }
    
    /**
     * scopesテーブルの新規レコードで本データを追加する.
     * 
     * @param db データベース
     */
    public void dbInsert(final SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(TOKENS_TOKENID_FIELD, mTokensTokenId);
        values.put(PROFILES_PROFILEID_FIELD, mProfilesProfileId);
        values.put(TIMESTAMP_FIELD, mTimestamp);
        values.put(EXPIRE_PERIOD_FIELD, mExpirePeriod);
        try {
            db.insert(LocalOAuthOpenHelper.SCOPES_TABLE, null, values);
        } catch (SQLiteException e) {
            throw e;
        }
    }
    
    /**
     * scopesテーブル上のレコードのtimestampを更新する.
     * 
     * @param db データベース
     */
    public void dbReplaceTimestamp(final SQLiteDatabase db) {
        
        ContentValues values = new ContentValues();
        values.put(TIMESTAMP_FIELD, mTimestamp);
        
        String where = SQLiteScopeDb.TOKENS_TOKENID_FIELD + " = " + mTokensTokenId + " and "
                + SQLiteScopeDb.PROFILES_PROFILEID_FIELD + " = " + mProfilesProfileId;
        
        try {
            db.update(LocalOAuthOpenHelper.SCOPES_TABLE, values, where, null);
        } catch (SQLiteException e) {
            throw e;
        }
    }
}
