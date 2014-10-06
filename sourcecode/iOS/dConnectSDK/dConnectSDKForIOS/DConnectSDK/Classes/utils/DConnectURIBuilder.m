//
//  DConnectURIBuilder.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectMessage.h"
#import "DConnectURIBuilder.h"
#import "DConnectURLProtocol.h"

@interface DConnectURIBuilder()

- (NSString *) descriptionForEncodeFlg:(BOOL)encode;

@end

@implementation DConnectURIBuilder

- (id) init {
    
    self = [super init];
    
    if (self) {
        _host = [DConnectURLProtocol host];
        _port = [DConnectURLProtocol port];
        _scheme = [DConnectURLProtocol scheme];
        _api = DConnectMessageDefaultAPI;
    }
    
    return self;
}

- (NSURL *) build {
    NSString *url = [self descriptionForEncodeFlg:YES];
    return [NSURL URLWithString:url];
}

- (void) addParameter:(NSString *)parameter forName:(NSString *)name {
    
    if (!_params) {
        _params = [NSMutableDictionary dictionary];
    }
    
    [_params setObject:parameter forKey:name];
}

#pragma mark - Private Methods

- (NSString *) description {
    return [self descriptionForEncodeFlg:NO];
}

- (NSString *) descriptionForEncodeFlg:(BOOL)encode {
    
    NSMutableString *url = [NSMutableString string];
    
    if (_scheme) {
        [url appendString:_scheme];
        [url appendString:@"://"];
    }
    
    if (_host) {
        [url appendString:_host];
    }
    
    if (_port > 0) {
        [url appendString:[NSString stringWithFormat:@":%d", _port]];
    }
    
    if (_path) {
        [url appendString:@"/"];
        [url appendString:_path];
    } else {
        if (_api) {
            [url appendString:@"/"];
            [url appendString:_api];
        }
        
        if (_profile) {
            [url appendString:@"/"];
            [url appendString:_profile];
        }
        
        if (_interface) {
            [url appendString:@"/"];
            [url appendString:_interface];
        }
        
        if (_attribute) {
            [url appendString:@"/"];
            [url appendString:_attribute];
        }
    }
    
    if (_params && _params.count > 0) {
        NSArray *keys = [_params allKeys];
        for (int i = 0; i < keys.count; i++) {
            if (i == 0) {
                [url appendString:@"?"];
            } else {
                [url appendString:@"&"];
            }
            
            NSString *key = [keys objectAtIndex:i];
            
            if (encode) {
                id set = [NSCharacterSet alphanumericCharacterSet];
                [url appendString:[key stringByAddingPercentEncodingWithAllowedCharacters:set]];
                [url appendString:@"="];
                [url appendString:[((NSString *)[_params objectForKey:key])
                                        stringByAddingPercentEncodingWithAllowedCharacters:set]];
            } else {
                [url appendString:key];
                [url appendString:@"="];
                [url appendString:[_params objectForKey:key]];
            }
        }
    }
    
    return url;
}

@end
