package com.example.app0.database;

import android.text.TextUtils;

import androidx.room.TypeConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Converters {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @TypeConverter
    public static Date fromTimestamp(String value) {
        if (TextUtils.isEmpty(value)) {
            return null;
        }
        try {
            return DATE_FORMAT.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @TypeConverter
    public static String dateToTimestamp(Date date) {
        if (date == null) {
            return null;
        }
        return DATE_FORMAT.format(date);
    }

    @TypeConverter
    public static String fromMood(Mood mood) {
        return mood == null ? null : mood.name();
    }

    @TypeConverter
    public static Mood toMood(String name) {
        if (TextUtils.isEmpty(name)) {
            return Mood.NEUTRAL;
        }
        try {
            return Mood.valueOf(name);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return Mood.NEUTRAL;
        }
    }
}