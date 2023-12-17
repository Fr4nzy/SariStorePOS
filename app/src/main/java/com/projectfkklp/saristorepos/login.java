package com.projectfkklp.saristorepos;

import android.content.Intent;
import android.os.Bundle;
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


import java.util.Collections;
import java.util.Objects;

public class login extends AppCompatActivity {
    Button loginButton;
    FirebaseAuth mAuth;

    // Use the new ActivityResultLauncher
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> onSignInResult(result.getResultCode(), result.getData())
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseApp.initializeApp(this);

        mAuth = FirebaseAuth.getInstance();

        loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(view -> {
            // Check if the user is already signed in with phone number
            if (mAuth.getCurrentUser() != null) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                // User is already signed in
                // You can redirect them to the main activity or do whatever is needed
            } else {
                // Launch FirebaseUI for Phone Number authentication
                launchFirebaseUIPhoneNumberAuth();
            }
        });

        Button gmailButton = findViewById(R.id.gmail_button);
        gmailButton.setOnClickListener(view -> {
            if(mAuth.getCurrentUser() != null) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            } else {
                launchFirebaseUIGmailAuth();
            }
        });
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
            Intent intent = new Intent(login.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            // Sign in failed
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (response == null) {
                // User pressed back button
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
