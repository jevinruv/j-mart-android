package com.jevin.jmart.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jevin.jmart.R;
import com.jevin.jmart.adapters.CartListAdapter;
import com.jevin.jmart.helpers.APIClient;
import com.jevin.jmart.helpers.ICartListener;
import com.jevin.jmart.helpers.MyApplication;
import com.jevin.jmart.helpers.SharedPreferencesManager;
import com.jevin.jmart.models.Cart;
import com.jevin.jmart.models.CartProduct;
import com.jevin.jmart.services.ICartService;
import com.jevin.jmart.views.CheckoutActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment implements ICartListener {

    private static final String TAG = CartFragment.class.getSimpleName();

    private RecyclerView recyclerView;
    private FloatingActionButton btnCheckout;
    private TextView lblEmpty;

    private List<CartProduct> cartProductList = new ArrayList<>();
    private CartListAdapter cartListAdapter;

    public CartFragment() {
    }

    public void btnCheckoutClicked() {

        if (!cartProductList.isEmpty()) {
            Intent intent = new Intent(getContext(), CheckoutActivity.class);
            startActivity(intent);
        }
        else{
            Toast.makeText(getContext(),getString(R.string.empty_cart),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        btnCheckout = view.findViewById(R.id.btn_checkout);
        lblEmpty = view.findViewById(R.id.lbl_empty);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        cartProductList = new ArrayList<>();
        cartListAdapter = new CartListAdapter(getActivity(), cartProductList);
        recyclerView.setAdapter(cartListAdapter);

        btnCheckout.setOnClickListener(v -> {
            btnCheckoutClicked();
        });

        MyApplication.setiCartListener(this);
        fetchCart();

        return view;
    }

    private void fetchCart() {

        int cartId = SharedPreferencesManager.getCartId(getContext());

        ICartService cartService = APIClient.getClient().create(ICartService.class);
        Call<Cart> call = cartService.get(cartId);

        call.enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                Cart cart = response.body();

                if(!cart.getCartProducts().isEmpty()){
                    cartProductList.clear();
                    cartProductList.addAll(cart.getCartProducts());
                    cartListAdapter.notifyDataSetChanged();
                }
                else{
                    lblEmpty.setVisibility(View.VISIBLE);
                }

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
        cartListAdapter.updateItem(cartProduct);
    }

    @Override
    public void itemRemoved(CartProduct cartProduct) {
        cartListAdapter.removeItem(cartProduct);
    }

    @Override
    public void cartCleared() {

    }

    @Override
    public void cartCheckOut() {

    }
}
