/*
 FileProvider.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.provider;

import java.io.File;
import java.io.FileNotFoundException;

import org.deviceconnect.android.provider.FileLocationParser.FileLocation;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;

/**
 * ファイル用のContentProvider.
 * 
 * このクラスはデバイスプラグインで一時的にファイルを保持してdConnectManagerにファイルを
 * 送信するためのContentProviderになる。
 * 
 * FileProfileを実装する場合には、必須となる。
 * 
 * AndroidManifest.xml に以下の記述を追加する必要がある。
 * <pre>
 * &lt;provider
 *     android:name=&quot;org.deviceconnect.android.provider.FileProvider&quot;
 *     android:authorities=&quot;com.mycompany.android.deviceplugin.provider&quot;
 *     android:exported=&quot;true&quot; /&gt;
 * </pre>
 * 
 * android:authoritiesの部分には、各デバイスプラグインでauthoritiesを設定すること。
 * 
 * <p>
 * デフォルトでは、SDカードの直下にデバイスプラグインのパッケージ名のフォルダを作成して、その下にファイルが保存される。<br/>
 * 保存場所を変えたい場合には、providerにメタデータを持たせることで変更できる。
 * </p>
 * 
 * <pre>
 * &lt;provider
 *     android:name=&quot;org.deviceconnect.android.provider.FileProvider&quot;
 *     android:authorities=&quot;com.mycompany.android.deviceplugin.provider&quot;
 *     android:exported=&quot;true&quot;&gt;
 *     &lt;meta-data
 *              android:name=&quot;filelocation&quot;
 *              android:resource=&quot;@xml/filelocation&quot; /&gt;
 * &lt;provider&gt;
 * </pre>
 * 
 * res/xml/filelocation.xmlを用意する。<br/>
 * 
 * 以下のようにexternal-locationタグを指定することで、SDカードに保存される。
 * <pre>
 * &lt;file-locations xmlns:android=&quot;http://schemas.android.com/apk/res/android&quot;&gt;
 *     &lt;external-location path=&quot;sample&quot; /&gt;
 * &lt;/file-locations&gt;
 * </pre>
 * 属性pathには、SDカードからのパスを指定することができる。<br/>
 * <br/>
 * 以下のようにinternal-locationタグを指定することで、端末内のアプリ領域に保存される。
 * <pre>
 * &lt;file-locations xmlns:android=&quot;http://schemas.android.com/apk/res/android&quot;&gt;
 *     &lt;internal-location path=&quot;sample&quot; /&gt;
 * &lt;/file-locations&gt;
 * </pre>
 * 属性pathには、/data/data/{アプリのパッケージ名}/filesからのパスを指定することができる。<br/>
 * <br/>
 * なお、属性pathには絶対パス、相対パスのいずれかを指定できる。
 * 絶対パスの先頭の / は省略可能。つまり、<code>path="/path/to/file"</code> と <code>path="path/to/file"</code>
 * は同じ意味になる。<br />
 * <br/>
 * 
 * 複数のexternal-location や internal-locationタグが記載された場合には、最後のタグが反映される。
 * 
 * @author NTT DOCOMO, INC.
 */
public class FileProvider extends ContentProvider {
    /**
     * ファイルの保存場所.
     */
    private FileLocation mLocation;
    
    /**
     * ファイルを入出力用のメソッド.
     * 
     * @param uri ContentProviderのURL
     * @param mode 書き込み読み込み時のモード
     * @return ファイルを読み書きするためのストリーム
     * @throws FileNotFoundException ファイルが見つからない場合に発生
     */
    @Override
    public ParcelFileDescriptor openFile(final Uri uri, final String mode) throws FileNotFoundException {
        String accessToken = uri.getQueryParameter("");
        if (!checkAccessToken(accessToken)) {
            throw new IllegalArgumentException("accessToken is invalid.");
        }
        File file = new File(getBasePath(), uri.getLastPathSegment());
        ParcelFileDescriptor parcel = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
        return parcel;
    }

    /**
     * ファイルを管理するためのベースとなるパスを取得する.
     * @return パス
     */
    public File getBasePath() {
        if (mLocation == null) {
            mLocation = FileLocationParser.parse(getContext());
        }
        if (mLocation.getType() == FileLocationParser.TYPE_EXTERNAL_PATH) {
            return new File(Environment.getExternalStorageDirectory(), mLocation.getPath());
        } else {
            return new File(getContext().getFilesDir(), mLocation.getPath());
        }
    }

    /**
     * アクセストークンの正当性をチェックする.
     * @param accessToken アクセストークン
     * @return アクセストークンが有効の場合にはtrue、それ以外はfalse
     */
    private boolean checkAccessToken(final String accessToken) {
        // TODO アクセストークンをチェックすること。
        return true;
    }

    @Override
    public int delete(final Uri uri, final String selection, 
            final String[] selectionArgs) {
        return 0;
    }

    @Override
    public String getType(final Uri uri) {
        return null;
    }

    @Override
    public Uri insert(final Uri uri, final ContentValues values) {
        return null;
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(final Uri uri, final String[] projection, 
            final String selection, final String[] selectionArgs, final String sortOrder) {
        return null;
    }

    @Override
    public int update(final Uri uri, final ContentValues values, 
            final String selection, final String[] selectionArgs) {
        return 0;
    }
}
