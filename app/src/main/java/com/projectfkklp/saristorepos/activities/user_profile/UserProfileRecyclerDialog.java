package com.projectfkklp.saristorepos.activities.user_profile;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.enums.UserRole;
import com.projectfkklp.saristorepos.utils.ProgressUtils;
import com.projectfkklp.saristorepos.utils.TestingUtils;

import android.view.Window;

public class UserProfileRecyclerDialog extends Dialog implements
        android.view.View.OnClickListener {

    public Activity activity;
    public Button btnJoinAsOwner, btnJoinAsAssistant;

    public UserProfileRecyclerDialog(Activity a) {
        super(a);

        this.activity = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.store_finder_recycler_dialog);
        btnJoinAsOwner = findViewById(R.id.btn_join_as_owner);
        btnJoinAsAssistant = findViewById(R.id.btn_join_as_assistant);
        btnJoinAsOwner.setOnClickListener(this);
        btnJoinAsAssistant.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        UserRole userRole = null;

        if (v.getId()==R.id.btn_join_as_owner) {
            userRole = UserRole.OWNER;
        }
        else if (v.getId()==R.id.btn_join_as_assistant) {
            userRole = UserRole.ASSISTANT;
        }
        else {
            dismiss();
        }

        if (userRole!=null){
            joinAs(userRole);
        }
    }

    private void joinAs(UserRole userRole){
        ProgressUtils.showDialog(activity, "Joining as "+userRole.label);
        TestingUtils.delay(1000, ()->{
            dismiss();
            ProgressUtils.dismissDialog();
        });
    }
}
