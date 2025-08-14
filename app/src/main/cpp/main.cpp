#include <jni.h>
#include <array>
#include "prayertimes/prayertimes.h"

extern "C"
JNIEXPORT jlongArray JNICALL
Java_com_widgetfiles_Native_NativeEngine_computeISNA(
        JNIEnv* env, jclass,
        jint year, jint month, jint day,
        jdouble lat, jdouble lng,
        jint utcOffsetMinutes) {

    isna::PrayerTimes pt = isna::compute(
            (int)year, (int)month, (int)day,
            (double)lat, (double)lng,
            (int)utcOffsetMinutes
    );

    std::array<jlong,8> out = {
            pt.fajr, pt.sunrise, pt.dhuhr, pt.asr,
            pt.sunset, pt.maghrib, pt.isha, pt.midnight
    };
    jlongArray arr = env->NewLongArray((jsize)out.size());
    env->SetLongArrayRegion(arr, 0, (jsize)out.size(), out.data());
    return arr;
}
