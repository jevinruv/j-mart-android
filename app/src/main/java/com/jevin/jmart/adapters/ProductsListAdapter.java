package com.jevin.jmart.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jevin.jmart.R;
import com.jevin.jmart.models.Product;
import com.jevin.jmart.views.ProductDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class ProductsListAdapter extends RecyclerView.Adapter<ProductsListAdapter.MyViewHolder> implements Filterable {

    private Context context;
    private List<Product> productList;
    private List<Product> productListFiltered;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, price, category;
        public ImageView icAdd, icRemove, image;
        public CardView cardView;

        public MyViewHolder(View view) {
            super(view);

            name = view.findViewById(R.id.lbl_name);
            price = view.findViewById(R.id.lbl_price);
            category = view.findViewById(R.id.lbl_category);
            image = view.findViewById(R.id.image);
//            icAdd =  view.findViewById(R.id.ic_add);
//            icRemove =  view.findViewById(R.id.ic_remove);
            cardView = view.findViewById(R.id.card);
        }
    }

    public ProductsListAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
        this.productListFiltered = productList;
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


}
