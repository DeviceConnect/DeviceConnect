/*
 LocalOAuth2Settings.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.localoauth;

/**
 * LocalOAuthの設定値を管理する.
 * @author NTT DOCOMO, INC.
 */
public final class LocalOAuth2Settings {
    
    /** 1秒あたりのミリ秒数. */
    public static final int MSEC = 1000;
    
    /** 1分あたりの秒数. */
    public static final int MINUTE = 60;
    
    /** 1時間あたりの秒数. */
    public static final int HOUR = 60 * MINUTE;
    
    /** 1日あたりの秒数. */
    public static final int DAY = 24 * HOUR;
    
    /** 長時間使用されなかったクライアントをクリーンアップするまでの時間[sec]. */
    public static final int CLIENT_CLEANUP_TIME = 30 * DAY;
    
    /** 無効な状態で残っているトークン(発行したクライアントIDがすでに破棄されている)をクリーンアップするまでの時間[sec]. */
    public static final int TOKEN_CLEANUP_TIME = 10 * DAY;
    
    
    /** クライアント数の上限.  */
    public static final int CLIENT_MAX = 100;
    
    /** スコープ毎のアクセストークンの有効期限デフォルト値[sec]. */
    public static final long DEFAULT_TOKEN_EXPIRE_PERIOD = 180 * DAY;  // 180日間[sec]
    
    /** 有効期限0が設定されたときに、初回アクセスを「有効期限内」として返す猶予時間[秒]. */
    public static final long ACCESS_TOKEN_GRACE_TIME = 1 * MINUTE; /* 1分[秒] */
    
    
    
    /**
     * コンストラクタ.
     */
    private LocalOAuth2Settings() {
        
    }
    
    
}
