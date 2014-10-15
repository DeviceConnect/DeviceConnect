/*
 DBCacheController.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.event.cache.db;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.deviceconnect.android.event.Event;
import org.deviceconnect.android.event.EventError;
import org.deviceconnect.android.event.cache.BaseCacheController;
import org.deviceconnect.android.event.cache.db.ClientDao.Client;
import org.deviceconnect.android.event.cache.db.EventSessionDao.EventSession;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * イベントデータをデータベースに保存し、キャッシュの操作機能を提供する. 
 * 
 *
 * @author NTT DOCOMO, INC.
 */
public final class DBCacheController extends BaseCacheController {
    
    /** 
     * コンテキストオブジェクト.
     */
    private Context mContext;
    
    /**
     * ロガー.
     */
    private Logger mLogger = Logger.getLogger("org.deviceconnect.dplugin");
    
    /** 
     * DBヘルパー.
     */
    private EventDBOpenHelper mHelper;
    
    /**
     * 指定されたコンテキストでDBCacheControllerのインスタンスを生成する.
     * 
     * @param context コンテキストオブジェクト
     */
    public DBCacheController(final Context context) {
        
        if (context == null) {
            throw new IllegalArgumentException("Context is null.");
        }
        
        mContext = context;
        mHelper = new EventDBOpenHelper(mContext);
    }
    
    /**
     * dbをオープンする.
     * 
     * @return データベースオブジェクト
     */
    private SQLiteDatabase openDB() {
        // 安全性と利便性を取り、毎回open、closeすることとする。
        SQLiteDatabase db;
        try {
            db = mHelper.getWritableDatabase();
        } catch (SQLiteException e) {
            db = null;
        }
        return db;
    }
    
    @Override
    public synchronized EventError addEvent(final Event event) {
        
        if (!checkParameter(event)) {
            return EventError.INVALID_PARAMETER;
        }
        
        EventError result = EventError.FAILED;
        SQLiteDatabase db = openDB();
        if (db == null) {
            return EventError.FAILED;
        }
        
        do {
            db.beginTransaction();
            long pId = ProfileDao.insert(db, event.getProfile());
            if (pId < 0) {
                break;
            }
            long iId = InterfaceDao.insert(db, event.getInterface(), pId);
            if (iId < 0) {
                break;
            }
            long aId = AttributeDao.insert(db, event.getAttribute(), iId);
            if (aId < 0) {
                break;
            }
            long dId = DeviceDao.insert(db, event.getDeviceId());
            if (dId < 0) {
                break;
            }
            long edId = EventDeviceDao.insert(db, aId, dId);
            if (edId < 0) {
                break;
            }
            long cId = ClientDao.insert(db, event);
            if (cId < 0) {
                break;
            }
            long esId = EventSessionDao.insert(db, edId, cId);
            if (esId < 0) {
                break;
            }
            result = EventError.NONE;
            db.setTransactionSuccessful();
        } while (false);
        db.endTransaction();
        db.close();
        
        return result;
    }

    @Override
    public synchronized EventError removeEvent(final Event event) {
        if (!checkParameter(event)) {
            return EventError.INVALID_PARAMETER;
        }
        
        SQLiteDatabase db = openDB();
        if (db == null) {
            return EventError.FAILED;
        }
        
        db.beginTransaction();
        EventError error = EventSessionDao.delete(db, event);
        if (error == EventError.NONE || error == EventError.NOT_FOUND) {
            db.setTransactionSuccessful();
        }
        db.endTransaction();
        db.close();
        
        return error;
    }

    @Override
    public synchronized boolean removeAll() {
        
        SQLiteDatabase db = openDB();
        if (db == null) {
            return false;
        }
        boolean result;
        db.beginTransaction();
        try {
            db.delete(EventDeviceSchema.TABLE_NAME, null, null);
            db.delete(AttributeSchema.TABLE_NAME, null, null);
            db.delete(InterfaceSchema.TABLE_NAME, null, null);
            db.delete(ProfileSchema.TABLE_NAME, null, null);
            db.delete(ClientSchema.TABLE_NAME, null, null);
            db.delete(DeviceSchema.TABLE_NAME, null, null);
            db.setTransactionSuccessful();
            result = true;
        } catch (SQLiteException e) {
            // 失敗したらロールバックで巻き返す。
            mLogger.severe("DBCacheController#removeAll(). Failed to remove all. " + e.getMessage());
            result = false;
        }
        db.endTransaction();
        db.close();
        return result;
    }
    
