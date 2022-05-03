package com.reactnativenativeqrdisplay;

import static com.facebook.react.bridge.UiThreadUtil.runOnUiThread;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.widget.RelativeLayout;

import com.facebook.react.views.view.ReactViewGroup;

import android.app.Activity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SKRNNativeQRDisplayView extends ReactViewGroup {
  public String _stringData;
  public int _ECC;
  Paint whitePaint = new Paint();
  Paint blackPaint = new Paint();
  Path whiteDrawPath = new Path();
  Path blackDrawPath = new Path();

  int lastRenderWidth;
  int lastRenderHeight;
  Bitmap asyncBitmap = null;
  ExecutorService executor = Executors.newSingleThreadExecutor();

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
    render();
  }

  public void setECC(int ECC) {
    _ECC = sanitizeECC(ECC);
    render();
  }

  native void drawQRIntoCanvas(Canvas canvas, Paint paint, int startX, int startY, int qrWidth, String stringData, int ECC);

  native long createQRCodeCPPForStringData(String stringData, int ECC);

  native void destroyQRCodeCPPForStringData(long qrCodePtr);

  native int QRCodeCPPGetSize(long qrCodePtr);

  native boolean QRCodeCPPGetValue(long qrCodePtr, int x, int y);

  native int sanitizeECC(int ECC);

  void render() {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        generateAsyncBitmap();
        //Background work here
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            //do something
            invalidate();
          }
        });
      }
    });
  }

  void generateAsyncBitmap() {
    int width = getWidth();
    int height = getHeight();
    int w = Math.min(width, height);
    if (w <= 0) return;
    Bitmap.Config conf = Bitmap.Config.ARGB_8888;
    // "The default density is the same density as the current display"
    // (From https://developer.android.com/reference/android/graphics/Bitmap)
    // So hopefully no need to provide density here
    Bitmap bmp = Bitmap.createBitmap(width, height, conf);
    Canvas canvas = new Canvas(bmp);

    float sX = (float) width / 2.0f - (float) w / 2.0f;
    float sY = (float) height / 2.0f - (float) w / 2.0f;
    long qrCodePtr = createQRCodeCPPForStringData(_stringData, _ECC);
    if (qrCodePtr == 0) {
      // Error occurred;
      return;
    }
    int size = QRCodeCPPGetSize(qrCodePtr);
    float pxSize = (float) width / (float) size;

    whiteDrawPath.rewind();
    blackDrawPath.rewind();
    for (int y = 0; y < size; y++) {
      for (int x = 0; x < size; x++) {
        boolean val = QRCodeCPPGetValue(qrCodePtr, x, y);
        if (val) {
          blackDrawPath.addRect(
            sX + x * pxSize,
            sY + y * pxSize,
            sX + (x + 1) * pxSize,
            sY + (y + 1) * pxSize,
            Path.Direction.CW
          );
        } else {
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
    asyncBitmap = bmp;
    lastRenderHeight = height;
    lastRenderWidth = width;
  }


  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if (getHeight() == lastRenderHeight && getWidth() == lastRenderWidth) {
      if (asyncBitmap == null) return;
      canvas.drawBitmap(asyncBitmap, null, new Rect(0, 0, getWidth(), getHeight()), null);
    }
    else {
      render();
    }
  }
}
