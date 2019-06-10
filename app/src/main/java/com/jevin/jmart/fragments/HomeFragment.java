package com.jevin.jmart.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.jevin.jmart.R;
import com.jevin.jmart.adapters.ProductsListAdapter;
import com.jevin.jmart.helpers.APIClient;
import com.jevin.jmart.helpers.SharedPreferencesManager;
import com.jevin.jmart.models.Cart;
import com.jevin.jmart.models.Product;
import com.jevin.jmart.services.ICartService;
import com.jevin.jmart.services.ProductService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {

    private static final String TAG = HomeFragment.class.getSimpleName();

    private RecyclerView recyclerView;
    private SearchView searchView;
    private SearchView.OnQueryTextListener queryTextListener;
    private List<Product> productList;
    private Cart cart;
    private ProductsListAdapter productsListAdapter;


    public HomeFragment() {
    }


    public static HomeFragment newInstance(String param1) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        productList = new ArrayList<>();
        cart = new Cart();
        productsListAdapter = new ProductsListAdapter(getActivity(), productList, cart);
        recyclerView.setAdapter(productsListAdapter);

        fetchProducts();
        fetchCart();

        return view;
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
                productsListAdapter.notifyDataSetChanged();
                Log.d(TAG, "Number of products received: " + products.size());
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    private void fetchCart() {

        ICartService cartService = APIClient.getClient().create(ICartService.class);

        int cartId = SharedPreferencesManager.getCartId(getContext());

        Call<Cart> call = cartService.get(cartId);

        call.enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {

                if (response.isSuccessful()) {
                    cart = response.body();
                    productsListAdapter.setCart(cart);
                    productsListAdapter.notifyDataSetChanged();
                    Log.d(TAG, "Number of carts received: " + cart);
                }
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
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
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                return true;
            default:
                break;
        }
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onOptionsItemSelected(item);
    }

}
