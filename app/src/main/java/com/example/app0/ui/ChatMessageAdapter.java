package com.example.app0.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app0.R;
import com.example.app0.models.JournalEntry;
import com.example.app0.models.JournalEntry;

import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_BOT = 2;

    private List<JournalEntry> messages;

    public ChatMessageAdapter(List<JournalEntry> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == VIEW_TYPE_USER) {
            View view = inflater.inflate(R.layout.journal_user_message, parent, false);
            return new UserMessageHolder(view);
        } else {
            View view = inflater.inflate(R.layout.journal_bot_message, parent, false);
            return new BotMessageHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        JournalEntry message = messages.get(position);

        if (holder instanceof UserMessageHolder) {
            ((UserMessageHolder) holder).bind(message);
        } else if (holder instanceof BotMessageHolder) {
            ((BotMessageHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isUser() ? VIEW_TYPE_USER : VIEW_TYPE_BOT;
    }

    static class UserMessageHolder extends RecyclerView.ViewHolder {
        private TextView messageText;

        UserMessageHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.txt_message);
        }

        void bind(JournalEntry message) {
            messageText.setText(message.getMessage());
        }
    }

    static class BotMessageHolder extends RecyclerView.ViewHolder {
        private TextView messageText;

        BotMessageHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.txt_message);
        }

        void bind(JournalEntry message) {
            messageText.setText(message.getMessage());
        }
    }
}