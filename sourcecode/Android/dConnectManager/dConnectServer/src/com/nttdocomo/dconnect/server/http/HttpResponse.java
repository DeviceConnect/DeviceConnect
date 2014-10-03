/*
 HttpResponse.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.dconnect.server.http;

import java.util.HashMap;
import java.util.Map;

/**
 * Httpレスポンスのデータを保持するクラス. デフォルトでステータスコードはOK(200)に設定されている。
 * 
 * @author NTT DOCOMO, INC.
 */
public final class HttpResponse {

	/** Content-Type. */
	private String contentType;

	/** body. */
	private byte[] body;

	/** ヘッダー群. */
	private Map<String, String> headers;

	/** ステータスコード. */
	private StatusCode code;

	/**
	 * HTTPレスポンスのステータスコード定数.
	 * 
	 * @author NTT DOCOMO, INC.
	 * 
	 */
	public static enum StatusCode {
		CONTINUE(100), SWITCHING_PROTOCOLS(101), OK(200), CREATED(201), ACCEPTED(
				202), NON_AUTHORITATIVE_INFOMATIN(203), NO_CONTENT(204), RESET_CONTENT(
				205), PARTIAL_CONTENT(206), MULTIPLE_CHOICES(300), MOVED_PERMANENTLY(
				301), MOVED_TEMPORARILY(302), SEE_OTHER(303), NOT_MODIFIED(304), USE_PROXY(
				305), BAD_REQUEST(400), UNAUTHORIZED(401), PAYMENT_REQUIRED(402), FORBIDDEN(
				403), NOT_FOUND(404), METHOD_NOT_ALLOWED(405), NOT_ACCEPTABLE(
				406), PROXY_AUTHENTICATION_REQUIRED(407), REQUEST_TIME_OUT(408), CONFICT(
				409), GONE(410), LENGTH_REQUIRED(411), PRECONDITION_FAILED(412), REQUEST_ENTRY_TOO_LARGE(
				413), REQUEST_URI_TOO_LARGE(414), UNSUPPORTED_MEDIA_TYPE(415), REQUEST_RANGE_NOT_SATISFIABLE(
				416), EXPECTATION_FAILED(417), INTERNAL_SERVER_ERROR(500), NOT_IMPLEMENTED(
				501), BAD_GATEWAY(502), SERVICE_UNAVAILABLE(503), GATEWAY_TIME_OUT(
				504), HTTP_VERSION_NOT_SUPPORTED(505);

		/** ステータスコード. */
		private final int mCode;

		/**
		 * コンストラクタ.
		 * 
		 * @param code
		 *            ステータスコード値
		 */
		private StatusCode(int code) {
			mCode = code;
		}

		/**
		 * ステータスコード値を取得する.
		 * 
		 * @return ステータスコード
		 */
		public int getCode() {
			return mCode;
		}
	}

	/**
	 * コンストラクタ.
	 */
	public HttpResponse() {
		headers = new HashMap<String, String>();
		code = StatusCode.OK;
	}

	/**
	 * Content-Typeを取得する.
	 * 
	 * @return Content-Type
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * Content-Typeを設定する.
	 * 
	 * @param contentType
	 *            設定するContent-Type
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * Bodyを取得する.
	 * 
	 * @return Bodyのデータ
	 */
	public byte[] getBody() {
		return body;
	}

	/**
	 * Bodyを設定する.
	 * 
	 * @param body
	 *            設定するBodyのデータ
	 */
	public void setBody(byte[] body) {
		this.body = body;
	}

	/**
	 * レスポンスにヘッダーを追加する.
	 * 当メソッドではContent-Typeを追加しない。Content-Typeを設定する場合はsetContentTypeを使うこと。
	 * 
	 * @param name
	 *            ヘッダー名
	 * @param value
	 *            値
	 * @throws IllegalArgumentException
	 *             引数値がnullの場合スローされる。
	 */
	public void addHeader(String name, String value) {

		if (name == null || value == null) {
			String argName = name == null ? "name" : "value";
			throw new IllegalArgumentException(argName + " must not be null.");
		}

		// Content-Typeは別に変数としてセットさせるため、ヘッダーには入れさせない
		if ("content-type".equals(name.toLowerCase())) {
			return;
		}

		headers.put(name, value);
	}

	/**
	 * ヘッダーを取得する.
	 * 
	 * @return ヘッダーのマップオブジェクト。
	 */
	public Map<String, String> getHeaders() {
		return headers;
	}

	/**
	 * ステータスコードを取得する.
	 * 
	 * @return ステータスコード
	 */
	public StatusCode getCode() {
		return code;
	}

	/**
	 * ステータスコードを設定する.
	 * 
	 * @param code
	 *            ステータスコード
	 */
	public void setCode(StatusCode code) {
		this.code = code;
	}
}
