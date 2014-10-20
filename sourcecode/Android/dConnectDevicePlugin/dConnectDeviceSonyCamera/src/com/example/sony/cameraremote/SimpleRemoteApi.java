/*
 * Copyright 2013 Sony Corporation
 */

package com.example.sony.cameraremote;

import java.io.IOException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.sony.cameraremote.ServerDevice.ApiService;
import com.example.sony.cameraremote.utils.SimpleHttpClient;

import android.util.Log;

/**
 * SimpleRemoteApi.
 */
public class SimpleRemoteApi {

    /** ロギングタグ. */
    private static final String TAG = SimpleRemoteApi.class.getSimpleName();

    /**
     * If you'd like to suppress detailed log output, change this value into
     * false.
     */
    private static final boolean FULL_LOG = false;

    /** Polling End Flag. */
    private static final int POLLING_FLAG_END = 20000;
    /** Polling Start Flag. */
    private static final int POLLING_FLAG_START = 8000;

    /**
     * API server device you want to send requests.
     */
    private ServerDevice mTargetServer;

    /**
     * Request ID of API calling. This will be counted up by each API calling.
     */
    private int mRequestId;

    /**
     * Constructor.
     * 
     * @param target server device of Remote API
     */
    public SimpleRemoteApi(final ServerDevice target) {
        mTargetServer = target;
        mRequestId = 1;
    }

    /**
     * Retrieves Action List URL from Server information.
     * 
     * @param service サービス
     * @return actionURL
     */
    private String findActionListUrl(final String service) {
        List<ApiService> services = mTargetServer.getApiServices();
        for (ApiService apiService : services) {
            if (apiService.getName().equals(service)) {
                return apiService.getActionListUrl();
            }
        }
        throw new IllegalStateException("actionUrl not found.");
    }

    /**
     * Request ID. Counted up after calling.
     * 
     * @return リクエストID
     */
    private int id() {
        return mRequestId++;
    }

    /**
     * Output a log line.
     * 
     * @param msg メッセージ
     */
    private void log(final String msg) {
        if (FULL_LOG) {
            Log.d(TAG, msg);
        }
    }

    // Camera Service APIs

