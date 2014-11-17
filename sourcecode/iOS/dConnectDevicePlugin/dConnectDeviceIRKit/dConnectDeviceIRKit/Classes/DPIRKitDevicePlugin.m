//
//  DPIRKitDevicePlugin.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DPIRKitDevicePlugin.h"
#import "DPIRKit_irkit.h"
#import "DPIRKitRemoteControllerProfile.h"
#import "DPIRKitConst.h"

NSString *const DPIRKitInfoVersion = @"DPIRKitVersion";
NSString *const DPIRKitInfoAPIKey = @"DPIRKitAPIKey";
NSString *const DPIRKitStoryBoardName = @"Storyboard_";
NSString *const DPIRKitPluginName = @"IRKit 1.0.1";

// Const.h
NSString *const DPIRKitBundleName = @"dConnectDeviceIRKit_resources";
NSString *const DPIRKitInfoPlistName = @"dConnectDeviceIRKit-Info";

@interface DPIRKitDevicePlugin()
<
// プロファイルデリゲート
DConnectNetworkServiceDiscoveryProfileDelegate,
DConnectSystemProfileDataSource,

// デバイス検知デリゲート
DPIRKitManagerDetectionDelegate
>

{
    NSMutableDictionary *_devices;
    DConnectEventManager *_eventManager;
    NSString *_version;
}

- (void) sendDeviceDetectionEventWithDevice:(DPIRKitDevice *)device online:(BOOL)online;

- (void) startObeservation;
- (void) stopObeservation;

@end

@implementation DPIRKitDevicePlugin

#pragma mark - Initializaion

- (id) init {
    
    self = [super init];
    
    if (self) {
        DConnectNetworkServiceDiscoveryProfile *np = [DConnectNetworkServiceDiscoveryProfile new];
        DConnectSystemProfile *sp = [DConnectSystemProfile new];
        DPIRKitRemoteControllerProfile *rcp = [[DPIRKitRemoteControllerProfile alloc] initWithDevicePlugin:self];
        
        np.delegate = self;
        sp.dataSource = self;
        
        [self addProfile:np];
        [self addProfile:sp];
        [self addProfile:rcp];
        
        _devices = [NSMutableDictionary dictionary];
        
        id<DConnectEventCacheController> controller = [[DConnectMemoryCacheController alloc] init];
        _eventManager = [DConnectEventManager sharedManagerForClass:[DPIRKitDevicePlugin class]];
        [_eventManager setController:controller];
        
        NSBundle *bundle = DPIRBundle();
        NSString* path = [bundle pathForResource:DPIRKitInfoPlistName ofType:@"plist"];
        NSDictionary *info = [NSDictionary dictionaryWithContentsOfFile:path];
        _version = [info objectForKey:DPIRKitInfoVersion];
        
        __weak typeof(self) _self = self;
        dispatch_async(dispatch_get_main_queue(), ^{
            
            NSNotificationCenter *nc = [NSNotificationCenter defaultCenter];
            UIApplication *application = [UIApplication sharedApplication];
            
            [nc addObserver:_self selector:@selector(startObeservation)
                       name:UIApplicationWillEnterForegroundNotification
                     object:application];
            
            [nc addObserver:_self selector:@selector(stopObeservation)
                       name:UIApplicationDidEnterBackgroundNotification
                     object:application];
            
            // NSNetServiceBrowserはUIスレッドで生成する必要があるためUIスレッドで実行する。
            DPIRKitManager *manager = [DPIRKitManager sharedInstance];
            manager.apiKey = [info objectForKey:DPIRKitInfoAPIKey];
            manager.detectionDelegate = _self;
            
            [_self startObeservation];
        });
        
        DPIRLog(@"== info ==");
        DPIRLog(@"%@", info);
        
        self.pluginName = DPIRKitPluginName;//[NSString stringWithFormat:@"%@ %@", DPIRKitPluginName, _version];
    }
    
    return self;
}

