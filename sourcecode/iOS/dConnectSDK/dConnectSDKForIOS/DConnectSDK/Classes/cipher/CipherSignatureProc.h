//
//  CipherSignatureProc.h
//  dConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>

@interface CipherSignatureProc : NSObject

- (void)setCipherPublicKey: (NSString *)cipherPublicKey;
- (NSString *)generateSignature: (NSString *) string;

@end
