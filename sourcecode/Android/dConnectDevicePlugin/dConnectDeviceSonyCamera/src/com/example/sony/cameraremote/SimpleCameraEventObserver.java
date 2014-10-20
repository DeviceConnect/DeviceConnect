/*
 * Copyright 2013 Sony Corporation
 */

package com.example.sony.cameraremote;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

/**
 * SimpleCameraEventObserver.
 */
public class SimpleCameraEventObserver {

    /** LiveView Statusナンバー. */
    private static final int VIEW_STATUS = 3;
    /** ShootMode ナンバー. */
    private static final int SHOOT_MODE = 21;
    /**  */
    private static final int TAKE_PICTURE = 5; 

    /**
     * A listener interface to receive these changes. These methods will be
     * called by UI thread.
     */
    public interface ChangeListener {

        /**
         * Called when the list of available APIs is modified.
         * 
         * @param apis a list of available APIs
         */
        void onApiListModified(List<String> apis);

        /**
         * Called when the value of "Camera Status" is changed.
         * 
         * @param status camera status (ex."IDLE")
         */
        void onCameraStatusChanged(String status);

        /**
         * Called when the value of "Liveview Status" is changed.
         * 
         * @param status liveview status (ex.true)
         */
        void onLiveviewStatusChanged(boolean status);

        /**
         * Called when the value of "Shoot Mode" is changed.
         * 
         * @param shootMode shoot mode (ex."still")
         */
        void onShootModeChanged(String shootMode);

        /**
         * Called when the value of "zoomPosition" is changed.
         * 
         * @param zoomPosition zoom position (ex.12)
         */
        void onZoomPositionChanged(int zoomPosition);

        // :
        // : add methods for Event data as necessary.
        /**
         * 写真撮影.
         * @param postImageUrl 撮影イメージのURL
         */
        void onTakePicture(final String postImageUrl);
    }
    /** No Error Status Code.*/
    private static final int STATUS_CODE_0 = 0;
    /** "Ane" Error Status Code.*/
    private static final int STATUS_CODE_1 = 1;
    /** No such method" Error Status Code.*/
    private static final int STATUS_CODE_12 = 12;
    /** "Timeout" Error Status Code.*/
    private static final int STATUS_CODE_2 = 2;
    /** "Already polling" Error Status Code.*/
    private static final int STATUS_CODE_40402 = 40402;

    /** Thread sleeping msec. */
    private static final int THREAD_SLEEP_MSEC = 5000;

    /**
     * RemoteApi.
     */
    private SimpleRemoteApi mRemoteApi;
    /**
     * Listner.
     */
    private ChangeListener mListener;
    /**
     * EventMonitor.
     */
    private boolean mWhileEventMonitoring = false;

    /**
     *  Current Camera Status value.
     */
    private String mCameraStatus;

    /**
     *  Current Liveview Status value.
     */
    private boolean mLiveviewStatus;

    /**
     *  Current Shoot Mode value.
     */
    private String mShootMode;

    /**
     *  Current Zoom Position value.
     */
    private int mZoomPosition;

    // :
    // : add attributes for Event data as necessary.

    /**
     * Constructor.
     * 
     * @param context context to notify the changes by UI thread.
     * @param apiClient API client
     */
    public SimpleCameraEventObserver(final Context context, final SimpleRemoteApi apiClient) {
        if (context == null) {
            throw new IllegalArgumentException("context is null.");
        }
        if (apiClient == null) {
            throw new IllegalArgumentException("apiClient is null.");
        }
        mRemoteApi = apiClient;
    }

