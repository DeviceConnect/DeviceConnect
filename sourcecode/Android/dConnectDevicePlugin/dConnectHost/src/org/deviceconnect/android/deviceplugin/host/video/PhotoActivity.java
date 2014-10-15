/*
 PhotoActivity.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */

package org.deviceconnect.android.deviceplugin.host.video;

import java.io.IOException;
import java.util.List;

import org.deviceconnect.android.deviceplugin.host.HostDeviceService;
import org.deviceconnect.android.deviceplugin.host.IHostMediaStreamRecordingService;
import org.deviceconnect.android.deviceplugin.host.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;

/**
 * Video Recorder.
 * 
 * @author NTT DOCOMO, INC.
 */
public class PhotoActivity extends Activity implements SurfaceHolder.Callback, Camera.PreviewCallback {

    /** Activity. */
    private static Activity mActivity;

    /** Debug Tag. */
    private static final String TAG = "HOST";

    /** Camera. */
    private static Camera mCamera;

    /** SurfaceView. */
    private static SurfaceView mSurfaceView;

    /** プロセス間通信でつなぐService. */
    private static IHostMediaStreamRecordingService mService;
    
    /** SurfaceViewのHolder. */
    private static SurfaceHolder mHolder;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // タイトルを非表示
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Cameraのインスタンスを取得
        mCamera = getCameraInstance();

        // Layoutを設定
        setContentView(R.layout.video_main);

        // SurfaceViewを取得
        mSurfaceView = (SurfaceView) findViewById(R.id.surface_view);

        // SurfaceView用 Holderを設定
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
        // Activityを取得
        mActivity = this;

        // ServiceにBinds
        Intent mIntent = new Intent(this, HostDeviceService.class);
        mIntent.setAction("camera");
        mActivity.bindService(mIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        
        // レシーバーを登録
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(PhotoConst.SEND_HOSTDP_TO_PHOTO);
        registerReceiver(myReceiver, mFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // レシーバーを削除
        unregisterReceiver(myReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Serviceをunbind
        mActivity.unbindService(mServiceConnection);

        if (mService != null) {
            mService = null;
        }
    }

    /**
     * Cameraのインスタンスを取得
     * 
     * @return cameraのインスタンス
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {

        }
        return c;
    }

    /**
     * SurfaceViewが生成された時に呼ばれる.
     * 
     * @param holder
     */
    public void surfaceCreated(final SurfaceHolder holder) {
        try {
            Camera.Parameters parameters = mCamera.getParameters();

            List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();
            List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();

            Camera.Size previewSize = previewSizes.get(previewSizes.size() - 1);
            Camera.Size pictureSize = pictureSizes.get(0);

            parameters.setPreviewSize(previewSize.width, previewSize.height);
            parameters.setPictureSize(pictureSize.width, pictureSize.height);

            mCamera.setPreviewDisplay(holder);
            mCamera.setPreviewCallback(this);
            mCamera.startPreview();
        } catch (Exception e) {
        }
    }

    /**
     * SurfaceViewの状態が変わった時に呼び出される.
     * 
     * @param holder フォルダ
     * @param format フォーマット
     * @param width 幅
     * @param height 高さ
     * 
     */
    public void surfaceChanged(final SurfaceHolder holder, final int format, final int width, final int height) {

        // Holderがない場合は、ここで終了
        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // 前に作られたPreviewを停止する
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            
        }

        // Previewの作成
        try {
            Camera.Parameters parameters = mCamera.getParameters();

            List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();
            List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();

            Camera.Size previewSize = previewSizes.get(previewSizes.size() - 1);
            Camera.Size pictureSize = pictureSizes.get(0);

            parameters.setPreviewSize(previewSize.width, previewSize.height);
            parameters.setPictureSize(pictureSize.width, pictureSize.height);

            mCamera.setPreviewDisplay(holder);
            mCamera.setPreviewCallback(this);
            mCamera.startPreview();
        } catch (Exception e) {
        }

    }

    /**
     * SurfaceViewが破棄された際に呼ばれる.
     * 
     * @param holder サーフェイスHolder
     */
    public void surfaceDestroyed(final SurfaceHolder holder) {

        if (mCamera != null) {
            try {
                mCamera.setPreviewCallback(null);
                mCamera.setPreviewDisplay(null);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            mCamera.stopPreview();
            mCamera.release();

            mCamera = null;
        }
    }

    @Override
    public void onPreviewFrame(final byte[] data, final Camera camera) {
        // Log.i(TAG, "data:" + data.length);
        mCamera.setPreviewCallback(null);

        int format = mCamera.getParameters().getPreviewFormat();
        int width = mCamera.getParameters().getPreviewSize().width;
        int height = mCamera.getParameters().getPreviewSize().height;
        // mCamera.stopPreview();
        if (mService == null) {
            // ServiceにBinds
            Intent mIntent = new Intent(this, HostDeviceService.class);
            mIntent.setAction("camera");
            mActivity.bindService(mIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        }

        try {
            mService.sendPreviewData(data, format, width, height);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        mCamera.setPreviewCallback(this);
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            finish();
        }
        return true;
    }

    /**
     * Boradcast Receiver.
     */
    private BroadcastReceiver myReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(final Context context, final Intent intent) {

            if (intent.getAction().equals(PhotoConst.SEND_HOSTDP_TO_PHOTO)) {
                String photoAction = intent.getStringExtra(PhotoConst.EXTRA_NAME);

                if (photoAction.equals(PhotoConst.EXTRA_VALUE_EXIT)) {

                    finish();

                }
            }
        }
    };

    /**
     * HostDeviceServiceとデータをやり取りするためのAIDL.
     */
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(final ComponentName name, final IBinder service) {
            // TODO Auto-generated method stub
            mService = IHostMediaStreamRecordingService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(final ComponentName name) {
            // TODO Auto-generated method stub
            mService = null;
        }
    };

    /**
     * キーが押された時のイベント.
     * 
     * @param keyCode キーコード　
     * @param event キーイベント
     * 
     * @return True
     */
    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            finish();
            return true;
        } else {
            return false;
        }
    }

}
