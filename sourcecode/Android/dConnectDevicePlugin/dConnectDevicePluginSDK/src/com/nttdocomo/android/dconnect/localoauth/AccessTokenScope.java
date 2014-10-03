/*
 AccessTokenScope.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.localoauth;

/**
 * アクセストークン有効期限データ.
 * @author NTT DOCOMO, INC.
 */
public class AccessTokenScope {
    
    /** スコープ名. */
    private String mScope;
    
    /** 有効期限[秒]. */
    private long mExpirePeriod;
    
    /**
     * コンストラクタ.
     * @param scope スコープ名
     * @param expirePeriod 有効期限[秒]
     */
    public AccessTokenScope(final String scope, final long expirePeriod) {
        mScope = scope;
        mExpirePeriod = expirePeriod;
    }
    
    /**
     * スコープ名を返す.
     * @return スコープ名
     */
    public String getScope() {
        return mScope;
    }
    
    /**
     * 有効期限[秒]を返す.
     * @return 有効期限[秒]
     */
    public long getExpirePeriod() {
        return mExpirePeriod;
    }
}
