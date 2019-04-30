package com.jevin.jmart.services;

import com.jevin.jmart.models.Cart;
import com.jevin.jmart.models.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CheckoutService {

    @GET("checkout/{id}")
    Call<Cart> doCheckOut(@Path("id") int id);

    @GET("checkout/history/{userId}")
    Call<List<Product>> getPurchaseHistory(@Path("userId") int id);
}
