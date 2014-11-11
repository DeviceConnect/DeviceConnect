/*
HueFargment02
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
 */

package org.deviceconnect.android.deviceplugin.hue.activity.fragment;

import java.util.List;

import org.deviceconnect.android.deviceplugin.hue.HueConstants;
import org.deviceconnect.android.deviceplugin.hue.R;

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
import android.widget.Toast;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHMessageType;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueParsingError;

/**
 * Hue設定画面(2)フラグメント.
 * 
 */
public class HueFragment02 extends Fragment implements OnClickListener {

    /** アクセスポイント. */
    private static PHAccessPoint mAccessPoint;

    /** HueSDKオブジェクト. */
    private static PHHueSDK mPhHueSDK;

    /** ステータスを表示するTextView. */
    private static TextView mTextViewStatus;

    /** Howtoを表示するTextView. */
    private static TextView mTextViewHowto;

    /** Button. */
    private static Button mButton;

    /** ステータス. */
    private static HueState mHueStatus = HueState.INIT;

    /** ImageView. */
    private static ImageView mImageView;

    /** 前回IPアドレスがスキャンできたかのフラグ. */
    private boolean lastSearchWasIPScan = false;
    
    /** 次のハンドラーの時間. */
    private static long mNextTime;
    
    /** ハンドラーの再呼び出しの命令. */
    private static final int INVALIDATE = 1;
    
    /** ハンドラー用のCounter. */
    private static int count = 0;
    
    /** Activity. */
    private Activity mActivity;
    
    /**
     * Hue接続状態.
     */
    private enum HueState {
        INIT, // 未認証
        NOCONNECT, // 未接続
        AUTHENTICATE_FAILD, // 認証失敗
        AUTHENTICATE_SUCCESS// 認証済み
    };

    public static HueFragment02 newInstance(final PHAccessPoint accessPoint) {
        HueFragment02 fragment = new HueFragment02();

        mAccessPoint = accessPoint;

        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        View mRootView = inflater.inflate(R.layout.hue_fragment_02, container, false);

        mActivity = this.getActivity();
        
        if (mRootView != null) {

            // Macアドレスを画面に反映.
            TextView mMacTextView = (TextView) mRootView.findViewById(R.id.text_mac);
            mMacTextView.setText(mAccessPoint.getMacAddress());

            // IPアドレスを画面に反映.
            TextView mIpTextView = (TextView) mRootView.findViewById(R.id.text_ip);
            mIpTextView.setText(mAccessPoint.getIpAddress());

            // 現在の状態を表示.
            mTextViewStatus = (TextView) mRootView.findViewById(R.id.textStatus);

            // 作業方法を表示.
            mTextViewHowto = (TextView) mRootView.findViewById(R.id.textHowto);

            // ボタン.
            mButton = (Button) mRootView.findViewById(R.id.btnBridgeTouroku);
            mButton.setOnClickListener(this);
            mButton.setVisibility(View.GONE);

            // 画像を表示.
            mImageView = (ImageView) mRootView.findViewById(R.id.iv01);
        }

        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        // ステータスを初期状態(INIT)に設定.
        mHueStatus = HueState.INIT;
        mTextViewStatus.setText(R.string.frag02_init);
        mTextViewHowto.setText(R.string.frag02_init_howto);
        
        // Hueのインスタンスを取得.
        mPhHueSDK = PHHueSDK.create();
        
        // HueブリッジからのCallbackを受け取るためのリスナーを登録.
        mPhHueSDK.getNotificationManager().registerSDKListener(mListener);

        // User名を追加.
        mAccessPoint.setUsername(HueConstants.USERNAME);
        
        // アクセスポイントに接続.
        if (!mPhHueSDK.isAccessPointConnected(mAccessPoint)) {
            mPhHueSDK.connect(mAccessPoint);
        } else {
            mHueStatus = HueState.AUTHENTICATE_SUCCESS;
        }
        
        // アニメーションの開始.
        mHandler.sendEmptyMessageDelayed(INVALIDATE, 1000);
    }
    
