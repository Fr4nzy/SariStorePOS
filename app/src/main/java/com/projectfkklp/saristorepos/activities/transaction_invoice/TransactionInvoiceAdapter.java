package com.projectfkklp.saristorepos.activities.transaction_invoice;

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
import com.projectfkklp.saristorepos.models.Transaction;
import com.projectfkklp.saristorepos.models.TransactionItem;
import com.projectfkklp.saristorepos.utils.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TransactionInvoiceAdapter extends RecyclerView.Adapter<TransactionInvoiceViewHolder>{
    private final Context context;
    private final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("MMMM d, yyyy hh:mm a");
    private final List<String> transactedProductIds;
    private List<Product> products;
    private List<TransactionItem> transactionItems;

    public TransactionInvoiceAdapter(Context context, List<String> transactedProductIds, List<Transaction> transactions) {
        this.context = context;
        this.transactedProductIds = transactedProductIds;

        loadTransactionItems(transactions);
    }

    private void loadTransactionItems(List<Transaction> transactions){
        transactionItems = new ArrayList<>();

        for (Transaction transaction:transactions){
            transactionItems.addAll(transaction.getItems());
        }
    }

    @NonNull
    @Override
    public TransactionInvoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        loadProducts();

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_invoice_recycler_view, parent, false);
        return new TransactionInvoiceViewHolder(view);
    }

    private void loadProducts(){
        // Dummy Product, replace on backend implementation
        products = new ArrayList<>(Arrays.asList(
                new Product("productabc001", "Product A", 5, 1, "", ""),
                new Product("productabc002", "Product B", 10, 5, "", ""),
                new Product("productabc003", "Product C", 15, 10, "", ""),
                new Product("productabc004", "Product D", 20, 20, "", ""),
                new Product("productabc005", "Product E", 25, 50, "", "")
        ));
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull TransactionInvoiceViewHolder holder, int position) {
        String transactedProductId = transactedProductIds.get(position);
        Product product = products.stream().filter(p -> p.getId().equals(transactedProductId)).findFirst().orElse(null);
        int qty = 0;
        float unitPrice = 0;

        for (TransactionItem item:transactionItems){
            if (item.getProductId().equals(transactedProductId)){
                qty+=item.getQuantity();
                unitPrice = item.getUnitPrice();
            }
        }

        assert product != null;
        if (!StringUtils.isNullOrEmpty(product.getImgUrl())){
            Glide.with(context).load(product.getImgUrl()).into(holder.image);
        }

        holder.invoiceNameText.setText(product.getName());
        holder.qtyAndPriceText.setText(String.format(
            "%d QTY * %s",
            qty,
            StringUtils.formatToPeso(unitPrice)
        ));
        holder.amountText.setText(
            String.format(
                "Amount: %s",
                StringUtils.formatToPeso(qty*unitPrice)
            )
        );
    }

    @Override
    public int getItemCount() {
        return transactedProductIds.size();
    }
}
