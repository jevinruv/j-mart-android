package com.jevin.jmart.views;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.jevin.jmart.R;
import com.jevin.jmart.adapters.ProductsListAdapter;
import com.jevin.jmart.fragments.HomeFragment;
import com.jevin.jmart.helpers.APIClient;
import com.jevin.jmart.models.Product;
import com.jevin.jmart.services.ProductService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SearchView searchView;
    private SearchView.OnQueryTextListener queryTextListener;

    private String searchKey = null;
    private List<Product> productList;
    private ProductsListAdapter productsListAdapter;
    private static final String TAG = HomeFragment.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        init();

        Intent intent = getIntent();

        if (intent.hasExtra("searchKey")) {
            searchKey = intent.getStringExtra("searchKey");
            productsListAdapter.getFilter().filter(searchKey);
            getSupportActionBar().setTitle(searchKey);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setMaxWidth(Integer.MAX_VALUE);

            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String query) {
                    productsListAdapter.getFilter().filter(query);
                    return false;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    productsListAdapter.getFilter().filter(query);
                    return false;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                getSupportActionBar().setTitle("JMart");
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }


    private void init() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productList = new ArrayList<>();
        productsListAdapter = new ProductsListAdapter(this, productList);
        recyclerView.setAdapter(productsListAdapter);

        fetchProducts();
    }

    private void fetchProducts() {
        ProductService productService = APIClient.getClient().create(ProductService.class);

        Call<List<Product>> call = productService.getAll();
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                List<Product> products = response.body();
                productList.clear();
                productList.addAll(products);
                productsListAdapter.getFilter().filter(searchKey);
                productsListAdapter.notifyDataSetChanged();
                Log.d(TAG, "Number of products received: " + products.size());
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

}
