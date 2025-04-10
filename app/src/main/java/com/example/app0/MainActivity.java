package com.example.app0;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.app0.fragments.JournalFragment;
import com.example.app0.fragments.MoodFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            // Create MoodFragment instance
            MoodFragment moodFragment = new MoodFragment();

            // Create a Bundle to pass data (if needed)
            Bundle bundle = new Bundle();
            bundle.putString("selectedDate", "2025-04-03"); // Example of passing selected date
            moodFragment.setArguments(bundle);

            // Add the mood fragment to the container
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.mood_fragment_container, moodFragment);
            transaction.commit();
        }
    }


}