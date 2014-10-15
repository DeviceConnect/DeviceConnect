/*
SonyCameraDeviceService
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
 */

package org.deviceconnect.android.deviceplugin.sonycamera;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import org.deviceconnect.android.deviceplugin.sonycamera.profile.SonyCameraMediaStreamRecordingProfile;
import org.deviceconnect.android.deviceplugin.sonycamera.profile.SonyCameraNetworkServiceDiscoveryProfile;
import org.deviceconnect.android.deviceplugin.sonycamera.profile.SonyCameraSystemProfile;
import org.deviceconnect.android.deviceplugin.sonycamera.profile.SonyCameraZoomProfile;
import org.deviceconnect.android.deviceplugin.sonycamera.sdk.ServerDevice;
import org.deviceconnect.android.deviceplugin.sonycamera.sdk.SimpleCameraEventObserver;
import org.deviceconnect.android.deviceplugin.sonycamera.sdk.SimpleRemoteApi;
import org.deviceconnect.android.deviceplugin.sonycamera.sdk.SimpleSsdpClient;
import org.deviceconnect.android.deviceplugin.sonycamera.utils.DConnectUtil;
import org.deviceconnect.android.deviceplugin.sonycamera.utils.SimpleLiveviewSlicer;
import org.deviceconnect.android.deviceplugin.sonycamera.utils.SimpleLiveviewSlicer.Payload;
import org.deviceconnect.android.deviceplugin.sonycamera.utils.UserSettings;
import org.deviceconnect.android.event.Event;
import org.deviceconnect.android.event.EventManager;
import org.deviceconnect.android.event.cache.db.DBCacheController;
import org.deviceconnect.android.message.DConnectMessageService;
import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.android.profile.MediaStreamRecordingProfile;
import org.deviceconnect.android.profile.NetworkServiceDiscoveryProfile;
import org.deviceconnect.android.profile.SystemProfile;
import org.deviceconnect.android.provider.FileManager;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.message.intent.message.IntentDConnectMessage;
import org.deviceconnect.profile.MediaStreamRecordingProfileConstants;
import org.deviceconnect.profile.NetworkServiceDiscoveryProfileConstants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

/**
 * SonyCameraデバイスプラグイン.
 */
public class SonyCameraDeviceService extends DConnectMessageService {
    /** ファイル名に付けるプレフィックス. */
    private static final String FILENAME_PREFIX = "sony_camera_";
    /** ファイルの拡張子. */
    private static final String FILE_EXTENSION = ".png";
    /** デバイス名. */
    private static final String DEVICE_NAME = "Sony Camera";
    /** デバイスID. */
    private static final String DEVICE_ID = "sony_camera";
    /** カメラステータス. */
    private static String mRecorderState = "";
    /** 接続中カメラが利用できるRemote API一覧. */
    private static String mAvailableApiList = "";
    /** プレビュースレッド調整用スリープ時間設定. */
    private static final int SLEEP_MSEC = 200;
    /** プレビューの最大値を定義. */
    private static final int MAX_PREVIEW = 10;
    /** リトライ回数. */
    private static final int MAX_RETRY_COUNT = 3;
    /** 待機時間. */
    private static final int WAIT_TIME = 100;

    /** 動画撮影モード. */
    private static final String SONY_CAMERA_SHOOT_MODE_MOVIE = "movie";
    /** 静止画撮影モード. */
    private static final String SONY_CAMERA_SHOOT_MODE_PIC = "still";

    /** ロガー. */
    private Logger mLogger = Logger.getLogger("deviceconnect.dplugin");

    /** SonyCameraとの接続管理クライアント. */
    private SimpleSsdpClient mSsdpClient;
    /** SonyCameraのリモートAPI. */
    private SimpleRemoteApi mRemoteApi;
    /** SonyCameraのイベント監視クラス. */
    private SimpleCameraEventObserver mEventObserver;

    /** SonyCameraの設定. */
    private UserSettings mSettings;

    /** 日付のフォーマット. */
    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyyMMdd_kkmmss", Locale.JAPAN);

    /** プレビューフラグ. */
    private boolean mWhileFetching = false;

    /** ファイル管理クラス. */
    private FileManager mFileMgr;

    /** リトライ回数. */
    private int mRetryCount;

    /** 撮影画像サイズ20Mの場合のピクセル数. */
    private static final int PIXELS_20_M = 20000000;
    /** 撮影画像サイズ18Mの場合のピクセル数. */
    private static final int PIXELS_18_M = 18000000;
    /** 撮影画像サイズ17Mの場合のピクセル数. */
    private static final int PIXELS_17_M = 17000000;
    /** 撮影画像サイズ13Mの場合のピクセル数. */
    private static final int PIXELS_13_M = 13000000;
    /** 撮影画像サイズ7.5Mの場合のピクセル数. */
    private static final int PIXELS_7_5_M = 7500000;
    /** 撮影画像サイズ25Mの場合のピクセル数. */
    private static final int PIXELS_5_M = 5000000;
    /** 撮影画像サイズ4.2Mの場合のピクセル数. */
    private static final int PIXELS_4_2_M = 4200000;
    /** 撮影画像サイズ3.7Mの場合のピクセル数. */
    private static final int PIXELS_3_7_M = 3700000;
    /** 入力タイムゾーンのパース計算用パラメータ. */
    private static final int CUL_PARAM_DATETIME = 100;
    /** 入力タイムゾーンのパース計算用パラメータ. */
    private static final int CUL_PARAM_MINITE = 60;

    /**
     * パーセント表示変換用.
     */
    private static final double VAL_TO_PERCENTAGE = 100.0;

    /**
     * executorインスタンス.
     */
    private ExecutorService mExecutor = Executors.newSingleThreadExecutor();

