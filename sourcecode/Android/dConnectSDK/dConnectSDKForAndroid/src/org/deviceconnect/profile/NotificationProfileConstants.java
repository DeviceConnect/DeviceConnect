/*
 NotificationProfileConstants.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.profile;

/**
 * Notification Profile API 定数群.<br/>
 * Notification Profile API のパラメータ名、インタフェース名、属性名、プロファイル名を定義する。
 * @author NTT DOCOMO, INC.
 */
public interface NotificationProfileConstants extends DConnectProfileConstants {

    /**
     * プロファイル名: {@value} .
     */
    String PROFILE_NAME = "notification";

    /**
     * 属性: {@value} .
     */
    String ATTRIBUTE_NOTIFY = "notify";

    /**
     * 属性:{@value} .
     */
    String ATTRIBUTE_ON_CLICK = "onclick";

    /**
     * 属性:{@value} .
     */
    String ATTRIBUTE_ON_CLOSE = "onclose";

    /**
     * 属性:{@value} .
     */
    String ATTRIBUTE_ON_ERROR = "onerror";

    /**
     * 属性:{@value} .
     */
    String ATTRIBUTE_ON_SHOW = "onshow";

    /**
     * パス: {@value}.
     */
    String PATH_PROFILE = PATH_ROOT + SEPARATOR + PROFILE_NAME;

    /**
     * パス: {@value} .
     */
    String PATH_NOTIFY = PATH_PROFILE + SEPARATOR + ATTRIBUTE_NOTIFY;

    /**
     * パス:{@value} .
     */
    String PATH_ON_CLICK = PATH_PROFILE + SEPARATOR + ATTRIBUTE_ON_CLICK;

    /**
     * パス:{@value} .
     */
    String PATH_ON_CLOSE = PATH_PROFILE + SEPARATOR + ATTRIBUTE_ON_CLOSE;

    /**
     * パス:{@value} .
     */
    String PATH_ON_ERROR = PATH_PROFILE + SEPARATOR + ATTRIBUTE_ON_ERROR;

    /**
     * パス:{@value} .
     */
    String PATH_ON_SHOW = PATH_PROFILE + SEPARATOR + ATTRIBUTE_ON_SHOW;

    /**
     * パラメータ:{@value} .
     */
    String PARAM_BODY = "body";

    /**
     * パラメータ:{@value} .
     */
    String PARAM_TYPE = "type";

    /**
     * パラメータ:{@value} .
     */
    String PARAM_DIR = "dir";

    /**
     * パラメータ:{@value} .
     */
    String PARAM_LANG = "lang";

    /**
     * パラメータ:{@value} .
     */
    String PARAM_TAG = "tag";

    /**
     * パラメータ:{@value} .
     */
    String PARAM_ICON = "icon";

    /**
     * パラメータ: {@value} .
     */
    String PARAM_NOTIFICATION_ID = "notificationId";

    /**
     * 通知タイプ定数.
     */
    enum NotificationType {
        /** 未定数値. */
        UNKNOWN(-1),
        /** 音声通話着信. */
        PHONE(0),
        /** メール着信. */
        MAIL(1),
        /** SMS着信. */
        SMS(2),
        /** イベント. */
        EVENT(3);

        /** モードの数値. */
        private int mValue;

        /**
         * 指定された文字列を定数値に持つ定数を定義する.
         * 
         * @param value 定数値
         */
        private NotificationType(final int value) {
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
         * 通知のタイプの定数値からインスタンスを取得する.
         * 
         * @param value コード
         * @return 通知タイプ
         */
        public static NotificationType getInstance(final int value) {
            for (NotificationType v : values()) {
                if (v.mValue == value) {
                    return v;
                }
            }
            return UNKNOWN;
        }
    }

    /**
     * 向き.
     * 
     */
    enum Direction {
        /** 未定数値. */
        UNKNOWN("Unknown"),
        /** 自動. */
        AUTO("auto"),
        /** 右から左. */
        RIGHT_TO_LEFT("rtl"),
        /** 左から右. */
        LEFT_TO_RIGHT("ltr");

        /** 方向定数文字列. */
        private String mValue;

        /**
         * 指定された文字列を定数値に持つ定数を定義する.
         * 
         * @param value 定数値
         */
        private Direction(final String value) {
            this.mValue = value;
        }

        /**
         * 定数値を取得する.
         * 
         * @return 定数値
         */
        public String getValue() {
            return mValue;
        }

        /**
         * 向きの定数値からインスタンスを取得する.
         * 
         * @param value コード
         * @return 向き
         */
        public static Direction getInstance(final String value) {
            for (Direction v : values()) {
                if (v.getValue().equals(value)) {
                    return v;
                }
            }
            return UNKNOWN;
        }
    }

}
