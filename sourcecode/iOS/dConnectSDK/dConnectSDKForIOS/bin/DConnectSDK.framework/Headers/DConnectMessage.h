//
//  DConnectMessage.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

/*!
 @file
 @brief dConnect上でやり取りされるデータを提供する。
 @author NTT DOCOMO
 */

/*!
 @brief 共通パラメータ: method。
 */
extern NSString *const DConnectMessageAction;

/*!
 @brief 共通パラメータ: deviceId。
 */
extern NSString *const DConnectMessageDeviceId;

/*!
 @brief 共通パラメータ: pluginId。
 */
extern NSString *const DConnectMessagePluginId;

/*!
 @brief 共通パラメータ: profile。
 */
extern NSString *const DConnectMessageProfile;

/*!
 @brief 共通パラメータ: interface。
 */
extern NSString *const DConnectMessageInterface;

/*!
 @brief 共通パラメータ: attribute。
 */
extern NSString *const DConnectMessageAttribute;

/*!
 @brief 共通パラメータ: sessionKey。
 */
extern NSString *const DConnectMessageSessionKey;

/*!
 @brief 共通パラメータ: accessToken。
 */
extern NSString *const DConnectMessageAccessToken;

/*!
 @brief 共通パラメータ: result。
 */
extern NSString *const DConnectMessageResult;

/*!
 @brief 共通パラメータ: errorCode。
 */
extern NSString *const DConnectMessageErrorCode;

/*!
 @brief 共通パラメータ: errorMessage。
 */
extern NSString *const DConnectMessageErrorMessage;

/*!
 @brief 共通パラメータ: api。
 */
extern NSString *const DConnectMessageAPI;

/*!
 @brief デフォルトAPI名: gotapi。
 */
extern NSString *const DConnectMessageDefaultAPI;

/*!
 @enum DConnectMessageActionType
 @brief アクションの定義。
 */
typedef NS_ENUM(NSUInteger, DConnectMessageActionType) {
    DConnectMessageActionTypeGet,		/*!< GETアクション */
    DConnectMessageActionTypePost,		/*!< POSTアクション */
    DConnectMessageActionTypePut,		/*!< PUTアクション */
    DConnectMessageActionTypeDelete,	/*!< DELETEアクション */
};

/*!
 @enum DConnectMessageResultType
 @brief resultの定義。
 */
typedef NS_ENUM(NSUInteger, DConnectMessageResultType) {
    DConnectMessageResultTypeOk = 0,				/*!< 処理に成功 */
    DConnectMessageResultTypeError = 1,				/*!< 処理に失敗 */
};

/*!
 @enum DConnectMessageErrorCodeType
 @brief エラーコードの定義。
 */
typedef NS_ENUM(NSUInteger, DConnectMessageErrorCodeType) {
    DConnectMessageErrorCodeUnknown  = 1,             /*!< 原因不明のエラー */
    DConnectMessageErrorCodeNotSupportProfile,        /*!< サポートされていないプロファイルにアクセスされた */
    DConnectMessageErrorCodeNotSupportAction,         /*!< サポートされていないアクションが指定された */
    DConnectMessageErrorCodeNotSupportAttribute,      /*!< サポートされていないアトリビュート・インターフェースが指定された */
    DConnectMessageErrorCodeEmptyDeviceId,            /*!< deviceIdが設定されていない */
    DConnectMessageErrorCodeNotFoundDevice,           /*!< デバイスが発見できなかった */
    DConnectMessageErrorCodeTimeout,                  /*!< タイムアウトが発生した */
    DConnectMessageErrorCodeUnknownAttribute,         /*!< 未知のインターフェース・アトリビュートにアクセスされた */
    DConnectMessageErrorCodeLowBattery,               /*!< バッテリー低下で操作不能 */
    DConnectMessageErrorCodeInvalidRequestParameter,  /*!< 不正なパラメータを受信した */
    DConnectMessageErrorCodeAuthorization,            /*!< 認証エラー */
    DConnectMessageErrorCodeExpiredAccessToken,       /*!< アクセストークンの有効期限が失効している */
    DConnectMessageErrorCodeEmptyAccessToken,         /*!< アクセストークンが設定されていない */
    DConnectMessageErrorCodeScope,                    /*!< スコープ外にアクセス要求された */
    DConnectMessageErrorCodeNotFoundClientId,         /*!< 認証時にclientIdが発見できなかった */
    DConnectMessageErrorCodeIllegalDeviceState,       /*!< デバイスの状態異常エラー */
    DConnectMessageErrorCodeIllegalServerState,       /*!< サーバーの状態異常エラー */
};

