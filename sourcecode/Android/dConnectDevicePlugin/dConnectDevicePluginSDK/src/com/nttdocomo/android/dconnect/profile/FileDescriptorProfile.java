/*
 FileDescriptorProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.profile;

import android.content.Intent;
import android.os.Bundle;

import com.nttdocomo.android.dconnect.message.MessageUtils;
import com.nttdocomo.dconnect.profile.FileDescriptorProfileConstants;

/**
 * File Descriptor プロファイル.
 * 
 * <p>
 * ファイルディスクリプタ操作機能を提供するAPI.<br/>
 * ファイルディスクリプタ操作機能を提供するデバイスプラグインは当クラスを継承し、対応APIを実装すること。 <br/>
 * </p>
 * 
 * <h1>各API提供メソッド</h1>
 * <p>
 * File Descriptor Profile の各APIへのリクエストに対し、以下のコールバックメソッド群が自動的に呼び出される。<br/>
 * サブクラスは以下のメソッド群からデバイスプラグインが提供するAPI用のメソッドをオーバーライドし、機能を実装すること。<br/>
 * オーバーライドされていない機能は自動的に非対応APIとしてレスポンスを返す。
 * </p>
 * <ul>
 * <li>File Descriptor Open API [GET] :
 * {@link FileDescriptorProfile#onGetOpen(Intent, Intent, String, String, String, String)}
 * </li>
 * <li>File Descriptor Close API [PUT] :
 * {@link FileDescriptorProfile#onPutClose(Intent, Intent, String, String))}</li>
 * <li>File Descriptor Read API [GET] :
 * {@link FileDescriptorProfile#onGetRead(Intent, Intent, String, String, Long, Long)
 * )}</li>
 * <li>File Descriptor Write API [PUT] :
 * {@link FileDescriptorProfile#onPutWrite(Intent, Intent, String, String, byte[], Long)
 * )}</li>
 * <li>File Descriptor WatchFile Event API [Register] :
 * {@link FileDescriptorProfile#onPutOnWatchFile(Intent, Intent, String, String)}
 * </li>
 * <li>File Descriptor WatchFile Event API [Unregister] :
 * {@link FileDescriptorProfile#onDeleteOnWatchFile(Intent, Intent, String, String)}
 * </li>
 * </ul>
 * @author NTT DOCOMO, INC.
 */
public abstract class FileDescriptorProfile extends DConnectProfile implements FileDescriptorProfileConstants {

    @Override
    public final String getProfileName() {
        return PROFILE_NAME;
    }

