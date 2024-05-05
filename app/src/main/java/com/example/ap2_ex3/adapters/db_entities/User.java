package com.example.ap2_ex3.adapters.db_entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "users")
public class User {
    @NonNull
    @PrimaryKey
    private String username;
    private int id;

    private String displayName;
    private String password;
    private String profilePic;

    public User(String username, String displayName, String password, String profilePic) {
        this.id = 0;
        this.username = username;
        this.displayName = displayName;
        this.password = password;
        this.profilePic = profilePic;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPassword() {
        return password;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username){this.username=username;}

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}