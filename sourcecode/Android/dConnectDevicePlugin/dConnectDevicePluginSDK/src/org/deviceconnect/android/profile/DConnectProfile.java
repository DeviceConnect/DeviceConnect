/*
 DConnectProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.profile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.message.intent.message.IntentDConnectMessage;
import org.deviceconnect.profile.DConnectProfileConstants;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

/**
 * DConnect プロファイルクラス.
 * @author NTT DOCOMO, INC.
 */
public abstract class DConnectProfile implements DConnectProfileConstants {

    /** バッファサイズを定義. */
    private static final int BUF_SIZE = 4096;

    /**
     * コンテキスト.
     */
    private Context mContext = null;

    /**
     * ロガー.
     */
    protected Logger mLogger = Logger.getLogger("org.deviceconnect.dplugin");

    /**
     * プロファイル名を取得する.
     * 
     * @return プロファイル名
     */
    public abstract String getProfileName();

    /**
     * RESPONSEメソッドハンドラー.<br>
     * リクエストパラメータに応じてデバイスのサービスを提供し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @return レスポンスパラメータを送信するか否か
     */
    public boolean onRequest(final Intent request, final Intent response) {
        String action = request.getAction();
        boolean send = true;
        try {
            if (IntentDConnectMessage.ACTION_GET.equals(action)) {
                send = onGetRequest(request, response);
            } else if (IntentDConnectMessage.ACTION_POST.equals(action)) {
                send = onPostRequest(request, response);
            } else if (IntentDConnectMessage.ACTION_PUT.equals(action)) {
                send = onPutRequest(request, response);
            } else if (IntentDConnectMessage.ACTION_DELETE.equals(action)) {
                send = onDeleteRequest(request, response);
            } else {
                mLogger.warning("Unknown action. action=" + action);
                MessageUtils.setNotSupportActionError(response);
            }
        } catch (Exception e) {
            mLogger.severe("Exception occurred in the profile. " + e.getMessage());
            MessageUtils.setUnknownError(response, e.getMessage());
        }
        return send;
    }

    /**
     * GETメソッドハンドラー.<br>
     * リクエストパラメータに応じてデバイスのサービスを提供し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onGetRequest(final Intent request, final Intent response) {
        MessageUtils.setNotSupportActionError(response);
        return true;
    }

    /**
     * POSTメソッドハンドラー.
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onPostRequest(final Intent request, final Intent response) {
        MessageUtils.setNotSupportActionError(response);
        return true;
    }

    /**
     * PUTメソッドハンドラー.<br>
     * リクエストパラメータに応じてデバイスのサービスを提供し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onPutRequest(final Intent request, final Intent response) {
        MessageUtils.setNotSupportActionError(response);
        return true;
    }

    /**
     * DELETEメソッドハンドラー.<br>
     * リクエストパラメータに応じてデバイスのサービスを提供し、その結果をレスポンスパラメータに格納する。
     * レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onDeleteRequest(final Intent request, final Intent response) {
        MessageUtils.setNotSupportActionError(response);
        return true;
    }

    /**
     * コンテキストの設定する.
     * 
     * @param context コンテキスト
     */
    public void setContext(final Context context) {
        mContext = context;
    }

    /**
     * コンテキストの取得する.
     * 
     * @return コンテキスト
     */
    public Context getContext() {
        return mContext;
    }

