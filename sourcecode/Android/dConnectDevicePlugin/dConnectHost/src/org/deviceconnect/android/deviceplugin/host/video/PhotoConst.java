/*
 PhotoConst.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.host.video;



/**
 * カメラ関連Broadcastで使用する定数を定義.
 *
 * @author NTT DOCOMO, INC.
 */
public class PhotoConst {

    /** カメラへのアクション. */
	public static final String SEND_HOSTDP_TO_PHOTO = "org.deviceconnect.android.intent.action.SEND_HOSTDP_TO_PHOTO";
	
	/** カメラからのアクション. */
	public static final String SEND_PHOTO_TO_HOSTDP = "org.deviceconnect.android.intent.action.SEND_PHOTO_TO_HOSTDP";

	/** コマンド名. */
	public static final String EXTRA_NAME = "command";
	
	/** シャッター処理. */
	public static final String EXTRA_VALUE_SHUTTER = "shutter";
	
	/** Zoom in. */
	public static final String EXTRA_VALUE_ZOOMIN = "zoomin";
	
	/** Zoom out. */
	public static final String EXTRA_VALUE_ZOOMOUT = "zoomout";
	
	/** 写真のURI. */
	public static final String EXTRA_VALUE_URI = "picture_uri";
	
	/** リクエストID. */
	public static final String EXTRA_VALUE_REQUESTID = "request_id";
	
	/** リクエストID. */
    public static final String EXTRA_VALUE_EXIT = "exit";
	
}
