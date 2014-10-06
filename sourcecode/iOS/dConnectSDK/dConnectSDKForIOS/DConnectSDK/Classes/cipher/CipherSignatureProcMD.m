//
//  CipherSignatureProcMD.m
//  dConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "CipherSignatureProcMD.h"
#import <CommonCrypto/CommonDigest.h>

@implementation CipherSignatureProcMD


// クラスメソッドの実装、初期化処理
+(id)init {
    id _id = [[self alloc] init];
    
    return _id;
}

/**
 * コンストラクタ.
 * @param signatureKind 暗号種類
 */
+ (CipherSignatureProcMD *)allocWithSignatureKind: (CipherSignatureKind) signatureKind {
    
    CipherSignatureProcMD *signatureProcMD = [self init];
    
    /* アルゴリズム名取得 */
    NSString *algorithmName = [CipherSignatureProcMD signatureKind2AlgorithmName: signatureKind];
    if (algorithmName == nil) {
        @throw @"signatureKind is not support.";
    } else {
        signatureProcMD.mSignatureKind = signatureKind;
    }
    
    return signatureProcMD;
}

/**
 * 指定された暗号種類をサポートするか判定.
 * @param signatureKind 暗号種類
 * @return true: サポートする / false: サポートしない
 */
+ (BOOL)isSupport: (CipherSignatureKind) signatureKind {
    BOOL result = false;
    
    /* アルゴリズム名取得 */
    NSString *algorithmName = [self signatureKind2AlgorithmName: signatureKind];
    if (algorithmName != nil) {
        result = true;
    }
    
    return result;
}


/**
 * アルゴリズム名取得.
 * @param signatureKind 暗号種別
 * @return not null: アルゴリズム名 / null: 該当無し
 */
+ (NSString *) signatureKind2AlgorithmName: (CipherSignatureKind) signatureKind {
    NSString *algorithmName = nil;
    
    /* メッセージダイジェストアルゴリズム(MD2, MD5, SHA, SHA-256, SHA-384, SHA-512が利用可能) */
    if (signatureKind == CIPHER_SIGNATURE_KIND_MD2) {
        algorithmName = @"MD2";
    } else if (signatureKind == CIPHER_SIGNATURE_KIND_MD5) {
        algorithmName = @"MD5";
    } else if (signatureKind == CIPHER_SIGNATURE_KIND_SHA) {
        algorithmName = @"SHA";
    } else if (signatureKind == CIPHER_SIGNATURE_KIND_SHA256) {
        algorithmName = @"SHA-256";
    } else if (signatureKind == CIPHER_SIGNATURE_KIND_SHA384) {
        algorithmName = @"SHA-384";
    } else if (signatureKind == CIPHER_SIGNATURE_KIND_SHA512) {
        algorithmName = @"SHA-512";
    }
    return algorithmName;
}


/**
 * 公開鍵を設定する.
 * ※RSAなど公開鍵が必要な場合は設定する。
 * @param cipherPublicKey 公開鍵
 */
- (void)setCipherPublicKey: (NSString *)cipherPublicKey {
    @throw @"setCipherPublicKey() can't use.";
}

/**
 * 文字列を暗号化してSignatureを生成する.
 *
 * @param string 暗号化する文字列
 * @return Signature
 */
- (NSString *)generateSignature: (NSString *) input {

    if (self.mSignatureKind == CIPHER_SIGNATURE_KIND_SHA512) {
        
        // (参考)
        // http://www.freeshow.net.cn/ja/questions/bc51d42bb285bedd4ac775044e2f45a025185b70acdec57c5b272897d9501ec1/
        const char *cstr = [input cStringUsingEncoding:NSUTF8StringEncoding];
        NSData *data = [NSData dataWithBytes:cstr length:input.length];
        uint8_t digest[CC_SHA512_DIGEST_LENGTH];
        
        // This is an iOS5-specific method.
        // It takes in the data, how much data, and then output format, which in this case is an int array.
        CC_SHA512(data.bytes, (CC_LONG)data.length, digest);
        
        NSMutableString* output = [NSMutableString stringWithCapacity:CC_SHA512_DIGEST_LENGTH * 2];
        
        // Parse through the CC_SHA512 results (stored inside of digest[]).
        for(int i = 0; i < CC_SHA512_DIGEST_LENGTH; i++) {
            [output appendFormat:@"%02x", digest[i]];
        }
        
        return output;
    } else {
        /* 対応していない */
        return nil;
    }
}

@end
