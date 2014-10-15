/*
 URIBuilder.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.deviceconnect.message.DConnectMessage;

/**
 * 指定された情報からAPIへのURLを提供するクラス.
 * 
 * <h3>サンプルコード</h3>
 * <pre>
 * {@code
 * URIBuilder builder = new URIBuilder();
 * builder.setScheme("http")
 * .setHost("localhost")
 * .setPort(4035)
 * .setProfile(BatteryProfileConstants.PROFILE_NAME)
 * .setAttribute(BatteryProfileConstants.ATTRIBUTE_ON_BATTERY_CHANGE)
 * .addParameter(DConnectMessage.EXTRA_DEVICE_ID, "deviceId")
 * .addParameter(DConnectMessage.EXTRA_SESSION_KEY, "sessionKey")
 * .addParameter(DConnectMessage.EXTRA_ACCESS_TOKEN, "accessToken");
 * 
 * URI uri = builder.build();
 * }
 * </pre>
 * 
 * 
 * @author NTT DOCOMO, INC.
 */
public class URIBuilder {

    /**
     * スキーム.
     */
    private String mScheme;

    /**
     * ホスト.
     */
    private String mHost;

    /**
     * ポート番号.
     */
    private int mPort = -1;

    /**
     * パス.
     */
    private String mPath = null;

    /**
     * パラメータ.
     */
    private List<NameValuePair> mParameters;

    /** API. */
    private String mApi = DConnectMessage.DEFAULT_API;
    
    /** プロファイル. */
    private String mProfile;
    /** インターフェース. */
    private String mInterface;
    /** アトリビュート. */
    private String mAttribute;

    /**
     * ロガー.
     */
    private Logger mLogger = Logger.getLogger("org.deviceconnect.sdk");

    /**
     * コンストラクタ.
     */
    public URIBuilder() {
    }

    /**
     * URIから {@link URIBuilder} クラスを生成する.
     *
     * @param uri URI
     * @throws URISyntaxException URIフォーマットが不正な場合
     */
    public URIBuilder(final String uri) throws URISyntaxException {
        this(new URI(uri));
    }

