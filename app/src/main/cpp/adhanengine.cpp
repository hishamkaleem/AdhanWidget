#include <jni.h>
#include <string>

std::string WidgetMessage() {
    return "Sample Message";
}

extern "C" {
JNIEXPORT jstring JNICALL
Java_com_widgetfiles_Native_NativeEngine_WidgetMessage(
        JNIEnv* env, jobject) {
    std::string message = WidgetMessage();
    return env->NewStringUTF(message.c_str());
    }
}