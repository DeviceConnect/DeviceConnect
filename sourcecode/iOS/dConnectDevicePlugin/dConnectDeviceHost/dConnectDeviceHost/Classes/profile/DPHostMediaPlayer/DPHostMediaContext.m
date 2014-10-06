//
//  DPHostMediaContext.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <ImageIO/CGImageProperties.h>
#import <MediaPlayer/MediaPlayer.h>

#import "DPHostMediaContext.h"

NSString *MediaContextMediaIdSchemeIPodAudio = @"ipod-audio";
NSString *MediaContextMediaIdSchemeIPodMovie = @"ipod-movie";
NSString *MediaContextMediaIdSchemeAssetsLibrary = @"assets-library";
NSString *MediaContextMediaIdSchemeIPodLibrary = @"ipod-library";

const MPMediaType TargetMPMediaType = MPMediaTypeAny;

static NSUInteger contextCacheCountMax = 100000;
static NSMutableArray *contextCacheKey;
static NSMutableArray *contextCacheVal;

@interface DPHostMediaContext ()

- (void) cache;

+ (DPHostMediaContext *) findContextWithMediaId:(NSString *)mediaId;

@end

@implementation DPHostMediaContext

+ (void)initialize
{
    if (self == [DPHostMediaContext class]) {
        contextCacheKey = [NSMutableArray array];
        contextCacheVal = [NSMutableArray array];
    }
}

- (void) cache
{
    if (![DPHostMediaContext findContextWithMediaId:_mediaId]) {
        if (contextCacheKey.count == contextCacheCountMax) {
            // 最大キャッシュ数に達していたら先頭の項目を消す。
            [contextCacheKey removeObjectAtIndex:0];
            [contextCacheVal removeObjectAtIndex:0];
        }
        
        // 末尾に追加する。
        [contextCacheKey addObject:_mediaId];
        [contextCacheVal addObject:self];
    }
}

+ (DPHostMediaContext *) findContextWithMediaId:(NSString *)mediaId
{
    NSUInteger index = [contextCacheKey indexOfObject:mediaId];
    if (index != NSNotFound) {
        return contextCacheVal[index];
    } else {
        return nil;
    }
}

+ (NSNumber *) persistentIdWithMediaIdURL:(NSURL *)mediaIdURL
{
    NSString *persistentIdStr = mediaIdURL.resourceSpecifier;
    unsigned long long persistentIdTmp;
    if (![[NSScanner scannerWithString:persistentIdStr] scanUnsignedLongLong:&persistentIdTmp]) {
        return nil;
    }
    return [NSNumber numberWithUnsignedLongLong:persistentIdTmp];
}

