package com.reactnativenativeqrdisplay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.widget.RelativeLayout;

import com.facebook.react.views.view.ReactViewGroup;

public class SKRNNativeQRDisplayView extends ReactViewGroup {
  public String _stringData;
  public int _ECC;
  Paint whitePaint = new Paint();
  Paint blackPaint = new Paint();
  Path whiteDrawPath = new Path();
  Path blackDrawPath = new Path();

  public SKRNNativeQRDisplayView(Context context) {
    super(context);
    setWillNotDraw(false);
    whitePaint.setColor(Color.WHITE);
    blackPaint.setColor(Color.BLACK);
//    new RelativeLayout.LayoutParams(
//      RelativeLayout.LayoutParams.WRAP_CONTENT,
//      RelativeLayout.LayoutParams.WRAP_CONTENT
//      );
  }
  public void setStringData(String data) {
    _stringData = data;
    invalidate();
  }
  public void setECC(int ECC) {
    _ECC = sanitizeECC(ECC);
    invalidate();
  }

  native void drawQRIntoCanvas(Canvas canvas, Paint paint, int startX, int startY, int qrWidth, String stringData, int ECC);
  native long createQRCodeCPPForStringData(String stringData, int ECC);
  native void destroyQRCodeCPPForStringData(long qrCodePtr);
  native int QRCodeCPPGetSize(long qrCodePtr);
  native boolean QRCodeCPPGetValue(long qrCodePtr, int x, int y);
  native int sanitizeECC(int ECC);

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    int width = getWidth();
    int height = getHeight();
    int w = Math.min(width, height);
    float sX = (float)width/2.0f - (float)w/2.0f;
    float sY = (float)height/2.0f - (float)w/2.0f;
    long qrCodePtr = createQRCodeCPPForStringData(_stringData, _ECC);
    if(qrCodePtr == 0) {
      // Error occured;
      return;
    }
    int size = QRCodeCPPGetSize(qrCodePtr);
    float pxSize = (float)width/(float)size;

    whiteDrawPath.rewind();
    blackDrawPath.rewind();
    for(int y = 0; y < size; y++) {
      for(int x = 0; x < size; x++) {
        boolean val = QRCodeCPPGetValue(qrCodePtr, x, y);
        if(val) {
          blackDrawPath.addRect(
            sX + x * pxSize,
            sY + y * pxSize,
            sX + (x + 1) * pxSize,
            sY + (y + 1) * pxSize,
            Path.Direction.CW
          );
        }
        else {
          whiteDrawPath.addRect(
            sX + x * pxSize,
            sY + y * pxSize,
            sX + (x + 1) * pxSize,
            sY + (y + 1) * pxSize,
            Path.Direction.CW
            );
        }
      }
    }
    canvas.drawPath(whiteDrawPath, whitePaint);
    canvas.drawPath(blackDrawPath, blackPaint);
    destroyQRCodeCPPForStringData(qrCodePtr);
  }
}
