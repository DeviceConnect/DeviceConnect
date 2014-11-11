//
//  DPHostMediaPlayerProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

// MARK: ファイルシステムやWeb上のメディアコンテンツも参照できる様にする？
// MARK: UIScreenの+screensを使って、別ディスプレイで動画表示できると良いかも？

#import <ImageIO/CGImageProperties.h>
#import <AssetsLibrary/AssetsLibrary.h>
#import <AVFoundation/AVFoundation.h>
#import <MediaPlayer/MediaPlayer.h>

#import <DConnectSDK/DConnectSDK.h>

#import "DPHostDevicePlugin.h"
#import "DPHostMediaPlayerProfile.h"
#import "DPHostNetworkServiceDiscoveryProfile.h"
#import "DPHostMediaContext.h"
#import "DPHostReachability.h"
#import "DPHostUtils.h"

@interface DPHostMediaPlayerProfile()

/// @brief イベントマネージャ
@property DConnectEventManager *eventMgr;

@property MediaPlayerType currentMediaPlayer; ///< 現在使われているメディアプレイヤー

/// 動画再生用ビューコントローラー
@property MPMoviePlayerViewController *viewController;
/// iPodプレイヤー
@property MPMusicPlayerController *musicPlayer;
/// iPodプレイヤーのクエリー
@property MPMediaQuery *defaultMediaQuery;

/// アセットライブラリ検索用のロック
@property NSObject *lockAssetsLibraryQuerying;
/// iPodライブラリ検索用のロック
@property NSObject *lockIPodLibraryQuerying;

-(void) nowPlayingItemChangedInIPod:(NSNotification *)notification;
- (void) nowPlayingItemChangedInMoviePlayer:(NSNotification *)notification;
-(void) playbackStateChangedInIPod:(NSNotification *)notification;
- (void) playbackStateChangedInMoviePlayer:(NSNotification *)notification;

/**
 @brief アセットライブラリ（カメラロール等）にあるアセット群からメディアコンテキスト群を生成する。
 @param query 文字列クエリー
 @param mimeType MIMEタイプに対するクエリー
 @return カメラロールなどのアセットライブラリにあるメディア群
 */
- (NSArray *)contextsBySearchingAssetsLibraryWithQuery:(NSString *)query
                                              mimeType:(NSString *)mimeType;

/**
 @brief iPodライブラリにあるメディア群からメディアコンテキスト群を生成する。
 @param query 文字列クエリー
 @param mimeType MIMEタイプに対するクエリー
 @return iPodライブラリにあるメディア群
 */
- (NSArray *)contextsBySearchingIPodLibraryWithQuery:(NSString *)query
                                            mimeType:(NSString *)mimeType;

- (MPMoviePlayerViewController *)viewControllerWithURL:(NSURL *)url;

/**
 @brief ムービープレイヤー（MPMoviePlayerViewController）が表示されている場合はYESを返却する。
 @return MPMoviePlayerViewControllerが表示されているかどうか。
 */
- (BOOL)moviePlayerViewControllerIsPresented;

/**
 MPMoviePlayerPlaybackDidFinishNotification通知の際に呼び出されるセレクター。
 @param[in] notification 受け取った通知
 */
- (void) videoFinished:(NSNotification*)notification;

@end

@implementation DPHostMediaPlayerProfile

- (instancetype)init
{
    self = [super init];
    if (self) {
        self.delegate = self;
        
        // イベントマネージャを取得
        self.eventMgr = [DConnectEventManager sharedManagerForClass:[DPHostDevicePlugin class]];
        
        // iPodプレイヤーを取得
        self.musicPlayer = [MPMusicPlayerController iPodMusicPlayer];
        self.musicPlayer.shuffleMode = MPMusicShuffleModeOff;
        self.defaultMediaQuery = [MPMediaQuery new];
        [self.defaultMediaQuery addFilterPredicate:
         [MPMediaPropertyPredicate predicateWithValue:[NSNumber numberWithInteger:TargetMPMediaType]
                                          forProperty:MPMediaItemPropertyMediaType]];
        
        NSNotificationCenter *notificationCenter = [NSNotificationCenter defaultCenter];
        // 現在再生中の曲が変わった時の通知
        [notificationCenter addObserver:self
                               selector:@selector(nowPlayingItemChangedInIPod:)
                                   name:MPMusicPlayerControllerNowPlayingItemDidChangeNotification
                                 object:_musicPlayer];
        // 再生状況が変わった時の通知
        [notificationCenter addObserver:self
                               selector:@selector(playbackStateChangedInIPod:)
                                   name:MPMusicPlayerControllerPlaybackStateDidChangeNotification
                                 object:_musicPlayer];
        
        // 通知開始
        [_musicPlayer beginGeneratingPlaybackNotifications];
        
        // 初期はiPodミュージックプレイヤーを設定しておく。
        _currentMediaPlayer = MediaPlayerTypeIPod;
    }
    return self;
}

- (void)dealloc
{
	// iTunes関連の通知の削除
    [[NSNotificationCenter defaultCenter] removeObserver:self];
	// 通知終了
	[_musicPlayer endGeneratingPlaybackNotifications];
}

// 現在再生中の曲が変わった時の通知
-(void) nowPlayingItemChangedInIPod:(NSNotification *)notification
{
    // イベントの取得
    NSArray *evts = [_eventMgr eventListForDeviceId:NetworkDiscoveryDeviceId
                                            profile:DConnectMediaPlayerProfileName
                                          attribute:DConnectMediaPlayerProfileAttrOnStatusChange];
    
    DConnectMessage *mediaPlayer = [DConnectMessage message];
    
    // 再生コンテンツ変更
    NSString *status;
    MPMediaItem *mediaItem = _musicPlayer.nowPlayingItem;
    if (_musicPlayer.playbackState == MPMusicPlaybackStateStopped
        && mediaItem) {
        status = DConnectMediaPlayerProfileStatusMedia;
    } else {
        status = DConnectMediaPlayerProfileStatusStop;
    }
    [DConnectMediaPlayerProfile setStatus:status target:mediaPlayer];
    if (mediaItem) {
        DPHostMediaContext *mediaCtx = [DPHostMediaContext contextWithMediaItem:mediaItem];
        if (mediaCtx.mediaId) {
            [DConnectMediaPlayerProfile setMediaId:mediaCtx.mediaId target:mediaPlayer];
        }
        if (mediaCtx.mimeType) {
            [DConnectMediaPlayerProfile setMIMEType:mediaCtx.mimeType target:mediaPlayer];
        }
        
        [DConnectMediaPlayerProfile setPos:_musicPlayer.currentPlaybackTime target:mediaPlayer];
    }
    
    // イベント送信
    for (DConnectEvent *evt in evts) {
        DConnectMessage *eventMsg = [DConnectEventManager createEventMessageWithEvent:evt];
        
        [DConnectMediaPlayerProfile setMediaPlayer:mediaPlayer target:eventMsg];
        
        [SELF_PLUGIN sendEvent:eventMsg];
    }
}

