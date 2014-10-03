/*
 EventSessionDao.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.event.cache.db;

import java.sql.Timestamp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.nttdocomo.android.dconnect.event.Event;
import com.nttdocomo.android.dconnect.event.EventError;
import com.nttdocomo.android.dconnect.event.cache.Utils;

/**
 * EventSessionテーブル用DAOクラス.
 * 
 *
 * @author NTT DOCOMO, INC.
 */
final class EventSessionDao implements EventSessionSchema {

    /**
     * Utilityクラスなのでprivate.
     */
    private EventSessionDao() {
    }
    
    /**
     * EventSessionテーブルのデータクラス.
     * 
     * @author NTT DOCOMO, INC.
     *
     */
    static class EventSession {
        
        /** 
         * ID.
         */
        long id;
        
        /** 
         * EventDeviceテーブルのID.
         */
        long edId;
        
        /** 
         * ClientテーブルのID.
         */
        long cId;
        
        /** 
         * 作成日.
         */
        Timestamp createDate;
        
        /** 
         * 更新日.
         */
        Timestamp updateDate;
    }

    /**
     * イベントとセッションデータのマッチング情報を登録する.
     * 
     * @param db データベース操作オブジェクト
     * @param eventDeviceId EventDeviceテーブルのID
     * @param clientId ClientテーブルのID
     * @return 登録出来た場合は登録時のIDを返す。重複している場合は登録済みのIDを返す。処理に失敗した場合は-1を返す。
     */
    static long insert(final SQLiteDatabase db, final long eventDeviceId, final long clientId) {
        
        long result = -1L;
        Cursor cursor = db.query(TABLE_NAME, new String[] {_ID}, ED_ID + "=? AND " + C_ID + "=?", 
                new String[] {"" + eventDeviceId, "" + clientId}, null, null, null);
        if (cursor.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put(ED_ID, eventDeviceId);
            values.put(C_ID, clientId);
            values.put(CREATE_DATE, Utils.getCurreTimestamp().getTime());
            values.put(UPDATE_DATE, Utils.getCurreTimestamp().getTime());
            result = db.insert(TABLE_NAME, null, values);
        } else if (cursor.moveToFirst()) {
            try {
                result = cursor.getLong(0);
            } catch (Exception e) {
                result = -1L;
            }
        }

        cursor.close();
        return result;
    }
    
    /**
     * 指定されたイベント情報を削除する.
     * 
     * @param db データベース操作オブジェクト
     * @param event イベントデータ
     * @return 処理結果
     */
    static EventError delete(final SQLiteDatabase db, final Event event) {
        
        EventSession data = get(db, event);
        if (data == null) {
            return EventError.NOT_FOUND;
        }
        
        int count = db.delete(TABLE_NAME, _ID + "=?", new String[] {"" + data.id});
        if (count == 0) {
            return EventError.NOT_FOUND;
        } else if (count != 1) {
            return EventError.FAILED;
        }
        
        Cursor c = db.query(TABLE_NAME, new String[] {_ID}, ED_ID + "=?", 
                new String[] {"" + data.edId}, null, null, null);
        if (c.getCount() == 0) {
            // デバイスに紐づくセッション情報がなくなったので削除
            count = EventDeviceDao.deleteById(db, data.edId);
            if (count != 1) {
                c.close();
                return EventError.FAILED;
            }
        }
        c.close();
        
        return EventError.NONE;
    }
    
    /**
     * 指定されたIDを持つ行を削除する.
     * 
     * @param db データベース操作オブジェクト
     * @param ids 削除するID一覧
     * @return 処理結果
     */
    static EventError delete(final SQLiteDatabase db, final String[] ids) {
        
        try {
            
            StringBuilder in = new StringBuilder();
            for (int i = 0; i < ids.length; i++) {
                in.append("?");
                if (i != ids.length - 1) {
                    in.append(",");
                }
            }
            int count = db.delete(TABLE_NAME, C_ID + " IN (" + in.toString() + ")", ids);
            if (count == 0) {
                return EventError.NOT_FOUND;
            }
        } catch (SQLiteException e) {
            return EventError.FAILED;
        }
        
        return EventError.NONE;
    }
    
    /**
     * 指定されたイベント情報からidを取得する.
     * 
     * @param db データベース操作オブジェクト
     * @param event イベントデータ
     * @return 見つかった場合は行のデータ、その他はnullを返す。
     */
    static EventSession get(final SQLiteDatabase db, final Event event) {
        
        EventSession result = null;
        StringBuilder sb = new StringBuilder();
        String join = " INNER JOIN ";
        String prepared = " = ? ";
        String and = " AND ";
        
        sb.append("SELECT es.");
        sb.append(_ID);
        sb.append(", es.");
        sb.append(ED_ID);
        sb.append(", es.");
        sb.append(C_ID);
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
        sb.append(and);
        sb.append("c.");
        sb.append(ClientSchema.SESSION_KEY);
        sb.append(prepared);
        sb.append(and);
        sb.append("c.");
        sb.append(ClientSchema.RECEIVER);
        sb.append(prepared);
        
        String inter = null2WhiteSpace(event.getInterface());
        String deviceId =  null2WhiteSpace(event.getDeviceId());
        String receiver = null2WhiteSpace(event.getReceiverName());
        
        String[] params = {event.getProfile(), inter, event.getAttribute(), 
                deviceId, event.getSessionKey(), receiver};
        Cursor c = db.rawQuery(sb.toString(), params);
        
        if (c.moveToFirst()) {
            result = new EventSession();
            try {
                result.id = c.getLong(0);
                result.edId = c.getLong(1);
                result.cId = c.getLong(2);
                long createTime = c.getLong(3);
                long updateTime = c.getLong(4);
                result.createDate = new Timestamp(createTime);
                result.updateDate = new Timestamp(updateTime);
            } catch (Exception e) {
                result = null;
            }
        }
        c.close();
        
        return result;
    }
    
    /**
     * 文字列がnullの場合空文字を返す.
     * 
     * @param str 解析対象文字列
     * @return nullの場合空文字、その他は引数の文字列をそのまま返す。
     */
    private static String null2WhiteSpace(final String str) {
        return (str == null) ? "" : str;
    }
}