    /**
     * Starts monitoring by continuously calling getEvent API.
     * 
     * @return true if it successfully started, false if a monitoring is already
     *         started.
     */
    public boolean start() {
        if (mWhileEventMonitoring) {
            return false;
        }

        mWhileEventMonitoring = true;
        new Thread() {

            @Override
            public void run() {
                // Call getEvent API continuously.
                boolean firstCall = true;
                MONITORLOOP: while (mWhileEventMonitoring) {

                    // At first, call as non-Long Polling.
                    boolean longPolling = firstCall ? false : true;

                    try {
                        // Call getEvent API.
                        JSONObject replyJson = mRemoteApi.getEvent(longPolling);

                        // Check error code at first.
                        int errorCode = findErrorCode(replyJson);
                        switch (errorCode) {
                            case STATUS_CODE_0: // no error
                                // Pass through.
                                break;
                            case STATUS_CODE_1: // "Any" error
                            case STATUS_CODE_12: // "No such method" error
                                break MONITORLOOP; // end monitoring.
                            case STATUS_CODE_2: // "Timeout" error
                                // Re-call immediately.
                                continue MONITORLOOP;
                            case STATUS_CODE_40402: // "Already polling" error
                                // Retry after 5 sec.
                                try {
                                    Thread.sleep(THREAD_SLEEP_MSEC);
                                } catch (InterruptedException e) {
                                  //Exceptionを受けるだけなので処理は行わない
                                }
                                continue MONITORLOOP;
                            default:
                                break MONITORLOOP; // end monitoring.
                        }

                        List<String> availableApis = findAvailableApiList(replyJson);
                        if (!availableApis.isEmpty()) {
                            fireApiListModifiedListener(availableApis);
                        }

                        // CameraStatus
                        String cameraStatus = findCameraStatus(replyJson);
                        if (cameraStatus != null
                                && !cameraStatus.equals(mCameraStatus)) {
                            mCameraStatus = cameraStatus;
                            fireCameraStatusChangeListener(cameraStatus);
                        }

                        // LiveviewStatus
                        Boolean liveviewStatus = findLiveviewStatus(replyJson);
                        if (liveviewStatus != null
                                && !liveviewStatus.equals(mLiveviewStatus)) {
                            mLiveviewStatus = liveviewStatus;
                            fireLiveviewStatusChangeListener(liveviewStatus);
                        }

                        // ShootMode
                        String shootMode = findShootMode(replyJson);
                        if (shootMode != null && !shootMode.equals(mShootMode)) {
                            mShootMode = shootMode;
                            fireShootModeChangeListener(shootMode);
                        }

                        // zoomPosition
                        int zoomPosition = findZoomInformation(replyJson);
                        if (zoomPosition != -1) {
                            mZoomPosition = zoomPosition;
                            fireZoomInformationChangeListener(0, 0, zoomPosition, 0);
                        }

                        // :
                        // : add implementation for Event data as necessary.
                        
                        String imageUrl = findTakePicture(replyJson);
                        if (imageUrl != null) {
                            fireTakePictureChangeListner(imageUrl);
                        }

                    } catch (IOException e) {
                        // Occurs when the server is not available now.
                        break MONITORLOOP;
                    } catch (JSONException e) {
                        break MONITORLOOP;
                    }

                    firstCall = false;
                } // MONITORLOOP end.

                mWhileEventMonitoring = false;
            }
        } .start();

        return true;
    }

    /**
     * Requests to stop the monitoring.
     */
    public void stop() {
        mWhileEventMonitoring = false;
    }

    /**
     * Checks to see whether a monitoring is already started.
     * 
     * @return true when monitoring is started.
     */
    public boolean isStarted() {
        return mWhileEventMonitoring;
    }

    /**
     * Sets a listener object.
     * 
     * @param listener リスナー
     */
    public void setEventChangeListener(final ChangeListener listener) {
        mListener = listener;
    }

    /**
     * Clears a listener object.
     */
    public void clearEventChangeListener() {
        mListener = null;
    }

    /**
     * Returns the current Camera Status value.
     * 
     * @return camera status
     */
    public String getCameraStatus() {
        return mCameraStatus;
    }

    /**
     * Returns the current Camera Status value.
     * 
     * @return camera status
     */
    public boolean getLiveviewStatus() {
        return mLiveviewStatus;
    }

