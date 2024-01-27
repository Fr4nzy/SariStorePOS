package com.projectfkklp.saristorepos.activities.store_recruitment;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.models.User;
import com.projectfkklp.saristorepos.utils.StringUtils;

public class UserRecruitmentRecycler extends RecyclerView.ViewHolder {
    CardView cardView;

    TextView userNameText;
    TextView userPhoneNumberText;
    TextView userGmailText;

    public UserRecruitmentRecycler(@NonNull View itemView) {
        super(itemView);

        cardView = itemView.findViewById(R.id.user_finder_store_card);
        userNameText = itemView.findViewById(R.id.user_finder_user_name);
        userPhoneNumberText = itemView.findViewById(R.id.user_recruit_user_phone);
        userGmailText = itemView.findViewById(R.id.user_recruit_user_gmail);
    }

    public void userVisibility(User user) {
        userNameText.setText(user.getName());
        setVisibilityAndText(userPhoneNumberText, user.getPhoneNumber());
        setVisibilityAndText(userGmailText, user.getGmail());
    }

    private void setVisibilityAndText(TextView textView, String text) {
        if (!StringUtils.isNullOrEmpty(text)) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(text);
        } else {
            textView.setVisibility(View.GONE);
        }
    }

}
