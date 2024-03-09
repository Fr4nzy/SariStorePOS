package com.projectfkklp.saristorepos.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.projectfkklp.saristorepos.R;

import java.util.Collections;

public class AuthenticationUtils {
    public static final Intent PHONE_SIGN_IN_INTENT = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(Collections.singletonList(
                    new AuthUI.IdpConfig.PhoneBuilder().build()
            ))
            .setTheme(R.style.Theme_SariStorePOS)
            .build();
    public static final Intent GMAIL_SIGN_IN_INTENT = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(Collections.singletonList(
                    new AuthUI.IdpConfig.GoogleBuilder().build()
            ))
            .setTheme(R.style.Theme_SariStorePOS)
            .build();

    public static ActivityResultLauncher<Intent> createSignInLauncher(
        ComponentActivity activity,
        ActivityResultCallback<ActivityResult> callback
    ){
        return activity.registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                callback.onActivityResult(result);
                if (result.getResultCode() == Activity.RESULT_OK) {
                    signOutFirebase(activity, x -> {});
                }
            }
        );
    }

    public static void signOutFirebase(Context context, OnCompleteListener<Void> onCompleteListener){
        FirebaseAuth.getInstance().signOut();
        AuthUI.getInstance().signOut(context).addOnCompleteListener(onCompleteListener);
    }

}
