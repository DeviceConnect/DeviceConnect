/*
 DConnectWebSocket.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.server.websocket;

/**
 * WebSocketインターフェース.
 * 
 * @author NTT DOCOMO, INC.
 */
public interface DConnectWebSocket {

	/**
	 * クライアントにイベントメッセージを送信します.
	 * 
	 * @param event イベントメッセージ
	 */
	void sendEvent(String event);
	
}
