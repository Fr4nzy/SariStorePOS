package com.projectfkklp.saristorepos.activities.inventory;

import android.annotation.SuppressLint;
import android.content.Context;
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

public class InventoryProductListAdapter extends RecyclerView.Adapter<InventoryProductListViewHolder>{

    private final Context context;
    private final List<Product> products;

    public InventoryProductListAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public InventoryProductListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_product_list_recycler, parent, false);
        return new InventoryProductListViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull InventoryProductListViewHolder holder, int position) {
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

        holder.productOosIndicatorText.setVisibility(product.getStocks()==0 ? View.VISIBLE:View.GONE);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

}