    /**
     * Returns the current Shoot Mode value.
     * 
     * @return shoot mode
     */
    public String getShootMode() {
        return mShootMode;
    }

    /**
     * Returns the current Zoom Position value.
     * 
     * @return zoom position
     */
    public int getZoomPosition() {
        return mZoomPosition;
    }

    /**
     *  Notifies the listener of available APIs change.
     * @param availableApis 利用可能APIリスト
     */
    private void fireApiListModifiedListener(final List<String> availableApis) {
        if (mListener != null) {
            mListener.onApiListModified(availableApis);
        }
    }

    /**
     *  Notifies the listener of Camera Status change.
     * @param status ステータス
     */
    private void fireCameraStatusChangeListener(final String status) {
        if (mListener != null) {
            mListener.onCameraStatusChanged(status);
        }
    }

    /**
     *  Notifies the listener of Liveview Status change.
     * @param status ステータス
     */
    private void fireLiveviewStatusChangeListener(final boolean status) {
        if (mListener != null) {
            mListener.onLiveviewStatusChanged(status);
        }
    }

    /**
     *  Notifies the listener of Shoot Mode change.
     * @param shootMode 撮影モード
     */
    private void fireShootModeChangeListener(final String shootMode) {
        if (mListener != null) {
            mListener.onShootModeChanged(shootMode);
        }
    }

    /**
     *  Notifies the listener of Zoom Information change.
     * @param zoomIndexCurrentBox 
     * @param zoomNumberBox 
     * @param zoomPosition 
     * @param zoomPositionCurrentBox 
     */
    private void fireZoomInformationChangeListener(final int zoomIndexCurrentBox,
            final int zoomNumberBox, final int zoomPosition, final int zoomPositionCurrentBox) {
        if (mListener != null) {
            mListener.onZoomPositionChanged(zoomPosition);
        }
    }

    /**
     * .
     * @param imageUrl 
     */
    private void fireTakePictureChangeListner(final String imageUrl) {
        if (mListener != null) {
            mListener.onTakePicture(imageUrl);
        }
    }

    /**
     *  Finds and extracts an error code from reply JSON data.
     * @param replyJson 
     * @return code
     * @throws JSONException 
     */
    private static int findErrorCode(final JSONObject replyJson) throws JSONException {
        int code = 0; // 0 means no error.
        if (replyJson.has("error")) {
            JSONArray errorObj = replyJson.getJSONArray("error");
            code = errorObj.getInt(0);
        }
        return code;
    }

    /**
     *  Finds and extracts a list of available APIs from reply JSON data.
     *  As for getEvent v1.0, results[0] => "availableApiList"
     * @param replyJson 
     * @return availavleApis
     * @throws JSONException 
     */
    private static List<String> findAvailableApiList(final JSONObject replyJson)
            throws JSONException {
        List<String> availableApis = new ArrayList<String>();
        int indexOfAvailableApiList = 0;
        JSONArray resultsObj = replyJson.getJSONArray("result");
        if (!resultsObj.isNull(indexOfAvailableApiList)) {
            JSONObject availableApiListObj = resultsObj
                    .getJSONObject(indexOfAvailableApiList);
            String type = availableApiListObj.getString("type");
            if ("availableApiList".equals(type)) {
                JSONArray apiArray = availableApiListObj.getJSONArray("names");
                for (int i = 0; i < apiArray.length(); i++) {
                    availableApis.add(apiArray.getString(i));
                }
            } 
        }
        return availableApis;
    }

    /**
     *  Finds and extracts a value of Camera Status from reply JSON data.
     *  As for getEvent v1.0, results[1] => "cameraStatus"
     * @param replyJson 
     * @return cameraStatus 
     * @throws JSONException 
     */
    private static String findCameraStatus(final JSONObject replyJson)
            throws JSONException {
        String cameraStatus = null;
        int indexOfCameraStatus = 1;
        JSONArray resultsObj = replyJson.getJSONArray("result");
        if (!resultsObj.isNull(indexOfCameraStatus)) {
            JSONObject cameraStatusObj = resultsObj
                    .getJSONObject(indexOfCameraStatus);
            String type = cameraStatusObj.getString("type");
            if ("cameraStatus".equals(type)) {
                cameraStatus = cameraStatusObj.getString("cameraStatus");
            }
        }
        return cameraStatus;
    }

