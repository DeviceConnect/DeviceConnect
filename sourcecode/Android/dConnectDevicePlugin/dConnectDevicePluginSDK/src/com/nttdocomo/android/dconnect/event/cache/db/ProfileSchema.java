/*
 ProfileSchema.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.event.cache.db;


/**
 * Profileテーブル スキーマ.
 * 
 *
 * @author NTT DOCOMO, INC.
 */
interface ProfileSchema extends BaseSchema {
    
    /** 
     * テーブル名 : {@value}.
     */
    String TABLE_NAME = "Profile";
    
    /** 
     * カラム : {@value} .
     * プロファイル名。
     */
    String NAME = "name";
    
    /** 
     * テーブルcreate文.
     */
    String CREATE = "CREATE TABLE " + TABLE_NAME + " (" 
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
            + NAME + " TEXT NOT NULL, "
            + CREATE_DATE + " INTEGER NOT NULL, "
            + UPDATE_DATE + " INTEGER NOT NULL, UNIQUE(" + NAME + "));";
    
}
