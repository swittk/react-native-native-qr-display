#include <jni.h>
#include "react-native-native-qr-display.h"
#include "qrcodegen.h"

jclass canvasClass;
jclass paintClass;
// drawRect (left, top, right, bottom, paint)
jmethodID drawRectMethod;
jmethodID setPaintARGBMethod;
extern "C"
JNIEXPORT jint JNICALL
Java_com_reactnativenativeqrdisplay_NativeQrDisplayModule_nativeMultiply(JNIEnv *env, jclass type, jint a, jint b) {
    return SKRNNativeQRDisplay::multiply(a, b);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_reactnativenativeqrdisplay_SKRNNativeQRDisplayView_drawQRIntoCanvas(JNIEnv *env,
                                                                             jobject thiz,
                                                                             jobject canvas,
                                                                             jobject,
                                                                             jint start_x,
                                                                             jint start_y,
                                                                             jint qr_width,
                                                                             jstring string_data,
                                                                             jint ecc) {
    // TODO: implement drawQRIntoCanvas()

}

extern "C"
jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    if(vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }
    jclass _canvasClass = static_cast<jclass>(env->NewGlobalRef(env->FindClass("android/graphics/Canvas")));
    canvasClass = _canvasClass;
    jclass _paintClass = static_cast<jclass>(env->NewGlobalRef(env->FindClass("android/graphics/Paint")));
    paintClass = _paintClass;
    drawRectMethod = env->GetMethodID(_canvasClass, "drawRect", "(FFFFLandroid/graphics/Paint;)V");
    setPaintARGBMethod = env->GetMethodID(_paintClass, "setARGB", "(IIII)V");
    env->GetMethodID(_paintClass, "setColor", "(J)V");
    return JNI_VERSION_1_6;
}

class QRCodeGenWrapper {
public:
    qrcodegen::QrCode qr;
    QRCodeGenWrapper(const char *str, qrcodegen::QrCode::Ecc ecc) :
    qr(qrcodegen::QrCode::encodeText(str, (qrcodegen::QrCode::Ecc)ecc)) {
    }

};


extern "C"
JNIEXPORT jlong JNICALL
Java_com_reactnativenativeqrdisplay_SKRNNativeQRDisplayView_createQRCodeCPPForStringData(
        JNIEnv *env, jobject thiz, jstring string_data, jint ecc) {
    jboolean isCopy = false;
    const char *str = env->GetStringUTFChars(string_data, &isCopy);
    try {
        QRCodeGenWrapper *wrapper = new QRCodeGenWrapper(str, (qrcodegen::QrCode::Ecc) ecc);
        return (jlong)wrapper;
    }
    catch(qrcodegen::data_too_long err) {
        printf("Error %s", err.what());
        return 0;
    }
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_reactnativenativeqrdisplay_SKRNNativeQRDisplayView_sanitizeECC(JNIEnv *env, jobject thiz,
                                                                        jint ECC) {
    switch (ECC) {
        case (int)qrcodegen::QrCode::Ecc::LOW:
        case (int)qrcodegen::QrCode::Ecc::QUARTILE:
        case (int)qrcodegen::QrCode::Ecc::MEDIUM:
        case (int)qrcodegen::QrCode::Ecc::HIGH:
            return ECC;
            break;
        default:
            return (int)qrcodegen::QrCode::Ecc::LOW;
            break;
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_reactnativenativeqrdisplay_SKRNNativeQRDisplayView_destroyQRCodeCPPForStringData(
        JNIEnv *env, jobject thiz, jlong qr_code_ptr) {
    QRCodeGenWrapper *wrapper = (QRCodeGenWrapper *)qr_code_ptr;
    delete wrapper;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_reactnativenativeqrdisplay_SKRNNativeQRDisplayView_QRCodeCPPGetSize(JNIEnv *env,
                                                                             jobject thiz,
                                                                             jlong qr_code_ptr) {
    QRCodeGenWrapper *wrapper = (QRCodeGenWrapper *)qr_code_ptr;
    return wrapper->qr.getSize();
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_reactnativenativeqrdisplay_SKRNNativeQRDisplayView_QRCodeCPPGetValue(JNIEnv *env,
                                                                              jobject thiz,
                                                                              jlong qr_code_ptr,
                                                                              jint x,
                                                                              jint y) {
    QRCodeGenWrapper *wrapper = (QRCodeGenWrapper *)qr_code_ptr;
    return wrapper->qr.getModule(x, y);
}
