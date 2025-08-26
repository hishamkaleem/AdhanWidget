#include "prayertimes.h"
#include <cmath>
#include <algorithm>
#include <cstdio>
#include <string>
#include <chrono>
#include <sstream>
#include <iostream>

namespace disp {

    //Functions for widget display calculations

    static inline int64_t nowUtc() {
        using namespace std::chrono;
        return duration_cast<milliseconds>(system_clock::now().time_since_epoch()).count();
    }

    static inline std::string formatDiff(int64_t diffMs) { //Formatting difference in ms
        if (diffMs <= 59'999) return "Now";
        int hours = int(diffMs / 3'600'000);
        int mins  = int((diffMs % 3'600'000) / 60'000);
        std::ostringstream oss; oss << hours << "h " << mins << "m";
        return oss.str();
    }

    static const char* iconFor(const std::string& name) {
        if (name == "Fajr")    return "🌄";
        if (name == "Dhuhr")   return "☀️";
        if (name == "Asr")     return "🕓";
        if (name == "Maghrib") return "🌇";
        if (name == "Isha")    return "🌙";
        return "🕒";
    }

    static int colorCalc(int64_t time, int64_t current, int64_t next) {
        const int64_t prayerDiff = next - current;

        int64_t timeLeft = next - time;
        if (timeLeft < 0) timeLeft = 0;

        double pct = double(timeLeft) / double(prayerDiff);
        if (pct > 1.0) pct = 1.0;
        if (pct < 0.0) pct = 0.0;

        int r, g, b = 0;

        if (pct >= 0.5) {
            double t = (1.0 - pct) / 0.5;
            r = int(0   + t * (255 - 0));
            g = int(255 - t * (255 - 165));
        }
        else {
            double t = (0.5 - pct) / 0.5;
            r = 255;
            g = int(165 - t * (165 - 0));
        }

        return (0xFF << 24) | (r << 16) | (g << 8) | b;
    }


    //Main display function for widget

    PrayerDisplay widgetInfo(PrayerTimes pt){
        int64_t timeNow = nowUtc();
        std::string currentPrayer;
        int64_t current, next;
        if (timeNow < pt.fajr){
            currentPrayer = "Isha";
            current = pt.isha;
            next = pt.fajr;
        }
        else if (timeNow < pt.dhuhr){
            currentPrayer = "Fajr";
            current = pt.fajr;
            next = pt.dhuhr;
        }
        else if (timeNow < pt.asr){
            currentPrayer = "Dhuhr";
            current = pt.dhuhr;
            next = pt.asr;
        }
        else if (timeNow < pt.maghrib){
            currentPrayer = "Asr";
            current = pt.asr;
            next = pt.maghrib;
        }
        else if (timeNow < pt.isha){
            currentPrayer = "Maghrib";
            current = pt.maghrib;
            next = pt.isha;
        }
        else{ //if still same day
            currentPrayer = "Isha";
            current = pt.isha;
            next = pt.fajr + 24ll*60*60*1000;
        }

        const char* prayerIcon = iconFor(currentPrayer); //icon

        const std::string timeRemaining = formatDiff(next-timeNow); //time remaining

        const int bgColor = colorCalc(timeNow, current, next); //color

        return {currentPrayer, timeRemaining, prayerIcon, bgColor};
    }
} // namespace disp