    @Override
    public void onCreate() {
        mLogger.entering(this.getClass().getName(), "onCreate");
        super.onCreate();

        EventManager.INSTANCE.setController(new DBCacheController(this));

        mSettings = new UserSettings(this);
        mSsdpClient = new SimpleSsdpClient();

        // ファイル管理クラスの作成
        mFileMgr = new FileManager(this);

        addProfile(new SonyCameraMediaStreamRecordingProfile());
        addProfile(new SonyCameraZoomProfile());
        /**
         * SonyCameraデバイスプラグインではSettingsプロファイルは非サポート.
         */
        //addProfile(new SonyCameraSettingsProfile());

        WifiManager wifiMgr = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        if (DConnectUtil.checkSSID(wifiInfo.getSSID())) {
            connectSonyCamera();
        } else {
            deleteSonyCameraSDK();
        }

        mLogger.exiting(this.getClass().getName(), "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mExecutor.shutdown();
        deleteSonyCameraSDK();
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        if (intent == null) {
            return START_STICKY;
        }

        String action = intent.getAction();
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
            if (state == WifiManager.WIFI_STATE_ENABLED) {
                WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
                if (DConnectUtil.checkSSID(wifiInfo.getSSID())) {
                    connectSonyCamera();
                } else {
                    deleteSonyCameraSDK();
                }
            } else if (state == WifiManager.WIFI_STATE_DISABLED) {
                deleteSonyCameraSDK();
            }
            return START_STICKY;
        } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            NetworkInfo ni = cm.getActiveNetworkInfo();
            if (ni != null) {
                NetworkInfo.State state = ni.getState();
                int type = ni.getType();
                if (ni.isConnected() && state == NetworkInfo.State.CONNECTED && type == ConnectivityManager.TYPE_WIFI) {
                    WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
                    if (DConnectUtil.checkSSID(wifiInfo.getSSID())) {
                        connectSonyCamera();
                    } else {
                        deleteSonyCameraSDK();
                    }
                }
            }
            return START_STICKY;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * SonyCameraデバイスの検索を行う. Network Service Deiscovery APIに対応する.
     * 
     * @param request リクエスト
     * @param response レスポンス
     * @return 即座にレスポンスを返す場合はtrue、それ以外はfalse
     */
    public boolean searchSonyCameraDevice(final Intent request, final Intent response) {
        mLogger.entering(this.getClass().getName(), "createSearchResponse");

        WifiManager wifiMgr = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        List<Bundle> services = new ArrayList<Bundle>();
        if (checkDevice() && DConnectUtil.checkSSID(wifiInfo.getSSID())) {
            mLogger.fine("device found: " + checkDevice());

            Bundle service = new Bundle();
            service.putString(NetworkServiceDiscoveryProfile.PARAM_ID, DEVICE_ID);
            service.putString(NetworkServiceDiscoveryProfile.PARAM_NAME, DEVICE_NAME);
            service.putString(NetworkServiceDiscoveryProfile.PARAM_TYPE,
                    NetworkServiceDiscoveryProfile.NetworkType.WIFI.getValue());
            service.putBoolean(NetworkServiceDiscoveryProfile.PARAM_ONLINE, true);
            service.putString(NetworkServiceDiscoveryProfile.PARAM_CONFIG, wifiInfo.getSSID());
            services.add(service);

            // SonyCameraを見つけたので、SSIDを保存しておく
            mSettings.setSSID(wifiInfo.getSSID());
        }

        response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_OK);
        response.putExtra(NetworkServiceDiscoveryProfile.PARAM_SERVICES, services.toArray(new Bundle[services.size()]));

        mLogger.exiting(this.getClass().getName(), "createSearchResponse");
        return true;
    }

