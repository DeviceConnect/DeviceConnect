/*
 AuthSignature.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.cipher.signature;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * Signatureクラス.
 * 
 * <pre>
 * (例)
 * String signature = AuthSignature.generateSignature("clientId", "grantType", 
 *                          "deviceId", new String[] {"scope1", "scope2"}, clientSecret);
 * </pre>
 * @author NTT DOCOMO, INC.
 */
public final class AuthSignature {

    /**
     * 暗号処理の種類.
     */
    public enum SignatureKind {
        /** 暗号処理の種類. */
        RSA1024, 
        /** 暗号処理の種類. */
        MD2, 
        /** 暗号処理の種類. */
        MD5, 
        /** 暗号処理の種類. */
        SHA, 
        /** 暗号処理の種類. */
        SHA256, 
        /** 暗号処理の種類. */
        SHA384, 
        /** 暗号処理の種類. */
        SHA512,
    };
    
    /**
     * 入力文字列.
     */
    private Map<String, String> mInputMaps = new HashMap<String, String>();

    /**
     * Signature生成処理.
     */
    private SignatureProc mSignatureProc = null;
    
    /**
     * コンストラクタ.
     * @param signatureKind 暗号化処理の種類
     */
    public AuthSignature(final SignatureKind signatureKind) {
        mSignatureProc = SignatureFactory.getInstance(signatureKind);
    }
    
    /**
     * 公開鍵設定.
     * @param cipherPublicKey 公開鍵設定
     */
    public void setCipherPublicKey(final String cipherPublicKey) {
        mSignatureProc.setCipherPublicKey(cipherPublicKey);
    }
    

    /**
     * 連結文字列バッファをクリアする.
     */
    private void empty() {
        mInputMaps = new HashMap<String, String>();
    }
    
    /**
     * 連結する文字列を追加する.
     * 
     * @param key キー
     * @param value バリュー
     */
    private void put(final String key, final String value) {
        mInputMaps.put(key, value);
    }
    
    /**
     * Signatureを生成して返す.
     * @param clientSecret クライアントシークレット
     * @return Signature
     */
    private String generateSignature(final String clientSecret) {
        
        Object[] keys = mInputMaps.keySet().toArray();
        Arrays.sort(keys);
        
        String str = "";
        for (int i = 0; i < keys.length; i++) {
            str += keys[i];
            str += mInputMaps.get(keys[i]);
        }
        
        str += "clientSecret";
        str += clientSecret;
        
        String signature = mSignatureProc.generateSignature(str);
        return signature;
    }
    
    /**
     * Signature生成(アクセストークン発行リクエストを送信するときに添付するSignatureを生成する).
     * 
     * @param clientId クライアントID
     * @param grantType グラントタイプ
     * @param deviceId デバイスID(UIアプリのときはnullまたは""を指定する)
     * @param scopes スコープ
     * @param clientSecret クライアントシークレット
     * @return Signature
     */
    public static String generateSignature(final String clientId,
            final String grantType, final String deviceId, final String[] scopes, final String clientSecret) {

        AuthSignature authSignature = new AuthSignature(AuthSignature.SignatureKind.SHA512);
        
        authSignature.empty();
        authSignature.put("clientId", clientId);
        authSignature.put("grantType", grantType);
        if (deviceId != null && deviceId.length() > 0) {
            authSignature.put("deviceId", deviceId);
        }
        
        String strScopes = "";
        Arrays.sort(scopes);
        for (int i = 0; i < scopes.length; i++) {
            String scope = scopes[i];
            if (i > 0) {
                strScopes += ",";
            }
            strScopes += scope;
        }
        authSignature.put("scopes", strScopes);
        
        String signature = authSignature.generateSignature(clientSecret);
        return signature;
    }

    /**
     * アクセストークン発行のレスポンスを返信するときに添付するSignatureを生成する.
     * 
     * @param accessToken アクセストークン
     * @param clientSecret クライアントシークレット
     * @return Signature
     */
    public static String generateSignature(final String accessToken, final String clientSecret) {
        AuthSignature authSignature = new AuthSignature(AuthSignature.SignatureKind.SHA512);
        authSignature.empty();
        authSignature.put("accessToken", accessToken);
        String signature = authSignature.generateSignature(clientSecret);
        return signature;
    }
    
}
