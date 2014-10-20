/*
 * Copyright 2013 Sony Corporation
 */

package com.example.sony.cameraremote;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.sony.cameraremote.utils.SimpleLiveviewSlicer;
import com.example.sony.cameraremote.utils.SimpleLiveviewSlicer.Payload;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * SimpleLiveviewSurfaceView.
 */
public class SimpleLiveviewSurfaceView extends SurfaceView implements SurfaceHolder.Callback {


    /** Remote APIインスタンス. */
    private SimpleRemoteApi mRemoteApi;
    /** View生成・破壊フラグ. */
    private boolean mWhileFetching;
    /** jpegデータ. */
    private final BlockingQueue<byte[]> mJpegQueue = new ArrayBlockingQueue<byte[]>(2);
    /** ミュート可否フラグ. */
    private final boolean mInMutableAvailable = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    /** ビューの内容描画用スレッド. */
    private Thread mDrawerThread;
    /** 横幅. */
    private int mPreviousWidth = 0;
    /** 縦幅. */
    private int mPreviousHeight = 0;
    /** フレームペイントインスタンス. */
    private final Paint mFramePaint;

    /**
     * Contractor.
     * 
     * @param context コンテクスト
     */
    public SimpleLiveviewSurfaceView(final Context context) {
        super(context);
        getHolder().addCallback(this);
        mFramePaint = new Paint();
        mFramePaint.setDither(true);
    }