    /**
     * フラッシュモード設定メソッド.
     * 
     * @param request リクエスト
     * @param response レスポンス
     * @param deviceId デバイスID
     * @param flashMode フラッシュモード
     * @return response レスポンス
     */
    public boolean onPutFlashMode(final Intent request, final Intent response, final String deviceId,
            final String flashMode) {
        final String[] modeList = {"off", "auto", "on", "slowSync", "rearSync", "wireless"};
        boolean checkResult = false;
        if (deviceId == null || !deviceId.equals(DEVICE_ID)) {
            MessageUtils.setEmptyDeviceIdError(response);
            return true;
        }
        for (String modecheck : modeList) {
            if (flashMode.equals(modecheck)) {
                checkResult = true;
            }
        }
        if (!checkResult) {
            MessageUtils.setInvalidRequestParameterError(response, "Not found " + flashMode + " in FlashModeList.");
            return true;
        }

        response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_OK);
        mExecutor.execute(new Runnable() {

            @Override
            public void run() {

                try {
                    JSONObject replyJson = mRemoteApi.getFlashMode();
                    if (isErrorReply(replyJson)) {
                        sendErrorResponse(request, response);
                    } else {
                        try {
                            JSONArray resultsObj = replyJson.getJSONArray("result");
                            if (resultsObj != null) {
                                sendResponse(request, response);
                            }
                        } catch (JSONException e) {
                            MessageUtils.setInvalidRequestParameterError(response);
                            sendErrorResponse(request, response);
                        }
                    }
                } catch (IOException e) {
                    MessageUtils.setInvalidRequestParameterError(response);
                    sendErrorResponse(request, response);
                }
            }
        });
        return true;
    }

    /**
     * カメラの情報を取得する.
     * 
     * @param request リクエスト
     * @param response レスポンス
     * @param deviceId デバイスID
     * @return 即座にレスポンスする場合はtrue、それ以外はfalse
     * @throws JSONException
     * @throws IOException
     */
    public boolean getMediaRecorder(final Intent request, final Intent response, final String deviceId) {

        if (deviceId == null || !deviceId.equals(DEVICE_ID)) {
            MessageUtils.setEmptyDeviceIdError(response);
            return true;
        }
        if (mAvailableApiList == null) {
            MessageUtils.setUnknownError(response);
            return true;
        }
        if (mAvailableApiList.indexOf("getStillSize") == -1) {
            MessageUtils.setNotSupportActionError(response);
            return true;
        }
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_OK);

                String aspect = "";
                String size = "";
                String cameraState = "";
                try {
                    JSONObject replyJson = mRemoteApi.getStillSize();
                    if (!isErrorReply(replyJson)) {
                        JSONArray resultsObj = replyJson.getJSONArray("result");
                        replyJson = resultsObj.getJSONObject(0);
                        aspect = replyJson.getString("aspect");
                        size = replyJson.getString("size");
                    }

                    replyJson = mRemoteApi.getEvent(false);
                    if (!isErrorReply(replyJson)) {
                        JSONArray resultObject = replyJson.getJSONArray("result");
                        replyJson = resultObject.getJSONObject(1);
                        cameraState = replyJson.getString("cameraStatus");
                    }
                } catch (IOException e) {
                    sendErrorResponse(request, response);
                    return;
                } catch (JSONException e) {
                    sendErrorResponse(request, response);
                    return;
                }

                int width = 0;
                int height = 0;
                int index = aspect.indexOf(":");
                if (index == -1) {
                    sendErrorResponse(request, response);
                } else {
                    width = Integer.valueOf(aspect.substring(0, index));
                    height = Integer.valueOf(aspect.substring(index + 1));
                    int stillSize = (int) pixelValueCalculate(width, height, size);

                    if (cameraState.equals("Error") || cameraState.equals("NotReady")
                            || cameraState.equals("MovieSaving") || cameraState.equals("AudioSaving")
                            || cameraState.equals("StillSaving") || cameraState.equals("IDLE")) {
                        mRecorderState = "inactive";
                    } else if (cameraState.equals("StillCapturing") || cameraState.equals("MediaRecording")
                            || cameraState.equals("AudioRecording") || cameraState.equals("IntervalRecording")) {
                        mRecorderState = "recording";
                    } else if (cameraState.equals("MovieWaitRecStart") || cameraState.equals("MoviewWaitRecStop")
                            || cameraState.equals("AudioWaitRecStart") || cameraState.equals("AudioRecWaitRecStop")
                            || cameraState.equals("IntervalWaitRecStart")
                            || cameraState.equals("IntervalWaitRecStop")) {
                        mRecorderState = "paused";
                    }

                    if (stillSize == 0) {
                        sendErrorResponse(request, response);
                    } else {
                        width *= stillSize;
                        height *= stillSize;
                        List<Bundle> recorders = new ArrayList<Bundle>();
                        Bundle recorder = new Bundle();
                        recorder.putString(MediaStreamRecordingProfile.PARAM_ID, deviceId);
                        recorder.putString(MediaStreamRecordingProfile.PARAM_NAME, "SonyCamera");
                        recorder.putString(MediaStreamRecordingProfile.PARAM_STATE, mRecorderState);
                        recorder.putInt(MediaStreamRecordingProfile.PARAM_IMAGE_WIDTH, width);
                        recorder.putInt(MediaStreamRecordingProfile.PARAM_IMAGE_HEIGHT, height);
                        recorder.putString(MediaStreamRecordingProfile.PARAM_MIME_TYPE, "image/png");
                        recorders.add(recorder);
                        response.putExtra(MediaStreamRecordingProfile.PARAM_RECORDERS,
                                recorders.toArray(new Bundle[recorders.size()]));
                        sendResponse(request, response);
                    }
                }
            }
        });
        return false;
    }

    /**
     * ピクセル数計算用メソッド.
     * 
     * @param widthVal width
     * @param heightVal height
     * @param size aspect
     * @return stillSize
     */
    private double pixelValueCalculate(final int widthVal, final int heightVal, final String size) {
        int pixels = 0;
        int width = widthVal;
        int height = heightVal;
        double pixelValue = 0;

        if (size.equals("20M")) {
            pixels = PIXELS_20_M;
            pixelValue = Math.sqrt(pixels / (width * height));
        } else if (size.equals("18M")) {
            pixels = PIXELS_18_M;
            pixelValue = Math.sqrt(pixels / (width * height));
        } else if (size.equals("17M")) {
            pixels = PIXELS_17_M;
            pixelValue = Math.sqrt(pixels / (width * height));
        } else if (size.equals("13M")) {
            pixels = PIXELS_13_M;
            pixelValue = Math.sqrt(pixels / (width * height));
        } else if (size.equals("7.5M")) {
            pixels = PIXELS_7_5_M;
            pixelValue = Math.sqrt(pixels / (width * height));
        } else if (size.equals("5M")) {
            pixels = PIXELS_5_M;
            pixelValue = Math.sqrt(pixels / (width * height));
        } else if (size.equals("4.2M")) {
            pixels = PIXELS_4_2_M;
            pixelValue = Math.sqrt(pixels / (width * height));
        } else if (size.equals("3.7M")) {
            pixels = PIXELS_3_7_M;
            pixelValue = Math.sqrt(pixels / (width * height));
        }

        return pixelValue;
    }

    /**
     * takephotoに対応するメソッド.
     * 
     * @param request リクエスト
     * @param response レスポンス
     * @param deviceId デバイスID
     * @param target ターゲット
     * @return 即座にレスポンスを返す場合はtrue、それ以外はfalse
     */
    public boolean onPostTakePhoto(final Intent request, final Intent response, final String deviceId,
            final String target) {
        mLogger.entering(this.getClass().getName(), "onPostTakePhoto");

        response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_OK);
        if (deviceId == null || !deviceId.equals(DEVICE_ID)) {
            mLogger.warning("deviceId is invalid. deviceId=" + deviceId);
            mLogger.exiting(this.getClass().getName(), "onPostTakePhoto");
            MessageUtils.setEmptyDeviceIdError(response);
            return true;
        }

        if ("MovieRecording".equals(mEventObserver.getCameraStatus())) {
            // 撮影中は、さらに撮影できないのでエラーを返す
            MessageUtils.setIllegalDeviceStateError(response);
            return true;
        }

        if (SONY_CAMERA_SHOOT_MODE_PIC.equals(mEventObserver.getShootMode())) {
            takePicture(request, response);
        } else {
            // 撮影モードが静止画になっていない場合はモードを切り替えてから撮影する
            setShootMode(request, response, SONY_CAMERA_SHOOT_MODE_PIC, new Runnable() {
                @Override
                public void run() {
                    takePicture(request, response);
                }
            });
        }
        mLogger.exiting(this.getClass().getName(), "onPostTakePhoto");
        return false;
    }

    /**
     * 写真撮影を行う.
     * 
     * @param request リクエスト
     * @param response レスポンス
     */
    private void takePicture(final Intent request, final Intent response) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                InputStream istream = null;
                response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_OK);
                try {
                    JSONObject replyJson = mRemoteApi.actTakePicture();
                    if (isErrorReply(replyJson)) {
                        sendErrorResponse(request, response);
                    } else {
                        JSONArray resultsObj = replyJson.getJSONArray("result");
                        JSONArray imageUrlsObj = resultsObj.getJSONArray(0);
                        String postImageUrl = null;
                        if (1 <= imageUrlsObj.length()) {
                            postImageUrl = imageUrlsObj.getString(0);
                        }
                        if (postImageUrl == null) {
                            sendErrorResponse(request, response);
                        } else {
                            istream = new URL(postImageUrl).openStream();
                            String filename = getFileName();
                            String uri = mFileMgr.saveFile(filename, istream);
                            if (filename != null) {
                                response.putExtra(MediaStreamRecordingProfile.PARAM_URI, uri);
                                response.putExtra(MediaStreamRecordingProfile.PARAM_PATH, mFileMgr.getBasePath()
                                        .toString() + "/" + filename);
                                notifyTakePhoto(filename, uri);
                                sendResponse(request, response);
                            } else {
                                sendErrorResponse(request, response);
                            }
                        }
                    }
                } catch (IOException e) {
                    mLogger.warning("Exception in takePicture." + e.toString());
                    sendErrorResponse(request, response);
                } catch (JSONException e) {
                    mLogger.warning("Exception in takePicture." + e.toString());
                    sendErrorResponse(request, response);
                } finally {
                    if (istream != null) {
                        try {
                            istream.close();
                        } catch (IOException e) {
                            mLogger.warning("Exception occurred in close.");
                        }
                    }
                }
            }
        });
    }

    /**
     * 動画撮影の要求を処理する.
     * 
     * @param request リクエスト
     * @param response レスポンス
     * @param deviceId デバイスID
     * @param target ターゲット
     * @param timeslice タイムスライス
     * @return 即座に返答する場合はtrue、それ以外はfalse
     */
    public boolean onPostRecord(final Intent request, final Intent response, final String deviceId,
            final String target, final long timeslice) {

        if (deviceId == null || !deviceId.equals(DEVICE_ID)) {
            MessageUtils.setEmptyDeviceIdError(response);
            return true;
        }

        if ("MovieRecording".equals(mEventObserver.getCameraStatus())) {
            // 撮影中は、さらに撮影できないのでエラーを返す
            MessageUtils.setIllegalDeviceStateError(response);
            return true;
        }

        if (SONY_CAMERA_SHOOT_MODE_MOVIE.equals(mEventObserver.getShootMode())) {
            startMovieRec(request, response);
        } else {
            // 撮影モードが動画になっていない場合には、撮影モードを切り替えてから撮影する
            setShootMode(request, response, SONY_CAMERA_SHOOT_MODE_MOVIE, new Runnable() {
                @Override
                public void run() {
                    startMovieRec(request, response);
                }
            });
        }
        return false;
    }

    /**
     * 動画撮影を開始する.
     * 
     * @param request リクエスト
     * @param response レスポンス
     */
    private void startMovieRec(final Intent request, final Intent response) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject replyJson = mRemoteApi.startMovieRec();
                    if (isErrorReply(replyJson)) {
                        sendErrorResponse(request, response);
                    } else {
                        JSONArray resultsObj = replyJson.getJSONArray("result");
                        int resultCode = resultsObj.getInt(0);
                        if (resultCode == 0) {
                            sendResponse(request, response);
                        } else {
                            sendErrorResponse(request, response);
                        }
                    }
                } catch (IOException e) {
                    mLogger.warning("Exception occurred in startMovieRec.");
                    sendErrorResponse(request, response);
                } catch (JSONException e) {
                    mLogger.warning("Exception occurred in startMovieRec.");
                    sendErrorResponse(request, response);
                }
            }
        });
    }

    /**
     * 撮影の停止要求を処理する.
     * 
     * @param request リクエスト
     * @param response レスポンス
     * @param deviceId デバイスID
     * @param mediaId メディアID
     * @return 即座に返答する場合はtrue、それ以外はfalse
     */
    public boolean onPutStop(final Intent request, final Intent response, final String deviceId, final String mediaId) {

        if (deviceId == null || !deviceId.equals(DEVICE_ID)) {
            MessageUtils.setEmptyDeviceIdError(response);
            return true;
        }

        if ("IDLE".equals(mEventObserver.getCameraStatus())) {
            // 撮影が開始されていないので、エラーを返す。
            MessageUtils.setIllegalDeviceStateError(response);
            return true;
        }

        if (!SONY_CAMERA_SHOOT_MODE_MOVIE.equals(mEventObserver.getShootMode())) {
            // 撮影モードが違うのでエラー
            MessageUtils.setIllegalDeviceStateError(response);
            return true;
        }

        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject replyJson = mRemoteApi.stopMovieRec();
                    if (isErrorReply(replyJson)) {
                        sendErrorResponse(request, response);
                    } else {
                        JSONArray resultsObj = replyJson.getJSONArray("result");
                        String thumbnailUrl = resultsObj.getString(0);
                        if (thumbnailUrl != null) {
                            sendResponse(request, response);
                        } else {
                            sendErrorResponse(request, response);
                        }
                    }
                } catch (IOException e) {
                    mLogger.warning("Exception occurred in stopMovieRec." + e.toString());
                    sendErrorResponse(request, response);
                } catch (JSONException e) {
                    mLogger.warning("Exception occured in stopMovieRec." + e.toString());
                    sendErrorResponse(request, response);
                }
            }
        });
        return false;
    }

    /**
     * SonyCameraの撮影モードを切り替える.
     * 
     * @param request リクエスト
     * @param response レスポンス
     * @param mode モード
     * @param run 切り替え後の処理
     */
    private void setShootMode(final Intent request, final Intent response, final String mode, final Runnable run) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject replyJson = mRemoteApi.setShootMode(mode);
                    if (isErrorReply(replyJson)) {
                        sendErrorResponse(request, response);
                    } else {
                        JSONArray resultsObj = replyJson.getJSONArray("result");
                        int resultCode = resultsObj.getInt(0);
                        if (resultCode == 0) {
                            run.run();
                        } else {
                            sendErrorResponse(request, response);
                        }
                    }
                } catch (IOException e) {
                    mLogger.warning("Exception occurred in setShootMode.");
                    sendErrorResponse(request, response);
                } catch (JSONException e) {
                    mLogger.warning("Exception occurred in setShootMode.");
                    sendErrorResponse(request, response);
                }
            }
        });
    }

    /**
     * 写真のデータを保存するファイル名を取得する.
     * 
     * @return 保存先のファイル名
     */
    private String getFileName() {
        return FILENAME_PREFIX + mSimpleDateFormat.format(new Date()) + FILE_EXTENSION;
    }

    /**
     * 写真撮影を通知する.
     * 
     * @param mediaid メディアID
     * @param uri 写真へのURI
     */
    public void notifyTakePhoto(final String mediaid, final String uri) {
        mLogger.entering(this.getClass().getName(), "notifyTakePhoto");

        List<Event> evts = EventManager.INSTANCE.getEventList(DEVICE_ID,
                MediaStreamRecordingProfileConstants.PROFILE_NAME, null,
                MediaStreamRecordingProfileConstants.ATTRIBUTE_ON_PHOTO);

        for (Event evt : evts) {
            Bundle photo = new Bundle();
            photo.putString(MediaStreamRecordingProfile.PARAM_PATH, mFileMgr.getBasePath().getPath().toString() + "/"
                    + mediaid);
            photo.putString(MediaStreamRecordingProfile.PARAM_MIME_TYPE, "image/png");

            Intent intent = new Intent(IntentDConnectMessage.ACTION_EVENT);
            intent.setComponent(ComponentName.unflattenFromString(evt.getReceiverName()));
            intent.putExtra(DConnectMessage.EXTRA_DEVICE_ID, DEVICE_ID);
            intent.putExtra(DConnectMessage.EXTRA_PROFILE, MediaStreamRecordingProfile.PROFILE_NAME);
            intent.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, MediaStreamRecordingProfile.ATTRIBUTE_ON_PHOTO);
            intent.putExtra(DConnectMessage.EXTRA_SESSION_KEY, evt.getSessionKey());
            intent.putExtra(MediaStreamRecordingProfile.PARAM_PHOTO, photo);

            sendEvent(intent, evt.getAccessToken());
        }
        mLogger.exiting(this.getClass().getName(), "notifyTakePhoto");
    }

    /**
     * 通知.
     * 
     * @param mediaId メディアID
     * @param uri プレビューへのURI
     */
    public void notifyDataAvailable(final String mediaId, final String uri) {
        List<Event> evts = EventManager.INSTANCE.getEventList(DEVICE_ID,
                MediaStreamRecordingProfileConstants.PROFILE_NAME, null,
                MediaStreamRecordingProfileConstants.ATTRIBUTE_ON_DATA_AVAILABLE);
        for (Event evt : evts) {
            Bundle media = new Bundle();
            media.putString(MediaStreamRecordingProfile.PARAM_PATH, mediaId);
            media.putString(MediaStreamRecordingProfile.PARAM_URI, uri);
            media.putString(MediaStreamRecordingProfile.PARAM_MIME_TYPE, "image/png");

            Intent intent = new Intent(IntentDConnectMessage.ACTION_EVENT);
            intent.setComponent(ComponentName.unflattenFromString(evt.getReceiverName()));
            intent.putExtra(DConnectMessage.EXTRA_DEVICE_ID, DEVICE_ID);
            intent.putExtra(DConnectMessage.EXTRA_PROFILE, MediaStreamRecordingProfile.PROFILE_NAME);
            intent.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, MediaStreamRecordingProfile.ATTRIBUTE_ON_DATA_AVAILABLE);
            intent.putExtra(DConnectMessage.EXTRA_SESSION_KEY, evt.getSessionKey());
            intent.putExtra(MediaStreamRecordingProfile.PARAM_MEDIA, media);

            sendEvent(intent, evt.getAccessToken());
        }
    }

    /**
     * SonyCameraに接続する.
     */
    private void connectSonyCamera() {
        mLogger.fine("Start a search for a SonyCamera device.");
        final boolean[] found = new boolean[1];
        mSsdpClient.search(new SimpleSsdpClient.SearchResultHandler() {
            @Override
            public void onDeviceFound(final ServerDevice device) {
                mLogger.fine("Found SonyCamera device." + device.getModelName());
                found[0] = true;
                createSonyCameraSDK(device);
            }

            @Override
            public void onFinished() {
                if (!found[0]) {
                    mLogger.warning("Cannot found SonyCamera device.");
                    // 見つからない場合には、何回か確認を行う
                    mRetryCount++;
                    if (mRetryCount < MAX_RETRY_COUNT) {
                        mExecutor.execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(WAIT_TIME);
                                } catch (InterruptedException e) {
                                    mLogger.warning("Exception ocurred in Thread.sleep.");
                                }
                                mLogger.fine("Retry connect to SonyCamera device.");
                                connectSonyCamera();
                            }
                        });
                    } else {
                        deleteSonyCameraSDK();
                    }
                }
            }

            @Override
            public void onErrorFinished() {
                mLogger.warning("Error occurred in SsdpClient#serarch.");
            }
        });
    }

    /**
     * SonyCameraデバイスSDKを作成する.
     * 
     * @param device SonyCameraデバイス
     */
    private void createSonyCameraSDK(final ServerDevice device) {
        if (mRemoteApi != null) {
            deleteSonyCameraSDK();
        }
        mRemoteApi = new SimpleRemoteApi(device);
        mAvailableApiList = getAvailableApi();

        createEventObserver();

        List<Event> evts = EventManager.INSTANCE.getEventList(DEVICE_ID,
                NetworkServiceDiscoveryProfileConstants.PROFILE_NAME, null,
                NetworkServiceDiscoveryProfileConstants.ATTRIBUTE_ON_SERVICE_CHANGE);

        for (Event evt : evts) {
            Bundle camera = new Bundle();
            camera.putString(NetworkServiceDiscoveryProfile.PARAM_NAME, DEVICE_NAME);
            camera.putString(NetworkServiceDiscoveryProfile.PARAM_TYPE, "WiFi");
            camera.putBoolean(NetworkServiceDiscoveryProfile.PARAM_STATE, true);
            camera.putBoolean(NetworkServiceDiscoveryProfile.PARAM_ONLINE, true);
            camera.putString(NetworkServiceDiscoveryProfile.PARAM_CONFIG, "");

            Intent intent = new Intent(IntentDConnectMessage.ACTION_EVENT);
            intent.setComponent(ComponentName.unflattenFromString(evt.getReceiverName()));
            intent.putExtra(DConnectMessage.EXTRA_DEVICE_ID, DEVICE_ID);
            intent.putExtra(DConnectMessage.EXTRA_PROFILE, NetworkServiceDiscoveryProfile.PROFILE_NAME);
            intent.putExtra(DConnectMessage.EXTRA_ATTRIBUTE,
                    NetworkServiceDiscoveryProfile.ATTRIBUTE_ON_SERVICE_CHANGE);
            intent.putExtra(DConnectMessage.EXTRA_SESSION_KEY, evt.getSessionKey());
            intent.putExtra(NetworkServiceDiscoveryProfile.PARAM_NETWORK_SERVICE, camera);

            sendEvent(intent, evt.getAccessToken());
        }
        mLogger.exiting(this.getClass().getName(), "createSonyCameraSDK");

    }

    /**
     * SonyCameraデバイスSDKを破棄する.
     */
    private void deleteSonyCameraSDK() {
        mWhileFetching = false;

        if (mEventObserver != null) {
            mEventObserver.stop();
            mEventObserver = null;
        }
        if (mRemoteApi != null) {
            mRemoteApi = null;
            // dConnectManagerにデバイスの消失を通知
            // Intent event = MessageUtils.createEventIntent();
        }
        mRetryCount = 0;
    }

    /**
     * SonyCameraデバイスからのイベントを待つスレッドを作成する.
     */
    private void createEventObserver() {
        if (mEventObserver == null || !mEventObserver.isStarted()) {
            mEventObserver = new SimpleCameraEventObserver(this, mRemoteApi);
            mEventObserver.setEventChangeListener(new SimpleCameraEventObserver.ChangeListener() {
                @Override
                public void onShootModeChanged(final String shootMode) {
                }

                @Override
                public void onCameraStatusChanged(final String status) {
                }

                @Override
                public void onApiListModified(final List<String> apis) {
                }

                @Override
                public void onZoomPositionChanged(final int zoomPosition) {
                }

                @Override
                public void onLiveviewStatusChanged(final boolean status) {
                }

                @Override
                public void onTakePicture(final String postImageUrl) {
                    mLogger.entering(this.getClass().getName(), "onTakePicture", postImageUrl);

                    InputStream istream = null;
                    try {
                        istream = new URL(postImageUrl).openStream();
                        String filename = getFileName();
                        String uri = mFileMgr.saveFile(filename, istream);
                        notifyTakePhoto(filename, uri);
                    } catch (IOException e) {
                        mLogger.warning("Exception in onTakePicture." + e.toString());
                    } finally {
                        if (istream != null) {
                            try {
                                istream.close();
                            } catch (IOException e) {
                                mLogger.warning("Exception occurred in close.");
                            }
                        }
                    }
                    mLogger.exiting(this.getClass().getName(), "onTakePicture");
                }
            });
            mEventObserver.start();
        }
    }

    /**
     * プレビューを追加する.
     * 
     * @return プレビューの開始ができた場合はtrue、それ以外はfalse
     */
    public synchronized boolean startPreview() {
        if (mRemoteApi == null) {
            return false;
        }
        if (mWhileFetching) {
            return false;
        }
        startPreview(mRemoteApi);
        return true;
    }

    /**
     * プレビューを停止する.
     * @return result
     */
    public synchronized boolean stopPreview() {
        if (mRemoteApi == null) {
            return false;
        }
        if (!mWhileFetching) {
            return false;
        }
        stopPreview(mRemoteApi);
        return true;
    }

    /**
     * プレビューを削除する.
     * 
     * @param api SonyCameraSDKのインスタンス
     */
    private void stopPreview(final SimpleRemoteApi api) {
        mWhileFetching = false;
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // Prepare for connecting.
                    JSONObject replyJson = null;
                    replyJson = mRemoteApi.stopLiveview();
                    
                    if (!isErrorReply(replyJson)) {
                        //エラーチェック
                        JSONArray resultsObj = replyJson.getJSONArray("result");
                    }

                } catch (IOException e) {
                    mLogger.warning("IOException while fetching: " + e.getMessage());
                } catch (JSONException e) {
                    mLogger.warning("JSONException while fetching");
                }
            }
        });
    }

    /**
     * プレビューを追加すり.
     * 
     * @param api SonyCameraSDKのインスタンス
     */
    private void startPreview(final SimpleRemoteApi api) {
        mWhileFetching = true;

        new Thread(new Runnable() {
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

                    long time = System.currentTimeMillis();
                    int count = 0;
                    while (mWhileFetching) {
                        final Payload payload = slicer.nextPayload();
                        if (payload == null) { // never occurs
                            continue;
                        }

                        if (System.currentTimeMillis() - time > SLEEP_MSEC) {
                            List<Event> evts = EventManager.INSTANCE.getEventList(DEVICE_ID,
                                    MediaStreamRecordingProfileConstants.PROFILE_NAME, null,
                                    MediaStreamRecordingProfileConstants.ATTRIBUTE_ON_DATA_AVAILABLE);
                            if (evts.size() > 0) {
                                String mediaId = "preview" + count + ".png";
                                String uri = mFileMgr.saveFile(mediaId, payload.getJpegData());
                                notifyDataAvailable(mediaId, uri);
                                count++;
                                count %= MAX_PREVIEW;
                            }
                            time = System.currentTimeMillis();
                        }
                    }
                } catch (IOException e) {
                    mLogger.warning("IOException while fetching: " + e.getMessage());
                } catch (JSONException e) {
                    mLogger.warning("JSONException while fetching");
                } finally {
                    // slicerのclose()に著しく時間がかかる場合があるので、別スレッドでclose()を実行する。
                    final SimpleLiveviewSlicer tmp = slicer;
                    if (slicer != null) {
                        ExecutorService slicerCloser = Executors.newSingleThreadExecutor();
                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                try {
                                    tmp.close();
                                } catch (IOException e) {
                                    mLogger.warning(
                                            "IOException while closing slicer: "
                                                    + e.getMessage());
                                }
                            }
                        };
                        slicerCloser.execute(thread);
                    }
                    try {
                        mRemoteApi.stopLiveview();
                    } catch (IOException e) {
                        mLogger.warning("IOException while closing slicer: " + e.getMessage());
                    }

                    mWhileFetching = false;
                }
            }
        }).start();
    }

    /**
     * SonyCameraデバイスのチェックを行う.
     * 
     * @return 有効な場合はtrue、それ以外はfalse
     */
    private boolean checkDevice() {
        return (mRemoteApi != null);
    }

    /**
     * 指定されたデータからレスポンスを作成する.
     * 
     * @param request リクエスト
     * @param response レスポンスするデータ
     */
    private void sendResponse(final Intent request, final Intent response) {
        response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_OK);
        sendBroadcast(response);
    }

    /**
     * エラーのレスポンスを作成し、送信する.
     * 
     * @param request リクエスト
     * @param response レスポンス
     */
    private void sendErrorResponse(final Intent request, final Intent response) {
        response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_ERROR);
        sendBroadcast(response);
    }

    /**
     * SonyCameraからの返り値のエラーチェック.
     * 
     * @param replyJson レスポンスJSON
     * @return エラーの場合はtrue、それ以外はfalse
     */
    private boolean isErrorReply(final JSONObject replyJson) {
        boolean hasError = (replyJson != null && replyJson.has("error"));
        return hasError;
    }

    @Override
    protected SystemProfile getSystemProfile() {
        return new SonyCameraSystemProfile(this);
    }

    @Override
    protected NetworkServiceDiscoveryProfile getNetworkServiceDiscoveryProfile() {
        return new SonyCameraNetworkServiceDiscoveryProfile();
    }

    /**
     * 時間を設定する.
     * 
     * @param request リクエスト
     * @param response レスポンス
     * @param deviceId デバイスID
     * @param date 時間 yyyy-mm-ddThh:mm:ss+TimeZone(ex:0900)
     * @return 即座に返答する場合はtrue、それ以外はfalse
     */
    public boolean onPutDate(final Intent request, final Intent response, final String deviceId, final String date) {

        if (deviceId == null || !deviceId.equals(DEVICE_ID)) {
            MessageUtils.setEmptyDeviceIdError(response);
            return true;
        }
        if (date == null) {
            MessageUtils.setInvalidRequestParameterError(response);
            return true;
        }
        if (!date.contains("+")) {
            MessageUtils.setInvalidRequestParameterError(response);
            return true;
        }
        if (mAvailableApiList == null) {
            MessageUtils.setUnknownError(response);
            return true;
        }
        if (mAvailableApiList.indexOf("setCurrentTime") == -1) {
            MessageUtils.setNotSupportActionError(response);
            return true;
        }

        /**
         * タイムゾーンをSonyCameraの入力仕様に合わせて変換
         */
        int index = date.indexOf("+");
        final String mDate = date.substring(0, index) + "Z";
        String timeZoneData = date.substring(index + 1);
        int timeZone;

        try {
            timeZone = Integer.valueOf(timeZoneData);
        } catch (NumberFormatException e) {
            MessageUtils.setInvalidRequestParameterError(response);
            return true;
        }

        int hTime = timeZone / CUL_PARAM_DATETIME;
        int hMinute = timeZone - (hTime * CUL_PARAM_DATETIME);
        final int mTimeZone = (hTime * CUL_PARAM_MINITE) + hMinute;
        mLogger.fine("Date:" + mDate + "\nTimeZone:" + mTimeZone);

        response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_OK);
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject replyJson = mRemoteApi.setCurrentTime(mDate, mTimeZone);
                    if (replyJson == null) {
                        sendErrorResponse(request, response);
                    }
                    if (isErrorReply(replyJson)) {
                        sendErrorResponse(request, response);
                    } else {
                        JSONArray resultsObj = replyJson.getJSONArray("result");
                        if (resultsObj != null) {
                            sendResponse(request, response);
                        } else {
                            sendErrorResponse(request, response);
                        }
                    }
                } catch (IOException e) {
                    mLogger.warning("Exception in setCurrentTime." + e.toString());
                    sendErrorResponse(request, response);
                } catch (JSONException e) {
                    mLogger.warning("Exception in setCurrentTime." + e.toString());
                    sendErrorResponse(request, response);
                }
            }
        });
        return false;
    }

    /**
     * ズーム用メソッド.
     * 
     * @param request リクエスト
     * @param response レスポンス
     * @param deviceId デバイスID
     * @param direction ズーム方向
     * @param movement ズーム動作
     * @return true
     */
    public boolean onPutActZoom(final Intent request, final Intent response, final String deviceId,
            final String direction, final String movement) {
        mLogger.entering(this.getClass().getName(), "onPutActZoom");

        response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_OK);
        if (deviceId == null || !deviceId.equals(DEVICE_ID)) {
            mLogger.warning("deviceId is invalid. deviceId=" + deviceId);
            mLogger.exiting(this.getClass().getName(), "onPutActZoom");
            MessageUtils.setEmptyDeviceIdError(response);
            return true;
        }
        if (direction == null || movement == null) {
            MessageUtils.setInvalidRequestParameterError(response);
            return true;
        }
        if (mAvailableApiList == null) {
            MessageUtils.setUnknownError(response);
            return true;
        }
        
        if (mAvailableApiList.indexOf("actZoom") == -1) {
            MessageUtils.setNotSupportActionError(response);
            return true;
        }
        if (!direction.equals("in")) {
            if (!direction.equals("out")) {
                MessageUtils.setInvalidRequestParameterError(response);
            }
        }
        if (!movement.equals("start")) {
            if (!movement.equals("stop")) {
                if (!movement.equals("1shot")) {
                    if (!movement.equals("max")) {
                        MessageUtils.setInvalidRequestParameterError(response);
                    }
                }
            }
        }

        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject replyJson;
                    if (movement.equals("max")) {
                        replyJson = mRemoteApi.actZoom(direction, "start");
                    } else {
                        replyJson = mRemoteApi.actZoom(direction, movement);
                    }

                    if (isErrorReply(replyJson)) {
                        sendErrorResponse(request, response);
                    } else {
                        JSONArray resultsObj = replyJson.getJSONArray("result");
                        if (resultsObj != null) {
                            sendResponse(request, response);
                        } else {
                            sendErrorResponse(request, response);
                        }
                    }
                } catch (IOException e) {
                    mLogger.warning("Exception in actZoom." + e.toString());
                    sendErrorResponse(request, response);
                } catch (JSONException e) {
                    mLogger.warning("Exception in actZoom." + e.toString());
                    sendErrorResponse(request, response);
                }
            }
        });

        return false;
    }

    /**
     * ズーム倍率取得用メソッド.
     * 
     * @param request request
     * @param response response
     * @param deviceId deviceID
     * @return result
     */
    public boolean onGetZoomDiameter(final Intent request, final Intent response, final String deviceId) {
        if (deviceId == null || !deviceId.equals(DEVICE_ID)) {
            mLogger.warning("deviceId is invalid. deviceId=" + deviceId);
            mLogger.exiting(this.getClass().getName(), "onGetZoomDiameter");
            MessageUtils.setEmptyDeviceIdError(response);
            sendResponse(response);
            return true;
        }
        if (mAvailableApiList == null) {
            MessageUtils.setUnknownError(response);
            sendResponse(response);
            return true;
        }
        if (mAvailableApiList.indexOf("getEvent") == -1) {
            MessageUtils.setNotSupportActionError(response);
            sendResponse(response);
            return true;
        }
        response.putExtra(DConnectMessage.EXTRA_RESULT, DConnectMessage.RESULT_OK);
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                double zoomDiameterParam = 0;
                try {
                    JSONObject replyJson = mRemoteApi.getEvent(mWhileFetching);
                    if (isErrorReply(replyJson)) {
                        sendErrorResponse(request, response);
                    } else {
                        JSONArray resultsObj = replyJson.getJSONArray("result");
                        replyJson = resultsObj.getJSONObject(2);
                        zoomDiameterParam = (Double) Double.valueOf(replyJson.getString("zoomPosition"))
                                / (Double) VAL_TO_PERCENTAGE;
                        DecimalFormat decimalFormat = new DecimalFormat("0.0#");
                        zoomDiameterParam = Double.valueOf(decimalFormat.format(zoomDiameterParam));

                        response.putExtra(SonyCameraZoomProfile.PARAM_ZOOM_DIAMETER, zoomDiameterParam);
                        sendResponse(request, response);
                    }
                } catch (IOException e) {
                    sendErrorResponse(request, response);
                } catch (JSONException e) {
                    sendErrorResponse(request, response);
                }
            }
        });

        return false;
    }

    /**
     * 接続中のカメラの利用可能Remote APIリストをStringで入手.
     * 
     * @return result Available API List
     */
    public String getAvailableApi() {
        String result = null;
        try {
            JSONObject replyJson = mRemoteApi.getAvailableApiList();
            if (replyJson != null) {
                JSONArray resultsObj = replyJson.getJSONArray("result");
                result = resultsObj.toString();
            }
        } catch (IOException e) {
            mLogger.warning("IOException in availableApiCheck.");
            e.printStackTrace();
        } catch (JSONException e) {
            mLogger.warning("JSONException in availableApiCheck.");
            e.printStackTrace();
        }
        return result;
    }
}
