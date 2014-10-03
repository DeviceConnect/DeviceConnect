/*
 VideoRecorder.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */

package com.nttdocomo.android.dconnect.deviceplugin.host.video;

import java.io.File;
import java.util.Date;

import com.nttdocomo.android.dconnect.deviceplugin.host.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;

/**
 * Video Recorder.
 * @author NTT DOCOMO, INC.
 */
public class VideoRecorder extends Activity implements SurfaceHolder.Callback {
    
    /** MediaRecorder. */
    private static MediaRecorder mRecorder;
    
    /** 録画中かどうか. */
    private static boolean isRecording;
    
    /** SurfaceHolder. */
    private static SurfaceHolder mHolder;
    
    /** Activity. */
    private static Activity mActivity;
    
    /** Debug Tag. */
    private static final String TAG = "PluginHost";
    
    /** Format Type. */
    private static final String FORMAT_TYPE = ".3gp";
    
    /** Frame Rate. */
    private static final int FRAME_RATE = 30;
    
    /** Camera. */
    private static Camera mCamera;
    
    /** MediaRecorder. */
    private static MediaRecorder mMediaRecorder;
    
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // タイトルを非表示
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Layoutを設定
        setContentView(R.layout.video_main);
        
        
        // Surface Viewを設定
        SurfaceView mSurfaceView = (SurfaceView) findViewById(R.id.surface_view);
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        // holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
        
        mActivity = this;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        
        
               
        // レシーバーを登録
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(VideoConst.SEND_HOSTDP_TO_VIDEO);
        registerReceiver(myReceiver, mFilter);
    }


    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaRecorder();       
        releaseCamera();
        
        if(mHolder != null){
            mHolder = null;
        }
        // レシーバーを削除
        unregisterReceiver(myReceiver);
    }
    
    
   

    private void releaseMediaRecorder(){
        if (mRecorder != null) {
            mRecorder.reset();   // clear recorder configuration
            mRecorder.release(); // release the recorder object
            mRecorder = null;
            mCamera.lock();           // lock camera for later use
        }
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }
    /**
     * Surface Viewが生成された時に呼ばれる.
     * 
     * @param holder フォルダ
     */
    public void surfaceCreated(final SurfaceHolder holder) {  
    }
    
    /**
     * Surface Viewに変化があった際に呼ばれる.
     * 
     * @param holder SurfaceHolder
     * @param format フォーマット
     * @param width 幅
     * @param height 高さ 
     */
    public void surfaceChanged(final SurfaceHolder holder, final int format, final int width, final int height) {
        mHolder = holder;
        mCamera = getCameraInstance();
        mRecorder = new MediaRecorder(); 
        mCamera.unlock();
        mRecorder.setCamera(mCamera);
        mRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT); 
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP); 
        mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
        File dir = Environment.getExternalStorageDirectory();
        
        Date mDate = new Date();
        String mFileName = "dconnect" + mDate.getTime() + FORMAT_TYPE;  
        File mFile = new File(dir, mFileName);
        mRecorder.setOutputFile(mFile.toString()); 
        mRecorder.setVideoFrameRate(FRAME_RATE); 
        mRecorder.setVideoSize(VideoConst.VIDEO_WIDTH, VideoConst.VIDEO_HEIGHT); 
        mRecorder.setPreviewDisplay(mHolder.getSurface()); 

        try {
            mRecorder.prepare(); 
        } catch (Exception e) {

        }
        mRecorder.start(); 
        isRecording = true;
    }

    /**
     * Serface Viewが破棄された際に呼ばれる.
     * 
     * @param holder フォルダ
     */
    public void surfaceDestroyed(final SurfaceHolder holder) {
        //
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
    
    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            
            finish();  
        }
        return true;
    }
    
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
    
    
    /**
     * 受信用のReceiver.
     */
    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        
        @Override
        public void onReceive(final Context context, final Intent intent) {
            

            if (intent.getAction().equals(VideoConst.SEND_HOSTDP_TO_VIDEO)) {
                String videoAction = intent.getStringExtra(VideoConst.EXTRA_NAME);
                if (videoAction.equals(VideoConst.EXTRA_VALUE_VIDEO_RECORD_STOP)) {
                   
                    finish();  
                } else if (videoAction.equals(VideoConst.EXTRA_VALUE_VIDEO_RECORD_PAUSE)) {
                    //mRecorder.stop();
                    //mRecorder.release(); 
                } else if (videoAction.equals(VideoConst.EXTRA_VALUE_VIDEO_RECORD_RESUME)) {
                    //mRecorder.start();           
                }                
            }
        }
    };
}
