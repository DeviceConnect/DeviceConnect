/*
 DevicePluginXmlProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.localoauth;

import java.util.HashMap;
import java.util.Map;

/**
 * DevicePlugin.xmlのProfile設定値.
 * @author NTT DOCOMO, INC.
 */
public class DevicePluginXmlProfile {
    
    /** プロファイル. */
    protected String mProfile;
    
    /** 有効期限(秒). */
    protected long mExpirePeriod;
    
    /** ロケール別プロファイル情報. */
    protected Map<String, DevicePluginXmlProfileLocale> mProfileLocales;
    
    
    /**
     * コンストラクタ.
     * @param profile プロファイル名
     * @param expirePeriod 有効期限(秒)
     */
    public DevicePluginXmlProfile(final String profile, final long expirePeriod) {
        mProfile = profile;
        mExpirePeriod = expirePeriod;        
        mProfileLocales = new HashMap<String, DevicePluginXmlProfileLocale>(); 
    }
    
    /**
     * プロファイル名を返す.
     * @return プロファイル名
     */
    public String getProfile() {
        return mProfile;
    }
    
    /**
     * 有効期限(秒)を返す.
     * @return 有効期限(秒)
     */
    public long getExpirePeriod() {
        return mExpirePeriod;
    }

    /**
     * ローカライズされたプロファイル名を設定する.
     * @param lang  ロケール文字列
     * @param name  ローカライズされたプロファイル名
     */
    public void putName(final String lang, final String name) {
        DevicePluginXmlProfileLocale locale = mProfileLocales.get(lang);
        if (locale != null) {
            locale.setName(name);
        } else {
            locale = new DevicePluginXmlProfileLocale(lang);  
            locale.setName(name);
            mProfileLocales.put(lang, locale);
        }
    }

    /**
     * ローカライズされたプロファイル名を設定する.
     * @param lang  ロケール文字列
     * @param description  ローカライズされたDescription
     */
    public void putDescription(final String lang, final String description) {
        DevicePluginXmlProfileLocale locale = mProfileLocales.get(lang);
        if (locale != null) {
            locale.setDescription(description);
        } else {
            locale = new DevicePluginXmlProfileLocale(lang);  
            locale.setDescription(description);
            mProfileLocales.put(lang, locale);
        }
    }

    /**
     * Locales配列を返す.
     * @return Locales配列 
     */
    public Map<String, DevicePluginXmlProfileLocale> getXmlProfileLocales() {
        return mProfileLocales;
    }

    
}
