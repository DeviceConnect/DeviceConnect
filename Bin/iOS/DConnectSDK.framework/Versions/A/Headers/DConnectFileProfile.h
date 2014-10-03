//
//  DConnectFileProfile.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

/*! 
 @file
 @brief Fileプロファイルを実装するための機能を提供する。
 @author NTT DOCOMO
 */
#import <DConnectSDK/DConnectProfile.h>

/*!
 @brief プロファイル名。
 */
extern NSString *const DConnectFileProfileName;

/*!
 @brief アトリビュート: list。
 */
extern NSString *const DConnectFileProfileAttrList;

/*!
 @brief アトリビュート: receive。
 */
extern NSString *const DConnectFileProfileAttrReceive;

/*!
 @brief アトリビュート: remove。
 */
extern NSString *const DConnectFileProfileAttrRemove;

/*!
 @brief アトリビュート: send。
 */
extern NSString *const DConnectFileProfileAttrSend;

/*!
 @brief アトリビュート: mkdir。
 */
extern NSString *const DConnectFileProfileAttrMkdir;

/*!
 @brief アトリビュート: rmdir。
 */
extern NSString *const DConnectFileProfileAttrRmdir;

/*!
 @brief パラメータ: mimetype。
 */
extern NSString *const DConnectFileProfileParamMIMEType;

/*!
 @brief パラメータ: files。
 */
extern NSString *const DConnectFileProfileParamFiles;

/*!
 @brief パラメータ: fileName。
 */
extern NSString *const DConnectFileProfileParamFileName;

/*!
 @brief パラメータ: fileSize。
 */
extern NSString *const DConnectFileProfileParamFileSize;

/*!
 @brief パラメータ: data。
 */
extern NSString *const DConnectFileProfileParamData;

/*!
 @brief パラメータ: path。
 */
extern NSString *const DConnectFileProfileParamPath;

/*!
 @brief パラメータ: fileType。
 */
extern NSString *const DConnectFileProfileParamFileType;

/*!
 @brief パラメータ: order。
 */
extern NSString *const DConnectFileProfileParamOrder;

/*!
 @brief パラメータ: order。
 */
extern NSString *const DConnectFileProfileParamForce;

/*!
 @brief パラメータ: offset。
 */
extern NSString *const DConnectFileProfileParamOffset;

/*!
 @brief パラメータ: limit。
 */
extern NSString *const DConnectFileProfileParamLimit;

/*!
 @brief パラメータ: count。
 */
extern NSString *const DConnectFileProfileParamCount;

/*!
 @brief パラメータ: updateDate。
 */
extern NSString *const DConnectFileProfileParamUpdateDate;

/*!
 @brief 並び順: 昇順。
 */
extern NSString *const DConnectFileProfileOrderASC;

/*!
 @brief 並び順: 降順。
 */
extern NSString *const DConnectFileProfileOrderDESC;


/*!
 @enum DConnectFileProfileFileType
 @brief ファイルタイプ定数。
 */
typedef NS_ENUM(NSUInteger, DConnectFileProfileFileType) {
    DConnectFileProfileFileTypeFile = 0, /*!< ファイル */
    DConnectFileProfileFileTypeDir,      /*!< ディレクトリ */
};


@class DConnectFileProfile;

/*!
 @protocol DConnectFileProfileDelegate
 @brief File Profile各のAPIリクエスト通知用デリゲート。
 
 File Profileの各APIへのリクエスト受信通知を受け取るデリゲート。
 */
@protocol DConnectFileProfileDelegate <NSObject>
@optional

#pragma mark - Get Methods

/*!
 @brief ファイル取得リクエストを受け取ったことをデリゲートに通知する。
 
 profileがファイル取得リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] File Receive API [GET]
 </p>
 
 @param[in] profile このイベントを通知するDConnectFileProfileのオブジェクト
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] path ファイルパス
 @retval YES レスポンスパラメータを返却する
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlink で返却すること。
 */
- (BOOL) profile:(DConnectFileProfile *)profile didReceiveGetReceiveRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
            path:(NSString *)path;

