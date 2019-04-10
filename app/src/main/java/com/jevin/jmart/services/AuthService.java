package com.jevin.jmart.services;

import com.jevin.jmart.forms.LoginForm;
import com.jevin.jmart.models.JwtResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {

    @POST("auth/signin")
    Call<JwtResponse> signIn(@Body LoginForm loginForm);
}
