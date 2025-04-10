package com.example.app0.moodtracker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.app0.R;
import com.example.app0.database.CalendarItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MoodCalendarView extends LinearLayout {
    private TextView monthYearText;
    private GridView calendarGrid;
    private CalendarAdapter adapter;
    private Calendar currentCalendar = Calendar.getInstance();
    private OnDateSelectedListener dateSelectedListener;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    // Map to store mood data: date string -> MoodEntry
    private Map<String, MoodEntry> moodEntries = new HashMap<>();

    public MoodCalendarView(Context context) {
        super(context);
        init(context);
    }

    public MoodCalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.calendar_layout, this, true);

        // Initialize views
        monthYearText = findViewById(R.id.month_year_text);
        calendarGrid = findViewById(R.id.calendar_grid);
        ImageButton prevButton = findViewById(R.id.prev_month_button);
        ImageButton nextButton = findViewById(R.id.next_month_button);

        // Set up month navigation
        prevButton.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, -1);
            updateCalendar();
        });

        nextButton.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, 1);
            updateCalendar();
        });

        // Set up calendar adapter
        adapter = new CalendarAdapter(context);
        calendarGrid.setAdapter(adapter);

        // Set date click listener
        calendarGrid.setOnItemClickListener((parent, view, position, id) -> {
            CalendarDate date = adapter.getItem(position);
            if (date.getMonth() == currentCalendar.get(Calendar.MONTH)) {
                String dateKey = getDateKey(date.getYear(), date.getMonth(), date.getDay());

                if (dateSelectedListener != null) {
                    dateSelectedListener.onDateSelected(
                            date.getYear(),
                            date.getMonth(), // This remains 0-based for Calendar
                            date.getDay(),
                            moodEntries.get(dateKey)
                    );
                }
            }
        });

        // Initialize with current month
        updateCalendar();
    }

    private void updateCalendar() {
        // Update month/year display
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        monthYearText.setText(dateFormat.format(currentCalendar.getTime()));

        // Update grid
        adapter.updateCalendar(currentCalendar, moodEntries);
    }

    public void setMoodEntry(int year, int month, int day, int moodResId, String notes) {
        String dateKey = getDateKey(year, month, day);
        MoodEntry entry = new MoodEntry(dateKey, moodResId, notes);
        moodEntries.put(dateKey, entry);
        updateCalendar();
    }

    public MoodEntry getMoodEntry(int year, int month, int day) {
        return moodEntries.get(getDateKey(year, month, day));
    }

    public void setOnDateSelectedListener(OnDateSelectedListener listener) {
        this.dateSelectedListener = listener;
    }

    // Interface for date selection callback
    public interface OnDateSelectedListener {
        void onDateSelected(int year, int month, int day, @Nullable MoodEntry moodEntry);
    }

    // Class for mood entries
    public static class MoodEntry {
        private String date; // Format: "yyyy-MM-dd"
        private final int moodResId;
        private final String notes;

        public MoodEntry(String date, int moodResId, String notes) {
            this.date = date;
            this.moodResId = moodResId;
            this.notes = notes;
        }

        public String getDate() {
            return date;
        }

        public int getMoodResId() {
            return moodResId;
        }

        public String getNotes() {
            return notes;
        }
    }
    public void removeMoodEntry(int year, int month, int day) {
        String dateKey = getDateKey(year, month, day);
        moodEntries.remove(dateKey);
        updateCalendar();
    }
    // Helper method to create consistent date keys
    private String getDateKey(int year, int month, int day) {
        return String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day);
    }

    // Update calendar with database items
    // Update calendar with database items
    public void updateWithCalendarItems(List<CalendarItem> items) {
        moodEntries.clear();
        if (items != null) {
            for (CalendarItem item : items) {
                Date date = item.getDate();
                if (date != null) {
                    String dateKey = DATE_FORMAT.format(date);
                    moodEntries.put(dateKey, new MoodEntry(
                            dateKey,
                            item.getMood().getIconResId(),
                            item.getEntry())
                    );
                } else {
                    // Log the issue for debugging
                    // You could also create a default date or skip this item
                    android.util.Log.w("MoodCalendarView", "Encountered CalendarItem with null date");
                }
            }
        }
        updateCalendar();
    }

    // Adapter for calendar grid
    private static class CalendarAdapter extends BaseAdapter {
        private final Context context;
        private final List<CalendarDate> cells = new ArrayList<>();
        private final LayoutInflater inflater;

        public CalendarAdapter(Context context) {
            this.context = context;
            this.inflater = LayoutInflater.from(context);
        }


        public void updateCalendar(Calendar calendar, Map<String, MoodEntry> moodEntries) {
            cells.clear();

            // Get start of month
            Calendar monthCalendar = (Calendar) calendar.clone();
            monthCalendar.set(Calendar.DAY_OF_MONTH, 1);
            int firstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK) - 1;

            // Fill cells
            monthCalendar.add(Calendar.DAY_OF_MONTH, -firstDayOfMonth);

            // Fill grid with days
            while (cells.size() < 42) {
                int day = monthCalendar.get(Calendar.DAY_OF_MONTH);
                int month = monthCalendar.get(Calendar.MONTH);
                int year = monthCalendar.get(Calendar.YEAR);

                // Format date key
                String dateKey = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day);
                MoodEntry entry = moodEntries.get(dateKey);

                cells.add(new CalendarDate(day, month, year, entry != null ? entry.getMoodResId() : 0));
                monthCalendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return cells.size();
        }

        @Override
        public CalendarDate getItem(int position) {
            return cells.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View cellView = convertView;
            if (cellView == null) {
                cellView = inflater.inflate(R.layout.day_cell_layout, parent, false);
            }

            // Get day information
            CalendarDate date = getItem(position);

            // Update views
            TextView dayText = cellView.findViewById(R.id.day_text);
            ImageView moodIcon = cellView.findViewById(R.id.mood_icon);

            // Set text
            dayText.setText(String.valueOf(date.getDay()));

            // Show mood icon if available
            if (date.getMoodResId() != 0) {
                moodIcon.setVisibility(View.VISIBLE);
                moodIcon.setImageResource(date.getMoodResId());
            } else {
                moodIcon.setVisibility(View.GONE);
            }

            return cellView;
        }
    }

    // Data class for calendar dates
    private static class CalendarDate {
        private final int day;
        private final int month;
        private final int year;
        private final int moodResId;

        public CalendarDate(int day, int month, int year, int moodResId) {
            this.day = day;
            this.month = month;
            this.year = year;
            this.moodResId = moodResId;
        }

        public int getDay() {
            return day;
        }

        public int getMonth() {
            return month;
        }

        public int getYear() {
            return year;
        }

        public int getMoodResId() {
            return moodResId;
        }
    }
}