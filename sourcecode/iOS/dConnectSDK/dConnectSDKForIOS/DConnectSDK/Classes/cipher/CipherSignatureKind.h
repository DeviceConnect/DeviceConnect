//
//  CipherSignatureKind.h
//  dConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>

/*!
 * @brief Eventのエラー定義.
 */
typedef NS_ENUM(NSUInteger, CipherSignatureKind) {
    CIPHER_SIGNATURE_KIND_RSA1024,
    CIPHER_SIGNATURE_KIND_MD2,
    CIPHER_SIGNATURE_KIND_MD5,
    CIPHER_SIGNATURE_KIND_SHA,
    CIPHER_SIGNATURE_KIND_SHA256,
    CIPHER_SIGNATURE_KIND_SHA384,
    CIPHER_SIGNATURE_KIND_SHA512,
};

