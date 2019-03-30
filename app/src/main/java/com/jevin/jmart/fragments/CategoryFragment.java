package com.jevin.jmart.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jevin.jmart.R;
import com.jevin.jmart.adapters.CategoryListAdapter;
import com.jevin.jmart.models.Category;
import com.jevin.jmart.helpers.APIClient;
import com.jevin.jmart.services.CategoryService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Category> categoryList;
    private CategoryListAdapter categoryListAdapter;
    private static final String TAG = CategoryFragment.class.getSimpleName();

    public CategoryFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_category, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        categoryList = new ArrayList<>();
        categoryListAdapter = new CategoryListAdapter(getActivity(), categoryList);
        recyclerView.setAdapter(categoryListAdapter);

        fetchCategories();

        return view;
    }

    private void fetchCategories() {
        CategoryService categoryService = APIClient.getClient().create(CategoryService.class);

        Call<List<Category>> call = categoryService.getAll();
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                List<Category> categories = response.body();
                categoryList.clear();
                categoryList.addAll(categories);
                categoryListAdapter.notifyDataSetChanged();
                Log.d(TAG, "Number of products received: " + categories.size());
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

}
