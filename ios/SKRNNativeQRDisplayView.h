//
//  SKRNNativeQRDisplayView.h
//  react-native-native-qr-display
//
//  Created by Switt Kongdachalert on 2/2/2565 BE.
//

#import "RCTViewManager.h"


@interface SKRNNativeQRDisplayView : RCTViewManager

@end

@interface SKRNNativeQRDisplayActualView : UIView
@property (retain, nonatomic) NSString *stringData;
@property (nonatomic) int ECC;
@end
