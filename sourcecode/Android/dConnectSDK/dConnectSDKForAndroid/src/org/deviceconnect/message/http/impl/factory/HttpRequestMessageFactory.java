/*
 HttpRequestMessageFactory.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.message.http.impl.factory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.protocol.HTTP;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.message.basic.message.DConnectRequestMessage;
import org.deviceconnect.utils.URIBuilder;

/**
 * HTTPリクエストメッセージファクトリー.
 * @author NTT DOCOMO, INC.
 */
public class HttpRequestMessageFactory extends AbstractHttpMessageFactory<HttpRequest> {

    /**
     * メッセージファクトリー.
     */
    private static HttpRequestMessageFactory mHttpMessageFactory =
            new HttpRequestMessageFactory();

    /**
     * ロガー.
     */
    private Logger mLogger = Logger.getLogger("org.deviceconnect.sdk");

    /**
     * メッセージファクトリーを取得する.
     * @return メッセージファクトリー
     */
    public static HttpRequestMessageFactory getMessageFactory() {
        return mHttpMessageFactory;
    }

    @Override
    public Class<HttpRequest> getPackagedClass() {
        return HttpRequest.class;
    }

    @Override
    public HttpRequest newPackagedMessage(final DConnectMessage message) {
        mLogger.entering(this.getClass().getName(), "newPackagedMessage", message);

        mLogger.fine("create http request from dmessage");
        HttpRequest request;
        try {
            request = createHttpRequest(message);
        } catch (URISyntaxException e) {
            mLogger.log(Level.INFO, e.toString(), e);
            mLogger.warning(e.toString());
            throw new IllegalArgumentException(e.toString());
        }

        mLogger.fine("put request headers");
        for (Header header: createHttpHeader(message)) {
            request.addHeader(header);
        }

        mLogger.fine("put request body");
        if (request instanceof HttpEntityEnclosingRequest) {
            HttpEntity entity = createHttpEntity(message);
            ((HttpEntityEnclosingRequest) request).setEntity(entity);
            request.addHeader(HTTP.CONTENT_LEN, "" + entity.getContentLength());
        } else {
            request.addHeader(HTTP.CONTENT_LEN, "0");
        }

        mLogger.exiting(this.getClass().getName(), "newPackagedMessage", request);
        return request;
    }

    @Override
    public DConnectMessage newDConnectMessage(final HttpRequest message) {
        mLogger.entering(this.getClass().getName(), "newDConnectMessage");

        DConnectRequestMessage dmessage = null;

        mLogger.fine("create dconnect message from request");
        dmessage = (DConnectRequestMessage) parseFirstLine(message);

        mLogger.fine("parse request headers");
        parseHttpHeader(dmessage, message);

        mLogger.fine("parser request line");
        parseRequestLine(dmessage, message);

        mLogger.fine("parse request query");
        parseRequestQuery(dmessage, message);

        mLogger.fine("parse request body");
        parseHttpBody(dmessage, message);

        mLogger.exiting(this.getClass().getName(), "newDConnectMessage", dmessage);
        return dmessage;
    }

    /**
     * HTTPリクエストラインを解析してdConnectメッセージに格納する.
     * @param dmessage dConnectメッセージ
     * @param request HTTPリクエスト
     */
    protected void parseRequestLine(
            final DConnectMessage dmessage, final HttpRequest request) {

        // parse uri, and set profile, interface and attribute
        URIBuilder uriBuilder;
        try {
            uriBuilder = new URIBuilder(request.getRequestLine().getUri());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }

        String uri = uriBuilder.getPath();
        int sepIndexAPI = uri.indexOf('/', 1);
        int sepIndexProfile = uri.indexOf('/', sepIndexAPI + 1);
        int sepIndexInterface = uri.indexOf('/', sepIndexProfile + 1);

        String api = null;
        String profile = null;
        String inter = null;
        String attribute = null;
        
        if (sepIndexAPI < 0) {
            // case: /gotapi
            api = uri.substring(1);
        } else if (sepIndexProfile < 0) {
            // case: /gotapi/system
            api = uri.substring(1, sepIndexAPI);
            profile = uri.substring(sepIndexAPI + 1, uri.length());
        } else if (sepIndexInterface < 0) {
            // case: /gotapi/notification/notify
            api = uri.substring(1, sepIndexAPI);
            profile = uri.substring(sepIndexAPI + 1, sepIndexProfile);
            attribute = uri.substring(sepIndexProfile + 1, uri.length());
        } else {
            // case: /gotapi/system/device/information
            api = uri.substring(1, sepIndexAPI);
            profile = uri.substring(sepIndexAPI + 1, sepIndexProfile);
            inter = uri.substring(sepIndexProfile + 1, sepIndexInterface);
            attribute = uri.substring(sepIndexInterface + 1, uri.length());
        }

        mLogger.fine("request profile: " + profile);
        mLogger.fine("request interface: " + inter);
        mLogger.fine("request attribute: " + attribute);

        if (api != null) {
            dmessage.put(DConnectMessage.EXTRA_API, api);
        }
        if (profile != null) {
            dmessage.put(DConnectMessage.EXTRA_PROFILE, profile);
        }
        if (inter != null) {
            dmessage.put(DConnectMessage.EXTRA_INTERFACE, inter);
        }
        if (attribute != null) {
            dmessage.put(DConnectMessage.EXTRA_ATTRIBUTE, attribute);
        }

    }

