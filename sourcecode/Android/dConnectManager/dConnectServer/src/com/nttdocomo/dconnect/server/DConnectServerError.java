/*
 DConnectServerError.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.dconnect.server;

/**
 * サーバーエラーコード群.
 * 
 * @author NTT DOCOMO, INC.
 */
public enum DConnectServerError {
	/** 起動失敗エラー. */
	LAUNCH_FAILED,
	/** 終了失敗エラー */
	SHUTDOWN_FAILED,
	/** イベント送信失敗エラー */
	SEND_EVENT_FAILED,
}
