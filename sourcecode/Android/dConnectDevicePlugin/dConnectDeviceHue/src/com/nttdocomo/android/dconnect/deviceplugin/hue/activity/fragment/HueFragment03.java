package com.nttdocomo.android.dconnect.deviceplugin.hue.activity.fragment;

import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.nttdocomo.android.dconnect.deviceplugin.hue.R;
import com.nttdocomo.android.dconnect.deviceplugin.hue.control.HueControl;
import com.nttdocomo.android.dconnect.deviceplugin.hue.control.HueControlBrige;
import com.nttdocomo.android.dconnect.deviceplugin.util.DcLoggerHue;
import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;

/**
HueFargment03
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

/**
 * Hue設定画面(3)フラグメント.
 */
public class HueFragment03   extends Fragment {
    
    /** IP. */
    private static String mIp = "";
    /**
     * ロガー.
     */
    private DcLoggerHue mLogger = new DcLoggerHue();
    
    private PHLightListenerNewLights mListener;

        
    View mRootView;
    public static HueFragment03 newInstance(String ip) {
        HueFragment03 fragment = new HueFragment03();
        mIp = ip;
        
        return fragment;
    }
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, 
            final Bundle savedInstanceState) {

        mLogger.entering(this, "onCreateView");

        try {
            mRootView = inflater.inflate(R.layout.hue_fragment_03, container, false);

            final Button btnSearchBridge = (Button) mRootView.findViewById(R.id.btnSearchLight);
            btnSearchBridge.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(final View v) {
                    try {
                        searchLight();
                        
                    } catch (Exception e) {
                        mLogger.warning(this, "onClick", "", e);
                    }
                    
                }
            });
            
        } catch (Exception e) {
            mLogger.warning(this, "onCreateView", "", e);
        }
        
        mLogger.exiting(this, "onCreateView");
        
        return mRootView;
    }

    /**
     * ライト検索.
     */
    private void searchLight() {
       
        boolean isSyncResponse = true;

        mLogger.entering(this, "searchLights");

        HueControlBrige cb = new HueControlBrige();
        
        PHBridge bridge = cb.getBridgeSync(mIp);

        if (bridge == null) {

            mLogger.exiting(this, "searchLights ブリッジが見つかりません");

            return;
            
        } else {
            
            mListener = new PHLightListenerNewLights();
            bridge.findNewLights(mListener);
            
            isSyncResponse = false;
        }
        
        ImageView img = (ImageView) mRootView.findViewById(R.id.iv04);
        img.setImageResource(R.drawable.img04);
        
        //showToast("30秒間ライトを検索します");
        
        mLogger.exiting(this, "searchLights", isSyncResponse);
        
    }
    /**
     * NEWリスナークラス.
     */
    class PHLightListenerNewLights implements PHLightListener {

        /**
         * NEWリスナークラス.
         */
        public PHLightListenerNewLights() {
            super();

            mLogger.entering(this, "SampleLightListener");

            mLogger.exiting(this, "SampleLightListener");
        }

        /**
         * The callback method for error.
         * 
         * @param code the error code 
         * @param message the error message 
         */
        @Override
        public void onError(final int code, final String message) {

            HueControl.destroyPHHueSDK(); //Hueとのやりとりを停止

            String msg = "ライトの検索でエラーが発生しました hue:code = " + Integer.toString(code) + "  message = " + message;
            mLogger.fine(this, "onError", msg);

            showToast("ライトの検索でエラーが発生しました。");
//            Toast.makeText(getActivity(), "ライトの検索でエラーが発生しました。", Toast.LENGTH_SHORT).show();
            
        }

        /**
         * Called to convey success without any data from bridge.
         */
        @Override
        public void onSuccess() {

            mLogger.fine(this, "onSuccess", "");

        }

        /**
         * The light headers received callback.
         * 
         * @param lightHeaders the array list of {@link PHBridgeResource}
         * 
         */
        @Override
        public void onReceivingLights(final List<PHBridgeResource> lightHeaders) {

            mLogger.fine(this, "onReceivingLights", lightHeaders);

            // 検索開始をOKとする

        }

        /**
         * Indicates search is complete.
         */
        @Override
        public void onSearchComplete() {

            mLogger.fine(this, "onSearchComplete", "");

        }

        @Override
        public void onStateUpdate(Map<String, String> arg0, List<PHHueError> arg1) {
          
            mLogger.fine(this, "onStateUpdate", arg1);
            // 検索開始でOKを返しているのでここでは返さない

            
        }

        @Override
        public void onReceivingLightDetails(PHLight arg0) {
            
        }
    }
    
    /**
     * アプリケーションContext.
     * @return アクティビティ
     */
    protected Context getApplicationContext() {
        return getActivity();

    }
    
    /**
     * Toastの表示.
     * @param msg 
     */
    protected void showToast(final String msg) {


        Context con = getApplicationContext();

        if (con != null) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

                    } catch (Exception e) {
                        e.toString();
                    }
                }
            });

        }

    }
    
    // 画面終了
    @Override
    public void onDestroy() {

        mLogger.entering(this, "onDestroy");
        try {
            
            if(mListener != null){
                
                
            }

            HueControl.destroyPHHueSDK();

        } catch (Exception e) {
            mLogger.warning(this, "onDestroy", "", e);

        }

        mLogger.exiting(this, "onDestroy");

        super.onDestroy();

    }

}
