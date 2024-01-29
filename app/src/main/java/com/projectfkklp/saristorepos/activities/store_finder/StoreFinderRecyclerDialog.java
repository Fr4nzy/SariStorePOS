package com.projectfkklp.saristorepos.activities.store_finder;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.Task;
import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.classes.ValidationStatus;
import com.projectfkklp.saristorepos.enums.UserRole;
import com.projectfkklp.saristorepos.interfaces.OnSetFirebaseDocument;
import com.projectfkklp.saristorepos.managers.UserStoreRelationManager;
import com.projectfkklp.saristorepos.models.Store;
import com.projectfkklp.saristorepos.utils.ProgressUtils;
import com.projectfkklp.saristorepos.utils.StringUtils;
import com.projectfkklp.saristorepos.utils.ToastUtils;

import android.view.Window;
import android.widget.TextView;

public class StoreFinderRecyclerDialog extends Dialog implements
        android.view.View.OnClickListener {

    public Activity activity;
    public Button btnJoinAsOwner, btnJoinAsAssistant, btnDismiss;
    private final Store store;

    public StoreFinderRecyclerDialog(Activity a, Store store) {
        super(a);

        this.activity = a;
        this.store = store;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.store_finder_recycler_dialog);

        initializedViews();

        btnJoinAsOwner.setOnClickListener(this);
        btnJoinAsAssistant.setOnClickListener(this);
        btnDismiss.setOnClickListener(this);
    }

    private void initializedViews() {
        TextView storeNameText = findViewById(R.id.store_finder_recycler_store_name);
        TextView storeAddressText = findViewById(R.id.store_finder_recycler_store_address);
        TextView storeIdText = findViewById(R.id.store_finder_recycler_store_id);

        btnJoinAsOwner = findViewById(R.id.btn_join_as_owner);
        btnJoinAsAssistant = findViewById(R.id.btn_join_as_assistant);
        btnDismiss = findViewById(R.id.btn_store_finder_dialog_dismiss);

        storeNameText.setText(store.getName());
        storeAddressText.setText(store.getAddress());
        storeIdText.setText(store.getId());
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

        UserStoreRelationManager.request(getContext(), store, userRole, new OnSetFirebaseDocument() {
            @Override
            public void onInvalid(ValidationStatus validationStatus) {
                dismiss();
                ProgressUtils.dismissDialog();
                ToastUtils.showFormattedErrors(getContext(), validationStatus.getErrors());
            }
            @Override
            public void onSuccess(Void task) {
                ToastUtils.show(getContext(), "Request to join has been sent");
                StoreFinderPage storeFinderPage = (StoreFinderPage)activity;
                storeFinderPage.research();
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
