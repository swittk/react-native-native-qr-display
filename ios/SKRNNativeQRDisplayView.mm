//
//  SKRNNativeQRDisplayView.m
//  react-native-native-qr-display
//
//  Created by Switt Kongdachalert on 2/2/2565 BE.
//

#import "SKRNNativeQRDisplayView.h"
#import "qrcodegen.h"

@implementation SKRNNativeQRDisplayView
RCT_EXPORT_MODULE(SKRNNativeQRDisplayView)
RCT_EXPORT_VIEW_PROPERTY(stringData, NSString);
RCT_EXPORT_VIEW_PROPERTY(ECC, int);
-(UIView *)view {
    return [SKRNNativeQRDisplayActualView new];
}
@end

@implementation SKRNNativeQRDisplayActualView {
    qrcodegen::QrCode::Ecc _QRECC;
}
-(id)initWithCoder:(NSCoder *)coder {
    self = [super initWithCoder:coder];
    if(!self) return nil;
    [self commonInit];
    return self;
}
-(id)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if(!self) return nil;
    [self commonInit];
    return self;
}
-(void)commonInit {
    self.backgroundColor = [UIColor clearColor];
    _QRECC = qrcodegen::QrCode::Ecc::LOW;
}
-(void)drawRect:(CGRect)rect {
    if(!_stringData) return;
    CGContextRef context = UIGraphicsGetCurrentContext();
    CGSize s = self.bounds.size;
    CGFloat w = MIN(s.width, s.height);
    CGPoint start = CGPointMake(s.width/2 - w/2, s.height/2 - w/2);
    try {
    qrcodegen::QrCode qr = qrcodegen::QrCode::encodeText([_stringData cStringUsingEncoding:NSUTF8StringEncoding], _QRECC);
        int qrSize = qr.getSize();
        CGFloat pxSize = w/(CGFloat)qrSize;
        
        CGColorRef blackColor = [UIColor blackColor].CGColor;
        CGColorRef whiteColor = [UIColor whiteColor].CGColor;
        
        for(int y = 0; y < qrSize; y++) {
            for(int x = 0; x < qrSize; x++) {
                CGRect rect = CGRectMake(
                                         start.x + x * pxSize,
                                         start.y + y * pxSize,
                                         pxSize, pxSize
                                         );
                bool isBlack = qr.getModule(x, y);
                if(isBlack) {
                    CGContextSetFillColorWithColor(context, blackColor);
                }
                else {
                    CGContextSetFillColorWithColor(context, whiteColor);
                }
                CGContextFillRect(context, rect);
            }
        }
    }
    catch(qrcodegen::data_too_long err) {
        NSLog(@"Error %s", err.what());
    }
}
-(void)setECC:(int)ECC {
    switch (ECC) {
        case (int)qrcodegen::QrCode::Ecc::LOW:
        case (int)qrcodegen::QrCode::Ecc::QUARTILE:
        case (int)qrcodegen::QrCode::Ecc::MEDIUM:
        case (int)qrcodegen::QrCode::Ecc::HIGH:
            _QRECC = (qrcodegen::QrCode::Ecc)ECC;
            break;
        default:
            _QRECC = qrcodegen::QrCode::Ecc::LOW;
            break;
    }
    [self setNeedsDisplay];
}
-(int)ECC {
    return (int)_QRECC;
}
-(void)setStringData:(NSString *)string {
//    NSLog(@"Set string %@", string);
    _stringData = string;
    [self setNeedsDisplay];
}
@end
