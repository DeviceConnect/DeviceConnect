//
//  DPHostMediaStreamRecordingProfile.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <AVFoundation/AVFoundation.h>
#import <DConnectSDK/DConnectSDK.h>

@interface DPHostMediaStreamRecordingProfile : DConnectMediaStreamRecordingProfile<DConnectMediaStreamRecordingProfileDelegate, AVCaptureAudioDataOutputSampleBufferDelegate, AVCaptureVideoDataOutputSampleBufferDelegate>

/// @brief イベントマネージャ
@property DConnectEventManager *eventMgr;

@end
