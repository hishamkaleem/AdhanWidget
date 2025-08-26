#include <jni.h>
#include "prayer/prayertimes.h"
#include "prayer/prayercalc/prayercalc.h"

extern "C"
JNIEXPORT jobject JNICALL
Java_com_widgetfiles_Native_NativeEngine_widgetInfoDisplay(
        JNIEnv* env, jclass,
        jobject ptObj) {

    jclass ptCls = env->GetObjectClass(ptObj);
    jfieldID fjId = env->GetFieldID(ptCls, "fajr",    "J");
    jfieldID dhId = env->GetFieldID(ptCls, "dhuhr",   "J");
    jfieldID asId = env->GetFieldID(ptCls, "asr",     "J");
    jfieldID mgId = env->GetFieldID(ptCls, "maghrib", "J");
    jfieldID isId = env->GetFieldID(ptCls, "isha",    "J");

    disp::PrayerTimes pt{};
    pt.fajr    = static_cast<int64_t>(env->GetLongField(ptObj, fjId));
    pt.dhuhr   = static_cast<int64_t>(env->GetLongField(ptObj, dhId));
    pt.asr     = static_cast<int64_t>(env->GetLongField(ptObj, asId));
    pt.maghrib = static_cast<int64_t>(env->GetLongField(ptObj, mgId));
    pt.isha    = static_cast<int64_t>(env->GetLongField(ptObj, isId));

    disp::PrayerDisplay disp = disp::widgetInfo(pt);

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


extern "C"
JNIEXPORT jobject JNICALL
Java_com_widgetfiles_Native_NativeEngine_computeUTC(
        JNIEnv* env, jobject,
        jint year, jint month, jint day,
        jdouble lat, jdouble lng,
        jdouble fajr_angle, jdouble isha_angle,
        jdouble horizon_deg, jdouble asr_shadow) {

    isna::Config cfg{};
    cfg.fajrAngleDeg = static_cast<double>(fajr_angle);
    cfg.ishaAngleDeg = static_cast<double>(isha_angle);
    cfg.horizonDeg   = static_cast<double>(horizon_deg);
    cfg.asrShadow    = static_cast<double>(asr_shadow);

    const isna::PrayerTimes pt = isna::computeUTC(
            static_cast<int>(year),
            static_cast<int>(month),
            static_cast<int>(day),
            static_cast<double>(lat),
            static_cast<double>(lng),
            cfg
    );

    jclass cls = env->FindClass("com/widgetfiles/widget/data/PrayerTimes");
    if (!cls) return nullptr;

    jmethodID ctor = env->GetMethodID(cls, "<init>", "(JJJJJ)V");
    if (!ctor) return nullptr;

    jobject obj = env->NewObject(
            cls, ctor,
            static_cast<jlong>(pt.fajr),
            static_cast<jlong>(pt.dhuhr),
            static_cast<jlong>(pt.asr),
            static_cast<jlong>(pt.maghrib),
            static_cast<jlong>(pt.isha)
    );

    return obj;
}