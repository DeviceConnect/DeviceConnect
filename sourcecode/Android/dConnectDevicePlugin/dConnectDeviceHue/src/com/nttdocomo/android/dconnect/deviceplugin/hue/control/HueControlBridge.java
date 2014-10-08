/*
HueControlBridge
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

package com.nttdocomo.android.dconnect.deviceplugin.hue.control;

import com.nttdocomo.android.dconnect.deviceplugin.hue.profile.HueLightProfileConstants;
import com.nttdocomo.android.dconnect.deviceplugin.hue.util.DConnectMessageHandler;
import com.nttdocomo.android.dconnect.deviceplugin.util.DcAsync;
import com.nttdocomo.android.dconnect.deviceplugin.util.DcLoggerHue;
import com.nttdocomo.dconnect.message.DConnectMessage;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;

/**
 * HueControlBridgeクラス.
 */
public class HueControlBridge extends HueControl {

    /**
     * ロガー.
     */
    private DcLoggerHue mLogger = new DcLoggerHue();
    /**
     * 通信可能なブリッジレスポンス.
     */
    private String mEnabledBridgeResponse;
    /**
     * レスポンスがあるかどうかの確認.
     */
    private boolean mIsResponsed;
    
    /**
     * タイムアウトになる回数.
     */
    private final int maxCnt = 10;

    /**
     * ブリッジの選択.
     * @param deviceid 
     * @return 選択されたブリッジ
     */
    public PHBridge selectBridgeSync(final String deviceid) {

        mLogger.entering(this, "selectBridgeSync(deviceid) deviceid", deviceid);

        PHHueSDK hueSdk = HueControl.getPHHueSDK();

        mLogger.fine(this, "selectBridgeSync(deviceid) hueSdk", hueSdk);

        PHAccessPoint accessPoint = new PHAccessPoint();
        accessPoint.setIpAddress(deviceid);
        accessPoint.setUsername(HueControl.APNAME);

        if (!hueSdk.isAccessPointConnected(accessPoint)) {
            hueSdk.connect(accessPoint);
        }

        PHBridge resBridge;
        resBridge = hueSdk.getSelectedBridge();
        hueSdk.enableHeartbeat(resBridge, PHHueSDK.HB_INTERVAL);
        hueSdk.getLastHeartbeat().put(
                resBridge.getResourceCache().getBridgeConfiguration().getIpAddress(),
                System.currentTimeMillis());
        
        mLogger.exiting(this, "selectBridgeSync(deviceid)", resBridge);

        return hueSdk.getSelectedBridge();

    }

    /**
     * ブリッジの取得.
     * @param deviceid 
     * @return 選択されたブリッジ
     */
    public PHBridge getBridgeSync(final String deviceid) {
        return selectBridgeSync(deviceid);
    }


    /**
     * 通信可能なブリッジ情報の初期化.
     */
    private synchronized void initEnabledBridgeData() {

        setEnabledBridgeData(false, "");
    }

    /**
     * 通信可能なブリッジ情報の取得.
     * @return 通信可能なブリッジレスポンス
     */
    private synchronized String getEnabledBridgeResponse() {

        return mEnabledBridgeResponse;

    }

    /**
     * レスポンス状態の取得.
     * @return レスポンスが通信可能かどうかの状態
     */
    private synchronized boolean getIsResponsed() {

        return mIsResponsed;

    }

    /**
     * 通信可能なレスポンス情報の格納.
     * @param isResponsed 
     * @param response 
     */
    private synchronized void setEnabledBridgeData(final boolean isResponsed, final String response) {

        mEnabledBridgeResponse = response;
        mIsResponsed = isResponsed;

    }

    /**
     * ブリッジが通信可能かどうかの確認.
     * @param ip 
     * @return boolean
     */
    public synchronized boolean isEnabledBridge(final String ip) {

        mLogger.entering(this, "isEnabledBridge", ip);

        try {

            initEnabledBridgeData();

            String url = "http://" + ip + "/api/" + HueControl.APNAME + "/lights/";

            DcAsync dcAsync = new DcAsync();
            dcAsync.getAsyncResponse2(url, new DConnectMessageHandler() {
                @Override
                public void handleMessage(final DConnectMessage message) {

                    if (message == null) {
                        mLogger.fine(this, "handleMessage", "null");
                        setEnabledBridgeData(true, "");
                        return;
                    }

                    mLogger.fine(this, "handleMessage", message.toString());

                    setEnabledBridgeData(true, message.toString());

                }
            });

            int cnt = 0;
            while (!getIsResponsed()) {

                mLogger.fine(this, "isEnabledBridge getIsResponsed ", getIsResponsed());
                sleep(HueLightProfileConstants.HUE_SLEEP_TIME_500);

                cnt++;
                mLogger.fine(this, "isEnabledBridge wait ", cnt);

                if (cnt > maxCnt) {
                    dcAsync.stopTask();

                    setEnabledBridgeData(true, "");
                    mLogger.exiting(this, "isEnabledBridge タイムアウト", ip);
                    return false;
                }
            }

            mLogger.fine(this, "isEnabledBridge mEnabledBridgeResponse ", mEnabledBridgeResponse);

            if (getEnabledBridgeResponse().length() == 0) {

                mLogger.exiting(this, "isEnabledBridge 返信無し", ip);

                return false;
            }

            if (getEnabledBridgeResponse().indexOf("unauthorized user") >= 0) {

                mLogger.exiting(this, "isEnabledBridge　未認証", ip);

                return false;
            }

            mLogger.exiting(this, "isEnabledBridge 成功", ip);

            return true;

        } catch (Exception e) {
            mLogger.warning(this, "isEnabledBridge", "", e);
            mLogger.exiting(this, "isEnabledBridge", ip);
            return false;
        }

    }

}
