/*
 FileProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.profile;

import java.io.IOException;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;

import com.nttdocomo.android.dconnect.message.MessageUtils;
import com.nttdocomo.android.dconnect.provider.FileManager;
import com.nttdocomo.dconnect.profile.FileProfileConstants;

/**
 * File プロファイル.
 * 
 * <p>
 * スマートデバイスに対してのファイル操作機能を提供するAPI.<br/>
 * スマートデバイスに対してのファイル操作機能を提供するデバイスプラグインは当クラスを継承し、対応APIを実装すること。 <br/>
 * </p>
 * 
 * <h1>各API提供メソッド</h1>
 * <p>
 * File Profile の各APIへのリクエストに対し、以下のコールバックメソッド群が自動的に呼び出される。<br/>
 * サブクラスは以下のメソッド群からデバイスプラグインが提供するAPI用のメソッドをオーバーライドし、機能を実装すること。<br/>
 * オーバーライドされていない機能は自動的に非対応APIとしてレスポンスを返す。
 * </p>
 * <ul>
 * <li>File Send API [POST] :
 * {@link FileProfile#onPostSend(Intent, Intent, String, String, String, 
 * com.nttdocomo.dconnect.profile.FileProfileConstants.FileType, byte[])}</li>
 * <li>Make Directory API [POST] :
 * {@link FileProfile#onPostMkdir(Intent, Intent, String, String)}</li>
 * <li>File Receive API [GET] :
 * {@link FileProfile#onGetReceive(Intent, Intent, String, String)}</li>
 * <li>File List API [GET] :
 * {@link FileProfile#onGetList(Intent, Intent, String, String, String, String[], Integer, Integer)}</li>
 * <li>File Remove API [DELETE] :
 * {@link FileProfile#onDeleteRemove(Intent, Intent, String, String)}</li>
 * <li>Remove Directory API [POST] :
 * {@link FileProfile#onDeleteRmdir(Intent, Intent, String, String, boolean)}</li>
 * </ul>
 * @author NTT DOCOMO, INC.
 */
public abstract class FileProfile extends DConnectProfile implements FileProfileConstants {

    /** ファイル管理クラス. */
    private FileManager mFileMgr;

    /**
     * コンストラクタ.
     * @param fileMgr ファイル管理クラス
     */
    public FileProfile(final FileManager fileMgr) {
        if (fileMgr == null) {
            throw new IllegalArgumentException("fileMgr is null.");
        }
        mFileMgr = fileMgr;
    }

    @Override
    public final String getProfileName() {
        return PROFILE_NAME;
    }

    @Override
    protected boolean onGetRequest(final Intent request, final Intent response) {
        String attribute = getAttribute(request);
        boolean result = true;

        String deviceId = getDeviceID(request);
        if (ATTRIBUTE_RECEIVE.equals(attribute)) {
            String path = getPath(request);
            result = onGetReceive(request, response, deviceId, path);
        } else if (ATTRIBUTE_LIST.equals(attribute)) {
            
            String path = getPath(request);
            String mimeType = getMIMEType(request);
            String order = getOrder(request);
            Integer offset = getOffset(request);
            Integer limit = getLimit(request);
            result = onGetList(request, response, deviceId, path, mimeType, order, offset, limit);
        } else {
            MessageUtils.setUnknownAttributeError(response);
        }

        return result;
    }

    @Override
    protected boolean onPostRequest(final Intent request, final Intent response) {
        String attribute = getAttribute(request);
        boolean result = true;

        if (ATTRIBUTE_SEND.equals(attribute)) {
            String uri = request.getStringExtra(FileProfile.PARAM_URI);
            byte[] data = getContentData(uri);
            if (data == null) {
                MessageUtils.setInvalidRequestParameterError(response);
            } else {
                String deviceId = getDeviceID(request);
                String path = getPath(request);
                String mimeType = getMIMEType(request);
                result = onPostSend(request, response, deviceId, path, mimeType, data);
            }
        } else if (ATTRIBUTE_MKDIR.equals(attribute)) {
            String path = getPath(request);
            String deviceId = getDeviceID(request);
            result = onPostMkdir(request, response, deviceId, path);
        } else {
            MessageUtils.setUnknownAttributeError(response);
        }

        return result;
    }
    
    @Override
    protected boolean onDeleteRequest(final Intent request, final Intent response) {
        String attribute = getAttribute(request);
        boolean result = true;

        if (ATTRIBUTE_REMOVE.equals(attribute)) {
            String deviceId = getDeviceID(request);
            String path = getPath(request);
            result = onDeleteRemove(request, response, deviceId, path);
        } else if (ATTRIBUTE_RMDIR.equals(attribute)) {
            String path = getPath(request);
            String deviceId = getDeviceID(request);
            boolean force = getForce(request);
            result = onDeleteRmdir(request, response, deviceId, path, force);
        } else {
            MessageUtils.setUnknownAttributeError(response);
        }

        return result;
    }

