package com.projectfkklp.saristorepos.activities.store_profile;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.projectfkklp.saristorepos.R;

public class StoreProfilePage extends AppCompatActivity {
    AlertDialog.Builder cancelConfirmationDialog;
    private boolean isEditing = false;
    private EditText storeNameEditText;
    private TextView storeAddressTextView;
    private ImageView toggleModeImageView;
    private ImageView saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_profile_page);

        // Initialize your views
        storeNameEditText = findViewById(R.id.store_profile_name);
        storeAddressTextView = findViewById(R.id.store_profile_address);
        toggleModeImageView = findViewById(R.id.store_profile_toggle_mode);
        saveButton = findViewById(R.id.store_profile_save_button);

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

        initializeDialogs();
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
        storeAddressTextView.setEnabled(false);
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
