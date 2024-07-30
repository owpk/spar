package ru.sparural.triggers.utils;

import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeHelper {

    public static Long currentTime() {
        return TimeUnit.SECONDS.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    public static Long minusDaysToEpoch(Integer days, Date startDate) {
        var currentDate = new java.sql.Date(startDate.getTime()).toLocalDate();
        var targetStartDate = currentDate.minusDays(days);
        return java.util.Date.from(targetStartDate.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant()).getTime() / 1000;
    }
}