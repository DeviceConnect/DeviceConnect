/*
 PublishAccessTokenListener.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.localoauth;

/**
 * アクセストークン発行リスナー.
 * @author NTT DOCOMO, INC.
 */
public interface PublishAccessTokenListener {

    /**
     * アクセストークン受信リスナー.
     * 
     * @param accessTokenData not null: 許可された。発行されたアクセストークンデータを返す。 / 
     *                        null: 拒否された。アクセストークンデータは無い。
     */
    void onReceiveAccessToken(final AccessTokenData accessTokenData);

    
    /**
     * 例外受信リスナー.
     * @param exception 例外
     */
    void onReceiveException(final Exception exception);
    
}
