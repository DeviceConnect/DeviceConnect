/*
 ChromeCastMediaPlayerProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.chromecast.profile;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.http.conn.util.InetAddressUtils;
import org.deviceconnect.android.deviceplugin.chromecast.ChromeCastService;
import org.deviceconnect.android.deviceplugin.chromecast.core.ChromeCastHttpServer;
import org.deviceconnect.android.deviceplugin.chromecast.core.ChromeCastMediaPlayer;
import org.deviceconnect.android.event.EventError;
import org.deviceconnect.android.event.EventManager;
import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.android.profile.MediaPlayerProfile;
import org.deviceconnect.message.DConnectMessage;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.MediaStatus;

/**
 * MediaPlayer プロファイル (Chromecast)
 * <p>
 * Chromecastのメディア操作を提供する
 * </p>
 * @author NTT DOCOMO, INC.
 */
public class ChromeCastMediaPlayerProfile extends MediaPlayerProfile {

    private static final int ERROR_VALUE_IS_NULL = 100;
    private String mMediaId = null;

    private static final String MESSAGE_BUFFERING                   = "buffering";
    private static final String MESSAGE_IDLE                        = "stop";
    private static final String MESSAGE_PAUSED                      = "pause";
    private static final String MESSAGE_PALYING                     = "play";
    private static final String MESSAGE_UNKNOWN                     = "unknown";
    private static final String ERROR_MESSAGE_DEVICE_NOT_ENABLE     = "Device is not enable";
    private static final String ERROR_MESSAGE_MEDIA_NOT_SELECTED    = "Media is not selected";
    private static final String ERROR_MESSAGE_PLAYSTATE_IS_NOT      = "Playstate is not";
    private static final String ERROR_MESSAGE_MEDIA_PLAY            = ERROR_MESSAGE_PLAYSTATE_IS_NOT + " " + MESSAGE_IDLE + " or " + MESSAGE_PAUSED;
    private static final String ERROR_MESSAGE_MEDIA_RESUME          = ERROR_MESSAGE_PLAYSTATE_IS_NOT + " " + MESSAGE_PAUSED;
    private static final String ERROR_MESSAGE_MEDIA_STOP            = ERROR_MESSAGE_PLAYSTATE_IS_NOT + " " + MESSAGE_PALYING + " or " + MESSAGE_PAUSED;
    private static final String ERROR_MESSAGE_MEDIA_PAUSE           = ERROR_MESSAGE_PLAYSTATE_IS_NOT + " " + MESSAGE_PALYING;
    private static final String ERROR_MESSAGE_MEDIA_MUTE            = ERROR_MESSAGE_PLAYSTATE_IS_NOT + " " + MESSAGE_PALYING + " or " + MESSAGE_PAUSED;
    private static final String ERROR_MESSAGE_MEDIA_VOLUME          = ERROR_MESSAGE_MEDIA_MUTE;
    private static final String ERROR_MESSAGE_MEDIA_SEEK            = ERROR_MESSAGE_MEDIA_MUTE;

    /**
     * 再生状態を文字列に変換する
     * 
     * @param   playState   再生状態
     * @return  再生状態の文字列を返す
     */
    public String getPlayStatus(int playState) {
        switch (playState) {
        case MediaStatus.PLAYER_STATE_BUFFERING:
            return MESSAGE_BUFFERING;
        case MediaStatus.PLAYER_STATE_IDLE:
            return MESSAGE_IDLE;
        case MediaStatus.PLAYER_STATE_PAUSED:
            return MESSAGE_PAUSED;
        case MediaStatus.PLAYER_STATE_PLAYING:
            return MESSAGE_PALYING;
        case MediaStatus.PLAYER_STATE_UNKNOWN:
        default:
            return MESSAGE_UNKNOWN;
        }
    }

    /**
     * サービスからChromeCastMediaPlayerを取得する
     * @param   なし
     * @return  ChromeCastMediaPlayer
     */
    private ChromeCastMediaPlayer getChromeCastApplication() {
        return ((ChromeCastService) getContext()).getChromeCastMediaPlayer();
    }

