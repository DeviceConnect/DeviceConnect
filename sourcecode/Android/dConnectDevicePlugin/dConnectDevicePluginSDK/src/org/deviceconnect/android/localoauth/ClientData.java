/*
 ClientData.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.localoauth;

/**
 * クライアントデータ.
 * @author NTT DOCOMO, INC.
 */
public class ClientData {

    /** クライアントID. */
    private String mClientId;

    /** クライアントシークレット. */
    private String mClientSecret;

    /**
     * コンストラクタ.
     * 
     * @param clientId クライアントID
     * @param clientSecret クライアントシークレット
     */
    public ClientData(final String clientId, final String clientSecret) {
        mClientId = clientId;
        mClientSecret = clientSecret;
    }

    /**
     * クライアントID取得.
     * 
     * @return クライアントID
     */
    public String getClientId() {
        return mClientId;
    }

    /**
     * クライアントシークレット取得.
     * 
     * @return クライアントシークレット
     */
    public String getClientSecret() {
        return mClientSecret;
    }
}