- (void) nowPlayingItemChangedInMoviePlayer:(NSNotification *)notification
{
    // イベントの取得
    NSArray *evts = [_eventMgr eventListForDeviceId:NetworkDiscoveryDeviceId
                                            profile:DConnectMediaPlayerProfileName
                                          attribute:DConnectMediaPlayerProfileAttrOnStatusChange];
    
    DConnectMessage *mediaPlayer = [DConnectMessage message];
    
    // 再生コンテンツ変更
    NSString *status;
    NSURL *contentURL = [notification.object contentURL];
    MPMoviePlaybackState playbackState = _viewController.moviePlayer.playbackState;
    if (playbackState == MPMoviePlaybackStateStopped
        && contentURL) {
        status = DConnectMediaPlayerProfileStatusMedia;
    } else {
        status = DConnectMediaPlayerProfileStatusStop;
    }
    [DConnectMediaPlayerProfile setStatus:status target:mediaPlayer];
    if (contentURL) {
        DPHostMediaContext *mediaCtx = [DPHostMediaContext contextWithURL:contentURL];
        if (mediaCtx) {
            if (mediaCtx.mediaId) {
                [DConnectMediaPlayerProfile setMediaId:mediaCtx.mediaId target:mediaPlayer];
            }
            if (mediaCtx.mimeType) {
                [DConnectMediaPlayerProfile setMIMEType:mediaCtx.mimeType target:mediaPlayer];
            }
        }
    }
    
    // イベント送信
    for (DConnectEvent *evt in evts) {
        DConnectMessage *eventMsg = [DConnectEventManager createEventMessageWithEvent:evt];
        
        [DConnectMediaPlayerProfile setMediaPlayer:mediaPlayer target:eventMsg];
        
        [SELF_PLUGIN sendEvent:eventMsg];
    }
}

// 再生状況が変わった時の通知
-(void) playbackStateChangedInIPod:(NSNotification *)notification
{
    // イベントの取得
    NSArray *evts = [_eventMgr eventListForDeviceId:NetworkDiscoveryDeviceId
                                            profile:DConnectMediaPlayerProfileName
                                          attribute:DConnectMediaPlayerProfileAttrOnStatusChange];
    
    DConnectMessage *mediaPlayer = [DConnectMessage message];
    NSString *status;
    switch (_musicPlayer.playbackState) {
        case MPMusicPlaybackStateStopped:
            status = DConnectMediaPlayerProfileStatusStop;
            break;
        case MPMusicPlaybackStatePlaying:
            status = DConnectMediaPlayerProfileStatusPlay;
            break;
        case MPMusicPlaybackStatePaused:
            status = DConnectMediaPlayerProfileStatusPause;
            break;
        default:
            break;
    }
    [DConnectMediaPlayerProfile setStatus:status target:mediaPlayer];
    
    MPMediaItem *mediaItem = _musicPlayer.nowPlayingItem;
    if (mediaItem) {
        DPHostMediaContext *mediaCtx = [DPHostMediaContext contextWithMediaItem:mediaItem];
        if (mediaCtx.mediaId) {
            [DConnectMediaPlayerProfile setMediaId:mediaCtx.mediaId target:mediaPlayer];
        }
        if (mediaCtx.mimeType) {
            [DConnectMediaPlayerProfile setMIMEType:mediaCtx.mimeType target:mediaPlayer];
        }
        
        [DConnectMediaPlayerProfile setPos:_musicPlayer.currentPlaybackTime target:mediaPlayer];
    }
    
    // イベント送信
    for (DConnectEvent *evt in evts) {
        DConnectMessage *eventMsg = [DConnectEventManager createEventMessageWithEvent:evt];
        
        [DConnectMediaPlayerProfile setMediaPlayer:mediaPlayer target:eventMsg];
        
        [SELF_PLUGIN sendEvent:eventMsg];
    }
}

- (void) playbackStateChangedInMoviePlayer:(NSNotification *)notification
{
    // イベントの取得
    NSArray *evts = [_eventMgr eventListForDeviceId:NetworkDiscoveryDeviceId
                                            profile:DConnectMediaPlayerProfileName
                                          attribute:DConnectMediaPlayerProfileAttrOnStatusChange];
    
    DConnectMessage *mediaPlayer = [DConnectMessage message];
    NSString *status;
    MPMoviePlaybackState playbackState = _viewController.moviePlayer.playbackState;
    switch (playbackState) {
        case MPMoviePlaybackStateStopped:
            status = DConnectMediaPlayerProfileStatusStop;
            break;
        case MPMoviePlaybackStatePlaying:
            status = DConnectMediaPlayerProfileStatusPlay;
            break;
        case MPMoviePlaybackStatePaused:
            status = DConnectMediaPlayerProfileStatusPause;
            break;
        default:
            break;
    }

    [DConnectMediaPlayerProfile setStatus:status target:mediaPlayer];
    
    NSURL *contentURL = _viewController.moviePlayer.contentURL;
    if (contentURL) {
        DPHostMediaContext *mediaCtx = [DPHostMediaContext contextWithURL:contentURL];
        if (mediaCtx.mediaId) {
            [DConnectMediaPlayerProfile setMediaId:mediaCtx.mediaId target:mediaPlayer];
        }
        if (mediaCtx.mimeType) {
            [DConnectMediaPlayerProfile setMIMEType:mediaCtx.mimeType target:mediaPlayer];
        }
        
        [DConnectMediaPlayerProfile setPos:_musicPlayer.currentPlaybackTime target:mediaPlayer];
    }
    
    // イベント送信
    for (DConnectEvent *evt in evts) {
        DConnectMessage *eventMsg = [DConnectEventManager createEventMessageWithEvent:evt];
        
        [DConnectMediaPlayerProfile setMediaPlayer:mediaPlayer target:eventMsg];
        
        [SELF_PLUGIN sendEvent:eventMsg];
    }
}

