package com.example.ap2_ex3;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.ap2_ex3.daos.AppSettingsDao;
import com.example.ap2_ex3.daos.ChatDao;
import com.example.ap2_ex3.daos.MessageDao;
import com.example.ap2_ex3.daos.UserChatRelationDao;
import com.example.ap2_ex3.daos.UserDao;
import com.example.ap2_ex3.adapters.db_entities.AppSettings;
import com.example.ap2_ex3.adapters.db_entities.Chat;
import com.example.ap2_ex3.adapters.db_entities.Message;
import com.example.ap2_ex3.adapters.db_entities.User;
import com.example.ap2_ex3.adapters.db_entities.UserChatRelation;

@Database(entities = {User.class, Chat.class, Message.class, UserChatRelation.class, AppSettings.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context, AppDatabase.class, "app_db")
                    .allowMainThreadQueries().build();
        }
        return instance;
    }
    public abstract UserDao getUserDao();
    public abstract ChatDao getChatDao();
    public abstract MessageDao getMessageDao();
    public abstract UserChatRelationDao getUserChatRelationDao();
    public abstract AppSettingsDao getAppSettingsDao();
}