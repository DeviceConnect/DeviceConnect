/*
 HttpRequest.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.dconnect.server.http;

import java.util.Map;

/**
 * Httpリクエストのデータを保持するクラス.
 * 
 * @author NTT DOCOMO, INC.
 */
public final class HttpRequest {

	/** HTTPメソッド GET. */
	public static final String HTTP_METHOD_GET = "GET";

	/** HTTPメソッド POST. */
	public static final String HTTP_METHOD_POST = "POST";

	/** HTTPメソッド PUT. */
	public static final String HTTP_METHOD_PUT = "PUT";

	/** HTTPメソッド DELETE. */
	public static final String HTTP_METHOD_DELETE = "DELETE";

	/** HTTPメソッド. */
	private String method;

	/** リクエストURI. */
	private String uri;

	/** ヘッダー群. */
	private Map<String, String> headers;

	/** HTTPリクエストのBodyデータ. */
	private byte[] body;

	/**
	 * HTTPメソッドを取得する.
	 * 
	 * @return HTTPメソッド名
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * HTTPメソッド名を設定する
	 * 
	 * @param method
	 *            HTTPメソッド名
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * リクエストURIを取得する
	 * 
	 * @return uri リクエストURI
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * リクエストURIを設定する
	 * 
	 * @param uri
	 *            設定するURI
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}

	/**
	 * ヘッダーを取得する.
	 * 
	 * @return ヘッダー
	 */
	public Map<String, String> getHeaders() {
		return headers;
	}

	/**
	 * ヘッダーを設定する.
	 * 
	 * @param headers
	 *            設定するヘッダーのデータ
	 */
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	/**
	 * HTTPリクエストのBodyデータを取得する.
	 * 
	 * @return HTTPリクエストのBodyデータ
	 */
	public byte[] getBody() {
		return body;
	}

	/**
	 * HTTPリクエストのBodyデータを設定する.
	 * 
	 * @param body
	 *            HTTPリクエストのBodyデータ
	 */
	public void setBody(byte[] body) {
		this.body = body;
	}

}
