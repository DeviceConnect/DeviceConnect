//
//  DPHostMediaContext.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <AssetsLibrary/AssetsLibrary.h>
#import <AVFoundation/AVFoundation.h>
#import <MediaPlayer/MediaPlayer.h>

#import <DConnectSDK/DConnectSDK.h>

extern NSString *MediaContextMediaIdSchemeIPodAudio; ///< iPodライブラリの音声メディアのmediaIdに使用されるスキーム
extern NSString *MediaContextMediaIdSchemeIPodMovie; ///< iPodライブラリの動画メディアのmediaIdに使用されるスキーム
extern NSString *MediaContextMediaIdSchemeAssetsLibrary;
extern NSString *MediaContextMediaIdSchemeIPodLibrary;

typedef NS_ENUM(NSUInteger, MediaPlayerType) {
    MediaPlayerTypeIPod,     ///< iPodミュージックプレイヤー
    MediaPlayerTypeMoviePlayer, ///< MoviePlayer + 独自UI
};

/// iPodライブラリで再生対象とするメディアのタイプ
extern const MPMediaType TargetMPMediaType;

@interface DPHostMediaContext : NSObject

@property AVAsset *media; ///< AVAssetオブジェクト

@property BOOL isAudio; ///< オーディオメディアかどうか
@property BOOL useIPodPlayer; ///< iPodプレイヤーで再生させるかどうか

@property NSString *mediaId; ///< メディアを識別するID（URL）
@property NSString *mimeType; ///< メディアのMIMEタイプ。
@property NSString *title; ///< タイトル名。楽曲名や動画名等。
/**
 @brief タイプ名
 
 タイトルの種別で、「Music」（楽曲名）、「Movie」（動画名）など。
 */
@property NSString *type;
@property NSString *language; ///< 言語。BCP47で定義されている文字列を設定する。(例)「ja」「en-us」
@property NSString *desc; ///< 内容についての説明。
@property NSURL *imageUri; ///< アルバムカバーや動画サムネイルなどの画像へのURI。
@property NSNumber *duration; ///< メディアの長さ
@property DConnectArray *creators; ///< 制作者情報
@property DConnectArray *keywords; ///< キーワード
@property DConnectArray *genres; ///< ジャンル

/*!
 @brief メディアID（URL）からiPodライブラリ項目のPersisten IDを取得する
 @param[in] mediaIdURL メディアID（URL）
 */
+ (NSNumber *) persistentIdWithMediaIdURL:(NSURL *)mediaIdURL;

/**
 @brief URLで参照されるのメディアコンテキストを返却する
 
 参照として考えられるのはiPodライブラリやカメラロールの項目のURL。
 
 @param url URL
 @return メディアコンテキスト
 */
+ (instancetype)contextWithURL:(NSURL *)url;

/**
 @brief カメラロール等のアセットのメディアコンテキストを返却する
 
 動画以外のアセットはメディアコンテキストとして生成
 
 @param asset アセット
 @return メディアコンテキスト
 */
+ (instancetype)contextWithAsset:(ALAsset *)asset;

/**
 @brief iTunesライブラリ項目のメディアコンテキストを返却する
 @param mediaItem iTunesライブラリ項目
 @param メディアコンテキスト
 */
+ (instancetype)contextWithMediaItem:(MPMediaItem *)mediaItem;

/**
 @brief メッセージにメディアコンテキストの各種メタ情報を設定する。
 @param[in,out] message 各種メタ情報を格納するメッセージ
 @param[in] omitMediaId メディアIDをメッセージに設定しない場合は<code>YES</code>、そうでなければ<code>NO</code>
 */
- (void) setVariousMetadataToMessage:(DConnectMessage *)message omitMediaId:(BOOL)omitMediaId;

@end
