package com.nttdocomo.android.dconnect.deviceplugin.hue.util;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import android.os.AsyncTask;
import com.nttdocomo.dconnect.message.DConnectMessage;
import com.nttdocomo.dconnect.message.basic.message.DConnectResponseMessage;
import com.nttdocomo.dconnect.message.client.DConnectClient;
import com.nttdocomo.dconnect.message.http.impl.client.HttpDConnectClient;
import com.nttdocomo.dconnect.message.http.impl.factory.HttpMessageFactory;

/**
DConnectUtil
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

/**
 * dConnectとの通信を簡略化するためのユーティリティクラス.
 * 
 */
public final class DConnectUtil {

    /** dConnectManagerのURI. */
    private static final String BASE_URI = "http://localhost:8080";

    /** Notification ProfileのURI. */
    private static final String NOTIFICATION_URI = BASE_URI + "/notification/notify";

    /** Network Service Discovery ProfileのURI. */
    private static final String DISCOVERY_URI = BASE_URI + "/network_service_discovery/getnetworkservices";

    /** Network Service Discovery ProfileのURI. */
    private static final String DISCOVERY_CHANGE_URI = BASE_URI + "/network_service_discovery/onservicechange";

    /** System ProfileのURI. */
    private static final String SYSTEM_PROFILE_URI = BASE_URI + "/system/device";

    /** 加速度センサーProfileのURI. */
    private static final String DEVICE_ORIENTATION_URI = BASE_URI + "/deviceorientation/ondeviceorientation";

    /** MediaStream Recording ProfileのURI. */
    private static final String MEDIASTREAM_RECORDING_URI = BASE_URI + "/mediastream_recording/takephoto";

    /** MediaStream Recording ProfileのURI. */
    private static final String MEDIASTREAM_ON_DATA_AVAILABLE_URI = BASE_URI + "/mediastream_recording/ondataavailable";

    /** MediaStream Recording ProfileのURI. */
    private static final String MEDIASTREAM_RECORD = BASE_URI + "/mediastream_recording/record";
    /** MediaStream Recording ProfileのURI. */
    private static final String MEDIASTREAM_STOP = BASE_URI + "/mediastream_recording/stop";

    /** File ProfileのURI. */
    private static final String FILE_URI = BASE_URI + "/file/receive";

    /**
     * コンストラクタ. ユーティリティクラスなのでprivateにしておく。
     */
    private DConnectUtil() {
    }

    /**
     * 非同期に指定されたテキストを指定されたデバイスに通知を行う.
     * 
     * @param deviceId 通知先のデバイス
     * @param body 通知するテキスト
     * @param listener 結果を通知するリスナー
     */
    public static void asyncNotifyBody(final String deviceId,
            final String body, final DConnectMessageHandler listener) {
        AsyncTask<Void, Void, DConnectMessage> task = new AsyncTask<Void, Void, DConnectMessage>() {
            @Override
            protected DConnectMessage doInBackground(final Void... params) {
                try {
                    DConnectClient client = new HttpDConnectClient();
                    HttpPost request = new HttpPost(NOTIFICATION_URI);
                    request.setEntity(new StringEntity(DConnectMessage.EXTRA_DEVICE_ID + "=" + deviceId + "&body="
                            + body));
                    HttpResponse response = client.execute(request);
                    return (new HttpMessageFactory()).newDConnectMessage(response);
                } catch (IOException e) {
                    return new DConnectResponseMessage(DConnectMessage.RESULT_ERROR);
                }
            }

            @Override
            protected void onPostExecute(final DConnectMessage message) {
                if (listener != null) {
                    listener.handleMessage(message);
                }
            }
        };
        task.execute();
    }

    /**
     * 非同期にデバイスを探索する.
     * 
     * @param listener 結果を通知するリスナー
     */
    public static void asyncSearchDevice(final DConnectMessageHandler listener) {
        AsyncTask<Void, Void, DConnectMessage> task = new AsyncTask<Void, Void, DConnectMessage>() {
            @Override
            protected DConnectMessage doInBackground(final Void... params) {
                try {
                    DConnectClient client = new HttpDConnectClient();
                    HttpGet request = new HttpGet(DISCOVERY_URI);
                    HttpResponse response = client.execute(request);
                    return (new HttpMessageFactory()).newDConnectMessage(response);
                } catch (IOException e) {
                    return new DConnectResponseMessage(DConnectMessage.RESULT_ERROR);
                }
            }

            @Override
            protected void onPostExecute(final DConnectMessage message) {
                if (listener != null) {
                    listener.handleMessage(message);
                }
            }
        };
        task.execute();
    }

