//
//  CipherAuthSignature.m
//  dConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "CipherAuthSignature.h"
#import "CipherSignatureProc.h"
#import "CipherSignatureKind.h"
#import "CipherSignatureFactory.h"


@interface CipherAuthSignature()

/**
 * 入力文字列(key-value).
 */
@property NSMutableDictionary *mInputMap;

// Signature生成処理.
@property CipherSignatureProc *mSignatureProc;


- (id)initWithSignatureKind: (CipherSignatureKind)signatureKind;
- (void)setCipherPublicKey: (NSString *)cipherPublicKey;
- (void) empty;
- (void) put: (NSString *)key forValue:(NSString *) value;
- (NSString *) generateSignature: (NSString *) clientSecret;


@end



@implementation CipherAuthSignature

// クラスメソッドの実装、初期化処理
+(id)init {
    id _id = [[self alloc] init];
    
    return _id;
}

/**
 * コンストラクタ.
 * @param signatureKind 暗号化処理の種類
 */
-(id)initWithSignatureKind: (CipherSignatureKind)signatureKind {
    id _id = [self init];
    
    self.mInputMap = [NSMutableDictionary dictionary];
    
    self.mSignatureProc = [CipherSignatureFactory getInstance: signatureKind];
    
    return _id;
}

/**
 * 公開鍵設定(RSAの場合に使用する).
 * @param cipherPublicKey 公開鍵設定
 */
-(void)setCipherPublicKey: (NSString *)cipherPublicKey {
    [self.mSignatureProc setCipherPublicKey: cipherPublicKey];
}

/**
 * 連結文字列バッファをクリアする.
 */
-(void) empty {
    
    [self.mInputMap removeAllObjects];
}

/**
 * 連結する文字列を追加する.
 *
 * @param key キー
 * @param value バリュー
 */
- (void) put: (NSString *)key forValue:(NSString *) value {
    [self.mInputMap setObject:value forKey:key];
}

/**
 * Signatureを生成して返す.
 * @param clientSecret クライアントシークレット
 * @return Signature
 */
- (NSString *) generateSignature: (NSString *) clientSecret {
    
    // keyをソートする
    NSArray *keys = [self sortedKeys:self.mInputMap];
    
    NSString *str = @"";
    NSUInteger c = [keys count];
    for (NSUInteger i = 0; i < c; i++) {
        NSString *key = [keys objectAtIndex:i];
        NSString *value = [self.mInputMap objectForKey: key];
        str = [str stringByAppendingString: key];
        str = [str stringByAppendingString: value];
    }
    
    str = [str stringByAppendingString: @"clientSecret"];
    str = [str stringByAppendingString: clientSecret];
    
    NSString *signature = [self.mSignatureProc generateSignature: str];
    return signature;
}


// NSStringをソートする比較関数
NSInteger stringSortCompareInfo(id aInfo1, id aInfo2, void *context)
{
	NSString *str1 = (NSString *) aInfo1;
	NSString *str2 = (NSString *) aInfo2;
    NSComparisonResult result = [str1 compare: str2];
    return result;
}

// NSMutableDictionaryのkeyをソートしてNSArray(NSString*のhairetu)に格納して返す.
- (NSArray *)sortedKeys: (NSMutableDictionary *) dictionary {
    
    // 全てのkeyを取得してarrayに格納
    NSMutableArray *array = [NSMutableArray array];
    for (NSString* key in [dictionary keyEnumerator]) {
        [array addObject: key];
    }
    
    // arrayをソートして返す
    NSArray *sortKeys = [array sortedArrayUsingFunction: stringSortCompareInfo context:NULL];
    return sortKeys;
}




/**
 * RSAでSignature生成(アクセストークン発行リクエストを送信するときに添付するSignatureを生成する).
 *
 * @param clientId クライアントID
 * @param grantType グラントタイプ
 * @param deviceId デバイスID(UIアプリのときはnullまたは""を指定する)
 * @param scopes スコープ
 * @param clientSecret クライアントシークレット
 * @return Signature
 */
+ (NSString *)generateSignatureWithClientId: (NSString *)clientId
                                  grantType: (NSString *)grantType
                                   deviceId: (NSString *)deviceId
                                     scopes: (NSArray *)scopes
                               clientSecret: (NSString *)clientSecret {
    
    CipherAuthSignature *authSignature =
            (CipherAuthSignature *)[[CipherAuthSignature alloc]initWithSignatureKind: CIPHER_SIGNATURE_KIND_SHA512];
    [authSignature empty];
    
    [authSignature put: @"clientId" forValue: clientId];
    [authSignature put: @"grantType" forValue: grantType];
    
    if (deviceId != nil && [deviceId length] > 0) {
        [authSignature put: @"deviceId" forValue: deviceId];
    }
    
    NSMutableArray *scopes__ = [scopes mutableCopy];
    NSString *strScopes = @"";
    NSArray *sortScopes = [scopes__ sortedArrayUsingFunction: stringSortCompareInfo
                                                     context:NULL];
    
    NSUInteger c = [sortScopes count];
    for (NSUInteger i = 0; i < c; i++) {
        NSString *scope = [sortScopes objectAtIndex: i];
        if (i > 0) {
            strScopes = [strScopes stringByAppendingString: @","];
        }
        strScopes = [strScopes stringByAppendingString: scope];
    }
    [authSignature put: @"scopes" forValue: strScopes];
    
    NSString *signature = [authSignature generateSignature: clientSecret];
    return signature;
}

/**
 * アクセストークン発行のレスポンスを返信するときに添付するSignatureを生成する.
 *
 * @param accessToken アクセストークン
 * @param clientSecret クライアントシークレット
 * @return Signature
 */
+ (NSString *)generateSignatureWithAccessToken: (NSString *)accessToken
                                  clientSecret: (NSString *)clientSecret {
    
    CipherAuthSignature *authSignature =
            (CipherAuthSignature *)[[CipherAuthSignature alloc]initWithSignatureKind: CIPHER_SIGNATURE_KIND_SHA512];
    [authSignature empty];
    
    [authSignature put: @"accessToken" forValue: accessToken];
    
    NSString *signature = [authSignature generateSignature: clientSecret];
    return signature;
}

@end
