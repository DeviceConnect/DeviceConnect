package com.nttdocomo.android.dconnect.deviceplugin.irkit;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.ContextWrapper;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * IRKitの操作をするクラス.
 * 
 */
public enum IRKitManager {
    /**
     * シングルトンなIRKitManagerのインスタンス.
     */
    INSTANCE;

    /**
     * Httpリクエスト ステータスコード 200.
     */
    private static final int STATUS_CODE_OK = 200;

    /**
     * IP解決のタイムアウト {@value} ミリ秒.
     */
    private static final int RESOLVE_TIMEOUT = 500;

    /**
     * HTTPリクエストのタイムアウト {@value} ミリ秒.
     */
    private static final int HTTP_REQUEST_TIMEOUT = 5000;

    /**
     * HTTPリクエストのタイムアウト {@value} ミリ秒.
     */
    private static final int HTTP_REQUEST_LONG_TIMEOUT = 30000;

    /**
     * パスワードの最大長.
     */
    private static final int MAX_PASSWORD_LENGTH = 63;

    /**
     * SSIDの最大長.
     */
    private static final int MAX_SSID_LENGTH = 32;

    /**
     * デバイスキーの最大長.
     */
    private static final int MAX_DEVICE_KEY_LENGTH = 32;

    /**
     * IRKitのサービスタイプ.
     */
    private static final String SERVICE_TYPE = "_irkit._tcp.local.";

    /**
     * IRKit AIPサーバーのホスト.
     */
    private static final String INTERNET_HOST = "api.getirkit.com";

    /**
     * IRKitをWiFiスポットにした場合のホスト.
     */
    public static final String DEVICE_HOST = "192.168.1.1";

    /**
     * マルチキャスト用のタグ.
     */
    private static final String MULTI_CAST_LOCK_TAG = "com.nttdocomo.android.dconnect.deviceplugin.irkit";

    /**
     * 16進数変換用コード.
     */
    private static final char[] HEX_CODE = "0123456789ABCDEF".toCharArray();

    /**
     * 日本以外の国リスト. regdomainの決定に利用する。
     */
    private static final String[] NOT_JP_COUNTRIES = {"CA", "MX", "US", "AU", "HK", "IN", "MY", "NZ", "PH", "TW",
            "RU", "AR", "BR", "CL", "CO", "CR", "DO", "DM", "EC", "PA", "PY", "PE", "PR", "VE" };

    /**
     * 検知リスナー.
     */
    private DetectionListener mDetectionListener;

    /**
     * DNSクラス.
     */
    private JmDNS mDNS;

    /**
     * サービス検知リスナー.
     */
    private ServiceListener mServiceListener;

    /**
     * IPのint値.
     */
    private int mIpValue;

    /**
     * 検知中フラグ.
     */
    private boolean mIsDetecting;

    /**
     * apikey.
     */
    private String mAPIKey;

    /**
     * regDomainを決定するためのキャリアの国コード.
     */
    private String mCountryCode;
    
    /** 
     * マルチキャストロック.
     */
    private MulticastLock mMultiLock;
    
    /** 
     * 検知したサービス一覧.
     */
    private ConcurrentHashMap<String, IRKitDevice> mServices;
    
    /** 
     * 消失検出ハンドラ.
     */
    private ServiceRemovingDiscoveryHandler mRemoveHandler;

    /**
     * WiFiのセキュリティタイプ.
     */
    public enum WiFiSecurityType {

        /**
         * 無し.
         */
        NONE(0),

        /**
         * WEP.
         */
        WEP(2),

        /**
         * WPA2.
         */
        WPA2(8);

        /**
         * コード.
         */
        int mCode;

        /**
         * 指定したコードを持つセキュリティタイプを定義する.
         * 
         * @param code コード
         */
        private WiFiSecurityType(final int code) {
            mCode = code;
        }
    }

    /**
     * IRKitManagerのインスタンスを生成する.
     */
    private IRKitManager() {
        mServiceListener = new ServiceListenerImpl();
        mServices = new ConcurrentHashMap<String, IRKitDevice>();
    }

    /**
     * 指定されたHostとPathへのGETリクエストを生成する.
     * 
     * @param host ホスト名
     * @param path パス
     * @return HttpGetのインスタンス
     */
    private HttpGet createGetRequest(final String host, final String path) {
        if (BuildConfig.DEBUG) {
            Log.d("IRKit", "http://" + host + path);
        }
        HttpGet req = new HttpGet("http://" + host + path);
        return req;
    }

