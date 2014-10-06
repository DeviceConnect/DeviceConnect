//
//  CipherSignatureFactory.h
//  dConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>
#import "CipherSignatureKind.h"
#import "CipherSignatureProc.h"


@interface CipherSignatureFactory : NSObject

+ (CipherSignatureProc *)getInstance: (CipherSignatureKind)signatureKind;

@end