    /**
     * HTTPリクエストクエリを解析してdConnectメッセージに格納する.
     * @param dmessage dConnectメッセージ
     * @param request HTTPリクエスト
     */
    protected void parseRequestQuery(
            final DConnectMessage dmessage, final HttpRequest request) {

        // parse query
        try {
            List<NameValuePair> query = URLEncodedUtils.parse(
                    new URI(request.getRequestLine().getUri()), "UTF-8");

            if (query != null && query.size() > 0) {
                mLogger.fine("request query size: " + query.size());
                mLogger.fine("request query: " + query);
                for (NameValuePair pair: query) {
                    mLogger.fine("request key/value: " + pair.getName() + ", " + pair.getValue());
                    try {
                        dmessage.put(pair.getName(), Integer.parseInt(pair.getValue()));
                    } catch (NumberFormatException e) {
                        dmessage.put(pair.getName(), pair.getValue());
                    }
                }
            }
        } catch (URISyntaxException e) {
            mLogger.warning(e.toString());
        }

    }

    /**
     * dConnectメッセージからHTTPリクエストを生成する.
     * @param dmessage dConnectメッセージ
     * @return HTTPリクエスト
     * @throws URISyntaxException URIシンタックスエラーの場合
     */
    protected HttpRequest createHttpRequest(
            final DConnectMessage dmessage) throws URISyntaxException {

        String method = dmessage.getString(DConnectMessage.EXTRA_METHOD);
        if (!DConnectMessage.METHOD_GET.equals(method)
                && !DConnectMessage.METHOD_POST.equals(method)
                && !DConnectMessage.METHOD_PUT.equals(method)
                && !DConnectMessage.METHOD_DELETE.equals(method)) {
            throw new IllegalArgumentException("invalid request method: " + method);
        }

        URIBuilder uriBuilder = new URIBuilder();

        // Make request path, /[api]/[profile]/[interface]/[attribute]
        //   from api, profile, interface and attribute
        StringBuilder path = new StringBuilder();
        String api = dmessage.getString(DConnectMessage.EXTRA_API);
        String profile = dmessage.getString(DConnectMessage.EXTRA_PROFILE);
        String inter = dmessage.getString(DConnectMessage.EXTRA_INTERFACE);
        String attr = dmessage.getString(DConnectMessage.EXTRA_ATTRIBUTE);
        
        if (api != null) {
            path.append("/");
            path.append(api);
        }
        
        if (profile != null) {
            path.append("/");
            path.append(profile);
        }
        if (inter != null) {
            path.append("/");
            path.append(inter);
        }
        if (attr != null) {
            path.append("/");
            path.append(attr);
        }
        uriBuilder.setPath(path.toString());

        // Make HttpHost, [scheme]://[host]:[port]
        //   from internal parameters
        String scheme = dmessage.getString(DConnectMessage.EXTRA_SCHEME);
        if (scheme != null) {
            uriBuilder.setScheme(scheme);
        }
        String host = dmessage.getString(DConnectMessage.EXTRA_HOST);
        if (host != null) {
            uriBuilder.setHost(host);
        }
        int port = dmessage.getInt(DConnectMessage.EXTRA_PORT);
        if (port > 0) {
            uriBuilder.setPort(port);
        }


        // Put queries, if method is GET or DELETE
        // - Ignore key starts with "_", that is internal parameter for client
        if (DConnectMessage.METHOD_GET.equals(method)
                || DConnectMessage.METHOD_DELETE.equals(method)) {
            Set<String> names = dmessage.keySet();
            if (names != null) {
                for (String name: names) {
                    if (name.equals(DConnectMessage.EXTRA_METHOD)
                            || name.equals(DConnectMessage.EXTRA_API)
                            || name.equals(DConnectMessage.EXTRA_PROFILE)
                            || name.equals(DConnectMessage.EXTRA_INTERFACE)
                            || name.equals(DConnectMessage.EXTRA_ATTRIBUTE)
                            || name.startsWith("_")) {
                        continue;
                    }
                    if (dmessage.get(name) != null) {
                        uriBuilder.addParameter(name, dmessage.get(name).toString());
                    } else {
                        uriBuilder.addParameter(name, null);
                    }
                }
            }
        }

        HttpRequest request;
        if (DConnectMessage.METHOD_GET.equals(method)) {
            request = new HttpGet(uriBuilder.build());
        } else if (DConnectMessage.METHOD_PUT.equals(method)) {
            request = new HttpPut(uriBuilder.build());
        } else if (DConnectMessage.METHOD_POST.equals(method)) {
            request = new HttpPost(uriBuilder.build());
        } else if (DConnectMessage.METHOD_DELETE.equals(method)) {
            request = new HttpDelete(uriBuilder.build());
        } else {
            throw new IllegalArgumentException("invalid request method: " + method);
        }

        return request;
    }

}
