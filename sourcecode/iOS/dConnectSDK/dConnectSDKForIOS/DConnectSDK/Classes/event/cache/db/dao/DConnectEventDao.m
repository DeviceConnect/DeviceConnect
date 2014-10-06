//
//  DConnectEventDao.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectEventDao.h"

NSString *const DConnectEventDaoClmId = @"id";
NSString *const DConnectEventDaoClmCreateDate = @"create_date";
NSString *const DConnectEventDaoClmUpdateDate = @"update_date";

long long getCurrentTimeInMillis() {
    return (long long) [[NSDate date] timeIntervalSince1970];
}