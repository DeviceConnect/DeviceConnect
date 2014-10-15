/*
 SettingsProfileConstants.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.profile;


/**
 * Settings Profile API 定数群.<br/>
 * Settings Profile API のパラメータ名、インタフェース名、属性名、プロファイル名を定義する。
 * @author NTT DOCOMO, INC.
 */
public interface SettingsProfileConstants extends DConnectProfileConstants {

    /**
     * プロファイル名: {@value} .
     */
    String PROFILE_NAME = "settings";

    /**
     * インターフェース: {@value} .
     */
    String INTERFACE_SOUND = "sound";

    /**
     * インターフェース: {@value} .
     */
    String INTERFACE_DISPLAY = "display";

    /**
     * 属性: {@value} .
     */
    String ATTRIBUTE_VOLUME = "volume";

    /**
     * 属性: {@value} .
     */
    String ATTRIBUTE_DATE = "date";

    /**
     * 属性: {@value} .
     */
    String ATTRIBUTE_LIGHT = "light";

    /**
     * 属性: {@value} .
     */
    String ATTRIBUTE_SLEEP = "sleep";

    /**
     * パス: {@value}.
     */
    String PATH_PROFILE = PATH_ROOT + SEPARATOR + PROFILE_NAME;

    /**
     * パス: {@value} .
     */
    String PATH_VOLUME = PATH_PROFILE + SEPARATOR 
            + INTERFACE_SOUND + SEPARATOR + ATTRIBUTE_VOLUME;

    /**
     * パス: {@value} .
     */
    String PATH_DATE = PATH_PROFILE + SEPARATOR + ATTRIBUTE_DATE;

    /**
     * パス: {@value} .
     */
    String PATH_LIGHT = PATH_PROFILE + SEPARATOR 
            + INTERFACE_DISPLAY + SEPARATOR + ATTRIBUTE_LIGHT;

    /**
     * パス: {@value} .
     */
    String PATHSLEEP = PATH_PROFILE + SEPARATOR 
            + INTERFACE_DISPLAY + SEPARATOR + ATTRIBUTE_SLEEP;
    
    /**
     * パラメータ: {@value} .
     */
    String PARAM_KIND = "kind";

    /**
     * パラメータ: {@value} .
     */
    String PARAM_LEVEL = "level";

    /**
     * パラメータ: {@value} .
     */
    String PARAM_DATE = "date";

    /**
     * パラメータ: {@value} .
     */
    String PARAM_TIME = "time";

    /** 
     * 最大Level.
     */
    double MAX_LEVEL = 1.0;

    /** 
     * 最小Level.
     */
    double MIN_LEVEL = 0;

    /**
     * 音量の種別定数.
     * 
     */
    enum VolumeKind {
        /** 未定義値. */
        UNKNOWN(-1),
        /** アラーム. */
        ALARM(1),
        /** 通話音. */
        CALL(2),
        /** 着信音. */
        RINGTONE(3),
        /** メール着信音. */
        MAIL(4),
        /** その他SNS等の着信音. */
        OTHER(5),
        /** メディアプレーヤーの音量. */
        MEDIA_PLAYER(6);

        /**
         * 仕様上の定数値.
         */
        private int mValue;

        /**
         * 指定された数値を定数値にもつ定数を定義する.
         * 
         * @param value 定数値
         */
        private VolumeKind(final int value) {
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
         * 定義値からインスタンスを取得する.
         * 
         * @param value
         *            コード
         * @return 音量種別
         */
        public static VolumeKind getInstance(final int value) {
            for (VolumeKind v : values()) {
                if (v.mValue == value) {
                    return v;
                }
            }
            return UNKNOWN;
        }

    }

}
