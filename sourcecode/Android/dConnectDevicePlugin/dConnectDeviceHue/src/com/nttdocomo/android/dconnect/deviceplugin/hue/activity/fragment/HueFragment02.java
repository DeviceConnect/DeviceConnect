package com.nttdocomo.android.dconnect.deviceplugin.hue.activity.fragment;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.nttdocomo.android.dconnect.deviceplugin.hue.R;
import com.nttdocomo.android.dconnect.deviceplugin.hue.control.HueControl;
import com.nttdocomo.android.dconnect.deviceplugin.hue.control.HueControl.BridgeConnectState;
import com.nttdocomo.android.dconnect.deviceplugin.util.DcLoggerHue;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHMessageType;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHHueParsingError;

/**
HueFargment03
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

/**
 * Hue設定画面(2)フラグメント.
 *
 */
public class HueFragment02 extends Fragment implements OnClickListener {
    
    static View mRootView;
    /**
     * ロガー.
     */
    private DcLoggerHue mLogger = new DcLoggerHue();
    
    /** Macアドレス. */
    private static String mMac = "";

    /** IPアドレス. */
    private static String mIp = "";
    
    /** Activity. */
    private static Activity mActivity;
    
    /** HueSDKオブジェクト. */
    private static PHHueSDK mPhHueSDK;
    
    /** ステータスを表示するTextView. */
    private static TextView mTextViewStatus;
    
    /** Howtoを表示するTextView. */
    private static TextView mTextViewHowto;
    
    /** IPを表示するTextView. */
    private static TextView mTextViewIp;
    
    /** Button. */
    private static Button mButton;
    
    /** ステータス. */
    private static HueState mStatus = HueState.INIT;
    
    /**
     * Hue接続状態
     */
    public enum HueState {

        INIT,//未認証
        NOCONNECT,//未接続
        AUTHENTICATE_FAILD,//認証失敗
        AUTHENTICATE_SUCCESS//認証済み
        
    };

    public static HueFragment02 newInstance(final String mac, final String ip) {
        HueFragment02 fragment = new HueFragment02();
 
        mIp = ip;
        mMac = mac;

//        connectBridge();
        
        return fragment;
    }

    private static void connectBridge() {
        DcLoggerHue myLogger = new DcLoggerHue();
        myLogger.entering("HueFragment02", "connectBridge");
        
        mListener = new DCPHSDKListener();

        mPhHueSDK = HueControl.getPHHueSDK();

        // Register the PHHueListener to receive callback notifications on Bridge events.
        mPhHueSDK.getNotificationManager().registerSDKListener(mListener);
        mPhHueSDK.getAccessPointsFound();

        PHAccessPoint accessPoint = new PHAccessPoint();
        accessPoint.setIpAddress(mIp);
        accessPoint.setUsername(HueControl.APNAME);

        mStatus = HueState.INIT;
        
        if (!mPhHueSDK.isAccessPointConnected(accessPoint)) {
            mPhHueSDK.connect(accessPoint);
            
        } else {
            mStatus = HueState.AUTHENTICATE_SUCCESS;
        }

        mHandler.sendEmptyMessageDelayed(INVALIDATE, 1000);

        myLogger.exiting("HueFragment02", "connectBridge");

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, 
            final Bundle savedInstanceState) {

        mLogger.entering(this, "onCreateView");
        
        try {
            mRootView = inflater.inflate(R.layout.hue_fragment_02, container, false);
            
            mTextViewIp = (TextView) mRootView.findViewById(R.id.textMac);
            mTextViewIp.setText(mMac);

            mTextViewIp = (TextView) mRootView.findViewById(R.id.textIp);
            mTextViewIp.setText(mIp);

            mTextViewStatus = (TextView) mRootView.findViewById(R.id.textStatus);
            mTextViewHowto = (TextView) mRootView.findViewById(R.id.textHowto);
            
            mButton = (Button) mRootView.findViewById(R.id.btnBridgeTouroku);
            mButton.setOnClickListener(this);
            mButton.setVisibility(View.GONE);
            
            mActivity = this.getActivity();
        } catch (Exception e) {
            mLogger.warning(this,  "onCreateView", "", e);
        }
        
        connectBridge();

        mLogger.exiting(this, "onCreateView");
        
        return mRootView;
    }
    
    // Create a Listener to receive bridge notifications.
    private static PHSDKListener mListener;

    private static class DCPHSDKListener implements PHSDKListener{

