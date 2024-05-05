package com.example.ap2_ex3.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Pair;
import android.view.View;
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
import com.example.ap2_ex3.adapters.ChatListItemAdapter;
import com.example.ap2_ex3.api.ChatApi;
import com.example.ap2_ex3.api.UserApi;
import com.example.ap2_ex3.adapters.db_entities.AppSettings;
import com.example.ap2_ex3.adapters.db_entities.Chat;
import com.example.ap2_ex3.adapters.db_entities.Message;
import com.example.ap2_ex3.adapters.db_entities.User;
import com.example.ap2_ex3.adapters.db_entities.UserChatRelation;
import com.example.ap2_ex3.fragments.AddChatFragment;
import com.example.ap2_ex3.interfaces.AppCallback;
import com.example.ap2_ex3.interfaces.ChatClicked;
import com.example.ap2_ex3.miscClasses.AsyncHttpClient;
import com.example.ap2_ex3.miscClasses.ChatItemData;
import com.example.ap2_ex3.view_models.ChatsListModel;
import com.example.ap2_ex3.view_models.ChatsListModelProvider;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class ChatListActivity extends AppCompatActivity {
    private User currentUser;
    private List<UserChatRelation> chats;
    private List<String> chatUserNames;
    private List<ChatItemData> chatViews;
    private ImageButton logoutButton;
    private ImageButton addChatButton;
    private AppDatabase appDatabase;
    private ChatListItemAdapter adapter;
    private ChatsListModel viewModel;
    private UserApi userApi;
    private String token;
    private AsyncHttpClient httpClient;
    private String appToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppSettings settings = MainActivity.database.getAppSettingsDao().get(1);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        Bundle extras = getIntent().getExtras();

        appDatabase = AppDatabase.getInstance(getApplicationContext());

        chatViews = new ArrayList<>();
        chatUserNames = new ArrayList<>();
        userApi = new UserApi();

        httpClient = new AsyncHttpClient(MainActivity.context.getString(R.string.base_url));

        if (extras != null) {
            appToken = extras.getString("appToken");
            token = extras.getString("token");
            String username = extras.getString("username");
            httpClient.setToken(token);
            userApi.getUser(username, token, new AppCallback<User>() {
                @Override
                public void onSuccess(User user) {
                    currentUser = user;

                    TextView displayName = findViewById(R.id.displayName);
                    displayName.setText(currentUser.getDisplayName());

                    ImageView profilePic = findViewById(R.id.profilePic);
                    if (currentUser.getProfilePic().charAt(4) == ':') {
                        currentUser.setProfilePic(currentUser.getProfilePic().substring(22));
                    }
                    byte[] imageBytes = Base64.decode(currentUser.getProfilePic(), Base64.DEFAULT);
                    Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    profilePic.setImageBitmap(decodedImage);

                    appDatabase.getUserDao().insert(currentUser);

                    populateChats();
                }

                @Override
                public void onFailure() {
                    throw new RuntimeException("Failed to get user");
                }
            });
        }

        ChatClicked chatClicked = new ChatClicked() {
            @Override
            public void onChatClicked(int index) {
                chats = appDatabase.getUserChatRelationDao().getChatsForUsername(currentUser.getUsername());
                User OtherUser = appDatabase.getUserDao().getUserByUsername(chatViews.get(index).getUsername());

                Intent intent = new Intent(ChatListActivity.this, ChatActivity.class);
                intent.putExtra("appToken", appToken);
                intent.putExtra("currentUsername", currentUser.getUsername());
                intent.putExtra("otherUsername", OtherUser.getUsername());
                intent.putExtra("chatId", chats.get(index).getChatId());
                intent.putExtra("chatIndex", index);
                intent.putExtra("token", token);

                startActivity(intent);
            }
        };

        viewModel = ChatsListModelProvider.getInstance(getApplication());

        RecyclerView chatList = findViewById(R.id.chatList);
        adapter = new ChatListItemAdapter(this, chatClicked);
        chatList.setAdapter(adapter);
        chatList.setLayoutManager(new LinearLayoutManager(this));

        viewModel.getChats().observe(this, new Observer<List<ChatItemData>>() {
            @Override
            public void onChanged(List<ChatItemData> chats) {
                adapter.setChats(chats);
                adapter.notifyDataSetChanged();
            }
        });

        logoutButton = findViewById(R.id.logoutButton);
        addChatButton = findViewById(R.id.addChatButton);
        addListeners();
    }

    private void populateChats() {
        ChatApi.getChats(httpClient, new AppCallback<Pair<JSONArray, Integer>>() {
            @Override
            public void onSuccess(Pair<JSONArray, Integer> resp) {
                try {
                    JSONArray jsonChats = resp.first;
                    int respCode = resp.second;
                    chatViews.clear();
                    appDatabase.getChatDao().deleteAll();
                    for (int i = 0; i < jsonChats.length(); i++) {
                        JSONObject jsonChat = jsonChats.getJSONObject(i);

                        String chatId = jsonChat.getString("id");

                        JSONObject chatUser = jsonChat.getJSONObject("user");

                        String chatUserName = chatUser.getString("username");
                        String chatDisplayName = chatUser.getString("displayName");
                        String chatProfilePic = chatUser.getString("profilePic");

                        Chat c = new Chat();
                        c.setId(chatId);
                        appDatabase.getChatDao().insert(c);

                        if (chatProfilePic.charAt(4) == ':') {
                            chatProfilePic = chatProfilePic.substring(22);
                        }

                        User otherUser = new User(
                                chatUserName,
                                chatDisplayName,
                                "",
                                chatProfilePic
                        );
                        appDatabase.getUserDao().insert(otherUser);

                        UserChatRelation relThe = new UserChatRelation(chatUserName, c.getId());
                        UserChatRelation relMe = new UserChatRelation(currentUser.getUsername(), c.getId());
                        appDatabase.getUserChatRelationDao().insert(relThe, relMe);

                        String msgCreated = "";
                        String msgContent = "";
                      
                        try {
                            JSONObject lastMessage = jsonChat.getJSONObject("lastMessage");

                            int msgId = lastMessage.getInt("id");
                            msgCreated = lastMessage.getString("created");
                            msgContent = lastMessage.getString("content");

                            Message msg = new Message(msgId, "", msgCreated, msgContent);
                            appDatabase.getMessageDao().insert(msg);

                        } catch (Exception e) {
                            System.out.println("Failed to acquire last message for chat" + c.getId());
                            System.out.println(e);
                        }

                        chatViews.add(new ChatItemData(
                                chatUserName,
                                chatProfilePic,
                                chatDisplayName,
                                msgContent,
                                msgCreated
                        ));
                        chatUserNames.add(chatUserName);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            viewModel.setChats(chatViews);
                        }
                    });

                } catch (Exception e) {
                    System.out.println("Failed to parse server response");
                    System.out.println(e);
                }
            }

            @Override
            public void onFailure() {
                Toast.makeText(getApplicationContext(), "Server response error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addListeners() {
        MainActivity.newChat.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                appDatabase.getUserDao().deleteAll();
                appDatabase.getChatDao().deleteAll();
                appDatabase.getUserChatRelationDao().deleteAll();
                appDatabase.getMessageDao().deleteAll();
                appDatabase.getUserDao().insert(currentUser);
                populateChats();
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform logout actions
                appDatabase.getAppSettingsDao().updatePassword(1, null);
                appDatabase.getAppSettingsDao().updateUsername(1, null);
                appDatabase.getUserDao().deleteAll();
                appDatabase.getChatDao().deleteAll();
                appDatabase.getUserChatRelationDao().deleteAll();
                appDatabase.getMessageDao().deleteAll();
                Intent intent = new Intent(ChatListActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        addChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddChatFragment addChatFragment = new AddChatFragment(chatUserNames, currentUser.getUsername());
                addChatFragment.show(getSupportFragmentManager(), "add_chat");
            }
        });
    }

    public void addChatWithUser(String username) {

        ChatApi.createChat(httpClient, username, new AppCallback<Pair<JSONObject, Integer>>() {
            @Override
            public void onSuccess(Pair<JSONObject, Integer> resp) {
                try {
                    JSONObject chat = resp.first;
                    int respCode = resp.second;
                    if (respCode == HttpURLConnection.HTTP_OK) {
                        String chatId = chat.getString("id");
                        Chat c = new Chat();
                        c.setId(chatId);
                        appDatabase.getChatDao().insert(c);

                        JSONObject userJson = chat.getJSONObject("user");
                        User user = new User(
                                userJson.getString("username"),
                                userJson.getString("displayName"),
                                "",
                                userJson.getString("profilePic")
                        );
                        if (user.getProfilePic().charAt(4) == ':') {
                            user.setProfilePic(user.getProfilePic().substring(22));
                        }
                        appDatabase.getUserDao().insert(user);

                        UserChatRelation other = new UserChatRelation(user.getUsername(), c.getId());
                        UserChatRelation me = new UserChatRelation(currentUser.getUsername(), c.getId());
                        appDatabase.getUserChatRelationDao().insert(other, me);

                        ChatItemData chatItemData = new ChatItemData(
                                user.getUsername(),
                                user.getProfilePic(),
                                user.getDisplayName(),
                                "",
                                "");

                        chatViews.add(chatItemData);
                        chatUserNames.add(user.getUsername());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                viewModel.setChats(chatViews);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "User not found", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

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
}
