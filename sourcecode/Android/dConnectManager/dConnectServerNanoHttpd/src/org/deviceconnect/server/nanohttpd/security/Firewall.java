/*
 Firewall.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.server.nanohttpd.security;

import java.util.ArrayList;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.deviceconnect.server.nanohttpd.BuildConfig;
import org.deviceconnect.server.nanohttpd.logger.AndroidHandler;

/**
 * ファイアウォール.
 * 
 * @author NTT DOCOMO, INC.
 */
public final class Firewall {
	
	/** ログ用タグ. */
	private static final String TAG = "Firewall";

	/** IPのホワイトリスト. */
	private ArrayList<String> mIPWhiteList;
	
	/** ロガー. */
	private final Logger mLogger = Logger.getLogger("dconnect.server");

	/**
	 * ファイアウォールを生成する.
	 */
	public Firewall() {
		this(null);
	}

	/**
	 * 接続元のIP制限リストを指定してファイアウォールを生成する.
	 * 
	 * @param ipList
	 *            IPのホワイトリスト。
	 */
	public Firewall(ArrayList<String> ipList) {
		mIPWhiteList = ipList;
		if (BuildConfig.DEBUG) {
			Handler handler = new AndroidHandler(TAG);
			handler.setFormatter(new SimpleFormatter());
			handler.setLevel(Level.ALL);
			mLogger.addHandler(handler);
			mLogger.setLevel(Level.WARNING);
		}
	}

	/**
	 * 指定されたIPがホワイトリストに含まれるかを調査する.
	 * 
	 * @param ip
	 *            調査対象となるIPアドレス。("127.0.0.1"などの文字列。)
	 * @return ホワイトリストに含まれる場合true、その他はfalseを返す。
	 */
	public boolean isWhiteIP(String ip) {
		mLogger.entering(getClass().getName(), "isWhiteIP", ip);
		if (mIPWhiteList == null || mIPWhiteList.size() == 0) {
			// IPのリストがない場合は制限なしと判断し、すべてtrueにする。
			mLogger.exiting(getClass().getName(), "isWhiteIP", true);
			return true;
		}

		// TODO: 必要ならば後々ワイルドカードや正規表現を利用できるようにする。
		// 192.168.0.*など
		for (String white : mIPWhiteList) {
			if (white.equals(ip)) {
				mLogger.exiting(getClass().getName(), "isWhiteIP", true);
				return true;
			}
		}

		mLogger.warning("Firewall#isWhiteIP(). Not allowed IP : " + ip);
		mLogger.exiting(getClass().getName(), "isWhiteIP", false);
		return false;
	}

	/**
	 * IPのホワイトリストを設定する.
	 * 
	 * @param ipList
	 *            IPのホワイトリスト。
	 */
	public void setIPWhiteList(ArrayList<String> ipList) {
		mIPWhiteList = ipList;
	}

}
