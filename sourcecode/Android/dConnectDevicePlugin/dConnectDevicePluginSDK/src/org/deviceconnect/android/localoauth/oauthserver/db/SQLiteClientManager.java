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

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.restlet.ext.oauth.PackageInfoOAuth;
import org.restlet.ext.oauth.internal.AbstractClientManager;
import org.restlet.ext.oauth.internal.Client;
import org.restlet.ext.oauth.internal.Client.ClientType;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;

/**
 * SQLite版ClientManager(RestletのMemoryClientManagerをベースに実装).
 */
public class SQLiteClientManager extends AbstractClientManager {

    /**
     * DBオブジェクト.
     */
    private SQLiteDatabase mDb = null;
    
    /**
     * DBオブジェクトを設定する.
     * 
     * @param db DBオブジェクト
     */
    public void setDb(final SQLiteDatabase db) {
        mDb = db;
    }

    /**
     * クライアント作成.
     * @param packageInfo パッケージ情報
     * @param clientId クライアントID
     * @param clientSecret クライアントシークレット
     * @param clientType クライアントタイプ
     * @param redirectURIs リダイレクトURIs
     * @param properties プロパティ
     * @return クライアントデータ
     */
    protected Client createClient(final PackageInfoOAuth packageInfo, final String clientId, final char[] clientSecret,
            final ClientType clientType, final String[] redirectURIs,
            final Map<String, Object> properties) {
        
        /* clientデータ設定 */
        SQLiteClient client = new SQLiteClient(UUID.randomUUID().toString(),
        		packageInfo, clientType, redirectURIs, properties);
        if (clientSecret != null) {
            client.setClientSecret(clientSecret);
        }

        /* DBに1件追加(失敗したらSQLiteException発生) */
        client.dbInsert(mDb);
        
        return client;
    }

    /**
     * クライアントIDをキーにクライアントデータ削除.
     * @param id クライアントID
     */
    public void deleteClient(final String id) {
        if (mDb != null) {
            Bundle where = new Bundle();
            where.putString(SQLiteClient.DATA_TYPE_STRING + "," + SQLiteClient.CLIENTID_FIELD, id);
            dbDeleteClients(mDb, where);
        } else {
            throw new SQLiteException("DBがオープンされていません。");
        }
    }

    /**
     * 長時間利用されていないクライアントをクリーンアップする.<br>
     * (1)clientsテーブルにあるがtokensテーブルにトークンが無い場合、clientsの登録日時がしきい値を越えていたら削除する。<br>
     * (2)clientsテーブルにあるがtokensテーブルにトークンが有る場合、tokensの登録日時がしきい値を越えていたら削除する。<br>
     * ※(2)の場合、トークンの有効期限内なら削除しない。
     * @param clientCleanupTime     クライアントをクリーンアップする未アクセス時間[sec].
     */
    public void cleanupClient(final int clientCleanupTime) {
        if (mDb != null) {
            final long msec = 1000; /* 1[sec] = 1000[msec] */
            final long cleanupTime = System.currentTimeMillis() - clientCleanupTime * msec;
            
            /* (1)に該当するclientsレコードを削除する */
            String sql1 = "delete from clients where "
                    + "not exists ( select * from tokens where clients.client_id = tokens.client_id ) "
                    + "and clients.registration_date < " + cleanupTime + ";";
            mDb.execSQL(sql1);
            
            /* (2)に該当するclientsレコードを削除する */
            String sql2 = "delete from clients where "
                    + "exists (select * from tokens where clients.client_id = tokens.client_id) "
                    + "and not exists ( select * from tokens, scopes "
                    + "where clients.client_id = tokens.client_id and tokens.id = scopes.tokens_tokenid "
                    + "and (scopes.timestamp + scopes.expire_period) > " + cleanupTime + ")";
            mDb.execSQL(sql2);
            
            /* (2)で削除されたclientsのtokensレコードを削除する(clientsがリンク切れしたtokensとscopesを削除する) */
            String sql3 = "delete from scopes where not exists ("
                    + "select * from tokens, clients "
                    + "where scopes.tokens_tokenid = tokens.id and tokens.client_id = clients.client_id);";
            mDb.execSQL(sql3);
            String sql4 = "delete from tokens where not exists ("
                    + "select * from clients where tokens.client_id = clients.client_id);";
            mDb.execSQL(sql4);
            
        } else {
            throw new SQLiteException("DBがオープンされていません。");
        }
    }

