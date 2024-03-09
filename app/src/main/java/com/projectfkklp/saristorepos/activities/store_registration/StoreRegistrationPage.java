package com.projectfkklp.saristorepos.activities.store_registration;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.activities.store_selector.StoreSelectorPage;
import com.projectfkklp.saristorepos.enums.UserRole;
import com.projectfkklp.saristorepos.enums.UserStatus;
import com.projectfkklp.saristorepos.managers.StoreManager;
import com.projectfkklp.saristorepos.managers.UserStoreRelationManager;
import com.projectfkklp.saristorepos.models.Store;
import com.projectfkklp.saristorepos.models.UserStoreRelation;
import com.projectfkklp.saristorepos.repositories.SessionRepository;
import com.projectfkklp.saristorepos.utils.ActivityUtils;
import com.projectfkklp.saristorepos.utils.ModelUtils;
import com.projectfkklp.saristorepos.utils.ProgressUtils;

import java.util.HashMap;

public class StoreRegistrationPage extends AppCompatActivity {

    private Store store;
    EditText storeNameText;
    EditText addressText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_registration_page);

        initializeViews();
    }

    private void initializeViews(){
        storeNameText = findViewById(R.id.store_registration_name);
        addressText = findViewById(R.id.store_registration_address);
    }

    public void registerStore(View view) {
        store = new Store(
            ModelUtils.createUUID(),
            storeNameText.getText().toString(),
            addressText.getText().toString()
        );
        ProgressUtils.showDialog(this, "Registering...");
        StoreManager.saveStore(store, (registrationUser, validationStatus, task) -> {
            if (!validationStatus.isValid()) {
                HashMap<String, String> errors = validationStatus.getErrors();
                if (errors.get("name") != null) {
                    storeNameText.setError(errors.get("name"));
                }
                if (errors.get("address") != null) {
                    addressText.setError(errors.get("address"));
                }

                ProgressUtils.dismissDialog();
                return;
            }
            task
                .continueWithTask(successTask->{
                    UserStoreRelation relation = new UserStoreRelation(
                        ModelUtils.createUUID(),
                        SessionRepository.getCurrentUser(this).getId(),
                        store.getId(),
                        UserRole.OWNER,
                        UserStatus.ACTIVE
                    );
                    return UserStoreRelationManager.save(relation);
                })
                .addOnSuccessListener(relationTask->{
                    Toast.makeText(this, "Registration success", Toast.LENGTH_SHORT).show();
                    ActivityUtils.navigateTo(this, StoreSelectorPage.class);
                })
                .addOnFailureListener(failedTask-> Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show())
                .addOnCompleteListener(completeTask-> ProgressUtils.dismissDialog())
            ;
        });
    }

    public void navigateBack(View view){
        finish();
    }

}
