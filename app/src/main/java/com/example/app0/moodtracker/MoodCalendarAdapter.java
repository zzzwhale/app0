package com.example.app0.moodtracker;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.app0.R;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MoodCalendarAdapter extends BaseAdapter {
    private Context context;
    private Calendar currentCalendar;
    private Map<Long, String> moodData = new HashMap<>();
    private int selectedPosition = -1;

    // Number of days to display (7 days * 6 weeks)
    private static final int DAYS_COUNT = 42;

    // Mapping for mood colors
    private static final int[] MOOD_BACKGROUND_COLORS = {
            R.color.mood_neutral_bg,  // Default/placeholder
            R.color.mood_very_sad_bg,    // 1: Very sad - dark gray
            R.color.mood_sad_bg,         // 2: Sad - dark green
            R.color.mood_neutral_bg,     // 3: Neutral - medium green
            R.color.mood_happy_bg,       // 4: Happy - light green
            R.color.mood_neutral_dark_bg, // 5: Neutral dark - dark gray
            R.color.mood_very_happy_bg,  // 6: Very happy - yellow
            R.color.mood_neutral_light_bg // 7: Neutral light - light gray
    };

    public MoodCalendarAdapter(Context context, Calendar calendar) {
        this.context = context;
        this.currentCalendar = (Calendar) calendar.clone();
        // Set calendar to first day of the month
        this.currentCalendar.set(Calendar.DAY_OF_MONTH, 1);
    }

    @Override
    public int getCount() {
        return DAYS_COUNT;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Create view if it doesn't exist
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_calendar_day, parent, false);
        }

        // Get UI components
        TextView dayText = convertView.findViewById(R.id.day_text);
        FrameLayout dayContainer = convertView.findViewById(R.id.day_container);
        ImageView moodIcon = convertView.findViewById(R.id.mood_icon);

        // Reset view
        dayContainer.setBackgroundResource(0);
        moodIcon.setVisibility(View.VISIBLE); // Set to VISIBLE by default, we'll style it appropriately

        // Calculate day for this position
        Calendar calendar = (Calendar) currentCalendar.clone();
        int firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        calendar.add(Calendar.DAY_OF_MONTH, position - firstDayOfMonth);

        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Set day number text
        dayText.setText(String.valueOf(dayOfMonth));

        // Check if day is in current month
        boolean isCurrentMonth = calendar.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH);

        // Check if we have mood data for this date
        long dateMillis = getDayMillis(calendar);
        String moodId = moodData.get(dateMillis);

        if (!isCurrentMonth) {
            // Days not in current month - make them gray circles with dimmed text
            moodIcon.setBackgroundResource(R.drawable.mood_background_empty);
            dayText.setAlpha(0.3f);
            moodIcon.setImageResource(0); // No face for out-of-month days
        } else {
            dayText.setAlpha(1.0f);

            if (moodId != null) {
                // We have mood data - apply appropriate mood style
                try {
                    int moodValue = Integer.parseInt(moodId);

                    // Set background color from the mood palette
                    int colorId = MOOD_BACKGROUND_COLORS[Math.min(moodValue, MOOD_BACKGROUND_COLORS.length - 1)];
                    moodIcon.setBackgroundResource(R.drawable.mood_background_filled);
                    moodIcon.setBackgroundTintList(ContextCompat.getColorStateList(context, colorId));

                    // Set the emoji
                    moodIcon.setImageResource(getMoodFaceResource(moodValue));
                } catch (NumberFormatException e) {
                    // If mood value is invalid, show empty
                    moodIcon.setBackgroundResource(R.drawable.mood_background_empty);
                    moodIcon.setImageResource(0);
                }
            } else {
                // No mood for this date - show empty
                moodIcon.setBackgroundResource(R.drawable.mood_background_empty);
                moodIcon.setImageResource(0);
            }
        }

        // Today indicator
        Calendar today = Calendar.getInstance();
        if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                calendar.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)) {
            dayContainer.setBackgroundResource(R.drawable.today_background);
        }

        // Selected day indicator (e.g. current date selected for entry)
        if (position == selectedPosition) {
            dayContainer.setBackgroundResource(R.drawable.selected_day_background);
        }

        return convertView;
    }

    // Helper method to get milliseconds for day (hours/minutes/seconds)
    private long getDayMillis(Calendar calendar) {
        Calendar dayCal = (Calendar) calendar.clone();
        dayCal.set(Calendar.HOUR_OF_DAY, 0);
        dayCal.set(Calendar.MINUTE, 0);
        dayCal.set(Calendar.SECOND, 0);
        dayCal.set(Calendar.MILLISECOND, 0);
        return dayCal.getTimeInMillis();
    }

    // Get mood face icon resource based on mood ID
    private int getMoodFaceResource(int moodValue) {
        switch (moodValue) {
            case 1: return R.drawable.mood_very_sad;
            case 2: return R.drawable.mood_sad;
            case 3: return R.drawable.mood_neutral;
            case 4: return R.drawable.mood_happy;
            case 5: return R.drawable.mood_very_happy;
            default: return R.drawable.mood_neutral;
        }
    }

    public void setMoodForDate(long dateTimeMillis, String moodId) {
        moodData.put(dateTimeMillis, moodId);
        notifyDataSetChanged();
    }

    public void updateCalendar(Calendar calendar) {
        this.currentCalendar = (Calendar) calendar.clone();
        this.currentCalendar.set(Calendar.DAY_OF_MONTH, 1);
        notifyDataSetChanged();
    }

    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
        notifyDataSetChanged();
    }
}