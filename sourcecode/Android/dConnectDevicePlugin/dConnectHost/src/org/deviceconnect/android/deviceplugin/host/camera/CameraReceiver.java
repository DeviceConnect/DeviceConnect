/*
 CameraReceiver.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */

package org.deviceconnect.android.deviceplugin.host.camera;

import java.util.HashMap;
import java.util.logging.Logger;

import org.deviceconnect.android.deviceplugin.host.profile.HostMediaStreamingRecordingProfile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * d-Connect Camera からの通知を受信するBroadcastReceiver.
 * @author NTT DOCOMO, INC.
 */
public class CameraReceiver extends BroadcastReceiver {

    /** Debug Tag. */
    private static final String TAG = "HOST";

	@Override
	public void onReceive(final Context context, final Intent intent) {
      
		// カメラから撮影通知を受信したらリクエストマップにメディアIDを格納する
		String action = intent.getAction();
		if (action.compareTo(CameraConst.SEND_CAMERA_TO_HOSTDP) == 0) {
			
			String name = intent.getStringExtra(CameraConst.EXTRA_NAME);
			
			// シャッター撮影完了通知を受信
			if (name.compareTo(CameraConst.EXTRA_NAME_SHUTTER) == 0) {
				
				String requestid = intent.getStringExtra(CameraConst.EXTRA_REQUESTID);
				String pictureUri = intent.getStringExtra(CameraConst.EXTRA_PICTURE_URI);
				
			    
				HashMap<String, String> requestMap = HostMediaStreamingRecordingProfile.getRequestMap();
				requestMap.put(requestid, pictureUri);
			} else if (name.compareTo(CameraConst.EXTRA_NAME_ZOOMIN) == 0) {
				
				String requestid = intent.getStringExtra(CameraConst.EXTRA_REQUESTID);

				HashMap<String, String> requestMap = HostMediaStreamingRecordingProfile.getRequestMap();
				requestMap.put(requestid, "zoomin");
			} else if (name.compareTo(CameraConst.EXTRA_NAME_ZOOMOUT) == 0) {
				
				String requestid = intent.getStringExtra(CameraConst.EXTRA_REQUESTID);
				
				HashMap<String, String> requestMap = HostMediaStreamingRecordingProfile.getRequestMap();
				requestMap.put(requestid, "zoomout");
			}
		}
     }
}
