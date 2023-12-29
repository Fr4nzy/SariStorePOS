package com.projectfkklp.saristorepos.adapters;

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
import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.activities.inventory.InventoryProductDetailPage;
import com.projectfkklp.saristorepos.models.Product;

import java.util.List;

public class InventoryPageAdapter extends RecyclerView.Adapter<InventoryPageAdapter.MyViewHolder> {

    private Context context;
    private List<Product> dataList;

    public InventoryPageAdapter(Context context, List<Product> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_product_list_recycler_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Product currentItem = dataList.get(position);

        // Use Glide to load the image
        Glide.with(context).load(currentItem.getImageURL()).into(holder.recImage);

        // Set text values
        holder.recProduct.setText(currentItem.getProduct());
        holder.recPrice.setText(String.valueOf(currentItem.getPrice()));
        holder.recStock.setText(String.valueOf(currentItem.getStock()));

        // Check if the stock is less than or equal to 0
        if (currentItem.getStock() <= 0) {
            holder.outOfStockWarning.setVisibility(View.VISIBLE);
        } else {
            holder.outOfStockWarning.setVisibility(View.GONE);
        }

        // Handle item click
        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to open the InventoryProductDetailPage
                Intent intent = new Intent(context, InventoryProductDetailPage.class);

                // Pass data to the intent
                intent.putExtra("Image", currentItem.getImageURL());
                intent.putExtra("Price", currentItem.getPrice());
                intent.putExtra("_Product", currentItem.getProduct());
                intent.putExtra("Key", currentItem.getKey());
                intent.putExtra("Stock", currentItem.getStock());

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

