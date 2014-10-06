//
//  DConnectFileManager.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

/*!
 @file
 @brief ファイル管理機能を提供するクラス。
 @author NTT DOCOMO
 */
#import <DConnectSDK/DConnectDevicePlugin.h>

/*!
 @brief デバイスプラグイン用のファイル管理クラス。
 
 デバイスプラグイン専用ディレクトリを用意し、そのディレクトリに簡易にアクセスするためのAPIを提供する。
 */
@interface DConnectFileManager : NSObject

/*!
 @brief デバイスプラグイン向けファイル管理オブジェクトを生成する。
 
 デバイスプラグイン専用ディレクトリ（アプリSandbox内のApplication Support以下のデバイスプラグインと同じ名称のディレクトリ）
 が存在しない場合、ここで作成する。
 
 @param[in] plugin ファイルマネージャと紐づけるデバイスプラグイン
 
 @retval DConnectFileManagerのインスタンス
 @retval nil インスタンスの作成に失敗した場合
 */
+ (instancetype) fileManagerForPlugin:(DConnectDevicePlugin *)plugin;

/*!
 @brief デバイスプラグインのインスタンスを取得する。
 
 @return デバイスプラグイン
 */
- (DConnectDevicePlugin *) plugin;

/*!
 @brief デバイスプラグイン専用ディレクトリへのURLを返却する。
 
 デバイスプラグイン専用ディレクトリが削除されている場合は新たに作成する。
 
 @return デバイスプラグイン専用ディレクトリへのURLを表すNSURL
 */
- (NSURL *) URL;

/*!
 @brief ファイルをデバイスプラグイン専用ディレクトリ以下に作成する。

 <p>
 指定されたパスが絶対パスの場合には、デバイスプラグイン専用ディレクトリではなく、指定されたパスに保存する。
 </p>
 <p>
 相対パスの場合には、デバイスプラグイン専用ディレクトリにファイルを保存する。
 </p>
 
 @param[in] path 作成するファイルのパス
 @param[in] contents 作成するファイルのデータ
 
 @retval 保存したファイルへのURL。
 @retval nil エラーが生じた場合。
 */
- (NSString *)createFileForPath:(NSString *)path contents:(NSData *)contents;

/*!
 デバイスプラグイン専用ディレクトリもしくは指定されたディレクトリのファイル項目をイテレートする。
 
 @param[in] mask イテレートのオプション。
 @param[in] dirPath ディレクトリのパス
 
 @retval デバイスプラグイン専用ディレクトリ内のファイル項目をイテレートするNSDirectoryEnumerator。
 @retval nil エラーが生じた場合。
 */
- (NSDirectoryEnumerator *)enumeratorWithOptions:(NSDirectoryEnumerationOptions)mask dirPath:(NSString*)dirPath;

/*!
 @brief デバイスプラグイン専用ディレクトリ以下のファイルを更新する。
 
 @param[in] path 更新したいファイルのパス
 @param[in] newPath 更新したいファイルの新たなファイルパス
 @param[in] newContents 更新したいファイルの新たなデータ
 
 @retval 更新したファイルのURL。
 @retval nil エラーが生じた場合。
 */
- (NSString *)updateFileForPath:(NSString *)path newPath:(NSString *)newPath newContents:(NSData *)newContents;

/*!
 @brief デバイスプラグイン専用ディレクトリ以下のファイルを削除する。
 
 @param[in] path 削除したいファイルのパス
 
 @retval YES 削除に成功
 @retval NO 削除に失敗
 */
- (BOOL)removeFileForPath:(NSString *)path;

/**
 @brief 拡張子に対応するMIMEタイプを返す。
 
 拡張子にはドットを含めないこと（e.g. "txt"）。
 
 @param[in] extension 拡張子
 
 @retval MIMEタイプ
 @retval nil 見つからなかった場合。
 */
+ (NSString *) searchMimeTypeForExtension:(NSString *)extension;

/**
 @brief MIMEタイプに対応する拡張子を返す。
 
 拡張子にはドットを含まない（e.g. "txt"）。
 
 @param[in] mimeType MIMEタイプ
 
 @retval 拡張子 複数あった場合は、最も短く辞書順で手前のものを返す。
 @retval nil 見つからなかった場合。
 */
+ (NSString *) searchExtensionForMimeType:(NSString *)mimeType;

/**
 @brief 任意のデータ型ファイルのMIMEタイプを返す。
 
 現状、application/octet-streamを返す。拡張子からMIMEタイプを推定できなかった場合、これを代わりに用いるとよい。
 
 @return MIMEタイプ「application/octet-stream」
 */
+ (NSString *) mimeTypeForArbitraryData;

@end
