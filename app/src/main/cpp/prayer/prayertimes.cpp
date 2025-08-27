#include "prayertimes.h"
#include <cmath>
#include <algorithm>
#include <cstdio>
#include <string>
#include <chrono>
#include <sstream>
#include <iostream>

namespace disp {

    static inline int64_t nowUtc() {
        using namespace std::chrono;
        return duration_cast<milliseconds>(system_clock::now().time_since_epoch()).count();
    }

    static inline std::string formatDiff(int64_t diffMs) {
        if (diffMs <= 59'999) return "Now";
        int hours = int(diffMs / 3'600'000);
        int mins  = int((diffMs % 3'600'000) / 60'000);
        std::ostringstream oss; oss << hours << "h " << mins << "m";
        return oss.str();
    }

    static int colorCalc(int64_t time, int64_t current, int64_t next) {
        int64_t prayerDiff = next - current;
        if (prayerDiff <= 0) prayerDiff = 1;

        int64_t timeLeft = next - time;
        if (timeLeft < 0) timeLeft = 0;

        double pct = double(timeLeft) / double(prayerDiff);
        if (pct > 1.0) pct = 1.0;
        if (pct < 0.0) pct = 0.0;

        int r = 0, g = 0, b = 0;
        if (pct >= 0.5) {
            double t = (1.0 - pct) / 0.5;
            r = int(255 * t);
            g = int(255 - t * 90);
        } else {
            double t = (0.5 - pct) / 0.5;
            r = 255;
            g = int(165 - t * 165);
        }
        return (0xFF << 24) | (r << 16) | (g << 8) | b;
    }


    // Main prayer display function

    PrayerDisplay widgetInfo(PrayerTimes pt) {
        const int64_t DAY = 24ll * 60 * 60 * 1000;
        int64_t timeNow = nowUtc();

        std::string currentPrayer;
        int64_t current = 0, next = 0;

        if (timeNow < pt.fajr) {
            currentPrayer = "Isha";
            current = pt.isha - DAY;
            next = pt.fajr;
        }
        else if (timeNow < pt.sunrise){
            currentPrayer = "Fajr";
            current = pt.fajr;
            next = pt.sunrise;
        }
        else if (timeNow < pt.dhuhr) {
            currentPrayer = "Sunrise";
            current = pt.sunrise;
            next = pt.dhuhr;
        }
        else if (timeNow < pt.asr) {
            currentPrayer = "Dhuhr";
            current = pt.dhuhr;
            next = pt.asr;
        }
        else if (timeNow < pt.maghrib) {
            currentPrayer = "Asr";
            current = pt.asr;
            next = pt.maghrib;
        }
        else if (timeNow < pt.isha) {
            currentPrayer = "Maghrib";
            current = pt.maghrib;
            next = pt.isha;
        }
        else {
            currentPrayer = "Isha";
            current = pt.isha;
            next = pt.fajr + DAY;
        }

        const std::string timeRemaining = formatDiff(next - timeNow);
        const int bgColor = colorCalc(timeNow, current, next);

        return { currentPrayer, timeRemaining, bgColor };
    }
}
