//
//  DConnectEventDao.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#define DCEForm(...) [NSString stringWithFormat:__VA_ARGS__]

extern NSString *const DConnectEventDaoClmId;
extern NSString *const DConnectEventDaoClmCreateDate;
extern NSString *const DConnectEventDaoClmUpdateDate;

long long getCurrentTimeInMillis();