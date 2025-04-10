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
        // TextUtils is used to check for null, will not cause NullPointerException unlike using value.isEmpty()
        if (TextUtils.isEmpty(value)) {
            return null;
        }
        try {
            // get yyyy-mm-dd format in String -> Date
            return DATE_FORMAT.parse(value);
        } catch (ParseException e) {
            // wrong format
            e.printStackTrace();
            return null;
        }
    }

    @TypeConverter
    public static String dateToTimestamp(Date date) {
        if (date == null) {
            return null;
        }
        // Convert Date in Date to String
        return DATE_FORMAT.format(date);
    }

    // Convert Mood into String
    @TypeConverter
    public static String fromMood(Mood mood) {
        return mood == null ? null : mood.name();
    }

    // Convert String back to Mood
    @TypeConverter
    public static Mood toMood(String name) {
        if (TextUtils.isEmpty(name)) {
            return Mood.NEUTRAL;
        }
        try {
            return Mood.valueOf(name);
        } catch (IllegalArgumentException e) {
            // Invalid String for mood
            e.printStackTrace();
            return Mood.NEUTRAL;
        }
    }
}