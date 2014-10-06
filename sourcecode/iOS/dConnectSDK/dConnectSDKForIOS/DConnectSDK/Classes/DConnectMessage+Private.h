//
//  DConnectMessage_Private.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectMessage.h"

@interface DConnectArray ()

- (NSArray *) internalArray;

@end

@interface DConnectMessage ()

- (NSMutableDictionary *) internalDictionary;

@end
