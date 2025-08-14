#pragma once
#include <cstdint>
#include <string>

namespace isna {

    struct PrayerTimes {
        int64_t fajr;
        int64_t sunrise;
        int64_t dhuhr;
        int64_t asr;
        int64_t sunset;
        int64_t maghrib;
        int64_t isha;
        int64_t midnight;
    };

    struct Config {
        double fajrAngleDeg = 15.0;
        double ishaAngleDeg = 15.0;
        double horizonDeg   = 0.833;
        double asrShadow    = 1.0;    // Standard
        enum HighLat { NightMiddle } highLat = NightMiddle;
        bool roundNearestMinute = true;
    };

    PrayerTimes compute(int year, int month, int day,
                        double latDeg, double lngDeg,
                        int utcOffsetMinutes,
                        const Config& cfg = Config{});

    std::string hhmm_local(int64_t epochMs);

} // namespace isna
