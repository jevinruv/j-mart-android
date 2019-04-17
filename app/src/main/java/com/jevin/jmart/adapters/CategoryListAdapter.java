package com.jevin.jmart.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jevin.jmart.R;
import com.jevin.jmart.models.Category;

import java.util.List;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.MyViewHolder> {

    private Context context;
    private List<Category> categoryList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public ImageView image;
        public RelativeLayout card;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.lbl_name);
            card = view.findViewById(R.id.card);
            image = view.findViewById(R.id.image);
        }
    }

    public CategoryListAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_list_category, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        final Category category = categoryList.get(position);
        holder.name.setText(category.getName());
        Glide.with(context).load(category.getImageUrl()).into(holder.image);

//        holder.card.setOnClickListener(view -> {
//            Intent intent = new Intent(context, ProductDetailActivity_2.class);
//            Gson gson = new Gson();
//            String categoryJson = gson.toJson(category);
//            intent.putExtra("category", categoryJson);
//            context.startActivity(intent);
//        });

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }


}
