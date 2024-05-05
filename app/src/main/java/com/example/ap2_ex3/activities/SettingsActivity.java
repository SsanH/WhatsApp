package com.example.ap2_ex3.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.SwitchCompat;

import com.example.ap2_ex3.R;
import com.example.ap2_ex3.adapters.db_entities.AppSettings;
import com.example.ap2_ex3.api.UserApi;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {
    AppSettings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        settings = MainActivity.database.getAppSettingsDao().get(1);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button changeServerButton = findViewById(R.id.change_server_button);
        AppCompatEditText serverAddressField = findViewById(R.id.server_address_field);
        ImageButton backButton = findViewById(R.id.backButton);
        SwitchCompat darkModeSwitch = findViewById(R.id.theme_switch);
        darkModeSwitch.setChecked(settings.getTheme() == 1);
        darkModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    MainActivity.database.getAppSettingsDao().updateTheme(1, 1);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    MainActivity.database.getAppSettingsDao().updateTheme(1, 0);
                }
            }
        });

        changeServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newServerAddress = Objects.requireNonNull(serverAddressField.getText()).toString();
                String tmpURL = settings.getBaseUrl();
                try {
                    MainActivity.database.getAppSettingsDao().updateBaseURL(1, newServerAddress);
                    UserApi userApi = new UserApi();
                    Toast.makeText(getApplicationContext(), "Server changed to: " + MainActivity.database.getAppSettingsDao().get(1).getBaseUrl(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
                    MainActivity.database.getAppSettingsDao().updateBaseURL(1, tmpURL);
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}