- (NSArray *)contextsBySearchingAssetsLibraryWithQuery:(NSString *)query
                                              mimeType:(NSString *)mimeType
{
    NSAssert(dispatch_get_current_queue() != dispatch_get_main_queue(),
             @"%s can not be invoked from the main queue; please invoke it from the other.", __PRETTY_FUNCTION__);
    
    __block BOOL failed = NO;
    __block NSMutableArray *ctxArr = [NSMutableArray new];
    
    @synchronized(_lockAssetsLibraryQuerying) {
        // アセットライブラリへのクエリ処理を排他にする。
        
        dispatch_semaphore_t semaphore = dispatch_semaphore_create(0);
        dispatch_time_t timeout = dispatch_time(DISPATCH_TIME_NOW, NSEC_PER_SEC * 30);
        
        ALAssetsLibrary *library = [ALAssetsLibrary new];
        NSUInteger groupTypes = ALAssetsGroupAll;
        NSString *mimeTypeLowercase = mimeType.lowercaseString;
        id mainLoopBlock = ^(ALAssetsGroup *group, BOOL *stop1)
        {
            if (failed) {
                // 失敗状態になっているのなら、処理を切り上げる。
                *stop1 = YES;
                return;
            }
            
            if(group != nil) {
                [group enumerateAssetsUsingBlock:^(ALAsset *result, NSUInteger index, BOOL *stop2)
                 {
                     if (failed) {
                         // 失敗状態になっているのなら、処理を切り上げる。
                         *stop2 = YES;
                         return;
                     }
                     
                     if (result) {
                         DPHostMediaContext *ctx = [DPHostMediaContext contextWithAsset:result];
                         if (!ctx) {
                             // コンテキスト作成失敗；スキップ
                             return;
                         }
                         
                         // クエリー検索
                         if (query) {
                             // クエリーのマッチングはファイル名に対して行う。
                             NSRange result = [ctx.title rangeOfString:query];
                             if (result.location == NSNotFound && result.length == 0) {
                                 // クエリーにマッチせず；スキップ。
                                 return;
                             }
                         }
                         // MIMEタイプ検索
                         if (mimeType) {
                             NSRange result = [ctx.mimeType rangeOfString:mimeTypeLowercase];
                             if (result.location == NSNotFound && result.length == 0) {
                                 // MIMEタイプにマッチせず；スキップ。
                                 return;
                             }
                         }
                         
                         @synchronized(ctxArr) {
                             [ctxArr addObject:ctx];
                         }
                     }
                 }];
            } else {
                // group == nil ⇒ イテレーション終了
                dispatch_semaphore_signal(semaphore);
            }
        };
        id failBlock = ^(NSError *error)
        {
            failed = YES;
            return;
        };
        
        [library enumerateGroupsWithTypes:groupTypes
                               usingBlock:mainLoopBlock
                             failureBlock:failBlock];
        
        // ライブラリのクエリー（非同期）が終わる、もしくはタイムアウトするまで待つ
        long result = dispatch_semaphore_wait(semaphore, timeout);
        if (result != 0) {
            // タイムアウト
            failed = YES;
        }
    }
    
    return failed ? nil : ctxArr;
}

- (NSArray *)contextsBySearchingIPodLibraryWithQuery:(NSString *)query
                                            mimeType:(NSString *)mimeType
{
    NSMutableArray *ctxArr = [NSMutableArray new];
    
    @synchronized(_lockIPodLibraryQuerying) {
        // iTunes Media
        MPMediaPropertyPredicate *predicate =
        [MPMediaPropertyPredicate predicateWithValue:@(TargetMPMediaType)
                                         forProperty:MPMediaItemPropertyMediaType];
        MPMediaQuery *mediaQuery = [MPMediaQuery new];
        [mediaQuery addFilterPredicate:predicate];
        NSArray *items = [mediaQuery items];
        
        NSString *mimeTypeLowsercase = mimeType.lowercaseString;
        for (MPMediaItem *mediaItem in items) {
            DPHostMediaContext *ctx = [DPHostMediaContext contextWithMediaItem:mediaItem];
            if (!ctx) {
                // コンテキスト作成失敗；スキップ
                continue;
            }
            
            // クエリー検索
            if (query) {
                // クエリーのマッチングはファイル名に対して行う。
                BOOL hit = false;
                NSRange result;
                // titleとcreators.creatorでマッチングを行う。
                result = [ctx.title rangeOfString:query];
                hit = hit || result.location != NSNotFound || result.length != 0;
                
                for (int i = 0; i < [ctx.creators count]; ++i) {
                    result = [[[ctx.creators objectAtIndex:i] stringForKey:DConnectMediaPlayerProfileParamCreator] rangeOfString:query];
                    hit = hit || result.location != NSNotFound || result.length != 0;
                }
                
                if (!hit) {
                    // クエリーにマッチせず；スキップ。
                    continue;
                }
            }
            // MIMEタイプ検索
            if (mimeType) {
                NSRange result = [ctx.mimeType rangeOfString:mimeTypeLowsercase];
                if (result.location == NSNotFound && result.length == 0) {
                    // MIMEタイプにマッチせず；スキップ。
                    continue;
                }
            }
            
            @synchronized(ctxArr) {
                [ctxArr addObject:ctx];
            }
        }
    }
    
    return ctxArr.count == 0 ? nil : ctxArr;
}

- (MPMoviePlayerViewController *)viewControllerWithURL:(NSURL *)url
{
    MPMoviePlayerViewController *viewController = [[MPMoviePlayerViewController alloc] initWithContentURL:url];
    viewController.moviePlayer.shouldAutoplay = NO;
    
    // 再生完了通知をさせない様にする；MPMoviePlayerViewControllerの初期動作だと、再生完了時に閉じる。
    // 再生完了時に閉じる処理を実行させたくないので、再生完了通知を一旦消す。
    [[NSNotificationCenter defaultCenter] removeObserver:_viewController
                                                    name:MPMoviePlayerPlaybackDidFinishNotification
                                                  object:_viewController.moviePlayer];
    // 再生完了の通知；独自の再生完了時に処理を行わせる。
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(videoFinished:)
                                                 name:MPMoviePlayerPlaybackDidFinishNotification
                                               object:viewController.moviePlayer];
    // 再生項目変更の通知
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(nowPlayingItemChangedInMoviePlayer:)
                                                 name:MPMoviePlayerNowPlayingMovieDidChangeNotification
                                               object:viewController.moviePlayer];
    // 再生状態変更の通知
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(playbackStateChangedInMoviePlayer:)
                                                 name:MPMoviePlayerPlaybackStateDidChangeNotification
                                               object:viewController.moviePlayer];
    
    return viewController;
}

- (BOOL)moviePlayerViewControllerIsPresented
{
    UIViewController *rootView = [UIApplication sharedApplication].keyWindow.rootViewController;
    return [rootView.presentedViewController class] == [MPMoviePlayerViewController class];
}

