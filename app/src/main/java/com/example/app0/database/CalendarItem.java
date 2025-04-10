package com.example.app0.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "calendarItems")
public class CalendarItem {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "date")
    private Date date;

    @ColumnInfo(name = "mood")
    private Mood mood;

    @ColumnInfo(name = "entry")
    private String entry;

    public CalendarItem(Date date, Mood mood, String entry) {
        this.date = date;
        this.mood = mood;
        this.entry = entry;
    }

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    public Mood getMood() { return mood; }
    public void setMood(Mood mood) { this.mood = mood; }
    public String getEntry() { return entry; }
    public void setEntry(String entry) { this.entry = entry; }
}