    /**
     * 指定されたHostとPathへのPOSTリクエストを生成する.
     * 
     * @param host ホスト名
     * @param path パス
     * @return HttpPostのインスタンス
     */
    private HttpPost createPostRequest(final String host, final String path) {
        HttpPost req = new HttpPost("http://" + host + path);
        return req;
    }

    /**
     * HttpClientを作成する.
     * 
     * @return HttpClientのインスタンス
     */
    private HttpClient createClient() {
        return createClient(HTTP_REQUEST_TIMEOUT);
    }

    /**
     * HttpClientを作成する.
     * 
     * @param timeout タイムアウト
     * @return HttpClientのインスタンス
     */
    private HttpClient createClient(final int timeout) {
        HttpClient client = new DefaultHttpClient();
        HttpParams params = client.getParams();
        HttpConnectionParams.setSoTimeout(params, timeout);
        HttpConnectionParams.setConnectionTimeout(params, timeout);
        return client;
    }

    /**
     * リクエストを実行する.
     * 
     * @param req リクエスト
     * @param client クライアント
     * @return レスポンスボディ。失敗、無い場合はnullを返す。
     */
    private String executeRequest(final HttpUriRequest req, final HttpClient client) {
        String body = null;
        HttpEntity entity = null;
        try {
            HttpResponse res = client.execute(req);
            if (res.getStatusLine().getStatusCode() == STATUS_CODE_OK) {
                entity = res.getEntity();
                body = EntityUtils.toString(entity);
            }
            
        } catch (Exception e) {
            body = null;
        } finally {
            if (entity != null) {
                try {
                    entity.consumeContent();
                } catch (IOException e) {
                    // フリーできない場合は特にリカバリーできないのであきらめる
                    if (BuildConfig.DEBUG) {
                        e.printStackTrace();
                    }
                }
            }

            client.getConnectionManager().shutdown();
        }

        return body;
    }

    /**
     * WiFi接続時のIPアドレスを解析する.
     * 
     * @param ipValue IPアドレスのint値
     * @return InetAddressのインスタンス。失敗した場合はnullを返す。
     */
    private InetAddress parseIPAddress(final int ipValue) {
        byte[] byteaddr = new byte[] {(byte) (ipValue & 0xff), (byte) (ipValue >> 8 & 0xff),
                (byte) (ipValue >> 16 & 0xff), (byte) (ipValue >> 24 & 0xff)};
        try {
            return InetAddress.getByAddress(byteaddr);
        } catch (UnknownHostException e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
            // IPが取れなければ検知不可能として処理させる。
            return null;
        }
    }

    /**
     * 文字列を16進数に変換する.
     * 
     * @param ori 変換元の文字列
     * @param maxLength 変換する最大長
     * @return 返還後の文字列
     */
    private String toHex(final String ori, final int maxLength) {

        String tmp = ori;
        if (tmp.length() > maxLength) {
            tmp = tmp.substring(0, maxLength);
        }

        byte[] data = tmp.getBytes();
        StringBuilder sb = new StringBuilder(data.length * 2);
        for (byte b : data) {
            sb.append(HEX_CODE[(b >> 4) & 0xF]);
            sb.append(HEX_CODE[(b & 0xF)]);
        }

        return sb.toString();
    }

    /**
     * CRC8変換.
     * 
     * @param data 変換元データ
     * @return 変換したデータ
     */
    private int crc8(final byte[] data) {

        int crc = 0;

        for (int i = 0; i < data.length; i++) {
            crc ^= (data[i] & 0xFF);
            for (int j = 0; j < 8; j++) {
                if ((crc & 0x80) != 0) {
                    crc = ((crc << 1) ^ 0x31) & 0xFF;
                } else {
                    crc <<= 1;
                }
            }
        }

        return crc;
    }

