package com.projectfkklp.saristorepos.activities.store_profile;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.enums.UserStatus;
import com.projectfkklp.saristorepos.models.User;
import com.projectfkklp.saristorepos.models.UserStoreRelation;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class StoreProfileAdapter extends RecyclerView.Adapter<StoreProfileRecycler> {
    private final Context context;
    private final List<UserStoreRelation> userStoreRelations;
    private final List<User> users;

    public StoreProfileAdapter(Context context, List<UserStoreRelation> userStoreRelations, List<User> users) {
        this.context = context;
        this.userStoreRelations = userStoreRelations;
        this.users = users;
    }

    @NonNull
    @Override
    public StoreProfileRecycler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_profile_recycler_view, parent, false);
        return new StoreProfileRecycler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreProfileRecycler holder, int position) {
        UserStoreRelation userStoreRelation = userStoreRelations.get(position);
        Optional<User> optionalStore = users.stream()
                .filter(s -> Objects.equals(s.getId(), userStoreRelation.getUserId()))
                .findFirst();
        User user = optionalStore.orElse(null);
        UserStatus status = userStoreRelation.getStatus();

        holder.userNameText.setText(user.getName());
        holder.userRoleText.setText(userStoreRelation.getRole().label);
        holder.gmailText.setText(user.getGmail());
        holder.phoneText.setText(user.getPhoneNumber());
        holder.idText.setText(user.getId());
        holder.userStatusText.setText(userStoreRelation.getStatus().label);

        // Actions Buttons
        if (status.storePositiveAction==null){
            holder.positiveButton.setVisibility(View.GONE);
        }
        else {
            holder.positiveButton.setText(status.storePositiveAction);
        }

        if (status.storeNegativeAction==null){
            holder.negativeButton.setVisibility(View.GONE);
        }
        else {
            holder.negativeButton.setText(status.storeNegativeAction);
            holder.negativeButton.setOnClickListener(v->
                    showConfirmationDialog(userStoreRelation, user)
            );
        }
    }

    private void showConfirmationDialog(UserStoreRelation userStoreRelation, User user) {
        UserStatus userStatus = userStoreRelation.getStatus();
        String dialogTitle = String.format(userStatus.storeConfirmationTitle, user.getName());
        String dialogMessage = String.format(userStatus.storeConfirmationMessage, user.getName());
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder
                .setTitle(dialogTitle)
                .setMessage(dialogMessage)
                .setPositiveButton("Yes", (dialog, which) -> {
                    // If confirmed to (Leave, Decline Invitation, Cancel Request)
                    // Then delete the userStoreRelation in firebase
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
