//
//  SignatureProc.m
//  dConnectCipher
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "CipherSignatureProc.h"

@implementation CipherSignatureProc

/**
 * 公開鍵を設定する.
 * ※RSAなど公開鍵が必要な場合は設定する。
 * @param cipherPublicKey 公開鍵
 */
- (void)setCipherPublicKey: (NSString *)cipherPublicKey {
    @throw @"Please Override.";
}

/**
 * 文字列を暗号化してSignatureを生成する.
 *
 * @param string 暗号化する文字列
 * @return Signature
 */
- (NSString *)generateSignature: (NSString *) string {
    @throw @"Please Override.";
}


@end
