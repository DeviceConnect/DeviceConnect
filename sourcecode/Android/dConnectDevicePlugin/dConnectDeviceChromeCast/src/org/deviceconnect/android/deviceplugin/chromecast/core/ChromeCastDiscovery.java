/*
 ChromeCastDiscovery.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.chromecast.core;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;

/**
 * Chromecast Discovery クラス
 * 
 * <p>
 * ReceiverアプリのアプリケーションIDに対応した、Chromecastデバイスを探索する
 * </p>
 * @author NTT DOCOMO, INC.
 */
public class ChromeCastDiscovery {
    private MediaRouter mMediaRouter;
    private MediaRouteSelector mMediaRouteSelector;
    private MediaRouter.Callback mMediaRouterCallback;
    private CastDevice mSelectedDevice;

    private ArrayList<String> mRouteNames;
    private final ArrayList<MediaRouter.RouteInfo> mRouteInfos;

    /**
     * コールバックのインターフェース
     * 
     * @param   なし
     * @return  なし
     */
    public interface Callbacks {
        /**
         * Chromecastデバイスの探索状態（発見or消失した場合）を通知する
         * 
         * @param   devices
         * @return  なし
         */
        public void onCastDeviceUpdate(ArrayList<String> devices);
        /**
         * Chromecastデバイスが選択されたときに通知する
         * 
         * @param   selectedDevice
         * @return  なし
         */
        public void onCastDeviceSelected(CastDevice selectedDevice);
        /**
         * Chromecastデバイスが非選択されたときに通知する
         * 
         * @param   なし
         * @return  なし
         */
        public void onCastDeviceUnselected();
    }

    private Callbacks callbacks;

    /**
     * コールバックを登録する
     * 
     * @param   callbacks   コールバック
     * @return  なし
     */
    public void setCallbacks(Callbacks callbacks) {
        this.callbacks = callbacks;
    }

    /**
     * コンストラクタ
     * <p>
     * ReceiverアプリのアプリケーションIDに対応した、Chromecastデバイスの探索を開始する
     * </p>
     * 
     * @param   context コンテキスト
     * @param   appId   ReceiverアプリのアプリケーションID
     * @return  なし
     */
    public ChromeCastDiscovery(Context context, String appId) {

        mRouteNames = new ArrayList<String>();
        mRouteInfos = new ArrayList<MediaRouter.RouteInfo>();

        mMediaRouter = MediaRouter.getInstance(context);
        mMediaRouteSelector = new MediaRouteSelector.Builder()
                .addControlCategory(
                        CastMediaControlIntent.categoryForCast(appId)).build();

        mMediaRouterCallback = new MediaRouter.Callback() {
            @Override
            public void onRouteAdded(MediaRouter router,
                    MediaRouter.RouteInfo info) {
                synchronized(this){
                    mRouteInfos.add(info);
                    mRouteNames.add(info.getName());
                }
                callbacks.onCastDeviceUpdate(mRouteNames);
            }

            @Override
            public void onRouteRemoved(MediaRouter router,
                    MediaRouter.RouteInfo info) {
                for (int i = 0; i < mRouteInfos.size(); i++) {
                    MediaRouter.RouteInfo routeInfo = mRouteInfos.get(i);
                    if (routeInfo.equals(info)) {
                        synchronized(this){
                            mRouteInfos.remove(i);
                            mRouteNames.remove(i);
                        }
                        callbacks.onCastDeviceUpdate(mRouteNames);
                        break;
                    }
                }
            }

            @Override
            public void onRouteSelected(MediaRouter router,
                    MediaRouter.RouteInfo info) {
                mSelectedDevice = CastDevice.getFromBundle(info.getExtras());
                callbacks.onCastDeviceSelected(mSelectedDevice);
            }

            @Override
            public void onRouteUnselected(MediaRouter router,
                    MediaRouter.RouteInfo info) {
                callbacks.onCastDeviceUnselected();
                mSelectedDevice = null;
            }

            @Override
            public void onRouteChanged(MediaRouter router,
                    MediaRouter.RouteInfo info) {
            }

            @Override
            public void onRoutePresentationDisplayChanged(MediaRouter router,
                    MediaRouter.RouteInfo info) {
            }

            @Override
            public void onRouteVolumeChanged(MediaRouter router,
                    MediaRouter.RouteInfo info) {
            }

            @Override
            public void onProviderAdded(MediaRouter router,
                    MediaRouter.ProviderInfo info) {
            }

            @Override
            public void onProviderChanged(MediaRouter router,
                    MediaRouter.ProviderInfo info) {
            }

            @Override
            public void onProviderRemoved(MediaRouter router,
                    MediaRouter.ProviderInfo info) {
            }
        };
    }

    /**
     * イベントを登録する
     * 
     * @param   なし
     * @return  なし
     */
    public void registerEvent() {
        update();
        mMediaRouter.addCallback(mMediaRouteSelector, mMediaRouterCallback,
                MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
    }

    /**
     * イベントの登録を解除する
     * 
     * @param   なし
     * @return  なし
     */
    public void unregisterEvent() {
        mMediaRouter.removeCallback(mMediaRouterCallback);
    }

    /**
     * Chromecastデバイス情報を更新する
     * 
     * @param   なし
     * @return  なし
     */
    private void update(){
        List<MediaRouter.RouteInfo> rInfos = mMediaRouter.getRoutes();

        mRouteInfos.clear();
        mRouteNames.clear();
        for(int i=0; i<rInfos.size(); i++){
            MediaRouter.RouteInfo info = rInfos.get(i);

            if(info.getDescription() != null){
                mRouteInfos.add(info);
                mRouteNames.add(info.getName());
            }
        }
    }
	
    /**
     * 選択されたChromecastデバイスを返す
     * 
     * @param   なし
     * @return  mSelectedDevice 選択されたデバイス
     */
    public CastDevice getSelectedDevice(){
        return mSelectedDevice;
    }

    /**
     * 接続可能なChromecastデバイスのデバイス名のリストを返す
     * 
     * @param   なし
     * @return  mRouteNames デバイス名のリスト
     */
    public ArrayList<String> getDeviceNames(){
        return mRouteNames;
    }

    /**
     * Chromecastデバイスを選択する
     * 
     * @param   name    Chromecastデバイス名
     * @return  なし
     */
    public void setRouteName(String name) {
        for (int i = 0; i < mRouteInfos.size(); i++) {
            MediaRouter.RouteInfo routeInfo = mRouteInfos.get(i);
            if (routeInfo.getName().equals(name)) {
                mMediaRouter.selectRoute(routeInfo);
                break;
            }
        }
    }
}
