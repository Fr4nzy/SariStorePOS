package com.projectfkklp.saristorepos.activities.user_login;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
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
import com.projectfkklp.saristorepos.enums.AuthenticationProvider;
import com.projectfkklp.saristorepos.managers.AuthenticationManager;
import com.projectfkklp.saristorepos.managers.SessionManager;
import com.projectfkklp.saristorepos.managers.UserManager;
import com.projectfkklp.saristorepos.repositories.AuthenticationRepository;
import com.projectfkklp.saristorepos.repositories.UserRepository;
import com.projectfkklp.saristorepos.utils.AuthenticationUtils;

import java.util.Objects;

public class UserLoginPage extends AppCompatActivity {
    private AuthenticationProvider authenticationProvider;
    private ActivityResultLauncher<Intent> signInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_login_page);

        FirebaseApp.initializeApp(this);
        AuthenticationManager.logout(this, task -> {});

        signInLauncher = AuthenticationUtils.createSignInLauncher(this, this::onSignInResult);

        // Get and display the app version
        displayAppVersion();
    }

    private void displayAppVersion() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionName = packageInfo.versionName;

            TextView versionTextView = findViewById(R.id.user_login_page_version_number);
            versionTextView.setText("Version " + versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void loginViaPhone(View view){
        signInLauncher.launch(AuthenticationUtils.PHONE_SIGN_IN_INTENT);
        authenticationProvider = AuthenticationProvider.PHONE;
    }

    public void loginViaGmail(View view){
        signInLauncher.launch(AuthenticationUtils.GMAIL_SIGN_IN_INTENT);
        authenticationProvider = AuthenticationProvider.GMAIL;
    }

    private void onSignInResult(@NonNull ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            FirebaseUser firebaseUser = AuthenticationRepository.getCurrentAuthentication();
            UserRepository.getUserByAuthentication(
                authenticationProvider,
                firebaseUser.getUid(),
                user -> {
                    if (user == null) {
                        Intent intent = new Intent(this, UserRegistrationPage.class);
                        intent.putExtra("uid", firebaseUser.getUid());
                        intent.putExtra("name", firebaseUser.getDisplayName());
                        intent.putExtra("authenticationProvider", authenticationProvider.value);
                        intent.putExtra("identifier",
                            authenticationProvider.value == AuthenticationProvider.PHONE.value
                                ? firebaseUser.getPhoneNumber()
                                : firebaseUser.getProviderData().get(1).getEmail()
                            );

                        startActivity(intent);
                    }
                    else {
                        // Since we login via gmail via provider
                        // And sometimes gmail can be changed
                        // This extra steps, update the user gmail in firebase database
                        if (authenticationProvider.value == AuthenticationProvider.GMAIL.value) {
                            user.setGmail(firebaseUser.getProviderData().get(1).getEmail());
                            UserManager.saveUser(user);
                        }

                        // Start session and next activity
                        SessionManager.setUser(this, user);
                        startActivity(new Intent(this, StoreSelectorPage.class));
                        finish();
                    }
                }
            );
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
