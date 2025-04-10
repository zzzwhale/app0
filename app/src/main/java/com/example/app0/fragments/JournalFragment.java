package com.example.app0.fragments;
// AI chatbot where user can write journal entries

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app0.R;
import com.example.app0.managers.ChatbotManager;
import com.example.app0.models.JournalEntry;
import com.example.app0.ui.ChatMessageAdapter;

import java.util.ArrayList;
import java.util.List;

public class JournalFragment extends Fragment {

    private RecyclerView recyclerView;
    private ChatMessageAdapter adapter;
    private List<JournalEntry> messages;
    private EditText messageInput;
    private ImageButton sendButton;
    private ChatbotManager chatbotManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_journal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize components
        chatbotManager = new ChatbotManager();
        messages = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recycler_chat_messages);
        messageInput = view.findViewById(R.id.edit_message);
        sendButton = view.findViewById(R.id.btn_send);  // Initialize sendButton

        // Back button handler
        view.findViewById(R.id.btn_back).setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        // Set up RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ChatMessageAdapter(messages);
        recyclerView.setAdapter(adapter);

        // Add welcome message
        addBotMessage("Hi! I'm your Plant Buddy. How can I help you today?");

        // Set up send message logic - make sure this is defined correctly
        sendButton.setOnClickListener(v -> {
            String message = messageInput.getText().toString().trim();
            if (!message.isEmpty()) {
                sendMessage(message);
            }
        });
    }

    private void sendMessage(String message) {
        // Create a new JournalEntry object with the correct constructor
        // Note: Replace this with the correct constructor for your JournalEntry class
        JournalEntry userEntry = createJournalEntry(message, true);

        messages.add(userEntry);
        adapter.notifyItemInserted(messages.size() - 1);
        recyclerView.smoothScrollToPosition(messages.size() - 1);
        messageInput.setText("");

        // Process message with ChatbotManager and get response
        chatbotManager.processMessage(message, response -> {
            addBotMessage(response);
        });
    }

    private void addBotMessage(String message) {
        // Add bot message to chat
        if (isAdded()) {
            requireActivity().runOnUiThread(() -> {
                JournalEntry botEntry = createJournalEntry(message, false);
                messages.add(botEntry);
                adapter.notifyItemInserted(messages.size() - 1);
                recyclerView.smoothScrollToPosition(messages.size() - 1);
            });
        }
    }

// Helper method to create JournalEntry objects based on class structure
    private JournalEntry createJournalEntry(String message, boolean isUser) {
        // Using the constructor we defined in JournalEntry
        return new JournalEntry(message, isUser, System.currentTimeMillis());
    }
}