    /**
     * クライアントIDをキーにDB検索し該当するクライアントを返す.
     * @param id クライアントID
     * @return  not null: クライアントIDが一致するクライアント / null: クライアントIDが一致するクライアント無し
     */
    public Client findById(final String id) {
        if (mDb != null) {
            Bundle where = new Bundle();
            where.putString(SQLiteClient.DATA_TYPE_STRING + "," + SQLiteClient.CLIENTID_FIELD, id);
            Client[] clients = dbLoadClients(mDb, where);
            if (clients == null || clients.length == 0) {
                return null;
            } else if (clients.length == 1) {
                return clients[0];
            } else {
                throw new SQLiteException("クライアントIDが2件以上のクライアントデータに設定されています。");
            }
            
        } else {
            throw new SQLiteException("DBがオープンされていません。");
        }
    }
    
    /**
     * デバイスIDをキーにDB検索し該当するクライアントを返す.
     * @param deviceId デバイスID
     * @return  not null: デバイスIDが一致するクライアント / null: デバイスIDが一致するクライアント無し
     */
    public Client findByDeviceId(final String deviceId) {
        if (mDb != null) {
            Bundle where = new Bundle();
            where.putString(SQLiteClient.DATA_TYPE_STRING + "," + SQLiteClient.DEVICEID_FIELD, deviceId);
            Client[] clients = dbLoadClients(mDb, where);
            if (clients == null || clients.length == 0) {
                return null;
            } else if (clients.length == 1) {
                return clients[0];
            } else {
                throw new SQLiteException("クライアントIDが2件以上のクライアントデータに設定されています。");
            }
            
        } else {
            throw new SQLiteException("DBがオープンされていません。");
        }
    }
    
    
    
    /**
     * 有効なclientsレコード数をカウントして返す.
     * @return 有効なclientsレコード数
     */
    public int countClients() {
        if (mDb != null) {
            int count = dbCountClients();
            return count;
        } else {
            throw new SQLiteException("DBがオープンされていません。");
        }
    }
    
    /**
     * パッケージ情報をキーにDB検索し該当するクライアントを返す。(追加).
     * @param	packageInfo	パッケージ情報
     * @return	not null: パッケージ情報が一致するクライアント / null: パッケージ情報が一致するクライアント無し
     */
    @Override
    public Client findByPackageInfo(final PackageInfoOAuth packageInfo) {
        if (mDb != null) {
            Bundle where = new Bundle();
            where.putString(SQLiteClient.DATA_TYPE_STRING + "," + SQLiteClient.PACKAGENAME_FIELD,
                    packageInfo.getPackageName());
            where.putString(SQLiteClient.DATA_TYPE_STRING + "," + SQLiteClient.DEVICEID_FIELD,
                    packageInfo.getDeviceId());
            SQLiteClient[] clients = dbLoadClients(mDb, where);
            if (clients == null || clients.length == 0) {
                return null;
            } else if (clients.length == 1) {
                return clients[0];
            } else {
                throw new SQLiteException("クライアントIDが2件以上のクライアントデータに設定されています。");
            }
            
        } else {
            throw new SQLiteException("DBがオープンされていません。");
        }
    }


