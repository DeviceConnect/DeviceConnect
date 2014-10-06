//
//  Page.h
//  Browser
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class Page;

@interface Page : NSManagedObject

@property (nonatomic, retain) NSString * category;
@property (nonatomic, retain) NSDate * created_date;
@property (nonatomic, retain) NSString * identifier;
@property (nonatomic, retain) NSNumber * priority;
@property (nonatomic, retain) NSString * sectionIndex;
@property (nonatomic, retain) NSString * title;
@property (nonatomic, retain) NSString * type;
@property (nonatomic, retain) NSString * url;
@property (nonatomic, retain) NSOrderedSet *children;
@property (nonatomic, retain) Page *parent;
@end

@interface Page (CoreDataGeneratedAccessors)

- (void)insertObject:(Page *)value inChildrenAtIndex:(NSUInteger)idx;
- (void)removeObjectFromChildrenAtIndex:(NSUInteger)idx;
- (void)insertChildren:(NSArray *)value atIndexes:(NSIndexSet *)indexes;
- (void)removeChildrenAtIndexes:(NSIndexSet *)indexes;
- (void)replaceObjectInChildrenAtIndex:(NSUInteger)idx withObject:(Page *)value;
- (void)replaceChildrenAtIndexes:(NSIndexSet *)indexes withChildren:(NSArray *)values;
- (void)addChildrenObject:(Page *)value;
- (void)removeChildrenObject:(Page *)value;
- (void)addChildren:(NSOrderedSet *)values;
- (void)removeChildren:(NSOrderedSet *)values;
@end