    /**
     * 指定されたオブジェクトがStringか指定されたNumberクラスかを判定し、指定されたNumberクラスへ変換する.
     * 
     * @param o 値
     * @param clazz 型情報
     * @param <T> ナンバークラスの型。判定出来るのは {@link Byte}、{@link Short}、{@link Integer}、
     *            {@link Long}、{@link Float}、{@link Double} のみ。
     * @return 指定されたナンバークラスのオブジェクト。変換に失敗した場合はnullを返す。
     */
    @SuppressWarnings("unchecked")
    private static <T extends Number> Number valueOf(final Object o, final Class<T> clazz) {
        if (o == null) {
            return null;
        }

        Number result = null;

        if (o instanceof String) {
            try {
                if (Integer.class.equals(clazz)) {
                    result = Integer.valueOf((String) o);
                } else if (Long.class.equals(clazz)) {
                    result = Long.valueOf((String) o);
                } else if (Double.class.equals(clazz)) {
                    result = Double.valueOf((String) o);
                } else if (Byte.class.equals(clazz)) {
                    result = Byte.valueOf((String) o);
                } else if (Short.class.equals(clazz)) {
                    result = Short.valueOf((String) o);
                } else if (Float.class.equals(clazz)) {
                    result = Float.valueOf((String) o);
                }
            } catch (NumberFormatException e) {
                result = null;
            }
        } else if (o.getClass().equals(clazz)) {
            result = (T) o;
        }

        return result;
    }

    /**
     * 指定されたオブジェクトがStringかIntegerかを判定し、Integerへ変換する.
     * 
     * @param o 値
     * @return 変換後の値。変換に失敗した場合はnullを返す。
     */
    public static Integer parseInteger(final Object o) {
        Integer res = (Integer) valueOf(o, Integer.class);
        return res;
    }

    /**
     * Intentの指定されたパラメータがStringかIntegerかを判定し、Integerへ変換する.
     * 
     * @param intent インテント
     * @param key パラメータキー
     * @return 変換後の値。変換に失敗した場合、またはパラメータが無い場合はnullを返す。
     */
    public static Integer parseInteger(final Intent intent, final String key) {
        Bundle b = intent.getExtras();
        if (b == null) {
            return null;
        }
        Integer res = parseInteger(b.get(key));
        return res;
    }

    /**
     * 指定されたオブジェクトがStringかLongかを判定し、Longへ変換する.
     * 
     * @param o 値
     * @return 変換後の値。変換に失敗した場合はnullを返す。
     */
    public static Long parseLong(final Object o) {
        Long res = (Long) valueOf(o, Long.class);
        return res;
    }

    /**
     * Intentの指定されたパラメータがStringかLongかを判定し、Longへ変換する.
     * 
     * @param intent インテント
     * @param key パラメータキー
     * @return 変換後の値。変換に失敗した場合、またはパラメータが無い場合はnullを返す。
     */
    public static Long parseLong(final Intent intent, final String key) {
        Bundle b = intent.getExtras();
        if (b == null) {
            return null;
        }
        Long res = parseLong(b.get(key));
        return res;
    }

    /**
     * 指定されたオブジェクトがStringかDoubleかを判定し、Doubleへ変換する.
     * 
     * @param o 値
     * @return 変換後の値。変換に失敗した場合はnullを返す。
     */
    public static Double parseDouble(final Object o) {
        Double res = (Double) valueOf(o, Double.class);
        return res;
    }

    /**
     * Intentの指定されたパラメータがStringかDoubleかを判定し、Doubleへ変換する.
     * 
     * @param intent インテント
     * @param key パラメータキー
     * @return 変換後の値。変換に失敗した場合、またはパラメータが無い場合はnullを返す。
     */
    public static Double parseDouble(final Intent intent, final String key) {
        Bundle b = intent.getExtras();
        if (b == null) {
            return null;
        }
        Double res = parseDouble(b.get(key));
        return res;
    }

    /**
     * 指定されたオブジェクトがStringかFloatかを判定し、Floatへ変換する.
     * 
     * @param o 値
     * @return 変換後の値。変換に失敗した場合はnullを返す。
     */
    public static Float parseFloat(final Object o) {
        Float res = (Float) valueOf(o, Float.class);
        return res;
    }

    /**
     * Intentの指定されたパラメータがStringかFloatかを判定し、Floatへ変換する.
     * 
     * @param intent インテント
     * @param key パラメータキー
     * @return 変換後の値。変換に失敗した場合、またはパラメータが無い場合はnullを返す。
     */
    public static Float parseFloat(final Intent intent, final String key) {
        Bundle b = intent.getExtras();
        if (b == null) {
            return null;
        }
        Float res = parseFloat(b.get(key));
        return res;
    }

