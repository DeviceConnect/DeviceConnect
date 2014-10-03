/*
 Preview.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.deviceplugin.host.camera;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nttdocomo.android.dconnect.deviceplugin.host.R;
import com.nttdocomo.android.dconnect.provider.FileManager;

/**
 * カメラのプレビューを表示するクラス.
 *
 * @author NTT DOCOMO, INC.
 */
class Preview extends ViewGroup implements SurfaceHolder.Callback, PictureCallback {
    /** デバック用タグ. */
    public static final String LOG_TAG = "DConnectCamera:Preview";

    /** Debug Tag. */
    private static final String TAG = "PluginHost";

    /** プレビューを表示するSurfaceView. */
    private SurfaceView mSurfaceView;
    /** SurfaceViewを一時的に保持するホルダー. */
    private SurfaceHolder mHolder;
    /** プレビューのサイズ. */
    private Size mPreviewSize;
    /** サポートしているプレビューのサイズ. */
    private List<Size> mSupportedPreviewSizes;
    /** カメラのインスタンス. */
    private Camera mCamera;

    /** ファイル管理クラス. */
    private FileManager mFileMgr;

    /** 日付のフォーマット. */
    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyyMMdd_kkmmss", Locale.JAPAN);

    /** ファイル名に付けるプレフィックス. */
    private static final String FILENAME_PREFIX = "android_camera_";

    /** ファイルの拡張子. */
    private static final String FILE_EXTENSION = ".png";
    /**
     * ホストデバイスプラグインから渡されたリクエストID.<br>
     * - Broadcastで指示された場合は設定する。<br>
     * - アプリ内ならの指示ならnullを設定する。<br>
     */
    private String mRequestid = null;

    /**
     * コンストラクタ.
     * 
     * @param context このクラスが属するコンテキスト
     */
    Preview(final Context context) {
        super(context);

        // ファイル管理クラスの作成
        mFileMgr = new FileManager(this.getContext());

        mSurfaceView = new SurfaceView(context);
        addView(mSurfaceView);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    /**
     * カメラのインスタンスを設定する.
     * 
     * @param camera カメラのインスタンス
     */
    public void setCamera(final Camera camera) {
        mCamera = camera;
        if (mCamera != null) {
            mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
            requestLayout();
        }
    }

    /**
     * カメラのインスタンスを切り替えます.
     * 
     * @param camera 切り替えるカメラのインスタンス
     */
    public void switchCamera(final Camera camera) {
        setCamera(camera);
        try {
            camera.setPreviewDisplay(mHolder);
        } catch (IOException exception) {
            Log.e(LOG_TAG, "IOException caused by setPreviewDisplay()", exception);
        }
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
        requestLayout();

        camera.setParameters(parameters);
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        // We purposely disregard child measurements because act as a
        // wrapper to a SurfaceView that centers the camera preview instead
        // of stretching it.
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);

        if (mSupportedPreviewSizes != null) {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
        }
    }

    @Override
    protected void onLayout(final boolean changed, final int l, final int t, final int r, final int b) {
        if (changed && getChildCount() > 0) {
            final View child = getChildAt(0);

            final int width = r - l;
            final int height = b - t;

            int previewWidth = width;
            int previewHeight = height;
            if (mPreviewSize != null) {
                previewWidth = mPreviewSize.width;
                previewHeight = mPreviewSize.height;
            }

            // Center the child SurfaceView within the parent.
            if (width * previewHeight > height * previewWidth) {
                final int scaledChildWidth = previewWidth * height / previewHeight;
                child.layout((width - scaledChildWidth) / 2, 0, (width + scaledChildWidth) / 2, height);
            } else {
                final int scaledChildHeight = previewHeight * width / previewWidth;
                child.layout(0, (height - scaledChildHeight) / 2, width, (height + scaledChildHeight) / 2);
            }
        }
    }

    @Override
    public void surfaceCreated(final SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
        try {
            if (mCamera != null) {
                mCamera.setPreviewDisplay(holder);
            }
        } catch (IOException exception) {
            Log.e(LOG_TAG, "IOException caused by setPreviewDisplay()", exception);
        }
    }

    @Override
    public void surfaceDestroyed(final SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }

