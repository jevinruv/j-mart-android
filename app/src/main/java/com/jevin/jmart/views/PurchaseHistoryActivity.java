package com.jevin.jmart.views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;

import com.jevin.jmart.R;
import com.jevin.jmart.adapters.PurchaseHistoryAdapter;
import com.jevin.jmart.helpers.APIClient;
import com.jevin.jmart.helpers.SharedPreferencesManager;
import com.jevin.jmart.models.Product;
import com.jevin.jmart.services.CheckoutService;
import com.jevin.jmart.services.ProductService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PurchaseHistoryActivity extends AppCompatActivity {

    private static final String TAG = PurchaseHistoryActivity.class.getSimpleName();

    private RecyclerView recyclerView;
    private List<Product> productList;
    private PurchaseHistoryAdapter purchaseHistoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.activity_purchase_history);

        init();
    }

    private void init() {

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productList = new ArrayList<>();
        purchaseHistoryAdapter = new PurchaseHistoryAdapter(this, productList);
        recyclerView.setAdapter(purchaseHistoryAdapter);

        fetchShoppingCartProducts();
    }

    private void fetchShoppingCartProducts() {

        int userId = SharedPreferencesManager.getUserId(this);
        CheckoutService checkoutService = APIClient.getClient().create(CheckoutService.class);

        Call<List<Product>> call = checkoutService.getPurchaseHistory(userId);
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                List<Product> products = response.body();
                productList.clear();
                productList.addAll(products);
                purchaseHistoryAdapter.notifyDataSetChanged();
                Log.d(TAG, "Number of products received: " + products.size());
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
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
}
