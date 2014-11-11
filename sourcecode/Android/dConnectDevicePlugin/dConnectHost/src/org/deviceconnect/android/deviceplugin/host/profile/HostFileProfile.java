/*
 HostFileProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */

package org.deviceconnect.android.deviceplugin.host.profile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.android.profile.FileProfile;
import org.deviceconnect.android.provider.FileManager;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.message.intent.message.IntentDConnectMessage;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Video;
import android.util.Log;
import android.webkit.MimeTypeMap;

/**
 * File Profile.
 * @author NTT DOCOMO, INC.
 */
public class HostFileProfile extends FileProfile {

    /** Debug Tag. */
    private static final String TAG = "HOST";

    /** FileManager. */
    private static FileManager mFileManager;

    /**
     * コンストラクタ.
     * 
     * @param fileMgr ファイル管理クラス.
     */
    public HostFileProfile(final FileManager fileMgr) {
        super(fileMgr);
        mFileManager = fileMgr;
    }

    @Override
    protected boolean onGetReceive(final Intent request, final Intent response, 
                        final String deviceId, final String path) {

        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (path == null) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else {
            File mFile = null;
            String filePath = "";

            // パス名の先頭に"/"が含まれている場合
            if (path.indexOf("/") == 0) {
                mFile = new File(getFileManager().getBasePath() + path);
                filePath = getFileManager().getContentUri() + path;
            } else {
                mFile = new File(getFileManager().getBasePath() + "/" + path);
                filePath = getFileManager().getContentUri() + path;
            }
            
            if (mFile.isFile()) {
                setResult(response, IntentDConnectMessage.RESULT_OK);
                response.putExtra(FileProfile.PARAM_MIME_TYPE, getMIMEType(path));
                response.putExtra(FileProfile.PARAM_URI, filePath);
            } else {
                MessageUtils.setInvalidRequestParameterError(response, "not found:" + path);
            }
        }
        return true;
    }

