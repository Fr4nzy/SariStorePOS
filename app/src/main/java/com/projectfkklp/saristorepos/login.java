package com.projectfkklp.saristorepos;

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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class login extends AppCompatActivity {
    private Button loginButton;
    private FirebaseAuth mAuth;

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

    private void checkAndCreateUserDocument() {
        // Get the current user from FirebaseAuth
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userUid = currentUser.getUid();

            Log.d("Firestore", "Current User UID: " + userUid);

            // Reference to the "users" collection
            CollectionReference usersCollection = FirebaseFirestore.getInstance().collection("users");

            // Check if the user's document already exists
            usersCollection.document(userUid).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (!document.exists()) {
                        // If the document does not exist, create a new one
                        Map<String, Object> userData = new HashMap<>();
                        // You can add more user-related data if needed
                        userData.put("uid", userUid);

                        usersCollection.document(userUid).set(userData)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("Firestore", "User document created");

                                    // Create "products" and "transactions" subcollections
                                    createSubcollections(userUid);
                                })
                                .addOnFailureListener(e -> Log.e("Firestore", "Error creating user document", e));
                    }
                } else {
                    Log.e("Firestore", "Error checking user document", task.getException());
                }
            });
        }
    }

    private void createSubcollections(String userUid) {
        // Reference to the user's document
        DocumentReference userDocument = FirebaseFirestore.getInstance().collection("users").document(userUid);

        // Create "products" subcollection and immediately delete the document
        userDocument.collection("products").add(new HashMap<>())
                .addOnSuccessListener(documentReference -> {
                    // Delete the document immediately after creation
                    documentReference.delete()
                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "Products subcollection created"))
                            .addOnFailureListener(e -> Log.e("Firestore", "Error deleting document in products subcollection", e));
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error creating products subcollection", e));

        // Create "transactions" subcollection and immediately delete the document
        userDocument.collection("transactions").add(new HashMap<>())
                .addOnSuccessListener(documentReference -> {
                    // Delete the document immediately after creation
                    documentReference.delete()
                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "Transactions subcollection created"))
                            .addOnFailureListener(e -> Log.e("Firestore", "Error deleting document in transactions subcollection", e));
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error creating transactions subcollection", e));
    }


    private void startMainActivity() {
        Intent intent = new Intent(login.this, MainActivity.class);
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
            FirebaseUser currentUser = mAuth.getCurrentUser();
            assert currentUser != null;
            String userUid = currentUser.getUid();

            Log.d("Firestore", "Current User UID: " + userUid);
            checkAndCreateUserDocument(); // Check and create the user document
            startMainActivity();
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