    /**
     * 指定条件でclientデータをDBから読み込む.
     * 
     * @param db DBオブジェクト
     * @param where where条件(key = value)。複数あるときはAnd条件になる。
     * @return 条件に一致したclientデータ配列。該当データが0件ならnull。
     */
    private SQLiteClient[] dbLoadClients(final SQLiteDatabase db, final Bundle where) {

        SQLiteClient[] result = null;

        String tables = LocalOAuthOpenHelper.CLIENTS_TABLE;
        String[] columns = SQLiteClient.CLIENT_ALL_FIELIDS;
        String selection = getSelection(where);
        Cursor c = db.query(tables, columns, selection, null, null, null, null);
        if (c.moveToFirst()) {
            int count = c.getCount();
            if (count > 0) {
                result = new SQLiteClient[count];
                for (int i = 0; i < count; i++) {

                    SQLiteClient client = new SQLiteClient();

                    final int idColumnIndex = c.getColumnIndex(SQLiteClient.ID_FIELD);
                    if (!c.isNull(idColumnIndex)) {
                        client.setId(c.getLong(idColumnIndex));
                    }

                    final int clientIdColumnIndex = c.getColumnIndex(SQLiteClient.CLIENTID_FIELD);
                    if (!c.isNull(clientIdColumnIndex)) {
                        client.setClientId(c.getString(clientIdColumnIndex));
                    }

                    String packageName = null;
                    final int packageNameColumnIndex = c.getColumnIndex(SQLiteClient.PACKAGENAME_FIELD);
                    if (!c.isNull(packageNameColumnIndex)) {
                        packageName = c.getString(packageNameColumnIndex);
                    }

                    String deviceId = null;
                    final int deviceIdColumnIndex = c.getColumnIndex(SQLiteClient.DEVICEID_FIELD);
                    if (!c.isNull(deviceIdColumnIndex)) {
                        deviceId = c.getString(deviceIdColumnIndex);
                    }

                    PackageInfoOAuth packageInfo = new PackageInfoOAuth(packageName, deviceId);
                    client.setPackageInfo(packageInfo);

                    final int clientSecretColumnIndex = c.getColumnIndex(SQLiteClient.CLIENTSECRET_FIELD);
                    if (!c.isNull(clientSecretColumnIndex)) {
                        client.setClientSecret(c.getString(clientSecretColumnIndex).toCharArray());
                    }

                    final int clientTypeColumnIndex = c.getColumnIndex(SQLiteClient.CLIENTTYPE_FIELD);
                    if (!c.isNull(clientTypeColumnIndex)) {
                        client.setClientType(ClientType.values()[c.getInt(clientTypeColumnIndex)]);
                    }

                    final int registrationDateColumnIndex = c.getColumnIndex(SQLiteClient.REGISTRATION_DATE_FIELD);
                    if (!c.isNull(registrationDateColumnIndex)) {
                        client.setRegistrationDate(c.getLong(registrationDateColumnIndex));
                    }

                    result[i] = client;

                    c.moveToNext();
                }
            }

        }

        return result;
    }

    /**
     * 指定条件でクライアントデータをDBから読み込む.
     * 
     * @param db DBオブジェクト
     * @param where where条件(key = value)。複数あるときはAnd条件になる。
     */
    private void dbDeleteClients(final SQLiteDatabase db, final Bundle where) {
        String table = LocalOAuthOpenHelper.CLIENTS_TABLE;
        String selection = getSelection(where);
        db.delete(table, selection, null);
    }

    /**
     * 有効なclientsレコード数をカウントして返す.
     * @return 有効なclientsレコード数
     */
    private int dbCountClients() {
        if (mDb != null) {
            String sql = "select count(*) from " + LocalOAuthOpenHelper.CLIENTS_TABLE;
            Cursor c = mDb.rawQuery(sql, null);
            c.moveToLast();
            int count = c.getInt(0);
            c.close();
            return count;
        } else {
            throw new SQLiteException("DBがオープンされていません。");
        }
    }
    
    /**
     * Bundle型の変数に設定したwhere条件をSQLのselection部に設定する文字列に変換して返す.
     * 
     * @param where where条件
     * @return SQLite関数のwhere部に指定する文字列
     */
    private String getSelection(final Bundle where) {
        String selection = null;

        if (where != null) {
            Set<String> whereKeys = where.keySet();
            if (whereKeys.size() > 0) {
                selection = "";
                int i = 0;
                for (String strWhereKey : whereKeys) {
                    String[] splitData = strWhereKey.split(",");
                    if (splitData == null || splitData.length != 2) {
                        throw new IllegalArgumentException("whereは { <データタイプ>,<whereキー> } の書式で設定して下さい。");
                    }
                    String whereDataType = splitData[0];
                    String whereKeyData = splitData[1];
                    
                    if (i > 0) {
                        selection += " and ";
                    }
                    
                    if (whereDataType.equals(SQLiteClient.DATA_TYPE_LONG)) {
                        long whereValue = where.getLong(strWhereKey);
                        selection += whereKeyData + " = " + whereValue;
                    } else if (whereDataType.equals(SQLiteClient.DATA_TYPE_STRING)) {
                        String whereValue = where.getString(strWhereKey);
                        if (whereValue == null) {
                            selection += whereKeyData + " is null";
                        } else {
                            selection += whereKeyData + " = '" + whereValue + "'";
                        }
                    } else {
                        throw new IllegalArgumentException("whereのデータタイプが認識できません。");
                    }
                    
                    i++;
                }
            }
        }

        return selection;
    }

}
