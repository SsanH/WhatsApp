package com.example.ap2_ex3.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ap2_ex3.R;
import com.example.ap2_ex3.adapters.db_entities.Message;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatMessagesAdapter extends RecyclerView.Adapter<ChatMessagesAdapter.ChatActivityViewHolder> {
    private final LayoutInflater mInflater;
    private List<Message> messages;
    private String currUsername;

    public ChatMessagesAdapter(Context context, List<Message> messages, String currUsername) {
        this.mInflater = LayoutInflater.from(context);
        this.messages = messages;
        this.currUsername = currUsername;
    }

    @NonNull
    @Override
    public ChatActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == 0) {
            itemView = mInflater.inflate(R.layout.outgoing_message_layout, parent, false);
        } else {
            itemView = mInflater.inflate(R.layout.incoming_message_layout, parent, false);
        }
        return new ChatActivityViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatActivityViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.bindMessage(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (message.getSenderUsername().equals(currUsername)) {
            return 0; // Outgoing message
        } else {
            return 1; // Incoming message
        }
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    class ChatActivityViewHolder extends RecyclerView.ViewHolder {
        private TextView messageText;
        private TextView messageTime;

        ChatActivityViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            messageTime = itemView.findViewById(R.id.messageTime);
        }

        void bindMessage(Message message) {
            messageText.setText(message.getContent());
            String time = formatDateTime(message.getCreated());
            messageTime.setText(time);
        }
        public String formatDateTime(String dateString) {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm | MM/dd", Locale.US);

            try {
                Date date = inputFormat.parse(dateString);
                return outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return "";
        }
    }
}
