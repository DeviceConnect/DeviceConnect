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
 * profile.
 */
public class SQLiteProfile {

    /** SQLフィールド名. */
    public static final String ID_FIELD = "id";

    /** SQLフィールド名. */
    public static final String PROFILE_NAME_FIELD = "profile_name";

    /** SQLフィールド名. */
    public static final String DESCRIPTION_FIELD = "description";

    /** SQLフィールド名一覧. */
    public static final String[] PROFILE_ALL_FIELIDS = {
        ID_FIELD,
        PROFILE_NAME_FIELD,
        DESCRIPTION_FIELD,
    };
    
    /** SQLフィールドのデータ型. */
    public static final String DATA_TYPE_STRING = "[STRING]";
    
    /** SQLフィールドのデータ型. */
    public static final String DATA_TYPE_LONG = "[LONG]";
    
    
    /** ID. */
    private long mId;
    
    /** プロファイル名. */
    private String mProfileName;
    
    /** description. */
    private String mDescription;
    
    
    /**
     * コンストラクタ.
     */
    public SQLiteProfile() {
        mId = 0;
        mProfileName = null;
        mDescription = null;
    }
    
    /**
     * コンストラクタ.
     * @param profileName プロファイル名
     * @param description description
     */
    protected SQLiteProfile(final String profileName, final String description) {
        mId = 0;
        mProfileName = profileName;
        mDescription = description;
    }
    
    /**
     * ID取得.
     * @return ID
     */
    public long getId() {
        return mId;
    }
    
    /**
     * ID設定.
     * @param id    ID
     */
    public void setId(final long id) {
        mId = id;
    }
    
    /**
     * プロファイル名取得.
     * @return プロファイル名
     */
    public String getProfileName() {
        return mProfileName;
    }
    
    /**
     * プロファイル名設定.
     * @param profileName    プロファイル名
     */
    public void setProfileName(final String profileName) {
        mProfileName = profileName;
    }
    
    /**
     * description設定.
     * @return description
     */
    public String getDescription() {
        return mDescription;
    }
    
    /**
     * description設定.
     * @param description    description
     */
    public void setDescription(final String description) {
        mDescription = description;
    }
    
    /**
     * profilesテーブルの新規レコードで本データを追加しIDを取得する.
     * 
     * @param db データベース
     */
    public void dbInsert(final SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(PROFILE_NAME_FIELD, mProfileName);
        values.put(DESCRIPTION_FIELD, mDescription);
        long profileId = -1;
        try {
            profileId = db.insert(LocalOAuthOpenHelper.PROFILES_TABLE, null, values);
        } catch (SQLiteException e) {
            throw e;
        }
        if (profileId < 0) {
            throw new SQLiteException("SQLiteException - insert error.");
        }
        this.mId = profileId;
    }
}
