package com.example.app0.managers;
// Handle AI chatbot interactions

import android.os.Handler;
import android.os.Looper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
public class ChatbotManager {
    // Simple responses for demonstration
    private Map<String, String> predefinedResponses;
    private Handler handler = new Handler(Looper.getMainLooper());

    public ChatbotManager() {
        predefinedResponses = new HashMap<>();
        setupResponses();
    }

    private void setupResponses() {
        predefinedResponses.put("hi", "Hi! First, take a deep breath. Could you share more on what is stressing you out?");
        predefinedResponses.put("hello", "Hello there! How are you feeling today?");
        predefinedResponses.put("stressed", "First, take a deep breath. Could you share more on what is stressing you out?");
        predefinedResponses.put("deadline", "I understand deadlines can be overwhelming. Let's break down your tasks into smaller steps.");
        predefinedResponses.put("exam", "Exams are stressful! Have you tried creating a study schedule?");
        predefinedResponses.put("help", "I'm here to help! What's on your mind?");
    }

    public void processMessage(String message, Consumer<String> responseCallback) {
        // Simulate network delay
        handler.postDelayed(() -> {
            String response = getResponse(message.toLowerCase());
            responseCallback.accept(response);
        }, 1000);
    }

    private String getResponse(String message) {
        // Simple keyword matching
        for (String keyword : predefinedResponses.keySet()) {
            if (message.contains(keyword)) {
                return predefinedResponses.get(keyword);
            }
        }

        // Default response
        return "I'm here to listen. Tell me more about how you're feeling.";
    }
}
