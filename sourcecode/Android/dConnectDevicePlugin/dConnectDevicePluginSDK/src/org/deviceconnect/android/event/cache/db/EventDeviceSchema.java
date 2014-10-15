/*
 EventDeviceSchema.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.event.cache.db;


/**
 * EventDeviceテーブル スキーマ.
 * 
 *
 * @author NTT DOCOMO, INC.
 */
interface EventDeviceSchema extends BaseSchema {
    
    /** 
     * テーブル名 : {@value}.
     */
    String TABLE_NAME = "EventDevice";

    /** 
     * アトリビュートID.
     * AttributeテーブルのID。
     */
    String A_ID = "a_id";
    
    /** 
     * デバイスID.
     * デバイステーブルのIDであり、Device Connectで定義するdeviceIdとは異なる。
     */
    String D_ID = "d_id";
    
    /** 
     * テーブルcreate文.
     */
    String CREATE = "CREATE TABLE " + TABLE_NAME + " (" 
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + A_ID + " INTEGER NOT NULL, " 
            + D_ID + " INTEGER NOT NULL, "
            + CREATE_DATE + " INTEGER NOT NULL, "
            + UPDATE_DATE + " INTEGER NOT NULL, UNIQUE(" + A_ID + ", " + D_ID + "));";
}
