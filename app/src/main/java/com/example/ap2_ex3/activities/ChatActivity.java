package com.example.ap2_ex3.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ap2_ex3.AppDatabase;
import com.example.ap2_ex3.R;
import com.example.ap2_ex3.adapters.ChatMessagesAdapter;
import com.example.ap2_ex3.api.MessageApi;
import com.example.ap2_ex3.daos.MessageDao;
import com.example.ap2_ex3.adapters.db_entities.AppSettings;
import com.example.ap2_ex3.adapters.db_entities.Message;
import com.example.ap2_ex3.adapters.db_entities.User;
import com.example.ap2_ex3.interfaces.AppCallback;
import com.example.ap2_ex3.miscClasses.AsyncHttpClient;
import com.example.ap2_ex3.view_models.ChatsListModel;
import com.example.ap2_ex3.view_models.ChatsListModelProvider;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import android.util.Pair;

public class ChatActivity extends AppCompatActivity {
    private List<Message> messages;
    private MessageDao messageDao;
    private ChatMessagesAdapter adapter;
    private User currentUser;
    private User otherUser;
    private String chatId;
    private AppDatabase appDatabase;
    private AsyncHttpClient httpClient;
    private RecyclerView msgList;
    private LinearLayoutManager linearLayout;
    private TextView msgInput;
    ChatsListModel chatsViewModel;
    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppSettings settings = MainActivity.database.getAppSettingsDao().get(1);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        extras = getIntent().getExtras();

        messages = new ArrayList<>();
        String currentUserName = getIntent().getStringExtra("currentUsername");
        String otherUserName = getIntent().getStringExtra("otherUsername");
        String token = getIntent().getStringExtra("token");
        chatId = getIntent().getStringExtra("chatId");

        httpClient = new AsyncHttpClient(settings.getBaseUrl());
        httpClient.setToken(token);

        appDatabase = AppDatabase.getInstance(getApplicationContext());

        currentUser = appDatabase.getUserDao().getUserByUsername(currentUserName);
        otherUser = appDatabase.getUserDao().getUserByUsername(otherUserName);

        chatsViewModel = ChatsListModelProvider.getInstance(getApplication());

        linearLayout = new LinearLayoutManager(this);

        msgList = findViewById(R.id.chatRecyclerView);
        adapter = new ChatMessagesAdapter(this, messages, currentUser.getUsername());
        msgList.setAdapter(adapter);
        msgList.setLayoutManager(linearLayout);

        ImageButton returnButton = findViewById(R.id.back_button);

        ImageView imageView = findViewById(R.id.profilePicImageView);
        imageView.setImageBitmap(null);
        if (otherUser.getProfilePic() != null) {
            byte[] decodedString = android.util.Base64.decode(otherUser.getProfilePic(), android.util.Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imageView.setImageBitmap(decodedByte);
        }

        TextView textView = findViewById(R.id.displayNameTextView);
        textView.setText(otherUser.getDisplayName());
        returnButton.setOnClickListener(v -> {
            finish();
        });

        Button sendButton = findViewById(R.id.sendButton);
        msgInput = findViewById(R.id.messageInputField);

        sendButton.setOnClickListener(v -> {
            sendMessage(msgInput.getText().toString());
        });

        populateMessages();
        MainActivity.newMessage.observe(this, new Observer<Message>() {
            @Override
            public void onChanged(Message message) {
                updateMessages(message);
            }
        });
    }

    private void sendMessage(String message) {
        if (message.length() == 0)
            return;
        MessageApi.createMessage(httpClient, message, chatId, new AppCallback<Pair<JSONObject, Integer>>() {
            @Override
            public void onSuccess(Pair<JSONObject, Integer> resp) {
                try {
                    JSONObject msgJson = resp.first;

                    int msgId = msgJson.getInt("id");
                    String created = msgJson.getString("created");
                    String content = msgJson.getString("content");
                    String senderUsername = msgJson.getJSONObject("sender").getString("username");

                    Message msg = new Message(msgId, senderUsername, created, content);
                    //appDatabase.getMessageDao().insert(msg);
                    messages.add(msg);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyItemInserted(messages.size() - 1);
                            msgList.scrollToPosition(messages.size() - 1);
                            TextView msgInput = findViewById(R.id.messageInputField);
                            msgInput.setText("");
                        }
                    });

                } catch (Exception e) {
                    System.out.println("Failed to parse server response");
                    System.out.println(e);
                }
            }

            @Override
            public void onFailure() {
            }
        });
    }

    public void populateMessages() {
        MessageApi.getMessages(httpClient, chatId, new AppCallback<Pair<JSONArray, Integer>>() {

            @Override
            public void onSuccess(Pair<JSONArray, Integer> resp) {
                try {

                    JSONArray messagesJson = resp.first;
                    messages.clear();
                    for (int i = 0; i < messagesJson.length(); i++) {
                        JSONObject msgJson = messagesJson.getJSONObject(i);
                        int msgId = msgJson.getInt("id");
                        String created = msgJson.getString("created");
                        String content = msgJson.getString("content");
                        String senderUsername = msgJson.getJSONObject("sender").getString("username");

                        Message msg = new Message(msgId, senderUsername, created, content);
                        messages.add(msg);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Collections.sort(messages, new Comparator<Message>() {
                                @Override
                                public int compare(Message o1, Message o2) {
                                    return o1.getCreated().compareTo(o2.getCreated());
                                }
                            });
                            adapter.setMessages(messages);
                        }
                    });

                } catch (Exception e) {
                    System.out.println("Failed to parse server response");
                    System.out.println(e);
                    Toast.makeText(getApplicationContext(), "Server response error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure() {
                Toast.makeText(getApplicationContext(), "Server response error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateMessages(Message newMessage) {
        if (Objects.equals(newMessage.getSenderUsername(), otherUser.getUsername())) {
            messages.add(newMessage);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyItemInserted(messages.size() - 1);
                    msgList.scrollToPosition(messages.size() - 1);
                }
            });
        } else {
            //do update chats request
        }
    }



}
