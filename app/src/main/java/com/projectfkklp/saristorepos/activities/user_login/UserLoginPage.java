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
import com.google.firebase.auth.FirebaseUser;
import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.activities.store_selector.StoreSelectorPage;
import com.projectfkklp.saristorepos.activities.user_registration.UserRegistrationPage;
import com.projectfkklp.saristorepos.enums.SignInMethod;
import com.projectfkklp.saristorepos.managers.AuthenticationManager;
import com.projectfkklp.saristorepos.managers.SessionManager;
import com.projectfkklp.saristorepos.managers.UserManager;
import com.projectfkklp.saristorepos.repositories.AuthenticationRepository;
import com.projectfkklp.saristorepos.repositories.UserRepository;
import com.projectfkklp.saristorepos.utils.AuthenticationUtils;

import java.util.Objects;

public class UserLoginPage extends AppCompatActivity {
    private SignInMethod signInMethod;
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
        signInMethod = SignInMethod.PHONE;
    }

    public void loginViaGmail(View view){
        signInLauncher.launch(AuthenticationUtils.GMAIL_SIGN_IN_INTENT);
        signInMethod = SignInMethod.GMAIL;
    }

    private void onSignInResult(@NonNull ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            FirebaseUser firebaseUser = AuthenticationRepository.getCurrentUser();
            UserRepository.getSignedInUser(signInMethod, user -> {
                if (user == null) {
                    Intent intent = new Intent(this, UserRegistrationPage.class);
                    intent.putExtra("uid", firebaseUser.getUid());
                    intent.putExtra("name", firebaseUser.getDisplayName());
                    intent.putExtra("signInMethod", signInMethod.value);
                    intent.putExtra("identifier",
                        signInMethod.value == SignInMethod.PHONE.value
                            ? firebaseUser.getPhoneNumber()
                            : firebaseUser.getProviderData().get(1).getEmail()
                        );

                    startActivity(intent);
                }
                else {
                    // Since we login via gmail via provider
                    // And sometimes gmail can be changed
                    // This extra steps, update the user gmail in firebase database
                    if (signInMethod.value == SignInMethod.GMAIL.value) {
                        user.setGmail(firebaseUser.getProviderData().get(1).getEmail());
                        UserManager.saveUser(user);
                    }

                    // Start session and next activity
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
