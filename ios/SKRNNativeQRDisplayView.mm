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

static dispatch_queue_t qrDrawingQueue() {
    static dispatch_once_t queueCreationGuard;
    static dispatch_queue_t queue;
    dispatch_once(&queueCreationGuard, ^{
        queue = dispatch_queue_create("skrnnativeqrdisplay.backgroundQueue", 0);
    });
    return queue;
}


@implementation SKRNNativeQRDisplayActualView {
    qrcodegen::QrCode::Ecc _QRECC;
    CGContextRef asyncContext;
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
    if(!self->asyncContext) {
        return;
    }
    CGContextRef context = UIGraphicsGetCurrentContext();
    CGImageRef img = CGBitmapContextCreateImage(asyncContext);
    CGContextDrawImage(context, self.bounds, img);
    CGContextRelease(asyncContext);
    asyncContext = NULL;
    CGImageRelease(img);
    
}
-(void)render {
    if(!_stringData) return;
    CGSize s = self.bounds.size;
    if(s.width == 0 || s.height == 0) return;
    CGFloat w = MIN(s.width, s.height);
    CGColorRef blackColor = [UIColor blackColor].CGColor;
    CGColorRef whiteColor = [UIColor whiteColor].CGColor;
    dispatch_async(qrDrawingQueue(), ^{
        UIGraphicsBeginImageContextWithOptions(s, false, 0.0);
        CGContextRef ctx = UIGraphicsGetCurrentContext();
        CGPoint start = CGPointMake(s.width/2 - w/2, s.height/2 - w/2);
        try {
            qrcodegen::QrCode qr = qrcodegen::QrCode::encodeText([self->_stringData cStringUsingEncoding:NSUTF8StringEncoding], self->_QRECC);
            int qrSize = qr.getSize();
            CGFloat pxSize = w/(CGFloat)qrSize;
                        
            for(int y = 0; y < qrSize; y++) {
                for(int x = 0; x < qrSize; x++) {
                    CGRect rect = CGRectMake(
                                             start.x + x * pxSize,
                                             start.y + y * pxSize,
                                             pxSize, pxSize
                                             );
                    bool isBlack = qr.getModule(x, y);
                    if(isBlack) {
                        CGContextSetFillColorWithColor(ctx, blackColor);
                    }
                    else {
                        CGContextSetFillColorWithColor(ctx, whiteColor);
                    }
                    CGContextFillRect(ctx, rect);
                }
            }
            // Retain before assigning
            CGContextRetain(ctx);
            self->asyncContext = ctx;
        }
        catch(qrcodegen::data_too_long err) {
            NSLog(@"Error %s", err.what());
        }
        UIGraphicsEndImageContext();
        dispatch_async(dispatch_get_main_queue(), ^{
            [self setNeedsDisplay];
        });
    });
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
    [self render];
}
-(int)ECC {
    return (int)_QRECC;
}
-(void)setStringData:(NSString *)string {
//    NSLog(@"Set string %@", string);
    _stringData = string;
    [self render];
}
@end
