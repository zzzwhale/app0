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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.app0.R;
import com.example.app0.database.CalendarItem;
import com.example.app0.database.Mood;
import com.example.app0.database.CalendarItemViewModel;
import com.example.app0.moodtracker.MoodCalendarView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class MoodFragment extends Fragment {
    private CalendarItemViewModel moodViewModel;
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

    // Edit and Delete buttons
    private Button updateButton;
    private Button deleteButton;

    // Currently selected date
    private int selectedYear, selectedMonth, selectedDay;
    private int selectedMoodResId;
    // Flag to track if we're editing or creating new
    private boolean isEditMode = false;

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

        // Create and add the update and delete buttons
        LinearLayout buttonLayout = new LinearLayout(requireContext());
        buttonLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);

        // Create Update Button
        updateButton = new Button(requireContext());
        updateButton.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
        updateButton.setText("Edit");

        // Create Delete Button
        deleteButton = new Button(requireContext());
        deleteButton.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
        deleteButton.setText("Delete");

        // Add buttons to layout
        buttonLayout.addView(updateButton);
        buttonLayout.addView(deleteButton);

        // Add button layout to moodDetailsContainer
        moodDetailsContainer.addView(buttonLayout);

        // Set up mood selection in dialog
        setupMoodSelectionListeners();

        // Set up close dialog button
        closeMoodDialog.setOnClickListener(v -> moodDialog.setVisibility(View.GONE));

        // Set up save button
        saveButton.setOnClickListener(v -> {
            if (isEditMode) {
                updateMoodEntry();
            } else {
                saveMoodEntry();
            }
        });

        // Set up edit button listener
        updateButton.setOnClickListener(v -> {
            MoodCalendarView.MoodEntry currentEntry = moodCalendar.getMoodEntry(
                    selectedYear, selectedMonth, selectedDay);

            if (currentEntry != null) {
                isEditMode = true;
                showMoodDialogForEdit(selectedYear, selectedMonth, selectedDay,
                        currentEntry.getMoodResId(), currentEntry.getNotes());
            }
        });

        // Set up delete button listener
        deleteButton.setOnClickListener(v -> {
            showDeleteConfirmationDialog();
        });

        // Set up the calendar date selection listener
        moodCalendar.setOnDateSelectedListener((year, month, day, moodEntry) -> {
            selectedYear = year;
            selectedMonth = month;
            selectedDay = day;

            // Update ViewModel with selected date
            moodViewModel.setSelectedDate(year, month, day);

            // Reset edit mode flag for new selections
            isEditMode = false;

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

        // Update dialog title/button text for new entry
        saveButton.setText("Save");

        // Format and display the date
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(calendar.getTime());

        // Hide details and show dialog
        moodDetailsContainer.setVisibility(View.GONE);
        moodDialog.setVisibility(View.VISIBLE);
    }

    private void showMoodDialogForEdit(int year, int month, int day, int moodResId, String existingNotes) {
        // Reset dialog state first
        resetMoodSelection();

        // Pre-select the current mood
        selectMoodInDialog(moodResId);

        // Pre-fill notes
        notesInput.setText(existingNotes);

        // Update dialog title/button text for editing
        saveButton.setText("Update");

        // Format and display the date
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(calendar.getTime());

        // Hide details and show dialog
        moodDetailsContainer.setVisibility(View.GONE);
        moodDialog.setVisibility(View.VISIBLE);
    }

    private void selectMoodInDialog(int moodResId) {
        // Select the correct mood icon based on resource ID
        if (moodResId == R.drawable.mood_very_sad) {
            moodVerySad.setSelected(true);
        } else if (moodResId == R.drawable.mood_sad) {
            moodSad.setSelected(true);
        } else if (moodResId == R.drawable.mood_neutral) {
            moodNeutral.setSelected(true);
        } else if (moodResId == R.drawable.mood_happy) {
            moodHappy.setSelected(true);
        } else if (moodResId == R.drawable.mood_very_happy) {
            moodVeryHappy.setSelected(true);
        }

        // Save the selected resource ID
        selectedMoodResId = moodResId;
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

    private void updateMoodEntry() {
        if (selectedMoodResId == 0) {
            // No mood selected, show error or select default
            selectedMoodResId = R.drawable.mood_neutral;
        }

        String notes = notesInput.getText().toString().trim();

        // Create date for the entry
        Calendar calendar = Calendar.getInstance();
        calendar.set(selectedYear, selectedMonth, selectedDay);
        Date date = calendar.getTime();

        // Create CalendarItem
        Mood mood = Mood.fromResId(selectedMoodResId);
        CalendarItem updatedItem = new CalendarItem(date, mood, notes);

        // Update mood via ViewModel
        moodViewModel.updateCalendarItem(updatedItem);

        // Close dialog and show details
        moodDialog.setVisibility(View.GONE);
        isEditMode = false;

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

    private void showDeleteConfirmationDialog() {
        // Create AlertDialog for confirmation
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Delete Mood Entry");
        builder.setMessage("Are you sure you want to delete this mood entry?");

        // Add buttons
        builder.setPositiveButton("Delete", (dialog, which) -> {
            deleteMoodEntry();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });

        builder.create().show();
    }

    private void deleteMoodEntry() {
        // Create a CalendarItem to delete
        Calendar calendar = Calendar.getInstance();
        calendar.set(selectedYear, selectedMonth, selectedDay);
        Date date = calendar.getTime();

        // Get the current entry to delete
        MoodCalendarView.MoodEntry entry = moodCalendar.getMoodEntry(selectedYear, selectedMonth, selectedDay);
        if (entry != null) {
            // Convert MoodResId to Mood enum
            Mood mood = Mood.fromResId(entry.getMoodResId());

            // Create a CalendarItem for deletion
            CalendarItem itemToDelete = new CalendarItem(date, mood, entry.getNotes());

            // Delete from repository via ViewModel
            moodViewModel.deleteCalendarItem(itemToDelete);

            // Update UI
            moodCalendar.removeMoodEntry(selectedYear, selectedMonth, selectedDay);

            // Hide details container
            moodDetailsContainer.setVisibility(View.GONE);
        }
    }
}