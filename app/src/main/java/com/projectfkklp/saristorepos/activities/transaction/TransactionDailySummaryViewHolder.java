package com.projectfkklp.saristorepos.activities.transaction;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.projectfkklp.saristorepos.R;


public class TransactionDailySummaryViewHolder extends RecyclerView.ViewHolder {
    TextView summaryDateText;
    TextView totalSoldItemsText;
    TextView totalSalesText;
    public TransactionDailySummaryViewHolder(@NonNull View itemView) {
        super(itemView);

        summaryDateText = itemView.findViewById(R.id.transaction_daily_summary_date);
        totalSoldItemsText = itemView.findViewById(R.id.transaction_daily_summary_sold_items);
        totalSalesText = itemView.findViewById(R.id.transaction_daily_summary_total_sales);;
    }
}
