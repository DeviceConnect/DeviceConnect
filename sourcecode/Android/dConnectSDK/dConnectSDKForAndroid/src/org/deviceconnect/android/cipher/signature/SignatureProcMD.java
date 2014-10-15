/*
 SignatureProcMD.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.cipher.signature;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.deviceconnect.android.cipher.signature.AuthSignature.SignatureKind;


/**
 * MD*, SHA* を使った暗号化処理.
 * @author NTT DOCOMO, INC.
 */
public class SignatureProcMD implements SignatureProc {

    /** アルゴリズム名. */
    private String mAlgorithmName = null;
    
    /**
     * コンストラクタ.
     * @param signatureKind 暗号種類
     */
    public SignatureProcMD(final SignatureKind signatureKind) {
        
        /* アルゴリズム名取得 */
        mAlgorithmName = signatureKind2AlgorithmName(signatureKind);
        if (mAlgorithmName == null) {
            throw new IllegalArgumentException("signatureKind is not support.");
        }
    }

    /**
     * 指定された暗号種類をサポートするか判定.
     * @param signatureKind 暗号種類
     * @return true: サポートする / false: サポートしない
     */
    public static boolean isSupport(final AuthSignature.SignatureKind signatureKind) {
        boolean result = false;
        
        /* アルゴリズム名取得 */
        String algorithmName = signatureKind2AlgorithmName(signatureKind);
        if (algorithmName != null) {
            result = true;
        }
        
        return result;
    }
    
    /**
     * アルゴリズム名取得.
     * @param signatureKind 暗号種別
     * @return not null: アルゴリズム名 / null: 該当無し
     */
    private static String signatureKind2AlgorithmName(final AuthSignature.SignatureKind signatureKind) {
        String algorithmName = null;
        /* メッセージダイジェストアルゴリズム(MD2, MD5, SHA, SHA-256, SHA-384, SHA-512が利用可能) */
        if (signatureKind == SignatureKind.MD2) {
            algorithmName = "MD2";
        } else if (signatureKind == SignatureKind.MD5) {
            algorithmName = "MD5";
        } else if (signatureKind == SignatureKind.SHA) {
            algorithmName = "SHA";
        } else if (signatureKind == SignatureKind.SHA256) {
            algorithmName = "SHA-256";
        } else if (signatureKind == SignatureKind.SHA384) {
            algorithmName = "SHA-384";
        } else if (signatureKind == SignatureKind.SHA512) {
            algorithmName = "SHA-512";
        }
        return algorithmName;
    }
    
    /**
     * 暗号用公開鍵設定(使用しないのでnullを返す).
     * @param cipherPublicKey 公開鍵
     */
    @Override
    public void setCipherPublicKey(final String cipherPublicKey) {
        throw new RuntimeException("setCipherPublicKey() can't use.");
    }

    /**
     * 文字列を暗号化してSignatureを生成する.
     * 
     * @param string 暗号化する文字列
     * @return Signature
     */
    public String generateSignature(final String string) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance(mAlgorithmName);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        md.update(string.getBytes());
        byte[] b = md.digest();
        String result = byte2String(b);
        return result;
    }
    
    /**
     * byte配列をstringに変換.
     * @param b byte配列
     * @return string
     */
    public static String byte2String(final byte[] b) {

        // ハッシュを16進数文字列に変換
        StringBuffer sb = new StringBuffer();
        int cnt = b.length;
        for (int i = 0; i < cnt; i++) {
            sb.append(Integer.toHexString((b[i] >> 4) & 0x0F));
            sb.append(Integer.toHexString(b[i] & 0x0F));
        }
        return sb.toString();
    }
}