- (void) videoFinished:(NSNotification*)notification
{

    int value = [[notification.userInfo valueForKey:MPMoviePlayerPlaybackDidFinishReasonUserInfoKey] intValue];
    if (value == MPMovieFinishReasonUserExited) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [_viewController.moviePlayer stop];
            [_viewController dismissMoviePlayerViewControllerAnimated];
            [self nowPlayingItemChangedInMoviePlayer:notification];
            
            // 一度閉じたら次回動画再生時には別のMPMoviePlayerViewControllerインスタンスを使うので、オブザーバーを削除しておく。
            [[NSNotificationCenter defaultCenter] removeObserver:self];
        });
    }
}

#pragma mark - Get Methods

- (BOOL)               profile:(DConnectMediaPlayerProfile *)profile
didReceiveGetPlayStatusRequest:(DConnectRequestMessage *)request
                      response:(DConnectResponseMessage *)response
                      deviceId:(NSString *)deviceId
{
    NSString *status;
    switch (_currentMediaPlayer) {
        case MediaPlayerTypeMoviePlayer:
            // MoviePlayer
            switch (_viewController.moviePlayer.playbackState) {
                case MPMoviePlaybackStateStopped:
                    status = DConnectMediaPlayerProfileStatusStop;
                    break;
                case MPMoviePlaybackStatePlaying:
                    status = DConnectMediaPlayerProfileStatusPlay;
                    break;
                case MPMoviePlaybackStatePaused:
                    status = DConnectMediaPlayerProfileStatusPause;
                    break;
                default:
                    break;
            }
            break;
        case MediaPlayerTypeIPod:
            // iPodミュージックプレイヤー
            switch (_musicPlayer.playbackState) {
                case MPMusicPlaybackStateStopped:
                    status = DConnectMediaPlayerProfileStatusStop;
                    break;
                case MPMusicPlaybackStatePlaying:
                    status = DConnectMediaPlayerProfileStatusPlay;
                    break;
                case MPMusicPlaybackStatePaused:
                    status = DConnectMediaPlayerProfileStatusPause;
                    break;
                default:
                    break;
            }
            break;
            
        default:
            [response setErrorToUnknownWithMessage:@"Unknown player type; this must be a bug."];
            return YES;
    }
    
    if (status) {
        [DConnectMediaPlayerProfile setStatus:status target:response];
        [response setResult:DConnectMessageResultTypeOk];
    } else {
        [response setErrorToUnknownWithMessage:@"Status is unknown."];
    }
    return YES;
}

- (BOOL)          profile:(DConnectMediaPlayerProfile *)profile
didReceiveGetMediaRequest:(DConnectRequestMessage *)request
                 response:(DConnectResponseMessage *)response
                 deviceId:(NSString *)deviceId
                  mediaId:(NSString *)mediaId
{
    if (!mediaId) {
        [response setErrorToInvalidRequestParameterWithMessage:@"mediaId must be specified."];
        return YES;
    }
    
    NSURL *url = [NSURL URLWithString:mediaId];
    DPHostMediaContext *ctx = [DPHostMediaContext contextWithURL:url];
    if (ctx) {
        [ctx setVariousMetadataToMessage:response omitMediaId:YES];
        [response setResult:DConnectMessageResultTypeOk];
    } else {
        [response setErrorToUnknownWithMessage:@"Failed to obtain a media context."];
    }
    
    return YES;
}

