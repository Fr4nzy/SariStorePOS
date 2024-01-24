package com.projectfkklp.saristorepos.activities.inventory;

import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.models.Product;
import com.projectfkklp.saristorepos.utils.StringUtils;

import java.util.List;

public class InventoryProductListAdapter extends RecyclerView.Adapter<InventoryProductListRecycler>{

    private final Context context;
    private final List<Product> products;

    public InventoryProductListAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public InventoryProductListRecycler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_product_list_recycler, parent, false);
        return new InventoryProductListRecycler(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull InventoryProductListRecycler holder, int position) {
        Product product = products.get(position);

        // Use Glide to load the image
        if (!StringUtils.isNullOrEmpty(product.getImgUrl())){
            Glide.with(context).load(product.getImgUrl()).into(holder.productImg);
        }

        holder.productNameText.setText(product.getName());
        holder.productStocksText.setText(String.format(
            "Stocks: %d",
            product.getStocks()
        ));
        holder.productUnitPriceText.setText(String.format(
            "Unit Price: ₱%.2f",
            product.getUnitPrice()
        ));

        holder.productOosIndicatorText.setVisibility(product.getStocks()== 0 ? View.VISIBLE:View.GONE);

        if (position==products.size()-1){
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.container.getLayoutParams();
            // Set the margin bottom
            layoutParams.setMargins(
                layoutParams.leftMargin,
                layoutParams.topMargin,
                layoutParams.rightMargin,
                20
            );
            // Apply the updated layout parameters to the CardView
            holder.container.setLayoutParams(layoutParams);
        }

        holder.container.setOnClickListener(v -> {
            Intent intent = new Intent(context, InventoryProductDetailPage.class);
            intent.putExtra("Product", new Product());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

}
