/*
 VibrationProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.profile;

import java.util.ArrayList;

import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.profile.VibrationProfileConstants;

import android.content.Intent;

/**
 * Vibration プロファイル.
 * 
 * <p>
 * スマートデバイスのバイブレーション操作機能を提供するAPI.<br/>
 * スマートデバイスのバイブレーション操作機能を提供するデバイスプラグインは当クラスを継承し、対応APIを実装すること。 <br/>
 * AndridManifest.xmlにてVIBRATEパーミッションの指定が必要。
 * </p>
 * 
 * <h1>各API提供メソッド</h1>
 * <p>
 * Vibration Profile の各APIへのリクエストに対し、以下のコールバックメソッド群が自動的に呼び出される。<br/>
 * サブクラスは以下のメソッド群からデバイスプラグインが提供するAPI用のメソッドをオーバーライドし、機能を実装すること。<br/>
 * オーバーライドされていない機能は自動的に非対応APIとしてレスポンスを返す。
 * </p>
 * <ul>
 * <li>Vibration Start API [PUT] :
 * {@link VibrationProfile#onPutVibrate(Intent, Intent, String, long[])}</li>
 * <li>Vibration Stop API [DELETE] :
 * {@link VibrationProfile#onDeleteVibrate(Intent, Intent, String)}</li>
 * </ul>
 * 
 * @author NTT DOCOMO, INC.
 */
public abstract class VibrationProfile extends DConnectProfile implements VibrationProfileConstants {

    /**
     * 振動パターンで使われる区切り文字.
     * 
     */
    public static final String VIBRATION_PATTERN_DELIM = ",";

    /**
     * デフォルトの最大バイブレーション鳴動時間. {@value} ミリ秒
     */
    public static final long DEFAULT_MAX_VIBRATION_TIME = 500;

    @Override
    public final String getProfileName() {
        return PROFILE_NAME;
    }

    @Override
    protected boolean onPutRequest(final Intent request, final Intent response) {
        String attribute = getAttribute(request);
        boolean result = true;

        if (ATTRIBUTE_VIBRATE.equals(attribute)) {
            result = onPutVibrate(request, response, getDeviceID(request), parsePattern(getPattern(request)));
        } else {
            MessageUtils.setUnknownAttributeError(response);
        }

        return result;
    }

    @Override
    protected boolean onDeleteRequest(final Intent request, final Intent response) {
        String attribute = getAttribute(request);
        boolean result = true;

        if (ATTRIBUTE_VIBRATE.equals(attribute)) {
            result = onDeleteVibrate(request, response, getDeviceID(request));
        } else {
            MessageUtils.setUnknownAttributeError(response);
        }

        return result;
    }

    // ------------------------------------
    // PUT
    // ------------------------------------

    /**
     * バイブ鳴動開始リクエストハンドラー.<br/>
     * デバイスを鳴動させ、その結果をレスポンスパラメータに格納する。 レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @param pattern バイブレーションの鳴動パターン配列。省略された場合は最大値を、不正なフォーマットでリクエストを受けた場合はnullが渡される。
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onPutVibrate(final Intent request, final Intent response, final String deviceId, 
            final long[] pattern) {
        setUnsupportedError(response);
        return true;
    }

    // ------------------------------------
    // DELETE
    // ------------------------------------

    /**
     * バイブ鳴動停止リクエストハンドラー.<br/>
     * デバイスの鳴動を終了させ、その結果をレスポンスパラメータに格納する。 レスポンスパラメータの送信準備が出来た場合は返り値にtrueを指定する事。
     * 送信準備ができていない場合は、返り値にfalseを指定し、スレッドを立ち上げてそのスレッドで最終的にレスポンスパラメータの送信を行う事。
     * 
     * @param request リクエストパラメータ
     * @param response レスポンスパラメータ
     * @param deviceId デバイスID
     * @return レスポンスパラメータを送信するか否か
     */
    protected boolean onDeleteVibrate(final Intent request, final Intent response, final String deviceId) {
        setUnsupportedError(response);
        return true;
    }

    // ------------------------------------
    // リクエストゲッターメソッド群
    // ------------------------------------

    /**
     * リクエストから鳴動パターンを取得する.
     * 
     * @param request リクエストパラメータ
     * @return 鳴動パターン文字列。無い場合はnullを返す。
     */
    public static String getPattern(final Intent request) {
        String value = request.getStringExtra(PARAM_PATTERN);
        return value;
    }

    // ------------------------------------
    // ユーティリティメソッド
    // ------------------------------------

    /**
     * 鳴動パターンを文字列から解析し、数値の配列に変換する.<br/>
     * 数値の前後の半角のスペースは無視される。その他の半角、全角のスペースは不正なフォーマットとして扱われる。
     * 
     * @param pattern 鳴動パターン文字列。空文字、nullの場合、最大値を返す。
     * @return 鳴動パターンの配列。解析できないフォーマットの場合nullを返す。
     */
    protected final long[] parsePattern(final String pattern) {

        if (pattern == null || pattern.length() == 0) {
            return new long[] { getMaxVibrationTime() };
        }

        long[] result = null;

        if (pattern.contains(VIBRATION_PATTERN_DELIM)) {
            String[] times = pattern.split(VIBRATION_PATTERN_DELIM);
            ArrayList<Long> values = new ArrayList<Long>();
            for (String time : times) {
                try {
                    String valueStr = time.trim();
                    if (valueStr.length() == 0) {
                        if (values.size() != times.length - 1) {
                            // 数値の間にスペースがある場合はフォーマットエラー
                            // ex. 100, , 100
                            values.clear();
                        }
                        break;
                    }
                    long value = Long.parseLong(time.trim());
                    values.add(value);
                } catch (NumberFormatException e) {
                    values.clear();
                    mLogger.warning("Exception in the VibrationProfile#parsePattern() method. " + e.toString());
                    break;
                }
            }

            if (values.size() != 0) {
                result = new long[values.size()];
                for (int i = 0; i < values.size(); i++) {
                    result[i] = values.get(i);
                }
            }
        } else {
            try {
                long time = Long.parseLong(pattern);
                result = new long[] { time };
            } catch (NumberFormatException e) {
                mLogger.warning("Exception in the VibrationProfile#parsePattern() method. " + e.toString());
            }
        }

        return result;
    }

    /**
     * バイブレーションの最大鳴動時間を取得する.<br/>
     * 実装クラス毎にオーバーライドし、適切な数値を返すこと。
     * 
     * @return 最大バイブレーション鳴動時間。単位はミリ秒。
     */
    protected long getMaxVibrationTime() {
        return DEFAULT_MAX_VIBRATION_TIME;
    }
}