- (BOOL)              profile:(DConnectMediaPlayerProfile *)profile
didReceiveGetMediaListRequest:(DConnectRequestMessage *)request
                     response:(DConnectResponseMessage *)response
                     deviceId:(NSString *)deviceId
                        query:(NSString *)query
                     mimeType:(NSString *)mimeType
                        order:(NSArray *)order
                       offset:(NSNumber *)offset
                        limit:(NSNumber *)limit
{
    NSString *sortTarget;
    NSString *sortOrder;
    if (order) {
        sortTarget = order[0];
        sortOrder = order[1];
        
        if (!sortTarget || !sortOrder) {
            [response setErrorToInvalidRequestParameterWithMessage:@"order is invalid."];
            return YES;
        }
    } else {
        sortTarget = DConnectMediaPlayerProfileParamTitle;
        sortOrder = DConnectMediaPlayerProfileOrderASC;
    }
    
    // ソート対象のNSStringもしくはNSNumberを返却するブロックを用意する。
    id (^accessor)(id);
    NSComparisonResult (^innerComp)(id, id);
    if ([sortTarget isEqualToString:DConnectMediaPlayerProfileParamMediaId]) {
        accessor = ^id(id obj) {
            return [(DPHostMediaContext *)obj mediaId];
        };
        innerComp = ^NSComparisonResult(id obj1, id obj2) {
            return [obj1 localizedCaseInsensitiveCompare: obj2];
        };
    } else if ([sortTarget isEqualToString:DConnectMediaPlayerProfileParamMIMEType]) {
        accessor = ^id(id obj) {
            return [(DPHostMediaContext *)obj mimeType];
        };
        innerComp = ^NSComparisonResult(id obj1, id obj2) {
            return [obj1 localizedCaseInsensitiveCompare: obj2];
        };
    } else if ([sortTarget isEqualToString:DConnectMediaPlayerProfileParamTitle]) {
        accessor = ^id(id obj) {
            return [(DPHostMediaContext *)obj title];
        };
        innerComp = ^NSComparisonResult(id obj1, id obj2) {
            return [obj1 localizedCaseInsensitiveCompare: obj2];
        };
    } else if ([sortTarget isEqualToString:DConnectMediaPlayerProfileParamType]) {
        accessor = ^id(id obj) {
            return [(DPHostMediaContext *)obj type];
        };
        innerComp = ^NSComparisonResult(id obj1, id obj2) {
            return [obj1 localizedCaseInsensitiveCompare: obj2];
        };
    } else if ([sortTarget isEqualToString:DConnectMediaPlayerProfileParamLanguage]) {
        accessor = ^id(id obj) {
            return [(DPHostMediaContext *)obj language];
        };
        innerComp = ^NSComparisonResult(id obj1, id obj2) {
            return [obj1 localizedCaseInsensitiveCompare: obj2];
        };
    } else if ([sortTarget isEqualToString:DConnectMediaPlayerProfileParamDescription]) {
        accessor = ^id(id obj) {
            return [(DPHostMediaContext *)obj description];
        };
        innerComp = ^NSComparisonResult(id obj1, id obj2) {
            return [obj1 localizedCaseInsensitiveCompare: obj2];
        };
    } else if ([sortTarget isEqualToString:DConnectMediaPlayerProfileParamDuration]) {
        accessor = ^id(id obj) {
            return [(DPHostMediaContext *)obj duration];
        };
        innerComp = ^NSComparisonResult(id obj1, id obj2) {
            return [obj1 compare: obj2];
        };
    } else if ([sortTarget isEqualToString:DConnectMediaPlayerProfileParamImageURI]) {
        accessor = ^id(id obj) {
            return[(DPHostMediaContext *)obj imageUri].absoluteString;
        };
        innerComp = ^NSComparisonResult(id obj1, id obj2) {
            return [obj1 localizedCaseInsensitiveCompare: obj2];
        };
    } else {
        [response setErrorToInvalidRequestParameterWithMessage:@"order is invalid."];
        return YES;
    }
    
    NSComparator comp;
    if ([sortOrder isEqualToString:DConnectMediaPlayerProfileOrderASC]) {
        comp = ^NSComparisonResult(id obj1, id obj2) {
            id obj1Tmp = accessor(obj1);
            id obj2Tmp = accessor(obj2);
            return innerComp(obj1Tmp, obj2Tmp);
        };
    } else if ([sortOrder isEqualToString:DConnectMediaPlayerProfileOrderDESC]) {
        comp = ^NSComparisonResult(id obj1, id obj2) {
            id obj1Tmp = accessor(obj1);
            id obj2Tmp = accessor(obj2);
            return innerComp(obj2Tmp, obj1Tmp);
        };
    } else {
        [response setErrorToInvalidRequestParameterWithMessage:@"order is invalid."];
        return YES;
    }
    
    if (offset) {
        if (offset.integerValue < 0) {
            [response setErrorToInvalidRequestParameterWithMessage:@"offset must be a non-negative value."];
            return YES;
        }
    }
    
    if (limit) {
        if (limit.integerValue <= 0) {
            [response setErrorToInvalidRequestParameterWithMessage:@"limit must be a positive value."];
            return YES;
        }
    }
    
    // アセットライブラリおよびiPodライブラリからメディアコンテキスト群を取得する。
    // TODO: 可能であれば、メディアコンテキスト群作成は1度だけにして、更新の度にメディアコンテキスト群も更新する様にする。
    // カメラロールやiPodライブラリのメディアリストが更新されたのを検知するiOS SDK APIがあるのなら、
    // それを契機にメディアコンテキスト群の更新を行う。
    NSMutableArray *ctxArr = [NSMutableArray array];
    [ctxArr addObjectsFromArray:[self contextsBySearchingAssetsLibraryWithQuery:query mimeType:mimeType]];
    [ctxArr addObjectsFromArray:[self contextsBySearchingIPodLibraryWithQuery:query mimeType:mimeType]];
    
    if (offset) {
        if (offset.integerValue >= ctxArr.count) {
            [response setErrorToInvalidRequestParameterWithMessage:@"offset exceeds the size of the media list."];
            return YES;
        }
    }
    
    // 並び替えを実行
    NSArray *tmpArr = [ctxArr sortedArrayUsingComparator:comp];
    
    // ページングのために配列の一部分だけ抜き出し
    if (offset || limit) {
        NSUInteger offsetVal = offset ? offset.unsignedIntegerValue : 0;
        NSUInteger limitVal = limit ? limit.unsignedIntegerValue : ctxArr.count;
        tmpArr = [tmpArr subarrayWithRange:
                  NSMakeRange(offset.unsignedIntegerValue,
                              MIN(ctxArr.count - offsetVal, limitVal))];
    }
    
    [DConnectMediaPlayerProfile setCount:(int)tmpArr.count target:response];
    DConnectArray *media = [DConnectArray array];
    for (DPHostMediaContext *ctx in tmpArr) {
        DConnectMessage *medium = [DConnectMessage message];
        [ctx setVariousMetadataToMessage:medium omitMediaId:NO];
        [media addMessage:medium];
    }
    [DConnectMediaPlayerProfile setMedia:media target:response];
    
    [response setResult:DConnectMessageResultTypeOk];
    
    return YES;
}

- (BOOL)         profile:(DConnectMediaPlayerProfile *)profile
didReceiveGetSeekRequest:(DConnectRequestMessage *)request
                response:(DConnectResponseMessage *)response
                deviceId:(NSString *)deviceId
{
    __block NSTimeInterval pos;
    void(^block)(void) = nil;
    if (_currentMediaPlayer == MediaPlayerTypeIPod) {
        block = ^{
            pos = _musicPlayer.currentPlaybackTime;
        };
    } else if (_currentMediaPlayer == MediaPlayerTypeMoviePlayer) {
        if (![self moviePlayerViewControllerIsPresented]) {
            [response setErrorToUnknownWithMessage:@"Movie player view controller is not presented; please perform Media PUT API first to present the view controller."];
            return YES;
        } else {
            block = ^{
                pos = _viewController.moviePlayer.playableDuration;
            };
        }
    } else {
        [response setErrorToUnknownWithMessage:@"Unknown player type; this must be a bug."];
        return YES;
    }
    
    if ([NSThread isMainThread]) {
        block();
    } else {
        dispatch_sync(dispatch_get_main_queue(), block);
    }
    
    [DConnectMediaPlayerProfile setPos:pos target:response];
    [response setResult:DConnectMessageResultTypeOk];
    
    return YES;
}

#pragma mark - Put Methods

