package ru.sparural.engine.loymax.utils;

import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Locale;

/**
 * @author Vorobyev Vyacheslav
 */
public class LoymaxTimeToSparTimeAdapter {

    public static long convertToEpochSeconds(String loymaxTime) {
        var odt = OffsetDateTime.parse(loymaxTime);
        var date = odt.toLocalDateTime();
        return date.toEpochSecond(ZoneOffset.UTC);
    }

    public static String convertToUTC(long timestamp) {
        timestamp *= 1000;
        Date currentDate = new Date(timestamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ROOT);
        return dateFormat.format(currentDate);
    }
}