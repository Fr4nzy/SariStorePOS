package com.projectfkklp.saristorepos.activities.transaction.transaction_history;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.models.Transaction;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class TransactionHistoryAdapter extends RecyclerView.Adapter<TransactionHistoryViewHolder>{
    private final Context context;
    private final List<Transaction> transactions;
    private final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("MMMM d, yyyy hh:mm a");
    public TransactionHistoryAdapter(Context context, List<Transaction> transactions) {
        this.context = context;
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public TransactionHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_history_recycler_view, parent, false);
        return new TransactionHistoryViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull TransactionHistoryViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);

        holder.dateTimeText.setText(dateTimeFormat.format(transaction.getDateTime()));
        holder.totalSoldItemsText.setText(String.format(
                "Total Quantity: %,d",
                transaction.getTotalQuantity()
        ));
        holder.totalSalesText.setText(String.format(
                "Total Sales: ₱%,.2f",
                transaction.getTotalSales()
        ));
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }
}