    /**
     * WiFiへの接続情報をシリアライズするためにbyte配列に変換する.
     * 
     * @param type セキュリティタイプ
     * @param ssid SSID
     * @param password パスワード
     * @param deviceKey デバイスキー
     * @return byte配列
     */
    private byte[] toBytes(final WiFiSecurityType type, final String ssid, final String password, 
            final String deviceKey) {

        ByteArrayBuffer buffer = new ByteArrayBuffer(MAX_DEVICE_KEY_LENGTH + 1 + MAX_PASSWORD_LENGTH + 1
                + MAX_SSID_LENGTH + 1 + 3); // セキュリティタイプ + wifi_is_set +
                                            // wifi_is_valid

        byte[] ssidByte = toBytes(ssid, MAX_SSID_LENGTH + 1);
        byte[] passByte = toBytes(password, MAX_PASSWORD_LENGTH + 1);
        byte[] keyByte = toBytes(deviceKey, MAX_DEVICE_KEY_LENGTH + 1);

        buffer.append(type.mCode);
        buffer.append(ssidByte, 0, ssidByte.length);
        buffer.append(passByte, 0, passByte.length);
        buffer.append(1); // wifi_is_set = true
        buffer.append(0); // wifi_is_valid = false
        buffer.append(keyByte, 0, keyByte.length);

        return buffer.toByteArray();
    }

    /**
     * 文字列を指定された長さのbyte配列に変換する.
     * 
     * @param str 文字列
     * @param length 長さ
     * @return byte配列
     */
    private byte[] toBytes(final String str, final int length) {

        byte[] res = new byte[length];
        byte[] data = str.getBytes();

        for (int i = 0; i < length; i++) {
            if (i == data.length) {
                break;
            }
            res[i] = data[i];
        }

        return res;
    }

    /**
     * サービスを登録する.
     * 
     * @param device サービス
     */
    private void addService(final IRKitDevice device) {
        mServices.put(device.getName(), device);
    }
    
    /**
     * サービスを削除する.
     * 
     * @param device サービス
     */
    private synchronized void removeService(final IRKitDevice device) {
        
        if (!isDetecting()) {
            return;
        }
        
        mServices.remove(device.getName());
        
        if (BuildConfig.DEBUG) {
            Log.d("", "Lost Device : " + device);
        }
        
        if (mDetectionListener != null) {
            mDetectionListener.onLostDevice(device);
        }
    }
    
    /**
     * regdomainを取得する.
     * 
     * @return regdomain
     */
    private String getRegDomain() {

        String regdomain = null;

        if (mCountryCode == null) {
            Locale locale = Locale.getDefault();
            mCountryCode = locale.getCountry();
        }

        if ("JP".equals(mCountryCode)) {
            regdomain = "2";
        } else {

            for (String code : NOT_JP_COUNTRIES) {
                if (code.equals(mCountryCode)) {
                    regdomain = "0";
                    break;
                }
            }

            if (regdomain == null) {
                regdomain = "1";
            }
        }
        return regdomain;
    }
    
    /**
     * 初期化を実行する.
     * 
     * @param context コンテキストオブジェクト。コンテキストは保持されない。
     */
    public void init(final ContextWrapper context) {
        mAPIKey = context.getString(R.string.apikey);

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        mCountryCode = tm.getSimCountryIso().toUpperCase(Locale.ENGLISH);
    }

    /**
     * 端末検知リスナーを設定する.
     * 
     * @param listener DetectionListenerのインスタンス
     */
    public synchronized void setDetectionListener(final DetectionListener listener) {
        mDetectionListener = listener;
    }

    /**
     * 検知中かどうか.
     * 
     * @return 検知中はtrue、その他はfalse
     */
    public synchronized boolean isDetecting() {
        return mIsDetecting;
    }

    /**
     * 端末検知を開始する.
     * 
     * @param context コンテキスト
     */
    public synchronized void startDetection(final ContextWrapper context) {
        
        if (isDetecting()) {
            return;
        }
        
        mIsDetecting = true;
        init(context);
        
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mMultiLock = wifi.createMulticastLock(MULTI_CAST_LOCK_TAG);
        mMultiLock.setReferenceCounted(true);
        mMultiLock.acquire();

        WifiInfo info = wifi.getConnectionInfo();
        if (info != null) {
            mIpValue = info.getIpAddress();
        } else {
            mIpValue = 0;
        }
        
        new Thread(new Runnable() {

            @Override
            public void run() {
                synchronized (INSTANCE) {
                    try {
                        if (mDNS != null || mIpValue == 0) {
                            mIsDetecting = false;
                            return;
                        }
                        InetAddress ia = parseIPAddress(mIpValue);
                        if (ia == null) {
                            mIsDetecting = false;
                            return;
                        }

                        mDNS = JmDNS.create(ia);
                        mDNS.addServiceListener(SERVICE_TYPE, mServiceListener);
                        mIsDetecting = true;
                        
                        if (BuildConfig.DEBUG) {
                            Log.d("IRKit", "start detection.");
                        }
                        
                    } catch (IOException e) {
                        if (BuildConfig.DEBUG) {
                            e.printStackTrace();
                        }
                        mIsDetecting = false;
                        mDNS = null;
                    }
                }
            }
        }).start();
    }

