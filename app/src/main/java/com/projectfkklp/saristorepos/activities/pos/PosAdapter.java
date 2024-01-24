package com.projectfkklp.saristorepos.activities.pos;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
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

public class PosAdapter extends RecyclerView.Adapter<PosViewHolder> {
    private final Context context;
    private final List<Product> products;

    public PosAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public PosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pos_recycler_view, parent, false);
        return new PosViewHolder(view);
    }

    @SuppressLint({"DefaultLocale", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull PosViewHolder holder, int position) {
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

        if (product.getStocks()==0){
            holder.productOosIndicatorText.setVisibility(View.VISIBLE);
            holder.container.setEnabled(false);
            holder.container.setBackgroundColor(Color.rgb(255, 220, 220));
        }
        else {
            holder.productOosIndicatorText.setVisibility(View.GONE);
            holder.container.setEnabled(true);

            // Ripple Effect code when clicked
            TypedValue outValue = new TypedValue();
            context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
            holder.container.setBackgroundResource(outValue.resourceId);
        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }


}
