/*
 LocalOAuthOpenHelper.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.localoauth.oauthserver.db;

import java.util.Locale;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * LocalOAuth用SQLiteOpenHelper.
 * @author NTT DOCOMO, INC.
 */
public class LocalOAuthOpenHelper extends SQLiteOpenHelper {

    /** DB名. */
    static final String DB = "localoauth.db";
    
    /** DBバージョン. */
    static final int DB_VERSION = 5;
    
    /** テーブル名(profiles). */
    static final String PROFILES_TABLE = "profiles";
    
    /** テーブル名(clients). */
    static final String CLIENTS_TABLE = "clients";
    
    /** テーブル作成コマンド(tokens). */
    static final String TOKENS_TABLE = "tokens";
    
    /** テーブル作成コマンド(scopes). */
    static final String SCOPES_TABLE = "scopes";
    
    /** userテーブルのuser_id(usersは今回作らないので、useridに格納する値を定義する). */
    static final int USERS_USER_ID = 0;
    
    /** DBに格納する日付を文字列で読み書きする際のフォーマット. */
    static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    /** DBに格納する日付を文字列で読み書きする際のLocale. */
    static final Locale DATETIME_LOCALE = Locale.JAPANESE;
    
    /** テーブル作成コマンド(profiles). */
    static final String CREATE_PROFILES_TABLE =
            "CREATE TABLE profiles ( "
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "profile_name VARCHAR(40), "
            + "description VARCHAR(40) "
            + ")";
    
    /** テーブル作成コマンド(clients). */
    static final String CREATE_CLIENTS_TABLE =
            "CREATE TABLE clients ( "
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "  /* ID */
            + "client_id VARCHAR(100), "                /* クライアントID */
            + "package_name VARCHAR(2000), "            /* パッケージ名 */
            + "device_id VARCHAR(100), "                /* デバイスID(無しのときはnull) */
            + "client_secret VARCHAR(100), "            /* クライアントシークレット */
            + "client_type INTEGER, "                   /* クライアントタイプ */
            + "registration_date INTEGER "              /* 登録日時(System.currentTimeMillis()で取得した値を格納する) */
            + ")";
    
    /** テーブル作成コマンド(tokens). */
    static final String CREATE_TOKENS_TABLE =
            "CREATE TABLE tokens ( "
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "  /* ID */
            + "access_token VARCHAR(100), "             /* アクセストークン */
            + "token_type VARCHAR(20), "                /* トークンタイプ */
            + "client_id VARCHAR(100), "                /* clients.id(FK) */
            + "users_userid INTEGER, "                  /* ユーザーID */
            + "registration_date INTEGER, "             /* レコード登録時間(System.currentTimeMills()で取得した時間) */
            + "access_date INTEGER, "                   /* [Ver3で追加]最終アクセス時間(System.currentTimeMills()で取得した時間) */
            + "application_name VARCHAR(100) "          /* [Ver4で追加]アプリケーション名 */
            + ") ";
    
    /** テーブル作成コマンド(scopes). */
    static final String CREATE_SCOPES_TABLE =
            "CREATE TABLE scopes ( "
            + "tokens_tokenid INTEGER, "        /* ID */
            + "profiles_profileid INTEGER, "    /* profiles.id(FK) */
            + "timestamp INTEGER, "             /* アクセス承認時間(System.currentTimeMills()で取得した時間) */
            + "expire_period INTEGER "  /* [Ver5で単位変更]有効期限[sec]({timestamp + expire_period * 1000} が有効期限が切れる時間) */
            + ")";
    
    /** テーブル削除コマンド(profiles). */
    static final String DROP_PROFILES_TABLE = "DROP TABLE profiles";
    
    /** テーブル削除コマンド(clients). */
    static final String DROP_CLIENTS_TABLE = "DROP TABLE clients ";
    
    /** テーブル削除コマンド(tokens). */
    static final String DROP_TOKENS_TABLE = "DROP TABLE tokens";

    /** テーブル削除コマンド(scopes). */
    static final String DROP_SCOPES_TABLE = "DROP TABLE scopes";

    /**
     * コンストラクタ.
     * @param c コンストラクタ
     */
    public LocalOAuthOpenHelper(final Context c) {
        super(c, DB, null, DB_VERSION);
    }

    /**
     * データベースが存在しないときにDBを構築する処理.
     * @param db    データベース
     */
    @Override
    public void onCreate(final SQLiteDatabase db) {
        db.execSQL(CREATE_CLIENTS_TABLE);
        db.execSQL(CREATE_PROFILES_TABLE);
        db.execSQL(CREATE_SCOPES_TABLE);
        db.execSQL(CREATE_TOKENS_TABLE);
    }

    /**
     * アップグレード処理.
     * @param   db  データベース
     * @param   oldVersion  旧バージョン
     * @param   newVersion  新バージョン
     */
    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        
        //      old  new    v2  v3
        //      1 -> 2      o   -
        //      1 -> 3      o   o
        //      2 -> 3      x   o
        
        /* Ver2の更新処理 */
        if (newVersion >= 2 && oldVersion < 2) {
            
            /* テーブル削除 */
            db.execSQL(DROP_CLIENTS_TABLE);
            db.execSQL(DROP_PROFILES_TABLE);
            db.execSQL(DROP_SCOPES_TABLE);
            db.execSQL(DROP_TOKENS_TABLE);
            
            /* テーブル構築 */
            db.execSQL(CREATE_CLIENTS_TABLE);
            db.execSQL(CREATE_PROFILES_TABLE);
            db.execSQL(CREATE_SCOPES_TABLE);
            db.execSQL(CREATE_TOKENS_TABLE);
        }
        /* Ver3の更新処理 */
        final int ver3 = 3;
        if (newVersion >= ver3 && oldVersion < ver3) {
            /* フィールド追加 */
            db.execSQL("ALTER TABLE tokens ADD access_date INTEGER");
            /* 初期値設定 */
            long currentTime = System.currentTimeMillis();
            db.execSQL("UPDATE tokens SET access_date = " + currentTime);
        }
        /* Ver4の更新処理 */
        final int ver4 = 4;
        if (newVersion >= ver4 && oldVersion < ver4) {
            /* フィールド追加 */
            db.execSQL("ALTER TABLE tokens ADD application_name VARCHAR(100)");
            /* 初期値設定 */
            db.execSQL("UPDATE tokens SET application_name = null");
        }
        /* Ver5の更新処理 */
        final int ver5 = 5;
        if (newVersion >= ver5 && oldVersion < ver5) {
            /* 有効期限の単位を[msec] -> [sec]に単位変更 */
            db.execSQL("UPDATE scopes SET expire_period = expire_period / 1000");
        }
    }

}
