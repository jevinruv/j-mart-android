package com.jevin.jmart.services;

import com.jevin.jmart.models.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ProductService {

    @GET("/products")
    Call<List<Product>> getAll();
}