- (BOOL)          profile:(DConnectMediaPlayerProfile *)profile
didReceivePutMediaRequest:(DConnectRequestMessage *)request
                 response:(DConnectResponseMessage *)response
                 deviceId:(NSString *)deviceId
                  mediaId:(NSString *)mediaId
{
    if (!mediaId) {
        [response setErrorToInvalidRequestParameterWithMessage:@"mediaId must be specified."];
        return YES;
    }
    
    NSURL *url = [NSURL URLWithString:mediaId];
    // 事前にメディアコンテキストを取得＆キャッシュしておく;
    // MPMoviePlayerControllerでAssetsLibrary項目を再生項目に設定した瞬間、再生項目変更イベントにより
    // メディア情報の取得が始まり、それによってMPMoviePlayerControllerが固まる（再生準備と項目情報取得の処理がかち合う）。
    // それを避ける為、再生項目変更前にDPHostMediaContextのキャッシュを完了せておき、再生項目変更イベント発生時には
    // 事前にコンテキストがキャッシュされている状況を作っておく。そうすれば、再生準備に項目情報取得の処理が走る事も無い筈。
    [DPHostMediaContext contextWithURL:url];
    
    NSNumber *persistentId;
    MPMediaItem *mediaItem;
    BOOL isIPodAudioMedia = [url.scheme isEqualToString:MediaContextMediaIdSchemeIPodAudio];
    BOOL isIPodMovieMedia = [url.scheme isEqualToString:MediaContextMediaIdSchemeIPodMovie];
    if (isIPodAudioMedia || isIPodMovieMedia) {
        // iTunesの曲の場合はMPMediaItemを用意する。
        
        persistentId = [DPHostMediaContext persistentIdWithMediaIdURL:url];
        
        MPMediaQuery *mediaQuery = _defaultMediaQuery.copy;
        [mediaQuery addFilterPredicate:
         [MPMediaPropertyPredicate predicateWithValue:persistentId
                                          forProperty:MPMediaItemPropertyPersistentID]];
        NSArray *items = [mediaQuery items];
        
        if (items.count == 0) {
            [response setErrorToInvalidRequestParameterWithMessage:@"Media specified by mediaId does not found."];
            return YES;
        } else {
            mediaItem = items[0];
        }
    }
    
    if (isIPodAudioMedia) {
        // iPodライブラリの音声メディアはiPodミュージックプレイヤーを使う。
        void(^block)(void) = ^{
            // nowPlayingItemは、現在指定されているプレイリストキューの中に含まれているメディア項目にしか変更できない。
            // 本プロファイル以外の介入でプレイリストキューが新たに指定したいメディア項目を含まない物に変更された場合、
            // nowPlayingItemを変更できない。なので毎回、指定したいメディア項目を含むプレイリストキューを指定し直す。
            [_defaultMediaQuery items];
            [_musicPlayer setQueueWithQuery:_defaultMediaQuery];
            _musicPlayer.nowPlayingItem = mediaItem;
        };
        if ([NSThread isMainThread]) {
            block();
        } else {
            dispatch_sync(dispatch_get_main_queue(), block);
        }
        
        if ([_musicPlayer.nowPlayingItem isEqual:mediaItem]) {
            [response setResult:DConnectMessageResultTypeOk];
            
            if (_currentMediaPlayer == MediaPlayerTypeMoviePlayer) {
                // MoviePlayerが表示されている場合は、再生を一時停止し閉じる。
                dispatch_async(dispatch_get_main_queue(), ^{
                    [_viewController.moviePlayer pause];
                    [_viewController dismissMoviePlayerViewControllerAnimated];
                    //                    [_viewController pause:nil];
                    //                    [_viewController dismiss:nil];
                });
            }
        } else {
            [response setErrorToUnknownWithMessage:@"Failed to change the playing media item."];
        }
        
        _currentMediaPlayer = MediaPlayerTypeIPod;
        
        return YES;
    } else {
        // TODO: ひょっとすると動画はMPMoviePlayerControllerで再生させる方が良いかもしれない。UIが用意されているし。
        
        if (isIPodMovieMedia) {
            NSNumber *isCloudItem = [mediaItem valueForProperty:MPMediaItemPropertyIsCloudItem];
            if (isCloudItem) {
                if ([isCloudItem boolValue]) {
                    // iCloud上の動画項目（つまりiOSデバイス側にまだダウンロードされていない）の場合は動画プレイヤーで再生できない。
                    // まずダウンロードしてもらうしか無い。
                    [response setErrorToUnknownWithMessage:
                     @"Media item specified is an iTunes movie item, and it must be downloaded into the iOS device before playing."];
                    return YES;
                }
            }
            
            // iPodライブラリの動画メディアはMoviePlayerを使う。
            // MoviePlayerではメディアのURLが必要なので、MPMediaItemからAssetURLを取得する。
            url = [mediaItem valueForProperty:MPMediaItemPropertyAssetURL];
            if (!url) {
                // iOSデバイスにダウンロード済みのメディアなのにAssetURLが無い；確証はないが保護されたメディア項目かもしれない。
                // その旨をエラーで返却する。
                [response setErrorToUnknownWithMessage:
                 @"Failed to pass the specified media item to the movie player; perhaps this media item is a protected media only playable in the official apps like \"Music\" and \"Videos\"."];
                return YES;
            }
        }
        
        // AVAssetの機能を使って、メディアが保護されているかどうかを調べて、保護されていなければ再生する；
        // 保護されたメディアを再生しようとすると再生に失敗する。
        AVURLAsset *asset = [AVURLAsset URLAssetWithURL:url options:nil];
        [asset loadValuesAsynchronouslyForKeys:@[@"hasProtectedContent", @"playable"] completionHandler:
         ^{
             NSError *error = nil;
             
             AVKeyValueStatus keyStatus;
             keyStatus = [asset statusOfValueForKey:@"playable" error:&error];
             if (keyStatus == AVKeyValueStatusFailed || error)
             {
                 [response setErrorToUnknownWithMessage:
                  @"Operation aborted; Failed to determine whether the specified media item is protected or not."];
                 [[DConnectManager sharedManager] sendResponse:response];
                 return;
             }
             
             if (!asset.playable) {
                 // 再生できない
                 [response setErrorToUnknownWithMessage:@"Media item is not playable."];
                 [[DConnectManager sharedManager] sendResponse:response];
                 return;
             }
             
             keyStatus = [asset statusOfValueForKey:@"hasProtectedContent" error:&error];
             if (keyStatus == AVKeyValueStatusFailed || error)
             {
                 [response setErrorToUnknownWithMessage:
                  @"Operation aborted; Failed to determine whether the specified media item is protected or not."];
                 [[DConnectManager sharedManager] sendResponse:response];
                 return;
             }
             
             if (asset.hasProtectedContent) {
                 // 保護コンテンツを持っている；再生できない
                 [response setErrorToUnknownWithMessage:
                  @"Media item specified is an iTunes movie item and is protected; protected movie media items are playable only in the official player apps like Music and Videos."];
                 [[DConnectManager sharedManager] sendResponse:response];
                 return;
             }
             
             if (_currentMediaPlayer == MediaPlayerTypeIPod &&
                 _musicPlayer.playbackState == MPMusicPlaybackStatePlaying) {
                 // iPodミュージックプレイヤーが再生されている場合は、再生を一時停止する。
                 dispatch_async(dispatch_get_main_queue(), ^{
                     [_musicPlayer pause];
                 });
             }
             
             void(^block)(void) = ^{
                 if ([(_viewController.moviePlayer.contentURL = url) isEqual:url]) {
                     // 初回より後のcontentURL変更で動画プレイヤーが黒い画面になって反応しなくなる問題への対応策
                     // http://stackoverflow.com/questions/10924930/set-new-contenturl-for-mpmovieplayercontroller
                     _viewController.moviePlayer.movieSourceType = MPMovieSourceTypeFile;
                     // 再生項目変更は、2度目以降ではprepareToPlayしないとダメ。
                     [_viewController.moviePlayer prepareToPlay];
                     [response setResult:DConnectMessageResultTypeOk];
                     _currentMediaPlayer = MediaPlayerTypeMoviePlayer;
                 } else {
                     [response setErrorToUnknownWithMessage:@"Failed to change the playing media item."];
                 }
                 [[DConnectManager sharedManager] sendResponse:response];
             };
             if ([self moviePlayerViewControllerIsPresented]) {
                 block();
             }
             else {
                 UIViewController *rootView = [UIApplication sharedApplication].keyWindow.rootViewController;
                 dispatch_async(dispatch_get_main_queue(), ^{
                     self.viewController = [self viewControllerWithURL:nil];
                     block();
                     [rootView presentMoviePlayerViewControllerAnimated:_viewController];
                 });
             }
         }];
        return NO;
    }
}

