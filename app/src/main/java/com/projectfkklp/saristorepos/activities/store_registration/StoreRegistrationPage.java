package com.projectfkklp.saristorepos.activities.store_registration;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.activities.dashboard.DashboardPage;
import com.projectfkklp.saristorepos.models.Store;
import com.projectfkklp.saristorepos.models.UserStoreRelation;

public class StoreRegistrationPage extends AppCompatActivity {

    private EditText storeNameEditText;
    private EditText storeAddressEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_registration_page);

        // Initialize UI elements
        storeNameEditText = findViewById(R.id.store_registration_name);
        storeAddressEditText = findViewById(R.id.store_registration_address);

        // Set up OnClickListener for the storeRegister Button
        Button storeRegisterButton = findViewById(R.id.storeRegister);
        storeRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the button click
                registerStore();
            }
        });
    }

    private void registerStore() {
        // Get the values from the EditText fields
        String storeName = storeNameEditText.getText().toString().trim();
        String storeAddress = storeAddressEditText.getText().toString().trim();

        if (storeName.isEmpty() || storeAddress.isEmpty()) {
            // Check if any field is empty, display a message if true
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
        } else {
            // Register the store
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

            // Create a new store object
            Store newStore = new Store(storeName, storeAddress);

            // Save the store to Firebase
            String storeId = databaseReference.child("stores").push().getKey();
            databaseReference.child("stores").child(storeId).setValue(newStore);

            // Create a UserStoreRelation and save it
            String currentUserId = "YourCurrentUserId"; // Replace with actual user ID
            UserStoreRelation userStoreRelation = new UserStoreRelation(currentUserId, storeId);
            UserStoreRelationManager.save(userStoreRelation);

            // Save the store ID in the session
            SessionManager.setCurrentStore(storeId);

            // Notify the user that the store has been registered
            Toast.makeText(this, "Store registered successfully!", Toast.LENGTH_SHORT).show();

            // Navigate to the DashboardPage
            Intent intent = new Intent(StoreRegistrationPage.this, DashboardPage.class);
            startActivity(intent);
        }
    }
}