    /**
     * 指定されたオブジェクトがStringかByteかを判定し、Byteへ変換する.
     * 
     * @param o 値
     * @return 変換後の値。変換に失敗した場合はnullを返す。
     */
    public static Byte parseByte(final Object o) {
        Byte res = (Byte) valueOf(o, Byte.class);
        return res;
    }

    /**
     * Intentの指定されたパラメータがStringかByteかを判定し、Byteへ変換する.
     * 
     * @param intent インテント
     * @param key パラメータキー
     * @return 変換後の値。変換に失敗した場合、またはパラメータが無い場合はnullを返す。
     */
    public static Byte parseByte(final Intent intent, final String key) {
        Bundle b = intent.getExtras();
        if (b == null) {
            return null;
        }
        Byte res = parseByte(b.get(key));
        return res;
    }

    /**
     * 指定されたオブジェクトがStringかShortかを判定し、Shortへ変換する.
     * 
     * @param o 値
     * @return 変換後の値。変換に失敗した場合はnullを返す。
     */
    public static Short parseShort(final Object o) {
        Short res = (Short) valueOf(o, Short.class);
        return res;
    }

    /**
     * Intentの指定されたパラメータがStringかShortかを判定し、Shortへ変換する.
     * 
     * @param intent インテント
     * @param key パラメータキー
     * @return 変換後の値。変換に失敗した場合、またはパラメータが無い場合はnullを返す。
     */
    public static Short parseShort(final Intent intent, final String key) {
        Bundle b = intent.getExtras();
        if (b == null) {
            return null;
        }
        Short res = parseShort(b.get(key));
        return res;
    }

    /**
     * 指定されたオブジェクトがStringかBooleanかを判定し、Booleanへ変換する.
     * Stringの場合は、"true"の場合true、"false"の場合falseを返す。その他はnullを返す。
     * 
     * @param o 値
     * @return 変換後の値。変換に失敗した場合はnullを返す。
     */
    public static Boolean parseBoolean(final Object o) {

        if (o instanceof String) {
            if (o.equals("true")) {
                return Boolean.TRUE;
            } else if (o.equals("false")) {
                return Boolean.FALSE;
            }
        } else if (o instanceof Boolean) {
            return (Boolean) o;
        }

        return null;
    }

    /**
     * Intentの指定されたパラメータがStringかBooleanかを判定し、Booleanへ変換する.
     * Stringの場合は、"true"の場合true、"false"の場合falseを返す。その他はnullを返す。
     * 
     * @param intent インテント
     * @param key キー
     * @return 変換後の値。変換に失敗した場合、またはパラメータが無い場合はnullを返す。
     */
    public static Boolean parseBoolean(final Intent intent, final String key) {
        Bundle b = intent.getExtras();
        if (b == null) {
            return null;
        }
        return parseBoolean(b.get(key));
    }

    /**
     * リクエストからデバイスIDを取得する.
     * 
     * @param request リクエストパラメータ
     * @return デバイスID。無い場合はnullを返す。
     */
    public static String getDeviceID(final Intent request) {
        String deviceid = request.getStringExtra(PARAM_DEVICE_ID);
        return deviceid;
    }

    /**
     * メッセージにデバイスIDを設定する.
     * 
     * @param message メッセージパラメータ
     * @param deviceId デバイスID
     */
    public static void setDeviceID(final Intent message, final String deviceId) {
        message.putExtra(PARAM_DEVICE_ID, deviceId);
    }

    /**
     * リクエストからプロファイル名を取得する.
     * 
     * @param request リクエストパラメータ
     * @return プロファイル名。無い場合はnullを返す。
     */
    public static String getProfile(final Intent request) {
        String profile = request.getExtras().getString(DConnectMessage.EXTRA_PROFILE);
        return profile;
    }

    /**
     * メッセージにプロファイル名を設定する.
     * 
     * @param message メッセージパラメータ
     * @param profile プロファイル名
     */
    public static void setProfile(final Intent message, final String profile) {
        message.putExtra(DConnectMessage.EXTRA_PROFILE, profile);
    }

