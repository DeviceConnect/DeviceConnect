/*
 DConnectServerEventListener.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.dconnect.server;

import com.nttdocomo.dconnect.server.http.HttpRequest;
import com.nttdocomo.dconnect.server.http.HttpResponse;

/**
 * DConnectServerからのイベントを受け取る為のリスナーインターフェース.
 * 
 * @author NTT DOCOMO, INC.
 */
public interface DConnectServerEventListener {

	/**
	 * Httpリクエストの受信時に呼び出されます. <br/>
	 * d-Connectのリクエストとして正しくないなどの理由でリクエストの受理を破棄する
	 * 場合は、戻り値としてfalseを返すことでサーバーに通常のHTTPリクエストとして処理させます。
	 * 
	 * @param req
	 *            Httpリクエスト
	 * @param res
	 *            Httpレスポンス
	 * @return リクエストを受理する場合はtrue、リクエストを無視する場合はfalseを返す
	 */
	boolean onReceivedHttpRequest(HttpRequest req, HttpResponse res);
	
	/**
	 * サーバーでエラーが発生した場合に呼び出されます.
	 * 
	 * @param errorCode エラー種類
	 */
	void onError(DConnectServerError errorCode);
	
	/**
	 * サーバーが起動した時に呼び出されます.
	 */
	void onServerLaunched();
	
}
