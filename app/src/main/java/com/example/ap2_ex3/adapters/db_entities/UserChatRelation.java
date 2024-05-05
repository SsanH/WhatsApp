package com.example.ap2_ex3.adapters.db_entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "userChatRelations",
        primaryKeys = {"username", "chatId"},
        foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "username",
                        childColumns = "username",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Chat.class,
                        parentColumns = "id",
                        childColumns = "chatId",
                        onDelete = ForeignKey.CASCADE)
        })
public class UserChatRelation {
    @NonNull
    private String username;
    @NonNull
    private String chatId;

    public UserChatRelation(String username, String chatId) {
        this.username = username;
        this.chatId = chatId;
    }

    public String getUsername() {
        return username;
    }

    public String getChatId() {
        return chatId;
    }
}