    /**
     * タイマーハンドラー
     */
    private final static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            
            if (msg.what == INVALIDATE) {
                if (mHueStatus == HueState.INIT) {
                    
                    // 画像をアニメーション.
                    if (count == 0) {
                        mImageView.setImageResource(R.drawable.img01);
                        count = 1;
                    } else {
                        mImageView.setImageResource(R.drawable.img02);
                        count = 0;
                    }
                    
                    msg = obtainMessage(INVALIDATE);
                    long current = SystemClock.uptimeMillis();

                    if (mNextTime < current) {
                        // 1000ms周期でタイマーイベントが発生
                        mNextTime = current + 1000;
                    }
                    sendMessageAtTime(msg, mNextTime);

                    // 1000ms周期でタイマーイベントが発生
                    mNextTime += 1000;

                } else if (mHueStatus == HueState.AUTHENTICATE_SUCCESS) {
                    
                    mTextViewStatus.setText(R.string.frag02_authsuccess);
                    mTextViewHowto.setText(R.string.frag02_authsuccess_howto);
                    mImageView.setImageResource(R.drawable.img05);
                    mButton.setText(R.string.frag02_authsuccess_btn);
                    
                    mTextViewStatus.invalidate();
                    mTextViewHowto.invalidate();
                    mImageView.invalidate();
                    mButton.setVisibility(View.VISIBLE);

                } else if (mHueStatus == HueState.AUTHENTICATE_FAILD) {

                    mTextViewStatus.setText(R.string.frag02_failed);
                    mTextViewHowto.setText("");
                    mImageView.setImageResource(R.drawable.img01);
                    mButton.setText(R.string.frag02_retry_btn);

                    mTextViewHowto.invalidate();
                    mImageView.invalidate();
                    mTextViewStatus.invalidate();
                    mButton.setVisibility(View.VISIBLE);

                } else if (mHueStatus == HueState.NOCONNECT) {

                    mTextViewStatus.setText(R.string.frag02_failed);
                    mTextViewHowto.setText("");
                    mImageView.setImageResource(R.drawable.img01);
                    mButton.setText(R.string.frag02_retry_btn);

                    mTextViewHowto.invalidate();
                    mImageView.invalidate();
                    mTextViewStatus.invalidate();
                    mButton.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    @Override
    public void onClick(final View v) {

        if (mHueStatus == HueState.AUTHENTICATE_SUCCESS) {
            FragmentManager manager = getFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();

            transaction.setCustomAnimations(R.anim.fragment_slide_right_enter,
                    R.anim.fragment_slide_left_exit,
                    R.anim.fragment_slide_left_enter, 
                    R.anim.fragment_slide_right_exit);
            
            transaction.replace(R.id.fragment_frame, new HueFragment03());

            transaction.commit();

        } else {
            mButton.setVisibility(View.INVISIBLE);

            // アクセスポイントに接続.
            if (!mPhHueSDK.isAccessPointConnected(mAccessPoint)) {
                mPhHueSDK.connect(mAccessPoint);
            } else {
                mHueStatus = HueState.AUTHENTICATE_SUCCESS;
            }
            
            // アニメーションの開始.
            mHandler.sendEmptyMessageDelayed(INVALIDATE, 1000);
        }

    }

    // 画面終了
    @Override
    public void onDestroy() {

        if (mListener != null) {
            mPhHueSDK.getNotificationManager().unregisterSDKListener(mListener);
        }
        mPhHueSDK.disableAllHeartbeat();

        super.onDestroy();

    }

    // hueブリッジのNotificationを受け取るためのリスナー.
    public PHSDKListener mListener = new PHSDKListener() {

        @Override
        public void onBridgeConnected(final PHBridge b) {
            mHueStatus = HueState.AUTHENTICATE_SUCCESS;
            mHandler.sendEmptyMessage(INVALIDATE);
            
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String message = getString(R.string.frag02_connected);
                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                    } catch(Exception e){}
                }
            });
            
            // 接続.
            mPhHueSDK.setSelectedBridge(b);
            mPhHueSDK.enableHeartbeat(b, PHHueSDK.HB_INTERVAL);
            mPhHueSDK.getLastHeartbeat().put(b.getResourceCache().getBridgeConfiguration().getIpAddress(),
                    System.currentTimeMillis());
        }

        @Override
        public void onAuthenticationRequired(final PHAccessPoint accessPoint) {
            mHueStatus = HueState.INIT;

            // 認証を実施.
            mPhHueSDK.startPushlinkAuthentication(accessPoint);

            // アニメーションの開始.
            mHandler.sendEmptyMessage(INVALIDATE);
        }

        @Override
        public void onAccessPointsFound(final List<PHAccessPoint> accessPoint) {
        }

        @Override
        public void onCacheUpdated(List<Integer> arg0, PHBridge bridge) {
        }

        @Override
        public void onConnectionLost(PHAccessPoint accessPoint) {
            
            if (!mPhHueSDK.getDisconnectedAccessPoint().contains(accessPoint)) {
                mPhHueSDK.getDisconnectedAccessPoint().add(accessPoint);
            }
        }

        @Override
        public void onConnectionResumed(PHBridge bridge) {
            
            mPhHueSDK.getLastHeartbeat().put(bridge.getResourceCache().getBridgeConfiguration().getIpAddress(),  System.currentTimeMillis());
            for (int i = 0; i < mPhHueSDK.getDisconnectedAccessPoint().size(); i++) {

                if (mPhHueSDK.getDisconnectedAccessPoint().get(i).getIpAddress().equals(bridge.getResourceCache().getBridgeConfiguration().getIpAddress())) {
                    mPhHueSDK.getDisconnectedAccessPoint().remove(i);
                }
            }
            
        }

        @Override
        public void onError(int code, String message) {
            if (code == PHMessageType.BRIDGE_NOT_FOUND) {
                if (!lastSearchWasIPScan) {
                    mPhHueSDK = PHHueSDK.getInstance();
                    PHBridgeSearchManager sm = (PHBridgeSearchManager) mPhHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
                    sm.search(false, false, true);               
                    lastSearchWasIPScan = true;
                } 
            }
        }

        @Override
        public void onParsingErrors(List<PHHueParsingError> arg0) {
        }
    };

}
