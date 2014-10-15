/*
 NotificationProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.profile;

import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.profile.NotificationProfileConstants;
import org.deviceconnect.profile.NotificationProfileConstants.Direction;
import org.deviceconnect.profile.NotificationProfileConstants.NotificationType;

import android.content.Intent;

/**
 * Notification プロファイル.
 * 
 * <p>
 * スマートデバイスのノーティフィケーションの操作機能を提供するAPI.<br/>
 * スマートデバイスのノーティフィケーションの操作機能を提供するデバイスプラグインは当クラスを継承し、対応APIを実装すること。 <br/>
 * </p>
 * 
 * <h1>各API提供メソッド</h1>
 * <p>
 * Notification Profile の各APIへのリクエストに対し、以下のコールバックメソッド群が自動的に呼び出される。<br/>
 * サブクラスは以下のメソッド群からデバイスプラグインが提供するAPI用のメソッドをオーバーライドし、機能を実装すること。<br/>
 * オーバーライドされていない機能は自動的に非対応APIとしてレスポンスを返す。
 * </p>
 * <ul>
 * <li>Notification API [POST] :
 * {@link NotificationProfile#onPostNotify(Intent, Intent, 
 * String, org.deviceconnect.profile.NotificationProfileConstants.NotificationType, 
 * org.deviceconnect.profile.NotificationProfileConstants.Direction, String, String, String, byte[])}
 * </li>
 * <li>Notification API [DELETE] :
 * {@link NotificationProfile#onDeleteNotify(Intent, Intent, String, String)}</li>
 * <li>Notification Click Event API [Register] :
 * {@link NotificationProfile#onPutOnClick(Intent, Intent, String, String)}</li>
 * <li>Notification Click Event API [Unregister] :
 * {@link NotificationProfile#onDeleteOnClick(Intent, Intent, String, String)}</li>
 * <li>Notification Show Event API [Register] :
 * {@link NotificationProfile#onPutOnShow(Intent, Intent, String, String)}</li>
 * <li>Notification Show Event API [Unregister] :
 * {@link NotificationProfile#onDeleteOnShow(Intent, Intent, String, String)}</li>
 * <li>Notification Close Event API [Register] :
 * {@link NotificationProfile#onPutOnClose(Intent, Intent, String, String)}</li>
 * <li>Notification Close Event API [Unregister] :
 * {@link NotificationProfile#onDeleteOnClose(Intent, Intent, String, String)}</li>
 * <li>Notification Error Event API [Register] :
 * {@link NotificationProfile#onPutOnError(Intent, Intent, String, String)}</li>
 * <li>Notification Error Event API [Unregister] :
 * {@link NotificationProfile#onDeleteOnError(Intent, Intent, String, String)}</li>
 * </ul>
 * @author NTT DOCOMO, INC.
 */
public class NotificationProfile extends DConnectProfile implements NotificationProfileConstants {

    @Override
    public final String getProfileName() {
        return PROFILE_NAME;
    }

    @Override
    protected boolean onPostRequest(final Intent request, final Intent response) {
        String attribute = getAttribute(request);
        boolean result = true;

        if (ATTRIBUTE_NOTIFY.equals(attribute)) {
            String uri = getUri(request);
            byte[] iconData = getContentData(uri);
            NotificationType type = getType(request);
            Direction dir = getDir(request);
            String lang = getLang(request);
            String body = getBody(request);
            String tag = getTag(request);
            String deviceId = getDeviceID(request);
            result = onPostNotify(request, response, deviceId, type, dir, lang, body, tag, iconData);
        } else {
            MessageUtils.setUnknownAttributeError(response);
        }

        return result;
    }

