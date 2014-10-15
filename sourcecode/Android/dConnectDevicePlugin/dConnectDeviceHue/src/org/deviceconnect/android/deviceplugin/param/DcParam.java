/*
DcParam
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

package org.deviceconnect.android.deviceplugin.param;

import org.deviceconnect.android.deviceplugin.util.DcLoggerHue;

import android.content.Intent;


/**
 * パラメータ関連クラス.
 * @author NTT DOCOMO, INC.
 */
public class DcParam {

    /**
     * Constructor.
     */
    protected DcParam() {

    }

    /**
     * 最大値定義.
     */
    private static final int UINT16_MAX_VALUE = 65535;

    /**
     * tmp value.
     */
    private static final int TMP_VALUE = 255;

    /**
     * UByte値取得.
     * 
     * @param value value
     * @return res
     * @throws DcParamException DcParam
     */
    protected static short getUbyteValue(final String value) throws DcParamException {

        short res = 0;
        DcLoggerHue logger = new DcLoggerHue();

        try {
            long temp = 0;

            temp = Long.parseLong(value);

            if (temp < 0) {
                res = 0;

            } else if (temp > TMP_VALUE) {
                res = TMP_VALUE;

            } else {
                res = (short) temp;
            }
        } catch (Exception e) {
            logger.fine("DcParam", "getUbyteValue", value);
            // 不正なリクエストパラメータエラー
            throwPramException("指定された値が正しくありません　：　" + value);

        }

        return res;
    }

    /**
     * valueの値チェック.
     * 
     * @param value value
     * @return res
     * @throws DcParamException DcParam
     */
    protected static int getUint16Value(final String value) throws DcParamException {

        int res = 0;
        DcLoggerHue logger = new DcLoggerHue();

        try {
            long temp = 0;

            temp = Long.parseLong(value);

            if (temp < 0) {
                res = 0;

            } else if (temp > UINT16_MAX_VALUE) {
                res = UINT16_MAX_VALUE;

            } else {
                res = (int) temp;
            }
        } catch (Exception e) {
            logger.fine("DcParam", "getUint16Value", value);
            // 不正なリクエストパラメータエラー
            throwPramException("指定された値が正しくありません　：　" + value);
        }

        return res;
    }

    /**
     * keyより関連情報取得.
     * 
     * @param request request
     * @param name name
     * @return value
     */
    protected static String getExtra(final Intent request, final String name) {

        String value;
        // DcLoggerHue logger = new DcLoggerHue();

        try {
            value = request.getStringExtra(name).trim();
            // logger.fine("DcParam", "getExtra " + name, value);
        } catch (Exception e) {
            value = "";
            // logger.fine("DcParam", "getSaturation", "パラメータ無し");
        }

        return value;
    }

    /**
     * valueの正常確認.
     * 
     * @param value value
     * @return true of valueUpper
     */
    protected static boolean getIsTrue(final String value) {

        String valueUpper = value.toUpperCase();

        return "TRUE".equals(valueUpper) || "1".equals(valueUpper);

    }

    /**
     * valueの異常確認.
     * 
     * @param value value
     * @return false of valueUpper
     */
    protected static boolean getIsFalse(final String value) {

        String valueUpper = value.toUpperCase();

        return "FALSE".equals(valueUpper) || "0".equals(valueUpper);

    }

    /**
     * ","削除用メソッド.
     * 
     * @param values value
     * @return strAry
     */
    protected static String[] getSplitKanma(final String values) {

        String[] strAry = values.split(",");

        for (String string : strAry) {
            string = string.trim();
        }

        return strAry;
    }

    /**
     * Exception throewメソッド.
     * 
     * @param msg message
     * @throws DcParamException DcParam
     */
    public static void throwPramException(final String msg) throws DcParamException {
        throw new DcParamException(msg);
    }
    
    /**
     * Exeption定義クラス.
     * 
     * 
     */
    @SuppressWarnings("serial")
    public static class DcParamException extends Exception {

        /**
         * コンストラクタ.
         * 
         * @param msg message
         */
        public DcParamException(final String msg) {
            super(msg);
        }

    }
}
