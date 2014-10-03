/*
 AudioConst.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */

package com.nttdocomo.android.dconnect.deviceplugin.host.audio;

/**
 * 音声録音Broadcastで使用する定数を定義.
 * 
 * [音声録音開始リクエストBroadcast]
 * ・ホストデバイスプラグインのHostMediaStreamingRecordingProfileから送信される。
 * ・action: SEND_HOSTDP_TO_AUDIO
 * ・putExtra(EXTRA_NAME, EXTRA_NAME_AUDIO_RECORD_START);
 *
 * [音声録音停止リクエストBroadcast]
 * ・ホストデバイスプラグインのHostMediaStreamingRecordingProfileへレスポンスを返す。
 * ・action: SEND_CAMERA_TO_AUDIO
 * ・putExtra(EXTRA_NAME, EXTRA_NAME_AUDIO_RECORD_STOP);
 *
 * [音声録音一時停止リクエストBroadcast]
 * ・ホストデバイスプラグインのHostMediaStreamingRecordingProfileへレスポンスを返す。
 * ・action: SEND_CAMERA_TO_AUDIO
 * ・putExtra(EXTRA_NAME, EXTRA_NAME_AUDIO_RECORD_PAUSE);
 *
 * @author NTT DOCOMO, INC.
 */
public class AudioConst {
    
    /** Audio呼び出しアクション. */
	public static final String SEND_HOSTDP_TO_AUDIO = "com.nttdocomo.android.dconnect.SEND_HOSTDP_TO_AUDIO";

	/** コマンド名. */
	public static final String EXTRA_NAME = "command";
	
	/** 再生. */
	public static final String EXTRA_NAME_AUDIO_RECORD_START = "start";
	
	/** 停止. */
	public static final String EXTRA_NAME_AUDIO_RECORD_STOP = "stop";
	
	/** 一時停止. */
	public static final String EXTRA_NAME_AUDIO_RECORD_PAUSE = "pause";
	
	/** Resume. */
	public static final String EXTRA_NAME_AUDIO_RECORD_RESUME = "resume";
	
}
