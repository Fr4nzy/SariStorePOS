package com.projectfkklp.saristorepos.activities.store_profile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.enums.UserRole;
import com.projectfkklp.saristorepos.enums.UserStatus;
import com.projectfkklp.saristorepos.managers.UserStoreRelationManager;
import com.projectfkklp.saristorepos.models.User;
import com.projectfkklp.saristorepos.models.UserStoreRelation;
import com.projectfkklp.saristorepos.utils.ActivityUtils;
import com.projectfkklp.saristorepos.utils.ProgressUtils;
import com.projectfkklp.saristorepos.utils.StringUtils;
import com.projectfkklp.saristorepos.utils.ToastUtils;

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

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull StoreProfileRecycler holder, int position) {
        UserStoreRelation userStoreRelation = userStoreRelations.get(position);
        Optional<User> optionalStore = users.stream()
            .filter(s -> Objects.equals(s.getId(), userStoreRelation.getUserId()))
            .findFirst();
        User user = optionalStore.orElse(null);
        UserStatus status = userStoreRelation.getStatus();

        assert user != null;
        holder.userNameText.setText(user.getName());
        holder.userRoleText.setText(userStoreRelation.getRole().label);
        if (StringUtils.isNullOrEmpty(user.getGmail())){
            holder.gmailText.setVisibility(View.GONE);
        }
        else {
            holder.gmailText.setText(user.getGmail());
        }
        if (StringUtils.isNullOrEmpty(user.getPhoneNumber())){
            holder.phoneText.setVisibility(View.GONE);
        }
        else {
            holder.phoneText.setText(user.getPhoneNumber());
        }
        holder.idText.setText(user.getId());
        holder.userStatusText.setText(userStoreRelation.getStatus().label);

        // Actions Buttons
        if (status.storePositiveAction==null){
            holder.positiveButton.setVisibility(View.GONE);
        }
        else {
            holder.positiveButton.setText(status.storePositiveAction);
            holder.positiveButton.setOnClickListener(v->{
                if (status.equals(UserStatus.ACTIVE)) {
                    showChangeRoleDialog(userStoreRelation, user);
                }
                else if (status.equals(UserStatus.REQUESTED)){
                    ProgressUtils.showDialog(context, "Accepting");
                    UserStoreRelationManager
                        .accept(userStoreRelation)
                        .addOnSuccessListener(task-> notifyDataSetChanged())
                        .addOnFailureListener(failedTask-> ToastUtils.show(context, failedTask.getMessage()))
                        .addOnCompleteListener(task->ProgressUtils.dismissDialog());
                }
            });
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
                ProgressUtils.showDialog(context, dialogTitle);
                UserStoreRelationManager
                    .delete(userStoreRelation.getId())
                    .addOnSuccessListener(task-> ActivityUtils.navigateTo((Activity)context, StoreProfilePage.class))
                    .addOnFailureListener(failedTask-> ToastUtils.show(context, failedTask.getMessage()))
                    .addOnCompleteListener(task->ProgressUtils.dismissDialog())
                ;
            })
            .setNegativeButton("No", (dialog, which) -> {
                dialog.dismiss();
            })
            .show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void showChangeRoleDialog(UserStoreRelation userStoreRelation, User user) {
        UserRole newRole = userStoreRelation.getRole() == UserRole.OWNER ? UserRole.ASSISTANT : UserRole.OWNER;
        String dialogTitle = "Changing Role...";
        String dialogMessage = String.format(
            "Change role of %s to %s?",
            user.getName(),
            newRole.label
        );
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder
            .setTitle(dialogTitle)
            .setMessage(dialogMessage)
            .setPositiveButton("Yes", (dialog, which) -> {
                ProgressUtils.showDialog(context,"Changing Role");
                userStoreRelation.setRole(newRole);
                UserStoreRelationManager
                    .saveRelation(userStoreRelation)
                    .addOnSuccessListener(t-> notifyDataSetChanged())
                    .addOnFailureListener(failedTask-> ToastUtils.show(context, failedTask.getMessage()))
                    .addOnCompleteListener(task->ProgressUtils.dismissDialog())
                ;
            })
            .setNegativeButton("No", (dialog, which) -> {
                dialog.dismiss();
            })
            .show();
    }


    @Override
    public int getItemCount() {
        return userStoreRelations.size();
    }

}
