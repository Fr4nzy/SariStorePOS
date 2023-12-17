package com.projectfkklp.saristorepos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CardView viewInventory = findViewById(R.id.viewInventory);
        CardView viewPOS = findViewById(R.id.viewPOS);
        CardView salesReport = findViewById(R.id.salesReport);
        CardView logout = findViewById(R.id.logout);

        viewInventory.setOnClickListener(this);
        viewPOS.setOnClickListener(this);
        salesReport.setOnClickListener(this);
        logout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent i;

        if (view.getId() == R.id.viewInventory) {
            i = new Intent(this, viewInventory.class); // Assuming ViewInventory is the correct class name
        } else if (view.getId() == R.id.viewPOS) {
            i = new Intent(this, viewPOS.class); // Assuming ViewPOS is the correct class name
        } else if (view.getId() == R.id.salesReport) {
            i = new Intent(this, ForecastEntry.class); // Assuming SalesReport is the correct class name
        } else if (view.getId() == R.id.logout) {
            i = new Intent(this, login.class); // Assuming Login is the correct class name
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear user session, sign out from Firebase, and go to the login screen
            FirebaseAuth.getInstance().signOut(); // Sign out from Firebase
            clearUserSession();
            startActivity(i);
            finish(); // Close the current activity to prevent going back to it on pressing back
        } else {
            return; // Do nothing for other cases
        }
        startActivity(i);
    }

    private void clearUserSession() {
        // Clear user session information (logout)
        SharedPreferences preferences = getSharedPreferences("user_session_pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear(); // Clear all stored preferences
        editor.apply();
    }
}
