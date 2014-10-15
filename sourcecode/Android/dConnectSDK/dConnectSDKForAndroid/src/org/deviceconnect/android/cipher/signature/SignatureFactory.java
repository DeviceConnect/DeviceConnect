/*
 SignatureFactory.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.cipher.signature;

import org.deviceconnect.android.cipher.signature.AuthSignature.SignatureKind;

/**
 * 暗号処理インスタンスを生成するファクトリークラス.
 * @author NTT DOCOMO, INC.
 */
public final class SignatureFactory {

    /**
     * コンストラクタ.
     */
    private SignatureFactory() {
        
    }
    
    /**
     * 暗号処理インスタンスを生成する.
     * @param signatureKind 生成する暗号処理の種類
     * @return 暗号処理インスタンス
     */
    public static SignatureProc getInstance(final SignatureKind signatureKind) {
        if (SignatureProcMD.isSupport(signatureKind)) {
            return new SignatureProcMD(signatureKind);
        } else {
            throw new IllegalArgumentException("signatureKind is not support.");
        }
    }
}
