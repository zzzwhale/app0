package com.example.app0.database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.app0.database.CalendarItem;
import com.example.app0.database.Mood;
import com.example.app0.database.CalendarItemRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CalendarItemViewModel extends AndroidViewModel {
    private final com.example.app0.database.CalendarItemRepository repository;
    private final LiveData<List<CalendarItem>> allCalendarItems;
    private final ExecutorService executorService;

    // Selected date information
    private final MutableLiveData<Integer> selectedYear = new MutableLiveData<>();
    private final MutableLiveData<Integer> selectedMonth = new MutableLiveData<>();
    private final MutableLiveData<Integer> selectedDay = new MutableLiveData<>();

    public CalendarItemViewModel(@NonNull Application application) {
        super(application);
        repository = new CalendarItemRepository(application);
        allCalendarItems = repository.getAllCalendarItems();
        executorService = Executors.newSingleThreadExecutor();

        // Initialize with current date
        Calendar calendar = Calendar.getInstance();
        selectedYear.setValue(calendar.get(Calendar.YEAR));
        selectedMonth.setValue(calendar.get(Calendar.MONTH));
        selectedDay.setValue(calendar.get(Calendar.DAY_OF_MONTH));
    }

    public LiveData<List<CalendarItem>> getAllCalendarItems() {
        return allCalendarItems;
    }

    public LiveData<CalendarItem> getSelectedDateMood() {
        Integer year = selectedYear.getValue();
        Integer month = selectedMonth.getValue();
        Integer day = selectedDay.getValue();

        if (year == null || month == null || day == null) {
            return new MutableLiveData<>(null);
        }

        return repository.getCalendarItemByDate(year, month, day);
    }

    public void setSelectedDate(int year, int month, int day) {
        selectedYear.setValue(year);
        selectedMonth.setValue(month);
        selectedDay.setValue(day);
    }

    public LiveData<Integer> getSelectedYear() {
        return selectedYear;
    }

    public LiveData<Integer> getSelectedMonth() {
        return selectedMonth;
    }

    public LiveData<Integer> getSelectedDay() {
        return selectedDay;
    }

    public void saveMoodEntry(int moodResId, String notes) {
        Integer year = selectedYear.getValue();
        Integer month = selectedMonth.getValue();
        Integer day = selectedDay.getValue();

        if (year == null || month == null || day == null) {
            return;
        }

        // Convert resource ID to Mood enum
        Mood mood = Mood.fromResId(moodResId);

        // Save mood entry
        repository.insertMoodEntry(year, month, day, mood, notes);
    }

    public void updateCalendarItem(CalendarItem item) {
        repository.update(item);
    }

    public void deleteCalendarItem(CalendarItem item) {
        repository.delete(item);
    }

    public List<CalendarItem> getAllCalendarItemsSync() {
        final List<CalendarItem>[] result = new List[1];
        executorService.execute(() -> {
            result[0] = repository.getAllCalendarItemsSync();
        });

        // Wait for result (not ideal, but simple for now)
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result[0];
    }
}