    /**
     * デバイスが有効か否かを返す<br/>
     * デバイスが無効の場合、レスポンスにエラーを設定する
     * 
     * @param   response    レスポンス
     * @param   app         ChromeCastMediaPlayer
     * @return  デバイスが有効か否か（有効: true, 無効: false）
     */
    private boolean isDeviceEnable(final Intent response, ChromeCastMediaPlayer app){
        if(!app.isDeviceEnable()){
            MessageUtils.setIllegalDeviceStateError(response, ERROR_MESSAGE_DEVICE_NOT_ENABLE);
            setResult(response, DConnectMessage.RESULT_ERROR);
            return false;
        }
        return true;
    }
	
    /**
     * メディアの状態を取得する
     * @param   response    レスポンス
     * @param   app         ChromeCastMediaPlayer
     * @return  デバイスが有効か否か（有効: true, 無効: false）
     */
    private MediaStatus getMediaStatus(final Intent response, ChromeCastMediaPlayer app){
        MediaStatus status = app.getMediaStatus();
        if (status == null) {
            MessageUtils.setIllegalDeviceStateError(response, ERROR_MESSAGE_MEDIA_NOT_SELECTED);
            setResult(response, DConnectMessage.RESULT_ERROR);
        }
        return status;
    }

    @Override
    protected boolean onPutPlay(final Intent request, final Intent response,
            final String deviceId) {
        ChromeCastMediaPlayer app = getChromeCastApplication();
        if(!isDeviceEnable(response, app))	return true;
        MediaStatus status = getMediaStatus(response, app);
        if (status == null) return true;

        if (status.getPlayerState() == MediaStatus.PLAYER_STATE_IDLE){
            app.play(response);
            return false;
        }else if(status.getPlayerState() == MediaStatus.PLAYER_STATE_PAUSED) {
            app.resume(response);
            return false;
        } else {
            MessageUtils.setIllegalDeviceStateError(response, ERROR_MESSAGE_MEDIA_PLAY);
            setResult(response, DConnectMessage.RESULT_ERROR);
            return true;
        }
    }

    @Override
    protected boolean onPutResume(final Intent request, final Intent response,
            final String deviceId) {
        ChromeCastMediaPlayer app = getChromeCastApplication();
        if(!isDeviceEnable(response, app))	return true;
        MediaStatus status = getMediaStatus(response, app);
        if (status == null) return true;

        if (status.getPlayerState() == MediaStatus.PLAYER_STATE_PAUSED) {
            app.resume(response);
            return false;
        } else {
            MessageUtils.setIllegalDeviceStateError(response, ERROR_MESSAGE_MEDIA_RESUME);
            setResult(response, DConnectMessage.RESULT_ERROR);
            return true;
        }
    }

    @Override
    protected boolean onPutStop(final Intent request, final Intent response,
            final String deviceId) {
        ChromeCastMediaPlayer app = getChromeCastApplication();
        if(!isDeviceEnable(response, app))	return true;
        MediaStatus status = getMediaStatus(response, app);
        if (status == null) return true;

        if (status.getPlayerState() == MediaStatus.PLAYER_STATE_PLAYING || 
                status.getPlayerState() == MediaStatus.PLAYER_STATE_PAUSED) {
            app.stop(response);
            return false;
        } else {
            MessageUtils.setIllegalDeviceStateError(response, ERROR_MESSAGE_MEDIA_STOP);
            setResult(response, DConnectMessage.RESULT_ERROR);
            return true;
        }
    }

