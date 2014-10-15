/*
 SignatureProc.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.cipher.signature;

/**
 * Signature生成処理インタフェース.
 * @author NTT DOCOMO, INC.
 */
public interface SignatureProc {
    
    /**
     * 公開鍵を設定する.
     * @param cipherPublicKey 公開鍵
     */
    void setCipherPublicKey(final String cipherPublicKey);
    
    /**
     * 文字列を暗号化してSignatureを生成する.
     * 
     * @param string 暗号化する文字列
     * @return Signature
     */
    String generateSignature(final String string);
    
}
