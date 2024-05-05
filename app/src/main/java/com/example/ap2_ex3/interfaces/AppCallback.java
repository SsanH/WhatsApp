package com.example.ap2_ex3.interfaces;

public interface AppCallback<T> {
    void onSuccess(T data);
    void onFailure();
}
