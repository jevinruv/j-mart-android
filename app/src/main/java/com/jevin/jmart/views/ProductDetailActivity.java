package com.jevin.jmart.views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.jevin.jmart.R;
import com.jevin.jmart.models.Product;

public class ProductDetailActivity extends AppCompatActivity {

    private TextView name, price, description;
    private ImageView image;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        init();
        setData();
    }

    private void init() {

        name = findViewById(R.id.name);
        price = findViewById(R.id.price);
        description = findViewById(R.id.description);
        image = findViewById(R.id.image);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Gson gson = new Gson();
            product = gson.fromJson(bundle.getString("product"), Product.class);
        }
    }

    public void setData() {
        name.setText(product.getName());
        price.setText(this.getString(R.string.price_with_currency, product.getPrice()));
        description.setText(product.getDescription());
        Glide.with(this).load(product.getImageUrl()).into(image);

    }

}
