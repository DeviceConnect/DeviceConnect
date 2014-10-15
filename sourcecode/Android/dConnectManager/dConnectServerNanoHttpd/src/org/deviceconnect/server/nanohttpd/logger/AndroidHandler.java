/*
 AndroidHandler.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.server.nanohttpd.logger;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import android.content.Context;
import android.util.Log;

/**
 * Android Logger.
 * @author NTT DOCOMO, INC.
 */
public class AndroidHandler extends Handler {

	/**
	 * Log Tag.
	 */
	private String mTag = null;

	/**
	 * Constructor.
	 */
	public AndroidHandler() {
		this(null);
	}

	/**
	 * Constructor with tag.
	 * 
	 * @param tag
	 *            Log Tag
	 */
	public AndroidHandler(final String tag) {
		setTag(tag);
	}

	@Override
	public void close() {
	}

	@Override
	public void flush() {
	}

	@Override
	public void publish(final LogRecord record) {
		if (record.getLevel().equals(Level.FINEST)) {
			Log.v(mTag, getFormatter().format(record));
		}
		if (record.getLevel().equals(Level.FINER)) {
			Log.v(mTag, getFormatter().format(record));
		}
		if (record.getLevel().equals(Level.FINE)) {
			Log.d(mTag, getFormatter().format(record));
		}
		if (record.getLevel().equals(Level.INFO)) {
			Log.i(mTag, getFormatter().format(record));
		}
		if (record.getLevel().equals(Level.WARNING)) {
			Log.w(mTag, getFormatter().format(record));
		}
		if (record.getLevel().equals(Level.SEVERE)) {
			Log.e(mTag, getFormatter().format(record));
		}
	}

	/**
	 * Set Log Tag.
	 * 
	 * @param tag
	 *            Log Tag
	 */
	public void setTag(final String tag) {
		mTag = tag;
	}

	/**
	 * Set Android Context.
	 * 
	 * @param context
	 *            Android Context
	 */
	public void setContext(final Context context) {
	}

}
