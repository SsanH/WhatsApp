package com.example.ap2_ex3.api;

import com.example.ap2_ex3.adapters.db_entities.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserServiceApi {
    @GET("Users/{username}")
    Call<User> getUser(@Path("username") String username, @Header("Authorization") String token);

    @POST("Users")
    Call<Void> addUser(@Body User user);

    @POST("Tokens")
    Call<String> addToken(@Body TokenRequest tokenRequest);
}
