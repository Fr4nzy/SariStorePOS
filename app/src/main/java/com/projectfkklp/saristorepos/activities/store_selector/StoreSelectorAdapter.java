package com.projectfkklp.saristorepos.activities.store_selector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.activities.dashboard.DashboardPage;
import com.projectfkklp.saristorepos.enums.UserStatus;
import com.projectfkklp.saristorepos.managers.SessionManager;
import com.projectfkklp.saristorepos.managers.UserStoreRelationManager;
import com.projectfkklp.saristorepos.models.Store;
import com.projectfkklp.saristorepos.models.UserStoreRelation;
import com.projectfkklp.saristorepos.utils.ActivityUtils;
import com.projectfkklp.saristorepos.utils.ProgressUtils;
import com.projectfkklp.saristorepos.utils.ToastUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class StoreSelectorAdapter extends RecyclerView.Adapter<StoreSelectorRecycler> {

    private final Context context;
    private final List<UserStoreRelation> userStoreRelations;
    private final List<Store> stores;

    public StoreSelectorAdapter(Context context, List<UserStoreRelation> userStoreRelations, List<Store> stores) {
        this.context = context;
        this.userStoreRelations = userStoreRelations;
        this.stores = stores;
    }

    @NonNull
    @Override
    public StoreSelectorRecycler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_selector_recycler_view, parent, false);
        return new StoreSelectorRecycler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreSelectorRecycler holder, int position) {
        UserStoreRelation userStoreRelation = userStoreRelations.get(position);
        Optional<Store> optionalStore = stores.stream()
            .filter(s -> Objects.equals(s.getId(), userStoreRelation.getStoreId()))
            .findFirst();
        Store store = optionalStore.orElse(null);
        UserStatus status = userStoreRelation.getStatus();

        assert store != null;
        holder.storeNameText.setText(store.getName());
        holder.storeAddressText.setText(store.getAddress());
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
            holder.positiveButton.setOnClickListener(v->{
                if (status.equals(UserStatus.ACTIVE)) {
                    SessionManager.setStore(context, store);
                    SessionManager.setUserRole(context, userStoreRelation.getRole());
                    context.startActivity(new Intent(context, DashboardPage.class));
                }
                else if (status.equals(UserStatus.INVITED)){
                    ProgressUtils.showDialog(context, "Accepting");
                    UserStoreRelationManager
                        .accept(userStoreRelation)
                        .addOnSuccessListener(task->ActivityUtils.navigateTo((Activity)context, StoreSelectorPage.class))
                        .addOnFailureListener(failedTask-> ToastUtils.show(context, failedTask.getMessage()))
                        .addOnCompleteListener(task->ProgressUtils.dismissDialog());
                }
            });
        }
        if (status.negativeAction==null){
            holder.negativeButton.setVisibility(View.GONE);
        }
        else {
            holder.negativeButton.setText(status.negativeAction);
            holder.negativeButton.setOnClickListener(v->
                showConfirmationDialog(userStoreRelation, store)
            );
        }
    }

    private void showConfirmationDialog(UserStoreRelation userStoreRelation, Store store) {
        UserStatus userStatus = userStoreRelation.getStatus();
        String dialogTitle = String.format(userStatus.confirmationTitle, store.getName());
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder
            .setTitle(dialogTitle)
            .setMessage(userStatus.confirmationMessage)
            .setPositiveButton("Yes", (dialog, which) -> {
                // If confirmed to (Leave, Decline Invitation, Cancel Request)
                // Then delete the userStoreRelation in firebase
                ProgressUtils.showDialog(context, dialogTitle);
                UserStoreRelationManager
                    .delete(userStoreRelation.getId())
                    .addOnSuccessListener(task->ActivityUtils.navigateTo((Activity)context, StoreSelectorPage.class))
                    .addOnFailureListener(failedTask-> ToastUtils.show(context, failedTask.getMessage()))
                    .addOnCompleteListener(task->ProgressUtils.dismissDialog())
                ;
            })
            .setNegativeButton("No", (dialog, which) -> {
                // Close confirmation dialog (produced from Leave, Decline, and Cancel buttons)
                dialog.dismiss();
            })
            .show();
    }


    @Override
    public int getItemCount() {
        return userStoreRelations.size();
    }

}
