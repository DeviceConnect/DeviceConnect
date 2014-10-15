/*
 MediaStreamRecordingProfileConstants.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.profile;

/**
 * Media Stream Recording Profile API 定数群.<br/>
 * Media Stream Recording Profile API のパラメータ名、インタフェース名、属性名、プロファイル名を定義する。
 * 
 * @author NTT DOCOMO, INC.
 */
public interface MediaStreamRecordingProfileConstants extends DConnectProfileConstants {

    /**
     * プロファイル名: {@value} .
     */
    String PROFILE_NAME = "mediastream_recording";

    /**
     * 属性: {@value} .
     */
    String ATTRIBUTE_MEDIARECORDER = "mediarecorder";

    /**
     * 属性: {@value} .
     */
    String ATTRIBUTE_TAKE_PHOTO = "takephoto";
    /**
     * 属性: {@value} .
     */
    String ATTRIBUTE_RECORD = "record";
    /**
     * 属性: {@value} .
     */
    String ATTRIBUTE_PAUSE = "pause";
    /**
     * 属性: {@value} .
     */
    String ATTRIBUTE_RESUME = "resume";
    /**
     * 属性: {@value} .
     */
    String ATTRIBUTE_STOP = "stop";
    /**
     * 属性: {@value} .
     */
    String ATTRIBUTE_MUTETRACK = "mutetrack";
    /**
     * 属性: {@value} .
     */
    String ATTRIBUTE_UNMUTETRACK = "unmutetrack";
    /**
     * 属性: {@value} .
     */
    String ATTRIBUTE_OPTIONS = "options";

    /**
     * 属性: {@value} .
     */
    String ATTRIBUTE_ON_PHOTO = "onphoto";

    /**
     * 属性: {@value} .
     */
    String ATTRIBUTE_ON_DATA_AVAILABLE = "ondataavailable";
    
    /**
     * 属性: {@value} .
     */
    String ATTRIBUTE_ON_RECORDING_CHANGE = "onrecordingchange";

    /**
     * パス: {@value}.
     */
    String PATH_PROFILE = PATH_ROOT + SEPARATOR + PROFILE_NAME;

    /**
     * パス: {@value} .
     */
    String PATH_MEDIARECORDER = PATH_PROFILE + SEPARATOR + ATTRIBUTE_MEDIARECORDER;

    /**
     * パス: {@value} .
     */
    String PATH_PHOTO = PATH_PROFILE + SEPARATOR + ATTRIBUTE_TAKE_PHOTO;

    /**
     * パス: {@value} .
     */
    String PATH_RECORD = PATH_PROFILE + SEPARATOR + ATTRIBUTE_RECORD;

    /**
     * パス: {@value} .
     */
    String PATH_PAUSE = PATH_PROFILE + SEPARATOR + ATTRIBUTE_PAUSE;

    /**
     * パス: {@value} .
     */
    String PATH_RESUME = PATH_PROFILE + SEPARATOR + ATTRIBUTE_RESUME;

    /**
     * パス: {@value} .
     */
    String PATH_STOP = PATH_PROFILE + SEPARATOR + ATTRIBUTE_STOP;

    /**
     * パス: {@value} .
     */
    String PATH_MUTETRACK = PATH_PROFILE + SEPARATOR + ATTRIBUTE_MUTETRACK;

    /**
     * パス: {@value} .
     */
    String PATH_UNMUTETRACK = PATH_PROFILE + SEPARATOR + ATTRIBUTE_UNMUTETRACK;

    /**
     * パス: {@value} .
     */
    String PATH_OPTIONS = PATH_PROFILE + SEPARATOR + ATTRIBUTE_OPTIONS;

    /**
     * パス: {@value} .
     */
    String PATH_ON_PHOTO = PATH_PROFILE + SEPARATOR + ATTRIBUTE_ON_PHOTO;

    /**
     * パス: {@value} .
     */
    String PATH_ON_DATA_AVAILABLE = PATH_PROFILE + SEPARATOR + ATTRIBUTE_ON_DATA_AVAILABLE;

