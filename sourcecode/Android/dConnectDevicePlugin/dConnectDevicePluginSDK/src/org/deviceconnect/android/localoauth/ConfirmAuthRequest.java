/*
 ConfirmAuthRequest.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.localoauth;

import java.util.Date;

/**
 * 承認確認画面表示リクエスト.<br>
 * - リクエストを保存しておき、承認／拒否のボタンがタップされた後の処理を行うまで、このクラスにパラメータを保存しておく。
 * @author NTT DOCOMO, INC.
 */
public class ConfirmAuthRequest {

    /** スレッドID. */
    private long mThreadId;
    
    /** 承認確認画面表示パラメータ. */
    private ConfirmAuthParams mConfirmAuthParams;

    /** アクセストークン発行リスナー. */
    private PublishAccessTokenListener mPublishAccessTokenListener;
    
    /** リクエスト時間. */
    private Date mRequestTime;
    
    /** 表示スコープ名配列. */
    private String[] mDisplayScopes;

    /**
     * コンストラクタ.
     * 
     * @param threadId スレッドID
     * @param confirmAuthParams パラメータ
     * @param publishAccessTokenListener アクセストークン発行リスナー
     * @param requestTime 承認確認画面表示要求した日時
     * @param displayScopes 表示用スコープ名配列
     */
    public ConfirmAuthRequest(final long threadId, final ConfirmAuthParams confirmAuthParams,
            final PublishAccessTokenListener publishAccessTokenListener, final Date requestTime,
            final String[] displayScopes) {
        mThreadId = threadId;
        mConfirmAuthParams = confirmAuthParams;
        mPublishAccessTokenListener = publishAccessTokenListener;
        mRequestTime = requestTime;
        mDisplayScopes = displayScopes;
    }

    /**
     * コンストラクタ.
     * 
     * @param threadId スレッドID
     * @param confirmAuthParams パラメータ
     * @param publishAccessTokenListener アクセストークン発行リスナー
     * @param displayScopes 表示用スコープ名配列
     */
    public ConfirmAuthRequest(final long threadId, final ConfirmAuthParams confirmAuthParams,
            final PublishAccessTokenListener publishAccessTokenListener,
            final String[] displayScopes) {
        this(threadId, confirmAuthParams, publishAccessTokenListener, new Date(), displayScopes);
    }

    /**
     * スレッドID取得.
     * @return スレッドID
     */
    public long getThreadId() {
        return mThreadId;
    }
    
    /**
     * 承認確認画面表示パラメータを取得.
     * 
     * @return 承認確認画面表示パラメータ
     */
    public ConfirmAuthParams getConfirmAuthParams() {
        return mConfirmAuthParams;
    }

    /**
     * アクセストークン発行リスナー取得.
     * @return アクセストークン発行リスナー
     */
    public PublishAccessTokenListener getPublishAccessTokenListener() {
        return mPublishAccessTokenListener;
    }

    /**
     * リクエスト時間を取得.
     * 
     * @return リクエスト時間
     */
    public Date getRequestTime() {
        return mRequestTime;
    }
    
    /**
     * 表示用スコープ名配列を取得.
     * @return 表示用スコープ名配列
     */
    public String[] getDisplayScopes() {
        return mDisplayScopes;
    }
}
