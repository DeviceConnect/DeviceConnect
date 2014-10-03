/*
 AttributeSchema.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.event.cache.db;


/**
 * Attributeテーブル スキーマ.
 * 
 *
 * @author NTT DOCOMO, INC.
 */
interface AttributeSchema extends BaseSchema {
    
    /** 
     * テーブル名 : {@value}.
     */
    String TABLE_NAME = "Attribute";

    /**
     * アトリビュート名.
     */
    String NAME = "name";

    /**
     * インターフェースID.
     * InterfaceテーブルのID。
     */
    String I_ID = "i_id";
    
    /** 
     * テーブルcreate文.
     */
    String CREATE = "CREATE TABLE " + TABLE_NAME + " (" 
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + I_ID + " INTEGER NOT NULL, "
            + NAME + " TEXT NOT NULL, " 
            + CREATE_DATE + " INTEGER NOT NULL, "
            + UPDATE_DATE + " INTEGER NOT NULL, UNIQUE(" + I_ID + "," + NAME + "));";
    
}
