/*
 ChromeCastApplication.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.chromecast.core;

import java.util.ArrayList;
import org.deviceconnect.android.deviceplugin.chromecast.BuildConfig;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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
 * アプリケーションIDに対応したReceiverアプリのコントロール
 * </p>
 * @author NTT DOCOMO, INC.
 */
public class ChromeCastApplication implements
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener{
    
    private static final String TAG = ChromeCastApplication.class.getSimpleName();

    private CastDevice mSelectedDevice;
    private GoogleApiClient mApiClient;
    private Cast.Listener mCastListener;
    private Context context;
    private String appId;
    private ArrayList<Callbacks> callbacks;
    private boolean isApplicationDisconnected = false;

    /**
     * コールバックのインターフェース
     * 
     * @param   なし
     * @return  なし
     */
    public interface Callbacks {
        /**
         * Chromecast Applicationにアタッチする
         * 
         * @param   なし
         * @return  なし
         */
        public void onAttach();

        /**
         * Chromecast Applicationにデタッチする
         * 
         * @param   なし
         * @return  なし
         */
        public void onDetach();
    }

    /**
     * コンストラクタ
     * 
     * @param   context     コンテキスト
     * @param   appId       ReceiverアプリのアプリケーションID
     * @return  なし
     */
    public ChromeCastApplication(Context context, String appId) {
        this.context = context;
        this.appId = appId;
        this.mSelectedDevice = null;
        callbacks = new ArrayList<Callbacks>();
    }
    
    @Override
    public void onConnected(Bundle connectionHint) {
        if(BuildConfig.DEBUG){
            Log.d(TAG, "onConnected");
        }
        if (mApiClient == null) {
            return;
        }

        try {
            if (connectionHint != null && connectionHint.getBoolean(Cast.EXTRA_APP_NO_LONGER_RUNNING)) {
                teardown();
            } else {
                launchApplication();
            }
        } catch (Exception e) {
            if(BuildConfig.DEBUG){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        if(BuildConfig.DEBUG){
            Log.d(TAG, "onConnectionSuspended$cause: " + cause);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if(BuildConfig.DEBUG){
            Log.d(TAG, "onConnectionFailed$result: " + result.toString());
        }
        mApiClient = null;
        connect();
    }

    /**
     * GoogleApiClientを取得する
     * 
     * @param   なし
     * @return  GoogleApiClient
     */
    public GoogleApiClient getGoogleApiClient(){
        return mApiClient;
    }

    /**
     * コールバックを登録する
     * 
     * @param   callbacks   コールバック
     * @return  なし
     */
    public void addCallbacks(Callbacks callbacks) {
        this.callbacks.add(callbacks);
    }

    /**
     * Chromecastデバイスをセットする
     * 
     * @param   mSelectedDevice
     * @return  なし
     */
    public void SetSelectedDevice(CastDevice mSelectedDevice) {
        this.mSelectedDevice = mSelectedDevice;
    }
    
    /**
     * Chromecastデバイスを取得する
     * 
     * @param   なし
     * @return  CastDevice
     */
    public CastDevice GetSelectedDevice() {
        return mSelectedDevice;
    }
    
    /**
     * GooglePlayServiceに接続し、Receiverアプリケーションを起動する
     * 
     * @param   なし
     * @return  なし
     */
    public void connect() {
        
        try {
            if(mApiClient != null && isApplicationDisconnected){
                isApplicationDisconnected = false;
                launchApplication();
            }
            
            if(mApiClient == null){
                isApplicationDisconnected = false;
                
                mCastListener = new Cast.Listener() {
                    @Override
                    public void onApplicationDisconnected(int statusCode) {
                        if(BuildConfig.DEBUG){
                            Log.d(TAG, "onApplicationDisconnected$statusCode: " + statusCode);
                        }
                        isApplicationDisconnected = true;
                    }
                    @Override
                    public void onApplicationStatusChanged() {
                        if(BuildConfig.DEBUG){
                            Log.d(TAG, "onApplicationStatusChanged");
                        }
                    }
                };
                
                Cast.CastOptions.Builder apiOptionsBuilder = 
                        Cast.CastOptions.builder(mSelectedDevice, mCastListener);
                mApiClient = new GoogleApiClient.Builder(this.context)
                        .addApi(Cast.API, apiOptionsBuilder.build())
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .build();
                mApiClient.connect();
            }
            
        } catch (Exception e) {
            if(BuildConfig.DEBUG){
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Receiverアプリケーションを終了し、GooglePlayServiceから切断し、再接続する
     * 
     * @param   なし
     * @return  なし
     */
    public void reconnect() {
        stopApplication(true);
    }
    
    /**
     * Receiverアプリケーションを終了し、GooglePlayServiceから切断する
     * 
     * @param   なし
     * @return  なし
     */
    public void teardown() {
        stopApplication(false);
    }
    
    /**
     * Receiverアプリケーションを起動する
     * 
     * @param   なし
     * @return  なし
     */
    private void launchApplication(){
        if(mApiClient != null && mApiClient.isConnected()){
            Cast.CastApi.launchApplication(mApiClient, appId, false).setResultCallback(new ResultCallback<Cast.ApplicationConnectionResult>() {
                @Override
                public void onResult(ApplicationConnectionResult result) {
                    Status status = result.getStatus();
                    if (status.isSuccess()) {
                        if(BuildConfig.DEBUG){
                            Log.d(TAG, "launchApplication$onResult: Success");
                        }
                        for (int i = 0; i < callbacks.size(); i++) {
                            callbacks.get(i).onAttach();
                        }
                    } else {
                        if(BuildConfig.DEBUG){
                            Log.d(TAG, "launchApplication$onResult: Fail");
                        }
                        teardown();
                    }
                }
            });
        }
    }
    
    /**
     * Receiverアプリケーションを停止する
     * <p>
     * 停止後、再接続することもできる
     * </p>
     * @param   isReconnect 再接続するか否か
     * @return  なし
     */
    private void stopApplication(final boolean isReconnect){
        
        if(mApiClient != null && mApiClient.isConnected()){
            Cast.CastApi.stopApplication(mApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status result) {
                    if(result.getStatus().isSuccess()){
                        if(BuildConfig.DEBUG){
                            Log.d(TAG, "stopApplication$onResult: Success");
                        }
                        
                        for(int i=0; i<callbacks.size(); i++){
                            callbacks.get(i).onDetach();
                        }
                        mApiClient.disconnect();
                        mApiClient = null;
                        
                        if(isReconnect){
                            connect();
                        }
                    }else{
                        if(BuildConfig.DEBUG){
                            Log.d(TAG, "stopApplication$onResult: Fail");
                        }
                    }
                }
            });
        }
    }
}
