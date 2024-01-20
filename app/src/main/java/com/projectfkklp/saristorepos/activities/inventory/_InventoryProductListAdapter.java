package com.projectfkklp.saristorepos.activities.inventory;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.models._Product;

import java.util.List;

public class _InventoryProductListAdapter extends RecyclerView.Adapter<_InventoryProductListRecycler> {

    private Context context;
    private List<_Product> dataList;

    public _InventoryProductListAdapter(Context context, List<_Product> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public _InventoryProductListRecycler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout._inventory_product_list_recycler_view, parent, false);
        return new _InventoryProductListRecycler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull _InventoryProductListRecycler holder, int position) {
        _Product currentItem = dataList.get(position);

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
                // Create an intent to open the _InventoryProductDetailPage
                Intent intent = new Intent(context, _InventoryProductDetailPage.class);

                // Pass data to the intent
                intent.putExtra("Image", currentItem.getImageURL());
                intent.putExtra("Price", currentItem.getPrice());
                intent.putExtra("Product", currentItem.getProduct());
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

}

