/*
 MediaPlayerProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.profile;

import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.profile.MediaPlayerProfileConstants;

import android.content.Intent;
import android.os.Bundle;

/**
 * MediaPlayer プロファイル.
 * 
 * <p>
 * スマートデバイス上のメディアの再生状態の変更要求を通知するAPI.<br/>
 * メディア操作を提供するデバイスプラグインは当クラスを継承し、対応APIを実装すること。 <br/>
 * </p>
 * 
 * <h1>各API提供メソッド</h1>
 * <p>
 * MediaPlayer Profile の各APIへのリクエストに対し、以下のコールバックメソッド群が自動的に呼び出される。<br/>
 * サブクラスは以下のメソッド群からデバイスプラグインが提供するAPI用のメソッドをオーバーライドし、機能を実装すること。<br/>
 * オーバーライドされていない機能は自動的に非対応APIとしてレスポンスを返す。
 * </p>
 * <ul>
 * </ul>
 * @author NTT DOCOMO, INC.
 */
public class MediaPlayerProfile extends DConnectProfile implements MediaPlayerProfileConstants {

    @Override
    public final String getProfileName() {
        return PROFILE_NAME;
    }

    @Override
    protected boolean onGetRequest(final Intent request, final Intent response) {
        boolean result = true;
        String attribute = getAttribute(request);

        if (attribute == null) {
            MessageUtils.setUnknownAttributeError(response);
        } else {
            String deviceId = getDeviceID(request);
            if (attribute.equals(ATTRIBUTE_MEDIA)) {
                String mediaId = getMediaId(request);
                result = onGetMedia(request, response, deviceId, mediaId);
            } else if (attribute.equals(ATTRIBUTE_MEDIA_LIST)) {
                String query = getQuery(request);
                String mimeType = getMIMEType(request);
                String[] orders = getOrder(request);
                Integer offset = getOffset(request);
                Integer limit = getLimit(request);
                result = onGetMediaList(request, response, deviceId, query, 
                        mimeType, orders, offset, limit);
            } else if (attribute.equals(ATTRIBUTE_PLAY_STATUS)) {
                result = onGetPlayStatus(request, response, deviceId);
            } else if (attribute.equals(ATTRIBUTE_SEEK)) {
                result = onGetSeek(request, response, deviceId);
            } else if (attribute.equals(ATTRIBUTE_VOLUME)) {
                result = onGetVolume(request, response, deviceId);
            } else if (attribute.equals(ATTRIBUTE_MUTE)) {
                result = onGetMute(request, response, deviceId);
            } else {
                MessageUtils.setUnknownAttributeError(response);
            }
        }

        return result;
    }

    @Override
    protected boolean onPutRequest(final Intent request, final Intent response) {
        boolean result = true;
        String attribute = getAttribute(request);

        if (attribute == null) {
            MessageUtils.setUnknownAttributeError(response);
        } else {
            String deviceId = getDeviceID(request);
            if (attribute.equals(ATTRIBUTE_MEDIA)) {
                String mediaId = getMediaId(request);
                result = onPutMedia(request, response, deviceId, mediaId);
            } else if (attribute.equals(ATTRIBUTE_PLAY)) {
                result = onPutPlay(request, response, deviceId);
            } else if (attribute.equals(ATTRIBUTE_STOP)) {
                result = onPutStop(request, response, deviceId);
            } else if (attribute.equals(ATTRIBUTE_PAUSE)) {
                result = onPutPause(request, response, deviceId);
            } else if (attribute.equals(ATTRIBUTE_RESUME)) {
                result = onPutResume(request, response, deviceId);
            } else if (attribute.equals(ATTRIBUTE_SEEK)) {
                Integer pos = getPos(request);
                result = onPutSeek(request, response, deviceId, pos);
            } else if (attribute.equals(ATTRIBUTE_VOLUME)) {
                Double volume = getVolume(request);
                result = onPutVolume(request, response, deviceId, volume);
            } else if (attribute.equals(ATTRIBUTE_MUTE)) {
                result = onPutMute(request, response, deviceId);
            } else if (attribute.equals(ATTRIBUTE_ON_STATUS_CHANGE)) {
                String sessionKey = getSessionKey(request);
                result = onPutOnStatusChange(request, response, deviceId, sessionKey);
            } else {
                MessageUtils.setUnknownAttributeError(response);
            }
        }

        return result;
    }