    /**
     * 最適なプレビューサイズを取得する. 指定されたサイズに最適なものがない場合にはnullを返却する。
     * 
     * @param sizes プレビューサイズ一覧
     * @param w 横幅
     * @param h 縦幅
     * @return 最適なプレビューサイズ
     */
    private Size getOptimalPreviewSize(final List<Size> sizes, final int w, final int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) {
            return null;
        }

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) {
                continue;
            }
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    @Override
    public void surfaceChanged(final SurfaceHolder holder, final int format, final int w, final int h) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
        requestLayout();

        mCamera.setParameters(parameters);
        mCamera.startPreview();
    }

    /**
     * 写真撮影後に実行される処理.
     */
    @Override
    public void onPictureTaken(final byte[] data, final Camera c) {
        Log.d(LOG_TAG, "onPictureTaken() - mRequestid:" + mRequestid);
        // Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length,
        // null);
        // String pictureUri =
        // MediaStore.Images.Media.insertImage(getContext().getContentResolver(),
        // bmp, "", null);

        /*
         * String pictureUri = ""; try { pictureUri = ((HostDeviceService)
         * getContext()).savePhoto(data); } catch (IOException e) { // TODO
         * Auto-generated catch block e.printStackTrace(); }
         */
        Log.i(TAG, "@@@@@  savePhoto");
        String fileName = FILENAME_PREFIX + mSimpleDateFormat.format(new Date()) + FILE_EXTENSION;
        try {
            mFileMgr.saveFile(fileName, data);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.i(TAG, "fileName:" + fileName);
        String pictureUri = "content://com.nttdocomo.android.dconnect.deviceplugin.host.provider/" + fileName;

        mCamera.startPreview();

        /* Toast表示 */
        String debugToast = getResources().getString(R.string.shutter) + " requestid:" + mRequestid + " pictureUri:"
                + pictureUri;
        Toast.makeText(getContext(), debugToast, Toast.LENGTH_SHORT).show();

        /* リクエストIDが登録されていたら、撮影完了後にホストデバイスプラグインへ撮影完了通知を送信する */
        if (mRequestid != null) {
            Context context = getContext();
            Intent intent = new Intent(CameraConst.SEND_CAMERA_TO_HOSTDP);
            intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            intent.putExtra(CameraConst.EXTRA_NAME, CameraConst.EXTRA_NAME_SHUTTER);
            intent.putExtra(CameraConst.EXTRA_REQUESTID, mRequestid);
            intent.putExtra(CameraConst.EXTRA_PICTURE_URI, pictureUri);
            context.sendBroadcast(intent);
            Log.d(LOG_TAG, "sendBroadcast() - action:" + CameraConst.SEND_CAMERA_TO_HOSTDP + " name:"
                    + CameraConst.EXTRA_NAME_SHUTTER + " mRequestid:" + mRequestid + " pictureUri:" + pictureUri);
        }

        // 写真をとったので、Activityを終了する
        ((CameraActivity) getContext()).checkCloseApplication();
    }

    /**
     * 写真撮影を開始する.
     * 
     * @param requestid リクエストID(Broadcastで指示された場合は設定する。アプリ内ならの指示ならnullを設定する)
     */
    public void takePicture(final String requestid) {
        Log.d(LOG_TAG, "takePicture() start - requestid:" + requestid);

        mRequestid = requestid;

        mCamera.takePicture(mShutterCallback, null, this);

        Toast.makeText(getContext(), R.string.shutter, Toast.LENGTH_SHORT).show();

        Log.d(LOG_TAG, "takePicture() end");
    }

    /**
     * ズームイン処理を行う.
     * 
     * @param requestid リクエストID(Broadcastで指示された場合は設定する。アプリ内ならの指示ならnullを設定する)
     */
    public void zoomIn(final String requestid) {
        Log.d(LOG_TAG, "zoomIn() start - requestid:" + requestid);

        mRequestid = requestid;

        /* ズームイン処理 */
        Camera.Parameters parameters = mCamera.getParameters();
        int nowZoom = parameters.getZoom();
        if (nowZoom < parameters.getMaxZoom()) {
            parameters.setZoom(nowZoom + 1);
        }
        mCamera.setParameters(parameters);

        /* Toast表示 */
        String debugToast = getResources().getString(R.string.zoomin) + " requestid:" + mRequestid;
        Toast.makeText(getContext(), debugToast, Toast.LENGTH_SHORT).show();

        /* リクエストIDが登録されていたら、撮影完了後にホストデバイスプラグインへズームイン完了通知を送信する */
        if (mRequestid != null) {
            Context context = getContext();
            Intent intent = new Intent(CameraConst.SEND_CAMERA_TO_HOSTDP);
            intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            intent.putExtra(CameraConst.EXTRA_NAME, CameraConst.EXTRA_NAME_ZOOMIN);
            intent.putExtra(CameraConst.EXTRA_REQUESTID, mRequestid);
            context.sendBroadcast(intent);
            Log.d(LOG_TAG, "sendBroadcast() - action:" + CameraConst.SEND_CAMERA_TO_HOSTDP + " name:"
                    + CameraConst.EXTRA_NAME_ZOOMIN + " mRequestid:" + mRequestid);
        }

        Log.d(LOG_TAG, "zoomIn() end");
    }

    /**
     * ズームアウト処理を行う.
     * 
     * @param requestid リクエストID(Broadcastで指示された場合は設定する。アプリ内ならの指示ならnullを設定する)
     */
    public void zoomOut(final String requestid) {
        Log.d(LOG_TAG, "zoomOut() start - requestid:" + requestid);

        mRequestid = requestid;

        /* ズームアウト処理 */
        Camera.Parameters parameters = mCamera.getParameters();
        int nowZoom = parameters.getZoom();
        if (nowZoom > 0) {
            parameters.setZoom(nowZoom - 1);
        }
        mCamera.setParameters(parameters);

        /* Toast表示 */
        String debugToast = getResources().getString(R.string.zoomout) + " requestid:" + mRequestid;
        Toast.makeText(getContext(), debugToast, Toast.LENGTH_SHORT).show();

        /* リクエストIDが登録されていたら、撮影完了後にホストデバイスプラグインへズームアウト完了通知を送信する */
        if (mRequestid != null) {
            Context context = getContext();
            Intent intent = new Intent(CameraConst.SEND_CAMERA_TO_HOSTDP);
            intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            intent.putExtra(CameraConst.EXTRA_NAME, CameraConst.EXTRA_NAME_ZOOMOUT);
            intent.putExtra(CameraConst.EXTRA_REQUESTID, mRequestid);
            context.sendBroadcast(intent);
            Log.d(LOG_TAG, "sendBroadcast() - action:" + CameraConst.SEND_CAMERA_TO_HOSTDP + " name:"
                    + CameraConst.EXTRA_NAME_ZOOMOUT + " mRequestid:" + mRequestid);
        }

        Log.d(LOG_TAG, "zoomOut() end");
    }

    /**
     * シャッターコールバック.
     * <p>
     * - シャッター音を鳴らすために使用する。
     */
    private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            // NOP
        }
    };
}
