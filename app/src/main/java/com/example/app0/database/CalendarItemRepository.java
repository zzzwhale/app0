package com.example.app0.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.app0.database.CalendarItem;
import com.example.app0.database.CalendarItemDao;
import com.example.app0.database.CalendarItemDatabase;
import com.example.app0.database.Mood;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CalendarItemRepository {
    private final CalendarItemDao calendarItemDao;
    private final LiveData<List<CalendarItem>> allCalendarItems;
    private final ExecutorService executorService;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    public CalendarItemRepository(Application application) {
        // create database
        CalendarItemDatabase database = CalendarItemDatabase.getInstance(application);
        // call database method: reference to DAO
        calendarItemDao = database.getCalendarItemDao();
        // use DAO to get all data, Live automatically updates the app when there are changes
        allCalendarItems = calendarItemDao.getAllCalendarItemsLive();
        // manage background activities
        executorService = Executors.newSingleThreadExecutor();
    }

    // methods from DAO
    public LiveData<List<CalendarItem>> getAllCalendarItems() {
        return allCalendarItems;
    }

    public LiveData<CalendarItem> getCalendarItemByDate(int year, int month, int day) {
        // Create a date string in yyyy-MM-dd format
        String dateString = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day);
        return calendarItemDao.getCalendarItemByDateLive(dateString);
    }

    // background threads
    public void insert(CalendarItem calendarItem) {
        executorService.execute(() -> calendarItemDao.insertCalendarItem(calendarItem));
    }

    public void update(CalendarItem calendarItem) {
        executorService.execute(() -> calendarItemDao.updateCalendarItem(calendarItem));
    }

    public void delete(CalendarItem calendarItem) {
        executorService.execute(() -> calendarItemDao.deleteCalendarItem(calendarItem));
    }


    public void insertMoodEntry(int year, int month, int day, Mood mood, String notes) {
        // Create date (month is 0-based in Calendar)
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        Date date = calendar.getTime();

        // Create calendar item
        CalendarItem item = new CalendarItem(date, mood, notes);

        // Insert into database
        insert(item);
    }

    public List<CalendarItem> getAllCalendarItemsSync() {
        return calendarItemDao.getAllCalendarItems();
    }
}