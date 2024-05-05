package com.example.ap2_ex3.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ap2_ex3.R;
import com.example.ap2_ex3.interfaces.ChatClicked;
import com.example.ap2_ex3.miscClasses.ChatItemData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatListItemAdapter extends RecyclerView.Adapter<ChatListItemAdapter.ChatListItemViewHolder> {

    class ChatListItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView profilePic;
        private TextView displayName;
        private TextView lastMessage;
        private TextView created;

        private ChatListItemViewHolder(View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.itemProfilePic);
            displayName = itemView.findViewById(R.id.itemDisplayName);
            lastMessage = itemView.findViewById(R.id.itemLastMessage);
            created = itemView.findViewById(R.id.itemCreated);
        }
    }

    private final LayoutInflater mInflater;
    private List<ChatItemData> chats;
    ChatClicked chatClicked;

    public ChatListItemAdapter(Context context, ChatClicked chatClicked) {
        mInflater = LayoutInflater.from(context);
        this.chatClicked = chatClicked;
    }

    public void setChats(List<ChatItemData> chats) {
        this.chats = chats;
    }

    public void addChat(ChatItemData chat) {
        this.chats.add(chat);
    }

    @NonNull
    @Override
    public ChatListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.chat_list_item, parent, false);
        return new ChatListItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListItemViewHolder holder, int position) {
        if (chats != null) {
            final ChatItemData current = chats.get(position);
            byte[] imageBytes = Base64.decode(current.getProfilePic(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            String lastMsg = current.getLastMessageCreated();
            holder.profilePic.setImageBitmap(bitmap);
            holder.displayName.setText(current.getDisplayName());
            String time = formatDateTime(current.getLastMessageCreated());
            holder.created.setText(time);
            holder.lastMessage.setText(current.getLastMessageContent());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chatClicked.onChatClicked(holder.getAdapterPosition());
                }
            });
        }
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

    @Override
    public int getItemCount() {
        return chats.size();
    }
}
