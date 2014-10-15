/*
 FileCacheController.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.event.cache;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.deviceconnect.android.event.Event;
import org.deviceconnect.android.event.EventError;

import android.content.Context;

/**
 * イベントデータをファイルに保存し、キャッシュの操作機能を提供する. データはメモリにキャッシュし、flushすることでファイルに書き出す。
 * 
 * 
 * @author NTT DOCOMO, INC.
 */
public class FileCacheController extends MemoryCacheController {

    /**
     * コンテキストオブジェクト. ファイル操作に利用する。
     */
    private Context mContext;

    /**
     * キャッシュファイル名.
     */
    private static final String CACHE_FILE_NAME = "org_deviceconnect_android_event_cache.dat";

    /**
     * ロガー.
     */
    private Logger mLogger = Logger.getLogger("org.deviceconnect.dplugin");
    
    /** 
     * 自動フラッシュフラグ.
     */
    private boolean mAutoFlush;

    /**
     * 自動フラッシュフラグを指定してFileCacheControllerのインスタンスを生成する.
     * 
     * @param context コンテキストオブジェクト
     * @param autoFlush 
     *            trueの場合、追加、削除系の操作を行う度に自動でflush()を実行する。
     *            falseの場合は明示的に呼び出すまでflush()しない。
     */
    public FileCacheController(final Context context, final boolean autoFlush) {
        if (context == null) {
            throw new IllegalArgumentException("Context is null.");
        }
        mContext = context;
        mAutoFlush = autoFlush;
        load();
    }
    
    /**
     * コンテキストを指定してファイルキャッシュコントローラーのインスタンスを生成する.
     * 自動フラッシュ機能はオフの状態になる。
     * 
     * @param context コンテキストオブジェクト
     */
    public FileCacheController(final Context context) {
        this(context, false);
    }
    
    @Override
    public synchronized void flush() {
        
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = mContext.openFileOutput(CACHE_FILE_NAME, Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(getCache());
        } catch (FileNotFoundException e) {
            mLogger.severe("Exception occurred in FileCacheController. " + e.getMessage());
        } catch (IOException e) {
            mLogger.severe("Exception occurred in FileCacheController. " + e.getMessage());
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                } else if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                mLogger.severe("Exception occurred in FileCacheController. " + e.getMessage());
            }
        }
    }

    /**
     * データをファイルからロードする.
     */
    @SuppressWarnings("unchecked")
    private void load() {
        Map<String, Map<String, List<Event>>> cache = null;
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = mContext.openFileInput(CACHE_FILE_NAME);
            ois = new ObjectInputStream(fis);
            cache = (Map<String, Map<String, List<Event>>>) ois.readObject();
            setCache(cache);
        } catch (FileNotFoundException e) {
            mLogger.severe("Exception occurred in FileCacheController. " + e.getMessage());
        } catch (StreamCorruptedException e) {
            mLogger.severe("Exception occurred in FileCacheController. " + e.getMessage());
        } catch (IOException e) {
            mLogger.severe("Exception occurred in FileCacheController. " + e.getMessage());
        } catch (ClassNotFoundException e) {
            mLogger.severe("Exception occurred in FileCacheController. " + e.getMessage());
        } catch (ClassCastException e) {
            mLogger.severe("Exception occurred in FileCacheController. " + e.getMessage());
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                } else if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                mLogger.severe("Exception occurred in FileCacheController. " + e.getMessage());
            }
        }
    }
    
    @Override
    public synchronized EventError addEvent(final Event event) {
        EventError error = super.addEvent(event);
        if (error == EventError.NONE && mAutoFlush) {
            flush();
        }
        return error;
    }

    @Override
    public synchronized EventError removeEvent(final Event event) {
        EventError error = super.removeEvent(event);
        if (error == EventError.NONE && mAutoFlush) {
            flush();
        }
        return error;
    }

    @Override
    public synchronized boolean removeAll() {
        boolean result = super.removeAll();
        if (mAutoFlush) {
            flush();
        }
        return result;
    }

    @Override
    public synchronized boolean removeEvents(final String sessionKey) {
        boolean result = super.removeEvents(sessionKey);
        if (mAutoFlush) {
            flush();
        }
        return result;
    }

}
