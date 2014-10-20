/*
 FileManager.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.provider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import org.deviceconnect.android.provider.FileLocationParser.FileLocation;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ProviderInfo;
import android.net.Uri;
import android.os.Environment;

/**
 * ファイルを管理するためのクラス.
 * 
 * @author NTT DOCOMO, INC.
 */
public class FileManager {
    /** ロガー. */
    private final Logger mLogger = Logger.getLogger("org.deviceconnect.dplugin");

    /** バッファサイズを定義. */
    private static final int BUF_SIZE = 8192;

    /** ファイルが生存できる有効時間を定義する. */
    private static final long DEFAULT_EXPIRE = 1000 * 60 * 5;

    /** ファイルが生存できる有効時間. */
    private long mExpire = DEFAULT_EXPIRE;

    /** コンテキスト. */
    private Context mContext;

    /** authority. */
    private String mAuthority;
    /**
     * ファイルの保存場所.
     */
    private FileLocation mLocation;

    /**
     * コンストラクタ.
     * 
     * @param context コンテキスト
     */
    public FileManager(final Context context) {
        mContext = context;
        File dir = getBasePath();
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                mLogger.warning("Cannot create a folder.");
            }
        }

        final String className = FileProvider.class.getName();
        PackageManager pkgMgr = context.getPackageManager();
        try {
            PackageInfo packageInfo = pkgMgr.getPackageInfo(context.getPackageName(), PackageManager.GET_PROVIDERS);
            ProviderInfo[] providers = packageInfo.providers;
            if (providers != null) {
                for (ProviderInfo provider : providers) {
                    if (className.equals(provider.name)) {
                        mAuthority = provider.authority;
                    }
                }
            }
            if (mAuthority == null) {
                throw new RuntimeException("Cannot found provider.");
            }
        } catch (NameNotFoundException e) {
            throw new RuntimeException("Cannot found provider.");
        }
    }

    /**
     * ファイルを管理するためのベースとなるパスを取得する.
     * 
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
     * デバイスプラグインのファイルコンテンツへのURIを取得する.
     * 
     * @return Content URI
     */
    public String getContentUri() {
        return "content://" + mAuthority;
    }

    /**
     * コンテキストを取得する.
     * 
     * @return コンテキスト
     */
    public final Context getContext() {
        return mContext;
    }

    /**
     * ファイルを保存して、アクセスするためのContentURIを返却する.
     * 
     * ここで、保存すると返り値にURIが返ってくる。 このURIをFile Profileのuriの値としてDevice Connect Managerに
     * 渡す事で、ファイルのやり取りができるようになる。
     * 
     * TODO 既に同じ名前のファイルが存在する場合の処理を考慮すること。
     * 
     * @param filename ファイル名
     * @param data ファイルデータ
     * @return 保存したファイルへのURI
     * 
     * @throws IOException ファイルの保存に失敗した場合に発生
     */
    public final String saveFile(final String filename, final byte[] data) throws IOException {
        File tmpPath = getBasePath();
        if (!tmpPath.exists()) {
            if (!tmpPath.mkdirs()) {
                throw new IOException("Cannot create a folder.");
            }
        }
        Uri u = Uri.parse("file://" + new File(tmpPath, filename).getAbsolutePath());
        ContentResolver contentResolver = mContext.getContentResolver();
        OutputStream out = null;
        try {
            out = contentResolver.openOutputStream(u, "w");
            out.write(data);
            out.flush();
        } catch (Exception e) {
            throw new IOException("Failed to save a file." + filename);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        String contentUri = getContentUri();
        if (contentUri == null) {
            throw new RuntimeException("Content URI is null.");
        } else if (!contentUri.endsWith("/")) {
            contentUri = contentUri + "/";
        }
        return contentUri + u.getLastPathSegment();
    }

    /**
     * ファイルを保存する.
     * 
     * ここで、保存すると返り値にURIが返ってくる。 このURIをFile Profileのuriの値としてDevice Connect Managerに
     * 渡す事で、ファイルのやり取りができるようになる。
     * 
     * @param filename ファイル名
     * @param in ストリーム
     * 
     * @return 保存したファイルへのURI
     * 
     * @throws IOException ファイルの保存に失敗した場合に発生
     */
    public final String saveFile(final String filename, final InputStream in) throws IOException {
        File tmpPath = getBasePath();
        if (!tmpPath.exists()) {
            if (!tmpPath.mkdirs()) {
                throw new IOException("Cannot create a folder.");
            }
        }
        Uri u = Uri.parse("file://" + new File(tmpPath, filename).getAbsolutePath());
        ContentResolver contentResolver = mContext.getContentResolver();
        OutputStream out = null;
        try {
            out = contentResolver.openOutputStream(u, "w");
            int len;
            byte[] data = new byte[BUF_SIZE];
            while ((len = in.read(data)) > 0) {
                out.write(data, 0, len);
            }
        } catch (Exception e) {
            throw new IOException("Failed to save a file." + filename);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        String contentUri = getContentUri();
        if (contentUri == null) {
            throw new RuntimeException("Content URI is null.");
        } else if (!contentUri.endsWith("/")) {
            contentUri = contentUri + "/";
        }
        return contentUri + u.getLastPathSegment();
    }

    /**
     * 有効期限の期間を指定する.
     * 
     * デフォルトでは、300000(5分)が設定してある。 あまり長い時間を指定するとキャッシュが消えないので注意が必要。
     * 
     * @param expire 有効期限(ミリ秒)
     */
    public final void setExpire(final long expire) {
        if (expire <= 0) {
            throw new IllegalArgumentException("expire is negative.");
        }
        mExpire = expire;
    }

    /**
     * 指定された名前のファイルを削除する.
     * 
     * @param name 削除するファイル名
     * @return ファイルの削除に成功した場合はtrue、それ以外はfalse
     */
    public boolean removeFile(final String name) {
        File file = new File(getBasePath(), name);
        if (file.isDirectory()) {
            return false;
        } else if (file.isFile()) {
            Boolean result = file.delete();
            return result;
        } else {
            return false;
        }
    }

    /**
     * デフォルトのフォルダをチェックして、中身を削除する.
     * @return 削除に成功した場合はtrue、失敗した場合はfalse
     */
    public boolean checkAndRemove() {
        return checkAndRemove(getBasePath());
    }

    /**
     * デフォルトのフォルダまたはファイルをチェックして、中身を削除する.
     * @param name フォルダ名
     * @return 削除に成功した場合はtrue、失敗した場合はfalse
     */
    public boolean checkAndRemove(final String name) {
        return checkAndRemove(new File(getBasePath(), name));
    }

    /**
     * ファイルをチェックして、中身を削除する.
     * @param file 削除するファイル
     * @return 削除に成功した場合はtrue、失敗した場合はfalse
     */
    public boolean checkAndRemove(final File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                checkAndRemove(files[i]);
            }
            return true;
        } else if (file.isFile()) {
            long modified = file.lastModified();
            if (System.currentTimeMillis() - modified > mExpire) {
                Boolean result = file.delete();
                return result;
            }
        }
        return false;
    }
}
