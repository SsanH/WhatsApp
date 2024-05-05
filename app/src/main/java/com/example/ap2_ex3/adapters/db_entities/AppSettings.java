package com.example.ap2_ex3.adapters.db_entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "AppSettings")
public class AppSettings {
    @PrimaryKey
    private int id;
    private String lastUsername = null;
    private String lastPassword = null;
    private String lastToken = null;
    private String baseUrl = "http://10.0.2.2:5000/api/";
    private int theme = 0;
    public AppSettings() {
    }
    public AppSettings(String baseUrl, int theme, int id) {
        this.id = id;
        this.baseUrl = baseUrl;
        this.theme = theme;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) { this.id = id; }

    public String getLastUsername() {
        return this.lastUsername;
    }

    public void setLastUsername(String username) {
        this.lastUsername = username;
    }

    public String getLastPassword() {
        return this.lastPassword;
    }

    public void setLastPassword(String password) {
        this.lastPassword = password;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    public int getTheme() {
        return theme;
    }
    public void setTheme(int theme) {
        this.theme = theme;
    }
    public String getLastToken() {
        return lastToken;
    }
    public void setLastToken(String lastToken) {
        this.lastToken = lastToken;
    }
}