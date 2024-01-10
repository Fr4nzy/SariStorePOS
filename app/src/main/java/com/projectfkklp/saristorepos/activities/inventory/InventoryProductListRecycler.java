package com.projectfkklp.saristorepos.activities.inventory;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.projectfkklp.saristorepos.R;

class InventoryProductListRecycler extends RecyclerView.ViewHolder {

    ImageView recImage;
    TextView recProduct, recPrice, recStock, outOfStockWarning;
    CardView recCard;

    public InventoryProductListRecycler(@NonNull View itemView) {
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
