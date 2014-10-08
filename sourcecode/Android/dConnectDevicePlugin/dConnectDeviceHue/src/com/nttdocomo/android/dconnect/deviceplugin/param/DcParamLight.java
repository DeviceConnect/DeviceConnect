/*
DcParamLight
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

package com.nttdocomo.android.dconnect.deviceplugin.param;

import android.content.Intent;
import com.nttdocomo.android.dconnect.profile.LightProfileConstants;

/**
 * ライトパラメータ関連クラス.
 * 
 * 
 */
public class DcParamLight extends DcParam implements LightProfileConstants {

    /**
     * .
     */
    private static final int PARSE_RADIX = 16;

    // ============================================================
    /**
     * ライトID取得.
     * 
     * @param request request
     * @return lightid
     */
    public static final String getLightID(final Intent request) {
        String lightid = getExtra(request, PARAM_LIGHT_ID);
        return lightid;
    }

    /**
     * 名前取得.
     * 
     * @param request request
     * @return myName
     */
    public static final String getName(final Intent request) {
        String myName = getExtra(request, PARAM_NAME);
        return myName;
    }

    // ============================================================
    // Create Group New I/F
    /**
     * グループ名取得.
     * 
     * @param request request
     * @return myName
     */
    public static final String getGroupName(final Intent request) {
        String myName = getExtra(request, PARAM_GROUP_NAME);
        return myName;
    }

    /**
     * ライトID取得.
     * 
     * @param request request
     * @return myName
     */
    public static final String getLightIds(final Intent request) {
        String myName = getExtra(request, PARAM_LIGHT_IDS);
        return myName;
    }

    // ============================================================
    // Create Group New I/F delete

    /**
     * グループID取得.
     * 
     * @param request request
     * @return myName
     */
    public static final String getGroupId(final Intent request) {
        String myName = getExtra(request, PARAM_GROUP_ID);
        return myName;
    }

    // ============================================================

    /**
     * ON情報取得.
     * 
     * @param request request
     * @return PARAM_ON
     */
    public static String getOn(final Intent request) {

        return getExtra(request, PARAM_ON);

    }

    /**
     * ON状態の確認.
     * 
     * @param request request
     * @return getOn(True)
     */
    public static boolean getOnIsTrue(final Intent request) {

        return getIsTrue(getOn(request));

    }

    /**
     * OFF状態確認.
     * 
     * @param request request
     * @return getOn(False)
     */
    public static boolean getOnIsFalse(final Intent request) {

        return getIsFalse(getOn(request));

    }

    // ============================================================

    /**
     * 輝度取得.
     * 
     * @param request request
     * @return PARAM_BRIGHTNESS
     */
    public static String getBrightness(final Intent request) {

        return getExtra(request, PARAM_BRIGHTNESS);
    }

    /**
     * 輝度パラメータの値チェック.
     * 
     * @param request request
     * @return Enable BrightNess
     */
    public static boolean getBrightnessIsEnable(final Intent request) {

        String value = getBrightness(request);

        return value.length() > 0;

    }

    /**
     * 輝度値取得.
     * 
     * @param request request
     * @return BrightnessValue
     * @throws DcParamException DcParamException
     */
    public static float getBrightnessValue(final Intent request) throws DcParamException {

        return getBrightnessValue(getBrightness(request));

    }

    /**
     * 輝度値取得.
     * 
     * @param value value
     * @return res
     * @throws DcParamException DcParamException
     */
    protected static float getBrightnessValue(final String value) throws DcParamException {

        float res = 0;

        try {
            float temp = 0;

            temp = Float.parseFloat(value);

            if (temp < 0) {
                res = 0;

            } else if (temp > 1) {
                res = 1;

            } else {
                res = (float) temp;
            }
        } catch (Exception e) {
            // 不正なリクエストパラメータエラーは０に丸めて返す
            res = 0;
        }

        return res;
    }

    // ============================================================
    /**
     * 発光色取得.
     * 
     * @param request request
     * @return PARAM_COLOR
     */
    public static String getColor(final Intent request) {

        return getExtra(request, PARAM_COLOR);

    }

    /**
     * 発光色の正常確認.
     * 
     * @param request request
     * @return EnableColor
     */
    public static boolean getColorIsEnable(final Intent request) {

        String values = getColor(request);

        return values.length() > 0;

    }

    /**
     * Integer返還.
     * 
     * @param hexString hexString
     * @return parsedString
     */
    protected static int convHexstringToInt(final String hexString) {
        return (int) (short) Long.parseLong(hexString, PARSE_RADIX);
    }

    /**
     * 発光色値取得.
     * 
     * @param request request
     * @return rgb
     * @throws DcParamException DcParam
     */
    public static RgbData getColorValue(final Intent request) throws DcParamException {

        String values = getColor(request);

        if (values.length() != INTEGER_SIX) {
            // 不正なリクエストパラメータエラー
            throwPramException("colorが正しくありません");
        }

        RgbData rgbdata = new RgbData();

        try {
            rgbdata.r = convHexstringToInt(values.substring(0, INTEGER_TWO));
            rgbdata.g = convHexstringToInt(values.substring(INTEGER_TWO, INTEGER_FOUR));
            rgbdata.b = convHexstringToInt(values.substring(INTEGER_FOUR, INTEGER_SIX));

        } catch (Exception e) {
            // 不正なリクエストパラメータエラー
            throwPramException("colorが正しくありません");
        }

        return rgbdata;

    }

    /**
     * RGBデータ関連クラス.
     * 
     * 
     */
    public static class RgbData {

        /**
         * 赤色パラメータ.
         */
        private int r;
        /**
         * 緑色パラメータ.
         */
        private int g;
        /**
         * 青色パラメータ.
         */
        private int b;

        /**
         * RgbData.
         */
        public RgbData() {
            super();

            r = 0;
            g = 0;
            b = 0;
        }

        /**
         * コンストラクタ.
         * 
         * @param red red
         * @param green green
         * @param blue blue
         */
        public RgbData(final int red, final int green, final int blue) {
            super();

            r = red;
            g = green;
            b = blue;
        }

        /**
         * Red getter.
         * 
         * @return red
         */
        public int getRedParam() {
            return r;
        }

        /**
         * Red setter.
         * 
         * @param rParam r
         */
        public void setRedParam(final int rParam) {
            r = rParam;
        }

        /**
         * Green getter.
         * 
         * @return green
         */
        public int getGreenParam() {
            return g;
        }

        /**
         * Green setter.
         * 
         * @param gParam g
         */
        public void setGreenParam(final int gParam) {
            g = gParam;
        }

        /**
         * Blue getter.
         * 
         * @return blue
         */
        public int getBlueParam() {
            return b;
        }

        /**
         * Blue setter.
         * 
         * @param bParam b
         */
        public void setBlueParam(final int bParam) {
            b = bParam;
        }
    }

    // ============================================================

    /**
     * Config取得.
     * 
     * @param request request
     * @return PARAM_CONFIG
     */
    public static String getConfig(final Intent request) {

        return getExtra(request, PARAM_CONFIG);
    }

    /**
     * Config正常確認.
     * 
     * @param request request
     * @return Config Enable
     */
    public static boolean getConfigIsEnable(final Intent request) {

        String value = getConfig(request);

        return value.length() > 0;

    }

    @Override
    public String getProfileName() {
        return null;
    }
}