    /**
     * receive属性取得リクエストハンドラー.<br/>
     * スマートフォンまたは周辺機器上のテキストや画像、音声、動画（リソースも含む）のデータを提供し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param path パス
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onGetReceive(final Intent request, final Intent response, final String deviceId, 
            final String path) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * list属性取得リクエストハンドラー.<br/>
     * スマートフォンまたは周辺機器上のテキストや画像、音声、動画（リソースも含む）のファイル名を検索し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param path ファイルのパス。省略された場合null。
     * @param mimeType ファイルのMIMEタイプ。省略された場合null。
     * @param order 並び順。省略された場合null。
     * @param offset 取得開始位置。省略された場合はnull。
     * @param limit 最大取得数。省略された場合はnull。
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onGetList(final Intent request, final Intent response, final String deviceId, final String path,
            final String mimeType, final String order, final Integer offset, final Integer limit) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * mkdir属性リクエストハンドラー.<br/>
     * 指定されたパスにディレクトリを作成しその結果をレスポンスパラメータに格納する。 
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param path ファイルパス。ファイル名を保存する。
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onPostMkdir(final Intent request, final Intent response, final String deviceId, 
            final String path) {
        setUnsupportedError(response);
        return true;
    }
    
    /**
     * send属性リクエストハンドラー.<br/>
     * スマートフォンまたは周辺機器から他方のスマートデバイスに対して、テキストや画像、音声、動画（リソースも含む）を送信し、
     * その結果をレスポンスパラメータに格納する。 レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param path ファイルパス。ファイル名を保存する。
     * @param mimeType ファイルのマイムタイプ。省略された場合はnullが渡される。
     * @param data uriパラメータから取得できるファイルのデータ。uriパラメータが省略された場合はnullが渡される。
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onPostSend(final Intent request, final Intent response, final String deviceId, 
            final String path, final String mimeType, final byte[] data) {
        setUnsupportedError(response);
        return true;
    }

    /**
     * remove属性リクエストハンドラー.<br/>
     * スマートフォンまたは周辺機器から他方のスマートデバイスに対して、テキストや画像、音声、動画（リソースも含む）を送信し、
     * その結果をレスポンスパラメータに格納する。 レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param path ファイルパス
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onDeleteRemove(final Intent request, final Intent response, final String deviceId, 
            final String path) {
        setUnsupportedError(response);
        return true;
    }
    
    /**
     * rmdir属性リクエストハンドラー.<br/>
     * 指定されたパスのディレクトリを削除しその結果をレスポンスパラメータに格納する。 
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param path ファイルパス
     * @param force 強制削除フラグ
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onDeleteRmdir(final Intent request, final Intent response, final String deviceId, 
            final String path, final boolean force) {
        setUnsupportedError(response);
        return true;
    }

    // ------------------------------------
    // セッターメソッド群
    // ------------------------------------

    /**
     * レスポンスにファイルのURIを設定する.
     * 
     * @param response レスポンスパラメータ
     * @param uri ファイルのURI
     */
    public static void setURI(final Intent response, final String uri) {
        response.putExtra(PARAM_URI, uri);
    }

    /**
     * レスポンスにファイルを設定する.
     * 
     * この中で引数に指定したファイルをFileManagerに保存し、uriを作成して レスポンスにファイルのURIを設定する。
     * 
     * @param response レスポンスパラメータ
     * @param name ファイル名
     * @param data uriパラメータから取得できるファイルのデータ。uriパラメータが省略された場合はnullが渡される。
     * 
     * @throws IOException ファイルの保存に失敗した場合に発生
     */
    public final void setURI(final Intent response, final String name, final byte[] data) throws IOException {
        FileManager fileMgr = getFileManager();
        if (fileMgr == null) {
            throw new IOException("FileManager is not implemented.");
        }

        String uri = fileMgr.saveFile(name, data);
        if (uri == null) {
            throw new IOException("Failed to save a file.");
        }
        setURI(response, uri);
    }

    /**
     * ファイルデータにファイルパスを設定する.
     * 
     * @param file ファイルデータ
     * @param path ファイルパス
     */
    public static void setPath(final Bundle file, final String path) {
        file.putString(PARAM_PATH, path);
    }
    
    /**
     * ファイルデータに更新日を設定する.
     * 
     * @param file ファイルデータ
     * @param updateDate 更新日
     */
    public static void setUpdateDate(final Bundle file, final String updateDate) {
        file.putString(PARAM_UPDATE_DATE, updateDate);
    }

    /**
     * ファイルデータにファイルのMIMEタイプを設定する.
     * 
     * @param file ファイルデータ
     * @param mimeType MIMEタイプ
     */
    public static void setMIMEType(final Bundle file, final String mimeType) {
        file.putString(PARAM_MIME_TYPE, mimeType);
    }
    
