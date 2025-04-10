package com.example.app0.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.app0.R;
import com.example.app0.database.CalendarItem;
import com.example.app0.database.Mood;
import com.example.app0.moodtracker.MoodCalendarView;
import com.example.app0.database.CalendarItemViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class MoodFragment extends Fragment {
    private CalendarItemViewModel moodViewModel;
    // change this to calendarviewmodel is code works
    private MoodCalendarView moodCalendar;
    private LinearLayout moodDetailsContainer;
    private TextView selectedDateText;
    private ImageView selectedMoodImage;
    private TextView moodNotesText;

    // Dialog views
    private View moodDialog;
    private ImageView closeMoodDialog;
    private ImageView moodVerySad, moodSad, moodNeutral, moodHappy, moodVeryHappy;
    private EditText notesInput;
    private Button saveButton;

    // Currently selected date
    private int selectedYear, selectedMonth, selectedDay;
    private int selectedMoodResId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mood, container, false);

        // Get the ViewModel
        moodViewModel = new ViewModelProvider(this).get(CalendarItemViewModel.class);

        // Initialize views
        moodCalendar = rootView.findViewById(R.id.mood_calendar);
        moodDetailsContainer = rootView.findViewById(R.id.mood_details_container);
        selectedDateText = rootView.findViewById(R.id.selected_date);
        selectedMoodImage = rootView.findViewById(R.id.selected_mood);
        moodNotesText = rootView.findViewById(R.id.mood_notes);

        // Create and add mood dialog
        moodDialog = inflater.inflate(R.layout.mood_dialog, container, false);
        ((ViewGroup) rootView).addView(moodDialog);
        moodDialog.setVisibility(View.GONE);

        // Initialize dialog views
        closeMoodDialog = moodDialog.findViewById(R.id.close_dialog);
        moodVerySad = moodDialog.findViewById(R.id.mood_very_sad);
        moodSad = moodDialog.findViewById(R.id.mood_sad);
        moodNeutral = moodDialog.findViewById(R.id.mood_neutral);
        moodHappy = moodDialog.findViewById(R.id.mood_happy);
        moodVeryHappy = moodDialog.findViewById(R.id.mood_very_happy);
        notesInput = moodDialog.findViewById(R.id.notes_input);
        saveButton = moodDialog.findViewById(R.id.done_button);

        // Set up mood selection in dialog
        setupMoodSelectionListeners();

        // Set up close dialog button
        closeMoodDialog.setOnClickListener(v -> moodDialog.setVisibility(View.GONE));

        // Set up save button
        saveButton.setOnClickListener(v -> saveMoodEntry());

        // Set up the calendar date selection listener
        moodCalendar.setOnDateSelectedListener((year, month, day, moodEntry) -> {
            selectedYear = year;
            selectedMonth = month;
            selectedDay = day;

            // Update ViewModel with selected date
            moodViewModel.setSelectedDate(year, month, day);

            // Show mood details if exists
            if (moodEntry != null) {
                showMoodDetails(year, month, day, moodEntry);
            } else {
                // Show dialog to create new mood entry
                showMoodDialog(year, month, day);
            }
        });

        // Observe calendar items
        moodViewModel.getAllCalendarItems().observe(getViewLifecycleOwner(), calendarItems -> {
            if (calendarItems != null) {
                updateCalendarWithItems(calendarItems);
            }
        });

        // Load initial data
        loadInitialData();

        return rootView;
    }

    private void updateCalendarWithItems(List<CalendarItem> calendarItems) {
        // Update calendar UI with database items
        moodCalendar.updateWithCalendarItems(calendarItems);
    }

    private void loadInitialData() {
        // Fetch all mood data on a background thread
        Executors.newSingleThreadExecutor().execute(() -> {
            List<CalendarItem> items = moodViewModel.getAllCalendarItemsSync();
            if (items != null) {
                requireActivity().runOnUiThread(() -> updateCalendarWithItems(items));
            }
        });
    }

    private void setupMoodSelectionListeners() {
        // Clear previous selection when selecting a new mood
        View.OnClickListener moodClickListener = v -> {
            resetMoodSelection();
            v.setSelected(true);

            // Set the selected mood resource id
            if (v.getId() == R.id.mood_very_sad) {
                selectedMoodResId = R.drawable.mood_very_sad;
            } else if (v.getId() == R.id.mood_sad) {
                selectedMoodResId = R.drawable.mood_sad;
            } else if (v.getId() == R.id.mood_neutral) {
                selectedMoodResId = R.drawable.mood_neutral;
            } else if (v.getId() == R.id.mood_happy) {
                selectedMoodResId = R.drawable.mood_happy;
            } else if (v.getId() == R.id.mood_very_happy) {
                selectedMoodResId = R.drawable.mood_very_happy;
            }
        };

        // Apply the listener to all mood icons
        moodVerySad.setOnClickListener(moodClickListener);
        moodSad.setOnClickListener(moodClickListener);
        moodNeutral.setOnClickListener(moodClickListener);
        moodHappy.setOnClickListener(moodClickListener);
        moodVeryHappy.setOnClickListener(moodClickListener);
    }

    private void resetMoodSelection() {
        moodVerySad.setSelected(false);
        moodSad.setSelected(false);
        moodNeutral.setSelected(false);
        moodHappy.setSelected(false);
        moodVeryHappy.setSelected(false);
        selectedMoodResId = 0;
    }

    private void showMoodDialog(int year, int month, int day) {
        // Reset dialog state
        resetMoodSelection();
        notesInput.setText("");

        // Format and display the date
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(calendar.getTime());

        // Hide details and show dialog
        moodDetailsContainer.setVisibility(View.GONE);
        moodDialog.setVisibility(View.VISIBLE);
    }

    private void showMoodDetails(int year, int month, int day, MoodCalendarView.MoodEntry moodEntry) {
        // Format and display the date
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(calendar.getTime());
        selectedDateText.setText(formattedDate);

        // Display mood and notes
        selectedMoodImage.setImageResource(moodEntry.getMoodResId());
        moodNotesText.setText(moodEntry.getNotes());

        // Hide dialog and show details
        moodDialog.setVisibility(View.GONE);
        moodDetailsContainer.setVisibility(View.VISIBLE);
    }

    private void saveMoodEntry() {
        if (selectedMoodResId == 0) {
            // No mood selected, show error or select default
            selectedMoodResId = R.drawable.mood_neutral;
        }

        String notes = notesInput.getText().toString().trim();

        // Save mood via ViewModel
        moodViewModel.saveMoodEntry(selectedMoodResId, notes);

        // Close dialog and show details
        moodDialog.setVisibility(View.GONE);

        // Create a temporary entry to display until the LiveData updates
        MoodCalendarView.MoodEntry entry = new MoodCalendarView.MoodEntry(
                String.format(Locale.US, "%04d-%02d-%02d",
                        selectedYear, selectedMonth + 1, selectedDay),
                selectedMoodResId,
                notes
        );

        // Update UI immediately
        moodCalendar.setMoodEntry(selectedYear, selectedMonth, selectedDay, selectedMoodResId, notes);
        showMoodDetails(selectedYear, selectedMonth, selectedDay, entry);
    }
}