package com.projectfkklp.saristorepos.activities.transaction.transaction_invoice;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.projectfkklp.saristorepos.R;

public class TransactionInvoiceViewHolder extends RecyclerView.ViewHolder {
    ShapeableImageView image;
    TextView invoiceNameText;
    TextView qtyAndPriceText;
    TextView amountText;
    public TransactionInvoiceViewHolder(@NonNull View itemView) {
        super(itemView);

        image = itemView.findViewById(R.id.transaction_invoice_image);
        invoiceNameText = itemView.findViewById(R.id.transaction_invoice_name);
        qtyAndPriceText = itemView.findViewById(R.id.transaction_invoice_qty_x_price);
        amountText = itemView.findViewById(R.id.transaction_invoice_amount);
    }
}