+ (instancetype)contextWithURL:(NSURL *)url {
    __block DPHostMediaContext *ctx;
    
    if ((ctx = [DPHostMediaContext findContextWithMediaId:url.absoluteString])) {
        // キャッシュにヒット；キャッシュを返却する。
        return ctx;
    }
    
    if ([url.scheme isEqualToString:MediaContextMediaIdSchemeIPodAudio] || [url.scheme isEqualToString:MediaContextMediaIdSchemeIPodMovie]) {
        NSNumber *persistentId = [DPHostMediaContext persistentIdWithMediaIdURL:url];
        
        MPMediaQuery *mediaQuery = [MPMediaQuery new];
        [mediaQuery addFilterPredicate:
         [MPMediaPropertyPredicate predicateWithValue:[NSNumber numberWithInteger:TargetMPMediaType]
                                          forProperty:MPMediaItemPropertyMediaType]];
        [mediaQuery addFilterPredicate:
         [MPMediaPropertyPredicate predicateWithValue:persistentId
                                          forProperty:MPMediaItemPropertyPersistentID]];
        NSArray *items = [mediaQuery items];
        
        MPMediaItem *mediaItem;
        if (items.count == 0) {
            return nil;
        } else {
            mediaItem = items[0];
        }
        
        ctx = [DPHostMediaContext contextWithMediaItem:mediaItem];
    } else if ([url.scheme isEqualToString:MediaContextMediaIdSchemeAssetsLibrary]) {
        dispatch_semaphore_t semaphore = dispatch_semaphore_create(0);
        // 30秒経ったらタイムアウト
        dispatch_time_t timeout = dispatch_time(DISPATCH_TIME_NOW, NSEC_PER_SEC * 30);

        [[ALAssetsLibrary new] assetForURL:url resultBlock:^(ALAsset *asset) {
            ctx = [DPHostMediaContext contextWithAsset:asset];
            dispatch_semaphore_signal(semaphore);
        } failureBlock:^(NSError *error) {}];
        
        // ライブラリのクエリー（非同期）が終わる、もしくはタイムアウトするまで待つ
        dispatch_semaphore_wait(semaphore, timeout);
    } else if ([url.scheme isEqualToString:MediaContextMediaIdSchemeIPodLibrary]) {
        // ipod-library:スキームのidはPersistentIdなので、それを取得する。
        NSNumber *persistentId = nil;
        for (NSString *keyValStr in [url.query componentsSeparatedByString:@"&"]) {
            NSArray *keyValArr = [keyValStr componentsSeparatedByString:@"="];
            if (keyValArr.count != 2) {
                continue;
            }
            if ([keyValArr[0] isEqualToString:@"id"]) {
                unsigned long long persistentIdTmp;
                if (![[NSScanner scannerWithString:keyValArr[1]] scanUnsignedLongLong:&persistentIdTmp]) {
                    continue;
                }
                persistentId = @(persistentIdTmp);
                break;
            }
        }
        if (!persistentId) {
            return nil;
        }
        
        MPMediaQuery *mediaQuery = [MPMediaQuery new];
        [mediaQuery addFilterPredicate:
         [MPMediaPropertyPredicate predicateWithValue:[NSNumber numberWithInteger:TargetMPMediaType]
                                          forProperty:MPMediaItemPropertyMediaType]];
        [mediaQuery addFilterPredicate:
         [MPMediaPropertyPredicate predicateWithValue:persistentId
                                          forProperty:MPMediaItemPropertyPersistentID]];
        NSArray *items = [mediaQuery items];
        
        MPMediaItem *mediaItem;
        if (items.count == 0) {
            return nil;
        } else {
            mediaItem = items[0];
        }
        
        ctx = [DPHostMediaContext contextWithMediaItem:mediaItem];
    } else {
        NSLog(@"Unknown media URL scheme.");
    }
    
    return ctx;
}

