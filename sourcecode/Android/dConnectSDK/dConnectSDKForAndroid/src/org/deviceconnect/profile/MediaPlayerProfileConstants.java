/*
 MediaPlayerProfileConstants.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.profile;


/**
 * MediaPlayer Profile API 定数群.
 * <p>
 * MediaPlayer Profile API のパラメータ名、インタフェース名、属性名、プロファイル名を定義する。
 * </p>
 * @author NTT DOCOMO, INC.
 */
public interface MediaPlayerProfileConstants extends DConnectProfileConstants {
    /**
     * プロファイル名: {@value}.
     */
    String PROFILE_NAME = "media_player";

    /**
     * 属性: {@value}.
     */
    String ATTRIBUTE_MEDIA = "media";

    /**
     * 属性: {@value}.
     */
    String ATTRIBUTE_MEDIA_LIST = "media_list";

    /**
     * 属性: {@value}.
     */
    String ATTRIBUTE_PLAY_STATUS = "play_status";

    /**
     * 属性: {@value}.
     */
    String ATTRIBUTE_PLAY = "play";

    /**
     * 属性: {@value}.
     */
    String ATTRIBUTE_STOP = "stop";

    /**
     * 属性: {@value}.
     */
    String ATTRIBUTE_PAUSE = "pause";

    /**
     * 属性: {@value}.
     */
    String ATTRIBUTE_RESUME = "resume";

    /**
     * 属性: {@value}.
     */
    String ATTRIBUTE_SEEK = "seek";

    /**
     * 属性: {@value}.
     */
    String ATTRIBUTE_VOLUME = "volume";

    /**
     * 属性: {@value}.
     */
    String ATTRIBUTE_MUTE = "mute";

    /**
     * 属性: {@value}.
     */
    String ATTRIBUTE_ON_STATUS_CHANGE = "onstatuschange";

    /**
     * パス: {@value}.
     */
    String PATH_PROFILE = PATH_ROOT + SEPARATOR + PROFILE_NAME;

    /**
     * パス: {@value}.
     */
    String PATH_MEDIA = PATH_PROFILE + SEPARATOR + ATTRIBUTE_MEDIA;

    /**
     * パス: {@value}.
     */
    String PATH_MEDIA_LIST = PATH_PROFILE + SEPARATOR + ATTRIBUTE_MEDIA_LIST;

    /**
     * パス: {@value}.
     */
    String PATH_PLAY_STATUS = PATH_PROFILE + SEPARATOR + ATTRIBUTE_PLAY_STATUS;

    /**
     * パス: {@value}.
     */
    String PATH_PLAY = PATH_PROFILE + SEPARATOR + ATTRIBUTE_PLAY;

    /**
     * パス: {@value}.
     */
    String PATH_STOP = PATH_PROFILE + SEPARATOR + ATTRIBUTE_STOP;

    /**
     * パス: {@value}.
     */
    String PATH_PAUSE = PATH_PROFILE + SEPARATOR + ATTRIBUTE_PAUSE;

    /**
     * パス: {@value}.
     */
    String PATH_RESUME = PATH_PROFILE + SEPARATOR + ATTRIBUTE_RESUME;

    /**
     * パス: {@value}.
     */
    String PATH_SEEK = PATH_PROFILE + SEPARATOR + ATTRIBUTE_SEEK;

    /**
     * パス: {@value}.
     */
    String PATH_VOLUME = PATH_PROFILE + SEPARATOR + ATTRIBUTE_VOLUME;

    /**
     * パス: {@value}.
     */
    String PATH_MUTE = PATH_PROFILE + SEPARATOR + ATTRIBUTE_MUTE;

    /**
     * パス: {@value}.
     */
    String PATH_ON_STATUS_CHANGE = PATH_PROFILE + SEPARATOR + ATTRIBUTE_ON_STATUS_CHANGE;

    /**
     * パラメータ: {@value}.
     */
    String PARAM_MEDIA = "media";

    /**
     * パラメータ: {@value}.
     */
    String PARAM_MEDIA_ID = "mediaId";

    /**
     * パラメータ: {@value}.
     */
    String PARAM_MEDIA_PLAYER = "mediaPlayer";

    /**
     * パラメータ: {@value}.
     */
    String PARAM_MIME_TYPE = "mimeType";

    /**
     * パラメータ: {@value}.
     */
    String PARAM_TITLE = "title";

    /**
     * パラメータ: {@value}.
     */
    String PARAM_TYPE = "type";

    /**
     * パラメータ: {@value}.
     */
    String PARAM_LANGUAGE = "language";

    /**
     * パラメータ: {@value}.
     */
    String PARAM_DESCRIPTION = "description";

    /**
     * パラメータ: {@value}.
     */
    String PARAM_IMAGE_URI = "imageUri";

