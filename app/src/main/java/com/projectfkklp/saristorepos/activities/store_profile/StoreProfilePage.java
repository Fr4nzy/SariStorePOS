package com.projectfkklp.saristorepos.activities.store_profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.enums.UserRole;
import com.projectfkklp.saristorepos.enums.UserStatus;
import com.projectfkklp.saristorepos.models.User;
import com.projectfkklp.saristorepos.models.UserStoreRelation;
import com.projectfkklp.saristorepos.utils.TestingUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class StoreProfilePage extends AppCompatActivity {
    AlertDialog.Builder cancelConfirmationDialog;
    private boolean isEditing = false;
    private EditText storeNameEditText;
    private TextView storeAddressTextView;
    private ImageView toggleModeImageView;
    private ImageView saveButton;
    RecyclerView storeProfileRecycler;
    ProgressBar progressBar;
    StoreProfileAdapter storeProfilePageAdapter;
    private List<UserStoreRelation> userStoreRelations;
    private List<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_profile_page);

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

    private void loadAssociatedUsers(){
        // Fetch Associated Stores from Firebase
        // Then, update recycler view
        progressBar.setVisibility(View.VISIBLE);
        TestingUtils.delay(1000, ()->{
            setDummyData();
            sortUserStoreRelations();
            initializeRecyclerView();
            progressBar.setVisibility(View.GONE);
        });
    }

    private void setDummyData(){
        userStoreRelations = new ArrayList<>(Arrays.asList(
            new UserStoreRelation(
                "001",
                "abcd001",
                "",
                UserRole.OWNER,
                UserStatus.ACTIVE
            ),
            new UserStoreRelation(
                "003",
                "abcd003",
                "",
                UserRole.OWNER,
                UserStatus.REQUESTED
            ),
            new UserStoreRelation(
                "002",
                "abcd002",
                "",
                UserRole.ASSISTANT,
                UserStatus.ACTIVE
            ),
            new UserStoreRelation(
                "004",
                "abcd004",
                "",
                UserRole.ASSISTANT,
                UserStatus.REQUESTED
            ),
            new UserStoreRelation(
                "005",
                "abcd005",
                "",
                UserRole.OWNER,
                UserStatus.INVITED
            ),
            new UserStoreRelation(
                "006",
                "abcd006",
                "",
                UserRole.ASSISTANT,
                UserStatus.INVITED
            )
        ));
        users = new ArrayList<>(Arrays.asList(
            new User(
                "abcd001",
                "User 1",
                "phone001",
                "09672410291",
                "gmail01",
                "user1@gmail.com"
            ),
            new User(
                "abcd002",
                "User 2",
                "phone002",
                "09672410292",
                "gmail02",
                "user2@gmail.com"
            ),
            new User(
                "abcd003",
                "User 3",
                "phone003",
                "09672410293",
                "gmail03",
                "user3@gmail.com"
            ),
            new User(
                "abcd004",
                "User 4",
                "phone004",
                "09672410291",
                "gmail04",
                "user4@gmail.com"
            ),
            new User(
                "abcd005",
                "User 5",
                "phone005",
                "09672410295",
                "gmail05",
                "user5@gmail.com"
            ),
            new User(
                "abcd006",
                "User 6",
                "phone006",
                "09672410296",
                "gmail06",
                "user6@gmail.com"
            )
        ));
    }

    private void initializeViews(){
        // Initialize your views
        storeNameEditText = findViewById(R.id.store_profile_name);
        storeAddressTextView = findViewById(R.id.store_profile_address);
        toggleModeImageView = findViewById(R.id.store_profile_toggle_mode);
        saveButton = findViewById(R.id.store_profile_save_button);
        storeProfileRecycler = findViewById(R.id.store_profile_recycler);
        progressBar = findViewById(R.id.store_profile_page_progress);

        // Set OnClickListener for the toggle mode ImageView
        toggleModeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMode();
            }
        });

        // Set OnClickListener for the save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });
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

        // TODO for backend: put condition here to check if hasEdits before showing cancel dialog
        {
            showCancelConfirmationDialog();
        }
    }

    private void enableEditing(){
        isEditing = true;
        toggleModeImageView.setImageResource(R.drawable.baseline_cancel_24);
        saveButton.setVisibility(View.VISIBLE);
        storeNameEditText.setEnabled(true);
        storeAddressTextView.setEnabled(true);
    }

    private void disableEditing(){
        isEditing=false;
        toggleModeImageView.setImageResource(R.drawable.edit);
        saveButton.setVisibility(View.GONE);
        storeNameEditText.setEnabled(false);
        storeAddressTextView.setEnabled(true);
    }

    private void showCancelConfirmationDialog(){
        cancelConfirmationDialog.show();
    }

    private void saveChanges() {
        // Implement the logic to save the changes here

        // After saving, disable editing and hide the save button
        storeNameEditText.setEnabled(false);
        storeAddressTextView.setEnabled(false);
        toggleModeImageView.setImageResource(R.drawable.edit);
        saveButton.setVisibility(View.GONE);

        isEditing = false;
    }

    public void dismiss(View view) {
        finish();
    }
}
