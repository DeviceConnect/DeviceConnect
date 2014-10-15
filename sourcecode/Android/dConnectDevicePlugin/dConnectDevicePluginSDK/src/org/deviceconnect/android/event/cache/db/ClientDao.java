/*
 ClientDao.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.event.cache.db;

import java.sql.Timestamp;

import org.deviceconnect.android.event.Event;
import org.deviceconnect.android.event.cache.Utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Clientテーブル用DAOクラス.
 * 
 * 
 * @author NTT DOCOMO, INC.
 */
final class ClientDao implements ClientSchema {

    /**
     * Clientテーブル情報クラス.
     * 
     * @author NTT DOCOMO, INC.
     * 
     */
    static class Client {
        /**
         * ID.
         */
        long id;

        /**
         * アクセストークン.
         */
        String accessToken;

        /**
         * セッションキー.
         */
        String sessionKey;

        /**
         * レシーバー.
         */
        String receiver;
        
        /** 
         * EventSessionの作成日.
         */
        Timestamp esCreateDate;
        
        /** 
         * EventSessionの更新日.
         */
        Timestamp esUpdateDate;
    }

    /**
     * Utilityクラスなのでprivate.
     */
    private ClientDao() {
    }

    /**
     * クライアントデータを登録する.
     * SessionKey、Receiverが同じ場合はAccessTokenを更新する。
     * 
     * @param db データベース操作オブジェクト
     * @param event イベントデータ
     * @return 登録出来た場合は登録時のIDを返す。重複している場合は登録済みのIDを返す。処理に失敗した場合は-1を返す。
     */
    static long insert(final SQLiteDatabase db, final Event event) {

        if (event == null) {
            throw new IllegalArgumentException("Event is null.");
        }

        long result = -1L;
        String receiver = event.getReceiverName();
        if (receiver == null) {
            receiver = "";
        }
        Cursor cursor = db.query(TABLE_NAME, new String[] {_ID}, SESSION_KEY + "=? AND " + RECEIVER + "=?",
                new String[] {event.getSessionKey(), receiver}, null, null, null);
        if (cursor.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put(ACCESS_TOKEN, event.getAccessToken());
            values.put(SESSION_KEY, event.getSessionKey());
            values.put(RECEIVER, receiver);
            values.put(CREATE_DATE, Utils.getCurreTimestamp().getTime());
            values.put(UPDATE_DATE, Utils.getCurreTimestamp().getTime());
            result = db.insert(TABLE_NAME, null, values);
        } else if (cursor.moveToFirst()) {
            try {
                result = cursor.getLong(0);
                // アクセストークンは更新されるので、重複の場合は常に新しいものにアップデートしておく
                ContentValues values = new ContentValues();
                values.put(ACCESS_TOKEN, event.getAccessToken());
                values.put(UPDATE_DATE, Utils.getCurreTimestamp().getTime());
                int count = db.update(TABLE_NAME, values, _ID + "=?", new String[]{"" + result});
                if (count != 1) {
                    result = -1;
                }
            } catch (Exception e) {
                result = -1L;
            }
        }
        cursor.close();

        return result;
    }

    /**
     * 指定したセッションキーをもつデータを取得する.
     * 
     * @param db データベース操作オブジェクト
     * @param sessionKey セッションキー
     * @return マッチする行データ。無い場合はnullを返す。
     */
    static Client[] getBySessionKey(final SQLiteDatabase db, final String sessionKey) {

        Client[] result = null;
        Cursor c = db.query(TABLE_NAME, new String[] {_ID, SESSION_KEY, ACCESS_TOKEN, RECEIVER}, SESSION_KEY + "=?", 
                new String[] {sessionKey}, null, null, null);
        
        if (c.moveToFirst()) {
            int index = 0;
            result = new Client[c.getCount()];
            do {
                try {
                    Client data = new Client();
                    data.id = c.getLong(0);
                    data.sessionKey = c.getString(1);
                    data.accessToken = c.getString(2);
                    data.receiver = c.getString(3);
                    result[index++] = data;
                } catch (Exception e) {
                    result = null;
                }
            } while (c.moveToNext());
        }
        c.close();

        return result;
    }

