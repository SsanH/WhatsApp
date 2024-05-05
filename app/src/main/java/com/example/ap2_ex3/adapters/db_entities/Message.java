package com.example.ap2_ex3.adapters.db_entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "messages",
        foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "username",
                        childColumns = "senderUsername",
                        onDelete = ForeignKey.CASCADE)
        })
public class Message {
    @NonNull
    @PrimaryKey
    private int id;
    private String senderUsername;
    private String created; //date
    private String content; //text

    public Message(int id, String senderUsername, String created, String content) {
        this.id = id;
        this.senderUsername = senderUsername;
        this.created = created;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public String getCreated() {
        return created;
    }

    public String getContent() {
        return content;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSenderUsername(String username) {
        this.senderUsername = username;
    }
}