    /**
     * 端末検知を終了する.
     */
    public synchronized void stopDetection() {
        if (mDNS != null) {
            mRemoveHandler = null;
            mIsDetecting = false;
            mServices.clear();
            mDNS.removeServiceListener(SERVICE_TYPE, mServiceListener);
            try {
                mDNS.close();
                if (BuildConfig.DEBUG) {
                    Log.d("IRKit", "close detection.");
                }
            } catch (IOException e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            } finally {
                // クローズできなかった場合は特に復旧できなさそうなので参照だけきっておく。
                mDNS = null;
            }
        }
        
        if (mMultiLock != null && mMultiLock.isHeld()) {
            mMultiLock.release();
            mMultiLock = null;
        }
    }

    /**
     * 指定したIPに紐づくIRKitから赤外線データを取得する.
     * 
     * @param ip IRKitのIPアドレス
     * @param callback 処理結果を受けるコールバック
     */
    public void fetchMessage(final String ip, final GetMessageCallback callback) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                HttpGet req = createGetRequest(ip, "/messages");
                HttpClient client = createClient();
                String message = executeRequest(req, client);
                callback.onGetMessage(message);
            }
        }).start();
    }

    /**
     * 赤外線データを送信する.
     * 
     * @param ip IRKitのIP
     * @param message 赤外線データ
     * @param callback 処理結果の通知を受けるコールバック
     */
    public void sendMessage(final String ip, final String message, final PostMessageCallback callback) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                HttpPost req = createPostRequest(ip, "/messages");
                HttpClient client = createClient();
                boolean result = false;
                try {
                    if (BuildConfig.DEBUG) {
                        Log.d("IRKit", "ip=" + ip + " post message : " + message);
                    }
                    StringEntity body = new StringEntity(message);
                    req.setEntity(body);
                    HttpResponse res = client.execute(req);
                    if (res.getStatusLine().getStatusCode() == STATUS_CODE_OK) {
                        result = true;
                    }
                } catch (ClientProtocolException e) {
                    if (BuildConfig.DEBUG) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    if (BuildConfig.DEBUG) {
                        e.printStackTrace();
                    }
                } finally {
                    client.getConnectionManager().shutdown();
                    callback.onPostMessage(result);
                }

            }
        }).start();
    }

    /**
     * clientkeyを取得する.
     * 
     * @param callback コールバック
     */
    public void fetchClientKey(final GetClientKeyCallback callback) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                String clientKey = null;

                do {
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("apikey", mAPIKey));
                    UrlEncodedFormEntity param = null;
                    try {
                        param = new UrlEncodedFormEntity(nameValuePairs);
                    } catch (UnsupportedEncodingException e) {
                        if (BuildConfig.DEBUG) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    HttpPost req = createPostRequest(INTERNET_HOST, "/1/clients");
                    HttpClient client = createClient(HTTP_REQUEST_LONG_TIMEOUT);

                    req.setEntity(param);
                    String body = executeRequest(req, client);

                    if (body == null) {
                        break;
                    }

                    try {
                        JSONObject json = new JSONObject(body);
                        clientKey = json.getString("clientkey");
                    } catch (JSONException e) {
                        if (BuildConfig.DEBUG) {
                            e.printStackTrace();
                        }
                        clientKey = null;
                        break;
                    }
                } while (false);

                callback.onGetClientKey(clientKey);
            }
        }).start();
    }

    /**
     * 新規にデバイスを生成する.
     * 
     * @param clientKey clientkey
     * @param callback コールバック
     */
    public void createNewDevice(final String clientKey, final GetNewDeviceCallback callback) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                String deviceKey = null;
                String deviceId = null;
                do {
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("clientkey", clientKey));
                    UrlEncodedFormEntity param = null;
                    try {
                        param = new UrlEncodedFormEntity(nameValuePairs);
                    } catch (UnsupportedEncodingException e) {
                        if (BuildConfig.DEBUG) {
                            e.printStackTrace();
                        }
                        break;
                    }

                    HttpPost req = createPostRequest(INTERNET_HOST, "/1/devices");
                    HttpClient client = createClient(HTTP_REQUEST_LONG_TIMEOUT);

                    req.setEntity(param);
                    String body = executeRequest(req, client);

                    if (body == null) {
                        break;
                    }

                    try {
                        JSONObject json = new JSONObject(body);
                        deviceId = json.getString("deviceid");
                        deviceKey = json.getString("devicekey");
                    } catch (JSONException e) {
                        if (BuildConfig.DEBUG) {
                            e.printStackTrace();
                        }
                        deviceId = null;
                        deviceKey = null;
                        break;
                    }
                } while (false);

                callback.onGetDevice(deviceId, deviceKey);
            }
        }).start();
    }

    /**
     * IRKitをWiFiへ接続させる.
     * 
     * @param ssid 接続させるWiFiのSSID
     * @param password パスワード
     * @param type セキュリティタイプ
     * @param deviceKey デバイスキー
     * @param callback コールバック
     */
    public void connectIRKitToWiFi(final String ssid, final String password, final WiFiSecurityType type,
            final String deviceKey, final IRKitConnectionCallback callback) {
        new Thread(new Runnable() {

            @Override
            public void run() {

                String ssidHex = toHex(ssid, MAX_SSID_LENGTH + 1);
                String tmpPassword = password;
                if (type == WiFiSecurityType.WEP && (password.length() == 5 || password.length() == 13)) {
                    tmpPassword = toHex(password, MAX_PASSWORD_LENGTH);
                }
                String passHex = toHex(tmpPassword, MAX_PASSWORD_LENGTH);
                byte[] crcData = toBytes(type, ssid, tmpPassword, deviceKey);
                int crc = crc8(crcData);
                String crcHex = String.format("%02x", crc);
                String regdomain = getRegDomain();
                String postData = String.format(Locale.ENGLISH, "%d/%s/%s/%s/%s//////%s", type.mCode, ssidHex, passHex,
                        deviceKey, regdomain, crcHex).toUpperCase(Locale.ENGLISH);

                HttpPost req = createPostRequest(DEVICE_HOST, "/wifi");
                HttpClient client = createClient();
                boolean result = false;
                try {
                    if (BuildConfig.DEBUG) {
                        Log.d("IRKit", "body : " + postData);
                    }
                    StringEntity body = new StringEntity(postData);
                    req.setEntity(body);
                    HttpResponse res = client.execute(req);
                    if (res.getStatusLine().getStatusCode() == STATUS_CODE_OK) {
                        result = true;
                    }
                } catch (ClientProtocolException e) {
                    if (BuildConfig.DEBUG) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    if (BuildConfig.DEBUG) {
                        e.printStackTrace();
                    }
                } finally {
                    client.getConnectionManager().shutdown();
                    callback.onConnectedToWiFi(result);
                }
            }
        }).start();
    }
    
    /**
     * 指定されたIPのデバイスがIRKitかをチェックする.
     * 
     * @param ip IPアドレス
     * @param callback コールバック
     */
    public void checkIfTargetIsIRKit(final String ip, final CheckingIRKitCallback callback) {
        new Thread(new Runnable() {

            @Override
            public void run() {

                boolean isIRKit = false;
                HttpGet req = createGetRequest(ip, "/");
                HttpClient client = createClient(HTTP_REQUEST_TIMEOUT);
                try {
                    HttpResponse res = client.execute(req);
                    Header[] headers = res.getAllHeaders();
                    for (Header h : headers) {
                        if (h.getName().equals("Server") && h.getValue().contains("IRKit")) {
                            isIRKit = true;
                            break;
                        }
                    }
                } catch (Exception e) {
                    if (BuildConfig.DEBUG) {
                        e.printStackTrace();
                    }
                } finally {
                    client.getConnectionManager().shutdown();
                }

                callback.onChecked(isIRKit);
            }
        }).start();
    }
    
    /**
     * IRKitがインターネットに接続したかをチェックする.
     * 
     * @param clientKey クライアントキー
     * @param deviceId デバイスID
     * @param callback コールバック
     */
    public void checkIfIRKitIsConnectedToInternet(final String clientKey, final String deviceId, 
            final IRKitConnectionCheckingCallback callback) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                String hostName = null;
                do {
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("clientkey", clientKey));
                    nameValuePairs.add(new BasicNameValuePair("deviceid", deviceId));
                    UrlEncodedFormEntity param = null;
                    try {
                        param = new UrlEncodedFormEntity(nameValuePairs);
                    } catch (UnsupportedEncodingException e) {
                        if (BuildConfig.DEBUG) {
                            e.printStackTrace();
                        }
                        break;
                    }

                    HttpPost req = createPostRequest(INTERNET_HOST, "/1/door");
                    HttpClient client = createClient(HTTP_REQUEST_LONG_TIMEOUT);

                    req.setEntity(param);
                    String body = executeRequest(req, client);

                    if (body == null) {
                        break;
                    }

                    try {
                        JSONObject json = new JSONObject(body);
                        hostName = json.getString("hostname");
                    } catch (JSONException e) {
                        if (BuildConfig.DEBUG) {
                            e.printStackTrace();
                        }
                        hostName = null;
                        break;
                    }
                } while (false);
                
                callback.onConnectedToInternet(hostName != null);
            }
        }).start();
    }

    /**
     * 検知の通知を受けるリスナー.
     */
    public interface DetectionListener {

        /**
         * デバイスを検知したことを通知する.
         * 
         * @param device 検知したIRKitのデバイス
         */
        void onFoundDevice(IRKitDevice device);

        /**
         * デバイスが消失したことを通知する.
         * 
         * @param device 消失したIRKitのデバイス
         */
        void onLostDevice(IRKitDevice device);
    }

    /**
     * 赤外線データの取得リクエストコールバック.
     */
    public interface GetMessageCallback {

        /**
         * 赤外線データを受け取ったことを通知する.
         * 
         * @param message 赤外線データ。取れなかった場合はnull。
         */
        void onGetMessage(String message);
    }

    /**
     * 赤外線データの送信リクエストコールバック.
     */
    public interface PostMessageCallback {
        /**
         * 送信が完了したことを通知する.
         * 
         * @param result 成功した場合true、その他はfalseを返す。
         */
        void onPostMessage(boolean result);
    }

    /**
     * clientkeyの取得リクエストコールバック.
     */
    public interface GetClientKeyCallback {

        /**
         * clientkeyを取得したことを通知する.
         * 
         * @param clientKey クライアントキー。取得できなかった場合はnullを返す。
         */
        void onGetClientKey(String clientKey);
    }

    /**
     * 新規デバイスの取得リクエストコールバック.
     */
    public interface GetNewDeviceCallback {

        /**
         * 新規デバイスが生成できたことを通知する.
         * 
         * @param deviceId デバイスID
         * @param deviceKey デバイスキー
         */
        void onGetDevice(String deviceId, String deviceKey);
    }

    /**
     * IRKitのWiFiへの接続処理完了コールバック.
     */
    public interface IRKitConnectionCallback {

        /**
         * WiFiへの接続処理が完了したことを通知する.
         * 
         * @param isConnect 接続成功ならtrue、その他はfalseを返す。
         */
        void onConnectedToWiFi(boolean isConnect);
    }
    
    /**
     * IRKitのインターネットへの接続確認完了コールバック.
     */
    public interface IRKitConnectionCheckingCallback {

        /**
         * インターネットへの接続確認が完了したことを通知する.
         * 
         * @param isConnect 接続成功ならtrue、その他はfalseを返す。
         */
        void onConnectedToInternet(boolean isConnect);
    }
    
    /**
     * IRKitかどうかのチェック完了コールバック.
     */
    public interface CheckingIRKitCallback {

        /**
         * IRKitかどうかの判断が出来たことを通知する.
         * 
         * @param isIRKit IRKitならtrue、その他はfalseを返す。
         */
        void onChecked(boolean isIRKit);
    }

    /**
     * サービス検知のリスナー実装.
     */
    private class ServiceListenerImpl implements ServiceListener {

        @Override
        public void serviceAdded(final ServiceEvent event) {
            if (BuildConfig.DEBUG) {
                Log.d("IRKit", "serviceAdded");
            }
            synchronized (INSTANCE) {
                if (mDetectionListener != null) {
                    mDNS.requestServiceInfo(SERVICE_TYPE, event.getName(), RESOLVE_TIMEOUT);
                }
            }
        }

        @Override
        public void serviceRemoved(final ServiceEvent event) {
            // do-nothing.
        }

        @Override
        public void serviceResolved(final ServiceEvent event) {
            ServiceInfo info = event.getInfo();
            String ip = null;
            
            Inet4Address[] ipv4 = info.getInet4Addresses();
            if (ipv4 != null && ipv4.length != 0) {
                ip = ipv4[0].toString();
            } else {
                Inet6Address[] ipv6 = info.getInet6Addresses();
                if (ipv6 != null && ipv6.length != 0) {
                    ip = ipv6[0].toString();
                }
            }

            if (ip == null) {
                // IPが解決できない場合は通知しない。
                return;
            }

            IRKitDevice device = new IRKitDevice();
            device.setName(info.getName().toUpperCase(Locale.ENGLISH));
            ip = ip.replace("/", ""); // /が前に入っているので削除する.
            device.setIp(ip);

            synchronized (INSTANCE) {
                Log.d("", "added lock");
                addService(device);
                
                if (BuildConfig.DEBUG) {
                    Log.d("IRKit", "serviceResolved ip=" + ip);
                    Log.d("IRKit", "device=" + device);
                    Log.d("IRKit", "devicename=" + device.getName());
                }
                
                if (mRemoveHandler == null) {
                    mRemoveHandler = new ServiceRemovingDiscoveryHandler();
                    mRemoveHandler.start();
                } else {
                    mRemoveHandler.refresh();
                }
                
                mDetectionListener.onFoundDevice(device);
            }
        }
    }
    
    /**
     * サービス消失検知ハンドラ.
     */
    private class ServiceRemovingDiscoveryHandler extends Thread {
        
        /** 
         * インターバル最大値.
         */
        private static final long MAX_INTERVAL = 4096 * 1000; 
        
        /** 
         * ロックオブジェクト.
         */
        private Object mLock;
        
        /** 
         * カウンタ.
         */
        private int mCounter;
        
        /** 
         * インターバル.
         */
        private long mNextInterval = 1;
        
        /** 
         * 待ち時間.
         */
        private long mDelay;
        
        /** 
         * 削除リスト.
         */
        private ArrayList<IRKitDevice> mRemoveList = new ArrayList<IRKitDevice>();
        
        /**
         * インスタンスの生成.
         */
        public ServiceRemovingDiscoveryHandler() {
            mNextInterval = 1;
            mLock = new Object();
        }
        
        /**
         * 検索インターバルをリフレッシュする.
         */
        public synchronized void refresh() {
            interrupt();
            mDelay = 0;
            mNextInterval = 1;
        }
        
        @Override
        public void run() {
            super.run();
            
            while (true) {
                
                if (!isDetecting()) {
                    break;
                }
                
                long pt = checkConnection();
                
                if (BuildConfig.DEBUG) {
                    Log.d("IRKit", "Check Time : " + pt);
                }
                
                synchronized (this) {
                    mNextInterval <<= 1;
                    mDelay = (mNextInterval * 1000) - pt;
                    if (mDelay < 0) {
                        mDelay = 0;
                    } else if (mDelay > MAX_INTERVAL) {
                        mDelay = MAX_INTERVAL;
                        mNextInterval = MAX_INTERVAL;
                    }
                }
                
                Log.d("IRKit", "start remove checking. " + mDelay);
                
                try {
                    if (isInterrupted()) {
                        throw new InterruptedException();
                    }
                    sleep(mDelay);
                } catch (InterruptedException e) {
                    if (BuildConfig.DEBUG) {
                        e.printStackTrace();
                    }
                    mNextInterval = 1;
                }
            }
        }

        
        /**
         * IRKitの接続チェックをする.
         * 
         * @return 実行時間
         */
        private long checkConnection() {
            mCounter = 0;
            mRemoveList.clear();
            long start = System.currentTimeMillis();
            synchronized (mServices) {
                final int max = mServices.size();
                for (final IRKitDevice device : mServices.values()) {
                    checkIfTargetIsIRKit(device.getIp(), new CheckingIRKitCallback() {
                        @Override
                        public void onChecked(final boolean isIRKit) {
                            synchronized (mRemoveList) {
                                
                                if (!isIRKit) {
                                    mRemoveList.add(device);
                                }
                                
                                if (++mCounter == max) {
                                    synchronized (mLock) {
                                        mLock.notifyAll();
                                    }
                                }
                            }
                        }
                    });
                }
            }
            
            try {
                synchronized (mLock) {
                    mLock.wait();
                }
            } catch (InterruptedException e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            } finally {
                for (IRKitDevice device : mRemoveList) {
                    removeService(device);
                }
            }
            
            return System.currentTimeMillis() - start;
        }
    }
}