+ (instancetype)contextWithAsset:(ALAsset *)asset
{
    if (!asset) {
        return nil;
    }
    
    DPHostMediaContext *instance = [self new];
    
    if (instance) {
        // iPodプレイヤーで再生させない
        instance.useIPodPlayer = NO;
        
        // ===== type =====
        NSString *type = [asset valueForProperty:ALAssetPropertyType];
        if ([type isEqualToString:ALAssetTypePhoto] || [type isEqualToString:ALAssetTypeUnknown]) {
            // タイプが写真・不明なものは取り扱わない。
            return nil;
        } else if ([type isEqualToString:ALAssetTypeVideo]) {
            instance.type = @"Movie";
        }
        instance.isAudio = NO;
        
        NSURL *url = [asset valueForProperty:ALAssetPropertyAssetURL];
        if (!url) {
            // 現在のApple IDとは別のIDに紐づけられた曲（つまり他人が購入したiTunes曲）だったり、
            // Purchaseにはあるけど、まだダウンロードされていない曲はURLが無く、MoviePlayerから再生できない。
            return nil;
        }
        
        ALAssetRepresentation *defaultRep = asset.defaultRepresentation;
        // 各種の使用するかもしれないメタデータ
        NSDictionary *metadata = defaultRep.metadata;
        NSDictionary *exifDict = metadata[(NSString *)kCGImagePropertyExifDictionary];
        NSDictionary *iptcDict = metadata[(NSString *)kCGImagePropertyIPTCDictionary];
        NSDictionary *pngDict = metadata[(NSString *)kCGImagePropertyPNGDictionary];
        NSDictionary *tiffDict = metadata[(NSString *)kCGImagePropertyTIFFDictionary];
        NSDictionary *ciffDict = metadata[(NSString *)kCGImagePropertyCIFFDictionary];
        NSDictionary *canonDict = metadata[(NSString *)kCGImagePropertyMakerCanonDictionary];
        
        instance.media = [AVAsset assetWithURL:url];
        
        // ===== mediaId =====
        instance.mediaId = url.absoluteString;
        
        // ===== mimeType =====
        NSString *mimeType = [DConnectFileManager searchMimeTypeForExtension:defaultRep.filename.pathExtension];
        if (!mimeType) {
            mimeType = [DConnectFileManager mimeTypeForArbitraryData];
        }
        instance.mimeType = mimeType;
        
        // ===== title =====
        // kCGImagePropertyPNGTitle
        NSString *title = pngDict[(NSString *)kCGImagePropertyPNGTitle];
        // kCGImagePropertyTIFFDocumentName
        if (!title) {
            title = tiffDict[(NSString *)kCGImagePropertyTIFFDocumentName];
        }
        // kCGImagePropertyCIFFImageName
        if (!title) {
            title = ciffDict[(NSString *)kCGImagePropertyCIFFImageName];
        }
        if (!title) {
            title = defaultRep.filename;
        }
        instance.title = title;
        
        // ===== description =====
        // kCGImagePropertyExifUserComment
        NSString *description = exifDict[(NSString *)kCGImagePropertyExifUserComment];
        // kCGImagePropertyPNGDescription
        if (!description) {
            description = pngDict[(NSString *)kCGImagePropertyPNGDescription];
        }
        // kCGImagePropertyCIFFDescription
        if (!description) {
            description = ciffDict[(NSString *)kCGImagePropertyCIFFDescription];
        }
        instance.desc = description;
        
        // TODO: サムネイルの提供の仕方を考える。Data URI？それとも一時ファイルとして画像を用意しておくとか（一時ファイルは面倒なのでやりたくない）？
        //    CGImageRef thumbnail = asset.thumbnail;
        //    instance.imageUri
        
        // ===== duration =====
        instance.duration = [asset valueForProperty:ALAssetPropertyDuration];
        
        // ===== creators-+-creator =====
        // =====          +-role    =====
        // kCGImagePropertyExifCameraOwnerName
        NSString *creator = exifDict[(NSString *)kCGImagePropertyExifCameraOwnerName];
        // kCGImagePropertyPNGAuthor
        if (!creator) {
            creator = pngDict[(NSString *)kCGImagePropertyPNGAuthor];
        }
        // kCGImagePropertyTIFFArtist
        if (!creator) {
            creator = tiffDict[(NSString *)kCGImagePropertyTIFFArtist];
        }
        // kCGImagePropertyCIFFOwnerName
        if (!creator) {
            creator = ciffDict[(NSString *)kCGImagePropertyCIFFOwnerName];
        }
        // kCGImagePropertyMakerCanonOwnerName
        if (!creator) {
            creator = canonDict[(NSString *)kCGImagePropertyMakerCanonOwnerName];
        }
        if (creator) {
            DConnectArray *creators = [DConnectArray array];
            DConnectMessage *message = [DConnectMessage message];
            [DConnectMediaPlayerProfile setCreator:creator target:message];
            [DConnectMediaPlayerProfile setRole:@"Owner" target:message];
            [creators addMessage:message];
            instance.creators = creators;
        }
        
        // ===== keywords =====
        // kCGImagePropertyIPTCKeywords
        // IPTCのドキュメントによれば、Keywordsはフリーフォームの文字らしい。区切り文字はカンマもしくはセミコロン。
        // https://www.iptc.org/std/photometadata/documentation/GenericGuidelines/index.htm#!Documents/iptccorecontentsection.htm
        NSString *keywordsStr = iptcDict[(NSString *)kCGImagePropertyIPTCKeywords];
        if (keywordsStr) {
            DConnectArray *keywords =
            [DConnectArray initWithArray:
             [keywordsStr componentsSeparatedByCharactersInSet:
              (NSCharacterSet *)[NSCharacterSet characterSetWithCharactersInString:@",;"]]];
            instance.keywords = keywords;
        }
    }
    
    // キャッシュする。
    [instance cache];
    
    return instance;
}

