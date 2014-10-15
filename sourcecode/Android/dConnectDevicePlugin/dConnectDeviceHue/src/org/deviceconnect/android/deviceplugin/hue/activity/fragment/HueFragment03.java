/*
HueFargment03
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
 */

package org.deviceconnect.android.deviceplugin.hue.activity.fragment;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.deviceconnect.android.deviceplugin.hue.R;
import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;

/**
 * Hue設定画面(3)フラグメント.
 */
public class HueFragment03 extends Fragment implements OnClickListener {

    /** TAG. */
    private static final String TAG = "HUE_FRAGMENT";

    /** アクセスポイント. */
    private static PHAccessPoint mAccessPoint;

    /** ライトの登録. */
    private static Button mButtonRegister;

    /** HueSDK. */
    private static PHHueSDK mPhHueSDK;

    /** ProgressView. */
    private View mProgressView;

    /** Activity. */
    private static Activity mActivity;

    /** LightHeader. */
    private List<PHBridgeResource> mLightHeaders;
    
    public static HueFragment03 newInstance(final PHAccessPoint accessPoint) {
        HueFragment03 fragment = new HueFragment03();

        mAccessPoint = accessPoint;

        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        View mRootView = inflater.inflate(R.layout.hue_fragment_03, container, false);

        if (mRootView != null) {
            mButtonRegister = (Button) mRootView.findViewById(R.id.btnSearchLight);
            mButtonRegister.setOnClickListener(this);

            mProgressView = mRootView.findViewById(R.id.progress_light_search);
        }

        mActivity = this.getActivity();

        return mRootView;
    }

    /**
     * ライト検索.
     */
    private void searchLight() {

        mPhHueSDK = PHHueSDK.create();
        PHBridge bridge = mPhHueSDK.getSelectedBridge();
        
        if (bridge == null) {
            mProgressView.setVisibility(View.GONE);
            return;
        } else {
            mProgressView.setVisibility(View.VISIBLE);
            bridge.findNewLights(mListener);
        }
    }

    /**
     * Lightリスナー.
     */
    PHLightListener mListener = new PHLightListener() {

        @Override
        public void onError(final int code, final String message) {
        }

        @Override
        public void onSuccess() {
        }

        @Override
        public void onReceivingLights(final List<PHBridgeResource> lightHeaders) {
            mLightHeaders = lightHeaders;
        }

        @Override
        public void onSearchComplete() {
            
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    
                    mProgressView.setVisibility(View.GONE);
                    mProgressView.invalidate();
                    
                    int lightSize = -1;
                    try {
                        lightSize = mLightHeaders.size();
                    } catch(Exception e){
                        lightSize = 0;
                    }
                    
                    try {
                        String message = getString(R.string.frag03_light_result1);
                        message += lightSize + getString(R.string.frag03_light_result2);
                        Toast.makeText(mActivity, message, Toast.LENGTH_LONG).show();
                    } catch (Exception e){}
                }
            });
        }

        @Override
        public void onStateUpdate(Map<String, String> arg0, List<PHHueError> arg1) {
        }

        @Override
        public void onReceivingLightDetails(PHLight arg0) {
        }
    };

    @Override
    public void onDestroy() {

        PHBridge bridge = mPhHueSDK.getSelectedBridge();
        if (bridge != null) {
            
            if (mPhHueSDK.isHeartbeatEnabled(bridge)) {
                mPhHueSDK.disableHeartbeat(bridge);
            }
            
            mPhHueSDK.disconnect(bridge);
            super.onDestroy();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.equals(mButtonRegister)) {
            searchLight();
        }
    }
}
