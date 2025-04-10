package com.example.app0.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.Date;
import java.util.List;

@Dao
public interface CalendarItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    // same key? -> replace the old one
    void insertCalendarItem(CalendarItem item);

    @Update
    void updateCalendarItem(CalendarItem item);

    @Delete
    void deleteCalendarItem(CalendarItem item);

    @Query("SELECT * FROM calendarItems")
    List<CalendarItem> getAllCalendarItems();

    @Query("SELECT * FROM calendarItems")
    LiveData<List<CalendarItem>> getAllCalendarItemsLive();

    @Query("SELECT * FROM calendarItems WHERE date = :date")
    CalendarItem getCalendarItemByDate(String date);

    // date is like 2004-10-28

    @Query("SELECT * FROM calendarItems WHERE date = :date")
    LiveData<CalendarItem> getCalendarItemByDateLive(String date);

    @Query("SELECT mood, COUNT(*) as count FROM calendarItems " +
            "WHERE strftime('%Y-%m', date) = :yearMonth " +
            "GROUP BY mood")
    List<MoodCount> getMoodCountsForMonth(String yearMonth);

    class MoodCount {
        public Mood mood;
        public int count;
    }
}