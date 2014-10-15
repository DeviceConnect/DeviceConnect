/*
 ScopeUtil.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.localoauth;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.restlet.ext.oauth.internal.Scope;

import android.content.Context;

/**
 * スコープ用Util.
 *
 * @author NTT DOCOMO, INC.
 */
public final class ScopeUtil {

    /**
     * コンストラクタ.
     */
    private ScopeUtil() {
        
    }

    
    /**
     * String[]をScope[]に変換する.
     * @param strScopes String[]の配列
     * @return Scope[]の配列
     */
    public static Scope[] stringToScope(final String[] strScopes) {
        Scope[] scopes = new Scope[strScopes.length];
        for (int i = 0; i < strScopes.length; i++) {
            scopes[i] = Scope.parse(strScopes[i]);
        }
        return scopes;
    }

    /**
     * Scope[]をString[]に変換する.
     * @param scopes Scope[]の配列
     * @return String[]の配列
     */
    public static String[] scopesToStrings(final Scope[] scopes) {
        String[] strScopes = new String[scopes.length];
        for (int i = 0; i < scopes.length; i++) {
            strScopes[i] = scopes[i].toString();
        }
        return strScopes;
    }
    
    /**
     * ArrayList<Scope>をArrayList<String>に変換する.
     * @param scopes ArrayList<Scope>の配列
     * @return ArrayList<String>の配列
     */
    public static ArrayList<String> scopesToStrings(final ArrayList<Scope> scopes) {
        
        ArrayList<String> strScopes = new ArrayList<String>();
        int c = scopes.size();
        for (int i = 0; i < c; i++) {
            strScopes.add(scopes.get(i).toString());
        }
        return strScopes;
    }

    /**
     * Scope[]をスコープ名配列(String[])に変換する.
     * @param scopes Scope[]の配列
     * @return スコープ名配列(String[])
     */
    public static String[] scopeToScopeNames(final Scope[] scopes) {
        String[] strScopeNames = new String[scopes.length];
        for (int i = 0; i < scopes.length; i++) {
            strScopeNames[i] = scopes[i].getScope();
        }
        return strScopeNames;
    }

    /**
     * スコープ名表示文字列を取得する(日本語表示できる場合は日本語名に変換して返す).
     * @param context コンテキスト
     * @param scope スコープ名
     * @param locale ロケール("ja", "en"等。アンダーバーがついている場合("ja_JP"等)は、アンダーバーから後の文字列は削除する)
     * @param supportProfiles deviceplugin.xmlから取得したサポートプロファイル情報
     * @return 表示用スコープ名(ローカライズできる場合はローカライズして返す)
     */
    public static String getDisplayScope(final Context context, final String scope,
            final String locale, final Map<String, DevicePluginXmlProfile> supportProfiles) {
        final String displayScopeKey = "display_scope_";
        String displayScope = null;
        
        /* 標準プロファイル名の場合 */
        int strId = context.getResources().getIdentifier(displayScopeKey + scope, "string", context.getPackageName());
        if (strId != 0) {
            displayScope = context.getString(strId);
        }
        
        /* deviceplugin.xmlから表示プロファイル名を取得する場合 */
        if (displayScope == null && locale != null && supportProfiles != null) {
            DevicePluginXmlProfile xmlProfile = supportProfiles.get(scope);
            if (xmlProfile != null) {
                Map<String, DevicePluginXmlProfileLocale> locales = xmlProfile.getXmlProfileLocales();
                if (locales != null) {
                    DevicePluginXmlProfileLocale xmlLocale = locales.get(locale);
                    if (xmlLocale != null) {
                        displayScope = xmlLocale.getName(); 
                    }
                }
            }
        }
        
        /* 該当なければそのまま表示する */
        if (displayScope == null) {
            displayScope = scope;
        }
        
        return displayScope;
    }


    /**
     * 有効期限日表示文字列を返す.
     * @param expirePeriodDateMSec 有効期限[msec] 
     * @return 有効期限日表示文字列
     */
    public static String getDisplayExpirePeriodDate(final long expirePeriodDateMSec) {
        Calendar c = Calendar.getInstance(); 
        c.setTimeInMillis(expirePeriodDateMSec);
        Date d = c.getTime();
        String s = DateFormat.getDateInstance(DateFormat.SHORT).format(d);
        return s;
    }
}
