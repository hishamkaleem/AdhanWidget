#include <jni.h>
#include "prayertimes/prayertimes.h"

extern "C"
JNIEXPORT jobject JNICALL
Java_com_widgetfiles_Native_NativeEngine_widgetInfoDisplay(
        JNIEnv* env, jclass,
        jint year, jint month, jint day,
        jdouble lat, jdouble lng,
        jint utcOffsetMinutes) {

    isna::PrayerDisplay disp = isna::widgetInfo((int)utcOffsetMinutes);

    jclass cls = env->FindClass("com/widgetfiles/widget/data/PrayerDisplay");

    jmethodID ctor = env->GetMethodID(
            cls,
            "<init>",
            "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)V"
    );

    jstring jName  = env->NewStringUTF(disp.prayerName.c_str());
    jstring jTime  = env->NewStringUTF(disp.timeRemaining.c_str());
    jstring jIcon  = env->NewStringUTF(disp.icon);

    jobject prayerObj = env->NewObject(
            cls, ctor,
            jName, jTime, jIcon, (jint)disp.bgColor
    );

    return prayerObj;
}
