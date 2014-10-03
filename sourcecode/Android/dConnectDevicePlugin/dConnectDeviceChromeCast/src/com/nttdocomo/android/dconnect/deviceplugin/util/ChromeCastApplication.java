package com.nttdocomo.android.dconnect.deviceplugin.util;

import java.util.ArrayList;
import android.content.Context;
import android.os.Bundle;
import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.Cast.ApplicationConnectionResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

/**
 * Chromecast Application クラス
 * <p>
 * アプリケーションIDに対応したReceiverアプリの起動、終了
 * </p>
 * 
 */
public class ChromeCastApplication {
	
	private CastDevice mSelectedDevice;
	private GoogleApiClient mApiClient;
	private Cast.Listener mCastListener;
	private ConnectionCallbacks mConnectionCallbacks;
	private ConnectionFailedListener mConnectionFailedListener;
	private boolean mApplicationStarted;
	private boolean mWaitingForReconnect;
	private String mSessionId;
	private Context context;
	private String appId;
	private ArrayList<Callbacks> callbacks;
	
	/**
     * コールバックのインターフェース
     * 
     * @param	なし
     * @return	なし
     */
	public interface Callbacks {
		/**
	     * Chromecast Applicationにアタッチする
	     * 
	     * @param	なし
	     * @return	なし
	     */
		public void onAttach();
		
		/**
	     * Chromecast Applicationにデタッチする
	     * 
	     * @param	なし
	     * @return	なし
	     */
		public void onDetach();
	}

	/**
     * コンストラクタ
     * 
     * @param	context コンテキスト
     * @param 	appId ReceiverアプリのアプリケーションID
     * @return	なし
     */
	public ChromeCastApplication(Context context, String appId) {
		this.context = context;
		this.appId = appId;
		this.mSelectedDevice = null;
		callbacks = new ArrayList<Callbacks>();
	}
	
	/**
     * GoogleApiClientを取得する
     * 
     * @param	なし
     * @return	GoogleApiClient
     */
	public GoogleApiClient getGoogleApiClient(){
		return mApiClient;
	}
	
	/**
     * コールバックを登録する
     * 
     * @param	callbacks コールバック
     * @return	なし
     */
	public void addCallbacks(Callbacks callbacks) {
		this.callbacks.add(callbacks);
	}

	/**
     * Chromecastデバイスをセットする
     * 
     * @param	mSelectedDevice
     * @return	なし
     */
	public void SetSelectedDevice(CastDevice mSelectedDevice) {
		this.mSelectedDevice = mSelectedDevice;
	}
	
	/**
     * Leaves (disconnects from) the receiver application.
     * 
     * @param	なし
     * @return	なし
     */
	public void leaveReceiver(){
		Cast.CastApi.leaveApplication(mApiClient);
	}
	
	/**
     * Closes the current connection to Google Play services and creates a new connection.
     * 
     * @param	なし
     * @return	なし
     */
	public void reconnect(){
		mApiClient.reconnect();
	}
	
	/**
     * Closes the connection to Google Play services.
     * 
     * @param	なし
     * @return	なし
     */
	public void disconnect(){
		mApiClient.disconnect();
	}
	
	/**
     * GooglePlayServiceに接続し、Receiverアプリケーションを起動する
     * 
     * @param	なし
     * @return	なし
     */
	public void launchReceiver() {
		try {
			if(mApiClient != null){
				if(mApiClient.isConnected() || mApiClient.isConnecting()){
					return;
				}
			}
			
			mCastListener = new Cast.Listener() {

				@Override
				public void onApplicationDisconnected(int errorCode) {
					teardown();
				}
				
				@Override
				public void onApplicationStatusChanged(){
				}

			};

			mConnectionCallbacks = new ConnectionCallbacks(this.appId);
			mConnectionFailedListener = new ConnectionFailedListener();
			Cast.CastOptions.Builder apiOptionsBuilder = Cast.CastOptions
					.builder(mSelectedDevice, mCastListener);
			mApiClient = new GoogleApiClient.Builder(this.context)
					.addApi(Cast.API, apiOptionsBuilder.build())
					.addConnectionCallbacks(mConnectionCallbacks)
					.addOnConnectionFailedListener(mConnectionFailedListener)
					.build();

			mApiClient.connect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
     * GooglePlayServiceへの接続情報（接続・切断）のイベントリスナ
     * 
     */
	private class ConnectionCallbacks implements
			GoogleApiClient.ConnectionCallbacks {

		private String app_id;

		/**
	     * コンストラクタ
	     * 
	     * @param	app_id ReceiverアプリのアプリケーションID
	     * @return	なし
	     */
		public ConnectionCallbacks(String app_id) {
			this.app_id = app_id;
		}

		@Override
		public void onConnected(Bundle connectionHint) {
			
			if (mApiClient == null) {
				return;
			}

			try {
				if (mWaitingForReconnect) {
					mWaitingForReconnect = false;

					if ((connectionHint != null)
							&& connectionHint
									.getBoolean(Cast.EXTRA_APP_NO_LONGER_RUNNING)) {						
						teardown();
					} else {
						
					}
				} else {
					Cast.CastApi
							.launchApplication(mApiClient, this.app_id, false)
							.setResultCallback(
									new ResultCallback<Cast.ApplicationConnectionResult>() {
										@Override
										public void onResult(
												ApplicationConnectionResult result) {

											Status status = result.getStatus();
											if (status.isSuccess()) {
												for(int i=0; i<callbacks.size(); i++){
													callbacks.get(i).onAttach();
												}
												mApplicationStarted = true;
											} else {
												teardown();
											}
										}
									});
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onConnectionSuspended(int cause) {
			mWaitingForReconnect = true;
		}
	}

	/**
     * GooglePlayServiceへの接続が失敗した場合のイベントリスナ
     * 
     */
	private class ConnectionFailedListener implements
			GoogleApiClient.OnConnectionFailedListener {
		@Override
		public void onConnectionFailed(ConnectionResult result) {
			teardown();
		}
	}

	/**
     * Receiverアプリケーションを終了し、GooglePlayServiceから切断する
     * 
     * @param	なし
     * @return	なし
     */
	public void teardown() {
		if (mApiClient != null) {
			if (mApplicationStarted) {
				if (mApiClient.isConnected()) {
					Cast.CastApi.stopApplication(mApiClient, mSessionId);
					for(int i=0; i<callbacks.size(); i++){
						callbacks.get(i).onDetach();
					}
					mApiClient.disconnect();
				}
				mApplicationStarted = false;
			}
			mApiClient = null;
		}
		mWaitingForReconnect = false;
		mSessionId = null;
	}
	
}
