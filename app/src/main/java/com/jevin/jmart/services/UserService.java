package com.jevin.jmart.services;

import com.jevin.jmart.models.User;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserService {

    @GET("users/{id}")
    Call<User> get(@Path("id") int id);

    @PUT("users")
    Call<User> updateUser(@Body User user);

    @DELETE("users/{id}")
    Call<Boolean> delete(@Path("id") int id);

    @POST("users/token")
    Call<Void> validateToken();
}