    @Override
    protected boolean onGetRequest(final Intent request, final Intent response) {
        String attribute = getAttribute(request);
        boolean result = true;

        if (attribute == null) {
            MessageUtils.setUnknownAttributeError(response);
        } else {
            String deviceId = getDeviceID(request);
            if (attribute.equals(ATTRIBUTE_OPEN)) {
                String path = getPath(request);
                Flag flag = getFlag(request);
                result = onGetOpen(request, response, deviceId, path, flag);
            } else if (attribute.equals(ATTRIBUTE_READ)) {
                String path = getPath(request);
                Long length = getLength(request);
                Long position = getPosition(request);
                result = onGetRead(request, response, deviceId, path, length, position);
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
            if (attribute.equals(ATTRIBUTE_CLOSE)) {
                String path = getPath(request);
                result = onPutClose(request, response, deviceId, path);
            } else if (attribute.equals(ATTRIBUTE_WRITE)) {
                String path = getPath(request);
                byte[] data = getContentData(getUri(request));
                Long position = getPosition(request);
                result = onPutWrite(request, response, deviceId, path, data, position);
            } else if (attribute.equals(ATTRIBUTE_ON_WATCH_FILE)) {
                result = onPutOnWatchFile(request, response, deviceId, getSessionKey(request));
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

        if (ATTRIBUTE_ON_WATCH_FILE.equals(attribute)) {
            result = onDeleteOnWatchFile(request, response, getDeviceID(request), getSessionKey(request));
        } else {
            MessageUtils.setUnknownAttributeError(response);
        }

        return result;
    }

    // ------------------------------------
    // GET
    // ------------------------------------

    /**
     * ファイルオープンリクエストハンドラー.<br/>
     * 指定されたファイルを開き、その結果をレスポンスパラメータに格納する。 レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param path ファイルパス
     * @param flag ファイルオープンモード
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onGetOpen(final Intent request, final Intent response, final String deviceId, 
            final String path, final Flag flag) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * ファイルリードリクエストハンドラー.<br/>
     * 指定されたファイルを取得し、その結果をレスポンスパラメータに格納する。 レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param path ファイルパス
     * @param length ファイルの読み込みサイズ
     * @param position 読み込み開始位置。省略された場合はnullが入る。
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onGetRead(final Intent request, final Intent response, final String deviceId, 
            final String path, final Long length, final Long position) {
        setUnsupportedError(response);
        return true;
    }

    // ------------------------------------
    // PUT
    // ------------------------------------

    /**
     * ファイルクローズリクエストハンドラー.<br/>
     * 指定されたファイルを閉じ、その結果をレスポンスパラメータに格納する。 レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param path ファイルパス
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onPutClose(final Intent request, final Intent response, final String deviceId, 
            final String path) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * ファイル書き込みリクエストハンドラー.<br/>
     * 指定されたファイルに書き込みをし、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param path ファイルパス
     * @param data 書き込むデータのバイナリ。リクエストパラメータのuriから取り出したデータ。
     * @param position ファイルの書き込み開始位置。省略された場合-1が入る。
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onPutWrite(final Intent request, final Intent response, final String deviceId,
            final String path, final byte[] data, final Long position) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * watchfileコールバック登録リクエストハンドラー.<br/>
     * watchfileコールバックを登録し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param sessionKey セッションキー
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onPutOnWatchFile(final Intent request, final Intent response, final String deviceId, 
            final String sessionKey) {
        setUnsupportedError(response);
        return true;
    }

    // ------------------------------------
    // DELETE
    // ------------------------------------

    /**
     * watchfileコールバック解除リクエストハンドラー.<br/>
     * watchfileコールバックを解除し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param sessionKey セッションキー
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onDeleteOnWatchFile(final Intent request, final Intent response, final String deviceId, 
            final String sessionKey) {
        setUnsupportedError(response);
        return true;
    }

    // ------------------------------------
    // セッターメソッド群
    // ------------------------------------

    /**
     * ファイルデータに現在更新時間を設定する.
     * 
     * @param file ファイルデータ
     * @param curr 現在の更新時間
     */
    public static void setCurr(final Bundle file, final String curr) {
        file.putString(PARAM_CURR, curr);
    }

    /**
     * ファイルデータに以前の更新時間を設定する.
     * 
     * @param file ファイルデータ
     * @param prev 以前の更新時間
     */
    public static void setPrev(final Bundle file, final String prev) {
        file.putString(PARAM_PREV, prev);
    }

    /**
     * レスポンスに読み込んだファイルのサイズを設定する.
     * 
     * @param response レスポンスパラメータ
     * @param size 読み込んだファイルのサイズ
     */
    public static void setSize(final Intent response, final int size) {
        response.putExtra(PARAM_SIZE, size);
    }

    /**
     * レスポンスにファイルのパスを設定する.
     * 
     * @param file ファイルデータ
     * @param path ファイルのパス
     */
    public static void setPath(final Bundle file, final String path) {
        file.putString(PARAM_PATH, path);
    }
    
    /**
     * レスポンスにファイルデータを設定する.
     * 
     * @param response レスポンスパラメータ
     * @param fileData ファイルデータ
     */
    public static void setFileData(final Intent response, final String fileData) {
        response.putExtra(PARAM_FILE_DATA, fileData);
    }
    
    /**
     * メッセージにファイル情報を設定する.
     * 
     * @param message メッセージパラメータ
     * @param file ファイル情報
     */
    public static void setFile(final Intent message, final Bundle file) {
        message.putExtra(PARAM_FILE, file);
    }

    // ------------------------------------
    // リクエストゲッターメソッド群
    // ------------------------------------

    /**
     * リクエストからPATHを取得する.
     * 
     * @param request リクエストパラメータ
     * @return PATH文字列。無い場合はnullを返す。
     */
    public static String getPath(final Intent request) {
        String path = request.getStringExtra(PARAM_PATH);
        return path;
    }

    /**
     * リクエストからフラグを取得する.
     * 
     * @param request リクエストパラメータ
     * @return フラグ文字列。無い場合はnullを返す。
     */
    public static Flag getFlag(final Intent request) {
        String value = request.getStringExtra(PARAM_FLAG);
        return Flag.getInstance(value);
    }

    /**
     * リクエストから読み込みサイズを取得する.
     * 
     * @param request リクエストパラメータ
     * @return ファイルサイズ。無い場合はnullを返す。
     */
    public static Long getLength(final Intent request) {
        return parseLong(request, PARAM_LENGTH);
    }

    /**
     * リクエストからファイルの読み込み開始位置を取得する.
     * 
     * @param request リクエストパラメータ
     * @return ファイル読み込み開始位置。無い場合は-1を返す。
     */
    public static Long getPosition(final Intent request) {
        return parseLong(request, PARAM_POSITION);
    }

    /**
     * リクエストからURIを取得する.
     * 
     * @param request リクエストパラメータ
     * @return ファイルのURI
     */
    public static String getUri(final Intent request) {
        return request.getStringExtra(PARAM_URI);
    }
}
