package com.projectfkklp.saristorepos.activities.inventory;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.projectfkklp.saristorepos.R;

public class InventoryProductListRecycler extends RecyclerView.ViewHolder {

    ImageView productImg;
    TextView productNameText, productStocksText, productUnitPriceText, productOosIndicatorText;

    public InventoryProductListRecycler(@NonNull View itemView) {
        super(itemView);

        // Initialize views
        productImg = itemView.findViewById(R.id.inventory_product_list_image);
        productNameText = itemView.findViewById(R.id.inventory_product_list_name);
        productStocksText = itemView.findViewById(R.id.inventory_product_list_stocks);
        productUnitPriceText = itemView.findViewById(R.id.inventory_product_list_unit_price);
        productOosIndicatorText = itemView.findViewById(R.id.inventory_product_list_out_of_stock);
    }
}