- (void) dealloc {
    _devices = nil;
    _eventManager = nil;
    
    NSNotificationCenter *nc = [NSNotificationCenter defaultCenter];
    UIApplication *application = [UIApplication sharedApplication];
    
    [nc removeObserver:self name:UIApplicationDidBecomeActiveNotification object:application];
    [nc removeObserver:self name:UIApplicationDidEnterBackgroundNotification object:application];

    [self stopObeservation];
}

#pragma mark - Public Methods

- (DPIRKitDevice *) deviceForDeviceId:(NSString *)deviceId {
    @synchronized (_devices) {
        return [_devices objectForKey:deviceId];
    }
}

#pragma mark - Private Methods

- (void) sendDeviceDetectionEventWithDevice:(DPIRKitDevice *)device online:(BOOL)online {
    
    BOOL hit = NO;
    @synchronized (_devices) {
        
        DPIRKitDevice *d = [_devices objectForKey:device.name];
        
        if (d) {
            hit = YES;
            if (!online) {
                [_devices removeObjectForKey:device.name];
            }
        } else if (online) {
            [_devices setObject:device forKey:device.name];
        }
    }
    
    if ((!hit && online) || (hit && !online)) {
        
        DConnectMessage *networkService = [DConnectMessage message];
        [DConnectNetworkServiceDiscoveryProfile setId:device.name target:networkService];
        [DConnectNetworkServiceDiscoveryProfile setName:device.name target:networkService];
        [DConnectNetworkServiceDiscoveryProfile setType:DConnectNetworkServiceDiscoveryProfileNetworkTypeWiFi
                                                 target:networkService];
        [DConnectNetworkServiceDiscoveryProfile setState:online target:networkService];
        [DConnectNetworkServiceDiscoveryProfile setOnline:online target:networkService];
        
        NSArray *events = [_eventManager eventListForProfile:DConnectNetworkServiceDiscoveryProfileName
                                                   attribute:DConnectNetworkServiceDiscoveryProfileAttrOnServiceChange];
        
        for (DConnectEvent *event in events) {
            DConnectMessage *message = [DConnectMessage message];
            [message setString:@"" forKey:DConnectMessageDeviceId];
            [message setString:DConnectNetworkServiceDiscoveryProfileName forKey:DConnectMessageProfile];
            [message setString:DConnectNetworkServiceDiscoveryProfileAttrOnServiceChange forKey:DConnectMessageAttribute];
            [message setString:event.sessionKey forKey:DConnectMessageSessionKey];
            [DConnectNetworkServiceDiscoveryProfile setNetworkService:networkService target:message];
            [self sendEvent:message];
        }
    }
}

- (void) startObeservation {
    
    @synchronized (_devices) {
        [_devices removeAllObjects];
    }
    
    [[DPIRKitManager sharedInstance] startDetection];
}

- (void) stopObeservation {
    [[DPIRKitManager sharedInstance] stopDetection];
}

#pragma mark - Profile Delegate
#pragma mark DConnectNetworkServiceDiscoveryProfileDelegate

- (BOOL)                       profile:(DConnectNetworkServiceDiscoveryProfile *)profile
didReceiveGetGetNetworkServicesRequest:(DConnectRequestMessage *)request
                              response:(DConnectResponseMessage *)response
{
    
    DConnectArray *services = [DConnectArray array];
    
    @synchronized (_devices) {
        
        for (DPIRKitDevice *device in _devices.allValues) {
            DConnectMessage *service = [DConnectMessage message];
            [DConnectNetworkServiceDiscoveryProfile setId:device.name target:service];
            [DConnectNetworkServiceDiscoveryProfile setName:device.name target:service];
            // WiFiでのみ接続するので常にWiFi。
            [DConnectNetworkServiceDiscoveryProfile setType:DConnectNetworkServiceDiscoveryProfileNetworkTypeWiFi
                                                     target:service];
            // 見つかっている時点でWiFiにつながっているので常にYES。
            [DConnectNetworkServiceDiscoveryProfile setOnline:YES target:service];
            [services addMessage:service];
        }
    }
    
    response.result = DConnectMessageResultTypeOk;
    [DConnectNetworkServiceDiscoveryProfile setServices:services target:response];
    
    return YES;
}

