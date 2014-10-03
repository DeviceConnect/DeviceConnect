/*
 EventManager.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.event;

import java.util.List;

import android.content.ComponentName;
import android.content.Intent;

import com.nttdocomo.android.dconnect.event.cache.EventCacheController;
import com.nttdocomo.android.dconnect.message.MessageUtils;
import com.nttdocomo.dconnect.message.DConnectMessage;

/**
 * イベント管理クラス. イベントの登録、解除、送信などはこのクラスを通すことで一元管理される。
 * 
 * 
 * @author NTT DOCOMO, INC.
 */
public enum EventManager {

    /**
     * シングルトンなEventManagerのインスタンス.
     */
    INSTANCE;

    /** 
     * キャッシュコントローラー.
     */
    private EventCacheController mController;
    
    /**
     * キャッシュの操作クラスを設定する.
     * スレッドセーフではないので、必要な場合は呼び出しもとで同期処理をすること。
     * 
     * @param controller キャッシュ操作オブジェクト。
     */
    public void setController(final EventCacheController controller) {
        mController = controller;
    }
    
    /**
     * コントローラーの設定状況を確認し、異常な場合は例外を投げる.
     */
    private void checkState() {
        if (mController == null) {
            throw new IllegalStateException("CacheController is not set.");
        }
    }
    
    /**
     * リクエストIntentからEventを生成する.
     * 
     * @param request リクエストデータ
     * @return イベントオブジェクト
     */
    private Event createEvent(final Intent request) {
        
        if (request == null) {
            throw new IllegalArgumentException("Request is null.");
        } 
        
        checkState();
        
        String deviceId = request.getStringExtra(DConnectMessage.EXTRA_DEVICE_ID);
        String profile = request.getStringExtra(DConnectMessage.EXTRA_PROFILE);
        String inter = request.getStringExtra(DConnectMessage.EXTRA_INTERFACE);
        String attribute = request.getStringExtra(DConnectMessage.EXTRA_ATTRIBUTE);
        String accessToken = request.getStringExtra(DConnectMessage.EXTRA_ACCESS_TOKEN);
        String sessionKey = request.getStringExtra(DConnectMessage.EXTRA_SESSION_KEY);
        ComponentName name = request.getParcelableExtra(DConnectMessage.EXTRA_RECEIVER);
        
        Event event = new Event();
        event.setSessionKey(sessionKey);
        event.setAccessToken(accessToken);
        event.setProfile(profile);
        event.setInterface(inter);
        event.setAttribute(attribute);
        event.setDeviceId(deviceId);
        if (name != null) {
            event.setReceiverName(name.flattenToString());
        }
        
        return event;
    }

    /**
     * 指定されたイベント登録用のリクエストからイベントデータを登録する.
     * 
     * @param request イベント登録リクエスト
     * @return 処理結果
     */
    public EventError addEvent(final Intent request) {
        Event event = createEvent(request);
        return mController.addEvent(event);
    }
    
    /**
     * 指定されたイベント解除用のリクエストからイベントデータを解除する.
     * 
     * @param request イベント解除リクエスト
     * @return 処理結果
     */
    public EventError removeEvent(final Intent request) {
        Event event = createEvent(request);
        return mController.removeEvent(event);
    }
    
    /**
     * 指定されたセッションキーに紐づくイベント情報を解除する.
     * 
     * @param sessionKey セッションキー
     * @return 削除に成功した場合はtrue、その他はfalseを返す。
     */
    public boolean removeEvents(final String sessionKey) {
        checkState();
        return mController.removeEvents(sessionKey);
    }
    
    /**
     * イベントを全て削除する.
     * 
     * @return 成功の場合true、その他はfalseを返す。
     */
    public boolean removeAll() {
        checkState();
        return mController.removeAll();
    }
    
    /**
     * キャッシュデータを書き込む.
     */
    public void flush() {
        checkState();
        mController.flush();
    }

    /**
     * 指定されたデバイスIDとAPIに紐づくイベント情報の一覧を取得する.
     * 
     * @param deviceId デバイスID
     * @param profile プロファイル名
     * @param inter インターフェース名
     * @param attribute アトリビュート名
     * @return イベントの一覧
     */
    public List<Event> getEventList(final String deviceId, final String profile, 
            final String inter, final String attribute) {
        checkState();
        return mController.getEvents(deviceId, profile, inter, attribute);
    }
    
    /**
     * 指定されたAPIに紐づくイベント情報の一覧を取得する.
     * 
     * @param profile プロファイル名
     * @param inter インターフェース名
     * @param attribute アトリビュート名
     * @return イベントの一覧
     */
    public List<Event> getEventList(final String profile, final String inter, final String attribute) {
        checkState();
        return getEventList(null, profile, inter, attribute);
    }
    
    /**
     * 指定されたAPIに紐づくイベント情報の一覧を取得する.
     * 
     * @param profile プロファイル名
     * @param attribute アトリビュート名
     * @return イベントの一覧
     */
    public List<Event> getEventList(final String profile, final String attribute) {
        checkState();
        return getEventList(profile, null, attribute);
    }
    
    /**
     * イベントデータからイベントメッセージ用のIntentを生成する.
     * 取得したIntentに適宜イベントオブジェクトを設定し送信すること。
     * 
     * @param event イベントデータ
     * @return イベントメッセージ用Intent
     */
    public static Intent createEventMessage(final Event event) {
        Intent message = MessageUtils.createEventIntent();
        message.putExtra(DConnectMessage.EXTRA_DEVICE_ID, event.getDeviceId());
        message.putExtra(DConnectMessage.EXTRA_PROFILE, event.getProfile());
        message.putExtra(DConnectMessage.EXTRA_INTERFACE, event.getInterface());
        message.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, event.getAttribute());
        message.putExtra(DConnectMessage.EXTRA_SESSION_KEY, event.getSessionKey());
        ComponentName cn = ComponentName.unflattenFromString(event.getReceiverName());
        message.setComponent(cn);
        return message;
    }
}
