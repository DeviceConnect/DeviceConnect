package com.nttdocomo.android.dconnect.deviceplugin.hue.control;

import java.util.List;

import android.os.Build;
import com.nttdocomo.android.dconnect.deviceplugin.util.DcLoggerHue;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHHueParsingError;

/**
HueControl
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

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
     * PH_SDKオブジェクト.
     */
    private static PHHueSDK mPhHueSDK;

    /**
     * 最大ウェイト値.
     */
    private static final int MAX_WAIT_COUNT = 15;
    /**
     * ウェイト時間(ミリ秒).
     */
    private static final long WAIT_MMSEC = 500;
    /**
     * アクセス回数.
     */
    private static int mGettingAccessPointWaitCount;
    /**
     * アクセスポイントリスト用アレイリスト.
     */
    private static List<PHAccessPoint> mAccessPointList;
    
    /**
     * アクセスポイントリストの取得.
     * @return アクセスポイント
     */
    private static synchronized List<PHAccessPoint> getAccessPointList() {
        return mAccessPointList;
    }

    
    public static synchronized BridgeConnectState getBridgeConnectState(){
        return mBridgeConnectState;
    }

    private static synchronized void setBridgeConnectState(BridgeConnectState state){
        mBridgeConnectState = state;
    }

    /**
     * アクセスポイントリストの長さの取得.
     * @return アクセスポイントリストの長さ
     */
    private static synchronized int getAccessPointListSize() {
        if (mAccessPointList == null) {
            return 0;
        } else {
            return mAccessPointList.size();
        }
    }

    /**
     * アクセスポイントリストの取得.
     * @param accessPointList 
     */
    private static synchronized void setAccessPointList(final List<PHAccessPoint> accessPointList) {
        mAccessPointList = accessPointList;
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
            //エラーは無視
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
     * アクセス回数：カウントアップ.
     */
    private static synchronized void addAccessPointCount() {
        mGettingAccessPointWaitCount++;
    }

    /**
     * アクセス回数：クリア.
     */
    private static synchronized void clearAccessPointCount() {
        mGettingAccessPointWaitCount = 0;
    }

    /**
     * アクセス回数：最大値.
     */
    private static synchronized void setMaxAccessPointCount() {
        mGettingAccessPointWaitCount = MAX_WAIT_COUNT;
    }

    /**
     * リトライ回数が最大値かどうかの確認.
     * @return アクセスリトライ回数
     */
    private static synchronized boolean isMaxAccessPointCount() {
        return mGettingAccessPointWaitCount >= MAX_WAIT_COUNT;
    }

    /**
     * ４回までアクセスリトライする.
     * @return アクセスポイントリスト
     */
    public List<PHAccessPoint> getSyncAllAccessPoint() {

        List<PHAccessPoint> list = getSyncAllAccessPointPrivate();
        
        //1回でだめなら4回たたく
        if (list == null || list.size() == 0) {
            list = getSyncAllAccessPointPrivate();
        }

        if (list == null || list.size() == 0) {
            list = getSyncAllAccessPointPrivate();
        }

        if (list == null || list.size() == 0) {
            list = getSyncAllAccessPointPrivate();
        }

        return list;
        
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
     * ブリッジ情報を取得.
     * @return ブリッジ情報 
     */
    private List<PHAccessPoint> getSyncAllAccessPointPrivate() {
        
        DcLoggerHue logger = new DcLoggerHue();
        logger.entering(this, "getAllAccessPoint");

        setAccessPointList(null);
        clearAccessPointCount();
        
        //ブリッジ情報を取得
        PHHueSDK hueSdk = HueControl.getPHHueSDK();
        setAccessPointList(hueSdk.getAccessPointsFound());
        
        if (getAccessPointListSize() > 0) {

            setMaxAccessPointCount();

        } else {
            doBridgeSearch(hueSdk);
            while (!isMaxAccessPointCount()) {
                
                sleep(WAIT_MMSEC);
                addAccessPointCount();
                
                if (getAccessPointListSize() > 0) {
                    setMaxAccessPointCount();
                }

//                logger.fine(this, "getSyncAllAccessPointPrivate wait cnt", mGettingAccessPointWaitCount);

            }
        
        }
        
        logger.exiting(this, "getSyncAllAccessPointPrivate", mAccessPointList);
        
        return getAccessPointList();
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
                
                setAccessPointList(accessPoint);
                setMaxAccessPointCount();
                logger.fine("HueControl", "onAccessPointsFound size", accessPoint.size());
                   
            } else {
                // FallBack Mechanism.  If a UPNP Search returns no results then perform an IP Scan.
//                Of course it could fail as the user has disconnected their bridge, 
//                connected to a wrong network or disabled Network Discovery on their 
//                router so it is not guaranteed to work.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                    PHBridgeSearchManager sm = (PHBridgeSearchManager) phHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
                    // Start the IP Scan Search if the UPNP and NPNP return 0 results.
                    sm.search(false, false, true);
                    
                    logger.fine("HueControl", "onAccessPointsFound", "sm.search(false, false, true)");

                }
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
