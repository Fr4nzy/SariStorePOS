package com.projectfkklp.saristorepos.activities.store_profile;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.projectfkklp.saristorepos.R;

public class StoreProfileRecycler extends RecyclerView.ViewHolder {
    TextView userNameText;
    TextView userRoleText;
    TextView gmailText;
    TextView phoneText;
    TextView idText;
    TextView userStatusText;
    Button positiveButton;
    Button negativeButton;

    public StoreProfileRecycler(@NonNull View itemView) {
        super(itemView);

        userNameText = itemView.findViewById(R.id.store_profile_user_name);
        userRoleText = itemView.findViewById(R.id.store_profile_user_role);
        gmailText = itemView.findViewById(R.id.store_profile_user_gmail);
        phoneText = itemView.findViewById(R.id.store_profile_user_phone);
        idText = itemView.findViewById(R.id.store_profile_user_id);
        userStatusText = itemView.findViewById(R.id.store_profile_user_status);
        positiveButton = itemView.findViewById(R.id.store_profile_positive_action);
        negativeButton = itemView.findViewById(R.id.store_profile_negative_action);
    }
}
