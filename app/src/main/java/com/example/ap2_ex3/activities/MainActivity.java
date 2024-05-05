package com.example.ap2_ex3.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.MutableLiveData;

import com.example.ap2_ex3.AppDatabase;
import com.example.ap2_ex3.R;
import com.example.ap2_ex3.Services.NotificationsService;
import com.example.ap2_ex3.adapters.db_entities.Message;
import com.example.ap2_ex3.api.UserApi;
import com.example.ap2_ex3.daos.ChatDao;
import com.example.ap2_ex3.daos.MessageDao;
import com.example.ap2_ex3.daos.UserChatRelationDao;
import com.example.ap2_ex3.daos.UserDao;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.example.ap2_ex3.adapters.db_entities.AppSettings;
import com.example.ap2_ex3.interfaces.AppCallback;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.iid.FirebaseInstanceId;
//import com.google.firebase.iid.InstanceIdResult;

public class MainActivity extends AppCompatActivity {
    public static MutableLiveData<Message> newMessage;
    public static MutableLiveData<Boolean> newChat;
    public static AppDatabase database;
    private UserDao userDao;
    private MessageDao messageDao;
    private ChatDao chatDao;
    private UserChatRelationDao userChatRelationDao;
    Button loginButton;
    Button registerButton;
    Button settingsButton;
    public static Context context;
    private String appToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = getApplicationContext();

        database = AppDatabase.getInstance(getApplicationContext());
        userDao = database.getUserDao();
        messageDao = database.getMessageDao();
        chatDao = database.getChatDao();
        userChatRelationDao = database.getUserChatRelationDao();
        newMessage = new MutableLiveData<>();
        newChat = new MutableLiveData<>();

        if (MainActivity.database.getAppSettingsDao().get(1) == null) {
            AppSettings settings = new AppSettings("http://10.0.2.2:5000/api/", AppCompatDelegate.MODE_NIGHT_NO, 1);
            MainActivity.database.getAppSettingsDao().insert(settings);
        }
        AppSettings settings = database.getAppSettingsDao().get(1);
        if (settings.getTheme() == 1) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(MainActivity.this, instanceIdResult -> {
            appToken = instanceIdResult.getToken();
            database.getAppSettingsDao().updateToken(1, appToken);
        });

        loginButton = findViewById(R.id.login_button);
        registerButton = findViewById(R.id.register_button);
        settingsButton = findViewById(R.id.settings_button);

        UserApi userApi = new UserApi();

        if (settings.getLastUsername() != null) {
            String username = settings.getLastUsername();
            String password = settings.getLastPassword();
            userApi.login(username, password, new AppCallback<String>() {
                @Override
                public void onSuccess(String token) {
                    database.getAppSettingsDao().updateUsername(1, username);
                    database.getAppSettingsDao().updatePassword(1, password);
                    Intent intent = new Intent(MainActivity.this, ChatListActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("token", token);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure() {
                    Toast.makeText(MainActivity.context, "auto login failed", Toast.LENGTH_SHORT).show();
                }
            });
        }

        addListeners();

        database = AppDatabase.getInstance(getApplicationContext());
        userDao = database.getUserDao();
        messageDao = database.getMessageDao();
        chatDao = database.getChatDao();
        userChatRelationDao = database.getUserChatRelationDao();
        context = getApplicationContext();

        userDao.deleteAll();
        messageDao.deleteAll();
        chatDao.deleteAll();
        userChatRelationDao.deleteAll();
    }

    private void addListeners() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start LoginActivity
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.putExtra("appToken", appToken);
                startActivity(intent);
            }
        });
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start SettingsActivity
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start RegisterActivity
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}