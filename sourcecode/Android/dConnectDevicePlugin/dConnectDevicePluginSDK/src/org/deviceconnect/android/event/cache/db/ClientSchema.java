/*
 ClientSchema.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.event.cache.db;


/**
 * Clientテーブル スキーマ.
 * 
 *
 * @author NTT DOCOMO, INC.
 */
interface ClientSchema extends BaseSchema {
    
    /** 
     * テーブル名 : {@value}.
     */
    String TABLE_NAME = "Client";

    /**
     * アクセストークン.
     */
    String ACCESS_TOKEN = "access_token";
    
    /** 
     * セッションキー.
     */
    String SESSION_KEY = "session_key";
    
    /** 
     * レシーバー.
     */
    String RECEIVER = "receiver";
    
    /** 
     * テーブルcreate文.
     */
    String CREATE = "CREATE TABLE " + TABLE_NAME + " (" 
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SESSION_KEY + " TEXT NOT NULL, "
            + ACCESS_TOKEN + " TEXT, "
            + RECEIVER + " TEXT DEFAULT '', "
            + CREATE_DATE + " INTEGER NOT NULL, "
            + UPDATE_DATE + " INTEGER NOT NULL, UNIQUE(" + SESSION_KEY + ", " + RECEIVER + "));";
    
}
