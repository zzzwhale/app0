// In Mood.java
package com.example.app0.database;

import com.example.app0.R;

public enum Mood {
    VERY_SAD(R.drawable.mood_very_sad),
    SAD(R.drawable.mood_sad),
    NEUTRAL(R.drawable.mood_neutral),
    HAPPY(R.drawable.mood_happy),
    VERY_HAPPY(R.drawable.mood_very_happy);

    private final int iconResId;

    Mood(int iconResId) {
        this.iconResId = iconResId;
    }

    public int getIconResId() {
        return iconResId;
    }
    public static Mood fromResId(int resId) {
        for (Mood mood : values()) {
            if (mood.iconResId == resId) {
                return mood;
            }
        }
        return NEUTRAL; // Default fallback
    }
}