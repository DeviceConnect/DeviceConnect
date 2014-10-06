//
//  CipherSignatureFactory.m
//  dConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "CipherSignatureFactory.h"
#import "CipherSignatureProcMD.h"

@implementation CipherSignatureFactory

/**
 * 暗号処理インスタンスを生成する.
 * @param signatureKind 生成する暗号処理の種類
 * @return 暗号処理インスタンス
 */
+ (CipherSignatureProc *)getInstance: (CipherSignatureKind)signatureKind {
    if ([CipherSignatureProcMD isSupport: signatureKind]) {
        CipherSignatureProcMD *signatureProc = [CipherSignatureProcMD allocWithSignatureKind: signatureKind];
        return signatureProc;
    } else {
        @throw @"signatureKind is not support.";
    }
}


@end
