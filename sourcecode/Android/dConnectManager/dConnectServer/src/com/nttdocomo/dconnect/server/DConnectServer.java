/*
 DConnectServer.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.dconnect.server;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.nttdocomo.dconnect.server.logger.LogHandler;
import com.nttdocomo.dconnect.server.websocket.DConnectWebSocket;

/**
 * d-Connect用HTTPサーバー.
 * 
 * @author NTT DOCOMO, INC.
 */
public abstract class DConnectServer {

	/** サーバーイベントの通知を受けるリスナークラス. */
	protected DConnectServerEventListener mListener;

	/** ロガー. */
	protected final Logger mLogger = Logger.getLogger("dconnect.server");

	/** WebSocketのセッション. */
	protected Map<String, DConnectWebSocket> mSockets;

	/** サーバー設定情報. */
	protected DConnectServerConfig mConfig;

	/** WebSocket受信時に送られてくるパラメータのキー : セッションキー. */
	protected static final String WEBSOCKET_PARAM_KEY_SESSION_KEY = "sessionKey";

	/** デバッグフラグ */
	private static final boolean DEBUG = false;

	/**
	 * コンストラクタ. サーバーを生成します
	 * 
	 * @param config
	 *            サーバー設定情報。
	 * @throws IllegalArgumentException
	 *             サーバー設定情報がnullの場合スローされる。
	 */
	public DConnectServer(DConnectServerConfig config) {

		if (config == null) {
			throw new IllegalArgumentException(
					"Configuration must not be null.");
		}

		mConfig = config;
		mSockets = new ConcurrentHashMap<String, DConnectWebSocket>();

		if (DEBUG) {
			LogHandler handler = new LogHandler("dconnect.server");
			handler.setFormatter(new SimpleFormatter());
			handler.setLevel(Level.ALL);
			mLogger.addHandler(handler);
			mLogger.setLevel(Level.WARNING);
		}
	}

	/**
	 * サーバーを非同期で起動する. オーバーライドするクラスで非同期処理を実装すること。
	 */
	public abstract void start();

	/**
	 * サーバーを終了させる.
	 */
	public abstract void shutdown();

	/**
	 * サーバーが起動しているか調査する.
	 * 
	 * @return 起動中の場合true、その他はfalseを返す
	 */
	public abstract boolean isRunning();

	/**
	 * サーバーのバージョンを返す.
	 * 
	 * @return サーバーバージョン
	 */
	public abstract String getVersion();

	/**
	 * イベントリスナーを設定します.
	 * 
	 * @param listener
	 *            リスナーオブジェクト
	 */
	public void setServerEventListener(DConnectServerEventListener listener) {
		this.mListener = listener;
	}

	/**
	 * 指定されたセッションキーを持つクライアントにWebSocketを通じてイベントメッセージを送る.
	 * 
	 * @param sessionKey
	 *            クライアントを特定するためのセッションキー
	 * @param event
	 *            送信するイベントメッセージ
	 * 
	 * @throws IOException
	 *             セッションが見つからない場合スローされる
	 * @throws RuntimeException
	 *             サーバーが稼働中で無い場合スローさせる
	 */
	public void sendEvent(String sessionKey, String event) throws IOException {

		mLogger.entering(getClass().getName(), "sendEvent", new Object[] {
				sessionKey, event });
		if (!isRunning()) {
			throw new RuntimeException("DConnectServer is not running.");
		}

		DConnectWebSocket socket = mSockets.get(sessionKey);
		if (socket == null) {
			throw new IOException("Cannot found session's socket.");
		}

		socket.sendEvent(event);
		mLogger.exiting(getClass().getName(), "sendEvent");
	}
}
