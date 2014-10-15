/*
 VideoPlayer.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */

package org.deviceconnect.android.deviceplugin.host.video;

import org.deviceconnect.android.deviceplugin.host.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.widget.MediaController;
import android.widget.VideoView;

/**
 * Video Player.
 * @author NTT DOCOMO, INC.
 */
public class VideoPlayer extends Activity implements OnCompletionListener {
    
    /** Debug Tag. */
    private static final String TAG = "HOST";
    
    /** VideoView. */
    private VideoView mVideoView;
    
    /** URI. */
    private Uri mUri;
    
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // タイトルを非表示
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.video_player);
        mVideoView = (VideoView) findViewById(R.id.videoView);
        
        // 再生するVideoのURI
        Intent mIntent = this.getIntent();
        mUri = mIntent.getData();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // ReceiverをRegister
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(VideoConst.SEND_HOSTDP_TO_VIDEOPLAYER);
        registerReceiver(mReceiver, mIntentFilter);
        
        MediaController mMediaController = new MediaController(this);
        mMediaController.setAnchorView(mVideoView);
        
        mVideoView.setMediaController(mMediaController);
        mVideoView.setKeepScreenOn(true);
        mVideoView.setVideoURI(mUri);
        mVideoView.requestFocus();
        mVideoView.setOnCompletionListener(this);
        
        mVideoView.start();
    }


    @Override
    protected void onPause() {
        super.onPause();

        // ReceiverをUnregister
        unregisterReceiver(mReceiver);
    }

    /**
     * 受信.
     * 受信用のReceiver
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        
        @Override
        public void onReceive(final Context context, final Intent intent) {

            if (intent.getAction().equals(VideoConst.SEND_HOSTDP_TO_VIDEOPLAYER)) {
                String mVideoAction = intent.getStringExtra(VideoConst.EXTRA_NAME);
              
                
                if (mVideoAction.equals(VideoConst.EXTRA_VALUE_VIDEO_PLAYER_PLAY)) {

                    mVideoView.start();
                } else if (mVideoAction.equals(VideoConst.EXTRA_VALUE_VIDEO_PLAYER_STOP)) {
                    mVideoView.stopPlayback();
                    finish();
                } else if (mVideoAction.equals(VideoConst.EXTRA_VALUE_VIDEO_PLAYER_PAUSE)) {

                    mVideoView.pause();
                } else if (mVideoAction.equals(VideoConst.EXTRA_VALUE_VIDEO_PLAYER_RESUME)) {
   
                    mVideoView.resume();
                    mVideoView.start();
                } else if (mVideoAction.equals(VideoConst.EXTRA_VALUE_VIDEO_PLAYER_SEEK)) {
                    int pos = intent.getIntExtra("pos", -1);
                    mVideoView.seekTo(pos);

                }
                
            }
        }
    };

    @Override
    public void onCompletion(final MediaPlayer mp) {
        finish();
    }
}