- (BOOL)         profile:(DConnectMediaPlayerProfile *)profile
didReceivePutPlayRequest:(DConnectRequestMessage *)request
                response:(DConnectResponseMessage *)response
                deviceId:(NSString *)deviceId
{
    if (_currentMediaPlayer == MediaPlayerTypeIPod) {
        if ([_musicPlayer playbackState] != MPMusicPlaybackStatePlaying) {
            MPMediaItem *mediaItem = _musicPlayer.nowPlayingItem;
            if (mediaItem) {
                NSNumber *isCloudItem = [mediaItem valueForProperty:MPMediaItemPropertyIsCloudItem];
                if (isCloudItem) {
                    DPHostReachability *networkReachability = [DPHostReachability reachabilityForInternetConnection];
                    NetworkStatus networkStatus = [networkReachability currentReachabilityStatus];
                    if ([isCloudItem boolValue] && networkStatus == NotReachable) {
                        // iCloud上の音楽項目（つまりiOSデバイス側にまだダウンロードされていない）で、尚かつインターネット接続が無い場合は
                        // 再生できない。
                        [response setErrorToUnknownWithMessage:
                         @"Internet is not reachable; the specified audio media item is an iClould item and its playback requires an Internet connection."];
                        return YES;
                    }
                }
                
                void(^block)(void) = ^{
                    [_musicPlayer play];
                };
                if ([NSThread isMainThread]) {
                    block();
                } else {
                    dispatch_sync(dispatch_get_main_queue(), block);
                }
                [response setResult:DConnectMessageResultTypeOk];
            } else {
                [response setErrorToUnknownWithMessage:@"Media cannot be played; media is not specified."];
            }
        } else {
            [response setErrorToUnknownWithMessage:@"Media cannot be played; it is already playing."];
        }
    } else if (_currentMediaPlayer == MediaPlayerTypeMoviePlayer) {
        if (![self moviePlayerViewControllerIsPresented]) {
            [response setErrorToUnknownWithMessage:@"Movie player view controller is not presented; please perform Media PUT API first to present the view controller."];
        } else {
            if (_viewController.moviePlayer.playbackState != MPMoviePlaybackStatePlaying) {
                if (_viewController.moviePlayer.contentURL) {
                    void(^block)(void) = ^{
                        //                        _viewController.moviePlayer.movieSourceType = MPMovieSourceTypeUnknown;
                        [_viewController.moviePlayer play];
                        [response setResult:DConnectMessageResultTypeOk];
                        
                        [[DConnectManager sharedManager] sendResponse:response];
                    };
                    if ([NSThread isMainThread]) {
                        block();
                    } else {
                        dispatch_sync(dispatch_get_main_queue(), block);
                    }
                    return NO;
                } else {
                    [response setErrorToUnknownWithMessage:@"Media cannot be played; media is not specified."];
                }
            } else {
                [response setErrorToUnknownWithMessage:@"Media cannot be played; it is already playing."];
            }
        }
    } else {
        [response setErrorToUnknownWithMessage:@"Unknown player type; this must be a bug."];
    }
    
    return YES;
}

- (BOOL)         profile:(DConnectMediaPlayerProfile *)profile
didReceivePutStopRequest:(DConnectRequestMessage *)request
                response:(DConnectResponseMessage *)response
                deviceId:(NSString *)deviceId
{
    void(^block)(void) = nil;
    if (_currentMediaPlayer == MediaPlayerTypeIPod) {
        block = ^{
            [_musicPlayer stop];
            [response setResult:DConnectMessageResultTypeOk];
        };
    } else if (_currentMediaPlayer == MediaPlayerTypeMoviePlayer) {
        if (![self moviePlayerViewControllerIsPresented]) {
            [response setErrorToUnknownWithMessage:@"Movie player view controller is not presented; please perform Media PUT API first to present the view controller."];
        } else {
            block = ^{
                // ムービープレイヤーを閉じる。
                [_viewController.moviePlayer stop];
                [_viewController dismissMoviePlayerViewControllerAnimated];
                [response setResult:DConnectMessageResultTypeOk];
            };
        }
    } else {
        [response setErrorToUnknownWithMessage:@"Unknown player type; this must be a bug."];
        return YES;
    }
    
    if (block) {
        if ([NSThread isMainThread]) {
            block();
        } else {
            dispatch_sync(dispatch_get_main_queue(), block);
        }
    }
    
    return YES;
}

- (BOOL)          profile:(DConnectMediaPlayerProfile *)profile
didReceivePutPauseRequest:(DConnectRequestMessage *)request
                 response:(DConnectResponseMessage *)response
                 deviceId:(NSString *)deviceId
{
    void(^block)(void) = nil;
    if (_currentMediaPlayer == MediaPlayerTypeIPod) {
        if ([_musicPlayer playbackState] == MPMusicPlaybackStatePlaying) {
            block = ^{
                [_musicPlayer pause];
                [response setResult:DConnectMessageResultTypeOk];
            };
        } else {
            [response setErrorToUnknownWithMessage:@"Media cannnot be paused; media is not playing."];
            return YES;
        }
    } else if (_currentMediaPlayer == MediaPlayerTypeMoviePlayer) {
        if (![self moviePlayerViewControllerIsPresented]) {
            [response setErrorToUnknownWithMessage:@"Movie player view controller is not presented; please perform Media PUT API first to present the view controller."];
        } else {
            if (_viewController.moviePlayer.playbackState == MPMusicPlaybackStatePlaying) {
                block = ^{
                    [_viewController.moviePlayer pause];
                    [response setResult:DConnectMessageResultTypeOk];
                };
            } else {
                [response setErrorToUnknownWithMessage:@"Media cannnot be paused; media is not playing."];
                return YES;
            }
        }
    } else {
        [response setErrorToUnknownWithMessage:@"Unknown player type; this must be a bug."];
        return YES;
    }
    
    if (block) {
        if ([NSThread isMainThread]) {
            block();
        } else {
            dispatch_sync(dispatch_get_main_queue(), block);
        }
    }
    
    return YES;
}