    /**
     * OnList実装メソッド.
     * 
     * @param request リクエスト
     * @param response レスポンス
     * @param deviceId デバイスID
     * @param path リストを表示するパス
     * @param mimeType MIME-TYPE
     * @param order 並び順
     * @param offset オフセット
     * @param limit 配列の最大数
     * @return 非同期処理を行っているため,falseとしておきスレッドで明示的にsendBroadcastで返却
     */
    @Override
    protected boolean onGetList(final Intent request, final Intent response, final String deviceId, final String path,
            final String mimeType, final String order, final Integer offset, final Integer limit) {

        if (deviceId == null) {
            createEmptyDeviceId(response);
            return true;
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
            return true;
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    File tmpDir = null;

                    Boolean currentTop = false;
                    if (path == null) {
                        // nullの時はTopに指定
                        tmpDir = getFileManager().getBasePath();
                        currentTop = true;
                    } else if (path.equals("/")) {
                        // /の場合はTopに指定
                        tmpDir = getFileManager().getBasePath();
                        currentTop = true;
                    } else if (path.equals("..")) {
                        // ..の場合は、1つ上のフォルダを指定
                        String[] mDirs = path.split("/", 0);
                        String mPath = "/";
                        for (int i = 0; i < mDirs.length - 1; i++) {
                            mPath += mDirs[i] + "/";
                        }
                        if (mDirs.length == 1) {
                            currentTop = true;
                        }

                        tmpDir = new File(getFileManager().getBasePath(), mPath);
                    } else {
                        // それ以外は、そのフォルダを指定
                        tmpDir = new File(getFileManager().getBasePath() + "/" + path);
                        currentTop = false;
                    }

                    File[] respFileList = tmpDir.listFiles();

                    if (respFileList == null) {
                        setResult(response, DConnectMessage.RESULT_ERROR);
                        MessageUtils.setUnknownError(response, "Dir is not exist:" + tmpDir);
                        getContext().sendBroadcast(response);
                    } else {

                        List<Bundle> resp = new ArrayList<Bundle>();
                        Bundle respParam = new Bundle();

                        // ..のフォルダを追加(常時)
                        if (!currentTop) {
                            File mParentDir = new File("..");
                            String mMineType = "folder/dir";
                            respParam = addResponseParamToArray(mParentDir, respParam, mMineType);

                            resp.add((Bundle) respParam.clone());
                        }

                        File[] tmpRespFileList = null;
                        if (order != null && order.endsWith("desc")) {
                            int last = respFileList.length;
                            tmpRespFileList = new File[last];
                            for (File file : respFileList) {

                                tmpRespFileList[last - 1] = file;
                                last--;
                            }
                            respFileList = tmpRespFileList;
                        }

                        int counter = 0;
                        int tmpLimit = 0;
                        int tmpOffset = 0;
                        if (limit != null && limit >= 0) {
                            tmpLimit = limit;
                        }
                        if (offset != null && offset >= 0) {
                            tmpOffset = offset;
                        }
                        int limitCounter = tmpLimit + tmpOffset;

                        for (File file : respFileList) {

                            if (limit == null || (limit != null && limitCounter > counter)) {
                                respParam = addResponseParamToArray(file, respParam, mimeType);
                                if (offset == null || (offset != null && counter >= offset)) {
                                    resp.add((Bundle) respParam.clone());
                                }
                            }
                            counter++;
                        }

                        // 結果を非同期で返信
                        setResult(response, IntentDConnectMessage.RESULT_OK);
                        response.putExtra(PARAM_COUNT, respFileList.length);
                        response.putExtra(PARAM_FILES, resp.toArray(new Bundle[resp.size()]));
                        getContext().sendBroadcast(response);
                    }
                }
            }).start();
        }
        return false;
    }

    @Override
    protected boolean onPostSend(final Intent request, final Intent response, final String deviceId, final String path,
            final String mimeType, final byte[] data) {

        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (path == null) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else if (data == null) {
            MessageUtils.setInvalidRequestParameterError(response, "data is null.");
            return true;
        } else {

            String mUri = null;
            try {
                mUri = getFileManager().saveFile(path, data);
            } catch (IOException e) {
                mUri = null;
            }
            if (mUri == null) {
                setResult(response, DConnectMessage.RESULT_ERROR);
                MessageUtils.setUnknownError(response, "Path is null, you must input path.");
            } else {
                String mMineType = getMIMEType(getFileManager().getBasePath() + "/" + path);

                Log.i(TAG, "mMineType:" + mMineType);

                // MimeTypeが不明の場合はエラーを返す
                if (mMineType == null) {
                    MessageUtils.setUnknownError(response, "Not support format");
                    setResult(response, DConnectMessage.RESULT_ERROR);
                    return true;
                }
                // 音楽データに関してはContents Providerに登録
                if (mMineType.endsWith("audio/mpeg") 
                        || mMineType.endsWith("audio/x-wav")
                        || mMineType.endsWith("audio/mp4") 
                        || mMineType.endsWith("audio/ogg")
                        || mMineType.endsWith("audio/mp3")
                        || mMineType.endsWith("audio/x-ms-wma")
                        ) {

                    MediaMetadataRetriever mMediaMeta = new MediaMetadataRetriever();
                    mMediaMeta.setDataSource(getFileManager().getBasePath() + "/" + path);
                    String mTitle = mMediaMeta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                    String mComposer = mMediaMeta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER);
                    String mArtist = mMediaMeta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                    String mDuration = mMediaMeta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    String mAuthor = mMediaMeta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_AUTHOR);
                    ContentResolver mContentResolver = this.getContext().getApplicationContext().getContentResolver();
                    ContentValues mValues = new ContentValues();

                    if (mTitle == null) {
                        String[] array = path.split("/");
                        mTitle = array[array.length - 1];
                    }
                    mValues.put(Audio.Media.TITLE, mTitle);
                    mValues.put(Audio.Media.DISPLAY_NAME, mTitle);
                    mValues.put(Audio.Media.COMPOSER, mComposer);
                    mValues.put(Audio.Media.ARTIST, mArtist);
                    mValues.put(Audio.Media.DURATION, mDuration);
                    // mValues.put(Audio.Media.DATE_TAKEN, dateTaken);
                    mValues.put(Audio.Media.MIME_TYPE, mMineType);
                    mValues.put(Audio.Media.DATA, getFileManager().getBasePath() + "/" + path);
                    mContentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, mValues);
                } else if (mMineType.endsWith("video/mp4") || mMineType.endsWith("video/3gpp")
                        || mMineType.endsWith("video/3gpp2") || mMineType.endsWith("video/mpeg")
                        || mMineType.endsWith("video/m4v")
                        ) {
                    MediaMetadataRetriever mMediaMeta = new MediaMetadataRetriever();
                    mMediaMeta.setDataSource(getFileManager().getBasePath() + "/" + path);
                    String mTitle = mMediaMeta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                    String mComposer = mMediaMeta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER);
                    String mArtist = mMediaMeta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                    String mDuration = mMediaMeta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    ContentResolver mContentResolver = this.getContext().getApplicationContext().getContentResolver();
                    ContentValues mValues = new ContentValues();

                    mValues.put(Video.Media.TITLE, mTitle);
                    mValues.put(Video.Media.DISPLAY_NAME, mTitle);
                    mValues.put(Video.Media.ARTIST, mArtist);
                    mValues.put(Video.Media.DURATION, mDuration);
                    mValues.put(Video.Media.MIME_TYPE, mMineType);
                    mValues.put(Video.Media.DATA, getFileManager().getBasePath() + "/" + path);
                    mContentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, mValues);
                }

                setResult(response, DConnectMessage.RESULT_OK);
            }
        }
        return true;
    }

    @Override
    protected boolean onDeleteRemove(final Intent request, final Intent response, final String deviceId,
            final String path) {

        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (path == null) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else {

            Boolean result = getFileManager().removeFile(path);

            if (result) {
                setResult(response, DConnectMessage.RESULT_OK);
            } else {
                setResult(response, DConnectMessage.RESULT_ERROR);
                MessageUtils.setUnknownError(response, "not found:" + path);
            }
        }
        return true;
    }
    
    @Override
    protected boolean onPostMkdir(final Intent request, final Intent response, final String deviceId, 
            final String path) {
        
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (path == null) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else {
            File mBaseDir = mFileManager.getBasePath();
            File mMakeDir = new File(mBaseDir, path);
            
            if (mMakeDir.isDirectory()) {
                setResult(response, DConnectMessage.RESULT_ERROR);
                MessageUtils.setUnknownError(response, "can not make dir :" + mMakeDir);
            } else {
                try {
                    mMakeDir.mkdir();
                    setResult(response, DConnectMessage.RESULT_OK);
                } catch (Exception e) {
                    setResult(response, DConnectMessage.RESULT_ERROR);
                    MessageUtils.setUnknownError(response, "can not make dir :" + mMakeDir);
                }
            }
        }
        
        return true;
    }
    
    @Override
    protected boolean onDeleteRmdir(final Intent request, final Intent response, final String deviceId, 
            final String path, final boolean force) {
        
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (path == null) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else {
            File mBaseDir = mFileManager.getBasePath();
            File mMakeDir = new File(mBaseDir, path);
            
           if (mMakeDir.isFile()) {
                setResult(response, DConnectMessage.RESULT_ERROR);
                MessageUtils.setUnknownError(response, mMakeDir + "is file");
           } else {
                try {
                    mMakeDir.delete();
                    setResult(response, DConnectMessage.RESULT_OK);
                } catch (Exception e) {
                    setResult(response, DConnectMessage.RESULT_ERROR);
                    MessageUtils.setUnknownError(response, "can not make dir :" + mMakeDir);
                }
           }
        }
        
        return true;
    }
    
    /**
     * ファイルパラメータ格納用メソッド.
     * 
     * @param file ファイル.
     * @param respParam ファイルパラメータ格納用Bundle.
     * @param mimeType マイムタイプ.
     * @return ファイルパラメータ格納済みBundle
     */
    protected Bundle addResponseParamToArray(final File file, final Bundle respParam, final String mimeType) {

        if (file.isFile()) {
            // ファイルの場合
            String path = file.getPath().replaceAll("" + mFileManager.getBasePath(), "");
            respParam.putString(PARAM_PATH, path);
            respParam.putString(PARAM_FILE_NAME, file.getName());
            respParam.putString(PARAM_MIME_TYPE, getMIMEType(file.getPath() + file.getName()));
            respParam.putString(PARAM_FILE_TYPE, "0");
            respParam.putLong(PARAM_FILE_SIZE, file.length());
            respParam.putString(PARAM_UPDATE_DATE,
                    "" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(file.lastModified()));
        } else {
            // フォルダの場合
            String path = file.getPath().replaceAll("" + mFileManager.getBasePath(), "");
            respParam.putString(PARAM_PATH, path);
            respParam.putString(PARAM_FILE_NAME, file.getName());
            respParam.putString(PARAM_MIME_TYPE, "dir/folder");
            respParam.putString(PARAM_FILE_TYPE, "1");
            respParam.putLong(PARAM_FILE_SIZE, file.length());
            respParam.putString(PARAM_UPDATE_DATE,
                    "" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(file.lastModified()));
        }

        return respParam;
    }

    /**
     * ファイル名からMIMEタイプ取得.
     * 
     * @param path パス
     * @return MIMEタイプ
     */
    public String getMIMEType(final String path) {

        // 拡張子を取得
        String ext = MimeTypeMap.getFileExtensionFromUrl(path);
        // 小文字に変換
        ext = ext.toLowerCase();
        // MIME Typeを返す
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
    }

    /**
     * デバイスIDをチェックする.
     * 
     * @param deviceId デバイスID
     * @return <code>deviceId</code>がテスト用デバイスIDに等しい場合はtrue、そうでない場合はfalse
     */
    private boolean checkDeviceId(final String deviceId) {
        String regex = HostNetworkServiceDiscoveryProfile.DEVICE_ID;
        Pattern mPattern = Pattern.compile(regex);
        Matcher match = mPattern.matcher(deviceId);

        return match.find();
    }

    /**
     * デバイスIDが空の場合のエラーを作成する.
     * 
     * @param response レスポンスを格納するIntent
     */
    private void createEmptyDeviceId(final Intent response) {
        MessageUtils.setEmptyDeviceIdError(response);
    }

    /**
     * デバイスが発見できなかった場合のエラーを作成する.
     * 
     * @param response レスポンスを格納するIntent
     */
    private void createNotFoundDevice(final Intent response) {
        MessageUtils.setNotFoundDeviceError(response);
    }
}
