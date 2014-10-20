/*
 AudioRecorder.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */

package org.deviceconnect.android.deviceplugin.host.audio;

import java.io.File;
import java.util.Date;

import org.deviceconnect.android.deviceplugin.host.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Video;
import android.view.MotionEvent;
import android.view.Window;

/**
 * AudioRecorder.
 * 
 * @author NTT DOCOMO, INC.
 */
public class AudioRecorder extends Activity {

    /** Debug Tag. */
    private static final String TAG = "HOST";

    /** MediaRecoder. */
    private MediaRecorder mMediaRecorder;

    /** ファイル名. */
    private String mFileName;

    /** フォルダURI. */
    private File mFile;

    /** フォーマット名. */
    private final static String FORMAT_TYPE = ".3gp";

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.audio_main);

        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

        File mDir = Environment.getExternalStorageDirectory();
        Date mDate = new Date();
        mFileName = "audio" + mDate.getTime() + FORMAT_TYPE;
        mFile = new File(mDir, mFileName);
        mMediaRecorder.setOutputFile(mFile.toString());

        try {
            mMediaRecorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMediaRecorder.start();
    }

    @Override
    public void onResume() {
        super.onResume();

        // 受信を開始
        IntentFilter filter = new IntentFilter();
        filter.addAction(AudioConst.SEND_HOSTDP_TO_AUDIO);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // 受信を停止.
        unregisterReceiver(mReceiver);

        // Contents Providerに登録.
        ContentResolver mContentResolver = this.getApplicationContext().getContentResolver();
        ContentValues mValues = new ContentValues();
        mValues.put(Video.Media.TITLE, mFileName);
        mValues.put(Video.Media.DISPLAY_NAME, mFileName);
        mValues.put(Video.Media.ARTIST, "DeviceConnect");
        mValues.put(Video.Media.MIME_TYPE, FORMAT_TYPE);
        mValues.put(Video.Media.DATA, mFile.toString());
        mContentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, mValues);
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaRecorder.release();

            Intent mIntent = new Intent();
            setResult(RESULT_OK, mIntent);
            finish();

        }
        return true;
    }

    /**
     * 受信用Receiver.
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(final Context context, final Intent intent) {

            if (intent.getAction().equals((AudioConst.SEND_HOSTDP_TO_AUDIO))) {
                String videoAction = intent.getStringExtra(AudioConst.EXTRA_NAME);
                if (videoAction.equals(AudioConst.EXTRA_NAME_AUDIO_RECORD_START)) {

                    // finish();
                } else if (videoAction.equals(AudioConst.EXTRA_NAME_AUDIO_RECORD_PAUSE)) {

                    // finish();
                } else if (videoAction.equals(AudioConst.EXTRA_NAME_AUDIO_RECORD_RESUME)) {

                    // finish();
                } else if (videoAction.equals(AudioConst.EXTRA_NAME_AUDIO_RECORD_STOP)) {
                    mMediaRecorder.stop();
                    mMediaRecorder.reset();
                    mMediaRecorder.release();
                    finish();
                }

            }
        }
    };
}