    /**
     * メッセージににファイルのMIMEタイプを設定する.
     * 
     * @param response メッセージ
     * @param mimeType MIMEタイプ
     */
    public static void setMIMEType(final Intent response, final String mimeType) {
        response.putExtra(PARAM_MIME_TYPE, mimeType);
    }

    /**
     * レスポンスにファイルデータ一覧を設定する.
     * 
     * @param response レスポンスパラメータ
     * @param files ファイルデータ一覧
     */
    public static void setFiles(final Intent response, final Bundle[] files) {
        response.putExtra(PARAM_FILES, files);
    }
    
    /**
     * レスポンスにファイルデータ一覧を設定する.
     * 
     * @param response レスポンスパラメータ
     * @param files ファイルデータ一覧
     */
    public static void setFiles(final Intent response, final List<Bundle> files) {
        setFiles(response, files.toArray(new Bundle[files.size()]));
    }

    /**
     * ファイルデータにファイル名を設定する.
     * 
     * @param file ファイルデータ
     * @param fileName ファイル名
     */
    public static void setFileName(final Bundle file, final String fileName) {
        file.putString(PARAM_FILE_NAME, fileName);
    }

    /**
     * ファイルデータにファイルサイズを設定する.
     * 
     * @param file ファイルデータ
     * @param fileSize ファイルサイズ
     */
    public static void setFileSize(final Bundle file, final long fileSize) {
        file.putLong(PARAM_FILE_SIZE, fileSize);
    }
    
    /**
     * レスポンスにカウントを設定する.
     * 
     * @param response レスンポンスデータ
     * @param count カウント
     */
    public static void setCount(final Intent response, final int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Count must be larger than 0.");
        }
        response.putExtra(PARAM_COUNT, count);
    }
    
    /**
     * レスポンスにファイルタイプを設定する.
     * 
     * @param response レスポンスデータ
     * @param fileType ファイルタイプ
     */
    public static void setFileType(final Intent response, final FileType fileType) {
        response.putExtra(PARAM_FILE_TYPE, fileType.getValue());
    }

    // ------------------------------------
    // ゲッターメソッド群
    // ------------------------------------

    /**
     * リクエストからファイル名を取得する.
     * 
     * @param request リクエストパラメータ
     * @return ファイル名。無い場合はnullを返す。
     */
    public static String getFileName(final Intent request) {
        return request.getStringExtra(PARAM_FILE_NAME);
    }

    /**
     * リクエストからファイルのURIを取得する.
     * 
     * @param request リクエストパラメータ
     * @return ファイルのURI。無い場合はnullを返す。
     */
    public static String getURI(final Intent request) {
        return request.getStringExtra(PARAM_URI);
    }

    /**
     * リクエストからファイルのMIMEタイプを取得する.
     * 
     * @param request リクエストパラメータ
     * @return ファイルのMIMEタイプ。無い場合はnullを返す。
     */
    public static String getMIMEType(final Intent request) {
        return request.getStringExtra(PARAM_MIME_TYPE);
    }

    /**
     * リクエストからファイルパスを取得する.
     * 
     * @param request リクエストパラメータ
     * @return ファイルパス。無い場合はnullを返す。
     */
    public static String getPath(final Intent request) {
        return request.getStringExtra(PARAM_PATH);
    }
    
    /**
     * リクエストからオーダーを取得する.
     * 
     * @param request リクエストデータ
     * @return オーダー。無い場合はnullを返す。
     */
    public static final String getOrder(final Intent request) {
        return request.getStringExtra(PARAM_ORDER);
    }
    
    /**
     * リクエストからオフセットを取得する.
     * 
     * @param request リクエストパラメータ
     * @return オフセット。無い場合はnullを返す。
     */
    public static Integer getOffset(final Intent request) {
        return parseInteger(request, PARAM_OFFSET);
    }
    
    /**
     * リクエストからリミットを取得する.
     * 
     * @param request リクエストパラメータ
     * @return リミット。無い場合は0を返す。
     */
    public static Integer getLimit(final Intent request) {
        return parseInteger(request, PARAM_LIMIT);
    }
    
    /**
     * リクエストから強制削除フラグを取得する.
     * 
     * @param request リクエストパラメータ
     * @return 強制削除フラグ。省略された場合はfalseを返す。
     */
    public static boolean getForce(final Intent request) {
        Boolean force = parseBoolean(request, PARAM_FORCE);
        if (force == null) {
            return false;
        }
        return force;
    }

    /**
     * FileManagerのインスタンスを取得する.
     * 
     * このクラスで実装されたFileManagerを経由してDevice Connect Managerにファイルを送信する。
     * 
     * @return FileManagerのインスタンス
     */
    protected FileManager getFileManager() {
        return mFileMgr;
    }
}