    @Override
    protected boolean onPutRequest(final Intent request, final Intent response) {
        String attribute = getAttribute(request);
        boolean result = true;

        if (attribute == null) {
            MessageUtils.setUnknownAttributeError(response);
        } else {
            
            String deviceId = getDeviceID(request);
            String sessionKey = getSessionKey(request);
            
            if (attribute.equals(ATTRIBUTE_ON_CLICK)) {
                result = onPutOnClick(request, response, deviceId, sessionKey);
            } else if (attribute.equals(ATTRIBUTE_ON_SHOW)) {
                result = onPutOnShow(request, response, deviceId, sessionKey);
            } else if (attribute.equals(ATTRIBUTE_ON_CLOSE)) {
                result = onPutOnClose(request, response, deviceId, sessionKey);
            } else if (attribute.equals(ATTRIBUTE_ON_ERROR)) {
                result = onPutOnError(request, response, deviceId, sessionKey);
            } else {
                MessageUtils.setUnknownAttributeError(response);
            }
        }

        return result;
    }

    @Override
    protected boolean onDeleteRequest(final Intent request, final Intent response) {
        String attribute = getAttribute(request);
        boolean result = true;

        if (attribute == null) {
            MessageUtils.setUnknownAttributeError(response);
        } else {
            
            String deviceId = getDeviceID(request);
            String sessionKey = getSessionKey(request);
            
            if (attribute.equals(ATTRIBUTE_ON_CLICK)) {
                result = onDeleteOnClick(request, response, deviceId, sessionKey);
            } else if (attribute.equals(ATTRIBUTE_ON_SHOW)) {
                result = onDeleteOnShow(request, response, deviceId, sessionKey);
            } else if (attribute.equals(ATTRIBUTE_ON_CLOSE)) {
                result = onDeleteOnClose(request, response, deviceId, sessionKey);
            } else if (attribute.equals(ATTRIBUTE_ON_ERROR)) {
                result = onDeleteOnError(request, response, deviceId, sessionKey);
            } else if (attribute.equals(ATTRIBUTE_NOTIFY)) {
                result = onDeleteNotify(request, response, getDeviceID(request), getNotificationId(request));
            } else {
                MessageUtils.setUnknownAttributeError(response);
            }
        }

        return result;
    }

    // ------------------------------------
    // PUT
    // ------------------------------------

    /**
     * onclickコールバック登録リクエストハンドラー.<br/>
     * onclickコールバックを登録し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param sessionKey セッションキー
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onPutOnClick(final Intent request, final Intent response, final String deviceId, 
            final String sessionKey) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * onshowコールバック登録リクエストハンドラー.<br/>
     * onshowコールバックを登録し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param sessionKey セッションキー
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onPutOnShow(final Intent request, final Intent response, final String deviceId, 
            final String sessionKey) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * oncloseコールバック登録リクエストハンドラー.<br/>
     * oncloseコールバックを登録し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param sessionKey セッションキー
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onPutOnClose(final Intent request, final Intent response, final String deviceId, 
            final String sessionKey) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * onerrorコールバック登録リクエストハンドラー.<br/>
     * onerrorコールバックを登録し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param sessionKey セッションキー
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onPutOnError(final Intent request, final Intent response, final String deviceId, 
            final String sessionKey) {
        setUnsupportedError(response);
        return true;
    }

    // ------------------------------------
    // POST
    // ------------------------------------

    /**
     * デバイスへのノーティフィケーション表示リクエストハンドラー.<br/>
     * ノーティフィケーションを表示し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param type 通知タイプ
     * @param dir メッセージの文字の向き。省略された場合null。
     * @param lang メッセージの言語。省略された場合null。
     * @param body 通知メッセージ。省略された場合null。
     * @param tag 任意タグ文字。省略された場合null。
     * @param iconData アイコン画像のバイナリ。省略された場合null。
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onPostNotify(final Intent request, final Intent response, final String deviceId, 
            final NotificationType type, final Direction dir, final String lang, final String body, 
            final String tag, final byte[] iconData) {
        setUnsupportedError(response);
        return true;
    }

    // ------------------------------------
    // DELETE
    // ------------------------------------

    /**
     * デバイスへのノーティフィケーション消去リクエストハンドラー.<br/>
     * ノーティフィケーションを消去し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param notificationId 通知ID
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onDeleteNotify(final Intent request, final Intent response, final String deviceId, 
            final String notificationId) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * onclickコールバック解除リクエストハンドラー.<br/>
     * onclickコールバックを解除し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param sessionKey セッションキー
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onDeleteOnClick(final Intent request, final Intent response, final String deviceId, 
            final String sessionKey) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * onshowコールバック解除リクエストハンドラー.<br/>
     * onshowコールバックを解除し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param sessionKey セッションキー
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onDeleteOnShow(final Intent request, final Intent response, final String deviceId, 
            final String sessionKey) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * oncloseコールバック解除リクエストハンドラー.<br/>
     * oncloseコールバックを解除し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param sessionKey セッションキー
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onDeleteOnClose(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * onerrorコールバック解除リクエストハンドラー.<br/>
     * onerrorコールバックを解除し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param sessionKey セッションキー
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onDeleteOnError(final Intent request, final Intent response, final String deviceId, 
            final String sessionKey) {
        setUnsupportedError(response);
        return true;
    }

    // ------------------------------------
    // レスポンスセッターメソッド群
    // ------------------------------------

    /**
     * レスポンスに通知IDを設定する.
     * 
     * @param response レスポンスパラメータ
     * @param notificationId 通知ID
     */
    public static void setNotificationId(final Intent response, final String notificationId) {
        response.putExtra(PARAM_NOTIFICATION_ID, notificationId);
    }

