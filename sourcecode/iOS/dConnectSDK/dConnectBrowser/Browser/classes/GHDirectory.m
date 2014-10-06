//
//  GHDirectory.m
//  Browser
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "GHDirectory.h"

@implementation GHDirectory


+ (GHPageModel*)checkDirectory:(Page*)page
{
    GHPageModel* model = [self copyPageModel:page];
    NSArray* children = [self findDirectory:page.children];
    if (children) {
        model.children = children;
    }
    return model;
}



+ (NSArray*)findDirectory:(NSOrderedSet*)set
{
    //子階層にフォルダがあるかチェック
    if ([self childrenFolderCount:set] > 0) {
        NSMutableArray* data = [[NSMutableArray alloc]init];
        for (Page* child in set ) {
            
            //フォルダのみ取得
            if([child.type isEqualToString:TYPE_FOLDER]){
                GHPageModel* result = [self checkDirectory:child];
                if (result) {
                    [data addObject:result];
                }
            }
        }
        return data;
    }else{
        return nil;
    }
    
    return nil;
}


+ (GHPageModel*)copyPageModel:(Page*)page
{
    GHPageModel* model = [[GHPageModel alloc]init];
    model.title = page.title;
    model.type = page.type;
    model.identifier = page.identifier;
    model.priority = page.priority;
    return model;
}


///フォルダの数を返す
+ (NSInteger)childrenFolderCount:(NSOrderedSet*)set
{
    NSInteger count = 0;
    for (Page* page in set) {
        if ([page.type isEqualToString:TYPE_FOLDER]) {
            count++;
        }
    }
    
    return count;
}


@end
