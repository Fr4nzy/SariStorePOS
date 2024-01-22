package com.projectfkklp.saristorepos.activities.transaction.transaction_history;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.projectfkklp.saristorepos.R;

public class TransactionHistoryViewHolder extends RecyclerView.ViewHolder {
    LinearLayout container;
    TextView dateTimeText;
    TextView totalSoldItemsText;
    TextView totalSalesText;
    public TransactionHistoryViewHolder(@NonNull View itemView) {
        super(itemView);

        container = itemView.findViewById(R.id.transaction_history_container);
        dateTimeText = itemView.findViewById(R.id.transaction_history_datetime);
        totalSoldItemsText = itemView.findViewById(R.id.transaction_history_sold_items);
        totalSalesText = itemView.findViewById(R.id.transaction_history_total_sales);
    }
}
