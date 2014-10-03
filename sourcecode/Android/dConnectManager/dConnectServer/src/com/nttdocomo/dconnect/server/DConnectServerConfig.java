/*
 DConnectServerConfig.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.dconnect.server;

import java.util.ArrayList;

/**
 * サーバーの設定情報.
 * 
 * @author NTT DOCOMO, INC.
 */
public final class DConnectServerConfig {

	// サーバーの設定値は起動後などに変更されるのを防ぐためBuilderでパラメータを設定させ
	// setterは本体には置かない。

	/** 最大コネクション数. */
	private int maxConnectionSize;

	/** WebSocket最大コネクション数. */
	private int maxWebSocketConnectionSize;

	/** ドキュメントルートのパス. */
	private String documentRootPath;

	/** SSLを使うかのフラグ. */
	private boolean isSsl;

	/** サーバーのポート番号. */
	private int port;

	/** サーバーのホスト名. */
	private String host;

	/** IPのホワイトリスト. */
	private ArrayList<String> ipWhiteList;

	/**
	 * 最大コネクション数を取得する.
	 * 
	 * @return 最大コネクション数
	 */
	public int getMaxConnectionSize() {
		return maxConnectionSize;
	}

	/**
	 * WebSocketの最大コネクション数を取得する.
	 * 
	 * @return WebSocketの最大コネクション数。
	 */
	public int getMaxWebSocketConnectionSize() {
		return maxWebSocketConnectionSize;
	}

	/**
	 * ドキュメントルートのパスを取得する.
	 * 
	 * @return ドキュメントルートのパス
	 */
	public String getDocumentRootPath() {
		return documentRootPath;
	}

	/**
	 * ポート番号を取得する.
	 * 
	 * @return ポート番号
	 */
	public int getPort() {
		return port;
	}

	/**
	 * ホスト名を取得する.
	 * 
	 * @return ホスト名
	 */
	public String getHost() {
		return host;
	}

	/**
	 * SSL通信を行うかをチェックする.
	 * 
	 * @return SSL通信をする場合true、しない場合はfalseを返す。
	 */
	public boolean isSsl() {
		return isSsl;
	}

	/**
	 * IPのホワイトリストを取得する.
	 * 
	 * @return IPのホワイトリスト。
	 */
	public ArrayList<String> getIPWhiteList() {
		return ipWhiteList;
	}

	/**
	 * コンストラクタ.
	 * 
	 * @param builder
	 *            ビルダー。
	 */
	private DConnectServerConfig(Builder builder) {
		// Builderを用いるためprivateに設定。
		this.documentRootPath = builder.documentRootPath;
		this.maxConnectionSize = builder.maxConnectionSize;
		this.maxWebSocketConnectionSize = builder.maxWebSocketConnectionSize;
		this.isSsl = builder.isSsl;
		this.port = builder.port;
		this.host = builder.host;
		this.ipWhiteList = builder.ipWhiteList;
	}

	/**
	 * DConnectServerConfigのビルダークラス.
	 * 
	 * @author NTT DOCOMO, INC.
	 * 
	 */
	public static final class Builder {

		/** 最大コネクション数 */
		private int maxConnectionSize = 64;

		/** WebSocket最大コネクション数 */
		private int maxWebSocketConnectionSize = 32;

		/** ドキュメントルートのパス */
		private String documentRootPath;

		/** SSLを使うかのフラグ. */
		private boolean isSsl;

		/** サーバーのポート番号. */
		private int port = -1;

		/** サーバーのホスト名. */
		private String host;

		/** IPのホワイトリスト. */
		private ArrayList<String> ipWhiteList;

		/**
		 * DConnectServerConfigのインスタンスを設定された設定値で生成する。
		 * 
		 * @return DConnectServerConfigのインスタンス。
		 * @throws IllegalStateException
		 *             ドキュメントルートがnullの場合、またはportが0未満の場合スローされる。
		 */
		public DConnectServerConfig build() {

			if (documentRootPath == null) {
				throw new IllegalStateException(
						"Document root must be not null.");
			} else if (port < 0) {
				throw new IllegalStateException("Port must be larger than 0.");
			}

			return new DConnectServerConfig(this);
		}

		/**
		 * 最大コネクション数を設定する.
		 * 
		 * @param maxConnectionSize
		 *            最大コネクション数。1以上を指定すること。
		 * 
		 * @return ビルダー。
		 * 
		 * @throws IllegalArgumentException
		 *             コネクション数が0以下の場合スローされる。
		 */
		public Builder maxConnectionSize(int maxConnectionSize) {

			if (maxConnectionSize <= 0) {
				throw new IllegalArgumentException(
						"MaxConnectionSize must be larger than 0.");
			}

			this.maxConnectionSize = maxConnectionSize;
			return this;
		}

		/**
		 * WebSocketの最大コネクション数を設定する.
		 * 
		 * @param maxWebSocketConnectionSize
		 *            WebSocketの最大コネクション数。1以上に設定すること。
		 * 
		 * @return ビルダー。
		 * @throws IllegalArgumentException
		 *             コネクション数が0以下の場合スローされる。
		 */
		public Builder maxWebSocketConnectionSize(int maxWebSocketConnectionSize) {

			if (maxWebSocketConnectionSize <= 0) {
				throw new IllegalArgumentException(
						"MaxWebSocketConnectionSize must be larger than 0.");
			}

			this.maxWebSocketConnectionSize = maxWebSocketConnectionSize;
			return this;
		}

		/**
		 * SSLの利用設定を行う.
		 * 
		 * @param isSsl
		 *            trueの場合SSL通信を行う。falseの場合はSSL通信を行わない。
		 * @return ビルダー。
		 */
		public Builder isSsl(boolean isSsl) {
			this.isSsl = isSsl;
			return this;
		}

		/**
		 * ポートを設定する.
		 * 
		 * @param port
		 *            サーバーのポート番号。
		 * @return ビルダー。
		 * @throws IllegalArgumentException
		 *             ポート番号が0未満の場合スローされる。
		 */
		public Builder port(int port) {
			if (port < 0) {
				throw new IllegalArgumentException(
						"Port must be larger than 0.");
			}
			this.port = port;
			return this;
		}

		/**
		 * ホスト名を設定する.
		 * 
		 * @param host
		 *            ホスト名。
		 * @return ビルダー。
		 */
		public Builder host(String host) {
			this.host = host;
			return this;
		}

		/**
		 * ドキュメントルートのパスを設定する.
		 * 
		 * @param documentRootPath
		 *            ドキュメントルートのパス。
		 * @return ビルダー。
		 * @throws IllegalArgumentException
		 *             documentRootPathがnullの場合スローされる。
		 */
		public Builder documentRootPath(String documentRootPath) {

			if (documentRootPath == null) {
				throw new IllegalArgumentException(
						"Document root must be not null.");
			}

			this.documentRootPath = documentRootPath;
			return this;
		}

		/**
		 * IPのホワイトリストを設定する.
		 * 
		 * @param ipWhiteList
		 *            IPのホワイトリスト。
		 * @return ビルダー。
		 */
		public Builder ipWhiteList(ArrayList<String> ipWhiteList) {
			this.ipWhiteList = ipWhiteList;
			return this;
		}
	}
}
