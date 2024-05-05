package com.example.ap2_ex3.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.ap2_ex3.adapters.db_entities.Message;

import java.util.List;

@Dao
public interface MessageDao {
    @Query("DELETE FROM messages")
    void deleteAll();
    @Insert
    void insert(Message... message);

    @Query("SELECT * FROM messages")
    List<Message> getAllMessages();
}