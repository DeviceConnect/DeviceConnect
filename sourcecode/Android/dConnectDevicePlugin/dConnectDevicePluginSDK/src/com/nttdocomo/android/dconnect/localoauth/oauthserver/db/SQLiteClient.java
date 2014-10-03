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

package com.nttdocomo.android.dconnect.localoauth.oauthserver.db;

import java.util.Map;
import org.restlet.ext.oauth.GrantType;
import org.restlet.ext.oauth.PackageInfoOAuth;
import org.restlet.ext.oauth.ResponseType;
import org.restlet.ext.oauth.internal.Client;

import com.nttdocomo.android.dconnect.localoauth.LocalOAuth2Main;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

/**
 * RestletのMemoryClientをベースにSQLite版Clientを追加.
 */
public class SQLiteClient implements Client {

    /** SQLフィールド名. */
    public static final String ID_FIELD = "id";
    
    /** SQLフィールド名. */
    public static final String CLIENTID_FIELD = "client_id";

    /** SQLフィールド名. */
    public static final String PACKAGENAME_FIELD = "package_name";

    /** SQLフィールド名. */
    public static final String DEVICEID_FIELD = "device_id";

    /** SQLフィールド名. */
    public static final String CLIENTSECRET_FIELD = "client_secret";

    /** SQLフィールド名. */
    public static final String CLIENTTYPE_FIELD = "client_type";

    /** SQLフィールド名. */
    public static final String REGISTRATION_DATE_FIELD = "registration_date";

    /** SQLフィールド名一覧. */
    public static final String[] CLIENT_ALL_FIELIDS = {
        ID_FIELD,
        CLIENTID_FIELD,
        PACKAGENAME_FIELD,
        DEVICEID_FIELD,
        CLIENTSECRET_FIELD,
        CLIENTTYPE_FIELD,
        REGISTRATION_DATE_FIELD
    };
    
    /** SQLフィールドのデータ型. */
    public static final String DATA_TYPE_STRING = "[STRING]";
    
    /** SQLフィールドのデータ型. */
    public static final String DATA_TYPE_LONG = "[LONG]";
    
    
    /** ID. */
    private long mId;
    
    /** クライアントID. */
    private String mClientId;
    
    /** パッケージ情報. */
    private PackageInfoOAuth mPackageInfo;

    /** クライアントシークレット. */
    private char[] mClientSecret;

    /** クライアントタイプ. */
    private ClientType mClientType;

    /** 登録日時(1970/1/1 00:00:00 UTC からの経過ミリ秒。System.currentTimeMillis()で取得した値). */
    private long mRegistrationDate = 0;
    
    /**
     * コンストラクタ.
     */
    public SQLiteClient() {
        mId = 0;
        mClientId = null;
        mPackageInfo = null;
        mClientSecret = null;
        mClientType = ClientType.CONFIDENTIAL;
        mRegistrationDate = System.currentTimeMillis();
    }
    
    /**
     * コンストラクタ.
     * @param clientId クライアントID
     * @param packageInfo パッケージ情報
     * @param clientType クライアントタイプ
     * @param redirectURIs リダイレクトURIs
     * @param properties プロパティ
     */
    protected SQLiteClient(final String clientId, final PackageInfoOAuth packageInfo, final ClientType clientType,
            final String[] redirectURIs, final Map<String, Object> properties) {
        this.mId = 0;
        this.mClientId = clientId;
        this.mPackageInfo = packageInfo;
        this.mClientType = clientType;
        mRegistrationDate = System.currentTimeMillis();
    }

    /**
     * IDを返す.
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
     * クライアントID取得.
     * @return クライアントID
     */
    public String getClientId() {
        return mClientId;
    }

    /**
     * クライアントID設定.
     * @param clientId  クライアントID
     */
    public void setClientId(final String clientId) {
        mClientId = clientId;
    }
    
    /**
     * パッケージ情報を取得.
     * @return	パッケージ名
     */
    public PackageInfoOAuth getPackageInfo() {
        return mPackageInfo;
    }

    /**
     * パッケージ情報設定.
     * @param packageInfo   パッケージ情報
     */
    public void setPackageInfo(final PackageInfoOAuth packageInfo) {
        mPackageInfo = packageInfo;
    }
    
    /**
     * クライアントシークレット取得.
     * @return クライアントシークレット
     */
    public char[] getClientSecret() {
        return mClientSecret;
    }

    /**
     * クライアントシークレット設定.
     * @param clientSecret クライアントシークレット
     */
    protected void setClientSecret(final char[] clientSecret) {
        this.mClientSecret = clientSecret;
    }

    /**
     * リダイレクトURIs取得.
     * @return リダイレクトURIs
     */
    public String[] getRedirectURIs() {
        String[] redirectURIs = new String[] {LocalOAuth2Main.DUMMY_REDIRECTURI};
        return redirectURIs;
    }

    /**
     * リダイレクトURIs設定.
     */
    public void setRedirectURIs(final String[] redirectURIs) {

    }
    
    
    /**
     * プロパティ取得.
     * @return プロパティ
     */
    public Map<String, Object> getProperties() {
        return null;
    }

    /**
     * レスポンスタイプが指定されたものと一致するか.
     * @param responseType レスポンスタイプ
     * @return true: 一致する / false: 一致しない
     */
    public boolean isResponseTypeAllowed(final ResponseType responseType) {
        boolean result = responseType == ResponseType.code;
        return result;
        
    }

    /**
     * グラントタイプが指定されたものと一致するか.
     * @param grantType グラントタイプ
     * @return true: 一致する / false: 一致しない
     */
    public boolean isGrantTypeAllowed(final GrantType grantType) {
        boolean result = grantType == GrantType.authorization_code;
        return result;
    }

    /**
     * クライアントタイプ取得.
     * @return クライアントタイプ
     */
    public ClientType getClientType() {
        return mClientType;
    }

    /**
     * クライアントタイプ設定.
     * @param clientType    クライアントタイプ
     */
    public void setClientType(final ClientType clientType) {
        mClientType = clientType;
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
     * clientsテーブルの新規レコードで本データを追加しIDを取得する.
     * 
     * @param db データベース
     * @exception SQLiteException SQLite処理で発生した例外.
     */
    public void dbInsert(final SQLiteDatabase db) throws SQLiteException {
        
        String clientSecret = String.copyValueOf(mClientSecret);
        
        ContentValues values = new ContentValues();
        values.put(CLIENTID_FIELD, mClientId);
        values.put(PACKAGENAME_FIELD, mPackageInfo.getPackageName());
        values.put(DEVICEID_FIELD, mPackageInfo.getDeviceId());
        values.put(CLIENTSECRET_FIELD, clientSecret);
        values.put(CLIENTTYPE_FIELD, mClientType.ordinal());
        values.put(REGISTRATION_DATE_FIELD, mRegistrationDate);
        long clientId = -1;
        try {
            clientId = db.insert(LocalOAuthOpenHelper.CLIENTS_TABLE, null, values);
        } catch (SQLiteException e) {
            throw e;
        }
        if (clientId < 0) {
            throw new SQLiteException("SQLiteException - insert error.");
        }
        this.mId = clientId;
    }
}
