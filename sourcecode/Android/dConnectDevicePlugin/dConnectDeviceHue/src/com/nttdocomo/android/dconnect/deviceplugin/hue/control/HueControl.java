/*
HueControl
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
 */

package com.nttdocomo.android.dconnect.deviceplugin.hue.control;

import java.util.List;

import com.nttdocomo.android.dconnect.deviceplugin.util.DcLoggerHue;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHHueParsingError;

/**
 * HueControlクラス.
 */
public class HueControl {

    public enum BridgeConnectState {

        STATE_INIT,
        STATE_CONNECT,
        STATE_NON_CONNECT,
        STATE_NOT_AUTHENTICATED
        
    };
    
    private static BridgeConnectState mBridgeConnectState = BridgeConnectState.STATE_INIT;
    
    
    /**
     * アプリケーションネーム.
     */
    public static final String APNAME = "DConnectDeviceHueAndroid";
    
    /**
     * アプリケーションネーム.
     */
    public static final String USERNAME = "DeviceConnetHueClient";
    
    /**
     * PH_SDKオブジェクト.
     */
    private static PHHueSDK mPhHueSDK;

    public static synchronized BridgeConnectState getBridgeConnectState(){
        return mBridgeConnectState;
    }

    private static synchronized void setBridgeConnectState(BridgeConnectState state){
        mBridgeConnectState = state;
    }

    /**
     * PH SDKオブジェクトの取得.
     * @return mPhHueSDK
     */
    public static synchronized PHHueSDK getPHHueSDK() {
        
        DcLoggerHue logger = new DcLoggerHue();
        logger.entering("HueControl", "getPHHueSDK");
        
        setBridgeConnectState(BridgeConnectState.STATE_INIT);
        
        if (mPhHueSDK == null) {
            
            logger.fine("HueControl",  "getPHHueSDK", "before PHHueSDK.create()");
            
            mPhHueSDK = PHHueSDK.create();
            mPhHueSDK.setDeviceName(APNAME);

            mPhHueSDK.getNotificationManager().registerSDKListener(mPhListener);

        }

        logger.exiting("HueControl", "getPHHueSDK");
        
        return mPhHueSDK;
    }

    /**
     * 終了時にHueSDKのハートビート、リスナーを止める.
     */
    public static synchronized void destroyPHHueSDK() {
        
        DcLoggerHue logger = new DcLoggerHue();
        logger.entering("HueControl", "destroyPHHueSDK");

        mPhHueSDK.disableAllHeartbeat();

        PHBridge bridge = mPhHueSDK.getSelectedBridge();

        if (bridge != null) {
            // TODO: エラー処理
            try {
                mPhHueSDK.disconnect(bridge);
            } catch (Exception e) {
            }
        }

        if (mPhListener != null) {
            mPhHueSDK.getNotificationManager().unregisterSDKListener(mPhListener);
        }
        setBridgeConnectState(BridgeConnectState.STATE_INIT);

        logger.exiting("HueControl", "destroyPHHueSDK");

    }
    
    /**
     * キャッシュをアップデートする.
     */
    public static void updateAccessPointCache() {
        
        DcLoggerHue logger = new DcLoggerHue();
        logger.entering("HueControl", "updateAccessPointCache");
        
        //ブリッジ情報を取得
        PHHueSDK hueSdk = HueControl.getPHHueSDK();
        
        PHBridgeSearchManager sm = (PHBridgeSearchManager) hueSdk.getSDKService(PHHueSDK.SEARCH_BRIDGE);
        // Start the UPNP Searching of local bridges.
        sm.search(true, true);
        
        hueSdk.getAccessPointsFound();
        
        logger.exiting("HueControl", "updateAccessPointCache");
    }
    
    /**
     * 指定ミリ秒実行を止めるメソッド.
     * @param mmsec 
     */
    protected synchronized void sleep(final long mmsec) {
        try {
            wait(mmsec);
        } catch (InterruptedException e) {
            e.toString();
        }
    }

    /**
     * ブリッジ検索.
     * @param hueSdk 
     */
    protected static void doBridgeSearch(final PHHueSDK hueSdk) {
      PHBridgeSearchManager sm = (PHBridgeSearchManager) hueSdk.getSDKService(PHHueSDK.SEARCH_BRIDGE);
      // Start the UPNP Searching of local bridges.
      sm.search(true, true, true);
  }
    
