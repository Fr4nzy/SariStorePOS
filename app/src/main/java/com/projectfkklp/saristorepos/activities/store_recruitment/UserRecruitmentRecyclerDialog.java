package com.projectfkklp.saristorepos.activities.store_recruitment;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.classes.ValidationStatus;
import com.projectfkklp.saristorepos.enums.UserRole;
import com.projectfkklp.saristorepos.interfaces.OnSetFirebaseDocument;
import com.projectfkklp.saristorepos.managers.UserStoreRelationManager;
import com.projectfkklp.saristorepos.models.User;
import com.projectfkklp.saristorepos.utils.ProgressUtils;
import com.projectfkklp.saristorepos.utils.StringUtils;
import com.projectfkklp.saristorepos.utils.ToastUtils;

public class UserRecruitmentRecyclerDialog extends Dialog implements
        android.view.View.OnClickListener {

    public Activity activity;
    public Button btnRecruitAsOwner, btnRecruitAsAssistant, btnDismiss;
    private final User user;

    public UserRecruitmentRecyclerDialog(Activity a, User user) {
        super(a);

        this.activity = a;
        this.user = user;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.user_recruitment_recycler_dialog);

        initializedViews();

        btnRecruitAsOwner.setOnClickListener(this);
        btnRecruitAsAssistant.setOnClickListener(this);
        btnDismiss.setOnClickListener(this);
    }

    private void initializedViews() {
        TextView userNameText = findViewById(R.id.user_recruitment_user_name);
        TextView userPhoneNumberText = findViewById(R.id.user_recruitment_user_phone);
        TextView userGmailText = findViewById(R.id.user_recruitment_user_gmail);

        btnRecruitAsOwner = findViewById(R.id.btn_recruit_as_owner);
        btnRecruitAsAssistant = findViewById(R.id.btn_recruit_as_assistant);
        btnDismiss = findViewById(R.id.btn_user_recruitment_dialog_dismiss);

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

    @Override
    public void onClick(View v) {
        UserRole userRole = null;

        if (v.getId()==R.id.btn_recruit_as_owner) {
            userRole = UserRole.OWNER;
        }
        else if (v.getId()==R.id.btn_recruit_as_assistant) {
            userRole = UserRole.ASSISTANT;
        }
        else {
            dismiss();
        }

        if (userRole!=null){
            recruitAs(userRole);
        }
    }

    private void recruitAs(UserRole userRole){
        ProgressUtils.showDialog(activity, "Recruiting as "+userRole.label);

        UserStoreRelationManager.invite(getContext(), user, userRole, new OnSetFirebaseDocument() {
            @Override
            public void onInvalid(ValidationStatus validationStatus) {
                dismiss();
                ProgressUtils.dismissDialog();
                ToastUtils.showFormattedErrors(getContext(), validationStatus.getErrors());
            }
            @Override
            public void onSuccess(Void task) {
                ToastUtils.show(getContext(), "Request to recruit has been sent");
                UserRecruitmentPage userRecruitmentPage = (UserRecruitmentPage)activity;
                userRecruitmentPage.research();
            }
            @Override
            public void onFailed(Exception exception) {
                ToastUtils.show(getContext(), exception.getMessage());
            }
            @Override
            public void onComplete(Task<Void> task) {
                dismiss();
                ProgressUtils.dismissDialog();
            }
        });
    }
}
