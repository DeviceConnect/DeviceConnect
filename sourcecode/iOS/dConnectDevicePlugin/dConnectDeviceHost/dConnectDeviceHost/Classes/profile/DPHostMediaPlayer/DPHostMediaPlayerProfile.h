//
//  DPHostMediaPlayerProfile.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <AVFoundation/AVFoundation.h>
#import <DConnectSDK/DConnectSDK.h>

/**
 iOS HostデバイスプラグインMediaPlayerプロファイル
 
 @note iPodライブラリの購入した曲の再生時にEULA内容の更新に伴う許諾承認が必要な場合があるのだが、Music.appなどで事前に
 承認をしていないとMediaPlayerが正しく曲再生を行えない現象が確認されている（Media PUT APIで購入した曲を設定した直後、
 次の曲に再生項目がスキップする様な動作が延々と繰り返され、その間にMediaPlayer Play APIやControl Center（画面下から上への
 スワイプで現れるiOS標準パネルUI）経由での曲再生ができなくなる）。
 この場合、ユーザがMusic.app側で、購入した曲をタップして再生するなどの動作でEULA承認画面を表示し、承認を行ってもらう必要が有る。
 */
@interface DPHostMediaPlayerProfile : DConnectMediaPlayerProfile <DConnectMediaPlayerProfileDelegate>

@end