/*!
 @brief ファイル一覧取得リクエストを受け取ったことをデリゲートに通知する。
 
 profileがファイル一覧取得リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] File List API [GET]
 </p>
 
 @param[in] profile このイベントを通知するDConnectFileProfileのオブジェクト
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] path ファイルパス。省略された場合はnil。
 @param[in] mimeType マイムタイプ。省略された場合はnil。
 @param[in] order 並び順。0スタートのインデックスで、偶数番にパラメータ名、奇数番に並び順が入る。。省略された場合はnil。
 @param[in] offset 検索のオフセット。省略された場合はnil。
 @param[in] limit 検索件数リミット。省略された場合はnil。
 @retval YES レスポンスパラメータを返却する
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlink で返却すること。
 */
- (BOOL) profile:(DConnectFileProfile *)profile didReceiveGetListRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
            path:(NSString *)path
        mimeType:(NSString *)mimeType
           order:(NSArray *)order
          offset:(NSNumber *)offset
           limit:(NSNumber *)limit;

#pragma mark - Post Methods

/*!
 @brief ファイル送信リクエストを受け取ったことをデリゲートに通知する。
 
 profileがファイル送信リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] File Send API [POST]
 </p>
 
 @param[in] profile このイベントを通知するDConnectFileProfileのオブジェクト
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] path ファイルパス。省略された場合はnil。
 @param[in] mimeType マイムタイプ。省略された場合はnil。
 @param[in] data ファイルのバイナリデータ
 @retval YES レスポンスパラメータを返却する
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlink で返却すること。
 */
- (BOOL) profile:(DConnectFileProfile *)profile didReceivePostSendRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
            path:(NSString *)path
        mimeType:(NSString *)mimeType
            data:(NSData *)data;

/*!
 @brief ディレクトリ作成リクエストを受け取ったことをデリゲートに通知する。
 
 profileがディレクトリ作成リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Make Directory API [POST]
 </p>
 
 @param[in] profile このイベントを通知するDConnectFileProfileのオブジェクト
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] path ファイルパス。省略された場合はnil。
 @retval YES レスポンスパラメータを返却する
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlink で返却すること。
 */
- (BOOL) profile:(DConnectFileProfile *)profile didReceivePostMkdirRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
            path:(NSString *)path;

#pragma mark - Delete Methods

/*!
 @brief ファイル削除リクエストを受け取ったことをデリゲートに通知する。
 
 profileがファイル削除リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] File Remove API [PUT]
 </p>
 
 @param[in] profile このイベントを通知するDConnectFileProfileのオブジェクト
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] path ファイルパス。省略された場合はnil。
 @retval YES レスポンスパラメータを返却する
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlink で返却すること。
 */- (BOOL) profile:(DConnectFileProfile *)profile didReceiveDeleteRemoveRequest:(DConnectRequestMessage *)request
           response:(DConnectResponseMessage *)response
           deviceId:(NSString *)deviceId
               path:(NSString *)path;

/*!
 @brief ディレクトリ削除リクエストを受け取ったことをデリゲートに通知する。
 
 profileがディレクトリ削除リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Remove Directory API [POST]
 </p>
 
 @param[in] profile このイベントを通知するDConnectFileProfileのオブジェクト
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] path ファイルパス。省略された場合はnil。
 @param[in] force 強制削除フラグ。省略された場合はfalse。
 @retval YES レスポンスパラメータを返却する
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlink で返却すること。
 */
- (BOOL) profile:(DConnectFileProfile *)profile didReceiveDeleteRmdirRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
            path:(NSString *)path
           force:(BOOL)force;

@end


/*!
 @class DConnectFileProfile
 @brief Fileプロファイル。
 
 File Profileの各APIへのリクエストを受信する。
 受信したリクエストは各API毎にデリゲートに通知される。
 */
@interface DConnectFileProfile : DConnectProfile

/*!
 @brief DConnectFileProfileのデリゲートオブジェクト。
 
 デリゲートは @link DConnectFileProfileDelegate @endlink を実装しなくてはならない。
 デリゲートはretainされない。
 */
@property (nonatomic, weak) id<DConnectFileProfileDelegate> delegate;

#pragma mark - Setter

/*!
 @brief メッセージにURIを設定する。
 @param[in] uri ファイルのURI
 @param[in,out] message ファイルのURIを格納するメッセージ
 */