    // ------------------------------------
    // リクエストゲッターメソッド群
    // ------------------------------------

    /**
     * リクエストから通知タイプを取得する.
     * 
     * @param request リクエストパラメータ
     * @return 通知タイプ。無い場合は{@link NotificationType#UNKNOWN}を返す。
     *         <ul>
     *         <li>{@link NotificationType#PHONE}</li>
     *         <li>{@link NotificationType#MAIL}</li>
     *         <li>{@link NotificationType#SMS}</li>
     *         <li>{@link NotificationType#EVENT}</li>
     *         <li>{@link NotificationType#UNKNOWN}</li>
     *         </ul>
     */
    public static NotificationType getType(final Intent request) {
        Integer value = parseInteger(request, PARAM_TYPE);
        if (value == null) {
            value = NotificationType.UNKNOWN.getValue();
        }
        NotificationType type = NotificationType.getInstance(value);
        return type;
    }

    /**
     * リクエストから向きを取得する.
     * 
     * @param request リクエストパラメータ
     * @return 向きを表す文字列。無い場合は{@link Direction#UNKNOWN}を返す。
     *         <ul>
     *         <li>{@link Direction#AUTO}</li>
     *         <li>{@link Direction#RIGHT_TO_LEFT}</li>
     *         <li>{@link Direction#LEFT_TO_RIGHT}</li>
     *         <li>{@link Direction#UNKNOWN}</li>
     *         </ul>
     */
    public static Direction getDir(final Intent request) {
        String value = request.getStringExtra(PARAM_DIR);
        Direction dir = Direction.getInstance(value);
        return dir;
    }

    /**
     * リクエストから言語を取得する.
     * 
     * @param request リクエストパラメータ
     * @return 言語。無い場合はnullを返す。
     */
    public static String getLang(final Intent request) {
        String value = request.getStringExtra(PARAM_LANG);
        return value;
    }

    /**
     * リクエストから通知メッセージを取得する.
     * 
     * @param request リクエストパラメータ
     * @return 通知メッセージ。無い場合はnullを返す。
     */
    public static String getBody(final Intent request) {
        String value = request.getStringExtra(PARAM_BODY);
        return value;
    }

    /**
     * リクエストからタグを取得する.
     * 
     * @param request リクエストパラメータ
     * @return タグ。無い場合はnullを返す。
     */
    public static String getTag(final Intent request) {
        String value = request.getStringExtra(PARAM_TAG);
        return value;
    }

    /**
     * リクエストからファイルのURIを取得する.
     * 
     * @param request リクエストパラメータ
     * @return ファイルのURI。無い場合はnullを返す。
     */
    public static String getUri(final Intent request) {
        return request.getStringExtra(PARAM_URI);
    }

    /**
     * リクエストからノーティフィケーションIDを取得する.
     * 
     * @param request リクエストパラメータ
     * @return ノーティフィケーションID。無い場合はnullを返す。
     */
    public static String getNotificationId(final Intent request) {
        String value = request.getStringExtra(PARAM_NOTIFICATION_ID);
        return value;
    }

}