+ (instancetype)contextWithMediaItem:(MPMediaItem *)mediaItem
{
    if (!mediaItem) {
        return nil;
    }
    
    DPHostMediaContext *instance = [self new];
    
    if (instance) {
        // iPodプレイヤーで再生させる
        instance.useIPodPlayer = YES;
        
        NSNumber *persistentId = [mediaItem valueForProperty:MPMediaItemPropertyPersistentID];
        if (!persistentId) {
            // 現在のApple IDとは別のApple IDに紐づけられた曲（つまり他人が購入したiTunes曲）だったり、
            // Purchaseにはあるけど、まだダウンロードされていない曲はMPMusicPlayerControllerから再生できない。
            return nil;
        }
        
        NSInteger type = [[mediaItem valueForProperty:MPMediaItemPropertyMediaType] integerValue];
        if ((type & MPMediaTypeAnyVideo) != 0) {
            instance.isAudio = NO;
            instance.type = @"Movie";
            instance.mediaId =
            [NSString stringWithFormat:@"%@:%llu",
             MediaContextMediaIdSchemeIPodMovie, persistentId.unsignedLongLongValue];
        } else if ((type & MPMediaTypeAnyAudio) != 0) {
            instance.isAudio = YES;
            instance.type = @"Audio";
            instance.mediaId =
            [NSString stringWithFormat:@"%@:%llu",
             MediaContextMediaIdSchemeIPodAudio, persistentId.unsignedLongLongValue];
        } else {
            // ????
            return nil;
        }
        
        NSURL *url = [mediaItem valueForProperty:MPMediaItemPropertyAssetURL];
        NSString *mimeType = [DConnectFileManager searchMimeTypeForExtension:url.path.pathExtension];
        if (!mimeType) {
            mimeType = [DConnectFileManager mimeTypeForArbitraryData];
        }
        instance.mimeType = mimeType;
        
        instance.title = [mediaItem valueForProperty:MPMediaItemPropertyTitle];

        instance.duration = [mediaItem valueForProperty:MPMediaItemPropertyPlaybackDuration];
        
        instance.desc = [mediaItem valueForProperty:MPMediaItemPropertyComments];
        
        NSString *creator = [mediaItem valueForProperty:MPMediaItemPropertyArtist];
        if (creator) {
            DConnectArray *creators = [DConnectArray array];
            DConnectMessage *message = [DConnectMessage message];
            [DConnectMediaPlayerProfile setCreator:creator target:message];
            [DConnectMediaPlayerProfile setRole:@"Artist" target:message];
            [creators addMessage:message];
            instance.creators = creators;
        }
    }
    
    // キャッシュする。
    [instance cache];
    
    return instance;
}

- (void) setVariousMetadataToMessage:(DConnectMessage *)message omitMediaId:(BOOL)omitMediaId
{
    if (_mediaId && !omitMediaId) {
        [DConnectMediaPlayerProfile setMediaId:_mediaId target:message];
    }
    if (_mimeType) {
        [DConnectMediaPlayerProfile setMIMEType:_mimeType target:message];
    }
    if (_title) {
        [DConnectMediaPlayerProfile setTitle:_title target:message];
    }
    if (_type) {
        [DConnectMediaPlayerProfile setType:_type target:message];
    }
    if (_language) {
        [DConnectMediaPlayerProfile setLanguage:_language target:message];
    }
    if (_desc) {
        [DConnectMediaPlayerProfile setDescription:_desc target:message];
    }
    if (_imageUri) {
        [DConnectMediaPlayerProfile setImageUri:_imageUri.absoluteString target:message];
    }
    if (_duration) {
        [DConnectMediaPlayerProfile setDuration:_duration.intValue target:message];
    }
    if (_creators) {
        [DConnectMediaPlayerProfile setCreators:_creators target:message];
    }
    if (_keywords) {
        [DConnectMediaPlayerProfile setKeywords:_keywords target:message];
    }
    if (_genres) {
        [DConnectMediaPlayerProfile setGenres:_genres target:message];
    }
}

@end
