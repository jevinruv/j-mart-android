package com.jevin.jmart.services;

import com.jevin.jmart.forms.ShoppingCartForm;
import com.jevin.jmart.models.Cart;
import com.jevin.jmart.models.CartProduct;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ICartService {

    @GET("carts/{id}")
    Call<Cart> get(@Path("id") int id);

    @POST("carts")
    Call<CartProduct> addOrUpdate(@Body ShoppingCartForm shoppingCartForm);

    @GET("carts/fetch/{userId}")
    Call<Cart> fetchCart(@Path("userId") int userId);

    @DELETE("carts/{id}")
    Call<Void> deleteCart(@Path("id") int id);

    @HTTP(method = "DELETE", path = "carts", hasBody = true)
    Call<CartProduct> deleteCartItem(@Body ShoppingCartForm shoppingCartForm);
}
