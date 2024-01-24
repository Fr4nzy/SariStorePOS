package com.projectfkklp.saristorepos.activities.pos;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.models.Product;
import com.projectfkklp.saristorepos.models.TransactionItem;
import com.projectfkklp.saristorepos.utils.StringUtils;

import java.util.List;
import java.util.Optional;

public class PosAdapter extends RecyclerView.Adapter<PosViewHolder> {
    private final int PALE_GREEN = Color.rgb(220, 255, 220);
    private final int PALE_RED = Color.rgb(255, 220, 220);

    private final Context context;
    private final List<Product> products;
    private final List<TransactionItem> transactionItems;

    public PosAdapter(Context context, List<Product> products, List<TransactionItem> transactionItems) {
        this.context = context;
        this.products = products;
        this.transactionItems = transactionItems;
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
            holder.clickableContainer.setEnabled(false);
            holder.container.setBackgroundColor(PALE_RED);

            return;
        }

        holder.productOosIndicatorText.setVisibility(View.GONE);
        holder.clickableContainer.setEnabled(true);
        holder.container.setBackgroundColor(isListed(product)? PALE_GREEN: Color.WHITE);

        // On Click
        holder.clickableContainer.setOnClickListener(view -> clickItem(holder, product));
    }

    private boolean isListed(Product product){
        return transactionItems
            .stream()
            .anyMatch(ti->ti.getProductId().equals(product.getId()));
    }

    private void clickItem(PosViewHolder holder, Product product){
        // product in transactionItems?
        Optional<TransactionItem> optionalTransactionItem = transactionItems
            .stream()
            .filter(ti->ti.getProductId().equals(product.getId()))
            .findFirst();
        boolean isListed = optionalTransactionItem.isPresent();

        if (isListed){
            transactionItems.remove(optionalTransactionItem.get());

            holder.container.setBackgroundColor(Color.WHITE);
        }
        else {
            transactionItems.add(new TransactionItem(
                product.getId(),
                1,
                product.getUnitPrice()
            ));

            holder.container.setBackgroundColor(PALE_GREEN);
        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }


}
