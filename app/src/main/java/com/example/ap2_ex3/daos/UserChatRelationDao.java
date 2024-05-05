package com.example.ap2_ex3.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ap2_ex3.adapters.db_entities.UserChatRelation;

import java.util.List;

@Dao
public interface UserChatRelationDao {
    @Query("DELETE FROM userChatRelations")
    void deleteAll();
    @Insert
    void insert(UserChatRelation... userChatRelation);

    @Update
    void update(UserChatRelation... userChatRelation);

    @Delete
    void delete(UserChatRelation... userChatRelation);

    @Query("SELECT * FROM userChatRelations")
    List<UserChatRelation> getAllUserChatRelations();

    @Query("SELECT * FROM userChatRelations WHERE username = :username")
    List<UserChatRelation> getChatsForUsername(String username);

    @Query("SELECT * FROM userChatRelations WHERE chatId = :chatId")
    List<UserChatRelation> getUsersForChatId(String chatId);
}