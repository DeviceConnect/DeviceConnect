/*
 CameraConst.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.deviceplugin.host.camera;

/**
 * カメラ関連Broadcastで使用する定数を定義.
 * 
 * [写真撮影リクエストBroadcast]
 * ・ホストデバイスプラグインのHostMediaStreamingRecordingProfileから送信される。
 * ・action: SEND_HOSTDP_TO_CAMERA
 * ・putExtra(EXTRA_NAME, EXTRA_NAME_SHUTTER);
 * ・putExtra(EXTRA_REQUESTID, <リクエストID>);
 *
 * [写真撮影レスポンスBroadcast]
 * ・ホストデバイスプラグインのHostMediaStreamingRecordingProfileへレスポンスを返す。
 * ・action: SEND_CAMERA_TO_HOSTDP
 * ・putExtra(EXTRA_NAME, EXTRA_NAME_SHUTTER);
 * ・putExtra(EXTRA_REQUESTID, <リクエストID>);
 * ・putExtra(EXTRA_PICTURE_URI, <画像URI>);
 *
 *
 * [ズームインリクエストBroadcast]
 * ・ホストデバイスプラグインのHostMediaStreamingRecordingProfileから送信される。
 * ・action: SEND_HOSTDP_TO_CAMERA
 * ・putExtra(EXTRA_NAME, EXTRA_NAME_ZOOMIN);
 * ・putExtra(EXTRA_REQUESTID, <リクエストID>);
 *
 * [ズームインレスポンスBroadcast]
 * ・ホストデバイスプラグインのHostMediaStreamingRecordingProfileへレスポンスを返す。
 * ・action: SEND_CAMERA_TO_HOSTDP
 * ・putExtra(EXTRA_NAME, EXTRA_NAME_ZOOMIN);
 * ・putExtra(EXTRA_REQUESTID, <リクエストID>);
 *
 *
 * [ズームアウトリクエストBroadcast]
 * ・ホストデバイスプラグインのHostMediaStreamingRecordingProfileから送信される。
 * ・action: SEND_HOSTDP_TO_CAMERA
 * ・putExtra(EXTRA_NAME, EXTRA_NAME_ZOOMOUT);
 * ・putExtra(EXTRA_REQUESTID, <リクエストID>);
 *
 * [ズームアウトレスポンスBroadcast]
 * ・ホストデバイスプラグインのHostMediaStreamingRecordingProfileへレスポンスを返す。
 * ・action: SEND_CAMERA_TO_HOSTDP
 * ・putExtra(EXTRA_NAME, EXTRA_NAME_ZOOMOUT);
 * ・putExtra(EXTRA_REQUESTID, <リクエストID>);
 *
 * @author NTT DOCOMO, INC.
 */
public class CameraConst {

    /** カメラへのアクション. */
	public static final String SEND_HOSTDP_TO_CAMERA = "com.nttdocomo.android.dconnect.SEND_HOSTDP_TO_CAMERA";
	
	/** カメラからのアクション. */
	public static final String SEND_CAMERA_TO_HOSTDP = "com.nttdocomo.android.dconnect.SEND_CAMERA_TO_HOSTDP";

	/** コマンド名. */
	public static final String EXTRA_NAME = "command";
	
	/** シャッター処理. */
	public static final String EXTRA_NAME_SHUTTER = "shutter";
	
	/** Zoom in. */
	public static final String EXTRA_NAME_ZOOMIN = "zoomin";
	
	/** Zoom out. */
	public static final String EXTRA_NAME_ZOOMOUT = "zoomout";
	
	/** 写真のURI. */
	public static final String EXTRA_PICTURE_URI = "picture_uri";
	
	/** リクエストID. */
	public static final String EXTRA_REQUESTID = "request_id";
	
}
