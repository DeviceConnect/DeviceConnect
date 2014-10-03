/*
 DConnectAndroidResponseReceiver.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.dconnect.message.intent;

import java.util.logging.Logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.nttdocomo.dconnect.message.DConnectMessage;
import com.nttdocomo.dconnect.message.intent.message.IntentDConnectMessage;

/**
 * DConnectAndroidClientTask用レスポンスレシーバ.
 * 
 * 
 * @author NTT DOCOMO, INC.
 */
public class DConnectAndroidResponseReceiver extends BroadcastReceiver {

	/** ロガー. */
	private Logger mLogger = Logger.getLogger("dconnect.sdk.android");

	/** リクエストコード エラー. */
	private static final int ERROR_CODE = Integer.MIN_VALUE;

	/**
	 * レシーバー登録フラグ.
	 */
	private boolean mRegistered;

	/**
	 * レシーバー登録フラグを取得する.
	 * 
	 * @return レシーバー登録フラグ
	 */
	public boolean isRegistered() {
		mLogger.entering(this.getClass().getName(), "isRegistered");
		mLogger.exiting(this.getClass().getName(), "isRegistered", mRegistered);
		return mRegistered;
	}

	/**
	 * レシーバー登録フラグを設定する.
	 * 
	 * @param registered
	 *            レシーバー登録フラグ
	 */
	public void setRegistered(final boolean registered) {
		mLogger.entering(this.getClass().getName(), "setRegistered", registered);
		mRegistered = registered;
		mLogger.exiting(this.getClass().getName(), "setRegistered");
	}

	@Override
	public void onReceive(final Context context, final Intent intent) {
		mLogger.entering(this.getClass().getName(), "onReceive", new Object[] {
				context, intent });

		String action = intent.getAction();
		if (action != null
				&& action.equals(IntentDConnectMessage.ACTION_RESPONSE)) {
			int reqCode = intent.getIntExtra(
					DConnectMessage.EXTRA_REQUEST_CODE, ERROR_CODE);
			if (reqCode != ERROR_CODE
					&& DConnectAndroidClientTask.getEventMap().indexOfKey(
							reqCode) >= 0) {
				DConnectAndroidClientTask.getEventMap().put(reqCode, intent);
			}
		}

		mLogger.exiting(this.getClass().getName(), "onReceive");
	}

}