@class DConnectResponseMessage;

/*!
 @brief リクエストメッセージに対するレスポンスメッセージを受け取るblocks。
 
 @param response レスポンスメッセージ
 */
typedef void (^DConnectResponseBlocks)(DConnectResponseMessage *response);

@class DConnectMessage;
@class DConnectArray;

/*!
 @brief DConnectMessageに格納するための配列。
 @code
 
 DConnectArray *array = [DConnectArray array];
 [array addInteger:1];
 
 @endcode
 */
@interface DConnectArray : NSObject <NSCopying, NSFastEnumeration>

/*!
 @brief 指定された配列をコピーしてインスタンスを作成する。
 @param[in] array コピーする配列
 @retval DConnectArrayのインスタンス
 */
+ (instancetype) initWithArray:(NSArray *)array;

/*!
 @brief intを最後尾に追加する。
 @param[in] num 追加する値
 */
- (void)addInteger:(int)num;

/*!
 @brief longを最後尾に追加する。
 @param num 追加する値
 */
- (void)addLong:(long)num;

/*!
 @brief floatを最後尾に追加する。
 @param[in] num 追加する値
 */
- (void)addFloat:(float)num;

/*!
 @brief doubleを最後尾に追加する。
 @param[in] num 追加する値
 */
- (void)addDouble:(double)num;

/*!
 @brief NSDataを最後尾に追加する。
 @param[in] data 追加する値
 */
- (void)addData:(NSData *)data;

/*!
 @brief NSStringを最後尾に追加する。
 @param[in] string 追加する値
 */
- (void)addString:(NSString *)string;

/*!
 @brief DConnectMessageを最後尾に追加する。
 @param[in] message 追加する値
 */
- (void)addMessage:(DConnectMessage *)message;

/*!
 @brief DConnectArrayを最後尾に追加する。
 @param[in] array 追加する値
 */
- (void)addArray:(DConnectArray *)array;

/*!
 @brief NSNumberを最後尾に追加する。
 @param[in] number 追加する値
 */
- (void)addNumber:(NSNumber *)number;

/*!
 @brief 指定されたインデックスの値をintとして取得する。
 @param[in] index インデックス
 @retval 指定されたインデックスの値
 @retval INT_MIN 指定されたインデックスがなかった場合
 */
- (int)integerAtIndex:(NSUInteger)index;

/*!
 @brief 指定されたインデックスの値をlongとして取得する。
 @param[in] index インデックス
 @retval 指定されたインデックスの値
 @retval LONG_MIN 指定されたインデックスがなかった場合
 */
- (long)longAtIndex:(NSUInteger)index;

/*!
 @brief 指定されたインデックスの値をfloatとして取得する。
 @param[in] index インデックス
 @retval 指定されたインデックスの値
 @retval FLT_MIN 指定されたインデックスがなかった場合
 */
- (float)floatAtIndex:(NSUInteger)index;

/*!
 @brief 指定されたインデックスの値をdoubleとして取得する。
 @param[in] index インデックス
 @retval 指定されたインデックスの値
 @retval DBL_MIN 指定されたインデックスがなかった場合
 */
- (double)doubleAtIndex:(NSUInteger)index;

/*!
 @brief 指定されたインデックスの値をNSDataとして取得する。
 @param[in] index インデックス
 @retval 指定されたインデックスの値
 @retval nil 指定されたインデックスがなかった場合
 */
- (NSData *)dataAtIndex:(NSUInteger)index;

/*!
 @brief 指定されたインデックスの値をNSStringとして取得する。
 @param[in] index インデックス
 @retval 指定されたインデックスの値
 @retval nil 指定されたインデックスがなかった場合
 */
- (NSString *)stringAtIndex:(NSUInteger)index;

/*!
 @brief 指定されたインデックスの値をNSNumberとして取得する。
 @param[in] index インデックス
 @retval 指定されたインデックスの値
 @retval nil 指定されたインデックスがなかった場合
 */
- (NSNumber *)numberAtIndex:(NSUInteger)index;

