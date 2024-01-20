package com.projectfkklp.saristorepos.activities.transaction.transaction_history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.models._Transaction;
import com.projectfkklp.saristorepos.utils.StringUtils;

import java.util.List;

public class TransactionHistoryAdapter extends RecyclerView.Adapter<TransactionHistoryAdapter.TransactionViewHolder> {

    private final List<_Transaction> transactionList;

    // Constructor to initialize the adapter with a list of transactions
    public TransactionHistoryAdapter(List<_Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    // Inner ViewHolder class to represent each item in the RecyclerView
    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView transactionIdTextView;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            transactionIdTextView = itemView.findViewById(R.id.transaction_summary);
        }
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_history_recycler_view, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        // Bind data to each item
        _Transaction transaction = transactionList.get(position);

        String transactionSummary = transaction.toString();
        holder.transactionIdTextView.setText(transactionSummary);
        holder.transactionIdTextView.setLines(StringUtils.getLinesCount(transactionSummary));
    }



    @Override
    public int getItemCount() {
        // Return the size of the list
        return transactionList.size();
    }
}