    /**
     * リクエストからインターフェース名を取得する.
     * 
     * @param request リクエストパラメータ
     * @return インターフェース名
     */
    public static String getInterface(final Intent request) {
        String inter = request.getExtras().getString(DConnectMessage.EXTRA_INTERFACE);
        return inter;
    }

    /**
     * リクエストから属性名を取得する.
     * 
     * @param request リクエストパラメータ
     * @return 属性名
     */
    public static String getAttribute(final Intent request) {
        String attribute = request.getExtras().getString(DConnectMessage.EXTRA_ATTRIBUTE);
        return attribute;
    }

    /**
     * メッセージに属性名を設定する.
     * 
     * @param message メッセージパラメータ
     * @param attribute コールバック名
     */
    public static void setAttribute(final Intent message, final String attribute) {
        message.putExtra(DConnectMessage.EXTRA_ATTRIBUTE, attribute);
    }

    /**
     * レスポンス結果を設定する.
     * 
     * @param response レスポンスパラメータ
     * @param result レスポンス結果
     */
    public static void setResult(final Intent response, final int result) {
        response.putExtra(DConnectMessage.EXTRA_RESULT, result);
    }

    /**
     * レスポンス結果を取得する.
     * 
     * @param response レスポンスパラメータ
     * @return レスポンス結果
     */
    public static int getResult(final Intent response) {
        int result = response.getIntExtra(DConnectMessage.EXTRA_RESULT, -1);
        return result;
    }

    /**
     * リクエストからセッションキーを取得する.
     * 
     * @param request リクエストパラメータ
     * @return セッションキー。無い場合はnullを返す。
     */
    public static String getSessionKey(final Intent request) {
        String sessionKey = request.getStringExtra(PARAM_SESSION_KEY);
        return sessionKey;
    }

    /**
     * メッセージにセッションキーを設定する.
     * 
     * @param message メッセージパラメータ
     * @param sessionKey セッションキー
     */
    public static void setSessionKey(final Intent message, final String sessionKey) {
        message.putExtra(PARAM_SESSION_KEY, sessionKey);
    }

    /**
     * リクエストからアクセストークンを取得する.
     * 
     * @param request リクエストパラメータ
     * @return アクセストークン。無い場合はnullを返す。
     */
    public static String getAccessToken(final Intent request) {
        String accessToken = request.getStringExtra(DConnectMessage.EXTRA_ACCESS_TOKEN);
        return accessToken;
    }

    /**
     * メッセージにアクセストークンを設定する.
     * 
     * @param message メッセージパラメータ
     * @param accessToken アクセストークン
     */
    public static void setAccessToken(final Intent message, final String accessToken) {
        message.putExtra(DConnectMessage.EXTRA_ACCESS_TOKEN, accessToken);
    }

    /**
     * レスポンスの結果として非サポートエラーを設定する.
     * 
     * @param response レスポンスパラメータ
     */
    public static void setUnsupportedError(final Intent response) {
        MessageUtils.setNotSupportAttributeError(response);
    }

    /**
     * レスポンスにリクエストコードを設定する.
     * 
     * @param response レスポンスパラメータ
     * @param requestCode リクエストコード
     */
    public static void setRequestCode(final Intent response, final int requestCode) {
        response.putExtra(DConnectMessage.EXTRA_REQUEST_CODE, requestCode);
    }

    /**
     * リクエストからリクエストコードを取得する.
     * 
     * @param request リクエストパラメータ
     * @return リクエストコード
     */
    public static int getRequestCode(final Intent request) {
        return request.getIntExtra(DConnectMessage.EXTRA_REQUEST_CODE, Integer.MIN_VALUE);
    }

    /**
     * コンテンツデータを取得する.
     * 
     * @param uri URI
     * @return コンテンツデータ
     */
    protected final byte[] getContentData(final String uri) {
        if (uri == null) {
            return null;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InputStream in = null;
        byte[] buf = new byte[BUF_SIZE];
        int len;
        try {
            ContentResolver r = getContext().getContentResolver();
            in = r.openInputStream(Uri.parse(uri));
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            return out.toByteArray();
        } catch (IOException e) {
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
