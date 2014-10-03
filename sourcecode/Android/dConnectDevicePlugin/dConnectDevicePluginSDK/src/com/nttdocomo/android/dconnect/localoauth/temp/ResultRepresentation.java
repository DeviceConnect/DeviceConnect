/*
 ResultRepresentation.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.localoauth.temp;

import org.restlet.representation.EmptyRepresentation;

/**
 * ResultRepresentation
 * @author NTT DOCOMO, INC.
 */
public class ResultRepresentation extends EmptyRepresentation {
	
	/** Result. */
    private boolean mResult = false;
    /** Text. */
	private String mText = "";
	/** Error. */
	private String mError = "";
    /** ErrorDetail. */
	private String mErrorDetail = "";

	/**
	 * Resultを設定する.
	 * @param result
	 */
	public void setResult(final boolean result) {
		mResult = result;
	}
	
	/**
	 * Resultを返す.
	 * @return Result
	 */
	public boolean getResult() {
		return mResult;
	}

	/**
	 * Textを設定する.
	 * @param text Text
	 */
	public void setText(final String text) {
		mText = text;
	}
	
	/**
	 * Textを返す.
	 * @return Text
	 */
	public String getText() {
		return mText;
	}

	/**
	 * Errorを設定する.
	 * @param error error
	 * @param errorDetail errorDetail
	 */
	public void setError(final String error, final String errorDetail) {
		mError = error;
		mErrorDetail = errorDetail;
	}

	/**
	 * Errorを返す.
	 * @return error
	 */
	public String getError() {
		return mError;
	}

	/**
	 * ErrorDetailを返す.
	 * @return ErrorDetail
	 */
	public String getErrorDetail() {
		return mErrorDetail;
	}
}
