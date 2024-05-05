package com.example.ap2_ex3.adapters.db_entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "chats")
public class Chat {
    @NonNull
    @PrimaryKey
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) { this.id = id; }
}
