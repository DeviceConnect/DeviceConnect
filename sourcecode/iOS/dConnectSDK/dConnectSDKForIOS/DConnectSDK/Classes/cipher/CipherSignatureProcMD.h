//
//  CipherSignatureProcMD.h
//  dConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "CipherSignatureProc.h"
#import "CipherSignatureFactory.h"

@interface CipherSignatureProcMD : CipherSignatureProc

/** アルゴリズム種別. */
@property CipherSignatureKind mSignatureKind;


+ (BOOL)isSupport: (CipherSignatureKind) signatureKind;
+ (CipherSignatureProcMD *)allocWithSignatureKind: (CipherSignatureKind) signatureKind;

- (void)setCipherPublicKey: (NSString *)cipherPublicKey;
- (NSString *)generateSignature: (NSString *) string;

@end