    /**
     * 加速度センサーのコールバックを登録する.
     * 
     * @param deviceId デバイスID
     * @param sessionKey セッションID
     * @param listener リスナー
     */
    public static void asyncRegisterDiscovery(final String deviceId, final String sessionKey,
            final DConnectMessageHandler listener) {
        AsyncTask<Void, Void, DConnectMessage> task = new AsyncTask<Void, Void, DConnectMessage>() {
            @Override
            protected DConnectMessage doInBackground(final Void... params) {
                try {
                    DConnectClient client = new HttpDConnectClient();
                    HttpPut request = new HttpPut(DISCOVERY_CHANGE_URI + "?deviceId=" + deviceId + "&sessionKey="
                            + sessionKey);
                    HttpResponse response = client.execute(request);
                    return (new HttpMessageFactory()).newDConnectMessage(response);
                } catch (IOException e) {
                    return new DConnectResponseMessage(DConnectMessage.RESULT_ERROR);
                }
            }

            @Override
            protected void onPostExecute(final DConnectMessage message) {
                if (listener != null) {
                    listener.handleMessage(message);
                }
            }
        };
        task.execute();
    }

    /**
     * 加速度センサーのコールバックを解除する.
     * 
     * @param deviceId デバイスID
     * @param sessionKey セッションID
     * @param listener リスナー
     */
    public static void asyncUnregisterDiscovery(final String deviceId, final String sessionKey,
            final DConnectMessageHandler listener) {
        AsyncTask<Void, Void, DConnectMessage> task = new AsyncTask<Void, Void, DConnectMessage>() {
            @Override
            protected DConnectMessage doInBackground(final Void... params) {
                try {
                    DConnectClient client = new HttpDConnectClient();
                    HttpDelete request = new HttpDelete(DISCOVERY_CHANGE_URI + "?deviceId=" + deviceId + "&sessionKey="
                            + sessionKey);
                    HttpResponse response = client.execute(request);
                    return (new HttpMessageFactory()).newDConnectMessage(response);
                } catch (IOException e) {
                    return new DConnectResponseMessage(DConnectMessage.RESULT_ERROR);
                }
            }

            @Override
            protected void onPostExecute(final DConnectMessage message) {
                if (listener != null) {
                    listener.handleMessage(message);
                }
            }
        };
        task.execute();
    }

    /**
     * 非同期にSystem Profileを取得する.
     * 
     * @param deviceId デバイスID
     * @param listener リスナー
     */
    public static void asyncSystemProfile(final String deviceId, final DConnectMessageHandler listener) {
        AsyncTask<Void, Void, DConnectMessage> task = new AsyncTask<Void, Void, DConnectMessage>() {
            @Override
            protected DConnectMessage doInBackground(final Void... params) {
                try {
                    DConnectClient client = new HttpDConnectClient();
                    HttpGet request = new HttpGet(SYSTEM_PROFILE_URI + "?deviceId=" + deviceId);
                    HttpResponse response = client.execute(request);
                    return (new HttpMessageFactory()).newDConnectMessage(response);
                } catch (IOException e) {
                    return new DConnectResponseMessage(DConnectMessage.RESULT_ERROR);
                }
            }

            @Override
            protected void onPostExecute(final DConnectMessage message) {
                if (listener != null) {
                    listener.handleMessage(message);
                }
            }
        };
        task.execute();
    }

    /**
     * 加速度センサーのコールバックを登録する.
     * 
     * @param deviceId デバイスID
     * @param sessionKey セッションID
     * @param listener リスナー
     */
    public static void asyncRegistAccel(final String deviceId, final String sessionKey,
            final DConnectMessageHandler listener) {
        AsyncTask<Void, Void, DConnectMessage> task = new AsyncTask<Void, Void, DConnectMessage>() {
            @Override
            protected DConnectMessage doInBackground(final Void... params) {
                try {
                    DConnectClient client = new HttpDConnectClient();
                    HttpPut request = new HttpPut(DEVICE_ORIENTATION_URI + "?deviceId=" + deviceId + "&sessionKey="
                            + sessionKey);
                    HttpResponse response = client.execute(request);
                    return (new HttpMessageFactory()).newDConnectMessage(response);
                } catch (IOException e) {
                    return new DConnectResponseMessage(DConnectMessage.RESULT_ERROR);
                }
            }

            @Override
            protected void onPostExecute(final DConnectMessage message) {
                if (listener != null) {
                    listener.handleMessage(message);
                }
            }
        };
        task.execute();
    }

