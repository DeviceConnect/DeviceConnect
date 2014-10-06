#import "ItemBridge.h"

@implementation ItemBridge
- (id)copyWithZone:(NSZone *)zone
{
    ItemBridge *copiedObject = [[[self class] allocWithZone:zone] init];
    if (copiedObject) {
        copiedObject->_macAdress = [_macAdress copyWithZone:zone];
        copiedObject->_ipAdress = [_ipAdress copyWithZone:zone];
    }
    return copiedObject;
}
@end
