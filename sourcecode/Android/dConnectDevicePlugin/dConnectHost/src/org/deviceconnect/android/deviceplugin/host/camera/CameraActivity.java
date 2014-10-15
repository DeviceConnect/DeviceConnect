/*
 CameraActivity.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.host.camera;

import org.deviceconnect.android.deviceplugin.host.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;

/**
 * d-Connect連携カメラアプリ.
 * 
 * @author NTT DOCOMO, INC.
 */
public class CameraActivity extends Activity {

	
	/** ハンドラー. */
	private Handler mHandler = null;
	
	/** プレビュー画面. */
	private Preview mPreview;
	
	/** 使用するカメラのインスタンス. */
    private Camera mCamera;
    
    /** カメラの個数. */
    private int numberOfCameras;
    
    /** カメラの固定. */
    private int cameraCurrentlyLocked;
    
    /** デフォルトのカメラID. */
    private int defaultCameraId;
	
    @Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
		 
		mHandler = new Handler();
		
        // Create a RelativeLayout container that will hold a SurfaceView,
        // and set it as the content of our activity.
        mPreview = new Preview(this);
        setContentView(mPreview);

        // Find the total number of cameras available
        numberOfCameras = Camera.getNumberOfCameras();

        // Find the ID of the default camera
        CameraInfo cameraInfo = new CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
                defaultCameraId = i;
            }
        }
        
        /* BroadcastReceiver登録 */
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(CameraConst.SEND_HOSTDP_TO_CAMERA);
		registerReceiver(mReceiver, intentFilter);
	}

	@Override
	protected void onResume() {
        super.onResume();

        // Open the default i.e. the first rear facing camera.
        mCamera = Camera.open();
        cameraCurrentlyLocked = defaultCameraId;
        mPreview.setCamera(mCamera);
        
        Intent intent = getIntent();
        if (intent != null) {
        	String action = intent.getAction();
			if (CameraConst.SEND_HOSTDP_TO_CAMERA.equals(action)) {
				String requestid = intent.getStringExtra(CameraConst.EXTRA_REQUESTID);
				takePictureRunnable(requestid);
			}
        }
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		// Because the Camera object is a shared resource, it's very
        // important to release it when the activity is paused.
        if (mCamera != null) {
            mPreview.setCamera(null);
            mCamera.release();
            mCamera = null;
        }
		unregisterReceiver(mReceiver);

	}
	
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate our menu which can gather user input for switching camera
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.camera_menu, menu);
        return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.switch_cam:
            // check for availability of multiple cameras
            if (numberOfCameras == 1) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(this.getString(R.string.camera_alert))
                       .setNeutralButton("Close", null);
                AlertDialog alert = builder.create();
                alert.show();
                return true;
            }

            // OK, we have multiple cameras.
            // Release this camera -> cameraCurrentlyLocked
            if (mCamera != null) {
                mCamera.stopPreview();
                mPreview.setCamera(null);
                mCamera.release();
                mCamera = null;
            }

            // Acquire the next camera and request Preview to reconfigure
            // parameters.
            mCamera = Camera.open((cameraCurrentlyLocked + 1) % numberOfCameras);
            cameraCurrentlyLocked = (cameraCurrentlyLocked + 1)
                    % numberOfCameras;
            mPreview.switchCamera(mCamera);
            // Start the preview
            mCamera.startPreview();
            return true;
        case R.id.item_shutter:
            mPreview.takePicture(null);
        	return true;
        case R.id.item_zoom_in:
            mPreview.zoomIn(null);
        	return true;
        case R.id.item_zoom_out:
            mPreview.zoomOut(null);
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
	}
	
	public void checkCloseApplication() {
		Intent intent = getIntent();
		if (intent != null) {
			String action = intent.getAction();
			if (CameraConst.SEND_HOSTDP_TO_CAMERA.equals(action)) {
				finish();
			}
		}
	}
	
	/**
	 * BroadcastReceiverをインナークラスで定義.
	 */
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
	    @Override
		public void onReceive(final Context context, final Intent intent) {
			String action = intent.getAction();
			
			
			if (action.compareTo(CameraConst.SEND_HOSTDP_TO_CAMERA) == 0) {
	        	String name = intent.getStringExtra(CameraConst.EXTRA_NAME);
	        	if (CameraConst.EXTRA_NAME_SHUTTER.compareTo(name) == 0) {
		        	/* シャッター操作依頼通知を受信 */
					String requestid = intent.getStringExtra(CameraConst.EXTRA_REQUESTID);
					/* 写真撮影 */
	        		takePictureRunnable(requestid);
	        	} else if (CameraConst.EXTRA_NAME_ZOOMIN.compareTo(name) == 0) {
		        	/* ズームイン操作依頼通知を受信 */
					String requestid = intent.getStringExtra(CameraConst.EXTRA_REQUESTID);
					/* ズームイン */
	        		zoomInRunnable(requestid);
	        	} else if (CameraConst.EXTRA_NAME_ZOOMOUT.compareTo(name) == 0) {
		        	/* ズームアウト操作依頼通知を受信 */
					String requestid = intent.getStringExtra(CameraConst.EXTRA_REQUESTID);
					/* ズームアウト */
	        		zoomOutRunnable(requestid);
	        	}
			}
		}
	};
	
	/**
	 * 写真撮影用Runnable実行する.
	 * @param requestid リクエストID
	 */
	private void takePictureRunnable(final String requestid) {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mPreview.takePicture(requestid);
			}
		}, 2000); 
	}
	
	/**
	 * ズームイン用Runnable実行.
	 * @param requestid リクエストID
	 */
	private void zoomInRunnable(final String requestid) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mPreview.zoomIn(requestid);
			}
		}); 
	}
	
	/**
	 * ズームアウト用Runnable実行.
	 * @param requestid リクエストID
	 */
	private void zoomOutRunnable(final String requestid) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mPreview.zoomOut(requestid);
			}
		}); 
	}
}