    @Override
    protected boolean onDeleteRequest(final Intent request, final Intent response) {
        boolean result = true;
        String attribute = getAttribute(request);
        if (attribute == null) {
            MessageUtils.setUnknownAttributeError(response);
        } else {
            String deviceId = getDeviceID(request);
            if (attribute.equals(ATTRIBUTE_MUTE)) {
                result = onDeleteMute(request, response, deviceId);
            } else if (attribute.equals(ATTRIBUTE_ON_STATUS_CHANGE)) {
                String sessionKey = getSessionKey(request);
                result = onDeleteOnStatusChange(request, response, deviceId, sessionKey);
            } else {
                MessageUtils.setUnknownAttributeError(response);
            }
        }
        return result;
    }

    /**
     * メディア情報取得リクエストハンドラー.
     * <p>
     * 指定されたメディアIDのメディア情報を提供し、その結果をレスポンスパラメータに格納する。
     * </p>
     * 
     * <p>
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。<br/>
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * </p>
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param mediaId メディアID
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onGetMedia(final Intent request, final Intent response,
            final String deviceId, final String mediaId) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * メディア一覧情報取得リクエストハンドラー.
     * <p>
     * デバイスが保持しているメディア一覧の情報を提供し、その結果をレスポンスパラメータに格納する。
     * </p>
     * 
     * 省略可能な変数は、以下のような可能性がある。
     * <ul>
     * <li>queryが省略された場合はnullが渡される。</li>
     * <li>mimeTypeが省略された場合はnullが渡される。</li>
     * <li>ordersが省略された場合はnullが渡される。</li>
     * <li>offset省略された場合は0が渡される。</li>
     * <li>limitが省略された場合は0が渡される。</li>
     * </ul>
     * 
     * <p>
     * ordersの配列は、偶数にソートを行う物理名、奇数には昇順・降順が格納される。<br>
     * 例) title,asc
     * <pre>
     * String name = orders[0];
     * String order = orders[1];
     * 
     * Order o = Order.getInstance(order);
     * switch (o) {
     * case Order.ASC:
     *     // ソート処理
     *     break;
     * case Order.DSEC:
     *     // ソート処理
     *     break;
     * default:
     *     break;
     * }
     * </pre>
     * nameにはtitleが格納され、orderにはascが格納される。<br>
     * デバイスプラグインでは、この値を参照してレスポンスのソートを行うこと。
     * </p>
     * 
     * <p>
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。<br/>
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * </p>
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param query クエリ
     * @param mimeType マイムタイプ
     * @param orders オーダー
     * @param offset オフセット
     * @param limit リミット
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onGetMediaList(final Intent request, final Intent response, final String deviceId,
            final String query, final String mimeType, final String[] orders, 
            final Integer offset, final Integer limit) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * デバイスの再生状態取得リクエストハンドラー.
     * <p>
     * デバイスの再生状態情報を提供し、その結果をレスポンスパラメータに格納する。
     * </p>
     * 
     * <p>
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。<br/>
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * </p>
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onGetPlayStatus(final Intent request, final Intent response, final String deviceId) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * デバイスのシーク状態取得リクエストハンドラー.
     * <p>
     * デバイスのシーク状態情報を提供し、その結果をレスポンスパラメータに格納する。
     * </p>
     * 
     * <p>
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。<br/>
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * </p>
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onGetSeek(final Intent request, final Intent response, final String deviceId) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * デバイスのボリューム取得リクエストハンドラー.
     * <p>
     * デバイスのボリューム情報を提供し、その結果をレスポンスパラメータに格納する。
     * </p>
     * 
     * <p>
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。<br/>
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * </p>
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onGetVolume(final Intent request, final Intent response, final String deviceId) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * デバイスのミュート状態取得リクエストハンドラー.
     * <p>
     * デバイスのミュート状態取得を提供し、その結果をレスポンスパラメータに格納する。
     * </p>
     * 
     * <p>
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。<br/>
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * </p>
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onGetMute(final Intent request, final Intent response, final String deviceId) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * デバイスに再生するメディア設定リクエストハンドラー.
     * <p>
     * デバイスに再生するメディア設定を提供し、その結果をレスポンスパラメータに格納する。
     * </p>
     * 
     * <p>
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。<br/>
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * </p>
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param mediaId メディアID
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onPutMedia(final Intent request, final Intent response,
            final String deviceId, final String mediaId) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * デバイスのメディア再生リクエストハンドラー.
     * <p>
     * デバイスのメディア再生機能を提供し、その結果をレスポンスパラメータに格納する。
     * </p>
     * 
     * <ul>
     * <li>デバイスにメディアが設定されていない場合にはエラーを返すこと。</li>
     * <li>デバイスが停止状態以外の状態の場合にはエラーを返すこと。</li>
     * </ul>
     * 
     * <p>
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。<br/>
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * </p>
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onPutPlay(final Intent request, final Intent response,
            final String deviceId) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * デバイスのメディア停止リクエストハンドラー.
     * <p>
     * デバイスのメディア停止機能を提供し、その結果をレスポンスパラメータに格納する。
     * </p>
     * 
     * <ul>
     * <li>デバイスにメディアが設定されていない場合にはエラーを返すこと。</li>
     * <li>デバイスが停止状態の場合にはエラーを返すこと、それ以外の状態の場合には停止状態に遷移すること。</li>
     * </ul>
     * 
     * <p>
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。<br/>
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * </p>
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onPutStop(final Intent request, final Intent response,
            final String deviceId) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * デバイスのメディア一時停止リクエストハンドラー.
     * <p>
     * デバイスのメディア一時停止機能を提供し、その結果をレスポンスパラメータに格納する。
     * </p>
     * 
     * <ul>
     * <li>デバイスにメディアが設定されていない場合にはエラーを返すこと。</li>
     * <li>デバイスが再生中以外の状態の場合にはエラーを返すこと。</li>
     * </ul>
     * 
     * <p>
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。<br/>
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * </p>
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onPutPause(final Intent request, final Intent response,
            final String deviceId) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * デバイスのメディア再生再開リクエストハンドラー.
     * <p>
     * デバイスのメディア再生再開機能を提供し、その結果をレスポンスパラメータに格納する。
     * </p>
     * 
     * <ul>
     * <li>デバイスにメディアが設定されていない場合にはエラーを返すこと。</li>
     * <li>デバイスが一時停止中以外の状態の場合にはエラーを返すこと。</li>
     * </ul>
     * 
     * <p>
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。<br/>
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * </p>
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onPutResume(final Intent request, final Intent response,
            final String deviceId) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * デバイスのシーク変更リクエストハンドラー.
     * <p>
     * デバイスのシーク変更を提供し、その結果をレスポンスパラメータに格納する。
     * </p>
     * 
     * <p>
     * posが指定されていない場合には-1が渡される。
     * </p>
     * 
     * <p>
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。<br/>
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * </p>
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param pos 再生位置
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onPutSeek(final Intent request, final Intent response, final String deviceId, final Integer pos) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * デバイスのボリューム変更リクエストハンドラー.
     * <p>
     * デバイスのボリューム変更を提供し、その結果をレスポンスパラメータに格納する。
     * </p>
     * 
     * <p>
     * valumeが指定されていない場合には-1が渡される。
     * </p>
     * 
     * <p>
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。<br/>
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * </p>
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param volume ボリューム
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onPutVolume(final Intent request, final Intent response,
            final String deviceId, final Double volume) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * デバイスのミュート変更リクエストハンドラー.
     * <p>
     * デバイスのミュート変更を提供し、その結果をレスポンスパラメータに格納する。
     * </p>
     * 
     * <p>
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。<br/>
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * </p>
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onPutMute(final Intent request, final Intent response, final String deviceId) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * デバイスのコンテンツ再生状態通知登録リクエストハンドラー.
     * <p>
     * デバイスのコンテンツ再生状態通知登録を提供し、その結果をレスポンスパラメータに格納する。
     * </p>
     * 
     * <p>
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。<br/>
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * </p>
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param sessionKey セッションキー
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onPutOnStatusChange(final Intent request, final Intent response,
            final String deviceId, final String sessionKey) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * デバイスのミュート解除リクエストハンドラー.
     * <p>
     * デバイスのミュート解除を提供し、その結果をレスポンスパラメータに格納する。
     * </p>
     * 
     * <p>
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。<br/>
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * </p>
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onDeleteMute(final Intent request, final Intent response, final String deviceId) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * デバイスのコンテンツ再生状態通知解除リクエストハンドラー.
     * <p>
     * デバイスのコンテンツ再生状態通知解除を提供し、その結果をレスポンスパラメータに格納する。
     * </p>
     * 
     * <p>
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。<br/>
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * </p>
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param sessionKey セッションキー
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onDeleteOnStatusChange(final Intent request, final Intent response,
            final String deviceId, final String sessionKey) {
        setUnsupportedError(response);
        return true;
    }

    // ------------------------------------
    // リクエストゲッターメソッド群
    // ------------------------------------

    // Setter

    /**
     * レスポンスにメディア数を設定する.
     * 
     * @param response レスポンスデータ
     * @param count メディア数
     */
    public static final void setCount(final Intent response, final int count) {
        response.putExtra(PARAM_COUNT, count);
    }

