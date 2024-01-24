package com.projectfkklp.saristorepos.activities.pos.checkout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.text.InputFilter;
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

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutViewHolder>{
    private final Context context;
    private final List<TransactionItem> transactionItems;
    private final List<Product> products;

    public CheckoutAdapter(Context context, List<TransactionItem> transactionItems, List<Product> products) {
        this.context = context;
        this.transactionItems = transactionItems;
        this.products = products;
    }

    @NonNull
    @Override
    public CheckoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkout_recycler_view, parent, false);
        return new CheckoutViewHolder(view);
    }

    @SuppressLint({"DefaultLocale", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull CheckoutViewHolder holder, int position) {
        TransactionItem transactionItem = transactionItems.get(position);
        Product product = products.stream()
            .filter(p -> p.getId().equals(transactionItem.getProductId()))
            .findFirst()
            .orElse(null);

        assert product != null;
        if (!StringUtils.isNullOrEmpty(product.getImgUrl())){
            Glide.with(context).load(product.getImgUrl()).into(holder.productImage);
        }
        holder.productNameText.setText(product.getName());
        holder.unitPriceText.setText(String.format(
            "Unit Price: %s",
            StringUtils.formatToPeso(transactionItem.getUnitPrice())
        ));
        holder.quantityEdit.setText(String.valueOf(transactionItem.getQuantity()));
        holder.subtotalPriceText.setText(StringUtils.formatToPeso(transactionItem.getAmount()));
        holder.leftItemText.setText(String.format(
            "%d item left",
            product.getStocks()
        ));

        holder.deleteBtn.setOnClickListener(view -> {
            showDeleteConfirmationDialog(transactionItem, product);
        });

        holder.minusBtn.setOnClickListener(view -> {
            if (transactionItem.getQuantity() == 1 ){
                showDeleteConfirmationDialog(transactionItem, product);
                return;
            }

            changeTransactionItemQuantity(holder, transactionItem, transactionItem.getQuantity()-1, product.getStocks());
        });

        holder.plusButton.setEnabled(transactionItem.getQuantity()<product.getStocks());
        holder.plusButton.setOnClickListener(view -> {
            changeTransactionItemQuantity(holder, transactionItem, transactionItem.getQuantity()+1,  product.getStocks());
        });
        holder.quantityEdit.setFilters(new InputFilter[]{new InputFilterMinMax(0, product.getStocks())});
        holder.quantityEdit.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus){
                return;
            }

            String newQuantityText = holder.quantityEdit.getText().toString();
            if(StringUtils.isNullOrEmpty(newQuantityText)){
                // Reset Quantity Edit Text
                holder.quantityEdit.setText(String.valueOf(transactionItem.getQuantity()));
                return;
            }

            int newQuantity = Integer.parseInt(newQuantityText);
            if (newQuantity==0){
                showDeleteConfirmationDialog(transactionItem, product);
                return;
            }

            transactionItem.setQuantity(newQuantity);
            holder.plusButton.setEnabled(newQuantity<product.getStocks());
            getParent().reloadViews();
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void showDeleteConfirmationDialog(TransactionItem transactionItem, Product product){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder
            .setTitle("Remove Item?")
            .setMessage(String.format(
                "Are you sure you want to remove \"%s\"?",
                product.getName()
            ))
            .setPositiveButton("Yes", (dialog, which) -> {
                transactionItems.remove(transactionItem);
                notifyDataSetChanged();
                getParent().reloadViews();
            })
            .setNegativeButton("No", (dialog, which) -> {
                dialog.dismiss();
            })
            .show();
    }

    private void changeTransactionItemQuantity(CheckoutViewHolder holder, TransactionItem transactionItem, int newQuantity, int stocks){
        transactionItem.setQuantity(newQuantity);
        holder.quantityEdit.setText(String.valueOf(newQuantity));
        holder.plusButton.setEnabled(newQuantity<stocks);
        getParent().reloadViews();
    }

    @Override
    public int getItemCount() {
        return transactionItems.size();
    }

    CheckoutPage getParent(){
        return (CheckoutPage) context;
    }

}
