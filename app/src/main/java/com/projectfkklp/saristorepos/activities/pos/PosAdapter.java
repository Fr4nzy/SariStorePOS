package com.projectfkklp.saristorepos.activities.pos;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.models.TransactionItem;

import java.util.List;

public class PosAdapter extends RecyclerView.Adapter<PosViewHolder>{
    private final Context context;
    private final List<TransactionItem> transactionItems;

    public PosAdapter(Context context, List<TransactionItem> transactionItems) {
        this.context = context;
        this.transactionItems = transactionItems;
    }

    @NonNull
    @Override
    public PosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pos_recycler_view, parent, false);
        return new PosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PosViewHolder holder, int position) {
        TransactionItem transactionItem = transactionItems.get(position);

    }

    @Override
    public int getItemCount() {
        return transactionItems.size();
    }

}