    /**
     *  Finds and extracts a value of Liveview Status from reply JSON data.
     *  As for getEvent v1.0, results[3] => "liveviewStatus"
     * @param replyJson 
     * @return liveviewstatus
     * @throws JSONException 
     */
    private static Boolean findLiveviewStatus(final JSONObject replyJson)
            throws JSONException {
        Boolean liveviewStatus = null;
        int indexOfLiveviewStatus = VIEW_STATUS;
        JSONArray resultsObj = replyJson.getJSONArray("result");
        if (!resultsObj.isNull(indexOfLiveviewStatus)) {
            JSONObject liveviewStatusObj = resultsObj
                    .getJSONObject(indexOfLiveviewStatus);
            String type = liveviewStatusObj.getString("type");
            if ("liveviewStatus".equals(type)) {
                liveviewStatus = liveviewStatusObj.getBoolean("liveviewStatus");
            }
        }
        return liveviewStatus;
    }

    /**
     *  Finds and extracts a value of Shoot Mode from reply JSON data.
     *  As for getEvent v1.0, results[21] => "shootMode"
     * @param replyJson 
     * @return shootMode
     * @throws JSONException 
     */
    private static String findShootMode(final JSONObject replyJson)
            throws JSONException {
        String shootMode = null;
        int indexOfShootMode = SHOOT_MODE;
        JSONArray resultsObj = replyJson.getJSONArray("result");
        if (!resultsObj.isNull(indexOfShootMode)) {
            JSONObject shootModeObj = resultsObj
                    .getJSONObject(indexOfShootMode);
            String type = shootModeObj.getString("type");
            if ("shootMode".equals(type)) {
                shootMode = shootModeObj.getString("currentShootMode");
            }
        }
        return shootMode;
    }

    /**
     *  Finds and extracts a value of Zoom Information from reply JSON data.
     *  As for getEvent v1.0, results[2] => "zoomInformation"
     * @param replyJson 
     * @return zoomPosition
     * @throws JSONException 
     */
    private static int findZoomInformation(final JSONObject replyJson)
            throws JSONException {
        int zoomPosition = -1;
        int indexOfZoomInformation = 2;
        JSONArray resultsObj = replyJson.getJSONArray("result");
        if (!resultsObj.isNull(indexOfZoomInformation)) {
            JSONObject zoomInformationObj = resultsObj
                    .getJSONObject(indexOfZoomInformation);
            String type = zoomInformationObj.getString("type");
            if ("zoomInformation".equals(type)) {
                zoomPosition = zoomInformationObj.getInt("zoomPosition");
            }
        }
        return zoomPosition;
    }

    /**
     * .
     * @param replyJson 
     * @return postImageUrl
     * @throws JSONException 
     */
    private static String findTakePicture(final JSONObject replyJson)
            throws JSONException {
        String postImageUrl = null;
        int indexOfTakePicture = TAKE_PICTURE;
        JSONArray resultsObj = replyJson.getJSONArray("result");
        if (!resultsObj.isNull(indexOfTakePicture)) {
            JSONArray takePictureObj = resultsObj
                    .getJSONArray(indexOfTakePicture);
            for (int i = 0; i < takePictureObj.length(); i++) {
                JSONObject takeObj = takePictureObj.getJSONObject(i);
                String type = takeObj.getString("type");
                if ("takePicture".equals(type)) {
                    JSONArray imageUrlObj = takeObj.getJSONArray("takePictureUrl");
                    if (1 <= imageUrlObj.length()) {
                        postImageUrl = imageUrlObj.getString(0);
                    }
                    if (postImageUrl == null) {
                        postImageUrl = null;
                    }
                }
            }
        }
        return postImageUrl;
    }

}