    /**
     * IDから行データを取得する.
     * 
     * @param db データベース操作オブジェクト
     * @param id ID
     * @return 見つかった場合は行のデータ、その他はnullを返す。
     */
    static Client getById(final SQLiteDatabase db, final long id) {

        Client result = null;
        Cursor c = db.query(TABLE_NAME, new String[] {_ID, SESSION_KEY, ACCESS_TOKEN, RECEIVER}, 
                _ID + "=?", new String[] {"" + id}, null, null, null);

        if (c.moveToFirst()) {
            result = new Client();
            try {
                result.id = c.getLong(0);
                result.sessionKey = c.getString(1);
                result.accessToken = c.getString(2);
                result.receiver = c.getString(3);
            } catch (Exception e) {
                result = null;
            }
        }
        c.close();

        return result;
    }

    /**
     * 指定されたAPIのパスとデバイスIDに紐づく送り先のデータを取得する.
     * 
     * @param db データベース操作オブジェクト
     * @param event イベントデータ
     * @return 見つかった場合は各行のデータ、その他はnullを返す。
     */
    static Client[] getByAPIAndDeviceId(final SQLiteDatabase db, final Event event) {

        Client[] result = null;
        StringBuilder sb = new StringBuilder();
        String join = " INNER JOIN ";
        String prepared = " = ? ";
        String and = " AND ";

        sb.append("SELECT c.");
        sb.append(_ID);
        sb.append(", c.");
        sb.append(SESSION_KEY);
        sb.append(", c.");
        sb.append(ACCESS_TOKEN);
        sb.append(", c.");
        sb.append(RECEIVER);
        sb.append(", es.");
        sb.append(CREATE_DATE);
        sb.append(", es.");
        sb.append(UPDATE_DATE);
        sb.append(" FROM ");
        sb.append(ProfileSchema.TABLE_NAME);
        sb.append(" as p");
        sb.append(join);
        sb.append(InterfaceSchema.TABLE_NAME);
        sb.append(" as i ON p.");
        sb.append(ProfileSchema._ID);
        sb.append(" = i.");
        sb.append(InterfaceSchema.P_ID);
        sb.append(join);
        sb.append(AttributeSchema.TABLE_NAME);
        sb.append(" as a ON i.");
        sb.append(InterfaceSchema._ID);
        sb.append(" = a.");
        sb.append(AttributeSchema.I_ID);
        sb.append(join);
        sb.append(EventDeviceSchema.TABLE_NAME);
        sb.append(" as ed ON a.");
        sb.append(AttributeSchema._ID);
        sb.append(" = ed.");
        sb.append(EventDeviceSchema.A_ID);
        sb.append(join);
        sb.append(DeviceSchema.TABLE_NAME);
        sb.append(" as d ON ed.");
        sb.append(EventDeviceSchema.D_ID);
        sb.append(" = d.");
        sb.append(DeviceSchema._ID);
        sb.append(join);
        sb.append(EventSessionSchema.TABLE_NAME);
        sb.append(" as es ON es.");
        sb.append(EventSessionSchema.ED_ID);
        sb.append(" = ed.");
        sb.append(EventDeviceSchema._ID);
        sb.append(join);
        sb.append(ClientSchema.TABLE_NAME);
        sb.append(" as c ON es.");
        sb.append(EventSessionSchema.C_ID);
        sb.append(" = c.");
        sb.append(ClientSchema._ID);
        sb.append(" WHERE p.");
        sb.append(ProfileSchema.NAME);
        sb.append(prepared);
        sb.append(and);
        sb.append("i.");
        sb.append(InterfaceSchema.NAME);
        sb.append(prepared);
        sb.append(and);
        sb.append("a.");
        sb.append(AttributeSchema.NAME);
        sb.append(prepared);
        sb.append(and);
        sb.append("d.");
        sb.append(DeviceSchema.DEVICE_ID);
        sb.append(prepared);

        String inter = (event.getInterface() == null) ? "" : event.getInterface();
        String deviceId = (event.getDeviceId() == null) ? "" : event.getDeviceId();
        String[] params = {event.getProfile(), inter, event.getAttribute(), deviceId};
        Cursor c = db.rawQuery(sb.toString(), params);

        if (c.moveToFirst()) {
            int index = 0;
            result = new Client[c.getCount()];
            do {
                try {
                    Client data = new Client();
                    data.id = c.getLong(0);
                    data.sessionKey = c.getString(1);
                    data.accessToken = c.getString(2);
                    data.receiver = c.getString(3);
                    long createTime = c.getLong(4);
                    long updateTime = c.getLong(5);
                    data.esCreateDate = new Timestamp(createTime);
                    data.esUpdateDate = new Timestamp(updateTime);
                    result[index++] = data;
                } catch (Exception e) {
                    result = null;
                }
            } while (c.moveToNext());
        }
        c.close();

        return result;
    }
}
