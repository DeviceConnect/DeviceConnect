//
//  SonyCameraRemoteApiUtil.h
//  dConnectDeviceSonyCamera
//
//  Created by 小林 伸郎 on 2014/06/25.
//  Copyright (c) 2014年 小林 伸郎. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "HttpAsynchronousRequest.h"
#import "SampleDeviceDiscovery.h"
#import "SampleCameraEventObserver.h"
#import "SampleLiveviewManager.h"

/*! @brief Sonyカメラの撮影状態.
 */
extern NSString *const SonyCameraStatusMovieRecording;

/*! @brief Sonyカメラのアイドル状態.
 */
extern NSString *const SonyCameraStatusIdle;

/*! @brief 動画撮影モード.
 */
extern NSString *const SonyCameraShootModeMovie;

/*! @brief 静止画撮影モード.
 */
extern NSString *const SonyCameraShootModePicture;


/*!
 @brief SonyCameraRemoteApiUtilのデリゲート。
 */
@protocol SonyCameraRemoteApiUtilDelegate <NSObject>

/*!
 @brief 画像が撮影されたときのイベント。
 @param[in] imageUrl 画像へのURL
 */
- (void) didReceivedImage:(NSString *)imageUrl;

@end



/*! @brief Sony Remote APIに簡易的にアクセスするためのクラス.
 */
@interface SonyCameraRemoteApiUtil : NSObject <HttpAsynchronousRequestParserDelegate, SampleEventObserverDelegate>

/*!
 @brief デリゲート。
 retainされない。
 */
@property (nonatomic, weak) id<SonyCameraRemoteApiUtilDelegate> delegate;

/*!
 @brief カメラの撮影状態.
 */
@property (nonatomic) NSString *cameraStatus;

/*! @brief カメラの撮影モード.
 */
@property (nonatomic) NSString *shootMode;

/*!
 @brief ズームポジション.
 */
@property (nonatomic) double zoomPosition;

/*! @brief APIリストを取得する.
 */
- (void) actGetApiList;

/*!
 @brief 指定されたapiが有効かチェックする。
 @param[in] api チェックするapi
 @retval YES 有効の場合
 @retval NO 無効の場合
 */
- (BOOL) isApiAvailable:(NSString *)api;

/*!
 @brief プレビューを開始する。
 @param[in] delegate デリゲート
 */
- (BOOL) actStartLiveView:(id<SampleLiveviewDelegate>) delegate;

/*!
 @brief プレビューを停止する。
 */
- (BOOL) actStopLiveView;

/*!
 @brief プレビューの状態を取得する。
 @retval YES プレビュー再生中
 @retval NO プレビュー停止中
 */
- (BOOL) isStartedLiveView;

/**
 @brief 写真撮影を行い、結果を返却する.
 
 タイムアウトが発生した場合にはnilを返却する。
 
 @return 撮影結果
 */
- (NSDictionary *) actTakePicture;

/*!
 @brief 撮影モードを切り替える.
 @param[in] mode 撮影モード
 @return モード切り替え結果
 */
- (BOOL) actSetShootMode:(NSString *)mode;

/*!
 @brief 動画撮影開始。
 @retval SonyCameraからのレスポンス
 @retval nil タイムアウト
 */
- (NSDictionary *) startMovieRec;

/*!
 @brief 動画撮影停止。
 @retval SonyCameraからのレスポンス
 @retval nil タイムアウト
 */
- (NSDictionary *) stopMovieRec;

/*!
 @brief ズーム要求を行う。
 @param[in] direction ズームの方向(in, out)
 @param[in] movement ズームのタイプ(start, stop, 1shot)
 @retval SonyCameraからのレスポンス
 @retval nil タイムアウト
 */
- (NSDictionary *) actZoom:(NSString *)direction movement:(NSString *)movement;

/*!
 @brief 静止画のサイズを取得する。
 @retval SonyCameraからのレスポンス
 @retval nil タイムアウト
 */
- (NSDictionary *) getStillSize;

- (BOOL) setDate:(NSString *)date;

@end