    /**
     * レスポンスにメディアを設定する.
     * 
     * @param response レスポンスデータ
     * @param media メディア
     */
    public static final void setMedia(final Intent response, final Bundle[] media) {
        response.putExtra(PARAM_MEDIA, media);
    }

    /**
     * レスポンスにメディアIDを設定する.
     * 
     * @param response レスポンスデータ
     * @param mediaId メディアID
     */
    public static final void setMediaId(final Intent response, final String mediaId) {
        response.putExtra(PARAM_MEDIA_ID, mediaId);
    }

    /**
     * 再生情報にメディアIDを設定する.
     * 
     * @param playStatus 再生情報
     * @param mediaId メディアID
     */
    public static final void setMediaId(final Bundle playStatus, final String mediaId) {
        playStatus.putString(PARAM_MEDIA_ID, mediaId);
    }

    /**
     * メッセージにメディアプレイヤー情報を設定する.
     * 
     * @param message メッセージ
     * @param mediaPlayer メディアプレイヤー
     */
    public static final void setMediaPlayer(final Intent message, final Bundle mediaPlayer) {
        message.putExtra(PARAM_MEDIA_PLAYER, mediaPlayer);
    }

    /**
     * レスポンスにコンテンツの再生状態を設定する.
     * 
     * @param response レスポンスデータ
     * @param playStatus コンテンツの再生状態
     */
    public static final void setStatus(final Intent response, final PlayStatus playStatus) {
        response.putExtra(PARAM_STATUS, playStatus.getValue());
    }

