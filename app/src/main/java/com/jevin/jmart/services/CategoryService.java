package com.jevin.jmart.services;

import com.jevin.jmart.models.Category;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CategoryService {

    @GET("categories")
    Call<List<Category>> getAll();
}
