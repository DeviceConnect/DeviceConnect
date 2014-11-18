/*
 VideoConst.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.host.video;

/**
 * 映像録画Broadcastで使用する定数を定義.
 * 
 * [映像録画開始リクエストBroadcast]
 * ・ホストデバイスプラグインのHostMediaStreamingRecordingProfileから送信される。
 * ・action: SEND_HOSTDP_TO_VIDEO
 * ・putExtra(EXTRA_NAME, EXTRA_NAME_VIDEO_RECORD_START);
 *
 * [映像録画停止リクエストBroadcast]
 * ・ホストデバイスプラグインのHostMediaStreamingRecordingProfileへレスポンスを返す。
 * ・action: SEND_CAMERA_TO_VIDEO
 * ・putExtra(EXTRA_NAME, EXTRA_NAME_VIDEO_RECORD_STOP);
 *
 * [映像録画一時停止リクエストBroadcast]
 * ・ホストデバイスプラグインのHostMediaStreamingRecordingProfileへレスポンスを返す。
 * ・action: SEND_CAMERA_TO_VIDEO
 * ・putExtra(EXTRA_NAME, EXTRA_NAME_VIDEO_RECORD_PAUSE);
 *
 * @author NTT DOCOMO, INC.
 */
public final class VideoConst {
    /**
     * Constructor.
     */
    private VideoConst() {

    }

    /** Video起動のAction名. */
    public static final String SEND_HOSTDP_TO_VIDEO = "org.deviceconnect.android.intent.action.SEND_HOSTDP_TO_VIDEO";

    /** Video起動のAction名. */
    public static final String SEND_VIDEO_TO_HOSTDP = "org.deviceconnect.android.intent.action.SEND_VIDEO_TO_HOSTDP";

    /** Video操作のコマンド名. */
    public static final String EXTRA_NAME = "command";

    /** 録画開始. */
    public static final String EXTRA_VALUE_VIDEO_RECORD_START = "start";

    /** 録画停止. */
    public static final String EXTRA_VALUE_VIDEO_RECORD_STOP = "stop";

    /** 録画撮影一時停止. */
    public static final String EXTRA_VALUE_VIDEO_RECORD_PAUSE = "pause";

    /** 録画撮影再開. */
    public static final String EXTRA_VALUE_VIDEO_RECORD_RESUME = "resume";

    /** Video起動のAction名. */
    public static final String SEND_HOSTDP_TO_VIDEOPLAYER =
            "org.deviceconnect.android.intent.action.SEND_HOSTDP_TO_VIDEOPLAYER";

    /** Video起動のAction名. */
    public static final String SEND_VIDEOPLAYER_TO_HOSTDP =
            "org.deviceconnect.android.intent.action.SEND_VIDEOPLAYER_TO_HOSTDP";

    /** 再生開始. */
    public static final String EXTRA_VALUE_VIDEO_PLAYER_PLAY = "play";

    /** 再生停止. */
    public static final String EXTRA_VALUE_VIDEO_PLAYER_STOP = "stop";

    /** 再生時停止. */
    public static final String EXTRA_VALUE_VIDEO_PLAYER_PAUSE = "pause";

    /** 再生再開. */
    public static final String EXTRA_VALUE_VIDEO_PLAYER_RESUME = "resume";

    /** 再生場所指定. */
    public static final String EXTRA_VALUE_VIDEO_PLAYER_SEEK = "seek";

    /** 撮影するサイズ(横). */
    public static final int VIDEO_WIDTH = 320;

    /** 撮影するサイズ(縦). */
    public static final int VIDEO_HEIGHT = 240;

}
