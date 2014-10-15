/*
 AbstractHttpMessageFactory.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.message.http.impl.factory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpMessage;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.descriptor.BodyDescriptor;
import org.apache.james.mime4j.message.SimpleContentHandler;
import org.apache.james.mime4j.parser.Field;
import org.apache.james.mime4j.parser.MimeEntityConfig;
import org.apache.james.mime4j.parser.MimeStreamParser;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.message.HttpHeaders;
import org.deviceconnect.message.basic.message.BasicDConnectMessage;
import org.deviceconnect.message.basic.message.DConnectRequestMessage;
import org.deviceconnect.message.basic.message.DConnectResponseMessage;
import org.deviceconnect.message.factory.MessageFactory;
import org.json.JSONException;

/**
 * HTTPメッセージファクトリ.
 * @param <M> メッセージクラス
 * @author NTT DOCOMO, INC.
 */
public abstract class AbstractHttpMessageFactory<M extends HttpMessage>
        implements MessageFactory<M> {

    /**
     * バッファサイズ.
     */
    private static final int BUFFER_SIZE = 1024;

    /**
     * ロガー.
     */
    private Logger mLogger = Logger.getLogger("org.deviceconnect.sdk");

    /**
     * HTTPメッセージ 1行目を解析してdConnectメッセージを作成する.
     * @param message HTTPメッセージ
     * @return dConnectメッセージ
     */
    protected DConnectMessage parseFirstLine(final M message) {
        mLogger.entering(getClass().getName(), "parseFirstLine", message);
        DConnectMessage dmessage = null;

        if (message instanceof HttpRequest) {

            // put action
            String method = ((HttpRequest) message).getRequestLine().getMethod();
            mLogger.fine("HTTP request method: " + method);

            DConnectRequestMessage drequest = new DConnectRequestMessage();
            if (HttpGet.METHOD_NAME.equals(method)) {
                drequest.setMethod(DConnectRequestMessage.METHOD_GET);
            } else if (HttpPut.METHOD_NAME.equals(method)) {
                drequest.setMethod(DConnectRequestMessage.METHOD_PUT);
            } else if (HttpPost.METHOD_NAME.equals(method)) {
                drequest.setMethod(DConnectRequestMessage.METHOD_POST);
            } else if (HttpDelete.METHOD_NAME.equals(method)) {
                drequest.setMethod(DConnectRequestMessage.METHOD_DELETE);
            } else {
                throw new IllegalArgumentException("invalid http request mehtod: " + method);
            }

            dmessage = drequest;
        } else if (message instanceof HttpResponse) {
            dmessage = new DConnectResponseMessage();
        } else {
            throw new IllegalArgumentException(
                    "unkown http message class instance: " + message.getClass().getName());
        }

        mLogger.exiting(getClass().getName(), "parseFirstLine", dmessage);
        return dmessage;
    }

    /**
     * HTTPヘッダを解析してdConnectメッセージへ格納する.
     * @param dmessage dConnectメッセージ
     * @param message HTTPメッセージ
     */
    protected void parseHttpHeader(final DConnectMessage dmessage, final M message) {
        mLogger.entering(getClass().getName(), "newDConnectMessage",
                new Object[] {dmessage, message});

        Header requestCode = message.getFirstHeader(HttpHeaders.X_REQUEST_CODE);
        if (requestCode != null) {
            try {
                dmessage.put(DConnectMessage.EXTRA_REQUEST_CODE,
                        Integer.parseInt(requestCode.getValue()));
            } catch (NumberFormatException e) {
                mLogger.warning(HttpHeaders.X_REQUEST_CODE + " is not number: " + requestCode);
            }
        }

        mLogger.exiting(getClass().getName(), "newDConnectMessage");
    }

    /**
     * HTTPボディを解析してdConnectメッセージへ格納する.
     * @param dmessage dConnectメッセージ
     * @param message HTTPメッセージ
     */
    protected void parseHttpBody(final DConnectMessage dmessage, final M message) {
        mLogger.entering(getClass().getName(), "newDConnectMessage",
                new Object[] {dmessage, message});

        HttpEntity entity = getHttpEntity(message);
        if (entity != null) {

            MimeStreamParser parser = new MimeStreamParser(new MimeEntityConfig());
            MultipartContentHandler handler = new MultipartContentHandler(dmessage);
            parser.setContentHandler(handler);

            StringBuilder headerBuffer = new StringBuilder();

            for (Header header: message.getAllHeaders()) {
                headerBuffer.append(header.getName());
                headerBuffer.append(": ");
                headerBuffer.append(header.getValue());
                headerBuffer.append(Character.toChars(HTTP.CR));
                headerBuffer.append(Character.toChars(HTTP.LF));
                mLogger.fine("header: " + header.getName() + ":" + header.getValue());
            }
            headerBuffer.append(Character.toChars(HTTP.CR));
            headerBuffer.append(Character.toChars(HTTP.LF));

            try {
                parser.parse(new SequenceInputStream(
                        new ByteArrayInputStream(headerBuffer.toString().getBytes("US-ASCII")),
                        entity.getContent()));
            } catch (IllegalStateException e) {
                mLogger.log(Level.FINE, e.toString(), e);
                mLogger.warning(e.toString());
            } catch (MimeException e) {
                mLogger.log(Level.FINE, e.toString(), e);
                mLogger.warning(e.toString());
            } catch (IOException e) {
                mLogger.log(Level.FINE, e.toString(), e);
                mLogger.warning(e.toString());
            }
        }

        mLogger.exiting(getClass().getName(), "newDConnectMessage");
    }

    /**
     * dConnectメッセージからHTTPヘッダリストを生成する.
     * @param dmessage dConnecメッセージ
     * @return HTTPヘッダリスト
     */
    protected List<Header> createHttpHeader(final DConnectMessage dmessage) {
        mLogger.entering(getClass().getName(), "createHttpHeader", dmessage);
        List<Header> headers = new ArrayList<Header>();

        Object requestCode = dmessage.get(DConnectMessage.EXTRA_REQUEST_CODE);
        if (requestCode != null) {
            headers.add(new BasicHeader(HttpHeaders.X_REQUEST_CODE, requestCode.toString()));
        }

        mLogger.exiting(getClass().getName(), "createHttpHeader", headers);
        return headers;
    }

    /**
     * dConnectメッセージからHTTPエンティティを生成する.
     * @param dmessage dConnectメッセージ
     * @return HTTPエンティティ
     */
    protected HttpEntity createHttpEntity(final DConnectMessage dmessage) {
        mLogger.entering(getClass().getName(), "createHttpEntity", dmessage);

        HttpEntity entity = null;
        try {
            DConnectMessage message = new BasicDConnectMessage(dmessage);
            message.remove(DConnectMessage.EXTRA_PROFILE);
            message.remove(DConnectMessage.EXTRA_INTERFACE);
            message.remove(DConnectMessage.EXTRA_ATTRIBUTE);
            message.remove(DConnectMessage.EXTRA_METHOD);

            entity = new ByteArrayEntity(message.toString(2).getBytes(HTTP.UTF_8));
        } catch (UnsupportedEncodingException e) {
            mLogger.log(Level.FINE, e.toString(), e);
            mLogger.warning(e.toString());
        }

        mLogger.exiting(getClass().getName(), "createHttpEntity", entity);
        return entity;
    }

    /**
     * HTTPエンティティを取得する.
     * HTTPメッセージがHTTPエンティティを持っていない場合は null を返す。
     * @param message HTTPメッセージ
     * @return HTTPエンティティ
     */
    private HttpEntity getHttpEntity(final M message) {
        mLogger.entering(getClass().getName(), "getHttpEntity", message);
        HttpEntity entity = null;

        if (message instanceof HttpEntityEnclosingRequest) {
            entity = ((HttpEntityEnclosingRequest) message).getEntity();
        } else if (message instanceof HttpResponse) {
            entity = ((HttpResponse) message).getEntity();
        }

        mLogger.exiting(getClass().getName(), "getHttpEntity", entity);
        return entity;
    }

    /**
     * 入力ストリームからbyte配列を読み込む.
     * @param is 入力ストリーム
     * @return byte配列
     * @throws IOException I/Oエラーが発生した場合
     */
    private byte[] loadBytes(final InputStream is) throws IOException {
        mLogger.entering(this.getClass().getName(), "loadBytes");

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        byte[] buf = new byte[BUFFER_SIZE];
        while (true) {
            int len = is.read(buf);
            if (len < 0) {
                break;
            }
            bout.write(buf, 0, len);
        }
        byte[] bytes = bout.toByteArray();

        mLogger.exiting(this.getClass().getName(), "loadBytes", bytes);
        return bytes;
    }

    /**
     * マルチパートコンテントハンドラー.
     */
    private class MultipartContentHandler extends SimpleContentHandler {

        /**
         * メッセージ.
         */
        private DConnectMessage mMessage;
        
        /** 
         * name属性値.
         */
        private String mName;
        
        /** 
         * データがファイルかのフラグ.
         */
        private boolean mFileFlg;
        
        /** 
         * マルチパートかのフラグ.
         */
        private boolean mMultipartFlg;
        
        /** 
         * ボディの解析が始まったかのフラグ.
         * ボディのヘッダーかどうかの判定に使う。
         */
        private boolean mBodyPartFlg;

        /**
         * コンストラクタ.
         * @param dmessage dConnectメッセージ
         */
        public MultipartContentHandler(final DConnectMessage dmessage) {
            mMessage = dmessage;
        }
        
        @Override
        public void startMultipart(final BodyDescriptor bd) throws MimeException {
            super.startMultipart(bd);
            mMultipartFlg = true;
        }
        
        @Override
        public void endMultipart() throws MimeException {
            super.endMultipart();
            mMultipartFlg = false;
        }
        
        @Override
        public void startBodyPart() throws MimeException {
            super.startBodyPart();
            mBodyPartFlg = true;
        }

        @Override
        public void endBodyPart() throws MimeException {
            super.endBodyPart();
            mBodyPartFlg = false;
        }

        @Override
        public void headers(final org.apache.james.mime4j.message.Header header) {
            
            if (mMultipartFlg && mBodyPartFlg) {
                for (Field field: header.getFields()) {
                    if (field.getName().equalsIgnoreCase("Content-Disposition")) {
                        
                        String body = field.getBody();
                        String[] attrs = body.split(";");
                        for (String attr : attrs) {
                            if (attr.contains("filename")) {
                                mFileFlg = true;
                            } else if (attr.contains("name")) {
                                String[] values = attr.split("=");
                                if (values.length == 2) {
                                    mName = values[1].replaceAll("\"", "");
                                }
                            }
                            
                        }
                    }
                    mLogger.fine("header: " + field.getName() + ":" + field.getBody());
                }
            }
        }

        @Override
        public void bodyDecoded(final BodyDescriptor bd, final InputStream is)
                throws IOException {
            mLogger.entering(getClass().getName(), "bodyDecoded", new Object[] {bd, is});

            byte[] bytes = loadBytes(is);
            
            if (mMultipartFlg && mName != null) {
                if (mFileFlg) {
                    mMessage.put(mName, bytes);
                } else if (bd.getMimeType().equals("text/plain")) {
                    mMessage.put(mName, new String(bytes, HTTP.UTF_8));
                }
            } else if (!mMultipartFlg) {
                if (bd.getMimeType().equals("application/json")) {
                    if (bytes.length > 0) {
                        String body = new String(bytes, HTTP.UTF_8);
                        mLogger.fine("response body: " + body);

                        try {
                            mMessage.putAll(new DConnectResponseMessage(body));
                        } catch (JSONException e) {
                            mLogger.warning(e.toString());
                        }
                    }
                } else if (bd.getMimeType().equals("text/plain")) {
                    String body = new String(bytes, HTTP.UTF_8);
                    // パラメータのパース
                    String[] parameters = body.split("&");
                    if (parameters.length > 0) {
                        for (String param : parameters) {
                            setKeyValue(param);
                        }
                    } else {
                        setKeyValue(body);
                    }
                }
            }

            mName = null;
            mFileFlg = false;
            mLogger.exiting(getClass().getName(), "bodyDecoded");
        }
        
        /**
         * 指定された文字列を解析し、キーバリュー形式で格納する.
         * 
         * @param param パラメータ文字列
         */
        private void setKeyValue(final String param) {
            String[] entry = param.split("=");
            if (entry.length != 2) {
                return;
            }
            
            mMessage.put(entry[0], entry[1]);
        }

    }

}