    /**
     * レスポンスに再生状態を設定する.
     * 
     * @param response レスポンスデータ
     * @param status 再生状態
     */
    public static final void setStatus(final Intent response, final Status status) {
        response.putExtra(PARAM_STATUS, status.getValue());
    }

    /**
     * メディア情報に再生状態を設定する.
     * 
     * @param media メディア情報
     * @param status 再生状態
     */
    public static final void setStatus(final Bundle media, final Status status) {
        setStatus(media, status.getValue());
    }

    /**
     * メディア情報に再生状態を設定する.
     * 
     * @param media メディア情報
     * @param status 再生状態
     */
    public static final void setStatus(final Bundle media, final String status) {
        media.putString(PARAM_STATUS, status);
    }

    /**
     * レスポンスに再生位置を設定する.
     * 
     * @param response レスポンスデータ
     * @param pos 再生位置
     */
    public static final void setPos(final Intent response, final int pos) {
        if (pos < 0) {
            throw new IllegalArgumentException("pos is negative.");
        }
        response.putExtra(PARAM_POS, pos);
    }

    /**
     * 再生情報に再生位置を設定する.
     * 
     * @param playStatus 再生情報
     * @param pos 再生位置
     */
    public static final void setPos(final Bundle playStatus, final int pos) {
        if (pos < 0) {
            throw new IllegalArgumentException("pos is negative.");
        }
        playStatus.putInt(PARAM_POS, pos);
    }

