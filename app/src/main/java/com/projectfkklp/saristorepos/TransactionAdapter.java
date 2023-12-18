package com.projectfkklp.saristorepos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private final List<TransactionDataClass> transactionList;

    // Constructor to initialize the adapter with a list of transactions
    public TransactionAdapter(List<TransactionDataClass> transactionList) {
        this.transactionList = transactionList;
    }

    // Inner ViewHolder class to represent each item in the RecyclerView
    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView transactionIdTextView;
        TextView transactionDateTextView;
        TextView transactionTotalAmountTextView;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            transactionIdTextView = itemView.findViewById(R.id.transactionID);
            transactionDateTextView = itemView.findViewById(R.id.transactionDate);
            transactionTotalAmountTextView = itemView.findViewById(R.id.transactionTotalAmount);
        }
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transactionhistorycard, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        // Bind data to each item
        TransactionDataClass transaction = transactionList.get(position);

        // Check if date is not null before formatting
        if (transaction.getDate() != null) {
            // Format the date using SimpleDateFormat
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            try {
                Date parsedDate = dateFormat.parse(transaction.getDate());
                SimpleDateFormat displayDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                String formattedDate = displayDateFormat.format(parsedDate);
                holder.transactionDateTextView.setText(formattedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            holder.transactionDateTextView.setText("N/A"); // or any default value
        }

        // Display totalAmount as String
        holder.transactionTotalAmountTextView.setText(String.valueOf(transaction.getTotalAmount()));
        holder.transactionIdTextView.setText(transaction.getTransactionId());
    }



    @Override
    public int getItemCount() {
        // Return the size of the list
        return transactionList.size();
    }
}
