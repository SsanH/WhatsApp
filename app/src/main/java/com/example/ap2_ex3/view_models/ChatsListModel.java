package com.example.ap2_ex3.view_models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ap2_ex3.miscClasses.ChatItemData;

import java.util.ArrayList;
import java.util.List;

public class ChatsListModel extends ViewModel {
    private MutableLiveData<List<ChatItemData>> chats;

    public ChatsListModel() {
        chats = new MutableLiveData<>();
        chats.setValue(new ArrayList<>());
    }

    public LiveData<List<ChatItemData>> getChats() {
        return chats;
    }

    public void setChats(List<ChatItemData> chats) {
        this.chats.setValue(chats);
    }

    public void updateChatItem(int index, String content, String created) {
        List<ChatItemData> chatItems = chats.getValue();
        if (chatItems != null && index >= 0 && index < chatItems.size()) {
            ChatItemData tmp = chatItems.get(index);
            tmp.setLastMessageContent(content);
            tmp.setLastMessageCreated(created);
            chatItems.set(index, tmp);
            chats.setValue(chatItems);
        }
    }

    public ChatItemData getChatItem(int index) {
        List<ChatItemData> chatItems = chats.getValue();
        if (chatItems != null && index >= 0 && index < chatItems.size()) {
            return chatItems.get(index);
        }
        return null;
    }
}
