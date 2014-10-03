package com.nttdocomo.android.dconnect.deviceplugin.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Chromecast HttpServer クラス
 * 
 * <p>
 * HttpServer機能を提供<br/>
 * - 選択されたファイルのみ配信する<br/>
 * </p>
 * 
 */
public class ChromeCastHttpServer extends NanoHTTPD {

	private String serverFileDir = null;
	private String serverFileRealName = null;
	private String serverFileDummyName = null;

	/**
     * コンストラクタ
     * 
     * @param	host	ipアドレス
     * @param	port	ポート番号
     * @return	なし
     */
	public ChromeCastHttpServer(String host, int port) {
		super(host, port);
	}

	/**
     * クライアントに応答する
     * 
     * @param	session		セッション
     * @return	response	レスポンス
     */
	public Response serve(IHTTPSession session) {
		Map<String, String> header = session.getHeaders();
		String uri = session.getUri();
		return respond(Collections.unmodifiableMap(header), session, uri);
	}

	/**
     * ファイルを設定する
     * 
     * @param	dir			ファイルのディレクトリへのパス
     * @param	realname	ファイル名
     * @param	dummyName	ファイル名 (ダミー)
     * @return	なし
     */
	public void setFilePath(String dir, String realName, String dummyName) {
		serverFileDir = dir;
		serverFileRealName = realName;
		serverFileDummyName = dummyName;
	}
	
	/**
     * クライアントをチェックする
     * 
     * @param	headers		ヘッダー
     * @return	有効か否か		(true: 有効, false: 無効)
     */
	private boolean checkRemote(Map<String, String> headers){
		String remoteAddr = headers.get("remote-addr");		
		InetAddress addr;
		try {
			addr = InetAddress.getByName(remoteAddr);
			if(addr.isSiteLocalAddress()){
				return true;
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
     * クライアントへのレスポンスを作成する
     * 
     * @param	headers		ヘッダー
     * @param	session		セッション
     * @param	uri			ファイルのURI
     * @return	response	レスポンス
     */
	private Response respond(Map<String, String> headers, IHTTPSession session,
			String uri) {
		
		if(!checkRemote(headers)){
			return createResponse(Response.Status.FORBIDDEN, NanoHTTPD.MIME_PLAINTEXT, "");
		}
		
		if(serverFileDir == null || serverFileRealName == null || serverFileDummyName == null || !serverFileDummyName.equals(uri)){
			serverFileDir = null;
			serverFileRealName = null;
			serverFileDummyName = null;
			return createResponse(Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "");
		}

		File file = new File(serverFileDir, serverFileRealName);

		Response response = null;
		response = serveFile(uri, headers, file, "");

		if (response == null) {
			return createResponse(Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "");
		}

		return response;
	}

	/**
     * レスポンスを作成する
     * 
     * @param	status		ステータス
     * @param	mimeType	MIMEタイプ
     * @param	message		メッセージ (InputStream)
     * @return	response	レスポンス
     */
	private Response createResponse(Response.Status status, String mimeType,
			InputStream message) {
		Response res = new Response(status, mimeType, message);
		res.addHeader("Accept-Ranges", "bytes");
		return res;
	}

	/**
     * レスポンスを作成する
     * 
     * @param	status		ステータス
     * @param	mimeType	MIMEタイプ
     * @param	message		メッセージ (String)
     * @return	response	レスポンス
     */
	private Response createResponse(Response.Status status, String mimeType,
			String message) {
		Response res = new Response(status, mimeType, message);
		res.addHeader("Accept-Ranges", "bytes");
		return res;
	}

	/**
     * ファイルのレスポンスを作成する
     * 
     * @param	uri			ファイルのURI
     * @param	header		ヘッダー
     * @param	file		ファイル
     * @param	mime		MIMEタイプ
     * @return	response	レスポンス
     */
	Response serveFile(String uri, Map<String, String> header, File file,
			String mime) {

		Response res;
		try {
			String etag = Integer.toHexString((file.getAbsolutePath()
					+ file.lastModified() + "" + file.length()).hashCode());

			long startFrom = 0;
			long endAt = -1;
			String range = header.get("range");
			if (range != null) {
				if (range.startsWith("bytes=")) {
					range = range.substring("bytes=".length());
					int minus = range.indexOf('-');
					try {
						if (minus > 0) {
							startFrom = Long.parseLong(range
									.substring(0, minus));
							endAt = Long.parseLong(range.substring(minus + 1));
						}
					} catch (NumberFormatException ignored) {
					}
				}
			}

			long fileLen = file.length();
			if (range != null && startFrom >= 0) {
				if (startFrom >= fileLen) {
					res = createResponse(Response.Status.RANGE_NOT_SATISFIABLE, NanoHTTPD.MIME_PLAINTEXT, "");
					res.addHeader("Content-Range", "bytes 0-0/" + fileLen);
					res.addHeader("ETag", etag);
				} else {
					if (endAt < 0) {
						endAt = fileLen - 1;
					}
					long newLen = endAt - startFrom + 1;
					if (newLen < 0) {
						newLen = 0;
					}

					final long dataLen = newLen;
					FileInputStream fis = new FileInputStream(file) {
						@Override
						public int available() throws IOException {
							return (int) dataLen;
						}
					};
					fis.skip(startFrom);

					res = createResponse(Response.Status.PARTIAL_CONTENT, mime, fis);
					res.addHeader("Content-Length", "" + dataLen);
					res.addHeader("Content-Range", "bytes " + startFrom + "-" + endAt + "/" + fileLen);
					res.addHeader("ETag", etag);
				}
			} else {
				if (etag.equals(header.get("if-none-match")))
					res = createResponse(Response.Status.NOT_MODIFIED, mime, "");
				else {
					res = createResponse(Response.Status.OK, mime, new FileInputStream(file));
					res.addHeader("Content-Length", "" + fileLen);
					res.addHeader("ETag", etag);
				}
			}
		} catch (IOException ioe) {
			res = createResponse(Response.Status.FORBIDDEN, NanoHTTPD.MIME_PLAINTEXT, "");
		}

		return res;
	}
}
