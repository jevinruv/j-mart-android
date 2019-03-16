package com.jevin.jmart.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jevin.jmart.R;
import com.jevin.jmart.models.Product;
import com.jevin.jmart.views.ProductDetailActivity;

import java.util.List;

public class ProductsListAdapter extends RecyclerView.Adapter<ProductsListAdapter.MyViewHolder> {

    private Context context;
    private List<Product> productList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, price, quantity;
        public ImageView icAdd, icRemove, image;
        public CardView cardView;

        public MyViewHolder(View view) {
            super(view);

            name = (TextView) view.findViewById(R.id.lbl_name);
            price = (TextView) view.findViewById(R.id.lbl_price);
//            quantity = (TextView) view.findViewById(R.id.quantity);
            image = (ImageView) view.findViewById(R.id.image);
//            icAdd = (ImageView) view.findViewById(R.id.ic_add);
//            icRemove = (ImageView) view.findViewById(R.id.ic_remove);
            cardView = (CardView) view.findViewById(R.id.card);
        }
    }

    public ProductsListAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_list_product, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        final Product product = productList.get(position);
        holder.name.setText(product.getName());
        holder.price.setText(context.getString(R.string.price_with_currency, product.getPrice()));
        Glide.with(context).load(product.getImageUrl()).into(holder.image);

        holder.cardView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            Gson gson = new Gson();
            String productJson = gson.toJson(product);
            intent.putExtra("product", productJson);
            context.startActivity(intent);
        });

//        holder.icAdd.setOnClickListener(view -> {
//            Toast.makeText(context, "add", Toast.LENGTH_SHORT).show();
//        });
//
//        holder.icRemove.setOnClickListener(view -> {
//            Toast.makeText(context, "rem", Toast.LENGTH_SHORT).show();
//        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }


}
