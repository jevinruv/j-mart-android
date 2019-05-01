package com.jevin.jmart.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jevin.jmart.R;
import com.jevin.jmart.helpers.APIClient;
import com.jevin.jmart.helpers.SharedPreferencesManager;
import com.jevin.jmart.models.Cart;
import com.jevin.jmart.models.CartProduct;
import com.jevin.jmart.services.CheckoutService;
import com.jevin.jmart.services.ICartService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity {

    private static final String TAG = CheckoutActivity.class.getSimpleName();

    private TextView lblQuantity, lblTotal;
    private EditText inputCardNo;
    private TextInputLayout inputLayoutCardNo;

    private List<CartProduct> cartProductList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
        fetchCart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void init() {

        lblQuantity = findViewById(R.id.lbl_quantity);
        lblTotal = findViewById(R.id.lbl_total);

        inputCardNo = findViewById(R.id.input_card_number);
        inputLayoutCardNo = findViewById(R.id.input_layout_card_number);
    }

    public void btnPayClicked(View view) {

        if (validateInput(inputCardNo, inputLayoutCardNo)) {

            int cartId = SharedPreferencesManager.getCartId(this);

            CheckoutService checkoutService = APIClient.getClient().create(CheckoutService.class);
            Call<Cart> call = checkoutService.doCheckOut(cartId);

            call.enqueue(new Callback<Cart>() {
                @Override
                public void onResponse(Call<Cart> call, Response<Cart> response) {
                    Cart cart = response.body();
                    int cartId = cart.getId();
                    SharedPreferencesManager.setCartId(getApplicationContext(), cartId);

                    Toast.makeText(getApplicationContext(), getString(R.string.checkout_success), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onFailure(Call<Cart> call, Throwable t) {
                    Log.e(TAG, t.toString());
                }
            });
        }
    }

    private void fetchCart() {

        int cartId = SharedPreferencesManager.getCartId(this);

        ICartService cartService = APIClient.getClient().create(ICartService.class);
        Call<Cart> call = cartService.get(cartId);

        call.enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                Cart cart = response.body();
                cartProductList.clear();
                cartProductList.addAll(cart.getCartProducts());

                setData();
                Log.d(TAG, "Number of cart products received: " + cartProductList.size());
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    private void setData() {

        AtomicReference<Double> total = new AtomicReference<>((double) 0);
        AtomicInteger quantity = new AtomicInteger();

        cartProductList
                .stream().forEach(cartProduct -> {

            quantity.addAndGet(cartProduct.getQuantity());
            total.updateAndGet(v -> new Double((double) (v + cartProduct.getQuantity() * cartProduct.getProduct().getPrice())));
        });

        lblQuantity.setText(String.valueOf(quantity.get()));
        lblTotal.setText(getString(R.string.price_with_currency, total.get()));

    }

    private boolean validateInput(EditText editText, TextInputLayout textInputLayout) {
        if (editText.getText().toString().trim().isEmpty()) {

            textInputLayout.setError(getString(R.string.err_msg_empty));
            requestFocus(editText);
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

}
