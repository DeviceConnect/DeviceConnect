//
//  DConnectFileManager.h
//  DConnectSDK
//
//  Created by 福井 重和 on 2014/06/27.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <DConnectSDK/DConnectSDK.h>

/*!
 @brief デバイスプラグイン用のファイル管理クラス。
 
 デバイスプラグイン専用ディレクトリを用意し、そのディレクトリに簡易にアクセスするためのAPIを提供する。
 */
@interface DConnectFileManager : NSObject

/*!
 @brief デバイスプラグイン向けファイル管理オブジェクトを生成する。
 
 デバイスプラグイン専用ディレクトリ（アプリSandbox内のApplication Support以下のデバイスプラグインと同じ名称のディレクトリ）
 が存在しない場合、ここで作成する。
 
 @param plugin ファイルマネージャと紐づけるデバイスプラグイン
 
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
 
 @return デバイスプラグイン専用ディレクトリへのURLを表す<code>NSURL</code>
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
 
 @param path 作成するファイルのパス
 @param contents 作成するファイルのデータ
 
 @retval 保存したファイルへのURL。
 @retval nil エラーが生じた場合。
 */
- (NSURL *)createFileForPath:(NSString *)path contents:(NSData *)contents;

/*!
 デバイスプラグイン専用ディレクトリもしくは指定されたディレクトリのファイル項目をイテレートする。
 
 @param mask イテレートのオプション。
 @param dirPath ディレクトリのパス
 
 @retval デバイスプラグイン専用ディレクトリ内のファイル項目をイテレートする<code>NSDirectoryEnumerator</code>。
 @retval nil エラーが生じた場合。
 */
- (NSDirectoryEnumerator *)enumeratorWithOptions:(NSDirectoryEnumerationOptions)mask dirPath:(NSString*)dirPath;

/*!
 @brief デバイスプラグイン専用ディレクトリ以下のファイルを更新する。
 
 @param path 更新したいファイルのパス
 @param newPath 更新したいファイルの新たなファイルパス
 @param newContents 更新したいファイルの新たなデータ
 
 @retval 更新したファイルのURL。
 @retval nil エラーが生じた場合。
 */
- (NSURL *)updateFileForPath:(NSString *)path newPath:(NSString *)newPath newContents:(NSData *)newContents;

/*!
 @brief デバイスプラグイン専用ディレクトリ以下のファイルを削除する。
 
 @param path 削除したいファイルのパス
 
 @retval YES 削除に成功
 @retval NO 削除に失敗
 */
- (BOOL)removeFileForPath:(NSString *)path;

/**
 @brief 拡張子に対応するMIMEタイプを返す。
 
 拡張子にはドットを含めないこと（e.g. "txt"）。
 
 @param extension 拡張子
 
 @retval <it>non-nil</it> MIMEタイプ。
 @retval nil 見つからなかった場合。
 */
+ (NSString *) searchMimeTypeForExtension:(NSString *)extension;

/**
 @brief MIMEタイプに対応する拡張子を返す。
 
 拡張子にはドットを含まない（e.g. "txt"）。
 
 @param mimeType MIMEタイプ
 
 @retval <it>non-nil</it> 拡張子。複数あった場合は、最も短く辞書順で手前のものを返す。
 @retval nil 見つからなかった場合。
 */
+ (NSString *) searchExtensionForMimeType:(NSString *)mimeType;

/**
 @brief 任意のデータ型ファイルのMIMEタイプを返す。
 
 現状、<code>application/octet-stream</code>を返す。拡張子からMIMEタイプを推定できなかった場合、これを代わりに用いるとよい。
 
 @return MIMEタイプ「<code>application/octet-stream</code>」
 */
+ (NSString *) mimeTypeForArbitraryData;

/**
 @brief MIMEタイプに対するクエリーに該当する拡張子を返す。
 
 MIMEタイプへのクエリーは、MIMEタイプを表す文字列への部分一致となる。
 
 @param mimeTypeQuery MIMEタイプへのクエリー
 @return 該当するMIMEタイプをもつ拡張子の配列
 */
+ (NSArray *) extensionsWithMatchingMimeType:(NSString *)mimeTypeQuery;

/**
 @brief 拡張子に対するクエリーに該当するMIMEタイプを返す。
 
 拡張子へのクエリーは、拡張子を表す文字列への部分一致となる。
 
 @param extensionQuery 拡張子へのクエリー
 @return 該当する拡張子をもつMIMEタイプの配列
 */
+ (NSArray *) mimeTypesWithMatchingExtensions:(NSString *)extensionsQuery;

@end
