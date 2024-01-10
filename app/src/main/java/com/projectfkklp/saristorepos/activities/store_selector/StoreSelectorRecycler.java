package com.projectfkklp.saristorepos.activities.store_selector;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.projectfkklp.saristorepos.R;

class StoreSelectorRecycler extends RecyclerView.ViewHolder {
    TextView storeNameText;
    TextView storeIdText;
    TextView storeUserRole;
    TextView userStatusText;
    Button positiveButton;
    Button negativeButton;

    public StoreSelectorRecycler(@NonNull View itemView) {
        super(itemView);

        storeNameText = itemView.findViewById(R.id.store_selector_store_name);
        storeIdText = itemView.findViewById(R.id.store_selector_store_id);
        storeUserRole = itemView.findViewById(R.id.store_selector_store_role);
        userStatusText = itemView.findViewById(R.id.store_selector_store_status);
        positiveButton = itemView.findViewById(R.id.store_selector_store_positive_action);
        negativeButton = itemView.findViewById(R.id.store_selector_store_negative_action);
    }
}
