//
//  UdpRequest.m
//  CameraRemoteSampleApp
//  Copyright 2014 Sony Corporation
//

#import "UdpRequest.h"

static id<UdpRequestDelegate> _discoveryDelegate;

@implementation UdpRequest
{
    CFSocketRef _cfSocket;
    NSThread *_timeoutThread;
    bool _didReceiveSsdp;
}

int _SSDP_RECEIVE_TIMEOUT = 10; // seconds
int _SSDP_PORT = 1900;
int _SSDP_MX = 1;
NSString *const _SSDP_ADDR = @"239.255.255.250";
NSString *const _SSDP_ST = @"urn:schemas-sony-com:service:ScalarWebAPI:1";

-(void) search: (id<UdpRequestDelegate>) delegate
{
    _discoveryDelegate = delegate;
    _didReceiveSsdp = false;
    _timeoutThread = [[NSThread alloc] initWithTarget:self selector:@selector(timeoutThread) object:nil];
    [_timeoutThread start];
    
    [self listen];
}

-(void) timeoutThread
{
    int i=0;
    while(i<_SSDP_RECEIVE_TIMEOUT && ![_timeoutThread isCancelled]) {
        sleep(1);
        i++;
    }
    if(CFSocketIsValid(_cfSocket))
    {
        _cfSocket = NULL;
    }
    if(!_didReceiveSsdp)
    {
        [_discoveryDelegate didReceiveDdUrl:NULL];
    }
}

- (void)listen
{
    if(![self initSocket])
    {
        return;
    }
    
    // Send from socket
    NSString* _message = [NSString stringWithFormat:@"M-SEARCH * HTTP/1.1\r\nHOST:%@:%d\r\nMAN:\"ssdp:discover\"\r\nMX:%d\r\nST:%@\r\n\r\n", _SSDP_ADDR,_SSDP_PORT,_SSDP_MX,_SSDP_ST];
    CFDataRef data = CFDataCreate(NULL, (const UInt8*)[_message UTF8String], [_message lengthOfBytesUsingEncoding:NSUTF8StringEncoding]);
    
    /* Set the port and address we want to send to */
    static struct sockaddr_in addr;
    memset(&addr, 0, sizeof(addr));
    addr.sin_len = sizeof(addr);
    addr.sin_family = AF_INET;
    addr.sin_addr.s_addr = inet_addr([_SSDP_ADDR UTF8String]);
    addr.sin_port = htons(_SSDP_PORT);
    
    NSData *address = [ NSData dataWithBytes: &addr length: sizeof(addr) ];
    
    if(CFSocketSendData(_cfSocket, (__bridge CFDataRef)address, data, 0.0) == kCFSocketSuccess)
    {
//        NSLog(@"UdpRequest callCFSocket Sending data");
    }
    else
    {
//        NSLog(@"UdpRequest callCFSocket Sending failed. Reinitialising socket for listening");
        CFSocketInvalidate(_cfSocket);
        if(![self initSocket])
        {
            CFRelease(data);
            return;
        }
        
        /* Set the port and address we want to listen on */
        if (CFSocketSetAddress(_cfSocket, (__bridge CFDataRef)(address)) != kCFSocketSuccess)
        {
//            NSLog(@"UdpRequest callCFSocket CFSocketSetAddress() failed\n = %d", errno);
        }
    }
    
    // Listen from socket
    CFRunLoopSourceRef cfSource = CFSocketCreateRunLoopSource(kCFAllocatorDefault, _cfSocket, 0);
    
    if(cfSource == NULL)
    {
//        NSLog(@"UdpRequest callCFSocket CFRunLoopSourceRef is null");
        CFRelease(data);
        return;
    }
    
    CFRunLoopAddSource(CFRunLoopGetCurrent(), cfSource, kCFRunLoopDefaultMode);
//    NSLog(@"UdpRequest callCFSocket Socket listening");
    
    CFRelease(cfSource);
    CFRelease(_cfSocket);
    CFRelease(data);
    CFRunLoopRun();
//    NSLog(@"UdpRequest callCFSocket CFRunLoopRun finish");
}

