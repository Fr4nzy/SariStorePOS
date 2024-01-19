package com.projectfkklp.saristorepos.activities.inventory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.projectfkklp.saristorepos.R;

public class InventoryProductListRecycler extends RecyclerView.ViewHolder {

    ImageView inventory_product_image;
    TextView inventory_product_label, inventory_price_label, inventory_stock_label, inventory_out_of_stock_indicator;
    CardView recCard;

    public InventoryProductListRecycler(@NonNull View itemView) {
        super(itemView);

        // Initialize views
        /*inventory_product_image = itemView.findViewById(R.id.inventory_product_image);
        recCard = itemView.findViewById(R.id.recCard);
        inventory_price_label = itemView.findViewById(R.id.inventory_price_label);
        inventory_stock_label = itemView.findViewById(R.id.inventory_stock_label);
        inventory_product_label = itemView.findViewById(R.id.inventory_product_label);
        inventory_out_of_stock_indicator = itemView.findViewById(R.id.inventory_out_of_stock_indicator;*/
    }
}