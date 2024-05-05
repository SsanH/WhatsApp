package com.example.ap2_ex3.api;

import android.util.Pair;

import com.example.ap2_ex3.activities.MainActivity;
import com.example.ap2_ex3.interfaces.AppCallback;
import com.example.ap2_ex3.miscClasses.AsyncHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

public class MessageApi {
    public static void createMessage(AsyncHttpClient httpClient,
                                  String message,
                                  String chatId,
                                  AppCallback<Pair<JSONObject, Integer>> callback) {
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("msg", message);

            httpClient.post("Chats/" + chatId + "/Messages", jsonObj.toString(), new AsyncHttpClient.AsyncHttpRequestCallback() {
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
            System.out.println("This exception should never actually raise but ok");
            System.out.println(e);
        }
    }

    public static void getMessages(AsyncHttpClient httpClient, String chatId, AppCallback<Pair<JSONArray, Integer>> callback) {
        httpClient.get("Chats/"+chatId+"/Messages", new AsyncHttpClient.AsyncHttpRequestCallback() {
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