/*
 * Function for initializing socket for SSDP
 */
- (BOOL) initSocket {
    CFSocketContext socketContext = {0, (__bridge void *)(self), NULL, NULL, NULL};
    _cfSocket = CFSocketCreate(NULL, PF_INET, SOCK_DGRAM, IPPROTO_UDP,
                               kCFSocketAcceptCallBack | kCFSocketDataCallBack , (CFSocketCallBack)receiveData, &socketContext);
    if (_cfSocket == NULL)
    {
//        NSLog(@"UDP socket could not be created\n");
        return NO;
    }
    
    CFSocketSetSocketFlags(_cfSocket, kCFSocketCloseOnInvalidate);
    
    static struct ip_mreq mreq;
    mreq.imr_multiaddr.s_addr = inet_addr([_SSDP_ADDR UTF8String]);
    mreq.imr_interface.s_addr = inet_addr([[self getIPAddress]UTF8String]);
    
    if(setsockopt(CFSocketGetNative(_cfSocket), IPPROTO_IP, IP_ADD_MEMBERSHIP,
                  (const void *)&mreq, sizeof(struct ip_mreq)))
    {
//        NSLog(@"UdpRequest callCFSocket IP_ADD_MEMBERSHIP error");
        return NO;
    }
    return YES;
}

void receiveData(CFSocketRef s,
                        CFSocketCallBackType type,
                        CFDataRef address,
                        const void *data,
                        void *info)
{
    if(data)
    {
        NSString* response = [[NSString alloc] initWithData:(__bridge NSData *)((CFDataRef)data) encoding:NSUTF8StringEncoding];
//        NSLog(@"UdpRequest CFSocket receiveData response = %@", response);
        
        UdpRequest *udpRequest = (__bridge UdpRequest *)info;
        NSString* ddUrl = [udpRequest parseSsdpResponse:response];
        if(ddUrl!=NULL)
        {
            ddUrl = [ddUrl stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
        }
//        NSLog(@"UdpRequest CFSocket receiveData didReceiveDdUrl = %@", ddUrl);
        [_discoveryDelegate didReceiveDdUrl:ddUrl];
        CFSocketInvalidate(s);
    }
}

-(NSString*) parseSsdpResponse:(NSString*) response
{
    NSString* ret = NULL;
    if(response == NULL)
    {
        return ret;
    }
    NSArray* first = [response componentsSeparatedByString:@"LOCATION:"];
    if(first!=nil && first.count == 2)
    {
        NSArray* second = [[first objectAtIndex:1] componentsSeparatedByString:@"\r\n"];
        if(second!=nil && second.count >= 2)
        {
            if(![[second objectAtIndex:0] isEqualToString:@""])
            {
                ret = [second objectAtIndex:0];
                _didReceiveSsdp = true;
            }
        }
    }
    return ret;
}

- (NSString *)getIPAddress
{
    NSString *address = @"0.0.0.0";
    struct ifaddrs *interfaces = NULL;
    struct ifaddrs *temp_addr = NULL;
    int success = 0;
    // retrieve the current interfaces - returns 0 on success
    success = getifaddrs(&interfaces);
    if (success == 0)
    {
        // Loop through linked list of interfaces
        temp_addr = interfaces;
        while(temp_addr != NULL)
        {
            if(temp_addr->ifa_addr->sa_family == AF_INET)
            {
//                NSLog(@"UdpRequest getIPAddress NIF = %@", [NSString stringWithUTF8String:temp_addr->ifa_name]);
                // Check if interface is en0 which is the wifi connection on the iPhone
                if([[NSString stringWithUTF8String:temp_addr->ifa_name] isEqualToString:@"en0"])
                {
                    address = [NSString stringWithUTF8String:inet_ntoa(((struct sockaddr_in *)temp_addr->ifa_addr)->sin_addr)];
                }
            }
            temp_addr = temp_addr->ifa_next;
        }
    }
    freeifaddrs(interfaces);
//    NSLog(@"UdpRequest getIPAddress = %@", address);
    return address;
}

@end