    /**
     * 加速度センサーのコールバックを解除する.
     * 
     * @param deviceId デバイスID
     * @param sessionKey セッションID
     * @param listener リスナー
     */
    public static void asyncUnregistAccel(final String deviceId, final String sessionKey,
            final DConnectMessageHandler listener) {
        AsyncTask<Void, Void, DConnectMessage> task = new AsyncTask<Void, Void, DConnectMessage>() {
            @Override
            protected DConnectMessage doInBackground(final Void... params) {
                try {
                    DConnectClient client = new HttpDConnectClient();
                    HttpDelete request = new HttpDelete(DEVICE_ORIENTATION_URI + "?deviceId=" + deviceId
                            + "&sessionKey=" + sessionKey);
                    HttpResponse response = client.execute(request);
                    return (new HttpMessageFactory()).newDConnectMessage(response);
                } catch (IOException e) {
                    return new DConnectResponseMessage(DConnectMessage.RESULT_ERROR);
                }
            }

            @Override
            protected void onPostExecute(final DConnectMessage message) {
                if (listener != null) {
                    listener.handleMessage(message);
                }
            }
        };
        task.execute();
    }

    /**
     * 加速度センサーのコールバックを登録する.
     * 
     * @param deviceId デバイスID
     * @param timeslice タイムスライス
     * @param listener リスナー
     */
    public static void asyncStartMovie(final String deviceId, final long timeslice,
            final DConnectMessageHandler listener) {
        AsyncTask<Void, Void, DConnectMessage> task = new AsyncTask<Void, Void, DConnectMessage>() {
            @Override
            protected DConnectMessage doInBackground(final Void... params) {
                try {
                    DConnectClient client = new HttpDConnectClient();
                    HttpPost request = new HttpPost(MEDIASTREAM_RECORD + "?deviceId=" + deviceId + "&timeslice="
                            + timeslice);
                    HttpResponse response = client.execute(request);
                    return (new HttpMessageFactory()).newDConnectMessage(response);
                } catch (IOException e) {
                    return new DConnectResponseMessage(DConnectMessage.RESULT_ERROR);
                }
            }

            @Override
            protected void onPostExecute(final DConnectMessage message) {
                if (listener != null) {
                    listener.handleMessage(message);
                }
            }
        };
        task.execute();
    }

    /**
     * 加速度センサーのコールバックを登録する.
     * 
     * @param deviceId デバイスID
     * @param mediaId メディアID
     * @param listener リスナー
     */
    public static void asyncStopMovie(final String deviceId,
            final String mediaId, final DConnectMessageHandler listener) {
        AsyncTask<Void, Void, DConnectMessage> task = new AsyncTask<Void, Void, DConnectMessage>() {
            @Override
            protected DConnectMessage doInBackground(final Void... params) {
                try {
                    DConnectClient client = new HttpDConnectClient();
                    HttpPut request = new HttpPut(MEDIASTREAM_STOP + "?deviceId=" + deviceId + "&mediaId=" + mediaId);
                    HttpResponse response = client.execute(request);
                    return (new HttpMessageFactory()).newDConnectMessage(response);
                } catch (IOException e) {
                    return new DConnectResponseMessage(DConnectMessage.RESULT_ERROR);
                }
            }

            @Override
            protected void onPostExecute(final DConnectMessage message) {
                if (listener != null) {
                    listener.handleMessage(message);
                }
            }
        };
        task.execute();
    }

    /**
     * 指定したデバイスIDの端末で写真撮影を行う.
     * 
     * @param deviceId デバイスID
     * @param listener リスナー
     */
    public static void asynTakePhoto(final String deviceId, final DConnectMessageHandler listener) {
        AsyncTask<Void, Void, DConnectMessage> task = new AsyncTask<Void, Void, DConnectMessage>() {
            @Override
            protected DConnectMessage doInBackground(final Void... params) {
                try {
                    DConnectClient client = new HttpDConnectClient();
                    HttpPost request = new HttpPost(MEDIASTREAM_RECORDING_URI + "?deviceId=" + deviceId);
                    HttpResponse response = client.execute(request);
                    return (new HttpMessageFactory()).newDConnectMessage(response);
                } catch (IOException e) {
                    return new DConnectResponseMessage(DConnectMessage.RESULT_ERROR);
                }
            }

            @Override
            protected void onPostExecute(final DConnectMessage message) {
                if (listener != null) {
                    listener.handleMessage(message);
                }
            }
        };
        task.execute();
    }

