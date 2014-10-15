/*
 LogHandler.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.server.logger;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Android Logger.
 * @author NTT DOCOMO, INC.
 */
public final class LogHandler extends Handler {

	/**
	 * Log Tag.
	 */
	private String mTag = null;

	/**
	 * Constructor.
	 */
	public LogHandler() {
		this(null);
	}

	/**
	 * Constructor with tag.
	 * 
	 * @param tag
	 *            Log Tag
	 */
	public LogHandler(final String tag) {
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
		System.out.println(mTag + " : " + getFormatter().format(record));
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

}