    /**
     * レスポンスにMIMEタイプを設定する.
     * 
     * @param response レスポンスデータ
     * @param mimeType MIMEタイプ
     */
    public static final void setMIMEType(final Intent response, final String mimeType) {
        response.putExtra(PARAM_MIME_TYPE, mimeType);
    }

    /**
     * メディア情報にMIMEタイプを設定する.
     * 
     * @param media メディア情報
     * @param mimeType MIMEタイプ
     */
    public static final void setMIMEType(final Bundle media, final String mimeType) {
        media.putString(PARAM_MIME_TYPE, mimeType);
    }

    /**
     * レスポンスにタイトルを設定する.
     * 
     * @param response レスポンスデータ
     * @param title タイトル
     */
    public static final void setTitle(final Intent response, final String title) {
        response.putExtra(PARAM_TITLE, title);
    }

    /**
     * メディア情報にタイトルを設定する.
     * 
     * @param media メディア情報
     * @param title タイトル
     */
    public static final void setTitle(final Bundle media, final String title) {
        media.putString(PARAM_TITLE, title);
    }

    /**
     * レスポンスにタイプ名を設定する.
     * 
     * @param response レスポンスデータ
     * @param type タイプ名
     */
    public static final void setType(final Intent response, final String type) {
        response.putExtra(PARAM_TYPE, type);
    }

    /**
     * メディア情報にタイプ名を設定する.
     * 
     * @param media メディア情報
     * @param type タイプ名
     */
    public static final void setType(final Bundle media, final String type) {
        media.putString(PARAM_TYPE, type);
    }

    /**
     * レスポンスに言語を設定する.
     * 
     * @param response レスポンスデータ
     * @param language 言語
     */
    public static final void setLanguage(final Intent response, final String language) {
        response.putExtra(PARAM_LANGUAGE, language);
    }

    /**
     * メディア情報に言語を設定する.
     * 
     * @param media メディア情報
     * @param language 言語
     */
    public static final void setLanguage(final Bundle media, final String language) {
        media.putString(PARAM_LANGUAGE, language);
    }

    /**
     * レスポンスに画像へのURIを設定する.
     * 
     * @param response レスポンスデータ
     * @param uri 画像へのURI
     */
    public static final void setImageUri(final Intent response, final String uri) {
        response.putExtra(PARAM_IMAGE_URI, uri);
    }

    /**
     * メディア情報に画像へのURIを設定する.
     * 
     * @param media メディア情報
     * @param uri 画像へのURI
     */
    public static final void setImageUri(final Bundle media, final String uri) {
        media.putString(PARAM_IMAGE_URI, uri);
    }

