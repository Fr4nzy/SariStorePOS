package com.projectfkklp.saristorepos.activities.store_recruitment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.models.User;

import java.util.List;

public class UserRecruitmentAdapter extends RecyclerView.Adapter<UserRecruitmentRecycler> {

    private Context context;
    private List<User> searchedUsers;

    public UserRecruitmentAdapter(Context context, List<User> users) {
        this.context = context;
        this.searchedUsers = users;
    }

    @NonNull
    @Override
    public UserRecruitmentRecycler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_recruitment_recycler_view, parent, false);
        return new UserRecruitmentRecycler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserRecruitmentRecycler holder, int position) {
        User searchedUser = searchedUsers.get(position);

        holder.userVisibility(searchedUser);

        holder.userNameText.setText(searchedUser.getName());
        holder.userPhoneNumberText.setText(searchedUser.getPhoneNumber());
        holder.userGmailText.setText(searchedUser.getGmail());

        holder.cardView.setOnClickListener(l->{
            UserRecruitmentRecyclerDialog cdd = new UserRecruitmentRecyclerDialog((Activity) context, searchedUser);
            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            cdd.show();
        });
    }

    @Override
    public int getItemCount() {return searchedUsers.size();}
}
