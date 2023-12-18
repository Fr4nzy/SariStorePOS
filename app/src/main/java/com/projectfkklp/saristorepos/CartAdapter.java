package com.projectfkklp.saristorepos;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import android.widget.Button;
import android.widget.Toast;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final Context context;
    private final List<DataClass> dataList;
    private OnQuantityChangeListener quantityChangeListener;

    public CartAdapter(Context context, List<DataClass> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    public interface OnQuantityChangeListener {
        void onQuantityChange(int position, int newQuantity);
    }

    public void setOnQuantityChangeListener(OnQuantityChangeListener listener) {
        this.quantityChangeListener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cartrecyclerview, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        DataClass cartItem = dataList.get(position);

        holder.cartProduct.setText(cartItem.getProduct());
        holder.quantityTextView.setText(String.valueOf(cartItem.getQuantity()));

        holder.btnMinus.setOnClickListener(v -> {
            int newQuantity = cartItem.getQuantity() - 1;
            if (newQuantity >= 1) {
                cartItem.setQuantity(newQuantity);
                updatePrice(holder, cartItem);
                notifyDataSetChanged();
                if (quantityChangeListener != null) {
                    quantityChangeListener.onQuantityChange(holder.getAdapterPosition(), newQuantity);

                }
                v.clearFocus();
            }
        });

        holder.btnPlus.setOnClickListener(v -> {
            int newQuantity = cartItem.getQuantity() + 1;
            if (newQuantity <= cartItem.getStock()) {
                cartItem.setQuantity(newQuantity);
                updatePrice(holder, cartItem);
                updateButtonVisibility(holder, cartItem);
                notifyDataSetChanged();
                if (quantityChangeListener != null) {
                    quantityChangeListener.onQuantityChange(holder.getAdapterPosition(), newQuantity);
                }
                v.clearFocus();
            } else {
                // Show a warning or handle the case where quantity exceeds stock
                Toast.makeText(context, "Cannot add more than available stock", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Add this method to update button visibility based on stock
    private void updateButtonVisibility(CartViewHolder holder, DataClass cartItem) {
        holder.btnMinus.setEnabled(cartItem.getQuantity() > 1);
        holder.btnPlus.setEnabled(cartItem.getQuantity() < cartItem.getStock());
    }

    private void updatePrice(CartViewHolder holder, DataClass cartItem) {
        double totalPrice = cartItem.getTotalPrice();
        String formattedTotalPrice = String.format("%.2f", totalPrice);
        holder.cartPrice.setText(formattedTotalPrice);
        updateOverallTotalAmount();
    }

    private void updateOverallTotalAmount() {
        if (context instanceof addtocart) {
            ((addtocart) context).updateOverallTotalAmount();
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView cartProduct, cartPrice, quantityTextView;
        Button btnMinus, btnPlus;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            cartProduct = itemView.findViewById(R.id.rec2Product);
            cartPrice = itemView.findViewById(R.id.recPrice);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnPlus = itemView.findViewById(R.id.btnPlus);
        }
    }
}