    /**
     * パラメータ: {@value}.
     */
    String PARAM_DURATION = "duration";

    /**
     * パラメータ: {@value}.
     */
    String PARAM_CREATORS = "creators";

    /**
     * パラメータ: {@value}.
     */
    String PARAM_CREATOR = "creator";

    /**
     * パラメータ: {@value}.
     */
    String PARAM_ROLE = "role";

    /**
     * パラメータ: {@value}.
     */
    String PARAM_KEYWORDS = "keywords";

    /**
     * パラメータ: {@value}.
     */
    String PARAM_GENRES = "genres";

    /**
     * パラメータ: {@value}.
     */
    String PARAM_QUERY = "query";

    /**
     * パラメータ: {@value}.
     */
    String PARAM_ORDER = "order";

    /**
     * パラメータ: {@value}.
     */
    String PARAM_OFFSET = "offset";

    /**
     * パラメータ: {@value}.
     */
    String PARAM_LIMIT = "limit";

    /**
     * パラメータ: {@value}.
     */
    String PARAM_COUNT = "count";

    /**
     * パラメータ: {@value}.
     */
    String PARAM_STATUS = "status";

    /**
     * パラメータ: {@value}.
     */
    String PARAM_POS = "pos";

    /**
     * パラメータ: {@value}.
     */
    String PARAM_VOLUME = "volume";

    /**
     * パラメータ: {@value}.
     */
    String PARAM_MUTE = "mute";

    /**
     * play_statusで指定するステータスを定義.
     */
    enum PlayStatus {
        /**
         * Play.
         */
        PLAY("play"),

        /**
         * Stop.
         */
        STOP("stop"),

        /**
         * Pause.
         */
        PAUSE("pause");

        /**
         * 定義値.
         */
        private String mValue;

        /**
         * 指定された定義値をもつ定数を宣言します.
         * 
         * @param value 定義値
         */
        private PlayStatus(final String value) {
            this.mValue = value;
        }

        /**
         * 定義値を取得する.
         * 
         * @return 定義値
         */
        public String getValue() {
            return mValue;
        }
        
        /**
         * 指定された文字列に対応するPlayStatusを取得する.
         * 指定された文字列に対応するPlayStatusが存在しない場合はnullを返却する.
         * @param value 文字列
         * @return PlayStatus
         */
        public static PlayStatus getInstance(final String value) {
            for (PlayStatus v : values()) {
                if (v.mValue.equals(value)) {
                    return v;
                }
            }
            return null;
        }
    };
    

    /**
     * onstatuschangeで受け取るステータス.
     */
    enum Status {
        /**
         * Play.
         */
        PLAY("play"),

        /**
         * Stop.
         */
        STOP("stop"),

        /**
         * Pause.
         */
        PAUSE("pause"),

        /**
         * Resume.
         */
        RESUME("resume"),

        /**
         * Mute.
         */
        MUTE("mute"),

        /**
         * Unmute.
         */
        UNMUTE("unmute"),

        /**
         * Media.
         */
        MEDIA("media"),

        /**
         * Volume.
         */
        VOLUME("volume"),

        /**
         * complete.
         */
        COMPLETE("complete");

        /**
         * 定義値.
         */
        private String mValue;

        /**
         * 指定された定義値をもつ定数を宣言します.
         * 
         * @param value 定義値
         */
        private Status(final String value) {
            this.mValue = value;
        }

        /**
         * 定義値を取得する.
         * 
         * @return 定義値
         */
        public String getValue() {
            return mValue;
        }

        /**
         * 指定された文字列に対応するStatusを取得する.
         * 指定された文字列に対応するStatusが存在しない場合はnullを返却する.
         * @param value 文字列
         * @return Status
         */
        public static Status getInstance(final String value) {
            for (Status v : values()) {
                if (v.mValue.equals(value)) {
                    return v;
                }
            }
            return null;
        }
    };

    /**
     * 並び順を定義する.
     */
    enum Order {
        /**
         * 昇順.
         */
        ASC("asc"),

        /**
         * 降順.
         */
        DSEC("desc");

        /**
         * 定義値.
         */
        private String mValue;

        /**
         * 指定された定義値をもつ定数を宣言します.
         * 
         * @param value 定義値
         */
        private Order(final String value) {
            this.mValue = value;
        }

        /**
         * 定義値を取得する.
         * 
         * @return 定義値
         */
        public String getValue() {
            return mValue;
        }

        /**
         * 指定された文字列に対応するOrderを取得する.
         * 指定された文字列に対応するOrderが存在しない場合はnullを返却する.
         * @param value 文字列
         * @return Status
         */
        public static Order getInstance(final String value) {
            for (Order v : values()) {
                if (v.mValue.equals(value)) {
                    return v;
                }
            }
            return null;
        }
    };
}
