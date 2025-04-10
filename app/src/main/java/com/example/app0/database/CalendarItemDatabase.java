package com.example.app0.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {CalendarItem.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class CalendarItemDatabase extends RoomDatabase {

    private static volatile CalendarItemDatabase INSTANCE;

    public abstract CalendarItemDao getCalendarItemDao();

    public static CalendarItemDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (CalendarItemDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    CalendarItemDatabase.class,
                                    "calendar-db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}