/*
 FileDescriptorProfileConstants.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.profile;


/**
 * File Descriptor Profile API 定数群.<br/>
 * File Descriptor Profile API のパラメータ名、インタフェース名、属性名、プロファイル名を定義する。
 * 
 * @author NTT DOCOMO, INC.
 */
public interface FileDescriptorProfileConstants extends DConnectProfileConstants {

    /**
     * プロファイル名: {@value} .
     */
    String PROFILE_NAME = "file_descriptor";

    /**
     * 属性:{@value} .
     */
    String ATTRIBUTE_OPEN = "open";

    /**
     * 属性:{@value} .
     */
    String ATTRIBUTE_CLOSE = "close";

    /**
     * 属性:{@value} .
     */
    String ATTRIBUTE_READ = "read";

    /**
     * 属性:{@value} .
     */
    String ATTRIBUTE_WRITE = "write";

    /**
     * 属性:{@value} .
     */
    String ATTRIBUTE_ON_WATCH_FILE = "onwatchfile";

    /**
     * パス: {@value}.
     */
    String PATH_PROFILE = PATH_ROOT + SEPARATOR + PROFILE_NAME;

    /**
     * パス:{@value} .
     */
    String PATH_OPEN = PATH_PROFILE + SEPARATOR + ATTRIBUTE_OPEN;

    /**
     * パス:{@value} .
     */
    String PATH_CLOSE = PATH_PROFILE + SEPARATOR + ATTRIBUTE_CLOSE;

    /**
     * パス:{@value} .
     */
    String PATH_READ = PATH_PROFILE + SEPARATOR + ATTRIBUTE_READ;

    /**
     * パス:{@value} .
     */
    String PATH_WRITE = PATH_PROFILE + SEPARATOR + ATTRIBUTE_WRITE;

    /**
     * パス:{@value} .
     */
    String PATH_ON_WATCH_FILE = PATH_PROFILE + SEPARATOR + ATTRIBUTE_ON_WATCH_FILE;

    /**
     * パラメータ: {@value} .
     */
    String PARAM_FLAG = "flag";

    /**
     * パラメータ: {@value} .
     */
    String PARAM_POSITION = "position";

    /**
     * パラメータ: {@value} .
     */
    String PARAM_LENGTH = "length";

    /**
     * パラメータ: {@value} .
     */
    String PARAM_SIZE = "size";

    /**
     * パラメータ: {@value} .
     */
    String PARAM_FILE = "file";

    /**
     * パラメータ: {@value} .
     */
    String PARAM_CURR = "curr";

    /**
     * パラメータ: {@value} .
     */
    String PARAM_PREV = "prev";

    /**
     * パラメータ: {@value} .
     */
    String PARAM_FILE_DATA = "fileData";
    
    /** 
     * パラメータ: {@value} .
     */
    String PARAM_PATH = "path";
    
    /** 
     * パラメータ: {@value} .
     */
    String PARAM_MEDIA = "media";
    
    /**
     * ファイルフラグ.
     */
    enum Flag {
        /** 
         * 未定義値. 
         */
        UNKNOWN("Unknown"),
        /** 
         * 読み込みのみ.
         */
        R("r"),
        /** 
         * 読み込み書き込み. 
         */
        RW("rw");
        
        /** 
         * 定義値.
         */
        private String mValue;
        
        /**
         * 指定された文字列を定義する列挙値を生成する.
         * 
         * @param value 定義値
         */
        private Flag(final String value) {
            mValue = value;
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
         * @return 定数。無い場合はnullを返す。
         */
        public static Flag getInstance(final String value) {

            for (Flag flag : values()) {
                if (flag.mValue.equals(value)) {
                    return flag;
                }
            }

            return UNKNOWN;
        }
    }
}
