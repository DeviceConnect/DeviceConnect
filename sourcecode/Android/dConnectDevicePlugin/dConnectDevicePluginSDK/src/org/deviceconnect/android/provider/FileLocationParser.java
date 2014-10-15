/*
 FileLocationParser.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.provider;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ProviderInfo;
import android.content.res.XmlResourceParser;

/**
 * ファイルの保存場所を解析する.
 * @author NTT DOCOMO, INC.
 */
final class FileLocationParser {
    /**
     * ファイル保存場所を格納するメタデータの名前.
     */
    public static final String FILE_PROVIDER_META_DATA = "filelocation";

    /**
     * Environment.getExternalStorageDirectory()で取得できる外部領域を表す.
     */
    public static final int TYPE_EXTERNAL_PATH = 1;

    /**
     * Context.getFilesDir()で取得できるfiles/を表す.
     */
    public static final int TYPE_INTERNAL_PATH = 2;

    /**
     * 外部領域を表すタグ.
     */
    private static final String TAG_EXTERNAL_PATH = "external-location";

    /**
     * アプリ内部保存領域を表すタグ.
     */
    private static final String TAG_INTERNAL_PATH = "internal-location";

    /**
     * パスを表すタグ.
     */
    private static final String ATTR_PATH = "path";

    /**
     * コンストラクタ.
     * ユーティリティクラスなので、privateにしておく。
     */
    private FileLocationParser() {
    }

    /**
     * AndroidManifest.xmlからproviderタグを探し出して、ファイルの保存先が定義されているxmlを解析する.
     * 
     * <p>
     * 保存箇所が設定されていない場合には、デフォルトの保存場所を指定する。<br/>
     * デフォルトの保存場所は、外部領域(SDカード)の直下にデバイスプラグインのパッケージで作成する。
     * </p>
     * 
     * @param context コンテキスト
     * @return ファイルの保存場所
     */
    public static FileLocation parse(final Context context) {
        final String className = FileProvider.class.getName();
        PackageManager pkgMgr = context.getPackageManager();
        FileLocation location = null;
        try {
            PackageInfo packageInfo = pkgMgr.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_PROVIDERS | PackageManager.GET_META_DATA);
            ProviderInfo[] providers = packageInfo.providers;
            if (providers != null) {
                for (ProviderInfo provider : providers) {
                    if (className.equals(provider.name)) {
                        location = parse(pkgMgr, provider);
                    }
                }
            }
        } catch (NameNotFoundException e) {
            location = null;
        }

        // デフォルト設定
        if (location == null) {
            location = new FileLocation();
            location.mType = TYPE_EXTERNAL_PATH;
            location.mPath = context.getPackageName();
        } else {
            // パスが指定されていない場合は、デフォルトを設定
            if (location.mPath == null) {
                location.mPath = context.getPackageName();
            }
        }
        return location;
    }

    /**
     * providerタグに存在するメタデータからxmlを読み込み、解析を行う.
     * 
     * <p>
     * 解析に失敗もしくは、メタデータが存在しない場合には<code>null</code>を返却する。
     * </p>
     * 
     * @param pkgMgr パッケージマネジャー
     * @param info providerタグ情報
     * @return 解析結果
     */
    private static FileLocation parse(final PackageManager pkgMgr, final ProviderInfo info) {
        XmlResourceParser xpp = info.loadXmlMetaData(pkgMgr, FILE_PROVIDER_META_DATA);
        if (xpp == null) {
            return null;
        }
        try {
           return parseFilePath(xpp);
        } catch (XmlPullParserException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * ファイル保存場所が記載されているxmlを解析する.
     * @param xpp xmlパーサ
     * @return 解析結果
     * @throws XmlPullParserException xmlの解析に失敗した場合
     * @throws IOException xmlファイルの読み込みに失敗した場合
     */
    private static FileLocation parseFilePath(final XmlResourceParser xpp) 
            throws XmlPullParserException, IOException {
        FileLocation location = new FileLocation();
        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            final String name = xpp.getName();
            switch (eventType) {
            case XmlPullParser.START_TAG:
                if (TAG_EXTERNAL_PATH.equals(name)) {
                    location.mType = TYPE_EXTERNAL_PATH;
                    location.mPath = xpp.getAttributeValue(null, ATTR_PATH);
                } else if (TAG_INTERNAL_PATH.equals(name)) {
                    location.mType = TYPE_INTERNAL_PATH;
                    location.mPath = xpp.getAttributeValue(null, ATTR_PATH);
                }
                break;
            case XmlPullParser.END_TAG:
                break;
            default:
                break;
            }
            eventType = xpp.next();
        }

        return location;
    }
    
    /**
     * ファイル保存場所のデータを格納するクラス.
     * @author NTT DOCOMO, INC.
     */
    public static class FileLocation {
        /**
         * 保存場所.
         * <ul>
         * <li>{@link FileLocationParser#TYPE_EXTERNAL_PATH}外部保存領域</li>
         * <li>{@link FileLocationParser#TYPE_INTERNAL_PATH}アプリ保存領域</li>
         * </ul>
         */
        private int mType;

        /**
         * 保存するパス.
         */
        private String mPath;

        /**
         * 保存場所のタイプを取得する.
         * @return タイプ
         */
        public int getType() {
            return mType;
        }
        /**
         * 保存場所のタイプを取得する.
         * @param type タイプ
         */
        public void setType(final int type) {
            this.mType = type;
        }
        /**
         * パスを取得する.
         * @return パス
         */
        public String getPath() {
            return mPath;
        }
        /**
         * パスを設定する.
         * @param path パス
         */
        public void setPath(final String path) {
            this.mPath = path;
        }
        
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("{")
                .append("type:")
                .append(mType)
                .append(", path:")
                .append(mPath)
                .append("}");
            return builder.toString();
        }
    }
}