        @Override
        public void onBridgeConnected(final PHBridge b) {
            DcLoggerHue myLogger = new DcLoggerHue();
            myLogger.entering("HueFragment02", "onBridgeConnected");

            mStatus = HueState.AUTHENTICATE_SUCCESS;
            mHandler.sendEmptyMessage(INVALIDATE);
           
            mPhHueSDK.setSelectedBridge(b);
            mPhHueSDK.enableHeartbeat(b, PHHueSDK.HB_INTERVAL);
            mPhHueSDK.getLastHeartbeat().put(
                    b.getResourceCache().getBridgeConfiguration() .getIpAddress(), System.currentTimeMillis());
          
            //隠れてライト検索をスタートさせておく
            b.findNewLights(null);

            HueControl.destroyPHHueSDK(); //Hueとのやりとりを停止
            
            myLogger.exiting("HueFragment02", "onBridgeConnected");

        }

        @Override
        public void onAuthenticationRequired(final PHAccessPoint accessPoint) {
            DcLoggerHue myLogger = new DcLoggerHue();
            myLogger.entering("HueFragment02", "onAuthenticationRequired");

           mStatus = HueState.INIT;
           mPhHueSDK.startPushlinkAuthentication(accessPoint);
           mHandler.sendEmptyMessage(INVALIDATE);

           myLogger.exiting("HueFragment02", "onAuthenticationRequired");

        }

        @Override
        public void onAccessPointsFound(final List<PHAccessPoint> accessPoint) {
            DcLoggerHue myLogger = new DcLoggerHue();
            myLogger.entering("HueFragment02", "onAccessPointsFound");
            myLogger.exiting("HueFragment02", "onAccessPointsFound");

        }

        @Override
        public void onConnectionLost(final PHAccessPoint arg0) {
            DcLoggerHue myLogger = new DcLoggerHue();
            myLogger.entering("HueFragment02", "onConnectionLost");
            myLogger.exiting("HueFragment02", "onConnectionLost");

        }

        @Override
        public void onConnectionResumed(final PHBridge arg0) {
            DcLoggerHue myLogger = new DcLoggerHue();
            myLogger.entering("HueFragment02", "onConnectionResumed");
            myLogger.exiting("HueFragment02", "onConnectionResumed");
        }

        @Override
        public void onError(final int code, final String message) {

            DcLoggerHue myLogger = new DcLoggerHue();
            myLogger.entering("HueFragment02", "onError", "code: " + String.valueOf(code) + "  msg:" + message);

            if (code == PHHueError.NO_CONNECTION) {
                bridgeNoConnect();

            } 
            else if (code == PHHueError.AUTHENTICATION_FAILED || code==1158) {  
                AuthFaild();
            } 
            else if (code == PHHueError.BRIDGE_NOT_RESPONDING) {
                bridgeNoConnect();


            } 
            else if (code == PHMessageType.BRIDGE_NOT_FOUND) {
                bridgeNoConnect();

            }

            myLogger.exiting("HueFragment02", "onError");

        }

        @Override
        public void onCacheUpdated(final List<Integer> arg0, final PHBridge arg1) {
            DcLoggerHue myLogger = new DcLoggerHue();
            myLogger.entering("HueFragment02", "onCacheUpdated");
            myLogger.exiting("HueFragment02", "onCacheUpdated");

        }

        @Override
        public void onParsingErrors(final List<PHHueParsingError> arg0) {
            DcLoggerHue myLogger = new DcLoggerHue();
            myLogger.entering("HueFragment02", "onParsingErrors");
            myLogger.exiting("HueFragment02", "onParsingErrors");

        }   
    }

    private static void bridgeNoConnect() {
        DcLoggerHue myLogger = new DcLoggerHue();
        myLogger.entering("HueFragment02", "bridgeNoConnect");

        HueControl.destroyPHHueSDK(); //Hueとのやりとりを停止

        mStatus = HueState.NOCONNECT;
        mHandler.sendEmptyMessage(INVALIDATE);

        myLogger.exiting("HueFragment02", "bridgeNoConnect");
       
    }

    
    private static void AuthFaild() {
        DcLoggerHue myLogger = new DcLoggerHue();
        myLogger.entering("HueFragment02", "AuthFaild");

        HueControl.destroyPHHueSDK(); //Hueとのやりとりを停止

        mStatus = HueState.AUTHENTICATE_FAILD;
        mHandler.sendEmptyMessage(INVALIDATE);

        myLogger.exiting("HueFragment02", "AuthFaild");
       
    }
    
    private static long mNextTime;
    private static final int INVALIDATE = 1;
    static int count = 0;
    
