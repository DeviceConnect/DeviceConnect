//
//  LocalOAuthProfileDao.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "LocalOAuthProfileDao.h"
#import "DConnectEventDao.h"
#import "LocalOAuthSQLiteProfile.h"

NSString *const LocalOAuthProfileDaoTableName = @"profiles";
NSString *const LocalOAuthProfileDaoId = @"id";
NSString *const LocalOAuthProfileDaoProfileName = @"profile_name";
NSString *const LocalOAuthProfileDaoDescription = @"description";

@interface LocalOAuthProfileDao() {
    
}

+ (NSArray *) loadProfiles: (NSString *)where
                  database: (DConnectSQLiteDatabase *)database;

@end



@implementation LocalOAuthProfileDao

+ (void) createWithDatabase:(DConnectSQLiteDatabase *)database {
    
    NSString *sql = DCEForm(@"CREATE TABLE %@ ("
                            @"%@ INTEGER PRIMARY KEY AUTOINCREMENT"
                            @", %@ TEXT"
                            @", %@ TEXT"
                            @");",
                            LocalOAuthProfileDaoTableName,
                            LocalOAuthProfileDaoId,
                            LocalOAuthProfileDaoProfileName,
                            LocalOAuthProfileDaoDescription);
    
    if (![database execSQL:sql]) {
        @throw @"error";
    }
}

+ (long long) insertWithName:(NSString *)name toDatabase:(DConnectSQLiteDatabase *)database
{
    long long result = [database insertIntoTable:LocalOAuthProfileDaoTableName
                               columns:@[LocalOAuthProfileDaoProfileName]
                                params:@[name]];
    
    return result;
}

/* public */
+ (NSArray *) load: (DConnectSQLiteDatabase *) database {
    
    NSArray *profiles = [self loadProfiles: nil
                                  database: database];
    return profiles;
}

/* public */
+ (LocalOAuthSQLiteProfile *) findByProfileName: (NSString *)name
                                 database:(DConnectSQLiteDatabase *)database {
    
    NSString *where = [NSString stringWithFormat: @"%@='%@'",
                       LocalOAuthProfileDaoProfileName, name];
    
    NSArray *profiles = [self loadProfiles: where database:database];
    if (profiles == nil || [profiles count] == 0) {
        return nil;
    } else if ([profiles count] == 1) {
        return profiles[0];
    } else {
        @throw @"同じプロファイル名が2件以上のプロファイルデータに設定されています。";
    }
}





/* private */
+ (NSArray *) loadProfiles: (NSString *)where
                  database: (DConnectSQLiteDatabase *)database {
    
    NSMutableArray *profiles = nil;
    
    NSString *sql =
        where != nil ?
            [NSString stringWithFormat:
                @"select"
                @"  %@"
                @", %@"
                @", %@"
                @" from %@"
                @" where %@",
                LocalOAuthProfileDaoId,
                LocalOAuthProfileDaoProfileName,
                LocalOAuthProfileDaoDescription,
                LocalOAuthProfileDaoTableName,
                where
            ] :
            [NSString stringWithFormat:
                @"select"
                @"  %@"
                @", %@"
                @", %@"
                @" from %@",
                LocalOAuthProfileDaoId,
                LocalOAuthProfileDaoProfileName,
                LocalOAuthProfileDaoDescription,
                LocalOAuthProfileDaoTableName
            ];
    DConnectSQLiteCursor *cursor = [database queryWithSQL: sql];
    if (!cursor) {
        return profiles;
    }
    
    if ([cursor moveToFirst]) {
        profiles = [NSMutableArray array];
        do {
            LocalOAuthSQLiteProfile *profile = [[LocalOAuthSQLiteProfile alloc]init];
            
            profile.id_ = [cursor longLongValueAtIndex:0];
            profile.profileName = [cursor stringValueAtIndex:1];
            profile.profileDescription = [cursor stringValueAtIndex:2];
            
            [profiles addObject:profile];
        } while ([cursor moveToNext]);
    }
    
    [cursor close];
    
    return profiles;
}


@end
