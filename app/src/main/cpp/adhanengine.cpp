#include <jni.h>

extern "C"{
    JNIEXPORT jfloat JNICALL
    Java_com_widgetfiles_native_NativeEngine_getQiblaDirection(
        JNIEnv* env, jobject, jdouble lat, jdouble lon) {
        return 1;
    }
}
