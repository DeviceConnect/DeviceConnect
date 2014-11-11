/*
 CheckAccessTokenResult.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.localoauth;

/**
 * アクセストークンチェック結果.
 * @author NTT DOCOMO, INC.
 */
public class CheckAccessTokenResult {

    /** true: アクセストークンを発行したクライアントIDあり / false: アクセストークンを発行したクライアントIDなし. */
    private boolean mIsExistClientId;

    /** true: アクセストークンあり / false: アクセストークンなし. */
    private boolean mIsExistAccessToken;

    /** true: スコープあり / false: スコープなし. */
    private boolean mIsExistScope;

    /** true: 有効期限内 / false: 有効期限切れ. */
    private boolean mIsNotExpired;

    /**
     * コンストラクタ.
     * 
     * @param isExistClientId true: アクセストークンを発行したクライアントIDあり / false: アクセストークンを発行したクライアントIDなし
     * @param isExistAccessToken true: アクセストークンあり / false: アクセストークンなし
     * @param isExistScope true: スコープあり / false: スコープなし
     * @param isNotExpired true: 有効期限内 / false: 有効期限切れ
     */
    public CheckAccessTokenResult(final boolean isExistClientId, final boolean isExistAccessToken, 
            final boolean isExistScope, final boolean isNotExpired) {
        mIsExistClientId = isExistClientId;
        mIsExistAccessToken = isExistAccessToken;
        mIsExistScope = isExistScope;
        mIsNotExpired = isNotExpired;
    }


    /**
     * アクセストークンが有効か判定結果を返す.
     * 
     * @return true: アクセストークンは有効 / false: アクセストークンは無効
     */
    public boolean checkResult() {
        boolean result = false;
        
        if (isExistClientId() && isExistAccessToken() && isExistScope() && isNotExpired()) {
            result = true;
        }
        
        return result;
    }

    /**
     * 判定結果(アクセストークンを発行したクライアントIDが存在するか)を返す.
     * 
     * @return true: アクセストークンを発行したクライアントIDあり / false: アクセストークンを発行したクライアントIDなし
     */
    public boolean isExistClientId() {
        return mIsExistClientId;
    }

    /**
     * 判定結果(アクセストークンが存在するか)を返す.
     * 
     * @return true: アクセストークンあり / false: アクセストークンなし
     */
    public boolean isExistAccessToken() {
        return mIsExistAccessToken;
    }

    /**
     * 判定結果(アクセストークンにスコープが登録されているか)を返す.
     * 
     * @return true: スコープあり / false: スコープなし.
     */
    public boolean isExistScope() {
        return mIsExistScope;
    }

    /**
     * 判定結果(アクセストークンの有効期限内か)を返す.
     * 
     * @return true: 有効期限は切れていない / false: 有効期限は切れている.
     */
    public boolean isNotExpired() {
        return mIsNotExpired;
    }
}
