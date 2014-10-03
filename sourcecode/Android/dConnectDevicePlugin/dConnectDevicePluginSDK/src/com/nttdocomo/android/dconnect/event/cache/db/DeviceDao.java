/*
 DeviceDao.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.event.cache.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.nttdocomo.android.dconnect.event.cache.Utils;

/**
 * Deviceテーブル DAOクラス.
 * 
 * 
 * @author NTT DOCOMO, INC.
 */
final class DeviceDao implements DeviceSchema {

    /**
     * インスタンスを生成する.
     */
    private DeviceDao() {
    }

    /**
     * デバイスIDを登録する.
     * 
     * @param db DB操作オブジェクト
     * @param deviceId デバイスID
     * @return 登録出来た場合は登録時のIDを返す。重複している場合は登録済みのIDを返す。処理に失敗した場合は-1を返す。
     */
    static long insert(final SQLiteDatabase db, final String deviceId) {

        String did = (deviceId != null) ? deviceId : "";
        long result = -1L;
        Cursor cursor = db.query(TABLE_NAME, new String[] {_ID}, DEVICE_ID + "=?", new String[] {did}, null, null,
                null);
        if (cursor.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put(DEVICE_ID, did);
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

}