    @Override
    protected boolean onPutPause(final Intent request, final Intent response,
            final String deviceId) {
        ChromeCastMediaPlayer app = getChromeCastApplication();
        if(!isDeviceEnable(response, app))	return true;
        MediaStatus status = getMediaStatus(response, app);
        if (status == null) return true;

        if (status.getPlayerState() == MediaStatus.PLAYER_STATE_PLAYING) {
            app.pause(response);
            return false;
        } else {
            MessageUtils.setIllegalDeviceStateError(response, ERROR_MESSAGE_MEDIA_PAUSE);
            setResult(response, DConnectMessage.RESULT_ERROR);
            return true;
        }
    }

    /**
     * メディアをミュートする<br/>
     * エラーの場合、レスポンスにエラーを設定する
     * 
     * @param   request     リクエスト
     * @param   response    レスポンス
     * @param   deviceId    デバイスID
     * @param   mute        ミュートするか否か（true: ミュートON, false: ミュートOFF）
     * @return  result      結果を返す（true: 成功, false: 失敗）
     */
    private boolean setMute(final Intent request, final Intent response,
            final String deviceId, boolean mute) {
        ChromeCastMediaPlayer app = getChromeCastApplication();
        if (!isDeviceEnable(response, app))	return true;
        MediaStatus status = getMediaStatus(response, app);
        if (status == null) return true;
        if (status.getPlayerState() == MediaStatus.PLAYER_STATE_PLAYING || 
                status.getPlayerState() == MediaStatus.PLAYER_STATE_PAUSED) {
            app.setMute(response, mute);
            return false;
        }else{
            MessageUtils.setIllegalDeviceStateError(response, ERROR_MESSAGE_MEDIA_MUTE);
            setResult(response, DConnectMessage.RESULT_ERROR);
            return true;
        }
    }
    @Override
    protected boolean onPutMute(final Intent request, final Intent response,
            final String deviceId) {
        return setMute(request, response, deviceId, true);
    }

    @Override
    protected boolean onDeleteMute(final Intent request, final Intent response,
            final String deviceId) {
        return setMute(request, response, deviceId, false);
    }

