package com.projectfkklp.saristorepos.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import android.widget.Button;
import android.widget.Toast;

import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.activities.pos.PosCheckoutPage;
import com.projectfkklp.saristorepos.models.Product;

public class CheckoutPageAdapter extends RecyclerView.Adapter<CheckoutPageAdapter.CartViewHolder> {

    private final Context context;
    private final List<Product> dataList;
    private OnQuantityChangeListener quantityChangeListener;

    public CheckoutPageAdapter(Context context, List<Product> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    public interface OnQuantityChangeListener {
        void onQuantityChange(int position, int newQuantity);
    }

    public void setOnQuantityChangeListener(OnQuantityChangeListener listener) {
        this.quantityChangeListener = listener;
    }

    //If uncommented, dataList value will go OutOfBounds
    /*private int cartItemCount = 0;*/
    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pos_checkout_recycler_view, parent, false);
        /*CartViewHolder cartViewHolder = new CartViewHolder(view);
        Product cartItem = dataList.get(cartItemCount);
        updatePrice(cartViewHolder, cartItem);
        cartItemCount++;*/
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product cartItem = dataList.get(position);

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
            try {
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
            }catch (Exception e) {
                return;
            }
        });

        // Initialize button visibility
        updateButtonVisibility(holder, cartItem);

    }

    // Add this method to update button visibility based on stock
    private void updateButtonVisibility(CartViewHolder holder, Product cartItem) {
        holder.btnMinus.setEnabled(cartItem.getQuantity() > 1);
        holder.btnPlus.setEnabled(cartItem.getQuantity() < cartItem.getStock());
    }

    private void updatePrice(CartViewHolder holder, Product cartItem) {
        double totalPrice = cartItem.getTotalPrice();
        String formattedTotalPrice = String.format("%.2f", totalPrice);
        holder.cartPrice.setText(formattedTotalPrice);
        updateOverallTotalAmount();
    }

    private void updateOverallTotalAmount() {
        if (context instanceof PosCheckoutPage) {
            ((PosCheckoutPage) context).updateOverallTotalAmount();
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

