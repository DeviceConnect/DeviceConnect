/*
 IRKitDevice.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.irkit;

/**
 * IRKit端末の情報を持つデータクラス.
 * @author NTT DOCOMO, INC.
 */
public class IRKitDevice {

    /**
     * デバイス名.
     */
    private String mName;

    /**
     * デバイスのIPアドレス. IPv4もしくはIPv6のいずれか。
     */
    private String mIp;

    /**
     * デバイス名を取得する.
     * 
     * @return デバイス名
     */
    public String getName() {
        return mName;
    }

    /**
     * デバイス名を設定する.
     * 
     * @param name デバイス名
     */
    public void setName(final String name) {
        mName = name;
    }

    /**
     * IPアドレスを取得する. IPv4もしくはIPv6のいずれか。
     * 
     * @return IPアドレス
     */
    public String getIp() {
        return mIp;
    }

    /**
     * PIアドレスを設定する.
     * 
     * @param ip IPアドレス
     */
    public void setIp(final String ip) {
        mIp = ip;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        if (mName != null) {
            builder.append("Name : " + mName);
        } else {
            builder.append("Name : null");
        }
        
        builder.append(", ");
        
        if (mIp != null) {
            builder.append("IP : " + mIp);
        } else {
            builder.append("IP : null");
        }
        builder.append("]");
        return builder.toString();
    }
}
