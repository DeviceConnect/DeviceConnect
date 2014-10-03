package com.nttdocomo.android.dconnect.deviceplugin.util;

import java.io.IOException;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.MediaStatus;
import com.google.android.gms.cast.RemoteMediaPlayer;
import com.google.android.gms.cast.RemoteMediaPlayer.MediaChannelResult;
import com.google.android.gms.common.api.ResultCallback;
import android.content.Intent;
import android.media.MediaExtractor;
import android.media.MediaFormat;

/**
 * Chromecast MediaPlayer クラス
 * 
 * <p>
 * MediaPlayer機能を提供する
 * </p>
 * 
 */
public class ChromeCastMediaPlayer implements ChromeCastApplication.Callbacks {

	ChromeCastApplication application;
	RemoteMediaPlayer mRemoteMediaPlayer;
	
	/**
     * コールバックのインターフェース
     * 
     * @param	なし
     * @return	なし
     */
	public interface Callbacks {
		/**
	     * 再生状態を通知する
	     * 
	     * @param	response
	     * @return	なし
	     */
		public void onChromeCastMediaPlayerStatusUpdate(MediaStatus status);
		/**
	     * 再生処理の結果を通知する
	     * 
	     * @param	response
	     * @param	result
	     * @param	message
	     * @return	なし
	     */
		public void onChromeCastMediaPlayerResult(Intent response, MediaChannelResult result, String message);
	}
	private Callbacks callbacks;
	
	/**
     * コールバックを登録する
     * 
     * @param	callbacks コールバック
     * @return	なし
     */
	public void setCallbacks(Callbacks callbacks) {
		this.callbacks = callbacks;
	}

	/**
     * コンストラクタ
     * 
     * @param	application	ChromeCastApplication
     * @return	なし
     */
	public ChromeCastMediaPlayer(ChromeCastApplication application) {
		this.application = application;
		this.application.addCallbacks(this);
	}
	
	/**
     * デバイスが有効か否かを返す
     * 
     * @param	なし
     * @return	デバイスが有効か否か（有効: true, 無効: false）
     */
	public boolean isDeviceEnable() {
		return (application.getGoogleApiClient() != null);
	}
	
