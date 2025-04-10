package com.example.app0.database;

import androidx.room.TypeConverter;

public class MoodConverter {
    @TypeConverter
    public static Mood toMood(String value) {
        return value == null ? null : Mood.valueOf(value);
    }

    @TypeConverter
    public static String fromMood(Mood mood) {
        return mood == null ? null : mood.name();
    }
}