    /**
     * レスポンスに説明文を設定する.
     * 
     * @param response レスポンスデータ
     * @param description 説明文
     */
    public static final void setDescription(final Intent response, final String description) {
        response.putExtra(PARAM_DESCRIPTION, description);
    }

    /**
     * メディア情報に説明文を設定する.
     * 
     * @param media メディア情報
     * @param description 説明文
     */
    public static final void setDescription(final Bundle media, final String description) {
        media.putString(PARAM_DESCRIPTION, description);
    }

    /**
     * レスポンスに曲の長さを設定する.
     * 
     * @param response レスポンスデータ
     * @param duration 曲の長さ
     */
    public static final void setDuration(final Intent response, final int duration) {
        if (duration < 0) {
            throw new IllegalArgumentException("duration is invalid.");
        }
        response.putExtra(PARAM_DURATION, duration);
    }

    /**
     * メディア情報に曲の長さを設定する.
     * 
     * @param media メディア情報
     * @param duration 曲の長さ
     */
    public static final void setDuration(final Bundle media, final int duration) {
        if (duration < 0) {
            throw new IllegalArgumentException("duration is invalid.");
        }
        media.putInt(PARAM_DURATION, duration);
    }

    /**
     * レスポンスに制作者情報一覧を設定する.
     * 
     * @param response レスポンスデータ
     * @param creators 制作者情報一覧
     */
    public static final void setCreators(final Intent response, final Bundle[] creators) {
        response.putExtra(PARAM_CREATORS, creators);
    }

    /**
     * メディア情報に制作者情報一覧を設定する.
     * 
     * @param media メディア情報
     * @param creators 制作者情報一覧
     */
    public static final void setCreators(final Bundle media, final Bundle[] creators) {
        media.putParcelableArray(PARAM_CREATORS, creators);
    }

    /**
     * 制作者情報に制作者名を設定する.
     * 
     * @param bundle 制作者情報
     * @param creator 制作者名
     */
    public static final void setCreator(final Bundle bundle, final String creator) {
        bundle.putString(PARAM_CREATOR, creator);
    }

    /**
     * 制作者情報に役割を設定する.
     * 
     * @param bundle 制作者情報
     * @param role 役割
     */
    public static final void setRole(final Bundle bundle, final String role) {
        bundle.putString(PARAM_ROLE, role);
    }

    /**
     * レスポンスデータにキーワード一覧を設定する.
     * 
     * @param response レスポンスデータ
     * @param keywords キーワード一覧 
     */
    public static final void setKeywords(final Intent response, final String[] keywords) {
        response.putExtra(PARAM_KEYWORDS, keywords);
    }

    /**
     * メディア情報にキーワード一覧を設定する.
     * 
     * @param media メディア情報
     * @param keywords キーワード一覧 
     */
    public static final void setKeywords(final Bundle media, final String[] keywords) {
        media.putStringArray(PARAM_KEYWORDS, keywords);
    }

    /**
     * レスポンスデータにジャンル一覧を設定する.
     * 
     * @param response レスポンスデータ
     * @param genres ジャンル一覧
     */
    public static final void setGenres(final Intent response, final String[] genres) {
        response.putExtra(PARAM_GENRES, genres);
    }

    /**
     * メディア情報にジャンル一覧を設定する.
     * 
     * @param media メディア情報
     * @param genres ジャンル一覧
     */
    public static final void setGenres(final Bundle media, final String[] genres) {
        media.putStringArray(PARAM_GENRES, genres);
    }

    /**
     * レスポンスにボリュームを設定する.
     * 
     * @param response レスポンスデータ
     * @param volume ボリューム(0.0 〜 1.0)
     */
    public static final void setVolume(final Intent response, final double volume) {
        if (volume < 0.0 || volume > 1.0) {
            throw new IllegalArgumentException("volume is invalid. volume=" + volume);
        }
        response.putExtra(PARAM_VOLUME, volume);
    }

