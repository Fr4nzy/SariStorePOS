package com.projectfkklp.saristorepos.activities.transaction.transaction_daily_summary;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.models.DailySalesSummary;
import com.projectfkklp.saristorepos.utils.StringUtils;

import java.util.List;

public class _TransactionDailySummaryAdapter extends RecyclerView.Adapter<_TransactionDailySummaryAdapter.BreakDownViewHolder> {
    private final List<DailySalesSummary> summaryList;

    public _TransactionDailySummaryAdapter(List<DailySalesSummary> summaryList) {
        this.summaryList = summaryList;
    }

    public static class BreakDownViewHolder extends RecyclerView.ViewHolder {
        public BreakDownViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    @NonNull
    @Override
    public _TransactionDailySummaryAdapter.BreakDownViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout._transaction_daily_summary_recycler_view, parent, false);
        return new _TransactionDailySummaryAdapter.BreakDownViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull _TransactionDailySummaryAdapter.BreakDownViewHolder holder, int position) {
        // Bind data to each item
        DailySalesSummary summary = summaryList.get(position);

        TextView summaryTextView = holder.itemView.findViewById(R.id.totalsummary);

        String summaryContent = summary.getContent();

        // Set the dynamic text to the TextView
        summaryTextView.setText(summaryContent);

        // Calculate the number of lines in the dynamic text
        int numberOfLines = StringUtils.getLinesCount(summaryContent);

        // Set the number of lines programmatically
        summaryTextView.setLines(numberOfLines);
    }

    @Override
    public int getItemCount() {
        // Return the size of the list
        return summaryList.size();
    }

}