- (BOOL)           profile:(DConnectMediaPlayerProfile *)profile
didReceivePutResumeRequest:(DConnectRequestMessage *)request
                  response:(DConnectResponseMessage *)response
                  deviceId:(NSString *)deviceId
{
    if (_currentMediaPlayer == MediaPlayerTypeIPod) {
        if ([_musicPlayer playbackState] == MPMusicPlaybackStatePaused) {
            MPMediaItem *mediaItem = _musicPlayer.nowPlayingItem;
            NSNumber *isCloudItem = [mediaItem valueForProperty:MPMediaItemPropertyIsCloudItem];
            if (isCloudItem) {
                DPHostReachability *networkReachability = [DPHostReachability reachabilityForInternetConnection];
                NetworkStatus networkStatus = [networkReachability currentReachabilityStatus];
                if ([isCloudItem boolValue] && networkStatus == NotReachable) {
                    // iCloud上の音楽項目（つまりiOSデバイス側にまだダウンロードされていない）で、尚かつインターネット接続が無い場合は
                    // 再生できない。
                    [response setErrorToUnknownWithMessage:
                     @"Internet is not reachable; the specified audio media item is an iClould item and its playback requires an Internet connection."];
                    return YES;
                }
            }
            
            void(^block)(void) = ^{
                [_musicPlayer play];
                [response setResult:DConnectMessageResultTypeOk];
            };
            if (block) {
                if ([NSThread isMainThread]) {
                    block();
                } else {
                    dispatch_sync(dispatch_get_main_queue(), block);
                }
            }
            
            [response setResult:DConnectMessageResultTypeOk];
        } else {
            [response setErrorToUnknownWithMessage:@"Media cannot be resumed; media is not paused."];
        }
    } else if (_currentMediaPlayer == MediaPlayerTypeMoviePlayer) {
        if (![self moviePlayerViewControllerIsPresented]) {
            [response setErrorToUnknownWithMessage:@"Movie player view controller is not presented; please perform Media PUT API first to present the view controller."];
        } else {
            if (_viewController.moviePlayer.playbackState == MPMoviePlaybackStatePaused) {
                void(^block)(void) = ^{
                    [_viewController.moviePlayer play];
                    [response setResult:DConnectMessageResultTypeOk];
                };
                if ([NSThread isMainThread]) {
                    block();
                } else {
                    dispatch_sync(dispatch_get_main_queue(), block);
                }
            } else {
                [response setErrorToUnknownWithMessage:@"Media cannot be resumed; media is not paused."];
            }
        }
    } else {
        [response setErrorToUnknownWithMessage:@"Unknown player type; this must be a bug."];
    }
    return YES;
}

- (BOOL)         profile:(DConnectMediaPlayerProfile *)profile
didReceivePutSeekRequest:(DConnectRequestMessage *)request
                response:(DConnectResponseMessage *)response
                deviceId:(NSString *)deviceId
                     pos:(NSNumber *)pos
{
    if (!pos) {
        [response setErrorToInvalidRequestParameterWithMessage:@"pos must be specified."];
        return YES;
    }
    
    void(^block)(void) = nil;
    if (_currentMediaPlayer == MediaPlayerTypeIPod) {
        MPMediaItem *nowPlayingItem = _musicPlayer.nowPlayingItem;
        NSNumber *playbackDuration = [nowPlayingItem valueForProperty:MPMediaItemPropertyPlaybackDuration];
        if ([playbackDuration compare:pos] == NSOrderedAscending) {
            [response setErrorToInvalidRequestParameterWithMessage:@"pos exceeds the playback duration."];
            return YES;
        }
        
        block = ^{
            _musicPlayer.currentPlaybackTime = pos.doubleValue;
        };
    } else if (_currentMediaPlayer == MediaPlayerTypeMoviePlayer) {
        if (![self moviePlayerViewControllerIsPresented]) {
            [response setErrorToUnknownWithMessage:@"Movie player view controller is not presented; please perform Media PUT API first to present the view controller."];
            return YES;
        } else {
            NSTimeInterval playbackDuration = _viewController.moviePlayer.duration;
            if (playbackDuration > [pos unsignedIntegerValue]) {
                [response setErrorToInvalidRequestParameterWithMessage:@"pos exceeds the playback duration."];
                return YES;
            }
            
            block = ^{
                _viewController.moviePlayer.currentPlaybackTime = [pos doubleValue];
            };
        }
    } else {
        [response setErrorToUnknownWithMessage:@"Unknown player type; this must be a bug."];
        return YES;
    }
    
    if ([NSThread isMainThread]) {
        block();
    } else {
        dispatch_sync(dispatch_get_main_queue(), block);
    }
    
    [response setResult:DConnectMessageResultTypeOk];
    return YES;
}

#pragma mark Event Registration

- (BOOL)                   profile:(DConnectMediaPlayerProfile *)profile
didReceivePutOnStatusChangeRequest:(DConnectRequestMessage *)request
                          response:(DConnectResponseMessage *)response
                          deviceId:(NSString *)deviceId
                        sessionKey:(NSString *)sessionkey
{
    switch ([_eventMgr addEventForRequest:request]) {
        case DConnectEventErrorNone:             // エラー無し.
            [response setResult:DConnectMessageResultTypeOk];
            break;
        case DConnectEventErrorInvalidParameter: // 不正なパラメータ.
            [response setErrorToInvalidRequestParameter];
            break;
        case DConnectEventErrorNotFound:         // マッチするイベント無し.
        case DConnectEventErrorFailed:           // 処理失敗.
            [response setErrorToUnknown];
            break;
    }
    
    return YES;
}

#pragma mark - Delete Methods
#pragma mark Event Unregistration

- (BOOL)                      profile:(DConnectMediaPlayerProfile *)profile
didReceiveDeleteOnStatusChangeRequest:(DConnectRequestMessage *)request
                             response:(DConnectResponseMessage *)response
                             deviceId:(NSString *)deviceId
                           sessionKey:(NSString *)sessionkey
{
    switch ([_eventMgr removeEventForRequest:request]) {
        case DConnectEventErrorNone:             // エラー無し.
            [response setResult:DConnectMessageResultTypeOk];
            break;
        case DConnectEventErrorInvalidParameter: // 不正なパラメータ.
            [response setErrorToInvalidRequestParameter];
            break;
        case DConnectEventErrorNotFound:         // マッチするイベント無し.
        case DConnectEventErrorFailed:           // 処理失敗.
            [response setErrorToUnknown];
            break;
    }
    
    return YES;
}

@end
