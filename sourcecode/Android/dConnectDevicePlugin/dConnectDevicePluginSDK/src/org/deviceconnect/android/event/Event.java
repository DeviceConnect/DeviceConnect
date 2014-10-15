/*
 Event.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.event;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * イベントデータクラス.
 * 
 *
 * @author NTT DOCOMO, INC.
 */
public class Event implements Serializable {

    /** 
     * シリアルバージョン.
     */
    private static final long serialVersionUID = 2249802839155396087L;
    
    /** 
     * プロファイル.
     */
    private String mProfile;
    
    /** 
     * インターフェース.
     */
    private String mInterface;
    
    /** 
     * 属性.
     */
    private String mAttribute;
    
    /**
     * デバイスID.
     */
    private String mDeviceId;
    
    /**
     * アクセストークン.
     */
    private String mAccessToken;
    
    /** 
     * セッションキー.
     */
    private String mSessionKey;
    
    /**
     * レシーバーのパッケージ名.
     */
    private String mReceiverName;
    
    /** 
     * 登録日.
     */
    private Timestamp mCreateDate;
    
    /** 
     * 更新日.
     */
    private Timestamp mUpdateDate;

    /**
     * プロファイルを取得する.
     * 
     * @return プロファイル名
     */
    public String getProfile() {
        return mProfile;
    }

    /**
     * プロファイルを設定する.
     * 
     * @param profile プロファイル
     */
    public void setProfile(final String profile) {
        this.mProfile = profile;
    }

    /**
     * 属性を取得する.
     * 
     * @return 属性名
     */
    public String getAttribute() {
        return mAttribute;
    }

    /**
     * 属性を設定する.
     * 
     * @param attribute 属性.
     */
    public void setAttribute(final String attribute) {
        this.mAttribute = attribute;
    }

    /**
     * デバイスIDを取得する.
     * 
     * @return デバイスID
     */
    public String getDeviceId() {
        return mDeviceId;
    }

    /**
     * デバイスIDを設定する.
     * 
     * @param deviceId デバイスID
     */
    public void setDeviceId(final String deviceId) {
        this.mDeviceId = deviceId;
    }

    /**
     * アクセストークンを取得する.
     * 
     * @return アクセストークン
     */
    public String getAccessToken() {
        return mAccessToken;
    }

    /**
     * アクセストークンを設定する.
     * 
     * 
     * @param accessToken アクセストークン
     */
    public void setAccessToken(final String accessToken) {
        this.mAccessToken = accessToken;
    }

    /**
     * インターフェースを取得する.
     * 
     * @return インターフェース
     */
    public String getInterface() {
        return mInterface;
    }

    /**
     * インターフェースを設定する.
     * 
     * @param inter インターフェース
     */
    public void setInterface(final String inter) {
        this.mInterface = inter;
    }
    
    /**
     * セッションキーを取得する.
     * 
     * @return セッションキー
     */
    public String getSessionKey() {
        return mSessionKey;
    }
    
    /**
     * セッションキーを設定する.
     * 
     * @param sessionKey セッションキー
     */
    public void setSessionKey(final String sessionKey) {
        this.mSessionKey = sessionKey;
    }
    
    /**
     * レシーバーのパッケージ名を返す.
     * @return レシーバーのパッケージ名
     */
    public String getReceiverName() {
        return mReceiverName;
    }
    
    /**
     * レシーバーのパッケージ名を設定します.
     * 
     * @param receiverName レシーバーのパッケージ名
     */
    public void setReceiverName(final String receiverName) {
        mReceiverName = receiverName;
    }
    
    /**
     * 登録日を取得する.
     * 
     * @return 登録日
     */
    public Timestamp getCreateDate() {
        return mCreateDate;
    }
    
    /**
     * 登録日を設定する.
     * 
     * @param createDate 登録日
     */
    public void setCreateDate(final Timestamp createDate) {
        mCreateDate = createDate;
    }
    
    /**
     * 更新日を取得する.
     * 
     * @return 登録日
     */
    public Timestamp getUpdateDate() {
        return mUpdateDate;
    }
    
    /**
     * 更新日を設定する.
     * 
     * @param updateDate 更新日
     */
    public void setUpdateDate(final Timestamp updateDate) {
        mUpdateDate = updateDate;
    }
    
    @Override
    public String toString() {
        
        StringBuilder to = new StringBuilder();
        to.append("[profile = ");
        to.append(mProfile);
        to.append(", interface = ");
        to.append(mInterface);
        to.append(", attribute = ");
        to.append(mAttribute);
        to.append(", deviceId = ");
        to.append(mDeviceId);
        to.append(", sessionKey = ");
        to.append(mSessionKey);
        to.append(", receiverName = ");
        to.append(mReceiverName);
        to.append(", accessToken = ");
        to.append(mAccessToken);
        to.append(", createDate = ");
        if (mCreateDate != null) {
            to.append(mCreateDate.toString());
        } else {
            to.append("null");
        }
        to.append(", updateDate = ");
        if (mUpdateDate != null) {
            to.append(mUpdateDate.toString());
        } else {
            to.append("null");
        }
        to.append("]");
        return to.toString();
    }
}
