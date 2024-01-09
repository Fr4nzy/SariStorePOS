package com.projectfkklp.saristorepos.activities.store_finder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.projectfkklp.saristorepos.R;

class StoreFinderRecycler extends RecyclerView.ViewHolder {
    CardView cardView;

    TextView storeNameText;
    TextView storeAddressText;
    TextView storeIdText;

    public StoreFinderRecycler(@NonNull View itemView) {
        super(itemView);

        cardView = itemView.findViewById(R.id.store_finder_store_card);
        storeNameText = itemView.findViewById(R.id.store_finder_store_name);
        storeAddressText = itemView.findViewById(R.id.store_finder_store_address);
        storeIdText = itemView.findViewById(R.id.store_finder_store_id);
    }
}
