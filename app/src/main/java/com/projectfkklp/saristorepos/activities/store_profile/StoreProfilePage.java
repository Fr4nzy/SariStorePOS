package com.projectfkklp.saristorepos.activities.store_profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.managers.SessionManager;
import com.projectfkklp.saristorepos.managers.StoreManager;
import com.projectfkklp.saristorepos.models.Store;
import com.projectfkklp.saristorepos.models.User;
import com.projectfkklp.saristorepos.models.UserStoreRelation;
import com.projectfkklp.saristorepos.repositories.SessionRepository;
import com.projectfkklp.saristorepos.repositories.StoreRepository;
import com.projectfkklp.saristorepos.repositories.UserRepository;
import com.projectfkklp.saristorepos.repositories.UserStoreRelationRepository;
import com.projectfkklp.saristorepos.utils.ActivityUtils;
import com.projectfkklp.saristorepos.utils.ProgressUtils;
import com.projectfkklp.saristorepos.utils.ToastUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class StoreProfilePage extends AppCompatActivity {
    AlertDialog.Builder cancelConfirmationDialog;
    private EditText storeNameEditText;
    private EditText storeAddressEditText;
    private ImageView toggleModeImageView;
    private ImageButton saveButton;
    RecyclerView storeProfileRecycler;
    ProgressBar progressBar;
    FrameLayout emptyFrame;
    StoreProfileAdapter storeProfilePageAdapter;

    private boolean isEditing = false;
    private Store store;
    private List<UserStoreRelation> userStoreRelations;
    private List<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_profile_page);

        loadStoreFromSession();
        initializeViews();
        initializeDialogs();
    }

    @Override
    protected void onStart() {
        // we use OnStart lice cycle
        // to reload (re-fetch from firebase) the associated stores
        // specially, when we pause this activity
        // e.g. we go to StoreFinderPage, and get back to this page
        // So the changes made can reflect here
        super.onStart();

        loadAssociatedUsers();
    }

    private void loadStoreFromSession(){
        store = SessionRepository.getCurrentStore(this);
    }

    private void loadAssociatedUsers(){
        // Fetch Associated Users from Firebase
        // Then, update recycler view
        progressBar.setVisibility(View.VISIBLE);
        UserStoreRelationRepository
            .getRelationsByStoreId(store.getId())
            .continueWithTask(task->{
                // Fetch userStoreRelations from the task
                userStoreRelations = task.getResult().toObjects(UserStoreRelation.class);
                sortUserStoreRelations();

                // extract user ids
                List<String> userIds = userStoreRelations.stream()
                    .map(UserStoreRelation::getUserId)
                    .collect(Collectors.toList());

                // continue by fetching associated users using extracted store ids
                return UserRepository.getUsersByIds(userIds);
            })
            .addOnSuccessListener(successTask->{
                users = successTask != null
                        ? successTask.toObjects(User.class)
                        : new ArrayList<>();
                initializeRecyclerView();
                emptyFrame.setVisibility(users.isEmpty()? View.VISIBLE: View.GONE);
            })
            .addOnFailureListener(failedTask-> ToastUtils.show(this, failedTask.getMessage()))
            .addOnCompleteListener(task-> progressBar.setVisibility(View.GONE))
        ;
    }

    private void initializeViews(){
        // Initialize your views
        {
            toggleModeImageView = findViewById(R.id.store_profile_toggle_mode);
            storeNameEditText = findViewById(R.id.store_profile_name);
            storeAddressEditText = findViewById(R.id.store_profile_address);
            saveButton = findViewById(R.id.store_profile_save_button);
            storeProfileRecycler = findViewById(R.id.store_profile_recycler);
            progressBar = findViewById(R.id.store_profile_page_progress);
            emptyFrame = findViewById(R.id.store_profile_empty_frame);
        }

        TextWatcher textWatcher  =new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                saveButton.setVisibility(hasEdits() ? View.VISIBLE: View.GONE);
            }
        };

        // Toggle Mode Button
        toggleModeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMode();
            }
        });

        // Store Name Edit Text
        {
            storeNameEditText.setText(store.getName());
            storeNameEditText.addTextChangedListener(textWatcher);
        }

        // Store Address Edit Text
        {
            storeAddressEditText.setText(store.getAddress());
            storeAddressEditText.addTextChangedListener(textWatcher);
        }
    }

    private boolean hasEdits(){
        return (
            !storeNameEditText.getText().toString().equals(store.getName())
            || !storeAddressEditText.getText().toString().equals(store.getAddress())
        );
    }

    private void sortUserStoreRelations(){
        userStoreRelations.sort(Comparator.comparing(userStoreRelation -> userStoreRelation.getStatus().orderInStoreProfile));
    }

    private void initializeRecyclerView(){
        // If no stores to display,
        // Then display empty fragment frame
        // And no need to setup recycler view
        // Set up RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        storeProfileRecycler.setLayoutManager(layoutManager);

        // Set up adapter
        storeProfilePageAdapter = new StoreProfileAdapter(this, userStoreRelations, users);
        storeProfileRecycler.setAdapter(storeProfilePageAdapter);
    }

    private void initializeDialogs(){
        cancelConfirmationDialog = new AlertDialog.Builder(this)
            .setTitle("Cancel Edit ?")
            .setMessage("You have unsaved changes, do you want to cancel edit?")
            .setPositiveButton("Yes", (dialog, which) -> disableEditing())
            .setNegativeButton("No", (dialog, which) -> dialog.dismiss());
    }

    private void toggleMode() {
        if (!isEditing) {
            // If not editing, regardless of state, switch to edit mode
            enableEditing();
            return;
        }

        if (hasEdits()){
            showCancelConfirmationDialog();
            return;
        }

        disableEditing();
    }

    private void enableEditing(){
        isEditing = true;
        toggleModeImageView.setImageResource(R.drawable.baseline_cancel_24);
        storeNameEditText.setEnabled(true);
        storeAddressEditText.setEnabled(true);
    }

    private void disableEditing(){
        isEditing=false;
        toggleModeImageView.setImageResource(R.drawable.edit);
        saveButton.setVisibility(View.GONE);
        storeNameEditText.setEnabled(false);
        storeAddressEditText.setEnabled(true);

        // Reset Fields
        storeNameEditText.setText(store.getName());
        storeAddressEditText.setText(store.getAddress());
    }

    private void showCancelConfirmationDialog(){
        cancelConfirmationDialog.show();
    }

    public void saveChanges(View view) {
        ProgressUtils.showDialog(this, "Updating...");
        StoreRepository
            .getStoreById(store.getId())
            .addOnSuccessListener(repositoryTask->{
                Store updatedStore = repositoryTask.toObject(Store.class);
                assert updatedStore != null;
                updatedStore.setName(storeNameEditText.getText().toString());
                updatedStore.setAddress(storeAddressEditText.getText().toString());

                StoreManager.saveStore(updatedStore, (updateStore, validationStatus, task)->{
                    if (!validationStatus.isValid()) {
                        HashMap<String, String> errors = validationStatus.getErrors();

                        if (errors.get("name") != null) {
                            storeNameEditText.setError(errors.get("name"));
                        }

                        if (errors.get("address") != null) {
                            storeNameEditText.setError(errors.get("address"));
                        }

                        ProgressUtils.dismissDialog();
                        return;
                    }

                    task
                        .addOnSuccessListener(successTask->{
                            SessionManager.setStore(this, updatedStore);
                            Toast.makeText(this, "Updating success", Toast.LENGTH_SHORT).show();
                            ActivityUtils.navigateTo(this, StoreProfilePage.class);
                        })
                        .addOnFailureListener(failedTask->
                            Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show()
                        );
                });
            })
            .addOnFailureListener(failedTask->
                Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show()
            )
            .addOnCompleteListener(completeTask-> ProgressUtils.dismissDialog());
    }

    public void dismiss(View view) {
        finish();
    }
}