    /**
     * タイマーハンドラー
     */
    private final static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            DcLoggerHue myLogger = new DcLoggerHue();
            myLogger.entering("HueFragment02", "Handler");
            
            if (msg.what == INVALIDATE) {
                if (mStatus == HueState.INIT){
               
                    count++;
                    if (count%2 == 0){
                        ImageView img = (ImageView) mRootView.findViewById(R.id.iv01);
                        img.setImageResource(R.drawable.img01);
                    } else {
                        ImageView img = (ImageView) mRootView.findViewById(R.id.iv01);
                        img.setImageResource(R.drawable.img02);
                    }
                    msg = obtainMessage(INVALIDATE);
                    long current = SystemClock.uptimeMillis();
                    if (mNextTime < current) {
                        // 100ms周期でタイマーイベントが発生
                        mNextTime = current + 1000;
                    }
                    sendMessageAtTime(msg, mNextTime);
                    // 100ms周期でタイマーイベントが発生
                    mNextTime += 1000;
                    mTextViewStatus.setText("認証中!!");
                    mTextViewHowto.setText("ブリッジのボタンを押してください。ボタンを押すと、本アプリが登録されます。");
                    
                } else if (mStatus == HueState.AUTHENTICATE_SUCCESS) {

                    dispAuthSuccess();
                    
               } else if (mStatus == HueState.AUTHENTICATE_FAILD) {

                   dispAuthFaild();
                   
               } else if (mStatus == HueState.NOCONNECT) {

                   dispNoConnect();
                   
               }
            }
            
            myLogger.exiting("HueFragment02", "Handler");

        }
    };

    private static void dispAuthSuccess() {
        DcLoggerHue myLogger = new DcLoggerHue();
        myLogger.entering("HueFragment02", "dispAuthSuccess");

        mTextViewStatus.setText("認証済み");
        mTextViewStatus.invalidate();
        mTextViewHowto.setText("設定が完了しています。ライトの設定を行ってください。");
        mTextViewHowto.invalidate();
        ImageView img = (ImageView) mRootView.findViewById(R.id.iv01);
        img.setImageResource(R.drawable.img05);
        img.invalidate();
        
        mButton.setText("ライト登録画面へ進む");
        mButton.setVisibility(View.VISIBLE);

        myLogger.exiting("HueFragment02", "dispAuthSuccess");
    }

    private static void dispAuthFaild() {
        DcLoggerHue myLogger = new DcLoggerHue();
        myLogger.entering("HueFragment02", "dispAuthFaild");

        mTextViewStatus.setText("認証失敗");

        dispFaildCommon();

        myLogger.exiting("HueFragment02", "dispAuthFaild");
    }

    private static void dispNoConnect() {
        DcLoggerHue myLogger = new DcLoggerHue();
        myLogger.entering("HueFragment02", "dispNoConnect");

        mTextViewStatus.setText("接続失敗");

        dispFaildCommon();
        
        myLogger.exiting("HueFragment02", "dispNoConnect");
    }

    private static void dispFaildCommon() {

        mTextViewStatus.invalidate();
        mTextViewHowto.setText("");
        mTextViewHowto.invalidate();
        ImageView img = (ImageView) mRootView.findViewById(R.id.iv01);
        img.setImageResource(R.drawable.img01);
        img.invalidate();
        
        mButton.setText("ブリッジにアプリを登録");
        mButton.setVisibility(View.VISIBLE);
    }
    
   
    @Override
    public void onClick(final View v) {
        mLogger.entering("HueFragment02", "onClick");

        if (mStatus == HueState.AUTHENTICATE_SUCCESS){
            FragmentManager manager = getFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            
            transaction.setCustomAnimations(
                    R.anim.fragment_slide_right_enter,
                    R.anim.fragment_slide_left_exit,
                    R.anim.fragment_slide_left_enter,
                    R.anim.fragment_slide_right_exit);
            
            transaction.replace(R.id.fragment_frame, HueFragment03.newInstance(mIp));
            
            transaction.commit(); 
            
        } else {
            mButton.setVisibility(View.INVISIBLE);

            connectBridge();
        }

        mLogger.exiting("HueFragment02", "onClick");

    }

    // 画面終了
    @Override
    public void onDestroy() {

        mLogger.entering(this, "onDestroy");
        try {
            
            if(mListener !=null){
                mPhHueSDK.getNotificationManager().unregisterSDKListener(mListener);
                
            }

            HueControl.destroyPHHueSDK();

        } catch (Exception e) {
            mLogger.warning(this, "onDestroy", "", e);

        }

        mLogger.exiting(this, "onDestroy");

        super.onDestroy();

    }

}