    /**
     * HueSDKのリスナー.
     */
    private static PHSDKListener mPhListener = new PHSDKListener() {

        @Override
        public void onAccessPointsFound(final List<PHAccessPoint> accessPoint) {

            DcLoggerHue logger = new DcLoggerHue();
            logger.entering("HueControl", "onAccessPointsFound size");

            PHHueSDK phHueSDK = getPHHueSDK();

            if (accessPoint != null && accessPoint.size() > 0) {
                phHueSDK.getAccessPointsFound().clear();
                phHueSDK.getAccessPointsFound().addAll(accessPoint);
                logger.fine("HueControl", "onAccessPointsFound size", accessPoint.size());
                   
            } else {
                PHBridgeSearchManager sm = (PHBridgeSearchManager) phHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
                sm.search(false, false, true);
            }
            
            logger.exiting("HueControl", "onAccessPointsFound");

        }
        
        @Override
        public void onCacheUpdated(final List<Integer> arg0, final PHBridge arg1) {

            DcLoggerHue logger = new DcLoggerHue();
            logger.entering("HueControl", "onCacheUpdated", arg1);
            logger.exiting("HueControl", "onCacheUpdated");
            
        }

        @Override
        public void onBridgeConnected(final PHBridge b) {
            
            DcLoggerHue logger = new DcLoggerHue();
            logger.entering("HueControl", "onBridgeConnected", b);

            PHHueSDK phHueSDK = getPHHueSDK();

            setBridgeConnectState(BridgeConnectState.STATE_CONNECT);

            phHueSDK.setSelectedBridge(b);
            phHueSDK.enableHeartbeat(b, PHHueSDK.HB_INTERVAL);
            phHueSDK.getLastHeartbeat().put(b.getResourceCache().getBridgeConfiguration() 
                    .getIpAddress(), System.currentTimeMillis());

            logger.exiting("HueControl", "onBridgeConnected");

        }

        @Override
        public void onAuthenticationRequired(final PHAccessPoint accessPoint) {
            
            DcLoggerHue logger = new DcLoggerHue();
            logger.entering("HueControl", "onAuthenticationRequired", accessPoint);

            PHHueSDK phHueSDK = getPHHueSDK();

            setBridgeConnectState(BridgeConnectState.STATE_NOT_AUTHENTICATED);

            phHueSDK.startPushlinkAuthentication(accessPoint);

            logger.exiting("HueControl", "onAuthenticationRequired");

        }

        @Override
        public void onConnectionResumed(final PHBridge bridge) {
            DcLoggerHue logger = new DcLoggerHue();
            logger.entering("HueControl", "onConnectionResumed", bridge);

            PHHueSDK phHueSDK = getPHHueSDK();

            setBridgeConnectState(BridgeConnectState.STATE_CONNECT);

            phHueSDK.getLastHeartbeat().put(bridge.getResourceCache().getBridgeConfiguration()
                    .getIpAddress(),  System.currentTimeMillis());
            for (int i = 0; i < phHueSDK.getDisconnectedAccessPoint().size(); i++) {

                if (phHueSDK.getDisconnectedAccessPoint().get(i).getIpAddress()
                        .equals(bridge.getResourceCache().getBridgeConfiguration().getIpAddress())) {
                    phHueSDK.getDisconnectedAccessPoint().remove(i);
                }
            }
            
            logger.exiting("HueControl", "onConnectionResumed");

        }

        @Override
        public void onConnectionLost(final PHAccessPoint accessPoint) {
            DcLoggerHue logger = new DcLoggerHue();
            logger.entering("HueControl", "onConnectionLost");

            PHHueSDK phHueSDK = getPHHueSDK();

            setBridgeConnectState(BridgeConnectState.STATE_NON_CONNECT);

            if (!phHueSDK.getDisconnectedAccessPoint().contains(accessPoint)) {
                phHueSDK.getDisconnectedAccessPoint().add(accessPoint);
            }
            
            logger.exiting("HueControl", "onConnectionLost");

        }
        
        @Override
        public void onError(final int code, final String message) {
            DcLoggerHue logger = new DcLoggerHue();
            logger.entering("HueControl", "onError", "on Error Called : " + code + ":" + message);

            logger.exiting("HueControl", "onError");

            switch (code) {
            case PHHueError.AUTHENTICATION_FAILED:
                setBridgeConnectState(BridgeConnectState.STATE_NON_CONNECT);
                break;

            case PHHueError.BRIDGE_NOT_RESPONDING:
                setBridgeConnectState(BridgeConnectState.STATE_NON_CONNECT);
                break;

            case PHHueError.NO_CONNECTION:
                setBridgeConnectState(BridgeConnectState.STATE_NON_CONNECT);
                break;

            default:
                break;
            }

        }


        @Override
        public void onParsingErrors(final List<PHHueParsingError> arg0) {
            
        }
    };
}
