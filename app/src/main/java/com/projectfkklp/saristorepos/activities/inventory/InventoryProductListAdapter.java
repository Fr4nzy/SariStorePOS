package com.projectfkklp.saristorepos.activities.inventory;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.models.Product;

import java.util.List;

public class InventoryProductListAdapter extends RecyclerView.Adapter<InventoryProductListRecycler>{

    private Context context;
    private List<Product> products;

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

    @Override
    public void onBindViewHolder(@NonNull InventoryProductListRecycler holder, int position) {
        Product currentItem = products.get(position);

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

}
