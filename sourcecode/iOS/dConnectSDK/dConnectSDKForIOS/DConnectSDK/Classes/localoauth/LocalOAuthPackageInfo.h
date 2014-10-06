//
//  LocalOAuthPackageInfo.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>

@interface LocalOAuthPackageInfo : NSObject

/** パッケージ名. */
@property NSString *packageName;
    
/** デバイスID(アプリの場合はnilを設定する). */
@property NSString *deviceId;

/*!
    コンストラクタ(アプリを指定する場合).
    @param[in] packageName	パッケージ名.
 */
- (LocalOAuthPackageInfo *) initWithPackageName: (NSString *)packageName;

/*!
    コンストラクタ(デバイスプラグインを指定する場合).
    @param[in] packageName	パッケージ名.
    @param[in] deviceId		デバイスID.
 */
- (LocalOAuthPackageInfo *) initWithPackageNameDeviceId: (NSString *)packageName deviceId:(NSString *)deviceId;

/*!
    オブジェクト比較.
    @param o	比較対象のオブジェクト
    @return YES: 同じ値を持つオブジェクトである。 / NO: 異なる値を持っている。
 */
- (BOOL) equals: (LocalOAuthPackageInfo *) o;



@end
