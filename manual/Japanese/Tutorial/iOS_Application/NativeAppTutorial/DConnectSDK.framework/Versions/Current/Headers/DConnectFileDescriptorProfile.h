//
//  DConnectFileDescriptorProfile.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

/*! 
 @file
 @brief FileDescriptorプロファイルを実装するための機能を提供する。
 @author NTT DOCOMO
 */
#import <DConnectSDK/DConnectProfile.h>

/*!
 @brief プロファイル名。
 */
extern NSString *const DConnectFileDescriptorProfileName;

/*!
 @brief アトリビュート: open。
 */
extern NSString *const DConnectFileDescriptorProfileAttrOpen;

/*!
 @brief アトリビュート: close。
 */
extern NSString *const DConnectFileDescriptorProfileAttrClose;

/*!
 @brief アトリビュート: read。
 */
extern NSString *const DConnectFileDescriptorProfileAttrRead;

/*!
 @brief アトリビュート: write。
 */
extern NSString *const DConnectFileDescriptorProfileAttrWrite;

/*!
 @brief アトリビュート: onwatchfile。
 */
extern NSString *const DConnectFileDescriptorProfileAttrOnWatchFile;

/*!
 @brief パラメータ: flag。
 */
extern NSString *const DConnectFileDescriptorProfileParamFlag;

/*!
 @brief パラメータ: length。
 */
extern NSString *const DConnectFileDescriptorProfileParamPosition;

/*!
 @brief パラメータ: size。
 */
extern NSString *const DConnectFileDescriptorProfileParamSize;

/*!
 @brief パラメータ: length。
 */
extern NSString *const DConnectFileDescriptorProfileParamLength;

/*!
 @brief パラメータ: binary。
 */
extern NSString *const DConnectFileDescriptorProfileParamFileData;

/*!
 @brief パラメータ: media。
 */
extern NSString *const DConnectFileDescriptorProfileParamMedia;

/*!
 @brief パラメータ: file。
 */
extern NSString *const DConnectFileDescriptorProfileParamFile;

/*!
 @brief パラメータ: curr。
 */
extern NSString *const DConnectFileDescriptorProfileParamCurr;

/*!
 @brief パラメータ: prev。
 */
extern NSString *const DConnectFileDescriptorProfileParamPrev;

/*!
 @brief パラメータ: path。
 */
extern NSString *const DConnectFileDescriptorProfileParamPath;

@class DConnectFileDescriptorProfile;

/*!
 @protocol DConnectFileDescriptorProfileDelegate
 @brief File Descriptor Profileの各APIリクエスト通知用デリゲート。
 
 File Descriptor Profileの各APIへのリクエスト受信通知を受け取るデリゲート。
 */
@protocol DConnectFileDescriptorProfileDelegate <NSObject>
@optional

#pragma mark - Get Methods

/*!
 @brief ファイルオープンリクエストを受け取ったことをデリゲートに通知する。
 
 profileがファイルオープンリクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] File Descriptor Open API [GET]
 </p>
 
 @param[in] profile このイベントを通知するDConnectFileDescriptorProfileのオブジェクト
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] path ファイルパス
 @param[in] flag フラグ
 @retval YES レスポンスパラメータを返却する
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlink で返却すること。
 */
- (BOOL) profile:(DConnectFileDescriptorProfile *)profile didReceiveGetOpenRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
            path:(NSString *)path
            flag:(NSString *)flag;

/*!
 @brief ファイル読み込みリクエストを受け取ったことをデリゲートに通知する。
 
 profileがファイル読み込みリクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] File Descriptor Read API [GET]
 </p>
 
 @param[in] profile このイベントを通知するDConnectFileDescriptorProfileのオブジェクト
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] path ファイルパス
 @param[in] length ファイル読み込みサイズ
 @param[in] position 読み込み開始位置。省略された場合はnilが入る。
 @retval YES レスポンスパラメータを返却する
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlink で返却すること。
 */
- (BOOL) profile:(DConnectFileDescriptorProfile *)profile didReceiveGetReadRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
            path:(NSString *)path
          length:(NSNumber *)length
        position:(NSNumber *)position;

#pragma mark - Put Methods

/*!
 @brief ファイルクローズリクエストを受け取ったことをデリゲートに通知する。
 
 profileがファイルクローズリクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] File Descriptor Close API [GET]
 </p>
 
 @param[in] profile このイベントを通知するDConnectFileDescriptorProfileのオブジェクト
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] path ファイルパス
 @retval YES レスポンスパラメータを返却する
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlink で返却すること。
 */
- (BOOL) profile:(DConnectFileDescriptorProfile *)profile didReceivePutCloseRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
            path:(NSString *)path;


/*!
 @brief ファイル書き込みリクエストを受け取ったことをデリゲートに通知する。
 
 profileがファイル書き込みリクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] File Descriptor Write API [GET]
 </p>
 
 @param[in] profile このイベントを通知するDConnectFileDescriptorProfileのオブジェクト
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] path ファイルパス
 @param[in] media ファイルに書き込むデータのバイナリ
 @param[in] position ファイルの書き込み開始位置。省略された場合nilが入る。
 @retval YES レスポンスパラメータを返却する
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlink で返却すること。
 */
