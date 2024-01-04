package com.projectfkklp.saristorepos.activities.user_registration;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseUser;
import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.activities.store_selector.StoreSelectorPage;
import com.projectfkklp.saristorepos.activities.user_login.UserLoginPage;
import com.projectfkklp.saristorepos.enums.SignInMethod;
import com.projectfkklp.saristorepos.managers.SessionManager;
import com.projectfkklp.saristorepos.managers.UserManager;
import com.projectfkklp.saristorepos.models.User;
import com.projectfkklp.saristorepos.repositories.AuthenticationRepository;
import com.projectfkklp.saristorepos.repositories.UserRepository;
import com.projectfkklp.saristorepos.utils.ActivityUtils;
import com.projectfkklp.saristorepos.utils.AuthenticationUtils;

import java.util.HashMap;
import java.util.Objects;

public class UserRegistrationPage  extends AppCompatActivity {
    private User user;
    private ActivityResultLauncher<Intent> signInLauncher;
    private SignInMethod signInMethod;
    EditText nameText;
    EditText gmailText;
    EditText phoneText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_registration_page);

        initializeViews();
        initializeUser();

        signInLauncher = AuthenticationUtils.createSignInLauncher(this, this::signIn);
    }

    private void initializeViews(){
        nameText = findViewById(R.id.signup_name);
        gmailText = findViewById(R.id.signup_gmail);
        phoneText = findViewById(R.id.signup_phone_number);

        nameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                user.setName(editable.toString());
            }
        });
    }

    private void initializeUser(){
        user = new User();

        Intent intent = getIntent();
        int signInMethod = intent.getIntExtra("signInMethod", 0);
        String uid = intent.getStringExtra("uid");
        String identifier = intent.getStringExtra("identifier");
        String name = intent.getStringExtra("name");

        user.setName(name);
        nameText.setText(name);
        if (signInMethod == SignInMethod.PHONE.value) {
            user.setPhoneUid(uid);
            phoneText.setText(identifier);
        }
        else {
            user.setGmailUid(uid);
            gmailText.setText(identifier);
        }
    }

    private void signIn(@NonNull ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            FirebaseUser firebaseUser = AuthenticationRepository.getCurrentUser();
            UserRepository.getSignedInUser(signInMethod, signedInUser -> {
                if (signedInUser == null) {
                    String name = firebaseUser.getDisplayName();

                    if (name != null) {
                        user.setName(name);
                        nameText.setText(name);
                    }

                    String uid = firebaseUser.getUid();
                    String identifier = signInMethod.value == SignInMethod.PHONE.value
                        ? firebaseUser.getPhoneNumber()
                        : firebaseUser.getProviderData().get(1).getEmail();

                    if (signInMethod.value == SignInMethod.PHONE.value) {
                        user.setPhoneUid(uid);
                        phoneText.setText(identifier);
                    }
                    else {
                        user.setGmailUid(uid);
                        gmailText.setText(identifier);
                    }
                }
                else {
                    Toast.makeText(this, "Already in use", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            // Sign in failed
            IdpResponse response = IdpResponse.fromResultIntent(result.getData());
            if (response == null) {
                Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show();
            }
            else if (Objects.requireNonNull(response.getError()).getErrorCode() == ErrorCodes.NO_NETWORK) {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Unknown error occurred", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void changePhoneNumber(View view){
        signInLauncher.launch(AuthenticationUtils.PHONE_SIGN_IN_INTENT);
        signInMethod = SignInMethod.PHONE;
    }

    public void changeGmail(View view){
        signInLauncher.launch(AuthenticationUtils.GMAIL_SIGN_IN_INTENT);
        signInMethod = SignInMethod.GMAIL;
    }

    public void register(View view){
        UserManager.registerUser(user, (registrationUser, validationStatus, task)->{
            if (task == null) {
                HashMap<String, String> errors = validationStatus.getErrors();
                if (errors.get("name") != null) {
                    nameText.setError(errors.get("name"));
                }
                return;
            }

            task.addOnSuccessListener(successTask->{
                SessionManager.setUser(this, user);
                Toast.makeText(this, "Registration success", Toast.LENGTH_SHORT).show();
                ActivityUtils.navigateTo(this, StoreSelectorPage.class);
            }).addOnFailureListener(failedTask->{
                Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
            });

        });
    }

    public void loginUsingOtherAccount(View view){
        startActivity(new Intent(this, UserLoginPage.class));
    }
}