    /**
     * Calls getAvailableApiList API to the target server. Request JSON data is
     * such like as below.
     * 
     * <pre>
     * {
     *   "method": "getAvailableApiList",
     *   "params": [""],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     * 
     * @throws IOException IO
     * @return JSON data of response
     */
    public JSONObject getAvailableApiList() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson = new JSONObject().put("method", "getAvailableApiList")
                    .put("params", new JSONArray()).put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls getApplicationInfo API to the target server. Request JSON data is
     * such like as below.
     * 
     * <pre>
     * {
     *   "method": "getApplicationInfo",
     *   "params": [""],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     * 
     * @throws IOException IO
     * @return JSON data of response
     */
    public JSONObject getApplicationInfo() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson = new JSONObject().put("method", "getApplicationInfo")
                    .put("params", new JSONArray()).put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls getShootMode API to the target server. Request JSON data is such
     * like as below.
     * 
     * <pre>
     * {
     *   "method": "getShootMode",
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     * 
     * @throws IOException IO
     * @return JSON data of response
     */
    public JSONObject getShootMode() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson = new JSONObject().put("method", "getShootMode").put("params", new JSONArray())
                    .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls setShootMode API to the target server. Request JSON data is such
     * like as below.
     * 
     * <pre>
     * {
     *   "method": "setShootMode",
     *   "params": ["still"],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     * 
     * @param shootMode shoot mode (ex. "still")
     * @throws IOException IO
     * @return JSON data of response
     */
    public JSONObject setShootMode(final String shootMode) throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson = new JSONObject().put("method", "setShootMode")
                    .put("params", new JSONArray().put(shootMode)).put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls getAvailableShootMode API to the target server. Request JSON data
     * is such like as below.
     * 
     * <pre>
     * {
     *   "method": "getAvailableShootMode",
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     * 
     * @throws IOException IO
     * @return JSON data of response
     */
    public JSONObject getAvailableShootMode() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson = new JSONObject().put("method", "getAvailableShootMode")
                    .put("params", new JSONArray()).put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls getSupportedShootMode API to the target server. Request JSON data
     * is such like as below.
     * 
     * <pre>
     * {
     *   "method": "getSupportedShootMode",
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     * 
     * @throws IOException IO
     * @return JSON data of response
     */
    public JSONObject getSupportedShootMode() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson = new JSONObject().put("method", "getSupportedShootMode")
                    .put("params", new JSONArray()).put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls startLiveview API to the target server. Request JSON data is such
     * like as below.
     * 
     * <pre>
     * {
     *   "method": "startLiveview",
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     * 
     * @throws IOException IO
     * @return JSON data of response
     */
    public JSONObject startLiveview() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson = new JSONObject().put("method", "startLiveview").put("params", new JSONArray())
                    .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls stopLiveview API to the target server. Request JSON data is such
     * like as below.
     * 
     * <pre>
     * {
     *   "method": "stopLiveview",
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     * 
     * @throws IOException IO
     * @return JSON data of response
     */
    public JSONObject stopLiveview() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson = new JSONObject().put("method", "stopLiveview").put("params", new JSONArray())
                    .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls startLiveview API to the target server. Request JSON data is such
     * like as below.
     * 
     * <pre>
     * {
     *   "method": "startLiveview",
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     * 
     * @param liveviewSize liveview Size M or L
     * @throws IOException IO
     * @return JSON data of response
     */
    public JSONObject startLiveviewWithSize(final String liveviewSize) throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson = new JSONObject().put("method", "startLiveviewWithSize")
                    .put("params", new JSONArray().put(liveviewSize))
                    .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls startRecMode API to the target server. Request JSON data is such
     * like as below.
     * 
     * <pre>
     * {
     *   "method": "startRecMode",
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     * 
     * @throws IOException IO
     * @return JSON data of response
     */
    public JSONObject startRecMode() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson = new JSONObject().put("method", "startRecMode").put("params", new JSONArray())
                    .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls stopRecMode API to the target server. Request JSON data is such
     * like as below.
     * 
     * <pre>
     * {
     *   "method": "stopRecMode",
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     * 
     * @throws IOException IO
     * @return JSON data of response
     */
    public JSONObject stopRecMode() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson = new JSONObject().put("method", "stopRecMode").put("params", new JSONArray())
                    .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls actTakePicture API to the target server. Request JSON data is such
     * like as below.
     * 
     * <pre>
     * {
     *   "method": "actTakePicture",
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     * 
     * @throws IOException IO
     * @return JSON data of response
     */
    public JSONObject actTakePicture() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson = new JSONObject().put("method", "actTakePicture").put("params", new JSONArray())
                    .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls startMovieRec API to the target server. Request JSON data is such
     * like as below.
     * 
     * <pre>
     * {
     *   "method": "startMovieRec",
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     * 
     * @throws IOException IO
     * @return JSON data of response
     */
    public JSONObject startMovieRec() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson = new JSONObject().put("method", "startMovieRec").put("params", new JSONArray())
                    .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls stopMovieRec API to the target server. Request JSON data is such
     * like as below.
     * 
     * <pre>
     * {
     *   "method": "stopMovieRec",
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     * 
     * @throws IOException IO
     * @return JSON data of response
     */
    public JSONObject stopMovieRec() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson = new JSONObject().put("method", "stopMovieRec").put("params", new JSONArray())
                    .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls actZoom API to the target server. Request JSON data is such like as
     * below.
     * 
     * <pre>
     * {
     *   "method": "actZoom",
     *   "params": ["in","stop"],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     * 
     * @param direction "in, out"
     * @param movement "start, stop, 1shot"
     * @throws IOException IO
     * @return JSON data of response
     */
    public JSONObject actZoom(final String direction, final String movement) throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson = new JSONObject().put("method", "actZoom")
                    .put("params", new JSONArray().put(direction).put(movement)).put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls getEvent API to the target server. Request JSON data is such like
     * as below.
     * 
     * <pre>
     * {
     *   "method": "getEvent",
     *   "params": [true],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     * 
     * @param longPollingFlag true means long polling request.
     * @throws IOException IO
     * @return JSON data of response
     */
    public JSONObject getEvent(final boolean longPollingFlag) throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson = new JSONObject().put("method", "getEvent")
                    .put("params", new JSONArray().put(longPollingFlag)).put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;
            // msec
            int longPollingTimeout = (longPollingFlag) ? POLLING_FLAG_END : POLLING_FLAG_START;

            log("Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString(), longPollingTimeout);
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * setCurrentTime.
     * 
     * @param date ex:2014-04-01T10:10:10Z
     * @param timezone 23*60(h)+59(m)
     * @return responseJson
     * @throws IOException IO
     */
    public JSONObject setCurrentTime(final String date, final int timezone) throws IOException {
        String service = "system";
        try {
            JSONObject requestJson = new JSONObject()
                    .put("method", "setCurrentTime")
                    .put("params",
                            new JSONArray().put(new JSONObject().put("dateTime", date)
                                    .put("timeZoneOffsetMinute", timezone).put("dstOffsetMinute", 0))).put("id", id())
                    .put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Get Flash Mode State.
     * 
     * @return JSON data of response
     * @throws IOException IO
     */
    public JSONObject getFlashMode() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson = new JSONObject().put("method", "getFlashMode").put("params", new JSONArray())
                    .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * set Flash Mode.
     * 
     * @param flashMode flashMode
     * @return response
     * @throws IOException IO
     */
    public JSONObject setFlashMode(final String flashMode) throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson = new JSONObject().put("method", "setFlashMode")
                    .put("params", new JSONArray().put(flashMode)).put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * setStillSize.
     * 
     * @param aspect aspect
     * @param size size
     * @return JSONObject
     * @throws IOException IO
     */
    public JSONObject setStillSize(final String aspect, final String size) throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson = new JSONObject().put("method", "setStillSize")
                    .put("params", new JSONArray().put(aspect).put(size)).put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * getStillSize.
     * 
     * @return JSONObject
     * @throws IOException IO
     */
    public JSONObject getStillSize() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson = new JSONObject().put("method", "getStillSize").put("params", new JSONArray())
                    .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }
}
