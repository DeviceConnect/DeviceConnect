package com.nttdocomo.android.dconnect.deviceplugin.util;

import java.io.IOException;
import android.content.Intent;
import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.Cast.MessageReceivedCallback;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

/**
 * Chromecast Message クラス
 * 
 * <p>
 * メッセージ機能を提供する
 * </p>
 * 
 */
public class ChromeCastMessage implements ChromeCastApplication.Callbacks {
	
	private class MessageChannel implements MessageReceivedCallback {

		/**
	     * メッセージの宛先(名前空間)を取得する
	     * 
	     * @param	なし
	     * @return	namespace	名前空間
	     */
		public String getNamespace() {
			return "urn:x-cast:com.name.space.chromecast.test.receiver";
		}

		@Override
		public void onMessageReceived(CastDevice castDevice, String namespace,
				String message) {
		}
	}
	
	/**
     * コールバックのインターフェース
     * 
     * @param	なし
     * @return	なし
     */
	public interface Callbacks {
		/**
	     * メッセージ処理の結果を通知する
	     * 
	     * @param	response
	     * @return	なし
	     */
		public void onChromeCastMessageResult(Intent response, Status result, String message);
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
	
	MessageChannel mMessageChannel;
	ChromeCastApplication application;
	
	/**
     * コンストラクタ
     * 
     * @param	application	ChromeCastApplication
     * @return	なし
     */
	public ChromeCastMessage(ChromeCastApplication application){
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
		mMessageChannel = new MessageChannel();
		try {
			Cast.CastApi.setMessageReceivedCallbacks(application.getGoogleApiClient(),
					mMessageChannel.getNamespace(), mMessageChannel);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDetach() {
		if (mMessageChannel != null) {
			try {
				Cast.CastApi.removeMessageReceivedCallbacks(application.getGoogleApiClient(),
						mMessageChannel.getNamespace());
			} catch (IOException e) {
				e.printStackTrace();
			}
			mMessageChannel = null;
		}
	}
	
	/**
     * メッセージを送信する
     * 
     * @param	response	レスポンス
     * @param	message		メッセージ
     * @return	なし
     */
	public void sendMessage(final Intent response, String message) {
		if (application.getGoogleApiClient() != null && mMessageChannel != null) {
			try {
				Cast.CastApi.sendMessage(application.getGoogleApiClient(),
						mMessageChannel.getNamespace(), message)
						.setResultCallback(new ResultCallback<Status>() {
							@Override
							public void onResult(Status result) {
								callbacks.onChromeCastMessageResult(response, result, null);
							}
						});
			} catch (Exception e) {
				callbacks.onChromeCastMessageResult(response, null, e.getMessage());
				e.printStackTrace();
			}
		}
	}

}
