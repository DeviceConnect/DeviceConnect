/*
 EventCacheController.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.event.cache;

import java.util.List;

import com.nttdocomo.android.dconnect.event.Event;
import com.nttdocomo.android.dconnect.event.EventError;

/**
 * イベントデータ操作インターフェース.
 * イベントデータの追加、削除、保存、検索の機能を提供する。
 * 
 *
 * @author NTT DOCOMO, INC.
 */
public interface EventCacheController {
    
    /**
     * イベントデータをキャッシュに追加する.
     * 
     * @param event イベントデータ
     * @return 処理結果
     */
    EventError addEvent(Event event);
    
    /**
     * イベントデータをキャッシュから削除する.
     * 
     * @param event イベントデータ
     * @return 処理結果
     */
    EventError removeEvent(Event event);
    
    /**
     * 指定されたセッションキーに紐づくイベント情報を全て削除する.
     * 
     * @param sessionKey セッションキー
     * @return 成功の場合true、その他はfalseを返す
     */
    boolean removeEvents(String sessionKey);
    
    /**
     * キャッシュからデータを全て削除する.
     * @return 成功の場合true、その他はfalseを返す
     */
    boolean removeAll();
    
    /**
     * キャッシュから指定された条件に合うイベントデータを取得する.
     * 
     * @param deviceId デバイスID
     * @param profile プロファイル名
     * @param inter インターフェース名
     * @param attribute 属性名
     * @param sessionKey セッションキー
     * @param receiver レシーバー名
     * @return イベントデータ。条件に合うものが無い場合はnullを返す。
     */
    Event getEvent(String deviceId, String profile, String inter, 
            String attribute, String sessionKey, String receiver);
    
    /**
     * キャッシュから条件にあうイベントデータの一覧を取得する.
     * 
     * @param deviceId デバイスID
     * @param profile プロファイル名
     * @param inter インターフェース名
     * @param attribute 属性名
     * @return イベントデータの一覧。無い場合は空のリストを返す。
     */
    List<Event> getEvents(String deviceId, String profile, String inter, String attribute);
    
    /**
     * キャッシュデータをフラッシュする.
     */
    void flush();
    
}