    /**
     * メディアプレイヤー情報にボリュームを設定する.
     * 
     * @param mediaPlayer メディアプレイヤー
     * @param volume ボリューム(0.0 〜 1.0)
     */
    public static final void setVolume(final Bundle mediaPlayer, final double volume) {
        if (volume < 0.0 || volume > 1.0) {
            throw new IllegalArgumentException("volume is invalid. volume=" + volume);
        }
        mediaPlayer.putDouble(PARAM_VOLUME, volume);
    }

    /**
     * レスポンスにボリュームを設定する.
     * 
     * @param response レスポンスデータ
     * @param mute ミュート有りはtrue、ミュート無しはfalse
     */
    public static final void setMute(final Intent response, final boolean mute) {
        response.putExtra(PARAM_MUTE, mute);
    }

    // Getter

    /**
     * リクエストからstatusを取得する.
     * <p>
     * 以下の属性以外の値が設定されていた場合にもnullを返却する。
     * <ul>
     * <li>play</li>
     * <li>stop</li>
     * <li>resume</li>
     * </ul>
     * </p>
     * @param request リクエストデータ
     * @return status。無い場合はnullを返す。
     */
    public static final PlayStatus getPlayStatus(final Intent request) {
        String value = request.getStringExtra(PARAM_STATUS);
        return PlayStatus.getInstance(value);
    }

    /**
     * リクエストからメディアIDを取得する.
     * 
     * @param request リクエストデータ
     * @return メディアID。無い場合はnullを返す。
     */
    public static final String getMediaId(final Intent request) {
        return request.getStringExtra(PARAM_MEDIA_ID);
    }

    /**
     * リクエストから再生位置を取得する.
     * 
     * @param request リクエストデータ
     * @return 再生位置。無い場合は0を返す。
     */
    public static Integer getPos(final Intent request) {
        return parseInteger(request, PARAM_POS);
    }

    /**
     * リクエストから再生状態を取得する.
     * 
     * @param request リクエストデータ
     * @return 再生状態。無い場合はnullを返す。
     */
    public static final Status getStatus(final Intent request) {
        String statusStr = request.getStringExtra(PARAM_STATUS);
        return Status.getInstance(statusStr);
    }

    /**
     * リクエストからボリュームを取得する.
     * 
     * @param request リクエストデータ
     * @return ボリューム。無い場合は-1を返す。
     */
    public static Double getVolume(final Intent request) {
        return parseDouble(request, PARAM_VOLUME);
    }

    /**
     * リクエストからクエリーを取得する.
     * 
     * 
     * @param request リクエストデータ
     * @return クエリー。無い場合はnullを返す。
     */
    public static final String getQuery(final Intent request) {
        String query = request.getStringExtra(PARAM_QUERY);
        return query;
    }

    /**
     * リクエストからマイムタイプを取得する.
     * @param request リクエストデータ
     * @return マイムタイプ。無い場合はnullを返す。
     */
    public static final String getMIMEType(final Intent request) {
        String mimeType = request.getStringExtra(PARAM_MIME_TYPE);
        return mimeType;
    }

    /**
     * リクエストからオーダーを取得する.
     * 
     * @param request リクエストデータ
     * @return オーダー。無い場合はnullを返す。
     */
    public static final String[] getOrder(final Intent request) {
        String order = request.getStringExtra(PARAM_ORDER);
        if (order == null) {
            return null;
        }
        return order.split(",");
    }

    /**
     * リクエストからオフセットを取得する.
     * 
     * @param request リクエストデータ
     * @return オフセット。無い場合には0を返す。
     */
    public static Integer getOffset(final Intent request) {
        return parseInteger(request, PARAM_OFFSET);
    }

    /**
     * リクエストからリミットを取得する.
     * 
     * @param request リクエストデータ
     * @return リミット。無い場合には0を返す。
     */
    public static Integer getLimit(final Intent request) {
        return parseInteger(request, PARAM_LIMIT);
    }
}
