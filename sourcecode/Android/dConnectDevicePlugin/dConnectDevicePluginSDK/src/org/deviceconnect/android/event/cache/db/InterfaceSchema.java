/*
 InterfaceSchema.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.event.cache.db;


/**
 * Interfaceテーブル スキーマ.
 * 
 * 
 * @author NTT DOCOMO, INC.
 */
interface InterfaceSchema extends BaseSchema {
    
    /** 
     * テーブル名 : {@value}.
     */
    String TABLE_NAME = "Interface";

    /**
     * インターフェース名.
     */
    String NAME = "name";

    /**
     * プロファイルID.
     */
    String P_ID = "p_id";

    /** 
     * テーブルcreate文.
     */
    String CREATE = "CREATE TABLE " + TABLE_NAME + " (" 
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + P_ID + " INTEGER NOT NULL, "
            + NAME + " TEXT DEFAULT '', "
            + CREATE_DATE + " INTEGER NOT NULL, "
            + UPDATE_DATE + " INTEGER NOT NULL, UNIQUE(" + P_ID + "," + NAME + "));";
}
