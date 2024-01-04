package com.projectfkklp.saristorepos.activities.user_login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import java.util.Objects;

public class UserLoginPage extends AppCompatActivity {
    private SIgnInMethod signInMethod;
    private ActivityResultLauncher<Intent> signInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_login_page);

        FirebaseApp.initializeApp(this);
        AuthenticationManager.logout(this, task -> {});

        signInLauncher = AuthenticationUtils.createSignInLauncher(this, this::onSignInResult);
    }

    public void loginViaPhone(View view){
        signInLauncher.launch(AuthenticationUtils.PHONE_SIGN_IN_INTENT);
        signInMethod = SIgnInMethod.PHONE;
    }

    public void loginViaGmail(View view){
        signInLauncher.launch(AuthenticationUtils.GMAIL_SIGN_IN_INTENT);
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
