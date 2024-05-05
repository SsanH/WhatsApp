package com.example.ap2_ex3.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ap2_ex3.AppDatabase;
import com.example.ap2_ex3.R;
import com.example.ap2_ex3.api.UserApi;
import com.example.ap2_ex3.adapters.db_entities.AppSettings;
import com.example.ap2_ex3.interfaces.AppCallback;

public class LoginActivity extends AppCompatActivity {
    private Button registerButton;
    private Button loginButton;
    private AppDatabase appDatabase;
    private UserApi userApi;
    private Bundle extras;

    protected void onCreate(Bundle savedInstanceState) {
        AppSettings settings = MainActivity.database.getAppSettingsDao().get(1);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        extras = getIntent().getExtras();

        registerButton = findViewById(R.id.back_to_register);
        loginButton = findViewById(R.id.login);
        appDatabase = AppDatabase.getInstance(getApplicationContext());

        userApi = new UserApi();

        EditText usernameEditText = findViewById(R.id.login_username);
        EditText passwordEditText = findViewById(R.id.login_password);

        addListeners();
    }

    private void addListeners() {
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the RegisterActivity
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText usernameEditText = findViewById(R.id.login_username);
                EditText passwordEditText = findViewById(R.id.login_password);
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString();

                userApi.login(username, password, new AppCallback<String>() {
                    @Override
                    public void onSuccess(String token) {
                        appDatabase.getAppSettingsDao().updateUsername(1, username);
                        appDatabase.getAppSettingsDao().updatePassword(1, password);
                        Intent intent = new Intent(LoginActivity.this, ChatListActivity.class);
                        intent.putExtra("username", username);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure() {
                        Toast.makeText(MainActivity.context, "Username or password incorrect", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
