package com.jevin.jmart.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jevin.jmart.R;
import com.jevin.jmart.adapters.CartListAdapter;
import com.jevin.jmart.helpers.APIClient;
import com.jevin.jmart.helpers.ICartListener;
import com.jevin.jmart.helpers.MyApplication;
import com.jevin.jmart.helpers.SharedPreferencesManager;
import com.jevin.jmart.models.Cart;
import com.jevin.jmart.models.CartProduct;
import com.jevin.jmart.services.ICartService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment implements ICartListener {

    private RecyclerView recyclerView;
    private List<CartProduct> cartProductList;
    private CartListAdapter cartListAdapter;
    private static final String TAG = CartFragment.class.getSimpleName();

    public CartFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        cartProductList = new ArrayList<>();
        cartListAdapter = new CartListAdapter(getActivity(), cartProductList);
        recyclerView.setAdapter(cartListAdapter);

        MyApplication.setiCartListener(this);
        fetchCart();

        return view;
    }

    private void fetchCart() {

        ICartService cartService = APIClient.getClient().create(ICartService.class);

        SharedPreferencesManager.setCartId(getContext(), 1);

        int cartId = SharedPreferencesManager.getCartId(getContext());

        Call<Cart> call = cartService.get(cartId);

        call.enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                Cart cart = response.body();
                cartProductList.clear();
                cartProductList.addAll(cart.getCartProducts());
                cartListAdapter.notifyDataSetChanged();

                Log.d(TAG, "Number of cart products received: " + cartProductList.size());
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }


    @Override
    public void itemAdded(CartProduct cartProduct) {
        cartListAdapter.addItem(cartProduct);
    }

    @Override
    public void itemUpdated(CartProduct cartProduct) {

    }

    @Override
    public void itemRemoved(CartProduct cartProduct) {

    }

    @Override
    public void cartCleared() {

    }

    @Override
    public void cartCheckOut() {

    }
}
