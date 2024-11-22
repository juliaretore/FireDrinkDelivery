package com.example.coffeeshop.data.model;

import com.example.coffeeshop.model.User;
import com.google.gson.annotations.SerializedName;

public class UserResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("user")
    private User user; // O objeto User retornado pelo backend

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }
}