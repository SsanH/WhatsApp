package com.example.ap2_ex3.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.ap2_ex3.adapters.db_entities.Chat;

import java.util.List;

@Dao
public interface ChatDao {
    @Query("DELETE FROM chats")
    void deleteAll();
    @Insert
    long insert(Chat chat);

    @Query("SELECT * FROM chats")
    List<Chat> getAllChats();

}