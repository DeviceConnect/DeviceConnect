/*
 EventError.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.event;

/**
 * Eventデータ操作系エラー.
 * 
 *
 * @author NTT DOCOMO, INC.
 */
public enum EventError {
    /** 
     * エラーなし.
     */
    NONE,
    
    /** 
     * 不正なパラメータ. 
     */
    INVALID_PARAMETER,
    
    /** 
     * マッチするイベントデータがない. 
     */
    NOT_FOUND,
    /** 
     * 処理失敗.
     */
    FAILED,
}
