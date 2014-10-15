/*
 DevicePluginXmlProfileLocale.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.localoauth;

/**
 * DevicePlugin.xmlのプロファイルロケール情報.
 * @author NTT DOCOMO, INC.
 */
public class DevicePluginXmlProfileLocale {

    /** ロケール文字列("ja","en"). */
    protected String mLocale;
    
    /** ローカライズされたプロファイル名. */
    protected String mName;
    
    /** ローカライズされたDescription. */
    protected String mDescription;

    
    /**
     * コンストラクタ(ロケール文字列指定).
     * @param locale    ロケール文字列("ja","en")
     */
    public DevicePluginXmlProfileLocale(final String locale) {
        mLocale = locale;
    }

    /**
     * ロケール文字列を返す.
     * @return locale ロケール文字列
     */
    public String getLocale() {
        return mLocale;
    }
    
    /**
     * ロケール文字列を設定.
     * @param locale ロケール文字列
     */
    public void setLocale(final String locale) {
        mLocale = locale;
    }
    
    /**
     * ローカライズされたプロファイル名を返す.
     * @return ローカライズされたプロファイル名
     */
    public String getName() {
        return mName;
    }
    
    /**
     * ローカライズされたプロファイル名を設定.
     * @param name ローカライズされたプロファイル名
     */
    public void setName(final String name) {
        mName = name;
    }
    
    /**
     * ローカライズされたDescriptionを設定.
     * @return ローカライズされたDescription
     */
    public String getDescription() {
        return mDescription;
    }
    
    /**
     * ローカライズされたDescriptionを設定.
     * @param description ローカライズされたDescription
     */
    public void setDescription(final String description) {
        mDescription = description;
    }

}
