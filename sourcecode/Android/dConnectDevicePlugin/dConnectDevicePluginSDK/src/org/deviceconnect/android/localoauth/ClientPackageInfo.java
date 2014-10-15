/*
 ClientPackageInfo.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.localoauth;

import org.restlet.ext.oauth.PackageInfoOAuth;

/**
 * クライアントパッケージ情報.
 * @author NTT DOCOMO, INC.
 */
public class ClientPackageInfo {

    /** パッケージ情報. */
    private PackageInfoOAuth mPackageInfo;
    
    /** クライアントID. */
    private String mClientId;
    
    
    /**
     * コンストラクタ.
     * @param packageInfo   パッケージ情報
     * @param clientId      クライアントID
     */
    public ClientPackageInfo(final PackageInfoOAuth packageInfo, final String clientId) {
        mPackageInfo = packageInfo;
        mClientId = clientId;
    }
    
    /**
     * パッケージ情報取得.
     * @return  パッケージ情報
     */
    public PackageInfoOAuth getPackageInfo() {
        return mPackageInfo;
    }
    
    /**
     * クライアントID取得.
     * @return  クライアントID
     */
    public String getClientId() {
        return mClientId;
    }
    
}
