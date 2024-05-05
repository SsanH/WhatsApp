package com.example.ap2_ex3.api;

import android.util.Pair;

import com.example.ap2_ex3.interfaces.AppCallback;
import com.example.ap2_ex3.miscClasses.AsyncHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

public class ChatApi {
    public static void createChat(AsyncHttpClient httpClient,
                                  String otherUsername,
                                  AppCallback<Pair<JSONObject, Integer>> callback) {
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("username", otherUsername);

            httpClient.post("Chats", jsonObj.toString(), new AsyncHttpClient.AsyncHttpRequestCallback() {
                @Override
                public void onSuccess(String response, int responseCode) {
                    try {
                        JSONObject jsonChat = new JSONObject(response);
                        callback.onSuccess(new Pair<>(jsonChat, responseCode));
                    } catch (Exception e) {
                        System.out.println(e);
                        callback.onSuccess(new Pair<>(new JSONObject(), responseCode));
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    System.out.println("Request failed. Error: " + e.getMessage());
                    callback.onFailure();
                }
            });
        } catch (Exception e) {
            System.out.println("This execption should never actually raise but ok");
            System.out.println(e);
        }
    }

    public static void getChats(AsyncHttpClient httpClient, AppCallback<Pair<JSONArray, Integer>> callback) {
        httpClient.get("Chats", new AsyncHttpClient.AsyncHttpRequestCallback() {
            @Override
            public void onSuccess(String response, int responseCode) {
                try {
                    JSONArray jsonChats = new JSONArray(response);
                    callback.onSuccess(new Pair<>(jsonChats, responseCode));
                } catch (Exception e) {
                    this.onFailure(e);
                }
            }

            @Override
            public void onFailure(Exception e) {
                System.out.println("Request failed. Error: " + e.getMessage());
                callback.onFailure();
            }
        });
    }
}
