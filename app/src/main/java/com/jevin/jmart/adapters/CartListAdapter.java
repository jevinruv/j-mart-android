package com.jevin.jmart.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.jevin.jmart.R;
import com.jevin.jmart.forms.ShoppingCartForm;
import com.jevin.jmart.models.CartProduct;
import com.jevin.jmart.models.Product;
import com.jevin.jmart.services.APIClient;
import com.jevin.jmart.services.CartService;
import com.jevin.jmart.views.ProductDetailActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.MyViewHolder> {

    private Context context;
    private List<CartProduct> cartProductList;
    private static final String TAG = CartListAdapter.class.getSimpleName();


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, price, quantity;
        public ImageView image;
        public Button btnAdd, btnSub, btnRemove;
        public CardView cardView;

        public MyViewHolder(View view) {
            super(view);

            name = view.findViewById(R.id.lbl_name);
            price = view.findViewById(R.id.lbl_price);
            quantity = view.findViewById(R.id.lbl_quantity);
            image = view.findViewById(R.id.image);
            btnAdd = view.findViewById(R.id.btn_add);
            btnSub = view.findViewById(R.id.btn_sub);
            btnRemove = view.findViewById(R.id.btn_remove);
            cardView = view.findViewById(R.id.card);
        }
    }

    public CartListAdapter(Context context, List<CartProduct> cartProductList) {
        this.context = context;
        this.cartProductList = cartProductList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_list_cart, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        final CartProduct cartProduct = cartProductList.get(position);
        final Product product = cartProduct.getProduct();

        holder.name.setText(product.getName());
        holder.quantity.setText(String.valueOf(cartProduct.getQuantity()));
        holder.price.setText(context.getString(R.string.price_with_currency, product.getPrice()));
        Glide.with(context).load(product.getImageUrl()).apply(RequestOptions.circleCropTransform()).into(holder.image);

        holder.cardView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            Gson gson = new Gson();
            String productJson = gson.toJson(product);
            intent.putExtra("product", productJson);
            context.startActivity(intent);
        });

        if (Integer.parseInt(holder.quantity.getText().toString()) <= 1) {
            holder.btnSub.setEnabled(false);
        }

        holder.btnAdd.setOnClickListener(view -> {
            btnAddClicked(holder, cartProduct);
        });

        holder.btnSub.setOnClickListener(view -> {
            btnSubClicked(holder, cartProduct);
        });

        holder.btnRemove.setOnClickListener(view -> {
            manageCartItem(cartProduct, 0, "delete");
        });

    }

    @Override
    public int getItemCount() {
        return cartProductList.size();
    }

    private void btnAddClicked(MyViewHolder holder, CartProduct cartProduct) {

        int quantity = Integer.parseInt(holder.quantity.getText().toString());
        quantity++;
        holder.quantity.setText(String.valueOf(quantity));
        holder.btnSub.setEnabled(true);

        manageCartItem(cartProduct, quantity, "update");
    }

    private void btnSubClicked(MyViewHolder holder, CartProduct cartProduct) {

        int quantity = Integer.parseInt(holder.quantity.getText().toString());
        quantity--;
        holder.quantity.setText(String.valueOf(quantity));

        manageCartItem(cartProduct, quantity, "update");

        if (quantity <= 1) {
            holder.btnSub.setEnabled(false);
        }
    }

    private void manageCartItem(CartProduct cartProduct, int quantity, String type) {

        CartService categoryService = APIClient.getClient().create(CartService.class);
        ShoppingCartForm shoppingCartForm = new ShoppingCartForm(cartProduct.getId(), cartProduct.getProduct().getId(), quantity);

        Call<CartProduct> call = null;

        if (type.equals("delete")) {
            call = categoryService.deleteCartItem(shoppingCartForm);
        } else {
            call = categoryService.add(shoppingCartForm);
        }

        call.enqueue(new Callback<CartProduct>() {
            @Override
            public void onResponse(Call<CartProduct> call, Response<CartProduct> response) {
                CartProduct cartProduct = response.body();
                Log.d(TAG, "Number of cart product received: " + cartProduct);
            }

            @Override
            public void onFailure(Call<CartProduct> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

}
