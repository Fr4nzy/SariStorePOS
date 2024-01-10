package com.projectfkklp.saristorepos.activities.pos;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.models.Product;

import java.util.List;

public class PointOfSalePageAdapter extends RecyclerView.Adapter<PointOfSalePageAdapter.MyViewHolder> {

    private Context context;
    private List<Product> dataList;
    private OnItemClickListener onItemClickListener; // Declare the listener

    public PointOfSalePageAdapter(Context context, List<Product> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pos_recycler_view, parent, false);
        return new MyViewHolder(view, onItemClickListener, dataList);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Product currentItem = dataList.get(position);

        // Log data properties
        Log.d("DataProperties", "_Product: " + currentItem.getProduct());
        Log.d("DataProperties", "Price: " + currentItem.getPrice());
        Log.d("DataProperties", "Stock: " + currentItem.getStock());

        // Use Glide to load the image
        Glide.with(context).load(currentItem.getImageURL()).into(holder.productImage);
        // Set text values
        holder.productLabel.setText(currentItem.getProduct());
        holder.priceLabel.setText("Price: "+String.valueOf(currentItem.getPrice()));
        holder.stockLabel.setText("Stock: "+String.valueOf(currentItem.getStock()));
        // Change card color if the product is out of stock
        if (currentItem.getStock() <= 0) {
            holder.cardContainer.setCardBackgroundColor(context.getResources().getColor(R.color.outOfStockColor));
            holder.outOfStockIndicator.setVisibility(View.VISIBLE);
        } else {
            // Set the default card color here (if needed)
            holder.cardContainer.setCardBackgroundColor(context.getResources().getColor(R.color.defaultCardColor));
            holder.outOfStockIndicator.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    // Interface for click events
    public interface OnItemClickListener {
        void onItemClick(Product data);
    }

    // Setter for click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        CardView cardContainer;
        LinearLayout outOfStockIndicator;
        ImageView productImage;
        TextView productLabel, priceLabel, stockLabel;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener, List<Product> dataList) {
            super(itemView);

            // Initialize views
            cardContainer = itemView.findViewById(R.id.card_container);
            productImage = itemView.findViewById(R.id.product_image);
            productLabel = itemView.findViewById(R.id.product_label);
            priceLabel = itemView.findViewById(R.id.price_label);
            stockLabel = itemView.findViewById(R.id.stock_label);
            outOfStockIndicator = itemView.findViewById(R.id.out_of_stock_indicator);

            // Set click listener for cartImageView
            cardContainer.setOnClickListener(v -> {
                // Handle click on the card view
                if (onItemClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Product clickedItem = dataList.get(position);

                        // Check if the product is in stock
                        if (clickedItem.getStock() > 0) {
                            // _Product is in stock, proceed with adding to cart
                            onItemClickListener.onItemClick(clickedItem);
                        } else {
                            // _Product is out of stock, show an alert dialog
                            showOutOfStockDialog();
                        }
                    }
                }
            });
        }
    }

    private void showOutOfStockDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Out of Stock");
        builder.setMessage("This product is currently out of stock.");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
            // Add more click listeners for other views as needed



    public void updateProductList(List<Product> newList) {
        dataList.clear();
        dataList.addAll(newList);
        notifyDataSetChanged();
    }
}
