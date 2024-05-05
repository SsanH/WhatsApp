package com.example.ap2_ex3.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ap2_ex3.adapters.db_entities.AppSettings;

import java.util.List;
@Dao
public interface AppSettingsDao {
    @Query("SELECT * FROM AppSettings")
    List<AppSettings> index();

    @Query("SELECT * FROM AppSettings WHERE id = :id")
    AppSettings get(int id);

    @Insert
    void insert(AppSettings... settings);

    @Update
    void update(AppSettings... settings);

    @Query("UPDATE AppSettings SET lastUsername = :newUsername WHERE id = :id")
    void updateUsername(int id, String newUsername);

    @Query("UPDATE AppSettings SET lastPassword = :newPassword WHERE id = :id")
    void updatePassword(int id, String newPassword);

    @Query("UPDATE AppSettings SET baseURL = :newBaseURL WHERE id = :id")
    void updateBaseURL(int id, String newBaseURL);

    @Query("UPDATE AppSettings SET theme = :newTheme WHERE id = :id")
    void updateTheme(int id, int newTheme);

    @Query("UPDATE AppSettings SET lastToken = :newToken WHERE id = :id")
    void updateToken(int id, String newToken);

    @Delete
    void delete(AppSettings... settings);
}