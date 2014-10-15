/*
 FileProfileConstants.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.profile;



/**
 * File Profile API 定数群.<br/>
 * File Profile API のパラメータ名、インタフェース名、属性名、プロファイル名を定義する。
 * 
 * @author NTT DOCOMO, INC.
 */
public interface FileProfileConstants extends DConnectProfileConstants {

    /**
     * プロファイル名: {@value} .
     */
    String PROFILE_NAME = "file";

    /**
     * 属性: {@value} .
     */
    String ATTRIBUTE_RECEIVE = "receive";
    
    /**
     * 属性: {@value} .
     */
    String ATTRIBUTE_SEND = "send";
    
    /**
     * 属性: {@value} .
     */
    String ATTRIBUTE_LIST = "list";
    
    /**
     * 属性: {@value} .
     */
    String ATTRIBUTE_RMDIR = "rmdir";
    
    /**
     * 属性: {@value} .
     */
    String ATTRIBUTE_MKDIR = "mkdir";
    
    /**
     * 属性: {@value} .
     */
    String ATTRIBUTE_REMOVE = "remove";

    /**
     * パス: {@value}.
     */
    String PATH_PROFILE = PATH_ROOT + SEPARATOR + PROFILE_NAME;

    /**
     * パス: {@value} .
     */
    String PATH_RECEIVE = PATH_PROFILE + SEPARATOR + ATTRIBUTE_RECEIVE;
    
    /**
     * パス: {@value} .
     */
    String PATH_SEND = PATH_PROFILE + SEPARATOR + ATTRIBUTE_SEND;
    
    /**
     * パス: {@value} .
     */
    String PATH_LIST = PATH_PROFILE + SEPARATOR + ATTRIBUTE_LIST;
    
    /**
     * パス: {@value} .
     */
    String PATH_RMDIR = PATH_PROFILE + SEPARATOR + ATTRIBUTE_RMDIR;
    
    /**
     * パス: {@value} .
     */
    String PATH_MKDIR = PATH_PROFILE + SEPARATOR + ATTRIBUTE_MKDIR;
    
    /**
     * パス: {@value} .
     */
    String PATH_REMOVE = PATH_PROFILE + SEPARATOR + ATTRIBUTE_REMOVE;

    /**
     * パラメータ: {@value} .
     */
    String PARAM_MIME_TYPE = "mimeType";

    /**
     * パラメータ: {@value} .
     */
    String PARAM_FILE_NAME = "fileName";

    /**
     * パラメータ: {@value} .
     */
    String PARAM_FILE_SIZE = "fileSize";

    /**
     * パラメータ: {@value} .
     */
    String PARAM_DATA = "data";
    
    /**
     * パラメータ: {@value} .
     */
    String PARAM_FORCE = "force";
    
    /** 
     * パラメータ: {@value} .
     */
    String PARAM_PATH = "path";
    
    /** 
     * パラメータ: {@value} .
     */
    String PARAM_FILE_TYPE = "fileType";
    
    /** 
     * パラメータ: {@value} .
     */
    String PARAM_ORDER = "order";
    
    /** 
     * パラメータ: {@value} .
     */
    String PARAM_OFFSET = "offset";
    
    /** 
     * パラメータ: {@value} .
     */
    String PARAM_LIMIT = "limit";
    
    /** 
     * パラメータ: {@value} .
     */
    String PARAM_COUNT = "count";
    
    /** 
     * パラメータ: {@value} .
     */
    String PARAM_UPDATE_DATE = "updateDate";
    
    /** 
     * パラメータ: {@value} .
     */
    String PARAM_FILES = "files";
    
    /**
     * ファイルタイプ.
     */
    enum FileType {
        /** 
         * タイプ ファイル.
         */
        FILE(0),
        /**
         * タイプ ディレクトリ.
         */
        DIR(1);
        
        /** 
         * 定義値.
         */
        private int mValue;
        
        /**
         * 指定された値を定義値とする列挙値を生成する.
         * 
         * @param value 定義値
         */
        private FileType(final int value) {
            mValue = value;
        }
        
        /**
         * 定義値を取得する.
         * 
         * @return 定義値
         */
        public int getValue() {
            return mValue;
        }
        
        /**
         * 定義値から列挙値を取得する.
         * 
         * @param value 定義値
         * @return 指定された定義値を持つ列挙値。存在しない場合はnullを返す。
         */
        public static FileType getInstance(final int value) {
            
            for (FileType type : values()) {
                if (type.getValue() == value) {
                    return type;
                }
            }
            
            return null;
        }
    }
    
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
