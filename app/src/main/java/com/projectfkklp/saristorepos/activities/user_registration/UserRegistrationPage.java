package com.projectfkklp.saristorepos.activities.user_registration;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.managers.SessionManager;

public class UserRegistrationPage  extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_registration_page);
    }
}
