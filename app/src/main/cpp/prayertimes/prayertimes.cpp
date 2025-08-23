#include "prayertimes.h"
#include <cmath>
#include <algorithm>
#include <cstdio>
#include <string>
#include <chrono>
#include <sstream>
#include <iostream>

namespace isna {

    static inline double d2r(double d){ return d*M_PI/180.0; }
    static inline double r2d(double r){ return r*180.0/M_PI; }
    static inline double pmod(double a, double b){ return std::fmod(std::fmod(a,b)+b,b); }
    static inline double aclip(double x){ return std::max(-1.0, std::min(1.0, x)); }

    static int64_t days_from_civil(int y, unsigned m, unsigned d){
        y -= m<=2;
        const int era = (y>=0 ? y : y-399) / 400;
        const unsigned yoe = static_cast<unsigned>(y - era*400);
        const unsigned doy = (153*(m + (m>2?-3:9)) + 2)/5 + d-1;
        const unsigned doe = yoe*365 + yoe/4 - yoe/100 + doy;
        return era*146097 + static_cast<int>(doe) - 719468;
    }
    static int64_t utc_midnight_ms(int y, int m, int d){
        return days_from_civil(y,m,d) * 86400ll * 1000ll;
    }

    struct SunPos { double decl; double eqtHours; };

    static SunPos sun_position(double timeHours, int64_t utcMidnightMs, double lngDeg){
        const double D = (utcMidnightMs / 86400000.0) - 10957.5 + (timeHours/24.0) - (lngDeg/360.0);
        const double g = pmod(357.529 + 0.98560028*D, 360.0);
        const double q = pmod(280.459 + 0.98564736*D, 360.0);
        const double L = pmod(q + 1.915*std::sin(d2r(g)) + 0.020*std::sin(d2r(2*g)), 360.0);
        const double e = 23.439 - 0.00000036*D;
        const double RA = pmod(r2d(std::atan2(std::cos(d2r(e))*std::sin(d2r(L)), std::cos(d2r(L))))/15.0, 24.0);
        return { r2d(std::asin(std::sin(d2r(e))*std::sin(d2r(L)))), q/15.0 - RA };
    }

    static double midday(double guess, int64_t base, double lng){
        const double eqt = sun_position(guess, base, lng).eqtHours;
        return pmod(12.0 - eqt, 24.0);
    }

    static double angle_time(double angleDeg, double guess, int dir,
                             int64_t base, double lat, double lng)
    {
        const auto sp = sun_position(guess, base, lng);
        const double num = -std::sin(d2r(angleDeg)) - std::sin(d2r(lat))*std::sin(d2r(sp.decl));
        const double den =  std::cos(d2r(lat))*std::cos(d2r(sp.decl));
        const double arg = num/den;
        if (!std::isfinite(arg)) return std::numeric_limits<double>::quiet_NaN();
        const double h = r2d(std::acos(aclip(arg)))/15.0;
        return midday(guess, base, lng) + h*(dir>=0?1.0:-1.0);
    }

    static double asr_angle(double shadowFactor, double guess,
                            int64_t base, double lat, double lng)
    {
        const auto sp = sun_position(guess, base, lng);
        const double x = shadowFactor + std::tan(d2r(std::fabs(lat - sp.decl)));
        return -r2d(std::atan(1.0/x));
    }

    static int64_t round_minute(int64_t ms){
        const int64_t oneMin = 60000;
        return ((ms + oneMin/2)/oneMin)*oneMin;
    }

    static int64_t hour_to_epoch_ms(double hourLocalSolar, int64_t baseUtcMs, double lngDeg){
        const double corrected = hourLocalSolar - (lngDeg/15.0);
        const int64_t ms = baseUtcMs + static_cast<int64_t>(std::floor(corrected*3600000.0 + 0.5));
        return round_minute(ms);
    }

    static void high_lat_adjust(double sunrise, double sunset, double &fajr, double &isha, double &maghrib){
        const double night = 24.0 + sunrise - sunset;
        const double portion = 0.5 * night; // NightMiddle
        if (std::isnan(fajr) || (sunrise - fajr) > portion) fajr = sunrise - portion;
        if (std::isnan(isha) || (isha - sunset) > portion) isha = sunset + portion;
        if (std::isnan(maghrib)) maghrib = sunset; // ISNA: maghrib at sunset
    }

    PrayerTimes compute(int year, int month, int day,
                        double latDeg, double lngDeg, int utcOffsetMinutes,
                        const Config& cfg) //Main function for prayer calculations
    {
        const int64_t baseUtcMs = utc_midnight_ms(year, month, day);

        double fajr=5, sunrise=6, dhuhr=12, asr=13, sunset=18, maghrib=18, isha=18, midnight=24;

        fajr    = angle_time(cfg.fajrAngleDeg, fajr, -1, baseUtcMs, latDeg, lngDeg);
        dhuhr   = midday(dhuhr, baseUtcMs, lngDeg);
        {
            const double a = asr_angle(cfg.asrShadow, asr, baseUtcMs, latDeg, lngDeg);
            asr = angle_time(a, asr, +1, baseUtcMs, latDeg, lngDeg);
        }
        sunset  = angle_time(cfg.horizonDeg,   sunset, +1, baseUtcMs, latDeg, lngDeg);
        maghrib = sunset; // ISNA: no extra minutes
        isha    = angle_time(cfg.ishaAngleDeg, isha, +1, baseUtcMs, latDeg, lngDeg);
        midnight= midday(midnight, baseUtcMs, lngDeg) + 12.0;

        high_lat_adjust(sunrise, sunset, fajr, isha, maghrib);

        auto toLocal = [&](double hour)->int64_t{
            return hour_to_epoch_ms(hour, baseUtcMs, lngDeg) + int64_t(utcOffsetMinutes)*60000ll;
        };

        return PrayerTimes{
                toLocal(fajr), toLocal(dhuhr), toLocal(asr),
                toLocal(maghrib), toLocal(isha)
        };
    }

    //***********************************************************************************

    //Functions for widget display calculations

    static inline int64_t localTime(int utcOffsetMinutes) { //Capturing current time in epoch
        using namespace std::chrono;
        int64_t ms = duration_cast<milliseconds>(system_clock::now().time_since_epoch()).count();
        return ms + int64_t(utcOffsetMinutes) * 60000ll;
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

    static int colorCalc(int64_t time, int64_t current, int64_t next){ //Function for bg color calculation
        return 0xFF00FF00;
    }

    //**************************************************************************************************************

    //Main display function for widget

    PrayerDisplay widgetInfo(int off){
        int64_t timeNow = localTime(off);
        std::string currentPrayer;
        int64_t current, next;
        //PrayerTimes pt = compute()
        PrayerTimes pt = {1755856200000, 1755883200000, 1755899400000, 1755908400000, 1755915600000};
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
} // namespace isna