    @Override
    public synchronized Event getEvent(final String deviceId, final String profile, final String inter, 
            final String attribute, final String sessionKey, final String receiver) {
        
        Event result = null;
        SQLiteDatabase db = null;
        do {
            
            db = openDB();
            if (db == null) {
                break;
            }
            
            Event search = new Event();
            search.setDeviceId(deviceId);
            search.setProfile(profile);
            search.setInterface(inter);
            search.setAttribute(attribute);
            search.setSessionKey(sessionKey);
            search.setReceiverName(receiver);
            // checkParameterエラー回避用データの設定
            search.setAccessToken("dammy");
            
            if (!checkParameter(search)) {
                break;
            }
            
            EventSession data = EventSessionDao.get(db, search);
            if (data == null) {
                break;
            }
            
            Client client = ClientDao.getById(db, data.cId);
            if (client == null) {
                break;
            }
            
            search.setAccessToken(client.accessToken);
            search.setCreateDate(data.createDate);
            search.setUpdateDate(data.updateDate);
            
            result = search;
            
        } while (false);
        
        if (db != null) {
            db.close();
        }
        
        return result;
    }

    @Override
    public synchronized List<Event> getEvents(final String deviceId, final String profile, 
            final String inter, final String attribute) {
        
        List<Event> result = new ArrayList<Event>();
        SQLiteDatabase db = null;
        do {
            db = openDB();
            if (db == null) {
                break;
            }
            
            Event search = new Event();
            search.setDeviceId(deviceId);
            search.setProfile(profile);
            search.setInterface(inter);
            search.setAttribute(attribute);
            // checkParameterエラー回避用データの設定
            search.setSessionKey("dammy");
            search.setAccessToken("dammy");
            search.setReceiverName("dammy");
            
            if (!checkParameter(search)) {
                break;
            }
            
            Client[] clients = ClientDao.getByAPIAndDeviceId(db, search);
            if (clients == null) {
                break;
            }
            
            for (Client client : clients) {
                Event event = new Event();
                event.setDeviceId(deviceId);
                event.setProfile(profile);
                event.setInterface(inter);
                event.setAttribute(attribute);
                event.setSessionKey(client.sessionKey);
                event.setAccessToken(client.accessToken);
                event.setReceiverName(client.receiver);
                event.setCreateDate(client.esCreateDate);
                event.setUpdateDate(client.esUpdateDate);
                result.add(event);
            }
            
        } while (false);
        
        if (db != null) {
            db.close();
        }
        
        return result;
    }

    @Override
    public void flush() {
        // do-nothing.
    }

    @Override
    public synchronized boolean removeEvents(final String sessionKey) {
        
        if (sessionKey == null) {
            throw new IllegalArgumentException("Session key is null.");
        }
        
        boolean result = false;
        SQLiteDatabase db = null;
        do {
            db = openDB();
            if (db == null) {
                break;
            }
            db.beginTransaction();
            Client[] clients = ClientDao.getBySessionKey(db, sessionKey);
            if (clients == null) {
                break;
            } else if (clients.length == 0) {
                result = true;
                break;
            }
            String[] ids = new String[clients.length];
            int i = 0;
            for (Client client : clients) {
                ids[i++] = "" + client.id;
            }
            EventError error = EventSessionDao.delete(db, ids);
            if (error == EventError.FAILED || error == EventError.INVALID_PARAMETER) {
                break;
            }
            db.setTransactionSuccessful();
            result = true;
        } while (false);
        
        if (db != null) {
            db.endTransaction();
            db.close();
        }
        
        return result;
    }
    
    /**
     * DBオープンヘルパー.
     * 
     * @author NTT DOCOMO, INC.
     *
     */
    private class EventDBOpenHelper extends SQLiteOpenHelper {

        /** 
         * DBファイル名.
         */
        private static final String DB_NAME = "__device_connect_event.db";
        
        /** 
         * バージョン番号.
         */
        private static final int DB_VERSION = 1;
        
        /**
         * DBオープンヘルパーを生成する.
         * 
         * @param context コンテキストオブジェクト
         */
        public EventDBOpenHelper(final Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(final SQLiteDatabase db) {
            // 外部キーの設定は設けていないので、リレーションは手動でしっかり管理すること。
            // DBが作れないと使えないので、例外は処理しない
            db.execSQL(ProfileSchema.CREATE);
            db.execSQL(InterfaceSchema.CREATE);
            db.execSQL(AttributeSchema.CREATE);
            db.execSQL(ClientSchema.CREATE);
            db.execSQL(DeviceSchema.CREATE);
            db.execSQL(EventDeviceSchema.CREATE);
            db.execSQL(EventSessionSchema.CREATE);
        }

        @Override
        public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
            // バージョン1なので特に処理無し。バージョンの変更がある場合は要対応。
        }
        
    }

}