    /**
     * パス: {@value} .
     */
    String PATH_ON_RECORDING_CHANGE = PATH_PROFILE + SEPARATOR + ATTRIBUTE_ON_RECORDING_CHANGE;

    /**
     * パラメータ: {@value} .
     */
    String PARAM_TARGET = "target";

    /**
     * パラメータ: {@value} .
     */
    String PARAM_RECORDERS = "recorders";

    /**
     * パラメータ: {@value} .
     */
    String PARAM_ID = "id";

    /**
     * パラメータ: {@value} .
     */
    String PARAM_NAME = "name";

    /**
     * パラメータ: {@value} .
     */
    String PARAM_STATE = "state";

    /**
     * パラメータ: {@value} .
     */
    String PARAM_IMAGE_WIDTH = "imageWidth";

    /**
     * パラメータ: {@value} .
     */
    String PARAM_IMAGE_HEIGHT = "imageHeight";

    /**
     * パラメータ: {@value} .
     */
    String PARAM_MIME_TYPE = "mimeType";

    /**
     * パラメータ: {@value} .
     */
    String PARAM_CONFIG = "config";

    /**
     * パラメータ: {@value} .
     */
    String PARAM_TIME_SLICE = "timeslice";

    /**
     * パラメータ: {@value} .
     */
    String PARAM_SETTINGS = "settings";

    /**
     * パラメータ: {@value} .
     */
    String PARAM_PHOTO = "photo";

    /**
     * パラメータ: {@value} .
     */
    String PARAM_MEDIA = "media";
    
    /**
     * パラメータ: {@value} .
     */
    String PARAM_STATUS = "status";
    
    /**
     * パラメータ: {@value} .
     */
    String PARAM_ERROR_MESSAGE = "errorMessage";
    
    /** 
     * パラメータ: {@value} .
     */
    String PARAM_PATH = "path";
    
    /** 
     * パラメータ: {@value} .
     */
    String PARAM_MIN = "min";
    
    /** 
     * パラメータ: {@value} .
     */
    String PARAM_MAX = "max";

    /**
     * カメラの状態定数.
     */
    enum RecorderState {
        /** 未定義値. */
        UNKNOWN("Unknown"),
        /** 停止中. */
        INACTIVE("inactive"),
        /** レコーディング中. */
        RECORDING("recording"),
        /** 一時停止中. */
        PAUSED("paused");

        /**
         * 定義値.
         */
        private String mValue;

        /**
         * 指定された文字列を定義値に持つカメラの状態を定義します.
         * 
         * @param value 定義値
         */
        private RecorderState(final String value) {
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
         * 定義値から定数を取得する.
         * 
         * @param value 定義値
         * @return 定数オブジェクト
         */
        public static RecorderState getInstance(final String value) {

            for (RecorderState state : values()) {
                if (state.getValue().equals(value)) {
                    return state;
                }
            }

            return UNKNOWN;
        }
    }
    
    /**
     * 動画撮影、音声録音の状態定数.
     */
    enum RecordingState {
        /** 未定義値. */
        UNKNOWN("Unknown"),
        /** 開始. */
        RECORDING("recording"),
        /** 終了. */
        STOP("stop"),
        /** 一時停止. */
        PAUSE("pause"),
        /** 再開. */
        RESUME("resume"),
        /** ミュート. */
        MUTETRACK("mutetrack"),
        /** ミュート解除. */
        UNMUTETRACK("unmutetrack"),
        /** エラー発生. */
        ERROR("error"),
        /** 警告発生. */
        WARNING("warning");
        /**
         * 定義値.
         */
        private String mValue;

        /**
         * 指定された文字列を定義値に持つ動画撮影または音声録音の状態を定義します.
         * 
         * @param value 定義値
         */
        private RecordingState(final String value) {
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
         * 定義値から定数を取得する.
         * 
         * @param value 定義値
         * @return 定数オブジェクト
         */
        public static RecordingState getInstance(final String value) {

            for (RecordingState state : values()) {
                if (state.getValue().equals(value)) {
                    return state;
                }
            }

            return UNKNOWN;
        }
    }
}