- (BOOL)                    profile:(DConnectNetworkServiceDiscoveryProfile *)profile
didReceivePutOnServiceChangeRequest:(DConnectRequestMessage *)request
                           response:(DConnectResponseMessage *)response
                           deviceId:(NSString *)deviceId
                         sessionKey:(NSString *)sessionKey
{
    
    DConnectEventError error = [_eventManager addEventForRequest:request];
    switch (error) {
        case DConnectEventErrorNone:
            response.result = DConnectMessageResultTypeOk;
            DPIRLog(@"Register ServiceChange Event. %@", sessionKey);
            break;
        case DConnectEventErrorInvalidParameter:
            [response setErrorToInvalidRequestParameter];
            break;
        default:
            [response setErrorToUnknown];
            break;
    }
    
    return YES;
}

- (BOOL)                       profile:(DConnectNetworkServiceDiscoveryProfile *)profile
didReceiveDeleteOnServiceChangeRequest:(DConnectRequestMessage *)request
                              response:(DConnectResponseMessage *)response
                              deviceId:(NSString *)deviceId
                            sessionKey:(NSString *)sessionKey
{
    
    DConnectEventError error = [_eventManager removeEventForRequest:request];
    switch (error) {
        case DConnectEventErrorNone:
            response.result = DConnectMessageResultTypeOk;
            DPIRLog(@"Unregister ServiceChange Event. %@", sessionKey);
            break;
        case DConnectEventErrorInvalidParameter:
            [response setErrorToInvalidRequestParameter];
            break;
        default:
            [response setErrorToUnknown];
            break;
    }
    
    return YES;
}

#pragma mark DConnectSystemProfileDataSource

- (NSString *) versionOfSystemProfile:(DConnectSystemProfile *)profile {
    return _version;
}

- (DConnectSystemProfileConnectState) profile:(DConnectSystemProfile *)profile
                         wifiStateForDeviceId:(NSString *)deviceId
{
    
    DConnectSystemProfileConnectState state = DConnectSystemProfileConnectStateOff;
    // TODO: 実際に接続を確認した方が良いかの検討
    @synchronized (_devices) {
        if (_devices.count > 0) {
            DPIRKitDevice *device = [_devices objectForKey:deviceId];
            if (device) {
                state = DConnectSystemProfileConnectStateOn;
            }
        }
    }
    
    return state;
}

- (UIViewController *) profile:(DConnectSystemProfile *)sender
         settingPageForRequest:(DConnectRequestMessage *)request
{
    
    NSBundle *bundle = DPIRBundle();
    
    // iphoneとipadでストーリーボードを切り替える
    UIStoryboard *sb;
    if (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPhone) {
        sb = [UIStoryboard storyboardWithName:[NSString stringWithFormat:@"%@iPhone", DPIRKitStoryBoardName]
                                       bundle:bundle];
    } else{
        sb = [UIStoryboard storyboardWithName:[NSString stringWithFormat:@"%@iPad", DPIRKitStoryBoardName]
                                       bundle:bundle];
    }
    UINavigationController *vc = [sb instantiateInitialViewController];
    return vc;
}

#pragma mark - DPIRKitManagerDetectionDelegate

- (void) manager:(DPIRKitManager *)manager didFindDevice:(DPIRKitDevice *)device {
    
    DPIRLog(@"found a device : %@", device);
    [self sendDeviceDetectionEventWithDevice:device online:YES];
}

- (void) manager:(DPIRKitManager *)manager didLoseDevice:(DPIRKitDevice *)device {
    DPIRLog(@"lost a device : %@", device);
    [self sendDeviceDetectionEventWithDevice:device online:NO];
}

@end
