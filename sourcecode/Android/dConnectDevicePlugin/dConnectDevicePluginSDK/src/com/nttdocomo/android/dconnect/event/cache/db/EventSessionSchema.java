/*
 EventSessionSchema.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.event.cache.db;


/**
 * EventSessionテーブル スキーマ.
 * 
 *
 * @author NTT DOCOMO, INC.
 */
interface EventSessionSchema extends BaseSchema {
    
    /** 
     * テーブル名 : {@value}.
     */
    String TABLE_NAME = "EventSession";

    /** 
     * イベントデバイスID.
     * EventDeviceテーブルのID。
     */
    String ED_ID = "ed_id";
    
    /** 
     * クライアントID.
     * ClientテーブルのID。
     */
    String C_ID = "c_id";
    
    /** 
     * テーブルcreate文.
     */
    String CREATE = "CREATE TABLE " + TABLE_NAME + " (" 
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ED_ID + " INTEGER NOT NULL, " 
            + C_ID + " INTEGER NOT NULL, " 
            + CREATE_DATE + " INTEGER NOT NULL, "
            + UPDATE_DATE + " INTEGER NOT NULL, UNIQUE(" 
            + ED_ID + ", " + C_ID + "));";
    
}
