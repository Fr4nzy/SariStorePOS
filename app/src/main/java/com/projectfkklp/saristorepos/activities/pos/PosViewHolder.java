package com.projectfkklp.saristorepos.activities.pos;

import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.projectfkklp.saristorepos.R;

public class PosViewHolder extends RecyclerView.ViewHolder {
    GridLayout clickableContainer;
    LinearLayout container;
    ImageView productImg;
    TextView productNameText, productStocksText, productUnitPriceText, productOosIndicatorText;

    public PosViewHolder(@NonNull View itemView) {
        super(itemView);

        // Initialize views
        clickableContainer = itemView.findViewById(R.id.product_picker_list_container_clickable);
        container = itemView.findViewById(R.id.product_picker_list_container);
        productImg = itemView.findViewById(R.id.product_picker_list_image);
        productNameText = itemView.findViewById(R.id.product_picker_list_name);
        productStocksText = itemView.findViewById(R.id.product_picker_list_stocks);
        productUnitPriceText = itemView.findViewById(R.id.product_picker_list_unit_price);
        productOosIndicatorText = itemView.findViewById(R.id.product_picker_list_out_of_stock);
    }
}
