package com.jevin.jmart.views;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jevin.jmart.BuildConfig;
import com.jevin.jmart.R;
import com.jevin.jmart.helpers.APIClient;
import com.jevin.jmart.helpers.SharedPreferencesManager;
import com.jevin.jmart.models.Cart;
import com.jevin.jmart.models.CartProduct;
import com.jevin.jmart.models.Product;
import com.jevin.jmart.services.CartService;
import com.jevin.jmart.services.ICartService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class ProductDetailActivity extends AppCompatActivity {

    private static final String TAG = ProductDetailActivity.class.getSimpleName();

    private TextView lblDiscountPrice, lblPrice, lblDescription, lblQuantity;
    private ImageView image;
    private Button btnAdd, btnSub, btnRemove;
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private LinearLayout layoutQuantity;

    private Product product;
    private Boolean inCart = false;

    private String sharePath = "no";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        init();
        setData();
        fetchCartItemQuantity();
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

    public void btnFABClicked(View view) {

        if (inCart) {
            inCart = false;
            fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_add));
            fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.light_green)));

            layoutQuantity.setVisibility(View.GONE);

            manageCartItem(0, "delete");

        } else {
            inCart = true;
            fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_remove));
            fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.light_red)));

            layoutQuantity.setVisibility(View.VISIBLE);
            lblQuantity.setText(String.valueOf(1));
            manageCartItem(1, "update");
        }
    }

    public void btnAddClicked(View view) {
        int quantity = Integer.parseInt(lblQuantity.getText().toString());
        quantity++;
        lblQuantity.setText(String.valueOf(quantity));
        btnSub.setEnabled(true);

        manageCartItem(quantity, "update");
    }

    public void btnSubClicked(View view) {
        int quantity = Integer.parseInt(lblQuantity.getText().toString());
        quantity--;
        lblQuantity.setText(String.valueOf(quantity));

        manageCartItem(quantity, "update");

        if (quantity <= 1) {
            btnSub.setEnabled(false);
        }
    }

    public void btnShareClicked(View view) {

        takeScreenshot();
        if (!sharePath.equals("no")) {
            share(sharePath);
        }
    }

    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpeg";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            String filePath = imageFile.getPath();
            sharePath = filePath;

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void share(String sharePath) {

        File file = new File(sharePath);

        Uri uri = FileProvider.getUriForFile(
                ProductDetailActivity.this,
                BuildConfig.APPLICATION_ID + ".provider", file);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(intent);
    }

    private void init() {

        lblPrice = findViewById(R.id.lbl_price);
        lblQuantity = findViewById(R.id.lbl_quantity);
        lblDiscountPrice = findViewById(R.id.lbl_discount_price);
        lblDescription = findViewById(R.id.lbl_description);
        image = findViewById(R.id.img_header);

        layoutQuantity = findViewById(R.id.layout_quantity);

        btnAdd = findViewById(R.id.btn_add);
        btnSub = findViewById(R.id.btn_sub);
        btnRemove = findViewById(R.id.btn_remove);

        fab = findViewById(R.id.btn_fab);

        toolbar = findViewById(R.id.toolbar);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Gson gson = new Gson();
            product = gson.fromJson(bundle.getString("product"), Product.class);
        }
    }

    private void setData() {

        toolbar.setTitle(product.getName());

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lblPrice.setText(this.getString(R.string.price_with_currency, product.getPrice()));
        lblDescription.setText(product.getDescription());

        Glide.with(this).load(product.getImageUrl()).into(image);
    }

    private void fetchCartItemQuantity() {

        int cartId = SharedPreferencesManager.getCartId(this);

        ICartService cartService = APIClient.getClient().create(ICartService.class);
        Call<Cart> call = cartService.get(cartId);

        call.enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {

                Cart cart = response.body();
                List<CartProduct> cartProductList = cart.getCartProducts();

                Optional<CartProduct> cartProduct = cartProductList
                        .stream()
                        .filter(cartProd -> cartProd.getProduct().getId() == product.getId())
                        .findAny();

                if (cartProduct.isPresent()) {
                    int quantity = cartProduct.get().getQuantity();
                    lblQuantity.setText(String.valueOf(quantity));

                    inCart = true;
                    fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_remove));
                    fab.setBackgroundTintList(
                            ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.light_red)));

                    layoutQuantity.setVisibility(View.VISIBLE);
                }

                Log.d(TAG, "Number of cart products received: " + cartProductList.size());
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    private void manageCartItem(int quantity, String type) {

        CartService cartService = new CartService();

        if (type.equals("delete")) {
            cartService.deleteCartItem(this, product.getId(), quantity);
        } else {
            cartService.addOrUpdate(this, product.getId(), quantity);
        }
    }

}


