package com.projectfkklp.saristorepos.activities.user_profile;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseUser;
import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.enums.AuthenticationProvider;
import com.projectfkklp.saristorepos.models.User;
import com.projectfkklp.saristorepos.repositories.AuthenticationRepository;
import com.projectfkklp.saristorepos.repositories.SessionRepository;
import com.projectfkklp.saristorepos.repositories.UserRepository;
import com.projectfkklp.saristorepos.utils.AuthenticationUtils;
import com.projectfkklp.saristorepos.utils.ProgressUtils;
import com.projectfkklp.saristorepos.utils.TestingUtils;
import com.projectfkklp.saristorepos.utils.ToastUtils;

import java.util.Objects;

public class UserProfilePage extends AppCompatActivity {
    ImageView iconButtonToggleMode;
    EditText profileNameText;
    EditText phoneText;
    EditText gmailText;
    Button updateButton;
    ImageView unlinkPhoneButton;
    ImageView unlinkGmailButton;
    AlertDialog.Builder cancelConfirmationDialog;
    AlertDialog.Builder unlinkPhoneConfirmationDialog;
    AlertDialog.Builder unlinkGmailConfirmationDialog;
    private boolean isEditing;
    private User currentUser;
    private User editUser;

    private ActivityResultLauncher<Intent> profileLauncher;
    private AuthenticationProvider authenticationProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_page);

        currentUser = SessionRepository.getCurrentUser(this);

        initializeViews();
        initializeData();
        initializeDialogs();

        profileLauncher = AuthenticationUtils.createSignInLauncher(this, this::profileSignIn);
    }

    private void initializeData(){
        isEditing=false;

        // dummy currentUser
        /*currentUser = new User("Juan Dela Cruz", "", "");*/
        editUser = new User(currentUser.getName(), currentUser.getPhoneUid(), currentUser.getGmailUid());
    }

    private void initializeViews(){
        iconButtonToggleMode = findViewById(R.id.user_profile_toggle_mode);
        profileNameText = findViewById(R.id.user_profile_name);
        phoneText = findViewById(R.id.user_profile_phone);
        gmailText = findViewById(R.id.user_profile_gmail);
        unlinkPhoneButton = findViewById(R.id.user_profile_unlink_phone);
        unlinkGmailButton = findViewById(R.id.user_profile_unlink_gmail);
        updateButton = findViewById(R.id.user_profile_update_button);

        profileNameText.setText(currentUser.getName());
        profileNameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                editUser.setName(editable.toString());
                updateButton.setEnabled(hasEdits());
            }
        });

        phoneText.setText(currentUser.getPhoneNumber());
        phoneText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                editUser.setPhoneUid(editable.toString());
                updateButton.setEnabled(hasEdits());
            }
        });

        gmailText.setText(currentUser.getGmail());
        gmailText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                editUser.setGmailUid(editable.toString());
                updateButton.setEnabled(hasEdits());
            }
        });
    }

    public void changeProfilePhoneNumber(View view){
        profileLauncher.launch(AuthenticationUtils.PHONE_SIGN_IN_INTENT);
        authenticationProvider = AuthenticationProvider.PHONE;
    }

    public void changeProfileGmail(View view){
        profileLauncher.launch(AuthenticationUtils.GMAIL_SIGN_IN_INTENT);
        authenticationProvider = AuthenticationProvider.GMAIL;
    }

    private void initializeDialogs(){
        cancelConfirmationDialog = new AlertDialog.Builder(this)
            .setTitle("Cancel Edit ?")
            .setMessage("You have unsaved changes, do you want to cancel edit?")
            .setPositiveButton("Yes", (dialog, which) -> disableEditing())
            .setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        unlinkPhoneConfirmationDialog = new AlertDialog.Builder(this)
            .setTitle("Unlink Phone ?")
            .setMessage("Are you sure you want to unlink your phone?")
            .setPositiveButton("Yes", (dialog, which) -> unlinkPhone())
            .setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        unlinkGmailConfirmationDialog = new AlertDialog.Builder(this)
                .setTitle("Unlink Phone ?")
                .setMessage("Are you sure you want to unlink your phone?")
                .setPositiveButton("Yes", (dialog, which) -> unlinkGmail())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss());
    }

    public void toggleMode(View view){
        // Regardless of the mode, when from read mode, enable editing
        if (!isEditing) {
            enableEditing();
            return;
        }

        // Else, check if there are any changes
        if (hasEdits()) {
            showCancelConfirmationDialog();
        }
        else {
            disableEditing();
        }
    }

    private boolean hasEdits() {
        return
            !editUser.getName().equals(currentUser.getName())
            || !editUser.getGmailUid().equals(currentUser.getGmailUid())
            || !editUser.getPhoneUid().equals(currentUser.getPhoneUid())
        ;
    }

    private void enableEditing(){
        isEditing=true;
        iconButtonToggleMode.setImageResource(R.drawable.baseline_cancel_24);
        profileNameText.setEnabled(true);
        phoneText.setEnabled(true); // Enable phoneText EditText
        gmailText.setEnabled(true); // Enable gmailText EditText

        unlinkPhoneButton.setVisibility(View.VISIBLE);
        unlinkGmailButton.setVisibility(View.VISIBLE);
        updateButton.setVisibility(View.VISIBLE);
        updateButton.setEnabled(false);
    }

    private void disableEditing(){
        isEditing=false;
        editUser = new User(currentUser.getName(), currentUser.getPhoneUid(), currentUser.getGmailUid());

        iconButtonToggleMode.setImageResource(R.drawable.edit);
        profileNameText.setEnabled(false);
        profileNameText.setText(currentUser.getName());
        phoneText.setEnabled(false); // Disable phoneText EditText
        phoneText.setText(currentUser.getPhoneNumber());
        gmailText.setEnabled(false); // Disable gmailText EditText
        gmailText.setText(currentUser.getGmail());

        unlinkPhoneButton.setVisibility(View.GONE);
        unlinkGmailButton.setVisibility(View.GONE);
        updateButton.setVisibility(View.GONE);
    }

    private void showCancelConfirmationDialog(){
        cancelConfirmationDialog.show();
    }

    public void showUnlinkPhoneConfirmationDialog(View view){
        unlinkPhoneConfirmationDialog.show();
    }

    public void showUnlinkGmailConfirmationDialog(View view){
        unlinkGmailConfirmationDialog.show();
    }

    private void unlinkPhone(){
        editUser.setPhoneUid("");
        phoneText.setText("");
    }

    private void unlinkGmail(){
        editUser.setGmailUid("");
        gmailText.setText("");
    }

    public void updateUser(View view){
        ProgressUtils.showDialog(this, "Updating User");
        TestingUtils.delay(1000, ()->{
            disableEditing();
            ProgressUtils.dismissDialog();
        });
    }

    private void profileSignIn(@NonNull ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            FirebaseUser firebaseUser = AuthenticationRepository.getCurrentAuthentication();
            UserRepository.getUserByAuthentication(authenticationProvider, signedInUser -> {
                if (signedInUser == null) {
                    String name = firebaseUser.getDisplayName();

                    if (name != null) {
                        currentUser.setName(name);
                        profileNameText.setText(name);
                    }

                    String uid = firebaseUser.getUid();

                    if (authenticationProvider.value == AuthenticationProvider.PHONE.value) {
                        String identifier = firebaseUser.getPhoneNumber();
                        currentUser.setPhoneUid(uid);
                        currentUser.setPhoneNumber(identifier);
                        phoneText.setText(identifier);
                    }
                    else {
                        String identifier = firebaseUser.getProviderData().get(1).getEmail();
                        currentUser.setGmailUid(uid);
                        currentUser.setGmail(identifier);
                        gmailText.setText(identifier);
                    }
                }
                else {
                    ToastUtils.show(this, "Already in Use");
                }
            }, AuthenticationRepository.getCurrentAuthenticationUid());
        }
        else {
            // Sign in failed
            IdpResponse response = IdpResponse.fromResultIntent(result.getData());
            if (response == null) {
                Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show();
            }
            else if (Objects.requireNonNull(response.getError()).getErrorCode() == ErrorCodes.NO_NETWORK) {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Unknown error occurred", Toast.LENGTH_SHORT).show();
            }
        }
    }
}