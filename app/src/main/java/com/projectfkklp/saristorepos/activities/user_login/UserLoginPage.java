package com.projectfkklp.saristorepos.activities.user_login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.FirebaseApp;
import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.activities.store_selector.StoreSelectorPage;
import com.projectfkklp.saristorepos.activities.user_registration.UserRegistrationPage;
import com.projectfkklp.saristorepos.enums.SIgnInMethod;
import com.projectfkklp.saristorepos.managers.AuthenticationManager;
import com.projectfkklp.saristorepos.managers.SessionManager;
import com.projectfkklp.saristorepos.repositories.UserRepository;
import com.projectfkklp.saristorepos.utils.AuthenticationUtils;

import java.util.Collections;
import java.util.Objects;

public class UserLoginPage extends AppCompatActivity {
    private SIgnInMethod signInMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_login_page);

        FirebaseApp.initializeApp(this);
        AuthenticationManager.logout(this, task -> {});
    }

    public void loginViaPhone(View view){
        AuthenticationUtils.authenticateViaPhone(this, this::onSignInResult);
        signInMethod = SIgnInMethod.PHONE;
    }

    public void loginViaGmail(View view){
        AuthenticationUtils.authenticateViaGmail(this, this::onSignInResult);
        signInMethod = SIgnInMethod.GMAIL;
    }

    private void onSignInResult(@NonNull ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            UserRepository.getSignedInUser(signInMethod, user -> {
                if (user == null) {
                    startActivity(new Intent(this, UserRegistrationPage.class));
                }
                else {
                    SessionManager.setUser(this, user);
                    startActivity(new Intent(this, StoreSelectorPage.class));
                }
            });
        } else {
            // Sign in failed
            IdpResponse response = IdpResponse.fromResultIntent(result.getData());
            if (response == null) {
                Toast.makeText(this, "Sign in was canceled", Toast.LENGTH_SHORT).show();
            }
            else if (Objects.requireNonNull(response.getError()).getErrorCode() == ErrorCodes.NO_NETWORK) {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Unknown error occurred", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