+ (void) setURI:(NSString *)uri target:(DConnectMessage *)message;

/*!
 @brief メッセージにマイムタイプを設定する。
 @param[in] mimeType マイムタイプ
 @param[in,out] message マイムタイプを格納するメッセージ
 */
+ (void) setMIMEType:(NSString *)mimeType target:(DConnectMessage *)message;

/*!
 @brief メッセージにファイル一覧を設定する。
 @param[in] files ファイル一覧
 @param[in,out] message ファイル一覧を格納するメッセージ
 */
+ (void) setFiles:(DConnectArray *)files target:(DConnectMessage *)message;

/*!
 @brief メッセージにファイル名を設定する。
 @param[in] fileName ファイル名
 @param[in,out] message ファイル名を格納するメッセージ
 */
+ (void) setFileName:(NSString *)fileName target:(DConnectMessage *)message;

/*!
 @brief メッセージにファイルサイズを設定する。
 @param[in] fileSize ファイルサイズ
 @param[in,out] message ファイルサイズを格納するメッセージ
 */
+ (void) setFileSize:(long long)fileSize target:(DConnectMessage *)message;

/*!
 @brief メッセージにファイルタイプを設定する。
 @param[in] fileType ファイルタイプ
 @param[in,out] message ファイルタイプを格納するメッセージ
 */
+ (void) setFileType:(int)fileType target:(DConnectMessage *)message;

/*!
 @brief メッセージにファイルパスを設定する。
 @param[in] path ファイルパス
 @param[in,out] message ファイルパスを格納するメッセージ
 */
+ (void) setPath:(NSString *)path target:(DConnectMessage *)message;

/*!
 @brief メッセージに検索件数を設定する。
 @param[in] count 検索件数
 @param[in,out] message 検索件数を格納するメッセージ
 */
+ (void) setCount:(int)count target:(DConnectMessage *)message;

/*!
 @brief メッセージに更新日を設定する。
 @param[in] updateDate 更新日
 @param[in,out] message 更新日を格納するメッセージ
 */
+ (void) setUpdateDate:(NSString *)updateDate tareget:(DConnectMessage *)message;

#pragma mark - Getter

/*!
 @brief リクエストデータからファイル名を取得する。
 @param[in] request リクエストパラメータ
 @return ファイル名。無い場合はnilを返す。
 */
+ (NSString *) fileNameFromRequest:(DConnectMessage *)request;

/*!
 @brief リクエストデータからマイムタイプを取得する。
 @param[in] request リクエストパラメータ
 @return マイムタイプ。無い場合はnilを返す。
 */
+ (NSString *) mimeTypeFromRequest:(DConnectMessage *)request;

/*!
 @brief リクエストデータからバイナリを取得する。
 @param[in] request リクエストパラメータ
 @return バイナリ。無い場合はnilを返す。
 */
+ (NSData *) dataFromRequest:(DConnectMessage *)request;

/*!
 @brief リクエストデータからファイルパスを取得する。
 @param[in] request リクエストパラメータ
 @return ファイルパス。無い場合はnilを返す。
 */
+ (NSString *) pathFromRequest:(DConnectMessage *)request;

/*!
 @brief リクエストデータから並び順を取得する。
 @param[in] request リクエストパラメータ
 @return 並び順。無い場合はnilを返す。
 */
+ (NSString *) orderFromRequest:(DConnectMessage *)request;

/*!
 @brief リクエストデータからオフセットを取得する。
 @param[in] request リクエストパラメータ
 @return オフセット。無い場合はnilを返す。
 */
+ (NSNumber *) offsetFromRequest:(DConnectMessage *)request;

/*!
 @brief リクエストデータからリミットを取得する。
 @param[in] request リクエストパラメータ
 @return リミット。無い場合はnilを返す。
 */
+ (NSNumber *) limitFromRequest:(DConnectMessage *)request;

/*!
 @brief リクエストデータから強制削除フラグを取得する。
 @param[in] request リクエストパラメータ
 @return 強制削除フラグ。無い場合はfalseを返す。
 */
+ (BOOL) forceFromRequest:(DConnectMessage *)request;

@end
