/*
 DeviceSchema.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.event.cache.db;


/**
 * Deviceテーブル スキーマ.
 * 
 *
 * @author NTT DOCOMO, INC.
 */
interface DeviceSchema extends BaseSchema {
    
    /** 
     * テーブル名 : {@value}.
     */
    String TABLE_NAME = "Device";

    /** 
     * デバイスID.
     */
    String DEVICE_ID = "device_id";
    
    /** 
     * テーブルcreate文.
     */
    String CREATE = "CREATE TABLE " + TABLE_NAME + " (" 
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
            + DEVICE_ID + " TEXT NOT NULL, "
            + CREATE_DATE + " INTEGER NOT NULL, "
            + UPDATE_DATE + " INTEGER NOT NULL, UNIQUE(" + DEVICE_ID + "));";
}
