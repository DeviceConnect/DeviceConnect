package org.restlet.ext.oauth.internal;

import org.deviceconnect.android.localoauth.LocalOAuth2Settings;
import org.deviceconnect.android.localoauth.ScopeUtil;

/**
 * スコープ.
 */
public class Scope {

    /**
     * スコープ名.
     */
    private String mScope;
    
    /**
     * トークン更新日時.
     * - 新規作成時およびアクセストークン再承認されたときに更新する。
     * - 1970/1/1 00:00:00 UTC からの経過ミリ秒。System.currentTimeMillis()で取得した値。
     */
    private long mTimestamp;
    
    /** トークン有効期間(sec単位). */
    private long mExpirePeriod = 0;
    
    
    /**
     * コンストラクタ.
     * @param scope スコープ名
     * @param timestamp トークン更新日時(1970/1/1 00:00:00 UTC からの経過ミリ秒。System.currentTimeMillis()で取得した値)
     * @param expirePeriod トークン有効期限（sec単位）
     */
    public Scope(final String scope, final long timestamp, final long expirePeriod) {
        mScope = scope;
        mTimestamp = timestamp;
        mExpirePeriod = expirePeriod;
    }
    
    /**
     * スコープ名取得.
     * @return スコープ名
     */
    public String getScope() {
        return mScope;
        
    }
    
    /**
     * スコープ名設定.
     * @param scope スコープ名
     */
    public void setScope(final String scope) {
        mScope = scope;
    }
    
    /**
     * タイムスタンプ取得.
     * @return タイムスタンプ
     */
    public long getTimestamp() {
        return mTimestamp;
    }
    
    /**
     * タイムスタンプ設定.
     * @param timestamp タイムスタンプ
     */
    public void setTimestamp(final long timestamp) {
        mTimestamp = timestamp;
    }
    
    /**
     * 有効期限取得.
     * @return 有効期限[sec]
     */
    public long getExpirePeriod() {
        return mExpirePeriod;
    }
    
    /**
     * 有効期限設定.
     * @param expirePeriod 有効期限[sec]
     */
    public void setExpirePeriod(final long expirePeriod) {
        mExpirePeriod = expirePeriod;
    }

    /**
     * 有効期限表示文字列を返す.
     * @return 有効期限表示文字列
     */
    public String getStrExpirePeriod() {
        long e = getTimestamp() + getExpirePeriod() * LocalOAuth2Settings.MSEC;
        String displayExpirePeriod = ScopeUtil.getDisplayExpirePeriodDate(e);
        return displayExpirePeriod;
    }
    
    
    /**
     * 有効期限切れか判定.
     * 
     * @return true: 有効期限切れ / false: 有効期限内
     */
    public boolean isExpired() {
        long elapsedTime = System.currentTimeMillis() - mTimestamp;
        long timeout = mExpirePeriod * LocalOAuth2Settings.MSEC;
        if (elapsedTime > timeout) {
            return true;
        }
        return false;
    }

    /**
     * スコープ名の配列を返す.
     * @param scope スコープ配列
     * @return スコープ名のString[]配列(0件ならnullを返す)
     */
    public static String[] toScopeStringArray(final Scope[] scope) {
        if (scope.length > 0) {
            String[] array = new String[scope.length];
            return array;
        }
        return null;
    }

    /**
     * 文字列に展開したScope値を解析してScope型に戻す.
     * @param strScope 文字列に展開したScope値("{スコープ名},{トークン更新日時},{トークン有効期限[sec]}")
     * @return Scope型のオブジェクト。変換失敗ならnullを返す。
     */
    public static Scope parse(final String strScope) {
        String[] div = strScope.split(",");
        if (div.length <= 0) {
            return null;
        }
        
        String scopeName = "";
        long timestamp = System.currentTimeMillis();
        long expirePeriod = LocalOAuth2Settings.DEFAULT_TOKEN_EXPIRE_PERIOD;
        
        if (div.length >= 1) {
            scopeName = div[0];
        }
        if (div.length >= 2) {
            try {
                timestamp = Long.parseLong(div[1]);
            } catch (NumberFormatException e) {
                
            }
        }
        if (div.length >= 3) {
            try {
                expirePeriod = Long.parseLong(div[2]);
            } catch (NumberFormatException e) {
            
            }
        }
        
        Scope scope = new Scope(scopeName, timestamp, expirePeriod);
        return scope;
    }
    
    @Override
    public String toString() {
        String strScope = mScope + "," + mTimestamp + "," + mExpirePeriod;
        return strScope;
    }
    
}