/*!
 @brief 指定されたインデックスの値をDConnectMessageとして取得する。
 @param[in] index インデックス
 @retval 指定されたインデックスの値
 @retval nil 指定されたインデックスがなかった場合
 */
- (DConnectMessage *)messageAtIndex:(NSUInteger)index;

/*!
 @brief 指定されたインデックスの値をDConnectArrayとして取得する。
 @param[in] index インデックス
 @retval 指定されたインデックスの値
 @retval nil 指定されたインデックスがなかった場合
 */
- (DConnectArray *)arrayAtIndex:(NSUInteger)index;

/*!
 @brief 配列のサイズを取得する。
 @retval 配列のサイズ
 */
- (unsigned int)count;

/*!
 @brief 指定されたオブジェクトが配列に含まれているかをチェックする。
 @param[in] obj チェックするオブジェクト
 @retval YES 含まれている場合
 @retval NO 含まれていない場合
 */
- (BOOL)containsObject:(id)obj;

/*!
 @brief 指定されたインデックスの値を取得する。
 @param[in] index インデックス
 @retval 指定されたインデックスで設定されている値
 @retval nil 指定されたインデックスに対応する値が存在しない場合
 */
- (id) objectAtIndex:(NSUInteger)index;

@end

/*!
 @brief DConnectArrayを作成するためのクラス。
 */
@interface DConnectArray (DConnectArrayCreation)

/*!
 @brief DConnectArrayのインスタンスを作成する。
 @return DConnectArrayのインスタンス
 */
+ (instancetype) array;
@end


/*!
 @class DConnectMessage
 @brief Device Connect上でやり取りされるメッセージ。
 @code
 
 DConnectMessage *message = [DConnectMessage message];
 [message setInteger:0 forKey:@"sample"];
 
 @endcode
 */
@interface DConnectMessage : NSObject <NSCopying>

/*!
 @brief 指定されたNSDictionaryをコピーしてDConnectMessageを作成する。
 @param[in] dict コピーするNSDictionary
 @retval DConnectMessageのインスタンス
 */
+ (instancetype) initWithDictionary:(NSDictionary *)dict;

/*!
 @brief 指定されたキーでintを登録する。
 @param[in] num 数値
 @param[in] aKey キー
 */
- (void)setInteger:(int)num forKey:(NSString *)aKey;

/*!
 @brief 指定されたキーでlongを登録する。
 @param[in] num 数値
 @param[in] aKey キー
 */
- (void)setLong:(long)num forKey:(NSString *)aKey;

/*!
 @brief 指定されたキーでlong longを登録する。
 @param[in] num 数値
 @param[in] aKey キー
 */
- (void)setLongLong:(long long)num forKey:(NSString *)aKey;

/*!
 @brief 指定されたキーでfloatを登録する。
 @param[in] num 数値
 @param[in] aKey キー
 */
- (void)setFloat:(float)num forKey:(NSString *)aKey;

/*!
 @brief 指定されたキーでdoubleを登録する。
 @param[in] num 数値
 @param[in] aKey キー
 */
- (void)setDouble:(double)num forKey:(NSString *)aKey;

/*!
 @brief 指定されたキーでBOOLを登録する。
 @param[in] num 数値
 @param[in] aKey キー
 */
- (void)setBool:(BOOL)num forKey:(NSString *)aKey;

/*!
 @brief 指定されたキーでNSDataを登録する。
 @param[in] data NSData
 @param[in] aKey キー
 */
- (void)setData:(NSData *)data forKey:(NSString *)aKey;

/*!
 @brief 指定されたキーでNSStringを登録する。
 @param[in] string 文字列
 @param[in] aKey キー
 */
- (void)setString:(NSString *)string forKey:(NSString *)aKey;

/*!
 @brief 指定されたキーでDConnectArrayを登録する。
 @param[in] array 配列
 @param[in] aKey キー
 */
- (void)setArray:(DConnectArray *)array forKey:(NSString *)aKey;


/*!
 @brief 指定されたキーでmessageを登録する。
 @param[in] message メッセージ
 @param[in] aKey キー
 */
- (void)setMessage:(DConnectMessage *)message forKey:(NSString *)aKey;

/*!
 @brief 指定されたキーでNSNumberを登録する。
 @param[in] number 数値
 @param[in] aKey キー
 */
- (void)setNumber:(NSNumber *)number forKey:(NSString *)aKey;