    /**
     * 加速度センサーのコールバックを登録する.
     * 
     * @param deviceId デバイスID
     * @param sessionKey セッションID
     * @param listener リスナー
     */
    public static void asyncRegisterOnDataAvaible(final String deviceId, final String sessionKey,
            final DConnectMessageHandler listener) {
        AsyncTask<Void, Void, DConnectMessage> task = new AsyncTask<Void, Void, DConnectMessage>() {
            @Override
            protected DConnectMessage doInBackground(final Void... params) {
                try {
                    DConnectClient client = new HttpDConnectClient();
                    HttpPut request = new HttpPut(MEDIASTREAM_ON_DATA_AVAILABLE_URI + "?deviceId=" + deviceId
                            + "&sessionKey=" + sessionKey);
                    HttpResponse response = client.execute(request);
                    return (new HttpMessageFactory()).newDConnectMessage(response);
                } catch (IOException e) {
                    return new DConnectResponseMessage(DConnectMessage.RESULT_ERROR);
                }
            }

            @Override
            protected void onPostExecute(final DConnectMessage message) {
                if (listener != null) {
                    listener.handleMessage(message);
                }
            }
        };
        task.execute();
    }

    /**
     * 加速度センサーのコールバックを解除する.
     * 
     * @param deviceId デバイスID
     * @param sessionKey セッションID
     * @param listener リスナー
     */
    public static void asyncUnregisterOnDataAvaible(final String deviceId, final String sessionKey,
            final DConnectMessageHandler listener) {
        AsyncTask<Void, Void, DConnectMessage> task = new AsyncTask<Void, Void, DConnectMessage>() {
            @Override
            protected DConnectMessage doInBackground(final Void... params) {
                try {
                    DConnectClient client = new HttpDConnectClient();
                    HttpDelete request = new HttpDelete(MEDIASTREAM_ON_DATA_AVAILABLE_URI + "?deviceId=" + deviceId
                            + "&sessionKey=" + sessionKey);
                    HttpResponse response = client.execute(request);
                    return (new HttpMessageFactory()).newDConnectMessage(response);
                } catch (IOException e) {
                    return new DConnectResponseMessage(DConnectMessage.RESULT_ERROR);
                }
            }

            @Override
            protected void onPostExecute(final DConnectMessage message) {
                if (listener != null) {
                    listener.handleMessage(message);
                }
            }
        };
        task.execute();
    }

    /**
     * 指定したデバイスIDの端末からファイルを取得sうる.
     * 
     * @param deviceId デバイスID
     * @param mediaId メディアID
     * @param listener リスナー
     */
    public static void asynFileReceive(final String deviceId, final String mediaId,
            final DConnectMessageHandler listener) {
        AsyncTask<Void, Void, DConnectMessage> task = new AsyncTask<Void, Void, DConnectMessage>() {
            @Override
            protected DConnectMessage doInBackground(final Void... params) {
                try {
                    DConnectClient client = new HttpDConnectClient();
                    HttpGet request = new HttpGet(FILE_URI + "?deviceId=" + deviceId + "&mediaId=" + mediaId);
                    HttpResponse response = client.execute(request);
                    return (new HttpMessageFactory()).newDConnectMessage(response);
                } catch (IOException e) {
                    return new DConnectResponseMessage(DConnectMessage.RESULT_ERROR);
                }
            }

            @Override
            protected void onPostExecute(final DConnectMessage message) {
                if (listener != null) {
                    listener.handleMessage(message);
                }
            }
        };
        task.execute();
    }

    /**
     * 指定されたURIのデータを取得する.
     * 
     * @param uri データが置いてあるURI
     * @return データ
     */
    public static byte[] getBytes(final String uri) {
        HttpGet request = new HttpGet(uri);
        DefaultHttpClient httpClient = new DefaultHttpClient();
        try {
            byte[] result = httpClient.execute(request, new ResponseHandler<byte[]>() {
                @Override
                public byte[] handleResponse(final HttpResponse response) throws IOException {
                    switch (response.getStatusLine().getStatusCode()) {
                    case HttpStatus.SC_OK:
                        return EntityUtils.toByteArray(response.getEntity());
                    case HttpStatus.SC_NOT_FOUND:
                        throw new RuntimeException("No Found.");
                    default:
                        throw new RuntimeException("Connection Error.");
                    }
                }
            });
            return result;
        } catch (ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
    }
}
