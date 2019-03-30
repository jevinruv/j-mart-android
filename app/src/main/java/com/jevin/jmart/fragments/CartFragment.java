package com.jevin.jmart.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.jevin.jmart.R;
import com.jevin.jmart.adapters.CartListAdapter;
import com.jevin.jmart.models.Cart;
import com.jevin.jmart.models.CartProduct;
import com.jevin.jmart.services.APIClient;
import com.jevin.jmart.services.ICartService;
import com.jevin.jmart.services.PusherClient;
import com.jevin.jmart.services.SharedPreferencesManager;
import com.pusher.client.Pusher;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment {

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

        fetchCart();
        init();

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

    public void init() {

        String CHANNEL_NAME = "cart" + SharedPreferencesManager.getCartId(getContext());

        Pusher pusher = PusherClient.getPusher();
        Channel channel = pusher.subscribe(CHANNEL_NAME);

        SubscriptionEventListener eventListener = new SubscriptionEventListener() {
            @Override
            public void onEvent(String channel, final String event, final String data) {
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        System.out.println("Received event with data: " + data);

                        Gson gson = new Gson();
                        CartProduct cartProduct = gson.fromJson(data, CartProduct.class);
                        cartListAdapter.addItem(cartProduct);
                    }

                });
            }
        };

        channel.bind("itemAdded", eventListener);
        channel.bind("itemUpdated", eventListener);
        channel.bind("itemRemoved", eventListener);
    }


}
