/*
 HostMediaPlayerProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.host.profile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.deviceconnect.android.deviceplugin.host.HostDeviceService;
import org.deviceconnect.android.event.EventError;
import org.deviceconnect.android.event.EventManager;
import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.android.profile.MediaPlayerProfile;
import org.deviceconnect.android.provider.FileManager;
import org.deviceconnect.message.DConnectMessage;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

/**
 * Media Player Profile.
 * @author NTT DOCOMO, INC.
 */
public class HostMediaPlayerProfile extends MediaPlayerProfile {

    /** Debug Tag. */
    private static final String TAG = "HOST";

    /** Error. */
    private static final int ERROR_VALUE_IS_NULL = 100;

    @Override
    protected boolean onPutPlay(final Intent request, final Intent response, final String deviceId) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
            ((HostDeviceService) getContext()).playMedia();
            setResult(response, DConnectMessage.RESULT_OK);
        }
        return true;
    }

    @Override
    protected boolean onPutStop(final Intent request, final Intent response, final String deviceId) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
            ((HostDeviceService) getContext()).stopMedia();
            setResult(response, DConnectMessage.RESULT_OK);
        }
        return true;
    }

    @Override
    protected boolean onPutPause(final Intent request, final Intent response, final String deviceId) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
            ((HostDeviceService) getContext()).pauseMedia();
            setResult(response, DConnectMessage.RESULT_OK);
        }
        return true;
    }

    @Override
    protected boolean onPutResume(final Intent request, final Intent response, final String deviceId) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
            ((HostDeviceService) getContext()).resumeMedia();
            setResult(response, DConnectMessage.RESULT_OK);
        }
        return true;
    }

    @Override
    protected boolean onGetPlayStatus(final Intent request, final Intent response, final String deviceId) {

        if (deviceId == null) {
            createEmptyDeviceId(response);
            return true;
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
            return true;
        } else {
            ((HostDeviceService) getContext()).getPlayStatus(response);
            return false;
        }

    }

    @Override
    protected boolean onPutMedia(final Intent request, final Intent response, final String deviceId,
            final String mediaId) {

        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (TextUtils.isEmpty(mediaId)) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else {
            if (checkInteger(mediaId)) {
                ((HostDeviceService) getContext()).putMediaId(response, mediaId);
            } else {
               FileManager mFileManager = new FileManager(this.getContext());
               
                long newMediaId = mediaIdFromPath(this.getContext(), mFileManager.getBasePath() + mediaId);
                ((HostDeviceService) getContext()).putMediaId(response, "" + newMediaId);
            }
                
            return false;

        }

        return true;
    }

    @Override
    protected boolean onGetMedia(final Intent request, final Intent response, final String deviceId,
            final String mediaId) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
            return false;
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
            return false;
        } else if (TextUtils.isEmpty(mediaId)) {
            MessageUtils.setInvalidRequestParameterError(response);
            return true;
        } else {
        
            ((HostDeviceService) getContext()).getMedia(response);
            return false;
        }
    }

    @Override
    protected boolean onGetMediaList(Intent request, Intent response, final String deviceId, final String query,
            final String mimeType, final String[] orders, final Integer offset, final Integer limit) {

        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
            Log.i(TAG, "onGetMediaList");
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

            // 音楽用のテーブルの項目.
            String[] mMusicParam = null;
            String[] mVideoParam = null;

            // URI
            Uri mMusicUriType = null;
            Uri mVideoUriType = null;

            // 検索用 Filterを作成.
            String mVideoFilter = "";
            String mMusicFilter = "";

            // Orderの処理
            String mOrderBy = "";

            if (mimeType != null) {
                mVideoFilter = "" + MediaStore.Video.Media.MIME_TYPE + "='" + mimeType + "'";
                mMusicFilter = "" + MediaStore.Audio.Media.MIME_TYPE + "='" + mimeType + "'";
            }
            if (query != null) {
                if (!mVideoFilter.equals("")) {
                    mVideoFilter += " AND ";
                }
                mVideoFilter += MediaStore.Video.Media.TITLE + " LIKE '%" + query + "%'";

                if (!mMusicFilter.equals("")) {
                    mMusicFilter += " AND ";
                }
                mMusicFilter += "(" + MediaStore.Audio.Media.TITLE + " LIKE '%" + query + "%'";
                mMusicFilter += " OR " + MediaStore.Audio.Media.COMPOSER + " LIKE '%" + query + "%')";
            }
            Log.i(TAG, "orders" + orders);
            if (orders != null) {
                mOrderBy = orders[0] + " " + orders[1];
            } else {
                mOrderBy = "title asc";
            }

            // 音楽用のテーブルキー設定.
            mMusicParam = new String[] { MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.COMPOSER, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media._ID, MediaStore.Audio.Media.MIME_TYPE, MediaStore.Audio.Media.DATE_ADDED };
            mMusicUriType = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

            // 動画用のテーブルキー設定.
            mVideoParam = new String[] { MediaStore.Video.Media.ALBUM, MediaStore.Video.Media.ARTIST,
                    MediaStore.Video.Media.LANGUAGE, MediaStore.Video.Media.TITLE, MediaStore.Video.Media.DURATION,
                    MediaStore.Video.Media._ID, MediaStore.Video.Media.MIME_TYPE, MediaStore.Video.Media.DATE_ADDED };

            mVideoUriType = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

            ContentResolver mContentResolver = this.getContext().getApplicationContext().getContentResolver();

            Cursor cursorMusic = mContentResolver.query(mMusicUriType, mMusicParam, mMusicFilter, null, mOrderBy);
            cursorMusic.moveToFirst();

            List<Bundle> list = new ArrayList<Bundle>();

            if (cursorMusic.getCount() > 0) {
                do {

                    String mId = cursorMusic.getString(cursorMusic.getColumnIndex(MediaStore.Audio.Media._ID));
                    String mType = cursorMusic.getString(cursorMusic.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE));
                    String mTitle = cursorMusic.getString(cursorMusic.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    int mDuration = cursorMusic.getInt(cursorMusic.getColumnIndex(MediaStore.Audio.Media.DURATION));
                    String mArtist = cursorMusic.getString(cursorMusic.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    String mComp = cursorMusic.getString(cursorMusic.getColumnIndex(MediaStore.Audio.Media.COMPOSER));
                    // 音楽のデータ作成
                    Bundle medium = new Bundle();

                    setType(medium, "Music");
                    setMediaId(medium, mId);
                    setMIMEType(medium, mType);
                    setTitle(medium, mTitle);
                    setDuration(medium, mDuration);

                    // Creatorを作成
                    Bundle creator = new Bundle();
                    setCreator(creator, mArtist);
                    setRole(creator, mComp);

                    setCreators(medium, new Bundle[] {creator });

                    if (limit == null || (limit != null && limitCounter > counter)) {
                        if (offset == null || (offset != null && counter >= offset)) {
                            list.add(medium);
                        }
                    }
                    counter++;
                } while (cursorMusic.moveToNext());
            }

            Cursor cursorVideo = mContentResolver.query(mVideoUriType, mVideoParam, mVideoFilter, null, mOrderBy);
            cursorVideo.moveToFirst();
            if (cursorVideo.getCount() > 0) {
                do {

                    // 映像のリストデータ作成
                    Bundle medium = new Bundle();

                    String mLang = cursorVideo.getString(cursorVideo.getColumnIndex(MediaStore.Video.Media.LANGUAGE));
                    String mId = cursorVideo.getString(cursorVideo.getColumnIndex(MediaStore.Video.Media._ID));
                    String mType = cursorVideo.getString(cursorVideo.getColumnIndex(MediaStore.Video.Media.MIME_TYPE));
                    String mTitle = cursorVideo.getString(cursorVideo.getColumnIndex(MediaStore.Video.Media.TITLE));
                    int mDuration = cursorVideo.getInt(cursorVideo.getColumnIndex(MediaStore.Video.Media.DURATION));
                    String mArtist = cursorVideo.getString(cursorVideo.getColumnIndex(MediaStore.Video.Media.ARTIST));
                    setType(medium, "Video");
                    setLanguage(medium, mLang);
                    setMediaId(medium, mId);
                    setMIMEType(medium, mType);
                    setTitle(medium, mTitle);
                    setDuration(medium, mDuration);

                    // Creatorを作成
                    Bundle creatorVideo = new Bundle();
                    setCreator(creatorVideo, mArtist);

                    setCreators(medium, new Bundle[] {creatorVideo });

                    if (limit == null || (limit != null && limitCounter > counter)) {
                        if (offset == null || (offset != null && counter >= offset)) {
                            list.add(medium);
                        }
                    }
                    counter++;
                } while (cursorVideo.moveToNext());
            }

            setCount(response, cursorMusic.getCount() + cursorVideo.getCount());
            setMedia(response, list.toArray(new Bundle[list.size()]));
            setResult(response, DConnectMessage.RESULT_OK);
        }

        return true;
    }

    @Override
    protected boolean onPutVolume(final Intent request, final Intent response, final String deviceId,
            final Double volume) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (volume == null) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else if (0.0 > volume || volume > 1.0) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else {
            AudioManager manager = (AudioManager) this.getContext().getSystemService(Context.AUDIO_SERVICE);

            double maxVolume = manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            manager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) (maxVolume * volume), 1);
            setResult(response, DConnectMessage.RESULT_OK);

            ((HostDeviceService) getContext()).sendOnStatusChangeEvent("volume");
        }
        return true;
    }

    @Override
    protected boolean onGetVolume(final Intent request, final Intent response, final String deviceId) {

        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
            AudioManager manager = (AudioManager) this.getContext().getSystemService(Context.AUDIO_SERVICE);

            double maxVolume = 1;
            double mVolume = 0;

            mVolume = manager.getStreamVolume(AudioManager.STREAM_MUSIC);
            maxVolume = manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            setVolume(response, mVolume / maxVolume);

            setResult(response, DConnectMessage.RESULT_OK);
        }
        return true;
    }

    @Override
    protected boolean onPutSeek(final Intent request, final Intent response, final String deviceId, final Integer pos) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (pos == null) {
            MessageUtils.setInvalidRequestParameterError(response);
            return true;
        } else if (0 > pos) {
            // MEMO 本テストプラグインでは pos の最大値チェックは行わないが、実際には行うべき.
            MessageUtils.setInvalidRequestParameterError(response);
        }
        ((HostDeviceService) getContext()).setMediaPos(response, pos);
        return false;
    }

    @Override
    protected boolean onGetSeek(final Intent request, final Intent response, final String deviceId) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
            int pos = ((HostDeviceService) getContext()).getMediaPos();
            setPos(response, pos);
            setResult(response, DConnectMessage.RESULT_OK);
        }
        return true;
    }

    @Override
    protected boolean onPutMute(final Intent request, final Intent response, final String deviceId) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
            AudioManager manager = (AudioManager) this.getContext().getSystemService(Context.AUDIO_SERVICE);
            manager.setStreamMute(AudioManager.STREAM_MUSIC, true);

            setResult(response, DConnectMessage.RESULT_OK);
        }
        return true;
    }

    @Override
    protected boolean onDeleteMute(final Intent request, final Intent response, final String deviceId) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
            AudioManager manager = (AudioManager) this.getContext().getSystemService(Context.AUDIO_SERVICE);
            manager.setStreamMute(AudioManager.STREAM_MUSIC, false);

            setResult(response, DConnectMessage.RESULT_OK);
        }
        return true;
    }

    @Override
    protected boolean onGetMute(final Intent request, final Intent response, final String deviceId) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
            setMute(response, true);
        }
        return true;
    }

    @Override
    protected boolean onPutOnStatusChange(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
            return true;
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
            return true;
        } else if (sessionKey == null) {
            MessageUtils.setInvalidRequestParameterError(response);

            return true;
        } else {
            // イベントの登録
            EventError error = EventManager.INSTANCE.addEvent(request);

            if (error == EventError.NONE) {
                ((HostDeviceService) getContext()).registerOnStatusChange(response, deviceId);
                return false;
            } else {
                MessageUtils.setError(response, ERROR_VALUE_IS_NULL, "Can not register event.");
                return true;
            }
        }
    }

    @Override
    protected boolean onDeleteOnStatusChange(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
            return true;
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
            return true;
        } else if (sessionKey == null) {
            MessageUtils.setInvalidRequestParameterError(response);
            return true;
        } else {
            // イベントの解除
            EventError error = EventManager.INSTANCE.removeEvent(request);
            if (error == EventError.NONE) {

                ((HostDeviceService) getContext()).unregisterOnStatusChange(response);
                return false;

            } else {
                MessageUtils.setError(response, ERROR_VALUE_IS_NULL, "Can not unregister event.");
                return true;

            }
        }

    }

    /**
     * ファイル名からMIMEタイプ取得.
     * 
     * @param path パス
     * @return MIME-TYPE
     */
    public String getMIMEType(final String path) {
        File tmpDir;
        Log.i(TAG, path);
        // 拡張子を取得
        String ext = MimeTypeMap.getFileExtensionFromUrl(path);
        // 小文字に変換
        ext = ext.toLowerCase();
        // MIME Typeを返す
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
    }

    /**
     * 数値かどうかをチェックする.
     * 
     * @param value チェックしたいID
     * @return 数値の場合はtrue、そうでない場合はfalse
     */
    private boolean checkInteger(final String value) {
        try {
            int intValue = Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * デバイスIDをチェックする.
     * 
     * @param deviceId デバイスID
     * @return <code>deviceId</code>がテスト用デバイスIDに等しい場合はtrue、そうでない場合はfalse
     */
    private boolean checkDeviceId(final String deviceId) {
        return HostNetworkServiceDiscoveryProfile.DEVICE_ID.equals(deviceId);
    }

    /**
     * デバイスIDが空の場合のエラーを作成する.
     * 
     * @param response レスポンスを格納するIntent
     */
    private void createEmptyDeviceId(final Intent response) {
        setResult(response, DConnectMessage.RESULT_ERROR);
    }

    /**
     * デバイスが発見できなかった場合のエラーを作成する.
     * 
     * @param response レスポンスを格納するIntent
     */
    private void createNotFoundDevice(final Intent response) {
        setResult(response, DConnectMessage.RESULT_ERROR);
    }
    
    /**
     * ファイルパスからメディアIDを取得する.
     * @param context コンテキスト
     * @param path パス
     * @return MediaID
     */
    public static long mediaIdFromPath(final Context context, final String path) {
        long id = 0;
        String[] mParam = {BaseColumns._ID };
        String[] mArgs = new String[] {path};
        
        // Audio
        Uri mAudioUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String mFilter = MediaStore.Audio.AudioColumns.DATA + " LIKE ?";
        
        // Search Contents Provider
        ContentResolver mAudioContentsProvider = context.getContentResolver();
        try {
            Cursor mAudioCursor = mAudioContentsProvider.query(mAudioUri, mParam, mFilter, mArgs, null);
            mAudioCursor.moveToFirst();
            int mIdField = mAudioCursor.getColumnIndex(mParam[0]);
            id = mAudioCursor.getLong(mIdField);
            mAudioCursor.close();

        } catch (Exception e) {}
       
        // Search video
        if (id == 0) {
            
            Uri mViodeUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            mFilter = MediaStore.Video.VideoColumns.DATA + " LIKE ?";
            
            // Search Contents Provider
            ContentResolver mVideoContentsProvider = context.getContentResolver();
            try {
                Cursor mVideoCursor = mVideoContentsProvider.query(mViodeUri, mParam, mFilter, mArgs, null);
               
                mVideoCursor.moveToFirst();
                int mIdField = mVideoCursor.getColumnIndex(mParam[0]);
                id = mVideoCursor.getLong(mIdField);
                mVideoCursor.close();
               

            } catch (Exception e) {
               
            }
            
        }
        
        return id;
    }
}