- (BOOL) profile:(DConnectFileDescriptorProfile *)profile didReceivePutWriteRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
            path:(NSString *)path
           media:(NSData *)media
        position:(NSNumber *)position;

#pragma mark Event Registration

/*!
 @brief onwatchfileイベント登録リクエストを受け取ったことをデリゲートに通知する。
 
 profileがonwatchfileイベント登録リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] File Descriptor WatchFile Event API [Register]
 </p>
 
 @param[in] profile このイベントを通知するDConnectFileDescriptorProfileのオブジェクト
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] sessionKey セッションキー
 @retval YES レスポンスパラメータを返却する
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectFileDescriptorProfile *)profile didReceivePutOnWatchFileRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey;

#pragma mark - Delete Methods
#pragma mark Event Unregistration

/*!
 @brief onwatchfileイベント解除リクエストを受け取ったことをデリゲートに通知する。
 
 profileがonwatchfileイベント解除リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] File Descriptor WatchFile Event API [Unregister]
 </p>
 
 @param[in] profile このイベントを通知するDConnectFileDescriptorProfileのオブジェクト
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] sessionKey セッションキー
 @retval YES レスポンスパラメータを返却する
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectFileDescriptorProfile *)profile didReceiveDeleteOnWatchFileRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey;

@end

/*!
 @class DConnectFileDescriptorProfile
 @brief File Descriptorプロファイル。
 
 File Descriptor Profileの各APIへのリクエストを受信する。
 受信したリクエストは各API毎にデリゲートに通知される。
 */
@interface DConnectFileDescriptorProfile : DConnectProfile

/*!
 @brief DConnectFileDescriptorProfileのデリゲートオブジェクト。
 
 デリゲートは @link DConnectFileDescriptorProfileDelegate @endlink を実装しなくてはならない。
 デリゲートはretainされない。
 */
@property (nonatomic, weak) id<DConnectFileDescriptorProfileDelegate> delegate;

#pragma mark - Setter

/*!
 @brief メッセージに現在の更新時間を設定する。
 @param[in] curr 現在の更新時間
 @param[in,out] message 現在の更新時間を格納するメッセージ
 */
+ (void) setCurr:(NSString *)curr target:(DConnectMessage *)message;

/*!
 @brief メッセージに以前の更新時間を設定する。
 @param[in] prev 以前の更新時間
 @param[in,out] message 以前の更新時間を格納するメッセージ
 */
+ (void) setPrev:(NSString *)prev target:(DConnectMessage *)message;

/*!
 @brief メッセージに読み込んだファイルのサイズを設定する。
 @param[in] size 読み込んだファイルのサイズ
 @param[in,out] message 読み込んだファイルのサイズを格納するメッセージ
 */
+ (void) setSize:(long long)size target:(DConnectMessage *)message;

/*!
 @brief メッセージにファイルデータを設定する。
 @param[in] fileData ファイルデータ
 @param[in,out] message ファイルデータを格納するメッセージ
 */
+ (void) setFileData:(NSString *)fileData target:(DConnectMessage *)message;

/*!
 @brief メッセージにパスを設定する。
 @param[in] path パス
 @param[in,out] message ファイルデータを格納するメッセージ
 */
+ (void) setPath:(NSString *)path target:(DConnectMessage *)message;

/*!
 @brief メッセージにファイル情報を設定する。
 @param[in] file ファイル情報
 @param[in,out] message ファイル情報を格納するメッセージ
 */
+ (void) setFile:(DConnectMessage *)file target:(DConnectMessage *)message;

#pragma mark - Getter

/*!
 @brief リクエストデータからファイルパスを取得する。
 @param[in] request リクエストパラメータ
 @return ファイルパス。無い場合はnilを返す。
 */
+ (NSString *) pathFromRequest:(DConnectMessage *)request;

/*!
 @brief リクエストデータからフラグを取得する。
 @param[in] request リクエストパラメータ
 @return フラグ文字列。無い場合はnilを返す。
 */
+ (NSString *) flagFromRequest:(DConnectMessage *)request;

/*!
 @brief リクエストデータからファイル長を取得する。
 @param[in] request リクエストパラメータ
 @return ファイル長。無い場合はnilを返す。
 */
+ (NSNumber *) lengthFromRequest:(DConnectMessage *)request;

/*!
 @brief リクエストデータから読み込み/書き込み位置を取得する。
 @param[in] request リクエストパラメータ
 @return 読み込み/書き込み位置。無い場合はnilを返す。
 */
+ (NSNumber *) positionFromRequest:(DConnectMessage *)request;

/*!
 @brief リクエストデータからバイナリを取得する。
 @param[in] request リクエストパラメータ
 @return バイナリ。無い場合はnilを返す。
 */
+ (NSData *) mediaFromRequest:(DConnectMessage *)request;

@end