    @Override
    protected boolean onGetMute(final Intent request, final Intent response,
            final String deviceId) {
        ChromeCastMediaPlayer app = getChromeCastApplication();
        if (!isDeviceEnable(response, app))	return true;
        if (getMediaStatus(response, app) == null) return true;

        int mute = app.getMute(response);
        if (mute == 1) {
            setMute(response, true);
            setResult(response, DConnectMessage.RESULT_OK);
            return true;
        } else if (mute == 0) {
            setMute(response, false);
            setResult(response, DConnectMessage.RESULT_OK);
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected boolean onPutVolume(final Intent request, final Intent response,
            final String deviceId, final Double volume) {
        ChromeCastMediaPlayer app = getChromeCastApplication();
        if (!isDeviceEnable(response, app))	return true;
        MediaStatus status = getMediaStatus(response, app);
        if (status == null) return true;

        if (volume == null) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else if (0.0 > volume || volume > 1.0) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else {
            if (status.getPlayerState() == MediaStatus.PLAYER_STATE_PLAYING || 
                    status.getPlayerState() == MediaStatus.PLAYER_STATE_PAUSED) {
                app.setVolume(response, volume);
                return false;
            }else{
                MessageUtils.setIllegalDeviceStateError(response, ERROR_MESSAGE_MEDIA_VOLUME);
                setResult(response, DConnectMessage.RESULT_ERROR);
                return true;
            }
        }
        return true;
    }

    @Override
    protected boolean onGetVolume(final Intent request, final Intent response,
            final String deviceId) {
        ChromeCastMediaPlayer app = getChromeCastApplication();
        if (!isDeviceEnable(response, app))	return true;
        if (getMediaStatus(response, app) == null) return true;

        double volume = app.getVolume(response);
        if (volume >= 0) {
            setVolume(response, volume);
            setResult(response, DConnectMessage.RESULT_OK);
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected boolean onPutSeek(final Intent request, final Intent response,
            final String deviceId, final Integer pos) {
        ChromeCastMediaPlayer app = getChromeCastApplication();
        if(!isDeviceEnable(response, app))	return true;

        if (pos == null) {
            MessageUtils.setInvalidRequestParameterError(response);
            return true;
        } else {
            MediaStatus status = getMediaStatus(response, app);
            if (status == null) return true;

            long posMillisecond = pos * 1000;
            if (0 > posMillisecond
                    || posMillisecond > status.getMediaInfo()
                    .getStreamDuration()) {
                MessageUtils.setInvalidRequestParameterError(response);
                return true;
            }

            if (status.getPlayerState() == MediaStatus.PLAYER_STATE_PLAYING || 
                    status.getPlayerState() == MediaStatus.PLAYER_STATE_PAUSED) {
                app.setSeek(response, posMillisecond);
                return false;
            }else{
                MessageUtils.setIllegalDeviceStateError(response, ERROR_MESSAGE_MEDIA_SEEK);
                setResult(response, DConnectMessage.RESULT_ERROR);
                return true;
            }
        }
    }

    @Override
    protected boolean onGetSeek(final Intent request, final Intent response,
            final String deviceId) {
        ChromeCastMediaPlayer app = getChromeCastApplication();
        if (!isDeviceEnable(response, app))	return true;
        if (getMediaStatus(response, app) == null) return true;
		
        long posSecond = app.getSeek(response) / 1000;
        if (posSecond >= 0) {
            setPos(response, (int) posSecond);
            setResult(response, DConnectMessage.RESULT_OK);
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected boolean onGetPlayStatus(final Intent request,
            final Intent response, final String deviceId) {
        ChromeCastMediaPlayer app = getChromeCastApplication();
        if(!isDeviceEnable(response, app))	return true;

        MediaStatus status = getMediaStatus(response, app);
        if (status == null) return true;
        String playStatus = getPlayStatus(status.getPlayerState());
        response.putExtra(MediaPlayerProfile.PARAM_STATUS, playStatus);
        setResult(response, DConnectMessage.RESULT_OK);
        return true;
    }

    @Override
    protected boolean onGetMedia(final Intent request, final Intent response,
            final String deviceId, final String mediaId) {
        ChromeCastMediaPlayer app = getChromeCastApplication();
        if(!isDeviceEnable(response, app))	return true;

        MediaStatus status = getMediaStatus(response, app);
        if (status == null) return true;
        String playStatus = getPlayStatus(status.getPlayerState());
        response.putExtra(DConnectMessage.EXTRA_RESULT,
                DConnectMessage.RESULT_OK);
        response.putExtra(MediaPlayerProfile.PARAM_MIME_TYPE, status
                .getMediaInfo().getContentType());
        response.putExtra(MediaPlayerProfile.PARAM_TITLE, status
                .getMediaInfo().getMetadata()
                .getString(MediaMetadata.KEY_TITLE));
        response.putExtra(MediaPlayerProfile.PARAM_MEDIA_ID, this.mMediaId);
        response.putExtra(MediaPlayerProfile.PARAM_DURATION, status
                .getMediaInfo().getStreamDuration() / 1000);
        response.putExtra(MediaPlayerProfile.PARAM_POS,
                status.getStreamPosition() / 1000);
        response.putExtra(MediaPlayerProfile.PARAM_STATUS, playStatus);

        setResult(response, DConnectMessage.RESULT_OK);
        return true;
    }

    /**
     * MD5の文字列を生成する
     * 
     * @param   str     文字列
     * @return  result  MD5文字列	
     */
    private String getMd5(String str) {
        String result = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(str.getBytes());
            byte[] hash = digest.digest();
            StringBuffer hex = new StringBuffer();
            for (int i = 0; i < hash.length; i++)
                hex.append(Integer.toHexString(0xFF & hash[i]));
            result = hex.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * URLをチェックする<br/>
     * 指定したURLに接続可能かどうかをチェックする
     */
    private class ThreadCheckURL extends Thread {
        private String value;
        private boolean result = false;
        private String message = null;

        public ThreadCheckURL(String url) {
            value = url;
        }

        @Override
        public void run() {
            if (value == null) {
                message = "url is null";
                return;
            }
            try {
                HttpURLConnection con = (HttpURLConnection) (new URL(value))
                        .openConnection();
                con.connect();
                con.disconnect();
                result = true;
            } catch (Exception e) {
                e.printStackTrace();
                message = e.getMessage();
            }
        }

        public boolean getValue() {
            return this.result;
        }

        public String getMessage() {
            return this.message;
        }
    }

    /**
     * 指定したURIからkeyとvalueに基づき、Cursorを取得する
     * 
     * @param   uri     URI
     * @param   key     キー
     * @param   value   バリュー
     * @return  cursor  カーソル
     */
    private Cursor getCursorFrom(Uri uri, String key, String value) {
        Cursor cursor = getContext().getApplicationContext()
                .getContentResolver()
                .query(uri, null, key + "=" + value + "", null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                if (cursor.getCount() == 1) {
                    return cursor;
                }
            }
        }
        return null;
    }

    /**
     * mediaIdからdummyUrlを生成する
     * 
     * @param   mediaId     メディアID
     * @return  dummyUrl    ダミーURL
     */
    private String getDummyUrlFromMediaId(int mediaId) {
        Uri targetUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String path = getPathFromUri(ContentUris.withAppendedId(targetUri,
                Long.valueOf(mediaId)));

        if (path == null) {
            return null;
        }

        ChromeCastHttpServer server = ((ChromeCastService) getContext())
                .getChromeCastHttpServer();
        String dir = new File(path).getParent();
        String realName = new File(path).getName();
        String dummyName = getMd5("" + System.currentTimeMillis());

        server.setFilePath(dir, realName, "/" + dummyName);
        return "http://" + getIpAddress() + ":" + server.getListeningPort()
                + "/" + dummyName;
    }

    @Override
    protected boolean onPutMedia(final Intent request, final Intent response,
            final String deviceId, final String mediaId) {
        ChromeCastMediaPlayer app = getChromeCastApplication();
        if(!isDeviceEnable(response, app))	return true;

        String url = null;
        String title = null;
        Integer mId = -1;

        if(mediaId == null){
            response.putExtra(DConnectMessage.EXTRA_VALUE, "mediaId is null");
            setResult(response, DConnectMessage.RESULT_ERROR);
            return true;
        }

        try {
            mId = Integer.parseInt(mediaId);
        } catch (Exception e) {
            url = mediaId;
        }

        if (url != null) {
            this.mMediaId = mediaId;
        } else {
            Cursor cursor = getCursorFrom(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    MediaStore.Video.Media._ID, mediaId);
            if (cursor == null){
                response.putExtra(DConnectMessage.EXTRA_VALUE, "mediaId is not exist");
                setResult(response, DConnectMessage.RESULT_ERROR);
                return true;
            }else {
                title = cursor.getString(cursor
                        .getColumnIndex(MediaStore.Video.Media.TITLE));
                url = getDummyUrlFromMediaId(mId);
            }
            this.mMediaId = mId.toString();
        }

        ThreadCheckURL thread = new ThreadCheckURL(url);
        thread.start();
        try {
            thread.join(3000);
            if (!thread.getValue()) {
                response.putExtra(DConnectMessage.EXTRA_VALUE,
                        thread.getMessage());
                setResult(response, DConnectMessage.RESULT_ERROR);
                return true;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            response.putExtra(DConnectMessage.EXTRA_VALUE, e.getMessage());
            setResult(response, DConnectMessage.RESULT_ERROR);
            return true;
        }

        if (title == null)
            title = "TITLE";

        app.load(response, url, title);

        return false;
    }

    /**
     * IPアドレスを取得する
     * 
     * @param   なし
     * @return  IPアドレス
     */
    private String getIpAddress() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = (NetworkInterface) networkInterfaces
                        .nextElement();
                Enumeration<InetAddress> ipAddrs = networkInterface
                        .getInetAddresses();
                while (ipAddrs.hasMoreElements()) {
                    InetAddress ip = (InetAddress) ipAddrs.nextElement();
                    String ipStr = ip.getHostAddress();
                    if (!ip.isLoopbackAddress()
                            && InetAddressUtils.isIPv4Address(ipStr)) {
                        return ipStr;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Uriからパスを取得する
     * 
     * @param   mUri    Uri
     * @return  パス
     */
    private String getPathFromUri(final Uri mUri) {
        try {
            Cursor c = this.getContext().getContentResolver()
                    .query(mUri, null, null, null, null);
            c.moveToFirst();
            String filename = c.getString(c
                    .getColumnIndex(MediaStore.MediaColumns.DATA));

            return filename;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * bundleにメディア情報を設定する
     * 
     * @param   MediaType   メディアタイプ
     * @param   Bundle      バンドル
     * @param   Cursor      カーソル
     * @return  なし
     */
    private void setMediaInformation(String MediaType, Bundle bundle,
            Cursor cursor) {
        if (MediaType.equals("Video")) {
            setType(bundle, MediaType);
            setLanguage(bundle, cursor.getString(cursor
                    .getColumnIndex(MediaStore.Video.Media.LANGUAGE)));
            setMediaId(bundle, cursor.getString(cursor
                    .getColumnIndex(MediaStore.Video.Media._ID)));
            setMIMEType(bundle, cursor.getString(cursor
                    .getColumnIndex(MediaStore.Video.Media.MIME_TYPE)));
            setTitle(bundle, cursor.getString(cursor
                    .getColumnIndex(MediaStore.Video.Media.TITLE)));
            setDuration(bundle, cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Video.Media.DURATION)));
            Bundle creator = new Bundle();
            setCreator(creator, cursor.getString(cursor
                    .getColumnIndex(MediaStore.Video.Media.ARTIST)));
            setCreators(bundle, new Bundle[] { creator });

        } else if (MediaType.equals("Audio")) {
            setType(bundle, "Music");
            setMediaId(bundle, cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media._ID)));
            setMIMEType(bundle, cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.MIME_TYPE)));
            setTitle(bundle, cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.TITLE)));
            setDuration(bundle, cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DURATION)));
            Bundle creator = new Bundle();
            setCreator(creator, cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.ARTIST)));
            setRole(creator, cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.COMPOSER)));
            setCreators(bundle, new Bundle[] { creator });

        }else if (MediaType.equals("Image")) {
            setType(bundle, MediaType);
            setMediaId(bundle, cursor.getString(cursor
                    .getColumnIndex(MediaStore.Images.Media._ID)));
            setMIMEType(bundle, cursor.getString(cursor
                    .getColumnIndex(MediaStore.Images.Media.MIME_TYPE)));
            setTitle(bundle, cursor.getString(cursor
                    .getColumnIndex(MediaStore.Images.Media.TITLE)));
            Bundle creator = new Bundle();
            setCreators(bundle, new Bundle[] { creator });
        } else {
            setType(bundle, MediaType);
        }
    }

    /**
     * メディア情報をリストアップする
     * 
     * @param   mediaType   メディアタイプ
     * @param   list        リスト
     * @param   filter      フィルター
     * @param   orderBy     ソートオーダー
     * @return  なし
     */
    private void listupMedia(String mediaType, List<Bundle> list, String filter,
            String orderBy) {
        Uri uriType = null;

        if(mediaType.equals("Video"))
            uriType = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        else if(mediaType.equals("Audio"))
            uriType = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        else if(mediaType.equals("Image"))
            uriType = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        ContentResolver mContentResolver = this.getContext()
                .getApplicationContext().getContentResolver();
        Cursor cursorVideo = mContentResolver.query(uriType, null, filter,
                null, orderBy);
        cursorVideo.moveToFirst();
        if (cursorVideo.getCount() > 0) {
            do {
                Bundle medium = new Bundle();
                setMediaInformation(mediaType, medium, cursorVideo);
                list.add(medium);
            } while (cursorVideo.moveToNext());
        }
    }
	
    /**
     * メディア情報をリストアップする
     * 
     * @param   mediaType   メディアタイプ
     * @param   list        リスト
     * @param   query       クエリー
     * @param   mimeType    MIMEタイプ
     * @param   orders      ソートオーダー
     * @return  なし
     */
    private void listupMedia(String mediaType, List<Bundle> list, String query,
            String mimeType, String[] orders) {
        String filter = "";
        String orderBy = "";

        if (mimeType != null) {
            String key = null;
            if(mediaType.equals("Video")){
                key = MediaStore.Video.Media.MIME_TYPE;
            }else if(mediaType.equals("Audio")){
                key = MediaStore.Audio.Media.MIME_TYPE;
            }else if(mediaType.equals("Image")){
                key = MediaStore.Images.Media.MIME_TYPE;
            }
            filter = "" + key + "='" + mimeType + "'";
        }
        if (query != null) {
            if (!filter.equals("")) {
                filter += " AND ";
            }
            if(mediaType.equals("Video")){
                filter += "(" + MediaStore.Video.Media.TITLE + " LIKE '%" + query + "%'";
            }else if(mediaType.equals("Audio")){
                filter += "(" + MediaStore.Audio.Media.TITLE + " LIKE '%" + query + "%'";
                filter += " OR " + MediaStore.Audio.Media.COMPOSER + " LIKE '%" + query + "%')";
            }else if(mediaType.equals("Image")){
                filter += "(" + MediaStore.Images.Media.TITLE + " LIKE '%" + query + "%'";
            }
        }

        if (orders != null) {
            orderBy = orders[0] + " " + orders[1];
        } else {
            orderBy = "title asc";
        }

        listupMedia(mediaType, list, filter, orderBy);
    }

    @Override
    protected boolean onGetMediaList(Intent request, Intent response,
            final String deviceId, final String query, final String mimeType,
            final String[] orders, final Integer offset, final Integer limit) {
        List<Bundle> list = new ArrayList<Bundle>();

        Bundle medium = null;
        Bundle creatorVideo = null;
        
        medium = new Bundle();
        setType(medium, "Video");
        setLanguage(medium, "Language");
        setMediaId(medium, "https://raw.githubusercontent.com/DeviceConnect/DeviceConnect/master/sphero_demo.MOV");
        setMIMEType(medium, "mov");
        setTitle(medium, "Title: Sample");
        setDuration(medium, 9999);
        creatorVideo = new Bundle();
        setCreator(creatorVideo, "Creator: Sample");
        setCreators(medium, new Bundle[] { creatorVideo });
        list.add(medium);

        listupMedia("Video", list, query, mimeType, orders);

        setCount(response, list.size());
        setMedia(response, list.toArray(new Bundle[list.size()]));
        setResult(response, DConnectMessage.RESULT_OK);

        return true;
    }

    @Override
    protected boolean onPutOnStatusChange(final Intent request,
            final Intent response, final String deviceId,
            final String sessionKey) {
        EventError error = EventManager.INSTANCE.addEvent(request);
        if (error == EventError.NONE) {
            ((ChromeCastService) getContext()).registerOnStatusChange(response,
                    deviceId, sessionKey);
            return false;
        } else {
            MessageUtils.setError(response, ERROR_VALUE_IS_NULL,
                    "Can not register event.");
            return true;
        }
    }

    @Override
    protected boolean onDeleteOnStatusChange(final Intent request,
            final Intent response, final String deviceId,
            final String sessionKey) {
        EventError error = EventManager.INSTANCE.removeEvent(request);
        if (error == EventError.NONE) {
            ((ChromeCastService) getContext())
                .unregisterOnStatusChange(response);
            return false;
        } else {
            MessageUtils.setError(response, ERROR_VALUE_IS_NULL,
                    "Can not unregister event.");
            return true;
        }
    }
}
