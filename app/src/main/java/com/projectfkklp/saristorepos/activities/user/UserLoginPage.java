package com.projectfkklp.saristorepos.activities.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.activities.dashboard.DashboardPage;
import com.projectfkklp.saristorepos.managers.UserManager;
import com.projectfkklp.saristorepos.models.User;
import com.projectfkklp.saristorepos.repositories.AuthenticationRepository;
import com.projectfkklp.saristorepos.repositories.UserRepository;

import java.util.Collections;
import java.util.Objects;

public class UserLoginPage extends AppCompatActivity {
    private Button loginButton;
    private FirebaseAuth mAuth;

    // Use the new ActivityResultLauncher
    private ActivityResultLauncher<Intent> signInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_login_page);

        signInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> onSignInResult(result.getResultCode(), result.getData())
        );

        FirebaseApp.initializeApp(this);

        try {
            mAuth = FirebaseAuth.getInstance();

            // check if user is already logged in, if yes, skip sign in and goto DashBoard
            if (mAuth.getCurrentUser() != null) {
                startMainActivity();
            }

            loginButton = findViewById(R.id.login_button);

            loginButton.setOnClickListener(view -> {
                // Check if the user is already signed in with a phone number
                if (mAuth.getCurrentUser() != null) {
                    checkAndCreateUserDocument(); // Check and create the user document
                } else {
                    // Launch FirebaseUI for Phone Number authentication
                    launchFirebaseUIPhoneNumberAuth();
                }
            });

            Button gmailButton = findViewById(R.id.gmail_button);

            gmailButton.setOnClickListener(view -> {
                if(mAuth.getCurrentUser() != null) {
                    checkAndCreateUserDocument();
                } else {
                    launchFirebaseUIGmailAuth();
                }
            });
        }
        catch (Exception e) {
        }
    }

    private void checkAndCreateUserDocument() {
        // Get the current user from FirebaseAuth
        UserRepository.getCurrentUser(user->{
            // If user is new
            if (user==null) {
                UserManager.saveUser( new User());
            }
        });
    }

    private void startMainActivity() {
        Intent intent = new Intent(UserLoginPage.this, DashboardPage.class);
        startActivity(intent);
        finish();
    }

    private void launchFirebaseUIPhoneNumberAuth() {
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(Collections.singletonList(
                        new AuthUI.IdpConfig.PhoneBuilder().build()
                ))
                .build();

        // Use the new ActivityResultLauncher to start the activity
        signInLauncher.launch(signInIntent);
    }

    private void launchFirebaseUIGmailAuth() {
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(Collections.singletonList(
                        new AuthUI.IdpConfig.GoogleBuilder().build()
                ))
                .build();

        // Use the new ActivityResultLauncher to start the activity
        signInLauncher.launch(signInIntent);
    }

    private void onSignInResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            // Successfully signed in
            String userUid = AuthenticationRepository.getCurrentUserUid();
            Log.d("Firestore", "Current _User UID: " + userUid);
            checkAndCreateUserDocument(); // Check and create the user document
            startMainActivity();
        } else {
            // Sign in failed
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (response == null) {
                // _User pressed back button
                return;
            }
            if (Objects.requireNonNull(response.getError()).getErrorCode() == ErrorCodes.NO_NETWORK) {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Unknown error occurred", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
