/*
 PhoneProfileConstants.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.profile;


/**
 * Phone Profile API 定数群.<br/>
 * Phone Profile API のパラメータ名、インタフェース名、属性名、プロファイル名を定義する。
 * @author NTT DOCOMO, INC.
 */
public interface PhoneProfileConstants extends DConnectProfileConstants {

    /**
     * プロファイル名: {@value} .
     */
    String PROFILE_NAME = "phone";

    /**
     * 属性: {@value} .
     */
    String ATTRIBUTE_CALL = "call";

    /**
     * 属性: {@value} .
     */
    String ATTRIBUTE_SET = "set";

    /**
     * 属性: {@value} .
     */
    String ATTRIBUTE_ON_CONNECT = "onconnect";

    /**
     * パス: {@value}.
     */
    String PATH_PROFILE = PATH_ROOT + SEPARATOR + PROFILE_NAME;

    /**
     * パス: {@value} .
     */
    String PATH_CALL = PATH_PROFILE + SEPARATOR + ATTRIBUTE_CALL;

    /**
     * パス: {@value} .
     */
    String PATH_SET = PATH_PROFILE + SEPARATOR + ATTRIBUTE_SET;

    /**
     * パス: {@value} .
     */
    String PATH_ON_CONNECT = PATH_PROFILE + SEPARATOR + ATTRIBUTE_ON_CONNECT;

    /**
     * パラメータ: {@value} .
     */
    String PARAM_PHONE_NUMBER = "phoneNumber";

    /**
     * パラメータ: {@value} .
     */
    String PARAM_MODE = "mode";

    /**
     * パラメータ: {@value} .
     */
    String PARAM_PHONE_STATUS = "phoneStatus";

    /**
     * パラメータ: {@value} .
     */
    String PARAM_STATE = "state";
    
    /**
     * 電話のモード定数.
     */
    enum PhoneMode {
        /** 未定義値. */
        UNKNOWN(-1),
        /** サイレントモード. */
        SILENT(0),
        /** マナーモード. */
        MANNER(1),
        /** 音あり. */
        SOUND(2);

        /** モードの数値. */
        private int mValue;

        /**
         * 指定さてた定数値を持つ定数を定義する.
         * 
         * @param value 定数値
         */
        private PhoneMode(final int value) {
            this.mValue = value;
        }

        /**
         * 値を取得する.
         * 
         * @return モードの値
         */
        public int getValue() {
            return mValue;
        }

        /**
         * 電話のモードのコード値からオブジェクトを取得する.
         * 
         * @param value
         *            コード
         * @return 電話のモード
         */
        public static PhoneMode getInstance(final int value) {
            for (PhoneMode mode : values()) {
                if (mode.mValue == value) {
                    return mode;
                }
            }
            return UNKNOWN;
        }
    }

    /**
     * 通話状態定数.
     */
    enum CallState {

        /** 未定義値. */
        UNKNOWN(-1),
        /** 通話開始. */
        START(0),
        /** 通話失敗. */
        FAILED(1),
        /** 通話終了. */
        FINISHED(2);

        /** モードの数値. */
        private int mValue;

        /**
         * 指定された数値を定数値に持つ定数を定義する.
         * 
         * @param value 定数値
         */
        private CallState(final int value) {
            this.mValue = value;
        }

        /**
         * 通話状態のコード値からオブジェクトを取得する.
         * 
         * @param value
         *            数値
         * @return 通話状態
         */
        public static CallState getInstance(final int value) {
            for (CallState state : values()) {
                if (state.mValue == value) {
                    return state;
                }
            }
            return UNKNOWN;
        }

        /**
         * 値を取得する.
         * 
         * @return モードの値
         */
        public int getValue() {
            return mValue;
        }
    }

}
