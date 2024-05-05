package com.example.ap2_ex3.api;

import android.widget.Toast;

import com.example.ap2_ex3.R;
import com.example.ap2_ex3.activities.MainActivity;
import com.example.ap2_ex3.adapters.db_entities.User;
import com.example.ap2_ex3.interfaces.AppCallback;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class UserApi {

    private Retrofit retrofit;
    private UserServiceApi userServiceApi;
    private String token;

    public UserApi() {
        String baseUrl = MainActivity.database.getAppSettingsDao().get(1).getBaseUrl();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        userServiceApi = retrofit.create(UserServiceApi.class);
        token = null;
    }

    public void addUser(User user, Callback<Void> callback) {
        Call<Void> call = userServiceApi.addUser(user);
        call.enqueue(callback);
    }

    public void login(String username, String password, AppCallback<String> callback) {
        TokenRequest tokenRequest = new TokenRequest(username, password, MainActivity.database.getAppSettingsDao().get(1).getLastToken());
        Call<String> call = userServiceApi.addToken(tokenRequest);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                if (response.isSuccessful()) {
                    String token = response.body();
                    callback.onSuccess(token);
                } else {
                    callback.onFailure();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callback.onFailure();
            }
        });
    }

    public void getUser(String username, String token, AppCallback<User> callback) {
        Call<User> userCall = userServiceApi.getUser(username, "bearer " + token);
        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User userResponse = response.body();
                    if (userResponse != null) {
                        callback.onSuccess(userResponse);
                    } else {
                        callback.onFailure();
                    }
                } else {
                    callback.onFailure();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                callback.onFailure();
            }
        });
    }

}