package com.jevin.jmart.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jevin.jmart.R;
import com.jevin.jmart.fragments.HomeFragment;
import com.jevin.jmart.models.Cart;
import com.jevin.jmart.models.CartProduct;
import com.jevin.jmart.models.Product;
import com.jevin.jmart.services.CartService;
import com.jevin.jmart.views.ProductDetailActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductsListAdapter extends RecyclerView.Adapter<ProductsListAdapter.MyViewHolder> implements Filterable {

    private static final String TAG = ProductsListAdapter.class.getSimpleName();

    private Context context;
    private List<Product> productList;
    private List<Product> productListFiltered;
    private Cart cart;
    private boolean inCart = false;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, price, category;
        public ImageView image;
        public CardView cardView;
        public Button btnCart;

        public MyViewHolder(View view) {
            super(view);

            name = view.findViewById(R.id.lbl_name);
            price = view.findViewById(R.id.lbl_price);
            category = view.findViewById(R.id.lbl_category);
            image = view.findViewById(R.id.image);
            cardView = view.findViewById(R.id.card);
            btnCart = view.findViewById(R.id.btn_cart);
        }
    }

    public ProductsListAdapter(Context context, List<Product> productList, Cart cart) {
        this.context = context;
        this.productList = productList;
        this.productListFiltered = productList;
        this.cart = cart;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_list_product, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        final Product product = productListFiltered.get(position);
        holder.name.setText(product.getName());
        holder.category.setText(product.getCategory().getName());
        holder.price.setText(context.getString(R.string.price_with_currency, product.getPrice()));
        Glide.with(context).load(product.getImageUrl()).into(holder.image);

        holder.cardView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            Gson gson = new Gson();
            String productJson = gson.toJson(product);
            intent.putExtra("product", productJson);
            context.startActivity(intent);
        });

        holder.btnCart.setOnClickListener(view -> {
            btnCartClicked(product, holder);
        });

        if (cart != null && cart.getCartProducts() != null) {
            setCartQuantity(product, holder);
        }

    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    @Override
    public int getItemCount() {
        return productListFiltered.size();
    }

    @Override
    public Filter getFilter() {

        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();

                if (charString.isEmpty()) {
                    productListFiltered = productList;
                } else {
                    List<Product> filteredList = new ArrayList<>();
                    for (Product row : productList) {

                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) ||
                                row.getCategory().getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    productListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = productListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                productListFiltered = (ArrayList<Product>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    private void setCartQuantity(Product product, MyViewHolder holder) {

        List<CartProduct> cartProductList = cart.getCartProducts();

        Optional<CartProduct> cartProduct = cartProductList
                .stream()
                .filter(cartProd -> cartProd.getProduct().getId() == product.getId())
                .findAny();

        if (cartProduct.isPresent()) {
            inCart = true;
            holder.btnCart.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_round_corner_red));
            holder.btnCart.setText("Remove");
        }
    }

    private void btnCartClicked(Product product, MyViewHolder holder) {

        if (inCart) {
            inCart = false;
            holder.btnCart.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_round_corner_green));
            holder.btnCart.setText("Add to Cart");

            manageCartItem(0, "delete", product);

        } else {
            inCart = true;
            holder.btnCart.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_round_corner_red));
            holder.btnCart.setText("Remove");

            manageCartItem(1, "update", product);
        }
    }

    private void manageCartItem(int quantity, String type, Product product) {

        CartService cartService = new CartService();

        if (type.equals("delete")) {
            cartService.deleteCartItem(context, product.getId(), quantity);
        } else {
            cartService.addOrUpdate(context, product.getId(), quantity);
        }
    }

}
