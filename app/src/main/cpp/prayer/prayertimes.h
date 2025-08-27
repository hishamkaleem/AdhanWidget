#pragma once
#include <cstdint>
#include <string>

namespace disp {

    struct PrayerTimes {
        int64_t fajr;
        int64_t sunrise;
        int64_t dhuhr;
        int64_t asr;
        int64_t maghrib;
        int64_t isha;
    };

    struct PrayerDisplay {
        std::string prayerName;
        std::string timeRemaining;
        const int bgColor;
    };


    PrayerDisplay widgetInfo(PrayerTimes pt);

} // namespace disp
