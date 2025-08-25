#pragma once
#include <cstdint>
#include <string>

namespace isna {

    struct PrayerTimes {
        int64_t fajr;
        int64_t dhuhr;
        int64_t asr;
        int64_t maghrib;
        int64_t isha;
    };

    struct Config {
        double fajrAngleDeg = 15.0;
        double ishaAngleDeg = 15.0;
        double horizonDeg   = 0.833;
        double asrShadow    = 1.0;
        enum HighLat { NightMiddle } highLat = NightMiddle;
        bool roundNearestMinute = true;
    };

    struct PrayerDisplay {
        std::string prayerName;
        std::string timeRemaining;
        const char* icon;
        const int bgColor;
    };

    PrayerTimes computeUTC(int year, int month, int day,
                           double latDeg, double lngDeg,
                           const Config& cfg);

    PrayerDisplay widgetInfo(PrayerTimes pt);

} // namespace isna
