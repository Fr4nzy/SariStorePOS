package com.example.saristorepos;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private Context context;
    private List<DataClass> dataList;

    public MyAdapter(Context context, List<DataClass> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerviewer, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DataClass currentItem = dataList.get(position);

        // Use Glide to load the image
        Glide.with(context).load(currentItem.getDataImage()).into(holder.recImage);

        // Set text values
        holder.recProduct.setText(currentItem.getDataProduct());
        holder.recPrice.setText(String.valueOf(currentItem.getDataPrice()));
        holder.recStock.setText(String.valueOf(currentItem.getDataStock()));

        // Check if the stock is less than or equal to 0
        if (currentItem.getDataStock() <= 0) {
            holder.outOfStockWarning.setVisibility(View.VISIBLE);
        } else {
            holder.outOfStockWarning.setVisibility(View.GONE);
        }

        // Handle item click
        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to open the DetailActivity
                Intent intent = new Intent(context, DetailActivity.class);

                // Pass data to the intent
                intent.putExtra("Image", currentItem.getDataImage());
                intent.putExtra("Price", currentItem.getDataPrice());
                intent.putExtra("Product", currentItem.getDataProduct());
                intent.putExtra("Key", currentItem.getKey());
                intent.putExtra("Stock", currentItem.getDataStock());

                // Start the activity
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView recImage;
        TextView recProduct, recPrice, recStock, outOfStockWarning;
        CardView recCard;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize views
            recImage = itemView.findViewById(R.id.recImage);
            recCard = itemView.findViewById(R.id.recCard);
            recPrice = itemView.findViewById(R.id.recPrice);
            recStock = itemView.findViewById(R.id.recStock);
            recProduct = itemView.findViewById(R.id.recProduct);
            outOfStockWarning = itemView.findViewById(R.id.outOfStockWarning);
        }
    }
}
