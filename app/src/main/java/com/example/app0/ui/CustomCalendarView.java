package com.example.app0.ui;
// Customise calendar to show mood icon + user selection interaction

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;

// this that built in function
import android.view.View;
import android.widget.CalendarView;

import com.example.app0.moodtracker.MoodCalendarView;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// Extends CalendarView
public abstract class CustomCalendarView extends CalendarView {
    private Map<Long, String> moodMap = new HashMap<>(); // Map to store mood icons for specific dates

    public CustomCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setMoodForDate(long dateInMillis, String mood) {
        moodMap.put(dateInMillis, mood);  // Store the mood for the selected date
        invalidate(); // Redraw the view
    }

    // Correctly listen for date selection changes
    public void initializeDateChangeListener() {
        setOnDateChangeListener(new OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                long selectedDateInMillis = view.getDate();
                String mood = moodMap.get(selectedDateInMillis);

                if (mood != null) {
                    // TODO: Implement logic to show mood icon
                    onDateSelectedListener.onDateSelected(selectedDateInMillis);
                }
            }
        });
    }


    public void setDateSelectedListener(OnDateSelectedListener listener) {
        // Allow other classes listen to selection events
        // e.g. the user taps on a date then this listener can trigger actions like saving the mood, updating calendar
    }

    // Override cell drawing to show mood icons
    protected abstract void onDrawCell(Canvas canvas, RectF rect, int day, boolean isToday,
                                       boolean isSelected, boolean isOutOfMonth);


    public interface OnDateSelectedListener {
        void onDateSelected(long dateInMillis);
    }

    private OnDateSelectedListener onDateSelectedListener;


    /*
    private MoodCalendarView.OnDateClickListener onDateClickListener;

    public void setOnDateClickListener(MoodCalendarView.OnDateClickListener listener) {
        this.onDateClickListener = listener;
    }
    /

    // Trigger listener when a date is selected
    @Override
    public void setOnDateChangeListener(OnDateChangeListener listener) {
        super.setOnDateChangeListener(new OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                if (onDateClickListener != null) {
                    // Convert selected date to Date object
                    Date selectedDate = new Date(view.getDate());
                    onDateClickListener.onDateClick(view, selectedDate);
                }

                // Call the original listener if provided
                if (listener != null) {
                    listener.onSelectedDayChange(view, year, month, dayOfMonth);
                }
            }


        });
    }
*/
}