    /**
     * URIから {@link URIBuilder} クラスを生成する.
     *
     * @param uri URI
     */
    public URIBuilder(final URI uri) {
        mScheme = uri.getScheme();
        mHost = uri.getHost();
        mPort = uri.getPort();
        mPath = uri.getPath();

        String query = uri.getQuery();
        mLogger.fine("uri query: " + query);
        if (query != null) {
            String[] params = query.split("&");
            for (String param : params) {
                String[] splitted = param.split("=");
                if (splitted != null && splitted.length == 2) {
                    addParameter(splitted[0], splitted[1]);
                } else {
                    addParameter(splitted[0], "");
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized String toString() {
        return toString(false);
    }

    /**
     * ASCIIのオブジェクト文字列を取得する.
     *
     * @return オブジェクト文字列
     */
    public String toASCIIString() {
        return toString(true);
    }

    /**
     * スキームを取得する.
     *
     * @return スキーム
     */
    public synchronized String getScheme() {
        return mScheme;
    }

    /**
     * スキームを設定する.
     *
     * @param scheme スキーム
     * @return {@link URIBuilder} インスタンス
     */
    public synchronized URIBuilder setScheme(final String scheme) {
        mScheme = scheme;
        return this;
    }

    /**
     * ホスト名を取得する.
     *
     * @return ホスト名
     */
    public synchronized String getHost() {
        return mHost;
    }

    /**
     * ホスト名を設定する.
     *
     * @param host ホスト名
     * @return {@link URIBuilder} インスタンス
     */
    public synchronized URIBuilder setHost(final String host) {
        mHost = host;
        return this;
    }

    /**
     * ポート番号を取得する. ポート番号が指定されていない場合は-1を返す
     *
     * @return ポート番号
     */
    public synchronized int getPort() {
        return mPort;
    }

    /**
     * ポート番号を設定する.
     *
     * @param port
     *            ポート番号
     * @return {@link URIBuilder} インスタンス
     */
    public synchronized URIBuilder setPort(final int port) {
        mPort = port;
        return this;
    }

    /**
     * パスを取得する.
     *
     * @return パス
     */
    public synchronized String getPath() {
        return mPath;
    }

    /**
     * APIのパスを文字列で設定する.
     * このパラメータが設定されている場合はビルド時に api、profile、interface、attribute は無視される。
     * 
     * @param path パス
     * @return {@link URIBuilder} インスタンス
     */
    public synchronized URIBuilder setPath(final String path) {
        mPath = path;
        return this;
    }

    /**
     * APIを取得する.
     * @return API
     */
    public synchronized String getApi() {
        return mApi;
    }

    /**
     * APIを取得する.
     * パスが設定されている場合には、このパラメータは無視される。
     * 
     * @param api API
     * @return {@link URIBuilder} インスタンス
     */
    public synchronized URIBuilder setApi(final String api) {
        mApi = api;
        return this;
    }

    /**
     * プロファイルを取得する.
     * @return プロファイル
     */
    public synchronized String getProfile() {
        return mProfile;
    }

    /**
     * プロファイルを設定する.
     *
     * パスが設定されている場合には、このパラメータは無視される。
     * 
     * @param profile プロファイル
     * @return {@link URIBuilder} インスタンス
     */
    public synchronized URIBuilder setProfile(final String profile) {
        mProfile = profile;
        return this;
    }

    /**
     * インターフェースを取得する.
     * @return インターフェース
     */
    public synchronized String getInterface() {
        return mInterface;
    }

    /**
     * インターフェースを設定する.
     *
     * パスが設定されている場合には、このパラメータは無視される。
     * 
     * @param inter インターフェース
     * @return {@link URIBuilder} インスタンス
     */
    public synchronized URIBuilder setInterface(final String inter) {
        mInterface = inter;
        return this;
    }

    /**
     * アトリビュートを取得する.
     * @return アトリビュート
     */
    public synchronized String getAttribute() {
        return mAttribute;
    }

    /**
     * アトリビュートを設定する.
     *
     * パスが設定されている場合には、このパラメータは無視される。
     * 
     * @param attribute アトリビュート
     * @return {@link URIBuilder} インスタンス
     */
    public synchronized URIBuilder setAttribute(final String attribute) {
        mAttribute = attribute;
        return this;
    }

    /**
     * クエリパラメータを取得する.
     *
     * @return クエリパラメータ
     */
    public synchronized List<NameValuePair> getQueryParams() {
        return mParameters;
    }

    /**
     * キーバリューでパラメータを追加する.
     *
     * @param name キー
     * @param value バリュー
     * @return {@link URIBuilder} インスタンス
     */
    public synchronized URIBuilder addParameter(final String name, final String value) {
        if (mParameters == null) {
            mParameters = new ArrayList<NameValuePair>();
        }
        mParameters.add(new BasicNameValuePair(name, value));
        return this;
    }

    /**
     * クエリパラメータを設定する.
     *
     * @param params パラメータ
     * @return {@link URIBuilder} インスタンス
     */
    public synchronized URIBuilder setParameters(final List<NameValuePair> params) {
        mParameters = params;
        return this;
    }

    /**
     * {@link URI} オブジェクトを取得する.
     *
     * @return {@link URI} オブジェクト
     * @throws URISyntaxException URIフォーマットが不正な場合
     */
    public URI build() throws URISyntaxException {
        URI uri = new URI(toString(true));
        return uri;
    }

    /**
     * URIを文字列にして取得する.
     * @param ascii ASCII変換の有無
     * @return URIを表す文字列
     */
    private synchronized String toString(final boolean ascii) {
        StringBuilder builder = new StringBuilder();

        if (mScheme != null) {
            builder.append(mScheme);
            builder.append("://");
        }
        if (mHost != null) {
            builder.append(mHost);
        }
        if (mPort > 0) {
            builder.append(":");
            builder.append(mPort);
        }
        if (mPath != null) {
            builder.append(mPath);
        } else {
            if (mApi != null) {
                builder.append("/");
                builder.append(mApi);
            }
            if (mProfile != null) {
                builder.append("/");
                builder.append(mProfile);
            }
            if (mInterface != null) {
                builder.append("/");
                builder.append(mInterface);
            }
            if (mAttribute != null) {
                builder.append("/");
                builder.append(mAttribute);
            }
        }

        if (mParameters != null && mParameters.size() > 0) {
            if (ascii) {
                builder.append("?");
                builder.append(URLEncodedUtils.format(mParameters, "UTF-8"));
            } else {
                for (int i = 0; i < mParameters.size(); i++) {
                    NameValuePair pair = mParameters.get(i);

                    if (i == 0) {
                        builder.append("?");
                    } else {
                        builder.append("&");
                    }

                    builder.append(pair.getName());
                    builder.append("=");
                    builder.append(pair.getValue());
                }
            }
        }

        return builder.toString();

    }
}
