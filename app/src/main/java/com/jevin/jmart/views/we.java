//package com.jevin.jmart.views;
//
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.support.v7.widget.GridLayoutManager;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//
//import com.jevin.jmart.R;
//import com.jevin.jmart.adapters.ProductsListAdapter;
//import com.jevin.jmart.models.Product;
//import com.jevin.jmart.services.APIClient;
//import com.jevin.jmart.services.ProductService;
//
//import java.util.List;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class MainActivity extends AppCompatActivity {
//
//    private static final String TAG = MainActivity.class.getSimpleName();
//    private RecyclerView recyclerView;
//    private List<Product> productList;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        data();
//        init();
//    }
//
//    public void init() {
//        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//    }
//
//    public void data() {
//
//        ProductService productService = APIClient.getClient().create(ProductService.class);
//
//        Call<List<Product>> call = productService.getAll();
//        call.enqueue(new Callback<List<Product>>() {
//            @Override
//            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
//
//                productList = response.body();
//                Log.d(TAG, "Number of products received: " + productList.size());
//                ProductsListAdapter productsListAdapter = new ProductsListAdapter(MainActivity.this, productList);
//                recyclerView.setAdapter(productsListAdapter);
//            }
//
//            @Override
//            public void onFailure(Call<List<Product>> call, Throwable t) {
//                Log.e(TAG, t.toString());
//            }
//        });
//
//    }
//
//}