	@Override
	public void onAttach() {
		mRemoteMediaPlayer = new RemoteMediaPlayer();
		mRemoteMediaPlayer
				.setOnStatusUpdatedListener(new RemoteMediaPlayer.OnStatusUpdatedListener() {
					@Override
					public void onStatusUpdated() {

						if (mRemoteMediaPlayer.getMediaStatus() == null)
							return;

						callbacks.onChromeCastMediaPlayerStatusUpdate(mRemoteMediaPlayer.getMediaStatus());
					}
				});

		mRemoteMediaPlayer
				.setOnMetadataUpdatedListener(new RemoteMediaPlayer.OnMetadataUpdatedListener() {
					@Override
					public void onMetadataUpdated() {
						MediaInfo mediaInfo = mRemoteMediaPlayer.getMediaInfo();

						if (mediaInfo == null)
							return;

						MediaMetadata metadata = mediaInfo.getMetadata();
					}
				});
		
		try {
			Cast.CastApi.setMessageReceivedCallbacks(application.getGoogleApiClient(),
					mRemoteMediaPlayer.getNamespace(), mRemoteMediaPlayer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDetach() {
		if (mRemoteMediaPlayer != null) {
			try {
				Cast.CastApi.removeMessageReceivedCallbacks(application.getGoogleApiClient(),
						mRemoteMediaPlayer.getNamespace());
			} catch (IOException e) {
				e.printStackTrace();
			}
			mRemoteMediaPlayer = null;
		}
	}

	/**
     * メディアをロードする
     * 
     * @param	response	レスポンス
     * @param	url			メディアのURL
     * @param	title		メディアのタイトル
     * @return	なし
     */
	public void load(final Intent response, String url, String title) {
		boolean isVideo = false;
		MediaInfo mediaInfo;
		
		String mimeTypeVideo = null;		
		try{
			MediaExtractor extractor = new MediaExtractor();
			int numTracks = extractor.getTrackCount();
			for (int i = 0; i < numTracks; ++i) {
				   MediaFormat format = extractor.getTrackFormat(i);
				   String mime = format.getString(MediaFormat.KEY_MIME);
				   String [] split = mime.split("/");
				   if(split.length >= 2){
					   if(split[0].equals("video")){
						   mimeTypeVideo = mime;
					   }
				   }
			}
			extractor.release();
			isVideo = true;
		}catch(Exception e){
			e.printStackTrace();
		}

		if (isVideo) {
			MediaMetadata mediaMetadata = new MediaMetadata(
					MediaMetadata.MEDIA_TYPE_MOVIE);
			mediaMetadata.putString(MediaMetadata.KEY_TITLE, title);

			if(mimeTypeVideo == null)
				mimeTypeVideo = "video/mp4";
			mediaInfo = new MediaInfo.Builder(url).setContentType(mimeTypeVideo)
					.setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
					.setMetadata(mediaMetadata).build();
		} else {
			MediaMetadata mediaMetadata = new MediaMetadata(
					MediaMetadata.MEDIA_TYPE_PHOTO);
			mediaMetadata.putString(MediaMetadata.KEY_TITLE, title);

			mediaInfo = new MediaInfo.Builder(url).setContentType("image/jpeg")
					.setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
					.setMetadata(mediaMetadata).build();
		}

		try {
			if(mRemoteMediaPlayer.getMediaStatus() != null && 
					mRemoteMediaPlayer.getMediaStatus().getPlayerState() != MediaStatus.PLAYER_STATE_IDLE){
				stop(response);
			}
			mRemoteMediaPlayer
					.load(application.getGoogleApiClient(), mediaInfo, false)
					.setResultCallback(
							new ResultCallback<RemoteMediaPlayer.MediaChannelResult>() {
								@Override
								public void onResult(MediaChannelResult result) {
									callbacks.onChromeCastMediaPlayerResult(response, result, "load");
								}
							});

		} catch (Exception e) {
			callbacks.onChromeCastMediaPlayerResult(response, null, e.getMessage());
			e.printStackTrace();
		}
	}

	/**
     * メディアをプレイする
     * 
     * @param	response	レスポンス
     * @return	なし
     */
	public void play(final Intent response) {
		try{
			mRemoteMediaPlayer.load(application.getGoogleApiClient(),
				mRemoteMediaPlayer.getMediaInfo(), true).setResultCallback(
				new ResultCallback<RemoteMediaPlayer.MediaChannelResult>() {
					@Override
					public void onResult(MediaChannelResult result) {
						callbacks.onChromeCastMediaPlayerResult(response, result,
								null);
					}
				});
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
     * メディアをレジュームする
     * 
     * @param	response	レスポンス
     * @return	なし
     */
	public void resume(final Intent response) {
		try {
			mRemoteMediaPlayer.play(application.getGoogleApiClient()).setResultCallback(
					new ResultCallback<RemoteMediaPlayer.MediaChannelResult>() {
						@Override
						public void onResult(MediaChannelResult result) {
							callbacks.onChromeCastMediaPlayerResult(response, result,
									null);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
     * メディアを停止する
     * 
     * @param	response	レスポンス
     * @return	なし
     */
	public void stop(final Intent response) {
		try {
			mRemoteMediaPlayer.stop(application.getGoogleApiClient()).setResultCallback(
					new ResultCallback<RemoteMediaPlayer.MediaChannelResult>() {
						@Override
						public void onResult(MediaChannelResult result) {
							callbacks.onChromeCastMediaPlayerResult(response, result,
									null);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
     * メディアを一時停止する
     * 
     * @param	response	レスポンス
     * @return	なし
     */
	public void pause(final Intent response) {
		try {
			mRemoteMediaPlayer.pause(application.getGoogleApiClient()).setResultCallback(
					new ResultCallback<RemoteMediaPlayer.MediaChannelResult>() {
						@Override
						public void onResult(MediaChannelResult result) {
							callbacks.onChromeCastMediaPlayerResult(response, result,
									null);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
     * メディアをミュートする
     * 
     * @param	response	レスポンス
     * @param	mute		ミュートするか否か (true: ミュートON, false: ミュートOFF)
     * @return	なし
     */
	public void setMute(final Intent response, boolean mute) {
		try {
			mRemoteMediaPlayer
					.setStreamMute(application.getGoogleApiClient(), mute)
					.setResultCallback(
							new ResultCallback<RemoteMediaPlayer.MediaChannelResult>() {
								@Override
								public void onResult(MediaChannelResult result) {
									callbacks.onChromeCastMediaPlayerResult(response, result,
											null);
								}
							});
		} catch (Exception e) {
			callbacks.onChromeCastMediaPlayerResult(response, null, e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
     * メディアのミュートの状態を取得する
     * 
     * @param	response	レスポンス
     * @return	int			ミュート状態 (1:ミュートON, 0: ミュートOFF, -1: エラー)
     */
	public int getMute(final Intent response) {
		try {
			if(mRemoteMediaPlayer.getMediaStatus().isMute())
				return 1;
			else
				return 0;
		} catch (Exception e) {
			callbacks.onChromeCastMediaPlayerResult(response, null, e.getMessage());
			e.printStackTrace();
		}
		return -1;
	}

	/**
     * メディアのボリュームを設定する
     * 
     * @param	response	レスポンス
     * @param	volume		ボリューム (0.0 <= volume <= 1.0)
     * @return	なし
     */
	public void setVolume(final Intent response, double volume) {
		try {
			mRemoteMediaPlayer
					.setStreamVolume(application.getGoogleApiClient(), volume)
					.setResultCallback(
							new ResultCallback<RemoteMediaPlayer.MediaChannelResult>() {
								@Override
								public void onResult(MediaChannelResult result) {
									callbacks.onChromeCastMediaPlayerResult(response, result,
											null);
								}
							});
		} catch (Exception e) {
			callbacks.onChromeCastMediaPlayerResult(response, null, e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
     * メディアのボリュームを取得する
     * 
     * @param	response	レスポンス
     * @return	volume		ボリューム (0 <= volume <= 1.0, -1: エラー)
     */
	public double getVolume(final Intent response){
		try {
			return mRemoteMediaPlayer.getMediaStatus().getStreamVolume();
		} catch (Exception e) {
			callbacks.onChromeCastMediaPlayerResult(response, null, e.getMessage());
			e.printStackTrace();
		}
		return -1;
	}
	
	/**
     * メディアのシークを設定する
     * 
     * @param	response	レスポンス
     * @param	pos			ポジション
     * @return	なし
     */
	public void setSeek(final Intent response, long pos) {
		try {
			mRemoteMediaPlayer
					.seek(application.getGoogleApiClient(), pos)
					.setResultCallback(
							new ResultCallback<RemoteMediaPlayer.MediaChannelResult>() {
								@Override
								public void onResult(MediaChannelResult result) {
									callbacks.onChromeCastMediaPlayerResult(response, result,
											null);
								}
							});
		} catch (Exception e) {
			callbacks.onChromeCastMediaPlayerResult(response, null, e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
     * メディアのシークを取得する
     * 
     * @param	response	レスポンス
     * @return	pos			ポジション (-1: エラー)
     */
	public long getSeek(final Intent response) {
		try {
			return mRemoteMediaPlayer.getApproximateStreamPosition();
		} catch (Exception e) {
			callbacks.onChromeCastMediaPlayerResult(response, null, e.getMessage());
			e.printStackTrace();
		}
		return -1;
	}
	
	/**
     * メディアの状態を取得する
     * 
     * @param	なし
     * @return	status	メディアの状態
     */
	public MediaStatus getMediaStatus() {
		MediaStatus status = null;
		try {
			status = mRemoteMediaPlayer.getMediaStatus();
		}catch(Exception e){
			e.printStackTrace();
		}
		return status;
	}
}