/*!
 @brief 指定されたキーのintを取得する。
 @param[in] aKey キー
 @retval 数値
 @retval INT_MIN キーが登録されていない場合
 */
- (int)integerForKey:(NSString *)aKey;

/*!
 @brief 指定されたキーのlongを取得する。
 @param[in] aKey キー
 @retval 数値
 @retval LONG_MIN キーが登録されていない場合
 */
- (long)longForKey:(NSString *)aKey;

/*!
 @brief 指定されたキーのlong longを取得する。
 @param[in] aKey キー
 @retval 数値
 @retval LONG_LONG_MIN キーが登録されていない場合
 */
- (long long)longLongForKey:(NSString *)aKey;

/*!
 @brief 指定されたキーのfloatを取得する。
 @param[in] aKey キー
 @retval 数値
 @retval FLT_MIN キーが登録されていない場合
 */
- (float)floatForKey:(NSString *)aKey;

/*!
 @brief 指定されたキーのdoubleを取得する。
 @param[in] aKey キー
 @retval 数値
 @retval DBL_MIN キーが登録されていない場合
 */
- (double)doubleForKey:(NSString *)aKey;

/*!
 @brief 指定されたキーのBOOLを取得する。
 @param[in] aKey キー
 @retval 数値
 @retval NO キーが登録されていない場合
 */
- (BOOL)boolForKey:(NSString *)aKey;

/*!
 @brief 指定されたキーのNSDataを取得する。
 @param[in] aKey キー
 @retval NSDataのインスタンス
 @retval nil 指定されたキーに対応するNSDataが存在しない場合
 */
- (NSData *)dataForKey:(NSString *)aKey;

/*!
 @brief 指定されたキーのNSStringを取得する。
 @param[in] aKey キー
 @retval NSStringのインスタンス
 @retval nil 指定されたキーに対応するNSStringが存在しない場合
 */
- (NSString *)stringForKey:(NSString *)aKey;

/*!
 @brief 指定されたキーのDConnectArrayを取得する。
 @param[in] aKey キー
 @retval DConnectArrayのインスタンス
 @retval nil 指定されたキーに対応するDConnectArrayが存在しない場合
 */
- (DConnectArray *)arrayForKey:(NSString *)aKey;

/*!
 @brief 指定されたキーのDConnectMessageを取得する。
 @param[in] aKey キー
 @retval DConnectMessageのインスタンス
 @retval nil 指定されたキーに対応するDConnectMessageが存在しない場合
 */
- (DConnectMessage *)messageForKey:(NSString *)aKey;

/*!
 @brief 指定されたキーのNSNumberを取得する。
 @param[in] aKey キー
 @retval NSNumberのインスタンス
 @retval nil 指定されたキーに対応するNSNumberが存在しない場合
 */
- (NSNumber *)numberForKey:(NSString *)aKey;

/*!
 @brief 指定されたキーのオブジェクトを取得する。
 @param[in] aKey キー
 @retval 指定されたキーで設定されているオブジェクト
 @retval nil 指定されたキーに対応するオブジェクトが存在しない場合
 */
- (id) objectForKey:(NSString *)aKey;

/*!
 @brief 指定されたキーが存在するかチェックする。
 @param[in] aKey 存在確認を行うキー
 @retval YES 存在する場合
 @retval NO 存在しない場合
 */
- (BOOL)hasKey:(NSString *)aKey;

/*!
 @brief JSONオブジェクトに変換する。
 
 Foundationオブジェクトから生成されたJSONデータを返す。
 戻り値は NSJSONSerialization::JSONObjectWithData:options:error:の戻り値と同じになる。
 
 @retval JSONオブジェクト 変換後のJSONオブジェクト
 @retval nil JSONのパースに失敗した場合
 */
- (id) convertToJSONObject;

/*!
 @brief JSON文字列に変換する。
 @retval JSON文字列 変換後のJSON文字列
 @retval nil JSONのパースに失敗した場合
 */
- (NSString *) convertToJSONString;

/*!
 @brief キーの一覧を返す。
 @return キーの一覧
 */
- (NSArray *) allKeys;

@end

/*!
 @brief DConnectMessageを作成するためのクラス。
 */
@interface DConnectMessage (DConnectMessageCreation)

/*!
 @brief DConnectMessageのインスタンスを作成する。
 @return DConnectMessageのインスタンス
 */
+ (instancetype) message;

@end
