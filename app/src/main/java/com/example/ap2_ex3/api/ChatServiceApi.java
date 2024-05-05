package com.example.ap2_ex3.api;

import com.example.ap2_ex3.adapters.db_entities.Chat;
import com.example.ap2_ex3.adapters.db_entities.Message;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ChatServiceApi {

    @POST("Chats")
    Call<Void> addChat(@Body Chat chat, @Header("Authorization") String token);

    @GET("Chats")
    Call<List<Gson>> getChats(@Header("Authorization") String token);

    @GET("Chats/{id}")
    Call<Chat> getChatById(@Body int id, @Header("Authorization") String token);

    @POST("Chats/{id}/Messages")
    Call<Void> addMessageToChat(@Body int id, String message, @Header("Authorization") String token);

    @GET("Chats/{id}/Messages")
    Call<Message[]> getMessagesFromChat(@Body int id, @Header("Authorization") String token);
}
