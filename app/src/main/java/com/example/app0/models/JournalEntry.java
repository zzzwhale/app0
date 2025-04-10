package com.example.app0.models;
// Represent the journal entry with (text, date, mood)

public class JournalEntry {
    private String message;
    private boolean isUser;
    private long timestamp;

    // Default constructor (needed for some frameworks)
    public JournalEntry() {
    }

    // Constructor
    public JournalEntry(String message, boolean isUser, long timestamp) {
        this.message = message;
        this.isUser = isUser;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public boolean isUser() {
        return isUser;
    }

    public long getTimestamp() {
        return timestamp;
    }
}