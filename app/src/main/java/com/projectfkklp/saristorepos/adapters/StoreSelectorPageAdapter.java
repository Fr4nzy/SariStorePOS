package com.projectfkklp.saristorepos.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.enums.UserStatus;
import com.projectfkklp.saristorepos.models.Store;
import com.projectfkklp.saristorepos.models.UserStoreRelation;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class StoreSelectorPageAdapter extends RecyclerView.Adapter<StoreSelectorPageAdapter.StoreSelectorViewHolder> {

    private Context context;
    private List<UserStoreRelation> userStoreRelations;
    private List<Store> stores;

    public StoreSelectorPageAdapter(Context context, List<UserStoreRelation> userStoreRelations, List<Store> stores) {
        this.context = context;
        this.userStoreRelations = userStoreRelations;
        this.stores = stores;
    }

    @NonNull
    @Override
    public StoreSelectorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_selector_recycler_view, parent, false);
        return new StoreSelectorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreSelectorViewHolder holder, int position) {
        UserStoreRelation userStoreRelation = userStoreRelations.get(position);
        Optional<Store> optionalStore = stores.stream()
                .filter(s -> Objects.equals(s.getId(), userStoreRelation.getStoreId()))
                .findFirst();
        Store store = optionalStore.orElse(null);
        UserStatus status = userStoreRelation.getStatus();

        assert store != null;
        holder.storeNameText.setText(store.getName());
        holder.storeUserRole.setText(userStoreRelation.getRole().label);
        holder.storeIdText.setText(String.format("@%s", store.getId()));

        // Status Text
        if (status==UserStatus.ACTIVE){
            holder.userStatusText.setVisibility(View.GONE);
        }
        else {
            holder.userStatusText.setText(status.label);
        }

        // Actions Buttons
        if (status.positiveAction==null){
            holder.positiveButton.setVisibility(View.GONE);
        }
        else {
            holder.positiveButton.setText(status.positiveAction);
        }
        if (status.negativeAction==null){
            holder.negativeButton.setText(status.negativeAction);
        }
        else {
            holder.negativeButton.setText(status.negativeAction);
            holder.negativeButton.setOnClickListener(v->{
                showConfirmationDialog(userStoreRelation, store);
            });
        }


    }

    private void showConfirmationDialog(UserStoreRelation userStoreRelation, Store store) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder
                .setTitle(String.format(userStoreRelation.getStatus().confirmationTitle, store.getName()))
                .setMessage(userStoreRelation.getStatus().confirmationMessage)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle positive button click
                        // Perform the action here
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle negative button click or simply dismiss the dialog
                        dialog.dismiss();
                    }
                })
                .show();
    }


    @Override
    public int getItemCount() {
        return userStoreRelations.size();
    }

    static class StoreSelectorViewHolder extends RecyclerView.ViewHolder {
        TextView storeNameText;
        TextView storeIdText;
        TextView storeUserRole;
        TextView userStatusText;
        Button positiveButton;
        Button negativeButton;

        public StoreSelectorViewHolder(@NonNull View itemView) {
            super(itemView);

            storeNameText = itemView.findViewById(R.id.store_selector_store_name);
            storeIdText = itemView.findViewById(R.id.store_selector_store_id);
            storeUserRole = itemView.findViewById(R.id.store_selector_store_role);
            userStatusText = itemView.findViewById(R.id.store_selector_store_status);
            positiveButton = itemView.findViewById(R.id.store_selector_store_positive_action);
            negativeButton = itemView.findViewById(R.id.store_selector_store_negative_action);
        }
    }
}
