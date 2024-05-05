package com.example.ap2_ex3.view_models;

import android.app.Application;

import androidx.lifecycle.ViewModelProvider;

import com.example.ap2_ex3.view_models.ChatsListModel;

public class ChatsListModelProvider {

    private static ChatsListModel chatsViewModel;

    public static ChatsListModel getInstance(Application application) {
        if (chatsViewModel == null) {
            synchronized (ChatsListModelProvider.class) {
                if (chatsViewModel == null) {
                    chatsViewModel = new ViewModelProvider.AndroidViewModelFactory(application).create(ChatsListModel.class);
                }
            }
        }
        return chatsViewModel;
    }
}
