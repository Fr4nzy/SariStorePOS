package com.projectfkklp.saristorepos.activities.pos;

import android.view.View;
import android.widget.FrameLayout;
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
    TextView productNameText, productStocksText, productUnitPriceText, productOosIndicatorText, itemQuantityText;
    FrameLayout cartFrame;

    public PosViewHolder(@NonNull View itemView) {
        super(itemView);

        // Initialize views
        clickableContainer = itemView.findViewById(R.id.pos_container_clickable);
        container = itemView.findViewById(R.id.pos_container);
        productImg = itemView.findViewById(R.id.pos_image);
        productNameText = itemView.findViewById(R.id.pos_name);
        productStocksText = itemView.findViewById(R.id.pos_stocks);
        productUnitPriceText = itemView.findViewById(R.id.pos_unit_price);
        productOosIndicatorText = itemView.findViewById(R.id.pos_out_of_stock);
        itemQuantityText = itemView.findViewById(R.id.pos_quantity);
        cartFrame = itemView.findViewById(R.id.pos_cart_frame);
    }
}
