package com.reactnativenativeqrdisplay;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.views.image.ReactImageView;
import com.facebook.react.views.view.ReactViewGroup;

public class SKRNNativeQRDisplayViewManager extends SimpleViewManager<SKRNNativeQRDisplayView> {
  public static final String REACT_CLASS = "SKRNNativeQRDisplayView";
  ReactApplicationContext mCallerContext;
  public SKRNNativeQRDisplayViewManager(ReactApplicationContext reactContext) {
    mCallerContext = reactContext;
  }

  @NonNull
  @Override
  public String getName() {
    return REACT_CLASS;
  }


  @Override
  public SKRNNativeQRDisplayView createViewInstance(ThemedReactContext context) {
    return new SKRNNativeQRDisplayView(context);
  }

  @ReactProp(name = "stringData")
  public void setStringData(SKRNNativeQRDisplayView view, String data) {
    view.setStringData(data);
  }
  @ReactProp(name = "ECC")
  public void setECC(SKRNNativeQRDisplayView view, int ECC) {
    view.setECC(ECC);
  }
}