    /**
     * Contractor.
     * 
     * @param context コンテクスト
     * @param attrs アトリビュート
     */
    public SimpleLiveviewSurfaceView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        mFramePaint = new Paint();
        mFramePaint.setDither(true);
    }

    /**
     * Contractor.
     * 
     * @param context コンテクスト
     * @param attrs アトリビュート
     * @param defStyle デフォルトスタイル
     */
    public SimpleLiveviewSurfaceView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        getHolder().addCallback(this);
        mFramePaint = new Paint();
        mFramePaint.setDither(true);
    }

    @Override
    public void surfaceChanged(final SurfaceHolder holder, final int format, final int width, final int height) {
        // do nothing.
    }

    @Override
    public void surfaceCreated(final SurfaceHolder holder) {
        // do nothing.
    }

    @Override
    public void surfaceDestroyed(final SurfaceHolder holder) {
        mWhileFetching = false;
    }

    /**
     * Bind a Remote API object to communicate with Camera device. Need to call
     * this method before calling start() method.
     * 
     * @param remoteApi リモートAPIインスタンス
     */
    public void bindRemoteApi(final SimpleRemoteApi remoteApi) {
        mRemoteApi = remoteApi;
    }

    /**
     * Start retrieving and drawing liveview frame data by new threads.
     * 
     * @return true if the starting is completed successfully, false otherwise.
     * @see SimpleLiveviewSurfaceView#bindRemoteApi(SimpleRemoteApi)
     */
    public boolean start() {
        if (mRemoteApi == null) {
            throw new IllegalStateException("RemoteApi is not set.");
        }
        if (mWhileFetching) {
            return false;
        }

        mWhileFetching = true;

        // A thread for retrieving liveview data from server.
        Thread mThread = new Thread() {
            @Override
            public void run() {
                SimpleLiveviewSlicer slicer = null;

                try {
                    // Prepare for connecting.
                    JSONObject replyJson = null;

                    replyJson = mRemoteApi.startLiveview();
                    if (!isErrorReply(replyJson)) {
                        JSONArray resultsObj = replyJson.getJSONArray("result");
                        String liveviewUrl = null;
                        if (1 <= resultsObj.length()) {
                            // Obtain liveview URL from the result.
                            liveviewUrl = resultsObj.getString(0);
                        }
                        if (liveviewUrl != null) {
                            // Create Slicer to open the stream and parse it.
                            slicer = new SimpleLiveviewSlicer();
                            slicer.open(liveviewUrl);
                        }
                    }

                    if (slicer == null) {
                        mWhileFetching = false;
                        return;
                    }

                    while (mWhileFetching) {
                        final Payload payload = slicer.nextPayload();
                        if (payload == null) { // never occurs
                            continue;
                        }

                        if (mJpegQueue.size() == 2) {
                            mJpegQueue.remove();
                        }
                        mJpegQueue.add(payload.getJpegData());
                    }
                } catch (IOException e) {
                  //Exceptionを受けるだけなので処理は行わない
                } catch (JSONException e) {
                  //Exceptionを受けるだけなので処理は行わない
                } finally {
                    // Finalize
                    try {
                        if (slicer != null) {
                            slicer.close();
                        }
                        mRemoteApi.stopLiveview();
                    } catch (IOException e) {
                      //Exceptionを受けるだけなので処理は行わない
                    }

                    if (mDrawerThread != null) {
                        mDrawerThread.interrupt();
                    }

                    mJpegQueue.clear();
                    mWhileFetching = false;
                }
            }
        };
        mThread.start();

        // A thread for drawing liveview frame fetched by above thread.
        mDrawerThread = new Thread() {
            @Override
            public void run() {
                Bitmap frameBitmap = null;

                BitmapFactory.Options factoryOptions = new BitmapFactory.Options();
                factoryOptions.inSampleSize = 1;
                if (mInMutableAvailable) {
                    initInBitmap(factoryOptions);
                }

                while (mWhileFetching) {
                    try {
                        byte[] jpegData = mJpegQueue.take();
                        frameBitmap = BitmapFactory.decodeByteArray(jpegData, 0, jpegData.length, factoryOptions);
                    } catch (IllegalArgumentException e) {
                        if (mInMutableAvailable) {
                            clearInBitmap(factoryOptions);
                        }
                        continue;
                    } catch (InterruptedException e) {
                        break;
                    }

                    if (mInMutableAvailable) {
                        setInBitmap(factoryOptions, frameBitmap);
                    }
                    drawFrame(frameBitmap);
                }

                if (frameBitmap != null) {
                    frameBitmap.recycle();
                }
                mWhileFetching = false;
            }
        };
        mDrawerThread.start();
        return true;
    }

    /**
     * Request to stop retrieving and drawing liveview data.
     */
    public void stop() {
        mWhileFetching = false;
    }

    /**
     * Check to see whether start() is already called.
     * 
     * @return true if start() is already called, false otherwise.
     */
    public boolean isStarted() {
        return mWhileFetching;
    }

    /**
     * Target API version.
     * 
     * @param options オプション
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void initInBitmap(final BitmapFactory.Options options) {
        options.inBitmap = null;
        options.inMutable = true;
    }

    /**
     * Target API version.
     * 
     * @param options オプション
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void clearInBitmap(final BitmapFactory.Options options) {
        if (options.inBitmap != null) {
            options.inBitmap.recycle();
            options.inBitmap = null;
        }
    }

    /**
     * Target API version.
     * 
     * @param options オプション
     * @param bitmap ビットマップ
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setInBitmap(final BitmapFactory.Options options, final Bitmap bitmap) {
        options.inBitmap = bitmap;
    }

    /**
     * Draw frame bitmap onto a canvas.
     * 
     * @param frame フレーム
     */
    private void drawFrame(final Bitmap frame) {
        if (frame.getWidth() != mPreviousWidth || frame.getHeight() != mPreviousHeight) {
            onDetectedFrameSizeChanged(frame.getWidth(), frame.getHeight());
            return;
        }
        Canvas canvas = getHolder().lockCanvas();
        if (canvas == null) {
            return;
        }
        int w = frame.getWidth();
        int h = frame.getHeight();
        Rect src = new Rect(0, 0, w, h);

        float by = Math.min((float) getWidth() / w, (float) getHeight() / h);
        int offsetX = (getWidth() - (int) (w * by)) / 2;
        int offsetY = (getHeight() - (int) (h * by)) / 2;
        Rect dst = new Rect(offsetX, offsetY, getWidth() - offsetX, getHeight() - offsetY);
        canvas.drawBitmap(frame, src, dst, mFramePaint);
        getHolder().unlockCanvasAndPost(canvas);
    }

    /**
     * Called when the width or height of liveview frame image is changed.
     * 
     * @param width width
     * @param height height
     */
    private void onDetectedFrameSizeChanged(final int width, final int height) {
        mPreviousWidth = width;
        mPreviousHeight = height;
        drawBlackFrame();
        drawBlackFrame();
        drawBlackFrame(); // delete triple buffers
    }

    /**
     * Draw black screen.
     */
    private void drawBlackFrame() {
        Canvas canvas = getHolder().lockCanvas();
        if (canvas == null) {
            return;
        }

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawRect(new Rect(0, 0, getWidth(), getHeight()), paint);
        getHolder().unlockCanvasAndPost(canvas);
    }

    /**
     * Parse JSON and returns a error code.
     * 
     * @param replyJson 送信用JSON
     * @return hasError
     */
    private static boolean isErrorReply(final JSONObject replyJson) {
        boolean hasError = (replyJson != null && replyJson.has("error"));
        return hasError;
    }
}
